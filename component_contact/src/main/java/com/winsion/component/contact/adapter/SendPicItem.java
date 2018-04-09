package com.winsion.component.contact.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.contact.R;
import com.winsion.component.contact.activity.chat.ChatPresenter;
import com.winsion.component.contact.constants.MessageStatus;
import com.winsion.component.contact.entity.UserMessage;
import com.winsion.component.contact.view.ChatImageView;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.adapter.abslistview.base.ItemViewDelegate;

import java.io.File;

import static com.winsion.component.contact.constants.MessageType.PICTURE;
import static com.winsion.component.contact.constants.MessageType.PICTURE_GROUP;

/**
 * Created by wyl on 2017/5/25
 */
public class SendPicItem implements ItemViewDelegate<UserMessage> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.contact_item_msg_send_pic;
    }

    @Override
    public boolean isForViewType(UserMessage message, int position) {
        int type = message.getType();
        String userId = CacheDataSource.getUserId();
        return (type == PICTURE || type == PICTURE_GROUP) && message.getSenderId().equals(userId);
    }

    @Override
    public void convert(ViewHolder holder, UserMessage message, int position) {
        ImageLoader.loadAddress(holder.getView(R.id.iv_head), CacheDataSource.getUserHeadAddress());

        ChatImageView imageView = holder.getView(R.id.iv_content);
        String picPath = message.getDescription();
        ImageLoader.loadAddress(imageView, picPath);
        imageView.setOnClickListener((View v) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(picPath)), "image/*");
            holder.getConvertView().getContext().startActivity(intent);
        });

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
