package ws.epod.sync;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

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
import ws.epod.Client.Structors.UploadImageInvoice;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.Var;
import ws.epod.PlanWork_Activity;
import ws.epod.R;

public class UploadDataPlan {

    private ProgressDialog progressDialog;
    private DatabaseHelper databaseHelper;
    private NarisBaseValue narisv;
    private APIInterface apiInterface;
    private ConnectionDetector netCon;

    private String encodedImagePic1;
    private String encodedImagePic2;
    private String encodedImagePic3;

    private String encodedImageInvoice;

    private Context context;

    public UploadDataPlan(Context context) {
        this.context = context;
    }

    @SuppressLint("StaticFieldLeak")
    public void Upload() {

        narisv = new NarisBaseValue(context);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        netCon = new ConnectionDetector(context);
        databaseHelper = new DatabaseHelper(context);



        new AsyncTask<String, Integer, String>() {

            int IsSuccess = 1;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(context.getString(R.string.sync_data));
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

                                            Log.d("kksksks", "doInBackground: "+uploadImage.get(0).toString());


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

                        Toast.makeText(context, mess, Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(context, mess, Toast.LENGTH_SHORT).show();
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
                String sql = "select id, (select delivery_no from plan) as delivery_no, order_no, consignment_no, invoice_no, pic_sign_load, pic_sign_unload, date_sign_load, date_sign_unload from pic_sign where status_upload_invoice = '0' and status_delete = '0' ";
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

                            ContactArray.put(i, contact);
                            i++;

                        } while (cursor.moveToNext());

                        Root.put("data", ContactArray);
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

                Call<ResponseBody> call = apiInterface.downloadWork(Var.UserLogin.driver_vehicle_id, Var.UserLogin.driver_id, Var.UserLogin.driver_serial, getDate, max_modified_date);
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
                }

            } catch (
                    Exception e) {
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
//                    Snackbar.make(viewFab, mess, Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();

                    Toast.makeText(context, mess, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    mess = "Sync error!!";
//                    Snackbar.make(viewFab, mess, Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();

                    Toast.makeText(context, mess, Toast.LENGTH_SHORT).show();

                    // rvPlanWork.setAdapter(sectionAdapter);
                    //// planWorkAdapter.notifyDataSetChanged();

                    break;
            }


        }
    }



}
