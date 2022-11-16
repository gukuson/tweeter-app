package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;

public interface IUserDAO {
    User registerUser(String username, String password, String firstName, String lastName, String image);
}
