package ws.epod.ObjectClass.SQLiteModel;

public class JobOperation_Model {

    public String pickdelname = "";
    public String consignment = "";
    public String boxs = "";
    public String unit = "";

    public JobOperation_Model( String pickdelname, String consignment, String boxs, String unit ) {
        this.pickdelname = pickdelname;
        this.consignment = consignment;
        this.boxs = boxs;
        this.unit = unit;
    }

    public String getPickdelname() {
        return pickdelname;
    }

    public void setPickdelname( String pickdelname ) {
        this.pickdelname = pickdelname;
    }

    public String getConsignment() {
        return consignment;
    }

    public void setConsignment( String consignment ) {
        this.consignment = consignment;
    }

    public String getBoxs() {
        return boxs;
    }

    public void setBoxs( String boxs ) {
        this.boxs = boxs;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit( String unit ) {
        this.unit = unit;
    }
}
