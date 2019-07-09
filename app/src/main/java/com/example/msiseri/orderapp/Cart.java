package com.example.msiseri.orderapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msiseri.orderapp.Common.Common;
import com.example.msiseri.orderapp.Database.Database;
import com.example.msiseri.orderapp.Helper.RecyclerItemTouchHelper;
import com.example.msiseri.orderapp.Interface.RecyclerItemTouchHelperListener;
import com.example.msiseri.orderapp.Model.MyResponse;
import com.example.msiseri.orderapp.Model.Notification;
import com.example.msiseri.orderapp.Model.Order;
import com.example.msiseri.orderapp.Model.Request;
import com.example.msiseri.orderapp.Model.Sender;
import com.example.msiseri.orderapp.Model.Token;
import com.example.msiseri.orderapp.Remote.APIService;
import com.example.msiseri.orderapp.ViewHolder.CartAdapter;
import com.example.msiseri.orderapp.ViewHolder.CartViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotal;
    Button btnPlaceOrder;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    APIService mService;
    RelativeLayout rootLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rootLayout = findViewById(R.id.rootLayout);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init service
        mService = Common.getFCMService();

        //Swipe to delete
        ItemTouchHelper.SimpleCallback callback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);


        //Init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotal = findViewById(R.id.total);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.size() > 0) {
                    showAlertDialog();
                }else{
                    Toast.makeText(Cart.this, "Your cart is empty!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadListFood();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
        builder.setTitle("One more step");
        builder.setMessage("Enter your information");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment= inflater.inflate(R.layout.order_address_comment,null);

        builder.setView(order_address_comment);

        final EditText edtAddress = order_address_comment.findViewById(R.id.edtAddress);
        final EditText edtComment= order_address_comment.findViewById(R.id.edtComment);

        builder.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotal.getText().toString(),
                        "0",
                        edtComment.getText().toString(),
                        cart
                );

                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number)
                        .setValue(request);

                //Delete cart
                new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                sendNotificationOrder(order_number);
                Toast.makeText(Cart.this, "Order successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Cart.this, Home.class);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = reference.orderByChild("severToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Token serverToken = postSnapshot.getValue(Token.class);

                    Notification notification = new Notification("Android","You have new order" + order_number);

                    Sender content = new Sender(serverToken.getToken(),notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success==1){
                                            Toast.makeText(Cart.this, "Order successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else{
                                            Toast.makeText(Cart.this, "Order fail", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR",t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {
        cart = new Database(this).getCarts(Common.currentUser.getPhone());
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total = 0;
        for(Order order:cart){
            total+=((Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity())));
        }
        Locale locale = new Locale("en","US");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        txtTotal.setText(format.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE)){
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int order) {
        cart.remove(order); //Remove by position

        new Database(this).cleanCart(Common.currentUser.getPhone()); // Delete cart from SQLite

        for(Order item:cart){
            new Database(this).addToCart(item);
        }
        loadListFood();// Refresh
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder){
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();
            final Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();
            new Database(getBaseContext()).deleteFromCart(deleteItem.getProductId(),Common.currentUser.getPhone());

            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
            for(Order item:orders){
                total+=((Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity())));
            }
            Locale locale = new Locale("en","US");
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            txtTotal.setText(format.format(total));

            Snackbar snackbar = Snackbar.make(rootLayout,name+ "removed from cart!",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);

                    int total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                    for(Order item:orders){
                        total+=((Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity())));
                    }
                    Locale locale = new Locale("en","US");
                    NumberFormat format = NumberFormat.getCurrencyInstance(locale);
                    txtTotal.setText(format.format(total));
                }
            });

            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }
}
