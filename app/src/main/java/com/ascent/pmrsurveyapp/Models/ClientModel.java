package com.ascent.pmrsurveyapp.Models;

import java.io.Serializable;

public class ClientModel implements Serializable {
    public String id = "", contactNumber="",email = "",name="";

    public ClientModel(){}

    public ClientModel(String id, String contactNumber, String email , String name) {
        this.id = id;
        this.contactNumber = contactNumber;
        this.email = email;
        this.name = name;
    }
}