package edu.byu.cs.tweeter.server.service;

import java.util.Random;

import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.FollowToggleRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoFollowDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

//    IFollowDAO followDAO;

//    public FollowService(DAOFactory daoFactory) {
//        super(daoFactory);
//        followDAO = daoFactory.getFollowDao();
//    }


    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link DynamoFollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request following needs to have a positive limit");
        }else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request following needs to have an authtoken");
        }
        return getFollowingDAO().getFollowees(request);
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request followers needs to have a positive limit");
        }else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request followers needs to have an authtoken");
        }

        return getFollowingDAO().getFollowers(request);
    }

    public Response unfollow(FollowToggleRequest request) {
        if(request.getAliasToToggleFollow() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an alias to unfollow");
        }else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request to unfollow needs to have an authtoken");
        }

        return new Response(true);
    }

    public Response follow(FollowToggleRequest request) {
        if(request.getAliasToToggleFollow() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an alias to follow");
        }else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request to follow needs to have an authtoken");
        }

        return new Response(true);
    }


    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getCurrUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an alias of the current user");
        }else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request to check is follower needs to have an authtoken");
        }else if(request.getSelectedUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request to have an alias of the user trying to find if they're a follower");
        }

        boolean isFollower = new Random().nextInt() > 0;
        return new IsFollowerResponse(isFollower);

    }

    public CountResponse getFollowersCount(CountRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an alias of the target user");
        }else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request to check followers count needs to have an authtoken");
        }
//        Hardcoded followers count
        return new CountResponse(20);
    }

    public CountResponse getFollowingCount(CountRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an alias of the target user");
        }else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request to check following count needs to have an authtoken");
        }
//        Hardcoded following count
        return new CountResponse(20);
    }

    /**
     * Returns an instance of {@link DynamoFollowDAO}. Allows mocking of the DynamoFollowDAO class
     * for testing purposes. All usages of DynamoFollowDAO should get their DynamoFollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    DynamoFollowDAO getFollowingDAO() {
        return new DynamoFollowDAO();
    }


}
