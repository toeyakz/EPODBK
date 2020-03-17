package ws.epod.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import ws.epod.DropPoint_Activity;
import ws.epod.ObjectClass.SQLiteModel.Plan_model;
import ws.epod.PlanWork_Activity;
import ws.epod.R;

public class PlanSection extends StatelessSection {


    String title;
    ArrayList<Plan_model> item;
    Context mContext;

    PlanWork_Activity planWork_activity;

    public PlanSection(String title, ArrayList<Plan_model> item, Context mContext) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_newplan)
                .headerResourceId(R.layout.section_header)
                .build());
        this.title = title;
        this.item = item;
        this.mContext = mContext;
    }

    @Override
    public int getContentItemsTotal() {
        return item.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final ItemViewHolder itemHolder = (ItemViewHolder) viewHolder;

//        String Delivery_date = planWork_activity.dateNewFormat(item.get(i).getDelivery_date());
//        itemHolder.tvPlanDate.setText(Delivery_date);

        itemHolder.tvRound_no.setText(item.get(i).getDelivery_no());
        itemHolder.tvDrop_planSeq.setText(String.valueOf(item.get(i).getPlan_seq()));
        if (item.get(i).getPick() != null) {
            itemHolder.tvDrop_pickUp.setText(String.valueOf(item.get(i).getPick()));
        } else {
            itemHolder.tvDrop_pickUp.setText("0");
        }
        if (item.get(i).getDeli() != null) {
            itemHolder.tvDrop_deli.setText(String.valueOf(item.get(i).getDeli()));
        } else {
            itemHolder.tvDrop_deli.setText("0");
        }
        itemHolder.tvDrop_Finish.setText(String.valueOf(item.get(i).getFinish()));
        if(!item.get(i).getFinish().equals("0") && !item.get(i).getDeli().equals("0")){
            if(item.get(i).getFinish().equals(item.get(i).getDeli())){
//                itemHolder.tvRound_no.setTextColor(Color.parseColor("#1D781F"));
//                itemHolder.tvDrop_planSeq.setTextColor(Color.parseColor("#1D781F"));
//                itemHolder.tvDrop_deli.setTextColor(Color.parseColor("#1D781F"));
//                itemHolder.tvDrop_Finish.setTextColor(Color.parseColor("#1D781F"));
//
//                itemHolder.tvStation_h.setTextColor(Color.parseColor("#1D781F"));
//                itemHolder.deliNo_h.setTextColor(Color.parseColor("#1D781F"));
//                itemHolder.tvDeli_h.setTextColor(Color.parseColor("#1D781F"));
//                itemHolder.tvFinish_h.setTextColor(Color.parseColor("#1D781F"));
//                itemHolder.deliNo_h.setTextColor(Color.parseColor("#1D781F"));
//                itemHolder.view2.setBackgroundColor(Color.parseColor("#1D781F"));
//

                itemHolder.cv2.setCardBackgroundColor(Color.parseColor("#adff2f"));
                itemHolder.cv2.setRadius(30);

            }else{

                itemHolder.cv2.setCardBackgroundColor(Color.parseColor("#ffffff"));
                itemHolder.cv2.setRadius(30);
//                itemHolder.tvRound_no.setTextColor(Color.parseColor("#696969"));
//                itemHolder.tvDrop_planSeq.setTextColor(Color.parseColor("#696969"));
//                itemHolder.tvDrop_deli.setTextColor(Color.parseColor("#696969"));
//                itemHolder.tvDrop_Finish.setTextColor(Color.parseColor("#696969"));
//
//                itemHolder.tvStation_h.setTextColor(Color.parseColor("#696969"));
//                itemHolder.deliNo_h.setTextColor(Color.parseColor("#696969"));
//                itemHolder.tvDeli_h.setTextColor(Color.parseColor("#696969"));
//                itemHolder.tvFinish_h.setTextColor(Color.parseColor("#696969"));
//                itemHolder.deliNo_h.setTextColor(Color.parseColor("#696969"));
//                itemHolder.view2.setBackgroundColor(Color.parseColor("#696969"));
            }
        }




        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "" + list.get(i).getDelivery_no(), Toast.LENGTH_SHORT).show();

                Log.d("ASdfASF6ASDD", "Delivery no: " + item.get(i).getDelivery_no() + "Delivery Date: " + item.get(i).getDelivery_date());
                Intent intent = new Intent(mContext, DropPoint_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // intent.putExtra("plan_seq", list.get(i).getRound_no());
                intent.putExtra("delivery_no", item.get(i).getDelivery_no());
                intent.putExtra("delivery_date", item.get(i).getDelivery_date());

                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }


    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
        String mDate = format1.format(cal.getTime());


        Log.d("Asfjhsadklf", "onBindHeaderViewHolder: "+title);


        headerHolder.section.setText(dateNewFormat(title));


    }

    private String dateNewFormat(String pattern) {
        String pattern2 = "dd-MM-yyyy";
        SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy");
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



class HeaderViewHolder extends RecyclerView.ViewHolder {

    public final TextView section;

    HeaderViewHolder(View view) {
        super(view);
        section = view.findViewById(R.id.section);
    }
}

class ItemViewHolder extends RecyclerView.ViewHolder {
    public final View rootView, view2;
    public final TextView tvRound_no, tvDrop_planSeq, tvDrop_pickUp, tvDrop_deli, tvDrop_Finish;
    public TextView tvStation_h, tvFinish_h, tvDeli_h, deliNo_h;
    public CardView cv2;


    ItemViewHolder(View view) {
        super(view);

        rootView = view;

        //tvPlanDate = itemView.findViewById(R.id.tvPlanDate);
        tvRound_no = itemView.findViewById(R.id.tvRound_no);
        tvDrop_planSeq = itemView.findViewById(R.id.tvDrop_planSeq);
        tvDrop_pickUp = itemView.findViewById(R.id.tvDrop_pickUp);
        tvDrop_deli = itemView.findViewById(R.id.tvDrop_deli);
        tvDrop_Finish = itemView.findViewById(R.id.tvDrop_Finish);
        tvStation_h = itemView.findViewById(R.id.tvStation_h);
        tvFinish_h = itemView.findViewById(R.id.tvFinish_h);
        tvDeli_h = itemView.findViewById(R.id.tvDeli_h);
        deliNo_h = itemView.findViewById(R.id.deliNo_h);
        view2 = itemView.findViewById(R.id.view2);
        cv2 = itemView.findViewById(R.id.cv2);

    }


}

