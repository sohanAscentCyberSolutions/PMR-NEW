package com.ascent.pmrsurveyapp.OperationSupervisor.Models;

import java.io.Serializable;

public class ShipperModel implements Serializable {
    public String id = "", firstName="",lastName = "",fullName="",contactNumber = "",alternateNumber="" , email="" , alternateEmail="",designation="";
    public ShipperModel(){}

    public ShipperModel(String id, String firstName, String lastName, String fullName, String contactNumber
            , String alternateNumber , String email, String alternateEmail , String designation) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.alternateNumber = alternateNumber;
        this.email = email;
        this.alternateEmail = alternateEmail;
        this.designation = designation;
    }
}