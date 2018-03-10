package com.winsion.component.task.fragment.issue;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.task.R;
import com.winsion.component.task.activity.issue.IssueActivity;
import com.winsion.component.task.constants.TaskType;

/**
 * Created by admin on 2016/8/11.
 * 发布命令/协作一级界面
 */
public class IssueFragment extends BaseFragment {

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.task_fragment_issue, null);
    }

    @Override
    protected void init() {
        addOnClickListeners(R.id.ll_issue_order, R.id.ll_issue_cooperation);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_issue_order) {
            IssueActivity.startIssueActivity(mContext, TaskType.COMMAND);
        } else if (id == R.id.ll_issue_cooperation) {
            IssueActivity.startIssueActivity(mContext, TaskType.COOPERATE);
        }
    }
}
