package com.ascent.pmrsurveyapp.Models;

import java.io.Serializable;

public class SuggestionsModel implements Serializable {
    public String name = "";
    public int id=0,height=0,length=0,volume=0,width=0;

    public SuggestionsModel(){}

    public SuggestionsModel(int id, String name, int height , int length, int volume, int width) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.length = length;
        this.volume = volume;
        this.width = width;
    }
}