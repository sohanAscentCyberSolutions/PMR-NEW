package com.ascent.pmrsurveyapp.OperationSupervisor.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.textfield.TextInputEditText;
import com.ascent.pmrsurveyapp.OperationSupervisor.JobExecutionDetails;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.ArticalModel;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.LayJobActivityHistoryBinding;
import com.ascent.pmrsurveyapp.databinding.LayJobSurveyReportBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobSurveyReport extends Fragment {

    LayJobSurveyReportBinding binding;

    Comman cmn;
    Activity mActivity;

    ArrayList<View> movingItemsRoad = new ArrayList<>();
    ArrayList<View> movingItemsSEA = new ArrayList<>();
    ArrayList<View> movingItemsAir = new ArrayList<>();
    ArrayList<View> movingItemsRail = new ArrayList<>();

    int totalVolumeRoad = 0,totalVolumeSEA = 0,totalVolumeAir = 0,totalVolumeRail = 0;
    int totalWeightRoad = 0,totalWeightSEA = 0,totalWeightAir = 0,totalWeightRail = 0;
    int totalItemsRoad = 0,totalItemsSEA = 0,totalItemsAir = 0,totalItemsRail = 0;

    int movePosition = 0;
    int movePositionMoveType = 0;

    public static JobSurveyReport newInstance() {
        return new JobSurveyReport();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.lay_job_survey_report, container, false);
        mActivity = getActivity();
        cmn = new Comman(mActivity);

        getSurveyData();


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



        return binding.getRoot();


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }


    void getSurveyData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject obj = new JSONObject(aResponse);
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
        String url  = "surveyreport?Find=ByInquiry&inquiryId="+JobExecutionDetails.data.job.inquiry.id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
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
        binding.etResidenceSizeDestination.setText(""+destinationAddressDetail.optString("residenceSize"));
        binding.etElevatorFloorDestination.setText(""+destinationAddressDetail.optString("floor"));
        binding.etElevatorFloorDestination.setFocusable(false);

        if (destinationAddressDetail.optBoolean("longCarry")){
            binding.rbLongCarryDestination.setChecked(true);
            binding.rbLongCarryDestination.setText("Yes");
            binding.layLongCarryDistanceDestination.setVisibility(View.VISIBLE);
            binding.etLongCarryDistanceDestination.setText(""+destinationAddressDetail.optString("longCarryDistance"));
            binding.etLongCarryDistanceUnitDestination.setText(""+destinationAddressDetail.optString("longCarryDistanceUnit"));
        }else{
            binding.rbLongCarryDestination.setChecked(false);
            binding.rbLongCarryDestination.setText("No");
            binding.layLongCarryDistanceDestination.setVisibility(View.GONE);
        }
        if (destinationAddressDetail.optBoolean("stairCarry")){
            binding.rbStairCarryDestination.setChecked(true);
            binding.rbStairCarryDestination.setText("Yes");
        }else{
            binding.rbStairCarryDestination.setChecked(false);
            binding.rbStairCarryDestination.setText("No");
        }

        if (destinationAddressDetail.optBoolean("additionalStop")){
            binding.rbAdditionalStopDestination.setChecked(true);
            binding.rbAdditionalStopDestination.setText("Yes");
            binding.layNumberofStopsDestination.setVisibility(View.VISIBLE);
            binding.etNumberofStopsDestination.setText(""+destinationAddressDetail.optString("numberOfStops"));
        }else{
            binding.rbAdditionalStopDestination.setChecked(false);
            binding.rbAdditionalStopDestination.setText("No");
            binding.layNumberofStopsDestination.setVisibility(View.GONE);
        }

        if (destinationAddressDetail.optBoolean("parkingRequirements")){
            binding.rbParkingRequirementsDestination.setChecked(true);
            binding.rbParkingRequirementsDestination.setText("Yes");
        }else{
            binding.rbParkingRequirementsDestination.setChecked(false);
            binding.rbParkingRequirementsDestination.setText("No");
        }

        if (destinationAddressDetail.optBoolean("externalElevator")){
            binding.rbExternalElevatorDestination.setChecked(true);
            binding.rbExternalElevatorDestination.setText("Yes");
        }else{
            binding.rbExternalElevatorDestination.setChecked(false);
            binding.rbExternalElevatorDestination.setText("No");
        }

        if (destinationAddressDetail.optBoolean("shuttle")){
            binding.rbShuttleDestination.setChecked(true);
            binding.rbShuttleDestination.setText("Yes");
            binding.layShuttleDistanceDestination.setVisibility(View.VISIBLE);
            binding.etShuttleDistanceDestination.setText(""+destinationAddressDetail.optString("shuttleDistance"));
            binding.etShuttleDistanceUnitDestination.setText(""+destinationAddressDetail.optString("shuttleDistanceUnit"));
        }else{
            binding.rbShuttleDestination.setChecked(false);
            binding.rbShuttleDestination.setText("No");
            binding.layShuttleDistanceDestination.setVisibility(View.GONE);
        }

        binding.etAccessNotesDestination.setText(""+cmn.replaceNull(destinationAddressDetail.optString("accessNotes")));

        if (inquiryDetail.optBoolean("storageNeeded")){
            binding.layStorageNeeded.setVisibility(View.VISIBLE);
            binding.cbStorageMode.setText(""+inquiryDetail.optString("storageMode"));
            binding.cbStorageAt.setText(""+inquiryDetail.optString("storageAt"));
            binding.cbStorageTerm.setText(""+inquiryDetail.optString("storageTerm"));
            binding.etPeriodFrom.setText(""+cmn.getDate(inquiryDetail.optLong("storagePeriodFrom")));
            binding.etPeriodTo.setText(""+cmn.getDate(inquiryDetail.optLong("storagePeriodTo")));
        }else{
            binding.layStorageNeeded.setVisibility(View.GONE);
        }

        binding.etSea.setText(""+inquiryDetail.optString("seaAllowance"));
        binding.etSea.setFocusable(false);
        binding.etAir.setText(""+inquiryDetail.optString("airAllowance"));
        binding.etSurface.setText(""+inquiryDetail.optString("surfaceAllowance"));
        binding.etClientDepartureDate.setText(""+cmn.getDate(inquiryDetail.optLong("departureDate")));
        binding.etPortofDeparture.setText(""+cmn.replaceNull(inquiryDetail.optString("portOfDeparture")));
        binding.etPortofEntry.setText(""+cmn.replaceNull(inquiryDetail.optString("portOfEntry")));
        binding.etGeneralCommentsforAllModes.setText(""+cmn.replaceNull(inquiryDetail.optString("generalComments")));

        if (inquiryDetail.optBoolean("previousExp")){
            binding.cbAnyPreviousExpinthepast.setChecked(true);
            binding.cbAnyPreviousExpinthepast.setText("Yes");
            binding.layWhowastheMover.setVisibility(View.VISIBLE);
            binding.etWhowastheMover.setText(""+cmn.replaceNull(inquiryDetail.optString("previousMover")));
        }else{
            binding.cbAnyPreviousExpinthepast.setChecked(false);
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

            final  TextInputEditText etInstructions = artical.findViewById(R.id.etInstructions);

            ivDelete.setVisibility(View.GONE);

            autoSearchTV.setEnabled(false);
            etQuentity.setEnabled(false);
            etWeight.setEnabled(false);
            etUnitVolume.setEnabled(false);


            autoSearchTV.setText(""+tempData.optString("name"));
            etQuentity.setText(""+tempData.optString("quantity"));
            etWeight.setText(""+tempData.optString("weight"));
            etUnitVolume.setText(""+tempData.optString("volume"));


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

}