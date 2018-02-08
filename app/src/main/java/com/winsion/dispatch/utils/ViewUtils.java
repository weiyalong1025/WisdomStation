package com.winsion.dispatch.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by 10295 on 2018/1/26
 */

public class ViewUtils {
    /**
     * 根据屏幕高度获取建议的ListView高度
     *
     * @param context    上下文
     * @param itemHeight 一条item的高度
     * @return 建议的ListView高度
     */
    public static int getSuggestMaxHeight(Context context, int itemHeight) {
        float percent = 2.0f;
        int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
        int a = (int) (heightPixels / percent);
        int b = a / itemHeight;
        return b * itemHeight;
    }

    /**
     * 数据更新之后重新计算ListView的高度
     *
     * @param listView 重新计算高度的ListView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        DisplayMetrics dm = listView.getContext().getResources().getDisplayMetrics();
        int widthSpec = View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.AT_MOST);
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View的宽高
            listItem.measure(widthSpec, 0);
            // 统计所有子项的总高度
            int measuredHeight = listItem.getMeasuredHeight();
            totalHeight += measuredHeight;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
