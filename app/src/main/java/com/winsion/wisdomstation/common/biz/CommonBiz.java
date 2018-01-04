package com.winsion.wisdomstation.common.biz;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.lzy.okgo.model.HttpParams;
import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.common.listener.SuccessListener;
import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.DBDataSource;
import com.winsion.wisdomstation.data.NetDataSource;
import com.winsion.wisdomstation.data.constants.Urls;
import com.winsion.wisdomstation.login.activity.LoginActivity;
import com.winsion.wisdomstation.mqtt.MQTTClient;
import com.winsion.wisdomstation.utils.ConvertUtils;
import com.winsion.wisdomstation.utils.constants.ListType;

/**
 * Created by 10295 on 2017/12/21 0021.
 */

public class CommonBiz {
    /**
     * 用户注销
     */
    public static void logout(Context context, SuccessListener listener) {
        MQTTClient.destroy();
        // 取消自动登录
        DBDataSource.getInstance().cancelAutoLogin();
        // 清空所有通知
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert nm != null;
        nm.cancelAll();
        // 请求用户退出接口
        HttpParams httpParams = new HttpParams();
        httpParams.put("userId", CacheDataSource.getUserId());
        NetDataSource.post(null, Urls.USER_LOGOUT, httpParams, null);
        // 清除缓存信息
        CacheDataSource.clearCache();
        listener.onSuccess();
        // 跳转登录界面
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String getBSSID(Context context) {
        String bssid = "";
        String ssid = "";
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().
                getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            bssid = connectionInfo.getBSSID();
            ssid = connectionInfo.getSSID();
        }
        ssid = ssid.substring(1, ssid.length() - 1);
        // 根据配置的SSID对BSSID过滤
        if (!TextUtils.isEmpty(bssid) &&
                !TextUtils.isEmpty(CacheDataSource.getSsid()) &&
                TextUtils.equals(ssid, CacheDataSource.getSsid())) {
            bssid = bssid.replace(":", "").replace("-", "");
        } else {
            bssid = "";
        }
        return bssid;
    }

    /**
     * 隐藏软键盘
     * 可以和{@link #showKeyboard(EditText, boolean)}搭配使用，进行键盘的显示隐藏控制。
     *
     * @param view 当前页面上任意一个可用的view
     */
    public static void hideKeyboard(final View view) {
        if (null == view) return;
        InputMethodManager imm = (InputMethodManager) view.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 针对给定的editText显示软键盘（editText会先获得焦点）.
     * 可以和{@link #hideKeyboard(View)}搭配使用，进行键盘的显示隐藏控制。
     */
    public static void showKeyboard(final EditText editText, boolean delay) {
        if (null == editText || !editText.requestFocus()) return;
        InputMethodManager imm = (InputMethodManager) editText.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        if (delay) {
            editText.postDelayed(() -> imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT), 200);
        } else {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 根据屏幕高度获取建议的ListView高度
     *
     * @param context    你懂得
     * @param itemHeight 一条item的高度
     * @param type       {@link ListType} 是什么类型上显示的ListView 可以是Dialog和PopupWindow
     * @return 建议的ListView高度
     */
    public static int getSuggestMaxHeight(Context context, int itemHeight, int type) {
        float percent = 1.5f;
        if (type == ListType.TYPE_DIALOG) {
            percent = 1.5f;
        } else if (type == ListType.TYPE_POPUP) {
            percent = 4;
        }
        int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
        int a = (int) (heightPixels / percent);
        int b = a / itemHeight;
        return b * itemHeight;
    }

    public static TimePickerView.Builder getMyTimePickerBuilder(Context context, TimePickerView.OnTimeSelectListener listener) {
        return new TimePickerView.Builder(context, listener)
                .isCyclic(false)
                .setOutSideCancelable(true)
                // 标题背景颜色
                .setTitleBgColor(0xFF373739)
                // 滚轮背景颜色
                .setBgColor(0xFF3B3B3D)
                // 取消按钮文字颜色
                .setCancelColor(0xFF373739)
                // 确定按钮文字颜色
                .setSubmitColor(0xFF46DBE2)
                // 取消和确定文字大小
                .setSubCalSize(ConvertUtils.getDimenSp(context, R.dimen.s18))
                // 滚轮文字大小
                .setContentSize(ConvertUtils.getDimenSp(context, R.dimen.s20))
                // 选中的文字颜色
                .setTextColorCenter(0xFF46DBE2)
                // 是否只显示中间选中项的label文字，false则每项item全部都带有label
                .isCenterLabel(false);
    }

    public static OptionsPickerView.Builder getMyOptionPickerBuilder(
            Context context, OptionsPickerView.OnOptionsSelectListener listener) {
        //条件选择器
        return new OptionsPickerView.Builder(context, listener)
                .setCyclic(false, false, false)
                .setOutSideCancelable(true)
                // 标题背景颜色
                .setTitleBgColor(0xFF373739)
                // 滚轮背景颜色
                .setBgColor(0xFF3B3B3D)
                // 取消按钮文字颜色
                .setCancelColor(0xFF373739)
                // 确定按钮文字颜色
                .setSubmitColor(0xFF46DBE2)
                // 取消和确定文字大小
                .setSubCalSize(ConvertUtils.getDimenSp(context, R.dimen.s18))
                // 滚轮文字大小
                .setContentTextSize(ConvertUtils.getDimenSp(context, R.dimen.s20))
                // 选中的文字颜色
                .setTextColorCenter(0xFF46DBE2)
                // 是否只显示中间选中项的label文字，false则每项item全部都带有label
                .isCenterLabel(false);
    }
}
