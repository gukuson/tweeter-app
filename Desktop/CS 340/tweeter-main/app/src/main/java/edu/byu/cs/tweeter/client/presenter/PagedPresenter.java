package edu.byu.cs.tweeter.client.presenter;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedTaskObserver;
import edu.byu.cs.tweeter.client.view.View;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter<PagedPresenter.PagedView<T>>{
    private T lastItem;
    private static final int PAGE_SIZE = 10;
    private boolean hasMorePages;
    private boolean isLoading = false;

    public PagedPresenter(PagedView<T> view) {
        super(view);
    }

    public interface PagedView<T> extends View {
        void goToUser(User user);
        void openHttp(Uri parse);
        void setLoadingFooter(boolean value);
        void addItems(List<T> items);
        void loadNextPage();
    }


    public boolean isLoading() {
        return isLoading;
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

    public SpannableString formatUrls(Status status, SpannableString spannableString) {
        for (String url : status.getUrls()) {
            int startIndex = status.getPost().indexOf(url);
            spannableString.setSpan(new URLSpan(url), startIndex, (startIndex + url.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    public void initializePage(Spanned s, int start, int end) {
        String clickable = s.subSequence(start, end).toString();

        if (clickable.contains("http")) {
            view.openHttp(Uri.parse(clickable));
        } else {
            fetchUser(clickable);
        }
    }

    public void fetchUser(String alias) {
        view.displayMessage("Getting user's profile...");
        new UserService().getUser(Cache.getInstance().getCurrUserAuthToken(), alias, new GetUserObserver());
    }

    private class GetUserObserver implements edu.byu.cs.tweeter.client.model.service.observer.GetUserObserver {
        private final String description = "get user's profile";
        @Override
        public void gotUser(User user) {
            view.goToUser(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage(failString(description) + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage(exceptionString(description) + exception.getMessage());
        }
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);

            getItems(Cache.getInstance().getCurrUserAuthToken(), user,
                    PAGE_SIZE, lastItem);
        }
    }

    abstract public void getItems(AuthToken currUserAuthToken, User user, int pageSize, T lastItem);
    abstract public String getDescription();

    public class PagedObserver implements PagedTaskObserver<T> {

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayErrorMessage(failString(getDescription()) + message);
        }

        @Override
        public void handleException(Exception exception) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayErrorMessage(exceptionString(getDescription()) + exception.getMessage());
        }

        @Override
        public void addItems(List<T> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            PagedPresenter.this.hasMorePages = hasMorePages;
            view.addItems(items);
        }
    }
}
