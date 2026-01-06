package com.ascent.pmrsurveyapp.SalesExecutive.Modals;

import com.ascent.pmrsurveyapp.Models.AddressModel;

import java.io.Serializable;

public class ClientModel implements Serializable {
    public String name = "", email="",contactNumber="";
    public AddressModel address = new AddressModel();

    public ClientModel(){}

    public ClientModel(String name, String email, String contactNumber , AddressModel address) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.address = address;

    }
}