package com.kgc.tiku.bluebird.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class Md5Utils {
    public static String md5Encode(String inStr) {
        try {
            byte[] md5Bytes = MessageDigest.getInstance("MD5").digest(inStr.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexValue = new StringBuilder();
            for (byte b : md5Bytes) {
                int val = b & 255;
                if (val < 16) {
                    hexValue.append(0);
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            return "";
        }
    }
}