package iii.com.chumeet.home;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import iii.com.chumeet.R;
import iii.com.chumeet.login.MainActivity;

public class HomeActivity extends AppCompatActivity{
    private static final String TAG = "HomeActivity";
    private Toolbar toolbar;



    //底部導覽列(程式碼是預設的)
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener(){

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item){

//Fragment創造與交換
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            switch (item.getItemId()){
                case R.id.navFind:
                    transaction.replace(R.id.content, new FindFragment()).commit();
                    return true;
                case R.id.navGoing:
                    transaction.replace(R.id.content, new GoingFragment()).commit();
                    return true;
                case R.id.navProfile:
                    transaction.replace(R.id.content, new ProfileFragment()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//底部導覽列
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//Fragment第一次的創造與交換
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new FindFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    //監聽返回鍵點擊事件，並創建一個退出對話框
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setMessage("Do you want to close it?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which){

//沒這樣寫，應用程式都關不掉
                            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("Exit me", true);
                            startActivity(intent);
                            finish();
//   以下要寫在MainActivity
//   Then nCreate() method add this to finish the MainActivity
//   if( getIntent().getBooleanExtra("Exit me", false)){
//     finish();
//   }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG,"************************"+getTaskId()+"我被消滅了***************************");
    }
}
