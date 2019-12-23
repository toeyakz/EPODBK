package ws.epod.ObjectClass.SQLiteModel;

public class CommentPickup_Model {
    private String id = "";
    private String consignment_no = "";
    private String order_no = "";
    private String invoice_no = "";
    private String comment = "";
    private String comment_deliver = "";
    private String delivery_no = "";

    public CommentPickup_Model(String id, String consignment_no, String order_no, String invoice_no, String comment, String comment_deliver, String delivery_no) {
        this.id = id;
        this.consignment_no = consignment_no;
        this.order_no = order_no;
        this.invoice_no = invoice_no;
        this.comment = comment;
        this.comment_deliver = comment_deliver;
        this.delivery_no = delivery_no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsignment_no() {
        return consignment_no;
    }

    public void setConsignment_no(String consignment_no) {
        this.consignment_no = consignment_no;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_deliver() {
        return comment_deliver;
    }

    public void setComment_deliver(String comment_deliver) {
        this.comment_deliver = comment_deliver;
    }

    public String getDelivery_no() {
        return delivery_no;
    }

    public void setDelivery_no(String delivery_no) {
        this.delivery_no = delivery_no;
    }
}
