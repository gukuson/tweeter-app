package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowToggleRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.IAuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service{

    IFollowDAO followDAO;
    IUserDAO userDAO;
    IAuthtokenDAO authtokenDAO;

    public FollowService(DAOFactory daoFactory) {
        super(daoFactory);
        followDAO = daoFactory.getFollowDao();
        userDAO = daoFactory.getUserDao();
        authtokenDAO = daoFactory.getAuthtokenDao();
    }


    public static void main(String[] args) {
//        FollowService followService = new FollowService(new DynamoDAOFactory());
//        IsFollowerResponse response = followService.isFollower(new IsFollowerRequest(new AuthToken(), "@AFollowee", "@Rob Boss0"));
//        assert response.isFollower();
    }

    private Pair<List<User>, Boolean> getItems(GetFollowRequest request, boolean isFollowers) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request followers needs to have a positive limit");
        }else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request followers needs to have an authtoken");
        }

        if (isValidAuthtoken(request.getAuthToken().getDatetime())) {
            if (request.getLastFollowAlias() != null) {
                System.out.println("Last alias is: " + request.getLastFollowAlias());
            }

            Pair<List<String>, Boolean> pagedItems;
            if (isFollowers) {
                pagedItems = followDAO.getFollowers(request);
            }else {
                pagedItems = followDAO.getFollowing(request);
            }

            List<User> responseFollowItems = new ArrayList<>();

            for (String alias : pagedItems.getFirst()) {
                User currUser = userDAO.getUser(alias);
                if (currUser != null) {
                    responseFollowItems.add(currUser);
                }
            }
            System.out.println(responseFollowItems);

            return new Pair<>(responseFollowItems, pagedItems.getSecond());
        }else {
            throw new RuntimeException("[Bad Request] Expired authtoken");
        }
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(GetFollowRequest request) {
        Pair<List<User>, Boolean> responseFollowees = getItems(request, false);
        return new FollowingResponse(responseFollowees.getFirst(), responseFollowees.getSecond());
    }

    public FollowersResponse getFollowers(GetFollowRequest request) {
        Pair<List<User>, Boolean> responseFollowers = getItems(request, true);
        return new FollowersResponse(responseFollowers.getFirst(), responseFollowers.getSecond());
    }

    public Response toggleFollow(FollowToggleRequest request, boolean shouldFollow) {
        if(request.getAliasToToggleFollow() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an alias to unfollow");
        }else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request to unfollow needs to have an authtoken");
        }
        if (! isValidAuthtoken(request.getAuthToken().getDatetime())) {
            throw new RuntimeException("[Bad Request] Expired authtoken");
        }
        String currentUserAlias = authtokenDAO.getAlias(request.getAuthToken());

        if (shouldFollow) {
            followDAO.addFollower(currentUserAlias, request.getAliasToToggleFollow());
        }else {
            followDAO.removeFollower(currentUserAlias, request.getAliasToToggleFollow());
        }

        return new Response(true);
    }

//    When unfollow need to update count for current user's following, person unfollowed followers
    public Response unfollow(FollowToggleRequest request) {
        return toggleFollow(request, false);
    }


    //    When follow need to update count for current user's following, person followed followers
    public Response follow(FollowToggleRequest request) {
        return toggleFollow(request, true);
    }


    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getCurrUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an alias of the current user");
        }else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request to check is follower needs to have an authtoken");
        }else if(request.getSelectedUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request to have an alias of the user trying to find if they're a follower");
        }
        if (! isValidAuthtoken(request.getAuthToken().getDatetime())) {
            throw new RuntimeException("[Bad Request] Expired authtoken");
        }

        boolean isFollower = followDAO.isFollower(request.getCurrUserAlias(), request.getSelectedUserAlias());
        System.out.println(request.getCurrUserAlias() + " " + isFollower + " follows " + request.getSelectedUserAlias());
        return new IsFollowerResponse(isFollower);
    }

    private void validateCountRequest(CountRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an alias of the target user");
        }else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request to check followers/following count needs to have an authtoken");
        }
        if (! isValidAuthtoken(request.getAuthToken().getDatetime())) {
            throw new RuntimeException("[Bad Request] Expired authtoken");
        }
    }

    public CountResponse getFollowersCount(CountRequest request) {
        validateCountRequest(request);
//        Hardcoded followers count
        int numFollowers = followDAO.getFollowersCount(request.getTargetUserAlias());
        return new CountResponse(numFollowers);
    }

    public CountResponse getFollowingCount(CountRequest request) {
        validateCountRequest(request);
        int numFollowing = followDAO.getFollowingCount(request.getTargetUserAlias());
//        Hardcoded following count
        return new CountResponse(numFollowing);
    }


}
