package ws.epod.ObjectClass.SQLiteModel;

public class Sign_Model {

    private String consignment_no = "";
    private String deli_note_no = "";
    private String status = "";
    private String signature = "";
    private String order_no = "";
    private String delivery_no = "";
    private String comment = "";
    private String into = "0";
    private String status_delete = "";

    public Sign_Model(String consignment_no, String deli_note_no, String status, String signature, String order_no, String delivery_no, String comment) {
        this.consignment_no = consignment_no;
        this.deli_note_no = deli_note_no;
        this.status = status;
        this.signature = signature;
        this.order_no = order_no;
        this.delivery_no = delivery_no;
        this.comment = comment;;
    }

    public String getStatus_delete() {
        return status_delete;
    }

    public void setStatus_delete(String status_delete) {
        this.status_delete = status_delete;
    }

    public String getInto() {
        return into;
    }

    public void setInto(String into) {
        this.into = into;
    }

    public String getConsignment_no() {
        return consignment_no;
    }

    public void setConsignment_no(String consignment_no) {
        this.consignment_no = consignment_no;
    }

    public String getDeli_note_no() {
        return deli_note_no;
    }

    public void setDeli_note_no(String deli_note_no) {
        this.deli_note_no = deli_note_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getDelivery_no() {
        return delivery_no;
    }

    public void setDelivery_no(String delivery_no) {
        this.delivery_no = delivery_no;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        Sign_Model itemCompare = (Sign_Model) obj;
        if(itemCompare.getDeli_note_no().equals(this.getDeli_note_no()))
            return true;

        return false;
    }
}
