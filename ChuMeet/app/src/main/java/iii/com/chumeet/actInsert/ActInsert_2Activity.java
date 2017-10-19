package iii.com.chumeet.actInsert;

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
    private Button btnActEnd,btNext;
    private CheckBox cbSports, cbLearn, cbFood, cbArts, cbMovie, cbGame, cbOutdoors, cbPets, cbOthers;
    private int poi_1, poi_2, poi_3, poi_4, poi_5, poi_6, poi_7, poi_8, poi_9;
    private int asYear, asMonth, asDay, asHour, asMinute;
    private int aeYear, aeMonth, aeDay, aeHour, aeMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_insert2);

        findViews();
        dateTime();

    }

    @Override
    protected void onStart() {
        super.onStart();

        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = calendar.get(Calendar.MONTH);
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        asYear = aeYear = thisYear;
        asMonth = aeMonth = thisMonth;
        asDay = aeDay = today;
        asHour = aeHour = hour;
        asMinute = aeMinute = minute;

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

////神奇小按鈕
//        TextView tmb = (TextView) findViewById(R.id.tvActPoi);
//        tmb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tvActEnd_visible();
//                tvActStart_Display.setText("2017-10-20 09:00");
//                tvActEnd_Display.setText("2017-10-20 15:00");
//            }
//        });
//下一步
        btNext = (Button) findViewById(R.id.btActInsert_next2);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actStart = tvActStart_Display.getText().toString();
                String actEnd = tvActEnd_Display.getText().toString();

                Bundle bundle = ActInsert_2Activity.this.getIntent().getExtras();
                bundle.putInt("poi_1", poi_1);
                bundle.putInt("poi_2", poi_2);
                bundle.putInt("poi_3", poi_3);
                bundle.putInt("poi_4", poi_4);
                bundle.putInt("poi_5", poi_5);
                bundle.putInt("poi_6", poi_6);
                bundle.putInt("poi_7", poi_7);
                bundle.putInt("poi_8", poi_8);
                bundle.putInt("poi_9", poi_9);
                bundle.putString("actStart", actStart);
                bundle.putString("actEnd", actEnd);

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
                poi_1 = 9;
            else
                poi_1 = 0;
            if (cbLearn.isChecked())
                poi_2 = 12;
            else
                poi_2 = 0;
            if (cbFood.isChecked())
                poi_3 = 18;
            else
                poi_3 = 0;
            if (cbArts.isChecked())
                poi_4 = 20;
            else
                poi_4 = 0;
            if (cbMovie.isChecked())
                poi_5 = 8;
            else
                poi_5 = 0;
            if (cbGame.isChecked())
                poi_6 = 21;
            else
                poi_6 = 0;
            if (cbOutdoors.isChecked())
                poi_7 = 23;
            else
                poi_7 = 0;
            if (cbPets.isChecked())
                poi_8 = 24;
            else
                poi_8 = 0;
            if (cbOthers.isChecked())
                poi_9 = 15;
            else
                poi_9 = 0;
        }
    };

    private void dateTime(){

//活動開始時間
        Button tvActStart = (Button) findViewById(R.id.btnDatePicker_ActStart);
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
                                if(asDay < today && asMonth == thisMonth){
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

                                aeYear = asYear;
                                aeMonth = asMonth;
                                aeDay = asDay;
                                aeHour = asHour;
                                aeMinute = asMinute;

                                tvActEnd_visible();
                            }
                        }, asHour, asMinute, false);

                timePicker.show();
                datePicker.show();

            }
        });

//活動結束時間
//預設將按鈕隱藏
        btnActEnd = (Button) findViewById(R.id.btnDatePicker_ActEnd);
        btnActEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(ActInsert_2Activity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                Calendar calendar = Calendar.getInstance();
                                int thisYear = calendar.get(Calendar.YEAR);
                                int thisMonth = calendar.get(Calendar.MONTH);

                                aeYear = year;
                                if(aeYear < asYear){
                                    Toast.makeText(ActInsert_2Activity.this, "不得早於開始時間", Toast.LENGTH_SHORT).show();
                                    aeYear = asYear;
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                if(aeYear > thisYear + 1){
                                    Toast.makeText(ActInsert_2Activity.this, "無效年份設定", Toast.LENGTH_SHORT).show();
                                    aeYear = asYear;
                                    aeMonth = asMonth;
                                    aeDay = asDay;
                                    aeHour = asHour;
                                    aeMinute = asMinute;
                                    return;
                                }
                                aeMonth = month;
                                if(aeMonth < asMonth && aeYear == thisYear){
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
                                if(aeDay < asDay && aeMonth == asMonth ){
                                    Toast.makeText(ActInsert_2Activity.this, "不得早於開始日期", Toast.LENGTH_SHORT).show();
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
                                if(aeHour < asHour && aeYear == asYear && aeMonth == asMonth && aeDay == asDay){
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
                        }, aeHour, aeMinute, false);
                timePicker.show();
                datePicker.show();

            }
        });

    }

//按鈕顯示
    private void tvActEnd_visible(){
        btnActEnd.setVisibility(View.VISIBLE);
        btNext.setVisibility(View.VISIBLE);
    }

    private String pad(int number) {
        if (number >= 10)
            return String.valueOf(number);
        else
            return "0" + String.valueOf(number);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG,"************************"+getTaskId()+"我被消滅了***************************");
    }

}

