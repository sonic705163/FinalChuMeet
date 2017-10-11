package iii.com.chumeet.act;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import iii.com.chumeet.R;

public class ActInsert_2Activity extends AppCompatActivity {
    private static final String TAG = "ActInsert_2Activity";
    private TextView tvActStart_Display, tvActEnd_Display;
    private TextView tvActEnd;
    private Button btNext;
    private CheckBox cbSports, cbLearn, cbFood, cbArts, cbMovie, cbGame, cbOutdoors, cbPets, cbOthers;
    private String poi_1, poi_2, poi_3, poi_4, poi_5, poi_6, poi_7, poi_8, poi_9;
    private int asYear, asMonth, asDay, asHour, asMinute;
    private int aeYear, aeMonth, aeDay, aeHour, aeMinute;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_insert2);

        findViews();
        dateTime();

    }

    private void findViews() {
        tvActStart_Display = (TextView) findViewById(R.id.tvDatePicker_ActStart_show);
        tvActEnd_Display = (TextView) findViewById(R.id.tvDatePicker_ActEnd_show);

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

//神奇小按鈕
        TextView tmb = (TextView) findViewById(R.id.tvActPoi);
        tmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvActEnd_visible();
                tvActStart_Display.setText("2017-10-19 09:00");
                tvActEnd_Display.setText("2017-10-19 15:00");
            }
        });
//下一步
        btNext = (Button) findViewById(R.id.btActInsert_next2);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actStart = tvActStart_Display.getText().toString();
                String actEnd = tvActEnd_Display.getText().toString();

                Bundle bundle = ActInsert_2Activity.this.getIntent().getExtras();
                bundle.putString("poi_1", poi_1);
                bundle.putString("poi_2", poi_2);
                bundle.putString("poi_3", poi_3);
                bundle.putString("poi_4", poi_4);
                bundle.putString("poi_5", poi_5);
                bundle.putString("poi_6", poi_6);
                bundle.putString("poi_7", poi_7);
                bundle.putString("poi_8", poi_8);
                bundle.putString("poi_9", poi_9);
                bundle.putString("actStart", actStart);
                bundle.putString("actEnd", actEnd);

                Log.d("actStart", actStart);

                Intent intent = new Intent(ActInsert_2Activity.this, ActInsert_3Activity.class);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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
        TextView tvActStart = (TextView) findViewById(R.id.tvDatePicker_ActStart);
        tvActStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatePickerDialog datePicker = new DatePickerDialog(ActInsert_2Activity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                Calendar calendar = Calendar.getInstance();
                                int thisYear = calendar.get(Calendar.YEAR);
                                int thisMonth = calendar.get(Calendar.MONTH);
                                int today = calendar.get(Calendar.DAY_OF_MONTH);

                                asYear = year;
                                if(asYear < thisYear || asYear > thisYear + 1){
                                    Toast.makeText(ActInsert_2Activity.this, "無效年份設定", Toast.LENGTH_SHORT).show();
                                    asYear = thisYear;
                                    asMonth = thisMonth;
                                    asDay = today;

                                    return;
                                }
                                asMonth = month;
                                if(asMonth < thisMonth && asYear <= thisYear ){
                                    Toast.makeText(ActInsert_2Activity.this, "無效月份設定", Toast.LENGTH_SHORT).show();
                                    asMonth = thisMonth;
                                    asDay = today;

                                    return;

                                }else if(asMonth >= thisMonth - 6 && asYear == thisYear + 1){
                                    Toast.makeText(ActInsert_2Activity.this, "日期請設定6個月以內", Toast.LENGTH_SHORT).show();
                                    asYear = thisYear;
                                    asMonth = thisMonth;
                                    asDay = today;

                                    return;
                                }
                                asDay = day;
                                if(asDay < today){
                                    Toast.makeText(ActInsert_2Activity.this, "無效日期設定", Toast.LENGTH_SHORT).show();
                                    asDay = today;


                                }
                            }
                        }, asYear, asMonth, asDay);

                TimePickerDialog timePicker = new TimePickerDialog (ActInsert_2Activity.this,
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

                                tvActEnd_visible();
                            }
                        }, asHour, asMinute, true);

                timePicker.show();
                datePicker.show();

            }
        });

//活動結束時間
//預設將按鈕隱藏
        tvActEnd = (TextView) findViewById(R.id.tvDatePicker_ActEnd);
        tvActEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(ActInsert_2Activity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                Calendar calendar = Calendar.getInstance();
                                int thisYear = calendar.get(Calendar.YEAR);
                                int thisMonth = calendar.get(Calendar.MONTH);
                                int today = calendar.get(Calendar.DAY_OF_MONTH);

                                aeYear = year;
                                if(aeYear < asYear){
                                    Toast.makeText(ActInsert_2Activity.this, "不得早於開始年份", Toast.LENGTH_SHORT).show();
                                    aeYear = asYear;
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                if(aeYear < thisYear || aeYear > thisYear + 1){
                                    Toast.makeText(ActInsert_2Activity.this, "無效年份設定", Toast.LENGTH_SHORT).show();
                                    aeYear = asYear;
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                aeMonth = month;
                                if(aeYear < asMonth){
                                    Toast.makeText(ActInsert_2Activity.this, "不得早於開始月份", Toast.LENGTH_SHORT).show();
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                if(aeMonth < thisMonth && aeYear <= thisYear ){
                                    Toast.makeText(ActInsert_2Activity.this, "無效月份設定", Toast.LENGTH_SHORT).show();
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;

                                }else if(aeMonth >= thisMonth - 6 && aeYear == thisYear + 1){
                                    Toast.makeText(ActInsert_2Activity.this, "日期請設定6個月以內", Toast.LENGTH_SHORT).show();
                                    aeYear = asYear;
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                aeDay = day;
                                if(aeDay < asDay){
                                    Toast.makeText(ActInsert_2Activity.this, "不得早於開始日期", Toast.LENGTH_SHORT).show();
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                if(aeDay < today){
                                    Toast.makeText(ActInsert_2Activity.this, "無效日期設定", Toast.LENGTH_SHORT).show();
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;

                                }
                            }
                        }, aeYear, aeMonth, aeDay);

                TimePickerDialog timePicker = new TimePickerDialog (ActInsert_2Activity.this,
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute){
                                aeHour = hour;
                                if(aeHour < asHour){
                                    Toast.makeText(ActInsert_2Activity.this, "不得早於開始時間", Toast.LENGTH_SHORT).show();
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                aeMinute = minute;
                                if(aeHour == asHour && aeMinute < asMinute){
                                    Toast.makeText(ActInsert_2Activity.this, "不得早於開始時間", Toast.LENGTH_SHORT).show();
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

//按鈕顯示
    private void tvActEnd_visible(){
        tvActEnd.setVisibility(View.VISIBLE);
        btNext.setVisibility(View.VISIBLE);
    }

    private String pad(int number) {
        if (number >= 10)
            return String.valueOf(number);
        else
            return "0" + String.valueOf(number);
    }

}

