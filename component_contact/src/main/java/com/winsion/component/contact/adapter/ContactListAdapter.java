package com.winsion.component.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.billy.cc.core.component.CC;
import com.winsion.component.basic.constants.ContactType;
import com.winsion.component.basic.constants.Formatter;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.DBDataSource;
import com.winsion.component.basic.entity.UserMessage;
import com.winsion.component.basic.utils.ConvertUtils;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.basic.utils.ToastUtils;
import com.winsion.component.contact.R;
import com.winsion.component.contact.activity.chat.ChatActivity;
import com.winsion.component.contact.constants.UserState;
import com.winsion.component.contact.entity.ContactEntity;
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

public class ContactListAdapter extends CommonAdapter<ContactEntity> {
    private final DBDataSource dbDataSource;

    public ContactListAdapter(Context context, List<ContactEntity> data) {
        super(context, R.layout.contact_item_contacts_list, data);
        dbDataSource = DBDataSource.getInstance(context);
    }

    @Override
    protected void convert(ViewHolder viewHolder, ContactEntity item, int position) {
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

        UserMessage draft = dbDataSource.getDraft(item.getConType() != ContactType.TYPE_CONTACTS,
                CacheDataSource.getUserId(), item.getConId());

        if (draft != null) {
            viewHolder.setVisible(R.id.ll_draft, true);
            viewHolder.setVisible(R.id.tv_history_text, false);
            viewHolder.setText(R.id.tv_draft, draft.getContent());
            viewHolder.setVisible(R.id.tv_time, false);
        } else {
            // 显示最后一条聊天记录
            List<UserMessage> messages = dbDataSource.getSingMessage(CacheDataSource.getUserId(), item.getConId());
            if (messages.size() != 0) {
                UserMessage userMessage = messages.get(messages.size() - 1);
                viewHolder.setVisible(R.id.ll_draft, false);
                viewHolder.setVisible(R.id.tv_history_text, true);
                int contactType = userMessage.getContactType();
                boolean isGroup = contactType != ContactType.TYPE_CONTACTS;
                String senderName = userMessage.getSenderName();
                String content = userMessage.getContent();
                viewHolder.setText(R.id.tv_history_text, isGroup ? senderName + "：" + content : content);
                viewHolder.setVisible(R.id.tv_time, true);
                viewHolder.setText(R.id.tv_time, ConvertUtils.formatDate(userMessage.getTime(), Formatter.DATE_FORMAT7));
            } else {
                viewHolder.setVisible(R.id.ll_draft, false);
                viewHolder.setVisible(R.id.tv_history_text, false);
                viewHolder.setVisible(R.id.tv_time, false);
            }
        }

        // 按钮点击事件
        viewHolder.setOnClickListener(R.id.buttonA, v -> {
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra("ContactEntity", item);
            mContext.startActivity(intent);
        });
        // 按钮点击事件
        viewHolder.setOnClickListener(R.id.buttonE, v -> CC.obtainBuilder("ComponentTask")
                .setActionName("toIssueActivity")
                .addParam("ISSUE_TYPE", 1)
                .addParam("toTeamsName", item.getConId())
                .addParam("toTeamsId", item.getConName())
                .build()
                .call());
        // 按钮点击事件
        viewHolder.setOnClickListener(R.id.buttonF, v -> CC.obtainBuilder("ComponentTask")
                .setActionName("toIssueActivity")
                .addParam("ISSUE_TYPE", 0)
                .addParam("toTeamsName", item.getConId())
                .addParam("toTeamsId", item.getConName())
                .build()
                .call());
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
