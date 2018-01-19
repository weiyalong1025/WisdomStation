package com.winsion.wisdomstation.modules.reminder.modules.systemremind.fragment;

import android.app.AlertDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseFragment;
import com.winsion.wisdomstation.main.activity.MainActivity;
import com.winsion.wisdomstation.modules.reminder.ReminderRootFragment;
import com.winsion.wisdomstation.modules.reminder.adapter.SystemRemindAdapter;
import com.winsion.wisdomstation.modules.reminder.constants.HandleType;
import com.winsion.wisdomstation.modules.reminder.constants.ReadStatus;
import com.winsion.wisdomstation.modules.reminder.entity.RemindEntity;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 7:20
 */

public class SystemRemindFragment extends BaseFragment implements SystemRemindContract.View,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
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
    private boolean isMultipleDeleteLayoutDisplaying;
    // 是否是全选
    private boolean isSelectAll;

    private View multipleDeleteLayout;
    private Button btnMultipleDeleteFooter;
    private Button btnSelectCount;
    private FrameLayout flMultipleDeleteHeader;
    private Button btnSelectAll;

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
        mLvAdapter.setOnSelectChangeListener(selectSize -> btnSelectCount.setText(getSelectCountHint(selectSize)));
        lvList.setAdapter(mLvAdapter);
    }

    // 获取已选项提示信息
    private String getSelectCountHint(int selectSize) {
        return String.format("%s%s%s", getString(R.string.selected), selectSize, getString(R.string.item));
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
        if (isMultipleDeleteLayoutDisplaying) {
            if (remindEntity.getReaded() == ReadStatus.UNREAD) {
                showToast(R.string.only_read_remind_can_be_selected);
            } else {
                mLvAdapter.selectOneItem((ViewHolder) view.getTag(), position);
            }
        } else if (remindEntity.getReaded() == ReadStatus.UNREAD) {
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
            initMultipleDeleteLayout();
        }
        isMultipleDeleteLayoutDisplaying = true;
        swipeRefresh.setEnabled(false);

        // 显示脚布局
        TranslateAnimation translateAnimationFooter;
        translateAnimationFooter = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f);
        translateAnimationFooter.setDuration(250);
        btnMultipleDeleteFooter.startAnimation(translateAnimationFooter);
        btnMultipleDeleteFooter.setVisibility(View.VISIBLE);

        // 显示头布局
        btnSelectCount.setText(getSelectCountHint(0));
        btnSelectAll.setText(R.string.select_all);
        TranslateAnimation translateAnimationHeader;
        translateAnimationHeader = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0f);
        translateAnimationHeader.setDuration(250);
        flMultipleDeleteHeader.startAnimation(translateAnimationHeader);
        flMultipleDeleteHeader.setVisibility(View.VISIBLE);

        // 更新ListView显示状态
        mLvAdapter.changeStatus(isMultipleDeleteLayoutDisplaying);
    }

    /**
     * 初始化多选删除布局
     */
    private void initMultipleDeleteLayout() {
        multipleDeleteLayout = getLayoutInflater().inflate(R.layout.layout_multiple_delete, null);
        btnMultipleDeleteFooter = multipleDeleteLayout.findViewById(R.id.btn_multiple_delete_footer);
        btnSelectCount = multipleDeleteLayout.findViewById(R.id.btn_select_count);
        flMultipleDeleteHeader = multipleDeleteLayout.findViewById(R.id.fl_multiple_delete_header);
        btnSelectAll = multipleDeleteLayout.findViewById(R.id.btn_select_all);
        // 全选按钮点击事件
        btnSelectAll.setOnClickListener(v -> {
            if (isSelectAll) {
                btnSelectAll.setText(R.string.select_all);
            } else {
                btnSelectAll.setText(R.string.select_none);
            }
            isSelectAll = !isSelectAll;
            mLvAdapter.selectAll(isSelectAll);
        });
        // 取消按钮点击事件
        multipleDeleteLayout.findViewById(R.id.btn_cancel).setOnClickListener(v -> hideMultipleDeleteLayout());
        // 删除按钮点击事件
        multipleDeleteLayout.findViewById(R.id.btn_multiple_delete_footer).setOnClickListener(v -> deleteSelectData());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getActivity().getWindow().addContentView(multipleDeleteLayout, params);
    }

    private void deleteSelectData() {
        List<RemindEntity> selectData = mLvAdapter.getSelectData();
        if (selectData.size() == 0) {
            showToast(getString(R.string.currently_no_selected_item));
        } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage(getConfirmDeleteHint(selectData.size()))
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel())
                    .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        mPresenter.handleReminds(selectData, HandleType.HANDLE_DELETE);
                        dialog.cancel();
                    })
                    .create()
                    .show();
        }
    }

    // 获取确认删除提示信息
    private String getConfirmDeleteHint(int selectSize) {
        return String.format("%s%s%s", getString(R.string.sure_to_delete), selectSize, getString(R.string.items));
    }

    /**
     * 隐藏多选删除布局
     */
    public void hideMultipleDeleteLayout() {
        isMultipleDeleteLayoutDisplaying = false;
        swipeRefresh.setEnabled(true);

        // 隐藏脚布局
        TranslateAnimation translateAnimationFooter;
        translateAnimationFooter = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f);
        translateAnimationFooter.setDuration(250);
        btnMultipleDeleteFooter.startAnimation(translateAnimationFooter);
        btnMultipleDeleteFooter.setVisibility(View.GONE);

        // 隐藏头布局
        TranslateAnimation translateAnimationHeader;
        translateAnimationHeader = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1f);
        translateAnimationHeader.setDuration(250);
        flMultipleDeleteHeader.startAnimation(translateAnimationHeader);
        flMultipleDeleteHeader.setVisibility(View.GONE);

        // 更新ListView显示状态
        mLvAdapter.changeStatus(isMultipleDeleteLayoutDisplaying);
        // 重置全选状态
        isSelectAll = false;
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
                    if (isMultipleDeleteLayoutDisplaying) {
                        hideMultipleDeleteLayout();
                    } else {
                        mLvAdapter.notifyDataSetChanged();
                    }
                    if (listData.size() == 0) {
                        tvHint.setText(R.string.no_data_click_to_retry);
                        showView(flContainer, tvHint);
                    }
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
        ReminderRootFragment parentFragment = (ReminderRootFragment) getParentFragment();
        parentFragment.getBrbView(2).showNumber(unreadCount);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.notifyUnreadSysRemindCountChanged(unreadCount);
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
