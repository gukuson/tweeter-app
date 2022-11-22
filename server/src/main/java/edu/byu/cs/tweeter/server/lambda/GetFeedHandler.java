package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

/**
 * An AWS lambda function that returns the users a user is following.
 */
public class GetFeedHandler implements RequestHandler<StatusesRequest, FeedResponse> {

    @Override
    public FeedResponse handleRequest(StatusesRequest request, Context context) {
        StatusService service = new StatusService(new DynamoDAOFactory());
        return service.getFeed(request);
    }

}
