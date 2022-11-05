package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.response.Response;

public abstract class SimpleAuthenticatedTask extends AuthenticatedTask {


    protected SimpleAuthenticatedTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
    }

    @Override
    protected void runTask() throws IOException, TweeterRemoteException {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually access the database from here when we aren't using dummy data.
        Response response = sendServerRequest();

        if (response.isSuccess()) {
            sendSuccessMessage();
        } else {
            sendFailedMessage(response.getMessage());
        }
    }

    protected abstract Response sendServerRequest() throws IOException, TweeterRemoteException;
}
