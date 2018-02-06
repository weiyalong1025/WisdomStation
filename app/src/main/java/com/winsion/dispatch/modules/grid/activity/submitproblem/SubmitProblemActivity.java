package com.winsion.dispatch.modules.grid.activity.submitproblem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseActivity;
import com.winsion.dispatch.capture.CaptureActivity;
import com.winsion.dispatch.modules.grid.entity.SubclassEntity;
import com.winsion.dispatch.view.TipDialog;
import com.winsion.dispatch.view.TitleView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.winsion.dispatch.capture.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN;

/**
 * Created by 10295 on 2018/2/2.
 * 上报问题界面
 */

public class SubmitProblemActivity extends BaseActivity implements SubmitProblemContact.View {
    @BindView(R.id.tv_title)
    TitleView tvTitle;
    @BindView(R.id.rl_device_info)
    RelativeLayout rlDeviceInfo;
    @BindView(R.id.tv_site)
    TextView tvSite;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.tv_subclass)
    TextView tvSubclass;
    @BindView(R.id.tv_grade)
    TextView tvGrade;
    @BindView(R.id.tv_time_limit)
    TextView tvTimeLimit;
    @BindView(R.id.et_word_content)
    EditText etWordContent;
    @BindView(R.id.lv_photo_list)
    ListView lvPhotoList;

    public static final String PATROL_DETAIL_ID = "patrolDetailId";
    // 地点
    public static final String SITE_NAME = "siteName";
    // 是否与设备相关
    public static final String DEVICE_DEPENDENT = "deviceDependent";
    // 拍照
    private static final int CODE_TAKE_PHOTO = 0;
    // 扫描二维码
    private static final int CODE_CAPTURE_QR = 1;

    private SubmitProblemContact.Presenter mPresenter;
    private String devicePatrolDetailId;
    private String siteName;
    private boolean deviceDependent;
    private TipDialog mLoadingDialog;

    @Override
    protected int setContentView() {
        return R.layout.activity_submit_problem;
    }

    @Override
    protected void start() {
        initPresenter();
        getIntentData();
        initView();
    }

    private void initPresenter() {
        mPresenter = new SubmitProblemPresenter(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        devicePatrolDetailId = intent.getStringExtra(PATROL_DETAIL_ID);
        siteName = intent.getStringExtra(SITE_NAME);
        deviceDependent = intent.getBooleanExtra(DEVICE_DEPENDENT, false);
    }

    private void initView() {
        tvTitle.setOnBackClickListener(v -> finish());
        tvTitle.setOnConfirmClickListener(v -> submit());

        if (!deviceDependent) {
            rlDeviceInfo.setVisibility(View.GONE);
        } else {
            tvSite.setText(siteName);
        }
    }

    /**
     * 上报问题
     */
    private void submit() {

    }

    @OnClick({R.id.tv_device_name, R.id.iv_scan, R.id.tv_subclass, R.id.iv_take_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_device_name:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                EditText editText = new EditText(this);
                builder.setView(editText);
                builder.setMessage("请输入设备ID");
                builder.setPositiveButton(R.string.btn_confirm, (DialogInterface dialog, int which) -> {
                    String deviceId = getText(editText);
                    if (!isEmpty(deviceId)) {
                        dialog.dismiss();
                        checkDeviceId(deviceId);
                    }
                });
                builder.setNegativeButton(R.string.cancel, (DialogInterface dialog, int which) -> dialog.dismiss());
                builder.create().show();

                int margin = getResources().getDimensionPixelSize(R.dimen.d10);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(margin, 0, margin, 0);
                editText.setLayoutParams(layoutParams);
                break;
            case R.id.iv_scan:
                startActivityForResult(CaptureActivity.class, CODE_CAPTURE_QR);
                break;
            case R.id.tv_subclass:
                break;
            case R.id.iv_take_photo:
                break;
        }
    }

    private void checkDeviceId(String deviceId) {
        // 查询设备编号中，显示dialog
        if (mLoadingDialog == null) {
            mLoadingDialog = new TipDialog.Builder(mContext)
                    .setIconType(TipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord(getString(R.string.dialog_on_search))
                    .create();
        }
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog.show();
        mPresenter.checkDeviceId(deviceId);
    }

    @Override
    public void checkDeviceIdSuccess(String deviceName, String classificationId, String deviceId) {
        mLoadingDialog.dismiss();
    }

    @Override
    public void checkDeviceIdFailed(@StringRes int errorInfo) {
        mLoadingDialog.dismiss();
        showToast(errorInfo);
    }

    @Override
    public void getSubclassSuccess(List<SubclassEntity> list) {

    }

    @Override
    public void getSubclassFailed() {

    }

    @Override
    public void submitSuccess() {

    }

    @Override
    public void submitFailed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_TAKE_PHOTO:

                    break;
                case CODE_CAPTURE_QR:
                    String result = data.getStringExtra(INTENT_EXTRA_KEY_QR_SCAN);
                    showToast(result);
                    break;
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
