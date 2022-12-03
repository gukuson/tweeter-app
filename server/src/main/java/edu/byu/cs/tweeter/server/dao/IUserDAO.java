package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;

public interface IUserDAO {
    User registerUser(String username, String password, String firstName, String lastName, String image);
    LoginResponse login(LoginRequest request);
    User getUser(String alias);
    void addUserBatch(List<User> users);
}
