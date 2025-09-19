package com.example.loaders;

import com.example.repo.PgRepository;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TeamLoader implements Loader {
    private final PgRepository repo;

    public TeamLoader(PgRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean supports(String link) {
        return link.startsWith("https://www.scrapethissite.com/pages/teams/");
    }

    @Override
    public void load(Document mongoDoc) {
        String content = mongoDoc.getString("content");
        if (content == null || content.isEmpty()) return;

        org.jsoup.nodes.Document html = Jsoup.parse(content);

        Elements teams = html.select("tr.team");
        for (Element team : teams) {
            Elements tds = team.select("td");
            if (tds.size() < 9) continue; // skip incomplete rows

            String name = tds.get(0).text();
            Integer year = parseIntSafe(tds.get(1).text());
            Integer wins = parseIntSafe(tds.get(2).text());
            Integer losses = parseIntSafe(tds.get(3).text());
            Integer otLosses = parseIntSafe(tds.get(4).text());
            Double winPct = parseDoubleSafe(tds.get(5).text());
            Integer goalsFor = parseIntSafe(tds.get(6).text());
            Integer goalsAgainst = parseIntSafe(tds.get(7).text());
            Integer diff = parseIntSafe(tds.get(8).text());

            repo.insertTeam(
                    mongoDoc.getString("uid"),
                    mongoDoc.getInteger("version", 1),
                    name, year, wins, losses, otLosses, winPct,
                    goalsFor, goalsAgainst, diff
            );
        }
    }

    private Integer parseIntSafe(String value) {
        try { return Integer.parseInt(value.trim()); } catch (Exception e) { return 0; }
    }

    private Double parseDoubleSafe(String value) {
        try { return Double.parseDouble(value.trim()); } catch (Exception e) { return 0.0; }
    }
}
