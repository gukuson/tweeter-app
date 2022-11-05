package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.service.StatusService;
import edu.byu.cs.tweeter.server.service.UserService;

public class PostStatusHandler implements RequestHandler<PostStatusRequest, Response> {
    @Override
    public Response handleRequest(PostStatusRequest request, Context context) {
        StatusService statusService = new StatusService();
        return statusService.postStatus(request);
    }
}
