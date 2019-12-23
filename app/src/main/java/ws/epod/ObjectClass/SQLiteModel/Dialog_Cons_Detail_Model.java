package ws.epod.ObjectClass.SQLiteModel;

public class Dialog_Cons_Detail_Model {

    public String global_no = "";
    public String deli_note_amount_price = "";
    public String detail_remarks = "";

    public Dialog_Cons_Detail_Model( String global_no, String deli_note_amount_price, String detail_remarks ) {
        this.global_no = global_no;
        this.deli_note_amount_price = deli_note_amount_price;
        this.detail_remarks = detail_remarks;
    }

    public String getGlobal_no() {
        return global_no;
    }

    public void setGlobal_no( String global_no ) {
        this.global_no = global_no;
    }

    public String getDeli_note_amount_price() {
        return deli_note_amount_price;
    }

    public void setDeli_note_amount_price( String deli_note_amount_price ) {
        this.deli_note_amount_price = deli_note_amount_price;
    }

    public String getDetail_remarks() {
        return detail_remarks;
    }

    public void setDetail_remarks( String detail_remarks ) {
        this.detail_remarks = detail_remarks;
    }
}
