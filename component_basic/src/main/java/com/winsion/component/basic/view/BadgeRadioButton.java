package com.winsion.component.basic.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.winsion.component.basic.R;

/**
 * Created by 10295 on 2018/1/19.
 * 可以显示角标的RadioButton
 */

public class BadgeRadioButton extends AppCompatRadioButton {
    private String number;
    private boolean showBadge;

    public BadgeRadioButton(Context context) {
        super(context);
    }

    public BadgeRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BadgeRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBadge) {
            String text = getText().toString();
            TextPaint paint = getPaint();
            Rect textRect = new Rect();
            paint.getTextBounds(text, 0, text.length() - 1, textRect);
            paint.setColor(Color.RED);
            int measuredHeight = getMeasuredHeight();
            int measuredWidth = getMeasuredWidth();
            float textWidth = paint.measureText(text);
            int textHeight = textRect.bottom - textRect.top;
            float redDotX = measuredWidth / 2 + textWidth / 2;
            int redDotY = measuredHeight / 2 - textHeight / 2;

            Paint numberPaint = new Paint();
            numberPaint.setColor(Color.WHITE);
            numberPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.basic_s9));
            numberPaint.setAntiAlias(true);
            numberPaint.setTextAlign(Paint.Align.CENTER);
            numberPaint.setTypeface(Typeface.DEFAULT_BOLD);
            numberPaint.getTextBounds(number, 0, number.length(), textRect);
            float numberWidth = numberPaint.measureText(number);
            float numberHeight = textRect.bottom - textRect.top;

            numberWidth = numberWidth > numberHeight ? numberWidth : numberHeight;
            int b = getResources().getDimensionPixelSize(R.dimen.basic_d3);

            RectF roundRect = new RectF();
            roundRect.left = redDotX - numberWidth / 2 - b;
            roundRect.top = redDotY - numberHeight / 2 - b;
            roundRect.right = redDotX + numberWidth / 2 + b;
            roundRect.bottom = redDotY + numberHeight / 2 + b;
            canvas.drawRoundRect(roundRect, getResources().getDimension(R.dimen.basic_d6), getResources().getDimension(R.dimen.basic_d6), paint);

            Paint.FontMetrics fontMetrics = numberPaint.getFontMetrics();
            float numberY = redDotY + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            canvas.drawText(number, redDotX, numberY, numberPaint);
        }
    }

    /**
     * 99以上显示99+
     * 0以下(包括0)不显示
     *
     * @param badgeNum
     */
    public void setNumber(int badgeNum) {
        if (badgeNum > 99) {
            number = "99+";
            showBadge = true;
        } else if (badgeNum <= 0) {
            showBadge = false;
        } else {
            number = String.valueOf(badgeNum);
            showBadge = true;
        }
        postInvalidate();
    }
}
