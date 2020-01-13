package ws.epod.ObjectClass.SQLiteModel;

public class PickingUpEexpand_Model {
    private String box_no= "";
    private String waybil_no = "";
    private String is_scaned = "";
    private String row_number = "";
    private String consignment = "";
    private String delivery_no = "";
    private String plan_seq = "";
    private String into = "0";
    private String comment = "";
    private String picture1 = "";
    private String picture2 = "";
    private String picture3 = "";
    private String time_begin = "";
    private String actual_lat = "";
    private String actual_lon = "";
    private String is_save = "";
    private String status_upload = "";
    private int count = 0;

    public PickingUpEexpand_Model( String box_no, String waybil_no, String is_scaned, String row_number, String consignment, String delivery_no, String plan_seq, String comment
    ,String picture1, String picture2, String picture3, String time_begin, String is_save, String status_upload) {
        this.box_no = box_no;
        this.waybil_no = waybil_no;
        this.is_scaned = is_scaned;
        this.row_number = row_number;
        this.consignment = consignment;
        this.delivery_no = delivery_no;
        this.plan_seq = plan_seq;
        this.comment = comment;
        this.picture1 = picture1;
        this.picture2 = picture2;
        this.picture3 = picture3;
        this.time_begin = time_begin;
        this.is_save = is_save;
        this.status_upload = status_upload;
    }

    public String getStatus_upload() {
        return status_upload;
    }

    public void setStatus_upload(String status_upload) {
        this.status_upload = status_upload;
    }

    public String getIs_save() {
        return is_save;
    }

    public void setIs_save(String is_save) {
        this.is_save = is_save;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTime_begin() {
        return time_begin;
    }

    public void setTime_begin(String time_begin) {
        this.time_begin = time_begin;
    }

    public String getActual_lat() {
        return actual_lat;
    }

    public void setActual_lat(String actual_lat) {
        this.actual_lat = actual_lat;
    }

    public String getActual_lon() {
        return actual_lon;
    }

    public void setActual_lon(String actual_lon) {
        this.actual_lon = actual_lon;
    }

    public String getPicture1() {
        return picture1;
    }

    public void setPicture1(String picture1) {
        this.picture1 = picture1;
    }

    public String getPicture2() {
        return picture2;
    }

    public void setPicture2(String picture2) {
        this.picture2 = picture2;
    }

    public String getPicture3() {
        return picture3;
    }

    public void setPicture3(String picture3) {
        this.picture3 = picture3;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getInto() {
        return into;
    }

    public void setInto(String into) {
        this.into = into;
    }


    public String getDelivery_no() {
        return delivery_no;
    }

    public void setDelivery_no( String delivery_no ) {
        this.delivery_no = delivery_no;
    }

    public String getPlan_seq() {
        return plan_seq;
    }

    public void setPlan_seq( String plan_seq ) {
        this.plan_seq = plan_seq;
    }

    public String getConsignment() {
        return consignment;
    }

    public void setConsignment( String consignment ) {
        this.consignment = consignment;
    }

    public String getRow_number() {
        return row_number;
    }

    public void setRow_number( String row_number ) {
        this.row_number = row_number;
    }

    public String getIs_scaned() {
        return is_scaned;
    }

    public void setIs_scaned( String is_scaned ) {
        this.is_scaned = is_scaned;
    }

    public String getBox_no() {
        return box_no;
    }

    public void setBox_no( String box_no ) {
        this.box_no = box_no;
    }

    public String getWaybil_no() {
        return waybil_no;
    }

    public void setWaybil_no( String waybil_no ) {
        this.waybil_no = waybil_no;
    }
}
