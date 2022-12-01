package edu.byu.cs.tweeter.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.IAuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;

public class UserService extends Service{
    IUserDAO userDAO;
    IAuthtokenDAO authtokenDAO;

    public UserService(DAOFactory daoFactory) {
        super(daoFactory);
        userDAO = daoFactory.getUserDao();
        authtokenDAO = daoFactory.getAuthtokenDao();
    }


    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        String hashedPassword = hashPassword(request.getPassword());
        request.setPassword(hashedPassword);

        LoginResponse response = userDAO.login(request);

        if (response.isSuccess()) {
//            Create authtoken and add to db
            AuthToken authToken = authtokenDAO.createAuthtoken(request.getUsername());
            response.setAuthToken(authToken);
        }
        return response;
    }

    public Response logout(LogoutRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] AuthToken is null");
        }
//        Delete authtoken from table
        authtokenDAO.deleteAuthtoken(request.getAuthToken().getToken());
        return new Response(true);
    }

    public AuthenticateResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        } else if (request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a first name");
        } else if (request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        } else if (request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing an image");
        }

        System.out.println("Before getting the userDAO register user");

        String hashedPassword = hashPassword(request.getPassword());
        User user = userDAO.registerUser(request.getUsername(), hashedPassword, request.getFirstName(), request.getLastName(), request.getImage());

        if (user == null) {
            return new AuthenticateResponse("Failed to register user, try a different alias/username");
        }else {
//          Create new authtoken and put in db
            AuthToken authToken = authtokenDAO.createAuthtoken(request.getUsername());
            return new AuthenticateResponse(user, authToken);
        }
    }


    public GetUserResponse getUser(GetUserRequest request) {
        if(request.getClickedAlias() == null){
            throw new RuntimeException("[Bad Request] User trying to fetch's alias is null");
        }
        else if(request.getCurrUserAuthToken() == null){
            throw new RuntimeException("[Bad Request] Authtoken is null while getting user");
        }

        if (isValidAuthtoken(request.getCurrUserAuthToken().getDatetime())) {
            User user = userDAO.getUser(request.getClickedAlias());
            if (user != null) {
                return new GetUserResponse(user);
            }else {
                return new GetUserResponse("Couldn't find user for " + request.getClickedAlias());
            }
        }else {
            throw new RuntimeException("[Bad Request] Authtoken expired");
        }
    }

}
