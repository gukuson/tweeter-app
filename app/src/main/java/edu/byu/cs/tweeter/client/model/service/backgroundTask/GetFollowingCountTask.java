package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {
    private static final String LOG_TAG = "GetFollowingCountTask";
    public static final String URL_PATH = "/getfollowingcount";

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }
//
//    @Override
//    protected int runCountTask() {
//        return 20;
//    }

    @Override
    protected CountResponse sendServerRequest() throws IOException, TweeterRemoteException {
        CountRequest request = new CountRequest(getAuthToken(), getTargetUser().getAlias());
        return getServerFacade().getCount(request, URL_PATH);
    }

    @Override
    protected void logException(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
    }
}
