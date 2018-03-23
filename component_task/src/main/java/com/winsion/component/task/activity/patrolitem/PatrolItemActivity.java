package com.winsion.component.task.activity.patrolitem;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.utils.ConvertUtils;
import com.winsion.component.basic.constants.Formatter;
import com.winsion.component.basic.view.CustomDialog;
import com.winsion.component.basic.view.TitleView;
import com.winsion.component.task.R;
import com.winsion.component.task.activity.submitproblem.SubmitProblemActivity;
import com.winsion.component.task.adapter.PatrolItemAdapter;
import com.winsion.component.task.biz.SubmitBiz;
import com.winsion.component.task.constants.DeviceState;
import com.winsion.component.task.constants.PatrolItemState;
import com.winsion.component.task.entity.PatrolItemEntity;
import com.winsion.component.task.entity.PatrolPlanEntity;

import org.greenrobot.eventbus.EventBus;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static com.winsion.component.task.constants.Intents.PatrolItem.PATROL_TASK_ENTITY;
import static com.winsion.component.task.constants.Intents.SubmitProblem.DEVICE_DEPENDENT;
import static com.winsion.component.task.constants.Intents.SubmitProblem.PATROL_ITEM_ENTITY;
import static com.winsion.component.task.constants.Intents.SubmitProblem.SITE_NAME;

/**
 * Created by 10295 on 2018/2/1.
 * 巡检项界面
 */

public class PatrolItemActivity extends BaseActivity implements PatrolItemContract.View,
        PatrolItemAdapter.Operator, SubmitBiz.SubmitListener {
    private TitleView tvTitle;
    private TextView tvArriveTime;
    private TextView tvFinishTime;
    private ListView lvList;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvHint;
    private FrameLayout flContainer;

    private static final int CODE_SUBMIT = 0; // 上报问题REQUEST_CODE

    private PatrolPlanEntity patrolPlanEntity;  // 上个页面带过来的
    private PatrolItemContract.Presenter mPresenter;
    private List<PatrolItemEntity> listData = new ArrayList<>();
    private PatrolItemAdapter mAdapter;
    private PatrolItemEntity devicePatrolItem;    // 跳转设备报修界面时带过去

    @Override
    protected int setContentView() {
        return R.layout.task_activity_patrol_item;
    }

    @Override
    protected void start() {
        initPresenter();
        initView();
        initIntentData();
        initViewData();
        initListener();
        initAdapter();
        initPatrolItemData();
    }

    private void initPresenter() {
        mPresenter = new PatrolItemPresenter(this);
        mPresenter.start();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvArriveTime = findViewById(R.id.tv_arrive_time);
        tvFinishTime = findViewById(R.id.tv_finish_time);
        lvList = findViewById(R.id.lv_list);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progress_bar);
        tvHint = findViewById(R.id.tv_hint);
        flContainer = findViewById(R.id.fl_container);
    }

    private void initIntentData() {
        patrolPlanEntity = (PatrolPlanEntity) getIntent().getSerializableExtra(PATROL_TASK_ENTITY);
        if (patrolPlanEntity == null) throw new InvalidParameterException();
    }

    private void initViewData() {
        // 到位时间
        String realStartTime = patrolPlanEntity.getRealstarttime();
        if (!equals(realStartTime, "-- --")) {
            tvArriveTime.setText(realStartTime.split(" ")[1].substring(0, 5));
        } else {
            tvArriveTime.setText(R.string.symbol_hour_minute);
        }

        // 完成时间
        String realEndTime = patrolPlanEntity.getRealendtime();
        if (!equals(realEndTime, "-- --")) {
            tvFinishTime.setText(realEndTime.split(" ")[1].substring(0, 5));
        } else {
            tvFinishTime.setText(R.string.symbol_hour_minute);
        }
    }

    private void initListener() {
        tvTitle.setOnBackClickListener(v -> finish());
        tvTitle.setOnConfirmClickListener(v -> {
            if (devicePatrolItem == null) {
                showToast(R.string.toast_get_patrol_data_first);
            } else {
                // 设备报修
                Intent intent = new Intent(mContext, SubmitProblemActivity.class);
                intent.putExtra(PATROL_ITEM_ENTITY, devicePatrolItem);
                intent.putExtra(SITE_NAME, patrolPlanEntity.getPointname());
                intent.putExtra(DEVICE_DEPENDENT, true);
                startActivityForResult(intent, CODE_SUBMIT);
            }
        });
        swipeRefresh.setColorSchemeResources(R.color.basic_blue1);
        swipeRefresh.setOnRefreshListener(() -> mPresenter.getPatrolItemData(patrolPlanEntity.getId()));
        addOnClickListeners(R.id.tv_hint);
    }

    private void initAdapter() {
        mAdapter = new PatrolItemAdapter(mContext, listData);
        mAdapter.setOperator(this);
        lvList.setAdapter(mAdapter);
    }

    @Override
    public void onNormalClick(PatrolItemEntity patrolItemEntity) {
        if (equals(patrolItemEntity.getDevicestate(), PatrolItemState.UNDONE)) {
            new CustomDialog.NormalBuilder(mContext)
                    .setMessage(R.string.dialog_sure_no_problem)
                    .setPositiveButton((dialog, which) -> ((SubmitBiz) mPresenter)
                            .submitWithoutDevice(patrolItemEntity, DeviceState.WORK, this))
                    .show();
        }
    }

    @Override
    public void onAbnormalClick(PatrolItemEntity patrolItemEntity) {
        if (equals(patrolItemEntity.getDevicestate(), PatrolItemState.UNDONE)) {
            new CustomDialog.CheckBoxBuilder(mContext)
                    .setMessage(R.string.dialog_sure_exist_problem)
                    .setCbHint(R.string.dialog_add_problem_desc)
                    .setPositiveButton((dialog, which) -> {
                        CustomDialog customDialog = (CustomDialog) dialog;
                        CustomDialog.CheckBoxBuilder builder = (CustomDialog.CheckBoxBuilder) customDialog.getBuilder();
                        if (builder.getCheckState()) {
                            // 跳转添加描述信息界面
                            Intent intent = new Intent(mContext, SubmitProblemActivity.class);
                            intent.putExtra(PATROL_ITEM_ENTITY, patrolItemEntity);
                            intent.putExtra(SITE_NAME, patrolPlanEntity.getPointname());
                            intent.putExtra(DEVICE_DEPENDENT, false);
                            startActivityForResult(intent, CODE_SUBMIT);
                        } else {
                            // 不添加描述，直接上报问题
                            ((SubmitBiz) mPresenter).submitWithoutDevice(patrolItemEntity,
                                    DeviceState.FAILURE, this);
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CODE_SUBMIT) {
            PatrolItemEntity patrolItemEntity = (PatrolItemEntity) data.getSerializableExtra(PATROL_ITEM_ENTITY);
            submitSuccess(patrolItemEntity, DeviceState.FAILURE);
        }
    }

    @Override
    public void submitSuccess(PatrolItemEntity patrolItemEntity, String deviceState) {
        String currentTime = ConvertUtils.formatDate(System.currentTimeMillis(), Formatter.DATE_FORMAT1);
        // 检查是否需要设置到位时间和完成时间
        if (isFirstItemFinish()) {
            patrolPlanEntity.setRealstarttime(currentTime);
            initViewData();
        }
        if (isLastItemFinish()) {
            patrolPlanEntity.setRealendtime(currentTime);
            initViewData();
        }

        // 是否是与设备相关的问题
        boolean deviceDependent = true;
        for (PatrolItemEntity entity : listData) {
            if (entity.getId().equals(patrolItemEntity.getId())) {
                deviceDependent = false;
                patrolItemEntity = entity;
                break;
            }
        }

        if (!deviceDependent) {
            // 设置巡检项操作后状态和操作时间
            patrolItemEntity.setDevicestate(equals(deviceState, DeviceState.WORK) ? PatrolItemState.NORMAL : PatrolItemState.ABNORMAL);
            patrolItemEntity.setPatroltime(currentTime);
            mAdapter.notifyDataSetChanged();

            // 已完成巡检项数量+1
            int finishCount = patrolPlanEntity.getFinishcount();
            patrolPlanEntity.setFinishcount(++finishCount);

            // 发送广播通知上个界面(PatrolPlanFragment)同步状态
            EventBus.getDefault().post(patrolPlanEntity);
        }
    }

    @Override
    public void submitFailed() {
        showToast(R.string.toast_submit_failed);
    }

    private boolean isLastItemFinish() {
        int count = 0;
        for (PatrolItemEntity patrolItemDto : listData) {
            if (equals(patrolItemDto.getDevicestate(), PatrolItemState.UNDONE)) {
                count++;
            }
        }
        return count == 1;
    }

    private boolean isFirstItemFinish() {
        int count = 0;
        for (PatrolItemEntity patrolItemDto : listData) {
            if (!equals(patrolItemDto.getDevicestate(), PatrolItemState.UNDONE)) {
                count++;
            }
        }
        return count == 0;
    }

    private void initPatrolItemData() {
        mPresenter.getPatrolItemData(patrolPlanEntity.getId());
    }

    @Override
    public void getPatrolItemDataSuccess(List<PatrolItemEntity> patrolItemEntities) {
        listData.clear();
        swipeRefresh.setRefreshing(false);
        if (patrolItemEntities.size() == 0) {
            devicePatrolItem = null;
            tvHint.setText(R.string.hint_no_data_click_retry);
            showView(flContainer, tvHint);
        } else {
            for (PatrolItemEntity patrolItemDto : patrolItemEntities) {
                if (equals(patrolItemDto.getItemdescribe(), "设备检查")) {
                    devicePatrolItem = patrolItemDto;
                } else {
                    listData.add(patrolItemDto);
                }
            }
            mAdapter.notifyDataSetChanged();
            showView(flContainer, swipeRefresh);
        }
    }

    @Override
    public void getPatrolItemDataFailed() {
        listData.clear();
        devicePatrolItem = null;
        swipeRefresh.setRefreshing(false);
        tvHint.setText(R.string.hint_load_failed_click_retry);
        showView(flContainer, tvHint);
    }

    @Override
    public void onClick(View view) {
        showView(flContainer, progressBar);
        mPresenter.getPatrolItemData(patrolPlanEntity.getId());
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}
