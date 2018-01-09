package com.winsion.wisdomstation.modules.operation.modules.issue.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.data.NetDataSource;
import com.winsion.wisdomstation.data.constants.Urls;
import com.winsion.wisdomstation.data.constants.ViewName;
import com.winsion.wisdomstation.data.entity.ResponseForQueryData;
import com.winsion.wisdomstation.data.listener.ResponseListener;
import com.winsion.wisdomstation.modules.operation.adapter.SelectTeamAdapter;
import com.winsion.wisdomstation.modules.operation.entity.TeamEntity;
import com.winsion.wisdomstation.view.TitleView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 10295 on 2018/1/8.
 * 发布命令/协作中选择班组
 */

public class SelectTeamActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TitleView tvTitle;
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

    // ListView数据
    private ArrayList<TeamEntity> listData = new ArrayList<>();
    private SelectTeamAdapter mLvAdapter;

    @Override
    protected int setContentView() {
        return R.layout.activity_select_team;
    }

    @Override
    protected void start() {
        initAdapter();
        initView();
        initData();
    }

    private void initAdapter() {
        mLvAdapter = new SelectTeamAdapter(mContext, listData);
        lvList.setAdapter(mLvAdapter);
    }

    private void initView() {
        tvTitle.setOnBackClickListener((View v) -> finish());
        tvTitle.setOnConfirmClickListener((View v) -> {
            if (mLvAdapter.getSelectedList().size() == 0) {
                showToast(R.string.currently_no_selected_item);
            } else {
                Intent selectData = new Intent().putExtra("selectData", mLvAdapter.getSelectedList());
                setResult(RESULT_OK, selectData);
                finish();
            }
        });
        swipeRefresh.setEnabled(false);
    }

    private void initData() {
        NetDataSource.post(getClass(), Urls.BASE_QUERY, null, null,
                ViewName.TEAMS_INFO, 1, new ResponseListener<ResponseForQueryData<List<TeamEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<TeamEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<TeamEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<TeamEntity>> result) {
                        List<TeamEntity> dataList = result.getDataList();
                        if (dataList.size() == 0) {
                            tvHint.setText(R.string.no_data_click_to_retry);
                            showView(flContainer, tvHint);
                        } else {
                            listData.clear();
                            listData.addAll(dataList);
                            mLvAdapter.notifyDataSetChanged();
                            showView(flContainer, swipeRefresh);
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
