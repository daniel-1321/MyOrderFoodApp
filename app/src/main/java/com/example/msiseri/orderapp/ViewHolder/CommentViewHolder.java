package com.example.msiseri.orderapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.msiseri.orderapp.R;

/**
 * Created by MSI SERI on 22-Feb-18.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {
    public TextView txtUserPhone, txtComment;
    public RatingBar ratingBar;
    public CommentViewHolder(View itemView) {
        super(itemView);
        txtUserPhone = itemView.findViewById(R.id.txtUserPhone);
        txtComment = itemView.findViewById(R.id.txtComment);
        ratingBar = itemView.findViewById(R.id.ratingBar);
    }

}
