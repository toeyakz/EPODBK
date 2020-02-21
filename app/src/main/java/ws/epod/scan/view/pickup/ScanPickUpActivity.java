package ws.epod.scan.view.pickup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
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

            boolean scannotFind = false;
            boolean isAdd = false;

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

            for (int i = 0; i < UtilScan.getListHeaderWaybill().size(); i++) {
                if (result.getText().equals(UtilScan.getListHeaderWaybill().get(i).getWaybill_no())) {
                    scannotFind = true;
                    isAdd = true;

                    new Handler().postDelayed(delayScan, 2000);
                }
            }

            if (isAdd) {
                lastText = result.getText();
                tvCodeScanned.setText(result.getText());
                beepManager.playBeepSoundAndVibrate();

                Invoice newInvoice = new Invoice(result.getText());
                UtilScan.addInvoice(newInvoice);

                if (UtilScan.getListWaybill().size() > 0) {
                    tvStat.setText("Have" + " " + UtilScan.getListWaybill().size() + " " + "waybill in list.");
                }
            }

            if (!scannotFind) {

                showDialog();
                // Toasty.info(getApplicationContext(), "This Waybill No doesn't exist.", Toast.LENGTH_SHORT, true).show();
                new Handler().postDelayed(delayScan, 2000);
            }

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

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
                finish();
            });
            alert.setButton2(getString(R.string.cancel), (dialog, which) -> alert.dismiss());
            alert.show();

        });
    }


    private void getSQLite() {
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
                String box_total = cursor.getString(cursor.getColumnIndex("box_total"));
                String box_checked = cursor.getString(cursor.getColumnIndex("box_checked"));
                String station_name = cursor.getString(cursor.getColumnIndex("station_name"));
                String station_address = cursor.getString(cursor.getColumnIndex("station_address"));
                String pay_type = cursor.getString(cursor.getColumnIndex("pay_type"));
                String global_total = cursor.getString(cursor.getColumnIndex("global_total"));
                String global_cancel = cursor.getString(cursor.getColumnIndex("global_cancel"));
                String price = cursor.getString(cursor.getColumnIndex("price"));
                int total_b = cursor.getInt(cursor.getColumnIndex("total_b"));

                Log.d("PickingUpLOG", "onCreate: " + "==>" + global_cancel);

                //  list.add(new PickingUp_Model(consignment, box_total, box_checked, global_total, station_address, pay_type, global_cancel, price, total_b));


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
                        String box_no = cursor_expand.getString(cursor_expand.getColumnIndex("box_no"));
                        String waybill_no = cursor_expand.getString(cursor_expand.getColumnIndex("waybill_no"));
                        String is_scaned = cursor_expand.getString(cursor_expand.getColumnIndex("is_scaned"));
                        String row_number = cursor_expand.getString(cursor_expand.getColumnIndex("row_number"));
                        String delivery_no2 = cursor_expand.getString(cursor_expand.getColumnIndex("delivery_no"));
                        String plan_seq2 = cursor_expand.getString(cursor_expand.getColumnIndex("plan_seq"));
                        String comment = cursor_expand.getString(cursor_expand.getColumnIndex("comment"));
                        String picture1 = cursor_expand.getString(cursor_expand.getColumnIndex("picture1"));
                        String picture2 = cursor_expand.getString(cursor_expand.getColumnIndex("picture2"));
                        String picture3 = cursor_expand.getString(cursor_expand.getColumnIndex("picture3"));
                        String time_begin = cursor_expand.getString(cursor_expand.getColumnIndex("time_begin"));
                        String is_save = cursor_expand.getString(cursor_expand.getColumnIndex("is_save"));
                        String status_upload = cursor_expand.getString(cursor_expand.getColumnIndex("status_upload"));
                        String order_no = cursor_expand.getString(cursor_expand.getColumnIndex("order_no"));

                        Log.d("a8s2a9", "getSQLite: " + consignment + ">" + waybill_no + ">" + is_scaned);

                        UtilScan.addInvoiceHeader(new InvoiceHeader(consignment, waybill_no, is_scaned));

                        //  list_expand.add(new PickingUpEexpand_Model(box_no, waybill_no, is_scaned, row_number, consignment, delivery_no2, plan_seq2, comment, picture1, picture2, picture3, time_begin, is_save, status_upload, order_no));
                    } while (cursor_expand.moveToNext());
                }


                //   expandableListDetail.put(consignment, list_expand);
            } while (cursor.moveToNext());
        }

//        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++)
//            expandableListView.expandGroup(i);
        // user_data.edit().clear();
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
