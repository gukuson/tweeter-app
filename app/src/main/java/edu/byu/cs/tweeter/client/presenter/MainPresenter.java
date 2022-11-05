package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.GetCountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.view.View;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter<MainPresenter.MainView> {

    private static final String LOG_TAG = "MainPresenter";
    private StatusService statusService;
    private final FollowService followService = new FollowService();

    public MainPresenter(MainView view) {
        super(view);
    }

    public interface MainView extends View {
        void setFollowButtonVisibility(boolean isVisble);
        void setFollowButtonText(int following);
        void showIsFollowing();
        void showNotFollowing();
        void setFollowerCount(String count);
        void setFollowingCount(String count);
        void updateFollowingFollowers();
        void setFollowButtonEnabled(boolean value);
        void goToLogin();
        void clearMessage();
    }

    public StatusService getStatusService() {
        if (statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }


    public abstract class SimpleFailObserver implements ServiceObserver {
        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage(failString(getDescription()) + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage(exceptionString(getDescription()) + exception.getMessage());
        }

        public abstract String getDescription();
    }

    public void makePost(String post) {
        view.displayMessage("Posting Status...");
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            getStatusService().postStatus(Cache.getInstance().getCurrUserAuthToken(),
                    newStatus, new MakePostObserver());

        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    public class MakePostObserver extends SimpleFailObserver implements SimpleNotificationObserver {

        @Override
        public String getDescription() {
            return "make post";
        }

        @Override
        public void handleSuccess() {
            view.clearMessage();
            view.displayMessage("Successfully Posted!");
        }
    }

    public void logout() {
        view.displayMessage("Logging Out...");
        new UserService().logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver());
    }

    private class LogoutObserver extends SimpleFailObserver implements SimpleNotificationObserver{

        @Override
        public String getDescription() {
            return "logout";
        }

        @Override
        public void handleSuccess() {
            view.clearMessage();
            logoutUser();
        }
    }

    public void logoutUser() {
        view.goToLogin();
        //Clear user data (cached data).
        Cache.getInstance().clearCache();
    }


    public void updateFollowingFollowers(User selectedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Get count of most recently selected user's followers.
        followService.getFollowersCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, executor, new GetFollowersCountObserver());

        // Get count of most recently selected user's followees (who they are following)
        followService.getFollowingCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, executor, new GetFollowingCountObserver());
    }

    private abstract class GetFollowersFollowingObserver extends SimpleFailObserver implements GetCountObserver {
        @Override
        public void setCount(String count) {
            setFollowCount(count);
        }

        protected abstract void setFollowCount(String count);
    }

    private class GetFollowersCountObserver extends GetFollowersFollowingObserver {

        @Override
        public String getDescription() {
            return "get followers count";
        }

        @Override
        protected void setFollowCount(String count) {
            view.setFollowerCount(count);
        }
    }

    private class GetFollowingCountObserver extends GetFollowersFollowingObserver {

        @Override
        public String getDescription() {
            return "get following count";
        }

        @Override
        public void setFollowCount(String count) {
            view.setFollowingCount(count);
        }

    }

    private abstract class FollowButtonObserver implements SimpleNotificationObserver {

        @Override
        public void handleFailure(String message) {
            view.displayMessage(failString(getDescription()) + message);
            view.setFollowButtonEnabled(true);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage(exceptionString(getDescription()) + exception.getMessage());
            view.setFollowButtonEnabled(true);
        }

        @Override
        public void handleSuccess() {
            view.updateFollowingFollowers();
            startUpdateFollowButton();
            view.setFollowButtonEnabled(true);
        }

        protected abstract String getDescription();
        protected abstract void startUpdateFollowButton();
    }

    private class UnfollowObserver extends FollowButtonObserver {

        @Override
        protected String getDescription() {
            return "unfollow";
        }

        @Override
        protected void startUpdateFollowButton() {
            updateFollowButton(true);
        }
    }

    private class FollowObserver extends FollowButtonObserver {

        @Override
        protected String getDescription() {
            return "follow";
        }

        @Override
        protected void startUpdateFollowButton() {
            updateFollowButton(false);
        }
    }

    public void toggleFollowButton(String buttonString, String isFollowing, User selectedUser) {
        if (buttonString.equals(isFollowing)) {
            followService.unfollow(Cache.getInstance().getCurrUserAuthToken(),
                    selectedUser, new UnfollowObserver());

            view.displayMessage("Removing " + selectedUser.getName() + "...");
        } else {
            followService.follow(Cache.getInstance().getCurrUserAuthToken(),
                    selectedUser, new FollowObserver());

            view.displayMessage("Adding " + selectedUser.getName() + "...");
        }
    }

    public void updateFollowButton(boolean removed) {
        // If follow relationship was removed.
        if (removed) {
            view.setFollowButtonText(R.string.follow);
            view.showNotFollowing();
        } else {
            view.setFollowButtonText(R.string.following);
            view.showIsFollowing();
        }
    }

    public void setFollowVisibility(User selectedUser) {
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            view.setFollowButtonVisibility(false);
        } else {
            view.setFollowButtonVisibility(true);
            followService.isFollower(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(),
                    selectedUser, new IsFollowerObserver());

        }
    }

    public class IsFollowerObserver extends SimpleFailObserver implements ServiceObserver {

//        private final String description = "determine following relationship";

        @Override
        public String getDescription() {
            return "determine following relationship";
        }

        public void isFollowing() {
            view.setFollowButtonText(R.string.following);
            view.showIsFollowing();
        }

        public void notFollowing() {
            view.setFollowButtonText(R.string.follow);
            view.showNotFollowing();
        }

    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }
}
