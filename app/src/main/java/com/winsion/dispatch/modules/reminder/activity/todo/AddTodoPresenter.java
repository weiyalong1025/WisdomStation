package com.winsion.dispatch.modules.reminder.activity.todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.DBDataSource;
import com.winsion.component.basic.entity.TodoEntity;
import com.winsion.component.basic.utils.ConvertUtils;
import com.winsion.component.basic.utils.constants.Formatter;
import com.winsion.dispatch.modules.reminder.receiver.todo.TodoReceiver;

import static com.winsion.dispatch.modules.reminder.constants.Intents.Todo.TODO_ID;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 2:51
 */

public class AddTodoPresenter implements AddTodoContract.Presenter {
    private final AddTodoContract.View mView;
    private final Context mContext;
    private final DBDataSource mDBDataSource;

    private AlarmManager alarmManager;

    AddTodoPresenter(AddTodoContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
        this.mDBDataSource = DBDataSource.getInstance(mContext.getApplicationContext());
    }

    @Override
    public void start() {
        // 获取闹钟服务
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public TodoEntity getTodoById(long todoId) {
        return mDBDataSource.getTodoEntityById(todoId);
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
        mDBDataSource.updateOrAddTodo(todoEntity);
        setAlarm(todoEntity);
        mView.updateOrAddSuccess();
    }

    @Override
    public void updateTodo(String desc, String date, String time, long todoId) {
        TodoEntity todoEntity = mDBDataSource.getTodoEntityById(todoId);
        // 取消之前的闹钟
        PendingIntent pendingIntent = getPendingIntent(todoEntity);
        alarmManager.cancel(pendingIntent);

        // 更新数据库中的数据
        String planDate = date + " " + time;
        long planDateMillis = ConvertUtils.parseDate(planDate, Formatter.DATE_FORMAT3);
        todoEntity.setContent(desc);
        todoEntity.setPlanDate(planDateMillis);
        mDBDataSource.updateOrAddTodo(todoEntity);

        // 设定新的闹钟
        setAlarm(todoEntity);

        // 回调更新成功状态
        mView.updateOrAddSuccess();
    }

    /**
     * 开启提醒
     */
    private void setAlarm(TodoEntity todoEntity) {
        PendingIntent pendingIntent = getPendingIntent(todoEntity);
        // API19之前set设置闹钟会精准提醒，API19之后需要用setExact
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, todoEntity.getPlanDate(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, todoEntity.getPlanDate(), pendingIntent);
        }
    }

    private PendingIntent getPendingIntent(TodoEntity todoEntity) {
        Intent intent = new Intent(mContext, TodoReceiver.class);
        intent.putExtra(TODO_ID, todoEntity.getId());
        long planDate = todoEntity.getPlanDate();
        return PendingIntent.getBroadcast(mContext, (int) planDate, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void exit() {

    }
}
