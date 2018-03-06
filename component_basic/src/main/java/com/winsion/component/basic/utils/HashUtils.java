package com.winsion.component.basic.utils;

import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * Created by rexxar on 17-4-5.
 * HashUtils
 */
public class HashUtils {
    /**
     * data+time+httpKey
     *
     * @param source
     * @return
     */
    public static String getSha1Str(String source) {
        try {
            source = URLEncoder.encode(source, "utf-8").toUpperCase();
            MessageDigest digest = MessageDigest
                    .getInstance("SHA-1");
            digest.update(source.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getSaltedPassword(String pwd, String pwdSalt) {
        return getSha1Str(pwd + pwdSalt);
    }

    public static boolean checkSaltedPassword(String pwd, String pwdSalt, String targetSaltedPwd) {
        return targetSaltedPwd.equals(getSaltedPassword(pwd, pwdSalt));
    }
}
