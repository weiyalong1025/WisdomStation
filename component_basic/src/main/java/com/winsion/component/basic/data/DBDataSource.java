package com.winsion.component.basic.data;

import android.content.Context;
import android.text.TextUtils;

import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.constants.MessageType;
import com.winsion.component.basic.entity.TodoEntity;
import com.winsion.component.basic.entity.TodoEntity_;
import com.winsion.component.basic.entity.UserEntity;
import com.winsion.component.basic.entity.UserEntity_;
import com.winsion.component.basic.entity.UserMessage;
import com.winsion.component.basic.entity.UserMessage_;

import java.util.Collections;
import java.util.List;

import io.objectbox.Box;

/**
 * Created by wyl on 2017/3/21.
 * 本地数据库数据
 */

public class DBDataSource {
    private static volatile DBDataSource mInstance;
    private final Box<UserEntity> mUserEntityBox;
    private final Box<TodoEntity> mTodoEntityBox;
    private final Box<UserMessage> mUserMessageBox;

    private DBDataSource(Context context) {
        mUserEntityBox = BasicBiz.getBoxStore(context).boxFor(UserEntity.class);
        mTodoEntityBox = BasicBiz.getBoxStore(context).boxFor(TodoEntity.class);
        mUserMessageBox = BasicBiz.getBoxStore(context).boxFor(UserMessage.class);
    }

    public static DBDataSource getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBDataSource.class) {
                if (mInstance == null) {
                    mInstance = new DBDataSource(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取所有登录过的用户，按照登录时间降序排序
     *
     * @return 用户集合
     */
    public List<UserEntity> getAllSavedUser() {
        return mUserEntityBox
                .query()
                .orderDesc(UserEntity_.lastLoginTime)
                .build()
                .find();
    }

    public UserEntity getUserByUsername(String username) {
        return mUserEntityBox
                .query()
                .equal(UserEntity_.username, username)
                .build()
                .findUnique();
    }

    public UserEntity getLastLoginUser() {
        return mUserEntityBox
                .query()
                .orderDesc(UserEntity_.lastLoginTime)
                .build()
                .findFirst();
    }

    public void deleteUser(UserEntity userEntity) {
        mUserEntityBox.remove(userEntity);
    }

    /**
     * 存储用户信息到数据库
     */
    public void saveUserInfo(UserEntity userEntity) {
        String username = userEntity.getUsername();
        UserEntity user = getUserByUsername(username);
        if (user != null) {
            userEntity.setId(user.getId());
        }
        mUserEntityBox.put(userEntity);
        // 把其他用户的自动登录置为false
        List<UserEntity> allSavedUser = getAllSavedUser();
        for (UserEntity entity : allSavedUser) {
            if (!TextUtils.equals(entity.getUsername(), username)) {
                entity.setIsAutoLogin(false);
                mUserEntityBox.put(entity);
            }
        }
    }

    /**
     * 取消当前用户的自动登录
     */
    public void cancelAutoLogin() {
        UserEntity lastLoginUser = getLastLoginUser();
        if (lastLoginUser != null) {
            lastLoginUser.setIsAutoLogin(false);
            mUserEntityBox.put(lastLoginUser);
        }
    }

    /**
     * 删除一条代办事项
     */
    public void deleteOneTodo(TodoEntity todoEntity) {
        mTodoEntityBox.remove(todoEntity.getId());
    }

    /**
     * 根据完成状态查询代办事项
     *
     * @param finishStatus 完成状态
     * @param userId       当前用户ID
     * @return 符合条件的待办事项集合
     */
    public List<TodoEntity> queryTodoByStatus(boolean finishStatus, String userId) {
        return mTodoEntityBox
                .query()
                .equal(TodoEntity_.finished, finishStatus)
                .and()
                .equal(TodoEntity_.belongUserId, userId)
                .order(TodoEntity_.planDate)
                .build()
                .find();
    }

    public TodoEntity getTodoEntityById(long todoId) {
        return mTodoEntityBox
                .query()
                .equal(TodoEntity_.id, todoId)
                .build()
                .findUnique();
    }

    public void updateOrAddTodo(TodoEntity todoEntity) {
        mTodoEntityBox.put(todoEntity);
    }

    /**
     * 获取单人消息记录
     *
     * @param userId
     * @param chatToUserId
     * @return
     */
    public List<UserMessage> getSingMessage(String userId, String chatToUserId) {
        List<UserMessage> userMessages = mUserMessageBox
                .query()
                .equal(UserMessage_.senderId, userId)
                .and()
                .equal(UserMessage_.receiverId, chatToUserId)
                .and()
                .equal(UserMessage_.belongUserId, userId)
                .and()
                .notEqual(UserMessage_.type, MessageType.DRAFT)
                .build()
                .find();

        userMessages.addAll(mUserMessageBox
                .query()
                .equal(UserMessage_.senderId, chatToUserId)
                .and()
                .equal(UserMessage_.receiverId, userId)
                .and()
                .equal(UserMessage_.belongUserId, userId)
                .and()
                .notEqual(UserMessage_.type, MessageType.DRAFT)
                .build()
                .find());

        Collections.sort(userMessages, (o1, o2) -> (int) (o1.getTime() - o2.getTime()));

        return userMessages;
    }

    /**
     * 获取组消息记录
     *
     * @param userId
     * @param groupId
     * @return
     */
    public List<UserMessage> getGroupMessage(String userId, String groupId) {
        return mUserMessageBox
                .query()
                .equal(UserMessage_.receiverId, groupId)
                .and()
                .equal(UserMessage_.belongUserId, userId)
                .and()
                .notEqual(UserMessage_.type, MessageType.DRAFT)
                .order(UserMessage_.time)
                .build()
                .find();
    }

    public UserMessage getDraft(boolean isGroup, String userId, String chatToUserId) {
        if (isGroup) {
            return mUserMessageBox
                    .query()
                    .equal(UserMessage_.receiverId, chatToUserId)
                    .and()
                    .equal(UserMessage_.belongUserId, userId)
                    .and()
                    .equal(UserMessage_.type, MessageType.DRAFT)
                    .order(UserMessage_.time)
                    .build()
                    .findUnique();
        } else {
            return mUserMessageBox
                    .query()
                    .equal(UserMessage_.senderId, userId)
                    .and()
                    .equal(UserMessage_.receiverId, chatToUserId)
                    .and()
                    .equal(UserMessage_.belongUserId, userId)
                    .and()
                    .equal(UserMessage_.type, MessageType.DRAFT)
                    .build()
                    .findUnique();
        }
    }

    public void saveMessage(UserMessage userMessage) {
        mUserMessageBox.put(userMessage);
    }

    public void deleteMessage(UserMessage userMessage) {
        mUserMessageBox.remove(userMessage);
    }
}
