package com.winsion.component.aad.fragment.aadlist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.winsion.component.aad.R;
import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.view.SpinnerView;

/**
 * Created by 10295 on 2018/3/23.
 * 到发列表Fragment（上行/下行）
 */

public class AADListFragment extends BaseFragment {
    private SpinnerView svSpinner;

    public static final String AAD_TYPE = "AAD_TYPE";
    /**
     * 到发-上行
     */
    public static final int TYPE_UP = 0;
    /**
     * 到发-下行
     */
    public static final int TYPE_DOWN = 1;

    private int mType = TYPE_UP;

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.aad_fragment_aad_list, null);
    }

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initView() {
        svSpinner = findViewById(R.id.sv_spinner);
    }

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getInt(AAD_TYPE, TYPE_UP);
        }
        svSpinner.setSearchContent(mType == TYPE_DOWN ? "下行" : "上行");
    }
}
