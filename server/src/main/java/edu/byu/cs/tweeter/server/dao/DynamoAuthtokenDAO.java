package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.server.beans.Authtoken;
import edu.byu.cs.tweeter.server.beans.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class DynamoAuthtokenDAO extends DynamoDAO implements IAuthtokenDAO{
    private static final String TableName = "authtoken";
    private final DynamoDbTable<Authtoken> table = getClient().table(TableName, TableSchema.fromBean(Authtoken.class));

    @Override
    public AuthToken createAuthtoken() {
        UUID uuid = UUID.randomUUID();

        Authtoken newAuthtoken = new Authtoken();
        newAuthtoken.setToken(uuid.toString());

        long currentTime = System.currentTimeMillis();
        newAuthtoken.setTimestamp(currentTime);

        table.putItem(newAuthtoken);

        return new AuthToken(uuid.toString(), currentTime);
    }
}
