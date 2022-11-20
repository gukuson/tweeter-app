package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.DAOFactory;

abstract public class Service {
//    Timer for the authtoken, 60,000 is a minute
    private final long AUTH_EXPIRE = 600000;
    protected DAOFactory daoFactory;

    public Service(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    boolean isValidAuthtoken(long datetime) {
        return ( (System.currentTimeMillis() - datetime) <= AUTH_EXPIRE);
    }
}
