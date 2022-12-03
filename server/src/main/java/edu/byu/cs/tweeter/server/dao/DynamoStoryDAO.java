package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.beans.Story;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;

public class DynamoStoryDAO extends DynamoStatusDAO<Story> implements IStoryDAO{
    private static final String TableName = "story";

    private static final String SenderAliasAttribute = "sender_alias";
    private static final String TimestampAttribute = "timestamp";

    private final DynamoDbTable<Story> table = getClient().table(TableName, TableSchema.fromBean(Story.class));


    public static void main(String[] args) {
//        List<String> testUrls = new ArrayList<>();
//        testUrls.add("https.//dksfj");
//        new DynamoStoryDAO().addPostToStory(new Status("post", new User("stanton", "anthony", "@AFollowee", "imageURL"), "date pretty", testUrls, null), System.currentTimeMillis());
//        new DynamoStoryDAO().getStory("@AFollowee");
//        long timestamp = 1669080805483L;
//        Pair<List<Status>, Boolean> result = new DynamoStoryDAO().getPagedStory("@AFollowee", 10, null);
//        System.out.println(result.toString());

    }

    @Override
    public void addPostToStory(Status newStatus) {
        User postUser = newStatus.getUser();
        Story newStory = new Story(postUser.getAlias(), newStatus.getTimestamp(), newStatus.getDate(), postUser.getFirstName(), postUser.getLastName(),
                postUser.getImageUrl(), newStatus.getPost(), newStatus.getUrls(), newStatus.getMentions());

        table.putItem(newStory);
    }


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

        return getStatusesFromStatusItems(dbStatuses);
    }


    @Override
    protected String getPartitionKey() {
        return SenderAliasAttribute;
    }

    @Override
    protected DynamoDbTable<Story> getTable() {
        return table;
    }

    @Override
    protected List<Status> getStatusesFromStatusItems(List<Story> statusItems) {
        List<Status> storyStatuses = new ArrayList<>();

        for (Story dbStatus : statusItems) {
            Status newStatus = new Status(dbStatus.getPost(), new User(dbStatus.getFirstname(), dbStatus.getLastname(), dbStatus.getSender_alias(),
                    dbStatus.getImageURL()), dbStatus.getTimestamp(), dbStatus.getDate(), dbStatus.getUrls(), dbStatus.getMentions());
            storyStatuses.add(newStatus);
        }

        return storyStatuses;
    }

    @Override
    <T, D> T getDTO(D item) {
        return null;
    }

    @Override
    <T> WriteBatch.Builder<T> getWriteBatchBuilder() {
        return null;
    }
}
