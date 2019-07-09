package com.example.msiseri.orderapp.ViewHolder;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.msiseri.orderapp.Interface.ItemClickListener;
import com.example.msiseri.orderapp.R;

/**
 * Created by MSI SERI on 05-Dec-17.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtMenuName;
    public ImageView image;
    private ItemClickListener itemClickListener;
    public MenuViewHolder(View itemView) {
        super(itemView);
        txtMenuName = itemView.findViewById(R.id.menu_name);
        image = itemView.findViewById(R.id.menu_image);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
