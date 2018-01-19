package com.winsion.wisdomstation.modules.operation.modules.mytask.activity;

import android.content.Context;
import android.content.Intent;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.modules.operation.entity.JobEntity;

import javax.annotation.Nonnull;

/**
 * Created by 10295 on 2018/1/19.
 * 我的任务Activity
 * 协作/命令/任务/网格/预案
 */

public class MyTaskActivity extends BaseActivity implements MyTaskContract.View {
    private static final String TASK_ENTITY = "taskEntity";

    private JobEntity mJobEntity;

    public static void startMyTaskActivity(Context context, @Nonnull JobEntity jobEntity) {
        Intent intent = new Intent(context, MyTaskActivity.class);
        intent.putExtra(TASK_ENTITY, jobEntity);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_my_task;
    }

    @Override
    protected void start() {
        initData();
    }

    private void initData() {
        mJobEntity = (JobEntity) getIntent().getSerializableExtra(TASK_ENTITY);
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
