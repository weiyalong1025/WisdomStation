package com.winsion.dispatch.modules.scene.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.task.activity.GridActivity;
import com.winsion.dispatch.R;

/**
 * Created by 10295 on 2017/12/10 0010.
 * 现场一级界面
 */

public class SceneRootFragment extends BaseFragment {

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_root_scene, null);
    }

    @Override
    protected void init() {
        addOnClickListeners(R.id.mv_grid, R.id.mv_main_passenger, R.id.mv_lost, R.id.mv_area_broadcast,
                R.id.mv_broadcast, R.id.mv_transfer, R.id.mv_elevator, R.id.mv_water, R.id.mv_video,
                R.id.mv_door, R.id.mv_air_conditioner, R.id.mv_air_handing, R.id.mv_pump,
                R.id.mv_cooling_tower, R.id.mv_light);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mv_grid:
                startActivity(GridActivity.class);
                break;
            case R.id.mv_main_passenger:
                showToast("重点旅客");
                break;
            case R.id.mv_lost:
                showToast("失物招领");
                break;
            case R.id.mv_area_broadcast:
                showToast("小区广播");
                break;
            case R.id.mv_broadcast:
                showToast("客运广播");
                break;
            case R.id.mv_transfer:
                showToast("交班");
                break;
            case R.id.mv_elevator:
                showToast("电梯");
                break;
            case R.id.mv_water:
                showToast("给水");
                break;
            case R.id.mv_video:
                showToast("视频");
                break;
            case R.id.mv_door:
                showToast("门禁");
                break;
            case R.id.mv_air_conditioner:
                showToast("空调");
                break;
            case R.id.mv_air_handing:
                showToast("空气处理机");
                break;
            case R.id.mv_pump:
                showToast("水泵");
                break;
            case R.id.mv_cooling_tower:
                showToast("冷却塔");
                break;
            case R.id.mv_light:
                showToast("照明");
                break;
        }
    }
}
