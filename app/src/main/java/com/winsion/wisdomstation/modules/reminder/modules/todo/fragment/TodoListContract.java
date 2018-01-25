package com.winsion.wisdomstation.modules.reminder.modules.todo.fragment;

import com.winsion.wisdomstation.base.BasePresenter;
import com.winsion.wisdomstation.base.BaseView;
import com.winsion.wisdomstation.modules.reminder.entity.TodoEntity;

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