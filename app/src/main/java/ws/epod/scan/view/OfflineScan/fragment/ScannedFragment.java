package ws.epod.scan.view.OfflineScan.fragment;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.ObjectClass.SQLiteModel.Sign_Model;
import ws.epod.ObjectClass.SQLiteModel.WaybillModel;
import ws.epod.R;
import ws.epod.scan.Util.OfflineScanUtil;
import ws.epod.scan.view.OfflineScan.adapter.WaybillScannedAdapter;
import ws.epod.scan.view.OfflineScan.adapter.WaybillUnScannedAdapter;

public class ScannedFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private RecyclerView rvScan;
    private ConstraintLayout btnSelect, btnUnSelect;
    private ImageView btnDeleteWaybill, imgBack_scan;

    private WaybillScannedAdapter adapter;


    public ScannedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scanned, container, false);

        initial(view);
        return view;
    }


    private void initial(View view) {

        databaseHelper = new DatabaseHelper(getContext());

        rvScan = view.findViewById(R.id.rvScan);
        btnSelect = view.findViewById(R.id.btnSelect);
        btnUnSelect = view.findViewById(R.id.btnUnSelect);
        btnDeleteWaybill = view.findViewById(R.id.btnDeleteWaybill);
        imgBack_scan = view.findViewById(R.id.imgBack_scan);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvScan.setLayoutManager(layoutManager);

        readData();
        onclick();

    }

    private void onclick() {

        btnDeleteWaybill.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.alpha);
            btnDeleteWaybill.startAnimation(animation);
            dialogDelete();
        });

        btnSelect.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.alpha);
            btnSelect.startAnimation(animation);
            boolean isSelectAll = true;

            for (int i = 0; i < adapter.getItemCount(); i++) {
                WaybillModel model = adapter.models.get(i);
                if (model.getInto().equals("0")) {
                    isSelectAll = true;
                    break;
                } else {
                    isSelectAll = false;
                }

            }

            if (isSelectAll) {

                for (int i = 0; i < adapter.getItemCount(); i++) {
                    WaybillModel model = adapter.models.get(i);
                    model.setInto("1");
                }
            } else {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    WaybillModel model = adapter.models.get(i);
                    model.setInto("0");
                }
            }
            rvScan.setAdapter(adapter);


        });

        imgBack_scan.setOnClickListener(v ->{
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.alpha);
            imgBack_scan.startAnimation(animation);
            if(getActivity() != null) {
                getActivity().finish();
            }
        });

    }

    private void dialogDelete() {

        AlertDialog alert = new AlertDialog.Builder(getContext()).create();
        alert.setTitle("ยืนยันการลบ Waybill?");
        alert.setCancelable(false);
        alert.setButton(getString(R.string.confirm), (dialog, which) -> {
            for (int i = 0; i < OfflineScanUtil.getSec().size(); i++) {
                Toasty.success(getContext(), OfflineScanUtil.getSec().size() + " items deleted!", Toast.LENGTH_SHORT, true).show();
                databaseHelper.db().delete("header_waybill", "id=?", new String[]{OfflineScanUtil.getSec().get(i).getId()});

            }
            readData();
        });
        alert.setButton2(getString(R.string.cancel), (dialog, which) -> alert.dismiss());
        alert.show();
    }

    private void readData() {

        ArrayList<WaybillModel> models = new ArrayList<>();

        String sql = "select * from header_waybill where status_complete = '1'";
        Cursor cursor = databaseHelper.selectDB(sql);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String waybill_no = cursor.getString(cursor.getColumnIndex("waybill_no"));
                String date_scan = cursor.getString(cursor.getColumnIndex("date_scan"));
                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                double lon = cursor.getDouble(cursor.getColumnIndex("lon"));
                String is_scanned = cursor.getString(cursor.getColumnIndex("is_scanned"));
                String status_complete = cursor.getString(cursor.getColumnIndex("status_complete"));

                models.add(new WaybillModel(id, waybill_no, date_scan, lat, lon, is_scanned, status_complete));

                Log.d("sdfs92d43", "readData: " + waybill_no);

            } while (cursor.moveToNext());
        }

        adapter = new WaybillScannedAdapter(models, getContext());
        rvScan.setAdapter(adapter);
    }
}
