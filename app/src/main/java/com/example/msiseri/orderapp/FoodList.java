package com.example.msiseri.orderapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.msiseri.orderapp.Common.Common;
import com.example.msiseri.orderapp.Database.Database;
import com.example.msiseri.orderapp.Interface.ItemClickListener;
import com.example.msiseri.orderapp.Model.Category;
import com.example.msiseri.orderapp.Model.Food;
import com.example.msiseri.orderapp.Model.Order;
import com.example.msiseri.orderapp.ViewHolder.FoodViewHolder;
import com.example.msiseri.orderapp.ViewHolder.MenuViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference foodList;
    RecyclerView recycler_food;
    RecyclerView.LayoutManager layoutManager;
    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar searchBar;
    Database db;


    //Create Target
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if (ShareDialog.canShow(SharePhotoContent.class)) {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        //Init Facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        db = new Database(this);

        recycler_food = findViewById(R.id.recycler_food);
        recycler_food.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_food.setLayoutManager(layoutManager);

        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if (!categoryId.isEmpty() && categoryId != null) {
            if (Common.isConnectedToInternet(getBaseContext())) {
                loadListFood(categoryId);
            } else {
                Toast.makeText(FoodList.this, "Check your Internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        searchBar = findViewById(R.id.searchBar);
        searchBar.setHint("Search food...");
        loadSuggest();


        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<String>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toString().toLowerCase())) {
                        suggest.add(search);
                    }
                    searchBar.setLastSuggestions(suggest);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //Search bar close => normal menu
                if (!enabled) {
                    recycler_food.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    private void startSearch(CharSequence text) {
        Query searchByName = foodList.orderByChild("name").equalTo(text.toString());

        FirebaseRecyclerOptions<Food> foodOption = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName, Food.class)
                .build();
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOption) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull final Food model) {
                viewHolder.food_name.setText(model.getName());
                viewHolder.price.setText("$" + model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this, "" + model.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(itemView);
            }
        };

        recycler_food.setAdapter(searchAdapter);
        searchAdapter.startListening();

    }

    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }
                        searchBar.setLastSuggestions(suggestList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {


                    }
                });
    }

    private void loadListFood(String categoryId) {
        Query searchById = foodList.orderByChild("menuId").equalTo(categoryId);

        FirebaseRecyclerOptions<Food> idOption = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchById, Food.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(idOption) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {
                viewHolder.food_name.setText(model.getName());
                viewHolder.price.setText("$" + model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                //Add favorite foods
                if (db.foodFavorites(adapter.getRef(position).getKey())) {
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                //Quick cart
                    viewHolder.cart_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isExists = new Database(getBaseContext()).checkFoodExist(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            if (!isExists) {
                                new Database(getBaseContext()).addToCart(new Order(
                                        Common.currentUser.getPhone(),
                                        adapter.getRef(position).getKey(),
                                        model.getName(),
                                        "1",
                                        model.getPrice(),
                                        model.getDiscount(),
                                        model.getImage()
                                ));

                                Toast.makeText(FoodList.this, "Added to cart successfully", Toast.LENGTH_SHORT).show();
                            }else{

                                new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(),adapter.getRef(position).getKey());
                                Toast.makeText(FoodList.this, "Your food has been increased 01 ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                viewHolder.share_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(FoodList.this, "kkkk", Toast.LENGTH_SHORT).show();
                        Picasso.with(getApplicationContext())
                                .load(model.getImage())
                                .into(target);
                    }
                });

                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!db.foodFavorites(adapter.getRef(position).getKey())) {
                            db.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, "Food has been added to Favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            db.deleteFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, "Food has been removed from Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this, "" + model.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(itemView);
            }
        };

        recycler_food.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
