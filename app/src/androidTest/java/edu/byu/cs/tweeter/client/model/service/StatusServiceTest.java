package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.observer.PagedTaskObserver;
import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.client.view.main.story.StoryFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusServiceTest {
    private FakeData fakeData;
    private StatusService statusService;
    private AuthToken authToken;
    private User allen;
    private CountDownLatch countDownLatch;
    private StoryObserver spyObserver;

    @BeforeEach
    public void setup() {
        fakeData = FakeData.getInstance();
        statusService = new StatusService();
        authToken = fakeData.getAuthToken();
        allen = fakeData.getFirstUser();
        resetCountDownLatch();
        spyObserver = Mockito.spy(new StoryObserver());
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @Test
    public void getStoryTest() throws IOException, TweeterRemoteException, InterruptedException {
        statusService.getStory(authToken, allen, 3, null, spyObserver);
        awaitCountDownLatch();

        Assertions.assertTrue(spyObserver.isSuccess());
        Assertions.assertNull(spyObserver.getMessage());
//        Couldn't set it equal since fakedata create statuses with different dates
        Assertions.assertEquals(3, spyObserver.getStory().size());
        Assertions.assertTrue(spyObserver.isHasMorePages());
        Assertions.assertNull(spyObserver.getException());

        Mockito.verify(spyObserver).addItems(Mockito.anyList(), Mockito.eq(true));
    }

public class StoryObserver implements PagedTaskObserver<Status> {
    private boolean success;
    private String message;
    private List<Status> story;
    private boolean hasMorePages;
    private Exception exception;

    @Override
    public void addItems(List<Status> items, boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
        this.story = items;
        this.success = true;
        this.message = null;
        this.exception = null;

        countDownLatch.countDown();
    }

    @Override
    public void handleFailure(String message) {
        this.success = false;
        this.message = message;
        this.story = null;
        this.hasMorePages = false;
        this.exception = null;

        countDownLatch.countDown();
    }

    @Override
    public void handleException(Exception exception) {
        this.success = false;
        this.message = null;
        this.story = null;
        this.hasMorePages = false;
        this.exception = exception;

        countDownLatch.countDown();
    }


    @Override
    public String failString(String description) {
        return PagedTaskObserver.super.failString(description);
    }

    @Override
    public String exceptionString(String description) {
        return PagedTaskObserver.super.exceptionString(description);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Status> getStory() {
        return story;
    }

    public void setStory(List<Status> story) {
        this.story = story;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}

}
