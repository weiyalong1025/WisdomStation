package com.winsion.wisdomstation.modules.operation.modules.issue.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.common.biz.CommonBiz;
import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.NetDataSource;
import com.winsion.wisdomstation.data.constants.OpeCode;
import com.winsion.wisdomstation.data.constants.Urls;
import com.winsion.wisdomstation.data.listener.ResponseListener;
import com.winsion.wisdomstation.data.listener.UploadListener;
import com.winsion.wisdomstation.media.activity.RecordAudioActivity;
import com.winsion.wisdomstation.media.activity.RecordVideoActivity;
import com.winsion.wisdomstation.media.activity.TakePhotoActivity;
import com.winsion.wisdomstation.media.adapter.RecordAdapter;
import com.winsion.wisdomstation.media.constants.FileStatus;
import com.winsion.wisdomstation.media.constants.FileType;
import com.winsion.wisdomstation.media.entity.RecordEntity;
import com.winsion.wisdomstation.modules.operation.constants.TaskType;
import com.winsion.wisdomstation.modules.operation.entity.FileDto;
import com.winsion.wisdomstation.modules.operation.entity.PublishParameter;
import com.winsion.wisdomstation.modules.operation.entity.RunEntity;
import com.winsion.wisdomstation.modules.operation.entity.TeamEntity;
import com.winsion.wisdomstation.utils.ConvertUtils;
import com.winsion.wisdomstation.utils.DirAndFileUtils;
import com.winsion.wisdomstation.utils.constants.Formatter;
import com.winsion.wisdomstation.view.TipDialog;
import com.winsion.wisdomstation.view.TitleView;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 发布命令/协作界面
 * Created by wyl on 2016/8/1.
 */
public class IssueActivity extends BaseActivity implements UploadListener {
    @BindView(R.id.tv_title)
    TitleView tvTitle;
    @BindView(R.id.list_view)
    ListView listView;

    private TextView tvStation;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvTeamList;
    private EditText etTitle;
    private TextView tvTrainNumber;
    private EditText etContent;

    private static final String ISSUE_TYPE = "issueType";
    private static final String TO_TEAM_ENTITY = "toTeamEntity";

    // 选择班组
    public static final int CODE_SELECT_TEAM = 0;
    // 选择车次
    public static final int CODE_SELECT_TRAIN = 1;
    // 拍照
    public static final int CODE_TAKE_PHOTO = 2;
    // 录像
    public static final int CODE_RECORD_VIDEO = 3;
    // 录音
    public static final int CODE_RECORD_AUDIO = 4;

    // 记录选中的车站，用于回显
    private int selectStationIndex;
    // 候选车次数据
    private List<String> stationList = new ArrayList<>();

    // 选中的班组ID
    private String teamIds;
    // 选中的班组名
    private String teamNames;
    // 车次ID
    private String runsId;
    // 发布类型 命令/协作 从上个页面带过来
    private int issueType;

    // 记录 拍照/录像/录音
    private RecordAdapter recordAdapter;
    private ArrayList<RecordEntity> recordEntities = new ArrayList<>();
    private File photoFile;
    private File videoFile;
    private File audioFile;
    // 发布中显示dialog
    private TipDialog mLoadingDialog;

    /**
     * @param context   上下文
     * @param issueType 发布类型(命令<COMMAND>/协作<COOPERATE>)
     *                  {@link TaskType}
     */
    public static void startIssueActivity(Context context, @TaskType int issueType) {
        startIssueActivity(context, issueType, null);
    }

    /**
     * @param context    上下文
     * @param issueType  发布类型(命令<COMMAND>/协作<COOPERATE>)
     *                   {@link TaskType}
     * @param teamEntity 发布给班组的班组对象，可以为空
     */
    public static void startIssueActivity(Context context, @TaskType int issueType, @Nullable TeamEntity teamEntity) {
        Intent intent = new Intent(context, IssueActivity.class);
        intent.putExtra(ISSUE_TYPE, issueType);
        if (teamEntity != null) {
            intent.putExtra(TO_TEAM_ENTITY, teamEntity);
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
        initView();
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
        TeamEntity toTeamEntity = (TeamEntity) intent.getSerializableExtra(TO_TEAM_ENTITY);
        if (toTeamEntity != null) {
            teamIds = toTeamEntity.getTeamid();
            teamNames = toTeamEntity.getTeamsName();
        }
    }

    // 选择车站数据
    private void initStationData() {
        stationList.add(getString(R.string.station_name));
    }

    private void initView() {
        initTitleView();
        initHeader();
    }

    private void initTitleView() {
        tvTitle.setOnBackClickListener(v -> showHintDialog());
        tvTitle.setOnConfirmClickListener(v -> {
            // 检查数据是否填写完整
            String title = getText(etTitle);
            String content = getText(etContent);
            if (isEmpty(getText(tvStation)) || isEmpty(teamIds) || isEmpty(title) || isEmpty(runsId) || isEmpty(content)) {
                showToast(getString(R.string.please_complete_the_information));
            } else {
                for (RecordEntity recordEntity : recordEntities) {
                    if (recordEntity.getFileStatus() != FileStatus.SYNCHRONIZED) {
                        showToast(getString(R.string.please_wait_for_the_files_upload_complete));
                        return;
                    }
                }
                // 隐藏软键盘
                CommonBiz.hideKeyboard(tvTitle);
                // 发布中，显示dialog
                showOnIssueDialog();
                // 发布
                issue();
            }
        });
    }

    private void issue() {
        ArrayList<FileDto> fileList = new ArrayList<>();
        for (RecordEntity recordEntity : recordEntities) {
            FileDto fileDto = new FileDto();
            fileDto.setFileName(recordEntity.getFile().getName());
            fileDto.setFileType(recordEntity.getFileType());
            fileList.add(fileDto);
        }

        PublishParameter publishParameter = new PublishParameter();
        publishParameter.setRunsId(runsId);
        publishParameter.setSsId(CommonBiz.getBSSID(this));
        publishParameter.setUsersId(CacheDataSource.getUserId());
        publishParameter.setTaskName(getText(etTitle));
        publishParameter.setPlanEndTime(getText(tvEndTime));
        publishParameter.setTaskType(issueType);
        publishParameter.setNote("");
        publishParameter.setPlanStartTime(getText(tvStartTime));
        publishParameter.setWorkContent(getText(etContent));
        publishParameter.setMonitorTeamId(CacheDataSource.getTeamId());
        publishParameter.setAreaId("");
        publishParameter.setOperatorTeamId(teamIds);
        publishParameter.setFileList(fileList);

        NetDataSource.post(getClass(), Urls.JOb, publishParameter, OpeCode.ISSUE,
                new ResponseListener<String>() {
                    @Override
                    public String convert(String jsonStr) {
                        return jsonStr;
                    }

                    @Override
                    public void onSuccess(String result) {
                        mLoadingDialog.dismiss();
                        showToast(R.string.issue_success);
                        finish();
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mLoadingDialog.dismiss();
                        showToast(R.string.issue_failed);
                    }
                });
    }

    private void showOnIssueDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new TipDialog.Builder(mContext)
                    .setIconType(TipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord(getString(R.string.on_issue))
                    .create();
        }
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog.show();
    }

    @SuppressLint("InflateParams")
    private void initHeader() {
        View issueHeader = LayoutInflater.from(mContext).inflate(R.layout.header_issue, null);

        TextView tvPerformerGroupHint = issueHeader.findViewById(R.id.tv_performer_group_hint);
        // 命令/协作内容  hint
        etContent = issueHeader.findViewById(R.id.et_content);
        switch (issueType) {
            case TaskType.COMMAND:
                tvPerformerGroupHint.setText(R.string.command_group);
                etContent.setHint(R.string.command_content);
                break;
            case TaskType.COOPERATE:
                tvPerformerGroupHint.setText(R.string.cooperation_group);
                etContent.setHint(R.string.cooperation_content);
                break;
        }

        tvStation = issueHeader.findViewById(R.id.tv_station);
        // 选择车站点击事件
        tvStation.setOnClickListener(this::showStationPickerView);

        tvStartTime = issueHeader.findViewById(R.id.tv_start_time);
        // 开始时间点击事件
        tvStartTime.setOnClickListener((View v) -> showTimePickerView((TextView) v, 0));
        // 进入界面默任回写开始时间
        tvStartTime.setText(ConvertUtils.formatDate(System.currentTimeMillis(), Formatter.DATE_FORMAT1));

        tvEndTime = issueHeader.findViewById(R.id.tv_end_time);
        // 结束时间点击事件
        tvEndTime.setOnClickListener((View v) -> showTimePickerView((TextView) v, 1));
        // 进入界面默任回写后一天的时间
        tvEndTime.setText(ConvertUtils.formatDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24, Formatter.DATE_FORMAT1));

        // 选择班组按钮点击事件
        ImageView ivAddPerformer = issueHeader.findViewById(R.id.iv_add_performer);
        ivAddPerformer.setOnClickListener((View v) -> startActivityForResult(SelectTeamActivity.class, CODE_SELECT_TEAM));

        tvTeamList = issueHeader.findViewById(R.id.tv_team_list);
        // 回显跳转过来时传递的班组
        if (isEmpty(teamNames)) {
            tvTeamList.setText(teamNames);
        }

        // 标题
        etTitle = issueHeader.findViewById(R.id.et_title);

        // 选择车次
        tvTrainNumber = issueHeader.findViewById(R.id.et_train_number);
        tvTrainNumber.setOnClickListener((View v) -> startActivityForResult(SelectTrainActivity.class, CODE_SELECT_TRAIN));

        listView.addHeaderView(issueHeader);
    }

    private void showStationPickerView(View v) {
        // 隐藏软键盘
        CommonBiz.hideKeyboard(v);
        // 创建选择器
        OptionsPickerView optionsPickerView = CommonBiz.getMyOptionPickerBuilder(mContext,
                (int options1, int options2, int options3, View v1) -> {
                    selectStationIndex = options1;
                    tvStation.setText(stationList.get(options1));
                })
                .build();
        optionsPickerView.setPicker(stationList);
        optionsPickerView.setSelectOptions(selectStationIndex);
        CommonBiz.selfAdaptionTopBar(optionsPickerView);
        optionsPickerView.show();
    }

    /**
     * 显示时间选择器
     *
     * @param textView 点击的textView
     * @param type     开始时间/结束时间 0/1
     */
    private void showTimePickerView(TextView textView, int type) {
        // 隐藏软键盘
        CommonBiz.hideKeyboard(textView);

        // 获取回显时间
        Date currentDate;
        String str = getText(textView);
        if (isEmpty(str)) {
            currentDate = new Date(System.currentTimeMillis());
        } else {
            currentDate = new Date(ConvertUtils.parseDate(str, Formatter.DATE_FORMAT1));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        TimePickerView.OnTimeSelectListener onTimeSelectListener = (date, v) -> {
            if (type == 0) {
                // 开始时间不能晚于结束时间
                if (date.getTime() > ConvertUtils.parseDate(getText(tvEndTime), Formatter.DATE_FORMAT1)) {
                    showToast(getString(R.string.start_time_not_later_than_the_end_of_time));
                    return;
                }
            } else if (type == 1) {
                // 结束时间不能早于开始时间
                if (date.getTime() < ConvertUtils.parseDate(getText(tvStartTime), Formatter.DATE_FORMAT1)) {
                    showToast(getString(R.string.end_time_cannot_be_earlier_than_the_start_time));
                    return;
                }
            }
            textView.setText(ConvertUtils.formatDate(date.getTime(), Formatter.DATE_FORMAT1));
        };

        // 创建选择器
        TimePickerView timePickerView = CommonBiz.getMyTimePickerBuilder(mContext, onTimeSelectListener)
                .setType(new boolean[]{true, true, true, true, true, false})
                .setRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1)
                .setDate(calendar)
                .build();
        CommonBiz.selfAdaptionTopBar(timePickerView);
        timePickerView.show();
    }

    private void initAdapter() {
        recordAdapter = new RecordAdapter(mContext, recordEntities);
        recordAdapter.setUploadPerformer(recordEntity -> NetDataSource.uploadFileNoData(getClass(), recordEntity.getFile(), this));
        listView.setAdapter(recordAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            RecordEntity recordEntity;
            switch (requestCode) {
                case CODE_SELECT_TEAM:
                    Serializable serializable = data.getSerializableExtra("selectData");
                    ArrayList<TeamEntity> selectData = (ArrayList<TeamEntity>) serializable;

                    StringBuilder teamIdsSb = new StringBuilder();
                    StringBuilder teamNamesSb = new StringBuilder();
                    for (int i = 0; i < selectData.size(); i++) {
                        if (i == selectData.size() - 1) {
                            teamIdsSb.append(selectData.get(i).getTeamid());
                            teamNamesSb.append(selectData.get(i).getTeamsName());
                        } else {
                            teamIdsSb.append(selectData.get(i).getTeamid()).append(",");
                            teamNamesSb.append(selectData.get(i).getTeamsName()).append(",");
                        }
                    }

                    teamNames = teamNamesSb.toString();
                    teamIds = teamIdsSb.toString();
                    tvTeamList.setText(teamNamesSb.toString());
                    break;
                case CODE_SELECT_TRAIN:
                    RunEntity runEntity = (RunEntity) data.getSerializableExtra("runEntity");
                    runsId = runEntity.getRunsid();
                    tvTrainNumber.setText(runEntity.getTrainnumber());
                    break;
                case CODE_TAKE_PHOTO:
                    // 拍照成功
                    recordEntity = new RecordEntity();
                    recordEntity.setFileType(FileType.PICTURE);
                    recordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                    recordEntity.setFile(photoFile);
                    recordEntities.add(recordEntity);
                    recordAdapter.notifyDataSetChanged();
                    // 上传
                    NetDataSource.uploadFileNoData(getClass(), photoFile, this);
                    break;
                case CODE_RECORD_VIDEO:
                    // 录像成功
                    recordEntity = new RecordEntity();
                    recordEntity.setFileType(FileType.VIDEO);
                    recordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                    recordEntity.setFile(videoFile);
                    recordEntities.add(recordEntity);
                    recordAdapter.notifyDataSetChanged();
                    // 上传
                    NetDataSource.uploadFileNoData(getClass(), videoFile, this);
                    break;
                case CODE_RECORD_AUDIO:
                    // 录音成功
                    recordEntity = new RecordEntity();
                    recordEntity.setFileType(FileType.AUDIO);
                    recordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                    recordEntity.setFile(audioFile);
                    recordEntities.add(recordEntity);
                    recordAdapter.notifyDataSetChanged();
                    // 上传
                    NetDataSource.uploadFileNoData(getClass(), audioFile, this);
                    break;
            }
        }
    }

    @OnClick({R.id.btn_take_photo, R.id.btn_video, R.id.btn_record})
    public void onViewClicked(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.btn_take_photo:
                try {
                    photoFile = DirAndFileUtils.getMediaFile(DirAndFileUtils.getIssueDir(), FileType.PICTURE);
                    bundle.putSerializable(TakePhotoActivity.FILE, photoFile);
                    startActivityForResult(TakePhotoActivity.class, CODE_TAKE_PHOTO, bundle);
                } catch (IOException e) {
                    showToast(R.string.please_check_sdcard_state);
                }
                break;
            case R.id.btn_video:
                try {
                    videoFile = DirAndFileUtils.getMediaFile(DirAndFileUtils.getIssueDir(), FileType.VIDEO);
                    bundle.putSerializable(RecordVideoActivity.FILE, videoFile);
                    startActivityForResult(RecordVideoActivity.class, CODE_RECORD_VIDEO, bundle);
                } catch (IOException e) {
                    showToast(R.string.please_check_sdcard_state);
                }
                break;
            case R.id.btn_record:
                try {
                    audioFile = DirAndFileUtils.getMediaFile(DirAndFileUtils.getIssueDir(), FileType.AUDIO);
                    bundle.putSerializable(RecordAudioActivity.FILE, audioFile);
                    startActivityForResult(RecordAudioActivity.class, CODE_RECORD_AUDIO, bundle);
                } catch (IOException e) {
                    showToast(R.string.please_check_sdcard_state);
                }
                break;
        }
    }

    @Override
    public void uploadProgress(File uploadFile, float progress) {
        for (RecordEntity recordEntity : recordEntities) {
            if (recordEntity.getFile() == uploadFile) {
                recordEntity.setFileStatus(FileStatus.UPLOADING);
                recordEntity.setProgress((int) progress);
                recordAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void uploadSuccess(File uploadFile) {
        for (RecordEntity recordEntity : recordEntities) {
            if (recordEntity.getFile() == uploadFile) {
                recordEntity.setFileStatus(FileStatus.SYNCHRONIZED);
                recordAdapter.notifyDataSetChanged();
                showToast(R.string.upload_success);
                break;
            }
        }
    }

    @Override
    public void uploadFailed(File uploadFile) {
        for (RecordEntity recordEntity : recordEntities) {
            if (recordEntity.getFile() == uploadFile) {
                recordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                recordAdapter.notifyDataSetChanged();
                showToast(R.string.upload_failed);
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showHintDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showHintDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.will_you_clear_out_the_data_after_you_exit)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    // 删除附件
                    deleteRecordFiles();
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * 没有发布而退出需要删除本地已经保存的附件
     */
    private void deleteRecordFiles() {
        if (recordEntities.size() != 0) {
            int deleteFileSize = 0;
            for (RecordEntity recordEntity : recordEntities) {
                File file = recordEntity.getFile();
                if (file.delete()) deleteFileSize++;
            }
            if (deleteFileSize != recordEntities.size()) {
                showToast(R.string.local_file_clear_failed);
            } else {
                showToast(R.string.local_file_clear_success);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetDataSource.unSubscribe(this);
    }
}
