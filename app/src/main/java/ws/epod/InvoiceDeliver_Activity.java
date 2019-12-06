package ws.epod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.SQLiteModel.Sign_Model;
import ws.epod.ObjectClass.SQLiteModel.Sign_i_Model;

public class InvoiceDeliver_Activity extends AppCompatActivity {

    private RecyclerView rvInv, recyclerView;
    private InvAdapter invAdapter;

    ImageView imgBack_Deliver, imageView22;

    ConnectionDetector netCon;
    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;
    private int statusArray = 0;
    private int statusArray2 = 0;
    private int commentArray = 0;

    TextView textView15, textView21, textView22, sign, tvNoData;

    View popupInputDialogView = null;
    AlertDialog alertDialog;

    SignAdapter signAdapter;
    Button signIn;
    ConstraintLayout selectAllBtn;

    FloatingActionButton fabHome, fabJobHome, fabJobToday;
    Animation showLayout, hideLayout;
    LinearLayout layoutJobHome, layoutJobToday;

    boolean isCheckAll = true;

    @Override
    protected void onResume() {
        super.onResume();
        setView();

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_deliver_);

        narisv = new NarisBaseValue(getApplicationContext());
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        fabHome = findViewById(R.id.fabHome);
        layoutJobHome = findViewById(R.id.layoutJobHome);
        layoutJobToday = findViewById(R.id.layoutJobToday);
        fabJobHome = findViewById(R.id.fabJobHome);
        fabJobToday = findViewById(R.id.fabJobToday);
        selectAllBtn = findViewById(R.id.selectAllBtn);

        imgBack_Deliver = findViewById(R.id.imgBack_Deliver);
        sign = findViewById(R.id.sign);

        showLayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_layout);
        hideLayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_layout);

        imgBack_Deliver.setOnClickListener(view -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
            imgBack_Deliver.startAnimation(animation);

            finish();
        });

        rvInv = findViewById(R.id.rvInv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvInv.setLayoutManager(layoutManager);

        rvInv.addOnScrollListener(new CustomScrollListener() {
        });
        setView();
        onClickFab();

        sign.setOnClickListener(view -> {

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    SharedPreferences user_data = getSharedPreferences("DATA_DETAIL_PICK", Context.MODE_PRIVATE);
                    String delivery_no = user_data.getString("delivery_no", "");
                    String plan_seq = user_data.getString("plan_seq", "");

                    if (invAdapter == null) {
                        cancel(true);
                    } else {

                        ArrayList<Sign_Model> sign_models = new ArrayList<>();
                        for (int i = 0; i < invAdapter.getItemCount(); i++) {
                            Sign_Model sign_model = invAdapter.list.get(i);

                            sign_models.add(sign_model);


                        }

                        invAdapter = new InvAdapter(sign_models, getApplicationContext());
                    }

                    statusArray = 0;
                    commentArray = 0;
                    statusArray2 = 0;
//

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    try {
                        rvInv.setAdapter(invAdapter);
                        //invAdapter.notifyDataSetChanged();
                    } finally {


                        for (int i = 0; i < invAdapter.getItemCount(); i++) {
                            Sign_Model sign_model = invAdapter.list.get(i);

                            if (sign_model.getStatus().equals("1")) {
                                statusArray2 += 1;
                            }
                            if (sign_model.getStatus().equals("2")) {
                                statusArray += 1;

                                if (!sign_model.getComment().equals("")) {
                                    commentArray += 1;
                                }
                            }
                        }

                        if (statusArray != 0 || statusArray2 != 0 || commentArray != 0) {

                            if (statusArray != 0 && commentArray != 0 && statusArray == commentArray && statusArray2 != 0) {
                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                                sign.startAnimation(animation);
                                dataToSign();
                                //  showDialogBox();
                                Log.d("hhhjujh", "onPostExecute: 3");
                            } else if (statusArray2 != 0 && statusArray != commentArray) {
                                Log.d("hhhjujh", "onPostExecute: 4");
                            } else if (statusArray2 != 0) {
                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                                sign.startAnimation(animation);
                                dataToSign();
                                //showDialogBox();
                                Log.d("hhhjujh", "onPostExecute: 2");
                            } else if (statusArray == commentArray) {
                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                                sign.startAnimation(animation);
                                dataToSign();
                                // showDialogBox();
                                Log.d("hhhjujh", "onPostExecute: 1");
                            } else if (statusArray != commentArray) {
                                Log.d("hhhjujh", "onPostExecute: เลือก reject - ยังไม่คอมเม้น");
                            }

                        } else {
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                            sign.startAnimation(animation);
                            Toast.makeText(InvoiceDeliver_Activity.this, "Please select Invoice.", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }.execute();


        });

    }

    private void dataToSign() {
        ArrayList<Sign_Model> signObjectClasses = new ArrayList<>();

        if (invAdapter == null) {
            Toast.makeText(this, "Adapter null.", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < invAdapter.getItemCount(); i++) {
                Sign_Model sign_model = invAdapter.list.get(i);

                if (!sign_model.getStatus().equals("0")) {
                    signObjectClasses.add(new Sign_Model(sign_model.getConsignment_no(), sign_model.getDeli_note_no(), sign_model.getStatus()
                            , sign_model.getSignature(), sign_model.getOrder_no(), sign_model.getDelivery_no(), sign_model.getComment()));
                }

            }
        }
        for (int i = 0; i < signObjectClasses.size(); i++) {
            Log.d("Akfjkasdhgflasfdgji", "dataToSign: " + signObjectClasses.get(i).getConsignment_no() + " > " + signObjectClasses.get(i).getStatus() + " comment: " + signObjectClasses.get(i).getComment());
        }


        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(signObjectClasses);
        prefsEditor.putString("DATA_SIGN_DELIVERY", json);
        prefsEditor.commit();

        Intent intent = new Intent(getApplicationContext(), Signature_Deliver_Activity.class);
        // intent.putExtra("signObjectClasses", bundle);
        // intent.putExtra("delivery_no", delivery_no);
        startActivity(intent);
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

        selectAllBtn.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
            selectAllBtn.startAnimation(animation);
            boolean isSelectAll = true;

            for (int i = 0; i < invAdapter.getItemCount(); i++) {
                Sign_Model sign_model = invAdapter.list.get(i);
                if (sign_model.getInto().equals("0")) {
                    isSelectAll = true;
                    break;
                } else {
                    isSelectAll = false;
                }

            }

            if (isSelectAll) {

                for (int i = 0; i < invAdapter.getItemCount(); i++) {
                    Sign_Model sign_model = invAdapter.list.get(i);
                    sign_model.setInto("1");
                    sign_model.setStatus("1");
                    sign_model.setComment("");
                }
            } else {
                for (int i = 0; i < invAdapter.getItemCount(); i++) {
                    Sign_Model sign_model = invAdapter.list.get(i);
                    sign_model.setInto("0");
                    sign_model.setStatus("0");
                    sign_model.setComment("");

                }
            }

            rvInv.setAdapter(invAdapter);


        });
    }

    public abstract class CustomScrollListener extends RecyclerView.OnScrollListener {

        public CustomScrollListener() {
        }

        @SuppressLint("RestrictedApi")
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    Log.d("Asfas5f", "The RecyclerView is not scrolling");
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    Log.d("Asfas5f", "Scrolling now");
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    Log.d("Asfas5f", "Scroll Settling");
                    break;

            }

        }

        @SuppressLint("RestrictedApi")
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {


            if (dy > 0) {
                Log.d("112121", "Scrolled Downwards");
                fabHome.hide();
                hideAll();

            } else if (dy < 0) {
                Log.d("112121", "Scrolled Upwards");
                fabHome.show();
                System.out.println("Scrolled Upwards");

            }
        }
    }

    private void hideAll() {

        layoutJobHome.startAnimation(hideLayout);
        layoutJobToday.startAnimation(hideLayout);
//        layBar.startAnimation(hideLayout);
//        layBar.setVisibility(View.GONE);
        layoutJobHome.setVisibility(View.GONE);
        layoutJobToday.setVisibility(View.GONE);

    }

    private void showAll() {

        layoutJobHome.startAnimation(showLayout);
        layoutJobToday.startAnimation(showLayout);
        layoutJobHome.setVisibility(View.VISIBLE);
        layoutJobToday.setVisibility(View.VISIBLE);

    }


    private void setView() {

        SharedPreferences user_data = getSharedPreferences("DATA_DETAIL_PICK", Context.MODE_PRIVATE);
        String delivery_no = user_data.getString("delivery_no", "");
        String plan_seq = user_data.getString("plan_seq", "");

        String sql = "select cm.deli_note_no  \n" +
                ", pl.activity_type \n" +
                ", cm.consignment_no  \n" +
                ", pl.plan_seq \n" +
                ",ifnull((select ps4.status_load from pic_sign ps4 where ps4.order_no = pl.order_no ),'') as status  \n" +
                ",ifnull((select ps4.status_unload from pic_sign ps4 where ps4.order_no = pl.order_no and ps4.pic_sign_unload <> '' ),'') as status_unload  \n" +
                ", pl.order_no  \n" +
                ",(SELECT pl.delivery_no) AS delivery_no  \n" +
                ",ifnull((select ci2.comment_deliver from comment_invoice ci2 where ci2.order_no = pl.order_no) ,'') as comment_deliver  \n" +
                ",ifnull((select ps2.pic_sign_load from pic_sign ps2 where ps2.order_no = pl.order_no) ,'') as pic_sign_load  \n" +
                ",ifnull((select ps2.pic_sign_unload from pic_sign ps2 where ps2.order_no = pl.order_no and ps2.pic_sign_unload <> '' ) ,'') as pic_sign_unload  \n" +
                "from Consignment cm  \n" +
                "inner join Plan pl on pl.consignment_no = cm.consignment_no  \n" +
                "LEFT JOIN comment_invoice ci on ci.consignment_no = cm.consignment_no  \n" +
                "LEFT JOIN pic_sign ps on ps.consignment_no = cm.consignment_no  \n" +
                "where pl.delivery_no = '" + delivery_no + "' AND pl.plan_seq = '" + plan_seq + "' AND pl.activity_type = 'UNLOAD' and ps.status_load = '1'  AND pl.trash = '0'  and pl.order_no in (select ps6.order_no from pic_sign ps6 where ps6.pic_sign_load <> '')  " +
                "GROUP by deli_note_no";
        Cursor cursor = databaseHelper.selectDB(sql);
        Log.d("isMapRoute", "total line " + sql);

        ArrayList<Sign_Model> sign_models = new ArrayList<>();
        cursor.moveToFirst();
        do {
            if (cursor.getCount() > 0) {

                String deli_note_no = cursor.getString(cursor.getColumnIndex("deli_note_no"));
                String consignment = cursor.getString(cursor.getColumnIndex("consignment_no"));
                String status = cursor.getString(cursor.getColumnIndex("status_unload"));
                String signature = cursor.getString(cursor.getColumnIndex("pic_sign_unload"));
                String order_no = cursor.getString(cursor.getColumnIndex("order_no"));
                String delivery_no2 = cursor.getString(cursor.getColumnIndex("delivery_no"));
                String comment = cursor.getString(cursor.getColumnIndex("comment_deliver"));

                Log.d("AsfweosiugE", "setView: " + deli_note_no + ">" + consignment + ">" + status);

                sign_models.add(new Sign_Model(consignment, deli_note_no, status, signature, order_no, delivery_no2, comment));

            }
        } while (cursor.moveToNext());


        invAdapter = new InvAdapter(sign_models, this);
        rvInv.setAdapter(invAdapter);

    }

    public class SignAdapter extends RecyclerView.Adapter<SignAdapter.ViewHolder> {

        ArrayList<Sign_i_Model> list;
        Context context;

        public SignAdapter(ArrayList<Sign_i_Model> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public SignAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sign_i_layout, parent, false);
            SignAdapter.ViewHolder viewHolder = new SignAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SignAdapter.ViewHolder holder, int position) {

            holder.textView15.setText(list.get(position).getDeli_note_no());
            holder.textView21.setText(list.get(position).getConsignment_no());

            if (list.get(position).getStatus().equals("1")) {
                holder.textView28.setText("Confirm");
            } else if (list.get(position).getStatus().equals("2")) {
                holder.textView28.setText("Reject");
            }

            if (isCheckAll) {
                holder.checkBox.setChecked(true);
                list.get(position).setInto("1");
                holder.checkBox.setEnabled(false);
            } else {
                holder.checkBox.setChecked(false);
                list.get(position).setInto("0");
                holder.checkBox.setEnabled(false);
            }

            holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    list.get(position).setInto("1");
                } else {
                    list.get(position).setInto("0");
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView textView15, textView21, textView28;
            CheckBox checkBox;

            public ViewHolder(View itemView) {
                super(itemView);

                textView15 = itemView.findViewById(R.id.textView15);
                textView21 = itemView.findViewById(R.id.textView21);
                textView28 = itemView.findViewById(R.id.textView28);
                checkBox = itemView.findViewById(R.id.checkBox);

            }
        }
    }

    public class InvAdapter extends RecyclerView.Adapter<InvAdapter.ViewHolder> {


        ArrayList<Sign_Model> list;
        Context context;

        InvAdapter(ArrayList<Sign_Model> list, Context context) {
            this.list = list;
            this.context = context;

        }

        public void reload(int i) {
            notifyItemChanged(i);
        }

        @NonNull
        @Override
        public InvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inv, parent, false);
            InvAdapter.ViewHolder viewHolder = new InvAdapter.ViewHolder(view);
            return viewHolder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(@NonNull InvAdapter.ViewHolder holder, int position) {

            holder.textView15.setText("InvoiceNo: " + list.get(position).getDeli_note_no());
            holder.textView21.setText("ConsignmentNo: " + list.get(position).getConsignment_no());


            if (!list.get(position).getSignature().equals("")) {
                holder.textView22.setText("Status: signed");
                holder.textView22.setVisibility(View.VISIBLE);
                holder.imageView11.setVisibility(View.VISIBLE);
                holder.reCheck.setVisibility(View.GONE);
                holder.comCheck.setVisibility(View.INVISIBLE);
                holder.imgEditBoxNoPickup.setEnabled(false);
            }
            if (list.get(position).getStatus().equals("0")) {
                holder.imgEditBoxNoPickup.setEnabled(false);
            }

            if (list.get(position).getStatus().equals("1")) {
                holder.comCheck.setChecked(true);
                //  holder.reCheck.setChecked(false);
            } else {
                holder.comCheck.setChecked(false);
            }


            if (list.get(position).getStatus().equals("2")) {
                holder.reCheck.setChecked(true);
                if (!list.get(position).getComment().equals("")) {
                    //holder.tvUseComment.setVisibility(View.GONE);
                    holder.tvUseComment.setText("Commented.");
                    holder.tvUseComment.setTextColor(R.color.colorPrimary);
                    holder.tvUseComment.setVisibility(View.VISIBLE);

                } else {
                    holder.tvUseComment.setVisibility(View.VISIBLE);
                }
                // holder.comCheck.setChecked(false);
            } else {
                holder.reCheck.setChecked(false);
            }


            holder.comCheck.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    holder.reCheck.setChecked(false);
                    list.get(position).setStatus("1");
                    list.get(position).setInto("1");
                    holder.imgEditBoxNoPickup.setEnabled(true);

                } else {
                    holder.imgEditBoxNoPickup.setEnabled(false);
                    list.get(position).setStatus("0");
                    list.get(position).setInto("0");
                }
            });
            holder.reCheck.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    holder.comCheck.setChecked(false);
                    list.get(position).setStatus("2");
                    holder.imgEditBoxNoPickup.setEnabled(true);
                    Log.d("SDss", "onBindViewHolder:  > > isCheck: " + list.get(position).getStatus() + ">" + list.get(position).getDeli_note_no());
                } else {
                    list.get(position).setStatus("0");
                    holder.imgEditBoxNoPickup.setEnabled(false);
                    Log.d("SDss", "onBindViewHolder:  > > unCheck: " + list.get(position).getStatus() + ">" + list.get(position).getDeli_note_no());
                }


            });


            if (list.get(position).getStatus().equals("0")) {
                holder.comCheck.setChecked(false);
            } else if (list.get(position).getStatus().equals("1")) {
                holder.comCheck.setChecked(true);
            }


            holder.imgEditBoxNoPickup.setOnClickListener(view -> {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                holder.imgEditBoxNoPickup.startAnimation(animation);
                showDialogComment(list.get(position).getConsignment_no(), list.get(position).getDeli_note_no(),
                        list.get(position).getOrder_no(), list.get(position).getDelivery_no(), position, list.get(position).getComment());
            });
            holder.imageView11.setOnClickListener(view -> {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                holder.imageView11.startAnimation(animation);
                showDialogCancel(list.get(position).getConsignment_no(), list.get(position).getDeli_note_no(), list.get(position).getOrder_no());
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox comCheck, reCheck;
            ImageView imgEditBoxNoPickup, imageView11;
            TextView tvUseComment, textView21, textView22, textView15;

            public ViewHolder(View itemView) {
                super(itemView);

                textView15 = itemView.findViewById(R.id.textView15);
                tvUseComment = itemView.findViewById(R.id.tvUseComment);
                textView21 = itemView.findViewById(R.id.textView21);
                textView22 = itemView.findViewById(R.id.textView22);
                comCheck = itemView.findViewById(R.id.comCheck);
                reCheck = itemView.findViewById(R.id.reCheck);
                imgEditBoxNoPickup = itemView.findViewById(R.id.imgEditBoxNoPickup);
                imageView11 = itemView.findViewById(R.id.imageView11);

            }
        }

        public void updateList(int i, String comment) {

            list.get(i).setComment(comment);
            notifyItemChanged(i, list.get(i));

            Log.d("position333", "updateList: " + list.get(i).getDeli_note_no());
            Log.d("position333", "updateList: " + i);
            // notifyItemChanged(i);

        }

    }

    private void showDialogCancel(String cons, String deli_note, String order_no) {
        final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setTitle(getString(R.string.alert));
        alertbox.setMessage("Cancel ?");


        alertbox.setNegativeButton(getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @SuppressLint("StaticFieldLeak")
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... voids) {
                                try {
//                                    ContentValues cv = new ContentValues();
//                                    cv.put("status", "0");
//                                    //  cv.put("modified_date", getDate);
//                                    databaseHelper.db().update("consignment", cv, "consignment_no= '" + cons + "' and deli_note_no = '" + deli_note + "' and trash = '0'", null);

                                    databaseHelper.db().delete("pic_sign", "order_no = ? and pic_sign_unload <> ? ", new String[]{order_no, "''"});
                                    databaseHelper.db().delete("comment_invoice", "order_no = ? and comment_deliver <> ?", new String[]{order_no, "''"});
                                } catch (Exception e) {

                                }

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                //invAdapter.notifyDataSetChanged();
                                setView();
                            }
                        }.execute();


                    }
                });
        alertbox.setNeutralButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();

                    }
                });

        alertbox.show();
    }

    private void showDialogComment(String cons, String inv, String order_no, String delivery_no, int position, String lastComment) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View popupInputDialogView = layoutInflater.inflate(R.layout.comment_sign, null);

        alertDialogBuilder.setView(popupInputDialogView);
        AlertDialog alertDialog = alertDialogBuilder.create();

        ImageView imgClose_dialog = popupInputDialogView.findViewById(R.id.imgClose_dialog);
        EditText edtComment_PICK = popupInputDialogView.findViewById(R.id.edtComment_PICK);
        Button btnSaveComent_PICK = popupInputDialogView.findViewById(R.id.btnSaveComent_PICK);

        edtComment_PICK.setText(lastComment);


        imgClose_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                imgClose_dialog.startAnimation(animation);
                alertDialog.dismiss();
            }
        });

        btnSaveComent_PICK.setOnClickListener(view -> {
            String commentText = "";
            if (!edtComment_PICK.getText().toString().trim().matches("")) {
                commentText = edtComment_PICK.getText().toString().trim();
            }

//            JSONObject json = new JSONObject();
//            JSONArray jsonArray = new JSONArray();
//
//            try {
//                json.put("consignment_no", cons);
//                json.put("order_no", order_no);
//                json.put("invoice_no", inv);
//                json.put("comment", commentText);
//                json.put("delivery_no", delivery_no);
//
//                jsonArray.put(json);
//                if (narisv.INSERT_AS_SQL("comment_invoice", jsonArray, "")) {
//                    Log.d("PlanWorkLOG", "SAVED Consignment.");
//                } else {
//                    Log.d("PlanWorkLOG", "FAIL save consignment.");
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            invAdapter.updateList(position, commentText);
//            invAdapter.list.get(position).setComment(commentText);
//            invAdapter.notifyItemChanged(position);

            alertDialog.dismiss();


        });


        alertDialog.show();
    }
}
