package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedTaskObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public void postStatus(AuthToken currUserAuthToken, Status newStatus, SimpleNotificationObserver observer) {
        PostStatusTask statusTask = new PostStatusTask(currUserAuthToken,
                newStatus, new SimpleNotificationHandler(observer));
        BackgroundTaskUtils.runTask(statusTask);
    }

    public void getStory(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedTaskObserver<Status> pagedTaskObserver) {
        GetStoryTask getStoryTask = new GetStoryTask(currUserAuthToken,
                user, pageSize, lastStatus, new PagedTaskHandler<Status>(pagedTaskObserver));
        BackgroundTaskUtils.runTask(getStoryTask);
    }

    public void getFeed(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedTaskObserver<Status> observer) {
        GetFeedTask getFeedTask = new GetFeedTask(currUserAuthToken,
                user, pageSize, lastStatus, new PagedTaskHandler<Status>(observer));
        BackgroundTaskUtils.runTask(getFeedTask);
    }

}
