package com.winsion.dispatch.main.activity;

import android.app.PendingIntent;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.billy.cc.core.component.CC;
import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.mqtt.MQTTClient;
import com.winsion.component.basic.mqtt.constants.MQType;
import com.winsion.component.basic.mqtt.entity.MQMessage;
import com.winsion.component.basic.mqtt.entity.TaskMessage;
import com.winsion.component.basic.utils.ToastUtils;
import com.winsion.dispatch.R;

/**
 * Created by wyl on 2017/12/8
 */
public class MainPresenter implements MainContract.Presenter, MQTTClient.Observer {
    private final MainContract.View mView;
    private final Context mContext;

    MainPresenter(MainContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {
        // 检查更新
        BasicBiz.checkVersionUpdate(mContext, this, false);
        // 监听MQ消息
        MQTTClient.addObserver(this);
    }

    @Override
    public void onMessageArrive(MQMessage msg) {
        switch (msg.getMessageType()) {
            case MQType.USER_LOGIN:
                String data = msg.getData();
                if (data.equals(CacheDataSource.getUserId())) {
                    // 用户在别的设备登录，强制下线
                    ToastUtils.showToast(mContext, R.string.toast_user_login_on_other_device);
                    CC.obtainBuilder("ComponentUser")
                            .setActionName("logout")
                            .build()
                            .callAsync();
                }
                break;
            case MQType.TASK_STATE:
                TaskMessage taskEvent = JSON.parseObject(msg.getData(), TaskMessage.class);
                if (taskEvent.getMonitorteamid().equals(CacheDataSource.getTeamId())) {

                }
                break;
        }
    }

    /**
     * 发送通知
     */
    private void sendNotification(String title, String content, boolean tts, PendingIntent pendingIntent) {
        /*NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
        Integer notificationId = 0;
        switch (dto.getType()) {
            case NotificationType.TYPE_MESSAGE:
                ChatUserDto chatUserDto = (ChatUserDto) intent.getSerializableExtra("ChatUserDto");
                String chatId = chatUserDto.getId();
                notificationId = map.get(chatId);
                if (notificationId == null) {
                    notificationId = ++mRequestCode;
                    map.put(chatId, notificationId);
                }
                break;
            case NotificationType.TYPE_TASK:
                notificationId = map.get(msg);
                if (notificationId == null) {
                    notificationId = ++mRequestCode;
                    map.put(msg, notificationId);
                }
                break;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //Ticker是状态栏显示的提示
        builder.setTicker(msg)
                //第一行内容  通常作为通知栏标题
                .setContentTitle(dto.getContentTitle())
                //第二行内容 通常是通知正文
                .setContentText(msg)
                //可以点击通知栏的删除按钮删除
                .setAutoCancel(true)
                //点击跳转的intent
                .setContentIntent(pIntent)
                //通知默认的声音 震动 呼吸灯
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                //系统状态栏显示的小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                //下拉显示的大图标
                .setLargeIcon(bitmap);*/
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
        MQTTClient.removeObserver(this);
    }
}
