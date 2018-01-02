package com.winsion.wisdomstation.operation.activity.issue;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.operation.constants.TaskType;
import com.winsion.wisdomstation.view.TitleView;

import butterknife.BindView;

/**
 * Created by wyl on 2016/8/1.
 */
public class IssueActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TitleView tvTitle;

    /**
     * 发布类型(命令/协作)
     * {@link com.winsion.wisdomstation.operation.constants.TaskType}
     */
    public static final String ISSUE_TYPE = "type";

    @Override
    protected int setContentView() {
        return R.layout.activity_issue;
    }

    @Override
    protected void start() {
        int issueType = getIntent().getIntExtra(ISSUE_TYPE, TaskType.COMMAND);
        switch (issueType){
            case TaskType.COMMAND:
                tvTitle.setTitleText(getString(R.string.issue_command));
                break;
            case TaskType.COOPERATE:
                tvTitle.setTitleText(getString(R.string.issue_cooperate));
                break;
        }
    }
}
