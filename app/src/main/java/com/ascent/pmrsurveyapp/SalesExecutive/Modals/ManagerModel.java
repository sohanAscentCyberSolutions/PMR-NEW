package com.ascent.pmrsurveyapp.SalesExecutive.Modals;

import java.io.Serializable;

public class ManagerModel implements Serializable {
    public String id = "", firstName="",lastName="",email="" , mobile = "" , officeNumber="",salutation= "";

    public ManagerModel(){}

    public ManagerModel(String id, String firstName, String lastName , String email, String mobile, String officeNumber,String salutation) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.officeNumber = officeNumber;
        this.salutation = salutation;
    }
}