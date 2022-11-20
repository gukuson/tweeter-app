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
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends SimpleAuthenticatedTask {
    private static final String LOG_TAG = "FollowTask";
    public static final String URL_PATH = "/follow";
    /**
     * The user that they want to follow.
     */
    private final User followee;

    public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
    }

    @Override
    protected void logException(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
    }

    @Override
    protected Response sendServerRequest() throws IOException, TweeterRemoteException {
        FollowToggleRequest request = new FollowToggleRequest(getAuthToken(), followee.getAlias());
        return getServerFacade().follow(request, URL_PATH);
    }

}
