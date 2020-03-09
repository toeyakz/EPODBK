package ws.epod.signature.delivery;

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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.Main_Activity;
import ws.epod.ObjectClass.LanguageClass;
import ws.epod.ObjectClass.SQLiteModel.Reason_model;
import ws.epod.ObjectClass.SQLiteModel.Sign_Model;
import ws.epod.ObjectClass.SQLiteModel.Sign_i_Model;
import ws.epod.PlanWork_Activity;
import ws.epod.R;

public class InvoiceDeliver_Activity extends AppCompatActivity {

    private RecyclerView rvInv, recyclerView;
    private InvAdapter invAdapter;

    ImageView imgBack_Deliver, imageView22, sign;

    ConnectionDetector netCon;
    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;
    private int statusReject = 0;
    private int statusComplete = 0;
    private int statusReturn = 0;
    private int commentReject = 0;
    private int commentReturn = 0;
    private int commentComplete = 0;

    String commentOfspinner = "";

    TextView textView15, textView21, textView22, tvNoData;

    View popupInputDialogView = null;
    AlertDialog alertDialog;

    SignAdapter signAdapter;
    Button signIn;
    ConstraintLayout selectAllBtn;

    FloatingActionButton fabHome, fabJobHome, fabJobToday;
    Animation showLayout, hideLayout;
    LinearLayout layoutJobHome, layoutJobToday;

    boolean isCheckAll = true;
    int positionScll = 0;

    @Override
    protected void onResume() {
        super.onResume();
        setView();

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageClass.setLanguage(getApplicationContext());
        setContentView(R.layout.activity_invoice_deliver_);

        narisv = new NarisBaseValue(getApplicationContext());
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        fabHome = findViewById(R.id.fabHome);
        layoutJobHome = findViewById(R.id.layoutJobHome);
        layoutJobToday = findViewById(R.id.layoutJobToday);
        fabJobHome = findViewById(R.id.fabJobHome);
        fabJobToday = findViewById(R.id.fabJobToday);
        selectAllBtn = findViewById(R.id.btnSelect);

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

                    statusComplete = 0;
                    statusReject = 0;
                    statusReturn = 0;
                    commentReject = 0;
                    commentReturn = 0;
                    commentComplete = 0;
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

                            Log.d("Adfjksdfgagsdfg", "onPostExecute: " + sign_model.getStatus());

                            if (sign_model.getSignature().equals("")) {
                                if (sign_model.getStatus().equals("1")) {
                                    statusComplete += 1;

                                    if (!sign_model.getComment().equals("")) {
                                        commentComplete += 1;
                                    }
                                }
                                if (sign_model.getStatus().equals("2")) {
                                    statusReject += 1;

                                    if (!sign_model.getComment().equals("")) {
                                        commentReject += 1;
                                    }
                                }
                                if (sign_model.getStatus().equals("3")) {
                                    statusReturn += 1;
                                    if (!sign_model.getComment().equals("")) {
                                        commentReturn += 1;
                                    }
                                }

                                if (statusReturn != commentReturn || statusReject != commentReject) {
                                    positionScll = i;
                                } else {
                                    positionScll = 0;
                                }
                            }
                        }

                        if (isCheckIntent(statusReturn, statusReject, statusComplete, commentReturn, commentReject)) {
                            //Toast.makeText(Invoice_Activity.this, "Gooo", Toast.LENGTH_SHORT).show();
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                            sign.startAnimation(animation);
                            dataToSign();
                        } else {
                            // Toast.makeText(InvoiceDeliver_Activity.this, "Please select Invoice.", Toast.LENGTH_SHORT).show();
                        }
                        rvInv.getLayoutManager().scrollToPosition(positionScll);

                    }

                }
            }.execute();


        });

    }

    private boolean isCheckIntent(int statusReturn, int statusReject, int statusComplete, int commentReturn, int commentReject) {

        if (statusReject != 0 || statusComplete != 0 || commentReject != 0 || statusReturn != 0 || commentReturn != 0) {

            if (statusReturn != 0) {
                if (statusReturn == commentReturn) {
                    Log.d("checkIntent", "onPostExecute:  ติ๊ก return และ คอมเม้น return แล้ว");
                } else {
                    Log.d("checkIntent", "onPostExecute:  ติ๊ก return ไม่ได้คอมเม้น");
                    Toast.makeText(InvoiceDeliver_Activity.this, "Please comment return.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if (statusReject != 0) {
                if (statusReject == commentReject) {
                    Log.d("checkIntent", "onPostExecute:  ติ๊ก reject และ คอมเม้น reject แล้ว");
                } else {
                    Log.d("checkIntent", "onPostExecute:  ติ๊ก reject ไม่ได้คอมเม้น");
                    Toast.makeText(InvoiceDeliver_Activity.this, "Please comment reject.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }


            if (statusComplete != 0) {
                Log.d("checkIntent", "onPostExecute:  ติ๊ก complete แล้ว");
            }
//            else {
//                Log.d("checkIntent", "onPostExecute:  ติ๊ก complete ไม่ได้คอมเม้น");
//                return false;
//
//            }


        } else {
            Toast.makeText(InvoiceDeliver_Activity.this, "Please select invoice.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    private void dataToSign() {
        ArrayList<Sign_Model> signObjectClasses = new ArrayList<>();

        if (invAdapter == null) {
            Toast.makeText(this, "Adapter null.", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < invAdapter.getItemCount(); i++) {
                Sign_Model sign_model = invAdapter.list.get(i);

                if (!sign_model.getStatus().equals("") && sign_model.getSignature().equals("")) {
                    signObjectClasses.add(new Sign_Model(sign_model.getConsignment_no(), sign_model.getDeli_note_no(), sign_model.getStatus()
                            , sign_model.getSignature(), sign_model.getOrder_no(), sign_model.getDelivery_no(), sign_model.getComment(), sign_model.getRemark()));
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

        String sql = "select cm.deli_note_no    \n" +
                ", pl.activity_type   \n" +
                ", cm.consignment_no    \n" +
                ", pl.plan_seq   \n" +
                ",ifnull((select ps4.status_unload from pic_sign ps4 where ps4.order_no = pl.order_no and ps4.invoice_no = cm.deli_note_no ),'') as status    \n" +
                ",ifnull((select ps2.status_delete from pic_sign ps2 where ps2.order_no = pl.order_no and ps2.invoice_no = cm.deli_note_no) ,'') as status_delete  \n" +
                ",ifnull((select ps4.status_unload from pic_sign ps4 where ps4.order_no = pl.order_no and ps4.invoice_no = cm.deli_note_no and ps4.pic_sign_unload <> '' ),'') as status_unload    \n" +
                ", pl.order_no    \n" +
                ",(SELECT pl.delivery_no) AS delivery_no    \n" +
                ",ifnull((select ps2.comment_unload from pic_sign ps2 where ps2.order_no = pl.order_no and ps2.invoice_no = cm.deli_note_no) ,'') as comment_deliver    \n" +
                ",ifnull((select ps2.pic_sign_load from pic_sign ps2 where ps2.order_no = pl.order_no and ps2.invoice_no = cm.deli_note_no) ,'') as pic_sign_load    \n" +
                ",ifnull((select ps2.pic_sign_unload from pic_sign ps2 where ps2.order_no = pl.order_no and ps2.invoice_no = cm.deli_note_no and ps2.pic_sign_unload <> '' ) ,'') as pic_sign_unload    \n" +
                ", cm.detail_remarks  \n" +
                "from Consignment cm    \n" +
                "inner join Plan pl on pl.consignment_no = cm.consignment_no     \n" +
                "LEFT JOIN pic_sign ps on ps.consignment_no = cm.consignment_no    \n" +
                "where pl.delivery_no = '" + delivery_no + "' AND pl.plan_seq = '" + plan_seq + "' AND pl.activity_type = 'UNLOAD' and pl.is_scaned <> '0' and ps.status_load <> '0'   " +
                "AND pl.trash = '0'  and pl.order_no in (select ps6.order_no from pic_sign ps6 where ps6.pic_sign_load <> '')  \n" +
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
                String remark = cursor.getString(cursor.getColumnIndex("detail_remarks"));

                Log.d("AsfweosiugE", "setView: " + deli_note_no + ">" + consignment + ">" + status);

                sign_models.add(new Sign_Model(consignment, deli_note_no, status, signature, order_no, delivery_no2, comment, remark));

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

            holder.textView15.setText(context.getString(R.string.order_no) + ": " + list.get(position).getOrder_no());
            holder.textView21.setText(context.getString(R.string.consignment2) + ": " + list.get(position).getConsignment_no());
            holder.textView9.setText(context.getString(R.string.invoice_no) + ": " + list.get(position).getDeli_note_no());
            holder.remark.setText(context.getString(R.string.remark) + ": " + list.get(position).getRemark());


            if (!list.get(position).getSignature().equals("")) {
                holder.textView22.setText("Status: signed");
                holder.textView22.setVisibility(View.VISIBLE);
                holder.imageView11.setVisibility(View.VISIBLE);
                holder.reCheck.setVisibility(View.GONE);
                holder.reTurnCheck.setVisibility(View.GONE);
                holder.comCheck.setVisibility(View.INVISIBLE);
                holder.imgEditBoxNoPickup.setEnabled(false);
                holder.imgEditBoxNoPickup.setVisibility(View.GONE);

                if (list.get(position).getStatus().equals("1")) {
                    holder.status_show.setVisibility(View.VISIBLE);
                    holder.status_show.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_black_24dp));
                } else if (list.get(position).getStatus().equals("2")) {
                    holder.status_show.setVisibility(View.VISIBLE);
                    holder.status_show.setImageDrawable(getResources().getDrawable(R.drawable.ic_do_not_disturb_on_black_24dp));
                } else {
                    holder.status_show.setVisibility(View.VISIBLE);
                    holder.status_show.setImageDrawable(getResources().getDrawable(R.drawable.img_ret));
                }
            } else {
                //holder.textView22.setText("Status: signed");
                holder.textView22.setVisibility(View.GONE);
                holder.imageView11.setVisibility(View.GONE);
                holder.reCheck.setVisibility(View.VISIBLE);
                holder.reTurnCheck.setVisibility(View.VISIBLE);
                holder.comCheck.setVisibility(View.VISIBLE);
                holder.imgEditBoxNoPickup.setEnabled(true);
                holder.imgEditBoxNoPickup.setVisibility(View.VISIBLE);
                holder.status_show.setVisibility(View.GONE);

                if (list.get(position).getStatus().equals("") || list.get(position).getStatus().equals("0")) {
                    holder.imgEditBoxNoPickup.setEnabled(false);
                }

            }

//            if (list.get(position).getStatus().equals("") || list.get(position).getStatus().equals("0")) {
//                holder.imgEditBoxNoPickup.setEnabled(false);
//            }

            if (list.get(position).getStatus().equals("1")) {
                holder.comCheck.setChecked(true);
                if (!list.get(position).getComment().equals("")) {
                    holder.tvUseComment.setVisibility(View.GONE);
//                    holder.tvUseComment.setText("Commented.");
//                    holder.tvUseComment.setTextColor(R.color.colorPrimary);
//                    holder.tvUseComment.setVisibility(View.VISIBLE);
                } else {
                    holder.tvUseComment.setVisibility(View.GONE);
                }
                //  holder.reCheck.setChecked(false);
            } else {
                holder.comCheck.setChecked(false);
            }

            if (list.get(position).getStatus().equals("3")) {
                holder.reTurnCheck.setChecked(true);
                if (!list.get(position).getComment().equals("")) {
                    holder.tvUseComment.setVisibility(View.GONE);
//                    holder.tvUseComment.setText("Commented.");
//                    holder.tvUseComment.setTextColor(R.color.colorPrimary);
//                    holder.tvUseComment.setVisibility(View.VISIBLE);
                } else {
                    holder.tvUseComment.setText(context.getString(R.string.enter_reson));
                    holder.tvUseComment.setTextColor(Color.RED);
                    holder.tvUseComment.setVisibility(View.VISIBLE);
                }
            } else {
                holder.reTurnCheck.setChecked(false);
            }

            if (list.get(position).getStatus().equals("2")) {
                holder.reCheck.setChecked(true);
                if (!list.get(position).getComment().equals("")) {
                    holder.tvUseComment.setVisibility(View.GONE);
                    //holder.tvUseComment.setVisibility(View.GONE);
//                    holder.tvUseComment.setText("Commented.");
//                    holder.tvUseComment.setTextColor(R.color.colorPrimary);
//                    holder.tvUseComment.setVisibility(View.VISIBLE);

                } else {
                    holder.tvUseComment.setText(context.getString(R.string.enter_reson));
                    holder.tvUseComment.setTextColor(Color.RED);
                    holder.tvUseComment.setVisibility(View.VISIBLE);
                }
                // holder.comCheck.setChecked(false);
            } else {
                holder.reCheck.setChecked(false);
            }


            holder.comCheck.setOnClickListener(v -> {
                if (((CheckBox) v).isChecked()) {
                    updateList(position, "", String.valueOf(position));

                    holder.reCheck.setChecked(false);
                    holder.reTurnCheck.setChecked(false);
                    list.get(position).setStatus("1");
                    list.get(position).setInto("1");

                    if (list.get(position).getStatus().equals("1") && list.get(position).getComment().equals("")) {
                        Log.d("Akkksk", "onBindViewHolder: 1");
                        holder.tvUseComment.setVisibility(View.GONE);
                    } else if (list.get(position).getStatus().equals("1") && !list.get(position).getComment().equals("")) {
                        Log.d("Akkksk", "onBindViewHolder: 2");
                        holder.tvUseComment.setVisibility(View.GONE);
//                        holder.tvUseComment.setText("Commented.");
//                        holder.tvUseComment.setTextColor(R.color.colorPrimary);
//                        holder.tvUseComment.setVisibility(View.VISIBLE);
                    }
                    holder.imgEditBoxNoPickup.setEnabled(true);

                } else {
                    holder.imgEditBoxNoPickup.setEnabled(false);
                    list.get(position).setStatus("0");
                    list.get(position).setInto("0");

                    if (list.get(position).getStatus().equals("0") && list.get(position).getComment().equals("")) {
                        Log.d("Akkksk", "onBindViewHolder: 1");
                        holder.tvUseComment.setVisibility(View.GONE);
                    } else if (list.get(position).getStatus().equals("0") && !list.get(position).getComment().equals("")) {
                        holder.tvUseComment.setVisibility(View.GONE);
                        list.get(position).setComment("");
                    }
                }
            });


            holder.reCheck.setOnClickListener(v -> {
                if (((CheckBox) v).isChecked()) {
                    updateList(position, "", String.valueOf(position));
                    holder.comCheck.setChecked(false);
                    holder.reTurnCheck.setChecked(false);
                    list.get(position).setStatus("2");

                    if (list.get(position).getStatus().equals("2") && list.get(position).getComment().equals("")) {
                        Log.d("Akkksk", "onBindViewHolder: 1");

                        holder.tvUseComment.setVisibility(View.VISIBLE);
                        holder.tvUseComment.setText(context.getString(R.string.enter_reson));
                        holder.tvUseComment.setTextColor(Color.RED);
                    } else if (list.get(position).getStatus().equals("2") && !list.get(position).getComment().equals("")) {
                        Log.d("Akkksk", "onBindViewHolder: 2");
                        holder.tvUseComment.setVisibility(View.GONE);
//                        holder.tvUseComment.setText("Commented.");
//                        holder.tvUseComment.setVisibility(View.VISIBLE);
                    }

                    holder.imgEditBoxNoPickup.setEnabled(true);
                    Log.d("SDss", "onBindViewHolder:  > > isCheck: " + list.get(position).getStatus() + ">" + list.get(position).getDeli_note_no());
                } else {
                    list.get(position).setStatus("0");
                    if (list.get(position).getStatus().equals("0") && list.get(position).getComment().equals("")) {
                        Log.d("Akkksk", "onBindViewHolder: 1");
                        holder.tvUseComment.setVisibility(View.GONE);
                    } else if (list.get(position).getStatus().equals("0") && !list.get(position).getComment().equals("")) {
                        holder.tvUseComment.setVisibility(View.GONE);
                        list.get(position).setComment("");
                    }
                    holder.imgEditBoxNoPickup.setEnabled(false);
                    Log.d("SDss", "onBindViewHolder:  > > unCheck: " + list.get(position).getStatus() + ">" + list.get(position).getDeli_note_no());
                }
            });

            holder.reTurnCheck.setOnClickListener(v -> {
                if (((CheckBox) v).isChecked()) {
                    holder.imgEditBoxNoPickup.setEnabled(true);
                    updateList(position, "", String.valueOf(position));
                    holder.comCheck.setChecked(false);
                    holder.reCheck.setChecked(false);
                    list.get(position).setStatus("3");
                    if (list.get(position).getStatus().equals("3") && list.get(position).getComment().equals("")) {
                        Log.d("Akkksk", "onBindViewHolder: 1");
                        holder.tvUseComment.setVisibility(View.VISIBLE);
                        holder.tvUseComment.setText(context.getString(R.string.enter_reson));
                        holder.tvUseComment.setTextColor(Color.RED);
                    } else if (list.get(position).getStatus().equals("3") && !list.get(position).getComment().equals("")) {
                        Log.d("Akkksk", "onBindViewHolder: 2");
                        holder.tvUseComment.setVisibility(View.GONE);
//                        holder.tvUseComment.setText("Commented.");
//                        holder.tvUseComment.setTextColor(R.color.colorPrimary);
//                        holder.tvUseComment.setVisibility(View.VISIBLE);
                    }

                    Log.d("SDss", "onBindViewHolder:  > > isCheck: " + list.get(position).getStatus() + ">" + list.get(position).getDeli_note_no());
                } else {
                    holder.imgEditBoxNoPickup.setEnabled(false);
                    list.get(position).setStatus("0");
                    if (list.get(position).getStatus().equals("0") && list.get(position).getComment().equals("")) {
                        Log.d("Akkksk", "onBindViewHolder: 1");
                        holder.tvUseComment.setVisibility(View.GONE);
                    } else if (list.get(position).getStatus().equals("0") && !list.get(position).getComment().equals("")) {
                        holder.tvUseComment.setVisibility(View.GONE);
                        list.get(position).setComment("");
                    }


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
                        list.get(position).getOrder_no(), list.get(position).getDelivery_no(), position, list.get(position).getComment(), list.get(position).getStatus());
            });
            holder.imageView11.setOnClickListener(view -> {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                holder.imageView11.startAnimation(animation);
                showDialogCancel(list.get(position).getConsignment_no(), list.get(position).getDeli_note_no(), list.get(position).getOrder_no(), list.get(position).getSignature(), position);
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox comCheck, reCheck, reTurnCheck;
            ImageView imgEditBoxNoPickup, imageView11, status_show;
            TextView tvUseComment, textView21, textView22, textView15, textView9, remark;

            public ViewHolder(View itemView) {
                super(itemView);

                textView15 = itemView.findViewById(R.id.textView15);
                textView9 = itemView.findViewById(R.id.textView9);
                reTurnCheck = itemView.findViewById(R.id.reTurnCheck);
                tvUseComment = itemView.findViewById(R.id.tvUseComment);
                textView21 = itemView.findViewById(R.id.textView21);
                textView22 = itemView.findViewById(R.id.textView22);
                comCheck = itemView.findViewById(R.id.comCheck);
                reCheck = itemView.findViewById(R.id.reCheck);
                imgEditBoxNoPickup = itemView.findViewById(R.id.imgEditBoxNoPickup);
                imageView11 = itemView.findViewById(R.id.imageView11);
                status_show = itemView.findViewById(R.id.status_show);
                remark = itemView.findViewById(R.id.tvRemark);

            }
        }

        public void updateList2(int i, String comment, String status) {

            if (comment.equals("")) {
                list.get(i).setComment("");
            } else {
                list.get(i).setComment(comment);
            }

            notifyItemChanged(i, list.get(i));

            Log.d("position333", "updateList: " + list.get(i).getDeli_note_no());
            Log.d("position333", "updateList: " + i);
            // notifyItemChanged(i);

        }

        public void updateList(int i, String comment, String status) {

            if (status.equals("3")) {
                list.get(i).setComment("");
            } else if (status.equals("2")) {
                list.get(i).setComment("");
            } else {
                list.get(i).setComment(comment);
            }

            notifyItemChanged(i, list.get(i));

            Log.d("position333", "updateList: " + list.get(i).getDeli_note_no());
            Log.d("position333", "updateList: " + i);
            // notifyItemChanged(i);

        }

    }

    private void showDialogCancel(String cons, String deli_note, String order_no, String signature, int position) {
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

                                    Log.d("8s5sc", "doInBackground: " + deli_note);
                                    ContentValues cv = new ContentValues();
                                    cv.put("pic_sign_unload", "");
                                    cv.put("date_sign_unload", "");
                                    cv.put("status_unload", "0");
                                    cv.put("comment_unload", "");
                                    cv.put("status_upload_invoice", "0");
                                    // cv.put("delivery_no", "");
                                    databaseHelper.db().update("pic_sign", cv, "order_no = '" + order_no + "' and invoice_no = '" + deli_note + "' and pic_sign_unload <> ''", null);


                               /*     String sql = "select * from pic_sign ps" +
                                            "inner join Plan pl on pl.consignment_no = ps.consignment_no" +
                                            " where ps.invoice_no = '" + deli_note + "' and  pl.activity_type = 'UNLOAD' and pl.order_no in (select ps2.order_no from pic_sign ps2 where pic_sign_unload <> '')";
                                    Cursor cursor = databaseHelper.selectDB(sql);
                                    ArrayList<Sign_Model> sign_models = new ArrayList<>();
                                    cursor.moveToFirst();
                                    do {
                                        if (cursor.getCount() > 0) {

                                            String pic_sign_unload = cursor.getString(cursor.getColumnIndex("pic_sign_unload"));
                                            String date_sign_unload = cursor.getString(cursor.getColumnIndex("date_sign_unload"));
                                            String status_unload = cursor.getString(cursor.getColumnIndex("status_unload"));
                                            String comment_unload = cursor.getString(cursor.getColumnIndex("comment_unload"));
                                            String status_upload_invoice = cursor.getString(cursor.getColumnIndex("status_upload_invoice"));

                                            Log.d("pic_sign_log", "setView: " + pic_sign_unload + ">" + date_sign_unload + ">" + status_unload+ comment_unload + ">" + status_upload_invoice );


                                        }
                                    } while (cursor.moveToNext());*/
//                                    ContentValues cv2 = new ContentValues();
//                                    cv2.put("comment_load", "");
//                                    cv2.put("status_load", "");
//                                  //  cv2.put("delivery_no", "");
//                                    databaseHelper.db().update("comment_invoice", cv2, "order_no = '" + order_no + "' and comment_load <> ''", null);


                                    ContentValues cv3 = new ContentValues();
                                    cv3.put("name_img", "");
                                    databaseHelper.db().update("image_invoice", cv3, "name_img = '" + signature + "' ", null);


//                                    databaseHelper.db().delete("pic_sign", "order_no = ? and pic_sign_unload <> ? ", new String[]{order_no, "''"});
//                                    databaseHelper.db().delete("comment_invoice", "order_no = ? and comment_unload <> ?", new String[]{order_no, "''"});
//                                    databaseHelper.db().delete("image_invoice", "name_img = ?", new String[]{signature});

//                                    File file = new File("/storage/emulated/0/Android/data/ws.epod/files/Signature/" + signature);
//                                    file.delete();

                                } catch (Exception e) {

                                }

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                //invAdapter.notifyDataSetChanged();
                                Log.d("f9d3fsd5", "onPostExecute: 9gsd4g9sgsdgsdgsdgs");
                                setView();
                                rvInv.scrollToPosition(position);
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

    private void showDialogComment(String cons, String inv, String order_no, String delivery_no, int position, String lastComment, String status) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View popupInputDialogView = layoutInflater.inflate(R.layout.comment_sign, null);

        alertDialogBuilder.setView(popupInputDialogView);
        AlertDialog alertDialog = alertDialogBuilder.create();

        ImageView imgClose_dialog = popupInputDialogView.findViewById(R.id.imgClose_dialog);
        EditText edtComment_PICK = popupInputDialogView.findViewById(R.id.edtComment_PICK);
        Button btnSaveComent_PICK = popupInputDialogView.findViewById(R.id.btnSaveComent_PICK);

        TextView textView32 = popupInputDialogView.findViewById(R.id.textView32);

        textView32.setText(getApplicationContext().getString(R.string.reason) + ":");

        edtComment_PICK.setText(lastComment);

        List<String> categories = new ArrayList<>();
        categories.add("File");
        categories.add("Edit");
        categories.add("View");
        categories.add("Navigate");
        categories.add("Code");
        categories.add("Analyze");
        categories.add("Refactor");
        categories.add("Build");


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

        if (lastComment != null) {
            int spinnerPosition = adapter.getPosition(lastComment);
            spinner.setSelection(spinnerPosition + 1);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i != -1) {
                    commentOfspinner = adapterView.getItemAtPosition(i).toString();
                } else {
                    commentOfspinner = "00";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        imgClose_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                imgClose_dialog.startAnimation(animation);
                alertDialog.dismiss();
            }
        });

        btnSaveComent_PICK.setOnClickListener(view -> {
//            String commentText = "";
//            if (!edtComment_PICK.getText().toString().trim().matches("")) {
//                commentText = edtComment_PICK.getText().toString().trim();
//            }

            if (commentOfspinner.equals("00")) {
                Log.d("jjjjdjdj", "showDialogComment: ว่าง " + commentOfspinner);
                commentOfspinner = "";
                invAdapter.updateList2(position, commentOfspinner, status);
            } else {
                Log.d("jjjjdjdj", "showDialogComment: ไม่ว่าง" + commentOfspinner);
                invAdapter.updateList2(position, commentOfspinner, status);
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
//            invAdapter.updateList2(position, commentText, status);
//            invAdapter.list.get(position).setComment(commentText);
//            invAdapter.notifyItemChanged(position);

            alertDialog.dismiss();


        });


        alertDialog.show();
    }
}
