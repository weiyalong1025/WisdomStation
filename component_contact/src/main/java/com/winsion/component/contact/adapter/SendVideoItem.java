package com.winsion.component.contact.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;

import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.contact.R;
import com.winsion.component.contact.activity.chat.ChatPresenter;
import com.winsion.component.contact.constants.MessageStatus;
import com.winsion.component.contact.entity.UserMessage;
import com.winsion.component.contact.view.ChatImageView;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.adapter.abslistview.base.ItemViewDelegate;

import java.io.ByteArrayOutputStream;
import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.winsion.component.contact.constants.MessageType.VIDEO;
import static com.winsion.component.contact.constants.MessageType.VIDEO_GROUP;

/**
 * Created by wyl on 2017/5/25
 */
public class SendVideoItem implements ItemViewDelegate<UserMessage> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.contact_item_msg_send_video;
    }

    @Override
    public boolean isForViewType(UserMessage message, int position) {
        int type = message.getType();
        String userId = CacheDataSource.getUserId();
        return (type == VIDEO || type == VIDEO_GROUP) && message.getSenderId().equals(userId);
    }

    @SuppressLint("CheckResult")
    @Override
    public void convert(ViewHolder holder, UserMessage message, int position) {
        ImageLoader.loadAddress(holder.getView(R.id.iv_head), CacheDataSource.getUserHeadAddress());

        String videoPath = message.getDescription();
        Observable.just(videoPath)
                .map((v) -> {
                    MediaMetadataRetriever media = new MediaMetadataRetriever();
                    media.setDataSource(v);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    media.getFrameAtTime(0).compress(Bitmap.CompressFormat.PNG, 100, out);
                    return out.toByteArray();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((byte[] bitmap) -> {
                    ChatImageView imageView = holder.getView(R.id.iv_content);
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length));
                    FrameLayout clickToPlay = holder.getView(R.id.click_to_play);
                    clickToPlay.setOnClickListener((View v) -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(videoPath)), "video/*");
                        holder.getConvertView().getContext().startActivity(intent);
                    });
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
