package com.winsion.wisdomstation.utils.constants;

/**
 * Created by 10295 on 2018/1/3.
 * 列表类型
 * 对话框还是PopupWindow
 * 用来根据类型返回不同的建议最大高度
 * 对话框建议最大高度为屏幕的2/3，PopupWindow建议最大高度为1/4
 */

public interface ListType {
    int TYPE_DIALOG = 0;
    int TYPE_POPUP = 1;
}
