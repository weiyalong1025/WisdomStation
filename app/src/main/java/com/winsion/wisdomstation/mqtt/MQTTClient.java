package com.winsion.wisdomstation.mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.winsion.wisdomstation.mqtt.entity.MQMessage;
import com.winsion.wisdomstation.utils.LogUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.lang.ref.WeakReference;
import java.util.Vector;

/**
 * Created by 10295 on 2017/12/21 0021.
 * MQTT客户端
 */

public class MQTTClient extends BroadcastReceiver implements IMqttActionListener {
    private static final String TAG = "MQTTClient";

    private static final int MQ_PORT = 1883;
    private static final String ALARM_TOPIC = "2017winsion/kingkong/alarm/topic";
    private static final String ORDER_TOPIC = "2017winsion/kingkong/order/topic";
    private static final String MONITOR_TOPIC = "2017winsion/kingkong/monitor/topic";
    private static final String REPORT_TOPIC = "2017winsion/kingkong/report/topic";
    private static final String[] TOPIC_NAMES = new String[]{ALARM_TOPIC, ORDER_TOPIC, MONITOR_TOPIC, REPORT_TOPIC};
    private static final int[] QOS = new int[]{0, 0, 0, 0};

    private static Vector<Observer> mObservers = new Vector<>();
    private static volatile MQTTClient mInstance;
    private MqttAndroidClient mClient;
    private WeakReference<Context> mContext;
    private boolean needReconnect;
    private ConnectListener mConnectListener;
    private Connector mConnector;
    private String mServerUrl;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(TAG, intent.getAction());
        if (isNetworkConnected()) {
            mContext.get().unregisterReceiver(this);
            reconnect();
        }
    }

    public interface ConnectListener {
        void connectSuccess();

        void connectFailed();
    }

    public interface Observer {
        void onMessageArrive(MQMessage msg);
    }

    public synchronized static void addObserver(Observer observer) {
        if (observer == null) throw new NullPointerException("observer == null");
        if (!mObservers.contains(observer)) mObservers.add(observer);
    }

    public synchronized void removeObserver(Observer observer) {
        mObservers.remove(observer);
    }

    private static void notifyObservers(MQMessage mqMessage) {
        for (Observer observer : mObservers) {
            if (observer != null && mqMessage != null)
                observer.onMessageArrive(mqMessage);
        }
    }

    private MQTTClient(Connector connector) {
        this.mConnectListener = connector.connectListener;
        this.mConnector = connector;
        this.mContext = new WeakReference<>(connector.context);
        mServerUrl = connector.host;
        String CLIENT_ID = "yalong" + System.currentTimeMillis();
        mClient = new MqttAndroidClient(mContext.get(), "tcp://" + mServerUrl + ":" + MQ_PORT, CLIENT_ID, new MemoryPersistence());
        mClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                LogUtils.i(TAG, "connectionLost：连接丢失");
                reconnect();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // 接到消息
                String msg = new String(message.getPayload());
                LogUtils.i(TAG, "messageArrived:" + msg);
                notifyObservers(JSON.parseObject(msg, MQMessage.class));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private static MQTTClient getInstance(Connector connector) {
        if (mInstance == null) {
            synchronized (MQTTClient.class) {
                if (mInstance == null) {
                    mInstance = new MQTTClient(connector);
                }
            }
        }
        return mInstance;
    }

    public static class Connector {
        private Context context;
        private ConnectListener connectListener;
        private String host;

        public Connector(Context context) {
            this.context = context;
        }

        public Connector listener(ConnectListener connectListener) {
            this.connectListener = connectListener;
            return this;
        }

        public Connector host(String host) {
            this.host = host;
            return this;
        }

        public void connect() {
            if (mInstance == null) {
                MQTTClient.getInstance(this).connect();
            } else {
                String currentHost = mInstance.getCurrentHost();
                if (!currentHost.equals(host)) {
                    destroy();
                    MQTTClient.getInstance(this).connect();
                }
            }
        }
    }

    private String getCurrentHost() {
        return mServerUrl;
    }

    void connect() {
        if (!isNetworkConnected()) {
            if (!needReconnect) {
                if (mConnectListener != null) mConnectListener.connectFailed();
            } else {
                IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                mContext.get().registerReceiver(this, intentFilter);
            }
        } else if (mClient != null && !mClient.isConnected()) {
            try {
                MqttConnectOptions options = new MqttConnectOptions();
                options.setConnectionTimeout(10);
                mClient.connect(options, null, this);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    void reconnect() {
        destroy();
        MQTTClient instance = getInstance(mConnector);
        instance.needReconnect = true;
        instance.connect();
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        LogUtils.i(TAG, "connect success !");
        try {
            mClient.subscribe(TOPIC_NAMES, QOS);
            if (!needReconnect) {
                needReconnect = true;
            }
            if (mConnectListener != null) {
                mConnectListener.connectSuccess();
                mConnector.connectListener = null;
                mConnectListener = null;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        LogUtils.i(TAG, "connect failed , server url is ：" + mServerUrl + " , exception: " + exception.toString());
        if (needReconnect) {
            reconnect();
        } else {
            if (mConnectListener != null) {
                mConnectListener.connectFailed();
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

    private void close() {
        try {
            mClient.setCallback(null);
            if (mClient.isConnected()) {
                mClient.close();
                mClient.disconnect();
            }
            mInstance = null;
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void destroy() {
        if (mInstance != null) mInstance.close();
    }
}
