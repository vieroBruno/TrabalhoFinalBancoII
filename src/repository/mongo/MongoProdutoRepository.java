package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import model.Produto;
import org.bson.types.ObjectId;
import repository.ProdutoRepository;

import java.util.ArrayList;
import java.util.List;

public class MongoProdutoRepository implements ProdutoRepository {

    private final MongoCollection<Produto> collection;

    public MongoProdutoRepository() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("produtos", Produto.class);
    }

    @Override
    public void save(Produto produto) {
        try {
            collection.insertOne(produto);
            System.out.println("Produto cadastrado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
        }
    }

    @Override
    public void update(Produto produto) {
        try {
            collection.replaceOne(Filters.eq("_id", produto.getId()), produto);
            System.out.println("Produto alterado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao alterar produto: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id_produto) {
        try {
            collection.deleteOne(Filters.eq("_id", new ObjectId(id_produto)));
            System.out.println("Produto excluído com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao excluir produto: " + e.getMessage());
        }
    }

    @Override
    public Produto findById(String id_produto) {
        try {
            return collection.find(Filters.eq("_id", new ObjectId(id_produto))).first();
        } catch (Exception e) {
            System.err.println("Erro ao buscar produto por ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Produto findByLegacyId(int id_produto_legado) {
        try {
            return collection.find(Filters.eq("id_produto", id_produto_legado)).first();
        } catch (Exception e) {
            System.err.println("Erro ao buscar produto por ID legado: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Produto> listAll() {
        List<Produto> produtos = new ArrayList<>();
        try {
            collection.find().into(produtos);
        } catch (Exception e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
        }
        return produtos;
    }
}