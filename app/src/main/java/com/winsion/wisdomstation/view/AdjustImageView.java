package com.winsion.wisdomstation.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by 10295 on 2017/12/22 0022.
 * 高度根据宽度自动调整的ImageView
 */

public class AdjustImageView extends AppCompatImageView {
    public AdjustImageView(Context context) {
        super(context);
    }

    public AdjustImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AdjustImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Drawable d = getDrawable();
        if (d != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
