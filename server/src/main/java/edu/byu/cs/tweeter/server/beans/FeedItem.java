package edu.byu.cs.tweeter.server.beans;

import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FeedItem {
//    Partition key
    private String receiver_alias;
//    Sort key
    private long timestamp;

    private String date;
    private String firstname;
    private String lastname;
    private String sender_alias;
    private String imageURL;
    private String post;
    /**
     * URLs contained in the post text.
     */
    private List<String> urls;
    /**
     * User mentions contained in the post text.
     */
    private List<String> mentions;

    public FeedItem() {
    }

    public FeedItem(String receiver_alias, long timestamp, String date, String firstname, String lastname, String sender_alias, String imageURL, String post, List<String> urls, List<String> mentions) {
        this.receiver_alias = receiver_alias;
        this.timestamp = timestamp;
        this.date = date;
        this.firstname = firstname;
        this.lastname = lastname;
        this.sender_alias = sender_alias;
        this.imageURL = imageURL;
        this.post = post;
        this.urls = urls;
        this.mentions = mentions;
    }

    @DynamoDbPartitionKey
    public String getReceiver_alias() {
        return receiver_alias;
    }

    public void setReceiver_alias(String receiver_alias) {
        this.receiver_alias = receiver_alias;
    }

    @DynamoDbSortKey
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSender_alias() {
        return sender_alias;
    }

    public void setSender_alias(String sender_alias) {
        this.sender_alias = sender_alias;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

}
