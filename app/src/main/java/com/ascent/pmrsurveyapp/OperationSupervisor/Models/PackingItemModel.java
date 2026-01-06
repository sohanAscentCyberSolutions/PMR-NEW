package com.ascent.pmrsurveyapp.OperationSupervisor.Models;

import android.view.View;

import java.io.Serializable;

public class PackingItemModel implements Serializable {
    public String id = "", itemNo="",crRef = "",article="",quantity = "",value="" , packedBy=""
            , conditionAtOrigin="",filePath="",fileName="",outwardDate="",unloadingDate="";

    public int loadingSequence = 0;
    public  View itemView;

    public boolean loadingDone=false,inwardDone=false,outwardDone=false,unloadingDone=false;

    public PackingItemModel(){}

    public PackingItemModel(String id, String itemNo, String crRef, String article, String quantity
            , String value , String packedBy, String conditionAtOrigin , String filePath ,String fileName
            , int loadingSequence , Boolean loadingDone, Boolean inwardDone, Boolean outwardDone, Boolean unloadingDone
    ,String outwardDate , String unloadingDate) {
        this.id = id;
        this.itemNo = itemNo;
        this.crRef = crRef;
        this.article = article;
        this.quantity = quantity;
        this.value = value;
        this.packedBy = packedBy;
        this.conditionAtOrigin = conditionAtOrigin;
        this.filePath = filePath;
        this.fileName = fileName;
        this.loadingSequence = loadingSequence;
        this.loadingDone = loadingDone;
        this.inwardDone = inwardDone;
        this.outwardDone = outwardDone;
        this.unloadingDone = unloadingDone;
        this.outwardDate = outwardDate;
        this.unloadingDate = unloadingDate;

    }


    public PackingItemModel(String itemNo, View itemView) {
        this.itemNo = itemNo;
        this.filePath = filePath;
        this.fileName = fileName;
        this.itemView = itemView;
    }

}