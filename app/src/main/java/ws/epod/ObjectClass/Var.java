package ws.epod.ObjectClass;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.File;
import java.security.AccessController;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Toey on 16/10/62.
 */
public class Var {

    //public static String dbname = "EPOD_DB.db";
    public static String dbname = Environment.getExternalStorageDirectory().getPath() + "/EPOD/EPOD_DB.db";
    // public static String dbname = "/storage/emulated/0/Android/data/ws.epod/files/DATABASE/EPOD_DB.db";

    //--------------
    public static UserObject UserLogin = new UserObject();
    public static PlanObject PlanObject = new PlanObject();
    //    public static PlanWorkObject PlanWorkSelected = new PlanWorkObject();
//    public static WorkObject WorkSelected = new WorkObject();
//    public static ArrayList<WorkLineObject> WorkLineSelected = new ArrayList<>();
    public static int MAXPHOTO = 99;
    public static int synced = 0;

    //-------------
    /*
    public static String WEBSERVICE =         "http://ilspda.tws.solutions/ILS_WSMOBILE/index.php?";
    public static String WEBSERVICE_PICTURE = "http://ilspda.tws.solutions/ILS_WSMOBILE/uploadpic.php";
    */

    public static String WEBSERVICE = "http://wisasoft.com:8997/EPOD_MSM/EPOD_MSMMOBILE/index_1.php?";
    public static String WEBSERVICE2 = "http://www.wisasoft.com:8997/TMS_MSM/resources/function/php/service.php?";
    //public static String host = "http://www.wisasoft.com:8997";
   // public static String host = "https://tms.misumi.co.th";
    public static String host = "http://tmsthai.com/tms_msm/";
    public static String WEBSERVICE_PICTURE = "http://wisasoft.com:8997/ILS/ILS_WSMOBILE/uploadpic.php";


    public static String getDateUploadDB() {

        String temp = "";
        String pattern = "yyyyMMdd_HHmmss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));

        if (String.valueOf(sdf).length() > 3) {
            temp = sdf.format(Calendar.getInstance().getTime());
        } else {
            Calendar c = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
            temp = df.format(c.getTime());
        }


        return temp;
    }

}
