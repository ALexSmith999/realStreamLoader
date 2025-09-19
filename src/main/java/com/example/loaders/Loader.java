package com.example.loaders;

import org.bson.Document;

public interface Loader {
    boolean supports(String link);
    void load(Document mongoDoc);
}
