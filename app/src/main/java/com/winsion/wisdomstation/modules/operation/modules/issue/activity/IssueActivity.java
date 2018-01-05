package com.winsion.wisdomstation.modules.operation.modules.issue.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.common.biz.CommonBiz;
import com.winsion.wisdomstation.media.adapter.RecordAdapter;
import com.winsion.wisdomstation.media.entity.RecordEntity;
import com.winsion.wisdomstation.modules.operation.constants.TaskType;
import com.winsion.wisdomstation.view.TitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 发布命令/协作界面
 * Created by wyl on 2016/8/1.
 */
public class IssueActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TitleView tvTitle;
    @BindView(R.id.list_view)
    ListView listView;

    private TextView tvStation;
    private TextView tvStartTime;

    private static final String ISSUE_TYPE = "issueType";
    private static final String TO_TEAM_ID = "toTeamId";
    private static final String TO_TEAM_NAME = "toTeamId";

    private List<String> stationList = new ArrayList<>();
    private ArrayList<RecordEntity> recordEntities = new ArrayList<>();
    private int issueType;
    private String toTeamId;
    private String toTeamName;
    private RecordAdapter recordAdapter;
    private int selectStationIndex;

    /**
     * @param context   上下文
     * @param issueType 发布类型(命令<COMMAND>/协作<COOPERATE>)
     *                  {@link com.winsion.wisdomstation.modules.operation.constants.TaskType}
     */
    public static void startIssueActivity(Context context, int issueType) {
        startIssueActivity(context, issueType, null, null);
    }

    /**
     * @param context    上下文
     * @param issueType  发布类型(命令<COMMAND>/协作<COOPERATE>)
     *                   {@link com.winsion.wisdomstation.modules.operation.constants.TaskType}
     * @param toTeamId   发布给班组的id，可以为空
     * @param toTeamName 发布给班组的name，可以为空
     */
    public static void startIssueActivity(Context context, int issueType, @Nullable String toTeamId, @Nullable String toTeamName) {
        Intent intent = new Intent(context, IssueActivity.class);
        intent.putExtra(ISSUE_TYPE, issueType);
        if (!TextUtils.isEmpty(toTeamId) && !TextUtils.isEmpty(toTeamName)) {
            intent.putExtra(TO_TEAM_ID, toTeamId);
            intent.putExtra(TO_TEAM_NAME, toTeamName);
        }
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_issue;
    }

    @Override
    protected void start() {
        initData();
        initHeader();
        initAdapter();
    }

    private void initData() {
        initIntentData();
        initStationData();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        issueType = intent.getIntExtra(ISSUE_TYPE, -1);
        if (issueType != TaskType.COMMAND && issueType != TaskType.COOPERATE) {
            throw new RuntimeException("The Issue type is not supported!");
        }
        switch (issueType) {
            case TaskType.COMMAND:
                tvTitle.setTitleText(getString(R.string.issue_command));
                break;
            case TaskType.COOPERATE:
                tvTitle.setTitleText(getString(R.string.issue_cooperate));
                break;
        }
        toTeamId = intent.getStringExtra(TO_TEAM_ID);
        toTeamName = intent.getStringExtra(TO_TEAM_NAME);
    }

    // 选择车站数据
    private void initStationData() {
        stationList.add(getString(R.string.station_name));
    }

    @SuppressLint("InflateParams")
    private void initHeader() {
        View issueHeader = LayoutInflater.from(mContext).inflate(R.layout.header_issue, null);

        TextView tvPerformerGroupHint = issueHeader.findViewById(R.id.tv_performer_group_hint);
        EditText etWordContent = issueHeader.findViewById(R.id.et_word_content);
        switch (issueType) {
            case TaskType.COMMAND:
                tvPerformerGroupHint.setText(R.string.command_group);
                etWordContent.setHint(R.string.command_content);
                break;
            case TaskType.COOPERATE:
                tvPerformerGroupHint.setText(R.string.cooperation_group);
                etWordContent.setHint(R.string.cooperation_content);
                break;
        }

        tvStation = issueHeader.findViewById(R.id.tv_station);
        // 选择车站点击事件
        tvStation.setOnClickListener(v -> {
            CommonBiz.hideKeyboard(v);
            OptionsPickerView optionsPickerView = CommonBiz.getMyOptionPickerBuilder(mContext,
                    (options1, options2, options3, v1) -> {
                        selectStationIndex = options1;
                        tvStation.setText(stationList.get(options1));
                    })
                    .build();
            optionsPickerView.setPicker(stationList);
            optionsPickerView.setSelectOptions(selectStationIndex);
            RelativeLayout tvTopBar = (RelativeLayout) optionsPickerView.findViewById(R.id.rv_topbar);
            ViewGroup.LayoutParams layoutParams = tvTopBar.getLayoutParams();
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.d45);
            tvTopBar.setLayoutParams(layoutParams);
            optionsPickerView.show();
        });

        tvStartTime = issueHeader.findViewById(R.id.tv_start_time);
        // 开始时间点击事件


        listView.addHeaderView(issueHeader);
    }

    private void initAdapter() {
        /*RecordEntity recordEntity = new RecordEntity();
        recordEntity.setFileType(FileType.AUDIO);
        recordEntity.setFileStatus(FileStatus.NO_UPLOAD);
        recordEntity.setFileName("VIO_201801051731.wav");
        recordEntities.add(recordEntity);

        RecordEntity recordEntity1 = new RecordEntity();
        recordEntity1.setFileType(FileType.PICTURE);
        recordEntity1.setFileStatus(FileStatus.UPLOADING);
        recordEntity1.setFileName("PIC_201801051732.png");
        recordEntity1.setProgress(20);
        recordEntities.add(recordEntity1);*/

        recordAdapter = new RecordAdapter(mContext, recordEntities);
        listView.setAdapter(recordAdapter);
    }
}
