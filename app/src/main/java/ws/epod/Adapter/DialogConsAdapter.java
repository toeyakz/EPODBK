package ws.epod.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ws.epod.ObjectClass.SQLiteModel.Dialog_Cons_Detail_Model;
import ws.epod.ObjectClass.SQLiteModel.JobList_Model;
import ws.epod.R;

public class DialogConsAdapter extends RecyclerView.Adapter<DialogConsAdapter.ViewHolder> {

    ArrayList<Dialog_Cons_Detail_Model> list;
    Context context;

    public DialogConsAdapter( ArrayList<Dialog_Cons_Detail_Model> list, Context context ) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public DialogConsAdapter.ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consign_detail, parent, false);
        DialogConsAdapter.ViewHolder viewHolder = new DialogConsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( @NonNull DialogConsAdapter.ViewHolder holder, int position ) {

        holder.textView3.setText("Global No: " + list.get(position).getGlobal_no());

        if ( !list.get(position).getDeli_note_amount_price().equals("")) {
            holder.textView13.setText("Price: " + list.get(position).getDeli_note_amount_price());
        } else {
            holder.textView13.setText("Price: -");
        }

        if( !list.get(position).getDetail_remarks().equals("")){
            holder.textView14.setText("Remark: " + list.get(position).getDetail_remarks());
        }else{
            holder.textView14.setText("Remark: -");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView3, textView13, textView14;

        public ViewHolder( View itemView ) {
            super(itemView);

            textView3 = itemView.findViewById(R.id.textView3);
            textView13 = itemView.findViewById(R.id.textView13);
            textView14 = itemView.findViewById(R.id.textView14);
//            tvPlan_in_Job = itemView.findViewById(R.id.tvPlan_in_Job);
//            tvStatusJob = itemView.findViewById(R.id.tvStatusJob);

        }
    }
}


