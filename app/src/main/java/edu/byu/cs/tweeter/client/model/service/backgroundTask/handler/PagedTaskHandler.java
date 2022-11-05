package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.observer.PagedTaskObserver;

public class PagedTaskHandler<T> extends BackgroundTaskHandler<PagedTaskObserver<T>>{
    public PagedTaskHandler(PagedTaskObserver<T> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagedTaskObserver<T> observer, Bundle data) {
        List<T> items = (List<T>) data.getSerializable(GetStoryTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetStoryTask.MORE_PAGES_KEY);
        observer.addItems(items, hasMorePages);
    }
}
