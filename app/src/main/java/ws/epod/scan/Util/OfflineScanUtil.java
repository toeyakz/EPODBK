package ws.epod.scan.Util;

import java.util.ArrayList;

import ws.epod.ObjectClass.SQLiteModel.WaybillModel;
import ws.epod.scan.model.OfflineScan.WaybillHeader;
import ws.epod.scan.model.OfflineScan.WaybillPoJo;
import ws.epod.scan.model.pickup.InvoiceHeader;

public class OfflineScanUtil {

    public interface OnWaybillListener {
        void onWaybillSet(ArrayList<WaybillPoJo> lists);
    }


    private static OnWaybillListener waybillListener;

    public static void setInvoiceListener(OnWaybillListener listener) {
        OfflineScanUtil.waybillListener = listener;
    }

    public static ArrayList<WaybillPoJo> list = new ArrayList<>();
    public static ArrayList<WaybillHeader> listHeader = new ArrayList<>();
    public static ArrayList<WaybillModel> listDelete= new ArrayList<>();

    public static boolean containInvoiceNumber(String number) {
        for(WaybillPoJo inv : OfflineScanUtil.list) {
            if(inv.getWaybill_no().contains(number)){ return true; }
        }
        return false;
    }

    public static void addWaybillHeader(WaybillHeader list) {
        OfflineScanUtil.listHeader.add(list);
    }

    public static void addWaybill(WaybillPoJo barcode) {
        OfflineScanUtil.list.add(barcode);
//        waybillListener.onWaybillSet(OfflineScanUtil.list);
    }


    public static void clearWaybillList() {
        OfflineScanUtil.list.clear();
        //   invoiceListener.onInvoiceSet(null);
    }

    public static void clearWaybillHeader() {
        OfflineScanUtil.listHeader.clear();
        //   invoiceListener.onInvoiceSet(null);
    }

    public static ArrayList<WaybillPoJo> getWaybillOffline() {
        return  OfflineScanUtil.list;
    }

    public static ArrayList<WaybillHeader> getWaybillHeader() {
        return  OfflineScanUtil.listHeader;
    }


    public static void addListToDelete(WaybillModel model ) {
        OfflineScanUtil.listDelete.add(model);
    }

    public static void deleteSec(int id) {
        OfflineScanUtil.listDelete.remove(id);
    }

    public static ArrayList<WaybillModel> getSec () {
        return OfflineScanUtil.listDelete;
    }





}
