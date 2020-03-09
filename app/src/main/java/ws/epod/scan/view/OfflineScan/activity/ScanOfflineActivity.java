package ws.epod.scan.view.OfflineScan.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ws.epod.scan.view.OfflineScan.adapter.WaybillUnScannedAdapter;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.LocationTrack;
import ws.epod.ObjectClass.SQLiteModel.WaybillModel;
import ws.epod.R;
import ws.epod.scan.Util.OfflineScanUtil;
import ws.epod.scan.view.OfflineScan.fragment.ScannedFragment;
import ws.epod.scan.view.OfflineScan.fragment.UnscannedFragment;

public class ScanOfflineActivity extends AppCompatActivity {

    private final static String TAG_FRAGMENT = "TAG_FRAGMENT";
    private FloatingActionButton fabScan;
    private IntentIntegrator qrScan;
    private ConnectionDetector netCon;
    private DatabaseHelper databaseHelper;
    private NarisBaseValue narisv;
    private LocationTrack locationTrack;
    BottomNavigationView nav_bar;

    private ImageView imgBack_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_offline);

        initial();

    }


    private void initial() {

        qrScan = new IntentIntegrator(this);
        narisv = new NarisBaseValue(ScanOfflineActivity.this);
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        locationTrack = new LocationTrack(ScanOfflineActivity.this);


        // findviewby

        //  imgBack_scan = findViewById(R.id.imgBack_scan);
        nav_bar = findViewById(R.id.nav_bar);

        nav_switch();
        addFragment(new UnscannedFragment(), false, "one");

//        imgBack_scan.setOnClickListener(v -> finish());


    }

    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {
        if(!addToBackStack){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_container, fragment)
                    .commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frame_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }

//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_container, fragment,TAG_FRAGMENT);
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    @Override
    public void onBackPressed(){
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1){
            finish();
        }
        else {
            super.onBackPressed();
        }
    }


//    @Override
//    public void onBackPressed() {
//
//        boolean isFragmentPopped = handleNestedFragmentBackStack(getSupportFragmentManager());
//        if (!isFragmentPopped) {
//            super.onBackPressed();
//        }
////        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
////           finish();
////        }else{
////            super.onBackPressed();
////        }
//    }

    private boolean handleNestedFragmentBackStack(androidx.fragment.app.FragmentManager fragmentManager) {

        List list = fragmentManager.getFragments();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Fragment fragment = (Fragment) list.get(i);
                boolean isPopped = handleNestedFragmentBackStack(fragment.getChildFragmentManager());
                if (isPopped) {
                    if (fragmentManager.getBackStackEntryCount() > 0) {
                        fragmentManager.popBackStack();
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }


    private void nav_switch() {

        nav_bar.setOnNavigationItemSelectedListener(menuItem -> {
            Fragment fragment;
            String backStateName = "";
            switch (menuItem.getItemId()) {
                case R.id.item_UnScanned:
                    fragment = new UnscannedFragment();

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, fragment)
                            .commit();

                    return true;
                case R.id.item_Scanned:
                    fragment = new ScannedFragment();
                    //addFragment(fragment, true, "two");

                    backStateName = fragment.getClass().getName();

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, fragment)
                            .commit();

                    return true;
            }
            return false;
        });
    }


}
