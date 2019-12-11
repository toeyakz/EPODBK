package ws.epod.Adapter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ws.epod.Deliver_Activity;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.MenuObject;
import ws.epod.R;
import ws.epod.PinkingUpMaster_Activity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

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


    public MainAdapter( ArrayList<MenuObject> list, Context context ) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder( @NonNull ViewGroup viewGroup, int i ) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu, viewGroup, false);
        MainAdapter.ViewHolder viewHolder = new MainAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder( @NonNull final MainAdapter.ViewHolder viewHolder, final int i ) {


        narisv = new NarisBaseValue(context);
        netCon = new ConnectionDetector(context);
        databaseHelper = new DatabaseHelper(context);

        viewHolder.picking_Menu.setText(list.get(i).TEXT);
        viewHolder.tvConsignmentMain.setText(list.get(i).CONSIGNMENT + "•"+context.getString(R.string.consignment));
        if(list.get(i).BOXES.equals("1")){
            viewHolder.tvBoxesMain.setText(list.get(i).BOXES + "•"+context.getString(R.string.box));
        }else{
            viewHolder.tvBoxesMain.setText(list.get(i).BOXES + "•"+context.getString(R.string.boxes));
        }

        viewHolder.textView4.setText(list.get(i).GLOBAL + "•"+context.getString(R.string.unit));
        viewHolder.icon.setImageResource(list.get(i).IMAGE);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( final View view ) {


                // Toast.makeText(context, list.get(i).TEXT, Toast.LENGTH_SHORT).show();
                if ( list.get(i).TEXT.equals("Picking Up") ) {

                    Toast.makeText(context, "Pick", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, PinkingUpMaster_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

//                    if ( !list.get(i).isscaned_pick.equals("0") ) {
//                        final AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
//                        alertbox.setTitle(context.getString(R.string.alert));
//                        alertbox.setMessage(context.getString(R.string.confirm_attendance));
//
//
//                        alertbox.setNegativeButton(context.getString(R.string.confirm),
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick( DialogInterface arg0,
//                                                         int arg1 ) {
//                                        String pattern = "yyyy-MM-dd kk:mm";
//                                        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
//                                        String date_now = sdf.format(Calendar.getInstance().getTime());
//
//                                        String stringLatitude = "";
//                                        String stringLongitude = "";
//
//                                        client = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//
//                                        if ( ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) !=
//                                                PackageManager.PERMISSION_GRANTED ) {
//                                            return;
//                                        }
//
//                                        Location location = client.getLastKnownLocation(client.NETWORK_PROVIDER);
//
////                                        double latitude = location.getLatitude();
////                                        double longitude = location.getLongitude();
////
////                                        stringLatitude = String.valueOf(latitude);
////                                        stringLongitude = String.valueOf(longitude);
////
////
////                                        Log.d("AfvWEBUHKLWLS", "onClick: " + date_now + "=>" + stringLatitude + "=>" + stringLongitude);
////                                        Log.d("AfvWEBUHKLWLS", "onClick: " + "=>" + stringLatitude + "=>" + stringLongitude);
////
//////
////                                        ContentValues cv = new ContentValues();
////                                        cv.put("time_actual_in", date_now);
////                                        cv.put("time_begin", date_now);
////                                        cv.put("actual_lat", stringLatitude);
////                                        cv.put("actual_lon", stringLongitude);
////                                        cv.put("actual_seq", list.get(i).actual_seq);
////                                        cv.put("modified_date", date_now);
////                                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + list.get(i).DELIVERY_NO + "' and plan_seq = '" + list.get(i).PLAN_SEQ + "' and activity_type = 'LOAD' and trash = '0'", null);
//
//                                        Toast.makeText(context, "Pick", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(context, PinkingUpMaster_Activity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        context.startActivity(intent);
//
//                                    }
//                                });
//                        alertbox.show();
//                    }else{
//                        Toast.makeText(context, "Pick", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(context, PinkingUpMaster_Activity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }



                } else {// DELIVERY

                    Toast.makeText(context, "deli", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Deliver_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

//                    if ( !list.get(i).isscaned_deli.equals("0") ) {
//                        final AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
//                        alertbox.setTitle(context.getString(R.string.alert));
//                        alertbox.setMessage(context.getString(R.string.confirm_attendance));
//
//
//                        alertbox.setNegativeButton(context.getString(R.string.confirm),
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick( DialogInterface arg0,
//                                                         int arg1 ) {
//                                        String pattern = "yyyy-MM-dd kk:mm";
//                                        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
//                                        String date_now = sdf.format(Calendar.getInstance().getTime());
//
//                                        String stringLatitude = "";
//                                        String stringLongitude = "";
//
//                                        client = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//
//                                        if ( ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) !=
//                                                PackageManager.PERMISSION_GRANTED ) {
//                                            return;
//                                        }
//
//                                        Location location = client.getLastKnownLocation(client.NETWORK_PROVIDER);
//
//                                        double latitude = location.getLatitude();
//                                        double longitude = location.getLongitude();
//
//                                        stringLatitude = String.valueOf(latitude);
//                                        stringLongitude = String.valueOf(longitude);
//
//
//                                        Log.d("AfvWEBUHKLWLS", "onClick: " + date_now + "=>" + stringLatitude + "=>" + stringLongitude);
//                                        Log.d("AfvWEBUHKLWLS", "onClick: " + "=>" + stringLatitude + "=>" + stringLongitude);
//
////
//                                        ContentValues cv = new ContentValues();
//                                        cv.put("time_actual_in", date_now);
//                                        cv.put("time_begin", date_now);
//                                        cv.put("actual_lat", stringLatitude);
//                                        cv.put("actual_lon", stringLongitude);
//                                        cv.put("actual_seq", list.get(i).actual_seq);
//                                        cv.put("modified_date", date_now);
//                                        databaseHelper.db().update("Plan", cv, "delivery_no= '" + list.get(i).DELIVERY_NO + "' and plan_seq = '" + list.get(i).PLAN_SEQ + "' and activity_type = 'UNLOAD' and trash = '0'", null);
//
//                                        Toast.makeText(context, "deli", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(context, Deliver_Activity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        context.startActivity(intent);
//
//                                    }
//                                });
//                        alertbox.show();
//                    }else{
//                        Toast.makeText(context, "deli", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(context, Deliver_Activity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView picking_Menu, tvConsignmentMain, tvBoxesMain, textView4;
        ImageView icon;

        public ViewHolder( View itemView ) {
            super(itemView);

            picking_Menu = itemView.findViewById(R.id.picking_Menu);
            tvConsignmentMain = itemView.findViewById(R.id.tvConsignmentMain);
            tvBoxesMain = itemView.findViewById(R.id.tvBoxesMain);
            textView4 = itemView.findViewById(R.id.textView4);
            icon = itemView.findViewById(R.id.icon);

        }
    }
}
