package ws.epod.util;

import java.util.ArrayList;

import ws.epod.ObjectClass.SQLiteModel.WaybillModel;

public class Util {

    public static ArrayList<WaybillModel> listWaybill = new ArrayList<>();


    public static void addWaybill(WaybillModel WaybillModel){
        Util.listWaybill.add(WaybillModel);
    }

    public static void deleteWaybill(int position){
        Util.listWaybill.remove(position);
    }


}
