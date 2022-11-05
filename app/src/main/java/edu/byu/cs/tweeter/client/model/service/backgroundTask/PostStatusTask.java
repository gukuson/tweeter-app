package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.Response;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends SimpleAuthenticatedTask {

    private static final String LOG_TAG = "PostStatusTask";
    public static final String URL_PATH = "/poststatus";
    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */
    private final Status status;

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(authToken, messageHandler);
        this.status = status;
    }

//    @Override
//    protected void runTask() {
//        // We could do this from the presenter, without a task and handler, but we will
//        // eventually access the database from here when we aren't using dummy data.
//
//        try {
//            PostStatusRequest request = new PostStatusRequest(getAuthToken(), status);
//            Response response = getServerFacade().postStatus(request, URL_PATH);
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
        PostStatusRequest request = new PostStatusRequest(getAuthToken(), status);
        return getServerFacade().postStatus(request, URL_PATH);
    }


    @Override
    protected void logException(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
    }
}
