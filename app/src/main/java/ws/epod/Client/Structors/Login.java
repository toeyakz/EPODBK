package ws.epod.Client.Structors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {


    @SerializedName("status")
    @Expose
    public String status = "";

    @SerializedName("username")
    @Expose
    public String username = "";

    @SerializedName("pass")
    @Expose
    public String pass = "";

    @SerializedName("serial")
    @Expose
    public String serial = "";

    @SerializedName("driver_id")
    @Expose
    public Integer driverId = 0;

    @SerializedName("driver_fname")
    @Expose
    public String driverFname = "";

    @SerializedName("driver_lname")
    @Expose
    public String driverLname = "";

    @SerializedName("vehicle_id")
    @Expose
    public Integer vehicleId = 0;

    @SerializedName("vehicle_name")
    @Expose
    public String vehicleName = "";

    @SerializedName("status_login")
    @Expose
    public Integer statusLogin = 0;

    @SerializedName("type")
    @Expose
    public String type = "";

    @SerializedName("message")
    @Expose
    public String message = "";

}

