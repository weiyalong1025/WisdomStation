package com.winsion.component.task.activity.patrolplan;

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

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.utils.ViewUtils;
import com.winsion.component.basic.view.TitleView;
import com.winsion.component.basic.view.WrapContentListView;
import com.winsion.component.task.R;
import com.winsion.component.task.activity.patrolitem.PatrolItemActivity;
import com.winsion.component.task.adapter.BluetoothPointAdapter;
import com.winsion.component.task.adapter.PatrolPlanAdapter;
import com.winsion.component.task.entity.BPEntity;
import com.winsion.component.task.entity.PatrolPlanEntity;
import com.winsion.component.task.utils.IbeaconUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.winsion.component.task.constants.Intents.PatrolItem.PATROL_TASK_ENTITY;

/**
 * Created by 10295 on 2017/12/26.
 * 巡检任务以及界面
 * TODO 蓝牙需要动态权限
 */
public class PatrolPlanActivity extends BaseActivity implements PatrolPlanContract.View, AdapterView.OnItemClickListener {
    private TextView tvDate;
    private ListView lvList;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvHint;
    private FrameLayout flContainer;

    private static final int REQUEST_ENABLE_BT = 100;

    private PatrolPlanContract.Presenter mPresenter;
    private PatrolPlanAdapter mLvAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothPointAdapter mBluetoothPointAdapter;
    private long lastUpdateTime;

    private List<PatrolPlanEntity> listData = new ArrayList<>();
    private HashMap<String, Long> btMap = new HashMap<>();
    private ArrayList<BPEntity> BPEntities = new ArrayList<>();
    private MyScanCallback mScanCallback;

    private static class MyScanCallback implements BluetoothAdapter.LeScanCallback {
        private WeakReference<PatrolPlanActivity> mActivity;

        MyScanCallback(PatrolPlanActivity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanData) {
            mActivity.get().scanResult(device, rssi, scanData);
        }
    }

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
                            startScan();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            logI("onReceive---------STATE_TURNING_OFF");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            stopScan();
                            logI("onReceive---------STATE_OFF");
                            break;
                    }
                    break;
            }
        }
    };

    @Override
    protected int setContentView() {
        return R.layout.task_fragment_patrol_plan;
    }

    @Override
    protected void start() {
        initPresenter();
        initView();
        initAdapter();
        initBluetooth();
        initListener();
        initData();
    }

    private void initPresenter() {
        mPresenter = new PatrolPlanPresenter(this);
    }

    private void initView() {
        ((TitleView) findViewById(R.id.tv_title)).setOnBackClickListener(v -> finish());
        tvDate = findViewById(R.id.tv_date);
        lvList = findViewById(R.id.lv_list);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progress_bar);
        tvHint = findViewById(R.id.tv_hint);
        flContainer = findViewById(R.id.fl_container);

        swipeRefresh.setColorSchemeResources(R.color.basic_blue1);
    }

    private void initAdapter() {
        mLvAdapter = new PatrolPlanAdapter(mContext, listData);
        lvList.setAdapter(mLvAdapter);
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler.sendEmptyMessage(0);

        // 注册监听蓝牙状态改变的广播
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBtReceiver, filter);

        mScanCallback = new MyScanCallback(this);

        // 开始扫描
        startScan();
    }

    private void startScan() {
        if (mBluetoothAdapter == null) {
            showToast(R.string.toast_bluetooth_not_support);
        } else if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startLeScan(mScanCallback);
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void stopScan() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.stopLeScan(mScanCallback);
        }
    }

    private void scanResult(BluetoothDevice device, int rssi, byte[] scanData) {
        IbeaconUtils.iBeacon ibeacon = IbeaconUtils.fromScanData(device, rssi, scanData);
        if (ibeacon != null) {
            String btAddress = ibeacon.bluetoothAddress;
            long lastTime = System.currentTimeMillis();
            btMap.put(btAddress, lastTime);
            BPEntity BPEntity = new BPEntity();
            BPEntity.setBluetoothId(btAddress);
            BPEntity.setLastTime(lastTime);
            if ((System.currentTimeMillis() - lastUpdateTime) > 1000 * 5) {
                updateOrAddBluetoothPoint(BPEntity);
            }
        }
    }

    private void updateOrAddBluetoothPoint(BPEntity BPEntity) {
        boolean isUpdate = false;
        for (BPEntity point : BPEntities) {
            if (equals(point.getBluetoothId(), BPEntity.getBluetoothId())) {
                BPEntities.remove(point);
                BPEntities.add(BPEntity);
                isUpdate = true;
                break;
            }
        }
        if (!isUpdate) {
            BPEntities.add(BPEntity);
        }
        Collections.sort(BPEntities, (o1, o2) -> (int) (o2.getLastTime() - o1.getLastTime()));
        if (mBluetoothPointAdapter != null) {
            mBluetoothPointAdapter.notifyDataSetChanged();
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED && requestCode == REQUEST_ENABLE_BT) {
            showToast(R.string.toast_open_bluetooth_failed);
        }
    }

    private void initListener() {
        EventBus.getDefault().register(this);
        swipeRefresh.setOnRefreshListener(this::initData);
        lvList.setOnItemClickListener(this);

        addOnClickListeners(R.id.iv_bluetooth, R.id.tv_hint);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PatrolPlanEntity patrolPlanEntity) {
        // 二级界面(PatrolItemActivity)更改了数据，同步该页面状态
        int positionInList = BasicBiz.halfSearch(listData, patrolPlanEntity);
        if (positionInList != -1) {
            listData.remove(positionInList);
            listData.add(positionInList, patrolPlanEntity);
            mLvAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PatrolPlanEntity patrolPlanEntity = listData.get(position);
        if (patrolPlanEntity.isArrive() || CacheDataSource.getTestMode()) {
            Intent intent = new Intent(mContext, PatrolItemActivity.class);
            intent.putExtra(PATROL_TASK_ENTITY, patrolPlanEntity);
            startActivity(intent);
        }
    }

    private void initData() {
        mPresenter.getPatrolPlanData();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_bluetooth) {
            @SuppressLint("InflateParams")
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.task_item_bluetooth_info, null);
            inflate.measure(0, 0);
            int suggestMaxHeight = ViewUtils.getSuggestMaxHeight(mContext, inflate.getMeasuredHeight());
            ListView listView = new WrapContentListView(getContext(), suggestMaxHeight);
            mBluetoothPointAdapter = new BluetoothPointAdapter(mContext, BPEntities);
            listView.setAdapter(mBluetoothPointAdapter);
            new AlertDialog.Builder(mContext)
                    .setView(listView)
                    .setCancelable(true)
                    .show();
        } else if (id == R.id.tv_hint) {
            initData();
            showView(flContainer, progressBar);
        }
    }

    @Override
    public void getPatrolPlanDataSuccess(List<PatrolPlanEntity> patrolPlanDate) {
        swipeRefresh.setRefreshing(false);
        if (patrolPlanDate.size() == 0) {
            tvHint.setText(R.string.hint_no_data_click_retry);
            showView(flContainer, tvHint);
        } else {
            listData.clear();
            listData.addAll(patrolPlanDate);
            checkIsArrive();
            String createDate = listData.get(0).getCreatedate();
            String[] split = createDate.split("-");
            String data = split[0] + "年" + split[1] + "月" + split[2] + "日";
            tvDate.setText(String.format("%s%s", data, getString(R.string.title_patrol_plan)));
            showView(flContainer, swipeRefresh);
        }
    }

    @Override
    public void getPatrolPlanDataFailed() {
        swipeRefresh.setRefreshing(false);
        tvHint.setText(R.string.hint_load_failed_click_retry);
        showView(flContainer, tvHint);
    }

    @Override
    public void handlerMessage(Message msg) {
        super.handlerMessage(msg);
        if (msg.what == 0) {
            checkIsArrive();
            mHandler.sendEmptyMessageDelayed(0, 1000 * 5);
        }
    }

    private void checkIsArrive() {
        for (PatrolPlanEntity patrolTaskDto : listData) {
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

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScan();
        unregisterReceiver(mBtReceiver);
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        mPresenter.exit();
    }
}
