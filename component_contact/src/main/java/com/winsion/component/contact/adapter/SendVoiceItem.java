package com.winsion.component.contact.adapter;

import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.contact.R;
import com.winsion.component.contact.activity.chat.ChatPresenter;
import com.winsion.component.contact.constants.MessageStatus;
import com.winsion.component.contact.entity.UserMessage;
import com.winsion.component.contact.view.VoiceView;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.adapter.abslistview.base.ItemViewDelegate;

import static com.winsion.component.contact.constants.MessageType.VOICE;
import static com.winsion.component.contact.constants.MessageType.VOICE_GROUP;

/**
 * Created by wyl on 2017/5/27
 */
public class SendVoiceItem implements ItemViewDelegate<UserMessage> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.contact_item_msg_send_voice;
    }

    @Override
    public boolean isForViewType(UserMessage message, int position) {
        int type = message.getType();
        String userId = CacheDataSource.getUserId();
        return (type == VOICE || type == VOICE_GROUP) && message.getSenderId().equals(userId);
    }

    @Override
    public void convert(ViewHolder holder, UserMessage message, int position) {
        ImageLoader.loadAddress(holder.getView(R.id.iv_head), CacheDataSource.getUserHeadAddress());

        VoiceView voiceView = holder.getView(R.id.voice_right);
        voiceView.setVoiceFileDir(message.getDescription());

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
    }
}
