package com.winsion.component.task.fragment.taskmonitor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.constants.OpeType;
import com.winsion.component.basic.view.CustomDialog;
import com.winsion.component.basic.view.SpinnerView;
import com.winsion.component.task.R;
import com.winsion.component.task.activity.taskmonitor.MonitorTaskDetailActivity;
import com.winsion.component.task.adapter.MonitorTaskListAdapter;
import com.winsion.component.task.adapter.delegate.GridDelegate;
import com.winsion.component.task.constants.TaskSpinnerState;
import com.winsion.component.task.constants.TaskState;
import com.winsion.component.task.constants.TaskType;
import com.winsion.component.task.entity.TaskEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static com.winsion.component.task.constants.Intents.MontorTaskDetail.TASK_ENTITY;

/**
 * Created by 10295 on 2017/12/25
 * 任务监控Fragment
 */

public class MonitorTaskListFragment extends BaseFragment implements MonitorTaskListContract.View, AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, SpinnerView.AfterTextChangeListener, GridDelegate.ConfirmButtonListener {
    private SpinnerView svSpinner;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvHint;
    private FrameLayout flContainer;
    private ListView lvList;
    private TextView trainNumberIndex;
    private ImageView ivShade;

    /**
     * 请求数据中
     */
    public static final int GETTING = 0;
    /**
     * 请求数据成功
     */
    public static final int GET_SUCCESS = 1;
    /**
     * 请求数据失败
     */
    public static final int GET_FAILED = 2;

    private int getDataState = GETTING;   // 请求数据状态
    private MonitorTaskListContract.Presenter mPresenter;
    private MonitorTaskListAdapter mLvAdapter;
    private List<TaskEntity> listData = new ArrayList<>();  // 当前显示数据
    private List<TaskEntity> allData = new ArrayList<>();   // 全部的
    private List<TaskEntity> unStartedData = new ArrayList<>(); // 未开始
    private List<TaskEntity> underwayData = new ArrayList<>();  // 进行中
    private List<TaskEntity> doneData = new ArrayList<>();  // 已完成
    private int statusPosition = TaskSpinnerState.STATE_ALL;    // 记录选了哪个状态进行筛选
    private String lastText = "";    // 搜索框中上一次输入的文字
    private Disposable timer;   // 定时刷新界面，更新执行时间

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.task_fragment_task_list, null);
    }

    @Override
    protected void init() {
        initPresenter();
        initView();
        initAdapter();
        initListener();
        initData();
        startCountTimeByRxAndroid();
    }

    private void initPresenter() {
        mPresenter = new MonitorTaskListPresenter(this);
    }

    private void initView() {
        svSpinner = findViewById(R.id.sv_spinner);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progress_bar);
        tvHint = findViewById(R.id.tv_hint);
        flContainer = findViewById(R.id.fl_container);
        lvList = findViewById(R.id.lv_list);
        trainNumberIndex = findViewById(R.id.train_number_index);
        ivShade = findViewById(R.id.iv_shade);

        swipeRefresh.setColorSchemeResources(R.color.basic_blue1);

        // 初始化车站选项
        List<String> stationList = new ArrayList<>();
        stationList.add(getString(R.string.spinner_station_name));
        svSpinner.setFirstOptionData(stationList);

        // 初始化状态选项
        List<String> statusList = Arrays.asList(getResources().getStringArray(R.array.taskStatusArray));
        svSpinner.setSecondOptionData(statusList);
    }

    private void initAdapter() {
        mLvAdapter = new MonitorTaskListAdapter(mContext, listData);
        lvList.setAdapter(mLvAdapter);
    }

    private void initListener() {
        swipeRefresh.setOnRefreshListener(this::initData);
        mLvAdapter.getGridDelegate().setConfirmButtonListener(this);
        lvList.setOnItemClickListener(this);
        lvList.setOnScrollListener(this);
        svSpinner.setAfterTextChangeListener(this);

        svSpinner.setFirstOptionItemClickListener((position) -> {
            showView(flContainer, progressBar);
            initData();
        });

        svSpinner.setSecondOptionItemClickListener((position) -> {
            statusPosition = position;
            filterData(true);
        });

        // 根据Spinner显示状态显隐透明背景
        svSpinner.setPopupDisplayChangeListener(status -> {
            switch (status) {
                case SpinnerView.PopupState.POPUP_SHOW:
                    ivShade.setVisibility(View.VISIBLE);
                    break;
                case SpinnerView.PopupState.POPUP_HIDE:
                    ivShade.setVisibility(View.GONE);
                    break;
            }
        });

        addOnClickListeners(R.id.tv_hint);
    }

    private void initData() {
        getDataState = GETTING;
        mPresenter.getMonitorTaskData();
    }


    @Override
    public void onPassButtonClick(TaskEntity taskEntity) {
        new CustomDialog.NormalBuilder(mContext)
                .setMessage(R.string.dialog_sure_to_pass)
                .setPositiveButton((dialog, which) -> {
                    taskEntity.setInOperation(true);
                    mLvAdapter.notifyDataSetChanged();
                    mPresenter.confirm(taskEntity, OpeType.PASS);
                })
                .show();
    }

    @Override
    public void onNotPassButtonClick(TaskEntity taskEntity) {
        new CustomDialog.NormalBuilder(mContext)
                .setMessage(R.string.dialog_sure_to_not_pass)
                .setPositiveButton((dialog, which) -> {
                    taskEntity.setInOperation(true);
                    mLvAdapter.notifyDataSetChanged();
                    mPresenter.confirm(taskEntity, OpeType.NOT_PASS);
                })
                .show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TaskEntity taskEntity = listData.get(position);
        if (taskEntity.getTaktype() != TaskType.GRID) {
            Intent intent = new Intent(mContext, MonitorTaskDetailActivity.class);
            intent.putExtra(TASK_ENTITY, taskEntity);
            startActivity(intent);
        }
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
                trainNumber = getString(R.string.value_nothing);
            }
            trainNumberIndex.setText(trainNumber);
        }
    }

    @Override
    public void afterTextChange(Editable s) {
        if (getDataState != GET_SUCCESS) {
            return;
        }
        lastText = s.toString().trim();
        if (TextUtils.isEmpty(lastText)) {
            filterData(false);
        } else {
            String filterStr = lastText.toLowerCase();
            listData.clear();
            for (TaskEntity task : getSameStatusList(statusPosition)) {
                String number = task.getTrainnumber();
                number = TextUtils.isEmpty(number) ? getString(R.string.value_nothing) : number;
                String trainNumber = number.toLowerCase();
                if (trainNumber.contains(filterStr)) {
                    listData.add(task);
                }
            }
            mLvAdapter.notifyDataSetChanged();

            if (listData.size() == 0) {
                tvHint.setText(R.string.hint_no_data_click_retry);
                showView(flContainer, tvHint);
            } else {
                showView(flContainer, swipeRefresh);
            }
        }
    }

    private List<TaskEntity> getSameStatusList(int status) {
        List<TaskEntity> tempList = new ArrayList<>();
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
        timer = Observable.interval(30, 30, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long aLong) -> mLvAdapter.notifyDataSetChanged());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (trainNumberIndex.getVisibility() == View.VISIBLE) {
            trainNumberIndex.setVisibility(View.GONE);
        }
    }

    @Override
    public void getMonitorTaskDataSuccess(List<TaskEntity> data) {
        getDataState = GET_SUCCESS;
        swipeRefresh.setRefreshing(false);
        allData.clear();
        allData.addAll(data);
        unStartedData.clear();
        underwayData.clear();
        doneData.clear();
        // 根据任务状态分类
        for (TaskEntity task : allData) {
            int workStatus = task.getTaskstatus();
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
        filterData(true);
    }

    @Override
    public void getMonitorTaskDataFailed() {
        getDataState = GET_FAILED;
        swipeRefresh.setRefreshing(false);
        tvHint.setText(getString(R.string.hint_load_failed_click_retry));
        showView(flContainer, tvHint);
    }

    @Override
    public void confirmFailed(String tasksId) {
        showToast(R.string.toast_confirm_failed);
        for (TaskEntity taskEntity : listData) {
            if (taskEntity.getTasksid().equals(tasksId)) {
                taskEntity.setInOperation(false);
                mLvAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void confirmSuccess(String tasksId, int opeType) {
        for (TaskEntity taskEntity : listData) {
            if (taskEntity.getTasksid().equals(tasksId)) {
                if (opeType == OpeType.PASS) {
                    listData.remove(taskEntity);
                } else {
                    taskEntity.setInOperation(false);
                    taskEntity.setTaskstatus(TaskState.GRID_NOT_PASS);
                    mLvAdapter.notifyDataSetChanged();
                }
                break;
            }
        }
    }

    /**
     * 过滤显示的数据
     *
     * @param restoreSearchContent 是否按照搜索框中文字进行筛选
     */
    private void filterData(boolean restoreSearchContent) {
        listData.clear();
        listData.addAll(getSameStatusList(statusPosition));
        mLvAdapter.notifyDataSetChanged();

        if (restoreSearchContent) {
            svSpinner.setSearchContent(lastText);
        } else {
            if (listData.size() == 0) {
                tvHint.setText(R.string.hint_no_data_click_retry);
                showView(flContainer, tvHint);
            } else {
                showView(flContainer, swipeRefresh);
            }
        }
    }

    @Override
    public void onClick(View v) {
        showView(flContainer, progressBar);
        initData();
    }

    /**
     * 滚动到对应taskId条目的位置
     */
    private void scrollToItem(TaskEntity taskEntity) {
        int positionInList = listData.indexOf(taskEntity);
        if (positionInList != -1) lvList.setSelection(positionInList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.dispose();
        mPresenter.exit();
    }
}
