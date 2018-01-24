package com.winsion.wisdomstation.modules.operation.modules.taskoperator.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.media.adapter.RecordAdapter;
import com.winsion.wisdomstation.media.entity.RecordEntity;
import com.winsion.wisdomstation.modules.operation.constants.RunState;
import com.winsion.wisdomstation.modules.operation.constants.TaskState;
import com.winsion.wisdomstation.modules.operation.constants.TaskType;
import com.winsion.wisdomstation.modules.operation.constants.TrainState;
import com.winsion.wisdomstation.modules.operation.entity.JobEntity;
import com.winsion.wisdomstation.utils.ConvertUtils;
import com.winsion.wisdomstation.utils.constants.Formatter;
import com.winsion.wisdomstation.view.DrawableCenterTextView;
import com.winsion.wisdomstation.view.GifView;
import com.winsion.wisdomstation.view.TitleView;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 10295 on 2018/1/19.
 * 任务执行人任务详情Activity
 * 协作/命令/任务/网格/预案
 */

public class OperatorTaskDetailActivity extends BaseActivity implements OperatorTaskDetailContract.View {
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
    private ListView lvList;

    private static final String TASK_ENTITY = "taskEntity";
    private List<RecordEntity> recordEntities = new ArrayList<>();
    private JobEntity mJobEntity;
    private RecordAdapter recordAdapter;

    public static void startOperatorTaskDetailActivity(Context context, @Nonnull JobEntity jobEntity) {
        Intent intent = new Intent(context, OperatorTaskDetailActivity.class);
        intent.putExtra(TASK_ENTITY, jobEntity);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        // 由于需要绑定header到该页面，所以这里先不设置布局
        return 0;
    }

    @Override
    protected void start() {
        bindView();
        getIntentData();
        initView();
        initAdapter();
        initData();
    }

    @SuppressLint("InflateParams")
    private void bindView() {
        setContentView(R.layout.activity_operator_task_detail);
        View headerView = getLayoutInflater().inflate(R.layout.header_operation_detail, null);
        lvList = findViewById(R.id.lv_list);
        lvList.addHeaderView(headerView);
        ButterKnife.bind(this);
    }

    private void getIntentData() {
        mJobEntity = (JobEntity) getIntent().getSerializableExtra(TASK_ENTITY);
    }

    private void initView() {
        initTitleView();
        initHeader();
    }

    private void initTitleView() {
        tvTitle.setOnBackClickListener(v -> finish());
    }

    private void initHeader() {
        initTrainModuleView();
        initTaskModuleView();
    }

    /**
     * 初始化车次模块数据
     */
    private void initTrainModuleView() {
        // 网格和预案任务不显示车次模块
        int taskType = mJobEntity.getTaktype();
        if (taskType == TaskType.GRID || taskType == TaskType.PLAN) {
            llTrainModule.setVisibility(View.GONE);
            divHeader.setVisibility(View.GONE);
        } else {
            llTrainModule.setVisibility(View.VISIBLE);
            divHeader.setVisibility(View.VISIBLE);
        }


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

        if (isTimeOut) {
            llBgColor.setBackgroundResource(R.color.yellow1);
            tvLastTime.setTextColor(getResources().getColor(R.color.red2));
        } else {
            llBgColor.setBackgroundResource(R.color.gray8);
            tvLastTime.setTextColor(getResources().getColor(R.color.blue1));
        }
    }

    @OnClick({R.id.btn_take_photo, R.id.btn_video, R.id.btn_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_take_photo:
                break;
            case R.id.btn_video:
                break;
            case R.id.btn_record:
                break;
        }
    }

    private void initAdapter() {
        // 上传附件列表adapter
        recordAdapter = new RecordAdapter(mContext, recordEntities);
        lvList.setAdapter(recordAdapter);
    }

    /**
     * 获取已经上传的附件记录
     */
    private void initData() {

    }

    @Override
    public Context getContext() {
        return mContext;
    }

}
