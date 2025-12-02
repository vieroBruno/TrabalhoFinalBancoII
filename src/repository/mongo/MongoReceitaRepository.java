package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import model.Item;
import model.Produto;
import model.Receita;
import repository.ReceitaRepository;

import java.util.ArrayList;
import java.util.List;

public class MongoReceitaRepository implements ReceitaRepository {
    private final MongoCollection<Item> collection;
    private final MongoCollection<Produto> produtoCollection;

    public MongoReceitaRepository() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("itens", Item.class);
        this.produtoCollection = db.getCollection("produtos", Produto.class);
    }

    @Override
    public void save(Receita receita) {
        collection.updateOne(
                Filters.eq("_id", receita.getId_item()),
                Updates.push("receita", receita)
        );
        System.out.println("Produto adicionado a receita com sucesso!");
    }

    @Override
    public void update(Receita receita) {
        collection.updateOne(
                Filters.and(
                        Filters.eq("_id", receita.getId_item()),
                        Filters.eq("receita.id_produto", receita.getId_produto())
                ),
                Updates.set("receita.$.quantidade_necessaria", receita.getQuantidade())
        );
        System.out.println("Quantidade do produto alterada com sucesso!");
    }

    @Override
    public void delete(int id_item, int id_produto) {
        collection.updateOne(
                Filters.eq("_id", id_item),
                Updates.pull("receita", new org.bson.Document("id_produto", id_produto))
        );
        System.out.println("Produto removido da receita com sucesso!");
    }

    @Override
    public Receita findById(int id_receita) {
        return null;
    }

    @Override
    public List<Produto> listarProdutosItem(int id_item) {
        Item item = collection.find(Filters.eq("_id", id_item)).first();
        List<Produto> produtosDetalhados = new ArrayList<>();

        if (item != null && item.getReceita() != null) {
            for (Receita r : item.getReceita()) {
                Produto p = produtoCollection.find(Filters.eq("_id", r.getId_produto())).first();
                if (p != null) {
                    Produto temp = new Produto(p.getNome(), p.getUnidade_medida(), r.getQuantidade());
                    temp.setId_produto(p.getId_produto());
                    produtosDetalhados.add(temp);
                }
            }
        }
        return produtosDetalhados;
    }

    @Override
    public boolean produtoJaExisteNaReceita(int id_item, int id_produto) {
        Item item = collection.find(Filters.and(
                Filters.eq("_id", id_item),
                Filters.eq("receita.id_produto", id_produto)
        )).first();
        return item != null;
    }
}