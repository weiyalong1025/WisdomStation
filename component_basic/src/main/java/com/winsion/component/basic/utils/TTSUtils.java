package com.winsion.component.basic.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.sinovoice.hcicloudsdk.android.tts.player.TTSPlayer;
import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.common.AuthExpireTime;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.hwr.HwrInitParam;
import com.sinovoice.hcicloudsdk.common.tts.TtsConfig;
import com.sinovoice.hcicloudsdk.common.tts.TtsInitParam;
import com.sinovoice.hcicloudsdk.player.TTSCommonPlayer;
import com.sinovoice.hcicloudsdk.player.TTSPlayerListener;
import com.winsion.component.basic.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by niudong on 2016/9/28.
 * QQ:166413163
 * Company:winsion
 */

public class TTSUtils {
    /**
     * 加载用户信息工具类
     */
    private AccountInfo mAccountInfo;
    private TtsConfig ttsConfig = null;
    private TTSPlayer mTtsPlayer = null;
    private static volatile TTSUtils mInstance;

    public static TTSUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (TTSUtils.class) {
                if (mInstance == null) {
                    mInstance = new TTSUtils(context);
                }
            }
        }
        return mInstance;
    }

    private TTSUtils(Context context) {
        mAccountInfo = AccountInfo.getInstance();

        if (!mAccountInfo.loadAccountInfo(context)) {
            return;
        }

        // 加载信息,返回InitParam, 获得配置参数的字符串
        InitParam initParam = getInitParam(context);
        String strConfig = initParam.getStringConfig();

        // 初始化
        int errCode = HciCloudSys.hciInit(strConfig, context);
        if (errCode != HciErrorCode.HCI_ERR_NONE && errCode != HciErrorCode.HCI_ERR_SYS_ALREADY_INIT) {
//            Toast.makeText(CustomApplication.context, "hciInit error: " + HciCloudSys.hciGetErrorInfo(errCode), Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取授权/更新授权文件 :
        errCode = checkAuthAndUpdateAuth();
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            // 由于系统已经初始化成功,在结束前需要调用方法hciRelease()进行系统的反初始化
//            Toast.makeText(CustomApplication.context, "CheckAuthAndUpdateAuth error: " + HciCloudSys.hciGetErrorInfo(errCode), Toast.LENGTH_SHORT).show();
            HciCloudSys.hciRelease();
            return;
        }
        //传入了capKey初始化TTS播发器
        initPlayer(context);
    }

    /**
     * 初始化播放器
     */
    private void initPlayer(Context context) {
        // 读取用户的调用的能力
        String capKey = mAccountInfo.getCapKey();
        // 构造Tts初始化的帮助类的实例
        TtsInitParam ttsInitParam = new TtsInitParam();
        // 获取App应用中的lib的路径
        String dataPath = context.getFilesDir().getAbsolutePath().replace("files", "lib");
        ttsInitParam.addParam(TtsInitParam.PARAM_KEY_DATA_PATH, dataPath);
        // 此处演示初始化的能力为tts.cloud.xiaokun, 用户可以根据自己可用的能力进行设置
        // 另外,此处可以传入多个能力值,并用;隔开
        ttsInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, capKey);
        // 使用lib下的资源文件,需要添加android_so的标记
        ttsInitParam.addParam(HwrInitParam.PARAM_KEY_FILE_FLAG, "android_so");
        mTtsPlayer = new TTSPlayer();
        // 配置TTS初始化参数
        ttsConfig = new TtsConfig();
        mTtsPlayer.init(ttsInitParam.getStringConfig(), new TTSEventProcess());
    }

    // 云端合成,不启用编码传输(默认encode=none)
    public void synth(Context context, String text) {
        // 读取用户的调用的能力
        String capKey = mAccountInfo.getCapKey();
        // 配置播放器的属性。包括：音频格式，音库文件，语音风格，语速等等。详情见文档。
        ttsConfig = new TtsConfig();
        // 音频格式
        ttsConfig.addParam(TtsConfig.BasicConfig.PARAM_KEY_AUDIO_FORMAT, "pcm16k16bit");
        // 指定语音合成的能力(云端合成,发言人是XiaoKun)
        ttsConfig.addParam(TtsConfig.SessionConfig.PARAM_KEY_CAP_KEY, capKey);
        // 设置合成语速
        ttsConfig.addParam(TtsConfig.BasicConfig.PARAM_KEY_SPEED, "5");
        // property为私有云能力必选参数，公有云传此参数无效
        ttsConfig.addParam("property", "cn_xiaokun_common");
        if (mTtsPlayer != null) {
            if (mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PLAYING
                    || mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PAUSE) {
                mTtsPlayer.stop();
            }
            if (mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_IDLE) {
                mTtsPlayer.play(text,
                        ttsConfig.getStringConfig());
            } else {
                ToastUtils.showToast(context, "内部播放器异常");
            }
        } else {
            new Handler(Looper.getMainLooper()).post(() ->
                    // 此处执行UI操作
                    ToastUtils.showToast(context, R.string.toast_first_use_tts));
        }
    }

    public void release() {
        if (mTtsPlayer != null) {
            mTtsPlayer.release();
        }
        HciCloudSys.hciRelease();
    }

    // 播放器回调
    private class TTSEventProcess implements TTSPlayerListener {

        @Override
        public void onPlayerEventPlayerError(TTSCommonPlayer.PlayerEvent playerEvent,
                                             int errorCode) {
        }

        @Override
        public void onPlayerEventProgressChange(TTSCommonPlayer.PlayerEvent playerEvent,
                                                int start, int end) {
        }

        @Override
        public void onPlayerEventStateChange(TTSCommonPlayer.PlayerEvent playerEvent) {
        }
    }

    /**
     * 获取授权
     *
     * @return 成功
     */
    private int checkAuthAndUpdateAuth() {
        // 获取系统授权到期时间
        int initResult;
        AuthExpireTime objExpireTime = new AuthExpireTime();
        initResult = HciCloudSys.hciGetAuthExpireTime(objExpireTime);
        if (initResult == HciErrorCode.HCI_ERR_NONE) {
            if (objExpireTime.getExpireTime() * 1000 > System
                    .currentTimeMillis()) {
                // 已经成功获取了授权,并且距离授权到期有充足的时间(>7天)
                return initResult;
            }
        }

        // 获取过期时间失败或者已经过期
        initResult = HciCloudSys.hciCheckAuth();
        if (initResult == HciErrorCode.HCI_ERR_NONE) {
            return initResult;
        } else {
            return initResult;
        }
    }

    /**
     * 加载初始化信息
     * <p>
     * 下文语境
     *
     * @return 系统初始化参数
     */
    private InitParam getInitParam(Context context) {
        String authDirPath = context.getFilesDir().getAbsolutePath();
        // 前置条件：无
        InitParam initparam = new InitParam();
        // 授权文件所在路径，此项必填
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_AUTH_PATH, authDirPath);
        // 是否自动访问云授权,详见 获取授权/更新授权文件处注释
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_AUTO_CLOUD_AUTH, "no");
        // 灵云云服务的接口地址，此项必填
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_CLOUD_URL, AccountInfo
                .getInstance().getCloudUrl());
        // 开发者Key，此项必填，由捷通华声提供
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_DEVELOPER_KEY, AccountInfo
                .getInstance().getDeveloperKey());
        // 应用Key，此项必填，由捷通华声提供
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_APP_KEY, AccountInfo
                .getInstance().getAppKey());
        /*// 配置日志参数
        String sdcardState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
            String logPath = FilePathUtils.getTTSLogPath();
            // 日志文件地址
            File fileDir = new File(logPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            // 日志的路径，可选，如果不传或者为空则不生成日志
            initparam.addParam(InitParam.LogParam.PARAM_KEY_LOG_FILE_PATH, logPath);
            // 日志数目，默认保留多少个日志文件，超过则覆盖最旧的日志
            initparam.addParam(InitParam.LogParam.PARAM_KEY_LOG_FILE_COUNT, "5");
            // 日志大小，默认一个日志文件写多大，单位为K
            initparam.addParam(InitParam.LogParam.PARAM_KEY_LOG_FILE_SIZE, "1024");
            // 日志等级，0=无，1=错误，2=警告，3=信息，4=细节，5=调试，SDK将输出小于等于logLevel的日志信息
            initparam.addParam(InitParam.LogParam.PARAM_KEY_LOG_LEVEL, "5");
        }*/
        return initparam;
    }

    public static class AccountInfo {

        private static AccountInfo mInstance;

        private Map<String, String> mAccountMap;

        private AccountInfo() {
            mAccountMap = new HashMap<>();
        }

        public static AccountInfo getInstance() {
            if (mInstance == null) {
                mInstance = new AccountInfo();
            }
            return mInstance;
        }

        String getCapKey() {
            return mAccountMap.get("capKey");
        }

        String getDeveloperKey() {
            return mAccountMap.get("developerKey");
        }

        String getAppKey() {
            return mAccountMap.get("appKey");
        }

        String getCloudUrl() {
            return mAccountMap.get("cloudUrl");
        }

        /**
         * 加载用户的注册信息
         *
         * @param
         */
        boolean loadAccountInfo(Context context) {
            boolean isSuccess = true;
            try {
                InputStream in;
                in = context.getResources().getAssets().open("AccountInfo.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(in,
                        "utf-8");
                BufferedReader br = new BufferedReader(inputStreamReader);
                String temp;
                String[] sInfo;
                temp = br.readLine();
                while (temp != null) {
                    if (!temp.startsWith("#") && !temp.equalsIgnoreCase("")) {
                        sInfo = temp.split("=");
                        if (sInfo.length == 2) {
                            if (sInfo[1] == null || sInfo[1].length() <= 0) {
                                isSuccess = false;
                                break;
                            }
                            mAccountMap.put(sInfo[0], sInfo[1]);
                        }
                    }
                    temp = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
            }

            return isSuccess;
        }
    }
}