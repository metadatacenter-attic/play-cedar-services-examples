package org.metadatacenter.examples.utils;

import com.mongodb.MongoClient;

public class MongoFactory {

    private static MongoClient mongoClient =
            new MongoClient();

    public static MongoClient getClient() {
        return mongoClient;
    }

}
