package com.example.stepcounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TimerFragment extends Fragment implements SensorEventListener {

    SensorManager mSensorManager = null;
    Sensor stepSensor;
    int totalSteps = 0;
    int previewsTotalSteps = 0;
    ProgressBar progressBar;
    TextView steps;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);
        progressBar = rootView.findViewById(R.id.progressBarSteps);
        steps = rootView.findViewById(R.id.steps);
        resetSteps();
        loadData();

        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        return rootView;
    }

    public void onResume(){
        super.onResume();
        if (stepSensor == null){
            Toast.makeText(getContext(),"This device has no sensor",Toast.LENGTH_SHORT).show();
        }
        else{
            mSensorManager.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void resetSteps(){
        steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Long press to reset steps",Toast.LENGTH_SHORT).show();
            }
        });
        steps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                previewsTotalSteps = totalSteps;
                steps.setText("0");
                progressBar.setProgress(0);
                saveData();
                return true;
            }
        });
    }

    public void saveData(){
        SharedPreferences sharedPref = getContext().getSharedPreferences("myPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("key1",String.valueOf(previewsTotalSteps));
        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPref = getContext().getSharedPreferences("myPref",Context.MODE_PRIVATE);
        int savedNumber = (int) sharedPref.getFloat("key1",0f);
        previewsTotalSteps = savedNumber;
    }

   // @Override
    //public void onSensorChanged(SensorEvent event) {
    //    if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
    //        totalSteps = (int) event.values[0];
    //        int currentSteps = totalSteps-previewsTotalSteps;
    //        steps.setText(String.valueOf(currentSteps));

    //        progressBar.setProgress(currentSteps);
    //    }
    //}
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            totalSteps = (int) event.values[0];
            int currentSteps = totalSteps - previewsTotalSteps;
            steps.setText(String.valueOf(currentSteps));

            // Mettre à jour la couleur du texte en fonction du nombre de pas
            if (currentSteps >= 100) {
                steps.setTextColor(Color.RED);
            } else if (currentSteps >= 50) {
                steps.setTextColor(Color.GREEN);
            } else {
                steps.setTextColor(Color.BLACK);
            }

            progressBar.setProgress(currentSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}