package com.winsion.dispatch.utils.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by 10295 on 2018/1/3.
 * 列表类型
 * 对话框还是PopupWindow
 * 用来根据类型返回不同的建议最大高度
 * 对话框建议最大高度为屏幕的2/3，PopupWindow建议最大高度为1/4
 */

@IntDef({ListType.TYPE_DIALOG, ListType.TYPE_POPUP})
@Retention(RetentionPolicy.SOURCE)
public @interface ListType {
    int TYPE_DIALOG = 0;
    int TYPE_POPUP = 1;
}
