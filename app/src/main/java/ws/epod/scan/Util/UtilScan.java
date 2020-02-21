package ws.epod.scan.Util;

import java.util.ArrayList;

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

    //MARK: Lists of invoice data
    private static ArrayList<Invoice> listInvoice = new ArrayList<>();
    private static ArrayList<InvoiceHeader> listInvoiceHeader = new ArrayList<>();

    private static ArrayList<InvoiceDelivery> listDeliveryWaybill = new ArrayList<>();
    private static ArrayList<InvoiceHeaderDelivery> listInvoiceHeaderDelivery = new ArrayList<>();

    public static boolean containInvoiceNumber(String number) {
        for(Invoice inv : UtilScan.listInvoice) {
            if(inv.getWaybill_no().contains(number)){ return true; }
        }
        return false;
    }

    public static boolean containInvoiceNumberDelivery(String number) {
        for(InvoiceDelivery inv : UtilScan.listDeliveryWaybill) {
            if(inv.getWaybill_no().contains(number)){ return true; }
        }
        return false;
    }

    public static void addInvoiceHeader(InvoiceHeader list) {
        UtilScan.listInvoiceHeader.add(list);
    }

    public static void addInvoiceHeaderDelivery(InvoiceHeaderDelivery list) {
        UtilScan.listInvoiceHeaderDelivery.add(list);
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
