package com.swim.recipees;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.FingerprintGestureController;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.accessibility.AccessibilityEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.swim.recipees.FragBrowse.recipes;

public class MainActivity extends AppCompatActivity{

    private PageAdapter page_adapter;
    private Toolbar toolbar;
    private ViewPager view_pager;
    private TabLayout tab_layout;
    private TabItem browse_tab, new_tab, favourites_tab;
    private FingerprintGestureController.FingerprintGestureCallback fingesture;
    public static int first_color;
    public static int second_color;
    public static int third_color;
    public static boolean night_mode;


    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = sp.getString("Recipes", "");
        Type type = new TypeToken<ArrayList<Recipe>>() {
        }.getType();
        recipes = gson.fromJson(json, type);
        if (recipes == null) {
            recipes = new ArrayList<>();
        }
        Log.i("Loaded recipes", recipes.toString());

        night_mode = sp.getBoolean("Night mode", false);
        

        if(night_mode){
            first_color = android.R.color.darker_gray;
            second_color = android.R.color.holo_red_dark;
            third_color = android.R.color.holo_green_dark;
            Log.i("Loaded colors", "night mode on");
        }else{
            first_color = android.R.color.holo_orange_dark;
            second_color = android.R.color.holo_red_light;
            third_color = android.R.color.holo_green_light;
            Log.i("Loaded colors", "night mode off");
        }



        tab_layout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar);
        view_pager = findViewById(R.id.view_pager);
        favourites_tab = findViewById(R.id.tab_favourites);
        browse_tab = findViewById(R.id.tab_browse);
        new_tab = findViewById(R.id.tab_new_recipe);



        page_adapter = new PageAdapter(getSupportFragmentManager(), tab_layout.getTabCount());
        view_pager.setAdapter(page_adapter);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, first_color));
        tab_layout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, first_color));
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, first_color));

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager.setCurrentItem(tab.getPosition());
                int color;
                if (tab.getPosition() == 1) {
                    color = second_color;
                } else if (tab.getPosition() == 2) {
                    color = third_color;
                } else {
                    color = first_color;
                }
                toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, color));
                tab_layout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, color));
                getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, color));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layout));
        fingesture = new FingerprintGestureController.FingerprintGestureCallback() {
            @Override
            public void onGestureDetectionAvailabilityChanged(boolean available) {
                super.onGestureDetectionAvailabilityChanged(available);
                Log.i("Fingesture changed", String.valueOf(available));
            }

            @Override
            public void onGestureDetected(int gesture) {
                Log.i("Fingesture: ", "gesture detected");
                super.onGestureDetected(gesture);
                if(gesture == FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_LEFT){
                    view_pager.setCurrentItem(Math.max(view_pager.getCurrentItem(), 0));
                    Log.i("Fingesture: ", "swipe left");
                }else if(gesture == FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_RIGHT){
                    view_pager.setCurrentItem(Math.min(view_pager.getCurrentItem(), 2));
                    Log.i("Fingesture: ", "swipe right");
                }
            }
        };



    }

    @Override
    public void onStop(){
        super.onStop();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor spe = sp.edit();
        spe.putBoolean("Night mode", night_mode);
        spe.apply();

    }

    @Override
    public void onResume(){
        super.onResume();
        toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, first_color));
        tab_layout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, first_color));
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, first_color));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        return true;
    }


}
