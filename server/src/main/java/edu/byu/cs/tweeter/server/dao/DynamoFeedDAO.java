package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.beans.FeedItem;
import edu.byu.cs.tweeter.server.beans.Story;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class DynamoFeedDAO extends DynamoDAO implements IFeedDAO{
    private static final String TableName = "feed";

    private final DynamoDbTable<FeedItem> table = getClient().table(TableName, TableSchema.fromBean(FeedItem.class));

    @Override
    public void addPostToFeed(String receiverAlias, Status newStatus, long currentMillis) {
        User statusUser = newStatus.getUser();
        FeedItem feedItem = new FeedItem(receiverAlias, currentMillis, newStatus.getDate(), statusUser.getFirstName(), statusUser.getLastName(),
                statusUser.getAlias(), statusUser.getImageUrl(), newStatus.getPost(), newStatus.getUrls(), newStatus.getMentions());

        table.putItem(feedItem);
    }
}
