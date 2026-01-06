package com.ascent.pmrsurveyapp.OperationSupervisor.Models;

import com.ascent.pmrsurveyapp.Models.AddressModel;
import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.AccountModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.ContactsModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel;

import java.io.Serializable;
import java.util.ArrayList;

public class JobModel implements Serializable {
    public String id = "", obJobNumber="",ibJobNumber = "",jobNumber="",inquiryDetail = "",createdOn=""
            , updatedOn="" , status="",moveType="",jobAreaType="",goodsType="",movingItemsVolume="",ownershipType="";

    public Boolean feedbackRequestSent = false,dispatchDetailAdded= false,storageDetailAdded=false,insuranceValueAdded=false,costSheetCreated=false
            ,costSheetUpdated=false,claimRequestCreated=false,invoiceGenerated=false,shipmentAdviseCreated=false,crewInstructionCreated=false,jobExecRequestSent=false
    ,destinationJobExecRequestSent=false,jobScheduled=false,destinationJobScheduled,insuranceRequestSent=false
            ,readyToDispatch=false,destinationCoordinatorAssigned=false,outwardSheetCreated=false;

    public InquiryModel inquiry = new InquiryModel();
    public AccountModel account = new AccountModel();
    public ShipperModel shipper = new ShipperModel();
    public CommanModel owningCoordinator = new CommanModel();
    public CommanModel originCoordinator = new CommanModel();
    public CommanModel destinationCoordinator = new CommanModel();
    public AddressModel originAddress = new AddressModel();
    public AddressModel destinationAddress = new AddressModel();


    public ArrayList<ContactsModel> contacts = new ArrayList<>();
    public JobModel(){}

    public JobModel(String id, String obJobNumber, String ibJobNumber, String jobNumber, InquiryModel inquiry
            , String inquiryDetail , AccountModel account,ShipperModel shipper, CommanModel owningCoordinator, CommanModel originCoordinator, CommanModel destinationCoordinator
            , String createdOn , String updatedOn,AddressModel originAddress,AddressModel destinationAddress
            , String status , String moveType, String jobAreaType, String goodsType, Boolean feedbackRequestSent, Boolean dispatchDetailAdded
            , Boolean storageDetailAdded, Boolean insuranceValueAdded, Boolean costSheetCreated, Boolean costSheetUpdated
            , Boolean claimRequestCreated, Boolean invoiceGenerated, Boolean shipmentAdviseCreated, Boolean crewInstructionCreated
            , Boolean jobExecRequestSent, Boolean destinationJobExecRequestSent, Boolean jobScheduled, Boolean destinationJobScheduled
            , Boolean insuranceRequestSent, Boolean readyToDispatch, Boolean destinationCoordinatorAssigned, Boolean outwardSheetCreated
            , String movingItemsVolume, String ownershipType) {
        this.id = id;
        this.obJobNumber = obJobNumber;
        this.ibJobNumber = ibJobNumber;
        this.jobNumber = jobNumber;
        this.inquiry = inquiry;
        this.inquiryDetail = inquiryDetail;
        this.account = account;
        this.shipper = shipper;
        this.owningCoordinator = owningCoordinator;
        this.originCoordinator = originCoordinator;
        this.destinationCoordinator = destinationCoordinator;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.originAddress = originAddress;
        this.destinationAddress = destinationAddress;
        this.status = status;
        this.moveType = moveType;
        this.jobAreaType = jobAreaType;
        this.goodsType = goodsType;
        this.feedbackRequestSent = feedbackRequestSent;
        this.dispatchDetailAdded = dispatchDetailAdded;
        this.storageDetailAdded = storageDetailAdded;
        this.insuranceValueAdded = insuranceValueAdded;
        this.costSheetCreated = costSheetCreated;
        this.costSheetUpdated = costSheetUpdated;
        this.claimRequestCreated = claimRequestCreated;
        this.invoiceGenerated = invoiceGenerated;
        this.shipmentAdviseCreated = shipmentAdviseCreated;
        this.crewInstructionCreated = crewInstructionCreated;
        this.jobExecRequestSent = jobExecRequestSent;
        this.destinationJobExecRequestSent = destinationJobExecRequestSent;
        this.jobScheduled = jobScheduled;
        this.destinationJobScheduled = destinationJobScheduled;
        this.insuranceRequestSent = insuranceRequestSent;
        this.readyToDispatch = readyToDispatch;
        this.destinationCoordinatorAssigned = destinationCoordinatorAssigned;
        this.outwardSheetCreated = outwardSheetCreated;
        this.movingItemsVolume = movingItemsVolume;
        this.ownershipType = ownershipType;
    }
}