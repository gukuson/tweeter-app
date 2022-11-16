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
}
