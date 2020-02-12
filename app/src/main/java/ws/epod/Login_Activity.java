package ws.epod;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ws.epod.Client.APIClient;
import ws.epod.Client.APIInterface;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.Var;

import static android.Manifest.permission.READ_CONTACTS;

public class Login_Activity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    private Toolbar toolbar;
    private TextView serial;
    private Button btnLogin;
    private EditText edtUsername, edtPassword;
    private String Username = "", password = "";
    private PopupWindow changeStatusPopUp;

    //private View mProgressView;
    private View mLoginFormView;

    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;
    ProgressDialog pd;
    String imei = "";
    String deviceName = "";
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    String getDate = "";
    boolean checkPermiss = false;

    ConnectionDetector netCon;

    ImageView show_pass_btn;

    private APIInterface apiInterface;

    public android.os.Handler handler = null;
    public static Runnable runnable = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( pd != null && pd.isShowing() ) {
            pd.cancel();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( Void... voids ) {

                checkPermissions();
                return null;
            }

            @Override
            protected void onPostExecute( Void aVoid ) {


                CheckDatabaseStructure();


                super.onPostExecute(aVoid);
            }
        }.execute();

        SharedPreferences login_data = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        String status_login= login_data.getString("status_login", null);
        if(status_login!= null){
            if(status_login.equals("1")){

                Var.UserLogin.driver_id = login_data.getString("driver_id", "");
                Var.UserLogin.driver_user = login_data.getString("username", "");
                Var.UserLogin.driver_pass = login_data.getString("pass", "");
                Var.UserLogin.driver_serial = login_data.getString("serial", "");
                Var.UserLogin.driver_brand = login_data.getString("driver_brand", "");
                Var.UserLogin.driver_truck_license = login_data.getString("vehicle_name", "");
                Var.UserLogin.driver_fname = login_data.getString("driver_fname", "");
                Var.UserLogin.driver_lname = login_data.getString("driver_lname", "");
                Var.UserLogin.driver_vehicle_id = login_data.getString("vehicle_id", "");
                Var.UserLogin.driver_status_login = login_data.getString("status_login", "");
                Intent intent= new Intent(getApplicationContext(), PlanWork_Activity.class);
                startActivity(intent);
            }
        }

//        isLogin();

    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        netCon = new ConnectionDetector(getApplicationContext());


        final SharedPreferences user_data = this.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        Username = user_data.getString("Username", "");
        password = user_data.getString("Password", "");
        boolean isChange = user_data.getBoolean("isChange", false);

        SharedPreferences Language_Locale = getSharedPreferences("PREFERENCE_LANGUAGE", Context.MODE_PRIVATE);
        String language = Language_Locale.getString("LANGUAGE_KEY", "ENGLISH");
        if ( language.equals("ENGLISH") ) {
            Configuration config = new Configuration();
            config.locale = Locale.ENGLISH;
            getResources().updateConfiguration(config, null);
            Language_Locale.edit().putString("LANGUAGE_KEY", "ENGLISH").apply();
        } else {
            Configuration config = new Configuration();
            config.locale = new Locale("th");
            getResources().updateConfiguration(config, null);
            Language_Locale.edit().putString("LANGUAGE_KEY", "THAI").apply();

        }
        setContentView(R.layout.activity_login_);
        final Toast tag = Toast.makeText(getBaseContext(), "Hello", Toast.LENGTH_SHORT);

        toolbar = findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.login);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        apiInterface = APIClient.getClient().create(APIInterface.class);


        new CountDownTimer(500, 1) {
            public void onTick( long millisUntilFinished ) {
                tag.show();
            }

            public void onFinish() {
                tag.cancel();
            }

        }.start();

        serial = findViewById(R.id.tvSerial);
        btnLogin = findViewById(R.id.btnLogin);
        show_pass_btn = findViewById(R.id.show_pass_btn);

        String pattern = "yyyy-MM-dd kk:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
        getDate = sdf.format(Calendar.getInstance().getTime());

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);

        if ( !Username.equals("") ) {
            user_data.edit().putBoolean("isChange", false).apply();
//            edtUsername.setText(Username);
//            edtPassword.setText(password);
            if ( isChange ) {
                user_data.edit().putBoolean("isChange", false).apply();
                edtUsername.setText(Username);
                edtPassword.setText(password);
            }

        }


        Username = edtUsername.getText().toString();
        password = edtPassword.getText().toString();

        //isStoragePermissionGranted();

        /*isReadStoragePermissionGranted();
        isWriteStoragePermissionGranted();
        isCameraePermissionGranted();
*/
        databaseHelper = new DatabaseHelper(getApplicationContext());
        narisv = new NarisBaseValue(Login_Activity.this);


        String val = Build.VERSION.RELEASE;
        val = val.replace(".", "");
        if ( Integer.parseInt(val) >= 600 ) {
            // only for gingerbread and newer versions
            if ( checkAndRequestPermissions() ) {

                // carry on the normal flow, as the case of  permissions  granted.
            } else {
                try {
                    Thread.sleep(15000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }


        if ( checkPermissions() ) {
            CheckDatabaseStructure();
            //  permissions  granted.
        }

        //set serial
        imei = narisv.getSerial();
        serial.setText(": " + imei);


        edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction( TextView textView, int i, KeyEvent keyEvent ) {
                if ( i == EditorInfo.IME_NULL ) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        //button onclick
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {


                //attemptLogin();
                Username = edtUsername.getText().toString().trim();
                password = edtPassword.getText().toString().trim();
                SharedPreferences user_data3 = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
                user_data3.edit().putString("Username", Username).apply();
                user_data3.edit().putString("Password", password).apply();
                user_data3.edit().putBoolean("isChange", true).apply();
                clearOldData();
//                new Authen_Online().execute(edtUsername.getText().toString().trim(), edtPassword.getText().toString().trim(), imei);
                // startActivity(new Intent(getApplicationContext(), PlanWork_Activity.class));

                loginSync(Username, password, imei);

            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        //mProgressView = findViewById(R.id.login_progress);

        showAndHidePassword();

    }

    private void isLogin(){
        SharedPreferences login_data = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        String status_login= login_data.getString("status_login", null);
        if(login_data != null){
            if(status_login.equals("1")){
                Intent intent = new Intent(getApplicationContext(), PlanWork_Activity.class);
                startActivity(intent);
            }

        }
    }

    private void showAndHidePassword() {

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {
            }

            public void onTextChanged( CharSequence s, int start, int before, int count ) {
                if ( edtPassword.getText().toString().length() > 0 ) {
                    show_pass_btn.setVisibility(View.VISIBLE);
                } else {
                    show_pass_btn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged( Editable editable ) {
                if ( edtPassword.getText().toString().length() > 0 ) {
                    show_pass_btn.setVisibility(View.VISIBLE);

                    show_pass_btn.setOnClickListener(view -> {
                        Log.d("sdfkj", "showAndHidePassword: ");
                        if ( edtPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()) ) {
                            ( (ImageView) ( view ) ).setImageResource(R.drawable.ic_visibility_off_black_24dp);
                            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            edtPassword.setSelection(edtPassword.getText().length());
                        } else {
                            ( (ImageView) ( view ) ).setImageResource(R.drawable.ic_remove_red_eye_black_24dp);
                            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            edtPassword.setSelection(edtPassword.getText().length());
                        }
                    });

                } else {
                    show_pass_btn.setVisibility(View.GONE);
                }
            }
        });


    }

    @SuppressLint("StaticFieldLeak")
    private void loginSync( final String user, final String pass, final String serail ) {

        new AsyncTask<Void, Void, Integer>() {

            ProgressDialog progressDialog;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(Login_Activity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.checking));
                progressDialog.show();

            }

            @Override
            protected Integer doInBackground( Void... voids ) {

                int IsSuccess = 0;

                Call<ResponseBody> call = apiInterface.login2nd(user, pass, serail);
       //         Call<ResponseBody> call = apiInterface.login2nd(user, pass, "c49ac656");
                try {
                    Response<ResponseBody> response = call.execute();
                    if ( response.code() == 200 ) {
                        String responseRecieved = response.body().string();
                        if ( responseRecieved != null ) {
                            if ( !responseRecieved.equals("") ) {
                                JSONArray jsonArray = new JSONArray(responseRecieved);
                                Log.d("sa89a6", "####");
                                if ( jsonArray.getJSONObject(0).getString("status").equals("Y") ) { // success

                                    jsonArray.getJSONObject(0).remove("status");
                                    Log.d("NARISLOG", "####" + jsonArray.toString());

                                    if (narisv.INSERT_AS_SQL("login", jsonArray, "")) {
                                        Log.d("insertLogin", "success: ");
                                    }else{
                                        Log.d("insertLogin", "fial: ");
                                    }

                                    SharedPreferences login_data = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
                                    Log.d("login_data", "####" + jsonArray.getJSONObject(0).getString("driver_id"));

                                    login_data.edit().putString("driver_id", jsonArray.getJSONObject(0).getString("driver_id")).apply();
                                    login_data.edit().putString("username", jsonArray.getJSONObject(0).getString("username")).apply();
                                    login_data.edit().putString("pass", jsonArray.getJSONObject(0).getString("pass")).apply();
                                    login_data.edit().putString("serial", jsonArray.getJSONObject(0).getString("serial")).apply();
                                    login_data.edit().putString("driver_brand", Build.BRAND).apply();
                                    login_data.edit().putString("vehicle_name", jsonArray.getJSONObject(0).getString("vehicle_name")).apply();
                                    login_data.edit().putString("driver_fname", jsonArray.getJSONObject(0).getString("driver_fname")).apply();
                                    login_data.edit().putString("driver_lname", jsonArray.getJSONObject(0).getString("driver_lname")).apply();
                                    login_data.edit().putString("vehicle_id", jsonArray.getJSONObject(0).getString("vehicle_id")).apply();
                                    login_data.edit().putString("status_login", jsonArray.getJSONObject(0).getString("status_login")).apply();


                                    SaveLastLogin();
                                    IsSuccess = 1;


//                                    if ( narisv.INSERT_AS_SQL_NO_REPLACE("login", jsonArray, "") ) {
//                                        Log.d("NARISLOG", "INSERT JSON SUCCESS");
//
//                                        String sql = "INSERT OR REPLACE into login (driver_brand) values('" + Build.BRAND + "')";
//                                        databaseHelper.execDB(sql);

//                                        Var.UserLogin.driver_id = jsonArray.getJSONObject(0).getString("driver_id");
//                                        Var.UserLogin.driver_user = jsonArray.getJSONObject(0).getString("username");
//                                        Var.UserLogin.driver_pass = jsonArray.getJSONObject(0).getString("pass");
//                                        Var.UserLogin.driver_serial = jsonArray.getJSONObject(0).getString("serial");
//                                        Var.UserLogin.driver_brand = android.os.Build.BRAND;
//                                        Var.UserLogin.driver_truck_license = jsonArray.getJSONObject(0).getString("vehicle_name");
//                                        Var.UserLogin.driver_fname = jsonArray.getJSONObject(0).getString("driver_fname");
//                                        Var.UserLogin.driver_lname = jsonArray.getJSONObject(0).getString("driver_lname");
//                                        Var.UserLogin.driver_vehicle_id = jsonArray.getJSONObject(0).getString("vehicle_id");
//                                        Var.UserLogin.driver_status_login = jsonArray.getJSONObject(0).getString("status_login");
//
//                                        SaveLastLogin();
//                                        IsSuccess = 1;
//                                    } else {
//                                        Log.d("NARISLOG", "INSERT JSON FAIL");
//                                        Log.d("isSuccess_0", "_01");
//                                        IsSuccess = 0; //0 sakito_config
//                                    }
                                } else if ( jsonArray.getJSONObject(0).getString("status").equals("N") && jsonArray.getJSONObject(0).getString("type").equals("U") ) { // fail
                                    Log.d("NARISLOG", "NOT FOUND USER : " + jsonArray.getJSONObject(0).getString("message"));
                                    IsSuccess = -1; //-1
                                } else if ( jsonArray.getJSONObject(0).getString("status").equals("N") && jsonArray.getJSONObject(0).getString("type").equals("P") ) {
                                    Log.d("NARISLOG", "NOT FOUND USER : " + jsonArray.getJSONObject(0).getString("message"));
                                    IsSuccess = -1;//-1
                                } else if ( jsonArray.getJSONObject(0).getString("status").equals("N") && jsonArray.getJSONObject(0).getString("type").equals("S") ) {
                                    Log.d("NARISLOG", "NOT FOUND USER : " + jsonArray.getJSONObject(0).getString("message"));
                                    IsSuccess = -2;//-2
                                } else if ( netCon.isConnectingToInternet() ) {
                                    Log.d("isSuccess_0", "0");
                                    IsSuccess = 0;//0
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    Log.d("isSuccess_0", "00");
                    IsSuccess = 0;
                    e.printStackTrace();
                } catch (JSONException e) {
                    IsSuccess = 0;
                    Log.d("isSuccess_0", "000");
                    e.printStackTrace();
                }


                return IsSuccess;
            }

            @Override
            protected void onPostExecute( Integer IsSuccess ) {
                super.onPostExecute(IsSuccess);

                Log.d("asda56s",IsSuccess+"");

                if ( IsSuccess == 1 ) {
                    SharedPreferences login_get = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
                    Var.UserLogin.driver_id = login_get.getString("driver_id", "");
                    Var.UserLogin.driver_user = login_get.getString("username", "");
                    Var.UserLogin.driver_pass = login_get.getString("pass", "");
                    Var.UserLogin.driver_serial = login_get.getString("serial", "");
                    Var.UserLogin.driver_brand = login_get.getString("driver_brand", "");
                    Var.UserLogin.driver_truck_license = login_get.getString("vehicle_name", "");
                    Var.UserLogin.driver_fname = login_get.getString("driver_fname", "");
                    Var.UserLogin.driver_lname = login_get.getString("driver_lname", "");
                    Var.UserLogin.driver_vehicle_id = login_get.getString("vehicle_id", "");
                    Var.UserLogin.driver_status_login = login_get.getString("status_login", "");

                   // login_get.edit().clear();

                    // Toast.makeText(Login_Activity.this, "Success !", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Login_Activity.this, PlanWork_Activity.class);
                    startActivity(i);
                } else if ( IsSuccess == -1 ) {
                    AlertDialog alert = new AlertDialog.Builder(Login_Activity.this).create();
                    alert.setTitle(getString(R.string.not_found));
                    alert.setMessage(getString(R.string.u_or_p_incorrect));
                    alert.setButton2(getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {

                        }
                    });
                    alert.show();

                    // attemptLogin();
                } else if ( IsSuccess == -2 ) {
                    AlertDialog alert = new AlertDialog.Builder(Login_Activity.this).create();
                    alert.setTitle("Serial !!");
                    alert.setMessage(getString(R.string.serial_not_match));
                    alert.setButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {

                        }
                    });
                    alert.show();
                } else if ( IsSuccess == 0 ) {
                    //Toast.makeText(Login_Activity.this, "Fail", Toast.LENGTH_SHORT).show();
                    AlertDialog alert = new AlertDialog.Builder(Login_Activity.this).create();
                    alert.setTitle(getString(R.string.server_connection));
                    alert.setMessage(getString(R.string.cannot_connect_to_the_server));
                    alert.setButton(getString(R.string.use_offline), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {
                            new Authen_Offline().execute(edtUsername.getText().toString(), edtPassword.getText().toString());
                        }
                    });
                    alert.setButton2(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {

                        }
                    });
                    alert.show();
                }

                progressDialog.dismiss();

            }
        }.execute();


//        pd = new ProgressDialog(Login_Activity.this);
//        pd.setCancelable(false);
//        pd.setMessage(getString(R.string.checking));
//        pd.show();
//
//        Call<List<Login>> call = apiInterface.login("sa", "1234", "330014f6a724236b");
//        call.enqueue(new Callback<List<Login>>() {
//            @Override
//            public void onResponse( Call<List<Login>> call, Response<List<Login>> response ) {
//                if ( response.code() == 200 ) {
//
//                    List<Login> login = response.body();
//                    String status = login.get(0).status;
//
//                    if ( status.equals("Y") ) {
//                        Log.d("respons", "onResponse: " + login.get(0).driverFname);
//                    } else {
//                        Log.d("respons", "onResponse: " + login.get(0).message);
//                    }
//
//                    pd.dismiss();
//
//                }
//            }
//
//            @Override
//            public void onFailure( Call<List<Login>> call, Throwable t ) {
//                call.cancel();
//                Log.d("errorCall", "onFailure: " + t.getMessage());
//                pd.dismiss();
//            }
//        });

    }


    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE };

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if ( result != PackageManager.PERMISSION_GRANTED ) {
                listPermissionsNeeded.add(p);

            }
        }
        if ( !listPermissionsNeeded.isEmpty() ) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        // CheckDatabaseStructure();
        checkPermiss = true;
        return true;
    }

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissionsList, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("TAG", "onRequestPermissionsResult: ");
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {

                if ( grantResults.length > 0 ) {
                    String permissionsDenied = "";
                    for (String per : permissionsList) {
                        if ( grantResults[0] == PackageManager.PERMISSION_DENIED ) {
                            permissionsDenied += "\n" + per;
                            Log.d("TAG", "onRequestPermissionsResult: 1");
                        }

                    }
                    CheckDatabaseStructure();
                    Log.d("TAG", "onRequestPermissionsResult: " + permissionsDenied);
                    // Show permissionsDenied
                    //updateViews();
                }


                //  permissions  granted.

                return;
            }
        }

    }



    private void CheckDatabaseStructure() {

        String sql = "";

        sql = "CREATE TABLE IF NOT EXISTS Plan (id TEXT(255,0), delivery_date TEXT(255,0), vehicle_name TEXT(255,0), blackbox TEXT(255,0), delivery_no TEXT(255,0), " +
                "plan_seq TEXT(255,0), station_id TEXT(255,0), station_code TEXT(255,0), station_name TEXT(255,0), station_address TEXT(255,0)," +
                "station_lat Decimal(9,6), station_lon Decimal(8,6), station_area TEXT(255,0), plan_in TEXT(255,0), plan_out TEXT(255,0)" +
                ", consignment_no TEXT(255,0), order_no TEXT(255,0), activity_type TEXT(255,0), box_no TEXT(255,0), waybill_no TEXT(255,0), weight TEXT(255,0)" +
                ", actual_seq TEXT(255,0), actual_lat Decimal(9,6), actual_lon Decimal(8,6), time_actual_in TEXT(255,0), time_actual_out TEXT(255,0)" +
                ", time_begin TEXT(255,0), time_end TEXT(255,0), signature TEXT(255,0), is_scaned TEXT(255,0), is_save TEXT(255,0), status_order_no TEXT(255,0)" +
                ", comment TEXT(255,0), picture1 TEXT(255,0), picture2 TEXT(255,0), picture3 TEXT(255,0), status_upload TEXT(255,0), driver_code TEXT(255,0)" +
                ", driver_name TEXT(255,0), modified_date TEXT(255,0), trash TEXT(255,0), total_box TEXT(255,0), UNIQUE(id));";
        databaseHelper.execDB(sql);

        sql = "CREATE TABLE IF NOT EXISTS consignment (id TEXT(255,0), item_code item_code(255,0), item_send_time TEXT(255,0), consignment_no TEXT(255,0), subsidiary_cd TEXT(255,0), deli_note_no TEXT(255,0)," +
                "crd TEXT(255,0), ship_plan_date TEXT(255,0), settlement_method TEXT(255,0), cust_shipmode TEXT(255,0), cust_cd TEXT(255,0), ship_to_cd TEXT(255,0), shipto_name TEXT(255,0)," +
                "ship_mode TEXT(255,0), ship_to_postal_cd TEXT(255,0), item_remarks TEXT(255,0), so_voucher_no TEXT(255,0), global_no TEXT(255,0), header_ref TEXT(255,0)" +
                ",  deli_note_amount_price TEXT(255,0), comet_seq TEXT(255,0), warehouse TEXT(255,0), detail_remarks TEXT(255,0), isPlan TEXT(255,0)" +
                ", modified_by TEXT(255,0), modified_date TEXT(255,0), create_by TEXT(255,0), create_date TEXT(255,0), trash TEXT(255,0)" +
                ", inactive TEXT(255,0), status TEXT(255,0), activity_type TEXT(255,0), total_box TEXT(255,0), UNIQUE(id));";
        databaseHelper.execDB(sql);

        sql = "CREATE TABLE IF NOT EXISTS image (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,name_img TEXT(255,0), status_img TEXT(255,0), UNIQUE(id));";
        databaseHelper.execDB(sql);

        sql = "CREATE TABLE IF NOT EXISTS pic_sign (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, consignment_no TEXT(255,0), order_no TEXT(255,0), invoice_no TEXT(255,0)" +
                ",pic_sign_load TEXT(255,0),pic_sign_unload TEXT(255,0),date_sign_load TEXT(255,0),date_sign_unload TEXT(255,0), delivery_no TEXT(255,0)" +
                ",status_load TEXT(255,0),status_unload TEXT(255,0),status_upload_invoice TEXT(255,0), status_delete TEXT(255,0), create_date TEXT(255,0), UNIQUE(order_no));";
        databaseHelper.execDB(sql);

        sql = "CREATE TABLE IF NOT EXISTS comment_invoice (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, consignment_no TEXT(255,0), order_no TEXT(255,0), invoice_no TEXT(255,0)" +
                ",comment_load TEXT(255,0), comment_unload TEXT(255,0), status_load TEXT(255,0), status_unload TEXT(255,0), delivery_no TEXT(255,0), status_upload_comment TEXT(255,0)" +
                ", create_date TEXT(255,0), UNIQUE(order_no));";
        databaseHelper.execDB(sql);

        sql = "CREATE TABLE IF NOT EXISTS Var (Var TEXT NOT NULL,Value TEXT,Value2 TEXT,MODIFIED_DATE TEXT,PRIMARY KEY(Var));";
        databaseHelper.execDB(sql);

        sql = "CREATE TABLE IF NOT EXISTS  login (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,username TEXT(255,0),pass TEXT(255,0),serial TEXT(255,0),driver_id TEXT(255,0)" +
                ",driver_fname TEXT(255,0),driver_lname TEXT(255,0),vehicle_id TEXT(255,0),vehicle_name TEXT(255,0),status_login TEXT(255,0)" +
                ",driver_brand TEXT(255,0), modified_date TEXT(255,0), UNIQUE(id));";
        databaseHelper.execDB(sql);

        sql = "CREATE TABLE IF NOT EXISTS reason (id TEXT(255,0), name TEXT(255,0) , UNIQUE(id));";
        databaseHelper.execDB(sql);

        sql = "CREATE TABLE IF NOT EXISTS image_invoice (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,name_img TEXT(255,0), status_img TEXT(255,0), create_date TEXT(255,0) , UNIQUE(id));";
        databaseHelper.execDB(sql);


    }

    private void clearOldData() {
    }

    private static String convertStreamToString( InputStream is ) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while (( line = reader.readLine() ) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    class Authen_Online extends AsyncTask<String, String, String> {


        int IsSuccess = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Login_Activity.this);
            pd.setCancelable(false);
            pd.setMessage(getString(R.string.checking));
            pd.show();
        }

        @Override
        protected String doInBackground( String... strings ) {

            try {
                String username = strings[0];
                String password = strings[1];
                String imei = strings[2];

                String url = Var.WEBSERVICE2 + "func=login&user=" + username + "&pass=" + password + "&serial=" + imei;
                //String url = Var.WEBSERVICE2 + "func=login&user=" + username + "&pass=" + password + "&serial=" + "330014f6a724236b";
                Log.d("NARISLOG", url);


                JSONArray re_json = narisv.getJsonFromUrl_reJsonArray(url);
                Log.d("NARISLOG", re_json.getJSONObject(0).getString("status"));


                if ( re_json.getJSONObject(0).getString("status").equals("Y") ) { // success

                    re_json.getJSONObject(0).remove("status");
                    Log.d("NARISLOG", "####" + re_json.toString());
                    if ( narisv.INSERT_AS_SQL("login", re_json, "") ) {
                        Log.d("NARISLOG", "INSERT JSON SUCCESS");


                        Var.UserLogin.driver_id = re_json.getJSONObject(0).getString("driver_id");
                        Var.UserLogin.driver_user = re_json.getJSONObject(0).getString("username");
                        Var.UserLogin.driver_pass = re_json.getJSONObject(0).getString("pass");
                        Var.UserLogin.driver_serial = re_json.getJSONObject(0).getString("serial");
                        Var.UserLogin.driver_brand = android.os.Build.BRAND;
                        Var.UserLogin.driver_truck_license = re_json.getJSONObject(0).getString("vehicle_name");
                        Var.UserLogin.driver_fname = re_json.getJSONObject(0).getString("driver_fname");
                        Var.UserLogin.driver_lname = re_json.getJSONObject(0).getString("driver_lname");
                        Var.UserLogin.driver_vehicle_id = re_json.getJSONObject(0).getString("vehicle_id");
                        Var.UserLogin.driver_status_login = re_json.getJSONObject(0).getString("status_login");


                        // Log.d("NARISLOG", "INSERT JSON FAIL");

                        SaveLastLogin();
                        IsSuccess = 1;
                    } else {
                        Log.d("NARISLOG", "INSERT JSON FAIL");
                        Log.d("isSuccess_0", "_01");
                        IsSuccess = 0; //0 sakito_config
                    }
                } else if ( re_json.getJSONObject(0).getString("status").equals("N") && re_json.getJSONObject(0).getString("type").equals("U") ) { // fail
                    Log.d("NARISLOG", "NOT FOUND USER : " + re_json.getJSONObject(0).getString("message"));
                    IsSuccess = -1; //-1
                } else if ( re_json.getJSONObject(0).getString("status").equals("N") && re_json.getJSONObject(0).getString("type").equals("P") ) {
                    Log.d("NARISLOG", "NOT FOUND USER : " + re_json.getJSONObject(0).getString("message"));
                    IsSuccess = -1;//-1
                } else if ( re_json.getJSONObject(0).getString("status").equals("N") && re_json.getJSONObject(0).getString("type").equals("S") ) {
                    Log.d("NARISLOG", "NOT FOUND USER : " + re_json.getJSONObject(0).getString("message"));
                    IsSuccess = -2;//-2
                } else if ( netCon.isConnectingToInternet() ) {
                    Log.d("isSuccess_0", "_02");
                    IsSuccess = 0;//0
                }

            } catch (Exception e) {

                Log.d("isSuccess_0", "_03 : " + e.getMessage());
                //IsSuccess = 1;//0
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
            pd.hide();

            if ( IsSuccess == 1 ) {
                // Toast.makeText(Login_Activity.this, "Success !", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Login_Activity.this, PlanWork_Activity.class);
                startActivity(i);
            } else if ( IsSuccess == -1 ) {
                AlertDialog alert = new AlertDialog.Builder(Login_Activity.this).create();
                alert.setTitle(getString(R.string.not_found));
                alert.setMessage(getString(R.string.u_or_p_incorrect));
                alert.setButton2(getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {

                    }
                });
                alert.show();

                // attemptLogin();
            } else if ( IsSuccess == -2 ) {
                AlertDialog alert = new AlertDialog.Builder(Login_Activity.this).create();
                alert.setTitle("Serial !!");
                alert.setMessage(getString(R.string.serial_not_match));
                alert.setButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {

                    }
                });
                alert.show();
            } else if ( IsSuccess == 0 ) {
                //Toast.makeText(Login_Activity.this, "Fail", Toast.LENGTH_SHORT).show();
                AlertDialog alert = new AlertDialog.Builder(Login_Activity.this).create();
                alert.setTitle(getString(R.string.server_connection));
                alert.setMessage(getString(R.string.cannot_connect_to_the_server));
                alert.setButton(getString(R.string.use_offline), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                      //  new Authen_Offline().execute(edtUsername.getText().toString(), edtPassword.getText().toString());
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
    }

    class Authen_Offline extends AsyncTask<String, String, String> {

        int IsSuccess = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Login_Activity.this);
            pd.setCancelable(false);
            pd.setMessage(getString(R.string.checking));
            pd.show();
        }

        @Override
        protected String doInBackground( String... params ) {
            try {
                Log.d("NARISLOG", "Authen Offline");
                String sql = "select * from login where username = '" + params[0] + "' and pass = '" + params[1] + "' ";
                Cursor c = databaseHelper.selectDB(sql);
                Log.d("NARISLOG", "total line " + c.getCount());
                if ( c.getCount() > 0 ) {
                    c.moveToFirst();
                    // Log.d("NARISLOG1", "_01"+c);
                    if ( c.getString(c.getColumnIndex("serial")).equals(imei) ) {

                        if ( c.getString(c.getColumnIndex("status_login")).equals("0") ) {
//                            String update_login = "UPDATE login SET driver_status_login = '1' WHERE driver_id = "+c.getString(c.getColumnIndex("driver_id"));
//                            databaseHelper.rawQuery(update_login);

                            ContentValues cv = new ContentValues();
                            cv.put("status_login", "1"); //These Fields should be your String values of actual column names

                            databaseHelper.db().update("login", cv, "driver_id= '" + c.getString(c.getColumnIndex("driver_id")) + "'", null);
                            databaseHelper.db().close();
                        }

                        String sql2 = "select * from login where username = '" + c.getString(c.getColumnIndex("username")) + "' and pass = '" + c.getString(c.getColumnIndex("pass")) + "' ";
                        Cursor c2 = databaseHelper.selectDB(sql2);
                        c2.moveToFirst();
                        // Log.d("asasdasasf", "doInBackground: "+update_login);
                        //Log.d("status_login", "=>" + Var.UserLogin.driver_status_login);
                        Var.UserLogin.driver_id = c2.getString(c2.getColumnIndex("driver_id"));
                        Var.UserLogin.driver_user = c2.getString(c2.getColumnIndex("username"));
                        Var.UserLogin.driver_pass = c2.getString(c2.getColumnIndex("pass"));
                        Var.UserLogin.driver_serial = c2.getString(c2.getColumnIndex("serial"));
                        Var.UserLogin.driver_brand = c2.getString(c2.getColumnIndex("driver_brand"));
                        Var.UserLogin.driver_truck_license = c2.getString(c2.getColumnIndex("vehicle_name"));
                        Var.UserLogin.driver_fname = c2.getString(c2.getColumnIndex("driver_fname"));
                        Var.UserLogin.driver_lname = c2.getString(c2.getColumnIndex("driver_lname"));
                        Var.UserLogin.driver_status_login = c2.getString(c2.getColumnIndex("status_login"));
                        c2.close();
                        //Log.d("NARISLOG1", "_02");
                        SaveLastLogin();
                        IsSuccess = 1;
                    } else {

                        IsSuccess = -2;
                    }

                } else {
                    IsSuccess = -1;
                }
                c.close();

            } catch (Exception e) {
                Log.d("NARISLOG", e.getMessage());
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
            pd.hide();

            if ( IsSuccess == 1 ) {
                Intent i = new Intent(Login_Activity.this, PlanWork_Activity.class);
                startActivity(i);
            } else if ( IsSuccess == -1 ) {
                AlertDialog alert = new AlertDialog.Builder(Login_Activity.this).create();
                alert.setTitle(getString(R.string.no_history_found));
                alert.setMessage(getString(R.string.user_not_found));
                alert.setButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {

                    }
                });
                alert.show();

                // attemptLogin();
            } else if ( IsSuccess == -2 ) {
                AlertDialog alert = new AlertDialog.Builder(Login_Activity.this).create();
                alert.setTitle("Serial !!");
                alert.setMessage(getString(R.string.serial_not_match));
                alert.setButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {

                    }
                });
                alert.show();
            } else {
                AlertDialog alert = new AlertDialog.Builder(Login_Activity.this).create();
                alert.setTitle(getString(R.string.error));
                alert.setMessage(getString(R.string.cannot_retrieve_user_data));
                alert.setButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {

                    }
                });
                alert.show();
            }
        }
    }

    private void SaveLastLogin() {
//        String sql1 = "IF NOT EXISTS (INSERT OR REPLACE into Var (Var,Value,Value2,MODIFIED_DATE) values('LASTLOGIN','" + Var.UserLogin.driver_user + "','" + Var.UserLogin.driver_pass + "',datetime('now','localtime')))" +
//                "BEGIN CREATE TABLE Var ";
        String sql = "INSERT OR REPLACE into Var (Var,Value,Value2,MODIFIED_DATE) values('LASTLOGIN','" + Var.UserLogin.driver_user + "','" + Var.UserLogin.driver_pass + "',datetime('now','localtime'))";
        databaseHelper.execDB(sql);
    }

    private boolean checkAndRequestPermissions() {
        //int pWriteStorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int pReadStorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int pInternet = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        int pAccessLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int pCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

       /* if ( pWriteStorage != PackageManager.PERMISSION_GRANTED ) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }*/
        if ( pReadStorage != PackageManager.PERMISSION_GRANTED ) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if ( pInternet != PackageManager.PERMISSION_GRANTED ) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if ( pAccessLocation != PackageManager.PERMISSION_GRANTED ) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if ( pCamera != PackageManager.PERMISSION_GRANTED ) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if ( !listPermissionsNeeded.isEmpty() ) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    private void attemptLogin() {
        if ( mAuthTask != null ) {
            return;
        }

        // Reset errors.
        edtUsername.setError(null);
        edtPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if ( !TextUtils.isEmpty(password) && !isPasswordValid(password) ) {
            edtPassword.setError(getString(R.string.error_invalid_password));
            focusView = edtPassword;
            cancel = true;
        }

        // Check for a valid email address.
//        if ( TextUtils.isEmpty(email) ) {
//            edtUsername.setError(getString(R.string.error_field_required));
//            focusView = edtUsername;
//            cancel = true;
//        } else if ( !isEmailValid(email) ) {
//            edtUsername.setError(getString(R.string.error_invalid_email));
//            focusView = edtUsername;
//            cancel = true;
//        }

        if ( cancel ) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private void populateAutoComplete() {
        if ( !mayRequestContacts() ) {
            return;
        }

        getLoaderManager().initLoader(0, null, (android.app.LoaderManager.LoaderCallbacks<Cursor>) this);
    }

    private boolean mayRequestContacts() {
        if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) {
            return true;
        }
        if ( checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED ) {
            return true;
        }
        if ( shouldShowRequestPermissionRationale(READ_CONTACTS) ) {
            Snackbar.make(edtUsername, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick( View v ) {
                            requestPermissions(new String[]{ READ_CONTACTS }, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{ READ_CONTACTS }, REQUEST_READ_CONTACTS);
        }
        return false;
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask( String email, String password ) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground( Void... params ) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if ( pieces[0].equals(mEmail) ) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute( final Boolean success ) {
            mAuthTask = null;
            //showProgress(false);

            if ( success ) {
                finish();
            } else {
                edtPassword.setError(getString(R.string.error_incorrect_password));
                edtPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }

    private boolean isEmailValid( String email ) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid( String password ) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress( final boolean show ) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2 ) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd( Animator animation ) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd( Animator animation ) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            //mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState( Bundle outState, PersistableBundle outPersistentState ) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString("Username", Username);
        outState.putString("Password", password);

    }

    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        super.onRestoreInstanceState(savedInstanceState);
        Username = savedInstanceState.getString("Username");
        password = savedInstanceState.getString("Password");
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.munu_login, menu); //your file name
        //menu.add(0, THAI, R.id.lang_thai, 300, "Thai");
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        SharedPreferences user_data = this.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        SharedPreferences Language_Locale = this.getSharedPreferences("PREFERENCE_LANGUAGE", Context.MODE_PRIVATE);
        switch (item.getItemId()) {
            case R.id.export_data_menu:
                Toast.makeText(this, "ส่งออกฐานข้อมูล", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.import_data_menu:
                Toast.makeText(this, "นำเข้าฐานข้อมูล", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.manage_data_menu:
                Toast.makeText(this, "จัดฐานข้อมูล", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.clear_data_menu:
                Toast.makeText(this, "ล้างฐานข้อมูล", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.lang_eng:

                Username = edtUsername.getText().toString();
                password = edtPassword.getText().toString();
                user_data.edit().putString("Username", Username).apply();
                user_data.edit().putString("Password", password).apply();
                user_data.edit().putBoolean("isChange", true).apply();

                Language_Locale.edit().putString("LANGUAGE_KEY", "ENGLISH").apply();
                Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
                return true;
            case R.id.lang_thai:

                Username = edtUsername.getText().toString();
                password = edtPassword.getText().toString();
                user_data.edit().putString("Username", Username).apply();
                user_data.edit().putString("Password", password).apply();
                user_data.edit().putBoolean("isChange", true).apply();

                Language_Locale.edit().putString("LANGUAGE_KEY", "THAI").apply();
                Intent intent2 = new Intent(getApplicationContext(), Login_Activity.class);
                startActivity(intent2);
                finish();
                overridePendingTransition(0, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
