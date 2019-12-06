package ws.epod.ObjectClass;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import androidx.annotation.Nullable;

import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ws.epod.Client.APIClient;
import ws.epod.Client.APIInterface;
import ws.epod.Client.Structors.UploadImage;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;

public class BackgroundService extends Service {

    public Context context = this;
    public android.os.Handler handler = null;
    public static Runnable runnable = null;

    ConnectionDetector netCon;
    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;

    private APIInterface apiInterface;

    String encodedImagePic1;
    String encodedImagePic2;
    String encodedImagePic3;

    int delay = ( 1 * 60 ) * 1000;

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {


        narisv = new NarisBaseValue(getApplicationContext());
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        apiInterface = APIClient.getClient().create(APIInterface.class);

        handler = new android.os.Handler();
        runnable = new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), "Service created!", Toast.LENGTH_LONG).show();
                new UpLoadWork().execute();
//                handler.postDelayed(runnable, 300000);
            }
        };
        handler.postDelayed(runnable, 1000);


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    private void UpLoadWork() {

        new AsyncTask<Void, Integer, Integer>() {

            @Override
            protected Integer doInBackground( Void... voids ) {

                int IsSuccess = 1;

                JSONObject Root = new JSONObject();
                String url = Var.WEBSERVICE2 + "func=setPlan&driver_id=" + Var.UserLogin.driver_id;
                try {

                    String sql = "select * from Plan where is_scaned <> '0'";
                    Cursor cursor = databaseHelper.selectDB(sql);

                    JSONArray ContactArray = new JSONArray();


                    Log.d("UploadWorkLog", "doInBackground: " + cursor.getCount());
                    int i = 0;
                    cursor.moveToFirst();
                    if ( cursor.getCount() > 0 ) {
                        do {

                            JSONObject contact = new JSONObject();

                            try {
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

                        JSONArray fa = narisv.SendAndGetJson_reJsonArray(Root, url);
                        if ( fa.getJSONObject(0).getString("status").equals("Y") ) {


//                            File f = new File(Environment.getExternalStorageDirectory()
//                                    + "/ContactDetail.txt");
//                            FileOutputStream fos = new FileOutputStream(f, true);
//                            PrintStream ps = new PrintStream(fos);
//                            ps.append(Root.toString());

                            IsSuccess = 1;
                        } else {
                            IsSuccess = 0;
                        }

                    } else {
                        //new LoadWork().execute();
                        IsSuccess = 1;
                    }

                } catch (Exception e) {
                    IsSuccess = 0;
                    e.printStackTrace();
                }


                return IsSuccess;
            }

            @Override
            protected void onPostExecute( Integer result ) {
                super.onPostExecute(result);

                switch (result) {
                    case 1:
                        //new LoadWork().execute();
                        break;
                    case 0:

                        break;
                }


            }
        }.execute();
    }

    public class UpLoadWork extends AsyncTask<String, String, String> {


        int IsSuccess = 1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground( String... strings ) {

            JSONObject Root = new JSONObject();
            JSONObject picture1 = new JSONObject();
            ArrayList<UploadImage.Data> uploadImage = new ArrayList<>();

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

                    String rootToString = Root.toString();
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), rootToString);

                    Call<ResponseBody> call = apiInterface.uploadwork(Var.UserLogin.driver_id, body);
                    Response<ResponseBody> response = call.execute();
                    if ( response.code() == 200 ) {
                        String responseRecieved = response.body().string();
                        if ( responseRecieved != null ) {
                            if ( !responseRecieved.equals("") ) {
                                JSONArray jsonArray = new JSONArray(responseRecieved);


                                Log.d("LogBackground", "doInBackground: " + jsonArray.getJSONObject(0).getString("status"));

                                if ( jsonArray.getJSONObject(0).getString("status").equals("Y") ) {
//
                                    for (int pic = 0; pic < jsonArray.getJSONObject(0).getJSONArray("returnId").length(); pic++) {

//                                        String json_data = jsonArray.getJSONObject(0).getJSONArray("returnId").getString(pic);
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
//
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
            switch (IsSuccess) {
                case -1:

                    break;
                case 1:

                    new DownloadWork().execute();

                    break;
                case 0:

                    break;
            }

        }

        class DownloadWork extends AsyncTask<String, String, String> {
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
                Log.d("LogBackground", "onPostExecute: Yes");
                handler.postDelayed(runnable, delay);



            }

        }

    }
}
