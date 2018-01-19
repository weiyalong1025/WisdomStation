package com.winsion.wisdomstation.modules.operation.modules.mytask.fragment;

import android.annotation.SuppressLint;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseFragment;
import com.winsion.wisdomstation.common.listener.StateListener;
import com.winsion.wisdomstation.data.constants.OpeType;
import com.winsion.wisdomstation.modules.operation.adapter.MyTaskAdapter;
import com.winsion.wisdomstation.modules.operation.biz.TaskCommBiz;
import com.winsion.wisdomstation.modules.operation.constants.TaskSpinnerState;
import com.winsion.wisdomstation.modules.operation.constants.TaskState;
import com.winsion.wisdomstation.modules.operation.entity.JobEntity;
import com.winsion.wisdomstation.modules.operation.modules.mytask.activity.MyTaskActivity;
import com.winsion.wisdomstation.utils.ConvertUtils;
import com.winsion.wisdomstation.utils.constants.Formatter;
import com.winsion.wisdomstation.view.SpinnerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by 10295 on 2017/12/15 0015.
 * 我的任务Fragment
 */

public class MyTaskFragment extends BaseFragment implements MyTaskContract.View, AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, SpinnerView.AfterTextChangeListener, MyTaskAdapter.OnButtonClickListener {
    @BindView(R.id.sv_spinner)
    SpinnerView svSpinner;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_hint)
    TextView tvRetry;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.lv_list)
    ListView lvList;
    @BindView(R.id.train_number_index)
    TextView trainNumberIndex;
    @BindView(R.id.iv_shade)
    ImageView ivShade;

    private MyTaskContract.Presenter mPresenter;
    private MyTaskAdapter mLvAdapter;
    private int mCurrentSysType = -1;
    // 当前显示数据
    private List<JobEntity> listData = new ArrayList<>();
    // 全部的
    private List<JobEntity> allData = new ArrayList<>();
    // 未开始
    private List<JobEntity> unStartedData = new ArrayList<>();
    // 进行中
    private List<JobEntity> underwayData = new ArrayList<>();
    // 已完成
    private List<JobEntity> doneData = new ArrayList<>();
    // 记录选了哪个状态进行筛选
    private int statusPosition = TaskSpinnerState.STATE_ALL;

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_task, null);
    }

    @Override
    protected void init() {
        initPresenter();
        initView();
        initListener();
        startCountTimeByRxAndroid();
    }

    private void initPresenter() {
        mPresenter = new MyTaskPresenter(this);
    }

    private void initView() {
        swipeRefresh.setColorSchemeResources(R.color.blue1);

        // 初始化车站选项
        List<String> stationList = new ArrayList<>();
        stationList.add(getString(R.string.station_name));
        svSpinner.setFirstOptionData(stationList);
        svSpinner.setFirstOptionItemClickListener((position) -> {
            showView(flContainer, progressBar);
            mPresenter.getMyTaskData(mCurrentSysType);
        });

        // 初始化状态选项
        List<String> statusList = Arrays.asList(getResources().getStringArray(R.array.taskStatusArray));
        svSpinner.setSecondOptionData(statusList);
        svSpinner.setSecondOptionItemClickListener((position) -> {
            svSpinner.clearSearchContent();
            statusPosition = position;
            filterData();
        });

        // 根据Spinner显示状态显隐透明背景
        svSpinner.setPopupDisplayChangeListener(status -> {
            switch (status) {
                case SpinnerView.POPUP_SHOW:
                    ivShade.setVisibility(View.VISIBLE);
                    break;
                case SpinnerView.POPUP_HIDE:
                    ivShade.setVisibility(View.GONE);
                    break;
            }
        });

        mLvAdapter = new MyTaskAdapter(mContext, listData);
        lvList.setAdapter(mLvAdapter);
    }

    private void initListener() {
        swipeRefresh.setOnRefreshListener(() -> mPresenter.getMyTaskData(mCurrentSysType));
        lvList.setOnItemClickListener(this);
        lvList.setOnScrollListener(this);
        svSpinner.setAfterTextChangeListener(this);
        mLvAdapter.setOnButtonClickListener(this);
    }

    /**
     * 更改任务状态按钮点击事件
     *
     * @param jobEntity 该条目对应的job
     * @param button    该条目上的button按钮
     */
    @Override
    public void onButtonClick(JobEntity jobEntity, View button) {
        // 更改任务状态按钮点击事件
        boolean isFinish = jobEntity.getWorkstatus() == TaskState.RUN;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
            button.setEnabled(false);
            int opeType = isFinish ? OpeType.COMPLETE : OpeType.BEGIN;
            TaskCommBiz.changeJobStatus(mContext, jobEntity, opeType, new StateListener() {
                @Override
                public void onSuccess() {
                    button.setEnabled(true);
                    String currentTime = ConvertUtils.formatDate(System.currentTimeMillis(), Formatter.DATE_FORMAT1);
                    if (isFinish) {
                        jobEntity.setWorkstatus(TaskState.DONE);
                        jobEntity.setRealendtime(currentTime);
                        underwayData.remove(jobEntity);
                        doneData.add(jobEntity);
                    } else {
                        jobEntity.setWorkstatus(TaskState.RUN);
                        jobEntity.setRealstarttime(currentTime);
                        unStartedData.remove(jobEntity);
                        underwayData.add(jobEntity);
                    }
                    filterData();
                    scrollToItem(jobEntity.getTasksid());
                }

                @Override
                public void onFailed() {
                    button.setEnabled(true);
                    showToast(R.string.change_the_state_of_failure);
                }
            });
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        int titleStrRes = isFinish ? R.string.sure_you_want_to_finish : R.string.sure_you_want_to_start;
        builder.setTitle(getString(titleStrRes));
        builder.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JobEntity jobEntity = listData.get(position);
        MyTaskActivity.startMyTaskActivity(mContext, jobEntity);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_IDLE:
                trainNumberIndex.setVisibility(View.GONE);
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
                if (listData.size() <= 2) {
                    return;
                }
                trainNumberIndex.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (listData.size() >= 2) {
            String trainNumber = listData.get(firstVisibleItem + 1).getTrainnumber();
            if (isEmpty(trainNumber)) {
                trainNumber = getString(R.string.nothing);
            }
            trainNumberIndex.setText(trainNumber);
        }
    }

    private String lastText;

    @Override
    public void afterTextChange(Editable s) {
        // 解决fragment切换造成多次调用该方法
        String s1 = s.toString().trim().toLowerCase();
        if (equals(lastText, s1)) {
            return;
        }

        lastText = s1;
        if (TextUtils.isEmpty(s.toString())) {
            filterData();
        } else {
            listData.clear();
            for (JobEntity task : getSameStatusList(statusPosition)) {
                String number = task.getTrainnumber();
                number = TextUtils.isEmpty(number) ? getString(R.string.nothing) : number;
                String trainNumber = number.toLowerCase();
                if (trainNumber.startsWith(s1)) {
                    listData.add(task);
                }
            }
            mLvAdapter.notifyDataSetChanged();
        }
    }

    private List<JobEntity> getSameStatusList(int status) {
        List<JobEntity> tempList = new ArrayList<>();
        switch (status) {
            case TaskSpinnerState.STATE_ALL:
                tempList.addAll(doneData);
                tempList.addAll(0, unStartedData);
                tempList.addAll(0, underwayData);
                break;
            case TaskSpinnerState.STATE_NOT_START:
                tempList.addAll(unStartedData);
                break;
            case TaskSpinnerState.STATE_DOING:
                tempList.addAll(underwayData);
                break;
            case TaskSpinnerState.STATE_DONE:
                tempList.addAll(doneData);
                break;
        }
        return tempList;
    }

    /**
     * 间隔60s刷新一次页面，实现计时效果
     */
    private void startCountTimeByRxAndroid() {
        Observable.interval(0, 60, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long aLong) -> mLvAdapter.notifyDataSetChanged());
    }

    @Override
    public void onResume() {
        super.onResume();
        // 根据系统类型获取数据 生产/网格
        int sysType = mPresenter.getCurrentSystemType();
        if (mCurrentSysType != sysType) {
            mCurrentSysType = sysType;
            showView(flContainer, progressBar);
            mPresenter.getMyTaskData(mCurrentSysType);
        }
    }

    @Override
    public void getMyTaskDataSuccess(List<JobEntity> data) {
        swipeRefresh.setRefreshing(false);
        svSpinner.clearSearchContent();
        allData.clear();
        allData.addAll(data);
        unStartedData.clear();
        underwayData.clear();
        doneData.clear();
        // 根据任务状态分类
        for (JobEntity task : allData) {
            int workStatus = task.getWorkstatus();
            switch (workStatus) {
                case TaskState.GRID_NOT_PASS:
                case TaskState.NOT_STARTED:
                    unStartedData.add(task);
                    break;
                case TaskState.RUN:
                    underwayData.add(task);
                    break;
                case TaskState.DONE:
                    doneData.add(task);
                    break;
            }
        }
        if (unStartedData.size() + underwayData.size() + doneData.size() == 0) {
            tvRetry.setText(R.string.no_data_click_to_retry);
            showView(flContainer, tvRetry);
        } else {
            filterData();
            showView(flContainer, swipeRefresh);
        }
    }

    @Override
    public void getMyTaskDataFailed() {
        swipeRefresh.setRefreshing(false);
        tvRetry.setText(getString(R.string.failure_load_click_retry));
        showView(flContainer, tvRetry);
    }

    /**
     * 过滤显示的数据
     */
    private void filterData() {
        listData.clear();
        listData.addAll(getSameStatusList(statusPosition));
        mLvAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tv_hint)
    public void onViewClicked() {
        showView(flContainer, progressBar);
        mPresenter.getMyTaskData(mCurrentSysType);
    }

    /**
     * 滚动到对应taskId条目的位置
     */
    public void scrollToItem(String taskId) {
        for (int i = 0; i < listData.size(); i++) {
            JobEntity jobEntity = listData.get(i);
            if (equals(jobEntity.getTasksid(), taskId)) {
                lvList.smoothScrollToPosition(i);
                break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (trainNumberIndex.getVisibility() == View.VISIBLE) {
            trainNumberIndex.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}