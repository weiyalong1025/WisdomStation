package com.winsion.wisdomstation;

import android.view.KeyEvent;
import android.view.View;

import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.common.constants.SystemType;
import com.winsion.wisdomstation.data.SPDataSource;
import com.winsion.wisdomstation.data.constants.SPKey;
import com.winsion.wisdomstation.main.activity.MainActivity;

import butterknife.OnClick;

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
    }

    @OnClick({R.id.ib_switch_oper, R.id.ib_switch_grid})
    public void onViewClicked(View view) {
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
