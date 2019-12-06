package ws.epod.ObjectClass.SQLiteModel;

public class PickingUpEexpand_Model {
    public String box_no= "";
    public String waybil_no = "";
    public String is_scaned = "";
    public String row_number = "";
    public String consignment = "";
    public String delivery_no = "";
    public String plan_seq = "";

    public PickingUpEexpand_Model( String box_no, String waybil_no, String is_scaned, String row_number, String consignment, String delivery_no, String plan_seq ) {
        this.box_no = box_no;
        this.waybil_no = waybil_no;
        this.is_scaned = is_scaned;
        this.row_number = row_number;
        this.consignment = consignment;
        this.delivery_no = delivery_no;
        this.plan_seq = plan_seq;
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
