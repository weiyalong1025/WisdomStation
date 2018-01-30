package com.winsion.dispatch;

import android.annotation.SuppressLint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winsion.dispatch.base.BaseFragment;

/**
 * Created by 10295 on 2017/12/10 0010.
 */

public class PlaceHolderFragment extends BaseFragment {
    @SuppressLint("SetTextI18n")
    @Override
    protected View setContentView() {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        textView.setText("Placeholder Fragment");
        textView.setTextColor(getColor(R.color.red2));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.s21));
        return textView;
    }

    @Override
    protected void init() {

    }
}
