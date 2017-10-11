package iii.com.chumeet.act;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import iii.com.chumeet.VO.ActVO;

import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;

/**
 * Created by sonic on 2017/10/9.
 */

public class ActPoiActivity extends AppCompatActivity {
    private final static String TAG = "ActPoiActivity";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvActs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_poi);

        rvActs = (RecyclerView) findViewById(R.id.rvActsPoi);
        rvActs.setLayoutManager(new LinearLayoutManager(this ,LinearLayoutManager.VERTICAL, false));

        swipeRefreshLayout =
                (SwipeRefreshLayout) findViewById(R.id.actPoiRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showAll();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }



    @Override
    protected void onStart(){
        super.onStart();

        showAll();

    }

    private void showAll() {
        Bundle bundle = this.getIntent().getExtras();
        Integer poiID = bundle.getInt("poiID");

        if(networkConnected(this)){
            String url = Common.URL + "ActServletAndroid";
            List<ActVO> actVOs = null;
            try{
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getActPoi");
                jsonObject.addProperty("poiID", poiID);
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
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
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
            int actID = actVO.getActID();

            new GetImageTask(url, actID, imageSize, myViewHolder.ivActImg).execute();

            myViewHolder.tvActName.setText(actVO.getActName());
            myViewHolder.tvActDate.setText(actVO.getActStartDate());
            myViewHolder.ivActImg.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    Intent intent = new Intent(ActPoiActivity.this, ActDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("actVO", actVO);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivActImg;
            TextView tvActName, tvActDate;

            MyViewHolder(View itemView) {
                super(itemView);
                ivActImg = (ImageView) itemView.findViewById(R.id.ivActImg);
                tvActName = (TextView) itemView.findViewById(R.id.tvActName);
                tvActDate = (TextView) itemView.findViewById(R.id.tvActDate);
            }
        }
    }
}
