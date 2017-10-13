package iii.com.chumeet.act;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import iii.com.chumeet.Common;
import iii.com.chumeet.R;
import iii.com.chumeet.Task.InsertTask;
import iii.com.chumeet.VO.ActVO;
import iii.com.chumeet.home.HomeActivity;

import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;

public class ActUpdateActivity extends AppCompatActivity {
    private final static String TAG = "ActUpdateActivity";
    private EditText etName, etLocationName, etContent;
    private Address address;
    private Double latitude, longitude;
    private TextView tvActStart_Display, tvActEnd_Display;
    private Button btnActEnd;
    private CheckBox cbSports, cbLearn, cbFood, cbArts, cbMovie, cbGame, cbOutdoors, cbPets, cbOthers;
    private String poi_1, poi_2, poi_3, poi_4, poi_5, poi_6, poi_7, poi_8, poi_9;
    private int asYear, asMonth, asDay, asHour, asMinute;
    private int aeYear, aeMonth, aeDay, aeHour, aeMinute;
    private byte[] image;
    private Bundle bundle;
    private ActVO actVO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_update);

        findViews();
        dateTime();


    }

    @Override
    protected void onStart() {
        super.onStart();

        bundle = this.getIntent().getExtras();
        actVO =  (ActVO) bundle.getSerializable("actVO");

        etName.setText(actVO.getActName());
        etLocationName.setText(actVO.getActAdr());
        tvActStart_Display.setText(actVO.getActStartDate().toString().substring(0,16));
        tvActEnd_Display.setText(actVO.getActEndDate().toString().substring(0,16));
        etContent.setText(actVO.getActContent());
    }

    private void findViews() {
        etName = (EditText) findViewById(R.id.edName_ActUpdate);
        etLocationName = (EditText) findViewById(R.id.edAdr_ActUpdate);

        tvActStart_Display = (TextView) findViewById(R.id.tvStartDate_ActUpdate_show);
        tvActEnd_Display = (TextView) findViewById(R.id.tvEndDate_ActUpdate_show);

        etContent = (EditText) findViewById(R.id.etContent_ActUpdate);

        cbSports = (CheckBox) findViewById(R.id.cbSports);
        cbLearn = (CheckBox) findViewById(R.id.cbLearn);
        cbFood = (CheckBox) findViewById(R.id.cbFood);
        cbArts = (CheckBox) findViewById(R.id.cbArts);
        cbMovie = (CheckBox) findViewById(R.id.cbMovie);
        cbGame = (CheckBox) findViewById(R.id.cbGame);
        cbOutdoors = (CheckBox) findViewById(R.id.cbOutdoors);
        cbPets = (CheckBox) findViewById(R.id.cbPets);
        cbOthers = (CheckBox) findViewById(R.id.cbOthers);

        cbSports.setOnCheckedChangeListener(chkListener);
        cbLearn.setOnCheckedChangeListener(chkListener);
        cbFood.setOnCheckedChangeListener(chkListener);
        cbArts.setOnCheckedChangeListener(chkListener);
        cbMovie.setOnCheckedChangeListener(chkListener);
        cbGame.setOnCheckedChangeListener(chkListener);
        cbOutdoors.setOnCheckedChangeListener(chkListener);
        cbPets.setOnCheckedChangeListener(chkListener);
        cbOthers.setOnCheckedChangeListener(chkListener);


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
                        String actStart = tvActStart_Display.getText().toString();
                        String actEnd = tvActEnd_Display.getText().toString();



                        actVO.setActName(actName);
                        actVO.setActLat(latitude);
                        actVO.setActLong(longitude);
                        actVO.setActStartDate(Timestamp.valueOf(actStart + ":00"));
                        actVO.setActEndDate(Timestamp.valueOf(actEnd + ":00"));

                        String content = etContent.getText().toString();

                        actVO.setActContent(content);
                        actVO.setActAdr(locationName);

                        Bitmap srcPicture = BitmapFactory.decodeResource(getResources(), R.drawable.p);
                        Bitmap picture = Common.downSize(srcPicture, 512);
                        image = Common.bitmapToPNG(picture);

                        if(isUpdateValid(actVO)){

                            Toast.makeText(getBaseContext(), "活動已修改", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ActUpdateActivity.this, ActDetailActivity.class);
                            Bundle bundle2 = new Bundle();
                            bundle2.putSerializable("actVO", actVO);
                            intent.putExtras(bundle2);
                            startActivity(intent);

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

    private CheckBox.OnCheckedChangeListener  chkListener = new CheckBox.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (cbSports.isChecked())
                poi_1 = "Sports";
            else
                poi_1 = "";
            if (cbLearn.isChecked())
                poi_2 = "Learning";
            else
                poi_2 = "";
            if (cbFood.isChecked())
                poi_3 = "Food and Drink";
            else
                poi_3 = "";
            if (cbArts.isChecked())
                poi_4 = "Arts";
            else
                poi_4 = "";
            if (cbMovie.isChecked())
                poi_5 = "Movie";
            else
                poi_5 = "";
            if (cbGame.isChecked())
                poi_6 = "Game";
            else
                poi_6 = "";
            if (cbOutdoors.isChecked())
                poi_7 = "Outdoors";
            else
                poi_7 = "";
            if (cbPets.isChecked())
                poi_8 = "Pets";
            else
                poi_8 = "";
            if (cbOthers.isChecked())
                poi_9 = "Others";
            else
                poi_9 = "";
        }
    };

    private void dateTime(){

//活動開始時間
        Button tvActStart = (Button) findViewById(R.id.btnStartDate_ActUpdate);
        tvActStart.setOnClickListener(new View.OnClickListener() {

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
                                if(asDay < today){
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

                            }
                        }, asHour, asMinute, true);

                timePicker.show();
                datePicker.show();

            }
        });

//活動結束時間
//預設將按鈕隱藏
        btnActEnd = (Button) findViewById(R.id.btnEndDate_ActUpdate);
        btnActEnd.setOnClickListener(new View.OnClickListener() {
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

                                aeYear = year;
                                if(aeYear < asYear){
                                    Toast.makeText(ActUpdateActivity.this, "不得早於開始年份", Toast.LENGTH_SHORT).show();
                                    aeYear = asYear;
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                if(aeYear < thisYear || aeYear > thisYear + 1){
                                    Toast.makeText(ActUpdateActivity.this, "無效年份設定", Toast.LENGTH_SHORT).show();
                                    aeYear = asYear;
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                aeMonth = month;
                                if(aeYear < asMonth){
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
                                if(aeDay < asDay){
                                    Toast.makeText(ActUpdateActivity.this, "不得早於開始日期", Toast.LENGTH_SHORT).show();
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                if(aeDay < today){
                                    Toast.makeText(ActUpdateActivity.this, "無效日期設定", Toast.LENGTH_SHORT).show();
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
                                if(aeHour < asHour){
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

                            }
                        }, aeHour, aeMinute, true);
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

                String jsonIn = new InsertTask(url, "update", actVO, image).execute().get();

                count = gson.fromJson(jsonIn, Integer.class);

            } catch (Exception e){
                Log.e(TAG, e.toString());
            }

        }else{
            showToast(this, R.string.msg_NoNetwork);
        }
        return count != null;
    }

    //監聽返回鍵點擊事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();

        }
        return true;
    }
}
