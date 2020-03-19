package ws.epod;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ws.epod.Adapter.MainAdapter;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.BackgroundService;
import ws.epod.ObjectClass.LanguageClass;
import ws.epod.ObjectClass.MenuObject;
import ws.epod.ObjectClass.Var;

public class Main_Activity extends LocalizationActivity {

    private Toolbar toolbar;
    private TextView tvMain_truck, tvMain_user_driver, tvMain_name_driver, tvMain_date_bar, tvMain_planSeq, tvMain_cusName, tvMain_address, tvMain_appoint, headerTxt_Main, textView8, textView10;
    private ImageView imgBack_Job_Main;
    private RecyclerView rvMain;
    ArrayList<MenuObject> list = new ArrayList<>();

    BackgroundService backgroundService;


    MainAdapter mainAdapter;
    ConnectionDetector netCon;
    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;
    String consignment_no_pick = "", consignment_no_deli = "", boxes_pick = "", boxes_deli = "", global_total_pick = "", global_total_deli = "",
            delivery_no02 = "", plan_seq02 = "", isscaned_pick = "", isscaned_deli = "", actual_seq = "", total_load = "", total_unload = "", box_scanned_load = "", box_scanned_unload = "";

    boolean che = true;


    String getDate = "";

    @Override
    public void onResume() {
        super.onResume();
        headerTxt_Main.setText(getApplicationContext().getString(R.string.job_operation));
        // stopService(new Intent(getApplicationContext(), BackgroundService.class));
        getSQLite();
        initView();
        addMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // startService(new Intent(getApplicationContext(),BackgroundService.class));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //  checkBackCon();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageClass.setLanguage(getApplicationContext());
        setContentView(R.layout.activity_main);


        narisv = new NarisBaseValue(Main_Activity.this);
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        String pattern = "yyyy-MM-dd kk:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
        getDate = sdf.format(Calendar.getInstance().getTime());

        imgBack_Job_Main = findViewById(R.id.imgBack_Job_Main);
        headerTxt_Main = findViewById(R.id.headerTxt_Main);
        imgBack_Job_Main.setOnClickListener(view -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
            imgBack_Job_Main.startAnimation(animation);
            finish();
            // checkBackCon();

        });

        rvMain = findViewById(R.id.rvMain);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvMain.setLayoutManager(layoutManager);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        getSQLite();


    }

//    private void checkBackCon() {
//        int plan_seq = getIntent().getExtras().getInt("plan_seq");
//        String delivery_no = getIntent().getExtras().getString("delivery_no");
//
//        String isscaned_pick = "";
//        String scaned_pick = "";
//
//        String sql = "select (select count( pl2.is_scaned) from Plan pl2 where pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and is_scaned = '0' and pl2.trash = pl.trash) as scaned_pick\n" +
//                ",(select count( pl2.is_scaned) from Plan pl2 where pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and is_scaned <> '0' and pl2.trash = pl.trash) as isscaned_pick\n" +
//                "from Plan pl\n" +
//                "where pl.delivery_no = '" + delivery_no + "' and  pl.plan_seq = '" + plan_seq + "' and pl.trash = '0' " +
//                "GROUP BY pl.delivery_no";
//        Cursor cursor = databaseHelper.selectDB(sql);
//        Log.d("PickingUpLOG_001", "total line " + cursor.getColumnCount());
//
//
//        cursor.moveToFirst();
//        if ( cursor.getCount() > 0 ) {
//            do {
//                isscaned_pick = cursor.getString(cursor.getColumnIndex("isscaned_pick"));
//                scaned_pick = cursor.getString(cursor.getColumnIndex("scaned_pick"));
//                Log.d("PickingUpLOG_001", isscaned_pick);
//            } while (cursor.moveToNext());
//        }
//
//        if ( !scaned_pick.equals("0") ) {// เช็คว่าถ้ายังสแกนไม่หมดหรือถ้าถูกเริ่มงานไปแล้วให้มี alert ขึ้นมา
//            final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
//            alertbox.setTitle(getString(R.string.alert));
//            alertbox.setMessage(getString(R.string.finish_job));
//            alertbox.setNegativeButton(getString(R.string.confirm),
//                    new DialogInterface.OnClickListener() {
//                        public void onClick( DialogInterface arg0,
//                                             int arg1 ) {
//                            int plan_seq = getIntent().getExtras().getInt("plan_seq");
//                            String delivery_no = getIntent().getExtras().getString("delivery_no");
//
//                            String pattern = "yyyy-MM-dd kk:mm";
//                            SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
//                            String date_now = sdf.format(Calendar.getInstance().getTime());
//
//                            ContentValues cv = new ContentValues();
//                            cv.put("time_actual_out", date_now);
//                            cv.put("time_end", date_now);
//                            cv.put("modified_date", getDate);
//                            databaseHelper.db().update("Plan", cv, "delivery_no= '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and trash = '0'", null);
//                            finish();
//                        }
//                    });
//            alertbox.setNeutralButton(getString(R.string.cancel),
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick( DialogInterface dialogInterface, int i ) {
//
//                            finish();
//
//                        }
//                    });
//
//            alertbox.show();
//        } else {
//            finish();
//        }
//    }

    private void addMenu() {

        list = new ArrayList<>();
        if (!consignment_no_pick.equals("0")) {
            MenuObject mo = new MenuObject();
            mo.IMAGE = R.drawable.ic_boxxx;
            mo.TEXT = "Picking Up";
            mo.CONSIGNMENT = consignment_no_pick;
            mo.BOXES = boxes_pick;
            mo.GLOBAL = global_total_pick;
            mo.DELIVERY_NO = delivery_no02;
            mo.PLAN_SEQ = plan_seq02;
            mo.isscaned_pick = isscaned_pick;
            mo.actual_seq = actual_seq;
            mo.total_load = total_load;
            mo.box_scanned = box_scanned_load;
            list.add(mo);
        }

        if (!consignment_no_deli.equals("0")) {
            MenuObject mo2 = new MenuObject();
            mo2.IMAGE = R.drawable.ic_delivery_truck1;
            mo2.TEXT = "Deliver";
            mo2.CONSIGNMENT = consignment_no_deli;
            mo2.BOXES = boxes_deli;
            mo2.GLOBAL = global_total_deli;
            mo2.DELIVERY_NO = delivery_no02;
            mo2.PLAN_SEQ = plan_seq02;
            mo2.isscaned_deli = isscaned_deli;
            mo2.actual_seq = actual_seq;
            mo2.total_load = total_unload;
            mo2.box_scanned = box_scanned_unload;
            list.add(mo2);

        }

        mainAdapter = new MainAdapter(list, getApplicationContext());
        rvMain.setAdapter(mainAdapter);
    }

    private void getSQLite() {

        int plan_seq = getIntent().getExtras().getInt("plan_seq");
        String delivery_no = getIntent().getExtras().getString("delivery_no");

        Log.d("JOBOPERATIONLOG", "plan_seq: " + plan_seq + "delivery_no: " + delivery_no);

        String sql = "select (select count(DISTINCT pl2.consignment_no ) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.plan_seq = pl.plan_seq  and pl2.delivery_no = pl.delivery_no and pl2.trash = pl.trash) as consignment_no_pick \n" +
                ", (select count(DISTINCT pl2.consignment_no ) from Plan pl2 where pl2.activity_type = 'UNLOAD' and pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and pl2.trash = pl.trash) as consignment_no_deli \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and pl2.trash = pl.trash) as boxes_pick \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'UNLOAD' and pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and pl2.trash = pl.trash) as boxes_deli \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and is_scaned = '0' and pl2.trash = pl.trash) as  pick_isscaned \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'UNLOAD' and pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and is_scaned = '0' and pl2.trash = pl.trash) as  deli_isscaned \n" +
                ", (select count( DISTINCT cm.global_no) from consignment cm inner join Plan pl2 on pl2.consignment_no = cm.consignment_no where pl2.activity_type = 'LOAD' and pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and pl2.trash = pl.trash) as global_total_pick \n" +
                ", (select count( DISTINCT cm.global_no) from consignment cm inner join Plan pl2 on pl2.consignment_no = cm.consignment_no where pl2.activity_type = 'UNLOAD' and pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and pl2.trash = pl.trash) as global_total_deli \n" +
                ", (select pl2.time_actual_in from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and pl2.trash = pl.trash LIMIT 1) as time_actual_in \n" +
                ", (select count(pl2.is_scaned) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and is_scaned = '0' and pl2.trash = pl.trash) as isscaned_pick \n" +
                ", (select count(pl2.is_scaned) from Plan pl2 where pl2.activity_type = 'UNLOAD' and pl2.plan_seq = pl.plan_seq and pl2.delivery_no = pl.delivery_no and is_scaned = '0' and pl2.trash = pl.trash) as isscaned_deli \n" +
                ", (select count(pl2.delivery_no)+1 from Plan pl2 where pl2.trash = '0' and pl2.actual_seq <> '0' and pl2.delivery_no = pl.delivery_no and pl2.activity_type = pl.activity_type) as actual_seq \n" +
                ",pl.delivery_no \n" +
                ",pl.plan_seq \n" +
                ",(SELECT sum(x1.total_box) from(select pl2.total_box from plan pl2 where pl2.delivery_no = pl.delivery_no and pl2.activity_type = 'LOAD' and pl2.plan_seq = pl.plan_seq and pl2.trash = pl.trash group by pl2.consignment_no)x1)as total_laod\n" +
                ",(SELECT sum(x1.total_box) from(select pl2.total_box from plan pl2 where pl2.delivery_no = pl.delivery_no and pl2.activity_type = 'UNLOAD' and pl2.plan_seq = pl.plan_seq and pl2.trash = pl.trash group by pl2.consignment_no)x1) as total_unlaod\n" +
                ",(select count(pl2.is_scaned) from plan pl2 where pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.activity_type = 'LOAD' and pl2.trash = pl.trash and pl2.is_scaned <> '0') as box_scanned_load " +
                ",(select count(pl2.is_scaned) from plan pl2 where pl2.delivery_no = pl.delivery_no and pl2.plan_seq = pl.plan_seq and pl2.activity_type = 'UNLOAD' and pl2.trash = pl.trash and pl2.is_scaned <> '0') as box_scanned_unload " +
                "from Plan pl \n" +
                "where pl.delivery_no = '" + delivery_no + "' and pl.plan_seq = '" + plan_seq + "' and pl.trash = '0'  " +
                "GROUP BY pl.delivery_no";
        Cursor cursor = databaseHelper.selectDB(sql);
        Log.d("JOBOPERATIONLOG", "total line " + cursor.getCount());
//        ArrayList<JobOperation_Model> jobOperationModels = new ArrayList<>();

        int sum = 0;
        int sum_scan = 0;
        cursor.moveToFirst();
        do {
            if (cursor.getCount() > 0) {
                Log.d("JOBOPERATIONLOG", "getSQLite: " + cursor.getString(cursor.getColumnIndex("global_total_pick")));
                consignment_no_pick = cursor.getString(cursor.getColumnIndex("consignment_no_pick"));
                consignment_no_deli = cursor.getString(cursor.getColumnIndex("consignment_no_deli"));
                boxes_pick = cursor.getString(cursor.getColumnIndex("boxes_pick"));
                boxes_deli = cursor.getString(cursor.getColumnIndex("boxes_deli"));
                global_total_pick = cursor.getString(cursor.getColumnIndex("global_total_pick"));
                global_total_deli = cursor.getString(cursor.getColumnIndex("global_total_deli"));

                delivery_no02 = cursor.getString(cursor.getColumnIndex("delivery_no"));
                plan_seq02 = cursor.getString(cursor.getColumnIndex("plan_seq"));
                isscaned_pick = cursor.getString(cursor.getColumnIndex("isscaned_pick"));
                isscaned_deli = cursor.getString(cursor.getColumnIndex("isscaned_deli"));
                actual_seq = cursor.getString(cursor.getColumnIndex("actual_seq"));
                total_load = cursor.getString(cursor.getColumnIndex("total_laod"));
                total_unload = cursor.getString(cursor.getColumnIndex("total_unlaod"));
                box_scanned_load = cursor.getString(cursor.getColumnIndex("box_scanned_load"));
                box_scanned_unload = cursor.getString(cursor.getColumnIndex("box_scanned_unload"));


                Log.d("JOBOPERATIONLOG", "onCreate: " + "==>" + isscaned_pick);
            }


        } while (cursor.moveToNext());


    }

    private void initView() {

        int plan_seq = getIntent().getExtras().getInt("plan_seq");
        String station_name = getIntent().getExtras().getString("station_name");
        String station_address = getIntent().getExtras().getString("station_address");
        String plan_in = getIntent().getExtras().getString("plan_in");
        String planIn = getIntent().getExtras().getString("plan_in");
        String delivery_no = getIntent().getExtras().getString("delivery_no");

        SharedPreferences data_detail_pick = getSharedPreferences("DATA_DETAIL_PICK", Context.MODE_PRIVATE);
        data_detail_pick.edit().putString("delivery_no", delivery_no).apply();
        data_detail_pick.edit().putString("plan_seq", String.valueOf(plan_seq)).apply();

        SharedPreferences data_detail_dialog_pick = getSharedPreferences("DATA_DETAIL_DIALOG_PICK", Context.MODE_PRIVATE);
        data_detail_dialog_pick.edit().putString("delivery_no", delivery_no).apply();
        data_detail_dialog_pick.edit().putString("plan_seq", String.valueOf(plan_seq)).apply();

        SharedPreferences data_detail_deli = getSharedPreferences("DATA_DETAIL_DELI", Context.MODE_PRIVATE);
        data_detail_deli.edit().putString("delivery_no", delivery_no).apply();
        data_detail_deli.edit().putString("plan_seq", String.valueOf(plan_seq)).apply();


        tvMain_truck = findViewById(R.id.tvMain_truck);
        tvMain_user_driver = findViewById(R.id.tvMain_user_driver);
        tvMain_name_driver = findViewById(R.id.tvMain_name_driver);
        tvMain_date_bar = findViewById(R.id.tvMain_date_bar);
        tvMain_planSeq = findViewById(R.id.tvMain_planSeq);
        tvMain_cusName = findViewById(R.id.tvMain_cusName);
        tvMain_address = findViewById(R.id.tvMain_address);
        tvMain_appoint = findViewById(R.id.tvMain_appoint);
        textView10 = findViewById(R.id.textView10);
        textView8 = findViewById(R.id.textView8);

        textView8.setText(getApplicationContext().getString(R.string.address) + ": ");
        textView10.setText(getApplicationContext().getString(R.string.appoint) + ": ");

        tvMain_truck.setText(" : " + Var.UserLogin.driver_truck_license);
        tvMain_user_driver.setText(" : " + Var.UserLogin.driver_user);
        tvMain_name_driver.setText(" : " + Var.UserLogin.driver_fname + " " + Var.UserLogin.driver_lname);
        tvMain_planSeq.setText(getString(R.string.drop) + ": " + plan_seq);

        tvMain_cusName.setText(station_name);
        tvMain_address.setText(station_address);
        if (!plan_in.equals("")) {
            String drop = dateNewFormat(plan_in);
            tvMain_appoint.setText(drop);
        } else {
            tvMain_appoint.setText("");
        }


        String CurrentLang = Locale.getDefault().getLanguage();

        if (CurrentLang.equals("en")) {
            String pattern = "EEEE, dd MMMM yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
            tvMain_date_bar.setText(sdf.format(Calendar.getInstance().getTime()));
        } else if (CurrentLang.equals("th")) {
            String pattern = "EEEE, dd MMMM yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("th", "th"));
            tvMain_date_bar.setText(sdf.format(Calendar.getInstance().getTime()));
        }
    }

    private String dateNewFormat(String pattern) {
        String pattern2 = "dd/MM/yyyy kk:mm";
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date newDate = null;
        try {
            newDate = spf.parse(pattern);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat(pattern2, new Locale("th", "th"));
        pattern = spf.format(newDate);

        return pattern;
    }
}
