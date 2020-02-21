package ws.epod.scan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ws.epod.Adapter.WaybillAdapter;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.LocationTrack;
import ws.epod.ObjectClass.SQLiteModel.WaybillModel;
import ws.epod.R;

public class ScanOfflineActivity extends AppCompatActivity {

//    private FloatingActionButton fabScan;
//    private IntentIntegrator qrScan;
//    private ConnectionDetector netCon;
//    private DatabaseHelper databaseHelper;
//    private NarisBaseValue narisv;
//    private LocationTrack locationTrack;
//
//
//    private RecyclerView rvScan;
//    private WaybillAdapter adapter;
//    private ImageView btnDeleteWaybill, imgBack_scan;
//
//    ArrayList<WaybillModel> dataWaybill;
//    private Runnable delayScan = new Runnable() {
//        @Override
//        public void run() {
//            startScan();
//        }
//    };
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scan_offline);
//
//        initial();
//
//    }
//
//    private void initial() {
//
//        qrScan = new IntentIntegrator(this);
//
//        narisv = new NarisBaseValue(ScanOfflineActivity.this);
//        netCon = new ConnectionDetector(getApplicationContext());
//        databaseHelper = new DatabaseHelper(getApplicationContext());
//
//        locationTrack = new LocationTrack(ScanOfflineActivity.this);
//
//        // findviewby
//        fabScan = findViewById(R.id.fabScan);
//        rvScan = findViewById(R.id.rvScan);
//        btnDeleteWaybill = findViewById(R.id.btnDeleteWaybill);
//        imgBack_scan = findViewById(R.id.imgBack_scan);
//
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        rvScan.setLayoutManager(layoutManager);
//
//
//        onClick();
//        readData();
//
//
//
//    }
//
//    private void onClick() {
//
//        fabScan.setOnClickListener(v -> startScan());
//
//        btnDeleteWaybill.setOnClickListener(v -> dialogDelete());
//
//        imgBack_scan.setOnClickListener(v -> finish());
//    }
//
//    private void dialogDelete() {
//
//        AlertDialog alert = new AlertDialog.Builder(ScanOfflineActivity.this).create();
//        alert.setTitle("ยืนยันการลบ Waybill?");
//        alert.setCancelable(false);
//        alert.setButton(getString(R.string.confirm), (dialog, which) -> {
//            for (int i = 0; i < Util.listWaybill.size(); i++) {
//                databaseHelper.db().delete("header_waybill", "id=?", new String[]{Util.listWaybill.get(i).getId()});
//            }
//            readData();
//        });
//        alert.setButton2(getString(R.string.cancel), (dialog, which) -> alert.dismiss());
//        alert.show();
//    }
//
//    private void readData() {
//
//        ArrayList<WaybillModel> models = new ArrayList<>();
//
//        dataWaybill = new ArrayList<>();
//        String sql = "select * from header_waybill";
//        Cursor cursor = databaseHelper.selectDB(sql);
//
//        cursor.moveToFirst();
//        if (cursor.getCount() > 0) {
//            do {
//                String id = cursor.getString(cursor.getColumnIndex("id"));
//                String waybill_no = cursor.getString(cursor.getColumnIndex("waybill_no"));
//                String date_scan = cursor.getString(cursor.getColumnIndex("date_scan"));
//                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
//                double lon = cursor.getDouble(cursor.getColumnIndex("lon"));
//                String is_scanned = cursor.getString(cursor.getColumnIndex("is_scanned"));
//                String status_complete = cursor.getString(cursor.getColumnIndex("status_complete"));
//
//                Log.d("s652a9s", "readData: " + waybill_no);
//
//                models.add(new WaybillModel(id, waybill_no, date_scan, lat, lon, is_scanned, status_complete));
//
//                dataWaybill.add(new WaybillModel(id, waybill_no, date_scan, lat, lon, is_scanned, status_complete));
//            } while (cursor.moveToNext());
//        }
//
//        adapter = new WaybillAdapter(models, getApplicationContext());
//        rvScan.setAdapter(adapter);
//    }
//
//    private void startScan() {
//        qrScan.setPrompt("Scan a barcode or qr code");
//        qrScan.setOrientationLocked(false);
//        qrScan.setBeepEnabled(false);
//        qrScan.initiateScan();
//
//    }
//
//    private void saveWaybill(String data) {
//
//        /*for (int i = 0; i < dataWaybill.size(); i++) {
//
//            if(!data.equals(dataWaybill.get(i).getWaybill_no())){
//                ContentValues cv = new ContentValues();
//                cv.put("waybill_no", data);
//                cv.put("date_scan", getdate());
//                cv.put("lat", getlat());
//                cv.put("lon", getlon());
//                cv.put("is_scanned", "1");
//                cv.put("status_complete", "0");
//                databaseHelper.db().insert("header_waybill", null, cv);
//
//                readData();
//                new Handler().postDelayed(delayScan, 1000);
//            }else{
//                Toasty.info(getApplicationContext(),"This waybill has been added to the list.",Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }*/
//
//
//
//
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            if (result.getContents() == null) {
//                Toast.makeText(this, "Result not found", Toast.LENGTH_SHORT).show();
//            } else {
//                String getScanText = result.getContents();
//                getScanText = getScanText.trim();
//
//                saveWaybill(getScanText);
//
//                // Toast.makeText(this, getScanText, Toast.LENGTH_SHORT).show();
//
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//
//    private String getdate() {
//
//        String temp = "";
//        String pattern = "yyyy-MM-dd kk:mm:ss";
//        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
//        temp = sdf.format(Calendar.getInstance().getTime());
//
//        return temp;
//    }
//
//    private String getlat() {
//
//        String lat = "";
//        if (locationTrack.canGetLocation()) {
//
//            double latitude = locationTrack.getLatitude();
//            lat = String.valueOf(latitude);
//
//        } else {
//
//            locationTrack.showSettingsAlert();
//        }
//
//        return lat;
//    }
//
//    private String getlon() {
//
//        String lon = "";
//        if (locationTrack.canGetLocation()) {
//
//            double longitude = locationTrack.getLongitude();
//            lon = String.valueOf(longitude);
//        } else {
//
//            locationTrack.showSettingsAlert();
//        }
//
//        return lon;
//    }


}
