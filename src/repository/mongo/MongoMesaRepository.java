package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import model.Mesa;
import repository.MesaRepository;

import java.util.ArrayList;
import java.util.List;

public class MongoMesaRepository implements MesaRepository {
    private final MongoCollection<Mesa> collection;

    public MongoMesaRepository() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("mesas", Mesa.class);
    }

    private int getNextId() {
        Mesa last = collection.find().sort(Sorts.descending("_id")).first();
        return (last != null) ? last.getId_mesa() + 1 : 1;
    }

    @Override
    public void save(Mesa mesa) {
        mesa.setId_mesa(getNextId());
        collection.insertOne(mesa);
        System.out.println("Mesa cadastrada com sucesso!");
    }

    @Override
    public void update(Mesa mesa) {
        collection.replaceOne(Filters.eq("_id", mesa.getId_mesa()), mesa);
        System.out.println("Mesa alterada com sucesso!");
    }

    @Override
    public void delete(int id_mesa) {
        collection.deleteOne(Filters.eq("_id", id_mesa));
        System.out.println("Mesa excluída com sucesso!");
    }

    @Override
    public Mesa findById(int id_mesa) {
        return collection.find(Filters.eq("_id", id_mesa)).first();
    }

    @Override
    public List<Mesa> listAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public Mesa findByNumero(int numero) {
        return collection.find(Filters.eq("numero", numero)).first();
    }
}