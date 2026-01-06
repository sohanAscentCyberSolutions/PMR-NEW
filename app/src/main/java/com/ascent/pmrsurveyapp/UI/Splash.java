package com.ascent.pmrsurveyapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Window;
import android.view.WindowManager;

import com.ascent.pmrsurveyapp.OperationSupervisor.DashboardOS;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.DashboardSalesExecutive;
import com.ascent.pmrsurveyapp.Utills.Comman;

public class Splash extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    Comman appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        appPreferences = new Comman(Splash.this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

       // logo = findViewById(R.id.logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (appPreferences.getIsLogged() == false) {
                    Intent mainIntent = new Intent(Splash.this, LogIn.class);
                    startActivity(mainIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    Intent mainIntent;
                    if (appPreferences.getUserType().equalsIgnoreCase(appPreferences.userSalesExecutive) || appPreferences.getUserType().equalsIgnoreCase(appPreferences.userPricingExecutive)){
                        mainIntent = new Intent(Splash.this, DashboardSalesExecutive.class);
                    }else if (appPreferences.getUserType().equalsIgnoreCase(appPreferences.userOperationSupervisor)){
                        mainIntent = new Intent(Splash.this, DashboardOS.class);
                    }else{
                        mainIntent = new Intent(Splash.this, Dashboard.class);
                    }
                    startActivity(mainIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}