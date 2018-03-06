package com.winsion.dispatch.modules.operation.activity.taskoperator;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winsion.dispatch.R;
import com.winsion.component.basic.base.BaseActivity;
import com.winsion.dispatch.common.biz.CommonBiz;
import com.winsion.dispatch.common.listener.StateListener;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.constants.OpeType;
import com.winsion.component.basic.data.listener.MyDownloadListener;
import com.winsion.component.basic.data.listener.UploadListener;
import com.winsion.dispatch.media.activity.AddNoteActivity;
import com.winsion.dispatch.media.activity.RecordAudioActivity;
import com.winsion.dispatch.media.activity.RecordVideoActivity;
import com.winsion.dispatch.media.activity.TakePhotoActivity;
import com.winsion.dispatch.media.adapter.RecordAdapter;
import com.winsion.dispatch.media.constants.FileStatus;
import com.winsion.dispatch.media.constants.FileType;
import com.winsion.dispatch.media.entity.LocalRecordEntity;
import com.winsion.dispatch.media.entity.ServerRecordEntity;
import com.winsion.dispatch.modules.operation.biz.ChangeStatusBiz;
import com.winsion.dispatch.modules.operation.constants.RunState;
import com.winsion.dispatch.modules.operation.constants.TaskState;
import com.winsion.dispatch.modules.operation.constants.TaskType;
import com.winsion.dispatch.modules.operation.constants.TrainState;
import com.winsion.dispatch.modules.operation.entity.JobEntity;
import com.winsion.component.basic.utils.ConvertUtils;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.FileUtils;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.basic.utils.ViewUtils;
import com.winsion.component.basic.utils.constants.Formatter;
import com.winsion.dispatch.view.CustomDialog;
import com.winsion.dispatch.view.DrawableCenterTextView;
import com.winsion.dispatch.view.TitleView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.winsion.dispatch.common.constants.Intents.Media.MEDIA_FILE;
import static com.winsion.dispatch.modules.operation.constants.Intents.OperatorTaskDetail.JOB_ENTITY;

/**
 * Created by 10295 on 2018/1/19.
 * 任务执行人任务详情Activity
 * 协作/命令/任务/网格/预案
 */

public class OperatorTaskDetailActivity extends BaseActivity implements OperatorTaskDetailContract.View, UploadListener, MyDownloadListener {
    private TitleView tvTitle;
    private TextView tvNumber;
    private TextView tvStartStationName;
    private TextView tvTrainStatus;
    private TextView tvEndStationName;
    private TextView tvTrack;
    private LinearLayout llTrack;
    private TextView tvPlatform;
    private LinearLayout llPlatform;
    private TextView tvPlanArrive;
    private TextView tvPlanDepart;
    private TextView tvWaitRoom;
    private LinearLayout llWaitRoom;
    private TextView tvRealArrive;
    private TextView tvRealDepart;
    private TextView tvCheckPort;
    private LinearLayout llCheckPort;
    private LinearLayout llTrainModule;
    private ImageView divHeader;
    private ImageView ivStatus;
    private ImageView ivTypeIcon;
    private TextView taskTypeName;
    private ImageView ivJobIcon;
    private TextView tvTaskName;
    private TextView tvLastTime;
    private TextView tvLocation;
    private DrawableCenterTextView btnNote;
    private Button btnStatus;
    private DrawableCenterTextView btnBroadcast;
    private TextView tvPlanTime;
    private TextView tvRealTime;
    private Button btnTakePhoto;
    private Button btnVideo;
    private Button btnRecord;
    private LinearLayout llBgColor;
    private TextView tvPublisherTitle;
    private ListView lvRecordPublisherGrid;
    private TextView tvPerformerTitle;
    private ListView lvRecordPerformer;
    private ImageView ivRecordDiv1;
    private ImageView ivRecordDiv2;
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

    public static final int CODE_NOTE = 0;  // 备注
    public static final int CODE_TAKE_PHOTO = 1;    // 拍照
    public static final int CODE_RECORD_VIDEO = 2;  // 录像
    public static final int CODE_RECORD_AUDIO = 3;  // 录音

    private OperatorTaskDetailContract.Presenter mPresenter;

    private JobEntity mJobEntity;   // 上个页面带过来的
    private List<LocalRecordEntity> performerRecordEntities = new ArrayList<>();    // 作业执行人上传附件集合
    private RecordAdapter performerRecordAdapter;   // 作业执行人上传附件列表Adapter
    private List<LocalRecordEntity> publisherRecordEntities = new ArrayList<>();    // 命令/协作发布人上传附件集合
    private RecordAdapter publisherRecordAdapter;   // 命令/协作发布人上传附件列表Adapter(用于命令/协作)

    private File noteFile;
    private File photoFile;
    private File videoFile;
    private File audioFile;

    // 定时刷新器(刷新任务执行时间)
    private Disposable timer;

    @Override
    protected int setContentView() {
        return R.layout.activity_operator_task_detail;
    }

    @Override
    protected void start() {
        initView();
        initPresenter();
        initAdapter();
        initData();
        initListener();
        updateLastTime();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvNumber = findViewById(R.id.tv_number);
        tvStartStationName = findViewById(R.id.tv_startStationName);
        tvTrainStatus = findViewById(R.id.tv_train_status);
        tvEndStationName = findViewById(R.id.tv_endStationName);
        tvTrack = findViewById(R.id.tv_track);
        llTrack = findViewById(R.id.ll_track);
        tvPlatform = findViewById(R.id.tv_platform);
        llPlatform = findViewById(R.id.ll_platform);
        tvPlanArrive = findViewById(R.id.tv_plan_arrive);
        tvPlanDepart = findViewById(R.id.tv_plan_depart);
        tvWaitRoom = findViewById(R.id.tv_wait_room);
        llWaitRoom = findViewById(R.id.ll_wait_room);
        tvRealArrive = findViewById(R.id.tv_real_arrive);
        tvRealDepart = findViewById(R.id.tv_real_depart);
        tvCheckPort = findViewById(R.id.tv_check_port);
        llCheckPort = findViewById(R.id.ll_check_port);
        llTrainModule = findViewById(R.id.ll_train_module);
        divHeader = findViewById(R.id.div_header);
        ivStatus = findViewById(R.id.iv_status);
        ivTypeIcon = findViewById(R.id.iv_type_icon);
        taskTypeName = findViewById(R.id.task_type_name);
        ivJobIcon = findViewById(R.id.iv_job_icon);
        tvTaskName = findViewById(R.id.tv_task_name);
        tvLastTime = findViewById(R.id.tv_last_time);
        tvLocation = findViewById(R.id.tv_location);
        btnNote = findViewById(R.id.btn_note);
        btnStatus = findViewById(R.id.btn_status);
        btnBroadcast = findViewById(R.id.btn_broadcast);
        tvPlanTime = findViewById(R.id.tv_plan_time);
        tvRealTime = findViewById(R.id.tv_real_time);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnVideo = findViewById(R.id.btn_video);
        btnRecord = findViewById(R.id.btn_record);
        llBgColor = findViewById(R.id.ll_bg_color);
        tvPublisherTitle = findViewById(R.id.tv_publisher_title);
        lvRecordPublisherGrid = findViewById(R.id.lv_record_publisher_grid);
        tvPerformerTitle = findViewById(R.id.tv_performer_title);
        lvRecordPerformer = findViewById(R.id.lv_record_performer);
        ivRecordDiv1 = findViewById(R.id.iv_record_div1);
        ivRecordDiv2 = findViewById(R.id.iv_record_div2);
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
    }

    private void initPresenter() {
        mPresenter = new OperatorTaskDetailPresenter(this);
        mJobEntity = (JobEntity) getIntent().getSerializableExtra(JOB_ENTITY);
    }

    private void initData() {
        initViewModule();

        // 获取作业执行人本地保存的和已经上传到服务器的附件记录
        String jobOperatorsId = mJobEntity.getJoboperatorsid();
        ArrayList<LocalRecordEntity> localFile = mPresenter.getPerformerLocalFile(jobOperatorsId);
        if (localFile.size() != 0) {
            performerRecordEntities.addAll(localFile);
            performerRecordAdapter.notifyDataSetChanged();
            ViewUtils.setListViewHeightBasedOnChildren(lvRecordPerformer);
        }
        mPresenter.getPerformerUploadedFile(jobOperatorsId);

        if (mJobEntity.getTaktype() == TaskType.COMMAND ||
                mJobEntity.getTaktype() == TaskType.COOPERATE ||
                mJobEntity.getTaktype() == TaskType.GRID) {
            // 获取命令/协作/网格任务发布人本地保存的和已经上传到服务器的附件记录
            localFile = mPresenter.getPublisherLocalFile(jobOperatorsId);
            if (localFile.size() != 0) {
                publisherRecordEntities.addAll(localFile);
                publisherRecordAdapter.notifyDataSetChanged();
                ListView listView = mJobEntity.getTaktype() == TaskType.GRID ?
                        lvRecordPublisherGrid : lvRecordPublisher;
                ViewUtils.setListViewHeightBasedOnChildren(listView);
            }
            mPresenter.getPublisherUploadedFile(mJobEntity.getJobsid());
        }
    }

    /**
     * 根据任务类型显示不同的VIEW模块
     */
    private void initViewModule() {
        int taskType = mJobEntity.getTaktype();
        if (taskType == TaskType.GRID) {
            lvRecordPublisher.setVisibility(View.GONE);
            rlOrderModule.setVisibility(View.GONE);
            llTrainModule.setVisibility(View.GONE);
            divHeader.setVisibility(View.GONE);
            lvRecordPublisherGrid.setVisibility(View.VISIBLE);
            tvPerformerTitle.setVisibility(View.VISIBLE);
            tvPublisherTitle.setVisibility(View.VISIBLE);
            ivRecordDiv1.setVisibility(View.VISIBLE);
            ivRecordDiv2.setVisibility(View.VISIBLE);
        } else if (taskType == TaskType.PLAN) {
            lvRecordPublisher.setVisibility(View.GONE);
            rlOrderModule.setVisibility(View.GONE);
            llTrainModule.setVisibility(View.GONE);
            divHeader.setVisibility(View.GONE);
            lvRecordPublisherGrid.setVisibility(View.GONE);
            tvPerformerTitle.setVisibility(View.GONE);
            tvPublisherTitle.setVisibility(View.GONE);
            ivRecordDiv1.setVisibility(View.GONE);
            ivRecordDiv2.setVisibility(View.GONE);
        } else if (taskType == TaskType.COOPERATE || taskType == TaskType.COMMAND) {
            lvRecordPublisher.setVisibility(View.VISIBLE);
            rlOrderModule.setVisibility(View.VISIBLE);
            llTrainModule.setVisibility(View.GONE);
            divHeader.setVisibility(View.VISIBLE);
            lvRecordPublisherGrid.setVisibility(View.GONE);
            tvPerformerTitle.setVisibility(View.GONE);
            tvPublisherTitle.setVisibility(View.GONE);
            ivRecordDiv1.setVisibility(View.GONE);
            ivRecordDiv2.setVisibility(View.GONE);
            initOrderModuleView(taskType);
        } else {
            lvRecordPublisher.setVisibility(View.GONE);
            rlOrderModule.setVisibility(View.GONE);
            llTrainModule.setVisibility(View.VISIBLE);
            divHeader.setVisibility(View.VISIBLE);
            lvRecordPublisherGrid.setVisibility(View.GONE);
            tvPerformerTitle.setVisibility(View.GONE);
            tvPublisherTitle.setVisibility(View.GONE);
            ivRecordDiv1.setVisibility(View.GONE);
            ivRecordDiv2.setVisibility(View.GONE);
            initTrainModuleView();
        }
        initTaskModuleView();
    }

    private void initListener() {
        tvTitle.setOnBackClickListener(v -> finish());
        addOnClickListeners(R.id.btn_status, R.id.btn_note, R.id.btn_broadcast,
                R.id.btn_take_photo, R.id.btn_video, R.id.btn_record);
    }

    /**
     * 初始化命令/协作模块数据
     *
     * @param taskType 作业类型(命令/协作)
     */
    private void initOrderModuleView(int taskType) {
        tvMonitorGroupHint.setText(taskType == TaskType.COMMAND ? R.string.name_issue_order_group
                : R.string.name_issue_cooperation_group);
        tvPerformerGroupHint.setText(taskType == TaskType.COMMAND ? R.string.name_order_group
                : R.string.name_cooperation_group);
        tvMonitorTeam.setText(mJobEntity.getMonitorteamname());
        tvPerformerTeam.setText(mJobEntity.getOpteamname());
        tvTitle1.setText(mJobEntity.getTaskname());
        String trainNumber = mJobEntity.getTrainnumber();
        trainNumber = TextUtils.isEmpty(trainNumber) ? getString(R.string.value_nothing) : trainNumber;
        tvTrainNumber.setText(trainNumber);
        tvStartTime.setText(mJobEntity.getPlanstarttime());
        tvEndTime.setText(mJobEntity.getPlanendtime());

        String prefix = taskType == TaskType.COMMAND ? getString(R.string.name_command_content) + "  " : getString(R.string.name_cooperation_content) + "  ";
        ForegroundColorSpan gray = new ForegroundColorSpan(0xFF69696D);
        SpannableStringBuilder builder = new SpannableStringBuilder()
                .append(prefix)
                .append(mJobEntity.getWorkcontent());
        builder.setSpan(gray, 0, prefix.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        etContent.setText(builder);
    }

    /**
     * 初始化车次模块数据
     */
    private void initTrainModuleView() {
        String trainNumber = mJobEntity.getTrainnumber();
        int color = 0xFFFFFFFF;
        switch (mJobEntity.getTrainlate()) {
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
        builder.setSpan(statusColor, mJobEntity.getTrainnumber().length(), trainNumber.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvNumber.setText(builder);
        int trainStatus = mJobEntity.getTrainstatus();
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

        String planArrive = ConvertUtils.splitToHM(mJobEntity.getArrivetime());
        tvPlanArrive.setText(planArrive);
        String planDepart = ConvertUtils.splitToHM(mJobEntity.getDeparttime());
        tvPlanDepart.setText(planDepart);
        String realArrive = ConvertUtils.splitToHM(mJobEntity.getRealarrivetime());
        if (equals(realArrive, planArrive) || isEmpty(realArrive)) {
            tvRealArrive.setTextColor(0xFF46DBE2);
        } else {
            tvRealArrive.setTextColor(0xFFE24D46);
        }
        tvRealArrive.setText(realArrive);
        String realDepart = ConvertUtils.splitToHM(mJobEntity.getRealdeparttime());
        if (equals(realDepart, planDepart) || isEmpty(realDepart)) {
            tvRealDepart.setTextColor(0xFF46DBE2);
        } else {
            tvRealDepart.setTextColor(0xFFE24D46);
        }
        tvRealDepart.setText(realDepart);

        String[] areaType = mJobEntity.getAreatypeno().split(",");
        String[] name = mJobEntity.getRunareaname().split(",");
        String[] strings = mPresenter.formatTrainData(areaType, name);
        tvStartStationName.setText(mJobEntity.getSstname());
        tvEndStationName.setText(mJobEntity.getEstname());
        tvTrack.setText(strings[0]);
        tvPlatform.setText(strings[1]);
        tvWaitRoom.setText(strings[2]);
        tvCheckPort.setText(strings[3]);
    }

    /**
     * 初始化任务模块数据
     */
    private void initTaskModuleView() {
        // 设置任务名
        tvTaskName.setText(mJobEntity.getTaskname());
        tvTaskName.setSelected(true);

        // 根据任务类型显示任务类型名称和任务类型图标
        int taskType = mJobEntity.getTaktype();
        switch (taskType) {
            // 任务
            case TaskType.TASK:
                tvTitle.setTitleText(R.string.title_operation_detail);
                taskTypeName.setText(R.string.name_operation_name);
                ivTypeIcon.setVisibility(View.GONE);
                break;
            // 命令
            case TaskType.COMMAND:
                tvTitle.setTitleText(R.string.title_command_detail);
                taskTypeName.setText(R.string.name_command_name);
                ivTypeIcon.setImageResource(R.drawable.ic_command);
                ivTypeIcon.setVisibility(View.VISIBLE);
                break;
            // 协作
            case TaskType.COOPERATE:
                tvTitle.setTitleText(R.string.title_cooperation_detail);
                taskTypeName.setText(R.string.name_cooperation_name);
                ivTypeIcon.setImageResource(R.drawable.ic_cooperation);
                ivTypeIcon.setVisibility(View.VISIBLE);
                break;
            // 网格
            case TaskType.GRID:
                tvTitle.setTitleText(R.string.title_grid_detail);
                taskTypeName.setText(R.string.value_grid_task);
                ivTypeIcon.setImageResource(R.drawable.ic_type_grid);
                ivTypeIcon.setVisibility(View.VISIBLE);
                break;
            // 预案
            case TaskType.PLAN:
                tvTitle.setTitleText(R.string.title_alarm_detail);
                taskTypeName.setText(R.string.value_alarm_task);
                ivTypeIcon.setImageResource(R.drawable.ic_type_alarm);
                ivTypeIcon.setVisibility(View.VISIBLE);
                break;
        }

        // 设置位置信息
        tvLocation.setText(mJobEntity.getTaskareaname());

        // 把需要用到的时间转换好
        int lastTime = 0;
        long currentTime = System.currentTimeMillis();

        String planStartTimeStr = mJobEntity.getPlanstarttime();
        String realStartTimeStr = mJobEntity.getRealstarttime();
        long planStartTime = ConvertUtils.parseDate(planStartTimeStr, Formatter.DATE_FORMAT1);
        long realStartTime = ConvertUtils.parseDate(realStartTimeStr, Formatter.DATE_FORMAT1);

        String planEndTimeStr = mJobEntity.getPlanendtime();
        String realEndTimeStr = mJobEntity.getRealendtime();
        long planEndTime = ConvertUtils.parseDate(planEndTimeStr, Formatter.DATE_FORMAT1);
        long realEndTime = ConvertUtils.parseDate(realEndTimeStr, Formatter.DATE_FORMAT1);

        // 设置任务计划和实际开始结束时间
        String splitPlanStartTime = ConvertUtils.splitToHM(planStartTimeStr);
        String splitPlanEndTime = ConvertUtils.splitToHM(planEndTimeStr);
        tvPlanTime.setText(String.format("%s ~ %s", splitPlanStartTime, splitPlanEndTime));
        String splitRealStartTime = ConvertUtils.splitToHM(realStartTimeStr);
        String splitRealEndTime = ConvertUtils.splitToHM(realEndTimeStr);
        tvRealTime.setText(String.format("%s ~ %s", splitRealStartTime, splitRealEndTime));

        boolean isTimeOut = false;
        int workStatus = mJobEntity.getWorkstatus();
        switch (workStatus) {
            case TaskState.NOT_STARTED:
                // 未开始
                // 设置按钮背景和文字
                btnStatus.setBackgroundResource(R.drawable.btn_bg_start);
                btnStatus.setText(getString(R.string.btn_click_to_start));
                // 设置持续时间
                tvLastTime.setText(String.format("%s%s", String.valueOf(lastTime), getString(R.string.suffix_minute)));
                // 判断是否超时
                isTimeOut = planStartTime < currentTime;
                if (isTimeOut) {
                    ivStatus.setImageResource(R.drawable.ic_timeout_unstart);
                } else {
                    ivStatus.setImageResource(R.drawable.ic_not_start);
                }
                break;
            case TaskState.RUN:
                // 进行中
                // 设置按钮背景和文字
                btnStatus.setBackgroundResource(R.drawable.btn_bg_finish);
                btnStatus.setText(getString(R.string.btn_click_to_finish));
                // 设置持续时间
                lastTime = (int) ((System.currentTimeMillis() - realStartTime) / (60 * 1000));
                tvLastTime.setText(String.format("%s%s", String.valueOf(lastTime), getString(R.string.suffix_minute)));
                // 判断是否超时
                isTimeOut = planEndTime < currentTime;
                if (isTimeOut) {
                    ImageLoader.loadGif(ivStatus, R.drawable.gif_doing_timeout);
                } else {
                    ImageLoader.loadGif(ivStatus, R.drawable.gif_doing);
                }
                break;
            case TaskState.DONE:
                // 已完成
                // 设置按钮背景和文字
                btnStatus.setBackgroundResource(R.drawable.btn_bg_done);
                btnStatus.setText(getString(R.string.spinner_finished));
                // 设置持续时间
                lastTime = (int) ((realEndTime - realStartTime) / (60 * 1000));
                tvLastTime.setText(String.format("%s%s", String.valueOf(lastTime), getString(R.string.suffix_minute)));
                // 判断是否超时
                isTimeOut = planEndTime < realEndTime;
                if (isTimeOut) {
                    ivStatus.setImageResource(R.drawable.ic_timeout_done);
                } else {
                    ivStatus.setImageResource(R.drawable.ic_done);
                }
                // 如果是网格任务已完成状态则显示为待验收
                if (taskType == TaskType.GRID) {
                    ivStatus.setImageResource(R.drawable.ic_wait_pass);
                    btnStatus.setText(R.string.btn_wait_acceptance);
                }
                break;
            case TaskState.GRID_NOT_PASS:
                // 验收未通过
                // 设置按钮背景和文字
                btnStatus.setBackgroundResource(R.drawable.btn_bg_restart);
                btnStatus.setText(getString(R.string.btn_restart));
                // 设置持续时间
                lastTime = (int) ((realEndTime - realStartTime) / (60 * 1000));
                tvLastTime.setText(String.format("%s%s", String.valueOf(lastTime), getString(R.string.suffix_minute)));
                // 判断是否超时
                isTimeOut = planEndTime < realEndTime;
                if (isTimeOut) {
                    ivStatus.setImageResource(R.drawable.ic_not_pass);
                } else {
                    ivStatus.setImageResource(R.drawable.ic_not_pass);
                }
                break;
        }

        // 根据是否超时设置任务模块背景色
        if (isTimeOut) {
            llBgColor.setBackgroundResource(R.color.basic_yellow1);
            tvLastTime.setTextColor(getResources().getColor(R.color.basic_red2));
        } else {
            llBgColor.setBackgroundResource(R.color.basic_gray8);
            tvLastTime.setTextColor(getResources().getColor(R.color.basic_blue1));
        }
    }

    private void initAdapter() {
        initPerformerRecordAdapter();
        int taskType = mJobEntity.getTaktype();
        if (taskType == TaskType.COMMAND || taskType == TaskType.COOPERATE || taskType == TaskType.GRID)
            initMonitorRecordAdapter();
    }

    /**
     * 初始化作业监控人附件列表Adapter
     */
    private void initMonitorRecordAdapter() {
        // 上传附件列表adapter
        publisherRecordAdapter = new RecordAdapter(mContext, publisherRecordEntities);
        // 设置下载文件具体操作
        publisherRecordAdapter.setDownloadPerformer(localRecordEntity -> {
            try {
                String userId = CacheDataSource.getUserId();
                String jobOperatorsId = mJobEntity.getJoboperatorsid();
                File publisherDir = DirAndFileUtils.getMonitorDir(userId, jobOperatorsId);
                mPresenter.download(localRecordEntity.getServerUri(), publisherDir.getAbsolutePath(), this);
            } catch (IOException e) {
                showToast(R.string.toast_check_sdcard);
            }
        });
        int taskType = mJobEntity.getTaktype();
        ListView listView = taskType == TaskType.COMMAND || taskType == TaskType.COOPERATE ?
                lvRecordPublisher : lvRecordPublisherGrid;
        listView.setAdapter(publisherRecordAdapter);
    }

    /**
     * 初始化作业执行人附件列表Adapter
     */
    private void initPerformerRecordAdapter() {
        // 上传附件列表adapter
        performerRecordAdapter = new RecordAdapter(mContext, performerRecordEntities);
        // 设置上传文件具体操作
        performerRecordAdapter.setUploadPerformer(localRecordEntity -> mPresenter.upload(mJobEntity, localRecordEntity.getFile(), this));
        // 设置下载文件具体操作
        performerRecordAdapter.setDownloadPerformer(localRecordEntity -> {
            try {
                String userId = CacheDataSource.getUserId();
                String jobOperatorsId = mJobEntity.getJoboperatorsid();
                File performerDir = DirAndFileUtils.getPerformerDir(userId, jobOperatorsId);
                mPresenter.download(localRecordEntity.getServerUri(), performerDir.getAbsolutePath(), this);
            } catch (IOException e) {
                showToast(R.string.toast_check_sdcard);
            }
        });
        lvRecordPerformer.setAdapter(performerRecordAdapter);
    }

    @Override
    public void uploadProgress(File uploadFile, int progress) {
        for (LocalRecordEntity localRecordEntity : performerRecordEntities) {
            if (localRecordEntity.getFile() == uploadFile) {
                localRecordEntity.setFileStatus(FileStatus.UPLOADING);
                localRecordEntity.setProgress(progress);
                performerRecordAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void uploadSuccess(File uploadFile) {
        for (LocalRecordEntity localRecordEntity : performerRecordEntities) {
            if (localRecordEntity.getFile() == uploadFile) {
                localRecordEntity.setFileStatus(FileStatus.SYNCHRONIZED);
                performerRecordAdapter.notifyDataSetChanged();
                showToast(R.string.toast_upload_success);
                break;
            }
        }
    }

    @Override
    public void uploadFailed(File uploadFile) {
        for (LocalRecordEntity localRecordEntity : performerRecordEntities) {
            if (localRecordEntity.getFile() == uploadFile) {
                localRecordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                performerRecordAdapter.notifyDataSetChanged();
                showToast(R.string.toast_upload_failed);
                break;
            }
        }
    }

    @Override
    public void downloadProgress(String serverUri, int progress) {
        for (LocalRecordEntity localRecordEntity : performerRecordEntities) {
            if (equals(localRecordEntity.getServerUri(), serverUri)) {
                localRecordEntity.setFileStatus(FileStatus.DOWNLOADING);
                localRecordEntity.setProgress(progress);
                performerRecordAdapter.notifyDataSetChanged();
                break;
            }
        }
        for (LocalRecordEntity localRecordEntity : publisherRecordEntities) {
            if (equals(localRecordEntity.getServerUri(), serverUri)) {
                localRecordEntity.setFileStatus(FileStatus.DOWNLOADING);
                localRecordEntity.setProgress(progress);
                publisherRecordAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void downloadSuccess(File file, String serverUri) {
        for (LocalRecordEntity localRecordEntity : performerRecordEntities) {
            if (equals(localRecordEntity.getServerUri(), serverUri)) {
                localRecordEntity.setFileStatus(FileStatus.SYNCHRONIZED);
                localRecordEntity.setFile(file);
                performerRecordAdapter.notifyDataSetChanged();
                showToast(R.string.toast_download_success);
                break;
            }
        }
        for (LocalRecordEntity localRecordEntity : publisherRecordEntities) {
            if (equals(localRecordEntity.getServerUri(), serverUri)) {
                localRecordEntity.setFileStatus(FileStatus.SYNCHRONIZED);
                localRecordEntity.setFile(file);
                publisherRecordAdapter.notifyDataSetChanged();
                showToast(R.string.toast_download_success);
                break;
            }
        }
    }

    @Override
    public void downloadFailed(String serverUri) {
        for (LocalRecordEntity localRecordEntity : performerRecordEntities) {
            if (equals(localRecordEntity.getServerUri(), serverUri)) {
                localRecordEntity.setFileStatus(FileStatus.NO_DOWNLOAD);
                performerRecordAdapter.notifyDataSetChanged();
                showToast(R.string.toast_download_failed);
                break;
            }
        }
        for (LocalRecordEntity localRecordEntity : publisherRecordEntities) {
            if (equals(localRecordEntity.getServerUri(), serverUri)) {
                localRecordEntity.setFileStatus(FileStatus.NO_DOWNLOAD);
                publisherRecordAdapter.notifyDataSetChanged();
                showToast(R.string.toast_download_failed);
                break;
            }
        }
    }

    /**
     * 获取执行人上传到服务器的附件记录成功
     *
     * @param serverRecordFileList 上传到服务器的附件列表
     */
    @Override
    public void onPerformerUploadedFileGetSuccess(List<ServerRecordEntity> serverRecordFileList) {
        boolean needRecalculateHeight = false;
        for (ServerRecordEntity entity : serverRecordFileList) {
            String[] split = entity.getFilepath().split("/");
            String fileName = split[split.length - 1];
            int position = checkFileExist(fileName, performerRecordEntities);
            if (position == -1) {
                // 本地没有
                LocalRecordEntity localRecordEntity = new LocalRecordEntity();
                localRecordEntity.setFileType(Integer.valueOf(entity.getType()));
                localRecordEntity.setFileStatus(FileStatus.NO_DOWNLOAD);
                localRecordEntity.setServerUri(entity.getFilepath());
                localRecordEntity.setFileName(entity.getFilepath().split("/")[split.length - 1]);
                performerRecordEntities.add(localRecordEntity);
                needRecalculateHeight = true;
            } else {
                // 本地存在
                LocalRecordEntity localRecordEntity = performerRecordEntities.get(position);
                localRecordEntity.setFileStatus(FileStatus.SYNCHRONIZED);
            }
        }
        performerRecordAdapter.notifyDataSetChanged();
        if (needRecalculateHeight) ViewUtils.setListViewHeightBasedOnChildren(lvRecordPerformer);
    }

    /**
     * 获取命令/协作/网格任务发布人上传到服务器的附件记录成功
     *
     * @param serverRecordFileList 上传到服务器的附件列表
     */
    @Override
    public void onPublisherUploadedFileGetSuccess(List<ServerRecordEntity> serverRecordFileList) {
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
        if (serverRecordFileList.size() != 0) publisherRecordAdapter.notifyDataSetChanged();
        if (needRecalculateHeight) {
            ListView listView = mJobEntity.getTaktype() == TaskType.GRID ?
                    lvRecordPublisherGrid : lvRecordPublisher;
            ViewUtils.setListViewHeightBasedOnChildren(listView);
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

    @Override
    public void onClick(View view) {
        String userId = CacheDataSource.getUserId();
        String jobOperatorsId = mJobEntity.getJoboperatorsid();
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_status:
                // 更改任务状态按钮点击事件
                int workStatus = mJobEntity.getWorkstatus();
                if (workStatus == TaskState.RUN || workStatus == TaskState.NOT_STARTED || workStatus == TaskState.GRID_NOT_PASS) {
                    showDialog(workStatus == TaskState.RUN, view);
                }
                break;
            case R.id.btn_broadcast:
                showToast("暂未开放");
                break;
            case R.id.btn_note:
                try {
                    noteFile = CommonBiz.getMediaFile(DirAndFileUtils.getPerformerDir(userId, jobOperatorsId), FileType.TEXT);
                    intent = new Intent(mContext, AddNoteActivity.class);
                    intent.putExtra(MEDIA_FILE, noteFile);
                    startActivityForResult(intent, CODE_NOTE);
                } catch (IOException e) {
                    showToast(R.string.toast_check_sdcard);
                }
                break;
            case R.id.btn_take_photo:
                try {
                    photoFile = CommonBiz.getMediaFile(DirAndFileUtils.getPerformerDir(userId, jobOperatorsId), FileType.PICTURE);
                    intent = new Intent(mContext, TakePhotoActivity.class);
                    intent.putExtra(MEDIA_FILE, photoFile);
                    startActivityForResult(intent, CODE_TAKE_PHOTO);
                } catch (IOException e) {
                    showToast(R.string.toast_check_sdcard);
                }
                break;
            case R.id.btn_video:
                try {
                    videoFile = CommonBiz.getMediaFile(DirAndFileUtils.getPerformerDir(userId, jobOperatorsId), FileType.VIDEO);
                    intent = new Intent(mContext, RecordVideoActivity.class);
                    intent.putExtra(MEDIA_FILE, videoFile);
                    startActivityForResult(intent, CODE_RECORD_VIDEO);
                } catch (IOException e) {
                    showToast(R.string.toast_check_sdcard);
                }
                break;
            case R.id.btn_record:
                try {
                    audioFile = CommonBiz.getMediaFile(DirAndFileUtils.getPerformerDir(userId, jobOperatorsId), FileType.AUDIO);
                    intent = new Intent(mContext, RecordAudioActivity.class);
                    intent.putExtra(MEDIA_FILE, audioFile);
                    startActivityForResult(intent, CODE_RECORD_AUDIO);
                } catch (IOException e) {
                    showToast(R.string.toast_check_sdcard);
                }
                break;
        }
    }

    /**
     * 更改任务状态前弹出对话框
     *
     * @param isRunning 当前任务是否正在进行中
     * @param btn       按钮Button
     */
    private void showDialog(boolean isRunning, View btn) {
        new CustomDialog.NormalBuilder(mContext)
                .setMessage(getString(isRunning ? R.string.dialog_sure_to_finish : R.string.dialog_sure_to_start))
                .setPositiveButton((dialog, which) -> changeStatus(isRunning, btn))
                .show();
    }

    private void changeStatus(boolean isRunning, View btn) {
        btn.setEnabled(false);
        int opeType = isRunning ? OpeType.COMPLETE : OpeType.BEGIN;
        ((ChangeStatusBiz) mPresenter).changeJobStatus(mContext, mJobEntity, opeType, new StateListener() {
            @Override
            public void onSuccess() {
                btn.setEnabled(true);
                String currentTime = ConvertUtils.formatDate(System.currentTimeMillis(), Formatter.DATE_FORMAT1);
                if (isRunning) {
                    mJobEntity.setWorkstatus(TaskState.DONE);
                    mJobEntity.setRealendtime(currentTime);
                } else {
                    mJobEntity.setWorkstatus(TaskState.RUN);
                    mJobEntity.setRealstarttime(currentTime);
                }
                // 状态发生改变，重新初始化任务模块
                initTaskModuleView();
                // 通知上个界面(OperatorTaskListFragment)同步数据
                EventBus.getDefault().post(mJobEntity);
            }

            @Override
            public void onFailed() {
                btn.setEnabled(true);
                showToast(R.string.toast_change_state_failed);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            LocalRecordEntity localRecordEntity;
            switch (requestCode) {
                case CODE_TAKE_PHOTO:
                    // 拍照成功
                    localRecordEntity = new LocalRecordEntity();
                    localRecordEntity.setFileType(FileType.PICTURE);
                    localRecordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                    localRecordEntity.setFile(photoFile);
                    localRecordEntity.setFileName(photoFile.getName());
                    performerRecordEntities.add(localRecordEntity);
                    performerRecordAdapter.notifyDataSetChanged();
                    ViewUtils.setListViewHeightBasedOnChildren(lvRecordPerformer);
                    // 上传
                    mPresenter.upload(mJobEntity, photoFile, this);
                    break;
                case CODE_RECORD_VIDEO:
                    // 录像成功
                    localRecordEntity = new LocalRecordEntity();
                    localRecordEntity.setFileType(FileType.VIDEO);
                    localRecordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                    localRecordEntity.setFile(videoFile);
                    localRecordEntity.setFileName(videoFile.getName());
                    performerRecordEntities.add(localRecordEntity);
                    performerRecordAdapter.notifyDataSetChanged();
                    ViewUtils.setListViewHeightBasedOnChildren(lvRecordPerformer);
                    // 上传
                    mPresenter.upload(mJobEntity, videoFile, this);
                    break;
                case CODE_RECORD_AUDIO:
                    // 录音成功
                    localRecordEntity = new LocalRecordEntity();
                    localRecordEntity.setFileType(FileType.AUDIO);
                    localRecordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                    localRecordEntity.setFile(audioFile);
                    localRecordEntity.setFileName(audioFile.getName());
                    performerRecordEntities.add(localRecordEntity);
                    performerRecordAdapter.notifyDataSetChanged();
                    ViewUtils.setListViewHeightBasedOnChildren(lvRecordPerformer);
                    // 上传
                    mPresenter.upload(mJobEntity, audioFile, this);
                    break;
                case CODE_NOTE:
                    // 添加备注成功
                    if (performerRecordEntities.size() == 0 || performerRecordEntities.get(0).getFileType() != FileType.TEXT) {
                        localRecordEntity = new LocalRecordEntity();
                        localRecordEntity.setFileType(FileType.TEXT);
                        localRecordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                        localRecordEntity.setFile(noteFile);
                        localRecordEntity.setFileName(noteFile.getName());
                        performerRecordEntities.add(0, localRecordEntity);
                    }
                    // 如果备注内容为空不显示
                    if (TextUtils.isEmpty(FileUtils.readFile2String(noteFile, "UTF-8"))) {
                        performerRecordEntities.remove(0);
                    }
                    performerRecordAdapter.notifyDataSetChanged();
                    ViewUtils.setListViewHeightBasedOnChildren(lvRecordPerformer);
                    break;
            }
        }
    }

    /**
     * 定时更新任务持续时间
     */
    private void updateLastTime() {
        timer = Observable.interval(30, 30, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long aLong) -> initTaskModuleView());
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
        timer.dispose();
    }
}
