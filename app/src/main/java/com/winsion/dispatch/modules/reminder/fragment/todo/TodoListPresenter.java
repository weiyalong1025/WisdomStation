package com.winsion.dispatch.modules.reminder.fragment.todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.DBDataSource;
import com.winsion.component.basic.entity.TodoEntity;
import com.winsion.dispatch.modules.reminder.receiver.todo.TodoReceiver;

import java.util.List;

import static com.winsion.dispatch.modules.reminder.constants.Intents.Todo.TODO_ID;

/**
 * Created by wyl on 2017/6/2
 */
public class TodoListPresenter implements TodoListContract.Presenter {
    private TodoListContract.View mView;
    private Context mContext;

    private AlarmManager alarmManager;

    TodoListPresenter(TodoListContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {
        // 获取闹钟服务
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void deleteTodo(TodoEntity todoEntity) {
        if (!todoEntity.getFinished()) {
            Intent intent = new Intent(mContext, TodoReceiver.class);
            intent.putExtra(TODO_ID, todoEntity.getId());
            long requestCode = todoEntity.getPlanDate();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, (int) requestCode,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
        DBDataSource.getInstance(mContext).deleteOneTodo(todoEntity);
        mView.notifyLocalDataChange();
    }

    @Override
    public List<TodoEntity> queryTodo(boolean isFinish) {
        return DBDataSource.getInstance(mContext).queryTodoByStatus(isFinish, CacheDataSource.getUserId());
    }

    /**
     * 第一次加载程序恢复设置闹钟提醒
     */
    @Override
    public void recoverAlarm() {
        for (TodoEntity todoEntity : queryTodo(false)) {
            // 已经过了时间的不恢复
            if (todoEntity.getPlanDate() >= System.currentTimeMillis()) {
                setAlarm(todoEntity);
            }
        }
    }

    /**
     * 开启提醒
     */
    private void setAlarm(TodoEntity todoEntity) {
        Intent intent = new Intent(mContext, TodoReceiver.class);
        intent.putExtra(TODO_ID, todoEntity.getId());
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
