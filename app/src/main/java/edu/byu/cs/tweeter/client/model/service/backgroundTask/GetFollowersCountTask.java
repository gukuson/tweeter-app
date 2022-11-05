package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {
    private static final String LOG_TAG = "GetFollowersCountTask";
    public static final String URL_PATH = "/getfollowerscount";

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected CountResponse sendServerRequest() throws IOException, TweeterRemoteException {
        CountRequest request = new CountRequest(getAuthToken(), getTargetUser().getAlias());
        return getServerFacade().getCount(request, URL_PATH);
    }


//    @Override
//    protected int runCountTask() {
//        return 20;
//    }

    @Override
    protected void logException(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
    }
}
