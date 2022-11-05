package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.util.Pair;

public abstract class AuthenticateTask extends BackgroundTask {

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    private User authenticatedUser;

    private AuthToken authToken;

    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    protected final String username;

    /**
     * The user's password.
     */
    protected final String password;

    protected AuthenticateTask(Handler messageHandler, String username, String password) {
        super(messageHandler);
        this.username = username;
        this.password = password;
    }


    @Override
    protected final void runTask() throws IOException, TweeterRemoteException {
        AuthenticateResponse response = sendServerRequest();

        if (response.isSuccess()) {
            this.authenticatedUser = response.getUser();
            this.authToken = response.getAuthToken();
            sendSuccessMessage();
        } else {
            sendFailedMessage(response.getMessage());
        }
    }
//        Pair<User, AuthToken> loginResult = runAuthenticationTask();
//
//        authenticatedUser = loginResult.getFirst();
//        authToken = loginResult.getSecond();
//
//        // Call sendSuccessMessage if successful
//        sendSuccessMessage();
//        // or call sendFailedMessage if not successful
//        // sendFailedMessage()
//    }

//    protected abstract Pair<User, AuthToken> runAuthenticationTask();
    protected abstract AuthenticateResponse sendServerRequest() throws IOException, TweeterRemoteException;

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, authenticatedUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
    }
}
