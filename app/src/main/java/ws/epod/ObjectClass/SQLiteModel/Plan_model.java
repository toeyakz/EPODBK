package ws.epod.ObjectClass.SQLiteModel;


public class Plan_model {
    public String delivery_date = "";
    public int plan_seq = 0;
    public String delivery_no = "";
    public String pick = "";
    public String deli = "";
    public String finish = "";




    public Plan_model( String delivery_date, String delivery_no, int plan_seq, String pick, String deli, String finish) {
        this.delivery_date = delivery_date;
        this.delivery_no = delivery_no;
        this.plan_seq = plan_seq;
        this.pick = pick;
        this.deli = deli;
        this.finish = finish;

    }


    public String getFinish() {
        return finish;
    }

    public void setFinish( String finish ) {
        this.finish = finish;
    }

    public String getPick() {
        return pick;
    }

    public void setPick( String pick ) {
        this.pick = pick;
    }

    public String getDeli() {
        return deli;
    }

    public void setDeli( String deli ) {
        this.deli = deli;
    }

    public String getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date( String delivery_date ) {
        this.delivery_date = delivery_date;
    }

    public String getDelivery_no() {
        return delivery_no;
    }

    public void setDelivery_no( String delivery_no ) {
        this.delivery_no = delivery_no;
    }

    public int getPlan_seq() {
        return plan_seq;
    }

    public void setPlan_seq( int plan_seq ) {
        this.plan_seq = plan_seq;
    }
}
