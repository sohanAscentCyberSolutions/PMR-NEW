package com.ascent.pmrsurveyapp.SalesExecutive.Modals;

import com.ascent.pmrsurveyapp.Models.AddressModel;
import com.ascent.pmrsurveyapp.Models.CommanModel;

import java.io.Serializable;
import java.util.ArrayList;

public class AccountModel implements Serializable {
    public String id = "", companyName="",email = "",mobile="",registrationCode = "",paymentTerms="" , gstinUin="" , landlineNumber="";
    public AddressModel address = new AddressModel();
    public CommanModel industry = new CommanModel();
    public  ManagerModel manager = new ManagerModel();
    public ArrayList<ContactsModel> contacts = new ArrayList<>();
    public AccountModel(){}

    public AccountModel(String id, String companyName,AddressModel address,ManagerModel manager,ArrayList<ContactsModel> contacts
            , String email , String mobile, String registrationCode , String paymentTerms , String gstinUin , CommanModel industry, String landlineNumber) {
        this.id = id;
        this.companyName = companyName;
        this.address = address;
        this.manager = manager;
        this.contacts = contacts;
        this.email = email;
        this.mobile = mobile;
        this.registrationCode = registrationCode;
        this.paymentTerms = paymentTerms;
        this.gstinUin = gstinUin;
        this.industry = industry;
        this.landlineNumber = landlineNumber;
    }
}