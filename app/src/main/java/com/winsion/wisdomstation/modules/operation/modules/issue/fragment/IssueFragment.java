package com.winsion.wisdomstation.modules.operation.modules.issue.fragment;

import android.view.View;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseFragment;
import com.winsion.wisdomstation.modules.operation.constants.TaskType;
import com.winsion.wisdomstation.modules.operation.modules.issue.activity.IssueActivity;

import butterknife.OnClick;

/**
 * Created by admin on 2016/8/11.
 */
public class IssueFragment extends BaseFragment {

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
