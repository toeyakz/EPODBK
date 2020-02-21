package ws.epod.scan.model.OfflineScan;

public class WaybillPoJo {
    private String waybill_no = "";

    public WaybillPoJo(String waybill_no) {
        this.waybill_no = waybill_no;
    }

    public String getWaybill_no() {
        return waybill_no;
    }

    public void setWaybill_no(String waybill_no) {
        this.waybill_no = waybill_no;
    }
}
