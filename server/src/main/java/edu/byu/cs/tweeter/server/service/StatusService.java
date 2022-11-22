package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.IAuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IStoryDAO;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService extends Service{
    IFollowDAO followDAO;
    IFeedDAO feedDao;
    IStoryDAO storyDAO;
    IAuthtokenDAO authtokenDAO;

    public StatusService(DAOFactory daoFactory) {
        super(daoFactory);
        followDAO = daoFactory.getFollowDao();
        feedDao = daoFactory.getFeedDao();
        storyDAO = daoFactory.getStoryDao();
        authtokenDAO = daoFactory.getAuthtokenDao();
    }

    public Response postStatus(PostStatusRequest request) {
        if(request.getNewStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request to post status needs to have a an authtoken");
        }
        if (! isValidAuthtoken(request.getAuthToken().getDatetime())) {
            throw new RuntimeException("[Bad Request] Expired authtoken");
        }

        storyDAO.addPostToStory(request.getNewStatus());

        String senderAlias = request.getNewStatus().getUser().getAlias();
        List<String> allFollowersOfSender = followDAO.getAllFollowersAliases(senderAlias);

//        4B This will be in batches
        for (String followerAlias : allFollowersOfSender) {
            feedDao.addPostToFeed(followerAlias, request.getNewStatus());
        }

        return new Response(true);
    }

    public Pair<List<Status>, Boolean> getPagedStatuses(StatusesRequest request, IStatusDAO statusDAO) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request for story needs to have a positive limit");
        }else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request to get story needs to have a an authtoken");
        }
        if (! isValidAuthtoken(request.getAuthToken().getDatetime())) {
            throw new RuntimeException("[Bad Request] Expired authtoken");
        }

        return statusDAO.getPagedStatuses(request.getFollowerAlias(), request.getLimit(), request.getLastTimestamp() );
    }

    public StoryResponse getStory(StatusesRequest request) {

        Pair<List<Status>, Boolean> pagedOfStatuses = getPagedStatuses(request, storyDAO);

        return new StoryResponse(pagedOfStatuses.getFirst(), pagedOfStatuses.getSecond());
    }

    public FeedResponse getFeed(StatusesRequest request) {
        Pair<List<Status>, Boolean> pagedOfStatuses = getPagedStatuses(request, feedDao);

        return new FeedResponse(pagedOfStatuses.getFirst(), pagedOfStatuses.getSecond());
    }


}
