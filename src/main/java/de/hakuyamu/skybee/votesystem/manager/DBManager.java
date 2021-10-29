package de.hakuyamu.skybee.votesystem.manager;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DBManager {

    private MongoClient mongoClient;
    private MongoDatabase database;

    public void connect() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("sb_vote_system");
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void disconnect() {
        mongoClient.close();
    }

}
