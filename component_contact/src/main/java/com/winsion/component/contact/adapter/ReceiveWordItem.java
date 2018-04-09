package com.winsion.component.contact.adapter;

import android.widget.TextView;

import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.contact.R;
import com.winsion.component.contact.entity.UserMessage;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.adapter.abslistview.base.ItemViewDelegate;

import static com.winsion.component.contact.constants.MessageType.WORD;
import static com.winsion.component.contact.constants.MessageType.WORD_GROUP;

/**
 * Created by wyl on 2017/5/24
 */
public class ReceiveWordItem implements ItemViewDelegate<UserMessage> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.contact_item_msg_receive_word;
    }

    @Override
    public boolean isForViewType(UserMessage message, int position) {
        int type = message.getType();
        String userId = CacheDataSource.getUserId();
        return (type == WORD || type == WORD_GROUP) && !message.getSenderId().equals(userId);
    }

    @Override
    public void convert(ViewHolder holder, UserMessage message, int position) {
        /*Contact contact = CacheData.getContact(message.getSenderMmpId());
        if (contact != null) {
            ImageLoader.loadAddress(holder.getView(R.id.iv_head), contact.getPhotourl());
        }*/

        holder.setText(R.id.chat_from_content, message.getContent());
        TextView tvContent = holder.getView(R.id.chat_from_content);
        tvContent.setMaxWidth(holder.getConvertView().getContext().getResources().getDisplayMetrics().widthPixels / 3 * 2);
        if (message.getIsGroup()) {
            holder.setText(R.id.tv_name, message.getSenderName());
        }
    }
}
