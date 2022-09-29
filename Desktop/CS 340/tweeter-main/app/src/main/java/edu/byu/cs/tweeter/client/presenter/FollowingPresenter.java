package edu.byu.cs.tweeter.client.presenter;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.following.FollowingFragment;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter {
    private static final int PAGE_SIZE = 10;
    private FollowService service;
    private View view;
    private User lastFollowee;
    private boolean hasMorePages;
    private boolean isLoading = false;

    public FollowingPresenter(View view) {
        service = new FollowService();
        this.view = view;
    }

    public boolean isLoading() {
        return isLoading;
    }


    public interface View {
        void displayInfoMessage(String message);
        void setLoadingFooter(boolean value);
        void addFollowees(List<User> followees);
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

    public void loadMoreItems(User user) {
        isLoading = true;
        view.setLoadingFooter(true);
        service.loadMoreFollowees(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE,
                lastFollowee, new GetFollowingObserver());
    }

    private class GetFollowingObserver implements FollowService.GetFollowingObserver {

        @Override
        public void addFollowees(List<User> followees, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;
            FollowingPresenter.this.hasMorePages = hasMorePages;
            view.addFollowees(followees);
        }

        @Override
        public void displayErrorMessage(String message) {
            isLoading = false;

            view.setLoadingFooter(false);

            view.displayInfoMessage("Failed to get following: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            isLoading = false;

            view.setLoadingFooter(false);

            view.displayInfoMessage("Failed to get following because of exception: " + ex.getMessage());

        }
    }
}
