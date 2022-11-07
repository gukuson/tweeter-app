package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status>{
    public StoryPresenter(PagedView<Status> view) {
        super(view);
    }

    @Override
    public void getItems(AuthToken currUserAuthToken, User user, int pageSize, Status lastItem) {
        new StatusService().getStory(currUserAuthToken, user,
                pageSize, lastItem, getPagedObserver());
    }

    @Override
    public String getDescription() {
        return "get story";
    }

    public PagedObserver getPagedObserver() {
        return new PagedObserver();
    }
}
