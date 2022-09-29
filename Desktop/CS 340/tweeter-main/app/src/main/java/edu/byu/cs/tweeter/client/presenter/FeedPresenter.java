package edu.byu.cs.tweeter.client.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.feed.FeedFragment;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter {

    public interface View {
        void displayInfoMessage(String message);
        void goToUser(User user);
        void openHttp(Uri parse);
        void setLoadingFooter(boolean value);
        void addStatuses(List<Status> statuses);
        void loadNextPage();
    }

    private static final int PAGE_SIZE = 10;
    private View view;
    private boolean hasMorePages;
    private Status lastStatus;
    private boolean isLoading = false;

    public boolean isLoading() {
        return isLoading;
    }
    public boolean hasMorePages() {
        return hasMorePages;
    }

    public FeedPresenter(View view) {
        this.view = view;

    }


    public void fetchUser(String alias) {
        view.displayInfoMessage("Getting user's profile...");
        new UserService().getUser(Cache.getInstance().getCurrUserAuthToken(), alias, new GetUserObserver());
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

    public void initializeFeed(Spanned s, int start, int end) {

        String clickable = s.subSequence(start, end).toString();

        if (clickable.contains("http")) {
            view.openHttp(Uri.parse(clickable));

        } else {
            fetchUser(clickable);

        }
    }

    public SpannableString formatUrls(Status status, SpannableString spannableString) {
        for (String url : status.getUrls()) {
            int startIndex = status.getPost().indexOf(url);
            spannableString.setSpan(new URLSpan(url), startIndex, (startIndex + url.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
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
        isLoading = true;
        view.setLoadingFooter(true);

        new StatusService().getFeed(Cache.getInstance().getCurrUserAuthToken(), user,
                PAGE_SIZE, lastStatus, new GetFeedObserver());

    }

    private class GetFeedObserver implements StatusService.GetFeedObserver {

        @Override
        public void addFeedStatuses(List<Status> statuses, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
            FeedPresenter.this.hasMorePages = hasMorePages;
            view.addStatuses(statuses);

        }

        @Override
        public void displayErrorMessage(String error) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayInfoMessage(error);
        }
    }


}
