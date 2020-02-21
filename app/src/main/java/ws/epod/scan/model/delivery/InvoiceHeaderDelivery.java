package ws.epod.scan.model.delivery;

public class InvoiceHeaderDelivery {
    private String consignment_no = "";
    private String waybill_no = "";
    private String is_scaned = "";

    public InvoiceHeaderDelivery(String consignment_no, String waybill_no, String is_scaned) {
        this.consignment_no = consignment_no;
        this.waybill_no = waybill_no;
        this.is_scaned = is_scaned;
    }

    public String getConsignment_no() {
        return consignment_no;
    }

    public void setConsignment_no(String consignment_no) {
        this.consignment_no = consignment_no;
    }

    public String getWaybill_no() {
        return waybill_no;
    }

    public void setWaybill_no(String waybill_no) {
        this.waybill_no = waybill_no;
    }

    public String getIs_scaned() {
        return is_scaned;
    }

    public void setIs_scaned(String is_scaned) {
        this.is_scaned = is_scaned;
    }
}

