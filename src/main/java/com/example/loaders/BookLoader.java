package com.example.loaders;

import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.example.repo.PgRepository;

public class BookLoader implements Loader {
    private final PgRepository repo;

    public BookLoader(PgRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean supports(String link) {
        return link.startsWith("https://books.toscrape.com");
    }

    @Override
    public void load(Document mongoDoc) {
        String content = mongoDoc.getString("content");
        org.jsoup.nodes.Document html = Jsoup.parse(content);

        Element title = html.selectFirst("div.product_main > h1");
        if (title == null) return;

        String bookTitle = title.text();
        String desc = "";
        Element descHeader = html.selectFirst("div#product_description");
        if (descHeader != null) {
            Element descPara = descHeader.nextElementSibling();
            if (descPara != null && descPara.tagName().equals("p")) {
                desc = descPara.text();
            }
        }

        // Extract table values
        Elements rows = html.select("table.table.table-striped tr");
        String upc = null, productType = null, priceExclTax = null,
                priceInclTax = null, tax = null, availability = null, reviews = null;

        for (Element row : rows) {
            String key = row.selectFirst("th").text();
            String value = row.selectFirst("td").text();
            switch (key) {
                case "UPC": upc = value; break;
                case "Product Type": productType = value; break;
                case "Price (excl. tax)": priceExclTax = value; break;
                case "Price (incl. tax)": priceInclTax = value; break;
                case "Tax": tax = value; break;
                case "Availability": availability = value; break;
                case "Number of reviews": reviews = value; break;
            }
        }

        // Rating
        String rating = null;
        Element ratingElem = html.selectFirst("p.star-rating");
        if (ratingElem != null) {
            String[] classes = ratingElem.className().split("\\s+");
            if (classes.length > 1) rating = classes[1];
        }

        repo.insertBook(
                mongoDoc.getString("uid"),
                mongoDoc.getInteger("version", 1),
                bookTitle, desc, upc, productType, priceExclTax,
                priceInclTax, tax, availability,
                reviews != null ? Integer.parseInt(reviews) : 0,
                rating
        );
    }
}
