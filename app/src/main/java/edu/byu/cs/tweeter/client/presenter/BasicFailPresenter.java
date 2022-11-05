package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.view.View;

public abstract class BasicFailPresenter<T extends View> extends Presenter<T> implements ServiceObserver {
    public BasicFailPresenter(T view) {
        super(view);
    }

    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage(failString(getDescription()) + message);
    }

    @Override
    public void handleException(Exception exception) {
        view.displayErrorMessage(exceptionString(getDescription()) + exception.getMessage());
    }

    public abstract String getDescription();
}
