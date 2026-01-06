package com.ascent.pmrsurveyapp.OperationSupervisor.Models;

import java.io.Serializable;

public class JobExecutionModel implements Serializable {
    public String id = "", volume="", status = "",requestLocation="",jobProgrammer = "",packingMaterialTransactions=""
            , branch="" , createdOn="";
    public Boolean packingMaterialAllocated=false,packingMaterialReturned=false,packingListCreated=false,loadingSheetCreated=false
            ,inwardSheetCreated=false,outwardSheetCreated=false,outwardCompleted=false,unloadingSheetCreated=false,unloadingCompleted=false,documentUploaded=false;
    public JobModel job = new JobModel();

    public JobExecutionModel(){}

    public JobExecutionModel(String id, JobModel job, String volume, String status, String requestLocation
            , String jobProgrammer , String packingMaterialTransactions, Boolean packingMaterialAllocated, Boolean packingMaterialReturned
            , Boolean packingListCreated, Boolean loadingSheetCreated, Boolean inwardSheetCreated, Boolean outwardSheetCreated
            , Boolean outwardCompleted, Boolean unloadingSheetCreated, Boolean unloadingCompleted, Boolean documentUploaded , String branch
            , String createdOn) {
        this.id = id;
        this.job = job  ;
        this.volume = volume;
        this.status = status;
        this.requestLocation = requestLocation;
        this.jobProgrammer = jobProgrammer;
        this.packingMaterialTransactions = packingMaterialTransactions;
        this.packingMaterialAllocated = packingMaterialAllocated;
        this.packingMaterialReturned = packingMaterialReturned;
        this.packingListCreated = packingListCreated;
        this.loadingSheetCreated = loadingSheetCreated;
        this.inwardSheetCreated = inwardSheetCreated;
        this.outwardSheetCreated = outwardSheetCreated;
        this.outwardCompleted = outwardCompleted;
        this.unloadingSheetCreated = unloadingSheetCreated;
        this.unloadingCompleted = unloadingCompleted;
        this.documentUploaded = documentUploaded;
        this.branch = branch;
        this.createdOn = createdOn;
    }
}