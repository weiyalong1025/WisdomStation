package com.winsion.wisdomstation.grid.fragment.problemmanage;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseFragment;
import com.winsion.wisdomstation.data.constants.OpeType;
import com.winsion.wisdomstation.grid.adapter.ProblemAdapter;
import com.winsion.wisdomstation.operation.constants.TaskState;
import com.winsion.wisdomstation.operation.entity.TaskEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wyl on 2017/6/29
 */
public class ProblemManageFragment extends BaseFragment implements ProblemManageContract.View, AdapterView.OnItemClickListener {
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
    private ProblemAdapter mLvAdapter;

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
        mLvAdapter = new ProblemAdapter(mContext, listData);
        lvList.setAdapter(mLvAdapter);
    }

    private void initListener() {
        swipeRefresh.setColorSchemeResources(R.color.blue1);
        swipeRefresh.setOnRefreshListener(this::initData);
        lvList.setOnItemClickListener(this);
        mLvAdapter.setOnPassClickListener(taskEntity -> mPresenter.confirm(taskEntity, OpeType.CONFIRMED));
        mLvAdapter.setOnNotPassClickListener(taskEntity -> mPresenter.confirm(taskEntity, OpeType.UN_PASS));
    }

    private void initData() {
        mPresenter.getData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*Intent intent = new Intent(mContext, RecordListActivity.class);
        intent.putExtra("taskId", listData.get(position).getTasksid());
        startActivity(intent);*/
    }

    @Override
    public void getDataSuccess(List<TaskEntity> dataList) {
        swipeRefresh.setRefreshing(false);
        listData.clear();
        for (TaskEntity problemDto : dataList) {
            if (problemDto.getTaskstatus() != TaskState.GRID_CONFIRMED) {
                listData.add(problemDto);
            }
        }
        if (listData.size() == 0) {
            tvHint.setText(R.string.no_data_click_to_retry);
            showView(flContainer, tvHint);
            return;
        }
        mLvAdapter.notifyDataSetChanged();
        showView(flContainer, swipeRefresh);
    }

    @Override
    public void getDataFailed(String errorInfo) {
        swipeRefresh.setRefreshing(false);
        tvHint.setText(R.string.failure_load_click_retry);
        showView(flContainer, tvHint);
    }

    @Override
    public void confirmSuccess() {
        swipeRefresh.setRefreshing(true);
        initData();
    }

    @Override
    public void confirmFailed(String errorInfo) {
        showToast("确认失败:" + errorInfo);
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
