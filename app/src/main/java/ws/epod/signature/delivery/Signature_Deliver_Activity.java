package ws.epod.signature.delivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import ws.epod.BuildConfig;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.LanguageClass;
import ws.epod.ObjectClass.SQLiteModel.Sign_Model;
import ws.epod.R;

public class Signature_Deliver_Activity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    Button save_button, cancel_back;
    SignaturePad signature_pad;
    ImageView clear_button, imgTakePhoto, showImageSig;

    private ConnectionDetector netCon;
    private DatabaseHelper databaseHelper;
    private NarisBaseValue narisv;

    String currentPhotoPath;
    Uri imageUri;
    private boolean isSignatured = false;
    ArrayList<String> listImg = new ArrayList<>();
    String[] imgList = new String[2];


    // String getDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        LanguageClass.setLanguage(getApplicationContext());
        setContentView(R.layout.activity_signature__deliver_);

        narisv = new NarisBaseValue(Signature_Deliver_Activity.this);
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

//        String pattern = "yyyy-MM-dd kk:mm:ss";
//        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
//        getDate = sdf.format(Calendar.getInstance().getTime());

        save_button = findViewById(R.id.save_button);
        clear_button = findViewById(R.id.clear_button);
        signature_pad = findViewById(R.id.signature_pad);
        cancel_back = findViewById(R.id.cancel_back);
        imgTakePhoto = findViewById(R.id.imgTakePhoto);
        showImageSig = findViewById(R.id.showImageSig);

        cancel_back.setOnClickListener(v -> {
            if (imgList != null) {
                //  for (int i = 0; i < listImg.size(); i++) {
                File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Signature/" + imgList[0]);
                file.delete();
                imgList = new String[2];
                //  }
            }
            finish();
        });

        imgTakePhoto.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
            imgTakePhoto.startAnimation(animation);

            if (imgList != null) {
                //  for (int i = 0; i < listImg.size(); i++) {
                File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Signature/" + imgList[0]);
                file.delete();
                imgList = new String[2];
                //  }
            }

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            signature_pad.clear();
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, 01);
                }
            }


        });


        signature_pad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Toast.makeText(Signature_Activity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                save_button.setEnabled(true);
                imgList = new String[2];
                isSignatured = true;
            }

            @Override
            public void onClear() {
                save_button.setEnabled(false);
                isSignatured = false;
            }
        });

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signature_pad.clear();

                if (imgList != null) {
//                    for (int i = 0; i < listImg.size(); i++) {
                    File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Signature/" + imgList[0]);
                    file.delete();
                    imgList = new String[2];
                    //  }

                    showImageSig.setImageBitmap(null);
                    showImageSig.setVisibility(View.GONE);
                    signature_pad.setVisibility(View.VISIBLE);
                }


            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = signature_pad.getSignatureBitmap();

                Log.d("Asfjklasasdf", "onClick: " + imgList[0]);

                if (imgList[0] != null) {
                    if (takeImageFromcamera()) {
                        Log.d("Asfjklasasdf", "onClick: ถ่ายรูป");
                        Toasty.success(Signature_Deliver_Activity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                        imgList = new String[2];
                        finish();
                    } else {
                        Toasty.error(Signature_Deliver_Activity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (addJpgSignatureToGallery(signatureBitmap)) {
                        Log.d("Asfjklasasdf", "onClick: เซ้็น");
                        Toasty.success(Signature_Deliver_Activity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toasty.error(Signature_Deliver_Activity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            imageUri = Uri.parse(currentPhotoPath);
            final File file = new File(imageUri.getPath());


            try {
                ResizeImages(currentPhotoPath);
                Bitmap myBitmap = BitmapFactory.decodeFile(imageUri.getPath());
                if (imageUri.getPath() != null) {
                    listImg = new ArrayList<>();
                    listImg.add(file.getName());

                    imgList[0] = file.getName();

                    signature_pad.setVisibility(View.GONE);
                    showImageSig.setVisibility(View.VISIBLE);
                    showImageSig.setImageBitmap(myBitmap);
                    save_button.setEnabled(true);

                } else {

                }

                //  Log.d("getPatha", "onActivityResult: " + imageUri.getPath() + "=>01" + file.getPath() + "=>02" + file.getName());


            } catch (FileNotFoundException e) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

        File file = new File(sPath);
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(bytes.toByteArray());
        fo.close();

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "EPOD_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Signature");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toasty.error(Signature_Deliver_Activity.this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
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

    private boolean takeImageFromcamera() {
        boolean result = false;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPrefs.getString("DATA_SIGN_DELIVERY", "");
        Type type = new TypeToken<ArrayList<Sign_Model>>() {
        }.getType();

        ArrayList<Sign_Model> arrayList = gson.fromJson(json, type);

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
                jsonInsertPicSign.put("status_unload", arrayList.get(i).getStatus());
                jsonInsertPicSign.put("date_sign_unload", getdate());
                jsonInsertPicSign.put("status_upload_invoice", "0");
                jsonInsertPicSign.put("pic_sign_unload", imgList[0]);
                jsonInsertPicSign.put("create_date", getdate());
                jsonInsertPicSign.put("status_delete", "0");
                if (!arrayList.get(i).getComment().equals("")) {
                    jsonInsertPicSign.put("comment_unload", arrayList.get(i).getComment());
                } else {
                    jsonInsertPicSign.put("comment_unload", "");
                }
                jsonInsertPicSign.put("delivery_no", arrayList.get(i).getDelivery_no());

                String sql = "INSERT INTO image_invoice (name_img, status_img, create_date) VALUES('" + imgList[0] + "','0', '" + getdate() + "')";
                databaseHelper.db().execSQL(sql);


                if (!arrayList.get(i).getComment().equals("")) {
                    jsonInsertComment.put("consignment_no", arrayList.get(i).getConsignment_no());
                    jsonInsertComment.put("order_no", arrayList.get(i).getOrder_no());
                    jsonInsertComment.put("invoice_no", arrayList.get(i).getDeli_note_no());
                    jsonInsertComment.put("comment_unload", arrayList.get(i).getComment());
                    jsonInsertComment.put("status_unload", arrayList.get(i).getStatus());
                    jsonInsertComment.put("delivery_no", arrayList.get(i).getDelivery_no());
                    jsonInsertComment.put("status_upload_comment", "0");
                    jsonInsertComment.put("create_date", getdate());
                } else {
                    jsonInsertComment.put("consignment_no", arrayList.get(i).getConsignment_no());
                    jsonInsertComment.put("order_no", arrayList.get(i).getOrder_no());
                    jsonInsertComment.put("invoice_no", arrayList.get(i).getDeli_note_no());
                    jsonInsertComment.put("comment_unload", "");
                    jsonInsertComment.put("status_unload", arrayList.get(i).getStatus());
                    jsonInsertComment.put("delivery_no", arrayList.get(i).getDelivery_no());
                    jsonInsertComment.put("status_upload_comment", "0");
                    jsonInsertComment.put("create_date", getdate());
                }

                //อัพเดต status ตาราง consignment
                ContentValues cv = new ContentValues();
                cv.put("status_order_no", arrayList.get(i).getStatus());
                databaseHelper.db().update("Plan", cv, "consignment_no= '" + arrayList.get(i).getConsignment_no() + "' and order_no='" + arrayList.get(i).getOrder_no() + "'" +
                        " and activity_type = 'UNLOAD' and trash = '0'", null);


                jsonArrayInsertPicSign.put(jsonInsertPicSign);
                jsonArrayInsertComment.put(jsonInsertComment);
                Log.d("sfdasdfasdf", "addJpgSignatureToGallery: " + jsonArrayInsertComment.toString());


                if (!arrayList.get(i).getStatus().equals("0")) {

                    String select_invoice = "select ps.* " +
                            "from pic_sign ps " +
                            "where ps.delivery_no = '" + arrayList.get(i).getDelivery_no() + "' and ps.consignment_no = '" + arrayList.get(i).getConsignment_no() + "' " +
                            "and ps.order_no = '" + arrayList.get(i).getOrder_no() + "' and ps.invoice_no = '" + arrayList.get(i).getDeli_note_no() + "'";
                    Cursor cursor = databaseHelper.selectDB(select_invoice);

                    Log.d("isListDrops8a3a89", "total line " + cursor.getCount());
                    // ArrayList<JobList_Model> jobList_models = new ArrayList<>();
                    cursor.moveToFirst();
                    do {
                        if (cursor.getCount() > 0) {
                            String delivery_no = cursor.getString(cursor.getColumnIndex("delivery_no"));
                            String consignment_no = cursor.getString(cursor.getColumnIndex("consignment_no"));
                            String order_no = cursor.getString(cursor.getColumnIndex("order_no"));
                            String invoice_no = cursor.getString(cursor.getColumnIndex("invoice_no"));


                            ContentValues update_invoice = new ContentValues();
                            update_invoice.put("pic_sign_unload", imgList[0]);
                            if (!arrayList.get(i).getComment().equals("")) {
                                update_invoice.put("comment_unload", arrayList.get(i).getComment());
                            } else {
                                update_invoice.put("comment_unload", "");
                            }
                            update_invoice.put("date_sign_unload", getdate());
                            update_invoice.put("status_unload", arrayList.get(i).getStatus());
                            update_invoice.put("status_upload_invoice", "0");
                            // cv.put("delivery_no", "");
                            databaseHelper.db().update("pic_sign", update_invoice, "delivery_no = '" + delivery_no + "' and consignment_no = '" + consignment_no + "'" +
                                    "and order_no = '" + order_no + "' and invoice_no = '" + invoice_no + "' ", null);


                        }

                    } while (cursor.moveToNext());

                    /*if (narisv.INSERT_AS_SQL("pic_sign", jsonArrayInsertPicSign, "")) {
                        Log.d("PlanWorkLOG", "SAVED Pic_sign.");
                        if (narisv.INSERT_AS_SQL("comment_invoice", jsonArrayInsertComment, "")) {
                            Log.d("PlanWorkLOG", "SAVED Comment.");
                        } else {
                            Log.d("PlanWorkLOG", "FAIL save Comment.");
                        }
                    } else {
                        Log.d("PlanWorkLOG", "FAIL save Pic_sign.");
                    }*/
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        sharedPrefs.edit().clear();
        result = true;

        return result;


    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Gson gson = new Gson();
            String json = sharedPrefs.getString("DATA_SIGN_DELIVERY", "");
            Type type = new TypeToken<ArrayList<Sign_Model>>() {
            }.getType();


            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "EPOD_" + timeStamp + "_";
            File storageDir = getExternalFilesDir("Signature");
            File image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );

            ArrayList<Sign_Model> arrayList = gson.fromJson(json, type);
            saveBitmapToJPG(signature, image);

            for (int i = 0; i < arrayList.size(); i++) {


                Log.d("Asfjshdflkasdfasd", "addJpgSignatureToGallery: " + arrayList.get(i).getDeli_note_no());
//
                JSONObject jsonInsertPicSign = new JSONObject();
                JSONArray jsonArrayInsertPicSign = new JSONArray();

                JSONObject jsonInsertComment = new JSONObject();
                JSONArray jsonArrayInsertComment = new JSONArray();

                try {
                    jsonInsertPicSign.put("consignment_no", arrayList.get(i).getConsignment_no());
                    jsonInsertPicSign.put("order_no", arrayList.get(i).getOrder_no());
                    jsonInsertPicSign.put("invoice_no", arrayList.get(i).getDeli_note_no());
                    jsonInsertPicSign.put("status_unload", arrayList.get(i).getStatus());
                    jsonInsertPicSign.put("date_sign_unload", getdate());
                    jsonInsertPicSign.put("status_upload_invoice", "0");
                    jsonInsertPicSign.put("pic_sign_unload", image.getName());
                    jsonInsertPicSign.put("create_date", getdate());
                    jsonInsertPicSign.put("status_delete", "0");
                    if (!arrayList.get(i).getComment().equals("")) {
                        jsonInsertPicSign.put("comment_unload", arrayList.get(i).getComment());
                    } else {
                        jsonInsertPicSign.put("comment_unload", "");
                    }
                    jsonInsertPicSign.put("delivery_no", arrayList.get(i).getDelivery_no());

                    String sql = "INSERT INTO image_invoice (name_img, status_img, create_date) VALUES('" + image.getName() + "','0', '" + getdate() + "')";
                    databaseHelper.db().execSQL(sql);


                    if (!arrayList.get(i).getComment().equals("")) {
                        jsonInsertComment.put("consignment_no", arrayList.get(i).getConsignment_no());
                        jsonInsertComment.put("order_no", arrayList.get(i).getOrder_no());
                        jsonInsertComment.put("invoice_no", arrayList.get(i).getDeli_note_no());
                        jsonInsertComment.put("comment_unload", arrayList.get(i).getComment());
                        jsonInsertComment.put("status_unload", arrayList.get(i).getStatus());
                        jsonInsertComment.put("delivery_no", arrayList.get(i).getDelivery_no());
                        jsonInsertComment.put("status_upload_comment", "0");
                        jsonInsertComment.put("create_date", getdate());
                    } else {
                        jsonInsertComment.put("consignment_no", arrayList.get(i).getConsignment_no());
                        jsonInsertComment.put("order_no", arrayList.get(i).getOrder_no());
                        jsonInsertComment.put("invoice_no", arrayList.get(i).getDeli_note_no());
                        jsonInsertComment.put("comment_unload", "");
                        jsonInsertComment.put("status_unload", arrayList.get(i).getStatus());
                        jsonInsertComment.put("delivery_no", arrayList.get(i).getDelivery_no());
                        jsonInsertComment.put("status_upload_comment", "0");
                        jsonInsertComment.put("create_date", getdate());
                    }

                    ContentValues cv = new ContentValues();
                    cv.put("status_order_no", arrayList.get(i).getStatus());
                    databaseHelper.db().update("Plan", cv, "consignment_no= '" + arrayList.get(i).getConsignment_no() + "' and order_no='" + arrayList.get(i).getOrder_no() + "'" +
                            " and activity_type = 'UNLOAD' and trash = '0'", null);


                    jsonArrayInsertPicSign.put(jsonInsertPicSign);
                    jsonArrayInsertComment.put(jsonInsertComment);


                    String select_invoice = "select ps.* " +
                            "from pic_sign ps " +
                            "where ps.delivery_no = '" + arrayList.get(i).getDelivery_no() + "' and ps.consignment_no = '" + arrayList.get(i).getConsignment_no() + "' " +
                            "and ps.order_no = '" + arrayList.get(i).getOrder_no() + "' and ps.invoice_no = '" + arrayList.get(i).getDeli_note_no() + "'";
                    Cursor cursor = databaseHelper.selectDB(select_invoice);

                    Log.d("isListDrops8a3a89", "total line " + cursor.getCount());
                    // ArrayList<JobList_Model> jobList_models = new ArrayList<>();
                    cursor.moveToFirst();
                    do {
                        if (cursor.getCount() > 0) {
                            String delivery_no = cursor.getString(cursor.getColumnIndex("delivery_no"));
                            String consignment_no = cursor.getString(cursor.getColumnIndex("consignment_no"));
                            String order_no = cursor.getString(cursor.getColumnIndex("order_no"));
                            String invoice_no = cursor.getString(cursor.getColumnIndex("invoice_no"));


                            ContentValues update_invoice = new ContentValues();
                            update_invoice.put("pic_sign_unload", image.getName());
                            if (!arrayList.get(i).getComment().equals("")) {
                                update_invoice.put("comment_unload", arrayList.get(i).getComment());
                            } else {
                                update_invoice.put("comment_unload", "");
                            }
                            update_invoice.put("date_sign_unload", getdate());
                            update_invoice.put("status_unload", arrayList.get(i).getStatus());
                            update_invoice.put("status_upload_invoice", "0");
                            // cv.put("delivery_no", "");
                            databaseHelper.db().update("pic_sign", update_invoice, "delivery_no = '" + delivery_no + "' and consignment_no = '" + consignment_no + "'" +
                                    "and order_no = '" + order_no + "' and invoice_no = '" + invoice_no + "' ", null);


                        }

                    } while (cursor.moveToNext());

                    /*if (narisv.INSERT_AS_SQL("pic_sign", jsonArrayInsertPicSign, "")) {
                        Log.d("PlanWorkLOG", "SAVED Pic_sign.");
                        if (narisv.INSERT_AS_SQL("comment_invoice", jsonArrayInsertComment, "")) {
                            Log.d("PlanWorkLOG", "SAVED Comment.");
                        } else {
                            Log.d("PlanWorkLOG", "FAIL save Comment.");
                        }
                    } else {
                        Log.d("PlanWorkLOG", "FAIL save Pic_sign.");
                    }*/


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            sharedPrefs.edit().clear();


            // saveBitmapToJPG(signature, image);


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
