package ws.epod.scan.model.OfflineScan;

public class WaybillPoJo {
    private String waybill_no = "";
    private String date = "";
    private String lat = "";
    private String lon = "";

    public WaybillPoJo(String waybill_no, String date, String lat, String lon) {
        this.waybill_no = waybill_no;
        this.date = date;
        this.lat = lat;
        this.lon = lon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getWaybill_no() {
        return waybill_no;
    }

    public void setWaybill_no(String waybill_no) {
        this.waybill_no = waybill_no;
    }
}
