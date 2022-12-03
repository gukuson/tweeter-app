package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IStoryDAO;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService extends Service{
    private final IFollowDAO followDAO;
    private final IFeedDAO feedDao;
    private final IStoryDAO storyDAO;
//    private final IAuthtokenDAO authtokenDAO;
    private final Gson gson;
    private final String POSTQ_URL = "https://sqs.us-west-2.amazonaws.com/633573532902/PostQueue";

    public StatusService(DAOFactory daoFactory) {
        super(daoFactory);
        followDAO = daoFactory.getFollowDao();
        feedDao = daoFactory.getFeedDao();
        storyDAO = daoFactory.getStoryDao();
        gson = new Gson();
//        authtokenDAO = daoFactory.getAuthtokenDao();
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

//        TODO: Put request into json, add to the PostQ, add to story, then return true
        String msgString = gson.toJson(request);

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(POSTQ_URL)
                .withMessageBody(msgString);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        sqs.sendMessage(send_msg_request);

        storyDAO.addPostToStory(request.getNewStatus());

////        4B This will be in batches
//        for (String followerAlias : allFollowersOfSender) {
//            feedDao.addPostToFeed(followerAlias, request.getNewStatus());
//        }

        return new Response(true);
    }

    public void addJobsToQueue(PostStatusRequest request) {
        String senderAlias = request.getNewStatus().getUser().getAlias();
        List<String> allFollowersOfSender = followDAO.getAllFollowersAliases(senderAlias);

        feedDao.createFeedBatches(allFollowersOfSender, request.getNewStatus());
//        if (allFollowersOfSender.size() > 25) {
//            for (int i = 0; i < allFollowersOfSender.size(); i += 25) {
//                List<String> batchedFollowers = allFollowersOfSender.subList(i, i + 25);
//            }
//        }else {
//            PostFeedDTO batchRequest = new PostFeedDTO(request.getNewStatus(), allFollowersOfSender);
//            gson.toJson(batchRequest);
//        }

    }

    public void addFeedBatch(String jsonString) {

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
