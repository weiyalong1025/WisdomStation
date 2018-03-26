package com.winsion.component.aad.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by 10295 on 2018/3/26.
 * 到发类型
 */

@IntDef({AADType.TYPE_UP, AADType.TYPE_DOWN})
@Retention(RetentionPolicy.SOURCE)
public @interface AADType {
    /**
     * 到发-上行
     */
    int TYPE_UP = 0;
    /**
     * 到发-下行
     */
    int TYPE_DOWN = 1;
}
