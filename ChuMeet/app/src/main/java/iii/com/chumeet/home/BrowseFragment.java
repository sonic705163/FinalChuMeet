package iii.com.chumeet.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import iii.com.chumeet.club.ClubDetailActivity;
import iii.com.chumeet.club.ClubVO;

import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;


public class BrowseFragment extends Fragment {
    private final static String TAG = "BrowseFragment";
//    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvClubs;
    private Toolbar toolbar ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_browse, container, false);

//        swipeRefreshLayout =
//                (SwipeRefreshLayout) view.findViewById(R.id.browseRefresh);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(true);
//                showAllClubs();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

        rvClubs = (RecyclerView) view.findViewById(R.id.rvClubs);

//        rvClubs.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvClubs.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.HORIZONTAL));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (Toolbar) getView().findViewById(R.id.toolbar_browse);
        toolbar.setTitle("Browse");
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


    }

    @Override
    public void onStart(){
        super.onStart();
        showAllClubs();
    }

    private void showAllClubs(){
        if(networkConnected(getActivity())){
            String url = Common.URL + "ClubServletAndroid";
            List<ClubVO> clubVOs = null;
            try{
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getAll");
                String jsonOut = jsonObject.toString();
                String jsonIn = new MyTask(url, jsonOut).execute().get();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<ClubVO>>(){}.getType();
                clubVOs = gson.fromJson(jsonIn, listType);
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            if(clubVOs == null || clubVOs.isEmpty()){
                showToast(getActivity(), R.string.msg_NoClubsFound);
            }else{
                rvClubs.setAdapter(new ClubsRecyclerViewAdapter(getActivity(), clubVOs));
            }
        }else{
            showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }

    private class ClubsRecyclerViewAdapter extends RecyclerView.Adapter<ClubsRecyclerViewAdapter.MyViewHolder>{
        private LayoutInflater layoutInflater;
        private List<ClubVO> clubVOs;
        private int imageSize;

        ClubsRecyclerViewAdapter(Context context, List<ClubVO> clubVOs){
            layoutInflater = LayoutInflater.from(context);
            this.clubVOs = clubVOs;

            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        @Override
        public int getItemCount(){
            return clubVOs.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View itemView = layoutInflater.inflate(R.layout.item_view_club, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int position){
            final ClubVO clubVO = clubVOs.get(position);
            String url = Common.URL + "ClubServletAndroid";
            int id = clubVO.getClubId();

//抓圖片
            new GetImageTask(url, id, imageSize, myViewHolder.ivClubImg).execute();
                myViewHolder.tvClubName.setText(clubVO.getClubName());
                myViewHolder.ivClubImg.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(getActivity(), ClubDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("clubVO", clubVO);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                });



        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivClubImg;
            TextView tvClubName;

            MyViewHolder(View itemView) {
                super(itemView);
                ivClubImg = (ImageView) itemView.findViewById(R.id.ivClubImg);
                tvClubName = (TextView) itemView.findViewById(R.id.tvClubName);

            }
        }
    }
}
