//package edu.byu.cs.tweeter.client.model.service.backgroundTask;

//import android.os.Handler;
//
//import java.util.List;
//
//import edu.byu.cs.tweeter.model.domain.AuthToken;
//import edu.byu.cs.tweeter.model.domain.User;
//import edu.byu.cs.tweeter.util.Pair;
//
///**
// * Background task that retrieves a page of other users being followed by a specified user.
// */
//public class GetFollowingTask extends PagedUserTask {
//
//    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
//                            Handler messageHandler) {
//        super(authToken, targetUser, limit, lastFollowee, messageHandler);
//    }
//
//    @Override
//    protected Pair<List<User>, Boolean> getItems() {
//        return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
//    }
//}

package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowRequest;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedTask<User> {

    private static final String LOG_TAG = "GetFollowingTask";
    public static final String URL_PATH = "/getfollowing";

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastItem, Handler messageHandler) {
        super(authToken, targetUser, limit, lastItem, messageHandler);
    }
//
//    /**
//     * Auth token for logged-in user.
//     */
//    protected AuthToken authToken;
//    /**
//     * The user whose following is being retrieved.
//     * (This can be any user, not just the currently logged-in user.)
//     */
//    protected User targetUser;
//    /**
//     * Maximum number of followed users to return (i.e., page size).
//     */
//    protected int limit;
//    /**
//     * The last person being followed returned in the previous page of results (can be null).
//     * This allows the new page to begin where the previous page ended.
//     */
//    protected User lastFollowee;
//    /**
//     * The followee users returned by the server.
//     */
//    private List<User> followees;
//    /**
//     * If there are more pages, returned by the server.
//     */
//    private boolean hasMorePages;

//    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
//                            Handler messageHandler) {
//        super(messageHandler);
//        this.authToken = authToken;
//        this.targetUser = targetUser;
//        this.limit = limit;
//        this.lastFollowee = lastFollowee;
//    }
//
//    @Override
//    protected void runTask() {
//        try {
//            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
//            String lastFolloweeAlias = lastFollowee == null ? null : lastFollowee.getAlias();
//
//            FollowingRequest request = new FollowingRequest(authToken, targetUserAlias, limit, lastFolloweeAlias);
//            FollowingResponse response = getServerFacade().getFollowees(request, FollowService.URL_PATH);
//
//            if (response.isSuccess()) {
//                this.followees = response.getFollowees();
//                this.hasMorePages = response.getHasMorePages();
//                sendSuccessMessage();
//            } else {
//                sendFailedMessage(response.getMessage());
//            }
//        } catch (IOException | TweeterRemoteException ex) {
//            Log.e(LOG_TAG, "Failed to get followees", ex);
//            sendExceptionMessage(ex);
//        }
//    }

    @Override
    protected void logException(Exception ex) {
        Log.e(LOG_TAG, "Failed to get followees", ex);
    }

    @Override
    protected PagedResponse<User> sendRequestToServer() throws IOException, TweeterRemoteException {
        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
        String lastFolloweeAlias = getLastItem() == null ? null : getLastItem().getAlias();

        GetFollowRequest request = new GetFollowRequest(getAuthToken(), targetUserAlias, getLimit(), lastFolloweeAlias);
        return getServerFacade().getFollowees(request, URL_PATH);
    }

//    protected void loadSuccessBundle(Bundle msgBundle) {
//        msgBundle.putSerializable(PagedTask.ITEMS_KEY, (Serializable) this.followees);
//        msgBundle.putBoolean(PagedTask.MORE_PAGES_KEY, this.hasMorePages);
//    }

}
