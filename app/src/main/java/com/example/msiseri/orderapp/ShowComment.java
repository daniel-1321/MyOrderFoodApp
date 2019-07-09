package com.example.msiseri.orderapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.msiseri.orderapp.Common.Common;
import com.example.msiseri.orderapp.Model.Rating;
import com.example.msiseri.orderapp.ViewHolder.CommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowComment extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference reference;

    String foodId = "";

    FirebaseRecyclerAdapter<Rating, CommentViewHolder> adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
        .setDefaultFontPath("fonts/restaurant_font.otf")
        .setFontAttrId(R.attr.fontPath)
        .build());
        setContentView(R.layout.activity_show_comment);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Rating");

        recyclerView = findViewById(R.id.recyclerComment);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if(getIntent() != null){
            foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);
        }
        if(!foodId.isEmpty() && foodId != null){
            Query query = reference.orderByChild("foodId").equalTo(foodId);

            FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                    .setQuery(query,Rating.class)
                    .build();

            adapter = new FirebaseRecyclerAdapter<Rating, CommentViewHolder>(options) {
                @Override
                public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.show_comment_layout,parent,false);
                    return new CommentViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Rating model) {
                    holder.ratingBar.setRating(Float.parseFloat(model.getRatingValue()));
                    holder.txtComment.setText(model.getComment());
                    holder.txtUserPhone.setText(model.getUserPhone());
                }
            };

            loadComment(foodId);
        }

    }

    private void loadComment(String foodId) {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
