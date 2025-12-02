package view;

import model.*;
import repository.mongo.*;
import service.*;
import util.ValidacaoHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Optional;

public class PedidoView {

    private final Scanner sc = new Scanner(System.in);

    private final PedidoService pedidoService = new PedidoService(new MongoPedidoRepository());
    private final MesaService mesaService = new MesaService(new MongoMesaRepository());
    private final FuncionarioService funcionarioService = new FuncionarioService(new MongoFuncionarioRepository());
    private final ItemService itemService = new ItemService(new MongoItemRepository());
    private final PedidoItemService pedidoItemService = new PedidoItemService(new MongoPedidoItemRepository());
    private final ProdutoService produtoService = new ProdutoService(new MongoProdutoRepository());
    private final ReceitaService receitaService = new ReceitaService(new MongoReceitaRepository());

    public void exibirMenu() {
        while (true) {
            System.out.println("\n=== Gestão de Pedidos (MongoDB) ===");
            System.out.println("1. Novo Pedido");
            System.out.println("2. Listar Todos os Pedidos");
            System.out.println("3. Listar Pedidos Ativos");
            System.out.println("4. Editar Pedido");
            System.out.println("5. Excluir Pedido");
            System.out.println("6. Listar Itens de um Pedido");
            System.out.println("7. Editar Itens de um Pedido (Adicionar/Remover/Qtd)");
            System.out.println("0. Voltar");

            int opcao = ValidacaoHelper.lerInteiro(sc, "Escolha uma opção: ");

            switch (opcao) {
                case 1: cadastrar(); break;
                case 2: listarTodos(); break;
                case 3: listarAtivosComTotal(); break;
                case 4: editarPedidoCompleto(); break;
                case 5: excluir(); break;
                case 6: listarItensDoPedido(); break;
                case 7: gerenciarItensDoPedido(); break;
                case 0: return;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private void gerenciarItensDoPedido() {
        System.out.println("\n--- Selecione o Pedido para Gerenciar Itens ---");
        Pedido pedido = selecionarPedido();
        if (pedido == null) return;

        while (true) {
            System.out.println("\n--- Gerenciando Itens do Pedido #" + pedido.getId_pedido() + " ---");
            System.out.println("1. Adicionar novo item");
            System.out.println("2. Remover item existente");
            System.out.println("3. Alterar quantidade de um item");
            System.out.println("0. Voltar/Salvar");

            int opcao = ValidacaoHelper.lerInteiro(sc, "Escolha: ");

            switch (opcao) {
                case 1:
                    adicionarItemAoPedidoExistente(pedido);
                    break;
                case 2:
                    removerItemDoPedido(pedido);
                    break;
                case 3:
                    alterarQuantidadeItem(pedido);
                    break;
                case 0:
                    return;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private void adicionarItemAoPedidoExistente(Pedido pedido) {
        Item itemSelecionado = selecionarItem();
        if (itemSelecionado == null) return;

        int quantidade = ValidacaoHelper.lerInteiro(sc, "Quantidade: ");

        if (!processarEstoque(itemSelecionado.getId_item(), quantidade, true)) {
            return;
        }

        if (pedido.getItens() == null) pedido.setItens(new ArrayList<>());

        boolean existe = false;
        for (PedidoItem pi : pedido.getItens()) {
            if (pi.getId_item() == itemSelecionado.getId_item()) {
                pi.setQuantidade(pi.getQuantidade() + quantidade);
                existe = true;
                break;
            }
        }

        if (!existe) {
            PedidoItem novoItem = new PedidoItem(pedido.getId_pedido(), itemSelecionado.getId_item(), quantidade);
            pedido.getItens().add(novoItem);
        }

        pedidoService.editarPedido(pedido);
        System.out.println("Item adicionado com sucesso!");
    }

    private void removerItemDoPedido(Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            System.out.println("O pedido não tem itens.");
            return;
        }

        listarItensSimples(pedido);
        int index = ValidacaoHelper.lerInteiro(sc, "Digite o número do item para remover (ou 0 para cancelar): ");

        if (index <= 0 || index > pedido.getItens().size()) return;

        PedidoItem itemRemover = pedido.getItens().get(index - 1);

        processarEstoque(itemRemover.getId_item(), itemRemover.getQuantidade(), false);

        pedido.getItens().remove(index - 1);

        pedidoService.editarPedido(pedido);
        System.out.println("Item removido e estoque estornado.");
    }

    private void alterarQuantidadeItem(Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            System.out.println("O pedido não tem itens.");
            return;
        }

        listarItensSimples(pedido);
        int index = ValidacaoHelper.lerInteiro(sc, "Digite o número do item para alterar (ou 0 para cancelar): ");

        if (index <= 0 || index > pedido.getItens().size()) return;

        PedidoItem itemAlterar = pedido.getItens().get(index - 1);
        int novaQtd = ValidacaoHelper.lerInteiro(sc, "Nova Quantidade: ");

        if (novaQtd <= 0) {
            System.out.println("Para remover o item, use a opção de remover.");
            return;
        }

        int diferenca = novaQtd - itemAlterar.getQuantidade();

        if (diferenca == 0) return;

        boolean consumir = diferenca > 0;
        int qtdAbsoluta = Math.abs(diferenca);

        if (!processarEstoque(itemAlterar.getId_item(), qtdAbsoluta, consumir)) {
            return;
        }

        itemAlterar.setQuantidade(novaQtd);

        pedidoService.editarPedido(pedido);
        System.out.println("Quantidade atualizada com sucesso.");
    }

    private boolean processarEstoque(int idItem, int quantidade, boolean consumirEstoque) {
        List<Produto> receita = receitaService.listarReceita(idItem);

        if (consumirEstoque) {
            for (Produto pReceita : receita) {
                Produto pEstoque = produtoService.findById(pReceita.getId_produto());
                if (pEstoque == null) {
                    System.out.println("Erro: Produto da receita não encontrado no estoque.");
                    return false;
                }
                double necessario = pReceita.getQuantidade() * quantidade;
                if (pEstoque.getQuantidade() < necessario) {
                    System.out.printf("Estoque insuficiente de %s. Necessário: %.2f, Disponível: %.2f\n",
                            pEstoque.getNome(), necessario, pEstoque.getQuantidade());
                    return false;
                }
            }
        }

        // Atualização efetiva
        for (Produto pReceita : receita) {
            Produto pEstoque = produtoService.findById(pReceita.getId_produto());
            if (pEstoque != null) {
                double delta = pReceita.getQuantidade() * quantidade;
                if (consumirEstoque) {
                    pEstoque.setQuantidade(pEstoque.getQuantidade() - delta);
                } else {
                    pEstoque.setQuantidade(pEstoque.getQuantidade() + delta);
                }
                produtoService.editarProduto(pEstoque);
            }
        }
        return true;
    }

    private void listarItensSimples(Pedido p) {
        System.out.println("--- Itens Atuais ---");
        int i = 0;
        for (PedidoItem pi : p.getItens()) {
            i++;
            Item item = itemService.findById(pi.getId_item());
            String nome = (item != null) ? item.getNome() : "Desconhecido";
            System.out.printf("%d - %s | Qtd: %d\n", i, nome, pi.getQuantidade());
        }
    }

    private Pedido selecionarPedido() {
        List<Pedido> pedidos = pedidoService.listarPedido();
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
            return null;
        }

        int cont = 0;
        for (Pedido p : pedidos) {
            cont++;
            Mesa m = mesaService.findById(p.getId_mesa());
            String numMesa = (m != null) ? String.valueOf(m.getNumero()) : "N/A";
            System.out.printf("%d - Pedido #%d (Mesa %s)\n", cont, p.getId_pedido(), numMesa);
        }
        System.out.println("0 - Cancelar");

        int escolha = ValidacaoHelper.lerInteiro(sc, "Escolha: ");
        if (escolha <= 0 || escolha > pedidos.size()) return null;

        return pedidos.get(escolha - 1);
    }

    private void listarItensDoPedido() {
        System.out.println("\n--- Selecione o Pedido para ver os itens ---");
        Pedido pedidoSelecionado = selecionarPedido();
        if (pedidoSelecionado == null) return;

        if (pedidoSelecionado.getItens() == null || pedidoSelecionado.getItens().isEmpty()) {
            System.out.println("Este pedido não possui itens.");
        } else {
            System.out.println("\n--- Itens do Pedido #" + pedidoSelecionado.getId_pedido() + " ---");
            double totalPedido = 0;

            for (PedidoItem pi : pedidoSelecionado.getItens()) {
                Item item = itemService.findById(pi.getId_item());
                String nomeItem = (item != null) ? item.getNome() : "Item (" + pi.getId_item() + ")";
                double preco = (item != null) ? item.getPreco_venda() : 0.0;
                double subtotal = preco * pi.getQuantidade();

                System.out.printf("- %s | Qtd: %d | Unit: R$ %.2f | Subtotal: R$ %.2f\n",
                        nomeItem, pi.getQuantidade(), preco, subtotal);

                totalPedido += subtotal;
            }
            System.out.printf("Total do Pedido: R$ %.2f\n", totalPedido);
        }
    }

    private void cadastrar() {
        System.out.println("\n--- Novo Pedido ---");

        Mesa mesaSelecionada = selecionarMesa("para adicionar um novo pedido");
        if (mesaSelecionada == null) return;

        Funcionario funcionarioSelecionado = selecionarFuncionario();
        if (funcionarioSelecionado == null) return;

        Pedido novoPedido = new Pedido(mesaSelecionada.getId_mesa(), 0, LocalDate.now(), "Ativo");
        novoPedido.setId_funcionario(funcionarioSelecionado.getIdFuncionario());

        int idNovoPedido = pedidoService.cadastrarPedido(novoPedido);

        if (idNovoPedido > 0) {
            System.out.println("Pedido criado com sucesso. ID: " + idNovoPedido);
            Pedido pedidoCriado = pedidoService.findById(idNovoPedido);

            System.out.println("Agora adicione os itens ao pedido...");
            while(true) {
                System.out.println("\n1. Adicionar Item");
                System.out.println("0. Finalizar inclusão inicial");
                int op = ValidacaoHelper.lerInteiro(sc, "Opção: ");
                if (op == 1) adicionarItemAoPedidoExistente(pedidoCriado);
                else break;
            }
        } else if (idNovoPedido == -1) {
            System.out.println("Erro: Esta mesa já possui um pedido ativo.");
        } else {
            System.out.println("Erro: Não foi possível criar o pedido.");
        }
    }

    private void editarPedidoCompleto() {
        System.out.println("\n--- Selecione o Pedido para Editar (Cabeçalho) ---");
        Pedido pedidoParaEditar = selecionarPedido();
        if (pedidoParaEditar == null) return;

        Mesa mesaAtual = mesaService.findById(pedidoParaEditar.getId_mesa());
        String nomeMesaAtual = (mesaAtual != null) ? String.valueOf(mesaAtual.getNumero()) : "N/A";

        System.out.println("\nEditando Pedido da Mesa " + nomeMesaAtual);

        System.out.println("Deseja alterar a mesa do pedido? (S/N)");
        if (sc.nextLine().equalsIgnoreCase("S")) {
            Mesa novaMesa = selecionarMesa("para qual deseja mover o pedido");
            if(novaMesa != null) {
                pedidoParaEditar.setId_mesa(novaMesa.getId_mesa());
            }
        }

        System.out.println("Deseja alterar o funcionário do pedido? (S/N)");
        if (sc.nextLine().equalsIgnoreCase("S")) {
            Funcionario novoFuncionario = selecionarFuncionario();
            if(novoFuncionario != null) {
                pedidoParaEditar.setId_funcionario(novoFuncionario.getIdFuncionario());
            }
        }

        System.out.println("Deseja alterar a data do pedido? (S/N)");
        if (sc.nextLine().equalsIgnoreCase("S")) {
            pedidoParaEditar.setData_pedido(ValidacaoHelper.lerData(sc, "Nova data do pedido (DD/MM/YYYY): "));
        }

        System.out.println("Deseja alterar o status do pedido? (S/N)");
        if (sc.nextLine().equalsIgnoreCase("S")) {
            System.out.print("Novo Status (ex: Pago, Cancelado, Ativo): ");
            pedidoParaEditar.setStatus(sc.nextLine());
        }

        pedidoService.editarPedido(pedidoParaEditar);
    }


    private void listarTodos() {
        List<Pedido> pedidos = pedidoService.listarPedido();

        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
            return;
        }
        System.out.println("\n--- Todos os Pedidos ---");
        for (Pedido p : pedidos) {
            Mesa m = mesaService.findById(p.getId_mesa());
            Funcionario f = funcionarioService.findById(p.getId_funcionario());

            String numMesa = (m != null) ? String.valueOf(m.getNumero()) : "N/A";
            String nomeFunc = (f != null) ? f.getNome() : "Desconhecido";

            System.out.printf("ID: %d | Mesa: %s | Garçom: %s | Data: %s | Status: %s | Qtd Itens: %d\n",
                    p.getId_pedido(), numMesa, nomeFunc, p.getData_pedido(), p.getStatus(),
                    (p.getItens() != null ? p.getItens().size() : 0));
        }
    }

    private void listarAtivosComTotal() {
        Map<Integer, Double> totais = pedidoService.listarPedidosAtivosComTotal();
        if (totais.isEmpty()) {
            System.out.println("Nenhum pedido ativo no momento.");
            return;
        }
        System.out.println("\n--- Pedidos Ativos e Totais ---");
        for (Map.Entry<Integer, Double> entry : totais.entrySet()) {
            System.out.printf("Mesa Número: %d - Total do Pedido: R$ %.2f\n", entry.getKey(), entry.getValue());
        }

        System.out.println("\nDeseja encerrar algum pedido?");
        int numeroMesa = ValidacaoHelper.lerInteiro(sc, "Se sim, digite o número da mesa. Se não, digite 0: ");

        if (numeroMesa > 0) {
            List<Pedido> pedidosAtivos = pedidoService.listarPedidosAtivos();
            Optional<Pedido> pedidoParaEncerrarOpt = pedidosAtivos.stream()
                    .filter(p -> {
                        Mesa m = mesaService.findById(p.getId_mesa());
                        return m != null && m.getNumero() == numeroMesa;
                    })
                    .findFirst();

            if (pedidoParaEncerrarOpt.isPresent()) {
                Pedido pedidoParaEncerrar = pedidoParaEncerrarOpt.get();
                pedidoParaEncerrar.setStatus("Pago");
                pedidoService.editarPedido(pedidoParaEncerrar);
                System.out.println("Pedido da mesa " + numeroMesa + " encerrado com sucesso!");
            } else {
                System.out.println("Nenhum pedido ativo encontrado para a mesa " + numeroMesa + ".");
            }
        }
    }

    private void excluir() {
        System.out.println("\n--- Selecione o Pedido para Excluir ---");
        Pedido pedidoParaExcluir = selecionarPedido();
        if (pedidoParaExcluir == null) return;

        System.out.println("\nTem certeza que deseja deletar esse pedido? Todas as informações relacionadas a esse pedido serão excluídas.");
        System.out.println("1. Sim");
        System.out.println("2. Não");

        int escolhafinal = ValidacaoHelper.lerInteiro(sc, "Confirme: ");

        if (escolhafinal == 1) {
            pedidoService.excluirPedido(pedidoParaExcluir.getId_pedido());
        } else {
            System.out.println("Operação cancelada!");
        }
    }

    private Mesa selecionarMesa(String acao) {
        System.out.println("\n--- Selecione a Mesa " + acao + " ---");
        List<Mesa> mesas = mesaService.listarMesa();

        if (mesas.isEmpty()) {
            System.out.println("Nenhuma mesa cadastrada.");
            return null;
        }

        int cont = 0;
        for (Mesa m : mesas) {
            cont++;
            System.out.println(cont + " - Mesa Número: " + m.getNumero());
        }
        System.out.println("0 - Cancelar");

        int escolha = ValidacaoHelper.lerInteiro(sc, "Escolha uma opção: ");
        if (escolha <= 0 || escolha > mesas.size()) return null;

        return mesas.get(escolha - 1);
    }

    private Funcionario selecionarFuncionario() {
        System.out.println("\n--- Selecione o Funcionario ---");
        List<Funcionario> funcionarios = funcionarioService.listarFuncionario();
        if (funcionarios.isEmpty()) return null;

        int cont = 0;
        for (Funcionario f : funcionarios) {
            cont++;
            System.out.println(cont + " - " + f.getNome());
        }
        System.out.println("0 - Cancelar");
        int escolha = ValidacaoHelper.lerInteiro(sc, "Escolha: ");
        if (escolha <= 0 || escolha > funcionarios.size()) return null;

        return funcionarios.get(escolha - 1);
    }

    private Item selecionarItem() {
        System.out.println("\n--- Selecione o Item ---");
        List<Item> items = itemService.listarItem();
        if (items.isEmpty()) return null;

        int cont = 0;
        for (Item i : items) {
            cont++;
            System.out.printf("%d - %s (R$ %.2f)\n", cont, i.getNome(), i.getPreco_venda());
        }
        System.out.println("0 - Cancelar");
        int escolha = ValidacaoHelper.lerInteiro(sc, "Escolha: ");
        if (escolha <= 0 || escolha > items.size()) return null;

        return items.get(escolha - 1);
    }
}