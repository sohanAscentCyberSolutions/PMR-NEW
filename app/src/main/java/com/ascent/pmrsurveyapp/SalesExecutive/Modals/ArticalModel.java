package com.ascent.pmrsurveyapp.SalesExecutive.Modals;

import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;

public class ArticalModel implements Serializable {
    public View rootView;
    public int moveType = 0;
    public ArrayList<View> articals = new ArrayList<>();
    public ArticalModel(){}

    public ArticalModel(ArrayList<View> articals, View rootView , int moveType) {
        this.articals = articals;
        this.rootView = rootView;
        this.moveType = moveType;
    }
}