package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User>{
    public FollowersPresenter(PagedView<User> view) {
        super(view);
    }

    @Override
    public void getItems(AuthToken currUserAuthToken, User user, int pageSize, User lastItem) {
        new FollowService().loadMoreFollowers(currUserAuthToken, user, pageSize,
                    lastItem, new PagedObserver());
    }

    @Override
    public String getDescription() {
        return "get followers";
    }


}
