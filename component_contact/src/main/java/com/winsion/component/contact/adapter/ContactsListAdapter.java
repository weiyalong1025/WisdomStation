package com.winsion.component.contact.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.contact.R;
import com.winsion.component.contact.constants.UserState;
import com.winsion.component.contact.entity.ContactEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 联系人列表Adapter
 */

public class ContactsListAdapter extends CommonAdapter<ContactEntity> {

    public ContactsListAdapter(Context context, List<ContactEntity> data) {
        super(context, R.layout.contact_item_contacts_list, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, ContactEntity item, int position) {
        // 设置用户名，角色名，位置信息
        viewHolder.setText(R.id.tv_nickname, item.getUsername());
        if (!TextUtils.isEmpty(item.getPostname())) {
            viewHolder.setText(R.id.tv_role_name, String.format("(%s)", item.getPostname()));
        } else {
            viewHolder.setText(R.id.tv_role_name, "");
        }
        viewHolder.setText(R.id.tv_position, item.getAreaname());
        // 加载头像
        ImageLoader.loadUrl(viewHolder.getView(R.id.iv_head), item.getPhotourl());

        //用户在线状态
        if (TextUtils.equals(item.getLoginstatus(), UserState.ON_LINE)) {
            viewHolder.setVisible(R.id.status_online, true);
            ImageLoader.setNormal(viewHolder.getView(R.id.iv_head));
        } else {
            viewHolder.setVisible(R.id.status_online, false);
            ImageLoader.setGrey(viewHolder.getView(R.id.iv_head));
        }

        /*// 显示草稿
        String draft = (String) SPUtils.get(mContext, bean.getUsersid() + "cg", "");
        String newMessage = (String) SPUtils.get(mContext, bean.getUsersid() + "new", "");
        // 存在草稿显示草稿
        if (!TextUtils.isEmpty(draft)) {
            String time = (String) SPUtils.get(mContext, bean.getUsersid() + "time", "");
            viewHolder.setText(R.id.tv_history_text, draft);
            viewHolder.setText(R.id.tv_time, time);
            viewHolder.setVisibility(R.id.tv_caogao, true);
        } else {
            // 不存在草稿显示最近的一条消息
            //取最新的消息
            String time = (String) SPUtils.get(getActivity(), bean.getUsersid() + "newtime", "");
            viewHolder.setText(R.id.tv_history_text, newMessage);
            viewHolder.setText(R.id.tv_time, time);
            viewHolder.setVisibility(R.id.tv_caogao, false);
        }*/

        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.div_bottom, true);
        } else {
            viewHolder.setVisible(R.id.div_bottom, false);
        }
    }
}
