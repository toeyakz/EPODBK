package ws.epod;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.LanguageClass;
import ws.epod.ObjectClass.SQLiteModel.SignObjectClass;
import ws.epod.ObjectClass.SQLiteModel.Sign_Model;

public class Signature_Activity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    Button clear_button, save_button;
    SignaturePad signature_pad;

    ConnectionDetector netCon;
    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;

    // String getDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        LanguageClass.setLanguage(getApplicationContext());
        setContentView(R.layout.activity_signature_);

        narisv = new NarisBaseValue(Signature_Activity.this);
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

//        String pattern = "yyyy-MM-dd kk:mm:ss";
//        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
//        getDate = sdf.format(Calendar.getInstance().getTime());

        save_button = findViewById(R.id.save_button);
        clear_button = findViewById(R.id.clear_button);
        signature_pad = findViewById(R.id.signature_pad);


        signature_pad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Toast.makeText(Signature_Activity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                save_button.setEnabled(true);
                clear_button.setEnabled(true);
            }

            @Override
            public void onClear() {
                save_button.setEnabled(false);
                clear_button.setEnabled(false);
            }
        });

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signature_pad.clear();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = signature_pad.getSignatureBitmap();
                if (addJpgSignatureToGallery(signatureBitmap)) {
                    Toast.makeText(Signature_Activity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Signature_Activity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Signature_Activity.this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getdate() {

        String temp = "";
        String pattern = "yyyy-MM-dd kk:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
        temp = sdf.format(Calendar.getInstance().getTime());

        return temp;
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Gson gson = new Gson();
            String json = sharedPrefs.getString("MyObject", "");
            Type type = new TypeToken<ArrayList<Sign_Model>>() {
            }.getType();

            ArrayList<Sign_Model> arrayList = gson.fromJson(json, type);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "EPOD_" + timeStamp + "_";
            File storageDir = getExternalFilesDir("Signature");
            File image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );



            for (int i = 0; i < arrayList.size(); i++) {
                Log.d("Asfjshdflkasdfasd", "addJpgSignatureToGallery: " + arrayList.get(i).getOrder_no() + " > " + arrayList.get(i).getStatus() + " comment:" + arrayList.get(i).getComment());

                try {

                    JSONObject jsonInsertPicSign = new JSONObject();
                    JSONArray jsonArrayInsertPicSign = new JSONArray();
                    JSONObject jsonInsertComment = new JSONObject();
                    JSONArray jsonArrayInsertComment = new JSONArray();

                    jsonInsertPicSign.put("consignment_no", arrayList.get(i).getConsignment_no());
                    jsonInsertPicSign.put("order_no", arrayList.get(i).getOrder_no());
                    jsonInsertPicSign.put("invoice_no", arrayList.get(i).getDeli_note_no());
                    jsonInsertPicSign.put("status_load", arrayList.get(i).getStatus());
                    jsonInsertPicSign.put("pic_sign_load", image.getName());
                    jsonInsertPicSign.put("date_sign_load", getdate());
                    jsonInsertPicSign.put("status_upload_invoice", "0");

                    if (!arrayList.get(i).getComment().equals("")) {
                        jsonInsertComment.put("consignment_no", arrayList.get(i).getConsignment_no());
                        jsonInsertComment.put("order_no", arrayList.get(i).getOrder_no());
                        jsonInsertComment.put("invoice_no", arrayList.get(i).getDeli_note_no());
                        jsonInsertComment.put("comment", arrayList.get(i).getComment());
                        jsonInsertComment.put("status_load", arrayList.get(i).getStatus());
                        jsonInsertComment.put("delivery_no", arrayList.get(i).getDelivery_no());
                        jsonInsertComment.put("status_upload_comment","0");
                    }

                    //อัพเดต status ตาราง consignment
                    ContentValues cv = new ContentValues();
                    cv.put("status_order_no", arrayList.get(i).getStatus());
                    databaseHelper.db().update("Plan", cv, "consignment_no= '" + arrayList.get(i).getConsignment_no() + "' and order_no='" + arrayList.get(i).getOrder_no() + "'" +
                            " and activity_type = 'LOAD' and trash = '0'", null);


                    jsonArrayInsertPicSign.put(jsonInsertPicSign);
                    jsonArrayInsertComment.put(jsonInsertComment);
                    Log.d("sfdasdfasdf", "addJpgSignatureToGallery: "+jsonArrayInsertComment.toString());


                    if (!arrayList.get(i).getStatus().equals("0")) {
                        if (narisv.INSERT_AS_SQL("pic_sign", jsonArrayInsertPicSign, "")) {
                            Log.d("PlanWorkLOG", "SAVED Pic_sign.");
                            if (narisv.INSERT_AS_SQL("comment_invoice", jsonArrayInsertComment, "")) {
                                Log.d("PlanWorkLOG", "SAVED Comment.");
                            } else {
                                Log.d("PlanWorkLOG", "FAIL save Comment.");
                            }
                        } else {
                            Log.d("PlanWorkLOG", "FAIL save Pic_sign.");
                        }
                    }

                    ContentValues cv2 = new ContentValues();
                    cv2.put("name_img", image.getName());
                    cv2.put("status_img", "0");
                    databaseHelper.db().insert("image_invoice", null, cv2);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            sharedPrefs.edit().clear();


//
//            ContentValues cv = new ContentValues();
//            cv.put("signature", image.getName());
//            cv.put("modified_date", getDate);
//
//            for (int i = 0; i < consignment_no.size(); i++) {
//                String cons = consignment_no.get(i);
//                Log.d("weg3wBdw", "addJpgSignatureToGallery: " + cons);
//              //  databaseHelper.db().update("Plan", cv, "consignment_no = '" + cons + "' and trash = '0'", null);
//                databaseHelper.db().
//            }


            saveBitmapToJPG(signature, image);


        } catch (IOException e) {

        }
        result = true;

        return result;
    }


    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        stream.close();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we hav e write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}
