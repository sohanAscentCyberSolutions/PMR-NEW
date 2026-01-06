package com.ascent.pmrsurveyapp.Adepters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ascent.pmrsurveyapp.Models.MenuModel;
import com.ascent.pmrsurveyapp.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nl.psdcompany.duonavigationdrawer.views.DuoOptionView;

public class MenuAdapter extends BaseAdapter {
    private List<MenuModel> mOptions = new ArrayList<>();
    private ArrayList<DuoOptionView> mOptionViews = new ArrayList<>();
    Context context1;

    public MenuAdapter(List<MenuModel> options, Context context) {
        mOptions = options;
        context1 = context;
    }

    @Override
    public int getCount() {
        return mOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return mOptions.get(position);
    }

    public void setViewSelected(int position, boolean selected) {

        // Looping through the options in the menu
        // Selecting the chosen option
        for (int i = 0; i < mOptionViews.size(); i++) {
            if (i == position) {
                mOptionViews.get(i).setSelected(selected);
            } else {
                mOptionViews.get(i).setSelected(!selected);
            }
        }
    }

    Drawable drawable_from_url(String url) throws java.net.MalformedURLException, java.io.IOException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpURLConnection connection = (HttpURLConnection)new URL(url) .openConnection();
        connection.setRequestProperty("User-agent","Mozilla/4.0");

        connection.connect();
        InputStream input = connection.getInputStream();

        return new BitmapDrawable(context1.getResources(), BitmapFactory.decodeStream(input));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String option = mOptions.get(position).value;

        // Using the DuoOptionView to easily recreate the demo
        final DuoOptionView optionView;
        if (convertView == null) {
            optionView = new DuoOptionView(parent.getContext());
        } else {
            optionView = (DuoOptionView) convertView;
        }
        (((ImageView)((RelativeLayout)((RelativeLayout)optionView.getChildAt(0)).getChildAt(1)).getChildAt(0))).setColorFilter(context1.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        (((TextView)((RelativeLayout)((RelativeLayout)optionView.getChildAt(0)).getChildAt(1)).getChildAt(1))).setTextSize(16.0f);
        (((TextView)((RelativeLayout)((RelativeLayout)optionView.getChildAt(0)).getChildAt(1)).getChildAt(1))).setTextColor(context1.getResources().getColor(R.color.white));

        try{
           /* if(mOptions.get(position).key.matches("dashboard")){
                optionView.bind(option, context1.getResources().getDrawable(R.drawable.ic_home), null);
            }else if(mOptions.get(position).key.matches("product_scan")){
                optionView.bind(option, context1.getResources().getDrawable(R.drawable.ic_barcode), null);
            }else if(mOptions.get(position).key.matches("service_assignments")){
                optionView.bind(option, drawable_from_url(mOptions.get(position).icon), null);
            } else {
                optionView.bind(option, null, null);
            } */
        } catch (Exception e){
            e.printStackTrace();
        }



        if(position==0){
            optionView.bind(option, context1.getDrawable(R.drawable.ic_home), null);
        }if(position==1){
            optionView.bind(option, context1.getDrawable(R.drawable.ic_assigned), null);
        }if(position==2){
            optionView.bind(option, context1.getDrawable(R.drawable.ic_closed), null);
        }if(position==3){
            optionView.bind(option, context1.getDrawable(R.drawable.ic_logout), null);
        }if(position==4){
            optionView.bind(option, context1.getDrawable(R.drawable.ic_logout), null);
        } if(position==5){
            optionView.bind(option, context1.getDrawable(R.drawable.ic_logout), null);
        } else {
            optionView.bind(option, null, null);
        }


        // Adding the views to an array list to handle view selection
        mOptionViews.add(optionView);

        return optionView;
    }
}
