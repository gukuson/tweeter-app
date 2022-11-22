package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface IStoryDAO {
    void addPostToStory(Status newStatus);
    List<Status> getStory(String currAlias);
    Pair<List<Status>, Boolean> getPagedStory(String currHandle, int pageSize, Long timestamp);
}
