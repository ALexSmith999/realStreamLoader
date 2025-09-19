package com.example.loaders;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import com.example.repo.PgRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorLoaderTest {

    static class StubPgRepository extends PgRepository {
        List<Object[]> insertedAuthors = new ArrayList<>();

        StubPgRepository() throws SQLException {
            super("jdbc:postgresql://dummy:5432/dummy", "user", "pass");
        }

        @Override
        public void insertAuthor(String uid, int version, String name, String birthDate,
                                 String birthLocation, String description) {
            insertedAuthors.add(new Object[]{uid, version, name, birthDate, birthLocation, description});
        }

        @Override
        public void close() {}
    }

    @Test
    void testAuthorLoader() throws SQLException {
        Document mongoDoc = new Document();
        mongoDoc.put("uid", "author123");
        mongoDoc.put("version", 1);
        mongoDoc.put("link", "https://quotes.toscrape.com");
        mongoDoc.put("content", """
            <div class="author-details">
                <h3 class="author-title">Jane Doe</h3>
                <span class="author-born-date">01 January 1970</span>
                <span class="author-born-location">in Wonderland</span>
                <div class="author-description">Famous author.</div>
            </div>
        """);

        StubPgRepository stubRepo = new StubPgRepository();
        AuthorLoader loader = new AuthorLoader(stubRepo);
        loader.load(mongoDoc);

        assertEquals(1, stubRepo.insertedAuthors.size());
        Object[] inserted = stubRepo.insertedAuthors.get(0);
        assertEquals("author123", inserted[0]);
        assertEquals(1, inserted[1]);
        assertEquals("Jane Doe", inserted[2]);
        assertEquals("01 January 1970", inserted[3]);
        assertEquals("in Wonderland", inserted[4]);
        assertEquals("Famous author.", inserted[5]);
    }
}
