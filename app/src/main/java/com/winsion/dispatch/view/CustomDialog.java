package com.winsion.dispatch.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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
     * 1.普通对话框(带标题，文字，取消，确定按钮)
     * 2.显示状态对话框(带ProgressBar，状态描述文字)
     * 3.进度条对话框(带标题，文字，进度条，取消按钮)
     * 4.复选框对话框(带标题，文字，一个复选框，确定/取消按钮)
     * 5.列表对话框(带标题和一个列表)
     */
    public static class Builder {
        private Context mContext; // 上下文
        private int mDialogType = TYPE_NORMAL; // 默认普通对话框
        private String mTitle;  // 对话框标题
        private String mMessage;    // 对话框文字
        private boolean mCancelable = true; // 是否可以取消
        private OnClickListener mNegativeButton;    // 取消按钮点击事件
        private OnClickListener mPositiveButton;    // 确定按钮点击事件
        private int mNegativeButtonText = R.string.btn_cancel;  // 取消按钮显示字体
        private int mPositiveButtonText = R.string.btn_confirm; // 确定按钮显示字体

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * 设置对话框类型
         */
        public Builder setType(@DialogType int dialogType) {
            this.mDialogType = dialogType;
            return this;
        }

        /**
         * 设置标题，如果不设置，标题部分将不可见
         */
        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        /**
         * 设置标题，如果不设置，标题部分将不可见
         */
        public Builder setTitle(@StringRes int titleRes) {
            this.mTitle = mContext.getString(titleRes);
            return this;
        }

        /**
         * 设置文字信息
         */
        public Builder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        /**
         * 设置文字信息
         */
        public Builder setMessage(@StringRes int messageRes) {
            this.mMessage = mContext.getString(messageRes);
            return this;
        }

        /**
         * 设置不可取消
         */
        public Builder setIrrevocable() {
            this.mCancelable = false;
            return this;
        }

        /**
         * 取消按钮点击事件
         */
        public Builder setNegativeButton(OnClickListener negativeButton) {
            this.mNegativeButton = negativeButton;
            return this;
        }

        /**
         * 取消按钮显示文字
         */
        public Builder setNegativeButtonText(@StringRes int negativeButtonText) {
            this.mNegativeButtonText = negativeButtonText;
            return this;
        }

        /**
         * 确定按钮点击事件
         */
        public Builder setPositiveButton(OnClickListener positiveButton) {
            this.mPositiveButton = positiveButton;
            return this;
        }

        /**
         * 确定按钮显示文字
         */
        public Builder setPositiveButtonText(@StringRes int positiveButtonText) {
            this.mPositiveButtonText = positiveButtonText;
            return this;
        }

        /**
         * 显示对话框，该方法包含了创建操作
         */
        public CustomDialog show() {
            final CustomDialog customDialog = create();
            customDialog.show();
            return customDialog;
        }

        /**
         * 创建对话框，但还没有显示
         */
        public CustomDialog create() {
            CustomDialog customDialog = new CustomDialog(mContext, R.style.Theme_AppCompat_Dialog_Alert);
            customDialog.setCancelable(mCancelable);
            Window window = customDialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
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

            btnDialogNegative.setText(mNegativeButtonText);
            btnDialogPositive.setText(mPositiveButtonText);

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
