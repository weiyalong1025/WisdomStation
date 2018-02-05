package com.winsion.dispatch.utils;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
            percent = 2.5f;
        }
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

    @IntDef({ListType.TYPE_DIALOG, ListType.TYPE_POPUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ListType {
        int TYPE_DIALOG = 0;
        int TYPE_POPUP = 1;
    }
}
