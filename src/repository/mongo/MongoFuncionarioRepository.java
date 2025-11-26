package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import model.Funcionario;
import repository.FuncionarioRepository;

import java.util.ArrayList;
import java.util.List;

public class MongoFuncionarioRepository implements FuncionarioRepository {
    private final MongoCollection<Funcionario> collection;

    public MongoFuncionarioRepository() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("funcionarios", Funcionario.class);
    }

    private int getNextId() {
        Funcionario last = collection.find().sort(Sorts.descending("_id")).first();
        return (last != null) ? last.getIdFuncionario() + 1 : 1;
    }

    @Override
    public void save(Funcionario funcionario) {
        funcionario.setIdFuncionario(getNextId());
        collection.insertOne(funcionario);
        System.out.println("Funcionário cadastrado com sucesso!");
    }

    @Override
    public void update(Funcionario funcionario) {
        collection.replaceOne(Filters.eq("_id", funcionario.getIdFuncionario()), funcionario);
        System.out.println("Funcionário alterado com sucesso!");
    }

    @Override
    public void delete(int id_funcionario) {
        collection.deleteOne(Filters.eq("_id", id_funcionario));
        System.out.println("Funcionário excluído com sucesso!");
    }

    @Override
    public Funcionario findById(int id_funcionario) {
        return collection.find(Filters.eq("_id", id_funcionario)).first();
    }

    @Override
    public List<Funcionario> listAll() {
        return collection.find().into(new ArrayList<>());
    }
}