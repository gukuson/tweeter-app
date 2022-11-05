package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends NewPagedTask<User> {

    private static final String LOG_TAG = "GetFollowersTask";
    public static final String URL_PATH = "/getfollowers";

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
    }

    @Override
    protected void logException(Exception ex) {
        Log.e(LOG_TAG, "Failed to get followers", ex);
    }

    @Override
    protected PagedResponse<User> sendRequestToServer() throws IOException, TweeterRemoteException {
        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
        String lastFollowerAlias = getLastItem() == null ? null : getLastItem().getAlias();

        FollowersRequest request = new FollowersRequest(getAuthToken(), targetUserAlias, getLimit(), lastFollowerAlias);
        return getServerFacade().getFollowers(request, URL_PATH);
    }
//
//    @Override
//    protected Pair<List<User>, Boolean> getItems() {
//        return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
//    }
}
