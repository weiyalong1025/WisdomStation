package com.winsion.dispatch;

import android.view.KeyEvent;
import android.view.View;

import com.winsion.dispatch.base.BaseActivity;
import com.winsion.dispatch.common.constants.SystemType;
import com.winsion.dispatch.data.SPDataSource;
import com.winsion.dispatch.data.constants.SPKey;
import com.winsion.dispatch.main.activity.MainActivity;

/**
 * Created by wyl on 2017/8/2
 * 切换系统
 */
public class SwitchSysActivity extends BaseActivity {

    @Override
    protected int setContentView() {
        return R.layout.activity_switch;
    }

    @Override
    protected void start() {
        addOnClickListeners(R.id.ib_switch_oper, R.id.ib_switch_grid);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_switch_oper:
                SPDataSource.put(this, SPKey.KEY_SYS_TYPE, SystemType.OPERATION);
                break;
            case R.id.ib_switch_grid:
                SPDataSource.put(this, SPKey.KEY_SYS_TYPE, SystemType.GRID);
                break;
        }
        startActivity(MainActivity.class, true);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isTaskRoot() && keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(MainActivity.class, true);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
