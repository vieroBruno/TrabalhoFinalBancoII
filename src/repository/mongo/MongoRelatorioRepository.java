package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import model.Produto;
import model.RelatorioItem;
import repository.RelatorioRepository;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MongoRelatorioRepository implements RelatorioRepository {

    private final MongoCollection<Produto> produtosCollection;
    private final MongoCollection<Document> pedidosCollection;
    private final MongoCollection<Document> itensCollection;

    public MongoRelatorioRepository() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.produtosCollection = db.getCollection("produtos", Produto.class);
        this.pedidosCollection = db.getCollection("pedidos");
        this.itensCollection = db.getCollection("itens");
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public double vendasPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(
                        Filters.and(
                                Filters.eq("status", "Pago"),
                                Filters.gte("data_pedido", toDate(inicio)),
                                Filters.lte("data_pedido", toDate(fim))
                        )
                ),
                Aggregates.unwind("$itens"),
                Aggregates.lookup(
                        "itens",
                        "itens.id_item",
                        "id_item",
                        "item_info"
                ),
                Aggregates.unwind("$item_info"),
                Aggregates.group(null,
                        Accumulators.sum("faturamento_total",
                                new Document("$multiply", Arrays.asList("$itens.quantidade", "$item_info.preco_venda"))
                        )
                )
        );

        Document result = pedidosCollection.aggregate(pipeline).first();
        return (result != null) ? result.getDouble("faturamento_total") : 0.0;
    }

    @Override
    public List<RelatorioItem> itensMaisVendidos(LocalDate inicio, LocalDate fim) {
        List<RelatorioItem> ranking = new ArrayList<>();
        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(
                        Filters.and(
                                Filters.gte("data_pedido", toDate(inicio)),
                                Filters.lte("data_pedido", toDate(fim))
                        )
                ),
                Aggregates.unwind("$itens"),
                Aggregates.group("$itens.id_item",
                        Accumulators.sum("quantidade_total", "$itens.quantidade")
                ),
                Aggregates.lookup(
                        "itens",
                        "_id",
                        "id_item",
                        "item_info"
                ),
                Aggregates.unwind("$item_info"),
                Aggregates.sort(Sorts.descending("quantidade_total")),
                Aggregates.project(
                        Projections.fields(
                                Projections.excludeId(),
                                Projections.computed("nome", "$item_info.nome"),
                                Projections.computed("descricao", "$item_info.descricao"),
                                Projections.computed("precoVenda", "$item_info.preco_venda"),
                                Projections.computed("quantidadeVendida", "$quantidade_total")
                        )
                )
        );


        for (Document doc : pedidosCollection.aggregate(pipeline)) {
            RelatorioItem item = new RelatorioItem();
            item.setNome(doc.getString("nome"));
            item.setDescricao(doc.getString("descricao"));
            item.setPrecoVenda(doc.getDouble("precoVenda"));
            item.setQuantidadeVendida(doc.getInteger("quantidadeVendida"));
            ranking.add(item);
        }
        return ranking;
    }

    @Override
    public List<RelatorioItem> itensQueMaisGeramReceita(LocalDate inicio, LocalDate fim) {
        List<RelatorioItem> ranking = new ArrayList<>();
        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(
                        Filters.and(
                                Filters.gte("data_pedido", toDate(inicio)),
                                Filters.lte("data_pedido", toDate(fim))
                        )
                ),
                Aggregates.unwind("$itens"),
                Aggregates.lookup("itens", "itens.id_item", "id_item", "item_info"),
                Aggregates.unwind("$item_info"),
                Aggregates.group("$itens.id_item",
                        Accumulators.first("nome", "$item_info.nome"),
                        Accumulators.first("descricao", "$item_info.descricao"),
                        Accumulators.first("precoVenda", "$item_info.preco_venda"),
                        Accumulators.sum("receita_total",
                                new Document("$multiply", Arrays.asList("$itens.quantidade", "$item_info.preco_venda"))
                        )
                ),
                Aggregates.sort(Sorts.descending("receita_total")),
                Aggregates.project(
                        Projections.fields(
                                Projections.excludeId(),
                                Projections.computed("nome", "$nome"),
                                Projections.computed("descricao", "$descricao"),
                                Projections.computed("precoVenda", "$precoVenda"),
                                Projections.computed("receitaGerada", "$receita_total")
                        )
                )
        );

        for (Document doc : pedidosCollection.aggregate(pipeline)) {
            RelatorioItem item = new RelatorioItem();
            item.setNome(doc.getString("nome"));
            item.setDescricao(doc.getString("descricao"));
            item.setPrecoVenda(doc.getDouble("precoVenda"));
            item.setReceitaGerada(doc.getDouble("receitaGerada"));
            ranking.add(item);
        }
        return ranking;
    }

    @Override
    public List<Produto> relatorioEstoqueBaixo(double nivelMinimo) {
        List<Produto> produtos = new ArrayList<>();
        try {
            produtosCollection.find(Filters.lte("quantidade", nivelMinimo)).into(produtos);
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório de estoque baixo: " + e.getMessage());
        }
        return produtos;
    }
}