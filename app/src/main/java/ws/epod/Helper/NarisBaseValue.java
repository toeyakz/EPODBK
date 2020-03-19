package ws.epod.Helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


import ws.epod.ObjectClass.Var;

public class NarisBaseValue {
    Context context;
    @SuppressLint("StaticFieldLeak")
    static DatabaseHelper databaseHelper;
    SQLiteDatabase sqlite;

    public static int firstlogin;

    public NarisBaseValue(Context context) {
        try {
            databaseHelper = new DatabaseHelper(context);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return;
            }
            sqlite = context.openOrCreateDatabase(Var.dbname, context.MODE_PRIVATE, null);
        } catch (Exception er) {
            String Er = er.getMessage();
            Log.e("Error : ", Er.toString());
        }
    }

//	public String getIMEI( Activity activity) {
//		TelephonyManager telephonyManager = (TelephonyManager) activity
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		return telephonyManager.getDeviceId();
//	}

    public String uploadFileDB(Context context, File file) {

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;

        if (!file.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return null;
        }

        int serverResponseCode = 0;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            URL url = new URL(Var.host + "/services/upload_epod_db.php");//path url service
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", file.getAbsolutePath());

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + file.getAbsolutePath() + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            Log.i("Huzza", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = conn.getResponseCode();
            Log.d("TAG", "uploadVideo ResponseCode: " + serverResponseCode);

            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ignored) {
            }

            try {
                boolean deleted = file.delete();
                if (deleted)
                    Log.d("s8a2s9as", "deleted: " + deleted);
                else Log.d("s8a2s9as", "deleted: " + deleted);
            } catch (Exception e) {
                Log.e("s8a2s9as", "onPostExecute: ", e);
            }

            Log.d("s8a2s9as", "Response: " + sb.toString());
            return sb.toString();
        } else {
            return "Could not upload";
        }
    }//end upload


    @SuppressLint("WrongConstant")
    public static void insertPlan(JSONArray json) {
        databaseHelper.db().beginTransaction();
        try {
            ContentValues values = new ContentValues();

            for (int i = 0; i < json.length(); i++) {

                JSONObject json_data = json.getJSONObject(i);
                values.put("id", json_data.getString("id"));
                values.put("delivery_date", json_data.getString("delivery_date"));
                values.put("vehicle_name", json_data.getString("vehicle_name"));
                values.put("blackbox", json_data.getString("blackbox"));
                values.put("delivery_no", json_data.getString("delivery_no"));
                values.put("plan_seq", json_data.getString("plan_seq"));
                values.put("station_code", json_data.getString("station_code"));
                values.put("station_name", json_data.getString("station_name"));
                values.put("station_address", json_data.getString("station_address"));
                values.put("station_lat", json_data.getString("station_lat"));
                values.put("station_lon", json_data.getString("station_lon"));
                values.put("station_area", json_data.getString("station_area"));
                values.put("plan_in", json_data.getString("plan_in"));
                values.put("plan_out", json_data.getString("plan_out"));
                values.put("consignment_no", json_data.getString("consignment_no"));
                values.put("order_no", json_data.getString("order_no"));
                values.put("activity_type", json_data.getString("activity_type"));
                values.put("box_no", json_data.getString("box_no"));
                values.put("waybill_no", json_data.getString("waybill_no"));
                values.put("weight", json_data.getString("weight"));
                values.put("actual_seq", json_data.getString("actual_seq"));
                values.put("actual_lat", json_data.getString("actual_lat"));
                values.put("actual_lon", json_data.getString("actual_lon"));
                values.put("time_actual_in", json_data.getString("time_actual_in"));
                values.put("time_actual_out", json_data.getString("time_actual_out"));
                values.put("time_begin", json_data.getString("time_begin"));
                values.put("time_end", json_data.getString("time_end"));
                values.put("signature", json_data.getString("signature"));
                values.put("is_scaned", json_data.getString("is_scaned"));
                values.put("is_save", json_data.getString("is_save"));
                values.put("comment", json_data.getString("comment"));
                values.put("picture1", json_data.getString("picture1"));
                values.put("picture2", json_data.getString("picture2"));
                values.put("picture3", json_data.getString("picture3"));
                values.put("status_upload", json_data.getString("status_upload"));
                values.put("driver_code", json_data.getString("driver_code"));
                values.put("driver_name", json_data.getString("driver_name"));
                values.put("modified_date", json_data.getString("modified_date"));
                values.put("trash", json_data.getString("trash"));
                values.put("total_box", json_data.getString("total_box"));

                databaseHelper.db().insertWithOnConflict("Plan",
                        null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }

            databaseHelper.db().setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            databaseHelper.db().endTransaction();
        }
        databaseHelper.db().close();
    }

    @SuppressLint("WrongConstant")
    public static void insertConsignment(JSONArray json) {
        databaseHelper.db().beginTransaction();
        try {
            ContentValues values = new ContentValues();

            for (int i = 0; i < json.length(); i++) {

                JSONObject json_data = json.getJSONObject(i);
                values.put("id", json_data.getString("id"));
                values.put("item_code", json_data.getString("item_code"));
                values.put("item_send_time", json_data.getString("item_send_time"));
                values.put("consignment_no", json_data.getString("consignment_no"));
                values.put("subsidiary_cd", json_data.getString("subsidiary_cd"));
                values.put("deli_note_no", json_data.getString("deli_note_no"));
                values.put("crd", json_data.getString("crd"));
                values.put("ship_plan_date", json_data.getString("ship_plan_date"));
                values.put("settlement_method", json_data.getString("settlement_method"));
                values.put("cust_shipmode", json_data.getString("cust_shipmode"));
                values.put("cust_cd", json_data.getString("cust_cd"));
                values.put("ship_to_cd", json_data.getString("ship_to_cd"));
                values.put("shipto_name", json_data.getString("shipto_name"));
                values.put("ship_mode", json_data.getString("ship_mode"));
                values.put("ship_to_postal_cd", json_data.getString("ship_to_postal_cd"));
                values.put("item_remarks", json_data.getString("item_remarks"));
                values.put("so_voucher_no", json_data.getString("so_voucher_no"));
                values.put("global_no", json_data.getString("global_no"));
                values.put("header_ref", json_data.getString("header_ref"));
                values.put("deli_note_amount_price", json_data.getString("deli_note_amount_price"));
                values.put("comet_seq", json_data.getString("comet_seq"));
                values.put("warehouse", json_data.getString("warehouse"));
                values.put("detail_remarks", json_data.getString("detail_remarks"));
                values.put("isPlan", json_data.getString("isPlan"));
                values.put("status", json_data.getString("status"));
                values.put("activity_type", json_data.getString("activity_type"));
                values.put("trash", json_data.getString("trash"));

                databaseHelper.db().insertWithOnConflict("consignment",
                        null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }

            databaseHelper.db().setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            databaseHelper.db().endTransaction();
        }
        databaseHelper.db().close();
    }

    @SuppressLint("WrongConstant")
    public static void insertLogin(JSONArray json) {
        databaseHelper.db().beginTransaction();
        try {
            ContentValues values = new ContentValues();

            for (int i = 0; i < json.length(); i++) {

                JSONObject json_data = json.getJSONObject(i);

               // values.put("id", json_data.getString("id"));
                values.put("username", json_data.getString("username"));
                values.put("pass", json_data.getString("pass"));
                values.put("serial", json_data.getString("serial"));
                values.put("driver_id", json_data.getString("driver_id"));
                values.put("driver_fname", json_data.getString("driver_fname"));
                values.put("driver_lname", json_data.getString("driver_lname"));
                values.put("vehicle_id", json_data.getString("vehicle_id"));
                values.put("vehicle_name", json_data.getString("vehicle_name"));
                values.put("status_login", json_data.getString("status_login"));

                databaseHelper.db().insertWithOnConflict("login",
                        null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }

            databaseHelper.db().setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            databaseHelper.db().endTransaction();
        }
        databaseHelper.db().close();
    }

    @SuppressLint("WrongConstant")
    public static void insertReason(JSONArray json) {
        databaseHelper.db().beginTransaction();
        try {
            ContentValues values = new ContentValues();

            for (int i = 0; i < json.length(); i++) {

                JSONObject json_data = json.getJSONObject(i);
                values.put("id", json_data.getString("id"));
                values.put("name", json_data.getString("name"));


                databaseHelper.db().insertWithOnConflict("reason",
                        null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }

            databaseHelper.db().setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            databaseHelper.db().endTransaction();
        }
        databaseHelper.db().close();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public String getSerial(Activity activity) {
        String serialNumber;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            serialNumber = Build.getSerial();
//        } else {
//            serialNumber = android.os.Build.SERIAL;
//        }
//        serialNumber = Settings.Secure.getString(context.getContentResolver(),
//                Settings.Secure.ANDROID_ID);;

         serialNumber = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return serialNumber;
    }


    public String get_value(String SQL, String col_name) {

        String value = null;
        Cursor c0 = databaseHelper.selectDB(SQL);

        if (c0 != null) {
            int round = 0;
            if (c0.moveToFirst()) {
                value = c0.getString(c0.getColumnIndex(col_name));

            }
        }
        return value;
    }

    public String[] get_values(String SQL, String col_name) {

        String value[];
        Cursor c0 = databaseHelper.selectDB(SQL);
        value = new String[c0.getCount()];
        if (c0 != null) {
            c0.moveToFirst();
            while (!c0.isAfterLast()) {
                value[c0.getPosition()] = c0.getString(c0
                        .getColumnIndex(col_name));
                Log.d("NARIS", value[c0.getPosition()]);
                c0.moveToNext();

            }

        }
        c0.close();
        return value;
    }

    public ArrayList<String> get_values_arraylist(String SQL, String col_name) {

        ArrayList<String> value = new ArrayList<String>();
        Cursor c0 = databaseHelper.selectDB(SQL);
        if (c0.getCount() > 0) {
            c0.moveToFirst();
            while (!c0.isAfterLast()) {
                value.add(c0.getString(c0.getColumnIndex(col_name)));
                c0.moveToNext();

            }

        }
        c0.close();
        return value;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String get_date() {
        // yyyy-MM-dd HH:mm:ss
        SimpleDateFormat df_b = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df_b.format(new Date()).toString();
    }

    public String get_date(int day) {
        SimpleDateFormat df_b = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date();
        d.setTime(d.getTime() + (day * 1000 * 60 * 60 * 24));

        return df_b.format(d).toString();
    }

    public JSONObject SendAndGetJson_reJsonObject(JSONObject json_send, String url) {
        try {
            HttpParams myParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(myParams, 20000);
            HttpConnectionParams.setSoTimeout(myParams, 20000);
            HttpClient httpclient = new DefaultHttpClient(myParams);
            String json = json_send.toString();

            HttpPost httppost = new HttpPost(url.toString());
            httppost.setHeader("Content-type", "application/json");

            StringEntity se = new StringEntity(json, org.apache.http.protocol.HTTP.UTF_8);
            se.setContentEncoding(new BasicHeader(
                    org.apache.http.protocol.HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);
            String temp = EntityUtils.toString(response.getEntity());
            Log.d("NARIS", temp);

            JSONObject json_return = new JSONObject(temp);

            Log.d("NARIS", json_return.toString());

            return json_return;
        } catch (Exception e) {
            return null;
        }


    }

    public JSONArray sendImageBase64(JSONObject jsonObject, String url_) {

        try {
            String data = jsonObject.toString();
            URL url = new URL(url_);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setFixedLengthStreamingMode(data.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            Log.d("Vicky", "Data to php = " + data);
            writer.flush();
            writer.close();
            out.close();
            connection.connect();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            String result = sb.toString();
            Log.d("Vicky", "Response from php = " + result);
            JSONArray Response = new JSONArray(result);

            connection.disconnect();
            return Response;

        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject SendAndGetJson_reJsonObject(JSONArray json_send, String url) {
        try {
            HttpParams myParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(myParams, 20000);
            HttpConnectionParams.setSoTimeout(myParams, 20000);
            HttpClient httpclient = new DefaultHttpClient(myParams);
            String json = json_send.toString();

            HttpPost httppost = new HttpPost(url.toString());
            httppost.setHeader("Content-type", "application/json");

            StringEntity se = new StringEntity(json, org.apache.http.protocol.HTTP.UTF_8);
            se.setContentEncoding(new BasicHeader(
                    org.apache.http.protocol.HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);
            String temp = EntityUtils.toString(response.getEntity());
            Log.d("NARIS", temp);

            JSONObject json_return = new JSONObject(temp);

            return json_return;
        } catch (Exception e) {
            return null;
        }


    }


    public JSONArray SendAndGetJson_reJsonArray(JSONObject json_send, String url) {
        try {
            HttpParams myParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(myParams, 20000);
            HttpConnectionParams.setSoTimeout(myParams, 20000);
            HttpClient httpclient = new DefaultHttpClient(myParams);
            String json = json_send.toString();

            //return null; //sakito edit

            HttpPost httppost = new HttpPost(url.toString());
            httppost.setHeader("Content-type", "application/json");

            StringEntity se = new StringEntity(json, org.apache.http.protocol.HTTP.UTF_8);
            se.setContentEncoding(new BasicHeader(
                    org.apache.http.protocol.HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);
            String temp = EntityUtils.toString(response.getEntity());

            JSONArray json_return = new JSONArray(temp);
            return json_return;

        } catch (Exception e) {
            return null;
        }


    }

    public JSONArray SendAndGetJson_reJsonArray(JSONArray json_send, String url) {
        try {
            HttpParams myParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(myParams, 100000);
            HttpConnectionParams.setSoTimeout(myParams, 100000);
            HttpClient httpclient = new DefaultHttpClient(myParams);
            String json = json_send.toString();
//return null; // sakito edit
            HttpPost httppost = new HttpPost(url.toString());
            httppost.setHeader("Content-type", "application/json");

            StringEntity se = new StringEntity(json, org.apache.http.protocol.HTTP.UTF_8);
            se.setContentEncoding(new BasicHeader(
                    org.apache.http.protocol.HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);
            String temp = EntityUtils.toString(response.getEntity());

            JSONArray json_return = new JSONArray(temp);
            return json_return;
        } catch (Exception e) {
            return null;
        }


    }

    public JSONArray getJsonFromUrl_reJsonArray(String url) {
        try {
            HttpParams myParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(myParams, 100000);
            HttpConnectionParams.setSoTimeout(myParams, 100000);
            HttpClient httpclient = new DefaultHttpClient(myParams);
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            String temp = EntityUtils.toString(response.getEntity());

            JSONArray json_return = new JSONArray(temp);
            Log.d("json_return", "getJsonFromUrl_reJsonArray: " + json_return);
            return json_return;
        } catch (Exception e) {
            Log.d("json_return", e.getMessage());
            return null;
        }

    }


//	public String getJsonFromUrl_reJsonArray3( String url){
//
//
//	}


    public JSONArray getJsonFromUrl_reJsonArray2(String url) {
        try {
            HttpParams myParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(myParams, 100000);
            HttpConnectionParams.setSoTimeout(myParams, 100000);
            HttpClient httpclient = new DefaultHttpClient(myParams);
            HttpPost httppost = new HttpPost(url.toString());
            HttpResponse response = httpclient.execute(httppost);
            String temp = EntityUtils.toString(response.getEntity());

            JSONArray json_return = new JSONArray(temp);
            Log.d("json_return2", "getJsonFromUrl_reJsonArray: " + json_return);
            return json_return;
        } catch (Exception e) {
            return null;
        }


    }

    public JSONObject getJsonFromUrl_reJsonObject(String url) {
        try {
            HttpParams myParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(myParams, 100000);
            HttpConnectionParams.setSoTimeout(myParams, 100000);
            HttpClient httpclient = new DefaultHttpClient(myParams);
            HttpPost httppost = new HttpPost(url.toString());
            HttpResponse response = httpclient.execute(httppost);
            String temp = EntityUtils.toString(response.getEntity());

            JSONObject json_return = new JSONObject(temp);
            return json_return;
        } catch (Exception e) {
            return null;
        }


    }


    public String getDeviceDetail() {
        return "Brand : " + android.os.Build.BRAND + ", Model :" + android.os.Build.MODEL + ", Android " + android.os.Build.VERSION.RELEASE + ", SERIAL : " + android.os.Build.SERIAL;
    }


    public JSONArray getJsonFromCursor(Cursor c) throws JSONException {
        JSONArray row = new JSONArray();
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                JSONObject col = new JSONObject();
                for (int i = 0; i < c.getColumnCount(); i++) {
                    String colname = c.getColumnName(i);
                    col.put(colname, c.getString(i));
                }
                row.put(col);
                c.moveToNext();
            }
            c.close();
        }
        return row;
    }


    public boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public boolean INSERT_AS_SQL(String tablename, JSONArray json, String sqlfordelete) {
        try {
            Log.d("NARIS_KEY", "START");
            if (json.length() > 0) {

                if (json.getJSONObject(0).isNull("requeststatus")) {


                    //if(sqlfordelete != null) databaseHelper.execDB(sqlfordelete);

                    Log.d("NARIS_KEY", "START 2");

                    JSONObject firstJSONObject = json.getJSONObject(0);
                    Iterator keysToCopyIterator = firstJSONObject.keys();


                    String SQL = "INSERT OR REPLACE INTO " + tablename + " (";
                    String SQL1 = "";
                    String SQL2 = "";

                    while (keysToCopyIterator.hasNext()) {
                        String key = (String) keysToCopyIterator.next();

                        SQL1 = SQL1.concat(key);
                        SQL2 = SQL2.concat("?");

                        if (keysToCopyIterator.hasNext()) {
                            SQL1 = SQL1.concat(",");
                            SQL2 = SQL2.concat(",");
                        }

                    }
                    SQL = SQL.concat(SQL1 + ") ");
                    SQL = SQL.concat("VALUES (" + SQL2 + ")");
                    Log.d("NARIS_KEY", SQL);

                    SQLiteStatement statement = sqlite.compileStatement(SQL);
                    sqlite.beginTransaction();


                    for (int i = 0; i < json.length(); i++) {
                        JSONObject jo = json.getJSONObject(i);
                        Iterator column = jo.keys();
                        try {
                            JSONObject c1 = json.getJSONObject(i);
                            statement.clearBindings();
                            int col_index = 1;
                            while (column.hasNext()) {
                                String col_key = (String) column.next();

                                statement.bindString(col_index, c1.getString(col_key));


                                col_index++;
                            }
                            statement.execute();


                        } catch (JSONException e) {
                            Log.d("Check_ERROR", "INSERT_AS_SQL: " + e.getMessage());
                            return false;
                        }

                    }


                    sqlite.setTransactionSuccessful();
                    sqlite.endTransaction();
                    Log.d("NARIS_KEY", "INSERT_AS_SQL: SUCCESS");
                } else {

                    return false;
                }
            }

        } catch (Exception e) {
            Log.d("NARISLOG", "INSERT AS SQL ERROR : " + e.getMessage());
            return false;
        }

        return true;

    }

    public boolean INSERT_AS_SQL_NO_REPLACE(String tablename, JSONArray json, String sqlfordelete) {
        try {
            Log.d("NARIS_KEY", "START");
            if (json.length() > 0) {

                if (json.getJSONObject(0).isNull("requeststatus")) {


                    //if(sqlfordelete != null) databaseHelper.execDB(sqlfordelete);

                    Log.d("NARIS_KEY", "START 2");

                    JSONObject firstJSONObject = json.getJSONObject(0);
                    Iterator keysToCopyIterator = firstJSONObject.keys();


                    String SQL = "INSERT INTO " + tablename + " (";
                    String SQL1 = "";
                    String SQL2 = "";

                    while (keysToCopyIterator.hasNext()) {
                        String key = (String) keysToCopyIterator.next();

                        SQL1 = SQL1.concat(key);
                        SQL2 = SQL2.concat("?");

                        if (keysToCopyIterator.hasNext()) {
                            SQL1 = SQL1.concat(",");
                            SQL2 = SQL2.concat(",");
                        }

                    }
                    SQL = SQL.concat(SQL1 + ") ");
                    SQL = SQL.concat("VALUES (" + SQL2 + ")");
                    Log.d("NARIS_KEY", SQL);

                    SQLiteStatement statement = sqlite.compileStatement(SQL);
                    sqlite.beginTransaction();


                    for (int i = 0; i < json.length(); i++) {
                        JSONObject jo = json.getJSONObject(i);
                        Iterator column = jo.keys();
                        try {
                            JSONObject c1 = json.getJSONObject(i);
                            statement.clearBindings();
                            int col_index = 1;
                            while (column.hasNext()) {
                                String col_key = (String) column.next();

                                statement.bindString(col_index, c1.getString(col_key));


                                col_index++;
                            }
                            statement.execute();


                        } catch (JSONException e) {
                            Log.d("Check_ERROR", "INSERT_AS_SQL: " + e.getMessage());
                            return false;
                        }

                    }


                    sqlite.setTransactionSuccessful();
                    sqlite.endTransaction();
                    Log.d("NARIS_KEY", "INSERT_AS_SQL: SUCCESS");
                } else {

                    return false;
                }
            }

        } catch (Exception e) {
            Log.d("NARISLOG", "INSERT AS SQL ERROR : " + e.getMessage());
            return false;
        }

        return true;

    }

    public boolean INSERT_AS_SQL_exceptcol(String tablename, JSONArray json, String sqlfordelete, ArrayList<String> col) {
        try {
            if (json.length() > 0) {

                if (json.getJSONObject(0).isNull("requeststatus")) {


                    //if(sqlfordelete != null) databaseHelper.execDB(sqlfordelete);

                    JSONObject firstJSONObject = json.getJSONObject(0);
                    Iterator keysToCopyIterator = firstJSONObject.keys();


                    String SQL = "INSERT OR REPLACE INTO " + tablename + " (";
                    String SQL1 = "";
                    String SQL2 = "";

                    while (keysToCopyIterator.hasNext()) {
                        String key = (String) keysToCopyIterator.next();
                        if (col.indexOf(key) == -1) {
                            SQL1 = SQL1.concat(key);
                            SQL2 = SQL2.concat("?");

                            if (keysToCopyIterator.hasNext()) {
                                SQL1 = SQL1.concat(",");
                                SQL2 = SQL2.concat(",");
                            }
                        }


                    }
                    SQL = SQL.concat(SQL1 + ") ");
                    SQL = SQL.concat("VALUES (" + SQL2 + ")");

                    SQLiteStatement statement = sqlite.compileStatement(SQL);
                    sqlite.beginTransaction();

                    for (int i = 0; i < json.length(); i++) {
                        JSONObject jo = json.getJSONObject(i);
                        Iterator column = jo.keys();
                        try {
                            JSONObject c1 = json.getJSONObject(i);
                            statement.clearBindings();
                            int col_index = 1;
                            while (column.hasNext()) {
                                String col_key = (String) column.next();
                                if (col.indexOf(col_key) == -1) {
                                    statement.bindString(col_index, c1.getString(col_key));
                                    col_index++;
                                }
                            }
                            statement.execute();

                        } catch (JSONException e) {
                            return false;
                        }

                    }


                    sqlite.setTransactionSuccessful();
                    sqlite.endTransaction();

                }
            }

        } catch (Exception e) {
            return false;
        }

        return true;

    }

    public Bitmap getRefelection(Bitmap image) {


        // The gap we want between the reflection and the original image
        final int reflectionGap = 0;

        // Get your bitmap from drawable folder
        Bitmap originalImage = image;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

  /*Create a Bitmap with the flip matix applied to it.
   We only want the bottom half of the image*/

        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                height / 2, width, height / 2, matrix, false);

        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Bitmap.Config.ARGB_8888);
        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        //Draw the reflection Image
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        // Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,
                originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
                + reflectionGap, 0x99ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);
        if (originalImage != null && originalImage.isRecycled()) {
            originalImage.recycle();
            originalImage = null;
        }
        if (reflectionImage != null && reflectionImage.isRecycled()) {
            reflectionImage.recycle();
            reflectionImage = null;
        }
        return bitmapWithReflection;
    }


}
