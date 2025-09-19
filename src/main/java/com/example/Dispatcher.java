package com.example;

import com.example.loaders.Loader;
import org.bson.Document;

import java.util.List;

public class Dispatcher {
    private final List<Loader> loaders;

    public Dispatcher(List<Loader> loaders) {
        this.loaders = loaders;
    }

    public void dispatch(Document doc) {
        String link = doc.getString("link");
        for (Loader loader : loaders) {
            if (loader.supports(link)) {
                loader.load(doc);
                break;
            }
        }
    }
}
