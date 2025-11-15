package repository.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    static {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(com.mongodb.MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        String uri = "mongodb://localhost:27017";

        try {
            mongoClient = MongoClients.create(uri);

            database = mongoClient.getDatabase("restaurantev2")
                    .withCodecRegistry(codecRegistry);

            System.out.println("Conexão com MongoDB estabelecida com sucesso.");

        } catch (Exception e) {
            System.err.println("Erro ao conectar ao MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Conexão com MongoDB fechada.");
        }
    }
}