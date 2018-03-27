package com.winsion.component.basic.utils;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by wyl on 2017/12/7
 */
public class ImageLoader {

    /**
     * 加载网络图片
     *
     * @param view 要设置图片的ImageView
     * @param url  图片地址
     */
    public static void loadUrl(ImageView view, String url) {
        if (view == null || view.getContext() == null) {
            return;
        }
        Glide.with(view.getContext())
                .asBitmap()
                .load(url)
                .into(view);
    }

    /**
     * 加载网络图片
     *
     * @param view  要设置图片的ImageView
     * @param url   图片地址
     * @param error 加载失败显示图
     */
    public static void loadUrl(ImageView view, String url, @DrawableRes int error) {
        if (view == null || view.getContext() == null) {
            return;
        }
        Glide.with(view.getContext())
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().error(error))
                .into(view);
    }

    /**
     * 加载网络图片
     *
     * @param view        要设置图片的ImageView
     * @param url         图片地址
     * @param placeHolder 占位图
     * @param error       加载失败显示图
     */
    public static void loadUrl(ImageView view, String url, @DrawableRes int placeHolder, @DrawableRes int error) {
        if (view == null || view.getContext() == null) {
            return;
        }
        Glide.with(view.getContext())
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().placeholder(placeHolder).error(error))
                .into(view);
    }

    /**
     * 加载资源文件
     *
     * @param view  要设置图片的ImageView
     * @param resId 资源ID
     */
    public static void loadRes(ImageView view, @DrawableRes int resId) {
        if (view == null || view.getContext() == null) {
            return;
        }
        Glide.with(view.getContext())
                .asBitmap()
                .load(resId)
                .into(view);
    }

    public static void loadGif(ImageView view, @DrawableRes int drawableRes) {
        Glide.with(view.getContext())
                .asGif()
                .load(drawableRes)
                .into(view);
    }

    /**
     * 将ImageView置灰
     */
    public static void setGrey(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
    }

    /**
     * 将ImageView恢复正常
     */
    public static void setNormal(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(1);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
    }
}
