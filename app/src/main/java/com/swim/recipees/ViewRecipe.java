package com.swim.recipees;

import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.round;

public class ViewRecipe extends AppCompatActivity implements SensorEventListener {

    private Recipe recipe;
    private boolean gyroscroll;
    private boolean proxifling;
    private SensorManager sensor_manager;
    private ScrollView scroll_view;
    private float[] point_of_reference;
    private boolean first_measure = true;
    private boolean fling_ready = false;
    final int FLING_FORCE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        Toolbar toolbar = findViewById(R.id.view_recipe_toolbar);
        TextView description = findViewById(R.id.view_recipe_descritpion);
        ImageView image = findViewById(R.id.view_recipe_image);
        scroll_view = findViewById(R.id.view_recipe_description_layout);
        gyroscroll = false;
        proxifling = false;
        sensor_manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        point_of_reference = new float[3];
        resetPOR();

        int color = getRandomColor();
        toolbar.setBackgroundColor(color);
        getWindow().setStatusBarColor(color);

        recipe = FragBrowse.recipes.get(getIntent().getIntExtra("recipe_position", 0));
        Log.i("Recipe in viewer: ", recipe.toString());

        if(recipe == null){
            recipe = new Recipe(getString(R.string.default_name), null, new ArrayList<FragNewRecipe.Ingredient>(), getString(R.string.default_description));
        }

        toolbar.setTitle(recipe.getName());
        description.setText(recipe.getDescription());
        image.setImageDrawable(recipe.getImage().getDrawable());
        setSupportActionBar(toolbar);

    }

    @Override
    public void onResume(){
        super.onResume();
        if(gyroscroll) {
            Sensor sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensor_manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            Log.i("Sensor started: ", "Accelerometer");
        }
        if(proxifling){
            Sensor sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensor_manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            Log.i("Sensor started: ", "Proximity");
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if(gyroscroll){
            gyroscroll = false;
            sensor_manager.unregisterListener(this);
            resetPOR();
            Log.i("Sensor stopped: ", "Accelerometer");
        }
        if(proxifling){
            proxifling = false;
            sensor_manager.unregisterListener(this);
            resetPOR();
            Log.i("Sensor stopped: ", "Proximity");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_recipe_menu, menu);
        menu.getItem(0).setChecked(gyroscroll);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.view_recipe_gyroscroll_switch:
                if(item.isChecked()){
                    gyroscroll = false;
                    item.setChecked(false);
                    sensor_manager.unregisterListener(this);
                    resetPOR();
                    Log.i("Sensor stopped: ", "Accelerometer");
                }else{
                    gyroscroll = true;
                    item.setChecked(true);
                    Sensor sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    sensor_manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
                    Log.i("Sensor started: ", "Accelerometer");
                }
                return true;
            case R.id.view_recipe_proxifling_switch:
                if(item.isChecked()){
                    proxifling = false;
                    item.setChecked(false);
                    sensor_manager.unregisterListener(this);
                    resetPOR();
                    Log.i("Sensor stopped: ", "Proximity");
                }else{
                    proxifling = true;
                    item.setChecked(true);
                    Sensor sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                    sensor_manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
                    Log.i("Sensor started: ", "Proximity");
                }
            default:
                break;
        }

        return true;
    }


    public static int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    private void resetPOR(){
        point_of_reference[0] = 0.0f;
        point_of_reference[1] = 0.0f;
        point_of_reference[2] = 0.0f;
        first_measure = true;
        fling_ready = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            if(first_measure){
                System.arraycopy(event.values, 0, point_of_reference, 0, point_of_reference.length);
                first_measure = false;
            }
            int dimension = 1;
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                dimension = 0;
            }
            scroll_view.smoothScrollBy(0, round(point_of_reference[dimension]-event.values[dimension]));
        }
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            Log.i("Proximity data: ", String.valueOf(event.values[0]));
            if(!fling_ready && event.values[0] == 0.0f){
                fling_ready = true;
            }else if(fling_ready && event.values[0] != 0.0f){
                fling_ready = false;
                scroll_view.fling(FLING_FORCE);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
