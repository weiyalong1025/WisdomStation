package com.winsion.wisdomstation.reminder.modules.systemremind.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseFragment;
import com.winsion.wisdomstation.reminder.adapter.SystemRemindAdapter;
import com.winsion.wisdomstation.reminder.constants.HandleType;
import com.winsion.wisdomstation.reminder.constants.ReadStatus;
import com.winsion.wisdomstation.reminder.entity.RemindEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 7:20
 */

public class SystemRemindFragment extends BaseFragment implements SystemRemindContract.View, AdapterView.OnItemClickListener {
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

    private List<RemindEntity> listData = new ArrayList<>();
    private SystemRemindContract.Presenter mPresenter;
    private SystemRemindAdapter mLvAdapter;

    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.layout_status, null);
    }

    @Override
    protected void init() {
        initPresenter();
        initAdapter();
        initListener();
        initData();
    }

    private void initPresenter() {
        mPresenter = new SystemRemindPresenter(this);
        mPresenter.start();
    }

    private void initAdapter() {
        mLvAdapter = new SystemRemindAdapter(mContext, listData);
        mLvAdapter.setOnDeleteBtnClickListener(remindEntity -> {
            if (remindEntity.getReaded() == ReadStatus.UNREAD) {
                showToast(R.string.only_read_remind_can_be_deleted);
            } else {
                ArrayList<RemindEntity> list = new ArrayList<>();
                list.add(remindEntity);
                mPresenter.handleReminds(list, HandleType.HANDLE_DELETE);
            }
        });
        lvList.setAdapter(mLvAdapter);
    }

    private void initListener() {
        lvList.setOnItemClickListener(this);
        swipeRefresh.setColorSchemeResources(R.color.blue1);
        swipeRefresh.setOnRefreshListener(this::initData);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RemindEntity remindEntity = listData.get(position);
        if (remindEntity.getReaded() == ReadStatus.UNREAD) {
            ArrayList<RemindEntity> list = new ArrayList<>();
            list.add(remindEntity);
            mPresenter.handleReminds(list, HandleType.HANDLE_READ);
        }
    }

    private void initData() {
        mPresenter.getRemindData();
    }

    @OnClick(R.id.tv_hint)
    public void onViewClicked() {
        showView(flContainer, progressBar);
        initData();
    }

    @Override
    public void getRemindDataSuccess(List<RemindEntity> remindEntities) {
        if (remindEntities.size() != 0) {
            listData.clear();
            listData.addAll(remindEntities);
            mLvAdapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
            showView(flContainer, swipeRefresh);
        } else {
            tvHint.setText(R.string.no_data_click_to_retry);
            showView(flContainer, tvHint);
        }
    }

    @Override
    public void getRemindDataFailed() {
        tvHint.setText(R.string.failure_load_click_retry);
        showView(flContainer, tvHint);
    }

    @Override
    public void handleRemindsSuccess(List<RemindEntity> reminds, int handleType) {
        switch (handleType) {
            case HandleType.HANDLE_READ:
                for (RemindEntity remind : reminds) {
                    remind.setReaded(ReadStatus.READ);
                }
                mLvAdapter.notifyDataSetChanged();
                break;
            case HandleType.HANDLE_DELETE:
                if (listData.containsAll(reminds)) {
                    showToast(R.string.delete_success);
                    listData.removeAll(reminds);
                    mLvAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void handleRemindsFailed(int handleType) {
        switch (handleType) {
            case HandleType.HANDLE_READ:
                showToast(R.string.set_read_status_failed);
                break;
            case HandleType.HANDLE_DELETE:
                showToast(R.string.delete_failed);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}
