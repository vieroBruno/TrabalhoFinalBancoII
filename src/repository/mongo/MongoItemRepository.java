package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import model.Item;
import repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;

public class MongoItemRepository implements ItemRepository {
    private final MongoCollection<Item> collection;

    public MongoItemRepository() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("itens", Item.class);
    }

    private int getNextId() {
        Item last = collection.find().sort(Sorts.descending("_id")).first();
        return (last != null) ? last.getId_item() + 1 : 1;
    }

    @Override
    public void save(Item item) {
        item.setId_item(getNextId());
        collection.insertOne(item);
        System.out.println("Item cadastrado com sucesso!");
    }

    @Override
    public void update(Item item) {
        Item existing = findById(item.getId_item());
        if(existing != null) {
            item.setReceita(existing.getReceita());
        }
        collection.replaceOne(Filters.eq("_id", item.getId_item()), item);
        System.out.println("Item alterado com sucesso!");
    }

    @Override
    public void delete(int id_item) {
        collection.deleteOne(Filters.eq("_id", id_item));
        System.out.println("Item excluído com sucesso!");
    }

    @Override
    public Item findById(int id_item) {
        return collection.find(Filters.eq("_id", id_item)).first();
    }

    @Override
    public List<Item> listAll() {
        return collection.find().into(new ArrayList<>());
    }
}