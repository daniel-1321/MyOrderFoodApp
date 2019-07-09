package com.example.msiseri.orderapp.Interface;

import android.support.v7.widget.RecyclerView;

/**
 * Created by MSI SERI on 04-Mar-18.
 */

public interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
