package edu.byu.cs.tweeter.server.dao;

public class DynamoDAOFactory implements DAOFactory{
    @Override
    public IFollowDAO getFollowDao() {
        return new DynamoFollowDAO();
    }

    @Override
    public IUserDAO getUserDao() {
        return new DynamoUserDAO();
    }

    @Override
    public IAuthtokenDAO getAuthtokenDao() {
        return new DynamoAuthtokenDAO();
    }

    @Override
    public IFeedDAO getFeedDao() {
        return new DynamoFeedDAO();
    }

    @Override
    public IStoryDAO getStoryDao() {
        return new DynamoStoryDAO();
    }
}
