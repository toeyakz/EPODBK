package ws.epod.scan.model.pickup;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Invoice {
    private String waybill_no = "";
    private String is_scanned = "";
    private String date = "";
    private double lat;
    private double lon;

    public Invoice(String waybill_no) {
        this.waybill_no = waybill_no;
        this.date = getdate();
    }



    private String getdate() {

        String temp = "";
        String pattern = "yyyy-MM-dd kk:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
        temp = sdf.format(Calendar.getInstance().getTime());

        return temp;
    }


    public String getIs_scanned() {
        return is_scanned;
    }

    public void setIs_scanned(String is_scanned) {
        this.is_scanned = is_scanned;
    }

    public String getWaybill_no() {
        return waybill_no;
    }

    public void setWaybill_no(String waybill_no) {
        this.waybill_no = waybill_no;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
