package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.Status;

public interface IStoryDAO extends IStatusDAO{
    void addPostToStory(Status newStatus);
//    List<Status> getStory(String currAlias);
//    Pair<List<Status>, Boolean> getPagedStory(String currHandle, int pageSize, Long timestamp);
}
