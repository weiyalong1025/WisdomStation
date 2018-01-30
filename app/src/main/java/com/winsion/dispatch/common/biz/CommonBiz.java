package com.winsion.dispatch.common.biz;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.view.BasePickerView;
import com.lzy.okgo.model.HttpParams;
import com.winsion.dispatch.R;
import com.winsion.dispatch.common.entity.UpdateEntity;
import com.winsion.dispatch.common.listener.SuccessListener;
import com.winsion.dispatch.data.CacheDataSource;
import com.winsion.dispatch.data.DBDataSource;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.data.constants.Urls;
import com.winsion.dispatch.data.listener.DownloadListener;
import com.winsion.dispatch.data.listener.ResponseListener;
import com.winsion.dispatch.login.activity.LoginActivity;
import com.winsion.dispatch.mqtt.MQTTClient;
import com.winsion.dispatch.utils.AppUtils;
import com.winsion.dispatch.utils.ConvertUtils;
import com.winsion.dispatch.utils.DirAndFileUtils;
import com.winsion.dispatch.utils.ToastUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by 10295 on 2017/12/21 0021.
 * 通用业务层
 */

public class CommonBiz {
    /**
     * 检查版本更新
     *
     * @param context  上下文
     * @param showHint 没有更新/检查更新失败是否显示提示信息
     */
    public static void checkVersionUpdate(Context context, boolean showHint) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("key", "gridApp");
        NetDataSource.post(context, Urls.CHECK_UPDATE, httpParams, new ResponseListener<UpdateEntity>() {
            @Override
            public UpdateEntity convert(String jsonStr) {
                return JSON.parseObject(jsonStr, UpdateEntity.class);
            }

            @Override
            public void onSuccess(UpdateEntity updateEntity) {
                int versionCode = Integer.valueOf(updateEntity.getVersionCode());
                if (AppUtils.getPackageInfo(context).versionCode < versionCode) {
                    // 需要更新
                    showUpdateDialog(context, updateEntity);
                } else if (showHint) {
                    // 不需要更新
                    ToastUtils.showToast(context, "当前已是最新版本");
                }
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                if (showHint) ToastUtils.showToast(context, "检查更新失败");
            }
        });
    }

    /**
     * 显示更新版本对话框
     *
     * @param context      上下文
     * @param updateEntity 包含下载地址、更新信息
     */
    private static void showUpdateDialog(Context context, UpdateEntity updateEntity) {
        // 需要更新,弹出对话框
        new AlertDialog.Builder(context)
                .setTitle("发现新版本")
                .setMessage(updateEntity.getUpdateInfo())
                .setCancelable(false)
                .setPositiveButton("立即更新", (dialog, which) -> downloadNewVersion(context, updateEntity.getDownloadUrl()))
                .setNegativeButton("暂不更新", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * 下载更新包
     *
     * @param context     上下文
     * @param downloadUrl 文件下载地址
     */
    private static void downloadNewVersion(Context context, String downloadUrl) {
        try {
            // 下载文件目标存储目录
            String targetDir = DirAndFileUtils.getUpdateDir().getAbsolutePath();
            String[] split = downloadUrl.split("/");
            // 更新包文件名
            String fileName = split[split.length - 1];
            File updateFile = new File(targetDir, fileName);

            // 显示下载进度对话框
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("版本更新");
            progressDialog.setMessage("正在下载安装包...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", (dialog, which) -> {
                NetDataSource.unSubscribe(downloadUrl);
                dialog.dismiss();
            });
            progressDialog.show();

            // 下载更新包
            NetDataSource.downloadFile(downloadUrl, downloadUrl, targetDir, new DownloadListener() {
                @Override
                public void downloadProgress(String serverUri, float progress) {
                    progressDialog.setProgress((int) progress);
                }

                @Override
                public void downloadSuccess(String serverUri) {
                    progressDialog.dismiss();
                    Uri downloadFileUri = Uri.fromFile(updateFile);
                    if (downloadFileUri != null) {
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(install);
                    }
                }

                @Override
                public void downloadFailed(String serverUri) {
                    progressDialog.setMessage(context.getString(R.string.download_failed));
                }
            });

        } catch (IOException e) {
            ToastUtils.showToast(context, R.string.please_check_sdcard_state);
        }
    }

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

    public static void selfAdaptionTopBar(BasePickerView pickerView) {
        RelativeLayout tvTopBar = (RelativeLayout) pickerView.findViewById(R.id.rv_topbar);
        if (tvTopBar == null) return;
        ViewGroup.LayoutParams layoutParams = tvTopBar.getLayoutParams();
        layoutParams.height = tvTopBar.getResources().getDimensionPixelSize(R.dimen.d45);
        tvTopBar.setLayoutParams(layoutParams);
    }
}
