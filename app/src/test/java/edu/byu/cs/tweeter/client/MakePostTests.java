package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * This class exists purely to prove that tests in your androidTest/java folder have the correct dependencies.
 * Click on the green arrow to the left of the class declarations to run. These tests should pass if all
 * dependencies are correctly set up.
 */
public class MakePostTests {
    private MainPresenter.MainView mockView;
    private StatusService mockService;
    private Cache mockCache;
    private MainPresenter presenterSpy;

    @BeforeEach
    public void setup() {
        mockView = Mockito.mock(MainPresenter.MainView.class);
        mockService = Mockito.mock(StatusService.class);
        mockCache = Mockito.mock(Cache.class);

        presenterSpy = Mockito.spy(new MainPresenter(mockView));
//        Mockito.doReturn(mockService).when(presenterSpy).getStatusService();
        Mockito.when(presenterSpy.getStatusService()).thenReturn(mockService);

        Cache.setInstance(mockCache);
    }

    @Test
    public void postSuccess() {
        Answer<Void> successAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AuthToken currAuthToken = invocation.getArgument(0);
                Status currStatus = invocation.getArgument(1);
                MainPresenter.MakePostObserver observer = invocation.getArgument(2, MainPresenter.MakePostObserver.class);

//                This should be checking that the input matches expected, like Test Authtoken
                Assertions.assertEquals(AuthToken.class, currAuthToken.getClass());
                Assertions.assertEquals(Status.class, currStatus.getClass());
                Assertions.assertEquals(MainPresenter.MakePostObserver.class, observer.getClass());

                observer.handleSuccess();
                return null;
            }
        };

        Mockito.when(mockCache.getCurrUserAuthToken()).thenReturn(new AuthToken("Test Authtoken"));

        Mockito.doAnswer(successAnswer).when(mockService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        String postString = "My test post";
        presenterSpy.makePost(postString);

        Mockito.verify(mockView).displayMessage("Posting Status...");
        Mockito.verify(mockView).clearMessage();
        Mockito.verify(mockView).displayMessage("Successfully Posted!");

    }

    @Test
    public void postFail() {
        Answer<Void> failAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainPresenter.MakePostObserver observer = invocation.getArgument(2, MainPresenter.MakePostObserver.class);
                observer.handleFailure("my fail string");
                return null;
            }
        };

        Mockito.doAnswer(failAnswer).when(mockService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        String postString = "My test post";
        presenterSpy.makePost(postString);

        Mockito.verify(mockView).displayMessage("Posting Status...");
        Mockito.verify(mockView, Mockito.times(0)).clearMessage();
        Mockito.verify(mockView).displayErrorMessage("Failed to make post: my fail string");
        Mockito.verify(mockView, Mockito.times(0)).displayMessage("Successfully Posted!");
    }

    @Test
    public void postException() {
        Answer<Void> exceptionAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainPresenter.MakePostObserver observer = invocation.getArgument(2, MainPresenter.MakePostObserver.class);
                observer.handleException(new Exception("Status was invalid"));
                return null;
            }
        };

        Mockito.doAnswer(exceptionAnswer).when(mockService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        String postString = "My test post";
        presenterSpy.makePost(postString);

        Mockito.verify(mockView).displayMessage("Posting Status...");
        Mockito.verify(mockView, Mockito.times(0)).clearMessage();
        Mockito.verify(mockView).displayErrorMessage("Failed to make post because of exception: Status was invalid");
        Mockito.verify(mockView, Mockito.times(0)).displayMessage("Successfully Posted!");
    }

}
