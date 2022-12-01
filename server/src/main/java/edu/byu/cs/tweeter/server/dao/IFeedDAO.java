package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public interface IFeedDAO extends IStatusDAO {
    void addPostToFeed(String followerAlias, Status newStatus);
    void addFeedBatch(List<String> allFollowersOfSender, Status newStatus);
//    Pair<List<Status>, Boolean> getPagedFeed(String followerAlias, int limit, Long lastTimestamp);
}
