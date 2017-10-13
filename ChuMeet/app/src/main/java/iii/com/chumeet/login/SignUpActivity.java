package iii.com.chumeet.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import iii.com.chumeet.Common;
import iii.com.chumeet.R;
import iii.com.chumeet.Task.MyTask;
import iii.com.chumeet.Tools;
import iii.com.chumeet.home.HomeActivity;

import static iii.com.chumeet.Common.downSize;
import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;

public class SignUpActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private final static String TAG = "SignUpActivity";
    private final static int REQ_TAKE_PICTURE = 0;
    private static final int REQUESTCODE_PHOTO_LIB = 1;
    private Integer memID = null;
    private Bitmap picture;
    private File file;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private ImageView ivTakePicture;
    private byte[] image;

//*************************************Task & 連線開始*****************************************
    private class SignUpTask extends AsyncTask<Object, Integer, String>{
        private final static String TAG = "SignUpTask";
        private String url, signUp, name, email, password;
        private byte[] image;


        private SignUpTask(String url, String signUp,String name, String email, String password, byte[] image) {
            this.url = url;
            this.signUp = signUp;
            this.name = name;
            this.email = email;
            this.password = password;
            this.image = image;
        }

    @Override
        protected String doInBackground(Object[] params) {
            String jsonIn;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", signUp);
            jsonObject.addProperty("name", name);
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("password", password);
            jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
            try {
                jsonIn = getRemoteData(url, jsonObject.toString()); //jsonOut & jsonIn
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return null;
            }
            return jsonIn;
        }
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
//*************************************Task & 連線結束*****************************************


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
    }

    @Override
    protected  void onStart(){
        super.onStart();
        checkPermission();
        checkPermission2();
    }

    private void findViews() {
        etName = (EditText) findViewById(R.id.etSignUpName);
        etEmail = (EditText) findViewById(R.id.etSignUpEmail);
        etPassword = (EditText) findViewById(R.id.etSignUpPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etSignUpConfirmPassword);
        ivTakePicture = (ImageView) findViewById(R.id.ivSignUpContext);

//        registerForContextMenu(ivTakePicture);

//檢查Email是否重複按鈕
        TextView tvCheckEmail = (TextView) findViewById(R.id.tvSignUpCheckEmail);
        tvCheckEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                if (email.length() <= 0) {
                    Toast.makeText(SignUpActivity.this,R.string.msg_InvalidCheckEmail,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isEmailUsable(email)) {
                    Toast.makeText(SignUpActivity.this,R.string.msg_CheckEmailOK,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SignUpActivity.this,R.string.msg_CheckEmailRepeat,Toast.LENGTH_SHORT).show();
                }
            }
        });

//註冊按鈕
        Button btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (name.length() <= 0){
                    Toast.makeText(SignUpActivity.this,R.string.msg_SignUpInvalidName,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.length() <= 0 ){
                    Toast.makeText(SignUpActivity.this,R.string.msg_SignUpInvalidEmail,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!email.matches(Tools.emailPattern)){
                    Toast.makeText(SignUpActivity.this,"請輸入有效的Email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length() <= 0){
                    Toast.makeText(SignUpActivity.this,R.string.msg_SignUpInvalidPassword,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(confirmPassword.length() <= 0 || !confirmPassword.equals(password)){
                    Toast.makeText(SignUpActivity.this,R.string.msg_SignUpInvalidConfirmPassword,Toast.LENGTH_SHORT).show();
                    return;
                }

                if(file != null) {
                    Bitmap srcPicture = BitmapFactory.decodeFile(file.getPath());
                    picture = downSize(srcPicture, 512);
                    image = Common.bitmapToPNG(picture);

                }else {
                    Bitmap srcPicture = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                    Bitmap picture = downSize(srcPicture, 512);
                    image = Common.bitmapToPNG(picture);
                }
                if(isEmailUsable(email) && confirmPassword.equals(password)){

                    if(isSignUpValid(name, email, password, image)){

                        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                        pref.edit()
                                .putBoolean("login", true)
                                .putString("email", email)
                                .putString("password", password)
                                .putInt("memID", memID)
                                .apply();
                        setResult(RESULT_OK);

                        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(SignUpActivity.this,   "SignUpFail",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(SignUpActivity.this,   R.string.msg_CheckEmailRepeat,Toast.LENGTH_SHORT).show();
                }
            }



        });


//神奇小按鈕
        TextView tmb = (TextView) findViewById(R.id.tvSignUpSet);
        tmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setText("小林");
                etEmail.setText("javahome@gmail.com");
                etPassword.setText("123123");
                etConfirmPassword.setText("123123");

            }
        });
    }

//
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_picture);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tackPicture:

                    takePicture();

                return true;

            case R.id.action_photoLib:


                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUESTCODE_PHOTO_LIB);



                return true;
            default:
                return false;
        }
    }


    //檢查裝置有沒有應用程式可以執行拍照動作，如果有則數量會大於0
    public boolean isIntentAvailable(Context context, Intent intent){
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


//書本12-25頁
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//指定存檔路徑
        file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture.jpg");

        Uri contentUri = FileProvider.getUriForFile(
                this, getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        if (isIntentAvailable(this, intent)) {
            startActivityForResult(intent, REQ_TAKE_PICTURE);   //啟動onActivityResult
        } else {
            showToast(this, R.string.msg_NoCameraApp);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQ_TAKE_PICTURE:
                    Bitmap srcPicture = BitmapFactory.decodeFile(file.getPath());
                    picture = downSize(srcPicture, 512);
                    ivTakePicture.setImageBitmap(picture);
                    break;

                case REQUESTCODE_PHOTO_LIB:
                    Uri uri = data.getData();
                    String[] columns = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, columns,
                            null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String imagePath = cursor.getString(0);
                        cursor.close();
                        Bitmap bitmap2 = BitmapFactory.decodeFile(imagePath);
                        bitmap2 = downSize(bitmap2, 512);

                        ivTakePicture.setImageBitmap(bitmap2);
                        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                        bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, out2);
                        image = out2.toByteArray();
                    }
                    break;
                default:
                    Toast.makeText(this, "no react", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    private boolean isSignUpValid(String name, String email, String password, byte[] image) {
        boolean answer = false;

        if(networkConnected(this)){
            String url = Common.URL + "SignUp_LogIn_Android";
            try {

                String jsonIn = new SignUpTask(url, "signUp", name, email, password, image).execute().get();

                Gson gson = new Gson();
                memID = gson.fromJson(jsonIn, Integer.class);

            } catch (Exception e){
                Log.e(TAG, e.toString());
            }
            answer = memID != null;
        }else{
            Toast.makeText(SignUpActivity.this,R.string.msg_NoNetwork,Toast.LENGTH_SHORT).show();
        }

        return answer;
    }

//檢查Email是否重複
    private boolean isEmailUsable(String email) {
        boolean answer = false;

        if(networkConnected(this)){
            String url = Common.URL + "SignUp_LogIn_Android";

            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "checkEmail");
                jsonObject.addProperty("email", email);
                String jsonOut = jsonObject.toString();
                String jsonIn = new MyTask(url, jsonOut).execute().get();

                Gson gson = new Gson();
                String ans = gson.fromJson(jsonIn, String.class);

                answer = !ans.equals("repeat");
            } catch (Exception e){
                Log.e(TAG, e.toString());
            }

        }else{
            Toast.makeText(SignUpActivity.this,R.string.msg_NoNetwork,Toast.LENGTH_SHORT).show();
        }
        return answer;
        }
    private void checkPermission2() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 2);
        }
    }
    //檢查若尚未授權, 則向使用者要求[拍照]權限
    private void checkPermission() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA
                    }, 1);

            checkPermission2();
        }
//        }else{
//            //已有權限，執行儲存程式
//
//        }

    }
}
