package com.winsion.dispatch.modules.operation.activity.issue;

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
import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseActivity;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.data.constants.Urls;
import com.winsion.dispatch.data.constants.ViewName;
import com.winsion.dispatch.data.entity.ResponseForQueryData;
import com.winsion.dispatch.data.listener.ResponseListener;
import com.winsion.dispatch.modules.operation.adapter.SelectTrainAdapter;
import com.winsion.dispatch.modules.operation.entity.RunEntity;
import com.winsion.dispatch.view.TitleView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 10295 on 2018/1/8.
 * 发送命令/协作中选择车次
 */

public class SelectTrainActivity extends BaseActivity implements TextWatcher {
    @BindView(R.id.tv_title)
    TitleView tvTitle;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.lv_list)
    ListView lvList;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.tv_hint)
    TextView tvHint;

    // ListView显示的数据
    private ArrayList<RunEntity> listData = new ArrayList<>();
    // 所有的车站数据
    private ArrayList<RunEntity> allData = new ArrayList<>();
    private SelectTrainAdapter mLvAdapter;

    @Override
    protected int setContentView() {
        return R.layout.activity_select_train;
    }

    @Override
    protected void start() {
        initAdapter();
        initListener();
        initData();
    }

    private void initAdapter() {
        mLvAdapter = new SelectTrainAdapter(mContext, listData);
        lvList.setAdapter(mLvAdapter);
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
                showToast(R.string.currently_no_selected_item);
            }
        });
        etSearch.addTextChangedListener(this);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

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
                            tvHint.setText(R.string.no_data_click_to_retry);
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
                        tvHint.setText(R.string.failure_load_click_retry);
                        showView(flContainer, tvHint);
                    }
                });
    }

    @OnClick(R.id.tv_hint)
    public void onViewClicked() {
        showView(flContainer, progressBar);
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetDataSource.unSubscribe(this);
    }
}
