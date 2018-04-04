package com.winsion.component.user.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.winsion.component.basic.listener.ClickListener;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.user.R;
import com.winsion.component.basic.entity.UserEntity;
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
        super(context, R.layout.user_item_user_option, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, UserEntity item, int position) {
        // 设置头像
        ImageView imageView = viewHolder.getView(R.id.civ_head);
        ImageLoader.loadAddress(imageView, item.getHeaderUrl(), R.drawable.basic_ic_head_single, R.drawable.basic_ic_head_single);

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
