package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class LoginPresenter extends AuthenticateUserPresenter {

    public LoginPresenter(AuthenticateView view) {
        super(view);
    }

    @Override
    public String getDescription() {
        return "login";
    }


    public void initiateLogin(String username, String password) {
        try {
            validateLogin(username, password);
            view.clearErrorView();
            view.displayMessage("Logging in ...");
            new UserService().login(username, password, this);
        }catch (Exception e) {
            view.displayErrorView(e.getMessage());
        }
    }

    public void validateLogin(String username, String password) {
        if (username.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (username.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (username.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

}
