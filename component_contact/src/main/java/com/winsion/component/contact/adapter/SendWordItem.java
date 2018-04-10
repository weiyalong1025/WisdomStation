package com.winsion.component.contact.adapter;

import android.widget.TextView;

import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.entity.UserMessage;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.contact.R;
import com.winsion.component.contact.activity.chat.ChatPresenter;
import com.winsion.component.contact.constants.MessageStatus;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.adapter.abslistview.base.ItemViewDelegate;

import static com.winsion.component.basic.constants.MessageType.WORD;

/**
 * Created by wyl on 2017/5/24
 */
public class SendWordItem implements ItemViewDelegate<UserMessage> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.contact_item_msg_send_word;
    }

    @Override
    public boolean isForViewType(UserMessage message, int position) {
        int type = message.getType();
        String userId = CacheDataSource.getUserId();
        return type == WORD && message.getSenderId().equals(userId);
    }

    @Override
    public void convert(ViewHolder holder, UserMessage message, int position) {
        ImageLoader.loadAddress(holder.getView(R.id.iv_head), CacheDataSource.getUserHeadAddress());

        int status = message.getStatus();
        switch (status) {
            case MessageStatus.SENDING:
                holder.setVisible(R.id.iv_send_failed, false);
                holder.setVisible(R.id.pb_sending, true);
                break;
            case MessageStatus.SEND_SUCCESS:
                holder.setVisible(R.id.iv_send_failed, false);
                holder.setVisible(R.id.pb_sending, false);
                break;
            case MessageStatus.SEND_FAILED:
                holder.setVisible(R.id.iv_send_failed, true);
                holder.setVisible(R.id.pb_sending, false);
                break;
        }

        holder.setOnClickListener(R.id.fl_message_status, (view) -> {
            if (status == MessageStatus.SEND_FAILED) {
                ChatPresenter.resendMessage(message);
            }
        });

        holder.setText(R.id.chat_send_content, message.getContent());
        TextView tvContent = holder.getView(R.id.chat_send_content);
        tvContent.setMaxWidth(holder.getConvertView().getContext().getResources().getDisplayMetrics().widthPixels / 3 * 2);
    }
}
