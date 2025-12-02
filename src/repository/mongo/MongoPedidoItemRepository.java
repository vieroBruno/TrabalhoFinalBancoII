package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import model.Pedido;
import model.PedidoItem;
import repository.PedidoItemRepository;

public class MongoPedidoItemRepository implements PedidoItemRepository {
    private final MongoCollection<Pedido> collection;

    public MongoPedidoItemRepository() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("pedidos", Pedido.class);
    }

    @Override
    public void save(PedidoItem pedidoItem) {
        collection.updateOne(
                Filters.eq("_id", pedidoItem.getId_pedido()),
                Updates.push("itens", pedidoItem)
        );
    }
}