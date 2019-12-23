package ws.epod.Client.Structors;

import java.util.ArrayList;

public class UploadImage {

    ArrayList<Data> data;

    public UploadImage( ArrayList<Data> data ) {
        this.data = data;
    }

    public static class Data {
        String id = "";
        String name = "";
        String seq = "";
        String img = "";

        public Data( String id, String name, String seq, String img ) {
            this.id = id;
            this.name = name;
            this.seq = seq;
            this.img = img;
        }
    }

}


