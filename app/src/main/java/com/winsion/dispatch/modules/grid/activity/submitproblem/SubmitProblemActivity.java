package com.winsion.dispatch.modules.grid.activity.submitproblem;

import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseActivity;

/**
 * Created by 10295 on 2018/2/2.
 * 上报问题界面
 */

public class SubmitProblemActivity extends BaseActivity {
    public static final String PATROL_DETAIL_ID = "patrolDetailId";
    public static final String SITE_NAME = "siteName";
    public static final String PROBLEM_TYPE = "problemType";


    // 与设备无关问题
    public static final int PROBLEM_WITHOUT_DEVICE = 0;
    // 与设备相关问题
    public static final int PROBLEM_WITH_DEVICE = 1;

    @Override
    protected int setContentView() {
        return R.layout.activity_submit_problem;
    }

    @Override
    protected void start() {

    }
}
