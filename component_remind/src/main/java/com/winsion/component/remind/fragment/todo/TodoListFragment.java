package com.winsion.component.remind.fragment.todo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.billy.cc.core.component.CC;
import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.entity.TodoEntity;
import com.winsion.component.basic.view.CustomDialog;
import com.winsion.component.basic.view.SpinnerView;
import com.winsion.component.remind.R;
import com.winsion.component.remind.activity.todo.AddTodoActivity;
import com.winsion.component.remind.adapter.TodoAdapter;
import com.winsion.component.remind.fragment.RemindRootFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.winsion.component.remind.constants.Intents.Todo.TODO_ID;

/**
 * Created by wyl on 2017/6/2
 */
public class TodoListFragment extends BaseFragment implements TodoListContract.View, AdapterView.OnItemClickListener {
    private SpinnerView svSpinner;
    private ListView lvRemindsList;
    private ImageView ivShade;
    private TextView tvHint;

    /**
     * 状态筛选-全部
     */
    private static final int STATE_ALL = 0;
    /**
     * 状态筛选-未完成
     */
    private static final int STATE_UNFINISHED = 1;
    /**
     * 状态筛选-已完成
     */
    private static final int STATE_FINISHED = 2;

    private List<TodoEntity> listData = new ArrayList<>();
    private TodoAdapter mAdapter;
    private TodoListContract.Presenter mPresenter;
    private int currentState = STATE_ALL;   // 当前筛选状态，默认全部

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.remind_fragment_todo, null);
    }

    @Override
    protected void init() {
        initPresenter();
        initView();
        initSpinner();
        initAdapter();
        initListener();
        recoverAlarm();
        initData(true);
    }

    private void initPresenter() {
        mPresenter = new TodoListPresenter(this);
        mPresenter.start();
    }

    private void initView() {
        svSpinner = findViewById(R.id.sv_spinner);
        lvRemindsList = findViewById(R.id.lv_reminds_list);
        ivShade = findViewById(R.id.iv_shade);
        tvHint = findViewById(R.id.tv_hint);
    }

    private void initSpinner() {
        List<String> statusList = Arrays.asList(getResources().getStringArray(R.array.todoStatusArray));
        svSpinner.setFirstOptionData(statusList);
        svSpinner.setFirstOptionItemClickListener(position -> {
            currentState = position;
            initData(false);
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
    }

    private void initAdapter() {
        mAdapter = new TodoAdapter(mContext, listData);
        lvRemindsList.setAdapter(mAdapter);
    }

    private void initListener() {
        EventBus.getDefault().register(this);
        mAdapter.setDeleteBtnClickListener(todoEntity -> new CustomDialog.NormalBuilder(mContext)
                .setMessage(R.string.dialog_sure_to_delete)
                .setPositiveButton((dialog, which) -> mPresenter.deleteTodo(todoEntity))
                .show());
        lvRemindsList.setOnItemClickListener(this);
        addOnClickListeners(R.id.btn_add, R.id.tv_hint);
    }

    /**
     * 获取代办事项数据
     *
     * @param isUpdateBadge 是否需要更新角标
     */
    private void initData(boolean isUpdateBadge) {
        listData.clear();
        switch (currentState) {
            case STATE_ALL:
                listData.addAll(mPresenter.queryTodo(false));
                listData.addAll(mPresenter.queryTodo(true));
                break;
            case STATE_UNFINISHED:
                listData.addAll(mPresenter.queryTodo(false));
                break;
            case STATE_FINISHED:
                listData.addAll(mPresenter.queryTodo(true));
                break;
        }
        mAdapter.notifyDataSetChanged();

        if (listData.size() == 0) {
            tvHint.setVisibility(View.VISIBLE);
            lvRemindsList.setVisibility(View.GONE);
        } else {
            tvHint.setVisibility(View.GONE);
            lvRemindsList.setVisibility(View.VISIBLE);
        }

        if (isUpdateBadge) {
            // 更新角标
            int unreadCount = 0;
            for (TodoEntity todoEntity : mPresenter.queryTodo(false)) {
                if (todoEntity.getPlanDate() < System.currentTimeMillis()) {
                    unreadCount++;
                }
            }
            RemindRootFragment parentFragment = (RemindRootFragment) getParentFragment();
            if (parentFragment != null) {
                parentFragment.getBrbView(1).showNumber(unreadCount);
            }
            FragmentActivity activity = getActivity();
            CC.obtainBuilder("ComponentApp")
                    .setActionName("notifyUnreadSysRemindCountChanged")
                    .addParam("activity", activity)
                    .addParam("unreadCount", unreadCount)
                    .build()
                    .call();
        }
    }

    /**
     * 避免退出程序后之前设置的闹钟不会提醒，需要将之前的提醒进行重新设置
     */
    private void recoverAlarm() {
        mPresenter.recoverAlarm();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!listData.get(position).getFinished()) {
            Intent intent = new Intent(mContext, AddTodoActivity.class);
            intent.putExtra(TODO_ID, listData.get(position).getId());
            startActivityForResult(intent);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_add) {
            startActivityForResult(AddTodoActivity.class);
        } else if (id == R.id.tv_hint) {
            initData(false);
        }
    }

    /**
     * 已读了一条代办事项，该事件由TodoReceiver发出
     *
     * @param event 提醒置为已读状态，刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TodoEntity event) {
        initData(true);
    }

    /**
     * 更新了一条代办事项
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            initData(false);
        }
    }

    /**
     * 删除了一条代办事项
     */
    @Override
    public void notifyLocalDataChange() {
        initData(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
        EventBus.getDefault().unregister(this);
    }
}
