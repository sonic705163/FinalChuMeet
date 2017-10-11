package iii.com.chumeet.act;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.List;

import iii.com.chumeet.Common;
import iii.com.chumeet.R;
import iii.com.chumeet.Task.GetImageTask;
import iii.com.chumeet.Task.MyTask;
import iii.com.chumeet.VO.Mem_ActMemVO;

import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;


public class ActMemFragment extends Fragment{
    private final static String TAG = "ActMemFragment";
    private RecyclerView rvMems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mem, container, false);

        rvMems = (RecyclerView) view.findViewById(R.id.rvMems);
        rvMems.setLayoutManager(new LinearLayoutManager(getActivity() ,LinearLayoutManager.HORIZONTAL, false));

        return view ;
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

        Bundle bundle = getArguments();
        int actID =  bundle.getInt("ActID");


        if(networkConnected(getActivity())){
            String url = Common.URL + "ActServletAndroid";
            List<Mem_ActMemVO> mem_actMemVOs = null;
            List<Mem_ActMemVO> actMemVOs = new ArrayList<>();
            try{
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getMem_ActMem");
                jsonObject.addProperty("id", actID);
                String jsonOut = jsonObject.toString();

                String jsonIn = new MyTask(url, jsonOut).execute().get();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<Mem_ActMemVO>>(){}.getType();
                mem_actMemVOs = gson.fromJson(jsonIn, listType);
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            if(mem_actMemVOs == null || mem_actMemVOs.isEmpty()){
                showToast(getActivity(), R.string.msg_NoActsFound);
            }else{
                for(Mem_ActMemVO actMem : mem_actMemVOs){
                    if(actMem.getActMemStatus()==1 || actMem.getActMemStatus()==2){

                        actMemVOs.add(actMem);
                    }
                }
                rvMems.setAdapter(new ActsRecyclerViewAdapter(getActivity(), actMemVOs));
            }
        }else{
            showToast(getActivity(), R.string.msg_NoNetwork);

        }
    }




    private class ActsRecyclerViewAdapter extends RecyclerView.Adapter<ActsRecyclerViewAdapter.MyViewHolder>{
        private LayoutInflater layoutInflater;
        private List<Mem_ActMemVO> actMemVOs;
        private int imageSize;

        ActsRecyclerViewAdapter(Context context, List<Mem_ActMemVO> actMemVOs){
            layoutInflater = LayoutInflater.from(context);
            this.actMemVOs = actMemVOs;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        @Override
        public int getItemCount(){
            return actMemVOs.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_act, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int position){
            final Mem_ActMemVO actMemVO = actMemVOs.get(position);
            String url = Common.URL + "MemServletAndroid";
            int id = actMemVO.getMemID();

            new GetImageTask(url, id, imageSize, myViewHolder.ivActImg).execute();

            myViewHolder.tvActName.setText(actMemVO.getMemName());
//            myViewHolder.tvActDate.setText(memVO.getMemStatus());
//            myViewHolder.ivActImg.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View view){
//
//                    Intent intent = new Intent(getActivity(), ActDetailActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("actVO", memVO);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                }
//            });
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
