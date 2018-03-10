package com.winsion.component.task.activity.issue;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.data.constants.Urls;
import com.winsion.component.basic.data.constants.ViewName;
import com.winsion.component.basic.data.entity.ResponseForQueryData;
import com.winsion.component.basic.data.listener.ResponseListener;
import com.winsion.component.basic.view.TitleView;
import com.winsion.component.task.R;
import com.winsion.component.task.adapter.SelectTeamAdapter;
import com.winsion.component.task.entity.TeamEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.winsion.component.task.constants.Intents.Issue.SELECT_TEAM;

/**
 * Created by 10295 on 2018/1/8.
 * 发布命令/协作中选择班组
 */

public class SelectTeamActivity extends BaseActivity {
    private TitleView tvTitle;
    private ListView lvList;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvHint;
    private FrameLayout flContainer;

    private ArrayList<TeamEntity> listData = new ArrayList<>(); // ListView数据
    private SelectTeamAdapter mLvAdapter;

    @Override
    protected int setContentView() {
        return R.layout.task_activity_select_team;
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
        lvList = findViewById(R.id.lv_list);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progress_bar);
        tvHint = findViewById(R.id.tv_hint);
        flContainer = findViewById(R.id.fl_container);
    }

    private void initListener() {
        tvTitle.setOnBackClickListener((View v) -> finish());
        tvTitle.setOnConfirmClickListener((View v) -> {
            if (mLvAdapter.getSelectedList().size() == 0) {
                showToast(R.string.toast_no_selected_item);
            } else {
                Intent intent = new Intent();
                intent.putExtra(SELECT_TEAM, mLvAdapter.getSelectedList());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        addOnClickListeners(R.id.tv_hint);
        swipeRefresh.setEnabled(false);
    }

    private void initAdapter() {
        mLvAdapter = new SelectTeamAdapter(mContext, listData);
        lvList.setAdapter(mLvAdapter);
    }

    private void initData() {
        NetDataSource.post(this, Urls.BASE_QUERY, null, null,
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
                            tvHint.setText(R.string.hint_no_data_click_retry);
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
