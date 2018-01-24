package com.winsion.wisdomstation.modules.reminder.modules.todo.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.DBDataSource;
import com.winsion.wisdomstation.modules.reminder.constants.ExtraName;
import com.winsion.wisdomstation.modules.reminder.entity.TodoEntity;
import com.winsion.wisdomstation.modules.reminder.modules.todo.receiver.TodoReceiver;

import java.util.List;

/**
 * Created by wyl on 2017/6/2
 */
public class TodoListPresenter implements TodoListContract.Presenter {
    private TodoListContract.View mToDoView;
    private Context mContext;

    private AlarmManager alarmManager;

    TodoListPresenter(TodoListContract.View toDoView) {
        this.mToDoView = toDoView;
        this.mContext = mToDoView.getContext();
    }

    @Override
    public void start() {
        // 获取闹钟服务
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void deleteToDo(TodoEntity todoEntity) {
        if (!todoEntity.getFinished()) {
            Intent intent = new Intent(mContext, TodoReceiver.class);
            intent.putExtra(ExtraName.NAME_TODO_ID, todoEntity.getId());
            long requestCode = todoEntity.getPlanDate();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, (int) requestCode,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
        DBDataSource.getInstance().deleteOneTodo(todoEntity);
        mToDoView.notifyLocalDataChange();
    }

    @Override
    public List<TodoEntity> queryToDo(boolean isFinish) {
        return DBDataSource.getInstance().queryTodoByStatus(isFinish, CacheDataSource.getUserId());
    }

    /**
     * 第一次加载程序恢复设置闹钟提醒
     */
    @Override
    public void recoverAlarm() {
        for (TodoEntity todoEntity : queryToDo(false)) {
            setAlarm(todoEntity);
        }
    }

    /**
     * 开启提醒
     */
    private void setAlarm(TodoEntity todoEntity) {
        Intent intent = new Intent(mContext, TodoReceiver.class);
        intent.putExtra(ExtraName.NAME_TODO_ID, todoEntity.getId());
        long requestCode = todoEntity.getPlanDate();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, (int) requestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // API19之前set设置闹钟会精准提醒，API19之后需要用setExact
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, requestCode, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, requestCode, pendingIntent);
        }
    }

    @Override
    public void exit() {

    }
}
