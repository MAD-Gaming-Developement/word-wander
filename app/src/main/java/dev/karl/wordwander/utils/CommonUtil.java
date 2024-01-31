package dev.karl.wordwander.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.internal.security.CertificateUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import dev.karl.wordwander.App;
import dev.karl.wordwander.R;

public class CommonUtil {

    public static boolean isEmojiCharacter(char c) {
        return (c == 0 || c == 9 || c == 10 || c == 13 || (c >= ' ' && c <= 55295) || ((c >= 57344 && c <= 65533) || (c >= 0 && c <= 65535))) ? false : true;
    }

    public static void log(String str) {
        Log.w("dev_log", " " + str);
    }

    public static void log(int i) {
        log("" + i);
    }

    public static void deAlert(Context context, String str) {
        Toast.makeText(context, " " + str, Toast.LENGTH_SHORT).show();
    }

    public static void deAlert(String str) {
        App app = App.context;
        Toast.makeText(app, " " + str, Toast.LENGTH_SHORT).show();
    }

    public static void alert(Context context, String str) {
        if (str == null) {
            log("CommonUtil->alert msg is null");
            str = "~No msg";
        }
        Toast.makeText(context, " " + str, Toast.LENGTH_SHORT).show();
    }

    public static void alert(String str) {
        if (str == null) {
            log("CommonUtil->alert msg is null");
            str = "~No msg";
        }
        App app = App.context;
        Toast.makeText(app, " " + str, Toast.LENGTH_SHORT).show();
    }

    public static void alertInCenter(String str) {
        if (str == null) {
            log("CommonUtil->alert msg is null");
            str = "~No msg";
        }
        Toast makeText = Toast.makeText(App.context, str, Toast.LENGTH_SHORT);
        makeText.setGravity(17, 0, 0);
        makeText.show();
    }

    public static void alert(String str, int i) {
        if (str == null) {
            log("CommonUtil->alert msg is null");
            str = "~No msg";
        }
        pop_content(App.context, str, i);
    }

    public static boolean isDebugEnv(Context context) {
        return (context.getApplicationInfo().flags & 2) != 0;
    }

    public static void pop_content(Context context, String str, int i) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.common_toast_alert, (ViewGroup) null);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.toast_icon);
        TextView textView = (TextView) inflate.findViewById(R.id.toast_text);
        if (i == -1) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setImageResource(i);
            imageView.setVisibility(View.VISIBLE);
        }
        textView.setText(str);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(17, 0, dip2px(context, 120.0f));
        toast.setView(inflate);
        toast.show();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);
        inflate.startAnimation(alphaAnimation);
    }

    public static int dip2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static String getYMdHms() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static String getCurDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(new Date());
    }

    public static String getTime4fileName() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss_SS").format(new Date());
    }

    public static String getYMD() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static String getYM() {
        return new SimpleDateFormat("yyyy-MM").format(new Date());
    }

    public static String getLog_YMD() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    public static long transDateToLong(String str) {
        try {
            Date parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").parse(str);
            if (parse != null) {
                return parse.getTime();
            }
            return 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getFormatDate(String str) {
        try {
            return new SimpleDateFormat("MM-dd HH:mm").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
            return str;
        }
    }

    public static final Date StringToData(String str, String str2) {
        try {
            return new SimpleDateFormat(str2).parse(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String DataToString(Date date, String str) {
        try {
            return new SimpleDateFormat(str).format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long getbetweenTime(Long l, Long l2) {
        if (l == null || l2 == null || l == 0 || l2 == 0) {
            return 0;
        }
        return (l - l2) / 1000;
    }

    public static Date getDateBefore(Date date, int i) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(5, instance.get(5) - i);
        return instance.getTime();
    }

    public static Date getDateAfter(Date date, int i) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(5, instance.get(5) + i);
        return instance.getTime();
    }

    public static String getDateBefore(String str, int i) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(StringToData(str, "yyyy-MM-dd"));
        instance.set(5, instance.get(5) - i);
        return DataToString(instance.getTime(), "yyyy-MM-dd");
    }

    public static String getDateAfter(String str, int i) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(StringToData(str, "yyyy-MM-dd"));
        instance.set(5, instance.get(5) + i);
        return DataToString(instance.getTime(), "yyyy-MM-dd");
    }

    public static int getConnectType(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return -1;
        }
        return activeNetworkInfo.getType();
    }

    public static Double roundDouble(double d, int i) {
        try {
            double pow = Math.pow(10.0d, (double) i);
            return Double.valueOf(Math.floor((d * pow) + 0.5d) / pow);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Double roundDouble2(double d, int i) {
        try {
            double pow = Math.pow(10.0d, (double) i);
            return Double.valueOf(Math.floor(d * pow) / pow);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void hideCurrActivitySoftInput(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            try {
                View currentFocus = activity.getCurrentFocus();
                if (currentFocus != null) {
                    ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(currentFocus.getWindowToken(), 2);
                }
            } catch (Exception unused) {
            }
        }
    }

    public static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException unused) {
            log("--->> 获取app版本名称失败");
            return null;
        }
    }

    public static Bitmap toOvalBitmap(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawRoundRect(rectF, 999.0f, 999.0f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }

    public static Bitmap toOvalBitmap(Bitmap bitmap, float f) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawRoundRect(rectF, f, f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }

    public static Drawable toRoundCorner(Drawable drawable, int i) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        float f = (float) i;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawRoundRect(rectF, f, f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return new BitmapDrawable(createBitmap);
    }

    public static void shareText(Context context, String str, String str2) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/*");
        intent.putExtra("android.intent.extra.SUBJECT", str);
        intent.putExtra("android.intent.extra.TEXT", str2);
        context.startActivity(Intent.createChooser(intent, str));
    }

    public static void copyFile(String str, String str2) {
        try {
            if (new File(str).exists()) {
                FileInputStream fileInputStream = new FileInputStream(str);
                FileOutputStream fileOutputStream = new FileOutputStream(str2);
                byte[] bArr = new byte[10240];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read != -1) {
                        fileOutputStream.write(bArr, 0, read);
                    } else {
                        fileOutputStream.flush();
                        fileInputStream.close();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkAppInstalled(Context context, String str) {
        PackageInfo packageInfo;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageInfo = null;
        }
        if (packageInfo == null) {
            return false;
        }
        return true;
    }

    public static void openGooglePlay(Context context, String str) {
        try {
            if (context.getPackageName() != null) {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + str));
                intent.setPackage(str);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent2 = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + str));
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String comFileSizeWithName(double d) {
        String str;
        if (d > 1024.0d) {
            d /= 1024.0d;
            if (d > 1024.0d) {
                d /= 1024.0d;
                if (d > 1024.0d) {
                    d /= 1024.0d;
                    str = "GB";
                } else {
                    str = "MB";
                }
            } else {
                str = "KB";
            }
        } else {
            str = "B";
        }
        Log.e("size2", d + "");
        return roundDouble(d, 1) + str;
    }

    public static boolean isFegexFFChar(String str) {
        return Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]").matcher(str).find();
    }

    public static String getWan(long j) {
        if (j >= 10000) {
            return roundDouble(((double) j) / 10000.0d, 1) + "万";
        }
        return j + "";
    }

    public static String getShiWan(long j) {
        if (j >= 100000) {
            return roundDouble2(((double) j) / 10000.0d, 1) + "万";
        }
        return j + "";
    }

    public static String getShiWan(double d) {
        if (d >= 100000.0d) {
            return roundDouble2(d / 10000.0d, 1) + "万";
        }
        return d + "";
    }

    public static String getShiWan(long j, int i) {
        if (i < 0) {
            i = 0;
        }
        if (j >= 100000) {
            double doubleValue = roundDouble2(((double) 105000) / 10000.0d, i).doubleValue();
            if (i == 0) {
                return ((long) doubleValue) + "万";
            }
            return doubleValue + "万";
        }
        return j + "";
    }

    public static String compressData(String str) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
            deflaterOutputStream.write(str.getBytes());
            deflaterOutputStream.close();
            return new String(Base64.encode(byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decompressData(String str) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(byteArrayOutputStream);
            inflaterOutputStream.write(Base64.decode(str));
            inflaterOutputStream.close();
            return new String(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String filterEmoji(String str) {
        int length = str.length();
        StringBuilder sb = null;
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (!isEmojiCharacter(charAt)) {
                if (sb == null) {
                    sb = new StringBuilder(str.length());
                }
                sb.append(charAt);
            }
        }
        if (sb == null || sb.length() == length) {
            return str;
        }
        return sb.toString();
    }

    public static String toDBC(String str) {
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == 12288) {
                charArray[i] = ' ';
            } else if (charArray[i] > 65280 && charArray[i] < 65375) {
                charArray[i] = (char) (charArray[i] - 65248);
            }
        }
        return new String(charArray);
    }

    public static String secToTime(int i) {
        if (i <= 0) {
            return "00:00";
        }
        int i2 = i / 60;
        if (i2 < 60) {
            return unitFormat(i2) + CertificateUtil.DELIMITER + unitFormat(i % 60);
        }
        int i3 = i2 / 60;
        if (i3 > 99) {
            return "99:59:59";
        }
        int i4 = i2 % 60;
        return unitFormat(i3) + CertificateUtil.DELIMITER + unitFormat(i4) + CertificateUtil.DELIMITER + unitFormat((i - (i3 * 3600)) - (i4 * 60));
    }

    public static String unitFormat(int i) {
        if (i < 0 || i >= 10) {
            return "" + i;
        }
        return AppEventsConstants.EVENT_PARAM_VALUE_NO + Integer.toString(i);
    }

    public static byte[] encrypt(String str, String str2) {
        try {
            KeyGenerator instance = KeyGenerator.getInstance("AES");
            instance.init(128, new SecureRandom(str2.getBytes()));
            SecretKeySpec secretKeySpec = new SecretKeySpec(instance.generateKey().getEncoded(), "AES");
            Cipher instance2 = Cipher.getInstance("AES");
            byte[] bytes = str.getBytes("utf-8");
            instance2.init(1, secretKeySpec);
            return instance2.doFinal(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
            return null;
        } catch (InvalidKeyException e3) {
            e3.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e4) {
            e4.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e5) {
            e5.printStackTrace();
            return null;
        } catch (BadPaddingException e6) {
            e6.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt(byte[] bArr, String str) {
        try {
            KeyGenerator instance = KeyGenerator.getInstance("AES");
            instance.init(128, new SecureRandom(str.getBytes()));
            SecretKeySpec secretKeySpec = new SecretKeySpec(instance.generateKey().getEncoded(), "AES");
            Cipher instance2 = Cipher.getInstance("AES");
            instance2.init(2, secretKeySpec);
            return instance2.doFinal(bArr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
            return null;
        } catch (InvalidKeyException e3) {
            e3.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e4) {
            e4.printStackTrace();
            return null;
        } catch (BadPaddingException e5) {
            e5.printStackTrace();
            return null;
        }
    }

    public static long cTimes(String str, String str2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return (simpleDateFormat.parse(str).getTime() - simpleDateFormat.parse(str2).getTime()) / 86400000;
        } catch (Exception unused) {
            return 0;
        }
    }

    public static String formatTime(Long l) {
        Integer num = 1000;
        Integer valueOf = Integer.valueOf(num.intValue() * 60);
        Integer valueOf2 = Integer.valueOf(valueOf.intValue() * 60);
        Integer valueOf3 = Integer.valueOf(valueOf2.intValue() * 24);
        Long valueOf4 = Long.valueOf(l.longValue() / ((long) valueOf3.intValue()));
        Long valueOf5 = Long.valueOf((l.longValue() - (valueOf4.longValue() * ((long) valueOf3.intValue()))) / ((long) valueOf2.intValue()));
        Long valueOf6 = Long.valueOf(((l.longValue() - (valueOf4.longValue() * ((long) valueOf3.intValue()))) - (valueOf5.longValue() * ((long) valueOf2.intValue()))) / ((long) valueOf.intValue()));
        Long valueOf7 = Long.valueOf((((l.longValue() - (valueOf4.longValue() * ((long) valueOf3.intValue()))) - (valueOf5.longValue() * ((long) valueOf2.intValue()))) - (valueOf6.longValue() * ((long) valueOf.intValue()))) / ((long) num.intValue()));
        Long.valueOf((((l.longValue() - (valueOf4.longValue() * ((long) valueOf3.intValue()))) - (valueOf5.longValue() * ((long) valueOf2.intValue()))) - (valueOf6.longValue() * ((long) valueOf.intValue()))) - (valueOf7.longValue() * ((long) num.intValue())));
        StringBuffer stringBuffer = new StringBuffer();
        if (valueOf4.longValue() > 0) {
            stringBuffer.append(valueOf4 + "天");
        } else {
            stringBuffer.append("0天");
        }
        if (valueOf5.longValue() > 0) {
            stringBuffer.append(valueOf5 + "时");
        } else {
            stringBuffer.append("0时");
        }
        if (valueOf6.longValue() > 0) {
            stringBuffer.append(valueOf6 + "分");
        } else {
            stringBuffer.append("0分");
        }
        if (valueOf7.longValue() > 0) {
            stringBuffer.append(valueOf7 + "秒");
        } else {
            stringBuffer.append("0秒");
        }
        return stringBuffer.toString();
    }

    public static String formatTime4FenMiao(Long l) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return simpleDateFormat.format(l).toString();
    }

    public static String getChannleID() {
        try {
            ApplicationInfo applicationInfo = App.context.getPackageManager().getApplicationInfo(App.context.getPackageName(), 128);
            return applicationInfo.metaData.getString("channel_id") + "";
        } catch (Exception unused) {
            return "";
        }
    }

    public static void addResourceToPhotoAlbumDB(String str) {
        App.context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(str))));
    }

    public static int getWindowWidth(Context context) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getWindowHeight(Context context) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static String getIPAddress(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return null;
        }
        if (activeNetworkInfo.getType() == 0) {
            try {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                    while (true) {
                        if (inetAddresses.hasMoreElements()) {
                            InetAddress nextElement = inetAddresses.nextElement();
                            if (!nextElement.isLoopbackAddress() && (nextElement instanceof Inet4Address)) {
                                return nextElement.getHostAddress();
                            }
                        }
                    }
                }
                return null;
            } catch (SocketException e) {
                e.printStackTrace();
                return null;
            }
        } else if (activeNetworkInfo.getType() == 1) {
            return intIP2StringIP(((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getIpAddress());
        } else {
            return null;
        }
    }

    public static String intIP2StringIP(int i) {
        return (i & 255) + "." + ((i >> 8) & 255) + "." + ((i >> 16) & 255) + "." + ((i >> 24) & 255);
    }

    public static int getRandom(int i, int i2) {
        return new Random().nextInt((i2 - i) + 1) + i;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static byte[] bmpToByteArray(Bitmap bitmap, boolean z) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        if (z) {
            bitmap.recycle();
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public static void toSelfSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), (String) null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction("android.intent.action.VIEW");
            intent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }

    public static boolean isWebviewUA(String str) {
        return Pattern.compile("(" + String.join("|", new String[]{"WebView", "Android.*(wv|\\.0\\.0\\.0)"}) + ")", 2).matcher(str).find();
    }

}
