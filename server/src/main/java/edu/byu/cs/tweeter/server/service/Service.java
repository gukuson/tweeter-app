package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;

abstract public class Service {
    protected DAOFactory daoFactory;

    public Service(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
}
