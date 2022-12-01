package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface IStatusDAO {
    Pair<List<Status>, Boolean> getPagedStatuses(String currHandle, int pageSize, Long timestamp);
}
