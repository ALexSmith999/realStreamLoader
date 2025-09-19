package com.example.loaders;

import com.example.repo.PgRepository;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class ProductLoader implements Loader {

    private final PgRepository repo;

    public ProductLoader(PgRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean supports(String link) {
        return link.startsWith("https://sandbox.oxylabs.io/products");
    }

    @Override
    public void load(Document mongoDoc) {
        String content = mongoDoc.getString("content");
        org.jsoup.nodes.Document html = Jsoup.parse(content);

        String title = html.title();
        String name = title.contains("|") ? title.split("\\|")[0].trim() : title.trim();

        String developer = getMeta(html, "og:developer");
        String platform = getMeta(html, "og:platform");
        String type = getMeta(html, "og:type");
        String price = getMeta(html, "og:price");
        String stock = getMeta(html, "og:availability");
        String genres = getMeta(html, "og:genre");

        repo.insertProduct(
                mongoDoc.getString("uid"),
                mongoDoc.getInteger("version", 1),
                name, developer, platform, type, price, stock, genres
        );
    }

    private String getMeta(org.jsoup.nodes.Document html, String property) {
        Element meta = html.selectFirst("meta[property=" + property + "]");
        return meta != null ? meta.attr("content").trim() : null;
    }
}
