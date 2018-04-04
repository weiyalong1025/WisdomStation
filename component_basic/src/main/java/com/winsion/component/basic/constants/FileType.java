package com.winsion.component.basic.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by wyl on 2017/6/21
 * 文件类型
 */
@IntDef({FileType.PICTURE, FileType.VIDEO, FileType.AUDIO, FileType.TEXT})
@Retention(RetentionPolicy.SOURCE)
public @interface FileType {
    /**
     * 图片
     */
    int PICTURE = 0;

    /**
     * 视频
     */
    int VIDEO = 1;

    /**
     * 音频
     */
    int AUDIO = 2;

    /**
     * 文本
     */
    int TEXT = 3;
}
