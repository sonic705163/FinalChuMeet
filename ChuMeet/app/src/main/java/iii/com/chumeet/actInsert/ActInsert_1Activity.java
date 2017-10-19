package iii.com.chumeet.actInsert;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import iii.com.chumeet.R;
import iii.com.chumeet.home.HomeActivity;

public class ActInsert_1Activity extends AppCompatActivity implements OnMapReadyCallback {
    private final static String TAG = "ActInsert_1Activity";
    private GoogleMap map;                                      //儲存地圖資訊
    private boolean isMapReady = false;
    private EditText etName, etLocationName;
    private Address address;
    private Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_insert);

        findViews();

        checkPermission();                                     //檢查若尚未授權，則向使用者要求定位權限

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fmMap_actInsert);
        mapFragment.getMapAsync(this);

    }




    private void findViews() {
        etName = (EditText) findViewById(R.id.etActInsertName);
        etLocationName = (EditText) findViewById(R.id.etActInsertLocation);

        Button btNext = (Button) findViewById(R.id.btActInsert_next);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actName = etName.getText().toString().trim();
                String locationName = etLocationName.getText().toString().trim();

                if(locationName.length() > 0 && actName.length() > 0 ){

                    locationNameToLatLng(locationName);

                    latitude = address.getLatitude();
                    longitude = address.getLongitude();

                    Intent intent = new Intent(ActInsert_1Activity.this, ActInsert_2Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("actName", actName);
                    bundle.putString("locationName", locationName);
                    bundle.putDouble("Lat", latitude);
                    bundle.putDouble("Lng", longitude);

                    intent.putExtras(bundle);
                    startActivity(intent);

                }else {
                    Toast.makeText(getBaseContext(), "請輸入名稱、地點", Toast.LENGTH_SHORT).show();
                }
            }
        });

//神奇小按鈕
        TextView tmb = (TextView) findViewById(R.id.tvActInsertName);
        tmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setText("JavaBA103班專題發表會");
                etLocationName.setText("320桃園市中壢區中大路300號");
            }
        });
    }

    public void onLocationNameClick(View view){
        if (!isMapReady){
            return;
        }
        String locationName = etLocationName.getText().toString().trim();

        if(locationName.length() > 0){
            locationNameToMarker(locationName);
        }else {
            Toast.makeText(getBaseContext(), R.string.msg_NoNetwork,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void locationNameToLatLng(String locationName){
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addressList = null;
        int maxResults = 1;
        try{

            //解譯地名/地址後可能產生多筆位置資訊，所以回傳List<Address>
            //將maxResults設為1，限定回傳1筆
            addressList = geocoder.getFromLocationName(locationName, maxResults);

        }catch (IOException e){

            //如無法連結到提供服務的伺服器，印出 Log.e
            Log.e(TAG, e.toString());

        }

        if(addressList == null || addressList.isEmpty()){

            Toast.makeText(getBaseContext(),"addressList is null", Toast.LENGTH_SHORT).show();

        }else {

            //因為當初只限定回傳1筆，所以只取第1個Address物件即可
            address = addressList.get(0);
        }
    }

    private void locationNameToMarker(String locationName){
        //新増標記前，清除舊有標記
        map.clear();
        locationNameToLatLng(locationName);

        //Address物件取出緯經度並轉呈LatLng物件
        LatLng position = new LatLng(address.getLatitude(),
                address.getLongitude());

        //將地址取出當作標記的描述文字
        String snippet = address.getAddressLine(0);

        //將地名或地址轉成位置後在地圖打上對應標記
        map.addMarker(new MarkerOptions().position(position)
                .title(locationName).snippet(snippet));

        //將鏡頭焦點設定在使用者輸入的地點上
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position).zoom(15).build();
        map.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));


    }

    @Override
    public void onMapReady(GoogleMap map) {         //當GoogleMap物件可以用會自動呼叫此方法
        isMapReady = true;

        this.map = map;

        map.setTrafficEnabled(true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

    }

    //檢查若尚未授權, 則向使用者要求定位權限
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION
            }, 200);
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
