package ws.epod.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ws.epod.scan.view.delivery.Deliver_Activity;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.MenuObject;
import ws.epod.scan.view.pickup.Pickup_Activity;
import ws.epod.R;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    int resourceId[];
    ArrayList<MenuObject> list;
    Context context;
    ArrayList<Integer> itemsimg;
    private static final String VALID = "OK";

    ConnectionDetector netCon;
    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;

    private LocationManager client;


    public MainAdapter(ArrayList<MenuObject> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu, viewGroup, false);
        MainAdapter.ViewHolder viewHolder = new MainAdapter.ViewHolder(view);
        return viewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MainAdapter.ViewHolder viewHolder, final int i) {

        narisv = new NarisBaseValue(context);
        netCon = new ConnectionDetector(context);
        databaseHelper = new DatabaseHelper(context);

        viewHolder.picking_Menu.setText(list.get(i).TEXT);
        Log.d("ASfj5ad26", "onBindViewHolder: " + list.get(i).total_load);


        viewHolder.tvTotal.setText("Total: " + list.get(i).total_load);


        viewHolder.tvConsignmentMain.setText(list.get(i).CONSIGNMENT + "•" + context.getString(R.string.consignment));
        if (list.get(i).BOXES.equals("1")) {
            viewHolder.tvBoxesMain.setText(context.getString(R.string.box) + " (" + list.get(i).box_scanned + " | " + list.get(i).BOXES + ")");
        } else {
            viewHolder.tvBoxesMain.setText(context.getString(R.string.boxes) + " (" + list.get(i).box_scanned + " | " + list.get(i).BOXES + ")");
        }

        viewHolder.textView4.setText(list.get(i).GLOBAL + "•Global");
        viewHolder.icon.setImageResource(list.get(i).IMAGE);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (list.get(i).TEXT.equals("Picking Up")) {

                    // Toast.makeText(context, "Pick", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Pickup_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("isSync",true);
                    context.startActivity(intent);

                } else {// DELIVERY

                    // Toast.makeText(context, "deli", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Deliver_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("isSync",true);
                    context.startActivity(intent);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView picking_Menu, tvConsignmentMain, tvBoxesMain, textView4, tvTotal;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);

            picking_Menu = itemView.findViewById(R.id.picking_Menu);
            tvConsignmentMain = itemView.findViewById(R.id.tvConsignmentMain);
            tvBoxesMain = itemView.findViewById(R.id.tvBoxesMain);
            textView4 = itemView.findViewById(R.id.textView4);
            icon = itemView.findViewById(R.id.icon);
            tvTotal = itemView.findViewById(R.id.tvTotal);

        }
    }
}
