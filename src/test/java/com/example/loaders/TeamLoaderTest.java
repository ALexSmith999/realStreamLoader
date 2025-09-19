package com.example.loaders;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import com.example.repo.PgRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TeamLoaderTest {

    static class StubPgRepository extends PgRepository {
        List<Object[]> insertedTeams = new ArrayList<>();

        StubPgRepository() throws SQLException {
            super("jdbc:postgresql://dummy:5432/dummy", "user", "pass");
        }

        @Override
        public void insertTeam(String uid, int version, String name, int year, int wins,
                               int losses, int otLosses, double winPct,
                               int goalsFor, int goalsAgainst, int goalDiff) {
            insertedTeams.add(new Object[]{uid, version, name, year, wins, losses, otLosses,
                    winPct, goalsFor, goalsAgainst, goalDiff});
        }

        @Override
        public void close() {}
    }

    @Test
    void testTeamLoader() throws SQLException {
        Document mongoDoc = new Document();
        mongoDoc.put("uid", "team123");
        mongoDoc.put("version", 1);
        mongoDoc.put("link", "sportslink");
        mongoDoc.put("content", """
            <div class="team">
                <span class="team-name">Dream Team</span>
                <span class="team-year">2025</span>
                <span class="team-wins">10</span>
                <span class="team-losses">5</span>
                <span class="team-ot-losses">1</span>
                <span class="team-win-pct">0.667</span>
                <span class="team-goals-for">50</span>
                <span class="team-goals-against">30</span>
                <span class="team-goal-diff">20</span>
            </div>
        """);

        StubPgRepository stubRepo = new StubPgRepository();
        TeamLoader loader = new TeamLoader(stubRepo);
        loader.load(mongoDoc);

        assertEquals(1, stubRepo.insertedTeams.size());
        Object[] inserted = stubRepo.insertedTeams.get(0);
        assertEquals("team123", inserted[0]);
        assertEquals(1, inserted[1]);
        assertEquals("Dream Team", inserted[2]);
        assertEquals(2025, inserted[3]);
        assertEquals(10, inserted[4]);
        assertEquals(5, inserted[5]);
        assertEquals(1, inserted[6]);
        assertEquals(0.667, inserted[7]);
        assertEquals(50, inserted[8]);
        assertEquals(30, inserted[9]);
        assertEquals(20, inserted[10]);
    }
}
