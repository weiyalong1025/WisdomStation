package com.winsion.component.contact.fragment.contact;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.utils.PinyinUtils;
import com.winsion.component.basic.view.SpinnerView;
import com.winsion.component.basic.view.expand.ActionSlideExpandableListView;
import com.winsion.component.contact.R;
import com.winsion.component.contact.adapter.ContactListAdapter;
import com.winsion.component.contact.constants.ContactType;
import com.winsion.component.contact.entity.ContactEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 10295 on 2018/3/1.
 * 联系人Fragment
 */

public class ContactFragment extends BaseFragment implements ContactContract.View,
        SpinnerView.AfterTextChangeListener {
    private SpinnerView svSpinner;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvHint;
    private FrameLayout flContainer;
    private ActionSlideExpandableListView lvList;
    private View viewShade;

    public static final String CONTACT_TYPE = "CONTACT_TYPE";
    /**
     * 请求数据中
     */
    public static final int GETTING = 0;
    /**
     * 请求数据成功
     */
    public static final int GET_SUCCESS = 1;
    /**
     * 请求数据失败
     */
    public static final int GET_FAILED = 2;

    private int getDataState = GETTING;   // 请求数据状态

    private ContactContract.Presenter mPresenter;
    private ArrayList<ContactEntity> mListData = new ArrayList<>();    // 列表数据
    private ArrayList<ContactEntity> mAllData = new ArrayList<>();   // 所有的数据
    private ContactListAdapter mLvAdapter;
    private String lastText = "";    // 搜索框中上一次输入的文字

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
        lvList = findViewById(R.id.ase_list);
        viewShade = findViewById(R.id.view_shade);

        swipeRefresh.setColorSchemeResources(R.color.basic_blue1);

        // 初始化车站选项
        List<String> stationList = new ArrayList<>();
        stationList.add(getString(R.string.spinner_station_name));
        svSpinner.setFirstOptionData(stationList);
    }

    private void initListener() {
        swipeRefresh.setOnRefreshListener(this::initData);
        svSpinner.setPopupDisplayChangeListener(status -> {
            switch (status) {
                case SpinnerView.PopupState.POPUP_SHOW:
                    viewShade.setVisibility(View.VISIBLE);
                    break;
                case SpinnerView.PopupState.POPUP_HIDE:
                    viewShade.setVisibility(View.GONE);
                    break;
            }
        });
        svSpinner.setAfterTextChangeListener(this);
        svSpinner.setFirstOptionItemClickListener(position -> {
            showView(flContainer, progressBar);
            initData();
        });
        addOnClickListeners(R.id.tv_hint);
    }

    private void initPresenter() {
        mPresenter = new ContactPresenter(this);

        mLvAdapter = new ContactListAdapter(mContext, mListData);
        lvList.setAdapter(mLvAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLvAdapter.notifyDataSetChanged();
    }

    private void initData() {
        getDataState = GETTING;
        Bundle arguments = getArguments();
        int contactType;
        if (arguments != null) {
            contactType = arguments.getInt(CONTACT_TYPE);
        } else {
            contactType = ContactType.TYPE_CONTACTS;
        }
        mPresenter.getContactData(contactType);
    }

    private Comparator<ContactEntity> mComparator = (o1, o2) -> {
        int result;
        String username1 = o1.getConName();
        String username2 = o2.getConName();
        String loginStatus1 = o1.getConLoginState();
        String loginStatus2 = o2.getConLoginState();
        if (!TextUtils.equals(loginStatus1, loginStatus2)) {
            result = Integer.valueOf(loginStatus1) - Integer.valueOf(loginStatus2);
        } else {
            String s1 = PinyinUtils.toPinYin(username1, "", PinyinUtils.Type.TYPE_LOWERCASE);
            String s2 = PinyinUtils.toPinYin(username2, "", PinyinUtils.Type.TYPE_LOWERCASE);
            result = s1.compareTo(s2);
        }
        return result;
    };

    @Override
    public void afterTextChange(Editable s) {
        if (getDataState != GET_SUCCESS) {
            return;
        }
        lastText = s.toString().trim();
        mListData.clear();
        if (TextUtils.isEmpty(lastText)) {
            mAllData.addAll(mListData);
            mListData.addAll(mAllData);
            Collections.sort(mListData, mComparator);
            mLvAdapter.notifyDataSetChanged();

            if (mListData.size() == 0) {
                tvHint.setText(R.string.hint_no_data_click_retry);
                showView(flContainer, tvHint);
            } else {
                showView(flContainer, swipeRefresh);
            }
        } else {
            String filterStr = lastText.toLowerCase();
            for (ContactEntity contactEntity : mAllData) {
                String str = contactEntity.getConName();
                if (str != null && str.contains(filterStr)) {
                    mListData.add(contactEntity);
                }
            }
            Collections.sort(mListData, mComparator);
            mLvAdapter.notifyDataSetChanged();

            if (mListData.size() == 0) {
                tvHint.setText(R.string.hint_no_data_click_retry);
                showView(flContainer, tvHint);
            } else {
                showView(flContainer, swipeRefresh);
            }
        }
    }

    @Override
    public void onClick(View view) {
        showView(flContainer, progressBar);
        initData();
    }

    @Override
    public void getContactsDataSuccess(List<? extends ContactEntity> contactEntities) {
        getDataState = GET_SUCCESS;
        swipeRefresh.setRefreshing(false);
        mAllData.clear();
        mAllData.addAll(contactEntities);

        svSpinner.setSearchContent(lastText);
    }

    @Override
    public void getContactsDataFailed() {
        getDataState = GET_FAILED;
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
