package ws.epod.scan.Util;

import java.util.ArrayList;
import java.util.HashMap;

import ws.epod.ObjectClass.SQLiteModel.PickingUpEexpand_Model;
import ws.epod.scan.model.delivery.InvoiceDelivery;
import ws.epod.scan.model.delivery.InvoiceHeaderDelivery;
import ws.epod.scan.model.pickup.Invoice;
import ws.epod.scan.model.pickup.InvoiceHeader;

public class UtilScan {

    public interface OnInvoiceListener {
        void onInvoiceSet(ArrayList<Invoice> lists);
    }

    private static OnInvoiceListener invoiceListener;

    public static void setInvoiceListener(OnInvoiceListener listener) {
        UtilScan.invoiceListener = listener;
    }

    //    public static void setInvoiceDeliveryListener(OnInvoiceListener listener) {
//        UtilScan.invoiceListener = listener;
//    }
    public static ArrayList<HashMap<String, String>> meMapArray = new ArrayList<>();
    public static HashMap<String,String> meMap = new HashMap<>();

    //MARK: Lists of invoice data
    private static ArrayList<Invoice> listInvoice = new ArrayList<>();
    private static ArrayList<InvoiceHeader> listInvoiceHeader = new ArrayList<>();

    private static ArrayList<InvoiceDelivery> listDeliveryWaybill = new ArrayList<>();
    private static ArrayList<InvoiceHeaderDelivery> listInvoiceHeaderDelivery = new ArrayList<>();


    public static void addMap(String key, String value) {
        UtilScan.meMap.put(key, value);
      //  meMapArray.add(meMap);
    }

    public static void addArMap(HashMap<String,String> meMap) {
        UtilScan.meMapArray.add(meMap);
        //  meMapArray.add(meMap);
    }

    public static HashMap<String, String> getMeMap()  {
        return UtilScan.meMap;
        //  meMapArray.add(meMap);
    }

    private static ArrayList<PickingUpEexpand_Model> pickArray = new ArrayList<>();
    // private static ArrayList<InvoiceHeaderDelivery> listInvoiceHeaderDelivery = new ArrayList<>();

    public static boolean containInvoiceNumber(String number) {
        for (Invoice inv : UtilScan.listInvoice) {
            if (inv.getWaybill_no().contains(number)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containInvoiceNumberDelivery(String number) {
        for (InvoiceDelivery inv : UtilScan.listDeliveryWaybill) {
            if (inv.getWaybill_no().contains(number)) {
                return true;
            }
        }
        return false;
    }

    public static void addPickArray(PickingUpEexpand_Model list) {
        UtilScan.pickArray.add(list);
    }

    public static void addInvoiceHeader(InvoiceHeader list) {
        UtilScan.listInvoiceHeader.add(list);
    }

    public static void clearInvoiceHeader() {
        // UtilScan.listInvoiceHeader.clear();
        listInvoiceHeader = new ArrayList<>();
        // listInvoiceHeader.onInvoiceSet(UtilScan.listInvoice);
    }

    public static void addInvoiceHeaderDelivery(InvoiceHeaderDelivery list) {
        UtilScan.listInvoiceHeaderDelivery.add(list);
    }

    public static void clearInvoiceHeaderDelivery() {
        listInvoiceHeaderDelivery = new ArrayList<>();
        //   invoiceListener.onInvoiceSet(null);
    }

    public static ArrayList<PickingUpEexpand_Model> getPickArray() {
        return UtilScan.pickArray;
    }

    public static ArrayList<InvoiceHeader> getListHeaderWaybill() {
        return UtilScan.listInvoiceHeader;
    }

    public static ArrayList<InvoiceHeaderDelivery> getListHeadeDeliveryrWaybill() {
        return UtilScan.listInvoiceHeaderDelivery;
    }


    public static void addInvoice(Invoice barcode) {
        UtilScan.listInvoice.add(barcode);
        invoiceListener.onInvoiceSet(UtilScan.listInvoice);
    }

    public static void addInvoiceDelivery(InvoiceDelivery barcode) {
        UtilScan.listDeliveryWaybill.add(barcode);
        // invoiceListener.onInvoiceSet(UtilScan.listInvoice);
    }

    public static void clearPickArray() {
        pickArray = new ArrayList<>();
        //   invoiceListener.onInvoiceSet(null);
    }

    public static void clearHeaderWaybillList() {
        UtilScan.listInvoice.clear();
        //   invoiceListener.onInvoiceSet(null);
    }

    public static void clearHeaderDeliveryWaybillList() {
        UtilScan.listDeliveryWaybill.clear();
        //   invoiceListener.onInvoiceSet(null);
    }

    public static void clearWaybillList() {
        //UtilScan.listInvoice.clear();
        invoiceListener.onInvoiceSet(UtilScan.listInvoice);
    }

    public static ArrayList<Invoice> getListWaybill() {
        return UtilScan.listInvoice;
    }

    public static ArrayList<InvoiceDelivery> getListDeliveryWaybill() {
        return UtilScan.listDeliveryWaybill;
    }
}
