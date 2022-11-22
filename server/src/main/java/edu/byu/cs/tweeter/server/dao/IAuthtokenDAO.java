package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface IAuthtokenDAO {
    AuthToken createAuthtoken(String alias);
    void deleteAuthtoken(String token);
    String getAlias(AuthToken authToken);
}
