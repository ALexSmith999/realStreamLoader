package com.example.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private final Properties props = new Properties();

    public Config(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(path)) {
            props.load(fis);
        }
    }

    public String getMongoUri() {
        String host = props.getProperty("mongo.host");
        String port = props.getProperty("mongo.port", "27017");
        String replicaSet = props.getProperty("mongo.replicaSet", "rs0");
        return String.format("mongodb://%s:%s/?replicaSet=%s", host, port, replicaSet);
    }

    public String getMongoDatabase() {
        return props.getProperty("mongo.database");
    }

    public String getMongoCollection() {
        return props.getProperty("mongo.collection");
    }

    public String getPgUrl() {
        String host = props.getProperty("pg.host");
        String port = props.getProperty("pg.port", "5432");
        String database = props.getProperty("pg.database");
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
    }

    public String getPgUser() {
        return props.getProperty("pg.user");
    }

    public String getPgPassword() {
        return props.getProperty("pg.password");
    }
}
