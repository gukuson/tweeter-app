package edu.byu.cs.tweeter.server.dao;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.beans.Follower;
import edu.byu.cs.tweeter.server.beans.Story;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoStoryDAO extends DynamoDAO implements IStoryDAO{
    private static final String TableName = "story";

    private static final String SenderAliasAttribute = "sender_alias";
    private static final String TimestampAttribute = "timestamp";

    private final DynamoDbTable<Story> table = getClient().table(TableName, TableSchema.fromBean(Story.class));


    public static void main(String[] args) throws ParseException {
//        List<String> testUrls = new ArrayList<>();
//        testUrls.add("https.//dksfj");
//        new DynamoStoryDAO().addPostToStory(new Status("post", new User("stanton", "anthony", "@AFollowee", "imageURL"), "date pretty", testUrls, null), System.currentTimeMillis());
//        new DynamoStoryDAO().getStory("@AFollowee");
        long timestamp = 1669080805483L;
        Pair<List<Status>, Boolean> result = new DynamoStoryDAO().getPagedStory("@AFollowee", 10, null);
        System.out.println(result.toString());

    }

    @Override
    public void addPostToStory(Status newStatus) {
        User postUser = newStatus.getUser();
        Story newStory = new Story(postUser.getAlias(), newStatus.getTimestamp(), newStatus.getDate(), postUser.getFirstName(), postUser.getLastName(),
                postUser.getImageUrl(), newStatus.getPost(), newStatus.getUrls(), newStatus.getMentions());

        table.putItem(newStory);
    }

    @Override
    public List<Status> getStory(String currAlias) {
        Key key = Key.builder()
                .partitionValue(currAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(false);

        QueryEnhancedRequest request = requestBuilder.build();

        List<Story> dbStatuses = table.query(request)
                .items()
                .stream()
                .collect(Collectors.toList());

        return getStatusesFromStoryItems(dbStatuses);
    }

    @Override
    public Pair<List<Status>, Boolean> getPagedStory(String currHandle, int pageSize, Long timestamp) {
        Key key = Key.builder()
                .partitionValue(currHandle)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(false)
                .limit(pageSize);
        // If you use iterators, it auto-fetches next page always, so instead limit the stream below
        //.limit(5);

        if (timestamp != null) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(SenderAliasAttribute, AttributeValue.builder().s(currHandle).build());
            startKey.put(TimestampAttribute, AttributeValue.builder().n(timestamp.toString()).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        PageIterable<Story> response = table.query(request);
        PageIterable<Story> pages = PageIterable.create(response);

        List<Story> storyItems = new ArrayList<>();

        pages.stream()
                .limit(1)
                .forEach(followerPage -> followerPage.items().forEach(v -> storyItems.add(v)));

        List<Status> storyStatuses = getStatusesFromStoryItems(storyItems);

        boolean hasMorePages = pages.iterator().next().lastEvaluatedKey() != null;
        return new Pair<>(storyStatuses, hasMorePages);
    }

    private List<Status> getStatusesFromStoryItems(List<Story> storyItems) {
        List<Status> storyStatuses = new ArrayList<>();

        for (Story dbStatus : storyItems) {
            Status newStatus = new Status(dbStatus.getPost(), new User(dbStatus.getFirstname(), dbStatus.getLastname(), dbStatus.getSender_alias(),
                    dbStatus.getImageURL()), dbStatus.getTimestamp(), dbStatus.getDate(), dbStatus.getUrls(), dbStatus.getMentions());
            storyStatuses.add(newStatus);
        }

        return storyStatuses;
    }

}
