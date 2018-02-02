package com.winsion.dispatch.modules.reminder.fragment.todo;

import com.winsion.dispatch.base.BasePresenter;
import com.winsion.dispatch.base.BaseView;
import com.winsion.dispatch.modules.reminder.entity.TodoEntity;

import java.util.List;

/**
 * Created by wyl on 2017/6/2
 */
class TodoListContract {
    interface View extends BaseView {
        void notifyLocalDataChange();
    }

    interface Presenter extends BasePresenter {
        void deleteToDo(TodoEntity todoEntity);

        List<TodoEntity> queryToDo(boolean isFinish);

        void recoverAlarm();

        void exit();
    }
}
