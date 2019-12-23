package ws.epod.ObjectClass.SQLiteModel;

import java.io.Serializable;

public class SignObjectClass implements Serializable {
    String consignment_no;
    String order_no;
    String invoice_no;

    public SignObjectClass(String consignment_no, String order_no, String invoice_no) {
        this.consignment_no = consignment_no;
        this.order_no = order_no;
        this.invoice_no = invoice_no;
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
}
