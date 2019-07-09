package com.example.msiseri.orderapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.msiseri.orderapp.Common.Common;
import com.example.msiseri.orderapp.Database.Database;
import com.example.msiseri.orderapp.Model.Food;
import com.example.msiseri.orderapp.Model.Order;
import com.example.msiseri.orderapp.Model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {
    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CounterFab btnCart;
    FloatingActionButton btnRating;
    RatingBar ratingBar;
    ElegantNumberButton numberButton;
    String foodId = "";
    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingFood;
    Food currentFood;
    Button btnShowComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        ratingFood = database.getReference("Rating");

        numberButton = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btnCart);
        btnRating = findViewById(R.id.btnRating);
        ratingBar = findViewById(R.id.ratingBar);
        btnShowComment = findViewById(R.id.btnShowComment);

        btnShowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showComment = new Intent(FoodDetail.this, ShowComment.class);
                showComment.putExtra(Common.INTENT_FOOD_ID,foodId);
                startActivity(showComment);
            }
        });

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        Common.currentUser.getPhone(),
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount(),
                        currentFood.getImage()
                ));

                Toast.makeText(FoodDetail.this, "Added to cart successfully", Toast.LENGTH_SHORT).show();
            }
        });

        btnCart.setCount(new Database(this).getCountCarts(Common.currentUser.getPhone()));
        food_description = findViewById(R.id.food_description);
        food_name = findViewById(R.id.food_name);
        food_price = findViewById(R.id.food_price);
        food_image = findViewById(R.id.food_image);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        //Get Food Detail
        if(getIntent() != null){
            foodId = getIntent().getStringExtra("FoodId");
        }
        if(!foodId.isEmpty()){
            if(Common.isConnectedToInternet(getBaseContext())) {
                getDetailFood(foodId);
                getRatingFood(foodId);
            }else{
                Toast.makeText(FoodDetail.this, "Check your Internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void getRatingFood(String foodId) {
        Query foodRating = ratingFood.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRatingValue());
                    count++;
                }
                if(count > 0){
                    float average = sum/count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very bad","Bad","Ok","Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Write your comment here")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        final Rating rating = new Rating(Common.currentUser.getPhone(),foodId,String.valueOf(value),comments);

        ratingFood.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodDetail.this, "Thank you for your rating", Toast.LENGTH_SHORT).show();
                    }
                });
//        ratingFood.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.child(Common.currentUser.getPhone()).exists()){
//                    //Remove rating
//                    ratingFood.child(Common.currentUser.getPhone()).removeValue();
//
//                    //Update new rating
//                    ratingFood.child(Common.currentUser.getPhone()).setValue(rating);
//                }
//                else{
//                    ratingFood.child(Common.currentUser.getPhone()).setValue(rating);
//                }
//                Toast.makeText(FoodDetail.this, "Thank you for your rating", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
