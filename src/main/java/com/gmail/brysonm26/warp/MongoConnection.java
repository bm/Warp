package com.gmail.brysonm26.warp;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Location;

import java.util.UUID;

public class MongoConnection {

    private String host;
    private int port;

    private MongoCollection<Document> warps;
    private MongoDatabase warpsDb;
    private MongoClient client;

    public MongoConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect(String host, int port) {
        try {
            client = new MongoClient(host, port);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        warpsDb = client.getDatabase("warps");
        warps = warpsDb.getCollection("warps");
        return true;
    }

    public void storeWarp(UUID uuid, String name, Location loc) {

    }

}
