package com.example.loaders;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import com.example.repo.PgRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookLoaderTest {

    // Stub repository that overrides PgRepository and avoids actual DB writes
    static class StubPgRepository extends PgRepository {
        List<Object[]> insertedBooks = new ArrayList<>();

        StubPgRepository() throws SQLException {
            super("jdbc:postgresql://dummy:5432/dummy", "user", "pass");
        }

        @Override
        public void insertBook(String uid, int version, String title, String description,
                               String upc, String productType, String priceExclTax,
                               String priceInclTax, String tax, String availability,
                               int numberOfReviews, String rating) {
            insertedBooks.add(new Object[]{uid, version, title, description, upc, productType,
                    priceExclTax, priceInclTax, tax, availability, numberOfReviews, rating});
        }

        @Override
        public void close() {
            // no-op
        }
    }

    @Test
    void testBookLoader() throws SQLException {
        // Sample MongoDB document
        Document mongoDoc = new Document();
        mongoDoc.put("uid", "book123");
        mongoDoc.put("version", 1);
        mongoDoc.put("content", """
            <html>
            <body>
            <div class="product_main">
                <h1>Sample Book</h1>
            </div>
            <div id="product_description"></div>
            <p>This is a description.</p>
            <table class="table table-striped">
                <tr><th>UPC</th><td>123456</td></tr>
                <tr><th>Product Type</th><td>Book</td></tr>
                <tr><th>Price (excl. tax)</th><td>10.00</td></tr>
                <tr><th>Price (incl. tax)</th><td>12.00</td></tr>
                <tr><th>Tax</th><td>2.00</td></tr>
                <tr><th>Availability</th><td>In stock</td></tr>
                <tr><th>Number of reviews</th><td>5</td></tr>
            </table>
            <p class="star-rating Three"></p>
            </body>
            </html>
        """);

        // Create stub repo and loader
        StubPgRepository stubRepo = new StubPgRepository();
        BookLoader loader = new BookLoader(stubRepo);

        // Execute loader
        loader.load(mongoDoc);

        // Verify the inserted book
        assertEquals(1, stubRepo.insertedBooks.size());
        Object[] inserted = stubRepo.insertedBooks.get(0);
        assertEquals("book123", inserted[0]);
        assertEquals(1, inserted[1]);
        assertEquals("Sample Book", inserted[2]);
        assertEquals("This is a description.", inserted[3]);
        assertEquals("123456", inserted[4]);
        assertEquals("Book", inserted[5]);
        assertEquals("10.00", inserted[6]);
        assertEquals("12.00", inserted[7]);
        assertEquals("2.00", inserted[8]);
        assertEquals("In stock", inserted[9]);
        assertEquals(5, inserted[10]);
        assertEquals("Three", inserted[11]);
    }
}
