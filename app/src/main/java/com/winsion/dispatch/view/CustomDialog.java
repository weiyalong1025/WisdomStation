package com.winsion.dispatch.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.winsion.dispatch.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.winsion.dispatch.view.CustomDialog.DialogType.TYPE_CHECK_BOX;
import static com.winsion.dispatch.view.CustomDialog.DialogType.TYPE_NORMAL;
import static com.winsion.dispatch.view.CustomDialog.DialogType.TYPE_PROGRESS;
import static com.winsion.dispatch.view.CustomDialog.DialogType.TYPE_STATE;

/**
 * Created by 10295 on 2018/2/9.
 * 自定义Dialog
 */

public class CustomDialog extends AlertDialog {
    CustomDialog(@NonNull Context context) {
        this(context, R.style.TipDialog);
    }

    private CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @IntDef({TYPE_NORMAL, TYPE_STATE, TYPE_PROGRESS, TYPE_CHECK_BOX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogType {
        int TYPE_NORMAL = 0;
        int TYPE_STATE = 1;
        int TYPE_PROGRESS = 2;
        int TYPE_CHECK_BOX = 3;
    }

    /**
     * DialogType:
     * 1.普通对话框(带标题，文字，取消/确定按钮)
     * 2.显示状态对话框(带ProgressBar,状态描述文字)
     * 3.进度条对话框(带标题，文字，进度条，取消按钮)
     * 4.复选框对话框(带标题，文字，一个复选框，确定/取消按钮)
     */
    public static class Builder {
        private Context mContext; // 上下文
        private int mDialogType = TYPE_NORMAL; // 默认普通对话框
        private int mStyle = R.style.Theme_AppCompat_Dialog_Alert;
        private String mTitle;  // 对话框标题
        private String mMessage;    // 对话框文字
        private boolean mCancelable; // 是否可以取消
        private OnClickListener mNegativeButton;    // 取消按钮点击事件
        private OnClickListener mPositiveButton;    // 确定按钮点击事件

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setType(@DialogType int dialogType) {
            this.mDialogType = dialogType;
            return this;
        }

        public Builder setStyle(@StyleRes int style) {
            this.mStyle = style;
            return this;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setTitle(@StringRes int titleRes) {
            this.mTitle = mContext.getString(titleRes);
            return this;
        }

        public Builder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        public Builder setMessage(@StringRes int messageRes) {
            this.mMessage = mContext.getString(messageRes);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.mCancelable = cancelable;
            return this;
        }

        public Builder setNegativeButton(OnClickListener negativeButton) {
            this.mNegativeButton = negativeButton;
            return this;
        }

        public Builder setPositiveButton(OnClickListener positiveButton) {
            this.mPositiveButton = positiveButton;
            return this;
        }

        public CustomDialog create() {
            CustomDialog customDialog = new CustomDialog(mContext, mStyle);
            customDialog.setCancelable(mCancelable);
            switch (mDialogType) {
                case TYPE_NORMAL:
                    initNormalDialog(customDialog);
                    break;
                case TYPE_STATE:
                    initStateDialog(customDialog);
                    break;
                case TYPE_PROGRESS:
                    initProgressDialog(customDialog);
                    break;
                case TYPE_CHECK_BOX:
                    initCheckBoxDialog(customDialog);
                    break;
            }
            return customDialog;
        }

        @SuppressLint("InflateParams")
        private void initNormalDialog(AlertDialog dialog) {
            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_type_normal, null);

            TextView tvDialogTitle = dialogView.findViewById(R.id.tv_dialog_title);
            TextView tvDialogMessage = dialogView.findViewById(R.id.tv_dialog_message);
            Button btnDialogNegative = dialogView.findViewById(R.id.btn_dialog_negative);
            Button btnDialogPositive = dialogView.findViewById(R.id.btn_dialog_positive);

            if (TextUtils.isEmpty(mTitle)) {
                tvDialogTitle.setVisibility(View.GONE);
            } else {
                tvDialogTitle.setText(mTitle);
            }

            tvDialogMessage.setText(mMessage);
            btnDialogNegative.setOnClickListener(v -> {
                if (mNegativeButton != null) {
                    mNegativeButton.onClick(dialog, BUTTON_NEGATIVE);
                }
                dialog.dismiss();
            });
            btnDialogPositive.setOnClickListener(v -> {
                if (mPositiveButton != null) {
                    mPositiveButton.onClick(dialog, BUTTON_POSITIVE);
                }
                dialog.dismiss();
            });

            dialog.setView(dialogView);
        }

        private void initStateDialog(AlertDialog dialog) {

        }

        private void initProgressDialog(AlertDialog dialog) {

        }

        private void initCheckBoxDialog(AlertDialog dialog) {

        }
    }
}
