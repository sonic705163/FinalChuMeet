package iii.com.chumeet.act;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import iii.com.chumeet.home.HomeActivity;
import iii.com.chumeet.R;

public class ActInsert_1Activity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    private final static String TAG = "ActInsert_1Activity";
    static final int MIN_TIME = 5000;                           //位置更新條件:5000 毫秒
    static final float MIN_DIST = 0;                            //位置更新條件:5 公尺
    private LocationManager mgr;                                //定位管理員
    private LatLng currPoint;                                   //儲存目前的位置
    private boolean isGPSEnabled;                               //GPS定位是否可用
    private boolean isNetworkEnabled;                           //網路定位是否可用
    private GoogleMap map;                                      //儲存地圖資訊
    private UiSettings uiSettings;                              //儲存地圖UI設定
    private boolean isMapReady = false;

    private EditText etName, etLocationName;
    private Address address;
    private Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_insert);

        findViews();

        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);      //取得系統服務LocationManager物件
        checkPermission();                                               //檢查若尚未授權，則向使用者要求定位權限

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fmMap_actInsert);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onResume(){
        super.onResume();

        enableLocationUpdates(true);        //開啟定位更新功能
    }

    @Override
    protected void onPause() {
        super.onPause();

        enableLocationUpdates(false);    //關閉定位更新功能
    }

    //開啟或關閉定位更新功能
    private void enableLocationUpdates(boolean isTurnOn) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){  // 使用者已經允許定位權限
            if (isTurnOn) {
                //檢查 GPS 與網路定位是否可用
                isGPSEnabled = mgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // 無提供者, 顯示提示訊息
                    Toast.makeText(this, "請確認已開啟定位功能!", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "取得定位資訊中...", Toast.LENGTH_LONG).show();
                    if (isGPSEnabled)
                        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, this);//向 GPS 定位提供者註冊位置事件監聽器
                    if (isNetworkEnabled)
                        mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST,  this);//向網路定位提供者註冊位置事件監聽器
                }
            }
            else {
                mgr.removeUpdates(this);    //停止監聽位置事件
            }
        }
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
                    return;
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

            Toast.makeText(getBaseContext(), R.string.msg_NoClubsFound,
                    Toast.LENGTH_SHORT).show();

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
    public void onLocationChanged(Location location) {
        if(location != null) { // 如果可以取得座標

            currPoint = new LatLng( location.getLatitude(), location.getLongitude()); //依照目前經緯度建立LatLng 物件

            if (map != null) { // 如果 Google Map 已經啟動完畢
                map.moveCamera(CameraUpdateFactory.newLatLng(currPoint)); // 將地圖中心點移到目前位置
                map.addMarker(new MarkerOptions().position(currPoint).title("目前位置")); //標記目前位置
            }
        }
        else { // 無法取得座標
            Toast.makeText(getBaseContext(), "暫時無法取得定位資訊...",
                    Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
