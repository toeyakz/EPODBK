package ws.epod.ObjectClass.SQLiteModel;

import java.util.ArrayList;

public class PickingUp_Model {
    public String consignment = "";
    public String box_total = "";
    public String box_checked = "";
    public String global_total = "";
    public String station_address = "";
    public String paytype = "";
    public String global_cancel = "";
    public String price = "";
    public int count = 0;
    private int total_b;
    private int num = 0;
    //public ArrayList<PickingUpEexpand_Model> list_expand;

    public PickingUp_Model( String consignment, String box_total,String box_checked, String global_total, String station_address, String paytype, String global_cancel,String price,int total_b ) {
        this.consignment = consignment;
        this.box_total = box_total;
        this.box_checked = box_checked;
        this.global_total = global_total;
        this.station_address = station_address;
        this.paytype = paytype;
        this.global_cancel = global_cancel;
        this.price = price;
        this.total_b = total_b;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getTotal_b() {
        return total_b;
    }

    public void setTotal_b(int total_b) {
        this.total_b = total_b;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice( String price ) {
        this.price = price;
    }

    public String getGlobal_total() {
        return global_total;
    }

    public void setGlobal_total( String global_total ) {
        this.global_total = global_total;
    }

    public String getGlobal_cancel() {
        return global_cancel;
    }

    public void setGlobal_cancel( String global_cancel ) {
        this.global_cancel = global_cancel;
    }

    public String getStation_address() {
        return station_address;
    }

    public void setStation_address( String station_address ) {
        this.station_address = station_address;
    }

    public String getBox_total() {
        return box_total;
    }

    public void setBox_total( String box_total ) {
        this.box_total = box_total;
    }

    public String getBox_checked() {
        return box_checked;
    }

    public void setBox_checked( String box_checked ) {
        this.box_checked = box_checked;
    }

    public String getConsignment() {
        return consignment;
    }

    public void setConsignment( String consignment ) {
        this.consignment = consignment;
    }



    public String getPaytype() {
        return paytype;
    }

    public void setPaytype( String paytype ) {
        this.paytype = paytype;
    }
}

