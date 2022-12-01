package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

public class FollowerFetcherHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println("In FollowerFetcherHandler");
            PostStatusRequest request = new Gson().fromJson(msg.getBody(), PostStatusRequest.class);
            System.out.println("Succesfully parsed queue message to request object");
            System.out.println(request.toString());
            StatusService statusService = new StatusService(new DynamoDAOFactory());
            statusService.addJobsToQueue(request);
        }
        return null;
    }
}
