package com.winsion.wisdomstation.reminder.fragment.todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.DBDataSource;
import com.winsion.wisdomstation.reminder.constants.ExtraName;
import com.winsion.wisdomstation.reminder.entity.TodoEntity;
import com.winsion.wisdomstation.reminder.receiver.TodoReceiver;

import java.util.List;

/**
 * Created by wyl on 2017/6/2
 */
public class TodoPresenter implements TodoContract.Presenter {
    private TodoContract.View mToDoView;
    private Context mContext;

    private AlarmManager alarmManager;

    public TodoPresenter(TodoContract.View toDoView) {
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

    @Override
    public void exit() {

    }
}
