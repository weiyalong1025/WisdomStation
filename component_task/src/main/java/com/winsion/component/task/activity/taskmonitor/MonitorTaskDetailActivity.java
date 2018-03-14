package com.winsion.component.task.activity.taskmonitor;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.view.TitleView;
import com.winsion.component.media.adapter.RecordAdapter;
import com.winsion.component.media.entity.LocalRecordEntity;
import com.winsion.component.task.R;
import com.winsion.component.task.constants.TaskType;
import com.winsion.component.task.entity.TaskEntity;

import java.util.ArrayList;
import java.util.List;

import static com.winsion.component.task.constants.Intents.MontorTaskDetail.TASK_ENTITY;

/**
 * Created by 10295 on 2018/3/13.
 * 任务监控人任务详情Activity
 * 协作/命令/任务/网格/预案
 * <p>
 * 先用taskId查出JobEntity，用JobEntity的jobsId查询发布人上传的附件
 */

public class MonitorTaskDetailActivity extends BaseActivity implements MonitorTaskDetailContract.View {
    private TitleView tvTitle;
    private TextView tvNumber;
    private TextView tvStartStationName;
    private TextView tvTrainStatus;
    private TextView tvEndStationName;
    private TextView tvTrack;
    private TextView tvPlatform;
    private TextView tvPlanArrive;
    private TextView tvPlanDepart;
    private TextView tvWaitRoom;
    private TextView tvRealArrive;
    private TextView tvRealDepart;
    private TextView tvCheckPort;
    private LinearLayout llTrainModule;
    private RelativeLayout rlOrderModule;
    private ListView lvRecordPublisher;
    private TextView tvMonitorGroupHint;
    private TextView tvMonitorTeam;
    private TextView tvPerformerGroupHint;
    private TextView tvPerformerTeam;
    private TextView tvTitle1;
    private TextView tvTrainNumber;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private EditText etContent;
    private ListView lvMonitorOperation;

    private MonitorTaskDetailContract.Presenter mPresenter;
    private TaskEntity mTaskEntity;
    private List<LocalRecordEntity> publisherRecordEntities = new ArrayList<>();    // 命令/协作发布人上传附件集合
    private RecordAdapter publisherRecordAdapter;   // 命令/协作发布人上传附件列表Adapter(用于命令/协作)

    @Override
    protected int setContentView() {
        return R.layout.task_activity_monitor_task_detail;
    }

    @Override
    protected void start() {
        initView();
        initPresenter();
        initAdapter();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvNumber = findViewById(R.id.tv_number);
        tvStartStationName = findViewById(R.id.tv_startStationName);
        tvTrainStatus = findViewById(R.id.tv_train_status);
        tvEndStationName = findViewById(R.id.tv_endStationName);
        tvTrack = findViewById(R.id.tv_track);
        tvPlatform = findViewById(R.id.tv_platform);
        tvPlanArrive = findViewById(R.id.tv_plan_arrive);
        tvPlanDepart = findViewById(R.id.tv_plan_depart);
        tvWaitRoom = findViewById(R.id.tv_wait_room);
        tvRealArrive = findViewById(R.id.tv_real_arrive);
        tvRealDepart = findViewById(R.id.tv_real_depart);
        tvCheckPort = findViewById(R.id.tv_check_port);
        llTrainModule = findViewById(R.id.ll_train_module);
        rlOrderModule = findViewById(R.id.rl_order_module);
        lvRecordPublisher = findViewById(R.id.lv_record_publisher);
        tvMonitorGroupHint = findViewById(R.id.tv_monitor_group_hint);
        tvMonitorTeam = findViewById(R.id.tv_monitor_team);
        tvPerformerGroupHint = findViewById(R.id.tv_performer_group_hint);
        tvPerformerTeam = findViewById(R.id.tv_performer_team);
        tvTitle1 = findViewById(R.id.tv_title1);
        tvTrainNumber = findViewById(R.id.tv_train_number);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        etContent = findViewById(R.id.et_content);
        lvMonitorOperation = findViewById(R.id.list_view);
    }

    private void initPresenter() {
        mPresenter = new MonitorTaskDetailPresenter();
        mTaskEntity = (TaskEntity) getIntent().getSerializableExtra(TASK_ENTITY);
    }

    private void initAdapter() {
        int taskType = mTaskEntity.getTaktype();
        if (taskType == TaskType.COMMAND || taskType == TaskType.COOPERATE || taskType == TaskType.GRID)
            initMonitorRecordAdapter();
    }

    /**
     * 初始化作业监控人附件列表Adapter
     */
    private void initMonitorRecordAdapter() {
        /*// 上传附件列表adapter
        publisherRecordAdapter = new RecordAdapter(mContext, publisherRecordEntities);
        // 设置下载文件具体操作
        publisherRecordAdapter.setDownloadPerformer(localRecordEntity -> {
            try {
                String userId = CacheDataSource.getUserId();
                String jobOperatorsId = mTaskEntity.getJoboperatorsid();
                File publisherDir = DirAndFileUtils.getMonitorDir(userId, jobOperatorsId);
                mPresenter.download(localRecordEntity.getServerUri(), publisherDir.getAbsolutePath(), this);
            } catch (IOException e) {
                showToast(R.string.toast_check_sdcard);
            }
        });
        int taskType = mJobEntity.getTaktype();
        ListView listView = taskType == TaskType.COMMAND || taskType == TaskType.COOPERATE ?
                lvRecordPublisher : lvRecordPublisherGrid;
        listView.setAdapter(publisherRecordAdapter);*/
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
