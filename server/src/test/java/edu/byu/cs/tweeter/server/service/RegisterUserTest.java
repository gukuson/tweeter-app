package edu.byu.cs.tweeter.server.service;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.UserService;

public class RegisterUserTest {
    private UserService userService;
    private RegisterRequest registerRequest;


    @BeforeEach
    public void setup() {
        userService = new UserService(new DynamoDAOFactory());
        registerRequest = new RegisterRequest( "firstName", "lastName", "username", "password", "imageBytes");
    }

    @Test
    public void registerTest() {
        AuthenticateResponse authenticateResponse = userService.register(registerRequest);
        assertFalse(authenticateResponse.isSuccess());
        System.out.println(authenticateResponse.getMessage());
    }

}
