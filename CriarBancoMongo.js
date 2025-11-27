db = db.getSiblingDB('restaurantev2');

db.funcionarios.drop();
db.mesas.drop();
db.produtos.drop();
db.itens.drop();
db.pedidos.drop();

db.funcionarios.insertMany([
    { _id: 1, nome: 'João Silva', cargo: 'Garçom', salario: 2200.00, telefone: '47999887766' },
    { _id: 2, nome: 'Maria Oliveira', cargo: 'Garçonete', salario: 2250.00, telefone: '47988776655' },
    { _id: 3, nome: 'Carlos Pereira', cargo: 'Gerente', salario: 4500.00, telefone: '47977665544' }
]);

db.mesas.insertMany([
    { _id: 1, numero: 1, capacidade: 4 },
    { _id: 2, numero: 2, capacidade: 2 },
    { _id: 3, numero: 3, capacidade: 6 },
    { _id: 4, numero: 4, capacidade: 4 },
    { _id: 5, numero: 5, capacidade: 8 }
]);

db.produtos.insertMany([
    { _id: 1, nome: 'Batata', unidade_medida: 'Quilogramas', quantidade: 20.0 },
    { _id: 2, nome: 'Óleo de Soja', unidade_medida: 'Litros', quantidade: 10.0 },
    { _id: 3, nome: 'Sal', unidade_medida: 'Gramas', quantidade: 50.0 },
    { _id: 4, nome: 'Pão de Hambúrguer', unidade_medida: 'Unidades', quantidade: 50 },
    { _id: 5, nome: 'Carne de Hambúrguer', unidade_medida: 'Gramas', quantidade: 7500.0 },
    { _id: 6, nome: 'Queijo Cheddar', unidade_medida: 'Gramas', quantidade: 2000.0 },
    { _id: 7, nome: 'Alface', unidade_medida: 'Gramas', quantidade: 1000.0 },
    { _id: 8, nome: 'Tomate', unidade_medida: 'Gramas', quantidade: 2000.0 },
    { _id: 9, nome: 'Refrigerante Cola', unidade_medida: 'Unidades', quantidade: 300 }
]);

db.itens.insertMany([
    {
        _id: 1,
        nome: 'Batata Frita Média',
        preco_venda: 15.00,
        descricao: 'Porção de 300g de batata frita crocante',
        receita: [
            { id_produto: 1, quantidade_necessaria: 0.3 },
            { id_produto: 3, quantidade_necessaria: 2.5 }
        ]
    },
    {
        _id: 2,
        nome: 'Hambúrguer Clássico',
        preco_venda: 25.00,
        descricao: 'Pão, carne 150g e queijo cheddar',
        receita: [
            { id_produto: 4, quantidade_necessaria: 1 },
            { id_produto: 5, quantidade_necessaria: 150 },
            { id_produto: 6, quantidade_necessaria: 20 }
        ]
    },
    {
        _id: 3,
        nome: 'X-Salada',
        preco_venda: 28.00,
        descricao: 'Pão, carne 150g, queijo, alface e tomate',
        receita: [
            { id_produto: 4, quantidade_necessaria: 1 },
            { id_produto: 5, quantidade_necessaria: 150 },
            { id_produto: 6, quantidade_necessaria: 20 },
            { id_produto: 7, quantidade_necessaria: 30 },
            { id_produto: 8, quantidade_necessaria: 40 }
        ]
    },
    {
        _id: 4,
        nome: 'Refrigerante Lata',
        preco_venda: 6.00,
        descricao: 'Lata de 350ml',
        receita: [
            { id_produto: 9, quantidade_necessaria: 1 }
        ]
    }
]);

db.pedidos.insertMany([
    {
        _id: 1,
        data_pedido: new Date('2025-09-20T00:00:00Z'),
        status: 'Pago',
        id_mesa: 1,
        id_funcionario: 1,
        itens: [
            { id_item: 1, quantidade: 2 },
            { id_item: 4, quantidade: 2 }
        ]
    },
    {
        _id: 2,
        data_pedido: new Date('2025-09-21T00:00:00Z'),
        status: 'Pago',
        id_mesa: 3,
        id_funcionario: 2,
        itens: [
            { id_item: 2, quantidade: 1 },
            { id_item: 1, quantidade: 1 },
            { id_item: 4, quantidade: 1 }
        ]
    },
    {
        _id: 3,
        data_pedido: new Date('2025-09-21T00:00:00Z'),
        status: 'Cancelado',
        id_mesa: 2,
        id_funcionario: 1,
        itens: [
            { id_item: 3, quantidade: 1 }
        ]
    },
    {
        _id: 4,
        data_pedido: new Date('2025-09-22T00:00:00Z'),
        status: 'Pago',
        id_mesa: 5,
        id_funcionario: 2,
        itens: [
            { id_item: 3, quantidade: 1 },
            { id_item: 1, quantidade: 1 },
            { id_item: 4, quantidade: 1 }
        ]
    },
    {
        _id: 5,
        data_pedido: new Date('2025-09-28T00:00:00Z'),
        status: 'Pago',
        id_mesa: 1,
        id_funcionario: 1,
        itens: [
            { id_item: 2, quantidade: 2 },
            { id_item: 4, quantidade: 2 }
        ]
    },
    {
        _id: 6,
        data_pedido: new Date('2025-09-29T00:00:00Z'),
        status: 'Ativo',
        id_mesa: 4,
        id_funcionario: 2,
        itens: [
            { id_item: 1, quantidade: 1 }
        ]
    }
]);
