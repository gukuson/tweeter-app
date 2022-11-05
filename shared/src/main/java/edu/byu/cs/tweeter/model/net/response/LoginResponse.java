package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;

/**
 * A response for a {@link LoginRequest}.
 */
public class LoginResponse extends AuthenticateResponse {

//    private User user;
//    private AuthToken authToken;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public LoginResponse(String message) {
        super(message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param user the now logged in user.
     * @param authToken the auth token representing this user's session with the server.
     */
    public LoginResponse(User user, AuthToken authToken) {
        super(user, authToken);
    }

//    /**
//     * Returns the logged in user.
//     *
//     * @return the user.
//     */
//    public User getUser() {
//        return user;
//    }
//
//    /**
//     * Returns the auth token.
//     *
//     * @return the auth token.
//     */
//    public AuthToken getAuthToken() {
//        return authToken;
//    }
}
