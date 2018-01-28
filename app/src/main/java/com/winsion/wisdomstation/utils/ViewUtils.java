package com.winsion.wisdomstation.utils;

import android.content.Context;

import com.winsion.wisdomstation.utils.constants.ListType;

/**
 * Created by 10295 on 2018/1/26
 */

public class ViewUtils {
    /**
     * 根据屏幕高度获取建议的ListView高度
     *
     * @param context    你懂得
     * @param itemHeight 一条item的高度
     * @param listType   {@link ListType} 是什么类型上显示的ListView 可以是Dialog和PopupWindow
     * @return 建议的ListView高度
     */
    public static int getSuggestMaxHeight(Context context, int itemHeight, @ListType int listType) {
        float percent = 1.5f;
        if (listType == ListType.TYPE_DIALOG) {
            percent = 1.5f;
        } else if (listType == ListType.TYPE_POPUP) {
            percent = 3;
        }
        int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
        int a = (int) (heightPixels / percent);
        int b = a / itemHeight;
        return b * itemHeight;
    }
}
