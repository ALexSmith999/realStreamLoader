package com.example.loaders;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import com.example.repo.PgRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountryLoaderTest {

    static class StubPgRepository extends PgRepository {
        List<Object[]> insertedCountries = new ArrayList<>();

        StubPgRepository() throws SQLException {
            super("jdbc:postgresql://dummy:5432/dummy", "user", "pass");
        }

        @Override
        public void insertCountry(String uid, int version, String name, String capital,
                                  long population, long area) {
            insertedCountries.add(new Object[]{uid, version, name, capital, population, area});
        }

        @Override
        public void close() {}
    }

    @Test
    void testCountryLoader() throws SQLException {
        Document mongoDoc = new Document();
        mongoDoc.put("uid", "country123");
        mongoDoc.put("version", 1);
        mongoDoc.put("link", "otherlink");
        mongoDoc.put("content", """
            <div class="country">
                <h3 class="country-name">Utopia</h3>
                <span class="country-capital">Dream City</span>
                <span class="country-population">1000000</span>
                <span class="country-area">50000</span>
            </div>
        """);

        StubPgRepository stubRepo = new StubPgRepository();
        CountryLoader loader = new CountryLoader(stubRepo);
        loader.load(mongoDoc);

        assertEquals(1, stubRepo.insertedCountries.size());
        Object[] inserted = stubRepo.insertedCountries.get(0);
        assertEquals("country123", inserted[0]);
        assertEquals(1, inserted[1]);
        assertEquals("Utopia", inserted[2]);
        assertEquals("Dream City", inserted[3]);
        assertEquals(1_000_000L, inserted[4]);
        assertEquals(50_000L, inserted[5]);
    }
}
