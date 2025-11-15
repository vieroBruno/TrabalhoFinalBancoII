package service;

import model.Produto;
// Importa a nova implementação do repositório
import repository.mongo.MongoProdutoRepository;
// Importa a interface
import repository.ProdutoRepository;

import java.util.List;

public class ProdutoService {

    // Usamos a INTERFACE
    private final ProdutoRepository repository;

    public ProdutoService() {
        // Instanciamos a IMPLEMENTAÇÃO do Mongo
        this.repository = new MongoProdutoRepository();
    }

    public void cadastrarProduto(Produto produto) {
        repository.save(produto);
    }

    public void editarProduto(Produto produto) {
        repository.update(produto);
    }

    public List<Produto> listarProduto() {
        return repository.listAll();
    }

    public void excluirProduto(String id_produto) {
        repository.delete(id_produto);
    }

    public Produto findById(String id_produto) {
        return repository.findById(id_produto);
    }

    public Produto findByLegacyId(int id_produto_legado) {
        return repository.findByLegacyId(id_produto_legado);
    }
}