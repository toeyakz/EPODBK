package ws.epod.scan.view.OfflineScan.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.LocationTrack;
import ws.epod.R;
import ws.epod.scan.Util.OfflineScanUtil;
import ws.epod.scan.model.OfflineScan.WaybillHeader;
import ws.epod.scan.model.OfflineScan.WaybillPoJo;

public class ScanViewActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {


    private static final int PERMISSIONS_CAMERA = 2;
    private DecoratedBarcodeView barcodeScannerView;
    private BeepManager beepManager;

    /// Other variable
    private String lastText;

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

    private LocationTrack locationTrack;

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
            new Handler().postDelayed(delayScan, 500);
        });


        alertDialog.show();

    }


    private BarcodeCallback scanCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {

            boolean isAdd = false;


            if (result.getText().equals(lastText) || OfflineScanUtil.containInvoiceNumber(result.getText())) {

                Toasty.info(getApplicationContext(), "มีในลิสอยู่แล้ว.", Toast.LENGTH_SHORT, true).show();

                new Handler().postDelayed(delayScan, 2000);
                return;
            }

            for (WaybillHeader header : OfflineScanUtil.getWaybillHeader()) {

                if (result.getText().equals(header.getWaybill_no())) {
                    isAdd = true;
                    new Handler().postDelayed(delayScan, 2000);
                } else {
                    isAdd = false;
                    new Handler().postDelayed(delayScan, 2000);
                }
            }

            if (isAdd) {
                Toasty.info(getApplicationContext(), "เคยเพิ่ม Waybill นี้ไปแล้ว.", Toast.LENGTH_SHORT, true).show();
            } else {
                lastText = result.getText();
                tvCodeScanned.setText(result.getText());



 //               beepManager.playBeepSoundAndVibrate();

                //set beep
                Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.beep);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
                r.play();

                WaybillPoJo invoice = new WaybillPoJo(result.getText(), getdate(), getlat(), getlon());
                OfflineScanUtil.addWaybill(invoice);

                if (OfflineScanUtil.getWaybillOffline().size() > 0) {
                    tvStat.setText("Have" + " " + OfflineScanUtil.getWaybillOffline().size() + " " + "waybill in list.");
                }
            }


        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_view);

        barcodeScannerView = findViewById(R.id.barcode_scanner);
        barcodeScannerView.setTorchListener(this);
        barcodeScannerView.decodeSingle(scanCallback);

        narisv = new NarisBaseValue(ScanViewActivity.this);
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        locationTrack = new LocationTrack(ScanViewActivity.this);

        beepManager = new BeepManager(this);

        tvCodeScanned = findViewById(R.id.tvCodeScanned);
        tvStat = findViewById(R.id.tv_status);
        btn_next = findViewById(R.id.btn_next);
        imgBack_test = findViewById(R.id.imgBack_test);
        btn_cancel = findViewById(R.id.btn_cancel);


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

        onClick();

    }

    private void onClick() {
        btn_next.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), PinkingUpMaster_Activity.class);
//            startActivity(intent);
            //           UtilScan.clearWaybillList();
            finish();
        });

        imgBack_test.setOnClickListener(v -> {
            AlertDialog alert = new AlertDialog.Builder(ScanViewActivity.this).create();
            alert.setTitle("ต้องการยกเลิกการสแกน?");
            alert.setCancelable(false);
            alert.setButton(getString(R.string.confirm), (dialog, which) -> {
//                UtilScan.clearHeaderWaybillList();
//                UtilScan.clearWaybillList();
                OfflineScanUtil.clearWaybillHeader();
                OfflineScanUtil.clearWaybillList();
                finish();
            });
            alert.setButton2(getString(R.string.cancel), (dialog, which) -> alert.dismiss());
            alert.show();
        });

        btn_cancel.setOnClickListener(v -> {
            AlertDialog alert = new AlertDialog.Builder(ScanViewActivity.this).create();
            alert.setTitle("ต้องการยกเลิกการสแกน?");
            alert.setCancelable(false);
            alert.setButton(getString(R.string.confirm), (dialog, which) -> {

                OfflineScanUtil.clearWaybillHeader();
                OfflineScanUtil.clearWaybillList();
//                UtilScan.clearHeaderWaybillList();
//                UtilScan.clearWaybillList();
                finish();
            });
            alert.setButton2(getString(R.string.cancel), (dialog, which) -> alert.dismiss());
            alert.show();

        });
    }

    private void getSQLite() {

        String sql = "select * from header_waybill";
        Cursor cursor = databaseHelper.selectDB(sql);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {

                String waybill_no = cursor.getString(cursor.getColumnIndex("waybill_no"));

                Log.d("a2f8a9", "getSQLite: " + waybill_no);

                OfflineScanUtil.addWaybillHeader(new WaybillHeader(waybill_no));


            } while (cursor.moveToNext());
        }
    }

    private String getdate() {

        String temp = "";
        String pattern = "yyyy-MM-dd kk:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
        temp = sdf.format(Calendar.getInstance().getTime());

        return temp;
    }

    private String getlat() {

        String lat = "";
        if (locationTrack.canGetLocation()) {

            double latitude = locationTrack.getLatitude();
            lat = String.valueOf(latitude);

        } else {

            locationTrack.showSettingsAlert();
        }

        return lat;
    }

    private String getlon() {

        String lon = "";
        if (locationTrack.canGetLocation()) {

            double longitude = locationTrack.getLongitude();
            lon = String.valueOf(longitude);
        } else {

            locationTrack.showSettingsAlert();
        }

        return lon;
    }

    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }

    @Override
    public void onResume() {
        super.onResume();
        getSQLite();
        barcodeScannerView.resume();
    }


    @Override
    public void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }
}
