package iii.com.chumeet.Task;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class InsertTask extends AsyncTask<Object, Integer, String> {
    private final static String TAG = "InsertTask";
    private String url, action;
    private Object vo;
    private byte[] image;


    public InsertTask(String url, String action, Object vo, byte[] image) {
        this.url = url;
        this.action = action;
        this.vo = vo;
        this.image = image;
    }

    @Override
    protected String doInBackground(Object... params) {
        String jsonIn;

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String voStr = gson.toJson(vo);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", action);
        jsonObject.addProperty("vo", voStr);
        jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
        try {
            jsonIn = getRemoteData(url, jsonObject.toString()); //jsonOut & jsonIn
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return jsonIn;
    }


    private String getRemoteData(String url, String jsonOut) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setRequestMethod("POST");
        con.setRequestProperty("charset", "UTF-8");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        bw.write(jsonOut);
        Log.d(TAG, "jsonOut:\n   " + jsonOut);
        bw.close();

        int responseCode = con.getResponseCode();
        StringBuilder jsonIn = new StringBuilder();
        if(responseCode == 200){
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while((line = br.readLine()) != null){
                jsonIn.append(line);
            }
        }else{
            Log.d(TAG, "response code:\n   " + responseCode);
        }
        con.disconnect();
        Log.d(TAG, "jsonIn:\n   " + jsonIn);
        return jsonIn.toString();

    }
}
