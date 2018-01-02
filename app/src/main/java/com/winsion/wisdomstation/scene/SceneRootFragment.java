package com.winsion.wisdomstation.scene;

import android.view.View;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseFragment;

import butterknife.OnClick;

/**
 * Created by 10295 on 2017/12/10 0010.
 */

public class SceneRootFragment extends BaseFragment {

    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_root_scene, null);
    }

    @Override
    protected void init() {

    }

    @OnClick({R.id.ib_main_passenger, R.id.iv_lost, R.id.iv_area_broadcast, R.id.iv_broadcast,
            R.id.iv_transfer, R.id.iv_elevator, R.id.iv_water, R.id.iv_video, R.id.iv_door,
            R.id.iv_air_conditioner, R.id.iv_air_handing, R.id.iv_pump, R.id.iv_cooling_tower,
            R.id.iv_light})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_main_passenger:
                showToast("重点旅客");
                break;
            case R.id.iv_lost:
                showToast("失物招领");
                break;
            case R.id.iv_area_broadcast:
                showToast("小区广播");
                break;
            case R.id.iv_broadcast:
                showToast("客运广播");
                break;
            case R.id.iv_transfer:
                showToast("交班");
                break;
            case R.id.iv_elevator:
                showToast("电梯");
                break;
            case R.id.iv_water:
                showToast("给水");
                break;
            case R.id.iv_video:
                showToast("视频");
                break;
            case R.id.iv_door:
                showToast("门禁");
                break;
            case R.id.iv_air_conditioner:
                showToast("空调");
                break;
            case R.id.iv_air_handing:
                showToast("空气处理机");
                break;
            case R.id.iv_pump:
                showToast("水泵");
                break;
            case R.id.iv_cooling_tower:
                showToast("冷却塔");
                break;
            case R.id.iv_light:
                showToast("照明");
                break;
        }
    }
}
