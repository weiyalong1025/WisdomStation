package debug;

import android.app.Application;

import com.billy.cc.core.component.CC;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.utils.CrashUtils;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.LogUtils;
import com.winsion.component.contact.BuildConfig;
import com.winsion.component.contact.R;

import java.io.IOException;

/**
 * Created by 10295 on 2018/3/23.
 * 联系人组件DEBUG版本Application
 */

public class ContactApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 全局捕获异常并保存异常信息
        CrashUtils.getInstance().init(this);

        // 初始化网络库
        NetDataSource.init(this, BuildConfig.DEBUG, getString(R.string.contact_app_name));

        // 初始化LOG
        initLog();

        // 初始化CC
        CC.enableVerboseLog(BuildConfig.DEBUG);
        CC.enableDebug(BuildConfig.DEBUG);
        CC.enableRemoteCC(BuildConfig.DEBUG);
    }

    private void initLog() {
        String logDir;
        try {
            logDir = DirAndFileUtils.getLogDir().toString();
        } catch (IOException e) {
            logDir = getCacheDir().toString();
        }
        String logTag = getString(R.string.contact_app_name);
        LogUtils.init(BuildConfig.DEBUG, true, logDir, LogUtils.FILTER_V, logTag);
    }
}
