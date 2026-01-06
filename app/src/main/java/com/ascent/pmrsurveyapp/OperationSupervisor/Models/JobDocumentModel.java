package com.ascent.pmrsurveyapp.OperationSupervisor.Models;

import android.view.View;

import java.io.Serializable;

public class JobDocumentModel implements Serializable {
    public String id = "", filePath="",fileName = "",description="";
    public View tempView = null;

    public JobDocumentModel(){}

    public JobDocumentModel(String id, String filePath, String fileName, String description) {
        this.id = id;
        this.filePath = filePath;
        this.fileName = fileName;
        this.description = description;
    }

    public JobDocumentModel(String id, String filePath, String fileName, String description , View temp) {
        this.id = id;
        this.filePath = filePath;
        this.fileName = fileName;
        this.description = description;
        this.tempView = temp;
    }

}