package ws.epod.Client;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ws.epod.Client.Structors.Login;
import ws.epod.Client.Structors.UploadImage;
import ws.epod.Client.Structors.UploadImageInvoice;

public interface APIInterface {

//    @POST("/TMS_MSM/services/service.php?func=login")
//    Call<List<Login>> login(@Query("user") String username, @Query("pass") String password, @Query("serial") String serial );
//
//    @POST("/TMS_MSM/services/service.php?func=login")
//    Call<ResponseBody> login2nd(@Query("user") String username, @Query("pass") String password, @Query("serial") String serial );
//
//    @POST("/TMS_MSM/services/service.php?func=setPlan")
//    Call<ResponseBody> uploadwork( @Query("driver_id") String driver_id,
//                                   @Body RequestBody body );
//    @POST("/TMS_MSM/services/service.php?func=setInvoice")
//    Call<ResponseBody> uploadInvoice(@Body RequestBody body );
//
//    @POST("/TMS_MSM/services/service.php?func=setComment")
//    Call<ResponseBody> uploadComment(@Body RequestBody body );
//
//    @POST("/TMS_MSM/services/service.php?func=setImgInvoice")
//    Call<ResponseBody> uploadPicture( @Body UploadImage root );
//
//    @POST("/TMS_MSM/services/service.php?func=setImg")
//    Call<ResponseBody> uploadPictureInvoice( @Body UploadImageInvoice root );
//
//    @POST("/TMS_MSM/services/service.php?func=getPlan")
//    Call<ResponseBody> downloadWork( @Query("vehicle_id") String vehicle_id,
//                                     @Query("driver_id") String driver_id,
//                                     @Query("serial") String serial,
//                                     @Query("phone_date") String phone_date,
//                                     @Query("date") String date );
//
//    @POST("/TMS_MSM/services/service.php?func=getConsignment2")
//    Call<ResponseBody> downloadConsignment( @Query("vehicle_id") String vehicle_id,
//                                            @Query("date") String date );
//
//    @POST("/TMS_MSM/services/service.php?func=logout")
//    Call<ResponseBody> logout( @Query("user") String user,
//                               @Query("serial") String serial );
//
//    @POST("/TMS_MSM/services/service.php?func=getReason")
//    Call<ResponseBody> reason();



    //********************************************************************************************
    //ลิง๕์จริง

    @POST("/services/service.php?func=login")
    Call<List<Login>> login(@Query("user") String username, @Query("pass") String password, @Query("serial") String serial );

    @POST("/services/service.php?func=login")
    Call<ResponseBody> login2nd(@Query("user") String username, @Query("pass") String password, @Query("serial") String serial );

    @POST("/services/service.php?func=setPlan")
    Call<ResponseBody> uploadwork( @Query("driver_id") String driver_id,
                                   @Body RequestBody body );
    @POST("/services/service.php?func=setInvoice")
    Call<ResponseBody> uploadInvoice(@Body RequestBody body );

    @POST("/services/service.php?func=setComment")
    Call<ResponseBody> uploadComment(@Body RequestBody body );

    @POST("/services/service.php?func=setImgInvoice")
    Call<ResponseBody> uploadPicture(@Body UploadImage root );

    @POST("/services/service.php?func=setImg")
    Call<ResponseBody> uploadPictureInvoice( @Body UploadImageInvoice root );

    @POST("/services/service.php?func=getPlan")
    Call<ResponseBody> downloadWork( @Query("vehicle_id") String vehicle_id,
                                     @Query("driver_id") String driver_id,
                                     @Query("serial") String serial,
                                     @Query("phone_date") String phone_date,
                                     @Query("date") String date );

    @POST("/services/service.php?func=getConsignment2")
    Call<ResponseBody> downloadConsignment( @Query("vehicle_id") String vehicle_id,
                                            @Query("date") String date );

    @POST("/services/service.php?func=logout")
    Call<ResponseBody> logout( @Query("user") String user,
                               @Query("serial") String serial );

    @POST("/services/service.php?func=getReason")
    Call<ResponseBody> reason();


}
