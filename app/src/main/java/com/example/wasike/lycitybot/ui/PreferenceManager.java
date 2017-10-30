package com.example.wasike.lycitybot.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.wasike.lycitybot.R;

public class PreferenceManager extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    //shared pref mode
    int PRIVATE_MODE = 0;

    //shared preferences file name
    private static final String PREF_NAME = "intro_slider-welcome";

    private static final String IS__FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PreferenceManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS__FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS__FIRST_TIME_LAUNCH, true);
    }

    //not sure if the onCreate will be used
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_manager);
    }
}
