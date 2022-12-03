package edu.byu.cs.tweeter.server.beans;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class User {
    private String firstname;
    private String lastname;
    private String user_alias;
    private String image_url;

    private String password;

    public User() {
    }

    public User(edu.byu.cs.tweeter.model.domain.User user) {
        this.firstname = user.getFirstName();
        this.lastname = user.getLastName();
        this.user_alias = user.getAlias();
        this.image_url = user.getImageUrl();
    }

    @DynamoDbPartitionKey
    public String getUser_alias() {
        return user_alias;
    }

    public void setUser_alias(String user_alias) {
        this.user_alias = user_alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
