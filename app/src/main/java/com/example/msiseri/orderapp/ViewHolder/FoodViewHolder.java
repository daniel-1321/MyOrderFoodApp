package com.example.msiseri.orderapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.msiseri.orderapp.Interface.ItemClickListener;
import com.example.msiseri.orderapp.R;

/**
 * Created by MSI SERI on 07-Dec-17.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView food_name,price;
    public ImageView food_image, fav_image, share_img, cart_img;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(View itemView) {
        super(itemView);
        food_name = itemView.findViewById(R.id.food_name);
        price = itemView.findViewById(R.id.price);
        food_image = itemView.findViewById(R.id.food_image);
        fav_image = itemView.findViewById(R.id.imgFav);
        share_img= itemView.findViewById(R.id.imgShare);
        cart_img= itemView.findViewById(R.id.imgCart);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
