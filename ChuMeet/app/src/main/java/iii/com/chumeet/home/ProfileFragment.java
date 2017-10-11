package iii.com.chumeet.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import iii.com.chumeet.Common;
import iii.com.chumeet.R;
import iii.com.chumeet.Task.GetImageTask;
import iii.com.chumeet.Task.MyTask;
import iii.com.chumeet.VO.MemVO;
import iii.com.chumeet.login.MainActivity;

import static android.content.Context.MODE_PRIVATE;
import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar ;
    private ImageView memImg;
    private TextView memName;
    private MemVO memVO;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        memImg = (ImageView) view.findViewById(R.id.ivProfileImg);
        memName = (TextView) view.findViewById(R.id.tvProfileName);

        swipeRefreshLayout =
                (SwipeRefreshLayout) view.findViewById(R.id.profileRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showResults();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (Toolbar) getView().findViewById(R.id.toolbar_profile);
        toolbar.setTitle("Profile");

        setHasOptionsMenu(true);

                            //一旦调用((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                            //就会导致ActivityonCreateOptionsMenu()方法的调用, 而Activity会根据其中Fragment是否设置了setHasOptionsMenu(true)来调用Fragment的
                            //onCreateOptionsMenu()方法, 调用顺序是树形的, 按层级调用, 中间如果有false则跳过.

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

                            //设置导航图标、添加菜单点击事件要在setSupportActionBar方法之后
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        SharedPreferences pref = getActivity().getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                        pref.edit().putBoolean("login", false).apply();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }

    //即先clear()一下, 這樣按鈕就只有Fragment中設置的自己的了, 不會有Activity中的按鈕
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_menu_profile, menu);
    }

    @Override
    public void onStart(){
        super.onStart();
        showResults();
    }


    public void showResults(){
        String url = Common.URL + "MemServletAndroid";
        int imageSize = getResources().getDisplayMetrics().widthPixels / 4;

        if(networkConnected(getActivity())){

            SharedPreferences pref = getActivity().getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
            Integer memID = pref.getInt("memID", 0);


            MemVO memVO = null;

            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findById");
                jsonObject.addProperty("id", memID);
                String jsonOut = jsonObject.toString();
                String jsonIn = new MyTask(url, jsonOut).execute().get();

                Gson gson = new Gson();
                Type type = new TypeToken<MemVO>(){}.getType();
                memVO = gson.fromJson(jsonIn, type);

            }catch (Exception e){
                Log.e(TAG, e.toString());
            }

            if(memVO == null){
                showToast(getActivity(), R.string.msg_NoClubsFound);
            }else{
                memName.setText(memVO.getMemName());
            }

            try{
                new GetImageTask(url, memID, imageSize, memImg).execute().get();
            }catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        }else{
            showToast(getActivity(), R.string.msg_NoNetwork);
        }

        Log.d(TAG, "showResults");
//        Bundle bundle = getActivity().getIntent().getExtras();
//        memVO = (MemVO) bundle.getSerializable("memVO");
//       if (memVO != null){
//           memName.setText(memVO.getMemName());
//       }

    }






}
