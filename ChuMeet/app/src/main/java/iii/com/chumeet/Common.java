package iii.com.chumeet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class Common {
    // Android官方模擬器連結本機web server可以直接使用 http://10.0.2.2
    public final static String URL = "http://10.0.2.2:8081/ChuMeet_Web/";
//    public final static String URL = "http://10.120.38.1:8081/ChuMeet_Web/";
//    public final static String URL = "http://10.120.38.7:8081/ChuMeet_Web/";
//    public final static String URL = "http://192.168.196.182:8081/ChuMeet_Web/";
    public final static String PREF_FILE = "preference";
    private static final String TAG = "Common";


    // check if the device connect to the network
    public static boolean networkConnected(Activity activity) {

// get the ConnectivityManager for handling management of network connections.
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

 // 取得正在運行中的網路資訊，如果沒有網路存在會取得null
// 判斷是否有Internet
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showToast(Context context, int message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static byte[] bitmapToPNG(Bitmap srcBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 轉成PNG不會失真，所以quality參數值會被忽略
        srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // 設定長寬不超過scaleSize
    public static Bitmap downSize(Bitmap srcBitmap, int newSize) {
        if (newSize <= 1) {
            // 如果欲縮小的尺寸過小，就直接定為128
            newSize = 128;
        }
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        String text = "source image size = " + srcWidth + "x" + srcHeight;
        Log.d(TAG, text);
        int longer = Math.max(srcWidth, srcHeight);

        if (longer > newSize) {
            double scale = longer / (double) newSize;
            int dstWidth = (int) (srcWidth / scale);
            int dstHeight = (int) (srcHeight / scale);
            srcBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, false);
            System.gc();
            text = "\nscale = " + scale + "\nscaled image size = " +
                    srcBitmap.getWidth() + "x" + srcBitmap.getHeight();
            Log.d(TAG, text);
        }
        return srcBitmap;
    }
}
