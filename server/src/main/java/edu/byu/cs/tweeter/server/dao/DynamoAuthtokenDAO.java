package edu.byu.cs.tweeter.server.dao;

import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.beans.Authtoken;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;

public class DynamoAuthtokenDAO extends DynamoDAO implements IAuthtokenDAO{
    private static final String TableName = "authtoken";
    private final DynamoDbTable<Authtoken> table = getClient().table(TableName, TableSchema.fromBean(Authtoken.class));

//    public static void main(String[] args) {
//        new DynamoAuthtokenDAO().deleteAuthtoken("f51a2ba7-dbcf-432f-acd4-ba37ab05fe22");
//    }

    @Override
    public AuthToken createAuthtoken(String alias) {
        UUID uuid = UUID.randomUUID();

        Authtoken newAuthtoken = new Authtoken();
        newAuthtoken.setToken(uuid.toString());

        newAuthtoken.setUser_alias(alias);

        long currentTime = System.currentTimeMillis();
        newAuthtoken.setTimestamp(currentTime);

        table.putItem(newAuthtoken);

        return new AuthToken(uuid.toString(), currentTime);
    }

    @Override
    public void deleteAuthtoken(String token) {
        Key key = Key.builder().
                partitionValue(token)
                .build();

        table.deleteItem(key);
    }

    @Override
    public String getAlias(AuthToken authToken) {
        Key key = Key.builder()
                .partitionValue(authToken.token)
                .build();

        return table.getItem(key).getUser_alias();
    }


    @Override
    <T, D> T getDTO(D item) {
        return null;
    }

    @Override
    <T> WriteBatch.Builder<T> getWriteBatchBuilder() {
        return null;
    }

    @Override
    <T> DynamoDbTable<T> getTable() {
        return null;
    }
}
