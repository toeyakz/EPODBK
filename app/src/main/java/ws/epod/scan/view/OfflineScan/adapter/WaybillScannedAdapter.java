package ws.epod.scan.view.OfflineScan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ws.epod.ObjectClass.SQLiteModel.WaybillModel;
import ws.epod.R;

public class WaybillScannedAdapter  extends RecyclerView.Adapter<WaybillScannedAdapter.ViewHolder> {

    ArrayList<WaybillModel> models;
    Context context;

    public WaybillScannedAdapter(ArrayList<WaybillModel> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @NonNull
    @Override
    public WaybillScannedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_waybill, parent, false);
        WaybillScannedAdapter.ViewHolder viewHolder = new WaybillScannedAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        holder.tvWaybillOffline.setText("Waybill No:" + models.get(i).getWaybill_no());
        holder.tvDateScanOffline.setText("Date Scan:" + models.get(i).getDate_Scan());

        holder.tvDateScanOffline.setTextColor(Color.parseColor("#1D781F"));
        holder.tvWaybillOffline.setTextColor(Color.parseColor("#1D781F"));



    }

    @Override
    public int getItemCount() {
        return models.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvWaybillOffline, tvDateScanOffline;
        CheckBox checkBox_scan;

        public ViewHolder(View itemView) {
            super(itemView);

            tvWaybillOffline = itemView.findViewById(R.id.tvWaybillOffline);
            tvDateScanOffline = itemView.findViewById(R.id.tvDateScanOffline);
            checkBox_scan = itemView.findViewById(R.id.checkBox_scan);


        }
    }

}