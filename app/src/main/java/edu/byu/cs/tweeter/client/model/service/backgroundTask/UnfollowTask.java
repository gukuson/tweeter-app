package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowToggleRequest;
import edu.byu.cs.tweeter.model.net.response.Response;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends SimpleAuthenticatedTask {

    private static final String LOG_TAG = "UnfollowTask";
    public static final String URL_PATH = "/unfollow";

    /**
     * The user that is being followed, that they want to unfollow
     */
    private final User followee;

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
    }

    @Override
    protected void logException(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
    }

//    @Override
//    protected void runTask() {
//        // We could do this from the presenter, without a task and handler, but we will
//        // eventually access the database from here when we aren't using dummy data.
//
//        // Call sendSuccessMessage if successful
//        sendSuccessMessage();
//        // or call sendFailedMessage if not successful
//        // sendFailedMessage()
//    }

    @Override
    protected Response sendServerRequest() throws IOException, TweeterRemoteException {
        FollowToggleRequest request = new FollowToggleRequest(getAuthToken(), followee.getAlias());
        return getServerFacade().unfollow(request, URL_PATH);
    }


}
