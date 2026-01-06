package com.ascent.pmrsurveyapp.Models;

import java.io.Serializable;

public class RequestsModel implements Serializable {
    public String id = "", branch="",createdBy = "",insuranceClaim="",requestDate="",startDate = "",surveyor="",type="",status="";
    public InquiryModel inquiryModel = new InquiryModel();
    public RequestsModel(){}

    public RequestsModel(String id, String branch, String createdBy , String insuranceClaim,String requestDate, String startDate , String surveyor,String type , InquiryModel inquiryModel , String status) {
        this.id = id;
        this.branch = branch;
        this.createdBy = createdBy;
        this.insuranceClaim = insuranceClaim;
        this.requestDate = requestDate;
        this.startDate = startDate;
        this.surveyor = surveyor;
        this.type = type;
        this.inquiryModel = inquiryModel;
        this.status = status;
    }
}