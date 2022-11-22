package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.net.request.GetFollowRequest;
import edu.byu.cs.tweeter.util.Pair;

public interface IFollowDAO {
    Pair<List<String>, Boolean> getFollowers(GetFollowRequest request);
    Pair<List<String>, Boolean> getFollowing(GetFollowRequest request);
    int getFollowersCount(String targetUserAlias);
    int getFollowingCount(String targetUserAlias);
    boolean isFollower(String currUserAlias, String selectedUserAlias);
    List<String> getAllFollowersAliases(String senderAlias);
}
