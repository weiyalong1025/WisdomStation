package com.winsion.component.basic.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.winsion.component.basic.R;
import com.winsion.component.basic.constants.FileStatus;
import com.winsion.component.basic.constants.FileType;
import com.winsion.component.basic.entity.LocalRecordEntity;
import com.winsion.component.basic.utils.FileUtils;
import com.winsion.component.basic.utils.ToastUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by wyl on 2017/6/22
 */
public class RecordAdapter extends CommonAdapter<LocalRecordEntity> {
    private UploadPerformer uploadPerformer;
    private DownloadPerformer downloadPerformer;

    public RecordAdapter(Context context, List<LocalRecordEntity> data) {
        super(context, R.layout.basic_item_record, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, LocalRecordEntity localRecordEntity, int position) {
        if (localRecordEntity.getFileType() != FileType.TEXT) {
            viewHolder.setVisible(R.id.ll_media, true);
            viewHolder.setVisible(R.id.tv_note, false);
            convertMediaData(viewHolder, localRecordEntity);
        } else {
            viewHolder.setVisible(R.id.ll_media, false);
            viewHolder.setVisible(R.id.tv_note, true);
            convertNoteData(viewHolder, localRecordEntity);
        }
    }

    private void convertMediaData(ViewHolder viewHolder, LocalRecordEntity localRecordEntity) {
        switch (localRecordEntity.getFileType()) {
            // 图片
            case FileType.PICTURE:
                viewHolder.setImageResource(R.id.iv_file_type, R.drawable.basic_ic_picture);
                break;
            // 视频
            case FileType.VIDEO:
                viewHolder.setImageResource(R.id.iv_file_type, R.drawable.basic_ic_video);
                break;
            // 音频
            case FileType.AUDIO:
                viewHolder.setImageResource(R.id.iv_file_type, R.drawable.basic_ic_audio);
                break;
        }

        int fileStatus = localRecordEntity.getFileStatus();

        switch (fileStatus) {
            case FileStatus.NO_UPLOAD:
            case FileStatus.UPLOADING:
            case FileStatus.SYNCHRONIZED:
            case FileStatus.NO_DOWNLOAD:
            case FileStatus.DOWNLOADING:
                viewHolder.setText(R.id.tv_file_name, localRecordEntity.getFileName());
                break;
        }

        switch (fileStatus) {
            case FileStatus.NO_UPLOAD:
            case FileStatus.NO_DOWNLOAD:
                viewHolder.setVisible(R.id.iv_status, true);
                viewHolder.setVisible(R.id.cpb_status, false);
                viewHolder.setImageResource(R.id.iv_status, R.drawable.basic_ic_failed);
                break;
            case FileStatus.UPLOADING:
            case FileStatus.DOWNLOADING:
                viewHolder.setVisible(R.id.iv_status, false);
                viewHolder.setVisible(R.id.cpb_status, true);
                viewHolder.setProgress(R.id.cpb_status, localRecordEntity.getProgress());
                break;
            case FileStatus.SYNCHRONIZED:
                viewHolder.setVisible(R.id.iv_status, true);
                viewHolder.setVisible(R.id.cpb_status, false);
                viewHolder.setImageResource(R.id.iv_status, R.drawable.basic_ic_synchronized);
                break;
        }

        viewHolder.setOnClickListener(R.id.ll_media, v -> {
            switch (localRecordEntity.getFileStatus()) {
                case FileStatus.SYNCHRONIZED:
                case FileStatus.UPLOADING:
                case FileStatus.NO_UPLOAD:
                    int fileType = localRecordEntity.getFileType();
                    String type = fileType == FileType.PICTURE ? "image/*"
                            : fileType == FileType.AUDIO ? "audio/*"
                            : fileType == FileType.VIDEO ? "video/*" : "";
                    if (type.equals("")) return;

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(localRecordEntity.getFile());
                        intent.setDataAndType(uri, type);
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        ToastUtils.showToast(mContext, R.string.toast_no_corresponding_program);
                    }

                    break;
                case FileStatus.NO_DOWNLOAD:
                    // 下载
                    if (downloadPerformer != null) {
                        downloadPerformer.download(localRecordEntity);
                    }
                    break;
            }
        });

        viewHolder.setOnClickListener(R.id.iv_status, v -> {
            // 上传
            if (localRecordEntity.getFileStatus() == FileStatus.NO_UPLOAD && uploadPerformer != null)
                uploadPerformer.upload(localRecordEntity);
        });
    }

    private void convertNoteData(ViewHolder viewHolder, LocalRecordEntity localRecordEntity) {
        int status = localRecordEntity.getFileStatus();
        String note;
        if (status == FileStatus.NO_UPLOAD || status == FileStatus.SYNCHRONIZED) {
            note = FileUtils.readFile2String(localRecordEntity.getFile(), "UTF-8");
        } else {
            note = "点击查看";
        }

        ForegroundColorSpan gray = new ForegroundColorSpan(0xFF69696D);
        String prefix = mContext.getString(R.string.name_note_content);
        SpannableStringBuilder builder = new SpannableStringBuilder()
                .append(prefix)
                .append(note);
        builder.setSpan(gray, 0, prefix.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        TextView tvNote = viewHolder.getView(R.id.tv_note);
        tvNote.setText(builder);
    }

    /**
     * 上传具体操作，上传中/上传失败/上传成功需要及时更新Adapter
     */
    public interface UploadPerformer {
        void upload(LocalRecordEntity localRecordEntity);
    }

    /**
     * 下载具体操作，下载中/下载失败/下载成功需要及时更新Adapter
     */
    public interface DownloadPerformer {
        void download(LocalRecordEntity localRecordEntity);
    }

    /**
     * 设置上传具体操作
     *
     * @param performer 上传执行器
     */
    public void setUploadPerformer(UploadPerformer performer) {
        this.uploadPerformer = performer;
    }

    /**
     * 设置下载具体操作
     *
     * @param performer 下载执行器
     */
    public void setDownloadPerformer(DownloadPerformer performer) {
        this.downloadPerformer = performer;
    }
}
