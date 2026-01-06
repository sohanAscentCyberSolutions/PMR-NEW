package com.ascent.pmrsurveyapp.SalesExecutive.Modals;

import java.io.Serializable;

public class StandardItemsModel implements Serializable {
    public String id = "", name="";
    public int height = 0 , width = 0 , length = 0;

    public double weight = 0,volume = 0;

    public StandardItemsModel(){}

    public StandardItemsModel(String id, String name, int height , int width, int length, double volume , double weight) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.width = width;
        this.length = length;
        this.volume = volume;
        this.weight = weight;
    }
}