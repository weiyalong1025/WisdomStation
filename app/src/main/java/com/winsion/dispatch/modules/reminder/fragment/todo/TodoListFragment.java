package com.winsion.dispatch.modules.reminder.fragment.todo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;

import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseFragment;
import com.winsion.dispatch.main.activity.MainActivity;
import com.winsion.dispatch.modules.reminder.ReminderRootFragment;
import com.winsion.dispatch.modules.reminder.activity.todo.AddTodoActivity;
import com.winsion.dispatch.modules.reminder.adapter.TodoAdapter;
import com.winsion.dispatch.modules.reminder.entity.TodoEntity;
import com.winsion.dispatch.modules.reminder.event.UpdateTodoEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.winsion.dispatch.modules.reminder.constants.Intents.Todo.TODO_ID;

/**
 * Created by wyl on 2017/6/2
 */
public class TodoListFragment extends BaseFragment implements TodoListContract.View, AdapterView.OnItemClickListener {
    @BindView(R.id.lv_reminders_list)
    ListView lvRemindersList;
    @BindView(R.id.btn_unfinished)
    RadioButton rbUnFinished;

    public static final int REQUEST_CODE = 911;

    private boolean mIsFinish;  // 显示未完成/已完成的数据
    private List<TodoEntity> listData = new ArrayList<>();
    private TodoAdapter mAdapter;
    private TodoListContract.Presenter mPresenter;

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_todo, null);
    }

    @Override
    protected void init() {
        initPresenter();
        initAdapter();
        initListener();
        recoverAlarm();
        initData(true);
    }

    private void initPresenter() {
        mPresenter = new TodoListPresenter(this);
        mPresenter.start();
    }

    private void initAdapter() {
        mAdapter = new TodoAdapter(mContext, listData);
        lvRemindersList.setAdapter(mAdapter);
        rbUnFinished.setChecked(true);
    }

    private void initListener() {
        EventBus.getDefault().register(this);
        mAdapter.setDeleteBtnClickListener(todoEntity -> new AlertDialog.Builder(mContext)
                .setMessage(getString(R.string.dialog_sure_to_delete))
                .setNegativeButton(getString(R.string.btn_cancel), (DialogInterface dialog, int which) -> dialog.cancel())
                .setPositiveButton(getString(R.string.btn_confirm), (DialogInterface dialog, int which) -> mPresenter.deleteTodo(todoEntity))
                .show());
        lvRemindersList.setOnItemClickListener(this);
    }

    private void initData(boolean isUpdateBadge) {
        List<TodoEntity> toDoBeen = mPresenter.queryTodo(mIsFinish);
        listData.clear();
        listData.addAll(toDoBeen);
        mAdapter.notifyDataSetChanged();

        if (isUpdateBadge) {
            // 更新角标
            int unreadCount = 0;
            for (TodoEntity todoEntity : mPresenter.queryTodo(false)) {
                if (todoEntity.getPlanDate() < System.currentTimeMillis()) {
                    unreadCount++;
                }
            }
            ReminderRootFragment parentFragment = (ReminderRootFragment) getParentFragment();
            parentFragment.getBrbView(1).showNumber(unreadCount);
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.notifyUnreadTodoCountChanged(unreadCount);
        }
    }

    /**
     * 避免退出程序后之前设置的闹钟不会提醒，需要将之前的提醒进行重新设置
     */
    private void recoverAlarm() {
        mPresenter.recoverAlarm();
    }

    @OnClick({R.id.btn_unfinished, R.id.btn_finished, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_unfinished:
                mIsFinish = false;
                initData(false);
                break;
            case R.id.btn_finished:
                mIsFinish = true;
                initData(false);
                break;
            case R.id.btn_add:
                startActivityForResult(AddTodoActivity.class, REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mIsFinish) {
            Intent intent = new Intent(mContext, AddTodoActivity.class);
            intent.putExtra(TODO_ID, listData.get(position).getId());
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    /**
     * 该事件由TodoReceiver发出
     *
     * @param event 提醒置为已读状态，刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateTodoEvent event) {
        initData(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            initData(true);
        }
    }

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
