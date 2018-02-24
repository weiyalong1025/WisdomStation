package com.winsion.dispatch.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.dispatch.R;

/**
 * Created by 10295 on 2018/2/9.
 * 自定义Dialog
 */

public class CustomDialog extends AlertDialog {
    private Builder mBuilder;

    private CustomDialog(@NonNull Context context, int themeResId, Builder builder) {
        super(context, themeResId);
        this.mBuilder = builder;
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    /**
     * DialogType:
     * 3.进度条对话框(带标题，文字，进度条，取消按钮)
     * 5.列表对话框(带标题和一个列表)
     */
    public static abstract class Builder {
        public Context mContext; // 上下文
        private boolean mCancelable = true; // 是否可以取消

        Builder(Context context) {
            mContext = context;
        }

        /**
         * 设置不可取消
         */
        public Builder setIrrevocable() {
            this.mCancelable = false;
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
            CustomDialog customDialog = new CustomDialog(mContext, R.style.Theme_AppCompat_Dialog_Alert, this);
            customDialog.setCancelable(mCancelable);
            Window window = customDialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
            setView(customDialog);
            return customDialog;
        }

        abstract void setView(CustomDialog dialog);
    }

    /**
     * 普通对话框(带标题，文字，取消，确定按钮)
     */
    public static class NormalBuilder extends Builder {
        private String mTitle;  // 对话框标题
        private String mMessage;    // 对话框文字
        private OnClickListener mNegativeButton;    // 取消按钮点击事件
        private OnClickListener mPositiveButton;    // 确定按钮点击事件
        private int mNegativeButtonText = R.string.btn_cancel;  // 取消按钮显示字体
        private int mPositiveButtonText = R.string.btn_confirm; // 确定按钮显示字体

        public NormalBuilder(Context context) {
            super(context);
        }

        /**
         * 设置标题，如果不设置，标题部分将不可见
         */
        public NormalBuilder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        /**
         * 设置标题，如果不设置，标题部分将不可见
         */
        public NormalBuilder setTitle(@StringRes int titleRes) {
            this.mTitle = mContext.getString(titleRes);
            return this;
        }

        /**
         * 设置文字信息
         */
        public NormalBuilder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        /**
         * 设置文字信息
         */
        public NormalBuilder setMessage(@StringRes int messageRes) {
            this.mMessage = mContext.getString(messageRes);
            return this;
        }

        /**
         * 取消按钮点击事件
         */
        public NormalBuilder setNegativeButton(OnClickListener negativeButton) {
            this.mNegativeButton = negativeButton;
            return this;
        }

        /**
         * 取消按钮显示文字
         */
        public NormalBuilder setNegativeButtonText(@StringRes int negativeButtonText) {
            this.mNegativeButtonText = negativeButtonText;
            return this;
        }

        /**
         * 确定按钮点击事件
         */
        public NormalBuilder setPositiveButton(OnClickListener positiveButton) {
            this.mPositiveButton = positiveButton;
            return this;
        }

        /**
         * 确定按钮显示文字
         */
        public NormalBuilder setPositiveButtonText(@StringRes int positiveButtonText) {
            this.mPositiveButtonText = positiveButtonText;
            return this;
        }

        @SuppressLint("InflateParams")
        @Override
        void setView(CustomDialog dialog) {
            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_normal, null);

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
    }

    /**
     * 显示状态对话框(带LoadingView，状态描述文字)
     */
    public static class StateBuilder extends Builder {
        private String mStateText;
        private TextView tvState;

        public StateBuilder(Context context) {
            super(context);
        }

        public StateBuilder setStateText(String stateText) {
            this.mStateText = stateText;
            return this;
        }

        public StateBuilder setStateText(@StringRes int stateText) {
            this.mStateText = mContext.getString(stateText);
            return this;
        }

        @SuppressLint("InflateParams")
        @Override
        void setView(CustomDialog dialog) {
            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_state, null);

            tvState = dialogView.findViewById(R.id.tv_state);
            tvState.setText(mStateText);

            dialog.setView(dialogView);
        }

        /**
         * 更新状态显示文字
         */
        public void updateTipWord(String newStateText) {
            tvState.setText(newStateText);
        }

        /**
         * 更新状态显示文字
         */
        public void updateTipWord(@StringRes int newStateText) {
            tvState.setText(mContext.getString(newStateText));
        }
    }

    /**
     * 复选框对话框(带标题，文字，一个复选框，确定/取消按钮)
     */
    public static class CheckBoxBuilder extends Builder {
        private String mMessage;    // 对话框文字
        private String mCbHint; // 复选框提示文字
        private OnClickListener mNegativeButton;    // 取消按钮点击事件
        private OnClickListener mPositiveButton;    // 确定按钮点击事件
        private int mNegativeButtonText = R.string.btn_cancel;  // 取消按钮显示字体
        private int mPositiveButtonText = R.string.btn_confirm; // 确定按钮显示字体
        private AppCompatCheckBox cbDialogHint;

        public CheckBoxBuilder(Context context) {
            super(context);
        }

        /**
         * 设置文字信息
         */
        public CheckBoxBuilder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        /**
         * 设置文字信息
         */
        public CheckBoxBuilder setMessage(@StringRes int messageRes) {
            this.mMessage = mContext.getString(messageRes);
            return this;
        }

        /**
         * 设置复选框提示文字
         */
        public CheckBoxBuilder setCbHint(String hint) {
            this.mCbHint = hint;
            return this;
        }

        /**
         * 设置复选框提示文字
         */
        public CheckBoxBuilder setCbHint(@StringRes int hint) {
            this.mCbHint = mContext.getString(hint);
            return this;
        }

        /**
         * 取消按钮点击事件
         */
        public CheckBoxBuilder setNegativeButton(OnClickListener negativeButton) {
            this.mNegativeButton = negativeButton;
            return this;
        }

        /**
         * 取消按钮显示文字
         */
        public CheckBoxBuilder setNegativeButtonText(@StringRes int negativeButtonText) {
            this.mNegativeButtonText = negativeButtonText;
            return this;
        }

        /**
         * 确定按钮点击事件
         */
        public CheckBoxBuilder setPositiveButton(OnClickListener positiveButton) {
            this.mPositiveButton = positiveButton;
            return this;
        }

        /**
         * 确定按钮显示文字
         */
        public CheckBoxBuilder setPositiveButtonText(@StringRes int positiveButtonText) {
            this.mPositiveButtonText = positiveButtonText;
            return this;
        }

        @SuppressLint("InflateParams")
        @Override
        void setView(CustomDialog dialog) {
            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_check_box, null);

            TextView tvDialogMessage = dialogView.findViewById(R.id.tv_dialog_message);
            cbDialogHint = dialogView.findViewById(R.id.cb_dialog_hint);
            Button btnDialogNegative = dialogView.findViewById(R.id.btn_dialog_negative);
            Button btnDialogPositive = dialogView.findViewById(R.id.btn_dialog_positive);

            tvDialogMessage.setText(mMessage);
            cbDialogHint.setText(mCbHint);
            cbDialogHint.setChecked(true);

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

        /**
         * 获取复选框选中状态
         */
        public boolean getCheckState() {
            return cbDialogHint.isChecked();
        }
    }

    /**
     * 进度条对话框(带标题，文字，进度条，取消按钮)
     */
    public static class ProgressBuilder extends Builder {
        private String mMessage;    // 对话框文字
        private OnClickListener mNegativeButton;    // 取消按钮点击事件
        private int mNegativeButtonText = R.string.btn_cancel;  // 取消按钮显示字体
        private TextView tvDialogMessage;   // 对话框文字TextView
        private ProgressBar pbProgress; // 进度条
        private TextView tvProgressText;    // 进度文字

        public ProgressBuilder(Context context) {
            super(context);
        }

        /**
         * 设置文字信息
         */
        public ProgressBuilder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        /**
         * 设置文字信息
         */
        public ProgressBuilder setMessage(@StringRes int messageRes) {
            this.mMessage = mContext.getString(messageRes);
            return this;
        }

        /**
         * 取消按钮点击事件
         */
        public ProgressBuilder setNegativeButton(OnClickListener negativeButton) {
            this.mNegativeButton = negativeButton;
            return this;
        }

        /**
         * 取消按钮显示文字
         */
        public ProgressBuilder setNegativeButtonText(@StringRes int negativeButtonText) {
            this.mNegativeButtonText = negativeButtonText;
            return this;
        }

        @SuppressLint("InflateParams")
        @Override
        void setView(CustomDialog dialog) {
            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress, null);

            tvDialogMessage = dialogView.findViewById(R.id.tv_dialog_message);
            pbProgress = dialogView.findViewById(R.id.pb_progress);
            tvProgressText = dialogView.findViewById(R.id.tv_progress_text);
            Button btnDialogNegative = dialogView.findViewById(R.id.btn_dialog_negative);

            tvDialogMessage.setText(mMessage);
            btnDialogNegative.setOnClickListener(v -> {
                if (mNegativeButton != null) {
                    mNegativeButton.onClick(dialog, BUTTON_NEGATIVE);
                }
                dialog.dismiss();
            });
            btnDialogNegative.setText(mNegativeButtonText);

            dialog.setView(dialogView);
        }

        public void updateMessage(String message) {
            tvDialogMessage.setText(message);
        }

        public void updateMessage(@StringRes int message) {
            tvDialogMessage.setText(mContext.getString(message));
        }

        public void setProgress(@IntRange(from = 0, to = 100) int progress) {
            pbProgress.setProgress(progress);
            tvProgressText.setText(String.format("%s/100", String.valueOf(progress)));
        }
    }
}
