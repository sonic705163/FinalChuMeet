package iii.com.chumeet.act;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.Calendar;

import iii.com.chumeet.Common;
import iii.com.chumeet.R;
import iii.com.chumeet.Task.InsertTask;
import iii.com.chumeet.VO.ActVO;

import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;
import static java.lang.Integer.valueOf;

public class ActInsert_3Activity extends AppCompatActivity {
    private static final String TAG = "ActInsert_3Activity";
    private CheckBox cbIAgreed;
    private EditText etContent;
    private Button btNext;
    private String actIdStr;
    private byte[] image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_insert3);

        findViews();
    }

    private void findViews() {
        cbIAgreed = (CheckBox) findViewById(R.id.cbActInsert_agreed);
        etContent = (EditText) findViewById(R.id.etActInsert_content);

        //神奇小按鈕
        TextView tmb = (TextView) findViewById(R.id.tvActInsert_content);
        tmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etContent.setText("1970年代初期，全球能源危機與工業國家的貿易保護政策，使我國面臨潮流及環境的嚴峻挑戰，" +
                        "如何從傳統產業轉型至技術密集產業，並提高國家整體競爭力，成為當時政府重要的產經發展政策。 \n" +
                        "1979年5月17日，行政院第1631次院會通過「科學技術發展方案」，決定由政府與民間共同籌設「財團法人資訊工業策進會」。" +
                        "同年7月24日，在資政 李國鼎先生的大力奔走下，創設以「推廣資訊技術有效應用，提升國家整體競爭力；塑造資訊工業發展環境與條件，" +
                        "增強資訊產業競爭力」為宗旨的「財團法人資訊工業策進會」（Institute for Information Industry, III）。 \n" +
                        "三十多年來，資策會參與規劃研擬並推動政府各項資訊產業政策、致力資通訊前瞻研發、普及與深化資訊應用、" +
                        "培育資訊科技人才及參與國家資訊基礎建設等各項業務，成就備受各界肯定。");
            }
        });

        cbIAgreed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //按鈕顯示
                if (cbIAgreed.isChecked())
                    btNext.setVisibility(View.VISIBLE);
                else
                    btNext.setVisibility(View.INVISIBLE);
            }
        });

        btNext = (Button) findViewById(R.id.btActInsert_final);
        btNext.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if(etContent.getText().length() <= 0){
                    Toast.makeText(ActInsert_3Activity.this,"請輸入活動內容",Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = ActInsert_3Activity.this.getIntent().getExtras();
                String actName = bundle.getString("actName");
                String locationName = bundle.getString("locationName");
                Double lat = bundle.getDouble("Lat");
                Double lng = bundle.getDouble("Lng");
                String poi_1 = bundle.getString("poi_1");
                String poi_2 = bundle.getString("poi_2");
                String poi_3 = bundle.getString("poi_3");
                String poi_4 = bundle.getString("poi_4");
                String poi_5 = bundle.getString("poi_5");
                String poi_6 = bundle.getString("poi_6");
                String poi_7 = bundle.getString("poi_7");
                String poi_8 = bundle.getString("poi_8");
                String poi_9 = bundle.getString("poi_9");
                String actStart = bundle.getString("actStart");
                String actEnd = bundle.getString("actEnd");

                ActVO actVO = new ActVO();

                actVO.setActName(actName);
                actVO.setActLocName(locationName);
                actVO.setActLat(lat);
                actVO.setActLong(lng);
                actVO.setActStartDate(Timestamp.valueOf(actStart + ":" + 00));
                actVO.setActEndDate(Timestamp.valueOf(actEnd + ":" + 00));
                actVO.setActSignStartDate(Timestamp.valueOf(actStart + ":" + 00));
                actVO.setActSignEndDate(Timestamp.valueOf(actStart + ":" + 00));

//主辦人和創建時間
                SharedPreferences pref = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                Integer memID = pref.getInt("memID", 0);


                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                int mMinute = calendar.get(Calendar.MINUTE);
                int mSecond = calendar.get(Calendar.SECOND);
                String today = String.valueOf(mYear + "-" + (mMonth + 1) + "-" + mDay + " " + mHour + ":" + mMinute + ":" + mSecond );

                actVO.setMemID(memID);
                actVO.setActCreateDate(Timestamp.valueOf(today));
//以下預設
                actVO.setActStatus(1);
                actVO.setActPriID(1);
                actVO.setActTimeTypeID(0);
                actVO.setActTimeTypeCnt("");
                actVO.setActMemMax(99);
                actVO.setActMemMin(1);
                actVO.setActIsHot(0);
                actVO.setActContent(etContent.getText().toString());
                actVO.setActPost(999);
                actVO.setActAdr(locationName);

                Bitmap srcPicture = BitmapFactory.decodeResource(getResources(), R.drawable.p);
                Bitmap picture = Common.downSize(srcPicture, 512);
                image = Common.bitmapToPNG(picture);

                if(isInsertValid(actVO)){

                    actVO.setActID(valueOf(actIdStr));

                    Intent intent = new Intent(ActInsert_3Activity.this, ActDetailActivity.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("actVO", actVO);
                    intent.putExtras(bundle2);
                    startActivity(intent);

                }else {
                    Toast.makeText(ActInsert_3Activity.this, "新增活動失敗", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean isInsertValid(ActVO actVO){
        boolean answer = false;

        if(networkConnected(ActInsert_3Activity.this)){
            String url = Common.URL + "ActServletAndroid";

            try {
                Gson gson = new Gson();

                String jsonIn = new InsertTask(url, "insert", actVO, image).execute().get();

                actIdStr = gson.fromJson(jsonIn, String.class);

            } catch (Exception e){
                Log.e(TAG, e.toString());
            }
            answer = !actIdStr.equals("");
        }else{
            showToast(this, R.string.msg_NoNetwork);
        }
        return answer;
    }


}
