package com.winsion.component.basic.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 支持高度值为 wrap_content 的 {@link ListView}，解决原生 {@link ListView} 在设置高度为 wrap_content 时高度计算错误的 bug。
 */

public class WrapContentListView extends ListView {
    private int mMaxHeight = Integer.MAX_VALUE >> 2;

    public WrapContentListView(Context context){
        super(context);
    }

    public WrapContentListView(Context context, int maxHeight) {
        super(context);
        mMaxHeight = maxHeight;
    }

    public WrapContentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapContentListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(mMaxHeight,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}