package com.winsion.wisdomstation.utils;

import android.os.Environment;
import android.support.annotation.IntDef;

import com.winsion.wisdomstation.media.constants.FileType;
import com.winsion.wisdomstation.utils.constants.Formatter;
import com.winsion.wisdomstation.utils.constants.Path;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Created by 10295 on 2017/12/17 0017.
 * 文件工具类
 */

public class FilePathUtils {
    private static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String ROOT_PATH = SD_PATH + "/Dispatch";
    private static final String RECORD_PATH = "/record";
    // 任务执行人记录保存路径
    private static final String PERFORMER = "/performer";
    // 任务监控人记录保存路径
    private static final String MONITOR = "/monitor";
    // 发布命令记录保存路径
    private static final String ISSUE = "/issue";
    // 失物招领记录保存路径
    private static final String LOST = "/lost";
    private static final String RADIO_RECORD = "/radioRecord";
    private static final String IM = "/im";
    private static final String TTS_LOG = "/TTSLog";
    private static StringBuilder stringBuilder = new StringBuilder();

    private static String getRootPath() throws Exception {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Path.ROOT;
        }
        throw new Exception("Please check SDCard state!");
    }

    public static String getPerformerPath(String userId, String jobOperatorId) throws Exception {
        stringBuilder.setLength(0);
        return stringBuilder
                .append(getRootPath())
                .append(File.separator)
                .append(RECORD_PATH)
                .append(File.separator)
                .append(userId)
                .append(File.separator)
                .append(jobOperatorId)
                .append(PERFORMER).toString();
    }

    public static String getIssuePath() {
        stringBuilder.setLength(0);
        stringBuilder.append(ROOT_PATH).append(ISSUE);
        return stringBuilder.toString();
    }

    public static String getLostPath() {
        stringBuilder.setLength(0);
        stringBuilder.append(ROOT_PATH).append(LOST);
        return stringBuilder.toString();
    }

    public static String getMonitorPath(String userId, String taskId) {
        stringBuilder.setLength(0);
        stringBuilder.append(ROOT_PATH).append(MONITOR).append("/").append(userId).append("/").append(taskId);
        return stringBuilder.toString();
    }

    public static String getRadioRecordPath() {
        stringBuilder.setLength(0);
        stringBuilder.append(ROOT_PATH).append(RADIO_RECORD);
        return stringBuilder.toString();
    }

    public static String getImRecordPath(String userId) {
        stringBuilder.setLength(0);
        stringBuilder.append(ROOT_PATH).append(IM).append("/").append(userId);
        return stringBuilder.toString();
    }

    public static String getTTSLogPath() {
        stringBuilder.setLength(0);
        stringBuilder.append(ROOT_PATH).append(TTS_LOG);
        return stringBuilder.toString();
    }

    public static String getMediaFilePath(String path, @FileTypeLimit int type) {
        File mediaStorageDir = new File(path);
        if (mediaStorageDir.exists() || mediaStorageDir.mkdirs()) {
            String timeStamp = Formatter.DATE_FORMAT11.format(new Date());
            File mediaFile;
            if (type == FileType.PICTURE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "IMG_" + timeStamp + ".jpg");
            } else if (type == FileType.VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "VID_" + timeStamp + ".mp4");
            } else if (type == FileType.AUDIO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "VOI_" + timeStamp + ".aac");
            } else if (type == FileType.TEXT) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "TEXT_NOTE.txt");
            } else {
                return null;
            }
            return mediaFile.getAbsolutePath();
        }
        return null;
    }

    @IntDef({FileType.PICTURE, FileType.VIDEO, FileType.AUDIO, FileType.TEXT})
    @Retention(RetentionPolicy.SOURCE)
    @interface FileTypeLimit {
    }
}
