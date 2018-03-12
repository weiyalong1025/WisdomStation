package com.winsion.component.task.activity.issue;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.constants.ViewName;
import com.winsion.component.basic.entity.ResponseForQueryData;
import com.winsion.component.basic.listener.ResponseListener;
import com.winsion.component.basic.view.TitleView;
import com.winsion.component.task.R;
import com.winsion.component.task.adapter.SelectTrainAdapter;
import com.winsion.component.task.entity.RunEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/1/8.
 * 发送命令/协作中选择车次
 */

public class SelectTrainActivity extends BaseActivity {
    private TitleView tvTitle;
    private EditText etSearch;
    private ListView lvList;
    private LinearLayout llContent;
    private ProgressBar progressBar;
    private FrameLayout flContainer;
    private TextView tvHint;

    private ArrayList<RunEntity> listData = new ArrayList<>();  // ListView显示的数据
    private ArrayList<RunEntity> allData = new ArrayList<>();   // 所有的车站数据
    private SelectTrainAdapter mLvAdapter;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String s1 = s.toString().trim().toLowerCase();
            if (TextUtils.isEmpty(s.toString())) {
                listData.clear();
                listData.addAll(allData);
                mLvAdapter.notifyDataSetChanged();
            } else {
                listData.clear();
                for (RunEntity runEntity : allData) {
                    String number = runEntity.getTrainnumber();
                    String trainNumber = number.toLowerCase();
                    if (trainNumber.startsWith(s1)) {
                        listData.add(runEntity);
                    }
                }
                mLvAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected int setContentView() {
        return R.layout.task_activity_select_train;
    }

    @Override
    protected void start() {
        initView();
        initListener();
        initAdapter();
        initData();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        etSearch = findViewById(R.id.et_search);
        lvList = findViewById(R.id.lv_list);
        llContent = findViewById(R.id.ll_content);
        progressBar = findViewById(R.id.progress_bar);
        flContainer = findViewById(R.id.fl_container);
        tvHint = findViewById(R.id.tv_hint);
    }

    private void initListener() {
        tvTitle.setOnBackClickListener((View v) -> finish());
        tvTitle.setOnConfirmClickListener(v -> {
            RunEntity selectTrainEntity = mLvAdapter.getSelectTrainEntity();
            if (selectTrainEntity != null) {
                Intent data = new Intent();
                data.putExtra("runEntity", selectTrainEntity);
                setResult(RESULT_OK, data);
                finish();
            } else {
                showToast(R.string.toast_no_selected_item);
            }
        });
        etSearch.addTextChangedListener(mTextWatcher);
        addOnClickListeners(R.id.tv_hint);
    }

    private void initAdapter() {
        mLvAdapter = new SelectTrainAdapter(mContext, listData);
        lvList.setAdapter(mLvAdapter);
    }

    private void initData() {
        NetDataSource.post(this, Urls.BASE_QUERY, null, null, ViewName.RUNS_INFO,
                1, new ResponseListener<ResponseForQueryData<List<RunEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<RunEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<RunEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<RunEntity>> result) {
                        List<RunEntity> dataList = result.getDataList();
                        if (dataList.size() == 0) {
                            tvHint.setText(R.string.hint_no_data_click_retry);
                            showView(flContainer, tvHint);
                        } else {
                            allData.clear();
                            allData.addAll(dataList);
                            listData.clear();
                            listData.addAll(dataList);
                            etSearch.setText("");
                            showView(flContainer, llContent);
                            mLvAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        tvHint.setText(R.string.hint_load_failed_click_retry);
                        showView(flContainer, tvHint);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        showView(flContainer, progressBar);
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetDataSource.unSubscribe(this);
    }
}
