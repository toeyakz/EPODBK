package ws.epod.scan.view.OfflineScan.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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

        imgBack_scan = findViewById(R.id.imgBack_scan);
        nav_bar = findViewById(R.id.nav_bar);

        nav_switch();
        loadFragment(new UnscannedFragment());

        imgBack_scan.setOnClickListener(v -> finish());
    }
    private void loadFragment(Fragment fragment){
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void nav_switch() {

        nav_bar.setOnNavigationItemSelectedListener(menuItem -> {
            Fragment fragment;
            switch (menuItem.getItemId()) {
                case R.id.item_UnScanned:
                    fragment = new UnscannedFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.item_Scanned:
                    fragment = new ScannedFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        });
    }



}
