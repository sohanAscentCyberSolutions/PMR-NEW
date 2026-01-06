package com.ascent.pmrsurveyapp.SalesExecutive.Modals;

import com.ascent.pmrsurveyapp.Models.CommanModel;

import java.io.Serializable;
import java.util.ArrayList;

public class QuotationHistoryModel implements Serializable {
    public String id="",addressLine1 = "", addressLine2="", cityName="", clientContactNumber="", clientEmail="", clientName="", companyName=""
            , date="", fristName="" , inquiryDate="", lastName="" , moveType="", originAddressLine1="" , originAddressLine2=""
            , originCityName="",originStateName="",quotationVersion="",stateName="",total="";
    public ArrayList<CommanModel> particulars = new ArrayList<>();

    public QuotationHistoryModel(){}

    public QuotationHistoryModel(String id ,String addressLine1, String addressLine2, String cityName, String clientContactNumber, String clientEmail
            , String clientName, String companyName, String date, String fristName, String inquiryDate, String lastName,
                                 String moveType, String originAddressLine1, String originAddressLine2, String originCityName, String originStateName,
             String quotationVersion, String stateName, String total, ArrayList<CommanModel> particulars) {
        this.id = id;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.cityName = cityName;
        this.clientContactNumber = clientContactNumber;
        this.clientEmail = clientEmail;
        this.clientName = clientName;
        this.companyName = companyName;
        this.date = date;
        this.fristName = fristName;
        this.inquiryDate = inquiryDate;
        this.lastName = lastName;
        this.moveType = moveType;
        this.originAddressLine1 = originAddressLine1;
        this.originAddressLine2 = originAddressLine2;
        this.originCityName = originCityName;
        this.originStateName = originStateName;
        this.quotationVersion = quotationVersion;
        this.stateName = stateName;
        this.total = total;
        this.particulars = particulars;
    }
}