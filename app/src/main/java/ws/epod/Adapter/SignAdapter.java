package ws.epod.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ws.epod.ObjectClass.SQLiteModel.Dialog_Cons_Detail_Model;
import ws.epod.ObjectClass.SQLiteModel.Sign_i_Model;
import ws.epod.R;

public class SignAdapter extends RecyclerView.Adapter<SignAdapter.ViewHolder> {

    public static ArrayList<Sign_i_Model> list;
    Context context;

    public SignAdapter( ArrayList<Sign_i_Model> list, Context context ) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SignAdapter.ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sign_i_layout, parent, false);
        SignAdapter.ViewHolder viewHolder = new SignAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( @NonNull SignAdapter.ViewHolder holder, int position ) {

        holder.textView15.setText(list.get(position).getDeli_note_no());
        holder.textView21.setText(list.get(position).getConsignment_no());

        if ( list.get(position).getStatus().equals("1") ) {
            holder.textView28.setText("Confirm");
        } else if ( list.get(position).getStatus().equals("2") ) {
            holder.textView28.setText("Reject");
        }

        holder.checkBox.setOnCheckedChangeListener(( compoundButton, b ) -> {
            if ( b ) {
                list.get(position).setConsignment_no(list.get(position).getConsignment_no());
            } else {
                list.get(position).setConsignment_no("");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView15, textView21, textView28;
        CheckBox checkBox;

        public ViewHolder( View itemView ) {
            super(itemView);

            textView15 = itemView.findViewById(R.id.textView15);
            textView21 = itemView.findViewById(R.id.textView21);
            textView28 = itemView.findViewById(R.id.textView28);
            checkBox = itemView.findViewById(R.id.checkBox);

        }
    }
}
