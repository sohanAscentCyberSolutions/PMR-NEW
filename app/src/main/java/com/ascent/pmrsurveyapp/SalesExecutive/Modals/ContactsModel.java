package com.ascent.pmrsurveyapp.SalesExecutive.Modals;

import java.io.Serializable;

public class ContactsModel implements Serializable {
    public String id = "" ,email = "", name="",number="";

    public ContactsModel(){}

    public ContactsModel(String id, String email, String name, String number) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.number = number;
    }
}