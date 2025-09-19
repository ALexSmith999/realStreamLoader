package com.example;

import com.example.config.Config;
import com.example.loaders.*;
import com.example.repo.PgRepository;
import com.mongodb.client.*;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;

import java.util.List;

public class ChangeStreamApp {

    public static void main(String[] args) throws Exception {
        // Load configuration
        Config config = new Config("src/main/resources/app.properties");

        String mongoUri = config.getMongoUri();
        String mongoDatabase = config.getMongoDatabase();
        String mongoCollection = config.getMongoCollection();

        String pgUrl = config.getPgUrl();
        String pgUser = config.getPgUser();
        String pgPassword = config.getPgPassword();

        // Initialize PostgreSQL repository
        PgRepository repo = new PgRepository(pgUrl, pgUser, pgPassword);

        // Initialize loaders
        Dispatcher dispatcher = new Dispatcher(List.of(
                new BookLoader(repo),
                new AuthorLoader(repo),
                new QuoteLoader(repo),
                new CountryLoader(repo),
                new ProductLoader(repo),
                new TeamLoader(repo)
        ));

        // Initialize MongoDB client and watch collection
        MongoClient mongoClient = MongoClients.create(mongoUri);
        MongoDatabase db = mongoClient.getDatabase(mongoDatabase);
        MongoCollection<Document> collection = db.getCollection(mongoCollection);

        System.out.println("Watching MongoDB changes...");

        try (MongoChangeStreamCursor<ChangeStreamDocument<Document>> cursor = collection.watch().cursor()) {
            while (cursor.hasNext()) {
                ChangeStreamDocument<Document> change = cursor.next();
                Document fullDoc = change.getFullDocument();
                if (fullDoc != null) {
                    dispatcher.dispatch(fullDoc); // Forward document to proper loader
                }
            }
        }
    }
}
