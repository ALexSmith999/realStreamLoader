package com.example;

import com.example.config.Config;
import com.example.loaders.*;
import com.example.repo.PgRepository;
import com.mongodb.client.*;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ChangeStreamApp {

    private static final Logger log = LoggerFactory.getLogger(ChangeStreamApp.class);

    public static void main(String[] args) throws Exception {
        // Load configuration
        Config config = new Config("src/main/resources/app.properties");
        log.info("Loaded configuration from app.properties");

        String mongoUri = config.getMongoUri();
        String mongoDatabase = config.getMongoDatabase();
        String mongoCollection = config.getMongoCollection();

        String pgUrl = config.getPgUrl();
        String pgUser = config.getPgUser();
        String pgPassword = config.getPgPassword();

        // Initialize PostgreSQL repository
        PgRepository repo = new PgRepository(pgUrl, pgUser, pgPassword);
        log.info("Initialized PostgreSQL repository");

        // Initialize loaders
        Dispatcher dispatcher = new Dispatcher(List.of(
                new BookLoader(repo),
                new AuthorLoader(repo),
                new QuoteLoader(repo),
                new CountryLoader(repo),
                new ProductLoader(repo),
                new TeamLoader(repo)
        ));
        log.info("Initialized loaders and dispatcher");

        // Initialize MongoDB client and watch collection
        MongoClient mongoClient = MongoClients.create(mongoUri);
        MongoDatabase db = mongoClient.getDatabase(mongoDatabase);
        MongoCollection<Document> collection = db.getCollection(mongoCollection);

        log.info("Watching MongoDB changes on {}.{}", mongoDatabase, mongoCollection);

        try (MongoChangeStreamCursor<ChangeStreamDocument<Document>> cursor = collection.watch().cursor()) {
            while (cursor.hasNext()) {
                ChangeStreamDocument<Document> change = cursor.next();
                Document fullDoc = change.getFullDocument();
                if (fullDoc != null) {
                    log.info("Detected new/updated document with UID: {}", fullDoc.getString("uid"));
                    try {
                        dispatcher.dispatch(fullDoc); // Forward document to proper loader
                        log.info("Document {} dispatched successfully", fullDoc.getString("uid"));
                    } catch (Exception e) {
                        log.error("Error dispatching document {}: {}", fullDoc.getString("uid"), e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error watching MongoDB collection: {}", e.getMessage(), e);
        } finally {
            mongoClient.close();
            repo.close();
            log.info("Resources closed, exiting ChangeStreamApp");
        }
    }
}
