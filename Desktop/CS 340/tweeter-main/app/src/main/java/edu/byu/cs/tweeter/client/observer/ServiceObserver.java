package edu.byu.cs.tweeter.client.observer;

public interface ServiceObserver {
    void handleFailure(String message);
    void handleException(Exception exception);
}
