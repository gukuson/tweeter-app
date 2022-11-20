package edu.byu.cs.tweeter.client.model.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeTest {
    private ServerFacade serverFacade;
    private FakeData fakeData;
    private User allen;

    @BeforeEach
    public void setup() {
        serverFacade = new ServerFacade();
        fakeData = FakeData.getInstance();

        allen = fakeData.getFirstUser();
    }


    @Test
    public void registerTest() throws IOException, TweeterRemoteException {
        RegisterRequest registerRequest = new RegisterRequest("First Name", "Last Name", "username", "password", "imageBytes");

        AuthenticateResponse response = serverFacade.register(registerRequest, RegisterTask.URL_PATH);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
        Assertions.assertNotNull(response.getAuthToken());
        Assertions.assertNotNull(response.getUser());

        Assertions.assertNull(response.getAuthToken().getToken());
        Assertions.assertNull(response.getAuthToken().getDatetime());
        Assertions.assertEquals(allen, response.getUser());
    }

    @Test
    public void getFollowersTest() throws IOException, TweeterRemoteException {
        GetFollowRequest followersRequest = new GetFollowRequest(fakeData.getAuthToken(), "username", 10, null);

        FollowersResponse response = serverFacade.getFollowers(followersRequest, GetFollowersTask.URL_PATH);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.getHasMorePages());

        Assertions.assertNotNull(response.getFollowers());

        Assertions.assertEquals(10, response.getFollowers().size());
        Assertions.assertEquals(allen, response.getFollowers().get(0));

    }

    @Test
    public void getFollowersCountTest() throws IOException, TweeterRemoteException {
        CountRequest request = new CountRequest(fakeData.getAuthToken(), "username");

        CountResponse response = serverFacade.getCount(request, GetFollowersCountTask.URL_PATH);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Assertions.assertEquals(20, response.getCount());
    }
}
