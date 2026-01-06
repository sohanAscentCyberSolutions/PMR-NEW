package com.ascent.pmrsurveyapp.SalesExecutive.Modals;

import com.ascent.pmrsurveyapp.Models.CommanModel;

import java.io.Serializable;

public class QuotationModel implements Serializable {
    public String id = "", date="";
    public CommanModel preparedBy = new CommanModel();
    public  InquiryModel inquiry = new InquiryModel();

    public QuotationModel(){}

    public QuotationModel(String id, String date, CommanModel preparedBy, InquiryModel inquiry) {
        this.id = id;
        this.date = date;
        this.preparedBy = preparedBy;
        this.inquiry = inquiry;
    }
}