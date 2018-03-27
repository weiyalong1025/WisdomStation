package com.winsion.component.contact.fragment.contacts;

import android.annotation.SuppressLint;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.view.SpinnerView;
import com.winsion.component.contact.R;
import com.winsion.component.contact.adapter.ContactsListAdapter;
import com.winsion.component.contact.entity.ContactEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/3/1.
 * 联系人Fragment
 */

public class ContactsFragment extends BaseFragment implements ContactsContract.View, SpinnerView.AfterTextChangeListener {
    private SpinnerView svSpinner;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvHint;
    private FrameLayout flContainer;
    private ListView lvList;
    private ImageView ivShade;

    private ContactsContract.Presenter mPresenter;
    private List<ContactEntity> mListData = new ArrayList<>();
    private ContactsListAdapter mLvAdapter;

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.contact_fragment_contacts, null);
    }

    @Override
    protected void init() {
        initView();
        initListener();
        initPresenter();
        initData();
    }

    private void initView() {
        svSpinner = findViewById(R.id.sv_spinner);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progress_bar);
        tvHint = findViewById(R.id.tv_hint);
        flContainer = findViewById(R.id.fl_container);
        lvList = findViewById(R.id.lv_list);
        ivShade = findViewById(R.id.iv_shade);

        swipeRefresh.setColorSchemeResources(R.color.basic_blue1);

        // 初始化车站选项
        List<String> stationList = new ArrayList<>();
        stationList.add(getString(R.string.spinner_station_name));
        svSpinner.setFirstOptionData(stationList);
    }

    private void initListener() {
        swipeRefresh.setOnRefreshListener(() -> mPresenter.getContactsData());
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
        svSpinner.setAfterTextChangeListener(this);
        svSpinner.setFirstOptionItemClickListener(position -> {
            showView(flContainer, progressBar);
            mPresenter.getContactsData();
        });
        addOnClickListeners(R.id.tv_hint);
    }

    private void initPresenter() {
        mPresenter = new ContactsPresenter(this);
    }

    private void initData() {
        mLvAdapter = new ContactsListAdapter(mContext, mListData);
        lvList.setAdapter(mLvAdapter);
        mPresenter.getContactsData();
    }

    @Override
    public void afterTextChange(Editable s) {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        showView(flContainer, progressBar);
        mPresenter.getContactsData();
    }

    @Override
    public void getContactsDataSuccess(List<ContactEntity> contactEntities) {
        swipeRefresh.setRefreshing(false);
        if (contactEntities.size() == 0) {
            tvHint.setText(R.string.hint_no_data_click_retry);
            showView(flContainer, tvHint);
        } else {
            mListData.clear();
            mListData.addAll(contactEntities);
            mLvAdapter.notifyDataSetChanged();
            showView(flContainer, swipeRefresh);
        }
    }

    @Override
    public void getContactsDataFailed() {
        swipeRefresh.setRefreshing(false);
        tvHint.setText(R.string.hint_load_failed_click_retry);
        showView(flContainer, tvHint);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}
