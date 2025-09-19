package com.example.loaders;

import com.example.repo.PgRepository;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class QuoteLoader implements Loader {
    private final PgRepository repo;

    public QuoteLoader(PgRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean supports(String link) {
        return link.startsWith("https://quotes.toscrape.com/");
    }

    @Override
    public void load(Document mongoDoc) {
        String content = mongoDoc.getString("content");
        if (content == null || content.isEmpty()) return;

        org.jsoup.nodes.Document html = Jsoup.parse(content);

        Elements quotes = html.select("div.quote");
        for (Element quote : quotes) {
            Element textElem = quote.selectFirst("span.text");
            Element authorElem = quote.selectFirst("small.author");
            if (textElem == null || authorElem == null) continue;

            String quoteText = textElem.text();
            String authorName = authorElem.text();

            // Try to find author_id; set null if not found
            Integer authorId = repo.findAuthorIdByName(authorName);

            // Tags
            Elements tagLinks = quote.select("div.tags a.tag");
            List<String> tagsList = tagLinks.eachText();
            String[] tags = tagsList.toArray(new String[0]);

            repo.insertQuote(
                    mongoDoc.getString("uid"),
                    mongoDoc.getInteger("version", 1),
                    authorId,
                    quoteText,
                    tags
            );
        }
    }
}
