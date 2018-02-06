package com.winsion.dispatch.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winsion.dispatch.R;

/**
 * Created by 10295 on 2017/12/10 0010.
 * 通用标题导航栏
 */

public class TitleView extends RelativeLayout {

    private TextView tvTitle;
    private ImageView ivBack;
    private TextView tvConfirm;

    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView, defStyleAttr, 0);
        boolean showBackButton = typedArray.getBoolean(R.styleable.TitleView_showBackButton, true);
        boolean showConfirmButton = typedArray.getBoolean(R.styleable.TitleView_showConfirmButton, true);
        String titleText = typedArray.getString(R.styleable.TitleView_titleText);
        String confirmButtonText = typedArray.getString(R.styleable.TitleView_confirmButtonText);
        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.view_title, this);
        tvTitle = findViewById(R.id.title_view_title);
        ivBack = findViewById(R.id.title_view_back);
        tvConfirm = findViewById(R.id.title_view_confirm);

        showBackButton(showBackButton);
        showConfirmButton(showConfirmButton);
        setTitleText(titleText);
        setConfirmButtonText(TextUtils.isEmpty(confirmButtonText) ? getResources().getString(R.string.btn_confirm) : confirmButtonText);
    }

    public void showBackButton(boolean isShow) {
        ivBack.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void showConfirmButton(boolean isShow) {
        tvConfirm.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setTitleText(String title) {
        tvTitle.setText(title);
    }

    public void setConfirmButtonText(String confirmButtonText) {
        tvConfirm.setText(confirmButtonText);
    }

    public void setOnBackClickListener(OnClickListener listener) {
        ivBack.setOnClickListener(listener);
    }

    public void setOnConfirmClickListener(OnClickListener listener) {
        tvConfirm.setOnClickListener(listener);
    }

}
