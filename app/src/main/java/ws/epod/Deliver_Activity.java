package ws.epod;

import android.annotation.SuppressLint;
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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import java.util.List;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import ws.epod.Adapter.DialogConsAdapter;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.LanguageClass;
import ws.epod.ObjectClass.LocationTrack;
import ws.epod.ObjectClass.SQLiteModel.DeliverExpand_Model;
import ws.epod.ObjectClass.SQLiteModel.Deliver_Model;
import ws.epod.ObjectClass.SQLiteModel.Dialog_Cons_Detail_Model;
import ws.epod.ObjectClass.SQLiteModel.Reason_model;

public class Deliver_Activity extends AppCompatActivity {

    ExpandableListView expandableListView;
    DeliverAdapter expandableListAdapter;

    ZXingScannerView scannerView;

    ConnectionDetector netCon;
    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;
    Uri uri;

    RecyclerView rvDialogCons;
    DialogConsAdapter dialogConsAdapter;

    String currentPhotoPath;

    private static final int IMAGE_01 = 1888;
    private static final int IMAGE_02 = 1889;
    private static final int IMAGE_03 = 1890;

    ImageView imgBack_Deliver, imgSave_dialog_Deli, imgClose_dialog, imgCommentPick_01, imgNewPick01, imgDeletePick01, imgCommentPick_02, imgNewPick02, imgDeletePick02, imgCommentPick_03, imgNewPick03, imgDeletePick03, imageView8, imgCameraScan;

    EditText edtComment_PICK, edtFineWaybillPick;

    TextView btnEnterWaybillNo, bnCloseJobDeliver;

    String INPUT_WAY = "PLUS";
    String SWICH_EXPAND = "ON";
    Button btnSaveComent_PICK;
    int lastPosition = 0;

    AlertDialog alertDialog;
    AlertDialog alertDialog2;

    String picture1 = "";
    String picture2 = "";
    String picture3 = "";

    private String commentOfspinner = "";

    String[] arrayNameImage = new String[3];


    ArrayList<String> picTemp1 = new ArrayList<>();
    ArrayList<String> picTemp2 = new ArrayList<>();
    ArrayList<String> picTemp3 = new ArrayList<>();
    ArrayList<String> deleteImage = new ArrayList<>();

    ArrayList<String> Temp1 = new ArrayList<>();
    ArrayList<String> Temp2 = new ArrayList<>();
    ArrayList<String> Temp3 = new ArrayList<>();

    ArrayList<String> p1 = new ArrayList<>();


    ArrayList<Deliver_Model> list = new ArrayList<>();
    ArrayList<DeliverExpand_Model> list_expand = new ArrayList<>();
    HashMap<String, ArrayList<DeliverExpand_Model>> expandableListDetail;

    IntentIntegrator qrScan;

    FloatingActionButton fabHome, fabJobHome, fabJobToday;
    Animation showLayout, hideLayout;
    LinearLayout layoutJobHome, layoutJobToday;

    LocationTrack locationTrack;

    boolean isc = false;

    private LocationManager client;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkBackCon();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageClass.setLanguage(getApplicationContext());
        setContentView(R.layout.activity_deliver_);

        narisv = new NarisBaseValue(Deliver_Activity.this);
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        arrayNameImage[0] = "";
        arrayNameImage[1] = "";
        arrayNameImage[2] = "";


        qrScan = new IntentIntegrator(this);
        locationTrack = new LocationTrack(Deliver_Activity.this);


        imgBack_Deliver = findViewById(R.id.imgBack_Deliver);
        imgSave_dialog_Deli = findViewById(R.id.imgSave_dialog_Deli);
        bnCloseJobDeliver = findViewById(R.id.bnCloseJobDeliver);
        edtFineWaybillPick = findViewById(R.id.edtFineWaybillPick);
        btnEnterWaybillNo = findViewById(R.id.btnEnterWaybillNo);
        imageView8 = findViewById(R.id.imageView8);
        imgCameraScan = findViewById(R.id.imgCameraScan);

        fabHome = findViewById(R.id.fabHome5);
        layoutJobHome = findViewById(R.id.layoutJobHome);
        layoutJobToday = findViewById(R.id.layoutJobToday);
        fabJobHome = findViewById(R.id.fabJobHome);
        fabJobToday = findViewById(R.id.fabJobToday);
        expandableListView = findViewById(R.id.exPandDeli);


        showLayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_layout);
        hideLayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_layout);

        getSQLite();
        onClickFab();

        imgCameraScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                imgCameraScan.startAnimation(animation);
                qrScan.initiateScan();
            }
        });

        imgBack_Deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                imgBack_Deliver.startAnimation(animation);
                checkBackCon();
                finish();
            }
        });

        bnCloseJobDeliver.setOnClickListener(view -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
            bnCloseJobDeliver.startAnimation(animation);
            Intent intent = new Intent(getApplicationContext(), InvoiceDeliver_Activity.class);
            startActivity(intent);
        });


//        cancelDeliver.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick( View view ) {
//
//                String delivery_no = "";
//                String plan_seq = "";
//                String consignment_no = "";
//
//                for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
//                    expandableListAdapter.getChildrenCount(i);
//                    for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
//                        final DeliverExpand_Model expandedList = (DeliverExpand_Model) expandableListAdapter.getChild(i, j);
//                        delivery_no = expandedList.getDelivery_no();
//                        plan_seq = expandedList.getPlan_seq();
//                        consignment_no = expandedList.getConsignment();
//                    }
//                }
//
//                Intent intent = new Intent(getApplicationContext(), Signature_Activity.class);
////                intent.addFlags(Intent.FLAG);
//                intent.putExtra("delivery_no", delivery_no);
//                intent.putExtra("plan_seq", plan_seq);
//                intent.putExtra("consignment_no", consignment_no);
//                startActivity(intent);
//            }
//        });

        imgSave_dialog_Deli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                imgSave_dialog_Deli.startAnimation(animation);

                final AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setTitle(getString(R.string.alert));
                alertbox.setMessage("SAVE JOB?");
                alertbox.setNegativeButton("SAVE",
                        new DialogInterface.OnClickListener() {

                            @SuppressLint("StaticFieldLeak")
                            public void onClick(DialogInterface arg0,
                                                int arg1) {

                                new AsyncTask<Void, Void, Void>() {
                                    int IsSuccess = 1;
                                    int positionGroup = -1;
                                    ProgressDialog pd;
                                    private int lastExpandedPosition = -1;

                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
                                        pd = new ProgressDialog(Deliver_Activity.this);
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

                                                int[] position = isCheckSaveBox(expandableListAdapter);
                                                positionGroup = position[1];
                                                if (position[0] == 1) {
                                                    for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                                                        expandableListAdapter.getChildrenCount(i);
                                                        for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                                                            final DeliverExpand_Model expandedList = (DeliverExpand_Model) expandableListAdapter.getChild(i, j);

                                                            Log.d("ASfasdjhfk", "doInBackground: " + expandedList.getConsignment() + "box : " + expandedList.getBox_no() + " status: " +
                                                                    expandedList.getIs_scaned() + " date: " + expandedList.getTime_begin()+ "lat: "+ expandedList.getActual_lat()+ "lon: "+
                                                                    expandedList.getActual_lon());
////
                                                            ContentValues cv = new ContentValues();

                                                            cv.put("is_scaned", expandedList.getIs_scaned());
                                                            cv.put("actual_lat", expandedList.getActual_lat());
                                                            cv.put("actual_lon", expandedList.getActual_lon());
                                                            cv.put("time_begin", expandedList.getTime_begin());
                                                            cv.put("status_upload", "0");

                                                            if (!expandedList.getPicture1().equals("")) {
                                                                cv.put("picture1", expandedList.getPicture1());
                                                            }
                                                            if (!expandedList.getPicture2().equals("")) {
                                                                cv.put("picture2", expandedList.getPicture2());
                                                            }
                                                            if (!expandedList.getPicture3().equals("")) {
                                                                cv.put("picture3", expandedList.getPicture3());
                                                            }
                                                            if (!expandedList.getComment().equals("")) {
                                                                cv.put("comment", expandedList.getComment());
                                                            }
                                                            cv.put("modified_date", getdate());
                                                            databaseHelper.db().update("Plan", cv, "delivery_no= '" + expandedList.getDelivery_no() + "' and plan_seq = '" + expandedList.getPlan_seq() + "' and activity_type = 'UNLOAD' and " +
                                                                    " consignment_no = '" + expandedList.getConsignment() + "' and box_no = '" + expandedList.getBox_no() + "' and trash = '0'", null);

                                                            if (!expandedList.getPicture1().equals("")) {
                                                                String sql = "INSERT INTO image (name_img, status_img) VALUES('" + expandedList.getPicture1() + "','0')";
                                                                databaseHelper.db().execSQL(sql);
                                                            }
                                                            if (!expandedList.getPicture2().equals("")) {
                                                                String sql = "INSERT INTO image (name_img, status_img) VALUES('" + expandedList.getPicture2() + "','0')";
                                                                databaseHelper.db().execSQL(sql);
                                                            }
                                                            if (!expandedList.getPicture3().equals("")) {
                                                                String sql = "INSERT INTO image (name_img, status_img) VALUES('" + expandedList.getPicture3() + "','0')";
                                                                databaseHelper.db().execSQL(sql);
                                                            }

                                                            lastExpandedPosition = i;
                                                            IsSuccess = 1;
                                                        }
                                                    }
                                                    Temp1 = new ArrayList<>();
                                                    Temp2 = new ArrayList<>();
                                                    Temp3 = new ArrayList<>();
                                                } else {
                                                    Log.d("checkFail", "doInBackground: save fail");
                                                    IsSuccess = 0;
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

                                        pd.dismiss();

                                        if (IsSuccess == 1) {
                                            Toast.makeText(Deliver_Activity.this, "Saved.", Toast.LENGTH_SHORT).show();
                                            getSQLite();

                                        } else {
                                            if (positionGroup != -1) {
                                                expandableListView.smoothScrollToPosition(positionGroup);
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //expandableListView.setSelectedGroup(positionGroub);

                                                        expandableListView.expandGroup(positionGroup);
                                                    }
                                                }, 500);


                                            }
                                            Toast.makeText(Deliver_Activity.this, "can't save.", Toast.LENGTH_SHORT).show();
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

                String get_waybill = edtFineWaybillPick.getText().toString();


                if (INPUT_WAY.equals("PLUS")) {
                    for (int i = 0;
                         i < expandableListAdapter.getGroupCount(); i++) {
                        for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                            final DeliverExpand_Model expandedList = (DeliverExpand_Model) expandableListAdapter.getChild(i, j);

                            if (get_waybill.equals(expandedList.getWaybil_no())) {

                                lastPosition = i;

                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setIs_scaned("1");
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setTime_begin(getdate());
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setActual_lat(getlat());
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setActual_lon(getlon());
                                isc = false;
                                Toast.makeText(Deliver_Activity.this, "Checked.", Toast.LENGTH_SHORT).show();
                                expandableListView.setAdapter(expandableListAdapter);
                                expandableListView.expandGroup(i);
                                expandableListAdapter.notifyDataSetChanged();
                            } else {
                                // Toast.makeText(PinkingUpMaster_Activity.this, "This Waybill No doesn't exist.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                } else {
                    for (int i = 0;
                         i < expandableListAdapter.getGroupCount(); i++) {
                        for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                            final DeliverExpand_Model expandedList = (DeliverExpand_Model) expandableListAdapter.getChild(i, j);

                            if (get_waybill.equals(expandedList.getWaybil_no())) {

                                lastPosition = i;

                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setIs_scaned("0");
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setTime_begin("");
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setActual_lat("");
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setActual_lon("");
                                isc = false;
                                Toast.makeText(Deliver_Activity.this, "Un Check.", Toast.LENGTH_SHORT).show();
                                expandableListView.setAdapter(expandableListAdapter);
                                expandableListView.expandGroup(i);
                                expandableListAdapter.notifyDataSetChanged();
                            } else {
                                //  Toast.makeText(PinkingUpMaster_Activity.this, "This Waybill No doesn't exist.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }


            }
        });


    }

    private String getlat() {

        String lat = "";
        if (locationTrack.canGetLocation()) {


            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            lat = String.valueOf(latitude);

            //  Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {

            locationTrack.showSettingsAlert();
        }

        return lat;
    }

    private String getlon() {

        String lon = "";
        if (locationTrack.canGetLocation()) {


            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            lon = String.valueOf(longitude);

            //  Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {

            locationTrack.showSettingsAlert();
        }

        return lon;
    }

    private String getdate() {

        String temp = "";
        String pattern = "yyyy-MM-dd kk:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
        temp = sdf.format(Calendar.getInstance().getTime());

        return temp;
    }

    private void onClickFab() {

        fabHome.setOnClickListener(v -> {
            if (layoutJobHome.getVisibility() == View.VISIBLE && layoutJobToday.getVisibility()
                    == View.VISIBLE) {
                hideAll();
            } else {
                showAll();

            }
        });

        fabJobHome.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PlanWork_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        fabJobToday.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Main_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        });

    }

    private void hideAll() {

        layoutJobHome.startAnimation(hideLayout);
        layoutJobToday.startAnimation(hideLayout);
        layoutJobHome.setVisibility(View.GONE);
        layoutJobToday.setVisibility(View.GONE);

    }

    private void showAll() {

        layoutJobHome.startAnimation(showLayout);
        layoutJobToday.startAnimation(showLayout);
        layoutJobHome.setVisibility(View.VISIBLE);
        layoutJobToday.setVisibility(View.VISIBLE);

    }

    private int[] isCheckSaveBox(DeliverAdapter expandableListAdapter) {

        int[] position = new int[2];
        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
            expandableListAdapter.getChildrenCount(i);
            int countScanned = 0;
            position[0] = 1;
            position[1] = -1;
            for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                final DeliverExpand_Model expandedList = (DeliverExpand_Model) expandableListAdapter.getChild(i, j);

                String scanned = expandedList.getIs_scaned();

                Log.d("checkFail", "isCheckSaveBox: " + scanned);

                if (scanned.equals("1") || scanned.equals("2")) {
                    countScanned++;
                }

            }

            if (expandableListAdapter.getChildrenCount(i) > 0 && expandableListAdapter.getChildrenCount(i) != countScanned && countScanned > 0) {
                //  errorSaveFail(i);
                position[0] = 0;
                position[1] = i;

                return position;

            }

        }
        return position;
    }


    private void checkBackCon() {

        for (int i = 0; i < Temp1.size(); i++) {
            Log.d("fsdlfjks", "checkBackCon: pic1: " + Temp1.get(i));
            File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + Temp1.get(i));
            file.delete();

        }
        Temp1 = new ArrayList<>();

        for (int i = 0; i < Temp2.size(); i++) {
            Log.d("fsdlfjks", "checkBackCon: pic2: " + Temp2.get(i));

            File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + Temp2.get(i));
            file.delete();

        }
        Temp2 = new ArrayList<>();

        for (int i = 0; i < Temp3.size(); i++) {
            Log.d("fsdlfjks", "checkBackCon: pic3: " + Temp3.get(i));

            File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + Temp3.get(i));
            file.delete();

        }
        Temp3 = new ArrayList<>();
    }

    private void getSQLite() {
        final SharedPreferences user_data = getSharedPreferences("DATA_DETAIL_DELI", Context.MODE_PRIVATE);
        String delivery_no = user_data.getString("delivery_no", "");
        String plan_seq = user_data.getString("plan_seq", "");

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
                "where pl.delivery_no = '" + delivery_no + "' and  pl.plan_seq = '" + plan_seq + "' and pl.activity_type = 'UNLOAD' and pl.trash = '0' and pl.order_no in (select order_no from pic_sign where pic_sign_load <> '' )" +
                "GROUP BY pl.delivery_no, pl.consignment_no";
        Cursor cursor = databaseHelper.selectDB(sql);
        Log.d("DeliverLOG", "total line " + cursor.getCount());

        expandableListDetail = new HashMap<>();
        list = new ArrayList<>();

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                String consignment = cursor.getString(cursor.getColumnIndex("consignment"));
                String box_total = cursor.getString(cursor.getColumnIndex("box_total"));
                String box_checked = cursor.getString(cursor.getColumnIndex("box_checked"));
                String station_address = cursor.getString(cursor.getColumnIndex("station_address"));
                String pay_type = cursor.getString(cursor.getColumnIndex("pay_type"));
                String global_total = cursor.getString(cursor.getColumnIndex("global_total"));
                String global_cancel = cursor.getString(cursor.getColumnIndex("global_cancel"));
                String price = cursor.getString(cursor.getColumnIndex("price"));

                Log.d("PickingUpLOG", "onCreate: " + "==>" + consignment + "==>" + box_total + "==>" + box_checked);

                list.add(new Deliver_Model(consignment, box_total, box_checked, global_total, station_address, pay_type, global_cancel, price));


                String sql_expand = "select delivery_no, plan_seq, box_no, waybill_no, is_scaned, comment, picture1, picture2, picture3, (box_no - 1)+1 as row_number from Plan where consignment_no = '" + consignment + "' and  activity_type = 'UNLOAD' and trash = '0' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "' ";
                Cursor cursor_expand = databaseHelper.selectDB(sql_expand);
                Log.d("PickingUpLOG", "total line " + cursor_expand.getCount());

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
                        String comment = cursor_expand.getString(cursor_expand.getColumnIndex("comment"));
                        String picture1 = cursor_expand.getString(cursor_expand.getColumnIndex("picture1"));
                        String picture2 = cursor_expand.getString(cursor_expand.getColumnIndex("picture2"));
                        String picture3 = cursor_expand.getString(cursor_expand.getColumnIndex("picture3"));

                        list_expand.add(new DeliverExpand_Model(box_no, waybill_no, is_scaned, row_number, consignment, delivery_no2, plan_seq2, comment, picture1, picture2, picture3));
                    } while (cursor_expand.moveToNext());
                }


                expandableListDetail.put(consignment, list_expand);
            } while (cursor.moveToNext());
        }

        expandableListAdapter = new DeliverAdapter(this, list, expandableListDetail);
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

                // edtFineWaybillPick.setText(result.getContents());

                if (INPUT_WAY.equals("PLUS")) {
                    for (int i = 0;
                         i < expandableListAdapter.getGroupCount(); i++) {
                        for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                            final DeliverExpand_Model expandedList = (DeliverExpand_Model) expandableListAdapter.getChild(i, j);

                            if (getScanText.equals(expandedList.getWaybil_no())) {

                                lastPosition = i;

                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setInto("1");
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setIs_scaned("1");
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setTime_begin(getdate());
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setActual_lat(getlat());
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setActual_lon(getlon());
                                isc = true;


                                Toast.makeText(Deliver_Activity.this, "Checked.", Toast.LENGTH_SHORT).show();
                                expandableListView.setAdapter(expandableListAdapter);
                                expandableListView.expandGroup(i);
                                expandableListAdapter.notifyDataSetChanged();
                                expandableListView.smoothScrollToPosition(i);
                            } else {
                                // Toast.makeText(PinkingUpMaster_Activity.this, "This Waybill No doesn't exist.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                } else {
                    for (int i = 0;
                         i < expandableListAdapter.getGroupCount(); i++) {
                        for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                            final DeliverExpand_Model expandedList = (DeliverExpand_Model) expandableListAdapter.getChild(i, j);

                            if (getScanText.equals(expandedList.getWaybil_no())) {

                                lastPosition = i;

                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setInto("0");
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setIs_scaned("0");
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setTime_begin("");
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setActual_lat("");
                                ((DeliverExpand_Model) expandableListAdapter.getChild(i, j)).setActual_lon("");
                                isc = true;

                                Toast.makeText(Deliver_Activity.this, "Un Check.", Toast.LENGTH_SHORT).show();
                                expandableListView.setAdapter(expandableListAdapter);
                                expandableListView.expandGroup(i);
                                expandableListAdapter.notifyDataSetChanged();
                                expandableListView.smoothScrollToPosition(i);
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

            final Uri imageUri = Uri.parse(currentPhotoPath);
            final File file = new File(imageUri.getPath());

            try {
                ResizeImages(currentPhotoPath);
                InputStream ims = new FileInputStream(file);
                imgCommentPick_01.setImageBitmap(BitmapFactory.decodeStream(ims));
                Log.d("getPatha", "onActivityResult: " + imageUri.getPath() + "=>01" + file.getPath() + "=>02" + file.getName());

                arrayNameImage[0] = file.getName();

                if (file.getName() != null) {
                    picTemp1.add(file.getName());
                }


                picture1 = file.getName();

            } catch (FileNotFoundException e) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }


            imgDeletePick01.setVisibility(View.GONE);

        }

        if (requestCode == IMAGE_02 && resultCode == Activity.RESULT_OK) {

            deleteImage.add("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picture2);

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
    public class DeliverAdapter extends BaseExpandableListAdapter {

        View popupInputDialogView = null;
        View popupInputDialogView2 = null;

        private final Context context;
        private final ArrayList<Deliver_Model> list;
        private final HashMap<String, ArrayList<DeliverExpand_Model>> expandableListDetail;

        public DeliverAdapter(Context context, ArrayList<Deliver_Model> list, HashMap<String, ArrayList<DeliverExpand_Model>> expandableListDetail) {
            this.context = context;
            this.list = list;
            this.expandableListDetail = expandableListDetail;
        }

        @Override
        public Object getChild(int listPosition, int expandedListPosition) {
            return this.expandableListDetail.get(this.list.get(listPosition).getConsignment_no())
                    .get(expandedListPosition);


        }

        private void updateChill(String key, int position, DeliverExpand_Model model, int positionGroup) {
            this.expandableListDetail.get(key)
                    .get(position);

            ArrayList<DeliverExpand_Model> expand_models = this.expandableListDetail.get(key);
            expand_models.set(position, model);

            this.expandableListDetail.put(key, expand_models);

            notifyDataSetChanged();
            expandableListView.expandGroup(positionGroup);


        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final DeliverExpand_Model expandedList = (DeliverExpand_Model) getChild(listPosition, expandedListPosition);

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_expanditemline, null);
            }

            final CheckBox checkBox;
            final TextView box_no, waybill_no, tvExpand_Count;
            final ImageView imgEditBoxNoPickup;

            box_no = convertView.findViewById(R.id.tvExpand_Box);
            waybill_no = convertView.findViewById(R.id.tvExpand_waybill_no);
            tvExpand_Count = convertView.findViewById(R.id.tvExpand_Count);
            checkBox = convertView.findViewById(R.id.cbExpand_isscaned);
            imgEditBoxNoPickup = convertView.findViewById(R.id.imgEditBoxNoPickup);

            Log.d("sdfgjhkasdfuiol", "getChildView: " + expandedList.getIs_scaned());

            checkBox.setEnabled(false);

            tvExpand_Count.setText(String.valueOf((expandedListPosition + 1)));
            box_no.setText("BoxNo. " + expandedList.getBox_no());
            waybill_no.setText("WaybillNo: " + expandedList.getWaybil_no());

            if (!checkBox.isChecked() && !expandedList.getIs_scaned().equals("1")) {
                imgEditBoxNoPickup.setEnabled(true);
            } else {
                imgEditBoxNoPickup.setEnabled(false);
            }

            if (expandedList.getIs_scaned().equals("0")) {
                imgEditBoxNoPickup.setEnabled(true);
            }

            if (expandedList.getIs_scaned().equals("2")) {
                checkBox.setChecked(true);
                imgEditBoxNoPickup.setEnabled(true);
               // checkBox.setEnabled(false);
                checkBox.setButtonDrawable(R.drawable.ic_indeterminate_check_box_black_24dp);

            } else {
              //  checkBox.setEnabled(true);
                checkBox.setButtonDrawable(R.drawable.custom_checkbox);
            }

            if (expandedList.getIs_scaned().equals("1")) {
                checkBox.setChecked(true);

            }

            if (isc) {
                if (expandedList.getInto().equals("0")) {
                    expandedList.setIs_scaned("0");
                    if (expandedList.getIs_scaned().equals("0")) {
                        checkBox.setChecked(false);
                        imgEditBoxNoPickup.setEnabled(true);
                        isc = false;
                    }
                } else {
                    expandedList.setIs_scaned("1");
                    expandedList.setTime_begin(getdate());

                    // Log.d("sdgfjkashdfsdf", "getChildView: "+ LocationLatLon.getLastLocation());
                    expandedList.setActual_lon(getlat());
                    expandedList.setActual_lon(getlon());

                    if (expandedList.getIs_scaned().equals("1")) {
                        checkBox.setChecked(true);
                        isc = false;
                        //  checkBox.setEnabled(false);
                        //              checkBox.setButtonDrawable(R.drawable.ic_check_box_disable);
                    }
                }
            } else {

            }





//            checkBox.setOnClickListener(v -> {
//                if (((CheckBox) v).isChecked()) {
//                    imgEditBoxNoPickup.setEnabled(false);
//                    expandedList.setIs_scaned("1");
//                    expandedList.setTime_begin(getdate());
//                    expandedList.setActual_lat(getlat());
//                    expandedList.setActual_lon(getlon());
//
//
//                } else {
//
//                    imgEditBoxNoPickup.setEnabled(true);
//                    expandedList.setIs_scaned("0");
//                    expandedList.setTime_begin("");
//                    expandedList.setActual_lat("");
//                    expandedList.setActual_lon("");
//                }
//
//            });

            imgEditBoxNoPickup.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                imgEditBoxNoPickup.startAnimation(animation);

                // showDialogBox(expandedList.getBox_no(), expandedList.getConsignment(), expandedList.getDelivery_no(), expandedList.getPlan_seq(), listPosition, expandedList, expandedListPosition);
                showDialogBox(expandedList, listPosition, expandedListPosition);
            });


            return convertView;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return this.expandableListDetail.get(this.list.get(listPosition).getConsignment_no())
                    .size();
        }

        @Override
        public Object getGroup(int listPosition) {
            return this.list.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            return this.list.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(final int listPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {

            final Deliver_Model listTitle = (Deliver_Model) getGroup(listPosition);

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_expandworkline, null);
            }

            final ImageView swichExpand = convertView.findViewById(R.id.swichExpand);
            final ImageView imgDetailConsignNo = convertView.findViewById(R.id.imgDetailConsignNo);

            ImageView imageView9 = convertView.findViewById(R.id.imageView9);
            TextView textView24 = convertView.findViewById(R.id.textView24);
            ImageView pick_pay_type = convertView.findViewById(R.id.pick_pay_type);
            TextView textView25 = convertView.findViewById(R.id.textView25);
            TextView tv_Global_cancel = convertView.findViewById(R.id.tv_Global_cancel);


            TextView consignment = convertView.findViewById(R.id.tvPickUp_Consignment);
            TextView textView26 = convertView.findViewById(R.id.textView26);
            consignment.setTypeface(null, Typeface.BOLD);
            consignment.setText("Cons.No: " + listTitle.getConsignment_no());
            textView26.setText(String.valueOf((listPosition + 1)));

            TextView tvPickUp_global = convertView.findViewById(R.id.tvPickUp_global);
            TextView box = convertView.findViewById(R.id.tvPickingUp_Box);


            if(!listTitle.getBox_checked().equals("0")){
                consignment.setTextColor(Color.parseColor("#1D781F"));
                box.setTextColor(Color.parseColor("#1D781F"));
                tvPickUp_global.setTextColor(Color.parseColor("#1D781F"));
                textView24.setTextColor(Color.parseColor("#1D781F"));
                textView26.setTextColor(Color.parseColor("#1D781F"));
            }else{
                consignment.setTextColor(Color.parseColor("#696969"));
                consignment.setTextColor(Color.parseColor("#696969"));
                box.setTextColor(Color.parseColor("#696969"));
                tvPickUp_global.setTextColor(Color.parseColor("#9C9C9C"));
                textView24.setTextColor(Color.parseColor("#696969"));
                textView26.setTextColor(Color.parseColor("#696969"));
            }

            box.setText("Box (" + listTitle.getBox_checked() + " | " + listTitle.getBox_total() + ")");

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
                        //lastData = listTitle.getConsignment();
                        swichExpand.setImageResource(R.drawable.jobon);
                    }
                    Log.d("Afsfss", "onClick: " + lastPosition);
                    notifyDataSetChanged();

                }
            });

            if (lastPosition == listPosition) {
                SWICH_EXPAND = "ON";
                swichExpand.setImageResource(R.drawable.jobon);
                //lastData = listTitle.getConsignment();
            } else {
                SWICH_EXPAND = "OFF";
                swichExpand.setImageResource(R.drawable.joboff);
            }

            Log.d("AsfWVsdGweg", "getGroupView: " + listTitle.getConsignment_no() + ">" + listTitle.getPaytype());

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
                textView24.setText(listTitle.getPaytype());
            }

            imgDetailConsignNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                    imgDetailConsignNo.startAnimation(animation);
                    dialogDetail(listTitle.getConsignment_no(), listTitle.getStation_address(), listTitle.getPaytype()
                            , listTitle.getBox_total(), listTitle.getGlobal_total(), listTitle.getPrice());
                }
            });


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

            final TabHost mTabHost;
            final TabWidget tabs;
            TextView tvConsignment_con_dialog;

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setCancelable(false);

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            popupInputDialogView2 = layoutInflater.inflate(R.layout.con_dialog_detail, null);

            alertDialogBuilder.setView(popupInputDialogView2);
            alertDialog2 = alertDialogBuilder.create();


            imgClose_dialog = popupInputDialogView2.findViewById(R.id.imgClose_dialog);
            mTabHost = popupInputDialogView2.findViewById(android.R.id.tabhost);
            tabs = popupInputDialogView2.findViewById(android.R.id.tabs);
            tvConsignment_con_dialog = popupInputDialogView2.findViewById(R.id.tvConsignment_con_dialog);

            TextView pick_dialog_station_address = popupInputDialogView2.findViewById(R.id.tv_dialog_station_address);
            ImageView pick_dialog_pay_type = popupInputDialogView2.findViewById(R.id.pick_dialog_pay_type);
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
                pick_dialog_pay_type_credit.setText(pay_type);
            }


            imgClose_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                    imgClose_dialog.startAnimation(animation);
                    alertDialog2.dismiss();
                }
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

        private void showDialogBox(DeliverExpand_Model picking1, int positionGroup, int positionChill) {

            DeliverExpand_Model picking = (DeliverExpand_Model) getChild(positionGroup, positionChill);

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
            TextView textView32 = popupInputDialogView.findViewById(R.id.textView32);
            TextView textView33 = popupInputDialogView.findViewById(R.id.textView33);

            tvConsignment_Dialog.setText(getApplicationContext().getString(R.string.consignment2) + ": " + picking.getConsignment());
            tv_BoxNo_Dialog.setText(getApplicationContext().getString(R.string.box_no) + ": " + picking.getBox_no());
            textView32.setText(getApplicationContext().getString(R.string.reason) + ":");
            textView33.setText(getApplicationContext().getString(R.string.picture) + ":");
            // DeliverExpand_Model picking = (DeliverExpand_Model) getChild(position, pos);

            Log.d("Asfjklassdf", "showDialogBox: " + picking.getPicture1() + ">" + picking.getPicture2() + ">" + picking.getPicture3());

            List<String> categories = new ArrayList<>();
            categories.add("File");
            categories.add("Edit");
            categories.add("View");
            categories.add("Navigate");
            categories.add("Code");
            categories.add("Analyze");
            categories.add("Refactor");
            categories.add("Build");


            ArrayList<Reason_model> reasonModels = new ArrayList<>();
            ArrayList<String> valueSpinner = new ArrayList<>();

            String sql_expand = "select name from reason";
            Cursor cursor_expand = databaseHelper.selectDB(sql_expand);

            cursor_expand.moveToFirst();
            if (cursor_expand.getCount() > 0) {
                do {
                    String name = cursor_expand.getString(cursor_expand.getColumnIndex("name"));
                    reasonModels.add(new Reason_model("", name));

                } while (cursor_expand.moveToNext());
            }

            for (int i = 0; i < reasonModels.size(); i++) {
                valueSpinner.add(reasonModels.get(i).getName());
            }


            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, valueSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            MaterialSpinner spinner = popupInputDialogView.findViewById(R.id.spinner);
            spinner.setAdapter(adapter);

            if (!picking.getComment().equals("")) {
                int spinnerPosition = adapter.getPosition(picking.getComment());
                spinner.setSelection(spinnerPosition + 1);

            }

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i != -1) {
                        commentOfspinner = adapterView.getItemAtPosition(i).toString();
                    } else {
                        commentOfspinner = "";
                    }
                    Log.d("ASdasdasd", "onClick: " + commentOfspinner);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            imgClose_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                    imgClose_dialog.startAnimation(animation);
                    data_intent.edit().clear();

                    for (int i = 0; i < picTemp1.size(); i++) {
                        Log.d("Djar", "onClick: " + picTemp1.get(i));

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
                    // expandableListView.expandGroup(position);
                }
            });


            btnSaveComent_PICK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // String commentText = edtComment_PICK.getText().toString();

                    Log.d("ASdasdasd", "onClick: " + commentOfspinner);

                    for (int i = 0; i < deleteImage.size(); i++) {

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
                                    Temp1.add(path);
                                    picking.setPicture1(path);

                                }
                                break;
                            case 1:
                                if (!path.equals("")) {
                                    cv.put("picture2", path);
                                    Temp2.add(path);
                                    picking.setPicture2(path);
                                }
                                break;
                            case 2:
                                if (!path.equals("")) {
                                    cv.put("picture3", path);
                                    Temp3.add(path);
                                    picking.setPicture3(path);
                                }
                                break;
                        }

                        index++;
                    }

                    if (!picture1.equals("") || !picture2.equals("") || !picture3.equals("") || !commentOfspinner.equals("")) {
                        cv.put("is_scaned", "2");
                        cv.put("comment", commentOfspinner);

                        picking.setComment(commentOfspinner);
                        picking.setIs_scaned("2");
                        picking.setTime_begin(getdate());
                        picking.setActual_lat(getlat());
                        picking.setActual_lon(getlon());
                    } else {
                        cv.put("comment", "");
                        cv.put("is_scaned", "0");

                        picking.setComment("");
                        picking.setIs_scaned("0");
                        picking.setTime_begin("");
                        picking.setActual_lat("");
                        picking.setActual_lon("");
                    }

//                    cv.put("modified_date", getdate());
//                    databaseHelper.db().update("Plan", cv, "delivery_no= '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and activity_type = 'UNLOAD' and " +
//                            " consignment_no = '" + consignment_no + "' and box_no = '" + box_no + "' and trash = '0'", null);
//
//                    Log.d("pathString", "onClick: " + delivery_no + "--" + plan_seq + "--" + consignment_no + "--" + box_no);
//
//                    ContentValues cv2 = new ContentValues();
//                    for (String path : arrayNameImage) {
//                        if (!path.equals("")) {
//                            cv2.put("name_img", path);
//                            cv2.put("status_img", "0");
//                            databaseHelper.db().insert("image", null, cv2);
//                        }
//                    }

                    updateChill(picking.getConsignment(), positionChill, picking, positionGroup);

                    //                   expandableListView.setAdapter(expandableListAdapter);


//                    getSQLite();
//                    expandableListView.expandGroup(position,true);
//                    expandableListView.smoothScrollToPosition(position);
                    alertDialog.dismiss();
                    Toast.makeText(Deliver_Activity.this, "Saved.", Toast.LENGTH_SHORT).show();

                }
            });


//**************************************************************************************************
//            String sql = "select comment, picture1 from Plan where activity_type = 'UNLOAD' and trash = '0' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "' " +
//                    "and box_no = '" + box_no + "' and consignment_no = '" + consignment_no + "'";
//            Cursor cursor = databaseHelper.selectDB(sql);
//            cursor.moveToFirst();
//            if (cursor.getCount() > 0) {
//                do {
//
//                    String comment = cursor.getString(cursor.getColumnIndex("comment"));
//                    picture1 = cursor.getString(cursor.getColumnIndex("picture1"));
//                    if (comment != null) {
//                        int spinnerPosition = adapter.getPosition(comment);
//                        spinner.setSelection(spinnerPosition + 1);
//                    }
            //edtComment_PICK.setText(comment);

            Log.d("dffgsd", "showDialogBox:getPicture: " + picking.getPicture1() + ">" + picking.getPicture2() + ">" + picking.getPicture3() + "\n" + picture1 + ">" + picture2 + ">" + picture3);

            picture1 = picking.getPicture1();

            if (!picture1.equals("")) {
                arrayNameImage[0] = picking.getPicture1();
                picTemp1.add(picture1);
                File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picture1);
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imgCommentPick_01.setImageBitmap(myBitmap);
                imgCommentPick_01.setEnabled(false);

                imgNewPick01.setVisibility(View.VISIBLE);
                imgDeletePick01.setVisibility(View.GONE);

                imgNewPick01.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                        imgNewPick01.startAnimation(animation);
                        data_intent.edit().putString("box_no", picking.getBox_no()).apply();
                        data_intent.edit().putString("consignment_no", picking.getConsignment()).apply();
                        data_intent.edit().putString("delivery_no", picking.getDelivery_no()).apply();
                        data_intent.edit().putString("plan_seq", picking.getPlan_seq()).apply();

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

                                        ContentValues cv = new ContentValues();
                                        cv.putNull("picture1");
                                        cv.put("modified_date", getdate());
                                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + picking.getDelivery_no() + "' and plan_seq = '" + picking.getPlan_seq() + "' and activity_type = 'UNLOAD' and " +
                                                " consignment_no = '" + picking.getConsignment() + "' and box_no = '" + picking.getBox_no() + "' and trash = '0'", null);

                                        databaseHelper.db().delete("image", "name_img=?", new String[]{picture1});

                                        imgCommentPick_01.setImageResource(R.mipmap.add_photo);
                                        imgNewPick01.setVisibility(View.GONE);
                                        imgDeletePick01.setVisibility(View.GONE);
                                        imgCommentPick_01.setEnabled(true);
                                        Toast.makeText(Deliver_Activity.this, "Successfully deleted.", Toast.LENGTH_SHORT).show();

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
                arrayNameImage[0] = "";
            }


//                } while (cursor.moveToNext());
//
//            }
//**************************************************************************************************

//            String sql02 = "select comment, picture2 from Plan where activity_type = 'UNLOAD' and trash = '0' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "' " +
//                    "and box_no = '" + box_no + "' and consignment_no = '" + consignment_no + "'";
//            Cursor cursor02 = databaseHelper.selectDB(sql02);
//
//            cursor02.moveToFirst();
//            if (cursor02.getCount() > 0) {
//                do {
//
//                    String comment = cursor02.getString(cursor02.getColumnIndex("comment"));
//                    picture2 = cursor02.getString(cursor02.getColumnIndex("picture2"));
//                    if (comment != null) {
//                        int spinnerPosition = adapter.getPosition(comment);
//                        spinner.setSelection(spinnerPosition + 1);
//                    }
            // edtComment_PICK.setText(comment);


            picture2 = picking.getPicture2();

            if (!picture2.equals("")) {
                arrayNameImage[1] = picking.getPicture2();
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
                        data_intent.edit().putString("box_no", picking.getBox_no()).apply();
                        data_intent.edit().putString("consignment_no", picking.getConsignment()).apply();
                        data_intent.edit().putString("delivery_no", picking.getDelivery_no()).apply();
                        data_intent.edit().putString("plan_seq", picking.getPlan_seq()).apply();

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
                                        cv.putNull("picture2");
                                        cv.put("modified_date", getdate());
                                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + picking.getDelivery_no() + "' and plan_seq = '" + picking.getPlan_seq() + "' and activity_type = 'UNLOAD' and " +
                                                " consignment_no = '" + picking.getConsignment() + "' and box_no = '" + picking.getBox_no() + "' and trash = '0'", null);

                                        databaseHelper.db().delete("image", "name_img=?", new String[]{picture2});

                                        imgCommentPick_02.setImageResource(R.mipmap.add_photo);
                                        imgNewPick02.setVisibility(View.GONE);
                                        imgDeletePick02.setVisibility(View.GONE);
                                        imgCommentPick_02.setEnabled(true);
                                        Toast.makeText(Deliver_Activity.this, "Successfully deleted.", Toast.LENGTH_SHORT).show();

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
                arrayNameImage[1] = "";
            }


//                } while (cursor.moveToNext());
//
//            }

//**************************************************************************************************

//            String sql03 = "select comment, picture3 from Plan where activity_type = 'UNLOAD' and trash = '0' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "' " +
//                    "and box_no = '" + box_no + "' and consignment_no = '" + consignment_no + "'";
//            Cursor cursor03 = databaseHelper.selectDB(sql03);
//
//            cursor03.moveToFirst();
//            if (cursor03.getCount() > 0) {
//                do {
//
//                    String comment = cursor03.getString(cursor03.getColumnIndex("comment"));
//                    picture3 = cursor03.getString(cursor03.getColumnIndex("picture3"));
//                    if (comment != null) {
//                        int spinnerPosition = adapter.getPosition(comment);
//                        spinner.setSelection(spinnerPosition + 1);
//                    }
            //edtComment_PICK.setText(comment);


            picture3 = picking.getPicture3();

            if (!picture3.equals("")) {
                arrayNameImage[2] = picking.getPicture3();
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
                        data_intent.edit().putString("box_no", picking.getBox_no()).apply();
                        data_intent.edit().putString("consignment_no", picking.getConsignment()).apply();
                        data_intent.edit().putString("delivery_no", picking.getDelivery_no()).apply();
                        data_intent.edit().putString("plan_seq", picking.getPlan_seq()).apply();

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
                                        cv.putNull("picture3");
                                        cv.put("modified_date", getdate());
                                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + picking.getDelivery_no() + "' and plan_seq = '" + picking.getPlan_seq() + "' and activity_type = 'UNLOAD' and " +
                                                " consignment_no = '" + picking.getConsignment() + "' and box_no = '" + picking.getBox_no() + "' and trash = '0'", null);

                                        databaseHelper.db().delete("image", "name_img=?", new String[]{picture3});

                                        imgCommentPick_03.setImageResource(R.mipmap.add_photo);
                                        imgNewPick03.setVisibility(View.GONE);
                                        imgDeletePick03.setVisibility(View.GONE);
                                        imgCommentPick_03.setEnabled(true);
                                        Toast.makeText(Deliver_Activity.this, "Successfully deleted.", Toast.LENGTH_SHORT).show();

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
                arrayNameImage[2] = "";
            }

            Log.d("dffgsd", "showDialogBox:getPicture2: " + picking.getPicture1() + ">" + picking.getPicture2() + ">" + picking.getPicture3() + "\n" + picture1 + ">" + picture2 + ">" + picture3);

//                } while (cursor.moveToNext());
//
//            }

            imgCommentPick_01.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    data_intent.edit().putString("box_no", picking.getBox_no()).apply();
                    data_intent.edit().putString("consignment_no", picking.getConsignment()).apply();
                    data_intent.edit().putString("delivery_no", picking.getDelivery_no()).apply();
                    data_intent.edit().putString("plan_seq", picking.getPlan_seq()).apply();

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

                    data_intent.edit().putString("box_no", picking.getBox_no()).apply();
                    data_intent.edit().putString("consignment_no", picking.getConsignment()).apply();
                    data_intent.edit().putString("delivery_no", picking.getDelivery_no()).apply();
                    data_intent.edit().putString("plan_seq", picking.getPlan_seq()).apply();

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

                    data_intent.edit().putString("box_no", picking.getBox_no()).apply();
                    data_intent.edit().putString("consignment_no", picking.getConsignment()).apply();
                    data_intent.edit().putString("delivery_no", picking.getDelivery_no()).apply();
                    data_intent.edit().putString("plan_seq", picking.getPlan_seq()).apply();

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
//                public void onClick(View view) {
//
//                    ฝฝString commentText = edtComment_PICK.getText().toString();
//
//                    if (commentOfspinner.matches("")) {
//                        ContentValues cv = new ContentValues();
//                        cv.putNull("comment");
//                        cv.put("is_scaned", "0");
//                        cv.put("modified_date", getDate);
//                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and activity_type = 'UNLOAD' and " +
//                                " consignment_no = '" + consignment_no + "' and box_no = '" + box_no + "' and trash = '0'", null);
//                        alertDialog.dismiss();
//                        getSQLite();
//                        Toast.makeText(Deliver_Activity.this, "Saved.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        ContentValues cv = new ContentValues();
//                        cv.put("comment", commentText);
//                        cv.put("is_scaned", "2");
//                        cv.put("modified_date", getDate);
//                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and activity_type = 'UNLOAD' and " +
//                                " consignment_no = '" + consignment_no + "' and box_no = '" + box_no + "' and trash = '0'", null);
//                        alertDialog.dismiss();
//                        getSQLite();
//                        Toast.makeText(Deliver_Activity.this, "Saved.", Toast.LENGTH_SHORT).show();
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
        File storageDir = getExternalFilesDir("Pictures");
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
