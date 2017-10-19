package iii.com.chumeet.actInsert;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import iii.com.chumeet.Common;
import iii.com.chumeet.R;
import iii.com.chumeet.Task.ActPoiChangeTask;
import iii.com.chumeet.Task.InsertOrUpdateTask;
import iii.com.chumeet.VO.ActPoiVO;
import iii.com.chumeet.VO.ActVO;
import iii.com.chumeet.act.ActDetailActivity;

import static iii.com.chumeet.Common.downSize;
import static iii.com.chumeet.Common.networkConnected;
import static iii.com.chumeet.Common.showToast;

public class ActInsert_3Activity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "ActInsert_3Activity";
    private final static int REQ_TAKE_PICTURE = 0;
    private static final int REQUESTCODE_PHOTO_LIB = 1;
    private File file;
    private Bitmap picture;
    private CheckBox cbIAgreed;
    private EditText etContent;
    private ImageView ivActImg;
    private Button btNext;
    private Integer actId;
    private byte[] image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_insert3);

        findViews();
    }

    @Override
    protected  void onStart(){
        super.onStart();
        checkPermission();
    }

    private void findViews() {
        cbIAgreed = (CheckBox) findViewById(R.id.cbActInsert_agreed);
        etContent = (EditText) findViewById(R.id.etActInsert_content);
        ivActImg = (ImageView) findViewById(R.id.ivActImg_ActInsert);
//        ivTackPicture = (ImageView) findViewById(R.id.ivTackPicture_ActInsert);

        //神奇小按鈕
        TextView tmb = (TextView) findViewById(R.id.tvActInsert_content);
        tmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etContent.setText("1970年代初期，全球能源危機與工業國家的貿易保護政策，使我國面臨潮流及環境的嚴峻挑戰，" +
                        "如何從傳統產業轉型至技術密集產業，並提高國家整體競爭力，成為當時政府重要的產經發展政策。 \n" +
                        "1979年5月17日，行政院第1631次院會通過「科學技術發展方案」，決定由政府與民間共同籌設「財團法人資訊工業策進會」。" +
                        "同年7月24日，在資政 李國鼎先生的大力奔走下，創設以「推廣資訊技術有效應用，提升國家整體競爭力；塑造資訊工業發展環境與條件，" +
                        "增強資訊產業競爭力」為宗旨的「財團法人資訊工業策進會」（Institute for Information Industry, III）。 \n" +
                        "三十多年來，資策會參與規劃研擬並推動政府各項資訊產業政策、致力資通訊前瞻研發、普及與深化資訊應用、" +
                        "培育資訊科技人才及參與國家資訊基礎建設等各項業務，成就備受各界肯定。");
            }
        });

        cbIAgreed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //按鈕顯示
                if (cbIAgreed.isChecked())
                    btNext.setVisibility(View.VISIBLE);
                else
                    btNext.setVisibility(View.INVISIBLE);
            }
        });

        btNext = (Button) findViewById(R.id.btActInsert_final);
        btNext.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if(etContent.getText().length() <= 0){
                    Toast.makeText(ActInsert_3Activity.this,"請輸入活動內容",Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = ActInsert_3Activity.this.getIntent().getExtras();
                String actName = bundle.getString("actName");
                String locationName = bundle.getString("locationName");
                Double lat = bundle.getDouble("Lat");
                Double lng = bundle.getDouble("Lng");

                String actStart = bundle.getString("actStart");
                String actEnd = bundle.getString("actEnd");

                ActPoiVO actPoiVO ;

                List<ActPoiVO> listVO = new ArrayList<>();

                for(int i=1; i<=9; i++) {
                    actPoiVO = new ActPoiVO();

                    int poi = bundle.getInt("poi_" + i );

                    if(poi != 0){
                        actPoiVO.setPoiid(poi);
                        listVO.add(actPoiVO);
                    }
                }


                ActVO actVO = new ActVO();

                actVO.setActName(actName);
                actVO.setActLocName(locationName);
                actVO.setActLat(lat);
                actVO.setActLong(lng);
                actVO.setActStartDate(Timestamp.valueOf(actStart + ":00"));
                actVO.setActEndDate(Timestamp.valueOf(actEnd + ":00"));
                actVO.setActSignStartDate(Timestamp.valueOf(actStart + ":00"));
                actVO.setActSignEndDate(Timestamp.valueOf(actEnd + ":00"));

//主辦人和創建時間
                SharedPreferences pref = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                Integer memID = pref.getInt("memID", 0);


                Timestamp today = new Timestamp(Calendar.getInstance().getTimeInMillis());

                actVO.setMemID(memID);
                actVO.setActCreateDate(today);
//以下預設
                actVO.setACTTYPE(1);
                actVO.setActStatus(1);
                actVO.setActPriID(1);
                actVO.setActTimeTypeID(1);
                actVO.setActTimeTypeCnt(null);
                actVO.setActMemMax(99);
                actVO.setActMemMin(1);
                actVO.setActIsHot(0);

                String content = etContent.getText().toString();

                actVO.setActContent(content);
                actVO.setActPost(999);
                actVO.setACTUID(null);
                actVO.setACTSHOWUNIT(null);
                actVO.setACTMASTERUNIT(null);
                actVO.setACTWEBSALES(null);
                actVO.setACTSOURCEWEBNAME(null);
                actVO.setACTONSALE(null);
                actVO.setACTPRICE(null);
                actVO.setActAdr(locationName);

                if(image==null) {
                    if(file != null) {
                        Bitmap srcPicture = BitmapFactory.decodeFile(file.getPath());
                        picture = downSize(srcPicture, 512);
                        image = Common.bitmapToPNG(picture);

                    }else {
                        Bitmap srcPicture = BitmapFactory.decodeResource(getResources(), R.drawable.p);
                        Bitmap picture2 = downSize(srcPicture, 512);
                        image = Common.bitmapToPNG(picture2);
                    }
                }

                if(isInsertValid(actVO, listVO)){

                    actVO.setActID(actId);

                    Intent intent = new Intent(ActInsert_3Activity.this, ActDetailActivity.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("actVO", actVO);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtras(bundle2);
                    startActivity(intent);
                    finish();

                }else {
                    Toast.makeText(ActInsert_3Activity.this, "新增活動失敗", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    private boolean isInsertValid(ActVO actVO, List<ActPoiVO> listVO){

        if(networkConnected(ActInsert_3Activity.this)){
            String url = Common.URL + "ActServletAndroid";

            try {
                Gson gson = new Gson();

                String jsonIn = new InsertOrUpdateTask(url, "insert", actVO, image).execute().get();

                actId = gson.fromJson(jsonIn, Integer.class);

                List<ActPoiVO> list = new ArrayList<>();
                for(ActPoiVO actPoiVO : listVO){
                    actPoiVO.setActid(actId);
                    list.add(actPoiVO);
                }
                String jsonIn2 = new ActPoiChangeTask(url, "actPoiInsert", list).execute().get();

                Log.d(TAG,jsonIn2);

            } catch (Exception e){
                Log.e(TAG, e.toString());
            }
        }else{
            showToast(this, R.string.msg_NoNetwork);
        }
        return actId != null;
    }

    //****************************************************拍照選單***************************************
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_picture);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tackPicture:
                takePicture();
                return true;
            case R.id.action_photoLib:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUESTCODE_PHOTO_LIB);
                return true;
            default:
                return false;
        }
    }
    //****************************************************拍照選單***************************************

    //檢查裝置有沒有應用程式可以執行拍照動作，如果有則數量會大於0
    public boolean isIntentAvailable(Context context, Intent intent){
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    //書本12-25頁
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //指定存檔路徑
        file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture.jpg");

        Uri contentUri = FileProvider.getUriForFile(
                this, getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        if (isIntentAvailable(this, intent)) {
            startActivityForResult(intent, REQ_TAKE_PICTURE);   //啟動onActivityResult
        } else {
            showToast(this, R.string.msg_NoCameraApp);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQ_TAKE_PICTURE:
                    Bitmap srcPicture = BitmapFactory.decodeFile(file.getPath());
                    picture = downSize(srcPicture, 512);
                    ivActImg.setImageBitmap(picture);
                    break;

                case REQUESTCODE_PHOTO_LIB:
                    Uri uri = data.getData();
                    String[] columns = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, columns,
                            null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String imagePath = cursor.getString(0);
                        cursor.close();
                        Bitmap bitmap2 = BitmapFactory.decodeFile(imagePath);
                        bitmap2 = downSize(bitmap2, 512);

                        ivActImg.setImageBitmap(bitmap2);

                        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                        bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, out2);
                        image = out2.toByteArray();
                    }
                    break;
                default:
                    Toast.makeText(this, "no react", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    //檢查若尚未授權, 則向使用者要求存取權限、拍照權限
    private void checkPermission() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&

                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA
                    }, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG,"************************"+getTaskId()+"我被消滅了***************************");
    }
}
