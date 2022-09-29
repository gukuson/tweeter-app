package edu.byu.cs.tweeter.client.presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter {
    private static final int PAGE_SIZE = 10;
    private View view;
    private User lastFollower;
    private boolean hasMorePages;
    private boolean isLoading = false;

    public FollowersPresenter(View view) {
        this.view = view;
    }

    public boolean isLoading() {
        return isLoading;
    }


    public interface View {
        void displayInfoMessage(String message);
        void setLoadingFooter(boolean value);
        void addFollowers(List<User> followees);
        void goToUser(User user);
        void loadNextPage();
    }

    public void loadMoreData(int visibleItemCount, int totalItemCount, int firstVisibleItemPosition) {
        if (!isLoading && hasMorePages) {
            if ((visibleItemCount + firstVisibleItemPosition) >=
                    totalItemCount && firstVisibleItemPosition >= 0) {
                // Run this code later on the UI thread
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    view.loadNextPage();
                }, 0);
            }
        }
    }


    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(true);
            new FollowService().loadMoreFollowers(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE,
                    lastFollower, new GetFollowersObserver());

        }
    }

    private class GetFollowersObserver implements FollowService.GetFollowersObserver {

        @Override
        public void addFollowers(List<User> followers, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
            FollowersPresenter.this.hasMorePages = hasMorePages;
            view.addFollowers(followers);
        }

        @Override
        public void displayErrorMessage(String message) {
            isLoading = false;

            view.setLoadingFooter(false);

            view.displayInfoMessage(message);
        }

    }

    public void fetchUser(String clickedAlias) {
        view.displayInfoMessage("Getting user's profile...");
        new UserService().getUser(Cache.getInstance().getCurrUserAuthToken(), clickedAlias, new GetUserObserver());
    }

    private class GetUserObserver implements UserService.GetUserObserver {
        @Override
        public void gotUser(User user) {
            view.goToUser(user);
        }

        @Override
        public void getUserFail(String message) {
            view.displayInfoMessage(message);
        }
    }

}
