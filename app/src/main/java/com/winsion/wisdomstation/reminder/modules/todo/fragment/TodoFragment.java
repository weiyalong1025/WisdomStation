package com.winsion.wisdomstation.reminder.modules.todo.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseFragment;
import com.winsion.wisdomstation.reminder.adapter.TodoAdapter;
import com.winsion.wisdomstation.reminder.constants.ExtraName;
import com.winsion.wisdomstation.reminder.entity.TodoEntity;
import com.winsion.wisdomstation.reminder.event.UpdateTodoEvent;
import com.winsion.wisdomstation.reminder.modules.todo.activity.TodoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wyl on 2017/6/2
 */
public class TodoFragment extends BaseFragment implements TodoContract.View, AdapterView.OnItemClickListener {
    @BindView(R.id.lv_reminders_list)
    ListView lvRemindersList;
    @BindView(R.id.btn_unfinished)
    RadioButton rbUnFinished;

    public static final int REQUEST_CODE = 911;

    // 显示未完成/已完成的数据
    private boolean mIsFinish;
    private List<TodoEntity> listData = new ArrayList<>();
    private TodoAdapter mAdapter;
    private TodoContract.Presenter mPresenter;

    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_todo, null);
    }

    @Override
    protected void init() {
        initPresenter();
        initAdapter();
        initListener();
        initData();
        rbUnFinished.setChecked(true);
    }

    private void initPresenter() {
        mPresenter = new TodoPresenter(this);
        mPresenter.start();
    }

    private void initAdapter() {
        mAdapter = new TodoAdapter(mContext, listData);
        lvRemindersList.setAdapter(mAdapter);
    }

    private void initListener() {
        EventBus.getDefault().register(this);
        mAdapter.setOnButtonClickListener(todoEntity -> mPresenter.deleteToDo(todoEntity));
        lvRemindersList.setOnItemClickListener(this);
    }

    private void initData() {
        List<TodoEntity> toDoBeen = mPresenter.queryToDo(mIsFinish);
        listData.clear();
        listData.addAll(toDoBeen);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.btn_unfinished, R.id.btn_finished, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_unfinished:
                mIsFinish = false;
                initData();
                break;
            case R.id.btn_finished:
                mIsFinish = true;
                initData();
                break;
            case R.id.btn_add:
                Intent intent = new Intent(mContext, TodoActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mIsFinish) {
            Intent intent = new Intent(mContext, TodoActivity.class);
            intent.putExtra(ExtraName.NAME_TODO_ID, listData.get(position).getId());
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    /**
     * 该事件由{@link com.winsion.wisdomstation.reminder.modules.todo.receiver.TodoReceiver}发出
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateTodoEvent event) {
        initData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            initData();
        }
    }

    @Override
    public void notifyLocalDataChange() {
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
        EventBus.getDefault().unregister(this);
    }
}
