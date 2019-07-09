package com.example.msiseri.orderapp.Remote;



import com.example.msiseri.orderapp.Model.MyResponse;
import com.example.msiseri.orderapp.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


/**
 * Created by MSI SERI on 04-Jan-18.
 */

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAbFyqXTY:APA91bH3C6iP3ZfOfVrMp9DMxt4e5c4qdDLqbJKevd9GuoRiaG6oWgt1Nz_sJ7vvg54HQQoF1kNgLS92BmRsQVVt1DXzsUyJHVRXi4rjZQxtDkHdry0huYFnr159w6tdLbOClzLd32gv"
            }

    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
