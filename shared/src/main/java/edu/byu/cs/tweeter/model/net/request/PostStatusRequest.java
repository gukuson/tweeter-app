package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusRequest {
    private AuthToken authToken;
    private Status newStatus;

    public PostStatusRequest(AuthToken authToken, Status newStatus) {
        this.authToken = authToken;
        this.newStatus = newStatus;
    }

    public PostStatusRequest() {
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public Status getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Status newStatus) {
        this.newStatus = newStatus;
    }
}
