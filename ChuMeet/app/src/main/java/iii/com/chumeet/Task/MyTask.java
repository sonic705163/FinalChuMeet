package iii.com.chumeet.Task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class MyTask extends AsyncTask<String, Integer, String> {
        private final static String TAG = " MyTask";
        private String url, outStr;

    public MyTask(String url, String outStr){
        this.url = url;
        this.outStr = outStr;
    }


    @Override
    protected String doInBackground(String... params){
            String inStr = null;
        try{
            inStr = getRemoteData();
        }catch (IOException e){
            Log.e(TAG, e.toString());
        }
        return inStr;
    }

//連線取資料
    private String getRemoteData() throws IOException {
        StringBuilder inStr = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setDoInput(true); // allow inputs
        con.setDoOutput(true); // allow outputs
        con.setUseCaches(false); // do not use a cached copy
        con.setRequestMethod("POST");
        con.setRequestProperty("charset", "UTF-8");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        bw.write(outStr);
        Log.d(TAG, "outStr:  " + outStr);
        bw.close();

        int responseCode = con.getResponseCode();

        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                inStr.append(line);
            }
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        con.disconnect();
        Log.d(TAG, "inStr:   " + inStr);
        return inStr.toString();
    }
}
