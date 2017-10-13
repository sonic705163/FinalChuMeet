package iii.com.chumeet.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import iii.com.chumeet.act.ActDetailActivity;

import static android.content.Context.MODE_PRIVATE;
import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;



public class GoingFragment extends Fragment {
    private static final String TAG = "GoingFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvActs;
    private Toolbar toolbar ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_going, container, false);

        rvActs = (RecyclerView) view.findViewById(R.id.rvActsGoing);
        rvActs.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeRefreshLayout =
                (SwipeRefreshLayout) view.findViewById(R.id.goingRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showAll();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view ;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) getView().findViewById(R.id.toolbar_going);
        toolbar.setTitle("You're going");

//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onStart() {
        super.onStart();
        showAll();
    }

    private void showAll(){
        SharedPreferences pref = getActivity().getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        Integer memID = pref.getInt("memID", 1);


        if(networkConnected(getActivity())){
            String url = Common.URL + "ActServletAndroid";
            List<ActVO> actVOs = null;
            try{
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getActGoing");
                jsonObject.addProperty("id", memID);
                String jsonOut = jsonObject.toString();
                String jsonIn = new MyTask(url, jsonOut).execute().get();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<ActVO>>(){}.getType();
                actVOs = gson.fromJson(jsonIn, listType);
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            if(actVOs == null || actVOs.isEmpty()){
                showToast(getActivity(), R.string.msg_NoActsFound);
            }else{
                rvActs.setAdapter(new ActsRecyclerViewAdapter(getActivity(), actVOs));
            }
        }else{
            showToast(getActivity(), R.string.msg_NoNetwork);

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
        public void onBindViewHolder(MyViewHolder myViewHolder, int postion){
            final ActVO actVO = actVOs.get(postion);
            String url = Common.URL + "ActServletAndroid";
            int id = actVO.getActID();

            new GetImageTask(url, id, imageSize, myViewHolder.ivActImg).execute();

            myViewHolder.tvActName.setText(actVO.getActName());
            myViewHolder.tvActDate.setText(actVO.getActStartDate().toString());
            myViewHolder.ivActImg.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    Intent intent = new Intent(getActivity(), ActDetailActivity.class);
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
