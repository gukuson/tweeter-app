package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;

/**
 * A paged response for a {@link FollowingRequest}.
 */
public class FeedResponse extends PagedResponse<Status> {

    private List<Status> feed;

    public FeedResponse() {
        super();
    }

    public List<Status> getFeed() {
        return feed;
    }

    public void setFeed(List<Status> feed) {
        this.feed = feed;
    }

    /**
     * Creates a response indicating that the corresponding request was unsuccessful. Sets the
     * success and more pages indicators to false.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public FeedResponse(String message) {
        super(false, message, false);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param feed the Statuses to be included in the result.
     * @param hasMorePages an indicator of whether more data is available for the request.
     */
    public FeedResponse(List<Status> feed, boolean hasMorePages) {
        super(true, hasMorePages, feed);
        this.feed = feed;
    }


    @Override
    public boolean equals(Object param) {
        if (this == param) {
            return true;
        }

        if (param == null || getClass() != param.getClass()) {
            return false;
        }

        FeedResponse that = (FeedResponse) param;

        return (Objects.equals(getItems(), that.getItems()) &&
                Objects.equals(this.getMessage(), that.getMessage()) &&
                this.isSuccess() == that.isSuccess());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItems());
    }

    @Override
    protected List<Status> getParsedItems() {
        return feed;
    }
}
