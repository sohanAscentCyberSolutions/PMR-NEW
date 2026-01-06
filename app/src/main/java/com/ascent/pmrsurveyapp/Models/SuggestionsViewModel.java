package com.ascent.pmrsurveyapp.Models;

import android.view.View;

import java.io.Serializable;

public class SuggestionsViewModel implements Serializable {
    public View view;
    public SuggestionsModel selectedSuggesstion = new SuggestionsModel();

    public SuggestionsViewModel(){}

    public SuggestionsViewModel(View view, SuggestionsModel selectedSuggesstion) {
        this.view = view;
        this.selectedSuggesstion = selectedSuggesstion;
    }
}