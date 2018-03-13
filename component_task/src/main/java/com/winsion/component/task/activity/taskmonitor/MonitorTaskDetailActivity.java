package com.winsion.component.task.activity.taskmonitor;

import android.content.Context;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.task.entity.TaskEntity;

import java.io.Serializable;

import static com.winsion.component.task.constants.Intents.MontorTaskDetail.TASK_ENTITY;

/**
 * Created by 10295 on 2018/3/13.
 */

public class MonitorTaskDetailActivity extends BaseActivity implements MonitorTaskDetailContract.View {

    @Override
    protected int setContentView() {
        return 0;
    }

    @Override
    protected void start() {
        initData();
    }

    private void initData() {
        TaskEntity taskEntity = (TaskEntity) getIntent().getSerializableExtra(TASK_ENTITY);
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
