package ws.epod;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.LanguageClass;
import ws.epod.ObjectClass.SQLiteModel.PickingUpEexpand_Model;
import ws.epod.ObjectClass.SQLiteModel.PickingUp_Model;

public class PickingUp_Activity extends AppCompatActivity {

    RecyclerView rvPickingUp;

    ConnectionDetector netCon;
    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;

    ArrayList<PickingUp_Model> list = new ArrayList<>();
    ArrayList<PickingUpEexpand_Model> list_expand = new ArrayList<>();
    HashMap<ArrayList<PickingUp_Model>, ArrayList<PickingUpEexpand_Model>> listDataChild;

    //PickingUpAdapter pickingUpAdapter;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        LanguageClass.setLanguage(getApplicationContext());
        setContentView(R.layout.activity_picking_up_);

        narisv = new NarisBaseValue(PickingUp_Activity.this);
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        rvPickingUp = findViewById(R.id.rvPickingUp);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvPickingUp.setLayoutManager(layoutManager);

        initView();
    }

    private void initView() {

        final SharedPreferences user_data = getSharedPreferences("DATA_DETAIL", Context.MODE_PRIVATE);
        String delivery_no = user_data.getString("delivery_no", "");
        String plan_seq = user_data.getString("plan_seq", "");

        String sql = "select (select DISTINCT consignment_no from Plan where activity_type = 'LOAD' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "') as consignment," +
                " (select count(box_no) from Plan where activity_type = 'LOAD' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "') as box_total," +
                " (select count(box_no) from Plan where activity_type = 'LOAD' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "' and is_scaned = '1') as box_checked" +
                " from Plan GROUP BY delivery_no";
        Cursor cursor = databaseHelper.selectDB(sql);
        Log.d("PickingUpLOG", "total line " + cursor.getCount());


        list = new ArrayList<>();
        while (cursor.moveToNext()) {

            String consignment = cursor.getString(cursor.getColumnIndex("consignment"));
            String box_total = cursor.getString(cursor.getColumnIndex("box_total"));
            String box_checked = cursor.getString(cursor.getColumnIndex("box_checked"));

            Log.d("PickingUpLOG", "onCreate: " + "==>" + consignment + "==>" + box_total + "==>" + box_checked);

            list.add(new PickingUp_Model(consignment, box_total, box_checked, "", "","","", ""));

        }

        String sql_expand = "select box_no, waybill_no from Plan where activity_type = 'LOAD' and delivery_no = '" + delivery_no + "' and plan_seq = '" + plan_seq + "'";
        Cursor cursor_expand = databaseHelper.selectDB(sql_expand);
        Log.d("PickingUpLOG", "total line " + cursor_expand.getCount());

        list_expand = new ArrayList<>();
        while (cursor_expand.moveToNext()) {

            String box_no = cursor.getString(cursor.getColumnIndex("box_no"));
            String waybill_no = cursor.getString(cursor.getColumnIndex("waybill_no"));

            list_expand.add(new PickingUpEexpand_Model(box_no, waybill_no,"","", "","","","","","","", "","",""));




        }
        listDataChild = new HashMap<>();
        listDataChild.put(list, list_expand);

        user_data.edit().clear();
//        pickingUpAdapter = new PickingUpAdapter(list, list_expand, listDataChild, getApplicationContext());
//        rvPickingUp.setAdapter(pickingUpAdapter);


    }
}
