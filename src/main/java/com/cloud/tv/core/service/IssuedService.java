package com.cloud.tv.core.service;

import com.cloud.tv.entity.Policy;
import com.cloud.tv.entity.Task;

import java.util.List;

public interface IssuedService {

    void pushtaskstatuslist();

    List<Task> query();

    String queryTask(String invisibleName, String type, List<Policy> policysNew);
}
