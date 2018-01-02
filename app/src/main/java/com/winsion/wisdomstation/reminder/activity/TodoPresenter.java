package com.winsion.wisdomstation.reminder.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.DBDataSource;
import com.winsion.wisdomstation.reminder.constants.ExtraName;
import com.winsion.wisdomstation.reminder.entity.TodoEntity;
import com.winsion.wisdomstation.reminder.receiver.TodoReceiver;
import com.winsion.wisdomstation.utils.ConvertUtils;
import com.winsion.wisdomstation.utils.constants.Formatter;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 2:51
 */

public class TodoPresenter implements TodoContract.Presenter {
    private TodoContract.View mView;
    private Context mContext;

    private AlarmManager alarmManager;

    public TodoPresenter(TodoContract.View view) {
        this.mView = view;
        this.mContext = mView.getContext();
    }

    @Override
    public void start() {
        // 获取闹钟服务
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public TodoEntity getTodoById(long todoId) {
        return DBDataSource.getInstance().getTodoById(todoId);
    }

    @Override
    public void addTodo(String desc, String date, String time) {
        String planDate = date + " " + time;
        long planDateMillis = ConvertUtils.parseDate(planDate, Formatter.DATE_FORMAT3);
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setContent(desc);
        todoEntity.setBelongUserId(CacheDataSource.getUserId());
        todoEntity.setFinished(false);
        todoEntity.setPlanDate(planDateMillis);
        DBDataSource.getInstance().updateOrAddTodo(todoEntity);
        setAlarm(todoEntity);
        mView.updateOrAddSuccess();
    }

    @Override
    public void updateTodo(String desc, long todoId) {
        TodoEntity toDoBeanDao = DBDataSource.getInstance().getTodoById(todoId);
        toDoBeanDao.setContent(desc);
        DBDataSource.getInstance().updateOrAddTodo(toDoBeanDao);
        mView.updateOrAddSuccess();
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
