package ws.epod.Client.Structors;

import java.util.ArrayList;

public class UploadImageInvoice {
    ArrayList<Data2> data;

    public UploadImageInvoice(ArrayList<Data2> data) {
        this.data = data;
    }

    public static class Data2 {
        String id = "";
        String name = "";
        String img = "";

        public Data2( String id, String name, String img ) {
            this.id = id;
            this.name = name;
            this.img = img;
        }
    }
}
