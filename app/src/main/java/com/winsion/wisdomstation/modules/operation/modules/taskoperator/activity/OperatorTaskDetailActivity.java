package com.winsion.wisdomstation.modules.operation.modules.taskoperator.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.common.listener.StateListener;
import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.NetDataSource;
import com.winsion.wisdomstation.data.constants.OpeType;
import com.winsion.wisdomstation.data.listener.DownloadListener;
import com.winsion.wisdomstation.data.listener.UploadListener;
import com.winsion.wisdomstation.media.activity.AddNoteActivity;
import com.winsion.wisdomstation.media.activity.RecordAudioActivity;
import com.winsion.wisdomstation.media.activity.RecordVideoActivity;
import com.winsion.wisdomstation.media.activity.TakePhotoActivity;
import com.winsion.wisdomstation.media.adapter.RecordAdapter;
import com.winsion.wisdomstation.media.constants.FileStatus;
import com.winsion.wisdomstation.media.constants.FileType;
import com.winsion.wisdomstation.media.entity.LocalRecordEntity;
import com.winsion.wisdomstation.media.entity.ServerRecordEntity;
import com.winsion.wisdomstation.modules.operation.biz.TaskCommBiz;
import com.winsion.wisdomstation.modules.operation.constants.RunState;
import com.winsion.wisdomstation.modules.operation.constants.TaskState;
import com.winsion.wisdomstation.modules.operation.constants.TaskType;
import com.winsion.wisdomstation.modules.operation.constants.TrainState;
import com.winsion.wisdomstation.modules.operation.entity.JobEntity;
import com.winsion.wisdomstation.utils.ConvertUtils;
import com.winsion.wisdomstation.utils.DirAndFileUtils;
import com.winsion.wisdomstation.utils.FileUtils;
import com.winsion.wisdomstation.utils.ViewUtils;
import com.winsion.wisdomstation.utils.constants.Formatter;
import com.winsion.wisdomstation.view.DrawableCenterTextView;
import com.winsion.wisdomstation.view.GifView;
import com.winsion.wisdomstation.view.TitleView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 10295 on 2018/1/19.
 * 任务执行人任务详情Activity
 * 协作/命令/任务/网格/预案
 */

public class OperatorTaskDetailActivity extends BaseActivity implements OperatorTaskDetailContract.View, UploadListener, DownloadListener {
    @BindView(R.id.tv_title)
    TitleView tvTitle;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_startStationName)
    TextView tvStartStationName;
    @BindView(R.id.tv_train_status)
    TextView tvTrainStatus;
    @BindView(R.id.tv_endStationName)
    TextView tvEndStationName;
    @BindView(R.id.tv_track)
    TextView tvTrack;
    @BindView(R.id.ll_track)
    LinearLayout llTrack;
    @BindView(R.id.tv_platform)
    TextView tvPlatform;
    @BindView(R.id.ll_platform)
    LinearLayout llPlatform;
    @BindView(R.id.tv_plan_arrive)
    TextView tvPlanArrive;
    @BindView(R.id.tv_plan_depart)
    TextView tvPlanDepart;
    @BindView(R.id.tv_wait_room)
    TextView tvWaitRoom;
    @BindView(R.id.ll_wait_room)
    LinearLayout llWaitRoom;
    @BindView(R.id.tv_real_arrive)
    TextView tvRealArrive;
    @BindView(R.id.tv_real_depart)
    TextView tvRealDepart;
    @BindView(R.id.tv_check_port)
    TextView tvCheckPort;
    @BindView(R.id.ll_check_port)
    LinearLayout llCheckPort;
    @BindView(R.id.ll_train_module)
    LinearLayout llTrainModule;
    @BindView(R.id.div_header)
    ImageView divHeader;
    @BindView(R.id.iv_status)
    ImageView ivStatus;
    @BindView(R.id.doing_gif)
    GifView doingGif;
    @BindView(R.id.iv_icon_order)
    ImageView ivIconOrder;
    @BindView(R.id.task_type_name)
    TextView taskTypeName;
    @BindView(R.id.iv_job_icon)
    ImageView ivJobIcon;
    @BindView(R.id.tv_task_name)
    TextView tvTaskName;
    @BindView(R.id.tv_last_time)
    TextView tvLastTime;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.btn_note)
    DrawableCenterTextView btnNote;
    @BindView(R.id.btn_status)
    Button btnStatus;
    @BindView(R.id.btn_broadcast)
    DrawableCenterTextView btnBroadcast;
    @BindView(R.id.tv_plan_time)
    TextView tvPlanTime;
    @BindView(R.id.tv_real_time)
    TextView tvRealTime;
    @BindView(R.id.btn_take_photo)
    Button btnTakePhoto;
    @BindView(R.id.btn_video)
    Button btnVideo;
    @BindView(R.id.btn_record)
    Button btnRecord;
    @BindView(R.id.ll_bg_color)
    LinearLayout llBgColor;
    @BindView(R.id.tv_publisher_title)
    TextView tvPublisherTitle;
    @BindView(R.id.lv_record_publisher)
    ListView lvRecordPublisher;
    @BindView(R.id.tv_performer_title)
    TextView tvPerformerTitle;
    @BindView(R.id.lv_record_performer)
    ListView lvRecordPerformer;
    @BindView(R.id.iv_record_div1)
    ImageView ivRecordDiv1;
    @BindView(R.id.iv_record_div2)
    ImageView ivRecordDiv2;

    // 备注
    public static final int CODE_NOTE = 0;
    // 拍照
    public static final int CODE_TAKE_PHOTO = 1;
    // 录像
    public static final int CODE_RECORD_VIDEO = 2;
    // 录音
    public static final int CODE_RECORD_AUDIO = 3;

    public static final String TASK_ENTITY = "taskEntity";

    private OperatorTaskDetailContract.Presenter mPresenter;
    private List<LocalRecordEntity> localRecordEntities = new ArrayList<>();
    private JobEntity mJobEntity;
    private RecordAdapter recordAdapter;
    private Disposable timer;
    // 是否更改了任务状态，用来刷新Fragment中的数据
    private boolean isChangeState = false;

    @Override
    protected int setContentView() {
        return R.layout.activity_operator_task_detail;
    }

    @Override
    protected void start() {
        initPresenter();
        getIntentData();
        initView();
        initAdapter();
        initData();
        updateLastTime();
    }

    private void initPresenter() {
        mPresenter = new OperatorTaskDetailPresenter(this);
    }

    private void getIntentData() {
        mJobEntity = (JobEntity) getIntent().getSerializableExtra(TASK_ENTITY);
    }

    private void initView() {
        initTitleView();
        initHeader();
    }

    private void initTitleView() {
        tvTitle.setOnBackClickListener(v -> {
            if (isChangeState) {
                Intent data = new Intent();
                data.putExtra("afterChangeEntity", mJobEntity);
                setResult(RESULT_OK, data);
            }
            finish();
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isChangeState) {
            Intent data = new Intent();
            data.putExtra("afterChangeEntity", mJobEntity);
            setResult(RESULT_OK, data);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initHeader() {
        int taskType = mJobEntity.getTaktype();
        if (taskType == TaskType.GRID || taskType == TaskType.PLAN) {
            llTrainModule.setVisibility(View.GONE);
            divHeader.setVisibility(View.GONE);
            lvRecordPublisher.setVisibility(View.VISIBLE);
            tvPerformerTitle.setVisibility(View.VISIBLE);
            tvPublisherTitle.setVisibility(View.VISIBLE);
            ivRecordDiv1.setVisibility(View.VISIBLE);
            ivRecordDiv2.setVisibility(View.VISIBLE);
        } else {
            llTrainModule.setVisibility(View.VISIBLE);
            divHeader.setVisibility(View.VISIBLE);
            lvRecordPublisher.setVisibility(View.GONE);
            tvPerformerTitle.setVisibility(View.GONE);
            tvPublisherTitle.setVisibility(View.GONE);
            ivRecordDiv1.setVisibility(View.GONE);
            ivRecordDiv2.setVisibility(View.GONE);
            initTrainModuleView();
        }
        initTaskModuleView();
    }

    /**
     * 初始化车次模块数据
     */
    private void initTrainModuleView() {
        String trainNumber = mJobEntity.getTrainnumber();
        int color = 0xFFFFFFFF;
        switch (mJobEntity.getTrainlate()) {
            case RunState.LATE:
                trainNumber = trainNumber + getString(R.string.late);
                color = 0xFFE24D46;
                break;
            case RunState.LATE_UNSURE:
                trainNumber = trainNumber + getString(R.string.late_unsure);
                color = 0xFFE24D46;
                break;
            case RunState.STOP:
                trainNumber = trainNumber + getString(R.string.stop_run);
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
                tvTrainStatus.setText(R.string.tickets_check_in_progress);
                break;
            case TrainState.FINISH:
                tvTrainStatus.setText(R.string.tickets_check_finished);
                break;
            case TrainState.STOP:
                tvTrainStatus.setText(R.string.stop_tickets_check);
                break;
            default:
                tvTrainStatus.setText(R.string.tickets_check_default_state);
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
        String[] strings = ConvertUtils.formatTrainData(areaType, name);
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
                tvTitle.setTitleText(getString(R.string.task_detail));
                taskTypeName.setText(R.string.task_name);
                ivIconOrder.setVisibility(View.GONE);
                break;
            // 命令
            case TaskType.COMMAND:
                tvTitle.setTitleText(getString(R.string.command_detail));
                taskTypeName.setText(R.string.command_name);
                ivIconOrder.setImageResource(R.drawable.ic_command);
                ivIconOrder.setVisibility(View.VISIBLE);
                break;
            // 协作
            case TaskType.COOPERATE:
                tvTitle.setTitleText(getString(R.string.cooperation_detail));
                taskTypeName.setText(R.string.cooperation_name);
                ivIconOrder.setImageResource(R.drawable.ic_cooperation);
                ivIconOrder.setVisibility(View.VISIBLE);
                break;
            // 网格
            case TaskType.GRID:
                tvTitle.setTitleText(getString(R.string.grid_detail));
                taskTypeName.setText(R.string.grid_task);
                ivIconOrder.setImageResource(R.drawable.ic_grid1);
                ivIconOrder.setVisibility(View.VISIBLE);
                break;
            // 预案
            case TaskType.PLAN:
                tvTitle.setTitleText(getString(R.string.alarm_detail));
                taskTypeName.setText(R.string.alarm_task);
                ivIconOrder.setImageResource(R.drawable.ic_alarm);
                ivIconOrder.setVisibility(View.VISIBLE);
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
                btnStatus.setText(getString(R.string.click_to_start));
                // 设置持续时间
                tvLastTime.setText(String.format("%s%s", String.valueOf(lastTime), getString(R.string.minute)));
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
                btnStatus.setText(getString(R.string.click_to_finish));
                // 设置持续时间
                lastTime = (int) ((System.currentTimeMillis() - realStartTime) / (60 * 1000));
                tvLastTime.setText(String.format("%s%s", String.valueOf(lastTime), getString(R.string.minute)));
                // 判断是否超时
                isTimeOut = planEndTime < currentTime;
                if (isTimeOut) {
                    doingGif.setMovieResource(R.drawable.gif_doing_timeout);
                } else {
                    doingGif.setMovieResource(R.drawable.gif_doing);
                }
                break;
            case TaskState.DONE:
                // 已完成
                // 设置按钮背景和文字
                btnStatus.setBackgroundResource(R.drawable.btn_bg_done);
                btnStatus.setText(getString(R.string.done));
                // 设置持续时间
                lastTime = (int) ((realEndTime - realStartTime) / (60 * 1000));
                tvLastTime.setText(String.format("%s%s", String.valueOf(lastTime), getString(R.string.minute)));
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
                    btnStatus.setText(R.string.wait_acceptance);
                }
                break;
            case TaskState.GRID_NOT_PASS:
                // 验收未通过
                // 设置按钮背景和文字
                btnStatus.setBackgroundResource(R.drawable.btn_bg_restart);
                btnStatus.setText(getString(R.string.restart));
                // 设置持续时间
                lastTime = (int) ((realEndTime - realStartTime) / (60 * 1000));
                tvLastTime.setText(String.format("%s%s", String.valueOf(lastTime), getString(R.string.minute)));
                // 判断是否超时
                isTimeOut = planEndTime < realEndTime;
                if (isTimeOut) {
                    ivStatus.setImageResource(R.drawable.ic_not_pass);
                } else {
                    ivStatus.setImageResource(R.drawable.ic_not_pass);
                }
                break;
        }

        switch (workStatus) {
            case TaskState.NOT_STARTED:
            case TaskState.DONE:
            case TaskState.GRID_NOT_PASS:
                doingGif.setVisibility(View.GONE);
                ivStatus.setVisibility(View.VISIBLE);
                break;
            case TaskState.RUN:
                doingGif.setVisibility(View.VISIBLE);
                ivStatus.setVisibility(View.GONE);
                break;
        }

        // 根据是否超时设置任务模块背景色
        if (isTimeOut) {
            /*// 渐变效果
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ValueAnimator valueAnimator = ValueAnimator.ofArgb(0xFF333339, 0xFF74592C);
                valueAnimator.setDuration(2000);
                valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
                valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                valueAnimator.addUpdateListener(animation -> {
                    int colorValue = (int) animation.getAnimatedValue();
                    llBgColor.setBackgroundColor(colorValue);
                });
                valueAnimator.start();
            } else {
                llBgColor.setBackgroundResource(R.color.yellow1);
            }*/
            llBgColor.setBackgroundResource(R.color.yellow1);
            tvLastTime.setTextColor(getResources().getColor(R.color.red2));
        } else {
            llBgColor.setBackgroundResource(R.color.gray8);
            tvLastTime.setTextColor(getResources().getColor(R.color.blue1));
        }
    }

    private void initAdapter() {
        // 上传附件列表adapter
        recordAdapter = new RecordAdapter(mContext, localRecordEntities);
        // 设置上传文件具体操作
        recordAdapter.setUploadPerformer(localRecordEntity -> NetDataSource.uploadFile(getClass(),
                mJobEntity, localRecordEntity.getFile(), this));
        // 设置下载文件具体操作
        recordAdapter.setDownloadPerformer(localRecordEntity -> {
            try {
                String userId = CacheDataSource.getUserId();
                String jobOperatorsId = mJobEntity.getJoboperatorsid();
                File performerDir = DirAndFileUtils.getPerformerDir(userId, jobOperatorsId);
                NetDataSource.downloadFile(getClass(), localRecordEntity.getServerUri(),
                        performerDir.getAbsolutePath(), this);
            } catch (IOException e) {
                showToast(R.string.please_check_sdcard_state);
            }
        });
        lvRecordPerformer.setAdapter(recordAdapter);
    }

    @Override
    public void uploadProgress(File uploadFile, float progress) {
        for (LocalRecordEntity localRecordEntity : localRecordEntities) {
            if (localRecordEntity.getFile() == uploadFile) {
                localRecordEntity.setFileStatus(FileStatus.UPLOADING);
                localRecordEntity.setProgress((int) progress);
                recordAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void uploadSuccess(File uploadFile) {
        for (LocalRecordEntity localRecordEntity : localRecordEntities) {
            if (localRecordEntity.getFile() == uploadFile) {
                localRecordEntity.setFileStatus(FileStatus.SYNCHRONIZED);
                recordAdapter.notifyDataSetChanged();
                showToast(R.string.upload_success);
                break;
            }
        }
    }

    @Override
    public void uploadFailed(File uploadFile) {
        for (LocalRecordEntity localRecordEntity : localRecordEntities) {
            if (localRecordEntity.getFile() == uploadFile) {
                localRecordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                recordAdapter.notifyDataSetChanged();
                showToast(R.string.upload_failed);
                break;
            }
        }
    }

    @Override
    public void downloadProgress(String serverUri, float progress) {
        for (LocalRecordEntity localRecordEntity : localRecordEntities) {
            if (equals(localRecordEntity.getServerUri(), serverUri)) {
                localRecordEntity.setFileStatus(FileStatus.DOWNLOADING);
                localRecordEntity.setProgress((int) progress);
                recordAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void downloadSuccess(String serverUri) {
        for (LocalRecordEntity localRecordEntity : localRecordEntities) {
            if (equals(localRecordEntity.getServerUri(), serverUri)) {
                localRecordEntity.setFileStatus(FileStatus.SYNCHRONIZED);
                recordAdapter.notifyDataSetChanged();
                showToast(R.string.download_success);
                break;
            }
        }
    }

    @Override
    public void downloadFailed(String serverUri) {
        for (LocalRecordEntity localRecordEntity : localRecordEntities) {
            if (equals(localRecordEntity.getServerUri(), serverUri)) {
                localRecordEntity.setFileStatus(FileStatus.NO_DOWNLOAD);
                recordAdapter.notifyDataSetChanged();
                showToast(R.string.download_failed);
                break;
            }
        }
    }

    /**
     * 获取本地保存的/已经上传到服务器的附件记录
     */
    private void initData() {
        String jobOperatorsId = mJobEntity.getJoboperatorsid();
        ArrayList<LocalRecordEntity> localFile = mPresenter.getLocalFile(jobOperatorsId);
        localRecordEntities.addAll(localFile);
        recordAdapter.notifyDataSetChanged();
        if (localFile.size() != 0) ViewUtils.setListViewHeightBasedOnChildren(lvRecordPerformer);
        mPresenter.getServerFile(jobOperatorsId);
    }

    /**
     * 获取上传到服务器的附件记录成功
     *
     * @param serverRecordFileList 上传到服务器的附件列表
     */
    @Override
    public void onServerFileGetSuccess(List<ServerRecordEntity> serverRecordFileList) {
        boolean needRecalculateHeight = false;
        for (ServerRecordEntity entity : serverRecordFileList) {
            String[] split = entity.getFilepath().split("/");
            String fileName = split[split.length - 1];
            int position = checkFileExist(fileName);
            if (position == -1) {
                // 本地没有
                LocalRecordEntity localRecordEntity = new LocalRecordEntity();
                localRecordEntity.setFileType(Integer.valueOf(entity.getType()));
                localRecordEntity.setFileStatus(FileStatus.NO_DOWNLOAD);
                localRecordEntity.setServerUri(entity.getFilepath());
                localRecordEntities.add(localRecordEntity);
                needRecalculateHeight = true;
            } else {
                // 本地存在
                LocalRecordEntity localRecordEntity = localRecordEntities.get(position);
                localRecordEntity.setFileStatus(FileStatus.SYNCHRONIZED);
            }
        }
        recordAdapter.notifyDataSetChanged();
        if (needRecalculateHeight) ViewUtils.setListViewHeightBasedOnChildren(lvRecordPerformer);
    }

    /**
     * 检查文件是否存在
     *
     * @param fileName 文件名
     * @return 不存在返回-1，存在返回该文件在集合中的position
     */
    private int checkFileExist(String fileName) {
        for (LocalRecordEntity entity : localRecordEntities) {
            if (entity.getFile().getName().equals(fileName)) {
                return localRecordEntities.indexOf(entity);
            }
        }
        return -1;
    }

    private File noteFile;
    private File photoFile;
    private File videoFile;
    private File audioFile;

    @OnClick({R.id.btn_status, R.id.btn_note, R.id.btn_broadcast, R.id.btn_take_photo, R.id.btn_video, R.id.btn_record})
    public void onViewClicked(View view) {
        Bundle bundle = new Bundle();
        String userId = CacheDataSource.getUserId();
        String jobOperatorsId = mJobEntity.getJoboperatorsid();
        switch (view.getId()) {
            case R.id.btn_status:
                // 更改任务状态按钮点击事件
                showDialog(mJobEntity.getWorkstatus() == TaskState.RUN, view);
                break;
            case R.id.btn_broadcast:
                showToast("发送广播");
                break;
            case R.id.btn_note:
                try {
                    noteFile = DirAndFileUtils.getMediaFile(DirAndFileUtils.getPerformerDir(userId, jobOperatorsId), FileType.TEXT);
                    bundle.putSerializable(AddNoteActivity.FILE, noteFile);
                    startActivityForResult(AddNoteActivity.class, CODE_NOTE, bundle);
                } catch (IOException e) {
                    showToast(R.string.please_check_sdcard_state);
                }
                break;
            case R.id.btn_take_photo:
                try {
                    photoFile = DirAndFileUtils.getMediaFile(DirAndFileUtils.getPerformerDir(userId, jobOperatorsId), FileType.PICTURE);
                    bundle.putSerializable(TakePhotoActivity.FILE, photoFile);
                    startActivityForResult(TakePhotoActivity.class, CODE_TAKE_PHOTO, bundle);
                } catch (IOException e) {
                    showToast(R.string.please_check_sdcard_state);
                }
                break;
            case R.id.btn_video:
                try {
                    videoFile = DirAndFileUtils.getMediaFile(DirAndFileUtils.getPerformerDir(userId, jobOperatorsId), FileType.VIDEO);
                    bundle.putSerializable(RecordVideoActivity.FILE, videoFile);
                    startActivityForResult(RecordVideoActivity.class, CODE_RECORD_VIDEO, bundle);
                } catch (IOException e) {
                    showToast(R.string.please_check_sdcard_state);
                }
                break;
            case R.id.btn_record:
                try {
                    audioFile = DirAndFileUtils.getMediaFile(DirAndFileUtils.getPerformerDir(userId, jobOperatorsId), FileType.AUDIO);
                    bundle.putSerializable(RecordAudioActivity.FILE, audioFile);
                    startActivityForResult(RecordAudioActivity.class, CODE_RECORD_AUDIO, bundle);
                } catch (IOException e) {
                    showToast(R.string.please_check_sdcard_state);
                }
                break;
        }
    }

    private void showDialog(boolean isFinish, View btn) {
        new AlertDialog.Builder(mContext)
                .setMessage(getString(isFinish ? R.string.sure_you_want_to_finish : R.string.sure_you_want_to_start))
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                    btn.setEnabled(false);
                    int opeType = isFinish ? OpeType.COMPLETE : OpeType.BEGIN;
                    TaskCommBiz.changeJobStatus(mContext, mJobEntity, opeType, new StateListener() {
                        @Override
                        public void onSuccess() {
                            isChangeState = true;
                            btn.setEnabled(true);
                            String currentTime = ConvertUtils.formatDate(System.currentTimeMillis(), Formatter.DATE_FORMAT1);
                            if (isFinish) {
                                mJobEntity.setWorkstatus(TaskState.DONE);
                                mJobEntity.setRealendtime(currentTime);
                            } else {
                                mJobEntity.setWorkstatus(TaskState.RUN);
                                mJobEntity.setRealstarttime(currentTime);
                            }
                            initTaskModuleView();
                        }

                        @Override
                        public void onFailed() {
                            btn.setEnabled(true);
                            showToast(R.string.change_the_state_of_failure);
                        }
                    });
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
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
                    localRecordEntities.add(localRecordEntity);
                    recordAdapter.notifyDataSetChanged();
                    ViewUtils.setListViewHeightBasedOnChildren(lvRecordPerformer);
                    // 上传
                    NetDataSource.uploadFile(getClass(), mJobEntity, photoFile, this);
                    break;
                case CODE_RECORD_VIDEO:
                    // 录像成功
                    localRecordEntity = new LocalRecordEntity();
                    localRecordEntity.setFileType(FileType.VIDEO);
                    localRecordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                    localRecordEntity.setFile(videoFile);
                    localRecordEntities.add(localRecordEntity);
                    recordAdapter.notifyDataSetChanged();
                    ViewUtils.setListViewHeightBasedOnChildren(lvRecordPerformer);
                    // 上传
                    NetDataSource.uploadFile(getClass(), mJobEntity, videoFile, this);
                    break;
                case CODE_RECORD_AUDIO:
                    // 录音成功
                    localRecordEntity = new LocalRecordEntity();
                    localRecordEntity.setFileType(FileType.AUDIO);
                    localRecordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                    localRecordEntity.setFile(audioFile);
                    localRecordEntities.add(localRecordEntity);
                    recordAdapter.notifyDataSetChanged();
                    ViewUtils.setListViewHeightBasedOnChildren(lvRecordPerformer);
                    // 上传
                    NetDataSource.uploadFile(getClass(), mJobEntity, audioFile, this);
                    break;
                case CODE_NOTE:
                    // 添加备注成功
                    if (localRecordEntities.size() == 0 || localRecordEntities.get(0).getFileType() != FileType.TEXT) {
                        localRecordEntity = new LocalRecordEntity();
                        localRecordEntity.setFileType(FileType.TEXT);
                        localRecordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                        localRecordEntity.setFile(noteFile);
                        localRecordEntities.add(0, localRecordEntity);
                    }
                    // 如果备注内容为空不显示
                    if (TextUtils.isEmpty(FileUtils.readFile2String(noteFile, "UTF-8"))) {
                        localRecordEntities.remove(0);
                    }
                    recordAdapter.notifyDataSetChanged();
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
