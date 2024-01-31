package dev.karl.wordwander.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.adjust.sdk.Constants;
import com.facebook.appevents.AppEventsConstants;

import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;

import kotlin.UByte;

public class DeviceIdUtil {
    public static String getDeviceId(Context context) {
        StringBuilder sb = new StringBuilder();
        String androidId = getAndroidId(context);
        String serial = getSERIAL();
        String replace = getDeviceUUID().replace("-", "");
        if (androidId != null && androidId.length() > 0) {
            sb.append(androidId);
            sb.append("|");
        }
        if (serial != null && serial.length() > 0) {
            sb.append(serial);
            sb.append("|");
        }
        if (replace != null && replace.length() > 0) {
            sb.append(replace);
        }
        if (sb.length() > 0) {
            try {
                String bytesToHex = bytesToHex(getHashByString(sb.toString()));
                if (bytesToHex != null && bytesToHex.length() > 0) {
                    return bytesToHex;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getSERIAL() {
        try {
            return Build.SERIAL;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getDeviceUUID() {
        try {
            return new UUID((long) ("3883756" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.DEVICE.length() % 10) + (Build.HARDWARE.length() % 10) + (Build.ID.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10) + (Build.SERIAL.length() % 10)).hashCode(), (long) Build.SERIAL.hashCode()).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static byte[] getHashByString(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA1");
            instance.reset();
            instance.update(str.getBytes(Constants.ENCODING));
            return instance.digest();
        } catch (Exception unused) {
            return "".getBytes();
        }
    }

    private static String bytesToHex(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & UByte.MAX_VALUE);
            if (hexString.length() == 1) {
                sb.append(AppEventsConstants.EVENT_PARAM_VALUE_NO);
            }
            sb.append(hexString);
        }
        return sb.toString().toUpperCase(Locale.CHINA);
    }
}
