package iii.com.chumeet.club;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import iii.com.chumeet.Common;
import iii.com.chumeet.home.HomeActivity;
import iii.com.chumeet.R;
import iii.com.chumeet.Task.GetImageTask;

public class ClubDetailActivity extends AppCompatActivity {
    private static final String TAG = "ClubDetailActivity";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView clubImg;
    private TextView clubName;
    private TextView clubCont;
    private ClubVO clubVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);

        clubImg = (ImageView) findViewById(R.id.ivClubDetImg);
        clubName = (TextView) findViewById(R.id.tvClubDetName);
        clubCont = (TextView) findViewById(R.id.tvClubDetContent);

        swipeRefreshLayout
                = (SwipeRefreshLayout) findViewById(R.id.clubDetailRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showResults();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        showResults();
//        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onStart(){
        super.onStart();
        showResults();
    }


    private void showResults() {
        String url = Common.URL + "ClubServletAndroid";
        clubVO = (ClubVO) getIntent().getSerializableExtra("clubVO");

        int imageSize = getResources().getDisplayMetrics().widthPixels / 4;

        int id = clubVO.getClubId();
        try {
            new GetImageTask(url, id, imageSize, clubImg).execute().get();
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
        clubName.setText(clubVO.getClubName());
        clubCont.setText(clubVO.getClubContent());



        Log.d(TAG, "showResults");
    }


    //監聽返回鍵點擊事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){

            Intent intent = new Intent(ClubDetailActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        }
        return true;
    }
}
