package ws.epod.ObjectClass.SQLiteModel;

public class JobList_Model {
    public String station_name = "";
    public String station_address = "";
    public int plan_seq = 0;
    public String plan_in = "";
    public double station_lat;
    public double station_lon;
    public String pick = "";
    public String pickUp = "";
    public String deli = "";
    public String delivery = "";
    public String delivery_no = "";


    public JobList_Model( String station_name, String station_address, int plan_seq, String plan_in, double station_lat, double station_lon,
                          String pick, String pickUp, String deli, String delivery, String delivery_no ) {
        this.station_name = station_name;
        this.station_address = station_address;
        this.plan_seq = plan_seq;
        this.plan_in = plan_in;
        this.station_lat = station_lat;
        this.station_lon = station_lon;
        this.pick = pick;
        this.pickUp = pickUp;
        this.deli = deli;
        this.delivery = delivery;
        this.delivery_no = delivery_no;
    }

    public String getDelivery_no() {
        return delivery_no;
    }

    public void setDelivery_no( String delivery_no ) {
        this.delivery_no = delivery_no;
    }

    public String getPick() {
        return pick;
    }

    public void setPick( String pick ) {
        this.pick = pick;
    }

    public String getPickUp() {
        return pickUp;
    }

    public void setPickUp( String pickUp ) {
        this.pickUp = pickUp;
    }

    public String getDeli() {
        return deli;
    }

    public void setDeli( String deli ) {
        this.deli = deli;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery( String delivery ) {
        this.delivery = delivery;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name( String station_name ) {
        this.station_name = station_name;
    }

    public double getStation_lat() {
        return station_lat;
    }

    public void setStation_lat( double station_lat ) {
        this.station_lat = station_lat;
    }

    public double getStation_lon() {
        return station_lon;
    }

    public void setStation_lon( double station_lon ) {
        this.station_lon = station_lon;
    }

    public String getStation_address() {
        return station_address;
    }

    public void setStation_address( String station_address ) {
        this.station_address = station_address;
    }

    public int getPlan_seq() {
        return plan_seq;
    }

    public void setPlan_seq( int plan_seq ) {
        this.plan_seq = plan_seq;
    }

    public String getPlan_in() {
        return plan_in;
    }

    public void setPlan_in( String plan_in ) {
        this.plan_in = plan_in;
    }
}
