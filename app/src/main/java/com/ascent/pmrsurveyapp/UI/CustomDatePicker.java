package com.ascent.pmrsurveyapp.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.databinding.ActivityCustomDatePickerBinding;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CustomDatePicker extends AppCompatActivity {

    ActivityCustomDatePickerBinding binding;
    Activity ctx;
    Comman cmn;
    int requestCode;
    boolean isOnlyDate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_date_picker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ctx = this;
        cmn = new Comman(this);

        requestCode = getIntent().getIntExtra("requestCode" , 0);
        isOnlyDate = getIntent().getBooleanExtra("isOnlyDate" , false);

        DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);
        TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);

        if (isOnlyDate){
            timePicker.setVisibility(View.GONE);
        }



        findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                Long time = calendar.getTimeInMillis();

                Log.e("date&time" , ""+cmn.getDateTime(time));

                Intent intent=new Intent();
                if (isOnlyDate){
                    intent.putExtra("datetime",cmn.getDate(time));
                }else {
                    intent.putExtra("datetime",cmn.getDateTime(time));
                }
                setResult(requestCode,intent);
                finish();
            }
        });

    }
}