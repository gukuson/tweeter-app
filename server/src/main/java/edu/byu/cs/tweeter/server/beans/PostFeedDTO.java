package edu.byu.cs.tweeter.server.beans;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class PostFeedDTO {
    private Status status;
    private List<String> receiverAliases;

    public PostFeedDTO() {
    }

    public PostFeedDTO(Status status, List<String> receiverAliases) {
        this.status = status;
        this.receiverAliases = receiverAliases;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getReceiverAliases() {
        return receiverAliases;
    }

    public void setReceiverAliases(List<String> receiverAliases) {
        this.receiverAliases = receiverAliases;
    }
}
