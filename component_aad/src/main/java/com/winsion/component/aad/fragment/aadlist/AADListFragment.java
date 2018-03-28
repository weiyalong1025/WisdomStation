package com.winsion.component.aad.fragment.aadlist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.component.aad.R;
import com.winsion.component.aad.adapter.AADListAdapter;
import com.winsion.component.aad.constants.AADType;
import com.winsion.component.aad.entity.AADEntity;
import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.view.SpinnerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/3/23.
 * 到发列表Fragment（上行/下行）
 */

public class AADListFragment extends BaseFragment implements SpinnerView.AfterTextChangeListener,
        AdapterView.OnItemClickListener, AADListContract.View {
    private SpinnerView svSpinner;
    private ProgressBar progressBar;
    private TextView tvHint;
    private FrameLayout flContainer;
    private ListView lvList;
    private TextView trainNumberIndex;
    private ImageView ivShade;
    private SwipeRefreshLayout swipeRefresh;

    public static final String AAD_TYPE = "AAD_TYPE";

    private int mType = AADType.TYPE_UP;    // 表示当前界面是上行还是下行
    private AADListContract.Presenter mPresenter;
    private String lastText = "";    // 搜索框中上一次输入的文字
    private List<AADEntity> mListData = new ArrayList<>();    // 列表数据
    private List<AADEntity> mAllData = new ArrayList<>();   // 所有的数据
    private AADListAdapter mAdapter;

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.aad_fragment_aad_list, null);
    }

    @Override
    protected void init() {
        initView();
        initIntentData();
        initListener();
        initData();
    }

    private void initView() {
        svSpinner = findViewById(R.id.sv_spinner);
        ivShade = findViewById(R.id.iv_shade);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progress_bar);
        tvHint = findViewById(R.id.tv_hint);
        flContainer = findViewById(R.id.fl_container);
        lvList = findViewById(R.id.lv_list);
        trainNumberIndex = findViewById(R.id.train_number_index);
    }

    private void initIntentData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getInt(AAD_TYPE, AADType.TYPE_UP);
        }
    }

    private void initListener() {
        swipeRefresh.setOnRefreshListener(() -> mPresenter.getAADListData(mType));
        lvList.setOnItemClickListener(this);
        // lvList.setOnScrollListener(this);
        svSpinner.setAfterTextChangeListener(this);

        svSpinner.setFirstOptionItemClickListener((position) -> {
            showView(flContainer, progressBar);
            mPresenter.getAADListData(mType);
        });

        // 根据Spinner显示状态显隐透明背景
        svSpinner.setPopupDisplayChangeListener(status -> {
            switch (status) {
                case SpinnerView.PopupState.POPUP_SHOW:
                    ivShade.setVisibility(View.VISIBLE);
                    break;
                case SpinnerView.PopupState.POPUP_HIDE:
                    ivShade.setVisibility(View.GONE);
                    break;
            }
        });

        addOnClickListeners(R.id.tv_hint);
    }

    private void initData() {
        // 初始化车站选项
        List<String> stationList = new ArrayList<>();
        stationList.add(getString(R.string.spinner_station_name));
        svSpinner.setFirstOptionData(stationList);

        mPresenter = new AADListPresenter(this);

        mAdapter = new AADListAdapter(mContext, mListData);
        lvList.setAdapter(mAdapter);

        mPresenter.getAADListData(mType);
    }

    @Override
    public void afterTextChange(Editable s) {
        lastText = s.toString().trim();
        mListData.clear();
        if (TextUtils.isEmpty(lastText)) {
            mListData.addAll(mAllData);
            mAdapter.notifyDataSetChanged();

            if (mListData.size() == 0) {
                tvHint.setText(R.string.hint_no_data_click_retry);
                showView(flContainer, tvHint);
            } else {
                showView(flContainer, swipeRefresh);
            }
        } else {
            String filterStr = lastText.toLowerCase();
            for (AADEntity aadEntity : mAllData) {
                String number = aadEntity.getTrainNumber();
                number = TextUtils.isEmpty(number) ? getString(R.string.value_nothing) : number;
                String trainNumber = number.toLowerCase();
                if (trainNumber.contains(filterStr)) {
                    mListData.add(aadEntity);
                }
            }
            mAdapter.notifyDataSetChanged();

            if (mListData.size() == 0) {
                tvHint.setText(R.string.hint_no_data_click_retry);
                showView(flContainer, tvHint);
            } else {
                showView(flContainer, swipeRefresh);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showToast("点击了");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (trainNumberIndex.getVisibility() == View.VISIBLE) {
            trainNumberIndex.setVisibility(View.GONE);
        }
    }

    @Override
    public void getAADListDataSuccess(List<AADEntity> aadEntities) {
        swipeRefresh.setRefreshing(false);
        mAllData.clear();
        mAllData.addAll(aadEntities);

        svSpinner.setSearchContent(lastText);
    }

    @Override
    public void getAADListDataFailed() {
        swipeRefresh.setRefreshing(false);
        tvHint.setText(getString(R.string.hint_load_failed_click_retry));
        showView(flContainer, tvHint);
    }

    @Override
    public void onClick(View v) {
        showView(flContainer, progressBar);
        mPresenter.getAADListData(mType);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}
