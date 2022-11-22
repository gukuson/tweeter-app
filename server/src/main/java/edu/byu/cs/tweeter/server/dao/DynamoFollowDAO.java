package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.net.request.GetFollowRequest;
import edu.byu.cs.tweeter.server.beans.Follower;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoFollowDAO extends DynamoDAO implements IFollowDAO{

    private static final String TableName = "follows";
    public static final String IndexName = "follows_index";

    private static final String FollowerAttr = "follower_handle";
    private static final String FolloweeAttr = "followee_handle";
//    private static final String FollowerNameAttr = "follower_name";
//    private static final String FolloweeNameAttr = "followee_name";

    private final DynamoDbTable<Follower> table = getClient().table(TableName, TableSchema.fromBean(Follower.class));
    private final DynamoDbIndex<Follower> index = getClient().table(TableName, TableSchema.fromBean(Follower.class)).index(IndexName);


    public static void main(String[] args) {
        DynamoFollowDAO followDAO = new DynamoFollowDAO();
//        for (int i = 0; i < 15; ++i) {
//            followDAO.addFollower("@AFollowee", "A Followee", "@Rob Boss" + i, "Rob Boss" + i);
//        }
//        followDAO.addFollower("@AFollowee", "A Followee", "@stanton", "testfollowing stanton");
//        Pair<List<String>, Boolean> testresponse = followDAO.getFollowing(new GetFollowRequest(null, "@AFollowee", 10, null));
//        System.out.println(testresponse.toString());
        boolean isFollowing = followDAO.isFollower("@AFollowee", "@Rob Boss0");
        System.out.println(isFollowing);
    }

    public void addFollower(String followerHandle,  String followerName, String followeeHandle, String followeeName) {

        Key key = Key.builder()
                .partitionValue(followerHandle).sortValue(followeeHandle)
                .build();

        // load it if it exists
        Follower follower = table.getItem(key);
        if(follower != null) {
            System.out.println("Already added this follower");
        } else {
            Follower newFollower = new Follower();
            newFollower.setFollower_handle(followerHandle);
            newFollower.setFollower_name(followerName);
            newFollower.setFollowee_handle(followeeHandle);
            newFollower.setFollowee_name(followeeName);

            table.putItem(newFollower);
        }
    }

    private Follower getFollower(String followerHandle, String followeeHandle) {
        Key key = Key.builder()
                .partitionValue(followerHandle).sortValue(followeeHandle)
                .build();

        return table.getItem(key);
    }

    public void deleteFollower(String followerHandle, String followeeHandle) {
        Key key = Key.builder()
                .partitionValue(followerHandle).sortValue(followeeHandle)
                .build();

        table.deleteItem(key);
    }

//    public void updateFollower(String followerHandle,  String followerName, String followeeHandle, String followeeName) {
//        Key key = Key.builder()
//                .partitionValue(followerHandle).sortValue(followeeHandle)
//                .build();
//
//        // load it if it exists
//        Follower follower = table.getItem(key);
//        if(follower != null) {
//            follower.setFollower_name(followerName);
//            follower.setFollowee_name(followeeName);
//            table.updateItem(follower);
//        } else {
//            System.out.println("This follower does not exit with the aliases provided");
//        }
//    }

    public List<String> getAllFollowersAliases(String followeeHandle) {
        List<Follower> dbFollowers = getAllFollowers(followeeHandle);
        return getAliasesFromFollowers(dbFollowers, true);
    }

//    Gets all the following for the passed in alias
    private List<Follower> getAllFollowees(String followerHandle) {
        Key key = Key.builder()
                .partitionValue(followerHandle)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));
        // If you use iterators, it auto-fetches next page always, so instead limit the stream below
        //.limit(5);

        QueryEnhancedRequest request = requestBuilder.build();

        return table.query(request)
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    private List<Follower> getAllFollowers(String followeeHandle) {

        Key key = Key.builder()
                .partitionValue(followeeHandle)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));
        // Unlike Tables, querying from an Index returns a PageIterable, so we want to just ask for
        // 1 page with pageSize items


        QueryEnhancedRequest request = requestBuilder.build();

        List<Follower> followers = new ArrayList<>();

        SdkIterable<Page<Follower>> results2 = index.query(request);
        PageIterable<Follower> pages = PageIterable.create(results2);

        pages.stream()
                .forEach(followerPage -> followerPage.items().forEach(v -> followers.add(v)));

        return followers;
    }

    private List<String> getAliasesFromFollowers (List<Follower> dbFollowers, boolean isFollowers) {
        List<String> followersAliases = new ArrayList<>();
        if (dbFollowers != null) {
            for (Follower dbFollower : dbFollowers) {
                String followerHandle;
                if (isFollowers) {
                    followerHandle = dbFollower.getFollower_handle();
                }else {
                    followerHandle = dbFollower.getFollowee_handle();
                }
                followersAliases.add(followerHandle);
            }
        }
        return followersAliases;
    }

    @Override
    public Pair<List<String>, Boolean> getFollowers(GetFollowRequest request) {
        Pair<List<Follower>, Boolean> dbFollowers = getPageItemsFromFollows(request.getFollowerAlias(), request.getLimit(), request.getLastFollowAlias(), true);
//        Pair<List<Follower>, Boolean> dbFollowers = getPaginatedFollowers(request.getFollowerAlias(), request.getLimit(), request.getLastFollowAlias());

        List<String> aliases = getAliasesFromFollowers(dbFollowers.getFirst(), true);

        return new Pair<>(aliases, dbFollowers.getSecond());
//        List<String> followersAliases = new ArrayList<>();
//        if (dbFollowers != null) {
//            for (Follower dbFollower : dbFollowers) {
//                followersAliases.add(dbFollower.getFollower_handle());
//            }
//        }
//        return followersAliases;
    }

    @Override
    public Pair<List<String>, Boolean> getFollowing(GetFollowRequest request) {
        Pair<List<Follower>, Boolean> dbFollowers = getPageItemsFromFollows(request.getFollowerAlias(), request.getLimit(), request.getLastFollowAlias(), false);
//        Pair<List<Follower>, Boolean> dbFollowers = getPaginatedFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFollowAlias());

        List<String> aliases = getAliasesFromFollowers(dbFollowers.getFirst(), false);

        return new Pair<>(aliases, dbFollowers.getSecond());
//        List<String> followingAliases = new ArrayList<>();
//        if (dbFollowers != null) {
//            for (Follower dbFollower : dbFollowers) {
//                followingAliases.add(dbFollower.getFollowee_handle());
//            }
//        }
//        return followingAliases;
    }

    @Override
    public int getFollowersCount(String targetUserAlias) {
        return getAllFollowers(targetUserAlias).size();
    }

    @Override
    public int getFollowingCount(String targetUserAlias) {
        return getAllFollowees(targetUserAlias).size();
    }

//    Does curr user follow selected user?
    @Override
    public boolean isFollower(String currUserAlias, String selectedUserAlias) {
        Follower follower = getFollower(currUserAlias, selectedUserAlias);
        return follower != null;
    }

    public Pair<List<Follower>, Boolean> getPageItemsFromFollows(String followsHandle, int pageSize, String lastHandle, boolean isFollower) {
        Key key = Key.builder()
                .partitionValue(followsHandle)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize);
        // If you use iterators, it auto-fetches next page always, so instead limit the stream below
        //.limit(5);

        if(isNonEmptyString(lastHandle)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            if (isFollower){
                startKey.put(FolloweeAttr, AttributeValue.builder().s(followsHandle).build());
                startKey.put(FollowerAttr, AttributeValue.builder().s(lastHandle).build());
            }else {
                startKey.put(FollowerAttr, AttributeValue.builder().s(followsHandle).build());
                startKey.put(FolloweeAttr, AttributeValue.builder().s(lastHandle).build());
            }

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        PageIterable<Follower> pages;
        if (isFollower) {
            SdkIterable<Page<Follower>> response = index.query(request);
            pages = PageIterable.create(response);
        }else {
            PageIterable<Follower> response = table.query(request);
            pages = PageIterable.create(response);
        }

        List<Follower> followItems = new ArrayList<>();

        pages.stream()
                .limit(1)
                .forEach(followerPage -> followerPage.items().forEach(v -> followItems.add(v)));

        boolean hasMorePages = pages.iterator().next().lastEvaluatedKey() != null;
        return new Pair<>(followItems, hasMorePages);
    }

//    public Pair<List<Follower>, Boolean> getPaginatedFollowees(String followerHandle, int pageSize, String lastFolloweeHandle) {
//        Key key = Key.builder()
//                .partitionValue(followerHandle)
//                .build();
//
//        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
//                .queryConditional(QueryConditional.keyEqualTo(key))
//                .limit(pageSize);
//        // If you use iterators, it auto-fetches next page always, so instead limit the stream below
//        //.limit(5);
//
//        if(isNonEmptyString(lastFolloweeHandle)) {
//            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
//            Map<String, AttributeValue> startKey = new HashMap<>();
//            startKey.put(FollowerAttr, AttributeValue.builder().s(followerHandle).build());
//            startKey.put(FolloweeAttr, AttributeValue.builder().s(lastFolloweeHandle).build());
//
//            requestBuilder.exclusiveStartKey(startKey);
//        }
//
//        QueryEnhancedRequest request = requestBuilder.build();
//
//        PageIterable<Follower> response = table.query(request);
//        PageIterable<Follower> pages = PageIterable.create(response);
//
//        List<Follower> followees = new ArrayList<>();
//
//        pages.stream()
//                .limit(1)
//                .forEach(followerPage -> followerPage.items().forEach(v -> followees.add(v)));
//
//        boolean hasMorePages = pages.iterator().next().lastEvaluatedKey() != null;
//        return new Pair<>(followees, hasMorePages);
//    }



//    public Pair<List<Follower>, Boolean> getPaginatedFollowers(String followeeHandle, int pageSize, String lastFollowerHandle) {
//        Key key = Key.builder()
//                .partitionValue(followeeHandle)
//                .build();
//
//        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
//                .queryConditional(QueryConditional.keyEqualTo(key))
//                .limit(pageSize);
//        // Unlike Tables, querying from an Index returns a PageIterable, so we want to just ask for
//        // 1 page with pageSize items
//
//        if(isNonEmptyString(lastFollowerHandle)) {
//            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
//            Map<String, AttributeValue> startKey = new HashMap<>();
//            startKey.put(FolloweeAttr, AttributeValue.builder().s(followeeHandle).build());
//            startKey.put(FollowerAttr, AttributeValue.builder().s(lastFollowerHandle).build());
//
//            requestBuilder.exclusiveStartKey(startKey);
//        }
//
//        QueryEnhancedRequest request = requestBuilder.build();
//
//        List<Follower> followers = new ArrayList<>();
//
//        SdkIterable<Page<Follower>> results2 = index.query(request);
//        PageIterable<Follower> pages = PageIterable.create(results2);
//        // limit 1 page, with pageSize items
//        pages.stream()
//                .limit(1)
//                .forEach(followerPage -> followerPage.items().forEach(v -> followers.add(v)));
//
//        boolean hasMorePages = pages.iterator().next().lastEvaluatedKey() != null;
//        return new Pair<>(followers, hasMorePages);
//    }
}