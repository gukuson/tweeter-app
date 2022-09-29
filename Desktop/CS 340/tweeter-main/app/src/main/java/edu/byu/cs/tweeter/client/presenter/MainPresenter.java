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
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter {

    private static final String LOG_TAG = "MainPresenter";

    public interface View {
        void displayMessage(String message);
        void setFollowButtonVisibility(boolean isVisble);
        void setFollowButtonText(int following);
        void showIsFollowing();
        void showNotFollowing();
        void setFollowerCount(String count);
        void setFollowingCount(String count);
        void updateFollowingFollowers();
        void setFollowButtonEnabled(boolean value);

        void clearInfoMessage();

        void goToLogin();

        void setPostingToastText(String message);

        void displayLogoutMessage(String message);
    }

    private View view;
    private FollowService followService;

    public MainPresenter(View view) {
        this.view = view;
        followService = new FollowService();
    }


    public void makePost(String post) {
        view.setPostingToastText("Posting Status...");

        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));

            new StatusService().postStatus(Cache.getInstance().getCurrUserAuthToken(),
                    newStatus, new MakePostObserver());

        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    private class MakePostObserver implements StatusService.MakePostObserver {

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }

        @Override
        public void cancelPostingToast() {
            view.clearInfoMessage();
        }
    }

    public void logout() {
        view.displayLogoutMessage("Logging Out...");
        new UserService().logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver());

    }

    private class LogoutObserver implements UserService.LogoutObserver{

        @Override
        public void logoutSuccess() {
            view.clearInfoMessage();
            logoutUser();
        }

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
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

    private class GetFollowersCountObserver implements FollowService.GetFollowersCountObserver {

        @Override
        public void setFollowerCount(String count) {
            view.setFollowerCount(count);
        }

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }
    }

    private class GetFollowingCountObserver implements FollowService.GetFollowingCountObserver {

        @Override
        public void setFollowingCount(String count) {
            view.setFollowingCount(count);
        }

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
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

    private class UnfollowObserver implements FollowService.UnfollowObserver {
        public void unfollowSuccess() {
            view.updateFollowingFollowers();
            updateFollowButton(true);
            view.setFollowButtonEnabled(true);
        }

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
            view.setFollowButtonEnabled(true);
        }
    }

    private class FollowObserver implements FollowService.FollowObserver {

        @Override
        public void followSuccess() {
            view.updateFollowingFollowers();
            updateFollowButton(false);
            view.setFollowButtonEnabled(true);
        }

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
            view.setFollowButtonEnabled(true);
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

    private class IsFollowerObserver implements FollowService.IsFollowerObserver {

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }

        @Override
        public void isFollowing() {
            view.setFollowButtonText(R.string.following);
            view.showIsFollowing();
        }

        @Override
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
