package com.winsion.dispatch.modules.operation.fragment.issue;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import com.winsion.component.basic.base.BaseFragment;
import com.winsion.dispatch.R;
import com.winsion.dispatch.modules.operation.activity.issue.IssueActivity;
import com.winsion.dispatch.modules.operation.constants.TaskType;

/**
 * Created by admin on 2016/8/11.
 * 发布命令/协作一级界面
 */
public class IssueFragment extends BaseFragment {

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_issue, null);
    }

    @Override
    protected void init() {
        addOnClickListeners(R.id.ll_issue_order, R.id.ll_issue_cooperation);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_issue_order:
                IssueActivity.startIssueActivity(mContext, TaskType.COMMAND);
                break;
            case R.id.ll_issue_cooperation:
                IssueActivity.startIssueActivity(mContext, TaskType.COOPERATE);
                break;
        }
    }
}
