package ws.epod.ObjectClass.SQLiteModel;

public class WaybillModel {

    private String id = "";
    private String waybill_no = "";
    private String date_Scan = "";
    private double lat;
    private double lon;
    private String is_scanned = "";
    private String status_complete = "";
    private String into = "0";


    public WaybillModel(String id, String waybill_no, String date_Scan, double lat, double lon, String is_scanned, String status_complete) {
        this.id = id;
        this.waybill_no = waybill_no;
        this.date_Scan = date_Scan;
        this.lat = lat;
        this.lon = lon;
        this.is_scanned = is_scanned;
        this.status_complete = status_complete;
    }

    public String getInto() {
        return into;
    }

    public void setInto(String into) {
        this.into = into;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWaybill_no() {
        return waybill_no;
    }

    public void setWaybill_no(String waybill_no) {
        this.waybill_no = waybill_no;
    }

    public String getDate_Scan() {
        return date_Scan;
    }

    public void setDate_Scan(String date_Scan) {
        this.date_Scan = date_Scan;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getIs_scanned() {
        return is_scanned;
    }

    public void setIs_scanned(String is_scanned) {
        this.is_scanned = is_scanned;
    }

    public String getStatus_complete() {
        return status_complete;
    }

    public void setStatus_complete(String status_complete) {
        this.status_complete = status_complete;
    }
}
