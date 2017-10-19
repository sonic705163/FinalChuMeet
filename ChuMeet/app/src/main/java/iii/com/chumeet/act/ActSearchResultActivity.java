package iii.com.chumeet.act;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import iii.com.chumeet.Common;
import iii.com.chumeet.R;
import iii.com.chumeet.Task.GetImageTask;
import iii.com.chumeet.Task.MyTask;
import iii.com.chumeet.Tools;
import iii.com.chumeet.VO.ActVO;
import iii.com.chumeet.home.HomeActivity;

import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;


public class ActSearchResultActivity extends AppCompatActivity {
    private final static String TAG = "ActSearchResultActivity";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvActs;
    private String query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_search_result);



        rvActs = (RecyclerView) findViewById(R.id.rvActsSearch);
        rvActs.setLayoutManager(new LinearLayoutManager(this ,LinearLayoutManager.VERTICAL, false));

        swipeRefreshLayout =
                (SwipeRefreshLayout) findViewById(R.id.actSearchRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showAll();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        query = getIntent().getAction();
    }


    @Override
    protected void onStart(){
        super.onStart();

        showAll();

    }

    private void showAll() {
        if(networkConnected(this)){
            String url = Common.URL + "ActServletAndroid";
            List<ActVO> actVOs = null;

            try{
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getActName");
                jsonObject.addProperty("actName", query);
                String jsonOut = jsonObject.toString();
                String jsonIn = new MyTask(url, jsonOut).execute().get();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<ActVO>>(){}.getType();
                actVOs = gson.fromJson(jsonIn, listType);

            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            if(actVOs == null || actVOs.isEmpty()){
                showToast(this, R.string.msg_NoActsFound);
            }else{
                rvActs.setAdapter(new ActsRecyclerViewAdapter(this, actVOs));
            }
        }else{
            showToast(this, R.string.msg_NoNetwork);
        }
    }


    private class ActsRecyclerViewAdapter extends RecyclerView.Adapter<ActsRecyclerViewAdapter.MyViewHolder>{
        private LayoutInflater layoutInflater;
        private List<ActVO> actVOs;
        private int imageSize;

        ActsRecyclerViewAdapter(Context context, List<ActVO> actVOs){
            layoutInflater = LayoutInflater.from(context);
            this.actVOs = actVOs;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 2;
        }

        @Override
        public int getItemCount(){
            return actVOs.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_act_big, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int position){
            final ActVO actVO = actVOs.get(position);
            String url = Common.URL + "ActServletAndroid";
            SharedPreferences pref = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
            int memID = pref.getInt("memID", 1);
            int actID = actVO.getActID();

            new GetImageTask(url, actID, imageSize, myViewHolder.ivActImg).execute();

            myViewHolder.tvActName.setText(actVO.getActName());
            myViewHolder.tvActStartDate.setText("開始日期: "+Tools.toFormat(actVO.getActStartDate()));
            myViewHolder.tvActEndDate.setText("結束日期: "+Tools.toFormat(actVO.getActEndDate()));
            if(actVO.getMemID()==memID) {
                myViewHolder.ivHost.setVisibility(View.VISIBLE);
            }

            myViewHolder.cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    Intent intent = new Intent(ActSearchResultActivity.this, ActDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("actVO", actVO);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivActImg, ivHost;
            TextView tvActName, tvActStartDate, tvActEndDate;
            CardView cardView;

            MyViewHolder(View itemView) {
                super(itemView);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
                ivActImg = (ImageView) itemView.findViewById(R.id.ivActImg);
                tvActName = (TextView) itemView.findViewById(R.id.tvActName);
                tvActStartDate = (TextView) itemView.findViewById(R.id.tvActStartDate);
                tvActEndDate = (TextView) itemView.findViewById(R.id.tvActEndDate);
                ivHost = (ImageView) itemView.findViewById(R.id.ivHost);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG,"************************"+getTaskId()+"我被消滅了***************************");
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
