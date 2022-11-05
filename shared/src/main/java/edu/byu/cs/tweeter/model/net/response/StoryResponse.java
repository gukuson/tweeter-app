package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;

/**
 * A paged response for a {@link FollowingRequest}.
 */
public class StoryResponse extends PagedResponse<Status> {

    private List<Status> story;

    public StoryResponse() {
        super();
    }

    public List<Status> getStory() {
        return story;
    }

    public void setStory(List<Status> story) {
        this.story = story;
    }

    /**
     * Creates a response indicating that the corresponding request was unsuccessful. Sets the
     * success and more pages indicators to false.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public StoryResponse(String message) {
        super(false, message, false);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param story the Statuses to be included in the result.
     * @param hasMorePages an indicator of whether more data is available for the request.
     */
    public StoryResponse(List<Status> story, boolean hasMorePages) {
        super(true, hasMorePages, story);
        this.story = story;
    }


    @Override
    public boolean equals(Object param) {
        if (this == param) {
            return true;
        }

        if (param == null || getClass() != param.getClass()) {
            return false;
        }

        StoryResponse that = (StoryResponse) param;

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
        return story;
    }
}
