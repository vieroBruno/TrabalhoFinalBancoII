package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import model.Item;
import model.Pedido;
import model.PedidoItem;
import repository.PedidoRepository;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoPedidoRepository implements PedidoRepository {
    private final MongoCollection<Pedido> collection;
    private final MongoCollection<Item> itemCollection;

    public MongoPedidoRepository() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("pedidos", Pedido.class);
        this.itemCollection = db.getCollection("itens", Item.class);
    }

    private int getNextId() {
        Pedido last = collection.find().sort(Sorts.descending("_id")).first();
        return (last != null) ? last.getId_pedido() + 1 : 1;
    }

    @Override
    public int save(Pedido pedido) {
        int nextId = getNextId();
        pedido.setId_pedido(nextId);
        collection.insertOne(pedido);
        System.out.println("Pedido cadastrado com sucesso!");
        return nextId;
    }

    @Override
    public void update(Pedido pedido) {

        if (pedido.getItens() == null) {
            Pedido existing = findById(pedido.getId_pedido());
            if (existing != null) {
                pedido.setItens(existing.getItens());
            }
        }

        collection.replaceOne(Filters.eq("_id", pedido.getId_pedido()), pedido);
        System.out.println("Pedido alterado com sucesso!");
    }

    @Override
    public void delete(int id_pedido) {
        collection.deleteOne(Filters.eq("_id", id_pedido));
        System.out.println("Pedido excluído com sucesso!");
    }

    @Override
    public Pedido findById(int id_pedido) {
        return collection.find(Filters.eq("_id", id_pedido)).first();
    }

    @Override
    public List<Pedido> listAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public List<Pedido> listAllAtivos() {
        return collection.find(Filters.eq("status", "Ativo")).into(new ArrayList<>());
    }

    @Override
    public Map<Integer, Double> listarPedidosAtivosComTotal() {
        Map<Integer, Double> totais = new HashMap<>();

        List<Pedido> ativos = listAllAtivos();

        for (Pedido p : ativos) {
            double totalPedido = 0;
            if (p.getItens() != null) {
                for (PedidoItem pi : p.getItens()) {
                    Item item = itemCollection.find(Filters.eq("_id", pi.getId_item())).first();
                    if (item != null) {
                        totalPedido += item.getPreco_venda() * pi.getQuantidade();
                    }
                }
            }

            MongoDatabase db = MongoConnection.getDatabase();
            Document mesaDoc = db.getCollection("mesas").find(Filters.eq("_id", p.getId_mesa())).first();
            if (mesaDoc != null) {
                int numeroMesa = mesaDoc.getInteger("numero");
                totais.put(numeroMesa, totalPedido);
            }
        }
        return totais;
    }

    @Override
    public boolean existePedidoAtivoNaMesa(int id_mesa) {
        long count = collection.countDocuments(Filters.and(
                Filters.eq("fk_mesas_id_mesa", id_mesa),
                Filters.eq("status", "Ativo")
        ));

        if (count == 0) {
            count = collection.countDocuments(Filters.and(
                    Filters.eq("id_mesa", id_mesa),
                    Filters.eq("status", "Ativo")
            ));
        }

        return count > 0;
    }
}