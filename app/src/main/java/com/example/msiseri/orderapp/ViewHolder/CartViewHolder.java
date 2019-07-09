package com.example.msiseri.orderapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.msiseri.orderapp.Common.Common;
import com.example.msiseri.orderapp.Interface.ItemClickListener;
import com.example.msiseri.orderapp.R;

/**
 * Created by MSI SERI on 04-Mar-18.
 */

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        ,View.OnCreateContextMenuListener{
    public TextView txtCartName, txtCartPrice;
    public ElegantNumberButton quantity;
    public ImageView cart_image;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    private ItemClickListener itemClickListener;

    public void setTxtCartName(TextView txtCartName) {
        this.txtCartName = txtCartName;
    }

    public CartViewHolder(View itemView) {
        super(itemView);
        txtCartName =  itemView.findViewById(R.id.cart_item_name);
        txtCartPrice = itemView.findViewById(R.id.cart_item_price);
        quantity = itemView.findViewById(R.id.quantity);
        cart_image = itemView.findViewById(R.id.cart_image);
        view_background = itemView.findViewById(R.id.view_background);
        view_foreground = itemView.findViewById(R.id.view_foreground);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select action");
        contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}
