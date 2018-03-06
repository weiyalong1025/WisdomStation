package com.winsion.dispatch.modules.contacts.activity.contacts;

import android.annotation.SuppressLint;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.dispatch.R;
import com.winsion.component.basic.base.BaseFragment;
import com.winsion.dispatch.view.SpinnerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/3/1.
 * 联系人Fragment
 */

public class ContactsFragment extends BaseFragment {
    private SpinnerView svSpinner;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvHint;
    private FrameLayout flContainer;
    private ListView lvList;

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_contacts, null);
    }

    @Override
    protected void init() {
        initView();
    }

    private void initView() {
        svSpinner = findViewById(R.id.sv_spinner);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progress_bar);
        tvHint = findViewById(R.id.tv_hint);
        flContainer = findViewById(R.id.fl_container);
        lvList = findViewById(R.id.lv_list);

        swipeRefresh.setColorSchemeResources(R.color.basic_blue1);

        // 初始化车站选项
        List<String> stationList = new ArrayList<>();
        stationList.add(getString(R.string.spinner_station_name));
        svSpinner.setFirstOptionData(stationList);
    }
}
