package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

public class JobHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        int i = 0;
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println("In JobHandler for loop, in loop" + i);
            ++i;
            DAOFactory daoFactory = new DynamoDAOFactory();
            daoFactory.getFeedDao().addFeedBatch(msg.getBody());
        }
        return null;
    }
}