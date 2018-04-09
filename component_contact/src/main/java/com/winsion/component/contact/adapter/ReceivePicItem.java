package com.winsion.component.contact.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.contact.R;
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
public class ReceivePicItem implements ItemViewDelegate<UserMessage> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.contact_item_msg_receive_pic;
    }

    @Override
    public boolean isForViewType(UserMessage message, int position) {
        int type = message.getType();
        String userId = CacheDataSource.getUserId();
        return (type == PICTURE || type == PICTURE_GROUP) && !message.getSenderId().equals(userId);
    }

    @Override
    public void convert(ViewHolder holder, UserMessage message, int position) {
        /*Contact contact = CacheData.getContact(message.getSenderMmpId());
        if (contact != null) {
            ImageLoader.loadAddress(holder.getView(R.id.iv_head), contact.getPhotourl());
        }*/

        ChatImageView imageView = holder.getView(R.id.iv_content);
        String picPath = message.getDescription();
        ImageLoader.loadAddress(imageView, picPath);
        imageView.setOnClickListener((View v) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(picPath)), "image/*");
            holder.getConvertView().getContext().startActivity(intent);
        });
    }
}
