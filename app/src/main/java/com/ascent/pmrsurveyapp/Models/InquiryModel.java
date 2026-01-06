package com.ascent.pmrsurveyapp.Models;

import java.io.Serializable;

public class InquiryModel implements Serializable {
    public String id = "", account="",date="",moveType="",contactPerson,goodsType="" , inquiryType="";

    public ClientModel shipper = new ClientModel();
    public AddressModel destinationAddress = new AddressModel();
    public AddressModel originAddress = new AddressModel();

    public InquiryModel(){}

    public InquiryModel(String id, String account, ClientModel shipper , String date, String moveType, AddressModel destinationAddress,AddressModel originAddress,String contactPerson , String goodsType , String inquiryType) {
        this.id = id;
        this.account = account;
        this.shipper = shipper;
        this.date = date;
        this.moveType = moveType;
        this.destinationAddress =destinationAddress;
        this.originAddress = originAddress;
        this.contactPerson = contactPerson;
        this.goodsType = goodsType;
        this.inquiryType = inquiryType;
    }
}