package com.winsion.wisdomstation.modules.grid.modules.patrolplan.fragment;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseFragment;
import com.winsion.wisdomstation.modules.grid.adapter.BluetoothPointAdapter;
import com.winsion.wisdomstation.modules.grid.adapter.PatrolPlanAdapter;
import com.winsion.wisdomstation.modules.grid.entity.BluetoothPoint;
import com.winsion.wisdomstation.modules.grid.entity.PatrolTaskEntity;
import com.winsion.wisdomstation.utils.IbeaconUtils;
import com.winsion.wisdomstation.utils.ViewUtils;
import com.winsion.wisdomstation.utils.constants.ListType;
import com.winsion.wisdomstation.view.WrapContentListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by 10295 on 2017/12/26.
 * 巡检任务以及界面
 * TODO 蓝牙需要动态权限
 */
public class PatrolPlanFragment extends BaseFragment implements PatrolPlanContract.View, AdapterView.OnItemClickListener, BluetoothAdapter.LeScanCallback {
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.lv_list)
    ListView lvList;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;

    private static final int REQUEST_ENABLE_BT = 100;

    private PatrolPlanPresenter mPresenter;
    private PatrolPlanAdapter mLvAdapter;
    private BluetoothAdapter mBtAdapter;
    private BluetoothPointAdapter bluetoothAdapter;
    private long lastUpdateTime;

    private List<PatrolTaskEntity> listData = new ArrayList<>();
    private HashMap<String, Long> btMap = new HashMap<>();
    private ArrayList<BluetoothPoint> bluetoothPoints = new ArrayList<>();

    private BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            logI("onReceive---------STATE_TURNING_ON");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            logI("onReceive---------STATE_ON");
                            mBtAdapter.startLeScan(PatrolPlanFragment.this);
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            logI("onReceive---------STATE_TURNING_OFF");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            mBtAdapter.stopLeScan(PatrolPlanFragment.this);
                            logI("onReceive---------STATE_OFF");
                            break;
                    }
                    break;
            }
        }
    };

    @Override
    public void handlerMessage(Message msg) {
        super.handlerMessage(msg);
        if (msg.what == 0) {
            checkIsArrive();
            mHandler.sendEmptyMessageDelayed(0, 1000 * 5);
        }
    }

    private void checkIsArrive() {
        for (PatrolTaskEntity patrolTaskDto : listData) {
            String bluetoothId = patrolTaskDto.getBluetoothid();
            if (isEmpty(bluetoothId)) {
                return;
            }
            String[] bluetoothIds = bluetoothId.split(",");
            boolean arrive = false;
            for (String id : bluetoothIds) {
                Long btTime = btMap.get(id);
                // 超过30秒没有接到蓝牙标签信号就认为离开该蓝牙
                if (btTime != null && System.currentTimeMillis() - btTime < 1000 * 30) {
                    arrive = true;
                    break;
                }
            }
            patrolTaskDto.setArrive(arrive);
        }
        mLvAdapter.notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_patrol_plan, null);
    }

    @Override
    protected void init() {
        initPresenter();
        initView();
        initBluetooth();
        initListener();
        initData();
    }

    private void initPresenter() {
        mPresenter = new PatrolPlanPresenter(this);
    }

    private void initView() {
        swipeRefresh.setColorSchemeResources(R.color.blue1);
        // adapter
        mLvAdapter = new PatrolPlanAdapter(mContext, listData);
        lvList.setAdapter(mLvAdapter);
    }

    private void initBluetooth() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler.sendEmptyMessage(0);
        // 检测蓝牙是否可用
        if (verifyBluetooth()) {
            mBtAdapter.startLeScan(this);
        }
        // 注册监听蓝牙状态改变的广播
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBtReceiver, filter);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        IbeaconUtils.iBeacon ibeacon = IbeaconUtils.fromScanData(device, rssi, scanRecord);
        if (ibeacon != null) {
            String btAddress = ibeacon.bluetoothAddress;
            long lastTime = System.currentTimeMillis();
            btMap.put(btAddress, lastTime);
            BluetoothPoint bluetoothPoint = new BluetoothPoint();
            bluetoothPoint.setBluetoothId(btAddress);
            bluetoothPoint.setLastTime(lastTime);
            if ((System.currentTimeMillis() - lastUpdateTime) > 1000 * 5) {
                updateOrAddBluetoothPoint(bluetoothPoint);
            }
        }
    }

    private void updateOrAddBluetoothPoint(BluetoothPoint bluetoothPoint) {
        boolean isUpdate = false;
        for (BluetoothPoint point : bluetoothPoints) {
            if (equals(point.getBluetoothId(), bluetoothPoint.getBluetoothId())) {
                bluetoothPoints.remove(point);
                bluetoothPoints.add(bluetoothPoint);
                isUpdate = true;
                break;
            }
        }
        if (!isUpdate) {
            bluetoothPoints.add(bluetoothPoint);
        }
        Collections.sort(bluetoothPoints, (o1, o2) -> (int) (o2.getLastTime() - o1.getLastTime()));
        if (bluetoothAdapter != null) {
            bluetoothAdapter.notifyDataSetChanged();
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    private boolean verifyBluetooth() {
        if (mBtAdapter == null) {
            showToast(R.string.your_device_does_not_support_bluetooth);
            return false;
        }
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED && requestCode == REQUEST_ENABLE_BT) {
            showToast(R.string.Failed_to_open_the_bluetooth);
        }
    }

    private void initListener() {
        swipeRefresh.setOnRefreshListener(this::initData);
        lvList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*PatrolTaskDto patrolTaskDto = patrolTaskList.get(position);
        if (patrolTaskDto.isArrive()) {
            Intent intent = new Intent(mContext, PatrolItemActivity.class);
            intent.putExtra("patrolId", patrolTaskDto.getId());
            intent.putExtra("siteName", patrolTaskDto.getPointname());
            intent.putExtra("startTime", patrolTaskDto.getRealstarttime());
            intent.putExtra("endTime", patrolTaskDto.getRealendtime());
            startActivity(intent);
        }*/
    }

    private void initData() {
        mPresenter.getPatrolPlanData();
    }

    @SuppressLint("InflateParams")
    @OnClick({R.id.iv_bluetooth, R.id.tv_hint})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_bluetooth:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View inflate = LayoutInflater.from(getContext()).inflate(R.layout.item_bluetooth_info, null);
                inflate.measure(0, 0);
                int suggestMaxHeight = ViewUtils.getSuggestMaxHeight(mContext, inflate.getMeasuredHeight(), ListType.TYPE_DIALOG);
                ListView listView = new WrapContentListView(getContext(), suggestMaxHeight);
                bluetoothAdapter = new BluetoothPointAdapter(mContext, bluetoothPoints);
                listView.setAdapter(bluetoothAdapter);
                builder.setView(listView);
                builder.setCancelable(true);
                builder.create().show();
                break;
            case R.id.tv_hint:
                initData();
                showView(flContainer, progressBar);
                break;
        }
    }

    @Override
    public void getPatrolPlanDataSuccess(List<PatrolTaskEntity> patrolPlanDate) {
        swipeRefresh.setRefreshing(false);
        if (patrolPlanDate.size() == 0) {
            tvHint.setText(R.string.no_data_click_to_retry);
            showView(flContainer, tvHint);
        } else {
            listData.clear();
            listData.addAll(patrolPlanDate);
            checkIsArrive();
            String createDate = listData.get(0).getCreatedate();
            String[] split = createDate.split("-");
            String data = split[0] + "年" + split[1] + "月" + split[2] + "日";
            tvDate.setText(String.format("%s%s", data, getString(R.string.patrol_plan)));
            showView(flContainer, swipeRefresh);
        }
    }

    @Override
    public void getPatrolPlanDataFailed() {
        swipeRefresh.setRefreshing(false);
        tvHint.setText(R.string.failure_load_click_retry);
        showView(flContainer, tvHint);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.stopLeScan(PatrolPlanFragment.this);
        }
        mHandler.removeCallbacksAndMessages(null);
        mContext.unregisterReceiver(mBtReceiver);
    }
}
