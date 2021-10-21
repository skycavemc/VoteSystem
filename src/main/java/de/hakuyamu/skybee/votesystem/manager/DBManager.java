package de.hakuyamu.skybee.votesystem.manager;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

public class DBManager {

    private MongoClient mongoClient;
    private DB database;
    private DBCollection users;
    private DBCollection event;

    public void setup() {
        try {
            mongoClient = new MongoClient();
            database = mongoClient.getDB("SBVoteSystem");
            users = database.getCollection("users");
            event = database.getCollection("event");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public DB getDatabase() {
        return database;
    }

    public DBCollection getUsers() {
        return users;
    }

    public DBCollection getEvent() {
        return event;
    }

    public void shutdown() {
        mongoClient.close();
    }

}
