package dev.karl.wordwander.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtils {
    public static boolean storageAvailable() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return new File(Environment.getExternalStorageDirectory().getAbsolutePath()).canWrite();
        }
        return false;
    }

    public static int copy(String str, String str2) {
        File file = new File(str);
        if (!file.exists()) {
            return -1;
        }
        File[] listFiles = file.listFiles();
        File file2 = new File(str2);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
                copy(listFiles[i].getPath() + "/", str2 + listFiles[i].getName() + "/");
            } else {
                CopySdcardFile(listFiles[i].getPath(), str2 + listFiles[i].getName());
            }
        }
        return 0;
    }

    private static int CopySdcardFile(String str, String str2) {
        try {
            FileInputStream fileInputStream = new FileInputStream(str);
            FileOutputStream fileOutputStream = new FileOutputStream(str2);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = fileInputStream.read(bArr);
                if (read > 0) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    fileInputStream.close();
                    fileOutputStream.close();
                    return 0;
                }
            }
        } catch (Exception unused) {
            return -1;
        }
    }

    public static void delFolder(String str) {
        try {
            delAllFile(str);
            new File(str.toString()).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean delAllFile(String str) {
        File file;
        File file2 = new File(str);
        if (!file2.exists() || !file2.isDirectory()) {
            return false;
        }
        String[] list = file2.list();
        boolean z = false;
        for (int i = 0; i < list.length; i++) {
            if (str.endsWith(File.separator)) {
                file = new File(str + list[i]);
            } else {
                file = new File(str + File.separator + list[i]);
            }
            if (file.isFile()) {
                file.delete();
            }
            if (file.isDirectory()) {
                delAllFile(str + File.separator + list[i]);
                delFolder(str + File.separator + list[i]);
                z = true;
            }
        }
        return z;
    }
}