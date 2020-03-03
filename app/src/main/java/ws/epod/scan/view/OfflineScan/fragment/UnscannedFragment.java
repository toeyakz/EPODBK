package ws.epod.scan.view.OfflineScan.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;

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
import ws.epod.scan.model.OfflineScan.WaybillPoJo;
import ws.epod.scan.view.OfflineScan.activity.ScanViewActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnscannedFragment extends Fragment {

    private FloatingActionButton fabScan;
    private IntentIntegrator qrScan;
    private ConnectionDetector netCon;
    private DatabaseHelper databaseHelper;
    private NarisBaseValue narisv;
    private LocationTrack locationTrack;
    BottomNavigationView nav_bar;


    private RecyclerView rvScan;
    private WaybillUnScannedAdapter adapter;
    private ImageView btnDeleteWaybill, imgBack_scan;

    ArrayList<WaybillModel> dataWaybill;
    private Runnable delayScan = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    public UnscannedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (OfflineScanUtil.getWaybillOffline() != null) {
            for (WaybillPoJo jo : OfflineScanUtil.getWaybillOffline()) {
//                Log.d("f9s2sdf4", "onResume: " + jo.getWaybill_no());
                saveWaybill(jo.getWaybill_no(), jo.getDate(), jo.getLat(), jo.getLon());
            }

            OfflineScanUtil.clearWaybillList();
            OfflineScanUtil.clearWaybillHeader();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_unscanned, container, false);


        initial(view);

        return view;
    }

    private void initial(View view) {

        qrScan = new IntentIntegrator(getActivity());
        narisv = new NarisBaseValue(getContext());
        netCon = new ConnectionDetector(getContext());
        databaseHelper = new DatabaseHelper(getContext());

        locationTrack = new LocationTrack(getContext());

        fabScan = view.findViewById(R.id.fabScan);
        rvScan = view.findViewById(R.id.rvScan);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvScan.setLayoutManager(layoutManager);

        readData();
        onClick(view);

    }

    private void onClick(View view) {

        fabScan.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), ScanViewActivity.class);
            startActivity(intent);
            //startScan();
        });

        //  btnDeleteWaybill.setOnClickListener(v -> dialogDelete());

        // imgBack_scan.setOnClickListener(v -> finish());
    }

    private void dialogDelete() {

        AlertDialog alert = new AlertDialog.Builder(getContext()).create();
        alert.setTitle("ยืนยันการลบ Waybill?");
        alert.setCancelable(false);
        alert.setButton(getString(R.string.confirm), (dialog, which) -> {
            for (int i = 0; i < OfflineScanUtil.getSec().size(); i++) {
                databaseHelper.db().delete("header_waybill", "id=?", new String[]{OfflineScanUtil.getSec().get(i).getId()});
            }
            readData();
        });
        alert.setButton2(getString(R.string.cancel), (dialog, which) -> alert.dismiss());
        alert.show();
    }

    private void readData() {

        ArrayList<WaybillModel> models = new ArrayList<>();

        dataWaybill = new ArrayList<>();
        String sql = "select * from header_waybill where status_complete = '0'";
        Cursor cursor = databaseHelper.selectDB(sql);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String waybill_no = cursor.getString(cursor.getColumnIndex("waybill_no"));
                String date_scan = cursor.getString(cursor.getColumnIndex("date_scan"));
                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                double lon = cursor.getDouble(cursor.getColumnIndex("lon"));
                String is_scanned = cursor.getString(cursor.getColumnIndex("is_scanned"));
                String status_complete = cursor.getString(cursor.getColumnIndex("status_complete"));

                models.add(new WaybillModel(id, waybill_no, date_scan, lat, lon, is_scanned, status_complete));

                Log.d("sdfs92d4", "readData: " + waybill_no);

                dataWaybill.add(new WaybillModel(id, waybill_no, date_scan, lat, lon, is_scanned, status_complete));
            } while (cursor.moveToNext());
        }

        adapter = new WaybillUnScannedAdapter(models, getContext());
        rvScan.setAdapter(adapter);
    }

    private void startScan() {
        qrScan.setPrompt("Scan a barcode or qr code");
        qrScan.setOrientationLocked(false);
        qrScan.setBeepEnabled(false);
        qrScan.initiateScan();

    }

    private void saveWaybill(String data, String date, String lat, String lon) {

        ContentValues cv = new ContentValues();
        cv.put("waybill_no", data);
        cv.put("date_scan", date);
        cv.put("lat", lat);
        cv.put("lon", lon);
        cv.put("is_scanned", "1");
        cv.put("status_complete", "1");
        databaseHelper.db().insert("header_waybill", null, cv);

        readData();


    }

}
