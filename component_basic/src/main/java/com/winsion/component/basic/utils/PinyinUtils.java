package com.winsion.component.basic.utils;

import android.support.annotation.IntDef;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 汉字转化为拼音的工具类
 *
 * @author liuyazhuang
 */
public class PinyinUtils {
    @IntDef({Type.TYPE_UPPERCASE, Type.TYPE_LOWERCASE, Type.TYPE_FIRST_UPPER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        /**
         * 全部大写
         */
        int TYPE_UPPERCASE = 0;
        /**
         * 全部小写
         */
        int TYPE_LOWERCASE = 1;
        /**
         * 首字母大写
         */
        int TYPE_FIRST_UPPER = 2;
    }

    public static String toPinYin(String str) throws BadHanyuPinyinOutputFormatCombination {
        return toPinYin(str, "", Type.TYPE_UPPERCASE);
    }

    public static String toPinYin(String str, String spera) throws BadHanyuPinyinOutputFormatCombination {
        return toPinYin(str, spera, Type.TYPE_UPPERCASE);
    }

    /**
     * 将str转换成拼音，如果不是汉字或者没有对应的拼音，则不作转换
     * 如： 明天 转换成 MINGTIAN
     *
     * @param str：要转化的汉字
     * @param spera：转化结果的分割符
     * @return
     * @throws BadHanyuPinyinOutputFormatCombination
     */
    public static String toPinYin(String str, String spera, @Type int type) {
        if (str == null || str.trim().length() == 0)
            return "";
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        if (type == Type.TYPE_UPPERCASE)
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        else
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);

        StringBuilder py = new StringBuilder();
        String temp;
        String[] t;
        try {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if ((int) c <= 128)
                    py.append(c);
                else {
                    t = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (t == null)
                        py.append(c);
                    else {
                        temp = t[0];
                        if (type == Type.TYPE_FIRST_UPPER)
                            temp = t[0].toUpperCase().charAt(0) + temp.substring(1);
                        py.append(temp).append(i == str.length() - 1 ? "" : spera);
                    }
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        return py.toString().trim();
    }
}