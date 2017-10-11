package iii.com.chumeet.Task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import iii.com.chumeet.R;

/**
 * Created by sonic on 2017/9/15.
 */

 public class GetImageTask extends AsyncTask<Object, Integer, Bitmap>{
        private final static String TAG = "ImgTask";
        private String url;
        private int id, imageSize;
        private ImageView imageView;



    public GetImageTask(String url, int id, int imageSize){
            this(url, id, imageSize, null);
    }

    public GetImageTask(String url, int id, int imageSize, ImageView imageView){
            this.url =  url;
            this.id = id;
            this.imageSize = imageSize;
            this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Object... params){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getImage");
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("imageSize", imageSize);

        Bitmap bitmap;
        try{
            bitmap = getRemoteImage(url, jsonObject.toString());
        }catch(IOException e){
            Log.e(TAG, e.toString());
            return null;
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        if(isCancelled() || imageView == null){
            return;
        }
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageResource(R.drawable.p);
        }
    }

    private Bitmap getRemoteImage(String url, String jsonOut) throws IOException{
        Bitmap bitmap = null;
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setRequestMethod("POST");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        bw.write(jsonOut);

        Log.d(TAG, "jsonOut: " + jsonOut);
        bw.close();

        int responseCode = con.getResponseCode();

        if(responseCode == 200){
            bitmap = BitmapFactory.decodeStream(con.getInputStream());

//            Log.d(TAG, "response code: " + responseCode);
        }else{

            Log.d(TAG, "response code: " + responseCode);
        }
        con.disconnect();
        return bitmap;
    }
}
