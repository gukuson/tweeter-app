package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.beans.FeedItem;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoFeedDAO extends DynamoStatusDAO<FeedItem> implements IFeedDAO{
    private static final String TableName = "feed";

    private static final String ReceiverAliasAttribute = "receiver_alias";
    private static final String TimestampAttribute = "timestamp";

    private final DynamoDbTable<FeedItem> table = getClient().table(TableName, TableSchema.fromBean(FeedItem.class));

    public static void main(String[] args) {
//        IFeedDAO feedDAO = new DynamoFeedDAO();
//        for (int i = 0; i < 15; ++i) {
//            feedDAO.addPostToFeed("@AFollowee", new Status("stanton didn't actually post this testfeed" + i, new User("stanton", "anthony", "@stanton", "imageURL"), System.currentTimeMillis() + i, "date pretty", null, null));
//        }
//        Long timestamp = 1669092020945L;
//        Pair<List<Status>, Boolean> result = new DynamoFeedDAO().getPagedFeed("@AFollowee", 10, timestamp);
//        System.out.println(result.toString());

    }

    @Override
    public void addPostToFeed(String receiverAlias, Status newStatus) {
        User statusUser = newStatus.getUser();
        FeedItem feedItem = new FeedItem(receiverAlias, newStatus.getTimestamp(), newStatus.getDate(), statusUser.getFirstName(), statusUser.getLastName(),
                statusUser.getAlias(), statusUser.getImageUrl(), newStatus.getPost(), newStatus.getUrls(), newStatus.getMentions());

        table.putItem(feedItem);
    }

    @Override
    protected String getPartitionKey() {
        return ReceiverAliasAttribute;
    }

    @Override
    protected DynamoDbTable<FeedItem> getTable() {
        return table;
    }

    @Override
    protected List<Status> getStatusesFromStatusItems(List<FeedItem> statusItems) {
        List<Status> feedStatuses = new ArrayList<>();

        for (FeedItem dbStatus : statusItems) {
            Status newStatus = new Status(dbStatus.getPost(), new User(dbStatus.getFirstname(), dbStatus.getLastname(), dbStatus.getSender_alias(),
                    dbStatus.getImageURL()), dbStatus.getTimestamp(), dbStatus.getDate(), dbStatus.getUrls(), dbStatus.getMentions());
            feedStatuses.add(newStatus);
        }

        return feedStatuses;
    }
}
