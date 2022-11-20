package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.GetFollowRequest;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoUserDAO;

public class PaginatedServerTest {
    private FollowService followService;
    private GetFollowRequest followersRequest;

    private final String FAKE_IMG = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";

    @BeforeEach
    public void setup() {
        followService = new FollowService(new DynamoDAOFactory());
        followersRequest = new GetFollowRequest(new AuthToken(), "@AFollowee", 5, null);
    }

    @Test
    public void getFollowersTest() {
//        FollowersResponse followersResponse = followService.getFollowers(followersRequest);
        DynamoUserDAO userDAO = new DynamoUserDAO();
        for (int i = 15; i < 25; ++i) {
            userDAO.addUser("@Rob Boss" + i, "testpassword", "Rob", "Boss", FAKE_IMG);
        }
//        assertFalse(authenticateResponse.isSuccess());
//        System.out.println(authenticateResponse.getMessage());
    }

}
