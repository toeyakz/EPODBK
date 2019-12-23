package ws.epod.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ws.epod.DropPoint_Activity;
import ws.epod.ObjectClass.SQLiteModel.Plan_model;
import ws.epod.PlanWork_Activity;
import ws.epod.R;

public class PlanWorkAdapter extends RecyclerView.Adapter<PlanWorkAdapter.ViewHolder> {


    PlanWork_Activity planWork_activity;
    ArrayList<Plan_model> list;
    Context context;

    public PlanWorkAdapter( ArrayList<Plan_model> list, Context context ) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup viewGroup, int i ) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_newplan, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( @NonNull ViewHolder viewHolder, final int i ) {

        String Delivery_date = planWork_activity.dateNewFormat(list.get(i).getDelivery_date());

        viewHolder.tvPlanDate.setText(Delivery_date);
        viewHolder.tvRound_no.setText(list.get(i).getDelivery_no());
       // viewHolder.tvDrop_planSeq.setText(String.valueOf(list.get(i).getPlan_seq()));
        viewHolder.tvDrop_planSeq.setText(String.valueOf(list.get(i).getPlan_seq()));
        if(list.get(i).getPick() != null){
            viewHolder.tvDrop_pickUp.setText(String.valueOf(list.get(i).getPick()));
        }else{
            viewHolder.tvDrop_pickUp.setText("0");
        }
        if(list.get(i).getDeli() != null){
            viewHolder.tvDrop_deli.setText(String.valueOf(list.get(i).getDeli()));
        }else{
            viewHolder.tvDrop_deli.setText("0");
        }

        viewHolder.tvDrop_Finish.setText(String.valueOf(list.get(i).getFinish()));


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                //Toast.makeText(context, "" + list.get(i).getDelivery_no(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DropPoint_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra("round", list.get(i).getRound_no());
                intent.putExtra("delivery_no", list.get(i).getDelivery_no());
                intent.putExtra("delivery_date", list.get(i).getDelivery_date());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPlanDate, tvRound_no, tvDrop_planSeq, tvDrop_pickUp, tvDrop_deli, tvDrop_Finish;

        public ViewHolder( View itemView ) {
            super(itemView);

            tvPlanDate = itemView.findViewById(R.id.tvPlanDate);
            tvRound_no = itemView.findViewById(R.id.tvRound_no);
            tvDrop_planSeq = itemView.findViewById(R.id.tvDrop_planSeq);
            tvDrop_pickUp = itemView.findViewById(R.id.tvDrop_pickUp);
            tvDrop_deli = itemView.findViewById(R.id.tvDrop_deli);
            tvDrop_Finish = itemView.findViewById(R.id.tvDrop_Finish);

        }
    }


}
