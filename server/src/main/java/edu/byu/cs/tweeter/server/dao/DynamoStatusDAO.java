package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public abstract class DynamoStatusDAO<T> extends DynamoDAO implements IStatusDAO{

    private static final String TimestampAttribute = "timestamp";

    @Override
    public Pair<List<Status>, Boolean> getPagedStatuses(String currHandle, int pageSize, Long timestamp) {
        Key key = Key.builder()
                .partitionValue(currHandle)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(false)
                .limit(pageSize);

        if (timestamp != null) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();

            startKey.put(getPartitionKey(), AttributeValue.builder().s(currHandle).build());
            startKey.put(TimestampAttribute, AttributeValue.builder().n(timestamp.toString()).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        PageIterable<T> response = getTable().query(request);
        PageIterable<T> pages = PageIterable.create(response);

        List<T> statusItems = new ArrayList<>();

        pages.stream()
                .limit(1)
                .forEach(followerPage -> followerPage.items().forEach(v -> statusItems.add(v)));

        List<Status> modelStatuses = getStatusesFromStatusItems(statusItems);

        boolean hasMorePages = pages.iterator().next().lastEvaluatedKey() != null;
        return new Pair<>(modelStatuses, hasMorePages);
    }

    protected abstract String getPartitionKey();

    protected abstract DynamoDbTable<T> getTable();

    protected abstract List<Status> getStatusesFromStatusItems(List<T> statusItems);
}
