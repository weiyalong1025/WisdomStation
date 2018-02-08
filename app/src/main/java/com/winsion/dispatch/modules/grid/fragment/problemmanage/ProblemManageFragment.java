package com.winsion.dispatch.modules.grid.fragment.problemmanage;

import android.annotation.SuppressLint;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseFragment;
import com.winsion.dispatch.data.constants.OpeType;
import com.winsion.dispatch.modules.grid.adapter.ProblemManageAdapter;
import com.winsion.dispatch.modules.operation.constants.TaskState;
import com.winsion.dispatch.modules.operation.entity.TaskEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wyl on 2017/6/29
 */
public class ProblemManageFragment extends BaseFragment implements ProblemManageContract.View,
        AdapterView.OnItemClickListener, ProblemManageAdapter.ConfirmButtonListener {
    @BindView(R.id.lv_list)
    ListView lvList;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;

    private ProblemManageContract.Presenter mPresenter;
    private List<TaskEntity> listData = new ArrayList<>();
    private ProblemManageAdapter mLvAdapter;

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_problem_manage, null);
    }

    @Override
    protected void init() {
        initPresenter();
        initAdapter();
        initListener();
        initData();
    }

    private void initPresenter() {
        mPresenter = new ProblemManagePresenter(this);
    }

    private void initAdapter() {
        mLvAdapter = new ProblemManageAdapter(mContext, listData);
        lvList.setAdapter(mLvAdapter);
    }

    private void initListener() {
        swipeRefresh.setColorSchemeResources(R.color.blue1);
        swipeRefresh.setOnRefreshListener(this::initData);
        lvList.setOnItemClickListener(this);
        mLvAdapter.setConfirmButtonListener(this);
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
        mPresenter.confirm(taskEntity, OpeType.PASS);
    }

    @Override
    public void onNotPassButtonClick(TaskEntity taskEntity) {
        mPresenter.confirm(taskEntity, OpeType.NOT_PASS);
    }

    @Override
    public void confirmSuccess() {

    }

    @Override
    public void confirmFailed(String errorInfo) {
        showToast(R.string.toast_confirm_failed);
    }

    @OnClick(R.id.tv_hint)
    public void onViewClicked() {
        showView(flContainer, progressBar);
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}
