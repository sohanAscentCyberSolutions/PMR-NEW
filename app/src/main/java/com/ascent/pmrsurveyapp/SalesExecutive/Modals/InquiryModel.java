package com.ascent.pmrsurveyapp.SalesExecutive.Modals;

import com.ascent.pmrsurveyapp.Models.AddressModel;
import com.ascent.pmrsurveyapp.Models.CommanModel;

import java.io.Serializable;

public class InquiryModel implements Serializable {
    public String id = "", contactPerson="",date = "",moveType="",status = "",goodsType="";
    public CommanModel account = new CommanModel();
    public CommanModel assignedTo = new CommanModel();
    public CommanModel shipper = new CommanModel();
    public ClientModel client = new ClientModel();
    public AddressModel destinationAddress = new AddressModel();
    public AddressModel originAddress = new AddressModel();

    public Boolean documentUploaded = false;


    public InquiryModel(){}

    public InquiryModel(String id, CommanModel account, CommanModel assignedTo, ClientModel client, AddressModel destinationAddress
            , AddressModel originAddress , String contactPerson, String date, String moveType , String status , CommanModel shipper , String goodsType , Boolean documentUploaded) {
        this.id = id;
        this.account = account;
        this.assignedTo = assignedTo;
        this.client = client;
        this.destinationAddress = destinationAddress;
        this.originAddress = originAddress;
        this.contactPerson = contactPerson;
        this.date = date;
        this.moveType = moveType;
        this.status = status;
        this.shipper = shipper;
        this.goodsType = goodsType;
        this.documentUploaded = documentUploaded;
    }

}