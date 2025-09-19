package com.example.loaders;

import com.example.repo.PgRepository;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class QuoteLoader implements Loader {

    private final PgRepository repo;

    public QuoteLoader(PgRepository repo) {
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

        Elements quotes = html.select("div.quote");
        for (Element quoteElem : quotes) {
            String quoteText = getText(quoteElem.selectFirst("span.text"));
            String author = getText(quoteElem.selectFirst("small.author"));

            List<String> tags = new ArrayList<>();
            Elements tagElems = quoteElem.select("div.tags a.tag");
            for (Element tag : tagElems) tags.add(tag.text());

            Integer authorId = repo.findAuthorIdByName(author); // implement lookup
            repo.insertQuote(
                    mongoDoc.getString("uid"),
                    mongoDoc.getInteger("version", 1),
                    authorId,
                    quoteText,
                    tags
            );
        }
    }

    private String getText(Element elem) {
        return elem != null ? elem.text().trim() : "";
    }
}
