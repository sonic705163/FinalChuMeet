package iii.com.chumeet.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import iii.com.chumeet.Common;
import iii.com.chumeet.home.HomeActivity;
import iii.com.chumeet.R;
import iii.com.chumeet.Task.MyTask;

import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;







public class LogInActivity extends AppCompatActivity {
    private final static String TAG = "LogInActivity";
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvMessage;
    private Integer memID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();

//防止使用者亂寫
        setResult(RESULT_CANCELED);
    }

    private void findViews(){
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        Button btnLogin = (Button) findViewById(R.id.btnLogIn);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if(email.length() <= 0 || password.length() <=0){
                    showMessage(R.string.msg_InvalidUserOrPassword);
                    return;
                }
//如果登入成功
//就儲存起來
                if(isUserValid(email, password)){

                    SharedPreferences pref = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                    pref.edit()
                            .putBoolean("login", true)
                            .putString("email", email)
                            .putString("password", password)
                            .putInt("memID", memID)
                            .apply();

                    setResult(RESULT_OK);       //防使用者亂寫

                    Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }else{
                    showMessage(R.string.msg_InvalidUserOrPassword);
                }
            }
        });

        //神奇小按鈕
        TextView tmb = (TextView) findViewById(R.id.tvLogin2);
        tmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etEmail.setText("member01@gmail");
                etPassword.setText("member01");


            }
        });
    }



    private void showMessage(int msgResId) {
        tvMessage.setText(msgResId);
    }



    private boolean isUserValid(String email, String password) {
        boolean answer = false;

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
                memID = gson.fromJson(jsonIn, Integer.class);

            } catch (Exception e){
                Log.e(TAG, e.toString());
            }
            answer = memID != null;
        }else{
            showToast(this, R.string.msg_NoNetwork);
        }

        return answer;
    }



}


