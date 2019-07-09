package com.example.msiseri.orderapp.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.msiseri.orderapp.Model.User;
import com.example.msiseri.orderapp.Remote.APIService;
import com.example.msiseri.orderapp.Remote.RetrofitClient;

/**
 * Created by MSI SERI on 05-Dec-17.
 */

public class Common {
    public static User currentUser; //save Username

    public static final String INTENT_FOOD_ID = "FoodId";

    public static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static String convertCodeToStatus(String status) {
        if(status.equals("0")){
            return "Placed";
        }else if(status.equals("1")){
            return "On my way";
        }else {
            return "Shipped";
        }
    }

    public static boolean isConnectedToInternet (Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
        if(connectivityManager != null){
            for(int i = 0; i < info.length;i++){
                if(info[i].getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }
}
