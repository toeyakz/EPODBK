package ws.epod.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ws.epod.Main_Activity;
import ws.epod.ObjectClass.SQLiteModel.JobList_Model;
import ws.epod.PlanWork_Activity;
import ws.epod.R;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {


    PlanWork_Activity planWork_activity;
    ArrayList<JobList_Model> list;

    Context context;

    public JobAdapter(ArrayList<JobList_Model> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public JobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_map, viewGroup, false);
        JobAdapter.ViewHolder viewHolder = new JobAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull JobAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.tvCustomer_name_Job.setText("("+list.get(i).getStation_code()+") "+list.get(i).getStation_name());
        viewHolder.tvStation_address_job.setText(list.get(i).getStation_address());
        viewHolder.tvPlan_seq_Job.setText(list.get(i).getPlan_seq() + ".");
        // viewHolder.textView20.setText(context.getString(R.string.appoint) + ": ");
        viewHolder.textView17.setText(context.getString(R.string.address) + ": ");

        Log.d("sfasef24", "onBindViewHolder: " + list.get(i).getPlan_seq());


        String pick = list.get(i).getPick();
        String pickup = list.get(i).getPickUp();
        String deli = list.get(i).getDeli();
        String delivery = list.get(i).getDelivery();

        if (!pick.equals("0") || !deli.equals("0")) {
            if ((pick.equals(pickup) && !deli.equals("0") && deli.equals(delivery)) ||
                    (deli.equals(delivery) && !pick.equals("0") && pick.equals(pickup))) {
              //  viewHolder.cdBackg.setBackgroundColor(Color.parseColor("#33FF33"));
                viewHolder.cdBackg.setCardBackgroundColor(Color.parseColor("#7cfc00"));
                viewHolder.cdBackg.setRadius(15);
            }else{
               // viewHolder.cdBackg.setBackgroundColor(Color.parseColor("#ffffff"));
                viewHolder.cdBackg.setCardBackgroundColor(Color.parseColor("#ffffff"));
                viewHolder.cdBackg.setRadius(15);
            }

        }

        String plan_out = dateNewFormat(list.get(i).getPlan_out());
        if (i == 0) {
            viewHolder.textView20.setText(context.getString(R.string.appoint) + ": ");
            viewHolder.tvPlan_in_Job.setText(plan_out);
        } else {
            viewHolder.textView20.setText(context.getString(R.string.appoint) + ": ");
            if (!list.get(i).getPlan_in().equals("")) {
                String plan_in = dateNewFormat(list.get(i).getPlan_in());
                viewHolder.tvPlan_in_Job.setText(plan_in);
            }

        }


//        if (!list.get(i).getPlan_in().equals("")) {
//            String plan_in = dateNewFormat(list.get(i).getPlan_in());
//            viewHolder.tvPlan_in_Job.setText(plan_in);
//        } else {
//            viewHolder.tvPlan_in_Job.setText("");
//        }


        viewHolder.tvStatusJob.setText(context.getString(R.string.pickup) + " (" + list.get(i).getPickUp() + "/" + list.get(i).getPick() + ") | " + context.getString(R.string.deliver) +
                " (" + list.get(i).getDelivery() + "/" + list.get(i).getDeli() + ")");

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                final AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setTitle(context.getString(R.string.alert));
                alertbox.setMessage(context.getString(R.string.want_to_navigate));

                alertbox.setNegativeButton(context.getString(R.string.navigate),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                try {
                                    if (list.get(i).getStation_lon() != 0) {
                                        String uri = "http://maps.google.com/maps?daddr=" + list.get(i).getStation_lat() + "," + list.get(i).getStation_lon() + " (" + "Where the party is at" + ")";
                                        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(uri));
                                        context.startActivity(intent);
                                    } else {

                                        Toast.makeText(context, "ลูกค้ารายนี้ไม่มีพิกัด ไม่สามารถนำทางได้", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception ex) {
                                    String Err = ex.getMessage();
                                }
                            }
                        });
                alertbox.setNeutralButton(context.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                alertbox.show();

                return true;
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!list.get(i).getPick().equals("0") || !list.get(i).getPickUp().equals("0")
                        || !list.get(i).getDeli().equals("0") || !list.get(i).getDelivery().equals("0")) {

                    Intent intent = new Intent(context, Main_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("plan_seq", list.get(i).getPlan_seq());
                    intent.putExtra("station_name", list.get(i).getStation_name());
                    intent.putExtra("station_address", list.get(i).getStation_address());
                    intent.putExtra("delivery_no", list.get(i).getDelivery_no());

                    if(i == 0 ){
                        intent.putExtra("plan_in", list.get(i).getPlan_out());
                    }else{
                        intent.putExtra("plan_in", list.get(i).getPlan_in());
                    }
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Can't start Page.", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCustomer_name_Job, tvStation_address_job, tvPlan_in_Job, tvPlan_seq_Job, tvStatusJob, textView20, textView17;
        CardView cdBackg;
        public ViewHolder(View itemView) {
            super(itemView);

            tvCustomer_name_Job = itemView.findViewById(R.id.tvCustomer_name_Job);
            tvStation_address_job = itemView.findViewById(R.id.tvStation_address_job);
            tvPlan_seq_Job = itemView.findViewById(R.id.tvPlan_seq_Job);
            tvPlan_in_Job = itemView.findViewById(R.id.tvPlan_in_Job);
            tvStatusJob = itemView.findViewById(R.id.tvStatusJob);
            textView20 = itemView.findViewById(R.id.textView20);
            textView17 = itemView.findViewById(R.id.textView17);
            cdBackg = itemView.findViewById(R.id.cdBackg);

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
