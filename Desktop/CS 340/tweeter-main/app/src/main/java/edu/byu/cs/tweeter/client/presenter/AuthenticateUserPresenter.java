package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.observer.LoginUserObserver;
import edu.byu.cs.tweeter.client.view.View;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticateUserPresenter extends Presenter<AuthenticateUserPresenter.AuthenticateView> implements LoginUserObserver {

    public AuthenticateUserPresenter(AuthenticateView view) {
        super(view);
    }

    public interface AuthenticateView extends View {
//        void displayMessage(String message);
        void clearMessage();
        void displayErrorView(String message);
        void clearErrorView();
        void navigateToUser(User registeredUser);
    }

    @Override
    public void loginSucceeded(User user) {
        view.clearMessage();
        view.clearErrorView();
        view.displayMessage("Hello " + user.getName());
        view.navigateToUser(user);
    }

    @Override
    public void handleFailure(String message) {
        view.clearMessage();
        view.displayErrorMessage(failString(getDescription()) + message);
    }

    @Override
    public void handleException(Exception exception) {
        view.clearMessage();
        view.displayErrorMessage(exceptionString(getDescription()) + exception.getMessage());
    }

    public abstract String getDescription();

}
