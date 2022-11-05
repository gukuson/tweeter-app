package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetUserRequest {
    private AuthToken currUserAuthToken;
    private String clickedAlias;

    public AuthToken getCurrUserAuthToken() {
        return currUserAuthToken;
    }

    public void setCurrUserAuthToken(AuthToken currUserAuthToken) {
        this.currUserAuthToken = currUserAuthToken;
    }

    public String getClickedAlias() {
        return clickedAlias;
    }

    public void setClickedAlias(String clickedAlias) {
        this.clickedAlias = clickedAlias;
    }

    public GetUserRequest() {
    }

    public GetUserRequest(AuthToken currUserAuthToken, String clickedAlias) {
        this.currUserAuthToken = currUserAuthToken;
        this.clickedAlias = clickedAlias;
    }
}
