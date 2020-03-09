package ws.epod.scan.view.pickup;

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
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ws.epod.BuildConfig;
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import fr.ganfra.materialspinner.MaterialSpinner;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ws.epod.Adapter.DialogConsAdapter;
import ws.epod.Client.APIClient;
import ws.epod.Client.APIInterface;
import ws.epod.Client.Structors.UploadImage;
import ws.epod.Client.Structors.UploadImageInvoice;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.SQLiteModel.WaybillModel;
import ws.epod.signature.pickup.Invoice_Activity;
import ws.epod.Main_Activity;
import ws.epod.ObjectClass.LanguageClass;
import ws.epod.ObjectClass.LocationTrack;
import ws.epod.ObjectClass.SQLiteModel.Dialog_Cons_Detail_Model;
import ws.epod.ObjectClass.SQLiteModel.PickingUpEexpand_Model;
import ws.epod.ObjectClass.SQLiteModel.PickingUp_Model;
import ws.epod.ObjectClass.SQLiteModel.Reason_model;
import ws.epod.ObjectClass.Var;
import ws.epod.PlanWork_Activity;
import ws.epod.R;
import ws.epod.scan.Util.UtilScan;
import ws.epod.scan.model.pickup.Invoice;
import ws.epod.sync.UploadDataPlan;

public class Pickup_Activity extends AppCompatActivity {

    ExpandableListView expandableListView;
    PickingUpAdapter expandableListAdapter;

    ZXingScannerView scannerView;
    private ProgressDialog progressDialog;

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

    ImageView imgClose_dialog, imgCommentPick_01, imgCommentPick_02, imgCommentPick_03, imgBack_test, fabSync,
            imgNewPick01, imgDeletePick01, imgNewPick02, imgDeletePick02, imgNewPick03, imgDeletePick03, imageView8, imgCameraScan, savePickingUp;
    EditText edtComment_PICK, edtFineWaybillPick;
    Button btnSaveComent_PICK;


    AlertDialog alertDialog;
    AlertDialog alertDialog2;

    TextView bnCloseJobPick, btnEnterWaybillNo;

    String INPUT_WAY = "CHECK";
    String SWICH_EXPAND = "OFF";
    int lastPosition = 0;
    String lastData = "";

    private FusedLocationProviderClient client;


    String picture1 = "";
    String picture2 = "";
    String picture3 = "";

    String commentOfspinner = "";

    String[] arrayNameImage = new String[3];

    ArrayList<Integer> posiGroup = new ArrayList();
    ArrayList<Integer> posiChile = new ArrayList();


    ArrayList<String> picTemp1 = new ArrayList<>();
    ArrayList<String> picTemp2 = new ArrayList<>();
    ArrayList<String> picTemp3 = new ArrayList<>();
    ArrayList<String> deleteImage = new ArrayList<>();

    ArrayList<String> Temp1 = new ArrayList<>();
    ArrayList<String> Temp2 = new ArrayList<>();
    ArrayList<String> Temp3 = new ArrayList<>();

    // Sync
    private APIInterface apiInterface;
    private String encodedImagePic1;
    private String encodedImagePic2;
    private String encodedImagePic3;
    private String encodedImageInvoice;

    RecyclerView rvDialogCons;
    DialogConsAdapter dialogConsAdapter;

    private int ch_list = 0;
    boolean isSave = true;
    boolean isSync = false;

    ArrayList<String> imWaibill = new ArrayList<>();

    LocationTrack locationTrack;

    private IntentIntegrator qrScan;

    FloatingActionButton fabHome, fabJobHome, fabJobToday;
    Animation showLayout, hideLayout;
    LinearLayout layoutJobHome, layoutJobToday;

    boolean isc = false;

    private int statusComment = 0;
    private int isComment = 0;
    private int statusCheck = 0;

    private UploadDataPlan uploadDataPlan;
    private ZXingScannerView mScannerView;

    int arrayIsScan = 0;
    //private LocationManager client;

    String issueScan = "";

    private Runnable delayScan = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    private Toolbar mTopToolbar;


    @Override
    protected void onResume() {
        super.onResume();
        //getSQLite();


        Log.d("sdasd63sd", "onCreate: ");
        if (UtilScan.getListWaybill() != null) {
            for (Invoice waybill : UtilScan.getListWaybill()) {
                Log.d("sdasd63sd", "onCreate: " + waybill.getWaybill_no());

                scan(waybill.getWaybill_no(), "", "", "");
            }

        }

        // Upload();

//        scannerView.setResultHandler(new ZXingScannerResultHandler());
        // Start camera on resume
//        scannerView.startCamera();
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
        setContentView(R.layout.activity_pickup);


        narisv = new NarisBaseValue(Pickup_Activity.this);
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());
        uploadDataPlan = new UploadDataPlan(Pickup_Activity.this);

        // progressDialog = new ProgressDialog(getApplicationContext());

        arrayNameImage[0] = "";
        arrayNameImage[1] = "";
        arrayNameImage[2] = "";

        qrScan = new IntentIntegrator(this);

        locationTrack = new LocationTrack(Pickup_Activity.this);


        imageView8 = findViewById(R.id.imageView8);
        savePickingUp = findViewById(R.id.savePickingUp);
        imgBack_test = findViewById(R.id.imgBack_test);
        edtFineWaybillPick = findViewById(R.id.edtFineWaybillPick);
        imgCameraScan = findViewById(R.id.imgCameraScan);
        btnEnterWaybillNo = findViewById(R.id.btnEnterWaybillNo);
        bnCloseJobPick = findViewById(R.id.bnCloseJobPick);

        fabHome = findViewById(R.id.fabHome3);
        layoutJobHome = findViewById(R.id.layoutJobHome);
        layoutJobToday = findViewById(R.id.layoutJobToday);
        fabJobHome = findViewById(R.id.fabJobHome);
        fabJobToday = findViewById(R.id.fabJobToday);
        fabSync = findViewById(R.id.fabSync);
        expandableListView = findViewById(R.id.exPandDeli);


        mTopToolbar = findViewById(R.id.toolbar_test);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        showLayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_layout);
        hideLayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_layout);


        if (getIntent().getExtras() != null) {
            isSync = getIntent().getExtras().getBoolean("isSync");
            if (isSync) {
                UtilScan.clearHeaderWaybillList();

                Upload();
                getSQLite();


//                if(UtilScan.getListWaybill() != null){
//                    for (Invoice waybill : UtilScan.getListWaybill()){
//                        Log.d("sdasd63sd", "onCreate: "+waybill.getWaybill_no());
//
//                        scan(waybill.getWaybill_no());
//                    }
//
//                }
            } else {

            }
        }


        onClickFab();


        bnCloseJobPick.setOnClickListener(view -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
            bnCloseJobPick.startAnimation(animation);

            saveCheckConFirm();

//            Intent intent = new Intent(getApplicationContext(), Invoice_Activity.class);
//            startActivity(intent);
        });

        imgCameraScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                imgCameraScan.startAnimation(animation);

                // startScan();


                UtilScan.clearHeaderWaybillList();
                Intent intents = new Intent(Pickup_Activity.this, ScanPickUpActivity.class);
                intents.putExtra("key", INPUT_WAY);
                startActivity(intents);
            }
        });

        imgBack_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                imgBack_test.startAnimation(animation);
                checkBackCon();
                finish();
            }
        });


        savePickingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                savePickingUp.startAnimation(animation);

                isSave();

            }
        });

        edtFineWaybillPick.requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(edtFineWaybillPick, InputMethodManager.SHOW_FORCED);


        edtFineWaybillPick.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            scan(edtFineWaybillPick.getText().toString(), "", "", "");
                            edtFineWaybillPick.setText("");
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        imageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (INPUT_WAY.equals("CHECK")) {
                    INPUT_WAY = "COMMENT";
                    imageView8.setImageResource(R.drawable.ic_indeterminate_check_box_black_24dp);
                } else if (INPUT_WAY.equals("COMMENT")) {
                    INPUT_WAY = "UNCHECK";
                    imageView8.setImageResource(R.drawable.ic_check_box_uncheck);
                } else if (INPUT_WAY.equals("UNCHECK")) {
                    INPUT_WAY = "CHECK";
                    imageView8.setImageResource(R.drawable.ic_check_box_checked);
                }
            }
        });

        btnEnterWaybillNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                btnEnterWaybillNo.startAnimation(animation);

                String getScanText = edtFineWaybillPick.getText().toString();
                scan(getScanText, "", "", "");
                edtFineWaybillPick.setText("");


                // edtFineWaybillPick.setText("");


            }
        });

        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        Log.d("Asfas5f", "The RecyclerView is not scrolling");
                        fabHome.setVisibility(View.VISIBLE);
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        Log.d("Asfas5f", "Scrolling now");
                        hideAll();
                        fabHome.setVisibility(View.GONE);
                        break;
                    case SCROLL_STATE_FLING:
                        Log.d("Asfas5f", "Scroll Settling");
                        hideAll();
                        fabHome.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pick_and_deli_menu, menu);

        MenuItem item_sync = menu.findItem(R.id.item_sync);
        MenuItem item_confirm = menu.findItem(R.id.item_confirm);
        MenuItem item_import_waybill = menu.findItem(R.id.item_import_waybill);

        if (item_sync != null) {
            tintMenuIcon(Pickup_Activity.this, item_sync, android.R.color.background_dark);
            tintMenuIcon(Pickup_Activity.this, item_confirm, android.R.color.background_dark);
            tintMenuIcon(Pickup_Activity.this, item_import_waybill, android.R.color.background_dark);
        }


        return true;
    }

    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));
        item.setIcon(wrapDrawable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.item_confirm) {
            saveCheckConFirm();
            return true;
        } else if (id == R.id.item_sync) {
            hideAll();
            saveCheck();
            return true;
        } else if (id == R.id.item_import_waybill) {
            imWaibill = new ArrayList<>();
            import_waybill();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void import_waybill() {


        final SharedPreferences user_data = getSharedPreferences("DATA_DETAIL_PICK", Context.MODE_PRIVATE);
        final String delivery_no = user_data.getString("delivery_no", "");
        final String plan_seq = user_data.getString("plan_seq", "");

        String sql = "select id,waybill_no,date_scan,lat,lon,is_scanned,status_complete from header_waybill where status_complete = '0'";
        Cursor cursor = databaseHelper.selectDB(sql);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {

                String waybill_no = cursor.getString(cursor.getColumnIndex("waybill_no"));
                String date_scan = cursor.getString(cursor.getColumnIndex("date_scan"));
                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                double lon = cursor.getDouble(cursor.getColumnIndex("lon"));

                String lat_ = String.valueOf(lat);
                String lon_ = String.valueOf(lon);

                scan(waybill_no, date_scan, lat_, lon_);


                /*String select_plan = "select \n" +
                        "(select DISTINCT pl2.consignment_no from Plan pl2 where pl2.activity_type = pl.activity_type and pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.consignment_no = pl.consignment_no and pl2.trash = pl.trash) as consignment \n" +
                        "from Plan pl\n" +
                        "inner join consignment cm on cm.consignment_no = pl.consignment_no \n" +
                        "where pl.delivery_no = '" + delivery_no + "' and  pl.plan_seq = '" + plan_seq + "' and pl.activity_type = 'LOAD' and pl.trash = '0' " +
                        "GROUP BY pl.delivery_no, pl.consignment_no";
                Cursor cursor_plan = databaseHelper.selectDB(select_plan);

                cursor_plan.moveToFirst();
                if (cursor_plan.getCount() > 0) {
                    do {

                        String consignment = cursor_plan.getString(cursor_plan.getColumnIndex("consignment"));
                        String select_plan2 = "select pl.waybill_no  \n" +
                                ", pl.is_scaned  \n" +
                                ", ROW_NUMBER() OVER(ORDER BY pl.box_no) as row_number   \n" +
                                "  from Plan pl where pl.consignment_no = '" + consignment + "' and pl.activity_type = 'LOAD' and pl.delivery_no = '" + delivery_no + "' and pl.plan_seq = '" + plan_seq + "' and pl.trash = '0'   " +
                                "order by row_number";
                        Cursor cursor_plan2 = databaseHelper.selectDB(select_plan2);

                        cursor_plan2.moveToFirst();
                        if (cursor_plan2.getCount() > 0) {
                            do {
                                String waybill_no_ = cursor_plan2.getString(cursor_plan2.getColumnIndex("waybill_no"));
                                String is_scaned = cursor_plan2.getString(cursor_plan2.getColumnIndex("is_scaned"));
                                String row_number = cursor_plan2.getString(cursor_plan2.getColumnIndex("row_number"));

                                if (waybill_no.equals(waybill_no_)) {

                                    ContentValues cv = new ContentValues();
                                    cv.put("status_complete", "1");
                                    databaseHelper.db().update("header_waybill", cv, "waybill_no = '" + waybill_no_ + "'", null);

                                }
                            } while (cursor_plan2.moveToNext());
                        }


                    } while (cursor_plan.moveToNext());
                }
*/

            } while (cursor.moveToNext());
        }

        Toasty.success(getApplicationContext(), "Checked " + imWaibill.size()+ " Waybill!", Toast.LENGTH_SHORT, true).show();


    }


    private void startScan() {
        qrScan.setPrompt("Scan a barcode or qr code");
        qrScan.setOrientationLocked(false);
        qrScan.setBeepEnabled(false);
        qrScan.initiateScan();

    }

    private boolean checkTotalScan() {


        ArrayList<String> total = new ArrayList<>();
        int sum = 0;
        int sum_scan = 0;
        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
            PickingUp_Model groupView = (PickingUp_Model) expandableListAdapter.getGroup(i);

            sum = sum + groupView.getTotal_b();
            Log.d("dfsdfwee", "con : " + groupView.getConsignment() + " total: " + groupView.getTotal_b());

            for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                PickingUpEexpand_Model childView = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                if (!childView.getIs_scaned().equals("0")) {
                    total.add(childView.getIs_scaned());
                }


            }

        }
        sum_scan = sum_scan + total.size();

        Log.d("dfsdfwee", "Is Scan: " + sum_scan);
        Log.d("dfsdfwee", "checkTotalScan: " + sum);

        if (sum_scan != sum) {
            return false;
        } else {
            return true;
        }
    }

    private void isSave() {
        statusComment = 0;
        isComment = 0;

        int positionGroup = 0;
        int positionChiew = 0;


        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
            PickingUp_Model groupView = (PickingUp_Model) expandableListAdapter.getGroup(i);
            for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                PickingUpEexpand_Model childView = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                if (childView.getIs_scaned().equals("2")) {
                    statusComment += 1;
                    if (!childView.getComment().equals("") || !childView.getPicture1().equals("") ||
                            !childView.getPicture2().equals("") || !childView.getPicture3().equals("")) {
                        isComment += 1;
                    } else {
                        positionGroup = i;
                        positionChiew = j;


                    }
                }
                if (childView.getIs_scaned().equals("1")) {
                    statusCheck = +1;
                }


            }

        }

        if (isCheckIntent(statusComment, isComment, statusCheck)) {

            final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
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
                                    pd = new ProgressDialog(Pickup_Activity.this);
                                    pd.setCancelable(false);
                                    pd.setMessage("Saving data..");
                                    pd.show();

                                }

                                @Override
                                protected Void doInBackground(Void... voids) {

                                    isSave = false;

                                    try {
                                        if (expandableListAdapter == null) {
                                            cancel(true);
                                        } else {
                                            Log.d("dsjspodgl", "doInBackground: 22222");

                                            // int[] position = isCheckSaveBox(expandableListAdapter);
                                            // positionGroup = position[1];
                                            // if (position[0] == 1) {
                                            Log.d("checkFail", "doInBackground: save");
                                            for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                                                expandableListAdapter.getChildrenCount(i);
                                                final PickingUp_Model picking = (PickingUp_Model) expandableListAdapter.getGroup(i);
                                                for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                                                    final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                                                    Log.d("ASfasdjhfk", "doInBackground: " + expandedList.getConsignment() + "box : " + expandedList.getBox_no() + " status: " +
                                                            expandedList.getIs_scaned() + " date: " + expandedList.getTime_begin() + "lat: " + expandedList.getActual_lat() + "lon: " +
                                                            expandedList.getActual_lon());

                                                    Log.d("ASfasdjhfk", "p1: " + expandedList.getPicture1() + " p2: " + expandedList.getPicture2() + " p3: " + expandedList.getPicture3());


                                                    ContentValues cv = new ContentValues();

                                                    cv.put("is_scaned", expandedList.getIs_scaned());
                                                    cv.put("actual_lat", expandedList.getActual_lat());
                                                    cv.put("actual_lon", expandedList.getActual_lon());
                                                    cv.put("time_begin", expandedList.getTime_begin());
                                                    if (expandedList.getIs_save().equals("2")) {
                                                        expandedList.setIs_save("1");
                                                        cv.put("is_save", expandedList.getIs_save());
                                                    }

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
                                                    databaseHelper.db().update("Plan", cv, "delivery_no= '" + expandedList.getDelivery_no() + "' and plan_seq = '" + expandedList.getPlan_seq() + "' and activity_type = 'LOAD' and " +
                                                            " consignment_no = '" + expandedList.getConsignment() + "' and waybill_no = '" + expandedList.getWaybil_no() + "' and trash = '0'", null);


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
                                                    //  databaseHelper.db().insert("image", null, cv2);


                                                    lastExpandedPosition = i;
                                                    IsSuccess = 1;
                                                }
                                            }

                                            Temp1 = new ArrayList<>();
                                            Temp2 = new ArrayList<>();
                                            Temp3 = new ArrayList<>();
//                                            } else {
//                                                // Toast.makeText(PinkingUpMaster_Activity.this, "fail.", Toast.LENGTH_SHORT).show();
//                                                Log.d("checkFail", "doInBackground: save fail");
//                                                IsSuccess = 0;
//                                            }


                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        IsSuccess = 0;
                                    }


                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                    isSave = true;

                                    pd.dismiss();

                                    if (IsSuccess == 1) {
                                        Toast.makeText(Pickup_Activity.this, "Saved.", Toast.LENGTH_SHORT).show();
                                        getSQLite();
                                        updateWaybill();


                                    } else {
                                        if (positionGroup != -1) {
                                            expandableListView.smoothScrollToPosition(positionGroup);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //expandableListView.setSelectedGroup(positionGroub);
//                                                        expandableListView.setAdapter(expandableListAdapter);
//                                                        expandableListAdapter.notifyDataSetChanged();
//                                                        expandableListView.smoothScrollToPositionFromTop(positionGroup, j);
                                                    expandableListView.expandGroup(positionGroup);
                                                }
                                            }, 500);


                                        }
                                        Toast.makeText(Pickup_Activity.this, "can't save.", Toast.LENGTH_SHORT).show();
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
        } else {


            expandableListView.setAdapter(expandableListAdapter);
            expandableListView.expandGroup(positionGroup);
            expandableListAdapter.notifyDataSetChanged();
            expandableListView.smoothScrollToPositionFromTop(positionGroup, positionChiew);


            Toasty.error(getApplicationContext(), "Please reason!", Toast.LENGTH_SHORT, true).show();
            return;
        }
    }

    private void updateWaybill() {

        final SharedPreferences user_data = getSharedPreferences("DATA_DETAIL_PICK", Context.MODE_PRIVATE);
        final String delivery_no = user_data.getString("delivery_no", "");
        final String plan_seq = user_data.getString("plan_seq", "");

        String sql = "select id,waybill_no,date_scan,lat,lon,is_scanned,status_complete from header_waybill where status_complete = '0'";
        Cursor cursor = databaseHelper.selectDB(sql);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {

                String waybill_no = cursor.getString(cursor.getColumnIndex("waybill_no"));
                String date_scan = cursor.getString(cursor.getColumnIndex("date_scan"));
                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                double lon = cursor.getDouble(cursor.getColumnIndex("lon"));

                String lat_ = String.valueOf(lat);
                String lon_ = String.valueOf(lon);

                //scan(waybill_no, date_scan, lat_, lon_);

                String select_plan = "select \n" +
                        "(select DISTINCT pl2.consignment_no from Plan pl2 where pl2.activity_type = pl.activity_type and pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.consignment_no = pl.consignment_no and pl2.trash = pl.trash) as consignment \n" +
                        "from Plan pl\n" +
                        "inner join consignment cm on cm.consignment_no = pl.consignment_no \n" +
                        "where pl.delivery_no = '" + delivery_no + "' and  pl.plan_seq = '" + plan_seq + "' and pl.activity_type = 'LOAD' and pl.trash = '0' " +
                        "GROUP BY pl.delivery_no, pl.consignment_no";
                Cursor cursor_plan = databaseHelper.selectDB(select_plan);

                cursor_plan.moveToFirst();
                if (cursor_plan.getCount() > 0) {
                    do {

                        String consignment = cursor_plan.getString(cursor_plan.getColumnIndex("consignment"));
                        String select_plan2 = "select pl.waybill_no  \n" +
                                ", pl.is_scaned  \n" +
                                ", ROW_NUMBER() OVER(ORDER BY pl.box_no) as row_number   \n" +
                                "  from Plan pl where pl.consignment_no = '" + consignment + "' and pl.activity_type = 'LOAD' and pl.delivery_no = '" + delivery_no + "' and pl.plan_seq = '" + plan_seq + "' and pl.trash = '0'   " +
                                "order by row_number";
                        Cursor cursor_plan2 = databaseHelper.selectDB(select_plan2);

                        cursor_plan2.moveToFirst();
                        if (cursor_plan2.getCount() > 0) {
                            do {
                                String waybill_no_ = cursor_plan2.getString(cursor_plan2.getColumnIndex("waybill_no"));
                                String is_scaned = cursor_plan2.getString(cursor_plan2.getColumnIndex("is_scaned"));
                                String row_number = cursor_plan2.getString(cursor_plan2.getColumnIndex("row_number"));

                                if (waybill_no.equals(waybill_no_)) {

                                    ContentValues cv = new ContentValues();
                                    cv.put("status_complete", "1");
                                    databaseHelper.db().update("header_waybill", cv, "waybill_no = '" + waybill_no_ + "'", null);

                                }
                            } while (cursor_plan2.moveToNext());
                        }


                    } while (cursor_plan.moveToNext());
                }


            } while (cursor.moveToNext());
        }
    }

    private boolean isCheckIntent(int statusComment, int isComment, int statusCheck) {

        if (statusComment != 0 || isComment != 0 || statusCheck != 0) {
            if (statusComment != 0) {
                if (statusComment == isComment) {

                } else {
                    //Toast.makeText(Invoice_Activity.this, "Please comment return.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (statusCheck != 0) {
            }
        } else {
            // Toasty.error(getApplicationContext(), "Please reason!", Toast.LENGTH_SHORT, true).show();
            return true;
        }

        return true;
    }

    private void scan(String value, String date, String lat, String lon) {
        boolean scannotFind = false;

        int num = 0;
        Log.d("s6s3d5", "scan: 1");
        if (INPUT_WAY.equals("CHECK")) {

            for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                // expandableListView.expandGroup(i);
                final PickingUp_Model listTitle = (PickingUp_Model) expandableListAdapter.getGroup(i);
                ArrayList<String> count_ = new ArrayList<>();
                for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                    final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                    //   if (expandedList.getIs_save().equals("0") || expandedList.getIs_save().equals("2")) {
                    if (expandedList.getIs_scaned().equals("0") || expandedList.getIs_scaned().equals("2")) {
                        if (value.equals(expandedList.getWaybil_no())) {

                            scannotFind = true;

                            if (!expandedList.getOrder_no().equals("")) {
                                Toasty.info(getApplicationContext(), "Please un sign this order.", Toast.LENGTH_SHORT, true).show();
                            } else {
                                // if (!expandedList.getIs_scaned().equals("2")) {

                                if (listTitle.getCount() >= 0) {
                                    Log.d("llsmf66", "num: " + num + "getnum: " + listTitle.getNum());

                                    int count = listTitle.getCount() + 1;
                                    listTitle.setCount(count);

                                    int c1 = Integer.parseInt(listTitle.getBox_checked());
                                    num = c1 + listTitle.getCount();

                                    if (expandedList.getIs_scaned().equals("2")) {
                                        num = num - 1;
                                    }

                                    listTitle.setNum(num);
//
                                }
                                //  }

                                //ถ้า date ไม่ว่าง คือ type import waybill
                                if (!date.equals("")) {
                                    imWaibill.add(value);
                                    expandedList.setIs_scaned("1");
                                    expandedList.setTime_begin(date);
                                    expandedList.setActual_lat(lat);
                                    expandedList.setActual_lon(lon);
                                    expandedList.setIs_save("2");
                                    expandedList.setComment("");
                                    expandedList.setPicture1("");
                                    expandedList.setPicture2("");
                                    expandedList.setPicture3("");
                                } else {
                                    expandedList.setIs_scaned("1");
                                    expandedList.setTime_begin(getdate());
                                    expandedList.setActual_lat(getlat());
                                    expandedList.setActual_lon(getlon());
                                    expandedList.setIs_save("2");
                                    expandedList.setComment("");
                                    expandedList.setPicture1("");
                                    expandedList.setPicture2("");
                                    expandedList.setPicture3("");
                                    Toasty.success(getApplicationContext(), "Checked!", Toast.LENGTH_SHORT, true).show();
                                }


                                Log.d("Asjkljkksdf", "(1)scan: " + getdate() + " lat:" + getlat() + " lon:" + getlon() + " scan:" + expandedList.getIs_scaned());




                                //ToastScan(icon,"Checked.");

                                expandableListView.setAdapter(expandableListAdapter);
                                expandableListView.expandGroup(i);
                                expandableListAdapter.notifyDataSetChanged();
                                expandableListView.smoothScrollToPositionFromTop(i, j);

                            }
                        } else {

                        }
                    } else {

                        if (value.equals(expandedList.getWaybil_no())) {
                            // ToastScan(null,"Scanned.");
                            scannotFind = true;
                            if (!expandedList.getOrder_no().equals("")) {
                                Toasty.info(getApplicationContext(), "Please un sign this order.", Toast.LENGTH_SHORT, true).show();
                            } else {
                                Toasty.info(getApplicationContext(), "Scanned.", Toast.LENGTH_SHORT, true).show();
                            }
                            //Toasty.info(getApplicationContext(), "Scanned.", Toast.LENGTH_SHORT, true).show();

                        }

                    }

                    if (expandedList.getIs_scaned().equals("1") || expandedList.getIs_scaned().equals("2")) {

                        count_.add(((PickingUpEexpand_Model) expandableListAdapter.getChild(i, j)).getIs_scaned());
                        for (int n = 0; n < count_.size(); n++) {
                            Log.d("asf3as69", "scan: " + String.valueOf(count_.get(n).length()));
                            Log.d("asf3as69", "scan:2 " + String.valueOf(count_.get(n)));
                            Log.d("asf3as69", "scan:3 " + String.valueOf(count_.size()));
                            listTitle.setBox_checked(String.valueOf(count_.size()));
                        }

                        expandableListView.setAdapter(expandableListAdapter);
                        //   expandableListView.expandGroup(i);
                        expandableListAdapter.notifyDataSetChanged();
                        expandableListView.smoothScrollToPositionFromTop(i, j);
                        Log.d("fjjpppsp", "scan: " + ((PickingUpEexpand_Model) expandableListAdapter.getChild(i, j)).getIs_scaned());
                    }
                    //   }

                }

            }


        } else if (INPUT_WAY.equals("UNCHECK")) {

            for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                //expandableListView.expandGroup(i);
                final PickingUp_Model listTitle = (PickingUp_Model) expandableListAdapter.getGroup(i);
                ArrayList<String> count_ = new ArrayList<>();
                for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                    final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                    //  if (expandedList.getIs_save().equals("0") || expandedList.getIs_save().equals("2")) {
                    // เป็น 1 หรือ 2
                    if (!expandedList.getIs_scaned().equals("0")) {
                        if (value.equals(expandedList.getWaybil_no())) {

                            scannotFind = true;

                            if (!expandedList.getOrder_no().equals("")) {
                                Toasty.info(getApplicationContext(), "Please un sign this order.", Toast.LENGTH_SHORT, true).show();
                            } else {

                                //  Log.d("sdfsdf","ก่อนหน้า"+ listTitle.getBox_checked());
                                if (!listTitle.getBox_checked().equals("0")) {
                                    // int count = listTitle.getCount() - 1;
                                    // int count2 = listTitle.getCount();
                                    Log.d("sadaaaa", "scan:ก่อน" + num);

                                    // num = num - 1;
                                    int count = listTitle.getCount() - 1;
                                    listTitle.setCount(count);

                                    int sum = listTitle.getNum() - 1;

                                    Log.d("sadaaaa", "scan:หลัง " + sum);


                                    //  int c1 = Integer.parseInt(listTitle.getBox_checked());


//                                    if (listTitle.getCount() <= 0) {
//                                    } else {
//                                        listTitle.setCount(count);
//                                    }
                                    listTitle.setNum(sum);
//                                    Log.d("sdfsdf", listTitle.getCount() + "");
//                                    listTitle.setBox_checked(String.valueOf(listTitle.getCount()));
                                }

                                // Log.d("sdfsdf", listTitle.getBox_checked());

                                expandedList.setIs_scaned("0");
                                expandedList.setTime_begin("");
                                expandedList.setActual_lat("");
                                expandedList.setActual_lon("");
                                expandedList.setIs_save("0");
                                expandedList.setComment("");
                                expandedList.setPicture1("");
                                expandedList.setPicture2("");
                                expandedList.setPicture3("");

                                Toasty.success(getApplicationContext(), "Un Check!", Toast.LENGTH_SHORT, true).show();

                                expandableListView.setAdapter(expandableListAdapter);
                                expandableListView.expandGroup(i);
                                expandableListAdapter.notifyDataSetChanged();
                                expandableListView.smoothScrollToPositionFromTop(i, j);
                            }
                        } else {
                        }
                    } else {

                        if (value.equals(expandedList.getWaybil_no())) {
                            scannotFind = true;

                            Toasty.info(getApplicationContext(), "Un scan.", Toast.LENGTH_SHORT, true).show();

                        }

                        //toastScan("Change the lower button to scan.");
                    }
                    //   }

                    if (expandedList.getIs_scaned().equals("1") || expandedList.getIs_scaned().equals("2")) {
                        // count_ = new ArrayList<>();
                        count_.add(((PickingUpEexpand_Model) expandableListAdapter.getChild(i, j)).getIs_scaned() + " waybill: " + expandedList.getWaybil_no());
                        listTitle.setBox_checked(String.valueOf(count_.size()));

                        Log.d("size array", "scan: " + count_.size());
                        expandableListView.setAdapter(expandableListAdapter);
                        // expandableListView.expandGroup(i);
                        expandableListAdapter.notifyDataSetChanged();
                        expandableListView.smoothScrollToPositionFromTop(i, j);
                        Log.d("fjjpppsp", "scan: " + ((PickingUpEexpand_Model) expandableListAdapter.getChild(i, j)).getIs_scaned());
                    } else if (expandedList.getIs_scaned().equals("0")) {

                        Log.d("size array", "scan: " + count_.size());
                        listTitle.setBox_checked(String.valueOf(count_.size()));

                        expandableListView.setAdapter(expandableListAdapter);
                        // expandableListView.expandGroup(i);
                        expandableListAdapter.notifyDataSetChanged();
                        expandableListView.smoothScrollToPositionFromTop(i, j);
                    }


                }


            }
        }
        if (INPUT_WAY.equals("COMMENT")) {

            for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                final PickingUp_Model listTitle = (PickingUp_Model) expandableListAdapter.getGroup(i);
                ArrayList<String> count_ = new ArrayList<>();
                for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                    final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                    // if (expandedList.getIs_save().equals("0") || expandedList.getIs_save().equals("2")) {
                    if (((PickingUpEexpand_Model) expandableListAdapter.getChild(i, j)).getIs_scaned().equals("0")
                            || ((PickingUpEexpand_Model) expandableListAdapter.getChild(i, j)).getIs_scaned().equals("1")) {
                        if (value.equals(expandedList.getWaybil_no())) {

                            scannotFind = true;
                            // lastPosition = i;
                            if (!expandedList.getOrder_no().equals("")) {
                                Toasty.info(getApplicationContext(), "P lease un sign this order.", Toast.LENGTH_SHORT, true).show();
                            } else {

                                // if (!expandedList.getIs_scaned().equals("2")) {
                                int sum = listTitle.getNum();
//                                if (listTitle.getNum() > 0) {
//                                    sum = listTitle.getNum() + 1;
//                                }
                                //   num += 1;

                                if (expandedList.getIs_scaned().equals("1")) {
                                    sum = listTitle.getNum();
                                }
                                if (expandedList.getIs_scaned().equals("0")) {
                                    Log.d("fl5s5", "scan: ......");
                                    int count = listTitle.getCount() + 1;
                                    listTitle.setCount(count);

                                    int c1 = Integer.parseInt(listTitle.getBox_checked());
                                    num = c1 + listTitle.getCount();

                                    sum = num;
                                }


//                                    int count = listTitle.getCount() + 1;
//                                    listTitle.setCount(count);
//                                    listTitle.setBox_checked(String.valueOf(listTitle.getCount()));
                                //  }

                                listTitle.setNum(sum);

                                expandedList.setIs_scaned("2");
                                expandedList.setTime_begin(getdate());
                                expandedList.setActual_lat(getlat());
                                expandedList.setActual_lon(getlon());
                                expandedList.setIs_save("2");

                                Log.d("Asjkljkksdf", "(2)scan: " + getdate() + " lat:" + getlat() + " lon:" + getlon());

                                Toasty.success(getApplicationContext(), "Please comment!", Toast.LENGTH_SHORT, true).show();

                                expandableListView.setAdapter(expandableListAdapter);
                                expandableListView.expandGroup(i);
                                expandableListAdapter.notifyDataSetChanged();
                                expandableListView.smoothScrollToPositionFromTop(i, j);
                            }
                        } else {

                        }
                    } else {
                        if (value.equals(expandedList.getWaybil_no())) {
                            scannotFind = true;
                            if (!expandedList.getOrder_no().equals("")) {
                                Toasty.info(getApplicationContext(), "Please un sign this order.", Toast.LENGTH_SHORT, true).show();
                            } else {
                                Toasty.info(getApplicationContext(), "Scanned.", Toast.LENGTH_SHORT, true).show();
                            }
                            //Toasty.info(getApplicationContext(), "Scanned.", Toast.LENGTH_SHORT, true).show();
                        }

                    }
                    //}

                    if (expandedList.getIs_scaned().equals("1") || expandedList.getIs_scaned().equals("2")) {
                        //  count_ = new ArrayList<>();
                        count_.add(((PickingUpEexpand_Model) expandableListAdapter.getChild(i, j)).getIs_scaned());

                        listTitle.setBox_checked(String.valueOf(count_.size()));

                        expandableListView.setAdapter(expandableListAdapter);
                        // expandableListView.expandGroup(i);
                        expandableListAdapter.notifyDataSetChanged();
                        expandableListView.smoothScrollToPositionFromTop(i, j);
                        Log.d("fjjpppsp", "scan: " + ((PickingUpEexpand_Model) expandableListAdapter.getChild(i, j)).getIs_scaned());
                    }
                }

            }
        }//comment


        if (!scannotFind) {
            issueScan = "This Waybill No doesn't exist.";
            if(date.equals("")){
                Toasty.info(getApplicationContext(), "This Waybill No doesn't exist.", Toast.LENGTH_SHORT, true).show();
               //Toasty.success(getApplicationContext(), "Checked "+ imWaibill.size() +" Waybill.", Toast.LENGTH_SHORT, true).show();
            }
        }



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
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));

        if (String.valueOf(sdf).length() > 3) {
            temp = sdf.format(Calendar.getInstance().getTime());
        } else {
            Calendar c = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            temp = df.format(c.getTime());
        }


        return temp;
    }

    private int[] isCheckSaveBox(PickingUpAdapter expandableListAdapter) {

        int[] position = new int[2];
        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
            expandableListAdapter.getChildrenCount(i);
            int countScanned = 0;
            position[0] = 1;
            position[1] = -1;
            for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                String scanned = expandedList.getIs_scaned();

                Log.d("checkFail", "isCheckSaveBox: " + expandedList.getComment() + ">" + expandedList.getIs_scaned() + ">" + expandedList.getWaybil_no() + " image >" + expandedList.getPicture1());

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

        fabSync.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
            fabSync.startAnimation(animation);

            hideAll();
            saveCheck();

        });

    }

    @SuppressLint("StaticFieldLeak")
    private void saveCheckConFirm() {
        boolean isSaved = true;

        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
            PickingUp_Model groupView = (PickingUp_Model) expandableListAdapter.getGroup(i);
            for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                PickingUpEexpand_Model childView = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                if (childView.getIs_save().equals("2")) {
                    isSaved = false;
                }
            }

        }


        if (isSaved) {
            if (checkTotalScan()) {
                Intent intent = new Intent(getApplicationContext(), Invoice_Activity.class);
                startActivity(intent);
                // Toast.makeText(getApplicationContext(), "ไปได้", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "No enter.", Toast.LENGTH_SHORT).show();
            }
            // Toast.makeText(getApplicationContext(), "saved.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Please save job.", Toast.LENGTH_SHORT).show();
        }

    }


    @SuppressLint("StaticFieldLeak")
    private void saveCheck() {
        boolean isSaved = true;

        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
            PickingUp_Model groupView = (PickingUp_Model) expandableListAdapter.getGroup(i);
            for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                PickingUpEexpand_Model childView = (PickingUpEexpand_Model) expandableListAdapter.getChild(i, j);

                if (childView.getIs_save().equals("2")) {
                    isSaved = false;
                }
            }

        }


        if (isSaved) {

            Upload();

            // Toast.makeText(getApplicationContext(), "saved.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Please save job.", Toast.LENGTH_SHORT).show();
        }

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


//    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler {
//
//        @Override
//        public void handleResult( Result result ) {
//
//            String resultCode = result.getText();
//
//
//            //setContentView(R.layout.activity_pickup);
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
        final SharedPreferences user_data = getSharedPreferences("DATA_DETAIL_PICK", Context.MODE_PRIVATE);
        final String delivery_no = user_data.getString("delivery_no", "");
        final String plan_seq = user_data.getString("plan_seq", "");

        String sql = "select (select DISTINCT pl2.consignment_no from Plan pl2 where pl2.activity_type = pl.activity_type and pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.consignment_no = pl.consignment_no and pl2.trash = pl.trash) as consignment\n" +
                ",(select count(pl2.box_no) from Plan pl2 where pl2.activity_type = pl.activity_type and pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.consignment_no = pl.consignment_no and pl2.trash = pl.trash) as box_total\n" +
                ",(select count(pl2.waybill_no) from Plan pl2 where pl2.activity_type = pl.activity_type and pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.is_scaned <> '0' and pl2.consignment_no = pl.consignment_no and pl2.trash = pl.trash) as box_checked\n" +
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
                int total_b = cursor.getInt(cursor.getColumnIndex("total_b"));

                Log.d("PickingUpLOG", "onCreate: " + "==>" + global_cancel);

                list.add(new PickingUp_Model(consignment, box_total, box_checked, global_total, station_address, pay_type, global_cancel, price, total_b));


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
                        String time_begin = cursor_expand.getString(cursor_expand.getColumnIndex("time_begin"));
                        String is_save = cursor_expand.getString(cursor_expand.getColumnIndex("is_save"));
                        String status_upload = cursor_expand.getString(cursor_expand.getColumnIndex("status_upload"));
                        String order_no = cursor_expand.getString(cursor_expand.getColumnIndex("order_no"));

                        Log.d("Aslalllalal", "getSQLite: " + consignment + ">" + waybill_no + ">" + is_scaned);

                        list_expand.add(new PickingUpEexpand_Model(box_no, waybill_no, is_scaned, row_number, consignment, delivery_no2, plan_seq2, comment, picture1, picture2, picture3, time_begin, is_save, status_upload, order_no));
                    } while (cursor_expand.moveToNext());
                }


                expandableListDetail.put(consignment, list_expand);
            } while (cursor.moveToNext());
        }

        expandableListAdapter = new PickingUpAdapter(this, list, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
//        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++)
//            expandableListView.expandGroup(i);
        // user_data.edit().clear();
    }

    private void ToastScan(Bitmap bm, String v) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast_scan,
                (ViewGroup) findViewById(R.id.mylayout));

        Toast custToast = new Toast(this);
        TextView tv = view.findViewById(R.id.textView1);
        ImageView img = view.findViewById(R.id.imageview);
        if (bm != null) {
            Log.d("dsdg", "ToastScan: notnull");
            img.setImageBitmap(bm);
        } else {
            //img.setImageBitmap(bm);
            Log.d("dsdg", "ToastScan: null");
        }
        tv.setText(v);
        custToast.setView(view);
        custToast.show();
    }


    private void ResizeImages(String sPath) throws IOException {

        Bitmap photo = BitmapFactory.decodeFile(sPath);
        // photo = Bitmap.createScaledBitmap(photo, 300, 300, false);

        int width = photo.getWidth();
        int height = photo.getHeight();

        int rotate = 0;

        try {
            ExifInterface exif = new ExifInterface(sPath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);


            Bitmap resizedBitmap = Bitmap.createBitmap(photo, 0, 0, width, height, matrix, true);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

            File file = new File(sPath);
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();

        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_warning__scan, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        Button btnCloseDialog = dialogView.findViewById(R.id.btnCloseDialog);

        AlertDialog alertDialog = dialogBuilder.create();
        btnCloseDialog.setOnClickListener(v -> {
            alertDialog.dismiss();
            issueScan = "";
            new Handler().postDelayed(delayScan, 500);
        });

        alertDialog.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // valueLoop = true;

//        if(requestCode == 112){
//            if(resultCode == RESULT_OK){
//                String value = data.getStringExtra("SCAN_RESULT");
//                Log.d("Lgos6sdf14", "onActivityResult: "+value);
//
//                scan(value);
//            }
//        }


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result not found", Toast.LENGTH_SHORT).show();
            } else {
                String getScanText = result.getContents();
                getScanText = getScanText.trim();

                scan(getScanText, "", "", "");
                if (issueScan.equals("This Waybill No doesn't exist.")) {
                    showDialog();
                    // return;
                } else {
                    new Handler().postDelayed(delayScan, 1000);
                }


                // startScan();
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

        private void updateChill(String key, int position, PickingUpEexpand_Model model, int positionGroup) {
            this.expandableListDetail.get(key)
                    .get(position);

            ArrayList<PickingUpEexpand_Model> expand_models = this.expandableListDetail.get(key);
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
        public View getChildView(final int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final PickingUpEexpand_Model expandedList = (PickingUpEexpand_Model) getChild(listPosition, expandedListPosition);
            final PickingUp_Model listTitle = (PickingUp_Model) getGroup(listPosition);
            Log.d("aassas", "getChildView: " + listPosition + ">" + expandedListPosition + ">" + expandedList.getIs_scaned() + " into>" + expandedList.getInto());
            LanguageClass.setLanguage(getApplicationContext());

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_expanditemline, null);
            }

            Log.d("qwegwegsdg", "getGroupView: " + (expandedListPosition + 1));


            final TextView box_no, waybill_no, tvExpand_Count, textView29;
            final ImageView imgEditBoxNoPickup;

            box_no = convertView.findViewById(R.id.tvExpand_Box);
            waybill_no = convertView.findViewById(R.id.tvExpand_waybill_no);
            tvExpand_Count = convertView.findViewById(R.id.tvExpand_Count);
            CheckBox checkBox = convertView.findViewById(R.id.cbExpand_isscaned);
            imgEditBoxNoPickup = convertView.findViewById(R.id.imgEditBoxNoPickup);
            textView29 = convertView.findViewById(R.id.textView29);


            tvExpand_Count.setText(String.valueOf((expandedListPosition + 1)));
            box_no.setText(context.getString(R.string.box_no) + ": " + expandedList.getBox_no());
            waybill_no.setText(context.getString(R.string.waybill_no) + ": " + expandedList.getWaybil_no());

            // Log.d("aassas", "getChildView: " + listPosition + ">" + expandedListPosition + ">" + expandedList.getIs_scaned() + " into>" + expandedList.getInto());

            checkBox.setEnabled(false);

            Log.d("logIsScanned", "waybill: " + expandedList.getWaybil_no() + " scn: " + expandedList.getIs_save());

            if (expandedList.getIs_scaned().equals("1")) {
                checkBox.setChecked(true);
                imgEditBoxNoPickup.setEnabled(false);
                textView29.setVisibility(View.GONE);
                checkBox.setButtonDrawable(R.drawable.custom_checkbox);
            } else if (expandedList.getIs_scaned().equals("2")) {
                checkBox.setChecked(true);
                imgEditBoxNoPickup.setEnabled(true);
                checkBox.setButtonDrawable(R.drawable.ic_indeterminate_check_box_black_24dp);
                if (!expandedList.getComment().equals("") || !expandedList.getPicture1().equals("")
                        || !expandedList.getPicture2().equals("") || !expandedList.getPicture3().equals("")) {
                    textView29.setVisibility(View.GONE);
                } else {
                    textView29.setVisibility(View.VISIBLE);
                }
            } else if (expandedList.getIs_scaned().equals("0")) {
                textView29.setVisibility(View.GONE);
                checkBox.setChecked(false);
                imgEditBoxNoPickup.setEnabled(false);
                checkBox.setButtonDrawable(R.drawable.custom_checkbox);
                //imgEditBoxNoPickup.setEnabled(true);
            }


//            if (!checkBox.isChecked() && !expandedList.getIs_scaned().equals("1")) {
//                imgEditBoxNoPickup.setEnabled(true);
//            } else {
//                imgEditBoxNoPickup.setEnabled(false);
//            }
//
//            if (expandedList.getIs_scaned().equals("0")) {
//                imgEditBoxNoPickup.setEnabled(true);
//            }
//
//
//            if (expandedList.getIs_scaned().equals("2")) {
//                checkBox.setChecked(true);
//                imgEditBoxNoPickup.setEnabled(true);
//                //  checkBox.setEnabled(false);
//                checkBox.setButtonDrawable(R.drawable.ic_indeterminate_check_box_black_24dp);
//
//            } else {
//                //  checkBox.setEnabled(true);
//                checkBox.setButtonDrawable(R.drawable.custom_checkbox);
//            }
//
//            if (expandedList.getIs_scaned().equals("1")) {
//                checkBox.setChecked(true);
//                //  checkBox.setEnabled(false);
//                //     checkBox.setButtonDrawable(R.drawable.ic_check_box_disable);
//            }
//
//            if (isc) {
//                if (expandedList.getInto().equals("0")) {
//                    expandedList.setIs_scaned("0");
//                    if (expandedList.getIs_scaned().equals("0")) {
//                        checkBox.setChecked(false);
//                        imgEditBoxNoPickup.setEnabled(true);
//                        isc = false;
//                    }
//                } else {
//                    expandedList.setIs_scaned("1");
//                    expandedList.setTime_begin(getdate());
//
//                    // Log.d("sdgfjkashdfsdf", "getChildView: "+ LocationLatLon.getLastLocation());
//                    expandedList.setActual_lon(getlat());
//                    expandedList.setActual_lon(getlon());
//
//                    if (expandedList.getIs_scaned().equals("1")) {
//                        checkBox.setChecked(true);
//                        isc = false;
//                        //  checkBox.setEnabled(false);
//                        //              checkBox.setButtonDrawable(R.drawable.ic_check_box_disable);
//                    }
//                }
//            } else {
//
//            }


//            checkBox.setOnClickListener(v -> {
//                if (((CheckBox) v).isChecked()) {
//                    imgEditBoxNoPickup.setEnabled(false);
//                    expandedList.setIs_scaned("1");
//                    expandedList.setTime_begin(getdate());
//                    expandedList.setActual_lat(getlat());
//                    expandedList.setActual_lon(getlon());
//
//                } else {
//
//                    imgEditBoxNoPickup.setEnabled(true);
//                    expandedList.setIs_scaned("0");
//                    expandedList.setTime_begin("");
//                    expandedList.setActual_lat("");
//                    expandedList.setActual_lon("");
//
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
            LanguageClass.setLanguage(getApplicationContext());


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
            ImageView pick_pay_type = convertView.findViewById(R.id.pick_pay_type);
            TextView textView25 = convertView.findViewById(R.id.textView25);
            TextView tv_Global_cancel = convertView.findViewById(R.id.tv_Global_cancel);
            TextView tvTotal = convertView.findViewById(R.id.tvTotal);

            consignment.setTypeface(null, Typeface.BOLD);
            consignment.setText(context.getString(R.string.consignment2) + ": " + listTitle.getConsignment());
            tvConGroupCountPick.setText(String.valueOf((listPosition + 1)));
            TextView box = convertView.findViewById(R.id.tvPickingUp_Box);
            TextView tvPickUp_global = convertView.findViewById(R.id.tvPickUp_global);

            //  listTitle.setNum(Integer.parseInt(listTitle.getBox_checked()));
//            if (!listTitle.getBox_total().equals("0")) {
            box.setText(context.getString(R.string.box) + " (" + listTitle.getBox_checked() + " | " + listTitle.getBox_total() + ")");
//            } else {
//            if (listTitle.getBox_checked().equals("1")) {
//                box.setText(context.getString(R.string.boxes) + " (" + listTitle.getBox_checked() + " | " + listTitle.getBox_total() + ")");
//            } else {
//                box.setText(context.getString(R.string.boxes) + " (" + listTitle.getNum() + " | " + listTitle.getBox_total() + ")");
//            }

            //  }

            tvTotal.setText("Total: " + listTitle.getTotal_b());


            Log.d("boxScanTotal", "total: " + listTitle.getBox_checked() + " cons: " + listTitle.getConsignment());


            if (listTitle.getBox_checked().equals(listTitle.getBox_total())) {
                consignment.setTextColor(Color.parseColor("#1D781F"));
                box.setTextColor(Color.parseColor("#1D781F"));
                tvPickUp_global.setTextColor(Color.parseColor("#1D781F"));
                textView24.setTextColor(Color.parseColor("#1D781F"));
                tvConGroupCountPick.setTextColor(Color.parseColor("#1D781F"));
                textView25.setTextColor(Color.parseColor("#1D781F"));
                tvTotal.setTextColor(Color.parseColor("#1D781F"));
            } else {
                consignment.setTextColor(Color.parseColor("#696969"));
                consignment.setTextColor(Color.parseColor("#696969"));
                box.setTextColor(Color.parseColor("#696969"));
                tvPickUp_global.setTextColor(Color.parseColor("#9C9C9C"));
                textView24.setTextColor(Color.parseColor("#696969"));
                tvConGroupCountPick.setTextColor(Color.parseColor("#696969"));
                textView25.setTextColor(Color.parseColor("#696969"));
                tvTotal.setTextColor(Color.parseColor("#696969"));
            }


            // Log.d("ASfjhbaskjdfgsdfasd", "getGroupView: " + listTitle.getGlobal_cancel());

            tvPickUp_global.setText("Global (" + listTitle.getGlobal_total() + ")");

            if (!listTitle.getGlobal_cancel().equals("0")) {
                tv_Global_cancel.setVisibility(View.VISIBLE);
                tv_Global_cancel.setText(listTitle.getGlobal_cancel() + " " + context.getString(R.string.canceled) + ".");
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
                textView24.setText(listTitle.getPaytype());

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
            LanguageClass.setLanguage(getApplicationContext());
            popupInputDialogView2 = layoutInflater.inflate(R.layout.con_dialog_detail, null);

            alertDialogBuilder.setView(popupInputDialogView2);
            alertDialog2 = alertDialogBuilder.create();


            imgClose_dialog = popupInputDialogView2.findViewById(R.id.imgClose_dialog);
            // mTabHost = popupInputDialogView2.findViewById(android.R.id.tabhost)
            tvConsignment_con_dialog = popupInputDialogView2.findViewById(R.id.tvConsignment_con_dialog);
            TextView pick_dialog_station_address = popupInputDialogView2.findViewById(R.id.tv_dialog_station_address);
            ImageView pick_dialog_pay_type = popupInputDialogView2.findViewById(R.id.pick_dialog_pay_type);
            TextView pick_dialog_pay_type_credit = popupInputDialogView2.findViewById(R.id.tv_dialog_pay_type_credit);
            TextView pick_summary = popupInputDialogView2.findViewById(R.id.tv_summary);
            TextView tv_dialog_thb = popupInputDialogView2.findViewById(R.id.tv_dialog_thb);
            TextView tv3363 = popupInputDialogView2.findViewById(R.id.tv3363);
            TextView tv3364 = popupInputDialogView2.findViewById(R.id.tv3364);
            TextView tv3365 = popupInputDialogView2.findViewById(R.id.tv3365);
            ImageView pick_dialog_img_credit = popupInputDialogView2.findViewById(R.id.dialog_img_credit);
            SegmentedButtonGroup segmentedButtonGroup = popupInputDialogView2.findViewById(R.id.buttonGroup_vectorDrawable);
            rvDialogCons = popupInputDialogView2.findViewById(R.id.rvDialogCons);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            rvDialogCons.setLayoutManager(layoutManager);

            tvConsignment_con_dialog.setText("Cons.No: " + consignment);
            pick_summary.setText(box_total + " Boxs\n" + global_total + " Global");
            tv3363.setText(getApplicationContext().getString(R.string.address) + " : ");
            tv3364.setText(getApplicationContext().getString(R.string.pay_type) + " : ");
            tv3365.setText(getApplicationContext().getString(R.string.summary) + " : ");

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

        private void showDialogBox(PickingUpEexpand_Model picking1, int positionGroup, int positionChill) {

            PickingUpEexpand_Model picking = (PickingUpEexpand_Model) getChild(positionGroup, positionChill);
            PickingUp_Model listTitle = (PickingUp_Model) getGroup(positionGroup);

            final SharedPreferences data_intent = getSharedPreferences("DATA_INTENT", Context.MODE_PRIVATE);
            TextView tvConsignment_Dialog, tv_BoxNo_Dialog;

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setCancelable(false);

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            LanguageClass.setLanguage(getApplicationContext());
            View popupInputDialogView = layoutInflater.inflate(R.layout.cus_dialog_pickingup, null);

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

            // Log.d("Afsdafsd", "showDi+lalogBox: "+listTitle.getConsignment());


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

//            }


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
                    //                   expandableListView.expandGroup(position);
                }
            });

            for (int i = 0; i < deleteImage.size(); i++) {
                Log.d("deltetImage", "onClick: " + deleteImage.get(i));
            }

            btnSaveComent_PICK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // String commentText = edtComment_PICK.getText().toString();

                    Log.d("ASdasdasd", "onClick: " + commentOfspinner);
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
                        picking.setComment(commentOfspinner);
                        // picking.setIs_scaned("2");
                        picking.setTime_begin(getdate());
                        picking.setActual_lat(getlat());
                        picking.setActual_lon(getlon());
//                        if (listTitle.getCount() >= 0) {
//                            int count = listTitle.getCount() + 1;
//                            listTitle.setCount(count);
//                            Log.d("asdad", "onClick: " + listTitle.getCount());
//                        }
//                        listTitle.setBox_checked(String.valueOf(listTitle.getCount()));

                        cv.put("comment", commentOfspinner);
                    } else {
                        cv.put("comment", "");
                        cv.put("is_scaned", "0");

                        picking.setComment("");
                        // picking.setIs_scaned("0");
                        picking.setTime_begin("");
                        picking.setActual_lat("");
                        picking.setActual_lon("");
//                        if (!listTitle.getBox_checked().equals("0")) {
//                            int count = listTitle.getCount() - 1;
//                            listTitle.setCount(count);
//                        }
//                        listTitle.setBox_checked(String.valueOf(listTitle.getCount()));
                    }


//                    cv.put("modified_date", getdate());
//                    databaseHelper.db().update("Plan", cv, "delivery_no= '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and activity_type = 'LOAD' and " +
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
//
                    updateChill(picking.getConsignment(), positionChill, picking, positionGroup);
                    //                  getSQLite();
//                    expandableListView.setAdapter(expandableListAdapter);
//                    expandableListView.smoothScrollToPositionFromTop(positionGroup,positionChill);
//                    expandableListAdapter.notifyDataSetChanged();
//                    expandableListView.expandGroup(positionGroup);
                    alertDialog.dismiss();
                    //Toast.makeText(PinkingUpMaster_Activity.this, "Saved.", Toast.LENGTH_SHORT).show();
                    Toasty.success(getApplicationContext(), "reasoned.", Toast.LENGTH_SHORT, true);
                }
            });


            //**************************************************************************************************
//            String sql = "select comment,  ifnull(picture1,'') picture1  from Plan where activity_type = 'LOAD' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "' " +
//                    "and box_no = '" + box_no + "' and consignment_no = '" + consignment_no + "' and trash = '0'";
//            Cursor cursor = databaseHelper.selectDB(sql);
//            cursor.moveToFirst();
//            if (cursor.getCount() > 0) {
//                do {

//                    String comment = cursor.getString(cursor.getColumnIndex("comment"));
//                    picture1 = cursor.getString(cursor.getColumnIndex("picture1"));
            picture1 = picking.getPicture1();


//                    if (comment != null) {
//                        int spinnerPosition = adapter.getPosition(comment);
//                        spinner.setSelection(spinnerPosition + 1);
//                    }
            // edtComment_PICK.setText(comment);


            Log.d("AsfiuASEHFIOPqeu", "showDialogBox: " + "/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + picture1);


            if (!picture1.equals("")) {
                try {
                    arrayNameImage[0] = picking.getPicture1();
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
                                        Toast.makeText(Pickup_Activity.this, "Successfully deleted.", Toast.LENGTH_SHORT).show();

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

//            String sql02 = "select comment, ifnull(picture2,'') picture2 from Plan where activity_type = 'LOAD' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "' " +
//                    "and box_no = '" + box_no + "' and consignment_no = '" + consignment_no + "' and trash = '0'";
//            Cursor cursor02 = databaseHelper.selectDB(sql02);
//
//            cursor02.moveToFirst();
//            if (cursor02.getCount() > 0) {
//                do {
//
//                    String comment = cursor02.getString(cursor02.getColumnIndex("comment"));
//                    picture2 = cursor02.getString(cursor02.getColumnIndex("picture2"));

            picture2 = picking.getPicture2();


//                    if (comment != null) {
//                        int spinnerPosition = adapter.getPosition(comment);
//                        spinner.setSelection(spinnerPosition + 1);
//                    }
            //edtComment_PICK.setText(comment);


            if (!picture2.equals("")) {
                picTemp2.add(picture2);
                arrayNameImage[1] = picking.getPicture2();
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
                                        cv.put("picture2", "");
                                        cv.put("modified_date", getdate());
                                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + picking.getDelivery_no() + "' and plan_seq = '" + picking.getPlan_seq() + "' and activity_type = 'LOAD' and " +
                                                " consignment_no = '" + picking.getConsignment() + "' and box_no = '" + picking.getBox_no() + "' and trash = '0'", null);

                                        databaseHelper.db().delete("image", "name_img=?", new String[]{picture2});

                                        imgCommentPick_02.setImageResource(R.mipmap.add_photo);
                                        imgNewPick02.setVisibility(View.GONE);
                                        imgDeletePick02.setVisibility(View.GONE);
                                        imgCommentPick_02.setEnabled(true);
                                        Toast.makeText(Pickup_Activity.this, "Successfully deleted.", Toast.LENGTH_SHORT).show();

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

//                } while (cursor02.moveToNext());
//
//            }
//**************************************************************************************************

//            String sql03 = "select comment, ifnull(picture3,'') picture3 from Plan where activity_type = 'LOAD' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "' " +
//                    "and box_no = '" + box_no + "' and consignment_no = '" + consignment_no + "' and trash = '0'";
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
            // edtComment_PICK.setText(comment);

            picture3 = picking.getPicture3();


            if (!picture3.equals("")) {
                picTemp3.add(picture3);
                arrayNameImage[2] = picking.getPicture3();
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
                                        cv.put("picture3", "");
                                        cv.put("modified_date", getdate());
                                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + picking.getDelivery_no() + "' and plan_seq = '" + picking.getPlan_seq() + "' and activity_type = 'LOAD' and " +
                                                " consignment_no = '" + picking.getConsignment() + "' and box_no = '" + picking.getBox_no() + "' and trash = '0'", null);

                                        databaseHelper.db().delete("image", "name_img=?", new String[]{picture3});
                                        //  databaseHelper.db().delete("image", "name_img=" + picture3, null);

                                        imgCommentPick_03.setImageResource(R.mipmap.add_photo);
                                        imgNewPick03.setVisibility(View.GONE);
                                        imgDeletePick03.setVisibility(View.GONE);
                                        imgCommentPick_03.setEnabled(true);
                                        Toast.makeText(Pickup_Activity.this, "Successfully deleted.", Toast.LENGTH_SHORT).show();

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
//
//                } while (cursor03.moveToNext());
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

    //***********************************************************************************************************************
    // Sync
    @SuppressLint("StaticFieldLeak")
    public void Upload() {

        narisv = new NarisBaseValue(Pickup_Activity.this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        netCon = new ConnectionDetector(Pickup_Activity.this);
        databaseHelper = new DatabaseHelper(Pickup_Activity.this);


        new AsyncTask<String, Integer, String>() {

            int IsSuccess = 1;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = new ProgressDialog(Pickup_Activity.this);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(getString(R.string.sync_data));
                progressDialog.show();

            }

            @Override
            protected String doInBackground(String... strings) {
                JSONObject Root = new JSONObject();
                JSONObject picture1 = new JSONObject();
                ArrayList<UploadImage.Data> uploadImage = new ArrayList<>();

                try {

                    String sql = "select * from Plan where is_scaned <> '0' and status_upload= '0' and trash = '0' ";
                    Cursor cursor = databaseHelper.selectDB(sql);

                    JSONArray ContactArray = new JSONArray();
                    File f = new File(Environment.getExternalStorageDirectory()
                            + "/ContactDetail.txt");
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(f, true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    PrintStream ps = new PrintStream(fos);

                    Log.d("UploadWorkLog", "doInBackground: " + cursor.getCount());
                    int i = 0;
                    cursor.moveToFirst();
                    if (cursor.getCount() > 0) {
                        do {

                            JSONObject contact = new JSONObject();

                            try {

//                            id_plan = cursor.getString(cursor.getColumnIndex("id"));
                                contact.put("id", cursor.getString(cursor.getColumnIndex("id")));
                                contact.put("delivery_date", cursor.getString(cursor.getColumnIndex("delivery_date")));
                                contact.put("vehicle_name", cursor.getString(cursor.getColumnIndex("vehicle_name")));
                                contact.put("blackbox", cursor.getString(cursor.getColumnIndex("blackbox")));
                                contact.put("delivery_no", cursor.getString(cursor.getColumnIndex("delivery_no")));
                                contact.put("plan_seq", cursor.getString(cursor.getColumnIndex("plan_seq")));
                                contact.put("station_code", cursor.getString(cursor.getColumnIndex("station_code")));
                                contact.put("station_name", cursor.getString(cursor.getColumnIndex("station_name")));
                                contact.put("station_address", cursor.getString(cursor.getColumnIndex("station_address")));
                                contact.put("station_lat", cursor.getString(cursor.getColumnIndex("station_lat")));
                                contact.put("station_lon", cursor.getString(cursor.getColumnIndex("station_lon")));
                                contact.put("station_area", cursor.getString(cursor.getColumnIndex("station_area")));
                                contact.put("plan_in", cursor.getString(cursor.getColumnIndex("plan_in")));
                                contact.put("plan_out", cursor.getString(cursor.getColumnIndex("plan_out")));
                                contact.put("consignment_no", cursor.getString(cursor.getColumnIndex("consignment_no")));
                                contact.put("order_no", cursor.getString(cursor.getColumnIndex("order_no")));
                                contact.put("activity_type", cursor.getString(cursor.getColumnIndex("activity_type")));
                                contact.put("box_no", cursor.getString(cursor.getColumnIndex("box_no")));
                                contact.put("waybill_no", cursor.getString(cursor.getColumnIndex("waybill_no")));
                                contact.put("weight", cursor.getString(cursor.getColumnIndex("weight")));
                                contact.put("actual_seq", cursor.getString(cursor.getColumnIndex("actual_seq")));
                                contact.put("actual_lat", cursor.getString(cursor.getColumnIndex("actual_lat")));
                                contact.put("actual_lon", cursor.getString(cursor.getColumnIndex("actual_lon")));
//                            contact.put("time_actual_in", cursor.getString(cursor.getColumnIndex("time_actual_in")));
//                            contact.put("time_actual_out", cursor.getString(cursor.getColumnIndex("time_actual_out")));
                                contact.put("time_begin", cursor.getString(cursor.getColumnIndex("time_begin")));
//                            contact.put("time_end", cursor.getString(cursor.getColumnIndex("time_end")));
                                contact.put("signature", cursor.getString(cursor.getColumnIndex("signature")));
                                contact.put("is_scanned", cursor.getString(cursor.getColumnIndex("is_scaned")));
                                contact.put("comment", cursor.getString(cursor.getColumnIndex("comment")));
                                contact.put("picture1", cursor.getString(cursor.getColumnIndex("picture1")));
                                contact.put("picture2", cursor.getString(cursor.getColumnIndex("picture2")));
                                contact.put("picture3", cursor.getString(cursor.getColumnIndex("picture3")));
                                contact.put("driver_code", cursor.getString(cursor.getColumnIndex("driver_code")));
                                contact.put("driver_name", cursor.getString(cursor.getColumnIndex("driver_name")));
                                contact.put("modified_date", cursor.getString(cursor.getColumnIndex("modified_date")));
                                contact.put("trash", cursor.getString(cursor.getColumnIndex("trash")));

                                ContactArray.put(i, contact);
                                i++;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } while (cursor.moveToNext());

                        Root.put("data", ContactArray);
                        ps.append(Root.toString());

                        Log.d("UploadWorkLog", "doInBackground: " + Root.toString());

                        String rootToString = Root.toString();
                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), rootToString);

                        Call<ResponseBody> call = apiInterface.uploadwork(Var.UserLogin.driver_id, body);
                        Response<ResponseBody> response = call.execute();
                        if (response.code() == 200) {
                            String responseRecieved = response.body().string();
                            if (responseRecieved != null) {
                                if (!responseRecieved.equals("")) {
                                    JSONArray jsonArray = new JSONArray(responseRecieved);

                                    if (jsonArray.getJSONObject(0).getString("status").equals("Y")) {
//
                                        for (int pic = 0; pic < jsonArray.getJSONObject(0).getJSONArray("returnId").length(); pic++) {

                                            String json_data = jsonArray.getJSONObject(0).getJSONArray("returnId").getString(pic);
                                            //เปิดดทีหลัง
                                            ContentValues cv = new ContentValues();
                                            cv.put("status_upload", "1");
                                            cv.put("is_save", "1");
                                            databaseHelper.db().update("Plan", cv, "id= '" + json_data + "'", null);

                                        }

                                        String sql_getPicture = "select pl.id" +
                                                ", ifnull((select im.name_img from image im where im.name_img = pl.picture1 and im.status_img = '0'), '') as picture1" +
                                                ", ifnull((select im.name_img from image im where im.name_img = pl.picture2 and im.status_img = '0'), '') as picture2" +
                                                ", ifnull((select im.name_img from image im where im.name_img = pl.picture3 and im.status_img = '0'), '') as picture3 " +
                                                "from plan pl " +
                                                "where pl.is_scaned = '2'";
                                        Cursor cursor_getPicture = databaseHelper.selectDB(sql_getPicture);

                                        int j = 0;
                                        cursor_getPicture.moveToFirst();
                                        if (cursor_getPicture.getCount() > 0) {
                                            do {

                                                if (!cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture1")).equals("")) {

                                                    File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture1")));
                                                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                                                    byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
                                                    encodedImagePic1 = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                                                    UploadImage.Data data = new UploadImage.Data(cursor_getPicture.getString(cursor_getPicture.getColumnIndex("id")),
                                                            cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture1")),
                                                            "1",
                                                            "data:image/jpeg;base64," + encodedImagePic1);
                                                    uploadImage.add(data);

                                                }

                                                if (!cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture2")).equals("")) {

                                                    File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture2")));
                                                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                                                    byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
                                                    encodedImagePic2 = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                                                    UploadImage.Data data = new UploadImage.Data(cursor_getPicture.getString(cursor_getPicture.getColumnIndex("id")),
                                                            cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture2")),
                                                            "2",
                                                            "data:image/jpeg;base64," + encodedImagePic2);
                                                    uploadImage.add(data);

                                                }

                                                if (!cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture3")).equals("")) {

                                                    File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture3")));
                                                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                                                    byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
                                                    encodedImagePic3 = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                                                    UploadImage.Data data = new UploadImage.Data(cursor_getPicture.getString(cursor_getPicture.getColumnIndex("id")),
                                                            cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture3")),
                                                            "3",
                                                            "data:image/jpeg;base64," + encodedImagePic3);
                                                    uploadImage.add(data);

                                                }


                                            } while (cursor_getPicture.moveToNext());

                                            UploadImage data = new UploadImage(uploadImage);

                                            Log.d("kksksks", "doInBackground: " + uploadImage.get(0).toString());


                                            if (data != null) {

                                                Call<ResponseBody> callImg = apiInterface.uploadPicture(data);

                                                Response<ResponseBody> responseImg = callImg.execute();
                                                if (responseImg.code() == 200) {
                                                    String responseRecievedImg = responseImg.body().string();
                                                    if (responseRecievedImg != null) {
                                                        if (!responseRecievedImg.equals("")) {
                                                            JSONArray jsonImg = new JSONArray(responseRecievedImg);

                                                            if (jsonImg.getJSONObject(0).getString("status").equals("Y")) {
                                                                Log.d("sdfsdf", "TRD_1: " + jsonImg.getJSONObject(0).getString("status"));

                                                                for (int pic = 0; pic < jsonImg.getJSONObject(0).getJSONArray("img").length(); pic++) {

                                                                    String json_data = jsonImg.getJSONObject(0).getJSONArray("img").getString(pic);
                                                                    Log.d("sdfsdf", "TRD_1: " + json_data);

                                                                    // เปิดทีหลัง
                                                                    ContentValues cv = new ContentValues();
                                                                    cv.put("status_img", "1");
                                                                    databaseHelper.db().update("image", cv, "name_img= '" + json_data + "'", null);

                                                                }

                                                            } else {
                                                                Log.d("sdfsdf", "TRD_1: Fail");
                                                            }

                                                        }
                                                    }
                                                }

                                            }//pic1
                                        }
                                        //publishProgress();
                                        IsSuccess = 1;
                                    } else {
                                        IsSuccess = 0;
                                    }

                                }
                            }
                        }


                    } else {

//                    new DownloadWork().execute();
                        IsSuccess = 1;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                String mess = "";
                switch (IsSuccess) {
                    case -1:
                        mess = "Synced (not found data)";

                        Toast.makeText(Pickup_Activity.this, mess, Toast.LENGTH_SHORT).show();

//                        Snackbar.make(viewFab, mess, Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
                        break;
                    case 1:
                        //deleteJobAndImage();
                        new uploadInvoice().execute();
                        new DownloadWork().execute();

                        break;
                    case 2:
                        mess = "Sync error!!";
                        Toast.makeText(Pickup_Activity.this, mess, Toast.LENGTH_SHORT).show();
//                        Snackbar.make(viewFab, mess, Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
                        break;
                }

            }


        }.execute();

    }


    public class uploadInvoice extends AsyncTask<String, String, String> {
        int IsSuccess = 1;

        @Override
        protected String doInBackground(String... strings) {

            JSONObject Root = new JSONObject();
            JSONObject RootCM = new JSONObject();
            ArrayList<UploadImageInvoice.Data2> uploadImage = new ArrayList<>();
            Log.d("statusUploadInvoice", "doInBackground: 1");
            try {
                String sql = "select ps.id  \n" +
                        ", ps.delivery_no  \n" +
                        ", ps.order_no  \n" +
                        ", ps.consignment_no  \n" +
                        ", ps.invoice_no  \n" +
                        ", ps.pic_sign_load  \n" +
                        ", ps.pic_sign_unload  \n" +
                        ", ps.date_sign_load  \n" +
                        ", ps.date_sign_unload   \n" +
                        ", ps.comment_load  \n" +
                        ", ps.comment_unload  \n" +
                        ", ps.status_load  \n" +
                        ", ps.status_unload  \n" +
                        "from pic_sign ps  \n" +
                        "where status_upload_invoice = '0' and status_delete = '0'";
                Cursor cursor = databaseHelper.selectDB(sql);
                JSONArray ContactArray = new JSONArray();

                int i = 0;
                cursor.moveToFirst();
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        do {

                            JSONObject contact = new JSONObject();

                            contact.put("id", cursor.getString(cursor.getColumnIndex("id")));
                            contact.put("vehicle_id", Var.UserLogin.driver_vehicle_id);
                            contact.put("delivery_no", cursor.getString(cursor.getColumnIndex("delivery_no")));
                            contact.put("order_no", cursor.getString(cursor.getColumnIndex("order_no")));
                            contact.put("consignment_no", cursor.getString(cursor.getColumnIndex("consignment_no")));
                            contact.put("invoice_no", cursor.getString(cursor.getColumnIndex("invoice_no")));
                            contact.put("pic_sign_load", cursor.getString(cursor.getColumnIndex("pic_sign_load")));
                            contact.put("pic_sign_unload", cursor.getString(cursor.getColumnIndex("pic_sign_unload")));
                            contact.put("date_sign_load", cursor.getString(cursor.getColumnIndex("date_sign_load")));
                            contact.put("date_sign_unload", cursor.getString(cursor.getColumnIndex("date_sign_unload")));
                            contact.put("comment_load", cursor.getString(cursor.getColumnIndex("comment_load")));
                            contact.put("comment_unload", cursor.getString(cursor.getColumnIndex("comment_unload")));
                            contact.put("status_load", cursor.getString(cursor.getColumnIndex("status_load")));
                            contact.put("status_unload", cursor.getString(cursor.getColumnIndex("status_unload")));

                            ContactArray.put(i, contact);
                            i++;

                        } while (cursor.moveToNext());

                        Root.put("data", ContactArray);

                        for (int s = 0; s < ContactArray.length(); s++) {
                            Log.d("as52a8", "doInBackground: " + ContactArray.get(s));
                        }
                        Log.d("statusUploadInvoice", "doInBackground: " + Root.toString());

                        String rootToString = Root.toString();
                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), rootToString);

                        Call<ResponseBody> call = apiInterface.uploadInvoice(body);
                        Response<ResponseBody> response = call.execute();
                        if (response.code() == 200) {
                            String received = response.body().string();
                            if (received != null) {
                                if (!received.equals("")) {
                                    JSONArray jsonArray = new JSONArray(received);
                                    if (jsonArray.getJSONObject(0).getString("status").equals("Y")) {
                                        //อัพเดตข้อมมูลหลังจาก upload แล้วเพื่อไม่ให้ข้อมูลซ้ำ
                                        for (int j = 0; j < jsonArray.getJSONObject(0).getJSONArray("data").length(); j++) {
                                            String json_data = jsonArray.getJSONObject(0).getJSONArray("data").getString(j);

                                            //เปิดทีหลัง
                                            ContentValues cv = new ContentValues();
                                            cv.put("status_upload_invoice", "1");
                                            databaseHelper.db().update("pic_sign", cv, "id= '" + json_data + "'", null);
                                        }

                                        //upload image *********************************************
                                        String sql_getPicture = "select ii.id, ii.name_img from image_invoice ii where (ii.name_img in (select ps1.pic_sign_load from pic_sign ps1) or ii.name_img in (select ps2.pic_sign_unload from pic_sign ps2)) and  ii.status_img = '0'";
                                        Cursor cursor_getPicture = databaseHelper.selectDB(sql_getPicture);

                                        cursor_getPicture.moveToFirst();
                                        if (cursor_getPicture != null) {
                                            if (cursor_getPicture.getCount() > 0) {
                                                do {

                                                    String id = cursor_getPicture.getString(cursor_getPicture.getColumnIndex("id"));
                                                    String img = cursor_getPicture.getString(cursor_getPicture.getColumnIndex("name_img"));

                                                    Log.d("Asfkjsaioosdf", "doInBackground: L0 " + img);

                                                    if (!img.equals("")) {

                                                        File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Signature/" + img);
                                                        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                                                        byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
                                                        encodedImageInvoice = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                                                        UploadImageInvoice.Data2 data = new UploadImageInvoice.Data2(id, img, "data:image/jpeg;base64," + encodedImageInvoice);
                                                        uploadImage.add(data);

                                                    }

                                                } while (cursor_getPicture.moveToNext());

                                                UploadImageInvoice data = new UploadImageInvoice(uploadImage);


                                                Call<ResponseBody> callImg = apiInterface.uploadPictureInvoice(data);

                                                Response<ResponseBody> responseImg = callImg.execute();
                                                if (responseImg.code() == 200) {
                                                    String responseRecievedImg = responseImg.body().string();
                                                    if (responseRecievedImg != null) {
                                                        if (!responseRecievedImg.equals("")) {
                                                            JSONArray jsonImg = new JSONArray(responseRecievedImg);

                                                            if (jsonImg.getJSONObject(0).getString("status").equals("Y")) {

                                                                for (int pic = 0; pic < jsonImg.getJSONObject(0).getJSONArray("img").length(); pic++) {

                                                                    String json_data = jsonImg.getJSONObject(0).getJSONArray("img").getString(pic);
                                                                    Log.d("TRD", "TRD_1: " + json_data);

                                                                    ContentValues cv = new ContentValues();
                                                                    cv.put("status_img", "1");
                                                                    databaseHelper.db().update("image_invoice", cv, "name_img= '" + json_data + "'", null);

                                                                }

                                                            } else {
                                                                Log.d("TRD", "TRD_1: Fail");
                                                            }

                                                        }
                                                    }
                                                }//code 200

                                            }
                                        }//cursor_getPicture != null
                                        else {
                                            Log.d("Asfkjsaioosdf", "cursor_getPicture: null");
                                        }


                                        //upload comment *********************************************
                                        String commentSQL = "select id, (select delivery_no from plan) as delivery_no, order_no, consignment_no, invoice_no, comment, comment_deliver from comment_invoice where status_upload_comment = '0'";
                                        Cursor cursorCM = databaseHelper.selectDB(commentSQL);
                                        JSONArray ContactCM = new JSONArray();

                                        int cm = 0;
                                        cursorCM.moveToFirst();
                                        if (cursorCM != null) {
                                            if (cursorCM.getCount() > 0) {
                                                do {
                                                    JSONObject contact2 = new JSONObject();

                                                    contact2.put("id", cursorCM.getString(cursorCM.getColumnIndex("id")));
                                                    contact2.put("vehicle_id", Var.UserLogin.driver_vehicle_id);
                                                    contact2.put("delivery_no", cursorCM.getString(cursorCM.getColumnIndex("delivery_no")));
                                                    contact2.put("order_no", cursorCM.getString(cursorCM.getColumnIndex("order_no")));
                                                    contact2.put("consignment_no", cursorCM.getString(cursorCM.getColumnIndex("consignment_no")));
                                                    contact2.put("invoice_no", cursorCM.getString(cursorCM.getColumnIndex("invoice_no")));
                                                    contact2.put("comment_sign_load", cursorCM.getString(cursorCM.getColumnIndex("comment")));
                                                    contact2.put("comment_sign_unload", cursorCM.getString(cursorCM.getColumnIndex("comment_deliver")));

                                                    ContactCM.put(cm, contact2);
                                                    cm++;

                                                } while (cursorCM.moveToNext());

                                                RootCM.put("data", ContactCM);
                                                Log.d("lksioasj", "doInBackground: " + RootCM.toString());
                                                String rootToStringCM = RootCM.toString();
                                                RequestBody bodyCM = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), rootToStringCM);

                                                Call<ResponseBody> callCM = apiInterface.uploadComment(bodyCM);
                                                Response<ResponseBody> responseCM = callCM.execute();
                                                if (responseCM.code() == 200) {
                                                    String responseRecievedCM = responseCM.body().string();
                                                    if (responseRecievedCM != null) {
                                                        if (!responseRecievedCM.equals("")) {
                                                            JSONArray jsonCM = new JSONArray(responseRecievedCM);

                                                            if (jsonCM.getJSONObject(0).getString("status").equals("Y")) {

                                                                for (int b = 0; b < jsonArray.getJSONObject(0).getJSONArray("data").length(); b++) {
                                                                    String json_data = jsonArray.getJSONObject(0).getJSONArray("data").getString(b);

                                                                    Log.d("lksioasj", "doInBackground: " + json_data);

                                                                    ContentValues cv = new ContentValues();
                                                                    cv.put("status_upload_comment", "1");
                                                                    databaseHelper.db().update("comment_invoice", cv, "id= '" + json_data + "'", null);
                                                                }

                                                            } else {

                                                            }

                                                        }
                                                    }
                                                }

                                            }
                                        }


                                        IsSuccess = 1;
                                    } else {
                                        IsSuccess = 0;
                                    }
                                }

                            }
                        }


                    }
                }

            } catch (Exception e) {
                Log.d("statusUploadInvoice", "catch :" + e.getMessage());
            }
            return null;
        }
    }

    public class DownloadWork extends AsyncTask<String, String, String> {

        int IsSuccess = 1;

        @Override
        protected String doInBackground(String... strings) {

            try {

                //ลบงานหลัง 7 วัน


                String max_modified_date = "";

                String sql_getMaxModifild_date = "select MAX(modified_date) as max_modified_date from Plan ";
                Cursor cursor_etMaxModifild_date = databaseHelper.selectDB(sql_getMaxModifild_date);
                cursor_etMaxModifild_date.moveToFirst();
                if (cursor_etMaxModifild_date.getCount() > 0) {
                    do {
                        max_modified_date = cursor_etMaxModifild_date.getString(cursor_etMaxModifild_date.getColumnIndex("max_modified_date"));

                    } while (cursor_etMaxModifild_date.moveToNext());
                } else {
                    max_modified_date += "";
                }
                Log.d("PlanWorkLOG", "MaxModifiedDate : " + max_modified_date);


                String pattern = "yyyy-MM-dd%kk:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
                String getDate = sdf.format(Calendar.getInstance().getTime());

//                String url = Var.WEBSERVICE2 + "func=getPlan&vehicle_id=" + Var.UserLogin.driver_vehicle_id + "&driver_id=" + Var.UserLogin.driver_id + "&serial=" +
//                        Var.UserLogin.driver_serial + "&phone_date=" + getDate + "&date+";
//                Log.d("PlanWorkLOG", url);
                // JSONArray GETJSON = narisv.getJsonFromUrl_reJsonArray(url);


                Call<ResponseBody> call = apiInterface.downloadWork(Var.UserLogin.driver_vehicle_id, Var.UserLogin.driver_id, Var.UserLogin.driver_serial, getdate(), max_modified_date);
                //Call<ResponseBody> call = apiInterface.downloadWork(Var.UserLogin.driver_vehicle_id, Var.UserLogin.driver_id, "4AB5F216", getDate, max_modified_date);
                Response<ResponseBody> response = call.execute();
                if (response.code() == 200) {
                    String responseRecieved = response.body().string();
                    if (responseRecieved != null) {
                        if (!responseRecieved.equals("")) {
                            JSONArray jsonArray = new JSONArray(responseRecieved);

                            NarisBaseValue.insertPlan(jsonArray);

                            Call<ResponseBody> callCons = apiInterface.downloadConsignment(Var.UserLogin.driver_vehicle_id, "");
                            Response<ResponseBody> responseCons = callCons.execute();
                            if (responseCons.code() == 200) {
                                String responseRecievedCons = responseCons.body().string();
                                if (!responseRecieved.equals("")) {
                                    JSONArray jsonArrayCons = new JSONArray(responseRecievedCons);
                                    NarisBaseValue.insertConsignment(jsonArrayCons);

                                    Call<ResponseBody> reaSon = apiInterface.reason();
                                    Response<ResponseBody> responseReason = reaSon.execute();
                                    if (responseReason.code() == 200) {
                                        String recievedReason = responseReason.body().string();
                                        if (recievedReason != null) {
                                            if (!responseRecieved.equals("")) {
                                                JSONArray jsonArrayReason = new JSONArray(recievedReason);

                                                NarisBaseValue.insertReason(jsonArrayReason);

                                                Call<ResponseBody> inVoice = apiInterface.invoice(Var.UserLogin.driver_vehicle_id);
                                                Response<ResponseBody> responseInvoice = inVoice.execute();
                                                if (responseInvoice.code() == 200) {
                                                    String recievedInvoice = responseInvoice.body().string();

                                                    //  Log.d("S5s52a9", "doInBackground: "+recievedInvoice);
                                                    if (recievedInvoice != null) {
                                                        if (!recievedInvoice.equals("")) {

                                                            JSONArray jsonArrayInvoice = new JSONArray(recievedInvoice);

                                                            for (int o = 0; o < jsonArrayInvoice.length(); o++) {


                                                                String delivery_no = jsonArrayInvoice.getJSONObject(o).getString("delivery_no");
                                                                String order_no = jsonArrayInvoice.getJSONObject(o).getString("order_no");
                                                                String consignment_no = jsonArrayInvoice.getJSONObject(o).getString("consignment_no");
                                                                String invoice_no = jsonArrayInvoice.getJSONObject(o).getString("invoice_no");

                                                                String sql_expand = "select count(delivery_no) as count_delivery\n" +
                                                                        " from pic_sign\n" +
                                                                        " where delivery_no = '" + delivery_no + "' and order_no = '" + order_no + "' and consignment_no = '" + consignment_no + "' and invoice_no = '" + invoice_no + "'";
                                                                Cursor cursor = databaseHelper.selectDB(sql_expand);

                                                                cursor.moveToFirst();
                                                                if (cursor.getCount() > 0) {
                                                                    String count_delivery = cursor.getString(cursor.getColumnIndex("count_delivery"));
                                                                    if (count_delivery.equals("0")) {

                                                                        String sql = "INSERT OR REPLACE INTO pic_sign (delivery_no, consignment_no, order_no, invoice_no, pic_sign_load, pic_sign_unload" +
                                                                                ", comment_load, comment_unload, date_sign_load, date_sign_unload, status_load, status_unload, status_upload_invoice" +
                                                                                ", status_delete, create_date) VALUES('" + jsonArrayInvoice.getJSONObject(o).getString("delivery_no") + "'" +
                                                                                ",'" + jsonArrayInvoice.getJSONObject(o).getString("consignment_no") + "'" +
                                                                                ", '" + jsonArrayInvoice.getJSONObject(o).getString("order_no") + "', '" + jsonArrayInvoice.getJSONObject(o).getString("invoice_no") + "'" +
                                                                                ", '" + jsonArrayInvoice.getJSONObject(o).getString("pic_sign_load") + "', '" + jsonArrayInvoice.getJSONObject(o).getString("pic_sign_unload") + "'" +
                                                                                ", '" + jsonArrayInvoice.getJSONObject(o).getString("comment_load") + "', '" + jsonArrayInvoice.getJSONObject(o).getString("comment_unload") + "'" +
                                                                                ", '" + jsonArrayInvoice.getJSONObject(o).getString("date_sign_load") + "', '" + jsonArrayInvoice.getJSONObject(o).getString("date_sign_unload") + "'" +
                                                                                ", '" + jsonArrayInvoice.getJSONObject(o).getString("status_load") + "', '" + jsonArrayInvoice.getJSONObject(o).getString("status_unload") + "','1','0','" + getdate() + "')";
                                                                        databaseHelper.db().execSQL(sql);

                                                                    } else {

                                                                        ContentValues cv = new ContentValues();
                                                                        cv.put("pic_sign_load", jsonArrayInvoice.getJSONObject(o).getString("pic_sign_load"));
                                                                        cv.put("pic_sign_unload", jsonArrayInvoice.getJSONObject(o).getString("pic_sign_unload"));
                                                                        cv.put("date_sign_load", jsonArrayInvoice.getJSONObject(o).getString("date_sign_load"));
                                                                        cv.put("date_sign_unload", jsonArrayInvoice.getJSONObject(o).getString("date_sign_unload"));
                                                                        cv.put("comment_load", jsonArrayInvoice.getJSONObject(o).getString("comment_load"));
                                                                        cv.put("comment_unload", jsonArrayInvoice.getJSONObject(o).getString("comment_unload"));
                                                                        cv.put("status_load", jsonArrayInvoice.getJSONObject(o).getString("status_load"));
                                                                        cv.put("status_unload", jsonArrayInvoice.getJSONObject(o).getString("status_unload"));
                                                                        cv.put("status_upload_invoice", "1");
                                                                        cv.put("status_delete", "0");
                                                                        databaseHelper.db().update("pic_sign", cv, "delivery_no = '" + delivery_no + "' and order_no = '" + order_no + "' " +
                                                                                "and consignment_no = '" + consignment_no + "' and invoice_no = '" + invoice_no + "'", null);

                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }


                                }
                            }
                        }
                    }
                }

/*                Call<ResponseBody> call = apiInterface.downloadWork(Var.UserLogin.driver_vehicle_id, Var.UserLogin.driver_id, Var.UserLogin.driver_serial, getDate, max_modified_date);
                Response<ResponseBody> response = call.execute();
                if (response.code() == 200) {
                    String responseRecieved = response.body().string();
                    if (responseRecieved != null) {
                        if (!responseRecieved.equals("")) {
                            Log.d("getPlanLog", "doInBackground: " + responseRecieved);

                            JSONArray jsonArray = new JSONArray(responseRecieved);
//                            for (int i = 0; i < jsonArray.length(); i++) {
//
//                                JSONObject json_data = jsonArray.getJSONObject(i);
//
//                            }


                            if (narisv.INSERT_AS_SQL("Plan", jsonArray, "")) {
                                Log.d("PlanWorkLOG", "SAVED INVOICE HEADER");

                                String url_consign = Var.WEBSERVICE2 + "func=getConsignment&vehicle_id=" + Var.UserLogin.driver_vehicle_id;

                                // JSONArray GETJSON_CONSIGN = narisv.getJsonFromUrl_reJsonArray(url_consign);

                                Call<ResponseBody> callCons = apiInterface.downloadConsignment(Var.UserLogin.driver_vehicle_id, "");
                                Response<ResponseBody> responseCons = callCons.execute();
                                if (responseCons.code() == 200) {
                                    String responseRecievedCons = responseCons.body().string();
                                    if (responseRecieved != null) {
                                        if (!responseRecieved.equals("")) {
                                            JSONArray jsonArrayCons = new JSONArray(responseRecievedCons);
                                            if (narisv.INSERT_AS_SQL("consignment", jsonArrayCons, "")) {
                                                Log.d("PlanWorkLOG", "SAVED Consignment.");

                                                Call<ResponseBody> reaSon = apiInterface.reason();
                                                Response<ResponseBody> responseReason = reaSon.execute();
                                                if (responseReason.code() == 200) {
                                                    String recievedReason = responseReason.body().string();
                                                    if (recievedReason != null) {
                                                        if (!responseRecieved.equals("")) {
                                                            JSONArray jsonArrayReason = new JSONArray(recievedReason);
                                                            if (narisv.INSERT_AS_SQL("reason", jsonArrayReason, "")) {
                                                                Log.d("PlanWorkLOG", "SAVED reason.");

                                                                Call<ResponseBody> inVoice = apiInterface.invoice(Var.UserLogin.driver_vehicle_id);
                                                                Response<ResponseBody> responseInvoice = inVoice.execute();
                                                                if (responseInvoice.code() == 200) {
                                                                    String recievedInvoice = responseInvoice.body().string();
                                                                    if (recievedInvoice != null) {
                                                                        if (!recievedInvoice.equals("")) {
                                                                            JSONArray jsonArrayInvoice = new JSONArray(recievedInvoice);
                                                                            for (int o = 0; o < jsonArrayInvoice.length(); o++) {

                                                                                String delivery_no = jsonArrayInvoice.getJSONObject(o).getString("delivery_no");
                                                                                String order_no = jsonArrayInvoice.getJSONObject(o).getString("order_no");
                                                                                String consignment_no = jsonArrayInvoice.getJSONObject(o).getString("consignment_no");
                                                                                String invoice_no = jsonArrayInvoice.getJSONObject(o).getString("invoice_no");

                                                                                String sql_expand = "select count(delivery_no) as count_delivery\n" +
                                                                                        " from pic_sign\n" +
                                                                                        " where delivery_no = '" + delivery_no + "' and order_no = '" + order_no + "' and consignment_no = '" + consignment_no + "' and invoice_no = '" + invoice_no + "'";
                                                                                Cursor cursor = databaseHelper.selectDB(sql_expand);

                                                                                cursor.moveToFirst();
                                                                                if (cursor.getCount() > 0) {
                                                                                    String count_delivery = cursor.getString(cursor.getColumnIndex("count_delivery"));
                                                                                    if (count_delivery.equals("0")) {

                                                                                        String sql = "INSERT OR REPLACE INTO pic_sign (delivery_no, consignment_no, order_no, invoice_no, pic_sign_load, pic_sign_unload" +
                                                                                                ", comment_load, comment_unload, date_sign_load, date_sign_unload, status_load, status_unload, status_upload_invoice" +
                                                                                                ", status_delete, create_date) VALUES('" + jsonArrayInvoice.getJSONObject(o).getString("delivery_no") + "'" +
                                                                                                ",'" + jsonArrayInvoice.getJSONObject(o).getString("consignment_no") + "'" +
                                                                                                ", '" + jsonArrayInvoice.getJSONObject(o).getString("order_no") + "', '" + jsonArrayInvoice.getJSONObject(o).getString("invoice_no") + "'" +
                                                                                                ", '" + jsonArrayInvoice.getJSONObject(o).getString("pic_sign_load") + "', '" + jsonArrayInvoice.getJSONObject(o).getString("pic_sign_unload") + "'" +
                                                                                                ", '" + jsonArrayInvoice.getJSONObject(o).getString("comment_load") + "', '" + jsonArrayInvoice.getJSONObject(o).getString("comment_unload") + "'" +
                                                                                                ", '" + jsonArrayInvoice.getJSONObject(o).getString("date_sign_load") + "', '" + jsonArrayInvoice.getJSONObject(o).getString("date_sign_unload") + "'" +
                                                                                                ", '" + jsonArrayInvoice.getJSONObject(o).getString("status_load") + "', '" + jsonArrayInvoice.getJSONObject(o).getString("status_unload") + "','1','0','" + getdate() + "')";
                                                                                        databaseHelper.db().execSQL(sql);

                                                                                    } else {

                                                                                        ContentValues cv = new ContentValues();
                                                                                        cv.put("pic_sign_load", jsonArrayInvoice.getJSONObject(o).getString("pic_sign_load"));
                                                                                        cv.put("pic_sign_unload", jsonArrayInvoice.getJSONObject(o).getString("pic_sign_unload"));
                                                                                        cv.put("date_sign_load", jsonArrayInvoice.getJSONObject(o).getString("date_sign_load"));
                                                                                        cv.put("date_sign_unload", jsonArrayInvoice.getJSONObject(o).getString("date_sign_unload"));
                                                                                        cv.put("comment_load", jsonArrayInvoice.getJSONObject(o).getString("comment_load"));
                                                                                        cv.put("comment_unload", jsonArrayInvoice.getJSONObject(o).getString("comment_unload"));
                                                                                        cv.put("status_load", jsonArrayInvoice.getJSONObject(o).getString("status_load"));
                                                                                        cv.put("status_unload", jsonArrayInvoice.getJSONObject(o).getString("status_unload"));
                                                                                        cv.put("status_upload_invoice", "1");
                                                                                        cv.put("status_delete", "0");
                                                                                        databaseHelper.db().update("pic_sign", cv, "delivery_no = '" + delivery_no + "' and order_no = '" + order_no + "' " +
                                                                                                "and consignment_no = '" + consignment_no + "' and invoice_no = '" + invoice_no + "'", null);

                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                            } else {
                                                                Log.d("PlanWorkLOG", "FAIL save reason.");
                                                            }
                                                        }
                                                    }
                                                }


                                            } else {
                                                Log.d("PlanWorkLOG", "FAIL save consignment.");
                                            }
                                            IsSuccess = 1;
                                        }
                                    }
                                }


                            } else {
                                Log.d("PlanWorkLOG", "FAIL");
                                IsSuccess = 2;
                            }

                        }
                    }
                }*/

            } catch (Exception e) {
                IsSuccess = 2;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String mess = "";
            switch (IsSuccess) {
                case 1:
                    mess = "Synced";
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    getSQLite();
                    isSync = false;
//                    Snackbar.make(viewFab, mess, Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();

                    Toast.makeText(Pickup_Activity.this, mess, Toast.LENGTH_SHORT).show();
                    Var.synced = 1;
                    break;
                case 2:
                    mess = "Sync error!!";
//                    Snackbar.make(viewFab, mess, Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();

                    Toast.makeText(Pickup_Activity.this, mess, Toast.LENGTH_SHORT).show();

                    // rvPlanWork.setAdapter(sectionAdapter);
                    //// planWorkAdapter.notifyDataSetChanged();

                    break;
            }


        }
    }


}
