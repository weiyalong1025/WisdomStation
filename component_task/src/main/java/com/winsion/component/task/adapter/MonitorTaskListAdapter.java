package com.winsion.component.task.adapter;

import android.content.Context;

import com.winsion.component.task.adapter.delegate.GridDelegate;
import com.winsion.component.task.adapter.delegate.OperationDelegate;
import com.winsion.component.task.entity.TaskEntity;
import com.zhy.adapter.abslistview.MultiItemTypeAdapter;

import java.util.List;

/**
 * Created by 10295 on 2018/3/21.
 * 任务监控一级界面列表Item
 */

public class MonitorTaskListAdapter extends MultiItemTypeAdapter<TaskEntity> {
    private final OperationDelegate operationDelegate;
    private final GridDelegate gridDelegate;

    public MonitorTaskListAdapter(Context context, List<TaskEntity> data) {
        super(context, data);
        operationDelegate = new OperationDelegate(context, data);
        gridDelegate = new GridDelegate(data);

        addItemViewDelegate(operationDelegate);
        addItemViewDelegate(gridDelegate);
    }

    public OperationDelegate getOperationDelegate() {
        return operationDelegate;
    }

    public GridDelegate getGridDelegate() {
        return gridDelegate;
    }
}
