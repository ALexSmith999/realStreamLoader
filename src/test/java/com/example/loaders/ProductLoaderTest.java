package com.example.loaders;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import com.example.repo.PgRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductLoaderTest {

    static class StubPgRepository extends PgRepository {
        List<Object[]> insertedProducts = new ArrayList<>();

        StubPgRepository() throws SQLException {
            super("jdbc:postgresql://dummy:5432/dummy", "user", "pass");
        }

        @Override
        public void insertProduct(String uid, int version, String name, String developer,
                                  String platform, String type, String price,
                                  String stock, String genres) {
            insertedProducts.add(new Object[]{uid, version, name, developer, platform, type, price, stock, genres});
        }

        @Override
        public void close() {}
    }

    @Test
    void testProductLoader() throws SQLException {
        Document mongoDoc = new Document();
        mongoDoc.put("uid", "product123");
        mongoDoc.put("version", 1);
        mongoDoc.put("link", "https://sandbox.oxylabs.io/products");
        mongoDoc.put("content", """
            <html>
            <head>
            <title>Game Title | Sandbox</title>
            <meta property="og:developer" content="Dev Studio"/>
            <meta property="og:platform" content="PC"/>
            <meta property="og:type" content="Game"/>
            <meta property="og:price" content="$59.99"/>
            <meta property="og:availability" content="In stock"/>
            <meta property="og:genre" content="Action"/>
            </head>
            </html>
        """);

        StubPgRepository stubRepo = new StubPgRepository();
        ProductLoader loader = new ProductLoader(stubRepo);
        loader.load(mongoDoc);

        assertEquals(1, stubRepo.insertedProducts.size());
        Object[] inserted = stubRepo.insertedProducts.get(0);
        assertEquals("product123", inserted[0]);
        assertEquals(1, inserted[1]);
        assertEquals("Game Title", inserted[2]);
        assertEquals("Dev Studio", inserted[3]);
        assertEquals("PC", inserted[4]);
        assertEquals("Game", inserted[5]);
        assertEquals("$59.99", inserted[6]);
        assertEquals("In stock", inserted[7]);
        assertEquals("Action", inserted[8]);
    }
}
