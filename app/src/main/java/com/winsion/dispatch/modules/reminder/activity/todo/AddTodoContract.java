package com.winsion.dispatch.modules.reminder.activity.todo;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.dispatch.modules.reminder.entity.TodoEntity;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 2:49
 */

class AddTodoContract {
    interface View extends BaseView {
        void updateOrAddSuccess();
    }

    interface Presenter extends BasePresenter {
        TodoEntity getTodoById(long todoId);

        /**
         * 添加一条待办事项
         *
         * @param desc 提醒内容
         * @param date 提醒日期
         * @param time 提醒时间
         */
        void addTodo(String desc, String date, String time);

        /**
         * 更新一条待办事项
         *
         * @param desc   更新后的提醒内容
         * @param date   更新后的提醒日期
         * @param time   更新后的提醒时间
         * @param todoId 更新的待办事项的ID
         */
        void updateTodo(String desc, String date, String time, long todoId);
    }
}
