package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.Response;

public abstract class AuthenticatedTask extends BackgroundTask {

    /**
     * Auth token for logged-in user.
     * This user is the "follower" in the relationship.
     */
    private final AuthToken authToken;

    protected AuthenticatedTask(AuthToken authToken, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }
}
