package iii.com.chumeet.act;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import iii.com.chumeet.Common;
import iii.com.chumeet.R;
import iii.com.chumeet.Task.GetImageTask;
import iii.com.chumeet.Task.InsertOrUpdateTask;
import iii.com.chumeet.VO.ActVO;

import static iii.com.chumeet.Common.downSize;
import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;

public class ActUpdateActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener  {
    private final static String TAG = "ActUpdateActivity";
    private final static int REQ_TAKE_PICTURE = 0;
    private static final int REQUESTCODE_PHOTO_LIB = 1;
    private Bitmap picture;
    private File file;
    private ImageView ivActImg;
    private EditText etName, etLocationName, etContent;
    private Address address;
    private Double latitude, longitude;
    private TextView tvActStart_Display, tvActEnd_Display;
    private String tvActStart, tvActEnd;
    private int asYear, asMonth, asDay, asHour, asMinute;
    private int aeYear, aeMonth, aeDay, aeHour, aeMinute;
    private byte[] image;
    private ActVO actVO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_update);

        findViews();
        dateTime();

        Bundle bundle = this.getIntent().getExtras();
        actVO = (ActVO) bundle.getSerializable("actVO");

        if(networkConnected(this)) {
            String url = Common.URL + "ActServletAndroid";
            try {
                int imageSize = getResources().getDisplayMetrics().widthPixels / 2;

                picture = new GetImageTask(url, actVO.getActID(), imageSize, ivActImg).execute().get();

                image = Common.bitmapToPNG(picture);

            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
        }else{
            showToast(this, R.string.msg_NoNetwork);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


        Bundle bundle = this.getIntent().getExtras();
        actVO = (ActVO) bundle.getSerializable("actVO");

        etName.setText(actVO.getActName());
        etLocationName.setText(actVO.getActAdr());


        String sDate = actVO.getActStartDate().toString();
        int year = Integer.valueOf(sDate.substring(0,4));
        int month = Integer.valueOf(sDate.substring(5,7))-1;
        int today = Integer.valueOf(sDate.substring(8,10));
        int hour = Integer.valueOf(sDate.substring(11,13));
        int minute = Integer.valueOf(sDate.substring(14,16));


        asYear = year;
        asMonth = month;
        asDay = today;
        asHour = hour;
        asMinute = minute;

        String sDate2 = actVO.getActEndDate().toString();
        int year2 = Integer.valueOf(sDate2.substring(0,4));
        int month2 = Integer.valueOf(sDate2.substring(5,7))-1;
        int today2 = Integer.valueOf(sDate2.substring(8,10));
        int hour2 = Integer.valueOf(sDate2.substring(11,13));
        int minute2 = Integer.valueOf(sDate2.substring(14,16));

        aeYear = year2;
        aeMonth = month2;
        aeDay = today2;
        aeHour = hour2;
        aeMinute = minute2;



        tvActStart = actVO.getActStartDate().toString().substring(0,16);
        tvActEnd = actVO.getActEndDate().toString().substring(0,16);

        tvActStart_Display.setText(actVO.getActStartDate().toString().substring(0,16));
        tvActEnd_Display.setText(actVO.getActEndDate().toString().substring(0,16));

        etContent.setText(actVO.getActContent());



        checkPermission();                  //權限請求

    }



    private void findViews() {
        ivActImg = (ImageView) findViewById(R.id.ivActImg_ActUpdate);
        etName = (EditText) findViewById(R.id.edName_ActUpdate);
        etLocationName = (EditText) findViewById(R.id.edAdr_ActUpdate);

        tvActStart_Display = (TextView) findViewById(R.id.tvStartDate_ActUpdate_show);
        tvActEnd_Display = (TextView) findViewById(R.id.tvEndDate_ActUpdate_show);

        etContent = (EditText) findViewById(R.id.etContent_ActUpdate);






        Button btnDone = (Button) findViewById(R.id.btnDone_ActUpdate);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actName = etName.getText().toString().trim();
                String locationName = etLocationName.getText().toString().trim();

                if(locationName.length() > 0 && actName.length() > 0 ){

                    locationNameToLatLng(locationName);

                    try {
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                    }catch (Exception e){
                        Toast.makeText(ActUpdateActivity.this,"地址無效請重新輸入",Toast.LENGTH_SHORT).show();
                        return;
                    }

                        actVO.setActName(actName);
                        actVO.setActLat(latitude);
                        actVO.setActLong(longitude);
                        actVO.setActStartDate(Timestamp.valueOf(tvActStart + ":00"));
                        actVO.setActEndDate(Timestamp.valueOf(tvActEnd + ":00"));
                        actVO.setActSignStartDate(Timestamp.valueOf(tvActStart + ":00"));
                        actVO.setActSignEndDate(Timestamp.valueOf(tvActEnd + ":00"));

                        String content = etContent.getText().toString();

                        actVO.setActContent(content);
                        actVO.setActAdr(locationName);


                        if(isUpdateValid(actVO)){

                            Toast.makeText(getBaseContext(), "活動已修改", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ActUpdateActivity.this, ActDetailActivity.class);
                            Bundle bundle2 = new Bundle();
                            bundle2.putSerializable("actVO", actVO);
                            intent.putExtras(bundle2);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(ActUpdateActivity.this, "修改活動失敗", Toast.LENGTH_SHORT).show();
                        }
                }else {
                    Toast.makeText(ActUpdateActivity.this, "請完整輸入", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void locationNameToLatLng(String locationName){
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addressList = null;
        int maxResults = 1;
        try{

            //解譯地名/地址後可能產生多筆位置資訊，所以回傳List<Address>
            //將maxResults設為1，限定回傳1筆
            addressList = geocoder.getFromLocationName(locationName, maxResults);

        }catch (IOException e){

            //如無法連結到提供服務的伺服器，印出 Log.e
            Log.e(TAG, e.toString());

        }

        if(addressList == null || addressList.isEmpty()){

            Toast.makeText(getBaseContext(), "addressList is NULL", Toast.LENGTH_SHORT).show();

        }else {

            //因為當初只限定回傳1筆，所以只取第1個Address物件即可
            address = addressList.get(0);

        }
    }



    private void dateTime(){

//活動開始時間
        final Button btnStartDate = (Button) findViewById(R.id.btnStartDate_ActUpdate);
        btnStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatePickerDialog datePicker = new DatePickerDialog(ActUpdateActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                Calendar calendar = Calendar.getInstance();
                                int thisYear = calendar.get(Calendar.YEAR);
                                int thisMonth = calendar.get(Calendar.MONTH);
                                int today = calendar.get(Calendar.DAY_OF_MONTH);


                                asYear = year;
                                if(asYear < thisYear || asYear > thisYear + 1){
                                    Toast.makeText(ActUpdateActivity.this, "無效年份設定", Toast.LENGTH_SHORT).show();
                                    asYear = thisYear;
                                    asMonth = thisMonth;
                                    asDay = today;

                                    return;
                                }
                                asMonth = month;
                                if(asMonth < thisMonth && asYear <= thisYear ){
                                    Toast.makeText(ActUpdateActivity.this, "無效月份設定", Toast.LENGTH_SHORT).show();
                                    asMonth = thisMonth;
                                    asDay = today;

                                    return;

                                }else if(asMonth >= thisMonth - 6 && asYear == thisYear + 1){
                                    Toast.makeText(ActUpdateActivity.this, "日期請設定6個月以內", Toast.LENGTH_SHORT).show();
                                    asYear = thisYear;
                                    asMonth = thisMonth;
                                    asDay = today;

                                    return;
                                }
                                asDay = day;
                                if(asDay < today && asMonth == thisMonth){
                                    Toast.makeText(ActUpdateActivity.this, "無效日期設定", Toast.LENGTH_SHORT).show();
                                    asDay = today;


                                }
                            }
                        }, asYear, asMonth, asDay);

                TimePickerDialog timePicker = new TimePickerDialog (ActUpdateActivity.this,
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute){
                                asHour = hour;
                                asMinute = minute;

                                tvActStart_Display.setText(String.valueOf(asYear) + "-" +
                                                                    pad(asMonth + 1) + "-" +
                                                                    pad(asDay) + " " +
                                                                    pad(asHour) + ":" +
                                                                    pad(asMinute));
                                tvActEnd_Display.setText(String.valueOf(asYear) + "-" +
                                                                    pad(asMonth + 1) + "-" +
                                                                    pad(asDay) + " " +
                                                                    pad(asHour) + ":" +
                                                                    pad(asMinute));


                                tvActStart = tvActStart_Display.getText().toString();
                                tvActEnd = tvActEnd_Display.getText().toString();

                                aeYear = asYear;
                                aeMonth = asMonth;
                                aeDay = asDay;
                                aeHour = asHour;
                                aeMinute = asMinute;

                            }
                        }, asHour, asMinute, false);

                timePicker.show();
                datePicker.show();

            }
        });

//活動結束時間
        Button btnEndDate = (Button) findViewById(R.id.btnEndDate_ActUpdate);
        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(ActUpdateActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                Calendar calendar = Calendar.getInstance();
                                int thisYear = calendar.get(Calendar.YEAR);
                                int thisMonth = calendar.get(Calendar.MONTH);

                                aeYear = year;
                                if(aeYear < asYear){
                                    Toast.makeText(ActUpdateActivity.this, "不得早於開始時間", Toast.LENGTH_SHORT).show();
                                    aeYear = asYear;
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                if(aeYear > thisYear + 1){
                                    Toast.makeText(ActUpdateActivity.this, "無效年份設定", Toast.LENGTH_SHORT).show();
                                    aeYear = asYear;
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                aeMonth = month;
                                if(aeMonth < asMonth && aeYear == thisYear){
                                    Toast.makeText(ActUpdateActivity.this, "不得早於開始月份", Toast.LENGTH_SHORT).show();
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                if(aeMonth < thisMonth && aeYear <= thisYear ){
                                    Toast.makeText(ActUpdateActivity.this, "無效月份設定", Toast.LENGTH_SHORT).show();
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;

                                }else if(aeMonth >= thisMonth - 6 && aeYear == thisYear + 1){
                                    Toast.makeText(ActUpdateActivity.this, "日期請設定6個月以內", Toast.LENGTH_SHORT).show();
                                    aeYear = asYear;
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                aeDay = day;
                                if(aeDay < asDay && aeMonth == asMonth ){
                                    Toast.makeText(ActUpdateActivity.this, "不得早於開始日期", Toast.LENGTH_SHORT).show();
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;

                                }

                            }
                        }, aeYear, aeMonth, aeDay);

                TimePickerDialog timePicker = new TimePickerDialog (ActUpdateActivity.this,
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute){
                                aeHour = hour;
                                if(aeHour < asHour && aeYear == asYear && aeMonth == asMonth && aeDay == asDay){
                                    Toast.makeText(ActUpdateActivity.this, "不得早於開始時間", Toast.LENGTH_SHORT).show();
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                aeMinute = minute;
                                if(aeHour == asHour && aeMinute < asMinute){
                                    Toast.makeText(ActUpdateActivity.this, "不得早於開始時間", Toast.LENGTH_SHORT).show();
                                    aeMinute = asMinute;
                                    return;
                                }

                                tvActEnd_Display.setText(String.valueOf(aeYear) + "-" +
                                                                    pad(aeMonth + 1) + "-" +
                                                                    pad(aeDay) + " " +
                                                                    pad(aeHour) + ":" +
                                                                    pad(aeMinute));

                                tvActEnd = tvActEnd_Display.getText().toString();

                            }
                        }, aeHour, aeMinute, false);
                timePicker.show();
                datePicker.show();

            }
        });
    }


    private String pad(int number) {
        if (number >= 10)
            return String.valueOf(number);
        else
            return "0" + String.valueOf(number);
    }


    private boolean isUpdateValid(ActVO actVO){
        Integer count = null;

        if(networkConnected(this)){
            String url = Common.URL + "ActServletAndroid";

            try {
                Gson gson = new Gson();

                String jsonIn = new InsertOrUpdateTask(url, "update", actVO, image).execute().get();

                count = gson.fromJson(jsonIn, Integer.class);

            } catch (Exception e){
                Log.e(TAG, e.toString());
            }

        }else{
            showToast(this, R.string.msg_NoNetwork);
        }
        return count != null;
    }

    //****************************************************拍照選單***************************************
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
    //****************************************************拍照選單***************************************

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
                    ivActImg.setImageBitmap(picture);
                    image = Common.bitmapToPNG(picture);
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

                        ivActImg.setImageBitmap(bitmap2);

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
    //檢查若尚未授權, 則向使用者要求存取權限、拍照權限
    private void checkPermission() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&

                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA
                    }, 1);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG,"************************"+getTaskId()+"我被消滅了***************************");
    }

}
