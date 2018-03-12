package com.winsion.component.task.activity.submitproblem;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.listener.UploadListener;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.ViewUtils;
import com.winsion.component.basic.view.CustomDialog;
import com.winsion.component.basic.view.TitleView;
import com.winsion.component.media.activity.TakePhotoActivity;
import com.winsion.component.media.adapter.RecordAdapter;
import com.winsion.component.media.biz.MediaBiz;
import com.winsion.component.media.constants.FileStatus;
import com.winsion.component.media.constants.FileType;
import com.winsion.component.media.entity.LocalRecordEntity;
import com.winsion.component.scanner.activity.CaptureActivity;
import com.winsion.component.task.R;
import com.winsion.component.task.biz.SubmitBiz;
import com.winsion.component.task.constants.DeviceState;
import com.winsion.component.task.entity.FileEntity;
import com.winsion.component.task.entity.PatrolItemEntity;
import com.winsion.component.task.entity.SubclassEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.winsion.component.media.constants.Intents.Media.MEDIA_FILE;
import static com.winsion.component.scanner.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN;
import static com.winsion.component.task.constants.Intents.SubmitProblem.DEVICE_DEPENDENT;
import static com.winsion.component.task.constants.Intents.SubmitProblem.PATROL_ITEM_ENTITY;
import static com.winsion.component.task.constants.Intents.SubmitProblem.SITE_NAME;

/**
 * Created by 10295 on 2018/2/2.
 * 上报问题界面
 */

public class SubmitProblemActivity extends BaseActivity implements SubmitProblemContact.View, UploadListener, SubmitBiz.SubmitListener {
    private TitleView tvTitle;
    private RelativeLayout rlDeviceInfo;
    private TextView tvSite;
    private TextView tvDeviceName;
    private TextView tvSubclass;
    private TextView tvGrade;
    private TextView tvTimeLimit;
    private EditText etWordContent;
    private ListView lvRecordList;

    private static final int CODE_TAKE_PHOTO = 0;   // 拍照
    private static final int CODE_CAPTURE_QR = 1;   // 扫描二维码

    private SubmitProblemContact.Presenter mPresenter;
    private PatrolItemEntity patrolItemEntity;
    private String siteName;
    private boolean deviceDependent;
    private CustomDialog customDialog;
    private File photoFile; // 拍摄的照片文件
    private List<LocalRecordEntity> localRecordEntities = new ArrayList<>(); // 上传的附件
    private RecordAdapter recordAdapter;
    private String mDeviceId;   // 设备ID
    private String mClassificationId;    // 设备对应的类别ID
    private String mSelectSubclassId;    // 所选子类ID
    private int selectSubclassPosition; // 选中的子项在列表中的位置

    @Override
    protected int setContentView() {
        return R.layout.task_activity_submit_problem;
    }

    @Override
    protected void start() {
        initPresenter();
        initView();
        initData();
        initListener();
        initAdapter();
    }

    private void initPresenter() {
        mPresenter = new SubmitProblemPresenter(this);
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        rlDeviceInfo = findViewById(R.id.rl_device_info);
        tvSite = findViewById(R.id.tv_site);
        tvDeviceName = findViewById(R.id.tv_device_name);
        tvSubclass = findViewById(R.id.tv_subclass);
        tvGrade = findViewById(R.id.tv_grade);
        tvTimeLimit = findViewById(R.id.tv_time_limit);
        etWordContent = findViewById(R.id.et_word_content);
        lvRecordList = findViewById(R.id.lv_record);
    }

    private void initData() {
        Intent intent = getIntent();
        patrolItemEntity = (PatrolItemEntity) intent.getSerializableExtra(PATROL_ITEM_ENTITY);
        siteName = intent.getStringExtra(SITE_NAME);
        deviceDependent = intent.getBooleanExtra(DEVICE_DEPENDENT, false);
    }

    private void initListener() {
        tvTitle.setOnBackClickListener(v -> showHintDialog());
        tvTitle.setOnConfirmClickListener(v -> submit());
        addOnClickListeners(R.id.tv_device_name, R.id.iv_scan, R.id.tv_subclass, R.id.iv_take_photo);

        if (!deviceDependent) {
            rlDeviceInfo.setVisibility(View.GONE);
        } else {
            tvSite.setText(siteName);
            tvTitle.setTitleText(R.string.btn_device_repair);
        }
    }

    private void initAdapter() {
        recordAdapter = new RecordAdapter(mContext, localRecordEntities);
        lvRecordList.setAdapter(recordAdapter);
    }

    /**
     * 上报问题
     */
    private void submit() {
        // 检查数据是否填写完整
        if (checkDataIsComplete()) {
            // 隐藏软键盘
            BasicBiz.hideKeyboard(tvTitle);
            // 上报中，显示对话框
            showDoingDialog(R.string.dialog_on_submit);
            // 获取上传的附件
            ArrayList<FileEntity> fileList = new ArrayList<>();
            for (LocalRecordEntity localRecordEntity : localRecordEntities) {
                FileEntity fileEntity = new FileEntity();
                fileEntity.setFileName(localRecordEntity.getFileName());
                fileEntity.setFileType(localRecordEntity.getFileType());
                fileList.add(fileEntity);
            }

            // 上报问题
            if (deviceDependent) {
                // 设备相关问题
                ((SubmitBiz) mPresenter).submitWithDevice(patrolItemEntity, mSelectSubclassId,
                        fileList, getText(etWordContent), mDeviceId, this);
            } else {
                // 设备无关问题
                ((SubmitBiz) mPresenter).submitWithoutDevice(patrolItemEntity, DeviceState.FAILURE,
                        fileList, getText(etWordContent), this);
            }
        }
    }

    /**
     * 检查数据是否填写完整
     */
    private boolean checkDataIsComplete() {
        if (deviceDependent) {
            String deviceName = getText(tvDeviceName);
            if (isEmpty(mSelectSubclassId) || isEmpty(deviceName)) {
                showToast(R.string.toast_complete_info);
                return false;
            }
        }
        if (isEmpty(getText(etWordContent))) {
            showToast(R.string.toast_complete_info);
            return false;
        }
        for (LocalRecordEntity localRecordEntity : localRecordEntities) {
            if (localRecordEntity.getFileStatus() != FileStatus.SYNCHRONIZED) {
                showToast(getString(R.string.toast_wait_for_files_upload_complete));
                return false;
            }
        }
        return true;
    }

    /**
     * 显示进行中对话框
     *
     * @param tipWord 提示状态文字
     */
    private void showDoingDialog(@StringRes int tipWord) {
        if (customDialog == null) {
            customDialog = new CustomDialog.StateBuilder(mContext)
                    .setStateText(tipWord)
                    .setIrrevocable()
                    .create();
        } else {
            ((CustomDialog.StateBuilder) customDialog.getBuilder()).updateTipWord(tipWord);
        }
        customDialog.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_device_name) {
            // 创建对话框
            new CustomDialog.EditTextBuilder(mContext)
                    .setMessage(R.string.title_input_device_id)
                    .setPositiveButton((dialog, which) -> {
                        CustomDialog.Builder builder = ((CustomDialog) dialog).getBuilder();
                        String inputText = ((CustomDialog.EditTextBuilder) builder).getInputText();
                        if (!isEmpty(inputText)) {
                            dialog.dismiss();
                            checkDeviceId(inputText);
                        }
                    })
                    .show();
        } else if (id == R.id.iv_scan) {
            // 跳转扫描二维码界面
            startActivityForResult(CaptureActivity.class, CODE_CAPTURE_QR);
        } else if (id == R.id.tv_subclass) {
            // 选择子类(应先添加设备)
            if (isEmpty(mClassificationId)) {
                showToast(R.string.toast_add_device_first);
            } else {
                // 隐藏软键盘
                BasicBiz.hideKeyboard(etWordContent);
                // 显示查询中对话框
                showDoingDialog(R.string.dialog_on_search);
                // 获取子类数据
                mPresenter.getSubclass(mClassificationId);
            }
        } else if (id == R.id.iv_take_photo) {
            // 跳转拍照界面
            try {
                photoFile = MediaBiz.getMediaFile(DirAndFileUtils.getIssueDir(), FileType.PICTURE);
                Intent intent = new Intent(mContext, TakePhotoActivity.class);
                intent.putExtra(MEDIA_FILE, photoFile);
                startActivityForResult(intent, CODE_TAKE_PHOTO);
            } catch (IOException e) {
                showToast(R.string.toast_check_sdcard);
            }
        }
    }

    /**
     * 根据输入或扫描到的设备ID查找设备对应的问题类别
     *
     * @param deviceId 设备ID
     */
    private void checkDeviceId(String deviceId) {
        // 查询设备编号中，显示dialog
        showDoingDialog(R.string.dialog_on_search);
        mPresenter.checkDeviceId(deviceId);
    }

    @Override
    public void checkDeviceIdSuccess(String deviceName, String classificationId, String deviceId) {
        customDialog.dismiss();
        tvDeviceName.setText(deviceName);
        mClassificationId = classificationId;
        mDeviceId = deviceId;

        // 清空上一次得到的数据
        mSelectSubclassId = null;
        tvSubclass.setText("");
        tvGrade.setText("");
        tvTimeLimit.setText("");
        selectSubclassPosition = 0;
    }

    @Override
    public void checkDeviceIdFailed(@StringRes int errorInfo) {
        customDialog.dismiss();
        showToast(errorInfo);
    }

    @Override
    public void getSubclassSuccess(List<SubclassEntity> list) {
        customDialog.dismiss();
        if (list.size() == 0) {
            showToast(R.string.toast_no_subclass);
        } else {
            // 创建选择器
            List<String> nameList = new ArrayList<>();
            for (SubclassEntity subclassDto : list) {
                nameList.add(subclassDto.getTypename());
            }
            OptionsPickerView.Builder pickerBuilder = BasicBiz.getMyOptionPickerBuilder(mContext,
                    (int options1, int options2, int options3, View v1) -> {
                        SubclassEntity subclassDto = list.get(options1);
                        mSelectSubclassId = subclassDto.getId();
                        tvSubclass.setText(subclassDto.getTypename());
                        tvGrade.setText(String.valueOf(subclassDto.getPriority()));
                        int planCostTime = subclassDto.getPlancosttime();
                        tvTimeLimit.setText(String.format("%s%s", planCostTime, getString(R.string.suffix_minute)));
                        selectSubclassPosition = options1;
                    });
            OptionsPickerView<String> pickerView = new OptionsPickerView<>(pickerBuilder);
            pickerView.setPicker(nameList);
            pickerView.setSelectOptions(selectSubclassPosition);
            BasicBiz.selfAdaptionTopBar(pickerView);
            pickerView.show();
        }
    }

    @Override
    public void getSubclassFailed() {
        customDialog.dismiss();
        showToast(R.string.toast_get_subclass_failed);
    }

    @Override
    public void submitSuccess(PatrolItemEntity patrolItemEntity, String deviceState) {
        customDialog.dismiss();
        Intent intent = new Intent();
        intent.putExtra(PATROL_ITEM_ENTITY, patrolItemEntity);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void submitFailed() {
        showToast(R.string.toast_submit_failed);
        customDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_TAKE_PHOTO:
                    // 拍照成功
                    LocalRecordEntity localRecordEntity = new LocalRecordEntity();
                    localRecordEntity.setFileType(FileType.PICTURE);
                    localRecordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                    localRecordEntity.setFile(photoFile);
                    localRecordEntity.setFileName(photoFile.getName());
                    localRecordEntities.add(localRecordEntity);
                    recordAdapter.notifyDataSetChanged();
                    ViewUtils.setListViewHeightBasedOnChildren(lvRecordList);
                    // 上传
                    mPresenter.uploadFile(photoFile, this);
                    break;
                case CODE_CAPTURE_QR:
                    String result = data.getStringExtra(INTENT_EXTRA_KEY_QR_SCAN);
                    checkDeviceId(result);
                    break;
            }
        }
    }

    @Override
    public void uploadProgress(File uploadFile, int progress) {
        for (LocalRecordEntity localRecordEntity : localRecordEntities) {
            if (localRecordEntity.getFile() == uploadFile) {
                localRecordEntity.setFileStatus(FileStatus.UPLOADING);
                localRecordEntity.setProgress(progress);
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
                showToast(R.string.toast_upload_success);
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
                showToast(R.string.toast_upload_failed);
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
        new CustomDialog.NormalBuilder(mContext)
                .setMessage(R.string.dialog_after_exiting_data_will_be_cleared_are_you_sure)
                .setPositiveButton((dialog, which) -> {
                    // 删除附件
                    deleteRecordFiles();
                    finish();
                })
                .show();
    }

    /**
     * 没有上报而退出需要删除本地已经保存的附件
     */
    private void deleteRecordFiles() {
        if (localRecordEntities.size() != 0) {
            int deleteFileSize = 0;
            for (LocalRecordEntity localRecordEntity : localRecordEntities) {
                File file = localRecordEntity.getFile();
                if (file.delete()) deleteFileSize++;
            }
            if (deleteFileSize != localRecordEntities.size()) {
                showToast(R.string.toast_local_file_clear_failed);
            } else {
                showToast(R.string.toast_local_file_clear_success);
            }
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
    }
}
