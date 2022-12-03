package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public abstract class DynamoDAO {
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

    protected static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public <T,D>void addBatch(List<D> items) {
        List<T> batchToWrite = new ArrayList<>();
        for (D item : items) {
            T dto = getDTO(item);
            batchToWrite.add(dto);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfDTOs(batchToWrite);
        }
    }

    abstract  <T, D> T getDTO(D item);

    private <T> void writeChunkOfDTOs(List<T> items) {
        if(items.size() > 25)
            throw new RuntimeException("Too many items to write");

        DynamoDbTable<T> table = getTable();
        WriteBatch.Builder<T> writeBuilder = getWriteBatchBuilder();
        for (T item : items) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = client.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    abstract  <T> WriteBatch.Builder<T> getWriteBatchBuilder();

    abstract <T> DynamoDbTable<T> getTable();

}
