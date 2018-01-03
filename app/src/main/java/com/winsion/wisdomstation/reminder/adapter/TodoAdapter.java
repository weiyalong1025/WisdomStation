package com.winsion.wisdomstation.reminder.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.view.View;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.common.listener.ClickListener;
import com.winsion.wisdomstation.reminder.entity.TodoEntity;
import com.winsion.wisdomstation.utils.ConvertUtils;
import com.winsion.wisdomstation.utils.constants.Formatter;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 1:24
 */

public class TodoAdapter extends CommonAdapter<TodoEntity> {
    private ClickListener<TodoEntity> onItemClickListener;

    public TodoAdapter(Context context, List<TodoEntity> data) {
        super(context, R.layout.item_todo, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, TodoEntity doEntity, int position) {
        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.iv_bottom, true);
        } else {
            viewHolder.setVisible(R.id.iv_bottom, false);
        }
        String planData = ConvertUtils.formatDate(doEntity.getPlanDate(), Formatter.DATE_FORMAT1);
        String[] split = planData.split(" ");
        String[] split1 = split[0].split("-");
        // 年
        viewHolder.setText(R.id.tv_year, split1[0] + getString(R.string.year));
        // 日期
        viewHolder.setText(R.id.tv_date, split1[1] + getString(R.string.month) + split1[2]);
        // 时间
        viewHolder.setText(R.id.tv_time, split[1].substring(0, 5));
        // 事项描述
        viewHolder.setText(R.id.tv_desc, doEntity.getContent());
        viewHolder.setOnClickListener(R.id.iv_delete, (View v) ->
                new AlertDialog.Builder(mContext)
                        .setMessage(getString(R.string.sure_you_want_to_delete_it))
                        .setNegativeButton(getString(R.string.cancel),
                                (DialogInterface dialog, int which) -> dialog.cancel())
                        .setPositiveButton(getString(R.string.confirm),
                                (DialogInterface dialog, int which) -> {
                                    if (onItemClickListener != null) {
                                        onItemClickListener.onClick(mDatas.get(position));
                                    }
                                    dialog.cancel();
                                })
                        .create()
                        .show()
        );
    }

    public void setOnButtonClickListener(ClickListener<TodoEntity> listener) {
        this.onItemClickListener = listener;
    }

    private String getString(@StringRes int strRes) {
        return mContext.getString(strRes);
    }
}
