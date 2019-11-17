package comp5216.sydney.edu.au.runningdiary.Support;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SDUtils {

    /**
     * get SD card state
     *
     * @return
     */
    public static boolean isSDCardMounted() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * @return get SD card root directory
     */
    public static String getSDCardRootDir() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     *
     * @param data     dave data
     * @param fileName 　saved file name
     * @return
     */
    public static boolean saveFileToExternalCacheDir(Context context, byte[] data,
                                                     String fileName) {
        if (isSDCardMounted()) {
            BufferedOutputStream bos = null;
            File fileDir = context.getExternalCacheDir();
            File file = new File(fileDir, fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bos.write(data);
                bos.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * get CD card data
     *
     * @param fileAbsolutePath 　　read file path
     * @return
     */
    public static byte[] loadDataFromSDCard(String fileAbsolutePath) {
        if (isSDCardMounted()) {
            BufferedInputStream bis = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
                bis = new BufferedInputStream(
                        new FileInputStream(fileAbsolutePath));
                byte[] buffer = new byte[1024 * 8];
                int len = 0;
                while ((len = bis.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                }
                return baos.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (baos != null) {
                        baos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}