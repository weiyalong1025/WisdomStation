package com.winsion.component.task.fragment.problemmanage;

import android.annotation.SuppressLint;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.constants.OpeType;
import com.winsion.component.basic.view.CustomDialog;
import com.winsion.component.task.R;
import com.winsion.component.task.adapter.ProblemManageAdapter;
import com.winsion.component.task.constants.TaskState;
import com.winsion.component.task.entity.TaskEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyl on 2017/6/29
 */
public class ProblemManageFragment extends BaseFragment implements ProblemManageContract.View,
        AdapterView.OnItemClickListener, ProblemManageAdapter.ConfirmButtonListener {
    private ListView lvList;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvHint;
    private FrameLayout flContainer;

    private ProblemManageContract.Presenter mPresenter;
    private List<TaskEntity> listData = new ArrayList<>();
    private ProblemManageAdapter mLvAdapter;

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.task_fragment_problem_manage, null);
    }

    @Override
    protected void init() {
        initPresenter();
        initView();
        initAdapter();
        initListener();
        initData();
    }

    private void initPresenter() {
        mPresenter = new ProblemManagePresenter(this);
    }

    private void initView() {
        lvList = findViewById(R.id.lv_list);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progress_bar);
        tvHint = findViewById(R.id.tv_hint);
        flContainer = findViewById(R.id.fl_container);

        swipeRefresh.setColorSchemeResources(R.color.basic_blue1);
    }

    private void initAdapter() {
        mLvAdapter = new ProblemManageAdapter(mContext, listData);
        lvList.setAdapter(mLvAdapter);
    }

    private void initListener() {
        swipeRefresh.setOnRefreshListener(this::initData);
        lvList.setOnItemClickListener(this);
        mLvAdapter.setConfirmButtonListener(this);
        addOnClickListeners(R.id.tv_hint);
    }

    private void initData() {
        mPresenter.getProblemData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*Intent intent = new Intent(mContext, RecordListActivity.class);
        intent.putExtra("taskId", listData.get(position).getTasksid());
        startActivity(intent);*/
    }

    @Override
    public void getProblemDataSuccess(List<TaskEntity> dataList) {
        swipeRefresh.setRefreshing(false);
        listData.clear();
        for (TaskEntity problemDto : dataList) {
            if (problemDto.getTaskstatus() != TaskState.GRID_CONFIRMED) {
                listData.add(problemDto);
            }
        }
        if (listData.size() == 0) {
            tvHint.setText(R.string.hint_no_data_click_retry);
            showView(flContainer, tvHint);
            return;
        }
        mLvAdapter.notifyDataSetChanged();
        showView(flContainer, swipeRefresh);
    }

    @Override
    public void getProblemDataFailed(String errorInfo) {
        swipeRefresh.setRefreshing(false);
        tvHint.setText(R.string.hint_load_failed_click_retry);
        showView(flContainer, tvHint);
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
    public void onClick(View view) {
        showView(flContainer, progressBar);
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}
