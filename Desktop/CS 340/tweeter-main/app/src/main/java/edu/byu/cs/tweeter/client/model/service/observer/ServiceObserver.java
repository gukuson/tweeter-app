package edu.byu.cs.tweeter.client.model.service.observer;

public interface ServiceObserver {
    void handleFailure(String message);
    void handleException(Exception exception);
    default String failString(String description) {
        return "Failed to " + description + ": ";
    }
    default String exceptionString(String description) {
        return "Failed to " + description + " because of exception: ";
    }
}
