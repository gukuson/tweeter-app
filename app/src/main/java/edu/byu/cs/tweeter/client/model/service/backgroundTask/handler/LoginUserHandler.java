package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.AuthenticateTask;
import edu.byu.cs.tweeter.client.model.service.observer.LoginUserObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginUserHandler extends BackgroundTaskHandler<LoginUserObserver>{
    public LoginUserHandler(LoginUserObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(LoginUserObserver observer, Bundle data) {
        User loggedInUser = (User) data.getSerializable(AuthenticateTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);

        // Cache user session information
        Cache.getInstance().setCurrUser(loggedInUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.loginSucceeded(loggedInUser);
    }
}
