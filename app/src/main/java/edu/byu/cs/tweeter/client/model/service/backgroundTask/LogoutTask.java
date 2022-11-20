package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.Response;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends SimpleAuthenticatedTask {

    private static final String LOG_TAG = "LogoutTask";
    public static final String URL_PATH = "/logout";

    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
    }
//
//    @Override
//    protected void runTask() {
//        // We could do this from the presenter, without a task and handler, but we will
//        // eventually remove the auth token from  the DB and will need this then.
//        try {
//            LogoutRequest request = new LogoutRequest(getAuthToken());
//            Response response = getServerFacade().logout(request, URL_PATH);
//
//            if (response.isSuccess()) {
//                sendSuccessMessage();
//            } else {
//                sendFailedMessage(response.getMessage());
//            }
//        } catch (Exception ex) {
//            Log.e(LOG_TAG, ex.getMessage(), ex);
//            sendExceptionMessage(ex);
//        }
//    }

    @Override
    protected Response sendServerRequest() throws IOException, TweeterRemoteException {
        LogoutRequest request = new LogoutRequest(getAuthToken());
        return getServerFacade().logout(request, URL_PATH);
    }

    @Override
    protected void logException(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
    }
}
