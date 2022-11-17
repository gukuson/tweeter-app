package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.server.beans.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class DynamoUserDAO extends DynamoDAO implements IUserDAO{
    private static final String TableName = "user";

    private static final String UserAlias = "user_alias";

    private final DynamoDbTable<User> table = getClient().table(TableName, TableSchema.fromBean(User.class));

    private URL uploadImageToS3(String imageEncryption, String username) {
        byte[] decodedBytes = Base64.getDecoder().decode(imageEncryption);

        InputStream fis = new ByteArrayInputStream(decodedBytes);

        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion("us-west-2")
                .build();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(decodedBytes.length);
        metadata.setContentType("image/png");
//        metadata.setCacheControl("public, max-age=31536000");
        String BUCKET_NAME = "stanton-cs340-bucket";
        String filename = username + "-image";
        s3.putObject(BUCKET_NAME, filename, fis, metadata);
        s3.setObjectAcl(BUCKET_NAME, filename, CannedAccessControlList.PublicRead);
        return s3.getUrl(BUCKET_NAME, filename);
    }

    @Override
    public edu.byu.cs.tweeter.model.domain.User registerUser(String username, String hashedPassword, String firstName, String lastName, String image) {
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        // see if user already exists
        User user = table.getItem(key);
        if(user != null) {
            System.out.println("This user already exists");
            return null;
        } else {
            URL imageUrl = uploadImageToS3(image, username);

            User newUser = new User();
            newUser.setUser_alias(username);
            newUser.setPassword(hashedPassword);
            newUser.setFirstname(firstName);
            newUser.setLastname(lastName);
            newUser.setImage_url(imageUrl.toString());

            table.putItem(newUser);
            return new edu.byu.cs.tweeter.model.domain.User(firstName, lastName, username, imageUrl.toString());
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Key key = Key.builder()
                .partitionValue(request.getUsername())
                .build();

        // see if user already exists
        User user = table.getItem(key);
        if (user != null) {
//            Correct username, then check password, create model user to return as well as authtoken to be created later
            if (Objects.equals(user.getPassword(), request.getPassword())) {
                edu.byu.cs.tweeter.model.domain.User modelUser = new edu.byu.cs.tweeter.model.domain.User(user.getFirstname(), user.getLastname(), user.getUser_alias(), user.getImage_url());
                return new LoginResponse(modelUser, null);
            }
        }
        return new LoginResponse("Incorrect username or password");
    }


}
