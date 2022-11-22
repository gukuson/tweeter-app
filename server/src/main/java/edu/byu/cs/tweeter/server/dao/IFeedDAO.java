package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.Status;

public interface IFeedDAO {
    void addPostToFeed(String followerAlias, Status newStatus, long currentMillis);
}
