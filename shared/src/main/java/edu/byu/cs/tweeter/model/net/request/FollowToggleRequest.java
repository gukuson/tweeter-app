package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FollowToggleRequest {
    private AuthToken authToken;
    private String aliasToToggleFollow;

    public FollowToggleRequest(AuthToken authToken, String aliasToToggleFollow) {
        this.authToken = authToken;
        this.aliasToToggleFollow = aliasToToggleFollow;
    }

    public FollowToggleRequest() {
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getAliasToToggleFollow() {
        return aliasToToggleFollow;
    }

    public void setAliasToToggleFollow(String aliasToToggleFollow) {
        this.aliasToToggleFollow = aliasToToggleFollow;
    }
}
