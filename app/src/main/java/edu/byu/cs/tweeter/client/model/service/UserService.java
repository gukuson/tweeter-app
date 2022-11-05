package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LoginUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.GetUserObserver;
import edu.byu.cs.tweeter.client.model.service.observer.LoginUserObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {
    public void logout(AuthToken currUserAuthToken, SimpleNotificationObserver observer) {
        LogoutTask logoutTask = new LogoutTask(currUserAuthToken, new SimpleNotificationHandler(observer));
        BackgroundTaskUtils.runTask(logoutTask);
    }

    public void registerUser(String firstName, String lastName, String alias, String password, String imageBytesBase64,
                             LoginUserObserver observer) {
        // Send register request.
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password, imageBytesBase64,
                new LoginUserHandler(observer));
        BackgroundTaskUtils.runTask(registerTask);
    }

    // Void bc asynchronous function, need LoginObserver so can call on that when background task is complete
    public void login(String username, String password, LoginUserObserver observer) {
//        Run logintask in background to log user in
        // Send the login request.
        LoginTask loginTask = new LoginTask(username, password, new LoginUserHandler(observer));
        BackgroundTaskUtils.runTask(loginTask);
    }

    public void getUser(AuthToken currUserAuthToken, String clickedAlias, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(currUserAuthToken,
                clickedAlias, new GetUserHandler(observer));
        BackgroundTaskUtils.runTask(getUserTask);
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private static class GetUserHandler extends BackgroundTaskHandler<GetUserObserver> {
        public GetUserHandler(GetUserObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(GetUserObserver observer, Bundle data) {
            User user = (User) data.getSerializable(GetUserTask.USER_KEY);
            observer.gotUser(user);
        }
    }

}
