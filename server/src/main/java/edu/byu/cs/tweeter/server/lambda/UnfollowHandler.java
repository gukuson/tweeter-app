package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowToggleRequest;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.service.FollowService;

public class UnfollowHandler implements RequestHandler<FollowToggleRequest, Response> {
    @Override
    public Response handleRequest(FollowToggleRequest request, Context context) {
        FollowService followService = new FollowService();
        return followService.unfollow(request);
    }
}