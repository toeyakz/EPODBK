package ws.epod.ObjectClass.SQLiteModel;

public class Sign_i_Model {

    public String consignment_no = "";
    public String deli_note_no = "";
    public String status = "";
    public String into = "";
    public String delivery_no = "";
    public String order_no = "";

    public Sign_i_Model( String consignment_no, String deli_note_no, String status, String delivery_no, String order_no) {
        this.consignment_no = consignment_no;
        this.deli_note_no = deli_note_no;
        this.status = status;
        this.delivery_no = delivery_no;
        this.order_no = order_no;
    }

    public String getDelivery_no() {
        return delivery_no;
    }

    public void setDelivery_no(String delivery_no) {
        this.delivery_no = delivery_no;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getInto() {
        return into;
    }

    public void setInto( String into ) {
        this.into = into;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus( String status ) {
        this.status = status;
    }

    public String getConsignment_no() {
        return consignment_no;
    }

    public void setConsignment_no( String consignment_no ) {
        this.consignment_no = consignment_no;
    }

    public String getDeli_note_no() {
        return deli_note_no;
    }

    public void setDeli_note_no( String deli_note_no ) {
        this.deli_note_no = deli_note_no;
    }
}