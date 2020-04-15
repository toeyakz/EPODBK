package ws.epod.scan.view.pickup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.SQLiteModel.PickingUpEexpand_Model;
import ws.epod.ObjectClass.SQLiteModel.PickingUp_Model;
import ws.epod.R;
import ws.epod.scan.Util.UtilScan;
import ws.epod.scan.model.pickup.Invoice;
import ws.epod.scan.model.pickup.InvoiceHeader;

public class ScanPickUpActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener, UtilScan.OnInvoiceListener {

    private static final int PERMISSIONS_CAMERA = 2;
    private DecoratedBarcodeView barcodeScannerView;
    private BeepManager beepManager;

    /// Other variable
    private String lastText;

    protected PowerManager.WakeLock mWakeLock;

    /// View
    private TextView tvStat;
    private TextView tvCodeScanned;
    private Button btn_next, btn_cancel;
    private ImageView imgBack_test;

    ConnectionDetector netCon;
    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;

    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;
    Pickup_Activity.PickingUpAdapter expandableListAdapter;
    private Runnable delayScan = new Runnable() {
        @Override
        public void run() {
            barcodeScannerView.decodeSingle(scanCallback);
        }
    };

    @Override
    public void onBackPressed() {

    }


    private void showDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_warning__scan, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        Button btnCloseDialog = dialogView.findViewById(R.id.btnCloseDialog);

        alertDialog = dialogBuilder.create();
        btnCloseDialog.setOnClickListener(v -> {
            alertDialog.dismiss();
            new Handler().postDelayed(delayScan, 1000);
        });


        alertDialog.show();

    }

    public void getAdapterInScan(Pickup_Activity.PickingUpAdapter adapter) {

        if (expandableListAdapter == null) {
            expandableListAdapter = adapter;
        }

        Log.d("asopdkas", "1");
//        for (int n = 0; n < expandableListAdapter.getGroupCount(); n++) {
//            final PickingUp_Model listTitle = (PickingUp_Model) expandableListAdapter.getGroup(n);
//            ArrayList<String> count_ = new ArrayList<>();
//            for (int j = 0; j < expandableListAdapter.getChildrenCount(n); j++) {
//                final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(n, j);
//                Log.d("sas74das2asf895asf", "waybill: " + expandedList.getWaybil_no() + ": " + expandedList.getIs_scaned());
//              //  Log.d("sas74das2", "result: " + result);
//
//
//            }
//        }


    }

    private void scan(BarcodeResult result) {
        for (int n = 0; n < expandableListAdapter.getGroupCount(); n++) {
            final PickingUp_Model listTitle = (PickingUp_Model) expandableListAdapter.getGroup(n);
            ArrayList<String> count_ = new ArrayList<>();
            for (int j = 0; j < expandableListAdapter.getChildrenCount(n); j++) {
                final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(n, j);
                Log.d("sas74das2", "waybill: " + expandedList.getWaybil_no() + ": " + expandedList.getIs_scaned());
                Log.d("sas74das2", "result: " + result);


            }
        }
    }


    private BarcodeCallback scanCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {

            //   scan(result);


            try {

                boolean scannotFind = false;
                boolean isAdd = false;
                boolean un = false;
                boolean ex = false;
                Log.d("asopdkas", "2");
                SharedPreferences prefs = getSharedPreferences("status_scan", Context.MODE_PRIVATE);


                Intent intent = getIntent();
                String INPUT_WAY = intent.getStringExtra("key");

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Gson gson = new Gson();
                String json = sharedPrefs.getString("ccsac", "");
                Type type = new TypeToken<ArrayList<PickingUpEexpand_Model>>() {
                }.getType();

                ArrayList<PickingUpEexpand_Model> arrayList = gson.fromJson(json, type);


//                for (int n = 0; n < expandableListAdapter.getGroupCount(); n++) {
//                    // expandableListView.expandGroup(i);
//                    final PickingUp_Model listTitle = (PickingUp_Model) expandableListAdapter.getGroup(n);
//                    ArrayList<String> count_ = new ArrayList<>();
//                    for (int j = 0; j < expandableListAdapter.getChildrenCount(n); j++) {
//                        final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(n, j);
//                        Log.d("sas74das2", "waybill: "+expandedList.getWaybil_no() +": " + expandedList.getIs_scaned());
//                    }
//                }


                if (alertDialog != null) {
                    if (alertDialog.isShowing()) {
                        return;
                    }
                }

                if (result.getText().equals(lastText) || UtilScan.containInvoiceNumber(result.getText())) {
                    Toasty.info(getApplicationContext(), "มีในลิสอยู่แล้ว.", Toast.LENGTH_SHORT, true).show();
                    new Handler().postDelayed(delayScan, 2000);
                    return;
                }


                HashMap<String, String> mMap = new HashMap<>();
                String status_scan = "";
                for (int p = 0; p < arrayList.size(); p++) {
                    Log.d("As9df6asd", arrayList.get(p).getWaybil_no() + ": " + arrayList.get(p).getIs_scaned());

                    //  for (int i = 0; i < UtilScan.getListHeaderWaybill().size(); i++) {


                    //  String is_scanned = UtilScan.getListHeaderWaybill().get(i).getIs_scaned();
                    String log_IsScanned = prefs.getString("Is_scaned", "");


                    if (result.getText().equals(arrayList.get(p).getWaybil_no())) {
                        Log.d("s82s", "barcodeResult: 0");
                        //  Log.d("s82s", "is_scanned: " + is_scanned);
                        Log.d("s82s", "log_IsScanned: " + log_IsScanned);


                        // if (!arrayList.get(p).getIs_scaned().equals("")) {
                        if (arrayList.get(p).getIs_scaned().equals("1") || arrayList.get(p).getIs_scaned().equals("2")) {

                            switch (INPUT_WAY) {
                                case "UNCHECK":
                                    isAdd = true;
                                    status_scan = arrayList.get(p).getIs_scaned();
                                    Log.d("s82s", "barcodeResult: 1");
                                    //  un = false;
                                    break;
                                case "COMMENT":
                                    isAdd = !arrayList.get(p).getIs_scaned().equals("2");
                                    status_scan = arrayList.get(p).getIs_scaned();
                                    break;
                                case "CHECK":
                                    Log.d("s82s", "barcodeResult: CHECK - 1 หรือ 2");
                                    status_scan = arrayList.get(p).getIs_scaned();
                                    isAdd = !arrayList.get(p).getIs_scaned().equals("1");

                                    // UtilScan.addMap("is_scanned", "1");
                                    break;
                            }

                            scannotFind = true;
                            new Handler().postDelayed(delayScan, 2000);
                            break;

                        } else if (arrayList.get(p).getIs_scaned().equals("0")) {
                            switch (INPUT_WAY) {
                                case "UNCHECK":
                                    Log.d("s82s", "barcodeResult: 2");
                                    isAdd = false;
                                    status_scan = arrayList.get(p).getIs_scaned();
                                    un = true;
                                    break;
                                case "CHECK":
                                case "COMMENT":
                                    Log.d("s82s", "barcodeResult: CHECK 0");
                                    //  UtilScan.addMap("waybill", result.getText());


                                    isAdd = true;
                                    status_scan = arrayList.get(p).getIs_scaned();
                                    break;
                            }
                            scannotFind = true;
                            // i = UtilScan.getListHeaderWaybill().size();
                            break;
                        }
//                            } else {
//                                if (is_scanned.equals("1") || is_scanned.equals("2")) {
//
//                                    switch (INPUT_WAY) {
//                                        case "UNCHECK":
//                                            isAdd = true;
//                                            Log.d("s82s", "barcodeResult: 1");
//                                            //  un = false;
//                                            break;
//                                        case "COMMENT":
//                                            isAdd = !is_scanned.equals("2");
//                                            break;
//                                        case "CHECK":
//                                            Log.d("s82s", "barcodeResult: CHECK - 1 หรือ 2");
//                                            isAdd = !is_scanned.equals("1");
//                                            break;
//                                    }
//
//                                    scannotFind = true;
//                                    new Handler().postDelayed(delayScan, 2000);
//                                    break;
//
//                                } else if (is_scanned.equals("0")) {
//                                    switch (INPUT_WAY) {
//                                        case "UNCHECK":
//                                            Log.d("s82s", "barcodeResult: 2");
//                                            isAdd = false;
//                                            un = true;
//                                            break;
//                                        case "CHECK":
//                                        case "COMMENT":
//                                            Log.d("s82s", "barcodeResult: CHECK 0");
//                                            isAdd = true;
//                                            break;
//                                    }
//                                    scannotFind = true;
//                                    // i = UtilScan.getListHeaderWaybill().size();
//                                    break;
//                                }
//                            }


                    } else {
                        if (arrayList.size() == (p + 1)) {
                            ex = true;
                            scannotFind = false;
                        }
                    }


                    //   }

                }
                if (isAdd) {
                    lastText = result.getText();
                    tvCodeScanned.setText(result.getText());
                    //  beepManager.playBeepSoundAndVibrate();

                    //set beep
                    Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.beep);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
                    r.play();

                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(500);
                    }

                    Invoice newInvoice = new Invoice(result.getText());
                    UtilScan.addInvoice(newInvoice);


                    if (INPUT_WAY.equals("CHECK")) {
                        mMap.put("waybill", result.getText());
                        mMap.put("is_scanned", status_scan);
                    } else if (INPUT_WAY.equals("UNCHECK")) {
                        mMap.put("waybill", result.getText());
                        mMap.put("is_scanned", status_scan);
                    } else {
                        mMap.put("waybill", result.getText());
                        mMap.put("is_scanned", status_scan);
                    }

                    UtilScan.addArMapPickup(mMap);


//                    for (String key : UtilScan.getMeMap().keySet()) {
//                        String value = UtilScan.getMeMap().get(key);
//
//                        Log.d("Asd6asd", "Key: " + key + " Value: " + value );
//                        Log.d("Asd6asd", "size: " + UtilScan.getMeMap().size() );
//                        //  Toast.makeText(getApplicationContext(), "Key: " + key + " Value: " + value, Toast.LENGTH_LONG).show();
//                    }
                    //  Log.d("AS6as3d", ""+UtilScan.getMeMap().size());


                    if (UtilScan.meMapArrayPickup.size() > 0) {
                        tvStat.setText("Have" + " " + UtilScan.meMapArrayPickup.size() + " " + "waybill in list.");
                    }

                    getSQLite();
                    new Handler().postDelayed(delayScan, 2000);

                    return;

                } else {
                    if (!ex) {
                        if (un) {
                            Toasty.info(getApplicationContext(), "Un Check.", Toast.LENGTH_SHORT, true).show();
                            new Handler().postDelayed(delayScan, 2000);
                        } else {
                            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.scan_dupp);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
                            r.play();
                            Toasty.info(getApplicationContext(), "scanned.", Toast.LENGTH_SHORT, true).show();
                            new Handler().postDelayed(delayScan, 2000);

                        }
                    }

                }

                if (!scannotFind) {
                    Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.scan_error);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
                    r.play();
                    showDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        barcodeScannerView = findViewById(R.id.barcode_scanner);
        barcodeScannerView.setTorchListener(this);
        barcodeScannerView.decodeSingle(scanCallback);

        narisv = new NarisBaseValue(ScanPickUpActivity.this);
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        beepManager = new BeepManager(this);

        tvCodeScanned = findViewById(R.id.tvCodeScanned);
        tvStat = findViewById(R.id.tv_status);
        btn_next = findViewById(R.id.btn_next);
        imgBack_test = findViewById(R.id.imgBack_test);
        btn_cancel = findViewById(R.id.btn_cancel);

        // Always on display
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        getSQLite();
//        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//        this.mWakeLock.acquire();

        // MARK: This case, Check Permission.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Show an explanation.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // Request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_CAMERA);
            }
        }

//            for (Invoice invoice : UtilScan.getListWaybill()) {
//                Log.d("waybillList", "barcodeResult: " + invoice.getWaybill_no());
//                Log.d("waybillList", "barcodeResult: " + UtilScan.getListWaybill().size());
//            }

        UtilScan.setInvoiceListener(this);


        onClick();


    }

    private void onClick() {
        btn_next.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), PinkingUpMaster_Activity.class);
//            startActivity(intent);
            UtilScan.clearWaybillList();
            finish();
        });

        imgBack_test.setOnClickListener(v -> {
            AlertDialog alert = new AlertDialog.Builder(ScanPickUpActivity.this).create();
            alert.setTitle("ต้องการยกเลิกการสแกน?");
            alert.setCancelable(false);
            alert.setButton(getString(R.string.confirm), (dialog, which) -> {
                UtilScan.clearHeaderWaybillList();
                UtilScan.clearWaybillList();
                UtilScan.clearPickArray();
                UtilScan.meMap = new HashMap<>();

                UtilScan.meMapArrayPickup = new ArrayList<>();
                SharedPreferences preferences = getSharedPreferences("ccsac", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().apply();
                finish();
            });
            alert.setButton2(getString(R.string.cancel), (dialog, which) -> alert.dismiss());
            alert.show();
        });

        btn_cancel.setOnClickListener(v -> {
            AlertDialog alert = new AlertDialog.Builder(ScanPickUpActivity.this).create();
            alert.setTitle("ต้องการยกเลิกการสแกน?");
            alert.setCancelable(false);
            alert.setButton(getString(R.string.confirm), (dialog, which) -> {
                UtilScan.clearHeaderWaybillList();
                UtilScan.clearWaybillList();
                UtilScan.clearPickArray();
                UtilScan.meMap = new HashMap<>();

                UtilScan.meMapArrayPickup = new ArrayList<>();
                SharedPreferences preferences = getSharedPreferences("ccsac", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().apply();
                finish();
                finish();
            });
            alert.setButton2(getString(R.string.cancel), (dialog, which) -> alert.dismiss());
            alert.show();

        });
    }


    private void getSQLite() {

        try {
            UtilScan.clearInvoiceHeader();

            final SharedPreferences user_data = getSharedPreferences("DATA_DETAIL_PICK", Context.MODE_PRIVATE);
            final String delivery_no = user_data.getString("delivery_no", "");
            final String plan_seq = user_data.getString("plan_seq", "");

            String sql = "select (select DISTINCT pl2.consignment_no from Plan pl2 where pl2.activity_type = pl.activity_type and pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.consignment_no = pl.consignment_no and pl2.trash = pl.trash) as consignment\n" +
                    ",(select count(pl2.box_no) from Plan pl2 where pl2.activity_type = pl.activity_type and pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.consignment_no = pl.consignment_no and pl2.trash = pl.trash) as box_total\n" +
                    ",(select count(pl2.box_no) from Plan pl2 where pl2.activity_type = pl.activity_type and pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.is_scaned <> '0' and pl2.consignment_no = pl.consignment_no and pl2.trash = pl.trash) as box_checked\n" +
                    ",(select pl2.station_name from Plan pl2 where pl2.activity_type = pl.activity_type and pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.consignment_no = pl.consignment_no and pl2.trash = pl.trash) as station_name\n" +
                    ",(select pl2.station_address from Plan pl2 where pl2.activity_type = pl.activity_type and pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.consignment_no = pl.consignment_no and pl2.trash = pl.trash) as station_address\n" +
                    ",(select cm.settlement_method from Plan pl2 where pl2.activity_type = pl.activity_type and pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq  and pl2.consignment_no = pl.consignment_no and pl2.trash = pl.trash) as pay_type\n" +
                    ",(select cm.deli_note_amount_price from consignment cm where cm.consignment_no = pl.consignment_no) as price\n" +
                    ",(select count(DISTINCT cm.global_no) from consignment cm where cm.consignment_no = pl.consignment_no) as global_total\n" +
                    ",(select count(DISTINCT cm.global_no) from consignment cm where cm.consignment_no = pl.consignment_no and cm.detail_remarks <> null) as global_cancel\n" +
                    ",(select pl2.total_box from Plan pl2 where pl2.activity_type = pl.activity_type and pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.consignment_no = pl.consignment_no and pl2.trash = pl.trash LIMIT 1) as total_b " +
                    "from Plan pl\n" +
                    "inner join consignment cm on cm.consignment_no = pl.consignment_no\n" +
                    "where pl.delivery_no = '" + delivery_no + "' and  pl.plan_seq = '" + plan_seq + "' and pl.activity_type = 'LOAD' and pl.trash = '0'" +
                    "GROUP BY pl.delivery_no, pl.consignment_no";
            Cursor cursor = databaseHelper.selectDB(sql);
            Log.d("PickingUpLOG", "total line " + cursor.getCount());

            // expandableListDetail = new HashMap<>();
            // list = new ArrayList<>();

            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    String consignment = cursor.getString(cursor.getColumnIndex("consignment"));
                    String sql_expand = "select pl.delivery_no \n" +
                            ", pl.plan_seq \n" +
                            ", pl.box_no \n" +
                            ", pl.waybill_no \n" +
                            ", pl.is_scaned \n" +
                            ", pl.comment \n" +
                            ", pl.picture1 \n" +
                            ", pl.picture2 \n" +
                            ", pl.picture3 \n" +
                            ", pl.time_begin \n" +
                            ", pl.is_save \n" +
                            ", pl.status_upload \n" +
                            ", ROW_NUMBER() OVER(ORDER BY pl.box_no) as row_number \n" +
                            ", ifnull((select pl2.order_no from Plan pl2 where pl2.consignment_no = pl.consignment_no and pl2. delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq  \n" +
                            " and pl2.order_no = pl.order_no and pl2.order_no in (select ps.order_no from pic_sign ps where pic_sign_load <> '' )),'') as order_no  \n" +
                            "  from Plan pl where pl.consignment_no = '" + consignment + "' and pl.activity_type = 'LOAD' and pl.delivery_no = '" + delivery_no + "' and pl.plan_seq = '" + plan_seq + "' and pl.trash = '0'  " +
                            "  order by row_number";
                    Cursor cursor_expand = databaseHelper.selectDB(sql_expand);
                    Log.d("PickingUpLOG", "total line " + cursor_expand.getColumnCount());

                    // list_expand = new ArrayList<>();

                    cursor_expand.moveToFirst();
                    if (cursor_expand.getCount() > 0) {
                        do {

                            String waybill_no = cursor_expand.getString(cursor_expand.getColumnIndex("waybill_no"));
                            String is_scaned = cursor_expand.getString(cursor_expand.getColumnIndex("is_scaned"));

                            Log.d("a8s2a9", "getSQLite: " + consignment + ">" + waybill_no + ">" + is_scaned);

                            UtilScan.addInvoiceHeader(new InvoiceHeader(consignment, waybill_no, is_scaned));

                        } while (cursor_expand.moveToNext());
                    }

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        barcodeScannerView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        barcodeScannerView.pause();
    }

    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }

    @Override
    public void onInvoiceSet(ArrayList<Invoice> lists) {
        if (lists != null) {

        }

    }
}
