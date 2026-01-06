package com.ascent.pmrsurveyapp.SalesExecutive.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.ascent.pmrsurveyapp.Utills.PDFTools;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.ArticalModel;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.ActivityAccountDetailsBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityInquiryDetailsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InquiryDetails extends AppCompatActivity {

    Comman cmn;
    Activity mActivity;
    ActivityInquiryDetailsBinding binding;
    String id = "";
    Boolean isSurveyReport = false;
    ArrayList<View> movingItemsRoad = new ArrayList<>();
    ArrayList<View> movingItemsSEA = new ArrayList<>();
    ArrayList<View> movingItemsAir = new ArrayList<>();
    ArrayList<View> movingItemsRail = new ArrayList<>();

    int totalVolumeRoad = 0,totalVolumeSEA = 0,totalVolumeAir = 0,totalVolumeRail = 0;
    int totalWeightRoad = 0,totalWeightSEA = 0,totalWeightAir = 0,totalWeightRail = 0;
    int totalItemsRoad = 0,totalItemsSEA = 0,totalItemsAir = 0,totalItemsRail = 0;

    int movePosition = 0;
    int movePositionMoveType = 0;

    String surveyId;


    JSONObject details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inquiry_details);

        mActivity = this;
        cmn = new Comman(mActivity);

        id = getIntent().getStringExtra("id");
        isSurveyReport = getIntent().getBooleanExtra("isSurveyReport" , false);

        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateData(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        binding.sagmentedGroupMovingItems.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(final int position) {
                // Handle stuff here
                Log.e("position" , ""+ position);
                movePosition = position;
                if (position== 0){
                    binding.laysurveyArticalHolderMoving.setVisibility(View.VISIBLE);
                    binding.surveyArticalHolderNotMoving.setVisibility(View.GONE);
                    binding.surveyArticalHolderMayBeMoving.setVisibility(View.GONE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(600)
                            .playOn(binding.surveyArticalHolderMoving);
                }else if (position== 1){
                    binding.laysurveyArticalHolderMoving.setVisibility(View.GONE);
                    binding.surveyArticalHolderNotMoving.setVisibility(View.VISIBLE);
                    binding.surveyArticalHolderMayBeMoving.setVisibility(View.GONE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(600)
                            .playOn(binding.surveyArticalHolderNotMoving);
                }else if (position== 2){
                    binding.laysurveyArticalHolderMoving.setVisibility(View.GONE);
                    binding.surveyArticalHolderNotMoving.setVisibility(View.GONE);
                    binding.surveyArticalHolderMayBeMoving.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(600)
                            .playOn(binding.surveyArticalHolderMayBeMoving);
                }
            }
        });

        binding.sagmentedGroupMoveType.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(final int position) {
                // Handle stuff here
                Log.e("position" , ""+ position);
                movePositionMoveType = position;
                resetMovingItems(position);
            }
        });

        if (isSurveyReport){
            CollapsingToolbarLayout.LayoutParams layoutParams = new CollapsingToolbarLayout.LayoutParams(CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT, CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 0);
            binding.marginLayout.setLayoutParams(layoutParams);
            binding.btExportSurvey.setVisibility(View.VISIBLE);
            binding.cardNewInquiries.setVisibility(View.GONE);
           // binding.toolbarMain.setVisibility(View.GONE);
            binding.tablayout.setVisibility(View.GONE);
            binding.btMoreInfo.setVisibility(View.GONE);
            binding.title.setText("Survey Details");

            binding.htabCollapseToolbar.setMinimumHeight(100);

           // getData();
            getSurveyData();
            binding.layContacts.setVisibility(View.GONE);
            binding.layAdditionalDetails.setVisibility(View.GONE);
            binding.laySurveyDetails.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn)
                    .duration(600)
                    .playOn(binding.laySurveyDetails);
        }else{
            getTabItems();
        }
        binding.btMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailDialog();
            }
        });


        ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        binding.btExportSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getXLData();
                int req1 =  ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
                int req2 =  ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (req1 != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    return;
                }

                if (req2 != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return;
                }
                String url = HttpRequest.pdfDownloadUrl+"surveyreport/export?id="+surveyId;
                Log.e("pdf url = " , url);
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                PDFTools.showPDFUrl(InquiryDetails.this, url , surveyId);
            }
        });

    }




    void showDetailDialog(){
        if (details != null){

            final Dialog d = new Dialog(mActivity);
            d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFade;
            d.setContentView(R.layout.lay_inquiry_details);
            TextView enquiryDate = d.findViewById(R.id.enquiryDate);
            TextView enquiryType = d.findViewById(R.id.enquiryType);
            TextView movementType = d.findViewById(R.id.movementType);
            TextView goodsType = d.findViewById(R.id.goodsType);
            TextView enquirySource = d.findViewById(R.id.enquirySource);
            TextView contactPerson = d.findViewById(R.id.contactPerson);
            TextView account = d.findViewById(R.id.account);
            TextView shipper = d.findViewById(R.id.shipper);
            TextView shipperEmail = d.findViewById(R.id.shipperEmail);
            TextView shipperContactNo = d.findViewById(R.id.shipperContactNo);
            TextView shipperAlternateEmail = d.findViewById(R.id.shipperAlternateEmail);
            TextView shipperAlternateNumber = d.findViewById(R.id.shipperAlternateNumber);
            TextView shipperDesignation = d.findViewById(R.id.shipperDesignation);
            TextView serviceType = d.findViewById(R.id.serviceType);
            TextView deliveryType = d.findViewById(R.id.deliveryType);
            TextView expectedMovementDate = d.findViewById(R.id.expectedMovementDate);
            TextView originAddress = d.findViewById(R.id.originAddress);
            TextView destinationAddress = d.findViewById(R.id.destinationAddress);


            enquiryDate.setText("" + cmn.getDate(details.optLong("inquiryDate")));
            enquiryType.setText(""+details.optString("inquiryType"));
            contactPerson.setText(""+cmn.replaceNull(details.optString("contactPerson")));
            movementType.setText(""+details.optString("movementType"));
            goodsType.setText(""+details.optString("goodsTypeValue"));
            enquirySource.setText(""+details.optString("source"));
            if (details.optJSONObject("account") == null){
                account.setText("individual");
            }else{
                account.setText("" + details.optJSONObject("account").optString("companyName"));
            }
            shipper.setText("" + details.optJSONObject("shipper").optString("fullName"));
            shipperEmail.setText("" + details.optJSONObject("shipper").optString("email"));
            shipperContactNo.setText("" + details.optJSONObject("shipper").optString("contactNumber"));
            shipperAlternateEmail.setText("" + cmn.replaceNull(details.optJSONObject("shipper").optString("alternateEmail")));
            shipperAlternateNumber.setText("" + cmn.replaceNull(details.optJSONObject("shipper").optString("alternateNumber")));
            shipperDesignation.setText("" + cmn.replaceNull(details.optJSONObject("shipper").optString("designation")));
            serviceType.setText(""+details.optString("serviceType"));
            deliveryType.setText(""+details.optString("deliveryType"));
            expectedMovementDate.setText("" + cmn.getDate(details.optLong("expectedMovementDate")));


            originAddress.setText("" + details.optJSONObject("originAddress").optString("addressLine1") + ","  + "\n" + details.optJSONObject("originAddress").optJSONObject("state").optString("name") + "," + details.optJSONObject("originAddress").optJSONObject("city").optString("name")  + "\n" + cmn.replaceNull(details.optJSONObject("originAddress").optString("pinCode"))+ "," + details.optJSONObject("originAddress").optJSONObject("country").optString("name"));
            destinationAddress.setText("" + cmn.replaceNull(details.optJSONObject("destinationAddress").optString("addressLine1")) + "," + "\n" + details.optJSONObject("destinationAddress").optJSONObject("state").optString("name") + "," + details.optJSONObject("destinationAddress").optJSONObject("city").optString("name") + "\n" +cmn.replaceNull( details.optJSONObject("destinationAddress").optString("pinCode"))+ "," + details.optJSONObject("destinationAddress").optJSONObject("country").optString("name"));



            Button btClose =  d.findViewById(R.id.btClose);
            btClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            d.show();
            d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }else{
            cmn.showToast("Unable to fetch details..");
        }

    }


    void updateData(String tabName){

        cmn.printLog("tab selected "+ tabName);

        if (tabName.equalsIgnoreCase("ACTIVITY HISTORY")){
            binding.layContacts.setVisibility(View.VISIBLE);
            binding.layAdditionalDetails.setVisibility(View.GONE);
            binding.laySurveyDetails.setVisibility(View.GONE);
            YoYo.with(Techniques.FadeIn)
                    .duration(600)
                    .playOn(binding.layContacts);
        }else if (tabName.equalsIgnoreCase("SURVRY REPORT")){
            binding.layContacts.setVisibility(View.GONE);
            binding.layAdditionalDetails.setVisibility(View.VISIBLE);
            binding.laySurveyDetails.setVisibility(View.GONE);
            YoYo.with(Techniques.FadeIn)
                    .duration(600)
                    .playOn(binding.layAdditionalDetails);
        }else{
            binding.layContacts.setVisibility(View.GONE);
            binding.layAdditionalDetails.setVisibility(View.GONE);
            binding.laySurveyDetails.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn)
                    .duration(600)
                    .playOn(binding.laySurveyDetails);
        }
    }

    void getData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject obj = new JSONObject(aResponse);
                        details = obj;
                        try {
                            if (obj.optJSONObject("account") != null){
                                binding.tvClient.setText("" + obj.optJSONObject("account").optString("companyName"));
                            }else{
                                binding.tvClient.setText("individual");
                            }
                            binding.tvExpMovdate.setText("" + cmn.getDate(obj.optLong("expectedMovementDate")));
                            binding.tvMoveType.setText("" + obj.optString("movementType"));
                            binding.tvContactPerson.setText("" + cmn.replaceNull(obj.optString("contactPerson")));
                            binding.tvContactNo.setText("" + obj.optJSONObject("shipper").optString("contactNumber"));
                            binding.tvEmail.setText("" + obj.optJSONObject("shipper").optString("email"));
                            binding.tvOriginAddress.setText("" + obj.optJSONObject("originAddress").optString("addressLine1") + ","  + "\n" + obj.optJSONObject("originAddress").optJSONObject("state").optString("name") + "," + obj.optJSONObject("originAddress").optJSONObject("city").optString("name")  + "\n" + cmn.replaceNull(obj.optJSONObject("originAddress").optString("pinCode"))+ "," + obj.optJSONObject("originAddress").optJSONObject("country").optString("name"));
                            binding.tvDestiAddress.setText("" + cmn.replaceNull(obj.optJSONObject("destinationAddress").optString("addressLine1")) + "," + "\n" + obj.optJSONObject("destinationAddress").optJSONObject("state").optString("name") + "," + obj.optJSONObject("destinationAddress").optJSONObject("city").optString("name") + "\n" +cmn.replaceNull( obj.optJSONObject("destinationAddress").optString("pinCode"))+ "," + obj.optJSONObject("destinationAddress").optJSONObject("country").optString("name"));
                            setDetails(obj);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                {
                    Toast.makeText(
                            mActivity,
                            "Not Got Response From Server.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        String url = "";
        url = "inquiry/"+id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(true);
    }


    void getSurveyData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject obj = new JSONObject(aResponse);

                        surveyId = obj.optString("id");

                        try {
                            setSurveyDetails(obj);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                {
                    Toast.makeText(
                            mActivity,
                            "Not Got Response From Server.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        String url = "";
        if (isSurveyReport){
            url = "surveyreport/"+id;
        }else{
            url = "surveyreport?Find=ByInquiry&inquiryId="+id;
        }
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(true);
    }

    void getTabItems(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject obj = new JSONObject(aResponse);
                        try {
                            if (obj.optBoolean("surveyReport") == true){
                                binding.tablayout.addTab(binding.tablayout.newTab().setText("Activity History"));
                                binding.tablayout.addTab(binding.tablayout.newTab().setText("Survey Report"));
                                getData();
                                getSurveyData();
                                binding.btExportSurvey.setVisibility(View.VISIBLE);
                                getActivityHistoryData();
                            }else{
                                binding.tablayout.addTab(binding.tablayout.newTab().setText("Activity History"));
                                getData();
                                getActivityHistoryData();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                {
                    Toast.makeText(
                            mActivity,
                            "Not Got Response From Server.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        String url = "";
        url = "inquiry/"+id+"?Find=GetInquiryData";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(true);
    }

    void setSurveyDetails(JSONObject details){
        JSONObject inquiryDetail = details.optJSONObject("inquiryDetail");
        binding.etSurveyDate.setText(""+cmn.getDate(inquiryDetail.optLong("surveyDate")));
        String packingDate =  cmn.getDate(inquiryDetail.optLong("packingDate"));
        String packingTime =  inquiryDetail.optString(  "packingTime");
        binding.etDateofPacking.setText(""+packingDate);
        binding.etTimeofPacking.setText(""+packingTime);

        binding.etMovingDate.setText(""+cmn.getDate(inquiryDetail.optLong("movingDate")));
        binding.etsurveyor.setText(""+inquiryDetail.optJSONObject("surveyor").optString("fullName"));
        binding.etMode.setText(""+inquiryDetail.optString("mode"));
        binding.etLoad.setText(""+inquiryDetail.optString("containerLoad"));
        binding.insurance.setText(""+inquiryDetail.optString("insuranceBy"));
        binding.insurance.setChecked(true);

        JSONObject originAddressDetail = inquiryDetail.optJSONObject("originAddressDetail");
        binding.etResidenceSize.setText(""+originAddressDetail.optString("residenceSize"));
        binding.etElevatorFloor.setText(""+originAddressDetail.optString("floor"));

        if (originAddressDetail.optBoolean("longCarry")){
            binding.rbLongCarry.setChecked(true);
            binding.rbLongCarry.setText("Yes");
            binding.layLongCarryDistance.setVisibility(View.VISIBLE);
            binding.etLongCarryDistance.setText(""+originAddressDetail.optString("longCarryDistance"));
            binding.etLongCarryDistanceUnit.setText(""+originAddressDetail.optString("longCarryDistanceUnit"));
        }else{
            binding.rbLongCarry.setChecked(false);
            binding.rbLongCarry.setText("No");
            binding.layLongCarryDistance.setVisibility(View.GONE);
        }
        if (originAddressDetail.optBoolean("stairCarry")){
            binding.rbStairCarry.setChecked(true);
            binding.rbStairCarry.setText("Yes");
        }else{
            binding.rbStairCarry.setChecked(false);
            binding.rbStairCarry.setText("No");
        }

        if (originAddressDetail.optBoolean("additionalStop")){
            binding.rbAdditionalStop.setChecked(true);
            binding.rbAdditionalStop.setText("Yes");
            binding.layNumberofStops.setVisibility(View.VISIBLE);
            binding.etNumberofStops.setText(""+originAddressDetail.optString("numberOfStops"));
        }else{
            binding.rbAdditionalStop.setChecked(false);
            binding.rbAdditionalStop.setText("No");
            binding.layNumberofStops.setVisibility(View.GONE);
        }

        if (originAddressDetail.optBoolean("parkingRequirements")){
            binding.rbParkingRequirements.setChecked(true);
            binding.rbParkingRequirements.setText("Yes");
        }else{
            binding.rbParkingRequirements.setChecked(false);
            binding.rbParkingRequirements.setText("No");
        }

        if (originAddressDetail.optBoolean("externalElevator")){
            binding.rbExternalElevator.setChecked(true);
            binding.rbExternalElevator.setText("Yes");
        }else{
            binding.rbExternalElevator.setChecked(false);
            binding.rbExternalElevator.setText("No");
        }

        if (originAddressDetail.optBoolean("shuttle")){
            binding.rbShuttle.setChecked(true);
            binding.rbShuttle.setText("Yes");
            binding.layShuttleDistance.setVisibility(View.VISIBLE);
            binding.etShuttleDistance.setText(""+originAddressDetail.optString("shuttleDistance"));
            binding.etShuttleDistanceUnit.setText(""+originAddressDetail.optString("shuttleDistanceUnit"));
        }else{
            binding.rbShuttle.setChecked(false);
            binding.rbShuttle.setText("No");
            binding.layShuttleDistance.setVisibility(View.GONE);
        }

        binding.etAccessNotes.setText(""+cmn.replaceNull(originAddressDetail.optString("accessNotes")));

        JSONObject destinationAddressDetail = inquiryDetail.optJSONObject("destinationAddressDetail");

        if (destinationAddressDetail == null){
            binding.etResidenceSizeDestination.setText("NA");
            binding.etElevatorFloorDestination.setText("NA");
            binding.etElevatorFloorDestination.setFocusable(false);


            binding.rbLongCarryDestination.setChecked(true);
            binding.rbLongCarryDestination.setText("NA");
            binding.layLongCarryDistanceDestination.setVisibility(View.GONE);

            binding.rbStairCarryDestination.setChecked(true);
            binding.rbStairCarryDestination.setText("NA");

            binding.rbAdditionalStopDestination.setChecked(true);
            binding.rbAdditionalStopDestination.setText("NA");
            binding.layNumberofStopsDestination.setVisibility(View.GONE);

            binding.rbParkingRequirementsDestination.setChecked(true);
            binding.rbParkingRequirementsDestination.setText("NA");

            binding.rbExternalElevatorDestination.setChecked(true);
            binding.rbExternalElevatorDestination.setText("NA");

            binding.rbShuttleDestination.setChecked(true);
            binding.rbShuttleDestination.setText("NA");
            binding.layShuttleDistanceDestination.setVisibility(View.GONE);

            binding.etAccessNotesDestination.setText("");

        }else{

            if (destinationAddressDetail.optString("residenceSize") == null){
                binding.etResidenceSizeDestination.setText("NA");
            } else {
                binding.etResidenceSizeDestination.setText(""+cmn.replaceNull(destinationAddressDetail.optString("residenceSize")));
            }

            if (destinationAddressDetail.optString("floor") == null){
                binding.etElevatorFloorDestination.setText("NA");
            } else {
                binding.etElevatorFloorDestination.setText(""+cmn.replaceNull(destinationAddressDetail.optString("floor")));
            }

            binding.etElevatorFloorDestination.setFocusable(false);

            if (destinationAddressDetail.optBoolean("longCarry")){
                binding.rbLongCarryDestination.setChecked(true);
                binding.rbLongCarryDestination.setText("Yes");
                binding.layLongCarryDistanceDestination.setVisibility(View.VISIBLE);
                binding.etLongCarryDistanceDestination.setText(""+destinationAddressDetail.optString("longCarryDistance"));
                binding.etLongCarryDistanceUnitDestination.setText(""+destinationAddressDetail.optString("longCarryDistanceUnit"));
            }else{
                binding.rbLongCarryDestination.setChecked(true);
                binding.rbLongCarryDestination.setText("No");
                binding.layLongCarryDistanceDestination.setVisibility(View.GONE);
            }
            if (destinationAddressDetail.optBoolean("stairCarry")){
                binding.rbStairCarryDestination.setChecked(true);
                binding.rbStairCarryDestination.setText("Yes");
            }else{
                binding.rbStairCarryDestination.setChecked(true);
                binding.rbStairCarryDestination.setText("No");
            }

            if (destinationAddressDetail.optBoolean("additionalStop")){
                binding.rbAdditionalStopDestination.setChecked(true);
                binding.rbAdditionalStopDestination.setText("Yes");
                binding.layNumberofStopsDestination.setVisibility(View.VISIBLE);
                binding.etNumberofStopsDestination.setText(""+destinationAddressDetail.optString("numberOfStops"));
            }else{
                binding.rbAdditionalStopDestination.setChecked(true);
                binding.rbAdditionalStopDestination.setText("No");
                binding.layNumberofStopsDestination.setVisibility(View.GONE);
            }

            if (destinationAddressDetail.optBoolean("parkingRequirements")){
                binding.rbParkingRequirementsDestination.setChecked(true);
                binding.rbParkingRequirementsDestination.setText("Yes");
            }else{
                binding.rbParkingRequirementsDestination.setChecked(true);
                binding.rbParkingRequirementsDestination.setText("No");
            }

            if (destinationAddressDetail.optBoolean("externalElevator")){
                binding.rbExternalElevatorDestination.setChecked(true);
                binding.rbExternalElevatorDestination.setText("Yes");
            }else{
                binding.rbExternalElevatorDestination.setChecked(true);
                binding.rbExternalElevatorDestination.setText("No");
            }

            if (destinationAddressDetail.optBoolean("shuttle")){
                binding.rbShuttleDestination.setChecked(true);
                binding.rbShuttleDestination.setText("Yes");
                binding.layShuttleDistanceDestination.setVisibility(View.VISIBLE);
                binding.etShuttleDistanceDestination.setText(""+destinationAddressDetail.optString("shuttleDistance"));
                binding.etShuttleDistanceUnitDestination.setText(""+destinationAddressDetail.optString("shuttleDistanceUnit"));
            }else{
                binding.rbShuttleDestination.setChecked(true);
                binding.rbShuttleDestination.setText("No");
                binding.layShuttleDistanceDestination.setVisibility(View.GONE);
            }

            binding.etAccessNotesDestination.setText(""+cmn.replaceNull(destinationAddressDetail.optString("accessNotes")));
        }



        if (inquiryDetail.optBoolean("storageNeeded")){
            binding.layStorageNeeded.setVisibility(View.VISIBLE);
            binding.cbStorageMode.setText(""+inquiryDetail.optString("storageMode"));
            binding.cbStorageMode.setChecked(true);
            binding.cbStorageAt.setText(""+inquiryDetail.optString("storageAt"));
            binding.cbStorageAt.setChecked(true);
            binding.cbStorageTerm.setText(""+inquiryDetail.optString("storageTerm"));
            binding.cbStorageTerm.setChecked(true);
            binding.etPeriodFrom.setText(""+cmn.getDate(inquiryDetail.optLong("storagePeriodFrom")));
            binding.etPeriodTo.setText(""+cmn.getDate(inquiryDetail.optLong("storagePeriodTo")));
        }else{
            binding.layStorageNeeded.setVisibility(View.GONE);
        }

        binding.etSea.setText(""+cmn.replaceNull(inquiryDetail.optString("seaAllowance")));
        binding.etSea.setFocusable(false);
        binding.etAir.setText(""+cmn.replaceNull(inquiryDetail.optString("airAllowance")));
        binding.etSurface.setText(""+cmn.replaceNull(inquiryDetail.optString("surfaceAllowance")));

        if (cmn.replaceNull(inquiryDetail.optString("departureDate")).isEmpty()){
            binding.etClientDepartureDate.setText("");
        }else{
            binding.etClientDepartureDate.setText(""+cmn.getDate(inquiryDetail.optLong("departureDate")));
        }
        binding.etPortofDeparture.setText(""+cmn.replaceNull(inquiryDetail.optString("portOfDeparture")));
        binding.etPortofEntry.setText(""+cmn.replaceNull(inquiryDetail.optString("portOfEntry")));
        binding.etGeneralCommentsforAllModes.setText(""+cmn.replaceNull(inquiryDetail.optString("generalComments")));

        if (inquiryDetail.optBoolean("previousExp")){
            binding.cbAnyPreviousExpinthepast.setChecked(true);
            binding.cbAnyPreviousExpinthepast.setText("Yes");
            binding.layWhowastheMover.setVisibility(View.VISIBLE);
            binding.etWhowastheMover.setText(""+cmn.replaceNull(inquiryDetail.optString("previousMover")));
        }else{
            binding.cbAnyPreviousExpinthepast.setChecked(true);
            binding.cbAnyPreviousExpinthepast.setText("No");
            binding.layWhowastheMover.setVisibility(View.GONE);
        }

        binding.cbmostfactor.setText(""+cmn.replaceNull(inquiryDetail.optString("decidingFactor")));
        binding.etAnyotherquotetakenifyes.setText(""+cmn.replaceNull(inquiryDetail.optString("anyOtherQuoteTakenBy")));

        binding.signatureImage.setImageBitmap(cmn.getDecodedImage(details.optString("shipperSignature")));
        binding.signatureImageSurveyor.setImageBitmap(cmn.getDecodedImage(details.optString("surveyorSignature")));

        JSONArray movingItems = inquiryDetail.optJSONArray("movingItems");

        JSONArray notMovingItems = inquiryDetail.optJSONArray("notMovingItems");

        JSONArray maybeMovingItems = inquiryDetail.optJSONArray("maybeMovingItems");

        for (int i = 0; i < movingItems.length() ; i++) {

            final JSONObject item = movingItems.optJSONObject(i);

            JSONArray areaTypes = item.optJSONArray("areaTypes");


            switch (item.optString("mode")){
                case "Air" :
                    totalVolumeAir = totalVolumeAir + item.optInt("totalVolume");
                    totalWeightAir = totalWeightAir + item.optInt("totalWeight");
                    totalItemsAir = totalItemsAir + item.optInt("totalItems");
                    break;
                case "Road" :
                    totalVolumeRoad = totalVolumeRoad + item.optInt("totalVolume");
                    totalWeightRoad = totalWeightRoad + item.optInt("totalWeight");
                    totalItemsRoad =  totalItemsRoad + item.optInt("totalItems");
                    break;
                case "SEA" :
                    totalVolumeSEA = totalVolumeSEA + item.optInt("totalVolume");
                    totalWeightSEA = totalWeightSEA + item.optInt("totalWeight");
                    totalItemsSEA = totalItemsSEA + item.optInt("totalItems");
                    break;
                case "Rail" :
                    totalVolumeRail = totalVolumeRail + item.optInt("totalVolume");
                    totalWeightRail = totalWeightRail + item.optInt("totalWeight");
                    totalItemsRail = totalWeightRail + item.optInt("totalItems");
                    break;
            }

            for (int j = 0; j < areaTypes.length() ; j++) {

                JSONObject tempObj = areaTypes.optJSONObject(j);

                final ArticalModel articalModel = new ArticalModel();
                final View articalMain = getLayoutInflater().inflate(R.layout.row_artical_main , null);
                final TextInputEditText etRoomName = articalMain.findViewById(R.id.etRoomName);
                ImageView ivDelete = articalMain.findViewById(R.id.ivDelete);
                ivDelete.setVisibility(View.GONE);
                etRoomName.setClickable(false);
                etRoomName.setText(""+tempObj.optString("areaType"));
                Button addMore = articalMain.findViewById(R.id.addMore);
                addMore.setVisibility(View.GONE);
                switch (item.optString("mode")){
                    case "Air" :
                        articalModel.moveType = 2; // 2 for Move Typ SEA
                        break;
                    case "Road" :
                        articalModel.moveType = 0; // 0 for Move Typ ROAD
                        break;
                    case "SEA" :
                        articalModel.moveType = 1; // 1 for Move Typ SEA
                        break;
                    case "Rail" :
                        articalModel.moveType = 3; // 3 for Move Typ Rail
                        break;
                }
                articalModel.rootView = articalMain;
                addSubArticalMoving(articalModel , tempObj.optJSONArray("articles"));

                switch (item.optString("mode")){
                    case "Air" :
                        movingItemsAir.add(articalMain);
                        break;
                    case "Road" :
                        movingItemsRoad.add(articalMain);
                        break;
                    case "SEA" :
                        movingItemsSEA.add(articalMain);
                        break;
                    case "Rail" :
                        movingItemsRail.add(articalMain);
                        break;
                }
            }

        }

        for (int i = 0; i < maybeMovingItems.length() ; i++) {

            final JSONObject item = maybeMovingItems.optJSONObject(i);

            JSONArray areaTypes = item.optJSONArray("areaTypes");

            addArtical(2,areaTypes , item);

        }

        for (int i = 0; i < notMovingItems.length() ; i++) {

            final JSONObject item = notMovingItems.optJSONObject(i);

            JSONArray areaTypes = item.optJSONArray("areaTypes");

            addArtical(1,areaTypes , item);

        }

        resetMovingItems(0);
    }

    void addSubArticalMoving(ArticalModel articalModel , JSONArray articles){
        final LinearLayout holderArtical = articalModel.rootView.findViewById(R.id.holderArtical);
        for (int i = 0; i < articles.length(); i++) {

            JSONObject tempData = articles.optJSONObject(i);

            final View artical = getLayoutInflater().inflate(R.layout.row_artical , null);
            ImageView ivDelete = artical.findViewById(R.id.ivDelete);

            final AutoCompleteTextView autoSearchTV = artical.findViewById(R.id.autoSearchTV);

            final LinearLayout layDiamentions = artical.findViewById(R.id.layDiamentions);
            layDiamentions.setVisibility(View.GONE);

            final TextInputEditText etQuentity = artical.findViewById(R.id.etQuentity);
            final TextInputEditText etWeight = artical.findViewById(R.id.etWeight);
            final  TextInputEditText etUnitVolume = artical.findViewById(R.id.etUnitVolume);
            final  TextInputEditText etRemarks = artical.findViewById(R.id.etRemarks);

            final  TextInputEditText etInstructions = artical.findViewById(R.id.etInstructions);

            ivDelete.setVisibility(View.GONE);

            autoSearchTV.setEnabled(false);
            etQuentity.setEnabled(false);
            etWeight.setEnabled(false);
            etUnitVolume.setEnabled(false);

            int qty = tempData.optInt("quantity");
            int weight = tempData.optInt("weight");
            int volume = tempData.optInt("volume");

            autoSearchTV.setText(""+tempData.optString("name"));
            etQuentity.setText(""+qty);
            etWeight.setText(""+(qty * weight));
            etUnitVolume.setText(""+(qty * volume));
            etRemarks.setText(""+cmn.replaceNull(tempData.optString("remarks")));


            JSONArray instructions = tempData.optJSONArray("instructions");

            String instructionsStr = "";

            for (int j = 0; j <instructions.length() ; j++) {
                try {
                    instructionsStr =  instructionsStr.concat(instructions.getString(j) + " ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            etInstructions.setEnabled(false);
            etInstructions.setText(instructionsStr);

            holderArtical.addView(artical);
        }
    }


    void addArtical(final int type , JSONArray areaTypes , JSONObject item){
        for (int j = 0; j < areaTypes.length() ; j++) {

            JSONObject tempObj = areaTypes.optJSONObject(j);
            final ArticalModel articalModel = new ArticalModel();
            final View articalMain = getLayoutInflater().inflate(R.layout.row_artical_main , null);
            final TextInputEditText etRoomName = articalMain.findViewById(R.id.etRoomName);
            ImageView ivDelete = articalMain.findViewById(R.id.ivDelete);
            ivDelete.setVisibility(View.GONE);
            etRoomName.setEnabled(false);
            etRoomName.setText(""+tempObj.optString("areaType"));
            Button addMore = articalMain.findViewById(R.id.addMore);
            addMore.setVisibility(View.GONE);
            switch (item.optString("mode")){
                case "Air" :
                    articalModel.moveType = 2; // 2 for Move Typ SEA
                    break;
                case "Road" :
                    articalModel.moveType = 0; // 0 for Move Typ ROAD
                    break;
                case "Sea" :
                    articalModel.moveType = 1; // 1 for Move Typ SEA
                    break;
                case "Rail" :
                    articalModel.moveType = 3; // 3 for Move Typ Rail
                    break;
            }
            articalModel.rootView = articalMain;
            addSubArticalMoving(articalModel , tempObj.optJSONArray("articles"));
            if (type == 1){ // for not moving
                binding.surveyArticalHolderNotMoving.addView(articalMain);
            }else{
                binding.surveyArticalHolderMayBeMoving.addView(articalMain);
            }
        }
    }


    void resetMovingItems(final  int moveType){
        if (moveType == 0){// 0 for Move Typ ROAD
            cmn.printLog("road moving Items "+movingItemsRoad.size());
            binding.surveyArticalHolderMoving.removeAllViews();
            for (int i = 0; i < movingItemsRoad.size() ; i++) {
                binding.surveyArticalHolderMoving.addView(movingItemsRoad.get(i));
            }
            binding.totalItems.setText(""+totalItemsRoad);
            binding.totalWeight.setText(""+totalWeightRoad);
            binding.totalVolume.setText(""+totalVolumeRoad);
        }else if (moveType == 1){ // 1 for Move Typ SEA
            binding.surveyArticalHolderMoving.removeAllViews();
            cmn.printLog("sea moving Items "+movingItemsSEA.size());
            for (int i = 0; i < movingItemsSEA.size() ; i++) {
                binding.surveyArticalHolderMoving.addView(movingItemsSEA.get(i));
            }
            binding.totalItems.setText(""+totalItemsSEA);
            binding.totalWeight.setText(""+totalWeightSEA);
            binding.totalVolume.setText(""+totalVolumeSEA);
        }else if (moveType == 2){// 1 for Move Typ AIR
            binding.surveyArticalHolderMoving.removeAllViews();
            cmn.printLog("air moving Items "+movingItemsAir.size());
            for (int i = 0; i < movingItemsAir.size() ; i++) {
                binding.surveyArticalHolderMoving.addView(movingItemsAir.get(i));
            }
            binding.totalItems.setText(""+totalItemsAir);
            binding.totalWeight.setText(""+totalWeightAir);
            binding.totalVolume.setText(""+totalVolumeAir);
        }else if (moveType == 3){// 1 for Move Typ RAIL
            binding.surveyArticalHolderMoving.removeAllViews();
            cmn.printLog("rail moving Items "+movingItemsRail.size());
            for (int i = 0; i < movingItemsRail.size() ; i++) {
                binding.surveyArticalHolderMoving.addView(movingItemsRail.get(i));
            }
            binding.totalItems.setText(""+totalItemsRail);
            binding.totalWeight.setText(""+totalWeightRail);
            binding.totalVolume.setText(""+totalVolumeRail);
        }
    }
    void setDetails(JSONObject detail){
       if (detail.optBoolean("additionalServices")){
           binding.layAdditionalServices.setVisibility(View.VISIBLE);
           binding.tvWarehousing.setText(""+detail.optString("warehousing"));
           binding.tvWarehouseTemprature.setText(""+detail.optString("warehouseTemprature"));
           String relocationServices = "";
           for (int i = 0; i <detail.optJSONArray("relocationServices").length() ; i++) {
               relocationServices = relocationServices.concat(detail.optJSONArray("relocationServices").optString(i)+",");
           }
           binding.tvRelocationServices.setText(relocationServices);
           String visaImmigrationAssistance = "";
           for (int i = 0; i <detail.optJSONArray("visaImmigrationAssistance").length() ; i++) {
               visaImmigrationAssistance = visaImmigrationAssistance.concat(detail.optJSONArray("visaImmigrationAssistance").optString(i)+",");
           }
           binding.tvVisaandImmigrationAssistance.setText(visaImmigrationAssistance);
           String handymanServices = "";
           for (int i = 0; i <detail.optJSONArray("handymanServices").length() ; i++) {
               handymanServices = handymanServices.concat(detail.optJSONArray("handymanServices").optString(i)+",");
           }
           binding.tvHandymanServices.setText(handymanServices);
       }else{
           binding.layAdditionalServices.setVisibility(View.GONE);
       }
        if (detail.optBoolean("petAvailable")) {
            JSONObject petInfo = detail.optJSONObject("petInfo");
            binding.layPet.setVisibility(View.VISIBLE);
            if (petInfo.optBoolean("petImport")) {
                binding.cbimportpet.setChecked(true);
                binding.cbimportpet.setText("Yes");
            } else {
                binding.cbimportpet.setChecked(true);
                binding.cbimportpet.setText("No");
            }
            if (petInfo.optBoolean("travelledToEuropeanCounty")) {
                binding.cbTravelledtoEuropeancountries.setChecked(true);
                binding.cbTravelledtoEuropeancountries.setText("Yes");
            } else {
                binding.cbTravelledtoEuropeancountries.setChecked(true);
                binding.cbTravelledtoEuropeancountries.setText("No");
            }

            if (petInfo.optBoolean("vaccinationBookletAvailable")) {
                binding.cbVaccinationBookletavailable.setChecked(true);
                binding.cbVaccinationBookletavailable.setText("Yes");
            } else {
                binding.cbVaccinationBookletavailable.setChecked(true);
                binding.cbVaccinationBookletavailable.setText("No");
            }

            if (petInfo.optBoolean("petInsuranceRequired")) {
                binding.cbPetInsuranceRequired.setChecked(true);
                binding.cbPetInsuranceRequired.setText("Yes");
            } else {
                binding.cbPetInsuranceRequired.setChecked(true);
                binding.cbPetInsuranceRequired.setText("No");
            }

            if (petInfo.optBoolean("petNocCertificate")) {
                binding.cbNOCCertificateforPet.setChecked(true);
                binding.cbNOCCertificateforPet.setText("Yes");
            } else {
                binding.cbNOCCertificateforPet.setChecked(true);
                binding.cbNOCCertificateforPet.setText("No");
            }

            if (petInfo.optBoolean("bloodTestReport")) {
                binding.cbBloodTestReport.setChecked(true);
                binding.cbBloodTestReport.setText("Yes");
            } else {
                binding.cbBloodTestReport.setChecked(true);
                binding.cbBloodTestReport.setText("No");
            }

            if (petInfo.optBoolean("vaccinationCertificate")) {
                binding.cbVaccinationCertificate.setChecked(true);
                binding.cbVaccinationCertificate.setText("Yes");
            } else {
                binding.cbVaccinationCertificate.setChecked(true);
                binding.cbVaccinationCertificate.setText("No");
            }
            binding.etRemarkIfAny.setText(""+petInfo.optString("remarks"));

            JSONArray petImages = petInfo.optJSONArray("petImages");

            if (petImages.length() == 0){
                binding.laypetImages.setVisibility(View.GONE);
                binding.tvNoPetImage.setVisibility(View.VISIBLE);
            }else{
                binding.laypetImages.setVisibility(View.VISIBLE);
                binding.tvNoPetImage.setVisibility(View.GONE);
                for (int i = 0; i < petImages.length() ; i++) {
                    ImageView iv = new ImageView(mActivity);
                    iv.setLayoutParams(new LinearLayout.LayoutParams(80 , 80));
                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                    try {
                        iv.setImageBitmap(cmn.getDecodedImage(petImages.getJSONObject(i).getString("filePath")));
                        binding.holderPetImages.addView(iv);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            JSONArray pets = petInfo.optJSONArray("pets");
            if (pets.length() == 0){
                binding.petQuestionary.setVisibility(View.GONE);
            }else{
                binding.petQuestionary.setVisibility(View.VISIBLE);
                try {
                    JSONObject pet = pets.getJSONObject(0);
                    binding.etPetType.setText(""+pet.optString("petType"));
                    binding.etPetOwnerName.setText(""+pet.optString("petOwnerName"));
                    binding.etShipMode.setText(""+pet.optString("shipMode"));
                    binding.etDeliveryTypePet.setText(""+pet.optString("deliveryType"));
                    binding.etBreedName.setText(""+pet.optString("breedName"));
                    binding.etPetAge.setText(""+pet.optString("petAge"));
                    binding.etPetSex.setText(""+pet.optString("petSex"));
                    binding.etPetCitizenship.setText(""+pet.optString("petCitizenship"));
                    binding.etPetWeight.setText(""+pet.optString("petWeight"));
                    if (pet.optBoolean("kennelAvailability")){
                        binding.cbKennelAvailibity.setChecked(true);
                        binding.cbKennelAvailibity.setText("Yes");
                        binding.layKannelAvailibity.setVisibility(View.VISIBLE);
                        binding.etLengthKanel.setText(""+pet.optString("kennelLength"));
                        binding.etBreathKanel.setText(""+pet.optString("kennelBreadth"));
                        binding.etHeightKanel.setText(""+pet.optString("kennelHeight"));
                    }else{
                        binding.cbKennelAvailibity.setChecked(false);
                        binding.cbKennelAvailibity.setText("No");
                        binding.layKannelAvailibity.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }else{
            binding.layPet.setVisibility(View.GONE);
        }
        if (detail.optBoolean("vehicleAvailable")){
            binding.cbVehicle.setChecked(true);
            binding.layVehicle.setVisibility(View.VISIBLE);
            JSONObject vehicleInfo = detail.optJSONArray("vehicleInfo").optJSONObject(0);
            binding.etVehicleType.setText(""+vehicleInfo.optString("vehicleType"));
            binding.etVehicleOwnerName.setText(""+vehicleInfo.optString("vehicleOwnerName"));
            binding.etShipModeVehicle.setText(""+vehicleInfo.optString("shipMode"));
            binding.etDeliveryTypeVehicle.setText(""+vehicleInfo.optString("deliveryType"));
            binding.etVehicleMake.setText(""+vehicleInfo.optString("vehicleMake"));
            binding.etVehicleModelNumber.setText(""+vehicleInfo.optString("vehicleModelNumber"));
            binding.etVehicleRegistrationNumber.setText(""+vehicleInfo.optString("vehicleRegistrationNumber"));
            binding.etVehicleColor.setText(""+vehicleInfo.optString("vehicleColor"));
            binding.etVehicleEngineCapacity.setText(""+vehicleInfo.optString("vehicleEngineCapacity"));
            binding.etEmissionStandard.setText(""+vehicleInfo.optString("emissionStandard"));
            binding.etRemarksVehicle.setText(""+vehicleInfo.optString("remarks"));

        }else{
            binding.layVehicle.setVisibility(View.GONE);
        }

        if (detail.optBoolean("additionalServices") == true || detail.optBoolean("petAvailable") == true || detail.optBoolean("vehicleAvailable") == true) {
            binding.tablayout.addTab(binding.tablayout.newTab().setText("Additional Details"));
        }


    }

    void getActivityHistoryData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONArray array = new JSONArray(aResponse);
                        for (int index = 0;index<array.length();index++){
                            JSONObject data = array.getJSONObject(index);
                            View temp = getLayoutInflater().inflate(R.layout.row_contacts , null);

                            TextView tvName = temp.findViewById(R.id.tvName);
                            TextView tvEmail = temp.findViewById(R.id.tvEmail);
                            TextView tvContactNo = temp.findViewById(R.id.tvContactNo);

                            tvName.setText(""+cmn.getDate(data.optLong("performedOn")));
                            tvEmail.setText(""+data.optString("description"));
                            tvContactNo.setText(""+data.optJSONObject("performedBy").optString("fullName"));

                            binding.activityHolder.addView(temp);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                {
                    Toast.makeText(
                            mActivity,
                            "Not Got Response From Server.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        String url = "";
        url = "inquiry/activities/"+id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }
}