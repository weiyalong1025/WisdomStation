package com.winsion.wisdomstation.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.text.TextUtils;
import android.view.View;

import com.winsion.wisdomstation.utils.constants.Formatter;
import com.winsion.wisdomstation.utils.constants.TrainAreaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yalong on 2016/6/15
 */
public class ConvertUtils {
    private static final char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private ConvertUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 毫秒值格式化为对应格式日期字符串
     */
    public static String formatDate(long date, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(date));
    }

    /**
     * 对应格式日期字符串解析为毫秒值
     *
     * @return 解析失败返回0
     */
    public static long parseDate(String date, SimpleDateFormat dateFormat) {
        if (TextUtils.isEmpty(date)) {
            return 0;
        }
        try {
            return dateFormat.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 标准日期格式(2016-09-29 09:00:00)切割为月日时分(07-24 09:00)
     *
     * @return 切割后的日期   为空的话会返回----- --:--
     */
    public static String splitToMDHM(String date) {
        String mdhm = "----- --:--";
        if (TextUtils.isEmpty(date)) {
            return mdhm;
        }
        try {
            Formatter.DATE_FORMAT1.parse(date);
        } catch (Exception e) {
            return mdhm;
        }
        return date.substring(5, 16);
    }

    /**
     * 标准日期格式(2016-09-29 09:00:00)切割为时分(09:00)
     *
     * @return 切割失败返回--:--
     */
    public static String splitToHM(String date) {
        String hm = "--:--";
        if (TextUtils.isEmpty(date)) {
            return hm;
        }
        try {
            Formatter.DATE_FORMAT1.parse(date);
        } catch (Exception e) {
            return hm;
        }
        return date.substring(11, 16);
    }

    public static String formatURL(String ip, String port) {
        String url = "";
        if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(port)) {
            url += "http://" + ip + ":" + port + "/";
        }
        return url;
    }

    /**
     * 格式化车次数据
     *
     * @return {股道，站台，检票口，候车室}
     */
    public static String[] formatTrainData(String[] areaType, String[] name) {
        String track = "--";
        String platform = "--";
        String waitRoom = "--";
        String checkPort = "--";
        for (int i = 0; i < name.length; i++) {
            switch (areaType[i]) {
                case TrainAreaType.TRACK:
                    if (TextUtils.equals(track, "--")) {
                        track = name[i];
                    } else {
                        track += "," + name[i];
                    }
                    break;
                case TrainAreaType.PLATFORM:
                    if (TextUtils.equals(platform, "--")) {
                        platform = name[i];
                    } else {
                        platform += "," + name[i];
                    }
                    break;
                case TrainAreaType.WAITING_ROOM:
                    if (TextUtils.equals(waitRoom, "--")) {
                        waitRoom = name[i];
                    } else {
                        waitRoom += "," + name[i];
                    }
                    break;
                case TrainAreaType.TICKET_ENTRANCE:
                    if (TextUtils.equals(checkPort, "--")) {
                        checkPort = name[i];
                    } else {
                        checkPort += "," + name[i];
                    }
                    break;
            }
        }
        return new String[]{track, platform, waitRoom, checkPort};
    }

    public static String getDigitFromStr(String str) {
        StringBuilder str2 = new StringBuilder();
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    str2.append(str.charAt(i));
                }
            }
        }
        return str2.toString();
    }

    public static String bytes2HexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            int len = bytes.length;
            if (len <= 0) {
                return null;
            } else {
                char[] ret = new char[len << 1];
                int i = 0;

                for (int var4 = 0; i < len; ++i) {
                    ret[var4++] = hexDigits[bytes[i] >>> 4 & 15];
                    ret[var4++] = hexDigits[bytes[i] & 15];
                }

                return new String(ret);
            }
        }
    }

    public static byte[] hexString2Bytes(String hexString) {
        if (StringUtils.isSpace(hexString)) {
            return null;
        } else {
            int len = hexString.length();
            if (len % 2 != 0) {
                hexString = "0" + hexString;
                ++len;
            }

            char[] hexBytes = hexString.toUpperCase().toCharArray();
            byte[] ret = new byte[len >> 1];

            for (int i = 0; i < len; i += 2) {
                ret[i >> 1] = (byte) (hex2Dec(hexBytes[i]) << 4 | hex2Dec(hexBytes[i + 1]));
            }

            return ret;
        }
    }

    private static int hex2Dec(char hexChar) {
        if (hexChar >= 48 && hexChar <= 57) {
            return hexChar - 48;
        } else if (hexChar >= 65 && hexChar <= 70) {
            return hexChar - 65 + 10;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static byte[] chars2Bytes(char[] chars) {
        if (chars != null && chars.length > 0) {
            int len = chars.length;
            byte[] bytes = new byte[len];

            for (int i = 0; i < len; ++i) {
                bytes[i] = (byte) chars[i];
            }

            return bytes;
        } else {
            return null;
        }
    }

    public static char[] bytes2Chars(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            int len = bytes.length;
            if (len <= 0) {
                return null;
            } else {
                char[] chars = new char[len];

                for (int i = 0; i < len; ++i) {
                    chars[i] = (char) (bytes[i] & 255);
                }

                return chars;
            }
        }
    }

    public static String byte2FitSize(long byteNum) {
        return byteNum < 0L ? "shouldn't be less than zero!" : (byteNum < 1024L ?
                String.format(Locale.getDefault(), "%.3fB", (double) byteNum) :
                (byteNum < 1048576L ? String.format(Locale.getDefault(), "%.3fKB",
                        (double) byteNum / 1024.0D) : (byteNum < 1073741824L ?
                        String.format(Locale.getDefault(), "%.3fMB",
                                (double) byteNum / 1048576.0D) :
                        String.format(Locale.getDefault(), "%.3fGB",
                                (double) byteNum / 1.073741824E9D))));
    }

    public static String bytes2Bits(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int var3 = bytes.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            byte aByte = bytes[var4];

            for (int j = 7; j >= 0; --j) {
                sb.append(((aByte >> j & 1) == 0 ? '0' : '1'));
            }
        }

        return sb.toString();
    }

    public static byte[] bits2Bytes(String bits) {
        int lenMod = bits.length() % 8;
        int byteLen = bits.length() / 8;
        if (lenMod != 0) {
            StringBuilder bitsBuilder = new StringBuilder(bits);
            for (int i = lenMod; i < 8; ++i) {
                bitsBuilder.insert(0, "0");
            }
            bits = bitsBuilder.toString();

            ++byteLen;
        }

        byte[] bytes = new byte[byteLen];

        for (int i = 0; i < byteLen; ++i) {
            for (int j = 0; j < 8; ++j) {
                bytes[i] = (byte) (bytes[i] << 1);
                bytes[i] = (byte) (bytes[i] | bits.charAt(i * 8 + j) - 48);
            }
        }

        return bytes;
    }

    public static ByteArrayOutputStream input2OutputStream(InputStream is) {
        if (is == null) {
            return null;
        } else {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] b = new byte[1024];

                int len;
                while ((len = is.read(b, 0, 1024)) != -1) {
                    os.write(b, 0, len);
                }
                return os;
            } catch (IOException var8) {
                var8.printStackTrace();
            } finally {
                CloseUtils.closeIO(is);
            }

            return null;
        }
    }

    public ByteArrayInputStream output2InputStream(OutputStream out) {
        return out == null ? null : new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
    }

    public static byte[] inputStream2Bytes(InputStream is) {
        return is == null ? null : input2OutputStream(is).toByteArray();
    }

    public static InputStream bytes2InputStream(byte[] bytes) {
        return bytes != null && bytes.length > 0 ? new ByteArrayInputStream(bytes) : null;
    }

    public static byte[] outputStream2Bytes(OutputStream out) {
        return out == null ? null : ((ByteArrayOutputStream) out).toByteArray();
    }

    public static OutputStream bytes2OutputStream(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            ByteArrayOutputStream os = null;
            try {
                os = new ByteArrayOutputStream();
                os.write(bytes);
                return os;
            } catch (IOException var7) {
                var7.printStackTrace();
            } finally {
                CloseUtils.closeIO(os);
            }
        }
        return null;
    }

    public static String inputStream2String(InputStream is, String charsetName) {
        if (is != null && !StringUtils.isSpace(charsetName)) {
            try {
                return new String(inputStream2Bytes(is), charsetName);
            } catch (UnsupportedEncodingException var3) {
                var3.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static InputStream string2InputStream(String string, String charsetName) {
        if (string != null && !StringUtils.isSpace(charsetName)) {
            try {
                return new ByteArrayInputStream(string.getBytes(charsetName));
            } catch (UnsupportedEncodingException var3) {
                var3.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static String outputStream2String(OutputStream out, String charsetName) {
        if (out != null && !StringUtils.isSpace(charsetName)) {
            try {
                return new String(outputStream2Bytes(out), charsetName);
            } catch (UnsupportedEncodingException var3) {
                var3.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static OutputStream string2OutputStream(String string, String charsetName) {
        if (string != null && !StringUtils.isSpace(charsetName)) {
            try {
                return bytes2OutputStream(string.getBytes(charsetName));
            } catch (UnsupportedEncodingException var3) {
                var3.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        if (bitmap == null) {
            return null;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(format, 100, baos);
            return baos.toByteArray();
        }
    }

    public static Bitmap bytes2Bitmap(byte[] bytes) {
        return bytes != null && bytes.length != 0 ? BitmapFactory.decodeByteArray(bytes, 0, bytes.length) : null;
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        return drawable == null ? null : ((BitmapDrawable) drawable).getBitmap();
    }

    public static Drawable bitmap2Drawable(Resources res, Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(res, bitmap);
    }

    public static byte[] drawable2Bytes(Drawable drawable, Bitmap.CompressFormat format) {
        return drawable == null ? null : bitmap2Bytes(drawable2Bitmap(drawable), format);
    }

    public static Drawable bytes2Drawable(Resources res, byte[] bytes) {
        return res == null ? null : bitmap2Drawable(res, bytes2Bitmap(bytes));
    }

    public static Bitmap view2Bitmap(View view) {
        if (view == null) {
            return null;
        } else {
            Bitmap ret = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(ret);
            Drawable bgDrawable = view.getBackground();
            if (bgDrawable != null) {
                bgDrawable.draw(canvas);
            } else {
                canvas.drawColor(-1);
            }

            view.draw(canvas);
            return ret;
        }
    }

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5F);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5F);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5F);
    }

    public static int getDimenSp(Context context, @DimenRes int spRes) {
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(spRes);
        return px2sp(context, dimensionPixelSize);
    }
}
