package com.example.msiseri.orderapp.Service;

import com.example.msiseri.orderapp.Common.Common;
import com.example.msiseri.orderapp.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by MSI SERI on 04-Jan-18.
 */

public class MyFirebaseIdService  extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        if(Common.currentUser != null){
            updateTokenToFirebase(tokenRefreshed);
        }
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Tokens");
        Token token = new Token(tokenRefreshed,true);//False means token is sent from Client
        reference.child(Common.currentUser.getPhone()).setValue(token);
    }
}
