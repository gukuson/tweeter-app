package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.beans.FeedItem;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoFeedDAO extends DynamoStatusDAO<FeedItem> implements IFeedDAO{
    private static final String TableName = "feed";

    private static final String ReceiverAliasAttribute = "receiver_alias";
    private static final String TimestampAttribute = "timestamp";

    protected final String JOBSQ_URL = "https://sqs.us-west-2.amazonaws.com/633573532902/JobsQueue";

    private final DynamoDbTable<FeedItem> table = getClient().table(TableName, TableSchema.fromBean(FeedItem.class));

    private final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
    private final Gson gson = new Gson();

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
    public void createFeedBatches(List<String> allFollowersOfSender, Status newStatus) {
        List<FeedItem> batchToWrite = new ArrayList<>();


        long statusTimestamp = newStatus.getTimestamp();
        String statusDate = newStatus.getDate();
        String statusFirstName = newStatus.getUser().getFirstName();
        String statusLastName = newStatus.getUser().getLastName();
        String statusAlias = newStatus.getUser().getAlias();
        String statusImage = newStatus.getUser().getImageUrl();
        String post = newStatus.getPost();
        List<String> urls = newStatus.getUrls();
        List<String> mentions = newStatus.getMentions();

        for (String receiverAlias : allFollowersOfSender) {
            FeedItem feedItem = new FeedItem(receiverAlias, statusTimestamp, statusDate, statusFirstName, statusLastName,
                    statusAlias, statusImage, post, urls, mentions);
            batchToWrite.add(feedItem);

            if (batchToWrite.size() == 25) {
                // serialize this batch up and send to queue to process
                sendBatchMessage(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // serialize this batch up and send to queue to process
            sendBatchMessage(batchToWrite);
        }
    }

    @Override
    public void addFeedBatch(String jsonString) {
        Type feedItemsList = new TypeToken<ArrayList<FeedItem>>() {}.getType();
        List<FeedItem> feedItems = gson.fromJson(jsonString, feedItemsList);

        writeChunkOfFeedItems(feedItems);
    }

    private void writeChunkOfFeedItems(List<FeedItem> feedItems) {
        if(feedItems.size() > 25)
            throw new RuntimeException("Too many feedItems to write");

        WriteBatch.Builder<FeedItem> writeBuilder = WriteBatch.builder(FeedItem.class).mappedTableResource(table);

        for (FeedItem item : feedItems) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }

        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = client.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfFeedItems(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void sendBatchMessage(List<FeedItem> batchToWrite) {
        String batchMessage = gson.toJson(batchToWrite);

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(JOBSQ_URL)
                .withMessageBody(batchMessage);

        sqs.sendMessage(send_msg_request);
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

    @Override
    <T, D> T getDTO(D item) {
        return null;
    }

    @Override
    <T> WriteBatch.Builder<T> getWriteBatchBuilder() {
        return null;
    }
}
