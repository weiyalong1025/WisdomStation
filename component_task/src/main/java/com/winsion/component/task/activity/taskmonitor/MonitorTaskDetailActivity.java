package com.winsion.component.task.activity.taskmonitor;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.listener.MyDownloadListener;
import com.winsion.component.basic.utils.ConvertUtils;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.ViewUtils;
import com.winsion.component.basic.view.TitleView;
import com.winsion.component.media.adapter.RecordAdapter;
import com.winsion.component.media.constants.FileStatus;
import com.winsion.component.media.entity.LocalRecordEntity;
import com.winsion.component.media.entity.ServerRecordEntity;
import com.winsion.component.task.R;
import com.winsion.component.task.activity.scenerecord.SceneRecordActivity;
import com.winsion.component.task.adapter.MonitorOperationAdapter;
import com.winsion.component.task.biz.TaskBiz;
import com.winsion.component.task.constants.Intents;
import com.winsion.component.task.constants.RunState;
import com.winsion.component.task.constants.TaskType;
import com.winsion.component.task.constants.TrainState;
import com.winsion.component.task.entity.JobEntity;
import com.winsion.component.task.entity.TaskEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.winsion.component.task.constants.Intents.MontorTaskDetail.TASK_ENTITY;

/**
 * Created by 10295 on 2018/3/13.
 * 任务监控人任务详情Activity
 * 协作/命令/任务/网格/预案
 * <p>
 * 先用taskId查出JobEntity，用JobEntity的jobsId查询发布人上传的附件
 */

public class MonitorTaskDetailActivity extends BaseActivity implements MonitorTaskDetailContract.View, AdapterView.OnItemClickListener {
    private TitleView tvTitle;
    private FrameLayout flContainer;
    private ScrollView svContent;
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
    private ProgressBar pbLoading;
    private TextView tvHint;

    private MonitorTaskDetailContract.Presenter mPresenter;
    private TaskEntity mTaskEntity;
    private List<LocalRecordEntity> publisherRecordEntities = new ArrayList<>();    // 命令/协作发布人上传附件集合
    private RecordAdapter publisherRecordAdapter;   // 命令/协作发布人上传附件列表Adapter(用于命令/协作)
    private MonitorOperationAdapter monitorOperationAdapter;    // 作业列表Adapter
    private List<JobEntity> mJobEntities;    // 任务下的作业数据

    // 定时刷新器(刷新任务执行时间)
    private Disposable timer;
    private String jobsId;

    @Override
    protected int setContentView() {
        return R.layout.task_activity_monitor_task_detail;
    }

    @Override
    protected void start() {
        initView();
        initPresenter();
        initData();
        initListener();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        flContainer = findViewById(R.id.fl_container);
        svContent = findViewById(R.id.sv_content);
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
        lvMonitorOperation = findViewById(R.id.lv_monitor_operation);
        pbLoading = findViewById(R.id.progress_bar);
        tvHint = findViewById(R.id.tv_hint);
    }

    private void initPresenter() {
        mPresenter = new MonitorTaskDetailPresenter(this);
        mTaskEntity = (TaskEntity) getIntent().getSerializableExtra(TASK_ENTITY);
    }

    private void initData() {
        // 获取任务下相关作业信息
        mPresenter.getTaskDetailInfo(mTaskEntity.getTasksid());
        // 根据任务类型设置界面标题
        int taskType = mTaskEntity.getTaktype();
        if (taskType == TaskType.PLAN) {
            tvTitle.setTitleText(R.string.title_alarm_detail);
        } else if (taskType == TaskType.COOPERATE || taskType == TaskType.COMMAND) {
            tvTitle.setTitleText(taskType == TaskType.COOPERATE ? R.string.title_cooperation_detail : R.string.title_command_detail);
        } else {
            tvTitle.setTitleText(R.string.title_task_detail);
        }
    }

    private void initListener() {
        addOnClickListeners(R.id.tv_hint);
        tvTitle.setOnBackClickListener(v -> finish());
    }

    @Override
    public void onClick(View view) {
        showView(flContainer, pbLoading);
        mPresenter.getTaskDetailInfo(mTaskEntity.getTasksid());
    }

    @Override
    public void getTaskDetailInfoSuccess(List<JobEntity> dataList) {
        this.mJobEntities = dataList;
        initViewModule();
        initAdapter();
        showView(flContainer, svContent);
    }

    @Override
    public void getTaskDetailInfoFailed() {
        tvHint.setText(R.string.hint_load_failed_click_retry);
        showView(flContainer, tvHint);
    }

    /**
     * 根据任务类型显示不同的VIEW模块
     */
    private void initViewModule() {
        JobEntity jobEntity = mJobEntities.get(0);
        int taskType = mTaskEntity.getTaktype();
        if (taskType == TaskType.PLAN) {
            rlOrderModule.setVisibility(View.GONE);
            llTrainModule.setVisibility(View.GONE);
        } else if (taskType == TaskType.COOPERATE || taskType == TaskType.COMMAND) {
            rlOrderModule.setVisibility(View.VISIBLE);
            llTrainModule.setVisibility(View.GONE);
            initOrderModuleView(jobEntity);
        } else {
            rlOrderModule.setVisibility(View.GONE);
            llTrainModule.setVisibility(View.VISIBLE);
            initTrainModuleView(jobEntity);
        }
    }

    /**
     * 初始化命令/协作模块数据
     */
    private void initOrderModuleView(JobEntity jobEntity) {
        int taskType = jobEntity.getTaktype();
        boolean isCommand = taskType == TaskType.COMMAND;
        tvMonitorGroupHint.setText(isCommand ? R.string.name_issue_order_group : R.string.name_issue_cooperation_group);
        tvPerformerGroupHint.setText(isCommand ? R.string.name_order_group : R.string.name_cooperation_group);
        tvMonitorTeam.setText(jobEntity.getMonitorteamname());
        tvPerformerTeam.setText(jobEntity.getOpteamname());
        tvTitle1.setText(jobEntity.getTaskname());
        String trainNumber = jobEntity.getTrainnumber();
        trainNumber = TextUtils.isEmpty(trainNumber) ? getString(R.string.value_nothing) : trainNumber;
        tvTrainNumber.setText(trainNumber);
        tvStartTime.setText(jobEntity.getPlanstarttime());
        tvEndTime.setText(jobEntity.getPlanendtime());

        String prefix = isCommand ? getString(R.string.name_command_content) + "  " : getString(R.string.name_cooperation_content) + "  ";
        ForegroundColorSpan gray = new ForegroundColorSpan(0xFF69696D);
        SpannableStringBuilder builder = new SpannableStringBuilder()
                .append(prefix)
                .append(jobEntity.getWorkcontent());
        builder.setSpan(gray, 0, prefix.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        etContent.setText(builder);
    }

    /**
     * 初始化车次模块数据
     */
    private void initTrainModuleView(JobEntity jobEntity) {
        String trainNumber = jobEntity.getTrainnumber();
        int color = 0xFFFFFFFF;
        switch (jobEntity.getTrainlate()) {
            case RunState.LATE:
                trainNumber = trainNumber + getString(R.string.value_late);
                color = 0xFFE24D46;
                break;
            case RunState.LATE_UNSURE:
                trainNumber = trainNumber + getString(R.string.value_late_unsure);
                color = 0xFFE24D46;
                break;
            case RunState.STOP:
                trainNumber = trainNumber + getString(R.string.value_stop_run);
                color = 0xFFE24D46;
                break;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(trainNumber);
        ForegroundColorSpan statusColor = new ForegroundColorSpan(color);
        builder.setSpan(statusColor, jobEntity.getTrainnumber().length(), trainNumber.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvNumber.setText(builder);

        int trainStatus = jobEntity.getTrainstatus();
        switch (trainStatus) {
            case TrainState.IN_PROGRESS:
                tvTrainStatus.setText(R.string.value_check_in_progress);
                break;
            case TrainState.FINISH:
                tvTrainStatus.setText(R.string.value_check_finished);
                break;
            case TrainState.STOP:
                tvTrainStatus.setText(R.string.value_check_stopped);
                break;
            default:
                tvTrainStatus.setText(R.string.default_check_state);
                break;
        }

        String planArrive = ConvertUtils.splitToHM(jobEntity.getArrivetime());
        tvPlanArrive.setText(planArrive);
        String planDepart = ConvertUtils.splitToHM(jobEntity.getDeparttime());
        tvPlanDepart.setText(planDepart);
        String realArrive = ConvertUtils.splitToHM(jobEntity.getRealarrivetime());
        if (equals(realArrive, planArrive) || isEmpty(realArrive)) {
            tvRealArrive.setTextColor(0xFF46DBE2);
        } else {
            tvRealArrive.setTextColor(0xFFE24D46);
        }
        tvRealArrive.setText(realArrive);
        String realDepart = ConvertUtils.splitToHM(jobEntity.getRealdeparttime());
        if (equals(realDepart, planDepart) || isEmpty(realDepart)) {
            tvRealDepart.setTextColor(0xFF46DBE2);
        } else {
            tvRealDepart.setTextColor(0xFFE24D46);
        }
        tvRealDepart.setText(realDepart);

        String[] areaType = jobEntity.getAreatypeno().split(",");
        String[] name = jobEntity.getRunareaname().split(",");
        String[] strings = ((TaskBiz) mPresenter).formatTrainData(areaType, name);
        tvStartStationName.setText(jobEntity.getSstname());
        tvEndStationName.setText(jobEntity.getEstname());
        tvTrack.setText(strings[0]);
        tvPlatform.setText(strings[1]);
        tvWaitRoom.setText(strings[2]);
        tvCheckPort.setText(strings[3]);
    }

    private void initAdapter() {
        int taskType = mTaskEntity.getTaktype();

        initMonitorOperationAdapter();

        if (taskType == TaskType.COMMAND || taskType == TaskType.COOPERATE) {
            jobsId = mJobEntities.get(0).getJobsid();
            initMonitorRecordAdapter(jobsId);
        }
    }

    private void initMonitorOperationAdapter() {
        monitorOperationAdapter = new MonitorOperationAdapter(mContext, mJobEntities);
        lvMonitorOperation.setAdapter(monitorOperationAdapter);
        lvMonitorOperation.setOnItemClickListener(this);
        updateLastTime();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JobEntity jobEntity = mJobEntities.get(position);
        String jobOperatorsId = jobEntity.getJoboperatorsid();
        Intent intent = new Intent(mContext, SceneRecordActivity.class);
        intent.putExtra(Intents.SceneRecord.JOB_OPERATORS_ID, jobOperatorsId);
        startActivity(intent);
    }

    /**
     * 初始化作业监控人附件列表Adapter
     */
    private void initMonitorRecordAdapter(String jobsId) {
        // 上传附件列表adapter
        publisherRecordAdapter = new RecordAdapter(mContext, publisherRecordEntities);
        // 设置下载文件具体操作
        publisherRecordAdapter.setDownloadPerformer(localRecordEntity -> {
            try {
                String userId = CacheDataSource.getUserId();
                File publisherDir = DirAndFileUtils.getMonitorDir(userId, jobsId);
                ((TaskBiz) mPresenter).downloadFile(localRecordEntity.getServerUri(),
                        publisherDir.getAbsolutePath(), myDownloadListener);
            } catch (IOException e) {
                showToast(R.string.toast_check_sdcard);
            }
        });
        lvRecordPublisher.setAdapter(publisherRecordAdapter);

        // 获取命令/协作发布人本地保存的和已经上传到服务器的附件记录
        publisherRecordEntities.addAll(((TaskBiz) mPresenter).getPublisherLocalFile(jobsId));
        notifyPublisherRecordDataSetChanged(true);
        mPresenter.getPublisherUploadedFile(jobsId);
    }

    private MyDownloadListener myDownloadListener = new MyDownloadListener() {
        @Override
        public void downloadProgress(String serverUri, int progress) {
            for (LocalRecordEntity localRecordEntity : publisherRecordEntities) {
                if (TextUtils.equals(localRecordEntity.getServerUri(), serverUri)) {
                    localRecordEntity.setFileStatus(FileStatus.DOWNLOADING);
                    localRecordEntity.setProgress(progress);
                    notifyPublisherRecordDataSetChanged(false);
                    break;
                }
            }
        }

        @Override
        public void downloadSuccess(File file, String serverUri) {
            for (LocalRecordEntity localRecordEntity : publisherRecordEntities) {
                if (TextUtils.equals(localRecordEntity.getServerUri(), serverUri)) {
                    localRecordEntity.setFileStatus(FileStatus.SYNCHRONIZED);
                    localRecordEntity.setFile(file);
                    notifyPublisherRecordDataSetChanged(false);
                    showToast(R.string.toast_download_success);
                    break;
                }
            }
        }

        @Override
        public void downloadFailed(String serverUri) {
            for (LocalRecordEntity localRecordEntity : publisherRecordEntities) {
                if (TextUtils.equals(localRecordEntity.getServerUri(), serverUri)) {
                    localRecordEntity.setFileStatus(FileStatus.NO_DOWNLOAD);
                    notifyPublisherRecordDataSetChanged(false);
                    showToast(R.string.toast_download_failed);
                    break;
                }
            }
        }
    };

    @Override
    public void onPublisherUploadFileGetSuccess(List<ServerRecordEntity> serverRecordFileList) {
        boolean needRecalculateHeight = false;
        for (ServerRecordEntity entity : serverRecordFileList) {
            String[] split = entity.getFilepath().split("/");
            String fileName = split[split.length - 1];
            int position = checkFileExist(fileName, publisherRecordEntities);
            if (position == -1) {
                // 本地没有
                LocalRecordEntity localRecordEntity = new LocalRecordEntity();
                localRecordEntity.setFileType(Integer.valueOf(entity.getType()));
                localRecordEntity.setFileStatus(FileStatus.NO_DOWNLOAD);
                localRecordEntity.setServerUri(entity.getFilepath());
                localRecordEntity.setFileName(entity.getFilepath().split("/")[split.length - 1]);
                publisherRecordEntities.add(localRecordEntity);
                needRecalculateHeight = true;
            } else {
                // 本地存在
                LocalRecordEntity localRecordEntity = publisherRecordEntities.get(position);
                localRecordEntity.setFileStatus(FileStatus.SYNCHRONIZED);
            }
        }
        if (serverRecordFileList.size() != 0)
            notifyPublisherRecordDataSetChanged(needRecalculateHeight);

        // 自动下载没有下载成功的文件
        for (LocalRecordEntity publisherRecordEntity : publisherRecordEntities) {
            if (publisherRecordEntity.getFileStatus() == FileStatus.NO_DOWNLOAD) {
                try {
                    String userId = CacheDataSource.getUserId();
                    File publisherDir = DirAndFileUtils.getMonitorDir(userId, jobsId);
                    ((TaskBiz) mPresenter).downloadFile(publisherRecordEntity.getServerUri(),
                            publisherDir.getAbsolutePath(), myDownloadListener);
                } catch (IOException e) {
                    showToast(R.string.toast_check_sdcard);
                }
            }
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param fileName 文件名
     * @return 不存在返回-1，存在返回该文件在集合中的position
     */
    private int checkFileExist(String fileName, List<LocalRecordEntity> recordEntities) {
        int position = -1;
        for (int i = 0; i < recordEntities.size(); i++) {
            if (recordEntities.get(i).getFileName().equals(fileName)) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
     * 任务发布人上传的附件数发生改变，刷新界面
     *
     * @param needRecalculateHeight 是否需要重新计算ListView高度
     */
    private void notifyPublisherRecordDataSetChanged(boolean needRecalculateHeight) {
        publisherRecordAdapter.notifyDataSetChanged();

        if (needRecalculateHeight) {
            ViewUtils.setListViewHeightBasedOnChildren(lvRecordPublisher);
        }
    }

    /**
     * 定时更新任务持续时间
     */
    private void updateLastTime() {
        if (timer == null) {
            timer = Observable.interval(30, 30, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((Long aLong) -> monitorOperationAdapter.notifyDataSetChanged());
        }
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
        if (timer != null) {
            timer.dispose();
        }
    }
}
