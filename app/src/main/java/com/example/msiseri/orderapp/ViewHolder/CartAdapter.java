package com.example.msiseri.orderapp.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.msiseri.orderapp.Cart;
import com.example.msiseri.orderapp.Common.Common;
import com.example.msiseri.orderapp.Database.Database;
import com.example.msiseri.orderapp.Interface.ItemClickListener;
import com.example.msiseri.orderapp.Model.Order;
import com.example.msiseri.orderapp.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by MSI SERI on 09-Dec-17.
 */


public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    private List<Order> listData = new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View view = inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, final int position) {
//        TextDrawable drawable = TextDrawable.builder().buildRound(""+listData.get(position).getQuantity(), Color.BLUE);
//        holder.imgCartCount.setImageDrawable(drawable);

        Picasso.with(cart.getBaseContext())
                .load(listData.get(position).getImage())
                .resize(70,70)
                .centerCrop()
                .into(holder.cart_image);
        
        holder.quantity.setNumber(listData.get(position).getQuantity());
        holder.quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);

                //Update total
                int total = 0;
                List<Order> orders = new Database(cart).getCarts(Common.currentUser.getPhone());
                for(Order item:orders){
                    total+=((Integer.parseInt(order.getPrice())) * (Integer.parseInt(item.getQuantity())));
                }
                Locale locale = new Locale("en","US");
                NumberFormat format = NumberFormat.getCurrencyInstance(locale);
                cart.txtTotal.setText(format.format(total));
            }
        });
        Locale locale = new Locale("en","US");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        int price = ((Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity())));
        holder.txtCartPrice.setText(format.format(price));
        holder.txtCartName.setText(listData.get(position).getProductName());
    }

    public Order getItem(int position){
        return listData.get(position);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeItem(int position){
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item, int position){
        listData.add(position,item);
        notifyItemInserted(position);
    }
}
