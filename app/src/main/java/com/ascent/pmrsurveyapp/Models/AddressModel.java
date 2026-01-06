package com.ascent.pmrsurveyapp.Models;

import java.io.Serializable;

public class AddressModel implements Serializable {
    public String addressLine1 = "", addressLine2="",area="",pinCode="";
    public CommanModel city = new CommanModel();
    public CommanModel state = new CommanModel();

    public AddressModel(){}

    public AddressModel(String addressLine1, String addressLine2, String area , CommanModel city, CommanModel state, String pinCode) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.area = area;
        this.city = city;
        this.state = state;
        this.pinCode = pinCode;
    }
}