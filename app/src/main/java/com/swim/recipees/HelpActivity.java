package com.swim.recipees;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toolbar;

public class HelpActivity extends AppCompatActivity{


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.help_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(HelpActivity.this, android.R.color.holo_blue_dark));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.help_menu, menu);
        menu.getItem(0).setChecked(MainActivity.night_mode);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.night_mode:
                if(!item.isChecked()){
                    item.setChecked(true);
                    MainActivity.night_mode = true;
                    MainActivity.first_color = android.R.color.darker_gray;
                    MainActivity.second_color = android.R.color.holo_red_dark;
                    MainActivity.third_color = android.R.color.holo_green_dark;
                    Log.i("Colors changed", "night mode");
                }else{
                    item.setChecked(false);
                    MainActivity.night_mode = false;
                    MainActivity.first_color = android.R.color.holo_orange_dark;
                    MainActivity.second_color = android.R.color.holo_red_light;
                    MainActivity.third_color = android.R.color.holo_green_light;
                    Log.i("Colors changed", "day mode");
                }
                return true;
            case R.id.about_me:
                Intent intent = new Intent(this, AboutMeActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
