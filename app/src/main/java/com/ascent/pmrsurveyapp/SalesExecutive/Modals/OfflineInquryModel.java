package com.ascent.pmrsurveyapp.SalesExecutive.Modals;

import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OfflineInquryModel extends RealmObject {

    @PrimaryKey
    private String id;

    private String data;

    public OfflineInquryModel(){}

   public OfflineInquryModel(String id ,String data){
        this.data = data;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getdata() {
        return this.data;
    }

    public void setdata(String data) {
        this.data = data;
    }


}
