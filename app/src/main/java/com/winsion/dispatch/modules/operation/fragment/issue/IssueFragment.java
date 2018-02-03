package com.winsion.dispatch.modules.operation.fragment.issue;

import android.annotation.SuppressLint;
import android.view.View;

import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseFragment;
import com.winsion.dispatch.modules.operation.activity.issue.IssueActivity;
import com.winsion.dispatch.modules.operation.constants.TaskType;

import butterknife.OnClick;

/**
 * Created by admin on 2016/8/11.
 * 发布命令/协作一级界面
 */
public class IssueFragment extends BaseFragment {

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_issue, null);
    }

    @Override
    protected void init() {

    }

    @OnClick({R.id.ll_issue_order, R.id.ll_issue_cooperation})
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