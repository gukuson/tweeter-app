package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class IsFollowerRequest {
    private AuthToken authToken;
    private String currUserAlias;
    private String selectedUserAlias;

    public IsFollowerRequest() {
    }

    public IsFollowerRequest(AuthToken authToken, String currUserAlias, String selectedUserAlias) {
        this.authToken = authToken;
        this.currUserAlias = currUserAlias;
        this.selectedUserAlias = selectedUserAlias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getCurrUserAlias() {
        return currUserAlias;
    }

    public void setCurrUserAlias(String currUserAlias) {
        this.currUserAlias = currUserAlias;
    }

    public String getSelectedUserAlias() {
        return selectedUserAlias;
    }

    public void setSelectedUserAlias(String selectedUserAlias) {
        this.selectedUserAlias = selectedUserAlias;
    }
}
