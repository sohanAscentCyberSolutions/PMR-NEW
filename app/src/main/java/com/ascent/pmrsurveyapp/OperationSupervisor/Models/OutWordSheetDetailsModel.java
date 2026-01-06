package com.ascent.pmrsurveyapp.OperationSupervisor.Models;

import java.io.Serializable;

public class OutWordSheetDetailsModel implements Serializable {
    public String id = "", itemNo="",outwardSequence = "",crRef="",article="";

    public OutWordSheetDetailsModel(){}

    public OutWordSheetDetailsModel(String id, String itemNo, String outwardSequence, String crRef, String article) {
        this.id = id;
        this.itemNo = itemNo;
        this.outwardSequence = outwardSequence;
        this.crRef = crRef;
        this.article = article;
    }
}