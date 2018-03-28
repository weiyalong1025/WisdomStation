package com.winsion.component.task.fragment.taskoperator;

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
import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.constants.Formatter;
import com.winsion.component.basic.constants.OpeType;
import com.winsion.component.basic.listener.StateListener;
import com.winsion.component.basic.utils.ConvertUtils;
import com.winsion.component.basic.view.CustomDialog;
import com.winsion.component.basic.view.SpinnerView;
import com.winsion.component.task.R;
import com.winsion.component.task.activity.taskoperator.OperatorTaskDetailActivity;
import com.winsion.component.task.adapter.OperatorTaskListAdapter;
import com.winsion.component.task.biz.TaskBiz;
import com.winsion.component.task.constants.TaskSpinnerState;
import com.winsion.component.task.constants.TaskState;
import com.winsion.component.task.entity.JobEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static com.winsion.component.task.constants.Intents.OperatorTaskDetail.JOB_ENTITY;

/**
 * Created by 10295 on 2017/12/15 0015
 * 我的任务Fragment
 */

public class OperatorTaskListFragment extends BaseFragment implements OperatorTaskListContract.View, AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, SpinnerView.AfterTextChangeListener, OperatorTaskListAdapter.OnButtonClickListener {
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
    private OperatorTaskListContract.Presenter mPresenter;
    private OperatorTaskListAdapter mLvAdapter;
    private List<JobEntity> listData = new ArrayList<>();   // 当前显示数据
    private List<JobEntity> allData = new ArrayList<>();    // 全部的
    private List<JobEntity> unStartedData = new ArrayList<>();  // 未开始
    private List<JobEntity> underwayData = new ArrayList<>();   // 进行中
    private List<JobEntity> doneData = new ArrayList<>();   // 已完成
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
        mPresenter = new OperatorTaskListPresenter(this);
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
        mLvAdapter = new OperatorTaskListAdapter(mContext, listData);
        lvList.setAdapter(mLvAdapter);
    }

    private void initListener() {
        EventBus.getDefault().register(this);
        swipeRefresh.setOnRefreshListener(this::initData);
        lvList.setOnItemClickListener(this);
        lvList.setOnScrollListener(this);
        svSpinner.setAfterTextChangeListener(this);
        mLvAdapter.setOnButtonClickListener(this);

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
        mPresenter.getMyTaskData();
    }

    /**
     * 更改任务状态按钮点击事件
     *
     * @param jobEntity 该条目对应的job
     */
    @Override
    public void onButtonClick(JobEntity jobEntity) {
        // 更改任务状态按钮点击事件
        int workStatus = jobEntity.getWorkstatus();
        if (workStatus == TaskState.RUN || workStatus == TaskState.NOT_STARTED || workStatus == TaskState.GRID_NOT_PASS) {
            boolean isFinish = workStatus == TaskState.RUN;
            new CustomDialog.NormalBuilder(mContext)
                    .setMessage(getString(isFinish ? R.string.dialog_sure_to_finish : R.string.dialog_sure_to_start))
                    .setPositiveButton((dialog, which) -> changeStatus(jobEntity, isFinish))
                    .show();
        }
    }

    private void changeStatus(JobEntity jobEntity, boolean isFinish) {
        jobEntity.setInOperation(true);
        mLvAdapter.notifyDataSetChanged();
        int opeType = isFinish ? OpeType.COMPLETE : OpeType.BEGIN;
        ((TaskBiz) mPresenter).changeJobStatus(mContext, jobEntity, opeType, new StateListener() {
            @Override
            public void onSuccess() {
                jobEntity.setInOperation(false);
                mLvAdapter.notifyDataSetChanged();
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
                filterData(true);
                scrollToItem(jobEntity);
            }

            @Override
            public void onFailed() {
                jobEntity.setInOperation(false);
                mLvAdapter.notifyDataSetChanged();
                showToast(R.string.toast_change_state_failed);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JobEntity jobEntity = listData.get(position);
        Intent intent = new Intent(mContext, OperatorTaskDetailActivity.class);
        intent.putExtra(JOB_ENTITY, jobEntity);
        startActivity(intent);
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
            for (JobEntity task : getSameStatusList(statusPosition)) {
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
    public void getMyTaskDataSuccess(List<JobEntity> data) {
        getDataState = GET_SUCCESS;
        swipeRefresh.setRefreshing(false);
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
        filterData(true);
    }

    @Override
    public void getMyTaskDataFailed() {
        getDataState = GET_FAILED;
        swipeRefresh.setRefreshing(false);
        tvHint.setText(getString(R.string.hint_load_failed_click_retry));
        showView(flContainer, tvHint);
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
    private void scrollToItem(JobEntity jobEntity) {
        int positionInList = listData.indexOf(jobEntity);
        if (positionInList != -1) lvList.setSelection(positionInList);
    }

    // 二级界面(OperatorTaskDetailActivity)更改了数据，同步该界面数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JobEntity afterChangeEntity) {
        int positionInList = BasicBiz.halfSearch(allData, afterChangeEntity);
        if (positionInList != -1) {
            JobEntity jobEntity = allData.get(positionInList);
            boolean isFinish = jobEntity.getWorkstatus() == TaskState.RUN;
            if (isFinish) {
                jobEntity.setWorkstatus(TaskState.DONE);
                jobEntity.setRealendtime(afterChangeEntity.getRealendtime());
                underwayData.remove(jobEntity);
                doneData.add(jobEntity);
            } else {
                jobEntity.setWorkstatus(TaskState.RUN);
                jobEntity.setRealstarttime(afterChangeEntity.getRealstarttime());
                unStartedData.remove(jobEntity);
                underwayData.add(jobEntity);
            }
            filterData(true);
            scrollToItem(jobEntity);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.dispose();
        EventBus.getDefault().unregister(this);
        mPresenter.exit();
    }
}
