package com.winsion.wisdomstation.reminder.modules.systemremind.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseFragment;
import com.winsion.wisdomstation.main.MainActivity;
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

public class SystemRemindFragment extends BaseFragment implements SystemRemindContract.View, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
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
    // 多选删除布局显示状态
    private boolean isMultipleDeleteLayoutDisplaying = false;
    private View multipleDeleteLayout;
    private Button btnMultipleDeleteFooter;
    private Button btnSelectCount;
    private LinearLayout llMultipleDeleteHeader;
    private Button btnAllSelect;

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
        swipeRefresh.setColorSchemeResources(R.color.blue1);
        swipeRefresh.setOnRefreshListener(this::initData);
        lvList.setOnItemClickListener(this);
        lvList.setOnItemLongClickListener(this);
    }

    private void initData() {
        mPresenter.getRemindData();
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isMultipleDeleteLayoutDisplaying) {
            showMultipleDeleteLayout();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isMultipleDeleteLayoutDisplaying) {
            hideMultipleDeleteLayout();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isMultipleDeleteLayoutDisplaying) {
            hideMultipleDeleteLayout();
        }
    }

    /**
     * 显示多选删除布局
     */
    public void showMultipleDeleteLayout() {
        if (multipleDeleteLayout == null) {
            multipleDeleteLayout = getLayoutInflater().inflate(R.layout.layout_multiple_delete, null);
            btnMultipleDeleteFooter = multipleDeleteLayout.findViewById(R.id.btn_multiple_delete_footer);
            btnSelectCount = multipleDeleteLayout.findViewById(R.id.btn_select_count);
            llMultipleDeleteHeader = multipleDeleteLayout.findViewById(R.id.ll_multiple_delete_header);
            btnAllSelect = multipleDeleteLayout.findViewById(R.id.btn_all_select);
            multipleDeleteLayout.findViewById(R.id.btn_cancel).setOnClickListener(v -> hideMultipleDeleteLayout());
            multipleDeleteLayout.findViewById(R.id.btn_multiple_delete_footer).setOnClickListener(v -> {
                showToast("删除点击");
            });
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getActivity().getWindow().addContentView(multipleDeleteLayout, params);
        }
        isMultipleDeleteLayoutDisplaying = true;
        swipeRefresh.setEnabled(false);
        TranslateAnimation translateAnimationFooter;
        TranslateAnimation translateAnimationHeader;
        // 显示多选删除布局
        // 显示脚布局
        translateAnimationFooter = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        translateAnimationFooter.setDuration(250);
        btnMultipleDeleteFooter.startAnimation(translateAnimationFooter);
        btnMultipleDeleteFooter.setVisibility(View.VISIBLE);
        // 显示头布局
        btnSelectCount.setText(getString(R.string.selected_item_zero));
        translateAnimationHeader = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
        translateAnimationHeader.setDuration(250);
        llMultipleDeleteHeader.startAnimation(translateAnimationHeader);
        llMultipleDeleteHeader.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏多选删除布局
     */
    public void hideMultipleDeleteLayout() {
        isMultipleDeleteLayoutDisplaying = false;
        swipeRefresh.setEnabled(true);
        TranslateAnimation translateAnimationFooter;
        TranslateAnimation translateAnimationHeader;
        // 隐藏多选删除布局
        // 隐藏脚布局
        translateAnimationFooter = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        translateAnimationFooter.setDuration(250);
        btnMultipleDeleteFooter.startAnimation(translateAnimationFooter);
        btnMultipleDeleteFooter.setVisibility(View.GONE);
        // 隐藏头布局
        translateAnimationHeader = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
        translateAnimationHeader.setDuration(250);
        llMultipleDeleteHeader.startAnimation(translateAnimationHeader);
        llMultipleDeleteHeader.setVisibility(View.GONE);
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
            updateUnreadCount();
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
                updateUnreadCount();
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

    /**
     * 更新未读提醒数
     */
    private void updateUnreadCount() {
        int unreadCount = 0;
        for (RemindEntity remindEntity : listData) {
            if (remindEntity.getReaded() == ReadStatus.UNREAD) {
                unreadCount++;
            }
        }
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateUnreadCount(unreadCount);
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
