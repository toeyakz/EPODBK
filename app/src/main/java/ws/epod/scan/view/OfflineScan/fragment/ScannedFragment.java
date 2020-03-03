package ws.epod.scan.view.OfflineScan.fragment;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ws.epod.Helper.DatabaseHelper;
import ws.epod.ObjectClass.SQLiteModel.WaybillModel;
import ws.epod.R;
import ws.epod.scan.view.OfflineScan.adapter.WaybillScannedAdapter;
import ws.epod.scan.view.OfflineScan.adapter.WaybillUnScannedAdapter;

public class ScannedFragment extends Fragment {

    private DatabaseHelper databaseHelper;

    private RecyclerView rvScan;

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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvScan.setLayoutManager(layoutManager);

        readData();
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
