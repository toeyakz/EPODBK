package ws.epod.ObjectClass;

import android.os.Environment;

/**
 * Created by Naris on 16/12/58.
 */
public class Var {
    //public static String dbname = "EPOD_DB.db";
     public static String dbname = Environment.getExternalStorageDirectory().getPath() + "/EPOD/EPOD_DB.db";
    public static String appfolder = Environment.getExternalStorageDirectory() + "/ILS";
    public static String folderimg = Environment.getExternalStorageDirectory() + "/ILS/img";
    public static String googleMapapi = "AIzaSyBpT35QdMbx2NuZY3l1R2XiznmmcICdBz8";

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
     public static String host = "http://www.wisasoft.com:8997";
    //public static String host = "https://tms.misumi.co.th";
    public static String WEBSERVICE_PICTURE = "http://wisasoft.com:8997/ILS/ILS_WSMOBILE/uploadpic.php";

}
