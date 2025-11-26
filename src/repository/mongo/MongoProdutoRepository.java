package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import model.Produto;
import repository.ProdutoRepository;

import java.util.ArrayList;
import java.util.List;

public class MongoProdutoRepository implements ProdutoRepository {
    private final MongoCollection<Produto> collection;

    public MongoProdutoRepository() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("produtos", Produto.class);
    }

    private int getNextId() {
        Produto last = collection.find().sort(Sorts.descending("_id")).first();
        return (last != null) ? last.getId_produto() + 1 : 1;
    }

    @Override
    public void save(Produto produto) {
        if (produto.getId_produto() == 0) {
            produto.setId_produto(getNextId());
        }
        collection.insertOne(produto);
        System.out.println("Produto cadastrado com sucesso!");
    }

    @Override
    public void update(Produto produto) {
        collection.replaceOne(Filters.eq("_id", produto.getId_produto()), produto);
        System.out.println("Produto alterado com sucesso!");
    }

    @Override
    public void delete(int id_produto) {
        collection.deleteOne(Filters.eq("_id", id_produto));
        System.out.println("Produto excluído com sucesso!");
    }

    @Override
    public Produto findById(int id_produto) {
        return collection.find(Filters.eq("_id", id_produto)).first();
    }

    @Override
    public List<Produto> listAll() {
        return collection.find().into(new ArrayList<>());
    }
}