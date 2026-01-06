package com.ascent.pmrsurveyapp.Utills;

import android.content.Context;

import com.ascent.pmrsurveyapp.Models.AddressModel;
import com.ascent.pmrsurveyapp.Models.ClientModel;
import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.Models.InquiryModel;
import com.ascent.pmrsurveyapp.Models.RequestsModel;
import com.ascent.pmrsurveyapp.Models.SuggestionsModel;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobExecutionModel;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobModel;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.PackingItemModel;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.ShipperModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.AccountModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.ContactsModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.ManagerModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.QuotationHistoryModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.QuotationModel;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Parser {

    Context ctx;

    public Parser(Context ctx){
        this.ctx = ctx;
    }

    public RequestsModel parseRequest(JSONObject object){

        InquiryModel inquiryModel = parseInquiry(object.optJSONObject("inquiry"));

        if (object != null) {
            String branch = "";
            if (checkFornull(object.optJSONObject("branch"))){
                if (checkFornull(object.optJSONObject("branch").optJSONObject("address"))){
                    if (checkFornull(object.optJSONObject("branch").optJSONObject("address").optJSONObject("city"))){
                        branch = object.optJSONObject("branch").optJSONObject("address").optJSONObject("city").optString("name");

                    }
                }
            }

            String createdBy = "";
            if (checkFornull(object.optJSONObject("createdBy"))){
                createdBy = object.optJSONObject("createdBy").optString("fullName");
            }

            String surveyor = "";
            if (checkFornull(object.optJSONObject("surveyor"))){
                surveyor = object.optJSONObject("surveyor").optString("fullName");
            }

            String requestDate = "";
            if (!object.optString("requestDate").isEmpty()) {
                requestDate = new Comman(ctx).getDateTime(new Long(object.optString("requestDate")));
                requestDate = requestDate.replace("00" , "12");
            }

            String startDate = "";
            if (!object.optString("startDate").isEmpty()) {
                startDate = new Comman(ctx).getDateTime(new Long(object.optString("startDate")));
                startDate = startDate.replace("00" , "12");
            }

            return new RequestsModel(object.optString("id"), branch ,createdBy, object.optString("insuranceClaim"),requestDate,startDate,surveyor, object.optString("type"),inquiryModel,object.optString("status"));
        }else {
            return new RequestsModel();
        }
    }

    public QuotationModel parseQuotation(JSONObject object){

        if (object != null) {
           com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel inquiry = parseInquirySales(object.optJSONObject("inquiry"));

            CommanModel preparedBy = new CommanModel();
            if (checkFornull(object.optJSONObject("preparedBy"))){
                preparedBy.name = object.optJSONObject("preparedBy").optString("firstName") + " "+object.optJSONObject("preparedBy").optString("lastName");
            }

            String date = "";
            if (!object.optString("date").isEmpty()) {
                date = new Comman(ctx).getDate(new Long(object.optString("date")));
            }

            return new QuotationModel(object.optString("id"), date ,preparedBy,inquiry);
        }else {
            return new QuotationModel();
        }
    }

    public PackingItemModel parsePackingItem(JSONObject object){

        if (object != null) {

            String outwardDate = "";
            if (!object.optString("outwardDate").isEmpty()) {
                try {
                    outwardDate = new Comman(ctx).getDate(new Long(object.optString("outwardDate")));
                }catch (Exception e){

                }
            }

            String unloadingDate = "";
            if (!object.optString("unloadingDate").isEmpty()) {
                try {
                    unloadingDate = new Comman(ctx).getDate(new Long(object.optString("unloadingDate")));
                }catch (Exception e){

                }
            }

            return new PackingItemModel(object.optString("id"),object.optString("itemNo"),object.optString("crRef"),object.optString("article")
                    ,object.optString("quantity"),object.optString("value"),object.optString("packedBy"),object.optString("conditionAtOrigin")
                    ,object.optString("filePath"),object.optString("fileName"),object.optInt("loadingSequence"),object.optBoolean("loadingDone")
                    ,object.optBoolean("inwardDone"),object.optBoolean("outwardDone"),object.optBoolean("unloadingDone")
                    ,outwardDate ,unloadingDate);
        }else {
            return new PackingItemModel();
        }
    }

    public QuotationHistoryModel parseQuotationHistory(JSONObject object){

        if (object != null) {
            ArrayList<CommanModel> particulars = new ArrayList<>();
            JSONArray particular = object.optJSONArray("particulars");
            if (particular != null){
                for (int i = 0; i <particular.length() ; i++) {
                    JSONObject tmp = particular.optJSONObject(i);
                    particulars.add(new CommanModel(tmp.optJSONObject("item").optString("name") , tmp.optJSONObject("item").optString("price") ));
                }
            }
            String date = "";
            if (!object.optString("date").isEmpty()) {
                date = new Comman(ctx).getDate(new Long(object.optString("date")));
            }

            String inquiryDate = "";
            if (!object.optString("inquiryDate").isEmpty()) {
                inquiryDate = new Comman(ctx).getDate(new Long(object.optString("inquiryDate")));
            }

            return new QuotationHistoryModel(object.optString("id"),object.optString("addressLine1"),object.optString("addressLine2"),object.optString("cityName"),object.optString("clientContactNumber")
                    ,object.optString("clientEmail"),object.optString("clientName"),object.optString("companyName"),date,object.optString("fristName"),inquiryDate,object.optString("lastName"),object.optString("moveType"),object.optString("originAddressLine1"),object.optString("originAddressLine2"),object.optString("originCityName"),object.optString("originStateName")
                    ,object.optString("quotationVersion"),object.optString("stateName"),object.optString("total"),particulars);
        }else {
            return new QuotationHistoryModel();
        }
    }

    public AccountModel parseAccount(JSONObject object){
        if (object != null){
        AddressModel address = parseAddress(object.optJSONObject("address"));
        CommanModel industry = parseComman(object.optJSONObject("industry"));
        ManagerModel manager = parseManager(object.optJSONObject("manager"));
        ArrayList<ContactsModel> contacts = new ArrayList<>();

        JSONArray contact = object.optJSONArray("contacts");
        if (contact != null){
            for (int i = 0; i < contact.length(); i++) {
                contacts.add(parseContacts(contact.optJSONObject(i)));
            }
        }

            return new AccountModel(object.optString("id"),object.optString("companyName"),address,manager,contacts,object.optString("email"),object.optString("mobile"), object.optString("registrationCode"), object.optString("paymentTerms"), object.optString("gstinUin"), industry, object.optString("landlineNumber"));
        }else {
            return new AccountModel();
        }
    }

    ContactsModel parseContacts(JSONObject object){
        if (object != null){
            return new ContactsModel(object.optString("id"),object.optString("email"),object.optString("name"),object.optString("number"));
        }else{
            return new ContactsModel();
        }
    }

    public InquiryModel parseInquiry(JSONObject object){
        if (object != null){
            String account = "";
            if (checkFornull(object.optJSONObject("account"))){
                account = object.optJSONObject("account").optString("companyName");
            }
            ClientModel clientModel = parseShipper(object.optJSONObject("shipper"));
            String date = "";
            if (!object.optString("date").isEmpty()) {
                date = new Comman(ctx).getDate(new Long(object.optString("date")));
            }

            AddressModel address = parseAddress(object.optJSONObject("destinationAddress"));
            AddressModel addressOrg = parseAddress(object.optJSONObject("originAddress"));

            return  new InquiryModel(object.optString("id"),account,clientModel,date ,object.optString("moveType"),address,addressOrg,object.optString("contactPerson"),object.optString("goodsType"),object.optString("inquiryType"));
        }else{
            return new InquiryModel();
        }
    }

    public JobExecutionModel parseJobExecutions(JSONObject object){
        if (object != null){
             JobModel job = parseJob(object.optJSONObject("job"));
            String createdOn = "";
            if (!object.optString("createdOn").isEmpty() || !object.optString("createdOn").equalsIgnoreCase("null")) {
                createdOn = new Comman(ctx).getDate(new Long(object.optString("createdOn")));
            }

            return  new JobExecutionModel(object.optString("id"),job,object.optString("volume")
                    ,object.optString("status"),object.optString("requestLocation"),object.optString("jobProgrammer"),object.optString("packingMaterialTransactions")
                    ,object.optBoolean("packingMaterialAllocated"),object.optBoolean("packingMaterialReturned"),object.optBoolean("packingListCreated"),object.optBoolean("loadingSheetCreated")
                    ,object.optBoolean("inwardSheetCreated"),object.optBoolean("outwardSheetCreated"),object.optBoolean("outwardCompleted")
                    ,object.optBoolean("unloadingSheetCreated"),object.optBoolean("unloadingCompleted")
                    ,object.optBoolean("documentUploaded"),object.optString("branch"),createdOn);
        }else{
            return new JobExecutionModel();
        }
    }

    public JobModel parseJob(JSONObject object){
        if (object != null){
             com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel inquiry = parseInquiryOS(object.optJSONObject("inquiry"));
             AccountModel account = new AccountModel();
             ShipperModel shipper = parseShippers(object.optJSONObject("shipper"));
             CommanModel owningCoordinator = new CommanModel();
             CommanModel originCoordinator = new CommanModel();
             CommanModel destinationCoordinator = new CommanModel();

            com.ascent.pmrsurveyapp.SalesExecutive.Modals.ClientModel client = parseClientSales(object.optJSONObject("client"));
            AddressModel destinationAddress = parseAddress(object.optJSONObject("destinationAddress"));
            AddressModel originAddress = parseAddress(object.optJSONObject("originAddress"));

            if (checkFornull(object.optJSONObject("account"))){
                account.companyName = object.optJSONObject("account").optString("companyName");
                account.id = object.optJSONObject("account").optString("id");
            }

            if (checkFornull(object.optJSONObject("shipper"))){
                shipper.fullName = object.optJSONObject("shipper").optString("fullName");
                shipper.id = object.optJSONObject("shipper").optString("id");
            }

            String createdOn = "";
            if (!object.optString("createdOn").isEmpty()) {
                try{
                createdOn = new Comman(ctx).getDate(new Long(object.optString("createdOn")));
                }catch (Exception e){

                }
            }

            String updatedOn = "";
            if (!object.optString("updatedOn").isEmpty()) {
                try{
                createdOn = new Comman(ctx).getDate(new Long(object.optString("updatedOn")));
                }catch (Exception e){

                }
            }


            return  new JobModel(object.optString("id"),object.optString("obJobNumber"),object.optString("ibJobNumber"),object.optString("jobNumber")
                   ,inquiry,object.optString("inquiryDetail"),account,shipper,owningCoordinator,originCoordinator,destinationCoordinator
                    ,createdOn,updatedOn,originAddress,destinationAddress ,object.optString("status"),object.optString("moveType")
                    ,object.optString("jobAreaType"),object.optString("goodsType"),object.optBoolean("feedbackRequestSent")
                    ,object.optBoolean("dispatchDetailAdded"),object.optBoolean("storageDetailAdded"),object.optBoolean("insuranceValueAdded")
                    ,object.optBoolean("costSheetCreated"),object.optBoolean("costSheetUpdated"),object.optBoolean("claimRequestCreated")
                    ,object.optBoolean("invoiceGenerated"),object.optBoolean("shipmentAdviseCreated"),object.optBoolean("crewInstructionCreated")
                    ,object.optBoolean("jobExecRequestSent"),object.optBoolean("destinationJobExecRequestSent"),object.optBoolean("jobScheduled")
                    ,object.optBoolean("destinationJobScheduled"),object.optBoolean("insuranceRequestSent"),object.optBoolean("readyToDispatch")
                    ,object.optBoolean("destinationCoordinatorAssigned"),object.optBoolean("outwardSheetCreated"),object.optString("movingItemsVolume")
                    ,object.optString("ownershipType"));
        }else{
            return new JobModel();
        }
    }

    public ShipperModel parseShippers(JSONObject object){
        if (object != null){
            return  new ShipperModel(object.optString("id"),object.optString("firstName"),object.optString("lastName"),object.optString("fullName")
                    ,object.optString("contactNumber"),object.optString("alternateNumber"),object.optString("email"),object.optString("alternateEmail"),object.optString("designation"));
        }else{
            return new ShipperModel();
        }
    }


    public com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel parseInquiryOS(JSONObject object){
        if (object != null){
            CommanModel account = new CommanModel();
            CommanModel assignedTo = new CommanModel();
            CommanModel shipper = new CommanModel();
            com.ascent.pmrsurveyapp.SalesExecutive.Modals.ClientModel client = parseClientSales(object.optJSONObject("client"));
            AddressModel destinationAddress = parseAddress(object.optJSONObject("destinationAddress"));
            AddressModel originAddress = parseAddress(object.optJSONObject("originAddress"));

            if (checkFornull(object.optJSONObject("account"))){
                account.name = object.optJSONObject("account").optString("companyName");
                account.id = object.optJSONObject("account").optString("id");
            }

            if (checkFornull(object.optJSONObject("shipper"))){
                shipper.name = object.optJSONObject("shipper").optString("fullName");
                shipper.id = object.optJSONObject("shipper").optString("id");
            }

            if (checkFornull(object.optJSONObject("assignedTo"))) {
                assignedTo.name = object.optJSONObject("assignedTo").optString("firstName") + " " + object.optJSONObject("assignedTo").optString("lastName");
                assignedTo.id = object.optJSONObject("assignedTo").optString("id");
            }

            String date = "";
            if (!object.optString("inquiryDate").isEmpty()){
                try {
                    date = new Comman(ctx).getDate(new Long(object.optString("inquiryDate")));
                }catch (Exception e){

                }

            }


            return  new com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel(object.optString("id"),account,assignedTo,client ,destinationAddress,originAddress,object.optString("contactPerson"),date
                    ,object.optString("movementType"),object.optString("status") , shipper,object.optString("goodsType") , object.optBoolean("documentUploaded"));
        }else{
            return new com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel();
        }
    }

    public com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel parseInquirySales(JSONObject object){
        if (object != null){
            CommanModel account = new CommanModel();
            CommanModel assignedTo = new CommanModel();
            CommanModel shipper = new CommanModel();
            com.ascent.pmrsurveyapp.SalesExecutive.Modals.ClientModel client = parseClientSales(object.optJSONObject("client"));
            AddressModel destinationAddress = parseAddress(object.optJSONObject("destinationAddress"));
            AddressModel originAddress = parseAddress(object.optJSONObject("originAddress"));

            if (checkFornull(object.optJSONObject("account"))){
                account.name = object.optJSONObject("account").optString("companyName");
                account.id = object.optJSONObject("account").optString("id");
            }

            if (checkFornull(object.optJSONObject("shipper"))){
                shipper.name = object.optJSONObject("shipper").optString("fullName");
                shipper.id = object.optJSONObject("shipper").optString("id");
            }

            if (checkFornull(object.optJSONObject("assignedTo"))) {
                assignedTo.name = object.optJSONObject("assignedTo").optString("firstName") + " " + object.optJSONObject("assignedTo").optString("lastName");
                assignedTo.id = object.optJSONObject("assignedTo").optString("id");
            }

            String date = "";
            if (!object.optString("inquiryDate").isEmpty()){
                try {
                    date = new Comman(ctx).getDate(new Long(object.optString("inquiryDate")));
                }catch (Exception e){

                }

            }


            return  new com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel(object.optString("id"),account,assignedTo,client ,destinationAddress,originAddress,object.optString("contactPerson"),date
                    ,object.optString("moveType"),object.optString("status") , shipper,object.optString("goodsTypeValue") , object.optBoolean("documentUploaded"));
        }else{
            return new com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel();
        }
    }

    public SuggestionsModel parseSuggestion(JSONObject object){
        if (object != null){
            return  new SuggestionsModel(object.optInt("id"),object.optString("name"),object.optInt("height"),object.optInt("length"),object.optInt("volume"),object.optInt("width"));
        }else{
            return new SuggestionsModel();
        }
    }

    public com.ascent.pmrsurveyapp.SalesExecutive.Modals.ClientModel parseClientSales(JSONObject object){
        if (object != null){
            AddressModel address = parseAddress(object.optJSONObject("address"));
            return  new com.ascent.pmrsurveyapp.SalesExecutive.Modals.ClientModel(object.optString("name"),object.optString("email"),object.optString("contactNumber"),address);
        }else{
            return new com.ascent.pmrsurveyapp.SalesExecutive.Modals.ClientModel();
        }
    }

    public ClientModel parseClient(JSONObject object){
        if (object != null){
           return  new ClientModel(object.optString("id"),object.optString("contactNumber"),object.optString("email"),object.optString("name"));
        }else{
            return new ClientModel();
        }
    }

    public ClientModel parseShipper(JSONObject object){
        if (object != null){
            return  new ClientModel(object.optString("id"),object.optString("contactNumber"),object.optString("email"),object.optString("fullName"));
        }else{
            return new ClientModel();
        }
    }


    public CommanModel parseComman(JSONObject object){
        if (object != null){
            return  new CommanModel(object.optString("id"),object.optString("name"));
        }else{
            return new CommanModel();
        }
    }

    public AddressModel parseAddress(JSONObject object){
        if (object != null){
            CommanModel city = parseComman(object.optJSONObject("city"));
            CommanModel state = parseComman(object.optJSONObject("state"));
            return  new AddressModel(object.optString("addressLine1"),object.optString("addressLine2"),object.optString("area"),city,state,object.optString("pinCode"));
        }else{
            return new AddressModel();
        }
    }

    public ManagerModel parseManager(JSONObject object){
        if (object != null){
            return  new ManagerModel(object.optString("id"),object.optString("firstName"),object.optString("lastName"),object.optString("email"),object.optString("mobile"),object.optString("officeNumber"),object.optString("salutation"));
        }else{
            return new ManagerModel();
        }
    }

/*
    public TerritoryModel parseTreeitory (JSONObject object){
        if (object!= null)
            return new TerritoryModel(object.optString("id"),object.optString("name"),object.optBoolean("active"));
        else  return new TerritoryModel();
    }



    public ItemsModel parseItem(JSONObject object){
        if (object != null){
            ItemsCategoryModel categoryModel = new ItemsCategoryModel();
            if (checkFornull(object.optJSONObject("category"))){
                categoryModel.id =  object.optJSONObject("category").optString("id");
                categoryModel.name =  object.optJSONObject("category").optString("name");
                if (checkFornull(object.optJSONObject("category").optJSONObject("parent"))) {
                    categoryModel.parent.id =  object.optJSONObject("category").optJSONObject("parent").optString("id");
                    categoryModel.name =  object.optJSONObject("category").optJSONObject("parent").optString("name");
                }
            }
            Double pr = object.optDouble("companyPrice");
            if (pr.isNaN()){
                pr = 0.0;
            }
            return  new ItemsModel(object.optString("id"),object.optString("name"),object.optString("modelNumber"),object.optDouble("price") ,pr,object.optString("encodedString"),object.optString("description"),object.optString("unit"),categoryModel);
        }else{
            return new ItemsModel();
        }
    }

    public AdminModel parseRetailerAdmin(JSONObject obj) {
        if (obj != null)
            return new AdminModel(obj.optString("id"),obj.optString("userName"),obj.optString("firstName"),obj.optString("lastName"),obj.optString("fullName"),obj.optString("email"),obj.optString("mobile"),obj.optString("alternateMobile"),obj.optString("gender"),obj.optString("assignedRole"));
        else return new AdminModel();
    }
    public ArrayList<AttachmentModel> parseKYCDocs(JSONArray array){
        ArrayList<AttachmentModel> docList = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    LayoutInflater inflater = LayoutInflater.from(ctx);
                    final View doc = inflater.inflate(R.layout.docs_row, null);
                    TextInputEditText etDocName = doc.findViewById(R.id.etDocName);
                    TextInputEditText aadhar = doc.findViewById(R.id.etAadharnumber);
                    etDocName.setText(object.optString("fileName"));
                    aadhar.setText(object.optString("number"));
                    Spinner sp = doc.findViewById(R.id.spDoc);
                    if (object.optString("fileName").contains("AADHAR_CARD")) {
                        sp.setSelection(0);
                    } else {
                        sp.setSelection(1);
                    }
                    etDocName.setText(object.optString("fileName"));
                    docList.add(new AttachmentModel(object.optString("name"), object.optString("fileName"), doc, object.optString("fileUrl")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return docList;
    }

    public RetailerModel parseRetailer(JSONObject obj){
        if (obj != null){
            CommanModel region = new CommanModel();
            CommanModel area = new CommanModel();
            CommanModel city = new CommanModel();
            CommanModel locality = new CommanModel();
            CommanModel pincode = new CommanModel();
            ArrayList<AttachmentModel> docList = new ArrayList<>();
            if (obj.optJSONObject("region") != null) {
                region = parseCommanModal(obj.optJSONObject("region"));
            }
            if (obj.optJSONObject("area") != null) {
                area = parseCommanModal(obj.optJSONObject("area"));
            }
            if (obj.optJSONObject("city") != null) {
                city = parseCommanModal(obj.optJSONObject("city"));
            }
            if (obj.optJSONObject("locality") != null) {
                locality = parseCommanModal(obj.optJSONObject("locality"));
            }

            if (obj.optJSONObject("pincode") != null) {
                pincode = new CommanModel(obj.optJSONObject("pincode").optInt("id"),obj.optJSONObject("pincode").optString("pincode") , "");
            }

            if (obj.optJSONObject("kycDocuments") != null) {
                docList = parseKYCDocs(obj.optJSONArray("kycDocuments"));
            }

            AdminModel admin = new AdminModel();
            if (obj.optJSONObject("retailerAdmin") != null){
                admin = parseRetailerAdmin(obj.optJSONObject("retailerAdmin"));
            }
            return new RetailerModel(obj.optString("id"),obj.optString("name"),obj.optString("gstNo"),obj.optString("contactPerson"),obj.optString("email"),obj.optString("mobile"),obj.optString("alternateMobile"),obj.optString("gender"),obj.optString("status")
                    ,obj.optString("code"),obj.optString("totalSaleParts"),obj.optString("shopSize"),obj.optString("tin"),obj.optString("addressLine1"),obj.optString("addressLine2"),admin,region,area,city,locality,pincode,docList);
        }else{
            return new RetailerModel();
        }
    }

    public AddressModel parseAddress(JSONObject obj) {
        if (obj != null){
            CommanModel city = new CommanModel(obj.optJSONObject("city").optInt("id"),obj.optJSONObject("city").optString("name") , "");
            CommanModel state = new CommanModel(obj.optJSONObject("state").optInt("id"),obj.optJSONObject("state").optString("name") , "");
            return new AddressModel(obj.optString("id"),obj.optString("addressLine1"),obj.optString("addressLine2"),obj.optString("area"),obj.optString("pinCode"), state , city);
        }else{
            return new AddressModel();
        }
    }


    /*
    public ItemsCategoryModel parseSubCategory(JSONObject object){
            ItemsCategoryModel categoryModel = new ItemsCategoryModel();
                categoryModel.id =  object.optString("id");
                categoryModel.name =  object.optString("name");
                if (checkFornull(object.optJSONObject("parent"))) {
                    categoryModel.parent.id =  object.optJSONObject("parent").optString("id");
                    categoryModel.parent.name =  object.optJSONObject("parent").optString("name");
                }
            return  categoryModel;
    }
    public CommanModel parseCommanModal(JSONObject object){
        return new CommanModel(object.optInt("id"),object.optString("name") , "");
    }

    public ItemsCategoryModel parseItemCategory(JSONObject object){
        ItemsParentModel parent = new ItemsParentModel();
        if (object.optJSONObject("parent") != null) {
         parent = parseItemCategoryParent(object.optJSONObject("parent"));
        }
//        ItemsCategoryModel itemsCategoryModel = new ItemsCategoryModel();
//        if (object.optBoolean("active") != null) {
//            parent = parseItemCategoryParent(object.optJSONObject("parent"));
//        }
        return new ItemsCategoryModel(object.optString("id"),object.optString("name"),parent ,object.optBoolean("active"));
    }
    public ItemsParentModel parseItemCategoryParent(JSONObject object){
        return new ItemsParentModel(object.optString("id"),object.optString("name") ,object.optBoolean("active"));
    }



    public RetailerApprovalModel parseRetailerApprovel(JSONObject object){
        RetailerModel retailerModel = parseRetailer(object.optJSONObject("retailer"));
        DistributorModel distributorModel = parseDistributor(object.optJSONObject("distributor"));

        return new RetailerApprovalModel(object.optString("id"),distributorModel,retailerModel,object.optString("status"),object.optString("comments"));
    }

    public PaymentModel parsePayment(JSONObject object){
        return new PaymentModel(object.optString("id"),object.optString("paymentDate"),object.optString("mode"),object.optString("instrumentNumber"),object.optString("bankName"),object.optInt("totalAmount"),object.optInt("paidAmount"),object.optInt("dueAmount"),object.optString("comments"),object.optString("fileName"),object.optString("filePath"),object.optString("orderId"));
    }
    public InvoiceModel parseInvoice(JSONObject object){
        return new InvoiceModel(object.optString("id"),object.optString("invoiceDate"),object.optString("invoiceNumber"),object.optString("status"));
    }

    public BrandModel parseBrand(JSONObject object){
        AddressModel address = parseAddress(object.optJSONObject("address"));
        AdminModel admin = parseRetailerAdmin(object.optJSONObject("brandAdmin"));
        return new BrandModel(object.optString("id"),object.optString("name"),object.optString("contactPerson"),object.optString("email"),object.optString("mobile"),object.optString("alternateMobile"),address,admin);
    }

    public OrderItemsModel parseOrderItems(JSONObject object){
        ItemsModel itemsModel = parseItem(object.optJSONObject("product"));
        return new OrderItemsModel(object.optString("id"),object.optDouble("quantity"),object.optDouble("amount"),object.optDouble("actualPrice"),object.optDouble("saving"),itemsModel);
    }

    public PrivilagesModel parsePrivilages(JSONObject object){
        return new PrivilagesModel(object.optString("id"),object.optString("name"),object.optString("displayName"),object.optString("requiredStatus"));
    }
    public OrdersModel parseOrder(JSONObject object){
        RetailerModel retailerModel = parseRetailer(object.optJSONObject("retailer"));
        DistributorModel distributorModel = new DistributorModel();
        if (object.optJSONObject("distributor") != null){
            distributorModel  = parseDistributor(object.optJSONObject("distributor"));
        }
        PaymentModel paymentModel = new PaymentModel();
        if (object.optJSONObject("payment") != null){
            paymentModel = parsePayment(object.optJSONObject("payment"));
        }
        InvoiceModel invoiceModel = new InvoiceModel();
        if (object.optJSONObject("invoice") != null){
            invoiceModel = parseInvoice(object.optJSONObject("invoice"));
        }
        BrandModel brand = new BrandModel();
        if (object.optJSONObject("brand") != null){
            brand  = parseBrand(object.optJSONObject("brand"));
        }
        AdminModel createdBy = new AdminModel();
        if (object.optJSONObject("createdBy") != null){
            createdBy = parseRetailerAdmin(object.optJSONObject("createdBy"));
        }
        ArrayList<OrderItemsModel> orderItemsdata = new ArrayList<>();
        if (object.optJSONArray("orderItems") != null){
            JSONArray orderItems = object.optJSONArray("orderItems");
            for (int i=0;i<orderItems.length();i++){
                orderItemsdata.add(parseOrderItems(orderItems.optJSONObject(i)));
            }
        }
        return new OrdersModel(object.optString("id"),distributorModel,retailerModel,object.optString("status"),object.optString("comments"),object.optString("orderDate"),object.optString("orderNumber"),paymentModel,invoiceModel,brand,orderItemsdata,object.optString("createdOn"),createdBy,object.optString("type"),object.optInt("totalAmount"),object.optInt("paidAmount"),parseOrderHistoryItems(object.optJSONArray("orderHistory")),object.optInt("totalItem") ,object.optString("tenant") );
    }


    public ArrayList<OrdersHistoryModel> parseOrderHistoryItems(JSONArray array){
        ArrayList<OrdersHistoryModel> orderItemsdata = new ArrayList<>();
        if (array != null){
            for (int i = 0; i <array.length() ; i++) {
                JSONObject object = array.optJSONObject(i);
                AdminModel createdBy = parseRetailerAdmin(object.optJSONObject("createdBy"));
                orderItemsdata.add(new OrdersHistoryModel(object.optString("id"),object.optString("status"),object.optString("comments"),object.optString("createdOn"),createdBy));
            }
        }
        return orderItemsdata;
    }

    public DispatchDetailsModel parseDispatchDetail(JSONObject object){
        String billingDate = "";
        if (!object.optString("billingDate").isEmpty()) {
            billingDate = new Comman(ctx).getDate(new Long(object.optString("billingDate")));
        }
        String dispatchDate = "";
        if (!object.optString("dispatchDate").isEmpty()) {
            dispatchDate = new Comman(ctx).getDate(new Long(object.optString("dispatchDate")));
        }
        return new DispatchDetailsModel(object.optInt("id") , billingDate , dispatchDate, object.optString("invoiceNumber"), object.optString("customer"), object.optString("city"), object.optString("state"), object.optString("transporter"), object.optString("docket"), object.optString("salesGroup"));
    }

    public InventoryModel parseInventory(JSONObject object){
        return new InventoryModel(object.optInt("id") , object.optString("plant") , object.optString("material"), object.optString("description"), object.optString("category"), object.optString("itemType"),
                object.optString("subType"), object.optString("type"), object.optString("unitOfMeasure"),
                object.optString("stocks"), object.optString("classification"), object.optString("status52"), object.optString("year"));
    }


    public DeliveryReportModel parseDeliveryReport(JSONObject object){

        String materialAvailabilityDate = "";
        if (!object.optString("materialAvailabilityDate").isEmpty()) {
            materialAvailabilityDate = new Comman(ctx).getDate(new Long(object.optString("materialAvailabilityDate")));
        }


        return new DeliveryReportModel(object.optInt("id") , object.optString("sapOrder") , materialAvailabilityDate, object.optString("referenceDocument"), object.optString("sellingPartyCode")
                , object.optString("po"),
                object.optString("sellingParty"), object.optString("shippingParty"), object.optString("material"),
                object.optString("quantity"), object.optString("measureUnit"), object.optString("value"), object.optString("orderReason"), object.optString("salesGroup"), object.optString("region"), object.optString("productType"));
    }

    public InvoiceReportModel parseInvoiceReport(JSONObject object){

        String billingDate = "";
        if (!object.optString("billingDate").isEmpty()) {
            billingDate = new Comman(ctx).getDate(new Long(object.optString("billingDate")));
        }


        return new InvoiceReportModel(object.optInt("id") , object.optString("material") , object.optString("salesGroup"), object.optString("region"), object.optString("salesOffice")
                , object.optString("billingDocument"),
                billingDate, object.optString("payer"), object.optString("billingCustomer"),
                object.optString("shippingCustomer"), object.optString("city"), object.optString("state"), object.optString("billedQuantity"), object.optString("billingType"), object.optString("netValue"), object.optString("taxAmount"));
    }


    public OpenOrderModel parseOpenOrdersReport(JSONObject object){

        String documentDate = "";
        if (!object.optString("documentDate").isEmpty()) {
            documentDate = new Comman(ctx).getDate(new Long(object.optString("documentDate")));
        }


        return new OpenOrderModel(object.optInt("id") , object.optString("material") , object.optString("salesGroup"), object.optString("region"), object.optString("plant")
                , object.optString("salesDocument"),
                documentDate, object.optString("purchaseOrderOn"), object.optString("soldToParty"),
                object.optString("name1"), object.optString("confirmedQuantity"), object.optString("openOrdersQuantity"), object.optString("openOrders"), object.optString("salesDocumentType"), object.optString("orderReason"), object.optString("product"));
    }
    */
    public boolean checkFornull(JSONObject obj){
        return (obj != null) ? true : false;
    }
    public boolean checkFornull(JSONArray obj){
        return (obj != null) ? true : false;
    }
    public boolean checkFornull(String obj){
        return (obj != null) ? true : false;
    }

    public boolean checkFornull(Double obj){
        return (obj != null) ? true : false;
    }
}
