package com.winsion.component.scene.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import com.billy.cc.core.component.CC;
import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.scene.R;

/**
 * Created by 10295 on 2017/12/10 0010.
 * 现场一级界面
 */

public class SceneRootFragment extends BaseFragment {

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.scene_fragment_root, null);
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
        int id = view.getId();
        if (id == R.id.mv_grid) {
            CC.obtainBuilder("ComponentTask")
                    .setContext(mContext)
                    .setActionName("toPatrolPlanActivity")
                    .build()
                    .callAsync();
        } else if (id == R.id.mv_main_passenger) {
            showToast("重点旅客");
        } else if (id == R.id.mv_lost) {
            showToast("失物招领");
        } else if (id == R.id.mv_area_broadcast) {
            showToast("小区广播");
        } else if (id == R.id.mv_broadcast) {
            showToast("客运广播");
        } else if (id == R.id.mv_transfer) {
            showToast("交班");
        } else if (id == R.id.mv_elevator) {
            showToast("电梯");
        } else if (id == R.id.mv_water) {
            showToast("给水");
        } else if (id == R.id.mv_video) {
            showToast("视频");
        } else if (id == R.id.mv_door) {
            showToast("门禁");
        } else if (id == R.id.mv_air_conditioner) {
            showToast("空调");
        } else if (id == R.id.mv_air_handing) {
            showToast("空气处理机");
        } else if (id == R.id.mv_pump) {
            showToast("水泵");
        } else if (id == R.id.mv_cooling_tower) {
            showToast("冷却塔");
        } else if (id == R.id.mv_light) {
            showToast("照明");
        }
    }
}
