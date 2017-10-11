package iii.com.chumeet.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import iii.com.chumeet.Common;
import iii.com.chumeet.home.HomeActivity;
import iii.com.chumeet.R;
import iii.com.chumeet.Task.MyTask;

import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //關閉應用程式的相關程式碼
        if( getIntent().getBooleanExtra("Exit me", false)){
            finish();
        }
    }
            //切換到SignUp頁面
    public void gotoHomeActivity(View v){
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

            //切換到會員登入頁面
    public void gotoLogInActivity(View v){

        Intent intent = new Intent(this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


            //如果在其他設備修改密碼
            //先檢查一下密碼
    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if(login){
            String email = pref.getString("email","");
            String password = pref.getString("password", "");
            if( isUserValid(email, password) ){
                setResult(RESULT_OK);
                startActivity(new Intent(this, HomeActivity.class));
            }
        }
    }

    private boolean isUserValid(String email, String password) {
        boolean answer = false;
        String id = null;
        if(networkConnected(this)){
            String url = Common.URL + "SignUp_LogIn_Android";

            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "logIn");
                jsonObject.addProperty("email", email);
                jsonObject.addProperty("password", password);
                String jsonOut = jsonObject.toString();
                String jsonIn = new MyTask(url, jsonOut).execute().get();

                Gson gson = new Gson();
                id = gson.fromJson(jsonIn, String.class);

            } catch (Exception e){
                Log.e(TAG, e.toString());
            }
            answer = id != null;
        }else{
            showToast(this, R.string.msg_NoNetwork);
        }

        return answer;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setMessage("Do you want to close it?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog0, int which){

//不這樣寫都不知道怎麼離開
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();

        }
        return true;
    }
}
