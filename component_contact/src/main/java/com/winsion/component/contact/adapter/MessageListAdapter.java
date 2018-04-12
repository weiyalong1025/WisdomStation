package com.winsion.component.contact.adapter;

import android.content.Context;

import com.winsion.component.basic.constants.ContactType;
import com.winsion.component.basic.constants.Formatter;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.DBDataSource;
import com.winsion.component.basic.entity.UserMessage;
import com.winsion.component.basic.entity.UserMessageList;
import com.winsion.component.basic.utils.ConvertUtils;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.contact.R;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 联系人列表Adapter
 */

public class MessageListAdapter extends CommonAdapter<UserMessageList> {
    private final DBDataSource dbDataSource;

    public MessageListAdapter(Context context, List<UserMessageList> data) {
        super(context, R.layout.contact_item_contacts_list, data);
        dbDataSource = DBDataSource.getInstance(context);
    }

    @Override
    protected void convert(ViewHolder viewHolder, UserMessageList item, int position) {
        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.div_bottom, true);
        } else {
            viewHolder.setVisible(R.id.div_bottom, false);
        }

        UserMessage draft = dbDataSource.getDraft(item.getContactType() != ContactType.TYPE_CONTACTS,
                CacheDataSource.getUserId(), item.getChatToId());

        if (draft != null) {
            viewHolder.setVisible(R.id.ll_draft, true);
            viewHolder.setVisible(R.id.tv_history_text, false);
            viewHolder.setText(R.id.tv_draft, draft.getContent());
            viewHolder.setVisible(R.id.tv_time, false);
        } else {
            // 显示最后一条聊天记录
            List<UserMessage> messages = dbDataSource.getSingMessage(CacheDataSource.getUserId(), item.getChatToId());
            if (messages.size() != 0) {
                UserMessage userMessage = messages.get(messages.size() - 1);
                viewHolder.setVisible(R.id.ll_draft, false);
                viewHolder.setVisible(R.id.tv_history_text, true);
                viewHolder.setVisible(R.id.tv_time, true);
                int contactType = userMessage.getContactType();
                boolean isGroup = contactType != ContactType.TYPE_CONTACTS;
                String senderName = userMessage.getSenderName();
                String content = userMessage.getContent();

                int unreadCount = item.getUnreadCount();
                String unreadCountText = "";
                if (unreadCount > 0) {
                    unreadCountText = unreadCount > 99 ? "99+" : String.valueOf(unreadCount);
                    unreadCountText = String.format("[%s条]", unreadCountText);
                }

                viewHolder.setText(R.id.tv_history_text, isGroup ? unreadCountText + senderName + "：" + content : unreadCountText + content);
                viewHolder.setText(R.id.tv_time, ConvertUtils.formatDate(userMessage.getTime(), Formatter.DATE_FORMAT7));
            } else {
                viewHolder.setVisible(R.id.ll_draft, false);
                viewHolder.setVisible(R.id.tv_history_text, false);
                viewHolder.setVisible(R.id.tv_time, false);
            }
        }

        // 设置联系人组名
        viewHolder.setText(R.id.tv_nickname, item.getChatToName());
        // 隐藏位置信息
        viewHolder.setVisible(R.id.iv_position_icon, false);
        viewHolder.setVisible(R.id.tv_position, false);

        int unreadCount = item.getUnreadCount();
        if (unreadCount > 0) {
            viewHolder.setVisible(R.id.iv_red_dot, true);
        } else {
            viewHolder.setVisible(R.id.iv_red_dot, false);
        }

        // 加载头像
        if (item.getContactType() != ContactType.TYPE_CONTACTS) {
            ImageLoader.loadRes(viewHolder.getView(R.id.iv_head), R.drawable.contact_ic_head_group);
        } else {
            ImageLoader.loadRes(viewHolder.getView(R.id.iv_head), R.drawable.basic_ic_head_single);
        }
        // 隐藏在线状态
        viewHolder.setVisible(R.id.status_online, false);
        viewHolder.setVisible(R.id.expandable, false);
    }
}
