package edu.byu.cs.tweeter.server.dao;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDAO {
    // DynamoDB client
    protected static DynamoDbEnhancedClient client;

    protected DynamoDbEnhancedClient getClient() {
        if (client == null) {
            DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                    .region(Region.US_WEST_2)
                    .build();

            client = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(dynamoDbClient)
                    .build();
        }
        return client;
    }
}
