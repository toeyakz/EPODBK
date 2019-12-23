package ws.epod.ObjectClass.SQLiteModel;

public class Deliver_Model {
    public String consignment_no = "";
    public String box_total = "";
    public String box_checked = "";
    public String global_total = "";
    public String station_address = "";
    public String paytype = "";
    public String global_cancel = "";
    public String price = "";


    public Deliver_Model( String consignment_no, String box_total,String box_checked, String global_total, String station_address, String paytype, String global_cancel, String price ) {
        this.consignment_no = consignment_no;
        this.box_total = box_total;
        this.box_checked = box_checked;
        this.global_total = global_total;
        this.station_address = station_address;
        this.paytype = paytype;
        this.global_cancel = global_cancel;
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice( String price ) {
        this.price = price;
    }

    public String getConsignment_no() {
        return consignment_no;
    }

    public void setConsignment_no( String consignment_no ) {
        this.consignment_no = consignment_no;
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

    public String getGlobal_total() {
        return global_total;
    }

    public void setGlobal_total( String global_total ) {
        this.global_total = global_total;
    }

    public String getStation_address() {
        return station_address;
    }

    public void setStation_address( String station_address ) {
        this.station_address = station_address;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype( String paytype ) {
        this.paytype = paytype;
    }

    public String getGlobal_cancel() {
        return global_cancel;
    }

    public void setGlobal_cancel( String global_cancel ) {
        this.global_cancel = global_cancel;
    }
}
