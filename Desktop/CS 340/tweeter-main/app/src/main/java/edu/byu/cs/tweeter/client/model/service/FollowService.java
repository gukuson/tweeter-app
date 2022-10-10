package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import java.util.concurrent.ExecutorService;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.GetCountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.PagedTaskObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public void getFollowersCount(AuthToken currUserAuthToken, User selectedUser, ExecutorService executor, GetCountObserver observer) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(currUserAuthToken,
                selectedUser, new GetCountHandler(observer));
        executor.execute(followersCountTask);
    }

    public void getFollowingCount(AuthToken currUserAuthToken, User selectedUser, ExecutorService executor, GetCountObserver observer) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(currUserAuthToken,
                selectedUser, new GetCountHandler(observer));
        executor.execute(followingCountTask);
    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver simpleNotificationObserver) {
        FollowTask followTask = new FollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(simpleNotificationObserver));
        BackgroundTaskUtils.runTask(followTask);
    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver simpleNotificationObserver) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(simpleNotificationObserver));
        BackgroundTaskUtils.runTask(unfollowTask);

    }

    public void isFollower(AuthToken currUserAuthToken, User currUser, User selectedUser, MainPresenter.IsFollowerObserver isFollowerObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken,
                currUser, selectedUser, new IsFollowerHandler(isFollowerObserver));
        BackgroundTaskUtils.runTask(isFollowerTask);
    }

    // IsFollowerHandler

    private static class IsFollowerHandler extends BackgroundTaskHandler<MainPresenter.IsFollowerObserver> {
        public IsFollowerHandler(MainPresenter.IsFollowerObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(MainPresenter.IsFollowerObserver observer, Bundle data) {
            boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            // If logged in user if a follower of the selected user, display the follow button as "following"
            if (isFollower) {
                observer.isFollowing();
            } else {
                observer.notFollowing();
            }
        }
    }

    public void loadMoreFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, PagedTaskObserver<User> observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollower, new PagedTaskHandler<User>(observer));
        BackgroundTaskUtils.runTask(getFollowersTask);
    }

    public void loadMoreFollowees(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedTaskObserver<User> observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new PagedTaskHandler<User>(observer));
        BackgroundTaskUtils.runTask(getFollowingTask);
    }

}
