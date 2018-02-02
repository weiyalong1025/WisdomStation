package com.winsion.dispatch.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/2/1.
 * Json工具类
 */

public class JsonUtils {
    public static <T> List<T> getTestEntities(Context context, Class<T> cls) {
        List<T> tEntities = new ArrayList<>();
        InputStream is = null;
        try {
            AssetManager assets = context.getAssets();
            String simpleName = cls.getSimpleName();
            String fileName = "testjson/" + simpleName + ".json";
            is = assets.open(fileName);
            int available = is.available();
            byte[] buffer = new byte[available];
            int read = is.read(buffer);
            if (read != -1) {
                String jsonStr = new String(buffer);
                tEntities.addAll(JSON.parseArray(jsonStr, cls));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(is);
        }
        return tEntities;
    }

    public static <T> T getTestEntity(Context context, Class<T> cls) {
        T t = null;
        InputStream is = null;
        try {
            AssetManager assets = context.getAssets();
            String simpleName = cls.getSimpleName();
            String fileName = "testjson/" + simpleName + ".json";
            is = assets.open(fileName);
            int available = is.available();
            byte[] buffer = new byte[available];
            int read = is.read(buffer);
            if (read != -1) {
                String jsonStr = new String(buffer);
                t = (JSON.parseObject(jsonStr, cls));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(is);
        }
        return t;
    }
}
