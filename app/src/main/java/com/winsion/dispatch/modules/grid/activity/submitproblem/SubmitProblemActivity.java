package com.winsion.dispatch.modules.grid.activity.submitproblem;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseActivity;
import com.winsion.dispatch.view.TitleView;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.tv_title)
    TitleView tvTitle;
    @BindView(R.id.tv_site)
    TextView tvSite;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.tv_subclass)
    TextView tvSubclass;
    @BindView(R.id.tv_grade)
    TextView tvGrade;
    @BindView(R.id.tv_time_limit)
    TextView tvTimeLimit;
    @BindView(R.id.et_word_content)
    EditText etWordContent;
    @BindView(R.id.lv_photo_list)
    ListView lvPhotoList;

    @Override
    protected int setContentView() {
        return R.layout.activity_submit_problem;
    }

    @Override
    protected void start() {
        initTitleView();
    }

    private void initTitleView() {
        tvTitle.setOnBackClickListener(v -> finish());
        tvTitle.setOnConfirmClickListener(v -> submit());
    }

    /**
     * 上报问题
     */
    private void submit() {

    }

    @OnClick({R.id.tv_device_name, R.id.iv_scan, R.id.tv_subclass, R.id.iv_take_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_device_name:
                break;
            case R.id.iv_scan:
                break;
            case R.id.tv_subclass:
                break;
            case R.id.iv_take_photo:
                break;
        }
    }
}
