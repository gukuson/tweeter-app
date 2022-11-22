package edu.byu.cs.tweeter.server.dao;

public interface DAOFactory {
    IFollowDAO getFollowDao();
    IUserDAO getUserDao();
    IAuthtokenDAO getAuthtokenDao();
    IFeedDAO getFeedDao();
    IStoryDAO getStoryDao();
}
