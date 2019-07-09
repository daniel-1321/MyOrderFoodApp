package com.example.msiseri.orderapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.msiseri.orderapp.Common.Common;
import com.example.msiseri.orderapp.Database.Database;
import com.example.msiseri.orderapp.Interface.ItemClickListener;
import com.example.msiseri.orderapp.Model.Banner;
import com.example.msiseri.orderapp.Model.Category;
import com.example.msiseri.orderapp.Model.Token;
import com.example.msiseri.orderapp.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import static android.text.TextUtils.isEmpty;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    TextView txtFullName;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    CounterFab fab;
    HashMap<String,String> image_list;
    SliderLayout sliderLayout;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("MENU");
        setSupportActionBar(toolbar);



        //Init paper
        Paper.init(this);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this,Cart.class);
                startActivity(cartIntent);
            }
        });

        fab.setCount(new Database(this).getCountCarts(Common.currentUser.getPhone()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set name for user
        View headerView = navigationView.getHeaderView(0);
        txtFullName = headerView.findViewById(R.id.txtFullName);
        txtFullName.setText("WELCOME MR/MRS. " + Common.currentUser.getName());

        //Load menu
        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        if(Common.isConnectedToInternet(getBaseContext())) {
            loadMenu();
        }else{
            Toast.makeText(getBaseContext(), "Check your Internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        updateToken(FirebaseInstanceId.getInstance().getToken());

        //Setup Slider
        setupSlider();
    }

    private void setupSlider() {
        sliderLayout = findViewById(R.id.slider);
        image_list = new HashMap<>();

        final DatabaseReference banners = database.getReference("Banner");

        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                    Banner banner = postSnapShot.getValue(Banner.class);

                    image_list.put(banner.getName()+"@@@"+banner.getId(),banner.getImage());
                }
                for(String key:image_list.keySet()){
                    String[] keySplit = key.split("@@@");
                    String nameOfFood = keySplit[0];
                    String idOfFood= keySplit[1];

                    final TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView
                            .description(nameOfFood)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(Home.this,FoodDetail.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId",idOfFood);
                    sliderLayout.addSlider(textSliderView);

                    banners.removeEventListener(this);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCarts(Common.currentUser.getPhone()));
        if(adapter!=null){
            adapter.startListening();
        }
    }

    private void updateToken(String token) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Tokens");
        Token data = new Token(token,true);//False means token is sent from Client
        reference.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void loadMenu() {

        FirebaseRecyclerOptions<Category> option = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.image);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(Home.this, "" + clickItem.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodList = new Intent(Home.this, FoodList.class);
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        Toast.makeText(Home.this, "" + adapter.getRef(position).getKey(), Toast.LENGTH_SHORT).show();
                        startActivity(foodList);
                    }
                });
            }

            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(itemView);
            }
        };
        recycler_menu.setAdapter(adapter);
        adapter.startListening();

    }


    @Override
    protected void onStop() {
        super.onStop();
        sliderLayout.stopAutoCycle();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.refresh){
            loadMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this,Cart.class);
            startActivity(cartIntent);
        } else if (id == R.id.nav_order) {
            Intent orderIntent = new Intent(Home.this,OrderStatus.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_exit) {

            //Delete Remember phone and password
            Paper.book().destroy();
            Intent signIn = new Intent(Home.this,SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);

        }else if(id == R.id.nav_change_pwd){
            showChangePwdDialog();
        }else if(id == R.id.nav_info){
            try {
                showInfoDialog();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if(id == R.id.nav_contact) {
            showContactDialog();

        }else if(id == R.id.nav_link){
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://medifoods.my/"));
            startActivity(browser);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showContactDialog() {
        final AlertDialog.Builder builder =new AlertDialog.Builder(Home.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View layout = inflater.inflate(R.layout.contact_layout, null);
        builder.setView(layout);
        builder.show();
    }

    private void showInfoDialog() throws FileNotFoundException {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View layout = inflater.inflate(R.layout.info_layout, null);
        builder.setView(layout);
        builder.show();
    }

    private void showChangePwdDialog() {
        final AlertDialog.Builder builder =new AlertDialog.Builder(Home.this);
        builder.setTitle("CHANGE PASSWORD");
        LayoutInflater inflater = LayoutInflater.from(this);
        final View layout = inflater.inflate(R.layout.change_password, null);

        final MaterialEditText edtPassword = layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword= layout.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRePassword = layout.findViewById(R.id.edtRePassword);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                final android.app.AlertDialog alertDialog = new SpotsDialog(Home.this);
                alertDialog.show();

                if(isEmpty(edtPassword.getText().toString()) || isEmpty(edtPassword.getText().toString()) || isEmpty(edtRePassword.getText().toString())){
                    alertDialog.dismiss();
                    Toast.makeText(Home.this, "Please fulfill all fields", Toast.LENGTH_SHORT).show();
                }else{
                    //Check old password
                    if(edtPassword.getText().toString().equals(Common.currentUser.getPassword())){
                        if(edtNewPassword.getText().toString().equals(edtRePassword.getText().toString())){
                            Map<String,Object> pwdUpdate = new HashMap<>();
                            pwdUpdate.put("Password",edtNewPassword.getText().toString());

                            DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                            user.child(Common.currentUser.getPhone())
                                    .updateChildren(pwdUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            alertDialog.dismiss();
                                            Toast.makeText(Home.this, "Password was updated", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else{
                            alertDialog.dismiss();
                            Toast.makeText(Home.this, "Please re-enter your password", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        alertDialog.dismiss();
                        Toast.makeText(Home.this, "Wrong password", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}
