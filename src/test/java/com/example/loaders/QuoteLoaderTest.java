package com.example.loaders;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import com.example.repo.PgRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuoteLoaderTest {

    static class StubPgRepository extends PgRepository {
        List<Object[]> insertedQuotes = new ArrayList<>();

        StubPgRepository() throws SQLException {
            super("jdbc:postgresql://dummy:5432/dummy", "user", "pass");
        }

        @Override
        public void insertQuote(String uid, int version, Integer authorId, String quoteText, String[] tags) {
            insertedQuotes.add(new Object[]{uid, version, authorId, quoteText, tags});
        }

        @Override
        public void close() {}
    }

    @Test
    void testQuoteLoader() throws SQLException {
        Document mongoDoc = new Document();
        mongoDoc.put("uid", "quote123");
        mongoDoc.put("version", 1);
        mongoDoc.put("link", "https://quotes.toscrape.com");
        mongoDoc.put("content", """
            <div class="quote">
                <span class="text">"Sample quote"</span>
                <small class="author">Jane Doe</small>
                <div class="tags">
                    <a class="tag">life</a>
                    <a class="tag">happiness</a>
                </div>
            </div>
        """);

        StubPgRepository stubRepo = new StubPgRepository();
        QuoteLoader loader = new QuoteLoader(stubRepo);
        loader.load(mongoDoc);

        assertEquals(1, stubRepo.insertedQuotes.size());
        Object[] inserted = stubRepo.insertedQuotes.get(0);
        assertEquals("quote123", inserted[0]);
        assertEquals(1, inserted[1]);
        assertEquals(null, inserted[2]); // authorId stub
        assertEquals("\"Sample quote\"", inserted[3]);
        String[] tags = (String[]) inserted[4];
        assertEquals(2, tags.length);
        assertEquals("life", tags[0]);
        assertEquals("happiness", tags[1]);
    }
}
