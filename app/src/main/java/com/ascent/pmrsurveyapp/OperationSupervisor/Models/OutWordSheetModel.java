package com.ascent.pmrsurveyapp.OperationSupervisor.Models;

import java.io.Serializable;

public class OutWordSheetModel implements Serializable {
    public String id = "", outwardDate="",createdBy = "",noOfPackages="";

    public OutWordSheetModel(){}

    public OutWordSheetModel(String id, String outwardDate, String createdBy, String noOfPackages) {
        this.id = id;
        this.outwardDate = outwardDate;
        this.createdBy = createdBy;
        this.noOfPackages = noOfPackages;
    }
}