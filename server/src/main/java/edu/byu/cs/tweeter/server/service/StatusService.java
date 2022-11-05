package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {

    public StoryResponse getStory(StoryRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request following needs to have a positive limit");
        }

        Pair<List<Status>, Boolean> pagedOfStatuses = getPageOfStatus(request.getLastStatusUserAlias(), request.getDate(), request.getLimit());

        return new StoryResponse(pagedOfStatuses.getFirst(), pagedOfStatuses.getSecond());
    }

    public FeedResponse getFeed(FeedRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request following needs to have a positive limit");
        }

        Pair<List<Status>, Boolean> pagedOfStatuses = getPageOfStatus(request.getLastStatusUserAlias(), request.getDate(), request.getLimit());

        return new FeedResponse(pagedOfStatuses.getFirst(), pagedOfStatuses.getSecond());
    }


    Pair<List<Status>, Boolean> getPageOfStatus(String lastStatusUserAlias, String lastStatusDate, int limit) {
        return getFakeData().getPageOfStatus(lastStatusUserAlias, lastStatusDate, limit);
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }
    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<Status> getDummyStatuses() {
        return getFakeData().getFakeStatuses();
    }
}
