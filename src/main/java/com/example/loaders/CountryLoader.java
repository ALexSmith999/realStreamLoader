package com.example.loaders;

import com.example.repo.PgRepository;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CountryLoader implements Loader {

    private final PgRepository repo;

    public CountryLoader(PgRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean supports(String link) {
        return link.contains("scrapethissite.com/pages/");
    }

    @Override
    public void load(Document mongoDoc) {
        String content = mongoDoc.getString("content");
        org.jsoup.nodes.Document html = Jsoup.parse(content);

        Elements countries = html.select("div.country");
        for (Element country : countries) {
            String name = getText(country.selectFirst("h3.country-name"));
            String capital = getText(country.selectFirst("span.country-capital"));
            long population = parseLong(getText(country.selectFirst("span.country-population")));
            long area = parseLong(getText(country.selectFirst("span.country-area")));

            repo.insertCountry(
                    mongoDoc.getString("uid"),
                    mongoDoc.getInteger("version", 1),
                    name, capital, population, area
            );
        }
    }

    private String getText(Element elem) {
        return elem != null ? elem.text().trim() : "";
    }

    private long parseLong(String val) {
        try { return Long.parseLong(val.replaceAll("[^0-9]", "")); }
        catch (NumberFormatException e) { return 0; }
    }
}
