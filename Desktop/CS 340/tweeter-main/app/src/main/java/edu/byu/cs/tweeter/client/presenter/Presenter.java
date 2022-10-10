package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.view.View;

public abstract class Presenter<T extends View> {
    protected T view;

    public Presenter(T view) {
        this.view = view;
    }

}
