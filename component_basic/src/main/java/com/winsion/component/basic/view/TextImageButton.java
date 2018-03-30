package com.winsion.component.basic.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatImageButton;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.winsion.component.basic.R;

public class TextImageButton extends AppCompatImageButton {
    private String text;
    private int color;
    private float textSize;

    public TextImageButton(Context context) {
        this(context, null);
    }

    public TextImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextImageButton);
        text = typedArray.getString(R.styleable.TextImageButton_text);
        color = typedArray.getColor(R.styleable.TextImageButton_textColor, 0x29292F);
        float defaultTextSize = getResources().getDimension(R.dimen.basic_s18);
        textSize = typedArray.getDimension(R.styleable.TextImageButton_textSize, defaultTextSize);
        typedArray.recycle();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(text)) {
            Paint textPaint = new Paint();
            textPaint.setColor(color);
            textPaint.setTextSize(textSize);
            textPaint.setAntiAlias(true);
            textPaint.setStyle(Paint.Style.STROKE);

            Rect rect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
            Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, rect.centerX(), baseline, textPaint);
        }
    }

    public void setText(@StringRes int textRes) {
        text = getResources().getString(textRes);
        postInvalidate();
    }
}
