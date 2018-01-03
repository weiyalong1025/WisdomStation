package com.winsion.wisdomstation.operation.modules.issue.fragment;

import android.content.Intent;
import android.view.View;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseFragment;
import com.winsion.wisdomstation.operation.modules.issue.activity.IssueActivity;
import com.winsion.wisdomstation.operation.constants.TaskType;

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
                Intent intent = new Intent(mContext, IssueActivity.class);
                intent.putExtra(IssueActivity.ISSUE_TYPE, TaskType.COMMAND);
                startActivity(intent);
                break;
            case R.id.ll_issue_cooperation:
                Intent intent1 = new Intent(mContext, IssueActivity.class);
                intent1.putExtra(IssueActivity.ISSUE_TYPE, TaskType.COOPERATE);
                startActivity(intent1);
                break;
        }
    }
}
