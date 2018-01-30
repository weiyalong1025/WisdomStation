//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.winsion.dispatch.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.winsion.dispatch.R;
import com.winsion.dispatch.utils.ConvertUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

public class CircleProgressBar extends ProgressBar {
    private static final int LINE = 0;
    private static final int SOLID = 1;
    private static final int SOLID_LINE = 2;
    private static final int LINEAR = 0;
    private static final int RADIAL = 1;
    private static final int SWEEP = 2;
    private static final float DEFAULT_START_DEGREE = -90.0F;
    private static final int DEFAULT_LINE_COUNT = 45;
    private static final float DEFAULT_LINE_WIDTH = 4.0F;
    private static final float DEFAULT_PROGRESS_TEXT_SIZE = 11.0F;
    private static final float DEFAULT_PROGRESS_STROKE_WIDTH = 1.0F;
    private static final String COLOR_FFF2A670 = "#fff2a670";
    private static final String COLOR_FFD3D3D5 = "#ffe3e3e5";
    private static final String DEFAULT_PATTERN = "%d%%";
    private final RectF mProgressRectF;
    private final Rect mProgressTextRect;
    private final Paint mProgressPaint;
    private final Paint mProgressBackgroundPaint;
    private final Paint mBackgroundPaint;
    private final Paint mProgressTextPaint;
    private float mRadius;
    private float mCenterX;
    private float mCenterY;
    private int mBackgroundColor;
    private int mLineCount;
    private float mLineWidth;
    private float mProgressStrokeWidth;
    private float mProgressTextSize;
    private int mProgressStartColor;
    private int mProgressEndColor;
    private int mProgressTextColor;
    private int mProgressBackgroundColor;
    private boolean mDrawProgressText;
    private String mprogressTextFormatPattern;
    private int mStyle;
    private int mShader;
    private Cap mCap;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mProgressRectF = new RectF();
        this.mProgressTextRect = new Rect();
        this.mProgressPaint = new Paint(1);
        this.mProgressBackgroundPaint = new Paint(1);
        this.mBackgroundPaint = new Paint(1);
        this.mProgressTextPaint = new Paint(1);
        this.adjustIndeterminate();
        this.initFromAttributes(context, attrs);
        this.initPaint();
    }

    private void initFromAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        this.mBackgroundColor = a.getColor(R.styleable.CircleProgressBar_background_color, 0);
        this.mDrawProgressText = a.getBoolean(R.styleable.CircleProgressBar_draw_progress_text, true);
        this.mLineCount = a.getInt(R.styleable.CircleProgressBar_line_count, 45);
        this.mprogressTextFormatPattern = a.hasValue(R.styleable.CircleProgressBar_progress_text_format_pattern) ? a.getString(R.styleable.CircleProgressBar_progress_text_format_pattern) : "%d%%";
        this.mStyle = a.getInt(R.styleable.CircleProgressBar_style, 0);
        this.mShader = a.getInt(R.styleable.CircleProgressBar_progress_shader, 0);
        this.mCap = a.hasValue(R.styleable.CircleProgressBar_progress_stroke_cap) ? Cap.values()[a.getInt(R.styleable.CircleProgressBar_progress_stroke_cap, 0)] : Cap.BUTT;
        this.mLineWidth = (float) a.getDimensionPixelSize(R.styleable.CircleProgressBar_line_width, ConvertUtils.dp2px(this.getContext(), 4.0F));
        this.mProgressTextSize = (float) a.getDimensionPixelSize(R.styleable.CircleProgressBar_progress_text_size, ConvertUtils.dp2px(this.getContext(), 11.0F));
        this.mProgressStrokeWidth = (float) a.getDimensionPixelSize(R.styleable.CircleProgressBar_progress_stroke_width, ConvertUtils.dp2px(this.getContext(), 1.0F));
        this.mProgressStartColor = a.getColor(R.styleable.CircleProgressBar_progress_start_color, Color.parseColor("#fff2a670"));
        this.mProgressEndColor = a.getColor(R.styleable.CircleProgressBar_progress_end_color, Color.parseColor("#fff2a670"));
        this.mProgressTextColor = a.getColor(R.styleable.CircleProgressBar_progress_text_color, Color.parseColor("#fff2a670"));
        this.mProgressBackgroundColor = a.getColor(R.styleable.CircleProgressBar_progress_background_color, Color.parseColor("#ffe3e3e5"));
        a.recycle();
    }

    private void initPaint() {
        this.mProgressTextPaint.setTextAlign(Align.CENTER);
        this.mProgressTextPaint.setTextSize(this.mProgressTextSize);
        this.mProgressPaint.setStyle(this.mStyle == 1 ? android.graphics.Paint.Style.FILL : android.graphics.Paint.Style.STROKE);
        this.mProgressPaint.setStrokeWidth(this.mProgressStrokeWidth);
        this.mProgressPaint.setColor(this.mProgressStartColor);
        this.mProgressPaint.setStrokeCap(this.mCap);
        this.mProgressBackgroundPaint.setStyle(this.mStyle == 1 ? android.graphics.Paint.Style.FILL : android.graphics.Paint.Style.STROKE);
        this.mProgressBackgroundPaint.setStrokeWidth(this.mProgressStrokeWidth);
        this.mProgressBackgroundPaint.setColor(this.mProgressBackgroundColor);
        this.mProgressBackgroundPaint.setStrokeCap(this.mCap);
        this.mBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
        this.mBackgroundPaint.setColor(this.mBackgroundColor);
    }

    private void updateProgressShader() {
        if (this.mProgressStartColor != this.mProgressEndColor) {
            Object shader = null;
            switch (this.mShader) {
                case 0:
                    shader = new LinearGradient(this.mProgressRectF.left, this.mProgressRectF.top, this.mProgressRectF.left, this.mProgressRectF.bottom, this.mProgressStartColor, this.mProgressEndColor, TileMode.CLAMP);
                    break;
                case 1:
                    shader = new RadialGradient(this.mCenterX, this.mCenterY, this.mRadius, this.mProgressStartColor, this.mProgressEndColor, TileMode.CLAMP);
                    break;
                case 2:
                    float radian = (float) ((double) this.mProgressStrokeWidth / 3.141592653589793D * 2.0D / (double) this.mRadius);
                    float rotateDegrees = (float) (-90.0D - (this.mCap == Cap.BUTT && this.mStyle == 2 ? 0.0D : Math.toDegrees((double) radian)));
                    shader = new SweepGradient(this.mCenterX, this.mCenterY, new int[]{this.mProgressStartColor, this.mProgressEndColor}, new float[]{0.0F, 1.0F});
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotateDegrees, this.mCenterX, this.mCenterY);
                    ((Shader) shader).setLocalMatrix(matrix);
            }

            this.mProgressPaint.setShader((Shader) shader);
        } else {
            this.mProgressPaint.setShader(null);
            this.mProgressPaint.setColor(this.mProgressStartColor);
        }

    }

    private void adjustIndeterminate() {
        try {
            Field e = ProgressBar.class.getDeclaredField("mOnlyIndeterminate");
            e.setAccessible(true);
            e.set(this, Boolean.FALSE);
            Field mIndeterminateField = ProgressBar.class.getDeclaredField("mIndeterminate");
            mIndeterminateField.setAccessible(true);
            mIndeterminateField.set(this, Boolean.FALSE);
            Field mCurrentDrawableField = ProgressBar.class.getDeclaredField("mCurrentDrawable");
            mCurrentDrawableField.setAccessible(true);
            mCurrentDrawableField.set(this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected synchronized void onDraw(Canvas canvas) {
        this.drawBackground(canvas);
        this.drawProgress(canvas);
        this.drawProgressText(canvas);
    }

    private void drawBackground(Canvas canvas) {
        if (this.mBackgroundColor != 0) {
            canvas.drawCircle(this.mCenterX, this.mCenterX, this.mRadius, this.mBackgroundPaint);
        }

    }

    private void drawProgressText(Canvas canvas) {
        if (this.mDrawProgressText) {
            String progressText = String.format(this.mprogressTextFormatPattern, this.getProgress());
            this.mProgressTextPaint.setTextSize(this.mProgressTextSize);
            this.mProgressTextPaint.setColor(this.mProgressTextColor);
            this.mProgressTextPaint.getTextBounds(progressText, 0, progressText.length(), this.mProgressTextRect);
            canvas.drawText(progressText, this.mCenterX, this.mCenterY + (float) (this.mProgressTextRect.height() / 2), this.mProgressTextPaint);
        }
    }

    private void drawProgress(Canvas canvas) {
        switch (this.mStyle) {
            case 0:
            default:
                this.drawLineProgress(canvas);
                break;
            case 1:
                this.drawSolidProgress(canvas);
                break;
            case 2:
                this.drawSolidLineProgress(canvas);
        }

    }

    private void drawLineProgress(Canvas canvas) {
        float unitDegrees = (float) (6.283185307179586D / (double) this.mLineCount);
        float outerCircleRadius = this.mRadius;
        float interCircleRadius = this.mRadius - this.mLineWidth;
        int progressLineCount = (int) ((float) this.getProgress() / (float) this.getMax() * (float) this.mLineCount);

        for (int i = 0; i < this.mLineCount; ++i) {
            float rotateDegrees = (float) i * unitDegrees;
            float startX = this.mCenterX + (float) Math.sin((double) rotateDegrees) * interCircleRadius;
            float startY = this.mCenterX - (float) Math.cos((double) rotateDegrees) * interCircleRadius;
            float stopX = this.mCenterX + (float) Math.sin((double) rotateDegrees) * outerCircleRadius;
            float stopY = this.mCenterX - (float) Math.cos((double) rotateDegrees) * outerCircleRadius;
            if (i < progressLineCount) {
                canvas.drawLine(startX, startY, stopX, stopY, this.mProgressPaint);
            } else {
                canvas.drawLine(startX, startY, stopX, stopY, this.mProgressBackgroundPaint);
            }
        }

    }

    private void drawSolidProgress(Canvas canvas) {
        canvas.drawArc(this.mProgressRectF, -90.0F, 360.0F, false, this.mProgressBackgroundPaint);
        canvas.drawArc(this.mProgressRectF, -90.0F, 360.0F * (float) this.getProgress() / (float) this.getMax(), true, this.mProgressPaint);
    }

    private void drawSolidLineProgress(Canvas canvas) {
        canvas.drawArc(this.mProgressRectF, -90.0F, 360.0F, false, this.mProgressBackgroundPaint);
        canvas.drawArc(this.mProgressRectF, -90.0F, 360.0F * (float) this.getProgress() / (float) this.getMax(), false, this.mProgressPaint);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mCenterX = (float) (w / 2);
        this.mCenterY = (float) (h / 2);
        this.mRadius = Math.min(this.mCenterX, this.mCenterY);
        this.mProgressRectF.top = this.mCenterY - this.mRadius;
        this.mProgressRectF.bottom = this.mCenterY + this.mRadius;
        this.mProgressRectF.left = this.mCenterX - this.mRadius;
        this.mProgressRectF.right = this.mCenterX + this.mRadius;
        this.updateProgressShader();
        this.mProgressRectF.inset(this.mProgressStrokeWidth / 2.0F, this.mProgressStrokeWidth / 2.0F);
    }

    public int getBackgroundColor() {
        return this.mBackgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        this.mBackgroundPaint.setColor(backgroundColor);
        this.invalidate();
    }

    public void setprogressTextFormatPattern(String progressTextFormatPattern) {
        this.mprogressTextFormatPattern = progressTextFormatPattern;
        this.invalidate();
    }

    public String getprogressTextFormatPattern() {
        return this.mprogressTextFormatPattern;
    }

    public void setProgressStrokeWidth(float progressStrokeWidth) {
        this.mProgressStrokeWidth = progressStrokeWidth;
        this.mProgressRectF.inset(this.mProgressStrokeWidth / 2.0F, this.mProgressStrokeWidth / 2.0F);
        this.invalidate();
    }

    public float getProgressStrokeWidth() {
        return this.mProgressStrokeWidth;
    }

    public void setProgressTextSize(float progressTextSize) {
        this.mProgressTextSize = progressTextSize;
        this.invalidate();
    }

    public float getProgressTextSize() {
        return this.mProgressTextSize;
    }

    public void setProgressStartColor(int progressStartColor) {
        this.mProgressStartColor = progressStartColor;
        this.updateProgressShader();
        this.invalidate();
    }

    public int getProgressStartColor() {
        return this.mProgressStartColor;
    }

    public void setProgressEndColor(int progressEndColor) {
        this.mProgressEndColor = progressEndColor;
        this.updateProgressShader();
        this.invalidate();
    }

    public int getProgressEndColor() {
        return this.mProgressEndColor;
    }

    public void setProgressTextColor(int progressTextColor) {
        this.mProgressTextColor = progressTextColor;
        this.invalidate();
    }

    public int getProgressTextColor() {
        return this.mProgressTextColor;
    }

    public void setProgressBackgroundColor(int progressBackgroundColor) {
        this.mProgressBackgroundColor = progressBackgroundColor;
        this.mProgressBackgroundPaint.setColor(this.mProgressBackgroundColor);
        this.invalidate();
    }

    public int getProgressBackgroundColor() {
        return this.mProgressBackgroundColor;
    }

    public int getLineCount() {
        return this.mLineCount;
    }

    public void setLineCount(int lineCount) {
        this.mLineCount = lineCount;
        this.invalidate();
    }

    public float getLineWidth() {
        return this.mLineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.mLineWidth = lineWidth;
        this.invalidate();
    }

    public int getStyle() {
        return this.mStyle;
    }

    public void setStyle(int style) {
        this.mStyle = style;
        this.mProgressPaint.setStyle(this.mStyle == 1 ? android.graphics.Paint.Style.FILL : android.graphics.Paint.Style.STROKE);
        this.mProgressBackgroundPaint.setStyle(this.mStyle == 1 ? android.graphics.Paint.Style.FILL : android.graphics.Paint.Style.STROKE);
        this.invalidate();
    }

    public int getShader() {
        return this.mShader;
    }

    public void setShader(int shader) {
        this.mShader = shader;
        this.updateProgressShader();
        this.invalidate();
    }

    public Cap getCap() {
        return this.mCap;
    }

    public void setCap(Cap cap) {
        this.mCap = cap;
        this.mProgressPaint.setStrokeCap(cap);
        this.mProgressBackgroundPaint.setStrokeCap(cap);
        this.invalidate();
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface ShaderMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface Style {
    }
}
