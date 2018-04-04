package com.winsion.component.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.basic.utils.ToastUtils;
import com.winsion.component.contact.R;
import com.winsion.component.contact.activity.chat.ChatActivity;
import com.winsion.component.contact.constants.UserState;
import com.winsion.component.contact.entity.ContactsEntity;
import com.winsion.component.contact.entity.ContactsGroupEntity;
import com.winsion.component.contact.entity.TeamEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 联系人列表Adapter
 */

public class ContactListAdapter<T> extends CommonAdapter<T> {

    public ContactListAdapter(Context context, List<T> data) {
        super(context, R.layout.contact_item_contacts_list, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, T item, int position) {
        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.div_bottom, true);
        } else {
            viewHolder.setVisible(R.id.div_bottom, false);
        }
        if (item instanceof ContactsEntity) {
            convertContact(viewHolder, (ContactsEntity) item);
        } else if (item instanceof TeamEntity) {
            convertTeam(viewHolder, (TeamEntity) item);
        } else if (item instanceof ContactsGroupEntity) {
            convertContactsGroup(viewHolder, (ContactsGroupEntity) item);
        }
    }

    private void convertContact(ViewHolder viewHolder, ContactsEntity contactsEntity) {
        // 设置联系人名
        viewHolder.setText(R.id.tv_nickname, contactsEntity.getUsername());
        if (!TextUtils.isEmpty(contactsEntity.getPostname())) {
            viewHolder.setText(R.id.tv_role_name, String.format("(%s)", contactsEntity.getPostname()));
        } else {
            viewHolder.setText(R.id.tv_role_name, "");
        }
        // 设置位置信息
        viewHolder.setText(R.id.tv_position, contactsEntity.getAreaname());
        // 加载头像
        ImageLoader.loadAddress(viewHolder.getView(R.id.iv_head), contactsEntity.getPhotourl(),
                R.drawable.basic_ic_head_single,
                R.drawable.basic_ic_head_single);
        // 设置在线状态
        if (TextUtils.equals(contactsEntity.getLoginstatus(), UserState.ON_LINE)) {
            viewHolder.setVisible(R.id.status_online, true);
            ImageLoader.setNormal(viewHolder.getView(R.id.iv_head));
        } else {
            viewHolder.setVisible(R.id.status_online, false);
            ImageLoader.setGrey(viewHolder.getView(R.id.iv_head));
        }
        // 隐藏对应按钮
        viewHolder.setVisible(R.id.buttonE, false);
        viewHolder.setVisible(R.id.buttonF, false);
        // 按钮点击事件
        viewHolder.setOnClickListener(R.id.buttonA, v -> {
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra("ContactEntity", contactsEntity);
            mContext.startActivity(intent);
        });
    }

    private void convertTeam(ViewHolder viewHolder, TeamEntity teamEntity) {
        // 设置班组名
        viewHolder.setText(R.id.tv_nickname, teamEntity.getTeamsName());
        viewHolder.setText(R.id.tv_role_name, String.format("(%s)", teamEntity.getUserCount() + "人"));
        // 隐藏位置信息
        viewHolder.setVisible(R.id.iv_position_icon, false);
        viewHolder.setVisible(R.id.tv_position, false);
        // 加载头像
        ImageLoader.loadRes(viewHolder.getView(R.id.iv_head), R.drawable.contact_ic_head_group);
        // 隐藏在线状态
        viewHolder.setVisible(R.id.status_online, false);
        // 隐藏对应按钮
        viewHolder.setVisible(R.id.buttonD, false);
        // 按钮点击事件
        viewHolder.setOnClickListener(R.id.buttonE, v -> {
            ToastUtils.showToast(mContext, "命令");
        });
    }

    private void convertContactsGroup(ViewHolder viewHolder, ContactsGroupEntity contactsGroupEntity) {
        // 设置联系人组名
        viewHolder.setText(R.id.tv_nickname, contactsGroupEntity.getGroupname());
        // 隐藏位置信息
        viewHolder.setVisible(R.id.iv_position_icon, false);
        viewHolder.setVisible(R.id.tv_position, false);
        // 加载头像
        ImageLoader.loadRes(viewHolder.getView(R.id.iv_head), R.drawable.contact_ic_head_group);
        // 隐藏在线状态
        viewHolder.setVisible(R.id.status_online, false);
        // 隐藏对应按钮
        viewHolder.setVisible(R.id.buttonB, false);
        viewHolder.setVisible(R.id.buttonD, false);
        viewHolder.setVisible(R.id.buttonE, false);
        viewHolder.setVisible(R.id.buttonF, false);
        // 按钮点击事件
        viewHolder.setOnClickListener(R.id.buttonC, v -> {
            ToastUtils.showToast(mContext, "对讲");
        });
    }
}
