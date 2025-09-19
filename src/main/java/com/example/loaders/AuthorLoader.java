package com.example.loaders;

import com.example.repo.PgRepository;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class AuthorLoader implements Loader {

    private final PgRepository repo;

    public AuthorLoader(PgRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean supports(String link) {
        return link.startsWith("https://quotes.toscrape.com");
    }

    @Override
    public void load(Document mongoDoc) {
        String content = mongoDoc.getString("content");
        org.jsoup.nodes.Document html = Jsoup.parse(content);

        Element authorDetails = html.selectFirst("div.author-details");
        if (authorDetails == null) return;

        String name = getText(authorDetails.selectFirst("h3.author-title"));
        String birthDate = getText(authorDetails.selectFirst("span.author-born-date"));
        String birthLocation = getText(authorDetails.selectFirst("span.author-born-location"));
        String description = getText(authorDetails.selectFirst("div.author-description"));

        repo.insertAuthor(
                mongoDoc.getString("uid"),
                mongoDoc.getInteger("version", 1),
                name, birthDate, birthLocation, description
        );
    }

    private String getText(Element elem) {
        return elem != null ? elem.text().trim() : "";
    }
}
