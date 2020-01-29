package ws.epod.ObjectClass.SQLiteModel;

public class Deliver_Model {
    private String consignment_no = "";
    private String box_total = "";
    private String box_checked = "";
    private String global_total = "";
    private String station_address = "";
    private String paytype = "";
    private String global_cancel = "";
    private String price = "";
    private int count = 0;
    private int total_b;


    public Deliver_Model( String consignment_no, String box_total,String box_checked, String global_total, String station_address, String paytype, String global_cancel, String price, int total_b ) {
        this.consignment_no = consignment_no;
        this.box_total = box_total;
        this.box_checked = box_checked;
        this.global_total = global_total;
        this.station_address = station_address;
        this.paytype = paytype;
        this.global_cancel = global_cancel;
        this.price = price;
        this.total_b = total_b;
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
