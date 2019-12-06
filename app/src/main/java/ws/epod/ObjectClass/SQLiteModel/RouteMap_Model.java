package ws.epod.ObjectClass.SQLiteModel;

public class RouteMap_Model {
    public String station_name = "";
    public String station_lat = "";
    public String station_lon = "";
    public String plan_seq = "";

    public RouteMap_Model( String station_name, String station_lat, String station_lon, String plan_seq) {
        this.station_name = station_name;
        this.station_lat = station_lat;
        this.station_lon = station_lon;
        this.plan_seq = plan_seq;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name( String station_name ) {
        this.station_name = station_name;
    }

    public String getPlan_seq() {
        return plan_seq;
    }

    public void setPlan_seq( String plan_seq ) {
        this.plan_seq = plan_seq;
    }

    public String getStation_lat() {
        return station_lat;
    }

    public void setStation_lat( String station_lat ) {
        this.station_lat = station_lat;
    }

    public String getStation_lon() {
        return station_lon;
    }

    public void setStation_lon( String station_lon ) {
        this.station_lon = station_lon;
    }
}
