package ws.epod;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ws.epod.Adapter.PlanSection;
import ws.epod.Adapter.PlanWorkAdapter;
import ws.epod.Client.APIClient;
import ws.epod.Client.APIInterface;
import ws.epod.Client.Structors.UploadImage;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.ObjectClass.SQLiteModel.Plan_model;
import ws.epod.ObjectClass.Var;

public class PlanWork_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvPlanWork;
    private PlanWorkAdapter planWorkAdapter;
    private ArrayList<String> planetList = new ArrayList();
    private TextView tvPlan_truck, tvPlan_user_driver, tvPlan_name_driver, tvPlan_date_bar, tvFilterStatus;

    ProgressDialog pd;
    ConnectionDetector netCon;
    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;

    //ImageView bt_refesh;
    public Context context = this;
    public android.os.Handler handler = null;
    public static Runnable runnable = null;

    boolean firstSync = false;

    String encodedImagePic1;
    String encodedImagePic2;
    String encodedImagePic3;

    private APIInterface apiInterface;

    FloatingActionButton ftPlan;

    SimpleDateFormat dateFormatter;


    ProgressDialog progressDialog;

    FloatingActionButton fabSearch, fabToday, fabFilterDate, bt_refesh;
    LinearLayout layoutToday, layoutFilterDate;
    Animation showButton, hideButton, showLayout, hideLayout;

    View viewFab;

    @Override
    protected void onStop() {
        super.onStop();
//        actionMenu.close(true);
        hideAll();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_plan_work_);
        narisv = new NarisBaseValue(PlanWork_Activity.this);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        toolbar = findViewById(R.id.toolbarPlanWork);

        viewFab = getWindow().getDecorView().findViewById(R.id.linearFab);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.delivery_plan);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));


        rvPlanWork = findViewById(R.id.rvPlanWork);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvPlanWork.setLayoutManager(layoutManager);

        rvPlanWork.addOnScrollListener(new CustomScrollListener());
//        actionMenu = findViewById(R.id.fab);
//        menu_item = findViewById(R.id.menu_item);
//        menu_item2 = findViewById(R.id.menu_item2);

        fabSearch = findViewById(R.id.fabSearch);
        fabToday = findViewById(R.id.fabToday);
        fabFilterDate = findViewById(R.id.fabFilterDate);

        layoutToday = findViewById(R.id.layoutToday);
        layoutFilterDate = findViewById(R.id.layoutFilterDate);


        showButton = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_button);
        hideButton = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_button);
        showLayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_layout);
        hideLayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_layout);


//        handler = new android.os.Handler();
//        runnable = new Runnable() {
//            public void run() {
//                startService(new Intent(getApplicationContext(),BackgroundService.class));
//            }
//        };
//        handler.postDelayed(runnable, 10000);

        inialView();
        //new UpLoadWork().execute();

    }


    public class CustomScrollListener extends RecyclerView.OnScrollListener {

        public CustomScrollListener() {
        }

        @SuppressLint("RestrictedApi")
        public void onScrollStateChanged( RecyclerView recyclerView, int newState ) {
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    Log.d("Asfas5f", "The RecyclerView is not scrolling");
                    fabSearch.setVisibility(View.VISIBLE);
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    Log.d("Asfas5f", "Scrolling now");
                    hideAll();
                    fabSearch.setVisibility(View.GONE);
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    Log.d("Asfas5f", "Scroll Settling");
                    hideAll();
                    fabSearch.setVisibility(View.GONE);
                    break;

            }

        }

        public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {
            if ( dx > 0 ) {
                System.out.println("Scrolled Right");
            } else if ( dx < 0 ) {
                System.out.println("Scrolled Left");
            } else {
                System.out.println("No Horizontal Scrolled");
            }

            if ( dy > 0 ) {
                System.out.println("Scrolled Downwards");
            } else if ( dy < 0 ) {
                System.out.println("Scrolled Upwards");
            } else {
                System.out.println("No Vertical Scrolled");
            }
        }
    }

    private void inialView() {

        tvPlan_truck = findViewById(R.id.tvPlan_truck);
        tvPlan_user_driver = findViewById(R.id.tvPlan_user_driver);
        tvPlan_name_driver = findViewById(R.id.tvPlan_name_driver);
        tvPlan_date_bar = findViewById(R.id.tvPlan_date_bar);
        bt_refesh = findViewById(R.id.bt_refesh);
        tvFilterStatus = findViewById(R.id.tvFilterStatus);

        tvPlan_truck.setText(" : " + Var.UserLogin.driver_truck_license);
        tvPlan_user_driver.setText(" : " + Var.UserLogin.driver_user);
        tvPlan_name_driver.setText(" : " + Var.UserLogin.driver_fname + " " + Var.UserLogin.driver_lname);

        String CurrentLang = Locale.getDefault().getLanguage();

        if ( CurrentLang.equals("en") ) {
            String pattern = "EEEE, dd MMMM yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
            tvPlan_date_bar.setText(sdf.format(Calendar.getInstance().getTime()));
        } else if ( CurrentLang.equals("th") ) {
            String pattern = "EEEE, dd MMMM yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("th", "th"));
            tvPlan_date_bar.setText(sdf.format(Calendar.getInstance().getTime()));
        }

        bt_refesh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                bt_refesh.startAnimation(animation);
                hideAll();
                new UploadWork2ND().execute();

            }
        });
//     fabButtontvFilterStatus
        fabSearch.setOnClickListener(view -> {
            if ( layoutToday.getVisibility() == View.VISIBLE && layoutFilterDate.getVisibility()
                    == View.VISIBLE ) {
                hideAll();

            } else {
                showAll();

            }
        });

        fabToday.setOnClickListener(view -> {
            hideAll();
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("th", "th"));
            String today = sdf.format(Calendar.getInstance().getTime());
            getDataFromSQLite(today, "", "");
            tvFilterStatus.setText("Filter by: Today");
        });
        fabFilterDate.setOnClickListener(view -> {
            hideAll();
            filterDateDialog();
        });


    }

    private void filterDateDialog() {

        View popupInputDialogView = null;
        AlertDialog alertDialog;


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        popupInputDialogView = layoutInflater.inflate(R.layout.filter_date_dialog, null);

        alertDialogBuilder.setView(popupInputDialogView);
        alertDialog = alertDialogBuilder.create();

        EditText edtFromDate = popupInputDialogView.findViewById(R.id.editText);
        EditText edtToDate = popupInputDialogView.findViewById(R.id.editText2);
        RelativeLayout button = popupInputDialogView.findViewById(R.id.button);
        ImageView imgClose_dialog = popupInputDialogView.findViewById(R.id.imgClose_dialog);
        TextView textView18 = popupInputDialogView.findViewById(R.id.textView18);


        edtFromDate.setSelection(edtFromDate.getText().length());
        edtToDate.setSelection(edtToDate.getText().length());

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        edtFromDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, ( datePicker, i, i1, i2 ) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(i, i1, i2);
                edtFromDate.setText(dateFormatter.format(newDate.getTime()));
                edtFromDate.setSelection(edtFromDate.getText().length());
                if ( !edtFromDate.getText().toString().trim().equals("") ) {
                    textView18.setVisibility(View.GONE);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        edtToDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, ( datePicker, i, i1, i2 ) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(i, i1, i2);
                edtToDate.setText(dateFormatter.format(newDate.getTime()));
                edtToDate.setSelection(edtToDate.getText().length());
                if ( !edtToDate.getText().toString().trim().equals("") ) {
                    textView18.setVisibility(View.GONE);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        button.setOnClickListener(view -> {
            String FilterFromDate = edtFromDate.getText().toString().trim();
            String FilterToDate = edtToDate.getText().toString().trim();

            if ( FilterFromDate.equals("") ) {
                textView18.setText("Please select from date.");
                textView18.setVisibility(View.VISIBLE);
            } else if ( FilterToDate.equals("") ) {
                textView18.setText("Please select to date.");
                textView18.setVisibility(View.VISIBLE);
            } else {
                getDataFromSQLite("", FilterFromDate, FilterToDate);
                tvFilterStatus.setText("Filter by: " + FilterFromDate + " to " + FilterToDate);
                alertDialog.dismiss();
            }


        });

        imgClose_dialog.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void hideAll() {

        layoutToday.startAnimation(hideLayout);
        layoutFilterDate.startAnimation(hideLayout);
        layoutToday.setVisibility(View.GONE);
        layoutFilterDate.setVisibility(View.GONE);
        fabSearch.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.filter));
        //fabSearch.startAnimation(hideButton);

    }

    private void showAll() {
        layoutToday.setVisibility(View.VISIBLE);
        layoutFilterDate.setVisibility(View.VISIBLE);
        layoutToday.startAnimation(showLayout);
        layoutFilterDate.startAnimation(showLayout);
        fabSearch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_close_black_24dp));
        //fabSearch.startAnimation(showButton);
    }


    @Override
    public void onResume() {
        super.onResume();

        //createCustomAnimation();

        // new LoadWork().execute();
        getDataFromSQLite("", "", "");
        tvFilterStatus.setText("Filter by: Today");
        if ( NarisBaseValue.firstlogin == 0 ) {
            new UploadWork2ND().execute();
            NarisBaseValue.firstlogin = 1;
        }


        //new getPlanWork().execute();
    }


    public class SendPostRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground( String... params ) {

            String postData = "";

            HttpURLConnection httpConnection = null;
            try {

                httpConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpConnection.setRequestMethod("POST");
                httpConnection.setDoOutput(true);

                DataOutputStream outputStream = new DataOutputStream(httpConnection.getOutputStream());
                outputStream.writeBytes("PostData=" + params[1]);
                outputStream.flush();
                outputStream.close();

                InputStream in = httpConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char currentData = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    postData += currentData;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if ( httpConnection != null ) {
                    httpConnection.disconnect();
                }
            }
            return postData;
        }

        @Override
        protected void onPostExecute( String result ) {
            super.onPostExecute(result);
        }
    }

    public class UploadWork2ND extends AsyncTask<String, String, String> {


        int IsSuccess = 1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PlanWork_Activity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Syncing data..");
            progressDialog.show();

        }

        @Override
        protected String doInBackground( String... strings ) {

            JSONObject Root = new JSONObject();
            JSONObject picture1 = new JSONObject();
            ArrayList<UploadImage.Data> uploadImage = new ArrayList<>();

//            String id_plan = "";
//            String url = Var.WEBSERVICE2 + "func=setPlan&driver_id=" + Var.UserLogin.driver_id;
//            String urlPic1 = "http://www.wisasoft.com:8997/TMS_MSM/resources/function/php/service.php?func=setImg";
            try {

                String sql = "select * from Plan where is_scaned <> '0' and status_upload= '0'";
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
                if ( cursor.getCount() > 0 ) {
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
                            contact.put("time_actual_in", cursor.getString(cursor.getColumnIndex("time_actual_in")));
                            contact.put("time_actual_out", cursor.getString(cursor.getColumnIndex("time_actual_out")));
                            contact.put("time_begin", cursor.getString(cursor.getColumnIndex("time_begin")));
                            contact.put("time_end", cursor.getString(cursor.getColumnIndex("time_end")));
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
                    if ( response.code() == 200 ) {
                        String responseRecieved = response.body().string();
                        if ( responseRecieved != null ) {
                            if ( !responseRecieved.equals("") ) {
                                JSONArray jsonArray = new JSONArray(responseRecieved);

                                if ( jsonArray.getJSONObject(0).getString("status").equals("Y") ) {
//
                                    for (int pic = 0; pic < jsonArray.getJSONObject(0).getJSONArray("returnId").length(); pic++) {

//                                        String json_data = jsonArray.getJSONObject(0).getJSONArray("returnId").getString(pic);
                                        //เปิดดทีหลัง
//                                        ContentValues cv = new ContentValues();
//                                        cv.put("status_upload", "1");
//                                        databaseHelper.db().update("Plan", cv, "id= '" + json_data + "'", null);

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
                                    if ( cursor_getPicture.getCount() > 0 ) {
                                        do {

                                            if ( !cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture1")).equals("") ) {

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

                                            if ( !cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture2")).equals("") ) {

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

                                            if ( !cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture3")).equals("") ) {

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


                                        if ( picture1 != null ) {

                                            Call<ResponseBody> callImg = apiInterface.uploadPicture(data);

                                            Response<ResponseBody> responseImg = callImg.execute();
                                            if ( responseImg.code() == 200 ) {
                                                String responseRecievedImg = responseImg.body().string();
                                                if ( responseRecievedImg != null ) {
                                                    if ( !responseRecievedImg.equals("") ) {
                                                        JSONArray jsonImg = new JSONArray(responseRecievedImg);

                                                        if ( jsonImg.getJSONObject(0).getString("status").equals("Y") ) {

                                                            for (int pic = 0; pic < jsonImg.getJSONObject(0).getJSONArray("img").length(); pic++) {

                                                                String json_data = jsonImg.getJSONObject(0).getJSONArray("img").getString(pic);
                                                                Log.d("TRD", "TRD_1: " + json_data);

                                                                // เปิดทีหลัง
//                                        ContentValues cv = new ContentValues();
//                                        cv.put("status_img", "1");
//                                        databaseHelper.db().update("image", cv, "name_img= '" + json_data + "'", null);

                                                            }

                                                        } else {
                                                            Log.d("TRD", "TRD_1: Fail");
                                                        }

                                                    }
                                                }
                                            }

                                        }//pic1
                                    }

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
        protected void onPostExecute( String s ) {
            super.onPostExecute(s);

            String mess = "";
            switch (IsSuccess) {
                case -1:
                    mess = "Synced (not found data)";
                    Snackbar.make(viewFab, mess, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
                case 1:
                    new DownloadWork().execute();
                    break;
                case 2:
                    mess = "Sync error!!";
                    Snackbar.make(viewFab, mess, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
            }
            if ( planWorkAdapter != null ) {
                planWorkAdapter.notifyDataSetChanged();
            }


        }
    }

    public class DownloadWork extends AsyncTask<String, String, String> {

        int IsSuccess = 1;

        @Override
        protected String doInBackground( String... strings ) {

            try {

                String max_modified_date = "";


                String sql_getMaxModifild_date = "select MAX(modified_date) as max_modified_date from Plan ";
                Cursor cursor_etMaxModifild_date = databaseHelper.selectDB(sql_getMaxModifild_date);
                cursor_etMaxModifild_date.moveToFirst();
                if ( cursor_etMaxModifild_date.getCount() > 0 ) {
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

                Call<ResponseBody> call = apiInterface.downloadWork(Var.UserLogin.driver_vehicle_id, Var.UserLogin.driver_id, Var.UserLogin.driver_serial, getDate, "");
                Response<ResponseBody> response = call.execute();
                if ( response.code() == 200 ) {
                    String responseRecieved = response.body().string();
                    if ( responseRecieved != null ) {
                        if ( !responseRecieved.equals("") ) {
                            Log.d("getPlanLog", "doInBackground: " + responseRecieved);

                            JSONArray jsonArray = new JSONArray(responseRecieved);
//                            for (int i = 0; i < jsonArray.length(); i++) {
//
//                                JSONObject json_data = jsonArray.getJSONObject(i);
//
//                            }


                            if ( narisv.INSERT_AS_SQL("Plan", jsonArray, "") ) {
                                Log.d("PlanWorkLOG", "SAVED INVOICE HEADER");

                                String url_consign = Var.WEBSERVICE2 + "func=getConsignment&vehicle_id=" + Var.UserLogin.driver_vehicle_id;

                                // JSONArray GETJSON_CONSIGN = narisv.getJsonFromUrl_reJsonArray(url_consign);

                                Call<ResponseBody> callCons = apiInterface.downloadConsignment(Var.UserLogin.driver_vehicle_id, "");
                                Response<ResponseBody> responseCons = callCons.execute();
                                if ( responseCons.code() == 200 ) {
                                    String responseRecievedCons = responseCons.body().string();
                                    if ( responseRecieved != null ) {
                                        if ( !responseRecieved.equals("") ) {
                                            JSONArray jsonArrayCons = new JSONArray(responseRecievedCons);
                                            if ( narisv.INSERT_AS_SQL("consignment", jsonArrayCons, "") ) {
                                                Log.d("PlanWorkLOG", "SAVED Consignment.");
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
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute( String s ) {
            super.onPostExecute(s);

            firstSync = true;

            String mess = "";
            switch (IsSuccess) {
                case 1:
                    mess = "Synced";
                    tvFilterStatus.setText("Filter by: Today");
                    getDataFromSQLite("", "", "");
                    if ( progressDialog.isShowing() ) {
                        progressDialog.dismiss();
                    }
                    Snackbar.make(viewFab, mess, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    if ( planWorkAdapter != null ) {
                        planWorkAdapter.notifyDataSetChanged();
                    }
                    break;
                case 2:
                    mess = "Sync error!!";
                    Snackbar.make(viewFab, mess, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    if ( planWorkAdapter != null ) {
                        planWorkAdapter.notifyDataSetChanged();
                    }
                    break;
            }


        }
    }

    public class UpLoadWork extends AsyncTask<String, String, String> {

        ProgressDialog pd;
        int IsSuccess = 1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(PlanWork_Activity.this);
            pd.setCancelable(false);
            pd.setMessage("Syncing data..");
            pd.show();

        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground( String... strings ) {


            JSONObject Root = new JSONObject();
            JSONObject picture1 = new JSONObject();

            String id_plan = "";
            String url = Var.WEBSERVICE2 + "func=setPlan&driver_id=" + Var.UserLogin.driver_id;
            String urlPic1 = "http://www.wisasoft.com:8997/TMS_MSM/resources/function/php/service.php?func=setImg";
            try {

                String sql = "select * from Plan where is_scaned <> '0' and status_upload= '0'";
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
                if ( cursor.getCount() > 0 ) {
                    do {

                        JSONObject contact = new JSONObject();

                        try {

                            id_plan = cursor.getString(cursor.getColumnIndex("id"));
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
                            contact.put("time_actual_in", cursor.getString(cursor.getColumnIndex("time_actual_in")));
                            contact.put("time_actual_out", cursor.getString(cursor.getColumnIndex("time_actual_out")));
                            contact.put("time_begin", cursor.getString(cursor.getColumnIndex("time_begin")));
                            contact.put("time_end", cursor.getString(cursor.getColumnIndex("time_end")));
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

                    JSONArray fa = narisv.SendAndGetJson_reJsonArray(Root, url);
                    if ( fa.getJSONObject(0).getString("status").equals("Y") ) {


                        for (int pic = 0; pic < fa.getJSONObject(0).getJSONArray("returnId").length(); pic++) {


                            String json_data = fa.getJSONObject(0).getJSONArray("returnId").getString(pic);

                            //เปิดดทีหลัง
//                                        ContentValues cv = new ContentValues();
//                                        cv.put("status_upload", "1");
//                                        databaseHelper.db().update("Plan", cv, "id= '" + json_data + "'", null);

                        }


                        String sql_getPicture = "select pl.id\n" +
                                ", ifnull((select im.name_img from image im where im.name_img = pl.picture1 and im.status_img = '0'), '') as picture1\n" +
                                ", ifnull((select im.name_img from image im where im.name_img = pl.picture2 and im.status_img = '0'), '') as picture2\n" +
                                ", ifnull((select im.name_img from image im where im.name_img = pl.picture3 and im.status_img = '0'), '') as picture3\n" +
                                "from plan pl\n" +
                                "where pl.is_scaned = '2'";
                        Cursor cursor_getPicture = databaseHelper.selectDB(sql_getPicture);

                        JSONArray imageArrayPic1 = new JSONArray();

                        Log.d("sdgwsdgwe", "doInBackground: --" + cursor_getPicture.getCount());
                        int j = 0;
                        cursor_getPicture.moveToFirst();
                        if ( cursor_getPicture.getCount() > 0 ) {
                            do {
                                JSONObject contactPic1 = new JSONObject();

                                //Log.d("sdgwsdgwe", "doInBackground: picture1--" + cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture1")));

                                if ( !cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture1")).equals("") ) {

                                    Log.d("AfWEIUGHMWENFNW", "doInBackground: --" + contactPic1.toString());

                                    contactPic1.put("id", cursor_getPicture.getString(cursor_getPicture.getColumnIndex("id")));
                                    contactPic1.put("name", cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture1")));
                                    contactPic1.put("seq", "1");

                                    File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture1")));
                                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                                    byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
                                    encodedImagePic1 = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                                    contactPic1.put("img", "data:image/jpeg;base64," + encodedImagePic1);

                                    imageArrayPic1.put(contactPic1);

                                }

                                if ( !cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture2")).equals("") ) {

                                    contactPic1 = new JSONObject();

                                    contactPic1.put("id", cursor_getPicture.getString(cursor_getPicture.getColumnIndex("id")));
                                    contactPic1.put("name", cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture2")));
                                    contactPic1.put("seq", "2");

                                    File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture2")));
                                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                                    byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
                                    encodedImagePic2 = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                                    contactPic1.put("img", "data:image/jpeg;base64," + encodedImagePic2);

                                    imageArrayPic1.put(contactPic1);

                                }

                                if ( !cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture3")).equals("") ) {

                                    contactPic1 = new JSONObject();

                                    contactPic1.put("id", cursor_getPicture.getString(cursor_getPicture.getColumnIndex("id")));
                                    contactPic1.put("name", cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture3")));
                                    contactPic1.put("seq", "3");

                                    File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Pictures/" + cursor_getPicture.getString(cursor_getPicture.getColumnIndex("picture3")));
                                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                                    byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
                                    encodedImagePic3 = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                                    contactPic1.put("img", "data:image/jpeg;base64," + encodedImagePic3);

                                    imageArrayPic1.put(contactPic1);

                                }


                            } while (cursor_getPicture.moveToNext());

                            picture1.put("data", imageArrayPic1);

                            Log.d("dataLog", "doInBackground: " + picture1);


                            if ( picture1 != null ) {
                                JSONArray sendPic1 = narisv.sendImageBase64(picture1, urlPic1);
                                if ( sendPic1.getJSONObject(0).getString("status").equals("Y") ) {

                                    for (int pic = 0; pic < sendPic1.getJSONObject(0).getJSONArray("img").length(); pic++) {

                                        String json_data = sendPic1.getJSONObject(0).getJSONArray("img").getString(pic);
                                        Log.d("TRD", "TRD_1: " + json_data);

//                                        ContentValues cv = new ContentValues();
//                                        cv.put("status_img", "1");
//                                        databaseHelper.db().update("image", cv, "name_img= '" + json_data + "'", null);

                                    }
                                } else {
                                    Log.d("TRD", "TRD_1: Fail");
                                }
                            }//pic1


                        }


                        new LoadWork().execute();
                        IsSuccess = 1;
                    } else {
                        IsSuccess = 0;
                    }

                } else {
                    new LoadWork().execute();
                    IsSuccess = 1;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute( String s ) {
            super.onPostExecute(s);


            String mess = "";
            switch (IsSuccess) {
                case -1:
                    mess = "Synced (not found data)";
                    break;
                case 1:
                    mess = "Synced";
                    getDataFromSQLite("", "", "");
                    tvFilterStatus.setText("Filter by: Today");
                    pd.dismiss();
                    break;
                case 0:
                    mess = "Sync error!!";
                    break;
            }
            Snackbar.make(rvPlanWork, mess, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            if ( planWorkAdapter != null ) {
                planWorkAdapter.notifyDataSetChanged();
            }
        }
    }

    public class LoadWork extends AsyncTask<String, String, String> {

        int IsSuccess = 1;


        @Override
        protected String doInBackground( String... strings ) {

            try {

                String max_modified_date = "";


                String sql_getMaxModifild_date = "select MAX(modified_date) as max_modified_date from Plan ";
                Cursor cursor_etMaxModifild_date = databaseHelper.selectDB(sql_getMaxModifild_date);
                cursor_etMaxModifild_date.moveToFirst();
                if ( cursor_etMaxModifild_date.getCount() > 0 ) {
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

                String url = Var.WEBSERVICE2 + "func=getPlan&vehicle_id=" + Var.UserLogin.driver_vehicle_id + "&driver_id=" + Var.UserLogin.driver_id + "&serial=" +
                        Var.UserLogin.driver_serial + "&phone_date=" + getDate + "&date+";
                Log.d("PlanWorkLOG", url);
                JSONArray GETJSON = narisv.getJsonFromUrl_reJsonArray(url);
                Log.d("PlanWorkLOG", GETJSON.toString());
                String sql2 = "";


                for (int i = 0; i < GETJSON.length(); i++) {

                    JSONObject json_data = GETJSON.getJSONObject(i);

                }

                if ( narisv.INSERT_AS_SQL("Plan", GETJSON, "") ) {
                    Log.d("PlanWorkLOG", "SAVED INVOICE HEADER");

                    String url_consign = Var.WEBSERVICE2 + "func=getConsignment&vehicle_id=" + Var.UserLogin.driver_vehicle_id;
                    JSONArray GETJSON_CONSIGN = narisv.getJsonFromUrl_reJsonArray(url_consign);

                    if ( narisv.INSERT_AS_SQL("consignment", GETJSON_CONSIGN, "") ) {
                        Log.d("PlanWorkLOG", "SAVED Consignment.");
                    } else {
                        Log.d("PlanWorkLOG", "FAIL save consignment.");
                    }
                    IsSuccess = 1;
                } else {
                    Log.d("PlanWorkLOG", "FAIL");
                    IsSuccess = 2;
                }


            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute( String s ) {
            super.onPostExecute(s);

            firstSync = true;

//            String mess = "";
//            switch (IsSuccess) {
//                case -1:
//                    mess = "Synced (not found data)";
//                    break;
//                case 1:
//                    mess = "LoadWork Synced";
//                    getDataFromsqlite();
//                    break;
//                case 2:
//                    mess = "LoadWork Sync error!!";
//                    break;
//            }
//            Snackbar.make(rvPlanWork, mess, Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//            planWorkAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate( String... values ) {
            super.onProgressUpdate(values);
        }


    }

    public void getDataFromSQLite( String today, String dateBegin, String dateEnd ) {

        String sql = "";
        if ( !dateBegin.equals("") && !dateEnd.equals("") ) {
            sql = "select pl.delivery_no\n" +
                    ", pl.delivery_date\n" +
                    ", count(DISTINCT pl.plan_seq) as plan_seq\n" +
                    ", (select count(DISTINCT pl2.consignment_no)\n" +
                    "        from Plan pl2\n" +
                    "        inner join consignment cm2 on cm2.consignment_no = pl2.consignment_no\n" +
                    "        where pl2.delivery_no = pl.delivery_no and pl2.activity_type = 'LOAD' and pl2.consignment_no = cm2.consignment_no  and cm2.trash = '0') as pick\n" +
                    ", (select count(DISTINCT pl2.consignment_no)\n" +
                    "        from Plan pl2\n" +
                    "        inner join consignment cm2 on cm2.consignment_no = pl2.consignment_no\n" +
                    "        where pl2.delivery_no = pl.delivery_no  and pl2.activity_type = 'UNLOAD' and pl2.consignment_no = cm2.consignment_no and cm2.trash = '0') as deli\n" +
                    "\n" +
                    ", (select count(DISTINCT pl2.consignment_no)\n" +
                    "        from Plan pl2\n" +
                    "        inner join consignment cm2 on cm2.consignment_no = pl2.consignment_no\n" +
                    "        where pl2.delivery_no = pl.delivery_no and pl2.activity_type = 'UNLOAD' and cm2.activity_type = 'UNLOAD' and pl2.consignment_no = cm2.consignment_no and cm2.trash = '0'\n" +
                    "        and (select count(cm3.deli_note_no) from consignment cm3 where cm3.status <> '0' and pl2.consignment_no = cm3.consignment_no)) as finish\n" +
                    "\n" +
                    "from Plan pl\n" +
                    "inner join consignment cm on cm.consignment_no = pl.consignment_no\n" +
                    "where (pl.delivery_date between '" + dateBegin + "' and '" + dateEnd + "') \n" +
                    "group by pl.delivery_no\n" +
                    "order by pl.delivery_date asc, pl.delivery_no";
        } else if ( !today.equals("") ) {
            sql = "select pl.delivery_no\n" +
                    ", pl.delivery_date\n" +
                    ", count(DISTINCT pl.plan_seq) as plan_seq\n" +
                    ", (select count(DISTINCT pl2.consignment_no)\n" +
                    "        from Plan pl2\n" +
                    "        inner join consignment cm2 on cm2.consignment_no = pl2.consignment_no\n" +
                    "        where pl2.delivery_no = pl.delivery_no and pl2.activity_type = 'LOAD' and pl2.consignment_no = cm2.consignment_no  and cm2.trash = '0') as pick\n" +
                    ", (select count(DISTINCT pl2.consignment_no)\n" +
                    "        from Plan pl2\n" +
                    "        inner join consignment cm2 on cm2.consignment_no = pl2.consignment_no\n" +
                    "        where pl2.delivery_no = pl.delivery_no  and pl2.activity_type = 'UNLOAD' and pl2.consignment_no = cm2.consignment_no and cm2.trash = '0') as deli\n" +
                    "\n" +
                    ", (select count(DISTINCT pl2.consignment_no)\n" +
                    "        from Plan pl2\n" +
                    "        inner join consignment cm2 on cm2.consignment_no = pl2.consignment_no\n" +
                    "        where pl2.delivery_no = pl.delivery_no and pl2.activity_type = 'UNLOAD' and cm2.activity_type = 'UNLOAD' and pl2.consignment_no = cm2.consignment_no and cm2.trash = '0'\n" +
                    "        and (select count(cm3.deli_note_no) from consignment cm3 where cm3.status <> '0' and pl2.consignment_no = cm3.consignment_no)) as finish\n" +
                    "\n" +
                    "from Plan pl\n" +
                    "inner join consignment cm on cm.consignment_no = pl.consignment_no\n" +
                    "where pl.delivery_date >= '" + today + "'\n" +
                    "group by pl.delivery_no\n" +
                    "order by pl.delivery_date asc, pl.delivery_no";
        } else {
            sql = "select pl.delivery_no\n" +
                    ", pl.delivery_date\n" +
                    ", count(DISTINCT pl.plan_seq) as plan_seq\n" +
                    ", (select count(DISTINCT pl2.consignment_no)\n" +
                    "        from Plan pl2\n" +
                    "        inner join consignment cm2 on cm2.consignment_no = pl2.consignment_no\n" +
                    "        where pl2.delivery_no = pl.delivery_no and pl2.activity_type = 'LOAD' and pl2.consignment_no = cm2.consignment_no  and cm2.trash = '0') as pick\n" +
                    ", (select count(DISTINCT pl2.consignment_no)\n" +
                    "        from Plan pl2\n" +
                    "        inner join consignment cm2 on cm2.consignment_no = pl2.consignment_no\n" +
                    "        where pl2.delivery_no = pl.delivery_no  and pl2.activity_type = 'UNLOAD' and pl2.consignment_no = cm2.consignment_no and cm2.trash = '0') as deli\n" +
                    "\n" +
                    ", (select count(DISTINCT pl2.consignment_no)\n" +
                    "        from Plan pl2\n" +
                    "        inner join consignment cm2 on cm2.consignment_no = pl2.consignment_no\n" +
                    "        where pl2.delivery_no = pl.delivery_no and pl2.activity_type = 'UNLOAD' and cm2.activity_type = 'UNLOAD' and pl2.consignment_no = cm2.consignment_no and cm2.trash = '0'\n" +
                    "        and (select count(cm3.deli_note_no) from consignment cm3 where cm3.status <> '0' and pl2.consignment_no = cm3.consignment_no)) as finish\n" +
                    "\n" +
                    "from Plan pl\n" +
                    "inner join consignment cm on cm.consignment_no = pl.consignment_no\n" +
                    "where pl.delivery_date >= date('now')\n" +
                    "group by pl.delivery_no\n" +
                    "order by pl.delivery_date asc, pl.delivery_no";
        }


        //ทำต่อ 5/11/2562
//        select pl.delivery_no
//                , pl.delivery_date
//                , count(DISTINCT pl.plan_seq) as plan_seq
//, (select count(DISTINCT pl2.consignment_no)
//        from Plan pl2
//        inner join consignment cm2 on cm2.consignment_no = pl2.consignment_no
//        where pl2.delivery_no = pl.delivery_no and pl2.activity_type = 'LOAD' and pl2.consignment_no = cm2.consignment_no and cm2.activity_type = 'LOAD' and cm2.trash = '0') as pick
//, (select count(DISTINCT pl2.consignment_no)
//        from Plan pl2
//        inner join consignment cm2 on cm2.consignment_no = pl2.consignment_no
//        where pl2.delivery_no = pl.delivery_no and cm2.activity_type = 'UNLOAD' and pl2.activity_type = 'UNLOAD' and pl2.consignment_no = cm2.consignment_no and cm2.trash = '0') as dili
//
//, (select count(DISTINCT pl2.consignment_no)
//        from Plan pl2
//        inner join consignment cm2 on cm2.consignment_no = pl2.consignment_no
//        where pl2.delivery_no = pl.delivery_no  and pl2.activity_type = 'UNLOAD' and pl2.consignment_no = cm2.consignment_no and cm2.trash = '0'
//        and (select count(cm3.deli_note_no) from consignment cm3 where cm3.status <> '0' and pl2.consignment_no = cm3.consignment_no)) as finnish
//
//        from Plan pl
//        inner join consignment cm on cm.consignment_no = pl.consignment_no
//        where pl.delivery_no = 'J201910000053'
//        group by pl.delivery_no
//        order by pl.delivery_date desc, pl.delivery_no

        ArrayList<Plan_model> studentArrayList = new ArrayList<>();
        ArrayList<String> dateArray = new ArrayList<>();
        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();

        Cursor cursor = databaseHelper.selectDB(sql);

        Log.d("getDataFromsqlite", "total line " + cursor.getCount());

        cursor.moveToFirst();
        do {
            if ( cursor.getCount() > 0 ) {
                String delivery_date = cursor.getString(cursor.getColumnIndex("delivery_date"));
                String delivery_no = cursor.getString(cursor.getColumnIndex("delivery_no"));
                int plan_seq = cursor.getInt(cursor.getColumnIndex("plan_seq"));
                String pick = cursor.getString(cursor.getColumnIndex("pick"));
                String deli = cursor.getString(cursor.getColumnIndex("deli"));
                String finish = cursor.getString(cursor.getColumnIndex("finish"));

                dateArray.add(delivery_date);
                studentArrayList.add(new Plan_model(delivery_date, delivery_no, plan_seq, pick, deli, finish));
            }

        } while (cursor.moveToNext());

        //ตัดวันที่ซ้ำออก
        HashSet hs = new HashSet();
        hs.addAll(dateArray);
        dateArray.clear();
        dateArray.addAll(hs);

        Collections.sort(dateArray, ( s, t1 ) -> {
            if ( s == null || t1 == null )
                return 0;
            return s.compareTo(t1);

        });

        for (int i = 0; i < dateArray.size(); i++) {
            final ArrayList<Plan_model> item = new ArrayList<>();

            for (Plan_model planModel : studentArrayList) {

                String mDate = planModel.getDelivery_date();
                if ( dateArray.get(i).equals(mDate) ) {
                    item.add(planModel);
                }

            }

            sectionAdapter.addSection(new PlanSection(dateNewFormat(dateArray.get(i)), item, getApplicationContext()));
        }


        //planWorkAdapter = new PlanWorkAdapter(studentArrayList, getApplicationContext());

        rvPlanWork.setAdapter(sectionAdapter);
        cursor.close();

    }

    public static String dateNewFormat( String pattern ) {
        String pattern2 = "dd/MM/yyyy";
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
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

    private void insertDumpdata() {

        new AsyncTask<String, String, String>() {

            int IsSuccess = 0;

            @Override
            protected String doInBackground( String... strings ) {

                try {
                    String url = "http://wisasoft.com:8997/EPOD_MSM/EPOD_MSMMOBILE/jsonDump.php";
                    Log.d("dumpData", url);
                    /* Service : Check login */
                    JSONArray re_json = narisv.getJsonFromUrl_reJsonArray(url);
                    Log.d("dumpData", re_json.toString());

                    if ( narisv.INSERT_AS_SQL("Plan", re_json, "") ) {
                        Log.d("dumpData", "INSERT JSON SUCCESS");

                        Var.PlanObject.truck_license = re_json.getJSONObject(0).getString("truck_license");
                        Var.PlanObject.delivery_date = re_json.getJSONObject(0).getString("delivery_date");
                        Var.PlanObject.round_no = re_json.getJSONObject(0).getInt("round_no");
                        Var.PlanObject.delivery_no = re_json.getJSONObject(0).getString("delivery_no");
                        Var.PlanObject.customer_id = re_json.getJSONObject(0).getInt("customer_id");
                        Var.PlanObject.customer_code = re_json.getJSONObject(0).getString("customer_code");
                        Var.PlanObject.customer_name = re_json.getJSONObject(0).getString("customer_name");
                        Var.PlanObject.station_id = re_json.getJSONObject(0).getInt("station_id");
                        Var.PlanObject.station_code = re_json.getJSONObject(0).getString("station_code");
                        Var.PlanObject.station_name = re_json.getJSONObject(0).getString("station_name");
                        Var.PlanObject.station_address = re_json.getJSONObject(0).getString("station_address");
                        Var.PlanObject.station_lat = re_json.getJSONObject(0).getDouble("station_lat");
                        Var.PlanObject.station_lon = re_json.getJSONObject(0).getDouble("station_lon");
                        Var.PlanObject.station_area = re_json.getJSONObject(0).getInt("station_area");
                        Var.PlanObject.plan_seq = re_json.getJSONObject(0).getInt("plan_seq");
                        Var.PlanObject.plan_in = re_json.getJSONObject(0).getString("plan_in");
                        Var.PlanObject.plan_out = re_json.getJSONObject(0).getString("plan_out");
                        Var.PlanObject.plan_distance = re_json.getJSONObject(0).getString("plan_distance");

                        IsSuccess = 1;
                    } else {
                        Log.d("dumpData", "INSERT JSON FAIL");
                        IsSuccess = 0;
                    }
                } catch (Exception e) {
                    Log.d("dumpData", "_02");
                    IsSuccess = 0;//0
                }
                return null;

            }

            @Override
            protected void onPostExecute( String s ) {
                super.onPostExecute(s);
                if ( IsSuccess == 1 ) {

                }
            }
        }.execute();

    }


    public class Logout extends AsyncTask<String, String, String> {

        int IsSuccess = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(PlanWork_Activity.this);
            pd.setCancelable(false);
            pd.setMessage(getString(R.string.connecting_to_server));
            pd.show();

            Log.d("NARISLOG", "Logout running");
        }

        @Override
        protected String doInBackground( String... params ) {
            try {
                String username = Var.UserLogin.driver_user;
                String serial = Var.UserLogin.driver_serial;

                Call<ResponseBody> call = apiInterface.logout(username, serial);
                Response<ResponseBody> response = call.execute();
                if ( response.code() == 200 ) {
                    String responseRecieved = response.body().string();
                    if ( responseRecieved != null ) {
                        if ( !responseRecieved.equals("") ) {
                            JSONArray jsonArray = new JSONArray(responseRecieved);

                            if ( jsonArray.getJSONObject(0).getString("status").equals("Y") ) { // success
                                Log.d("IsLog", "01");
                                IsSuccess = 1;

                            } else {
                                Log.d("IsLog", "02");
                                IsSuccess = 0;
                            }

                        }
                    }
                }

            } catch (Exception e) {
                Log.d("IsLog", "03");
                IsSuccess = 0;
            }


            return null;
        }

        @Override
        protected void onProgressUpdate( String... values ) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute( String s ) {
            super.onPostExecute(s);


            if ( IsSuccess == 1 ) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( pd != null && pd.isShowing() ) {
            pd.cancel();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {


    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.munu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        switch (item.getItemId()) {
            case R.id.logout_menu:


                Log.d("NARISLOG", "Logout Alert..");

                if ( netCon.isConnectingToInternet() ) {
                    AlertDialog alert = new AlertDialog.Builder(PlanWork_Activity.this).create();
                    alert.setTitle(R.string.doyouwanttologout);
                    alert.setMessage(getString(R.string.confirm_logout));
                    alert.setButton(getString(R.string.logout), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {
                            new Logout().execute();
                            Log.d("NARISLOG", "Logout confirm..");
                        }
                    });
                    alert.setButton2(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {

                        }
                    });
                    alert.show();
                } else {

                    Log.d("status_login", "isLogin => " + Var.UserLogin.driver_status_login);

                    if ( Var.UserLogin.driver_status_login.equals("1") ) {

                        AlertDialog alert = new AlertDialog.Builder(PlanWork_Activity.this).create();
                        alert.setTitle(R.string.doyouwanttologout);
                        alert.setMessage(getString(R.string.confirm_logout));
                        alert.setButton(getString(R.string.logout), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick( DialogInterface dialog, int which ) {

                                //update status login
                                ContentValues cv = new ContentValues();
                                cv.put("status_login", "0");
                                databaseHelper.db().update("login", cv, "driver_id= '" + Var.UserLogin.driver_id + "'", null);

                                //select to view status login
                                String sql = "select * from login where username = '" + Var.UserLogin.driver_user + "' and pass = '" + Var.UserLogin.driver_pass + "' ";
                                Cursor c = databaseHelper.selectDB(sql);
                                c.moveToFirst();
                                Log.d("status_login", "Logged out ==> " + c.getString(c.getColumnIndex("status_login")));

                                finish();

                            }
                        });
                        alert.setButton2(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick( DialogInterface dialog, int which ) {

                            }
                        });
                        alert.show();
                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
