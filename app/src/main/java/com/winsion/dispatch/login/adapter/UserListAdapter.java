package com.winsion.dispatch.login.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.winsion.dispatch.R;
import com.winsion.dispatch.common.listener.ClickListener;
import com.winsion.dispatch.login.entity.UserEntity;
import com.winsion.component.basic.utils.ImageLoader;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2018/2/8.
 * UserListAdapter
 */

public class UserListAdapter extends CommonAdapter<UserEntity> {
    private ClickListener<UserEntity> deleteBtnClickListener;

    public UserListAdapter(Context context, List<UserEntity> data) {
        super(context, R.layout.item_user_option, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, UserEntity item, int position) {
        // 设置头像
        ImageView imageView = viewHolder.getView(R.id.civ_head);
        ImageLoader.loadUrl(imageView, item.getHeaderUrl(), R.drawable.ic_head_single, R.drawable.ic_head_single);

        // 设置用户名
        String username = item.getUsername();
        viewHolder.setText(R.id.item_text, username);

        // 删除点击事件
        viewHolder.setOnClickListener(R.id.iv_delete, (v) -> {
            if (deleteBtnClickListener != null) {
                deleteBtnClickListener.onClick(item);
            }
        });
    }

    public void setDeleteBtnClickListener(ClickListener<UserEntity> listener) {
        this.deleteBtnClickListener = listener;
    }
}
