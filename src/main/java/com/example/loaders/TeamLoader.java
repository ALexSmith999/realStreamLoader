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
        return link.contains("scrapethissite.com/pages/"); // or adjust as needed
    }

    @Override
    public void load(Document mongoDoc) {
        String content = mongoDoc.getString("content");
        org.jsoup.nodes.Document html = Jsoup.parse(content);

        Elements teams = html.select("tr.team");
        for (Element team : teams) {
            String name = getText(team.selectFirst("td.name"));
            int year = parseInt(getText(team.selectFirst("td.year")));
            int wins = parseInt(getText(team.selectFirst("td.wins")));
            int losses = parseInt(getText(team.selectFirst("td.losses")));
            int otLosses = parseInt(getText(team.selectFirst("td.ot-losses")));
            double winPct = parseDouble(getText(team.selectFirst("td.pct")));
            int goalsFor = parseInt(getText(team.selectFirst("td.gf")));
            int goalsAgainst = parseInt(getText(team.selectFirst("td.ga")));
            int diff = parseInt(getText(team.selectFirst("td.diff")));

            repo.insertTeam(
                    mongoDoc.getString("uid"),
                    mongoDoc.getInteger("version", 1),
                    name, year, wins, losses, otLosses, winPct,
                    goalsFor, goalsAgainst, diff
            );
        }
    }

    private String getText(Element elem) {
        return elem != null ? elem.text().trim() : "";
    }

    private int parseInt(String val) {
        try { return Integer.parseInt(val.replaceAll("[^0-9]", "")); }
        catch (Exception e) { return 0; }
    }

    private double parseDouble(String val) {
        try { return Double.parseDouble(val); }
        catch (Exception e) { return 0.0; }
    }
}
