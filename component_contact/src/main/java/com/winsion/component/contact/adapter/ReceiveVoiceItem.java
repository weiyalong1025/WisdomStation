package com.winsion.component.contact.adapter;

import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.entity.UserMessage;
import com.winsion.component.contact.R;
import com.winsion.component.basic.constants.ContactType;
import com.winsion.component.contact.view.VoiceView;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.adapter.abslistview.base.ItemViewDelegate;

import static com.winsion.component.basic.constants.MessageType.VOICE;

/**
 * Created by wyl on 2017/5/27
 */
public class ReceiveVoiceItem implements ItemViewDelegate<UserMessage> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.contact_item_msg_receive_voice;
    }

    @Override
    public boolean isForViewType(UserMessage message, int position) {
        int type = message.getType();
        String userId = CacheDataSource.getUserId();
        return type == VOICE && !message.getSenderId().equals(userId);
    }

    @Override
    public void convert(ViewHolder holder, UserMessage message, int position) {
        /*Contact contact = CacheData.getContact(message.getSenderMmpId());
        if (contact != null) {
            ImageLoader.loadAddress(holder.getView(R.id.iv_head), contact.getPhotourl());
        }*/

        VoiceView voiceView = holder.getView(R.id.voice_left);
        voiceView.setVoiceFileDir(message.getDescription());

        if (message.getContactType() != ContactType.TYPE_CONTACTS) {
            holder.setText(R.id.tv_name, message.getSenderName());
            holder.setVisible(R.id.tv_name, true);
        } else {
            holder.setVisible(R.id.tv_name, false);
        }
    }
}
