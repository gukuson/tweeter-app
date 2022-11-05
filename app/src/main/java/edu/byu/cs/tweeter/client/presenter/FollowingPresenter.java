package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User>{
    public FollowingPresenter(PagedView<User> view) {
        super(view);
    }

    @Override
    public void getItems(AuthToken currUserAuthToken, User user, int pageSize, User lastItem) {
        new FollowService().loadMoreFollowees(Cache.getInstance().getCurrUserAuthToken(), user, pageSize,
                lastItem, new PagedObserver());
    }

    @Override
    public String getDescription() {
        return "get following";
    }

}
