package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedTask<Status> {

    private static final String LOG_TAG = "GetStoryTask";
    public static final String URL_PATH = "/getstory";

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected void logException(Exception ex) {
        Log.e(LOG_TAG, "Failed to get story", ex);
    }

    @Override
    protected PagedResponse<Status> sendRequestToServer() throws IOException, TweeterRemoteException {
        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
        Long timestamp = getLastItem() == null ? null : getLastItem().getTimestamp();
//        String lastStatusUserAlias = getLastItem() == null ? null : getLastItem().getUser().getAlias();
//        String date = getLastItem() == null ? null : getLastItem().getDate();

        StoryRequest request = new StoryRequest(getAuthToken(), targetUserAlias, getLimit(), timestamp);
        return getServerFacade().getStory(request, URL_PATH);
    }

//    @Override
//    protected Pair<List<Status>, Boolean> getItems() {
//        return getFakeData().getPageOfStatus(getLastItem(), getLimit());
//    }
}
