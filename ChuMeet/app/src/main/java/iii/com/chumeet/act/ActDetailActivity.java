package iii.com.chumeet.act;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import iii.com.chumeet.Common;
import iii.com.chumeet.Contents;
import iii.com.chumeet.QRCodeEncoder;
import iii.com.chumeet.R;
import iii.com.chumeet.Task.GetImageTask;
import iii.com.chumeet.Task.MyTask;
import iii.com.chumeet.VO.ActMemVO;
import iii.com.chumeet.VO.ActVO;
import iii.com.chumeet.VO.Mem_ActMemVO;
import iii.com.chumeet.home.HomeActivity;

import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;


public class ActDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "ActDetailActivity";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView actImg;
    private TextView actName,actCont,actDate,actHost,actLoc;
    private Button btnJoin;
    private GoogleMap map;
    private ActVO actVO;
    private Bundle bundle;
    private Integer status = null;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar_QRCode);
        actImg = (ImageView) findViewById(R.id.ivActDetImg);
        actName = (TextView) findViewById(R.id.tvActDetName);
        actCont = (TextView) findViewById(R.id.tvClubDetContent);
        actDate = (TextView) findViewById(R.id.tvActDetDate);
        actLoc = (TextView) findViewById(R.id.tvActDetLoc);
        actHost = (TextView) findViewById(R.id.tvActDetHost);

        swipeRefreshLayout =
                (SwipeRefreshLayout) findViewById(R.id.actDetailRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showResults();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        btnJoin = (Button) findViewById(R.id.btnJoinAct);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Common.URL + "ActServletAndroid";
                String jsonIn = null;
                ActMemVO actMemVO = null;

                SharedPreferences pref = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                Integer memID = pref.getInt("memID", 0);

                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "getActMem");          //一直點會不會有問題?
                    jsonObject.addProperty("id", actVO.getActID());
                    jsonObject.addProperty("memId", memID);
                    String jsonOut = jsonObject.toString();

                    jsonIn = new MyTask(url, jsonOut).execute().get();

                    Gson gson = new Gson();
                    actMemVO = gson.fromJson(jsonIn, ActMemVO.class);

                }catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if(actMemVO != null) {
                    status = actMemVO.getActMemStatus();
                }

                if(status == null) {                                        //第一次加入
                    try {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "joinAct");
                        jsonObject.addProperty("actMemStatus", 2);
                        jsonObject.addProperty("id", actVO.getActID());
                        jsonObject.addProperty("memId", memID);
                        String jsonOut = jsonObject.toString();

                        jsonIn = new MyTask(url, jsonOut).execute().get();

                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (jsonIn != null) {
                        btnJoin.setText("退出活動");
                        actMemFmStart();
                    } else {
                        Log.d(TAG, "加入失敗");
                    }
                }else{
                    switch (status) {
                        case 1:

                            break;
                        case 2:
                            try{
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("action", "ActMemStatusChange");
                                jsonObject.addProperty("actMemStatus", 6);
                                jsonObject.addProperty("id", actVO.getActID());
                                jsonObject.addProperty("memId", memID);
                                String jsonOut = jsonObject.toString();

                                jsonIn = new MyTask(url, jsonOut).execute().get();

                            }catch (Exception e){
                                Log.e(TAG, e.toString());
                            }
                            if (jsonIn != null) {
                                btnJoin.setText("加入活動");
                                actMemFmStart();
                            } else {
                                Log.d(TAG, "退出失敗");
                            }
                            break;
                        case 3:
                            btnJoin.setText("你不可能在這");
                            break;
                        case 4:
                            btnJoin.setText("你不可能進得來");
                            break;
                        case 5:
                            btnJoin.setText("加入活動");
                            break;
                        case 6:
                            try {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("action", "ActMemStatusChange");
                                jsonObject.addProperty("actMemStatus", 2);
                                jsonObject.addProperty("id", actVO.getActID());
                                jsonObject.addProperty("memId", memID);
                                String jsonOut = jsonObject.toString();

                                jsonIn = new MyTask(url, jsonOut).execute().get();

                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }

                            if (jsonIn != null) {
                                btnJoin.setText("退出活動");
                                actMemFmStart();
                            } else {
                                Log.d(TAG, "加入失敗");
                            }
                            break;
                    }
                }
            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fmMap);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_detail, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_QRCode:
                if(status==1) {                            //1=hoder, 2=joined member
                    onGenerateQrCodeClick();
                }else if(status==2){
                    onScanQrCodeClick();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onScanQrCodeClick() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        // Set to true to enable saving the barcode image and sending its path in the result Intent.
        integrator.setBarcodeImageEnabled(true);
        // Set to false to disable beep on scan.
        integrator.setBeepEnabled(false);
        // Use the specified camera ID.
        integrator.setCameraId(0);
        // By default, the orientation is locked. Set to false to not lock.
        integrator.setOrientationLocked(false);
        // Set a prompt to display on the capture screen.
        integrator.setPrompt("Scan a QR Code");
        // Initiates a scan
        integrator.initiateScan();
    }

    // Getting the results after scanning
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null && intentResult.getContents() != null) {
            String s = intentResult.getContents();
            Toast.makeText(this, "簽到成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Result Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    public void onGenerateQrCodeClick() {
        Integer etQRCodeText = actVO.getActID();
        String qrCodeText = etQRCodeText.toString();
        Log.d(TAG, qrCodeText);

        // QR code image's length is 1/2 the width of the window,
        int dimension = getResources().getDisplayMetrics().widthPixels /2;

        // Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrCodeText, null,
                Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                dimension);
        try {
            LayoutInflater inflater = getLayoutInflater();
            final View view = inflater.inflate(R.layout.qrcode, null);

            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            ImageView myImage = (ImageView) view.findViewById(R.id.ivQRCode);
            myImage.setImageBitmap(bitmap);

            new AlertDialog.Builder(this)
                    .setView(view)
                    .setNeutralButton("取消",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();

        } catch (WriterException e) {
            Log.e(TAG, e.toString());
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        //先確定一下你有沒有加入過
        checkJoined();

//字太長修一下

        StringBuilder sb = new StringBuilder(actVO.getActName());
        sb.setLength(14);
        toolbar.setTitle(sb);
        toolbar.inflateMenu(R.menu.toolbar_menu_detail);
        setSupportActionBar(toolbar);


        showResults();

    }


    private void checkJoined() {
        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        Integer memID = pref.getInt("memID", 0);

        bundle = this.getIntent().getExtras();
        actVO =  (ActVO) bundle.getSerializable("actVO");

        ActMemVO actMemVO ;


        if (networkConnected(this)) {
            String url = Common.URL + "ActServletAndroid";

            try{
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getActMem");          //先找ActMem有沒有資料
                jsonObject.addProperty("id", actVO.getActID());
                jsonObject.addProperty("memId", memID);
                String jsonOut = jsonObject.toString();

                String jsonIn = new MyTask(url, jsonOut).execute().get();

                Gson gson = new Gson();
                actMemVO = gson.fromJson(jsonIn, ActMemVO.class);

                if(actMemVO != null){
                    status = actMemVO.getActMemStatus();        //有資料時取actMemStatus//
                    switch (status){                            //1=hoder, 2=joined member 3=invited 4=banned 5=tracking 6=leave
                        case 1:
                            btnJoin.setText("解散活動");
                            break;
                        case 2:
                            btnJoin.setText("退出活動");
                            break;
                        case 3:
                            btnJoin.setText("你不可能在這");
                            break;
                        case 4:
                            btnJoin.setText("你不可能進得來");
                            break;
                        case 5:
                            btnJoin.setText("加入活動");
                            break;
                        case 6:
                            btnJoin.setText("加入活動");
                            break;

                    }
                }
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            actMemFmStart();
        }else{
            showToast(this, R.string.msg_NoNetwork);
        }

    }

    private void actMemFmStart(){
        bundle.putInt("ActID", actVO.getActID());
        ActMemFragment fragment = new ActMemFragment();

        fragment.setArguments(bundle);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fmActDetail, fragment).commit();
    }





    private void showResults() {
        String url = Common.URL + "ActServletAndroid";

        if(networkConnected(this)){
            try{
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getActHost");
                jsonObject.addProperty("id", actVO.getActID());
                jsonObject.addProperty("actMemStatus", 1);

                String jsonOut = jsonObject.toString();
                String jsonIn = new MyTask(url, jsonOut).execute().get();

                Gson gson = new Gson();
                Mem_ActMemVO mem_actMemVO = gson.fromJson(jsonIn, Mem_ActMemVO.class);

                int imageSize = getResources().getDisplayMetrics().widthPixels / 4;

                new GetImageTask(url, actVO.getActID(), imageSize, actImg).execute().get();

                actName.setText(actVO.getActName());
                actCont.setText(actVO.getActContent());
                actLoc.setText(actVO.getActLocName());
                actHost.setText(mem_actMemVO.getMemName());
                String startDate = actVO.getActStartDate();
                String endDate = actVO.getActEndDate();

                actDate.setText("Start:  " + startDate + "\nEnd:    " + endDate);

            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
        }else{
            showToast(this, R.string.msg_NoNetwork);
        }
    }





    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        map.setTrafficEnabled(true);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        if(actVO == null){
            showToast(this, R.string.msg_NoActsFound);
        }else{
            showMap(actVO);
        }
    }

    private void showMap(ActVO actVO){
        LatLng position = new LatLng(actVO.getActLat(), actVO.getActLong());
        String snippet = getString(R.string.col_Name) + ":" + actVO.getActName() + "\n" +
                getString(R.string.col_Address) + ":" + actVO.getActAdr();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(9)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);

        map.addMarker(new MarkerOptions()
                .position(position)
                .title(actVO.getActName())
                .snippet(snippet));

//        map.setInfoWindowAdapter(new MyInfoWindowAdapter(this, actVO));

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
