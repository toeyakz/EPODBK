package ws.epod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import ws.epod.Adapter.DialogConsAdapter;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.SQLiteModel.Dialog_Cons_Detail_Model;
import ws.epod.ObjectClass.SQLiteModel.PickingUpEexpand_Model;
import ws.epod.ObjectClass.SQLiteModel.PickingUp_Model;

public class PinkingUpMaster_Activity extends AppCompatActivity {

    ExpandableListView expandableListView;
    PickingUpAdapter expandableListAdapter;

    ZXingScannerView scannerView;

    ConnectionDetector netCon;
    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;
    private static final int IMAGE_01 = 1888;
    private static final int IMAGE_02 = 1889;
    private static final int IMAGE_03 = 1890;

    String currentPhotoPath;

    ArrayList<PickingUp_Model> list = new ArrayList<>();
    ArrayList<PickingUpEexpand_Model> list_expand = new ArrayList<>();
    HashMap<String, ArrayList<PickingUpEexpand_Model>> expandableListDetail;

    ImageView imgClose_dialog, imgCommentPick_01, imgCommentPick_02, imgCommentPick_03, imgBack_test,
            imgNewPick01, imgDeletePick01, imgNewPick02, imgDeletePick02, imgNewPick03, imgDeletePick03, imageView8, imgCameraScan, imgDetailConsignNo;
    EditText edtComment_PICK, edtFineWaybillPick;
    Button btnSaveComent_PICK;
    CheckBox checkBox;

    AlertDialog alertDialog;
    AlertDialog alertDialog2;

    TextView bnCloseJobPick, btnEnterWaybillNo, savePickingUp;

    String INPUT_WAY = "PLUS";
    String SWICH_EXPAND = "OFF";
    int lastPosition = 0;
    String lastData = "";

    String getDate = "";

    String picture1 = "";
    String picture2 = "";
    String picture3 = "";

    String[] arrayNameImage = new String[3];


    ArrayList<String> picTemp1 = new ArrayList<>();
    ArrayList<String> picTemp2 = new ArrayList<>();
    ArrayList<String> picTemp3 = new ArrayList<>();
    ArrayList<String> deleteImage = new ArrayList<>();

    RecyclerView rvDialogCons;
    DialogConsAdapter dialogConsAdapter;

    private IntentIntegrator qrScan;


    @Override
    protected void onResume() {
        super.onResume();
        // getSQLite();

//        scannerView.setResultHandler(new ZXingScannerResultHandler());
        // Start camera on resume
//        scannerView.startCamera();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // checkBackCon();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_up_master);


        narisv = new NarisBaseValue(PinkingUpMaster_Activity.this);
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        arrayNameImage[0] = "";
        arrayNameImage[1] = "";
        arrayNameImage[2] = "";

        qrScan = new IntentIntegrator(this);

        String pattern = "yyyy-MM-dd kk:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
        getDate = sdf.format(Calendar.getInstance().getTime());

        imageView8 = findViewById(R.id.imageView8);
        savePickingUp = findViewById(R.id.savePickingUp);
        imgBack_test = findViewById(R.id.imgBack_test);
        edtFineWaybillPick = findViewById(R.id.edtFineWaybillPick);
        imgCameraScan = findViewById(R.id.imgCameraScan);
        btnEnterWaybillNo = findViewById(R.id.btnEnterWaybillNo);
        bnCloseJobPick = findViewById(R.id.bnCloseJobPick);

//        if ( getIntent().getStringExtra("publicKey") != null ) {
//
//            String resultCode = getIntent().getStringExtra("publicKey");
//
//            Log.d("asdfqawergok", "handleResult: " + resultCode);
//
//            edtFineWaybillPick.setText(resultCode);
//
//        }

        getSQLite();

        bnCloseJobPick.setOnClickListener(view -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
            bnCloseJobPick.startAnimation(animation);
            Intent intent = new Intent(getApplicationContext(), Invoid_Activity.class);
            startActivity(intent);
        });

        imgCameraScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                imgCameraScan.startAnimation(animation);


                qrScan.initiateScan();
//                scannerView = new ZXingScannerView(getApplicationContext());
//                scannerView.setResultHandler(new ZXingScannerResultHandler());
//
//                setContentView(scannerView);
//                scannerView.startCamera();
            }
        });

        imgBack_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                imgBack_test.startAnimation(animation);
                finish();
                //checkBackCon();
            }
        });


        savePickingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                savePickingUp.startAnimation(animation);

                final AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setTitle(getString(R.string.alert));
                alertbox.setMessage("SAVE JOB?");
                alertbox.setNegativeButton("SAVE",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {

                                new AsyncTask<Void, Void, Void>() {
                                    int IsSuccess = 1;
                                    ProgressDialog pd;
                                    private int lastExpandedPosition = -1;

                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
                                        pd = new ProgressDialog(PinkingUpMaster_Activity.this);
                                        pd.setCancelable(false);
                                        pd.setMessage("Saving data..");
                                        pd.show();

                                    }

                                    @Override
                                    protected Void doInBackground(Void... voids) {

                                        try {
                                            if (expandableListAdapter == null) {
                                                cancel(true);
                                            } else {


                                                for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                                                    expandableListAdapter.getChildrenCount(i);
                                                    final PickingUp_Model picking = (PickingUp_Model) expandableListAdapter.getGroup(i);
                                                    for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                                                        final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                                                        //  Log.d("lloldo", "doInBackground: " + picking.getConsignment());

//                                                        if (picking.getConsignment().equals(expandedList.getConsignment())) {
//                                                            if (expandedList.getIs_scaned().equals("0")) {
//                                                                Log.d("lloldo", "doInBackground: " + expandedList.getConsignment() + ">" + expandedList.getIs_scaned());
//
//                                                            } else {
//
//                                                            }
//                                                        } else {
//
//                                                        }

                                                        ContentValues cv = new ContentValues();
                                                        cv.put("is_scaned", expandedList.getIs_scaned());
                                                        cv.put("modified_date", getDate);
                                                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + expandedList.getDelivery_no() + "' and plan_seq = '" + expandedList.getPlan_seq() + "' and activity_type = 'LOAD' and " +
                                                                " consignment_no = '" + expandedList.getConsignment() + "' and box_no = '" + expandedList.getBox_no() + "' and trash = '0'", null);
//

                                                        lastExpandedPosition = i;
                                                        IsSuccess = 1;
                                                    }
                                                }


                                            }

                                        } catch (Exception e) {
                                            IsSuccess = 0;
                                        }


                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);

                                        getSQLite();

                                        pd.dismiss();

                                        if (IsSuccess == 1) {
                                            Toast.makeText(PinkingUpMaster_Activity.this, "Saved.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(PinkingUpMaster_Activity.this, "can't save.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }.execute();

                            }
                        });
                alertbox.setNeutralButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                alertbox.show();


            }
        });

        imageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (INPUT_WAY.equals("PLUS")) {
                    INPUT_WAY = "MINUS";
                    imageView8.setImageResource(R.drawable.ic_toggleminus);

                } else {
                    INPUT_WAY = "PLUS";
                    imageView8.setImageResource(R.drawable.ic_toggleplus);
                }
            }
        });

        btnEnterWaybillNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                btnEnterWaybillNo.startAnimation(animation);

                String get_waybill = edtFineWaybillPick.getText().toString();


                if (INPUT_WAY.equals("PLUS")) {
                    for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {

                        for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                            final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                            if (get_waybill.equals(expandedList.getWaybil_no())) {

                                lastPosition = i;

                                ((PickingUpEexpand_Model) expandableListAdapter.getChild(i, j)).setIs_scaned("1");
                                Toast.makeText(PinkingUpMaster_Activity.this, "Checked.", Toast.LENGTH_SHORT).show();
                                expandableListView.setAdapter(expandableListAdapter);
                                expandableListView.expandGroup(i);
                                //expandableListAdapter.notifyDataSetChanged();
                            } else {
                                // Toast.makeText(PinkingUpMaster_Activity.this, "This Waybill No doesn't exist.", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                } else {
                    for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {

                        for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                            final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                            if (get_waybill.equals(expandedList.getWaybil_no())) {

                                lastPosition = i;
                                ((PickingUpEexpand_Model) expandableListAdapter.getChild(i, j)).setIs_scaned("0");
                                Toast.makeText(PinkingUpMaster_Activity.this, "Un Check.", Toast.LENGTH_SHORT).show();
                                expandableListView.setAdapter(expandableListAdapter);
                                expandableListView.expandGroup(i);
                               // expandableListAdapter.notifyDataSetChanged();
                            } else {
                                //  Toast.makeText(PinkingUpMaster_Activity.this, "This Waybill No doesn't exist.", Toast.LENGTH_SHORT).show();
                            }

                        }


                    }
                }


            }
        });


    }


//    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler {
//
//        @Override
//        public void handleResult( Result result ) {
//
//            String resultCode = result.getText();
//
//
//            //setContentView(R.layout.activity_picking_up_master);
//            //scannerView.stopCamera();
//            Intent intent = new Intent(getApplicationContext(), PinkingUpMaster_Activity.class);
//            intent.putExtra("publicKey", result.getText());
//            scannerView.stopCamera();
//            startActivity(intent);
//            finish();
//
//        }
//    }

    private void checkBackCon() {
        final SharedPreferences user_data = getSharedPreferences("DATA_DETAIL_PICK", Context.MODE_PRIVATE);
        final String delivery_no = user_data.getString("delivery_no", "");
        final String plan_seq = user_data.getString("plan_seq", "");

        String isscaned_pick = "";

        String sql = "select (select count( pl2.is_scaned) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and is_scaned = '0' and pl2.trash = pl.trash) as isscaned_pick\n" +
                "from Plan pl\n" +
                "where pl.delivery_no = '" + delivery_no + "' and  pl.plan_seq = '" + plan_seq + "' and pl.trash = '0'" +
                "GROUP BY pl.delivery_no";
        Cursor cursor = databaseHelper.selectDB(sql);
        Log.d("PickingUpLOG_001", "total line " + cursor.getColumnCount());

        list_expand = new ArrayList<>();

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                isscaned_pick = cursor.getString(cursor.getColumnIndex("isscaned_pick"));
                Log.d("PickingUpLOG_001", isscaned_pick);
            } while (cursor.moveToNext());
        }

        if (!isscaned_pick.equals("0")) {
            final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setTitle(getString(R.string.alert));
            alertbox.setMessage(getString(R.string.work_pending));


            alertbox.setNegativeButton(getString(R.string.confirm),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0,
                                            int arg1) {
                            finish();
                        }
                    });
            alertbox.setNeutralButton(getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.cancel();

                        }
                    });

            alertbox.show();
        } else {
            user_data.edit().clear();
            finish();
        }
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
                "from Plan pl\n" +
                "inner join consignment cm on cm.consignment_no = pl.consignment_no\n" +
                "where pl.delivery_no = '" + delivery_no + "' and  pl.plan_seq = '" + plan_seq + "' and pl.activity_type = 'LOAD' and pl.trash = '0'" +
                "GROUP BY pl.delivery_no, pl.consignment_no";
        Cursor cursor = databaseHelper.selectDB(sql);
        Log.d("PickingUpLOG", "total line " + cursor.getCount());

        expandableListDetail = new HashMap<>();
        list = new ArrayList<>();

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

                Log.d("PickingUpLOG", "onCreate: " + "==>" + global_cancel);

                list.add(new PickingUp_Model(consignment, box_total, box_checked, global_total, station_address, pay_type, global_cancel, price));


                String sql_expand = "select delivery_no, plan_seq, box_no, waybill_no, is_scaned, (box_no - 1)+1 as row_number from Plan where consignment_no = '" + consignment + "' and activity_type = 'LOAD' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and trash = '0'";
                Cursor cursor_expand = databaseHelper.selectDB(sql_expand);
                Log.d("PickingUpLOG", "total line " + cursor_expand.getColumnCount());

                list_expand = new ArrayList<>();

                cursor_expand.moveToFirst();
                if (cursor_expand.getCount() > 0) {
                    do {
                        String box_no = cursor_expand.getString(cursor_expand.getColumnIndex("box_no"));
                        String waybill_no = cursor_expand.getString(cursor_expand.getColumnIndex("waybill_no"));
                        String is_scaned = cursor_expand.getString(cursor_expand.getColumnIndex("is_scaned"));
                        String row_number = cursor_expand.getString(cursor_expand.getColumnIndex("row_number"));
                        String delivery_no2 = cursor_expand.getString(cursor_expand.getColumnIndex("delivery_no"));
                        String plan_seq2 = cursor_expand.getString(cursor_expand.getColumnIndex("plan_seq"));


                        list_expand.add(new PickingUpEexpand_Model(box_no, waybill_no, is_scaned, row_number, consignment, delivery_no2, plan_seq2));
                    } while (cursor_expand.moveToNext());
                }

                expandableListView = findViewById(R.id.exPandDeli);
                expandableListDetail.put(consignment, list_expand);
            } while (cursor.moveToNext());
        }

        expandableListAdapter = new PickingUpAdapter(this, list, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        user_data.edit().clear();
    }


    private void ResizeImages(String sPath) throws IOException {

        Bitmap photo = BitmapFactory.decodeFile(sPath);
        // photo = Bitmap.createScaledBitmap(photo, 300, 300, false);

        int width = photo.getWidth();
        int height = photo.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap resizedBitmap = Bitmap.createBitmap(photo, 0, 0, width, height, matrix, true);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);

        File file = new File(sPath);
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(bytes.toByteArray());
        fo.close();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result not found", Toast.LENGTH_SHORT).show();
            } else {
                String getScanText = result.getContents();
                getScanText = getScanText.trim();

               // edtFineWaybillPick.setText(getScanText);



                if (INPUT_WAY.equals("PLUS")) {
                    for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {

                        for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                            final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);



                            if (getScanText.equals(expandedList.getWaybil_no())) {

                                Log.d("ASdgfjksdzfgsdf", "onActivityResult: "+result.getContents());
                                lastPosition = i;

                                ((PickingUpEexpand_Model) expandableListAdapter.getChild(i, j)).setIs_scaned("1");
                                Toast.makeText(PinkingUpMaster_Activity.this, "Checked.", Toast.LENGTH_SHORT).show();
                                expandableListView.setAdapter(expandableListAdapter);
                                expandableListView.expandGroup(i);
                                //expandableListAdapter.notifyDataSetChanged();
                            } else {
                                // Toast.makeText(PinkingUpMaster_Activity.this, "This Waybill No doesn't exist.", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                } else {
                    for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {

                        for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                            final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                            if (getScanText.equals(expandedList.getWaybil_no())) {

                                lastPosition = i;
                                ((PickingUpEexpand_Model) expandableListAdapter.getChild(i, j)).setIs_scaned("0");
                                Toast.makeText(PinkingUpMaster_Activity.this, "Un Check.", Toast.LENGTH_SHORT).show();
                                expandableListView.setAdapter(expandableListAdapter);
                                expandableListView.expandGroup(i);
                                //expandableListAdapter.notifyDataSetChanged();
                            } else {
                                //  Toast.makeText(PinkingUpMaster_Activity.this, "This Waybill No doesn't exist.", Toast.LENGTH_SHORT).show();
                            }

                        }


                    }
                }




            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


        if (requestCode == IMAGE_01 && resultCode == Activity.RESULT_OK) {

            deleteImage.add("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picture1);

            final Uri imageUri_1 = Uri.parse(currentPhotoPath);
            final File file_1 = new File(imageUri_1.getPath());

            try {
                ResizeImages(currentPhotoPath);
                InputStream ims = new FileInputStream(file_1);
                imgCommentPick_01.setImageBitmap(BitmapFactory.decodeStream(ims));
                Log.d("getPatha", "onActivityResult: " + imageUri_1.getPath() + "=>01" + file_1.getPath() + "=>02" + file_1.getName());

                arrayNameImage[0] = file_1.getName();


                if (file_1.getName() != null) {
                    picTemp1.add(file_1.getName());
                }


                picture1 = file_1.getName();

                Log.d("AsfiuASEHFIOPqeu", "file_A: " + imageUri_1.getPath());

            } catch (FileNotFoundException e) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }


            imgDeletePick01.setVisibility(View.GONE);


        }


        if (requestCode == IMAGE_02 && resultCode == Activity.RESULT_OK) {


            deleteImage.add("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picture2);
//            try {
//                if ( !picture1.equals("") ) {
//                    File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picture1);
//                    file.delete();
//                }
//
//            } catch (Exception e) {
//
//            }

            final Uri imageUri = Uri.parse(currentPhotoPath);
            final File file = new File(imageUri.getPath());

            try {
                ResizeImages(currentPhotoPath);
                InputStream ims = new FileInputStream(file);
                imgCommentPick_02.setImageBitmap(BitmapFactory.decodeStream(ims));
                Log.d("getPatha", "onActivityResult: " + imageUri.getPath() + "=>01" + file.getPath() + "=>02" + file.getName());
                arrayNameImage[1] = file.getName();


                if (file.getName() != null) {
                    picTemp2.add(file.getName());
                }


                picture2 = file.getName();

            } catch (FileNotFoundException e) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }


            imgDeletePick02.setVisibility(View.GONE);

        }

        if (requestCode == IMAGE_03 && resultCode == Activity.RESULT_OK) {


            deleteImage.add("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picture3);
            final Uri imageUri = Uri.parse(currentPhotoPath);
            final File file = new File(imageUri.getPath());

            try {
                ResizeImages(currentPhotoPath);
                InputStream ims = new FileInputStream(file);
                imgCommentPick_03.setImageBitmap(BitmapFactory.decodeStream(ims));
                Log.d("getPatha", "onActivityResult: " + imageUri.getPath() + "=>01" + file.getPath() + "=>02" + file.getName());

                arrayNameImage[2] = file.getName();


                if (file.getName() != null) {
                    picTemp3.add(file.getName());
                }


                picture3 = file.getName();
            } catch (FileNotFoundException e) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }


            imgDeletePick03.setVisibility(View.GONE);


//            if ( data_intent != null ) {
////                btnSaveComent_PICK.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick( View view ) {
////                        String commentText = edtComment_PICK.getText().toString();
////                        Toast.makeText(PinkingUpMaster_Activity.this, "Saved.", Toast.LENGTH_SHORT).show();
////
////                        for (int i = 0; i < deleteImage.size(); i++) {
////                            Log.d("AsfiuASEHFIOPqeu", "onClick: " + deleteImage.get(i));
////                            File file = new File(deleteImage.get(i));
////                            file.delete();
////                        }
////
////                        ContentValues cv = new ContentValues();
////                        cv.put("comment", commentText);
////                        cv.put("picture3", file.getName());
////                        cv.put("is_scaned", "2");
////                        cv.put("modified_date", getDate);
////                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and activity_type = 'LOAD' and " +
////                                " consignment_no = '" + consignment_no + "' and box_no = '" + box_no + "' and trash = '0'", null);
////
////                        ContentValues cv2 = new ContentValues();
////                        cv2.put("name_img", file.getName());
////                        cv2.put("status_img", "0");
////                        databaseHelper.db().insert("image", null, cv2);
////                        getSQLite();
////                        alertDialog.dismiss();
////                    }
////                });
//
//
//                data_intent.edit().clear();
//            }
        }

        for (String in : arrayNameImage) {
            Log.d("ASaa", "onActivityResult: " + in);
        }


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    //***********************************************************************************************************************************************************************//
    public class PickingUpAdapter extends BaseExpandableListAdapter {

        View popupInputDialogView = null;
        View popupInputDialogView2 = null;
        boolean expanded = false;

        private final Context context;
        private final ArrayList<PickingUp_Model> list;
        private final HashMap<String, ArrayList<PickingUpEexpand_Model>> expandableListDetail;

        PickingUpAdapter(Context context, ArrayList<PickingUp_Model> list, HashMap<String, ArrayList<PickingUpEexpand_Model>> expandableListDetail) {
            this.context = context;
            this.expandableListDetail = expandableListDetail;
            this.list = list;
        }


        @Override
        public Object getChild(int listPosition, int expandedListPosition) {
            return this.expandableListDetail.get(this.list.get(listPosition).getConsignment())
                    .get(expandedListPosition);
        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(final int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) getChild(listPosition, expandedListPosition);


            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_expanditemline, null);
            }

            Log.d("qwegwegsdg", "getGroupView: " + (expandedListPosition + 1));

            final TextView box_no, waybill_no, tvExpand_Count;
            final ImageView imgEditBoxNoPickup;

            box_no = convertView.findViewById(R.id.tvExpand_Box);
            waybill_no = convertView.findViewById(R.id.tvExpand_waybill_no);
            tvExpand_Count = convertView.findViewById(R.id.tvExpand_Count);
            checkBox = convertView.findViewById(R.id.cbExpand_isscaned);
            imgEditBoxNoPickup = convertView.findViewById(R.id.imgEditBoxNoPickup);


            tvExpand_Count.setText(String.valueOf((expandedListPosition + 1)));
            box_no.setText("BoxNo. " + expandedList.getBox_no());
            waybill_no.setText("WaybillNo: " + expandedList.getWaybil_no());


            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        expandedList.setIs_scaned("1");
                        imgEditBoxNoPickup.setClickable(false);
                    } else {
                        expandedList.setIs_scaned("0");
                        imgEditBoxNoPickup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                                imgEditBoxNoPickup.startAnimation(animation);

                                showDialogBox(expandedList.getBox_no(), expandedList.getConsignment(), expandedList.getDelivery_no(), expandedList.getPlan_seq());
                            }
                        });
                    }

                }
            });


            if (expandedList.getIs_scaned().equals("1")) {
                checkBox.setChecked(true);
                checkBox.setEnabled(false);
                checkBox.setButtonDrawable(R.drawable.ic_check_box_disable);
            } else if (expandedList.getIs_scaned().equals("0")) {
                checkBox.setChecked(false);
                imgEditBoxNoPickup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                        imgEditBoxNoPickup.startAnimation(animation);

                        showDialogBox(expandedList.getBox_no(), expandedList.getConsignment(), expandedList.getDelivery_no(), expandedList.getPlan_seq());
                    }
                });

            }
            if (expandedList.getIs_scaned().equals("2")) {
                //imgEditBoxNoPickup.setEnabled(false);
                // checkBox.setEnabled(false);
                checkBox.setButtonDrawable(R.drawable.ic_indeterminate_check_box_black_24dp);

            }

            if (!checkBox.isChecked()) {
                imgEditBoxNoPickup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                        imgEditBoxNoPickup.startAnimation(animation);

                        showDialogBox(expandedList.getBox_no(), expandedList.getConsignment(), expandedList.getDelivery_no(), expandedList.getPlan_seq());
                    }
                });
            } else {

            }


            return convertView;
        }


        @Override
        public int getChildrenCount(int listPosition) {
            return this.expandableListDetail.get(this.list.get(listPosition).getConsignment())
                    .size();
        }

        @Override
        public Object getGroup(int listPosition) {
            return this.list.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            //return 5;
            return this.list.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(final int listPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {

            final PickingUp_Model listTitle = (PickingUp_Model) getGroup(listPosition);


            Log.d("sfwagvSDv", "getGroupView: " + (listPosition + 1));

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_expandworkline, null);
            }
            TextView consignment = convertView.findViewById(R.id.tvPickUp_Consignment);
            TextView tvConGroupCountPick = convertView.findViewById(R.id.textView26);
            final ImageView swichExpand = convertView.findViewById(R.id.swichExpand);
            final ImageView imgDetailConsignNo = convertView.findViewById(R.id.imgDetailConsignNo);

            ImageView imageView9 = convertView.findViewById(R.id.imageView9);
            TextView textView24 = convertView.findViewById(R.id.textView24);
            TextView pick_pay_type = convertView.findViewById(R.id.pick_pay_type);
            TextView textView25 = convertView.findViewById(R.id.textView25);
            TextView tv_Global_cancel = convertView.findViewById(R.id.tv_Global_cancel);


            consignment.setTypeface(null, Typeface.BOLD);
            consignment.setText("Cons.No: " + listTitle.getConsignment());
            tvConGroupCountPick.setText(String.valueOf((listPosition + 1)));
            TextView box = convertView.findViewById(R.id.tvPickingUp_Box);
            TextView tvPickUp_global = convertView.findViewById(R.id.tvPickUp_global);
            box.setText("Box (" + listTitle.getBox_checked() + " | " + listTitle.getBox_total() + ")");

            // Log.d("ASfjhbaskjdfgsdfasd", "getGroupView: " + listTitle.getGlobal_cancel());

            tvPickUp_global.setText("Global (" + listTitle.getGlobal_total() + ")");

            if (!listTitle.getGlobal_cancel().equals("0")) {
                tv_Global_cancel.setVisibility(View.VISIBLE);
                tv_Global_cancel.setText(listTitle.getGlobal_cancel() + " Canceled.");
            }

            ImageView img_selection = convertView.findViewById(R.id.img_arrow_drop);
            int imageResourceId = isExpanded ? R.mipmap.ic_arrow_drop_down
                    : R.mipmap.ic_arrow_drop_up;
            img_selection.setImageResource(imageResourceId);


            swichExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (swichExpand.getDrawable() == getResources().getDrawable(R.drawable.jobon)) {
                        SWICH_EXPAND = "OFF";
                        swichExpand.setImageResource(R.drawable.joboff);
                    } else {
                        SWICH_EXPAND = "ON";
                        lastPosition = listPosition;
                        lastData = listTitle.getConsignment();
                        swichExpand.setImageResource(R.drawable.jobon);
                    }
                    Log.d("Afsfss", "onClick: " + lastPosition);

                    notifyDataSetChanged();

                    Log.d("askljb", "onClick: " + SWICH_EXPAND);
                }
            });

            if (lastPosition == listPosition) {
                SWICH_EXPAND = "ON";
                swichExpand.setImageResource(R.drawable.jobon);
                lastData = listTitle.getConsignment();
            } else {
                SWICH_EXPAND = "OFF";
                swichExpand.setImageResource(R.drawable.joboff);
            }

            if (listTitle.getPaytype().equals("COD")) {

                pick_pay_type.setVisibility(View.VISIBLE);
                textView25.setVisibility(View.VISIBLE);
                imageView9.setVisibility(View.GONE);
                textView24.setVisibility(View.GONE);

                textView25.setText(listTitle.getPrice() + " THB");


            } else {
                imageView9.setVisibility(View.VISIBLE);
                textView24.setVisibility(View.VISIBLE);
                pick_pay_type.setVisibility(View.GONE);
                textView25.setVisibility(View.GONE);

            }

            imgDetailConsignNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                    imgDetailConsignNo.startAnimation(animation);
                    dialogDetail(listTitle.getConsignment(), listTitle.getStation_address(), listTitle.getPaytype()
                            , listTitle.getBox_total(), listTitle.getGlobal_total(), listTitle.getPrice());
                }
            });

            //notifyDataSetChanged();


            return convertView;
        }


        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return true;
        }


        private void dialogDetail(String consignment, String station_address, String pay_type, String box_total, String global_total, String price) {

            TextView tvConsignment_con_dialog;

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setCancelable(false);

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            popupInputDialogView2 = layoutInflater.inflate(R.layout.con_dialog_detail, null);

            alertDialogBuilder.setView(popupInputDialogView2);
            alertDialog2 = alertDialogBuilder.create();


            imgClose_dialog = popupInputDialogView2.findViewById(R.id.imgClose_dialog);
            // mTabHost = popupInputDialogView2.findViewById(android.R.id.tabhost)
            tvConsignment_con_dialog = popupInputDialogView2.findViewById(R.id.tvConsignment_con_dialog);
            TextView pick_dialog_station_address = popupInputDialogView2.findViewById(R.id.tv_dialog_station_address);
            TextView pick_dialog_pay_type = popupInputDialogView2.findViewById(R.id.pick_dialog_pay_type);
            TextView pick_dialog_pay_type_credit = popupInputDialogView2.findViewById(R.id.tv_dialog_pay_type_credit);
            TextView pick_summary = popupInputDialogView2.findViewById(R.id.tv_summary);
            TextView tv_dialog_thb = popupInputDialogView2.findViewById(R.id.tv_dialog_thb);
            ImageView pick_dialog_img_credit = popupInputDialogView2.findViewById(R.id.dialog_img_credit);
            SegmentedButtonGroup segmentedButtonGroup = popupInputDialogView2.findViewById(R.id.buttonGroup_vectorDrawable);
            rvDialogCons = popupInputDialogView2.findViewById(R.id.rvDialogCons);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            rvDialogCons.setLayoutManager(layoutManager);

            tvConsignment_con_dialog.setText("Cons.No: " + consignment);
            pick_summary.setText(box_total + " Boxs\n" + global_total + " Unit(Global)");

            if (!station_address.equals("")) {
                pick_dialog_station_address.setText(station_address);
            } else {
                pick_dialog_station_address.setText("-");
            }

            if (pay_type.equals("COD")) {

                pick_dialog_pay_type.setVisibility(View.VISIBLE);
                tv_dialog_thb.setVisibility(View.VISIBLE);
                pick_dialog_img_credit.setVisibility(View.GONE);
                pick_dialog_pay_type_credit.setVisibility(View.GONE);

                tv_dialog_thb.setText(price + " THB");

            } else {
                pick_dialog_img_credit.setVisibility(View.VISIBLE);
                pick_dialog_pay_type_credit.setVisibility(View.VISIBLE);
                pick_dialog_pay_type.setVisibility(View.GONE);
                tv_dialog_thb.setVisibility(View.GONE);

            }


            imgClose_dialog.setOnClickListener(view -> {

                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                imgClose_dialog.startAnimation(animation);
                alertDialog2.dismiss();
            });


            ArrayList<Dialog_Cons_Detail_Model> global = new ArrayList<>();

            SharedPreferences user_data = getSharedPreferences("DATA_DETAIL_DIALOG_PICK", Context.MODE_PRIVATE);
            String delivery_no = user_data.getString("delivery_no", "");
            String plan_seq = user_data.getString("plan_seq", "");

            String sql = "select DISTINCT cm.global_no, cm.deli_note_amount_price, cm.detail_remarks " +
                    "from plan pn " +
                    "inner join consignment cm on cm.consignment_no = pn.consignment_no " +
                    "where pn.delivery_no = '" + delivery_no + "' and pn.plan_seq = '" + plan_seq + "' and pn.consignment_no = '" + consignment + "' ";

            Log.d("AsfWEGSDVAS", "dialogDetail: " + sql);
            Cursor cursor = databaseHelper.selectDB(sql);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    String global_no = cursor.getString(cursor.getColumnIndex("global_no"));
                    String deli_note_amount_price = cursor.getString(cursor.getColumnIndex("deli_note_amount_price"));
                    String detail_remarks = cursor.getString(cursor.getColumnIndex("detail_remarks"));

                    global.add(new Dialog_Cons_Detail_Model(global_no, deli_note_amount_price, detail_remarks));

                } while (cursor.moveToNext());
            }

            dialogConsAdapter = new DialogConsAdapter(global, context);
            rvDialogCons.setAdapter(dialogConsAdapter);
            user_data.edit().clear();


            segmentedButtonGroup.setPosition(0, true);
            segmentedButtonGroup.setOnPositionChangedListener(position -> {
                switch (position) {
                    case 0:

                        ArrayList<Dialog_Cons_Detail_Model> global2 = new ArrayList<>();

                        SharedPreferences user_data2 = getSharedPreferences("DATA_DETAIL_DIALOG_PICK", Context.MODE_PRIVATE);
                        String delivery_no2 = user_data2.getString("delivery_no", "");
                        String plan_seq2 = user_data2.getString("plan_seq", "");

                        String sql2 = "select DISTINCT cm.global_no, cm.deli_note_amount_price, cm.detail_remarks " +
                                "from plan pn " +
                                "inner join consignment cm on cm.consignment_no = pn.consignment_no " +
                                "where pn.delivery_no = '" + delivery_no2 + "' and pn.plan_seq = '" + plan_seq2 + "' and pn.consignment_no = '" + consignment + "' ";
                        Cursor cursor2 = databaseHelper.selectDB(sql2);
                        cursor2.moveToFirst();
                        if (cursor2.getCount() > 0) {
                            do {
                                String global_no = cursor2.getString(cursor2.getColumnIndex("global_no"));
                                String deli_note_amount_price = cursor2.getString(cursor2.getColumnIndex("deli_note_amount_price"));
                                String detail_remarks = cursor2.getString(cursor2.getColumnIndex("detail_remarks"));

                                global2.add(new Dialog_Cons_Detail_Model(global_no, deli_note_amount_price, detail_remarks));

                            } while (cursor2.moveToNext());
                        }


                        dialogConsAdapter = new DialogConsAdapter(global2, context);
                        rvDialogCons.setAdapter(dialogConsAdapter);
                        user_data2.edit().clear();
                        break;
                    case 1:
                        ArrayList<Dialog_Cons_Detail_Model> global3 = new ArrayList<>();

                        SharedPreferences user_data3 = getSharedPreferences("DATA_DETAIL_DIALOG_PICK", Context.MODE_PRIVATE);
                        String delivery_no3 = user_data3.getString("delivery_no", "");
                        String plan_seq3 = user_data3.getString("plan_seq", "");

                        String sql3 = "select DISTINCT cm.global_no, cm.deli_note_amount_price, cm.detail_remarks " +
                                "from plan pn " +
                                "inner join consignment cm on cm.consignment_no = pn.consignment_no " +
                                "where pn.delivery_no = '" + delivery_no3 + "' and pn.plan_seq = '" + plan_seq3 + "' and pn.consignment_no = '" + consignment + "' and cm.detail_remarks = '' ";
                        Cursor cursor3 = databaseHelper.selectDB(sql3);
                        cursor3.moveToFirst();
                        if (cursor3.getCount() > 0) {
                            do {
                                String global_no = cursor3.getString(cursor3.getColumnIndex("global_no"));
                                String deli_note_amount_price = cursor3.getString(cursor3.getColumnIndex("deli_note_amount_price"));
                                String detail_remarks = cursor3.getString(cursor3.getColumnIndex("detail_remarks"));

                                global3.add(new Dialog_Cons_Detail_Model(global_no, deli_note_amount_price, detail_remarks));

                            } while (cursor3.moveToNext());
                        }


                        dialogConsAdapter = new DialogConsAdapter(global3, context);
                        rvDialogCons.setAdapter(dialogConsAdapter);
                        user_data3.edit().clear();

                        break;
                    case 2:
                        ArrayList<Dialog_Cons_Detail_Model> global4 = new ArrayList<>();

                        SharedPreferences user_data4 = getSharedPreferences("DATA_DETAIL_DIALOG_PICK", Context.MODE_PRIVATE);
                        String delivery_no4 = user_data4.getString("delivery_no", "");
                        String plan_seq4 = user_data4.getString("plan_seq", "");

                        String sql4 = "select DISTINCT cm.global_no, cm.deli_note_amount_price, cm.detail_remarks " +
                                "from plan pn " +
                                "inner join consignment cm on cm.consignment_no = pn.consignment_no " +
                                "where pn.delivery_no = '" + delivery_no4 + "' and pn.plan_seq = '" + plan_seq4 + "' and pn.consignment_no = '" + consignment + "' and cm.detail_remarks <> '' ";
                        Cursor cursor4 = databaseHelper.selectDB(sql4);
                        cursor4.moveToFirst();
                        if (cursor4.getCount() > 0) {
                            do {
                                String global_no = cursor4.getString(cursor4.getColumnIndex("global_no"));
                                String deli_note_amount_price = cursor4.getString(cursor4.getColumnIndex("deli_note_amount_price"));
                                String detail_remarks = cursor4.getString(cursor4.getColumnIndex("detail_remarks"));

                                global4.add(new Dialog_Cons_Detail_Model(global_no, deli_note_amount_price, detail_remarks));

                            } while (cursor4.moveToNext());
                        }


                        dialogConsAdapter = new DialogConsAdapter(global4, context);
                        rvDialogCons.setAdapter(dialogConsAdapter);
                        user_data4.edit().clear();

                        break;
                }

            });
            segmentedButtonGroup.getPosition();


            alertDialog2.show();

        }

        private void showDialogBox(final String box_no, final String consignment_no, final String delivery_no, final String plan_seq) {


            final SharedPreferences data_intent = getSharedPreferences("DATA_INTENT", Context.MODE_PRIVATE);
            TextView tvConsignment_Dialog, tv_BoxNo_Dialog;

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setCancelable(false);

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            popupInputDialogView = layoutInflater.inflate(R.layout.cus_dialog_pickingup, null);

            alertDialogBuilder.setView(popupInputDialogView);
            alertDialog = alertDialogBuilder.create();

            tvConsignment_Dialog = popupInputDialogView.findViewById(R.id.tvConsignment_con_dialog);
            tv_BoxNo_Dialog = popupInputDialogView.findViewById(R.id.tv_BoxNo_Dialog);
            imgClose_dialog = popupInputDialogView.findViewById(R.id.imgClose_dialog);
            imgCommentPick_01 = popupInputDialogView.findViewById(R.id.imgCommentPick_01);
            edtComment_PICK = popupInputDialogView.findViewById(R.id.edtComment_PICK);
            btnSaveComent_PICK = popupInputDialogView.findViewById(R.id.btnSaveComent_PICK);
            imgNewPick01 = popupInputDialogView.findViewById(R.id.imgNewPick01);
            imgDeletePick01 = popupInputDialogView.findViewById(R.id.imgDeletePick01);
            imgCommentPick_02 = popupInputDialogView.findViewById(R.id.imgCommentPick_02);
            imgNewPick02 = popupInputDialogView.findViewById(R.id.imgNewPick02);
            imgDeletePick02 = popupInputDialogView.findViewById(R.id.imgDeletePick02);
            imgCommentPick_03 = popupInputDialogView.findViewById(R.id.imgCommentPick_03);
            imgNewPick03 = popupInputDialogView.findViewById(R.id.imgNewPick03);
            imgDeletePick03 = popupInputDialogView.findViewById(R.id.imgDeletePick03);

            tvConsignment_Dialog.setText("Cons.No: " + consignment_no);
            tv_BoxNo_Dialog.setText("BoxNo: " + box_no);

            imgClose_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                    imgClose_dialog.startAnimation(animation);
                    data_intent.edit().clear();

                    Log.d("Djar", "onClick: 1" + picTemp1);
                    Log.d("Djar", "onClick: 2" + picTemp2);
                    Log.d("Djar", "onClick: 3" + picTemp3);

                    for (int i = 0; i < picTemp1.size(); i++) {


                        if (i != 0) {
                            File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picTemp1.get(i));
                            file.delete();
                        }
                    }

                    picTemp1 = new ArrayList<>();

                    for (int i = 0; i < picTemp2.size(); i++) {

                        if (i != 0) {
                            File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picTemp2.get(i));
                            file.delete();
                        }
                    }

                    picTemp2 = new ArrayList<>();

                    for (int i = 0; i < picTemp3.size(); i++) {

                        if (i != 0) {
                            File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picTemp3.get(i));
                            file.delete();
                        }
                    }

                    picTemp3 = new ArrayList<>();


                    alertDialog.dismiss();
                }
            });

            for (int i = 0; i < deleteImage.size(); i++) {
                Log.d("deltetImage", "onClick: " + deleteImage.get(i));
            }

            btnSaveComent_PICK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String commentText = edtComment_PICK.getText().toString();


                    for (int i = 0; i < deleteImage.size(); i++) {
                        Log.d("deltetImage", "onClick: " + deleteImage.get(i));
                        File file = new File(deleteImage.get(i));
                        file.delete();
                    }

                    deleteImage = new ArrayList<>();
                    picTemp1 = new ArrayList<>();
                    picTemp2 = new ArrayList<>();
                    picTemp3 = new ArrayList<>();

                    ContentValues cv = new ContentValues();
                    int index = 0;
                    for (String path : arrayNameImage) {

                        Log.d("pathString", "onClick: " + path);
                        switch (index) {
                            case 0:
                                if (!path.equals("")) {
                                    cv.put("picture1", path);
                                }
                                break;
                            case 1:
                                if (!path.equals("")) {
                                    cv.put("picture2", path);
                                }
                                break;
                            case 2:
                                if (!path.equals("")) {
                                    cv.put("picture3", path);
                                }
                                break;
                        }

                        index++;
                    }

                    if (!picture1.equals("") || !picture2.equals("") || !picture3.equals("") || !commentText.matches("")) {
                        cv.put("is_scaned", "2");
                        cv.put("comment", commentText);
                    } else {
                        cv.put("comment", "");
                        cv.put("is_scaned", "0");
                    }


                    cv.put("modified_date", getDate);
                    databaseHelper.db().update("Plan", cv, "delivery_no= '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and activity_type = 'LOAD' and " +
                            " consignment_no = '" + consignment_no + "' and box_no = '" + box_no + "' and trash = '0'", null);

                    Log.d("pathString", "onClick: " + delivery_no + "--" + plan_seq + "--" + consignment_no + "--" + box_no);

                    ContentValues cv2 = new ContentValues();
                    for (String path : arrayNameImage) {
                        if (!path.equals("")) {
                            cv2.put("name_img", path);
                            cv2.put("status_img", "0");
                            databaseHelper.db().insert("image", null, cv2);
                        }
                    }

                    getSQLite();
                    alertDialog.dismiss();
                    Toast.makeText(PinkingUpMaster_Activity.this, "Saved.", Toast.LENGTH_SHORT).show();

                }
            });


            //**************************************************************************************************
            String sql = "select comment,  ifnull(picture1,'') picture1  from Plan where activity_type = 'LOAD' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "' " +
                    "and box_no = '" + box_no + "' and consignment_no = '" + consignment_no + "' and trash = '0'";
            Cursor cursor = databaseHelper.selectDB(sql);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {

                    String comment = cursor.getString(cursor.getColumnIndex("comment"));
                    picture1 = cursor.getString(cursor.getColumnIndex("picture1"));
                    edtComment_PICK.setText(comment);


                    Log.d("AsfiuASEHFIOPqeu", "showDialogBox: " + "/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picture1);


                    if (!picture1.equals("")) {
                        try {

                            picTemp1.add(picture1);
                            File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picture1);
                            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            imgCommentPick_01.setImageBitmap(myBitmap);
                        } catch (Exception e) {
                        }

                        imgCommentPick_01.setEnabled(false);

                        imgNewPick01.setVisibility(View.VISIBLE);
                        imgDeletePick01.setVisibility(View.GONE);

                        imgNewPick01.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                                imgNewPick01.startAnimation(animation);
                                data_intent.edit().putString("box_no", box_no).apply();
                                data_intent.edit().putString("consignment_no", consignment_no).apply();
                                data_intent.edit().putString("delivery_no", delivery_no).apply();
                                data_intent.edit().putString("plan_seq", plan_seq).apply();

                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {
                                    }
                                    if (photoFile != null) {
                                        Uri photoURI = FileProvider.getUriForFile(context,
                                                BuildConfig.APPLICATION_ID + ".provider",
                                                photoFile);
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(takePictureIntent, IMAGE_01);
                                    }
                                }
                            }
                        });

                        imgDeletePick01.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                                imgDeletePick01.startAnimation(animation);

                                final AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                                alertbox.setTitle(context.getString(R.string.alert));
                                alertbox.setMessage("Delete this image?");

                                alertbox.setNegativeButton("DELETE",
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface arg0,
                                                                int arg1) {


//
//                                                ContentValues cv = new ContentValues();
//                                                cv.put("picture1", "");
//                                                cv.put("modified_date", getDate);
//                                                databaseHelper.db().update("Plan", cv, "delivery_no= '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and activity_type = 'LOAD' and " +
//                                                        " consignment_no = '" + consignment_no + "' and box_no = '" + box_no + "' and trash = '0'", null);
//
//                                                databaseHelper.db().delete("image", "name_img=?", new String[]{ picture1 });

                                                //databaseHelper.db().execSQL("DELETE FROM " + imageTable + " WHERE " + CONTACTS_COLUMN_TITLE + "= '" + title + "'");

                                                imgCommentPick_01.setImageResource(R.mipmap.add_photo);
                                                imgNewPick01.setVisibility(View.GONE);
                                                imgDeletePick01.setVisibility(View.GONE);
                                                imgCommentPick_01.setEnabled(true);
                                                Toast.makeText(PinkingUpMaster_Activity.this, "Successfully deleted.", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                alertbox.setNeutralButton(context.getString(R.string.cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                alertbox.show();


                            }
                        });

                    } else {
                        picTemp1.add("");
                    }

                } while (cursor.moveToNext());

            }
//**************************************************************************************************

            String sql02 = "select comment, ifnull(picture2,'') picture2 from Plan where activity_type = 'LOAD' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "' " +
                    "and box_no = '" + box_no + "' and consignment_no = '" + consignment_no + "' and trash = '0'";
            Cursor cursor02 = databaseHelper.selectDB(sql02);

            cursor02.moveToFirst();
            if (cursor02.getCount() > 0) {
                do {

                    String comment = cursor02.getString(cursor02.getColumnIndex("comment"));
                    picture2 = cursor02.getString(cursor02.getColumnIndex("picture2"));
                    edtComment_PICK.setText(comment);


                    if (!picture2.equals("")) {
                        picTemp2.add(picture2);
                        File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picture2);
                        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        imgCommentPick_02.setImageBitmap(myBitmap);
                        imgCommentPick_02.setEnabled(false);

                        imgNewPick02.setVisibility(View.VISIBLE);
                        imgDeletePick02.setVisibility(View.GONE);

                        imgNewPick02.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                                imgNewPick02.startAnimation(animation);
                                data_intent.edit().putString("box_no", box_no).apply();
                                data_intent.edit().putString("consignment_no", consignment_no).apply();
                                data_intent.edit().putString("delivery_no", delivery_no).apply();
                                data_intent.edit().putString("plan_seq", plan_seq).apply();

                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {
                                    }
                                    if (photoFile != null) {
                                        Uri photoURI = FileProvider.getUriForFile(context,
                                                BuildConfig.APPLICATION_ID + ".provider",
                                                photoFile);
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(takePictureIntent, IMAGE_02);
                                    }
                                }
                            }
                        });


                        imgDeletePick02.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                                imgDeletePick02.startAnimation(animation);


                                final AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                                alertbox.setTitle(context.getString(R.string.alert));
                                alertbox.setMessage("Delete this image?");

                                alertbox.setNegativeButton(context.getString(R.string.delete),
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface arg0,
                                                                int arg1) {

                                                ContentValues cv = new ContentValues();
                                                cv.put("picture2", "");
                                                cv.put("modified_date", getDate);
                                                databaseHelper.db().update("Plan", cv, "delivery_no= '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and activity_type = 'LOAD' and " +
                                                        " consignment_no = '" + consignment_no + "' and box_no = '" + box_no + "' and trash = '0'", null);

                                                databaseHelper.db().delete("image", "name_img=?", new String[]{picture2});

                                                imgCommentPick_02.setImageResource(R.mipmap.add_photo);
                                                imgNewPick02.setVisibility(View.GONE);
                                                imgDeletePick02.setVisibility(View.GONE);
                                                imgCommentPick_02.setEnabled(true);
                                                Toast.makeText(PinkingUpMaster_Activity.this, "Successfully deleted.", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                alertbox.setNeutralButton(context.getString(R.string.cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                alertbox.show();


                            }
                        });

                    } else {
                        picTemp2.add("");
                    }

                } while (cursor02.moveToNext());

            }
//**************************************************************************************************

            String sql03 = "select comment, ifnull(picture3,'') picture3 from Plan where activity_type = 'LOAD' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "' " +
                    "and box_no = '" + box_no + "' and consignment_no = '" + consignment_no + "' and trash = '0'";
            Cursor cursor03 = databaseHelper.selectDB(sql03);

            cursor03.moveToFirst();
            if (cursor03.getCount() > 0) {
                do {

                    String comment = cursor03.getString(cursor03.getColumnIndex("comment"));
                    picture3 = cursor03.getString(cursor03.getColumnIndex("picture3"));
                    edtComment_PICK.setText(comment);


                    if (!picture3.equals("")) {
                        picTemp3.add(picture3);
                        File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picture3);
                        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        imgCommentPick_03.setImageBitmap(myBitmap);
                        imgCommentPick_03.setEnabled(false);

                        imgNewPick03.setVisibility(View.VISIBLE);
                        imgDeletePick03.setVisibility(View.GONE);

                        imgNewPick03.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                                imgNewPick03.startAnimation(animation);
                                data_intent.edit().putString("box_no", box_no).apply();
                                data_intent.edit().putString("consignment_no", consignment_no).apply();
                                data_intent.edit().putString("delivery_no", delivery_no).apply();
                                data_intent.edit().putString("plan_seq", plan_seq).apply();

                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {
                                    }
                                    if (photoFile != null) {
                                        Uri photoURI = FileProvider.getUriForFile(context,
                                                BuildConfig.APPLICATION_ID + ".provider",
                                                photoFile);
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(takePictureIntent, IMAGE_03);
                                    }
                                }
                            }
                        });

                        imgDeletePick03.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                                imgDeletePick03.startAnimation(animation);

                                final AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                                alertbox.setTitle(context.getString(R.string.alert));
                                alertbox.setMessage("Delete this image?");

                                alertbox.setNegativeButton("DELETE",
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface arg0,
                                                                int arg1) {

                                                ContentValues cv = new ContentValues();
                                                cv.put("picture3", "");
                                                cv.put("modified_date", getDate);
                                                databaseHelper.db().update("Plan", cv, "delivery_no= '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and activity_type = 'LOAD' and " +
                                                        " consignment_no = '" + consignment_no + "' and box_no = '" + box_no + "' and trash = '0'", null);

                                                databaseHelper.db().delete("image", "name_img=?", new String[]{picture3});
                                                //  databaseHelper.db().delete("image", "name_img=" + picture3, null);

                                                imgCommentPick_03.setImageResource(R.mipmap.add_photo);
                                                imgNewPick03.setVisibility(View.GONE);
                                                imgDeletePick03.setVisibility(View.GONE);
                                                imgCommentPick_03.setEnabled(true);
                                                Toast.makeText(PinkingUpMaster_Activity.this, "Successfully deleted.", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                alertbox.setNeutralButton(context.getString(R.string.cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                alertbox.show();


                            }
                        });

                    } else {
                        picTemp3.add("");

                    }

                } while (cursor03.moveToNext());

            }

            imgCommentPick_01.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    data_intent.edit().putString("box_no", box_no).apply();
                    data_intent.edit().putString("consignment_no", consignment_no).apply();
                    data_intent.edit().putString("delivery_no", delivery_no).apply();
                    data_intent.edit().putString("plan_seq", plan_seq).apply();

                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                    imgCommentPick_01.startAnimation(animation);


                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                        }
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(context,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, IMAGE_01);
                        }
                    }


                }
            });

            imgCommentPick_02.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    data_intent.edit().putString("box_no", box_no).apply();
                    data_intent.edit().putString("consignment_no", consignment_no).apply();
                    data_intent.edit().putString("delivery_no", delivery_no).apply();
                    data_intent.edit().putString("plan_seq", plan_seq).apply();

                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                    imgCommentPick_02.startAnimation(animation);

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                        }
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(context,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, IMAGE_02);
                        }
                    }


                }
            });

            imgCommentPick_03.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    data_intent.edit().putString("box_no", box_no).apply();
                    data_intent.edit().putString("consignment_no", consignment_no).apply();
                    data_intent.edit().putString("delivery_no", delivery_no).apply();
                    data_intent.edit().putString("plan_seq", plan_seq).apply();

                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                    imgCommentPick_03.startAnimation(animation);

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                        }
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(context,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, IMAGE_03);
                        }
                    }

                }
            });


//            btnSaveComent_PICK.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick( View view ) {
//
//                    String commentText = edtComment_PICK.getText().toString();
//
//                    if ( commentText.matches("") ) {
//                        ContentValues cv = new ContentValues();
//                        cv.putNull("comment");
//                        cv.put("is_scaned", "0");
//                        cv.put("modified_date", getDate);
//                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and activity_type = 'LOAD' and " +
//                                " consignment_no = '" + consignment_no + "' and box_no = '" + box_no + "' and trash = '0'", null);
//                        alertDialog.dismiss();
//                        getSQLite();
//                        Toast.makeText(PinkingUpMaster_Activity.this, "Saved.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        ContentValues cv = new ContentValues();
//                        cv.put("comment", commentText);
//                        cv.put("is_scaned", "2");
//                        cv.put("modified_date", getDate);
//                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and activity_type = 'LOAD' and " +
//                                " consignment_no = '" + consignment_no + "' and box_no = '" + box_no + "' and trash = '0'", null);
//                        alertDialog.dismiss();
//                        getSQLite();
//                        Toast.makeText(PinkingUpMaster_Activity.this, "Saved.", Toast.LENGTH_SHORT).show();
//
//                    }
//
//
//                }
//            });


            alertDialog.show();

        }


    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "EPOD_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


}
