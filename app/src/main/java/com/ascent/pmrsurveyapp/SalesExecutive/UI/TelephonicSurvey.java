package com.ascent.pmrsurveyapp.SalesExecutive.UI;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.textfield.TextInputEditText;
import com.ascent.pmrsurveyapp.Fragments.Request_Details;
import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.Models.RequestsModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.ArticalSelector;
import com.ascent.pmrsurveyapp.SalesExecutive.Fragmants.Artical_Selector_Dialog;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.ArticalModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.StandardItemsModel;
import com.ascent.pmrsurveyapp.UI.SurveyReport;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.ActivityTelephonicSurveyBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class TelephonicSurvey extends AppCompatActivity {
    Comman cmn;
    Activity mActivity;
    ActivityTelephonicSurveyBinding binding;
    public static RequestsModel requestData;
    ArrayList<StandardItemsModel> standardItems = new ArrayList<>();
    CommanModel selectedSurveyor;

    String inquiryId = "";
    Long inquiryDate;

    ArrayList<View> movingItemsRoad = new ArrayList<>();
    ArrayList<View> movingItemsSEA = new ArrayList<>();
    ArrayList<View> movingItemsAir = new ArrayList<>();
    ArrayList<View> movingItemsRail = new ArrayList<>();

    String requiredPetDocument = "" , imageOfPet="";

    String[] roomItems = {"All Room" , "Bed Room" , "Lounge" , "Laundry" , "Hall" , "Dining Room"
            , "Study Room"  , "Living Room" , "Porch/Outdoor" , "Attic/Loft" , "Kitchen" , "Miscellaneous"
            , "Bath Room" , "Deck/Patio" , "Home Office" , "Garden" , "Basement"};

    boolean isDetail = false;
    int movePosition = 0;
    int movePositionMoveType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_telephonic_survey);
        mActivity = this;
        cmn = new Comman(mActivity);

        binding.btAdd.setVisibility(View.GONE);

        isDetail = getIntent().getBooleanExtra("isDetail" , false);



        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkValidation()){
                    submitRequest();
                }

            }
        });
        if (isDetail){
            getReportDetails();
        }else{
            binding.cbKennelAvailibityYes.setChecked(false);
            binding.cbKennelAvailibityNo.setChecked(true);
            binding.layKannelAvailibity.setVisibility(View.GONE);
            inquiryId = getIntent().getStringExtra("id");
            setupListiners();
           // addArticalMove(0);
           // addArticalMove(1);
           // addArticalMove(2);
           // addArticalMove(3);
           // addArtical(1);
           // addArtical(2);
            getinquiryDetails();
            getStandardItems();
        }
        binding.sagmentedGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(final int position) {
                // Handle stuff here
                Log.e("position" , ""+ position);

                if (position== 0){
                    binding.surveyInfo.setVisibility(View.VISIBLE);
                    binding.surveyInfoAdvance.setVisibility(View.GONE);
                    binding.surveyInfoArtical.setVisibility(View.GONE);
                    binding.surveyInfoSignature.setVisibility(View.GONE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(700)
                            .playOn(findViewById(R.id.surveyInfo));
                    if (!isDetail)
                        binding.btAdd.setVisibility(View.GONE);
                }else if (position== 1){
                    binding.surveyInfo.setVisibility(View.GONE);
                    binding.surveyInfoAdvance.setVisibility(View.VISIBLE);
                    binding.surveyInfoArtical.setVisibility(View.GONE);
                    binding.surveyInfoSignature.setVisibility(View.GONE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(700)
                            .playOn(findViewById(R.id.surveyInfoAdvance));
                    binding.scrollView.scrollTo(0,0);
                    if (!isDetail)
                        binding.btAdd.setVisibility(View.GONE);
                }else if (position== 2){
                    binding.surveyInfo.setVisibility(View.GONE);
                    binding.surveyInfoAdvance.setVisibility(View.GONE);
                    binding.surveyInfoArtical.setVisibility(View.VISIBLE);
                    binding.surveyInfoSignature.setVisibility(View.GONE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(700)
                            .playOn(findViewById(R.id.surveyInfoArtical));
                    if (!isDetail)
                        binding.btAdd.setVisibility(View.GONE);
                }else{
                    binding.surveyInfo.setVisibility(View.GONE);
                    binding.surveyInfoAdvance.setVisibility(View.GONE);
                    binding.surveyInfoArtical.setVisibility(View.GONE);
                    binding.surveyInfoSignature.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(700)
                            .playOn(findViewById(R.id.surveyInfoSignature));
                    if (!isDetail)
                        binding.btAdd.setVisibility(View.VISIBLE);
                }
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
                            .duration(700)
                            .playOn(findViewById(R.id.surveyArticalHolderMoving));
                }else if (position== 1){
                    binding.laysurveyArticalHolderMoving.setVisibility(View.GONE);
                    binding.surveyArticalHolderNotMoving.setVisibility(View.VISIBLE);
                    binding.surveyArticalHolderMayBeMoving.setVisibility(View.GONE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(700)
                            .playOn(findViewById(R.id.surveyArticalHolderNotMoving));
                }else if (position== 2){
                    binding.laysurveyArticalHolderMoving.setVisibility(View.GONE);
                    binding.surveyArticalHolderNotMoving.setVisibility(View.GONE);
                    binding.surveyArticalHolderMayBeMoving.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(700)
                            .playOn(findViewById(R.id.surveyArticalHolderMayBeMoving));

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

        // getSuggestions();
    }

    boolean checkValidation(){

        if (isEmpty(binding.etDateofPacking)){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Enter Date Of Packing");
            return false;
        }

        if (isEmpty(binding.etTimeofPacking)){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Enter Time Of Packing");
            return false;
        }
        if (isEmpty(binding.etMovingDate)){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Enter Moving Date");
            return false;
        }
        if (isEmpty(binding.etMovingDate)){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Date of Packing");
            return false;
        }
       if (isEmpty(binding.etMode)){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select a mode.");
            return false;
        }
        if (isEmpty(binding.etLoad)){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select a load.");
            return false;
        }
        if (!binding.insuranceIndiviual.isChecked() && !binding.insuranceByCmpny.isChecked() && !binding.insuranceByPMR.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select an insurance option.");
            return false;
        }
        if (isEmpty(binding.etResidenceSize)){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Origin Residence Size.");
            return false;
        }
        if (isEmpty(binding.etElevatorFloor)){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Enter Origin Floor.");
            return false;
        }
        if (!binding.rbLongCarryYes.isChecked() && !binding.rbLongCarryNo.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Origin Long Carry option.");
            return false;
        }

        if (binding.rbLongCarryYes.isChecked()){
            if (isEmpty(binding.etLongCarryDistance)){
                binding.sagmentedGroup.setPosition(0,true);
                cmn.showToast("Please Enter Origin Long Carry Distance.");
                return false;
            }
            if (isEmpty(binding.etLongCarryDistanceUnit)){
                binding.sagmentedGroup.setPosition(0,true);
                cmn.showToast("Please Enter Origin Long Carry Distance Unit.");
                return false;
            }
        }

        if (!binding.rbStairCarryYes.isChecked() && !binding.rbStairCarryNo.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Origin Stair Carry option.");
            return false;
        }
        if (!binding.rbAdditionalStopYes.isChecked() && !binding.rbAdditionalStopNo.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Origin Additional Stop option.");
            return false;
        }

        if (binding.rbAdditionalStopYes.isChecked()){
            if (isEmpty(binding.etNumberofStops)){
                binding.sagmentedGroup.setPosition(0,true);
                cmn.showToast("Please Origin Additional No of Stopes.");
                return false;
            }
        }

        if (!binding.rbParkingRequirementsYes.isChecked() && !binding.rbParkingRequirementsNo.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Origin Parking Requirements option.");
            return false;
        }

        if (!binding.rbExternalElevatorYes.isChecked() && !binding.rbExternalElevatorNo.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Origin External Elevator option.");
            return false;
        }

        if (!binding.rbShuttleYes.isChecked() && !binding.rbShuttleNo.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Origin Shuttle option.");
            return false;
        }

        if (binding.rbShuttleYes.isChecked()){
            if (isEmpty(binding.etShuttleDistance)){
                binding.sagmentedGroup.setPosition(0,true);
                cmn.showToast("Please Enter Origin Shuttle Distance.");
                return false;
            }
            if (isEmpty(binding.etShuttleDistanceUnit)){
                binding.sagmentedGroup.setPosition(0,true);
                cmn.showToast("Please Enter Origin Shuttle Distance Unit.");
                return false;
            }
        }

     /*   if (isEmpty(binding.etResidenceSizeDestination)){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Destination Residence Size.");
            return false;
        }
        if (isEmpty(binding.etElevatorFloorDestination)){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Enter Destination Floor.");
            return false;
        }
        if (!binding.rbLongCarryDestinationYes.isChecked() && !binding.rbLongCarryDestinationNo.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Destination Long Carry option.");
            return false;
        }

        if (binding.rbLongCarryDestinationYes.isChecked()){
            if (isEmpty(binding.etLongCarryDistanceDestination)){
                binding.sagmentedGroup.setPosition(0,true);
                cmn.showToast("Please Enter Destination Long Carry Distance.");
                return false;
            }
            if (isEmpty(binding.etLongCarryDistanceUnitDestination)){
                binding.sagmentedGroup.setPosition(0,true);
                cmn.showToast("Please Enter Destination Long Carry Distance Unit.");
                return false;
            }
        }

        if (!binding.rbStairCarryDestinationYes.isChecked() && !binding.rbStairCarryDestinationNo.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Destination Stair Carry option.");
            return false;
        }
        if (!binding.rbAdditionalStopDestinationYes.isChecked() && !binding.rbAdditionalStopDestinationNo.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Destination Additional Stop option.");
            return false;
        }

        if (binding.rbAdditionalStopDestinationYes.isChecked()){
            if (isEmpty(binding.etNumberofStopsDestination)){
                binding.sagmentedGroup.setPosition(0,true);
                cmn.showToast("Please Destination Additional No of Stopes.");
                return false;
            }
        }

        if (!binding.rbParkingRequirementsDestinationYes.isChecked() && !binding.rbParkingRequirementsDestinationNo.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Destination Parking Requirements option.");
            return false;
        }

        if (!binding.rbExternalElevatorDestinationYes.isChecked() && !binding.rbExternalElevatorDestinationNo.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Destination External Elevator option.");
            return false;
        }

        if (!binding.rbShuttleDestinationYes.isChecked() && !binding.rbShuttleDestinationNo.isChecked()){
            binding.sagmentedGroup.setPosition(0,true);
            cmn.showToast("Please Select Destination Shuttle option.");
            return false;
        }

        if (binding.rbShuttleDestinationYes.isChecked()){
            if (isEmpty(binding.etShuttleDistanceDestination)){
                binding.sagmentedGroup.setPosition(0,true);
                cmn.showToast("Please Enter Destination Shuttle Distance.");
                return false;
            }
            if (isEmpty(binding.etShuttleDistanceUnitDestination)){
                binding.sagmentedGroup.setPosition(0,true);
                cmn.showToast("Please Enter Destination Shuttle Distance Unit.");
                return false;
            }
        }*/

      /*  if (isEmpty(binding.etSea)){
            binding.sagmentedGroup.setPosition(1,true);
            cmn.showToast("Please Enter Sea (CFT GROSS)");
            return false;
        }
        if (isEmpty(binding.etAir)){
            binding.sagmentedGroup.setPosition(1,true);
            cmn.showToast("Please Enter Air (KG NET)");
            return false;
        }

        if (isEmpty(binding.etSurface)){
            binding.sagmentedGroup.setPosition(1,true);
            cmn.showToast("Please Enter Surface (CFT GROSS)");
            return false;
        }

        if (isEmpty(binding.etClientDepartureDate)){
            binding.sagmentedGroup.setPosition(1,true);
            cmn.showToast("Please Select Client Departure Date");
            return false;
        }*/
        if (!binding.cbAnyPreviousExpinthepastYes.isChecked() && !binding.cbAnyPreviousExpinthepastNo.isChecked()){
            binding.sagmentedGroup.setPosition(1,true);
            cmn.showToast("Please Select Any Previous Exp. in the past option.");
            return false;
        }

        if (!binding.cbmostfactorPrice.isChecked() && !binding.cbmostfactorYourPreference.isChecked() && !binding.cbmostfactorNameFacilities.isChecked()){
            binding.sagmentedGroup.setPosition(1,true);
            cmn.showToast("Please Select Single most factor in deciding a mover option.");
            return false;
        }

        if (movingItemsAir.isEmpty() && movingItemsRail.isEmpty() && movingItemsRoad.isEmpty() && movingItemsSEA.isEmpty()){
            cmn.showToast("Please Add Atleast one Artical to submit.");
            return false;
        }else{
            for (int i = 0; i < movingItemsRoad.size() ; i++) {
                View articalMain = movingItemsRoad.get(i);
                final TextInputEditText etRoomName = articalMain.findViewById(R.id.etRoomName);

                if (isEmpty(etRoomName)){
                    binding.sagmentedGroup.setPosition(2,true);
                    cmn.showToast("Please Enter Road Article Room Name");
                    return false;
                }else{
                    LinearLayout holderArtical = articalMain.findViewById(R.id.holderArtical);
                    for (int j = 0; j < holderArtical.getChildCount() ; j++) {
                        View articleSub = holderArtical.getChildAt(i);
                        final AutoCompleteTextView autoSearchTV = articleSub.findViewById(R.id.autoSearchTV);
                        final TextInputEditText etWeight = articleSub.findViewById(R.id.etWeight);
                        final  TextInputEditText etUnitVolume = articleSub.findViewById(R.id.etUnitVolume);

                        if (autoSearchTV.getText().toString().isEmpty()){
                            binding.sagmentedGroup.setPosition(2,true);
                            cmn.showToast("Please Enter Article Name");
                            return false;
                        }

                        if (isEmpty(etWeight)){
                            binding.sagmentedGroup.setPosition(2,true);
                            cmn.showToast("Please Enter Article Weight");
                            return false;
                        }

                        if (isEmpty(etUnitVolume)){
                            binding.sagmentedGroup.setPosition(2,true);
                            cmn.showToast("Please Enter Article Unit Volume");
                            return false;
                        }

                    }
                }
            }

            for (int i = 0; i < movingItemsRail.size() ; i++) {
                View articalMain = movingItemsRail.get(i);
                final TextInputEditText etRoomName = articalMain.findViewById(R.id.etRoomName);

                if (isEmpty(etRoomName)){
                    binding.sagmentedGroup.setPosition(2,true);
                    cmn.showToast("Please Enter Rail Article Room Name");
                    return false;
                }else{
                    LinearLayout holderArtical = articalMain.findViewById(R.id.holderArtical);
                    for (int j = 0; j < holderArtical.getChildCount() ; j++) {
                        View articleSub = holderArtical.getChildAt(i);
                        final AutoCompleteTextView autoSearchTV = articleSub.findViewById(R.id.autoSearchTV);
                        final TextInputEditText etWeight = articleSub.findViewById(R.id.etWeight);
                        final  TextInputEditText etUnitVolume = articleSub.findViewById(R.id.etUnitVolume);

                        if (autoSearchTV.getText().toString().isEmpty()){
                            binding.sagmentedGroup.setPosition(2,true);
                            cmn.showToast("Please Enter Article Name");
                            return false;
                        }

                        if (isEmpty(etWeight)){
                            binding.sagmentedGroup.setPosition(2,true);
                            cmn.showToast("Please Enter Article Weight");
                            return false;
                        }

                        if (isEmpty(etUnitVolume)){
                            binding.sagmentedGroup.setPosition(2,true);
                            cmn.showToast("Please Enter Article Weight");
                            return false;
                        }

                    }
                }
            }

            for (int i = 0; i < movingItemsAir.size() ; i++) {
                View articalMain = movingItemsAir.get(i);
                final TextInputEditText etRoomName = articalMain.findViewById(R.id.etRoomName);

                if (isEmpty(etRoomName)){
                    binding.sagmentedGroup.setPosition(2,true);
                    cmn.showToast("Please Enter Air Article Room Name");
                    return false;
                }else{
                    LinearLayout holderArtical = articalMain.findViewById(R.id.holderArtical);
                    for (int j = 0; j < holderArtical.getChildCount() ; j++) {
                        View articleSub = holderArtical.getChildAt(i);
                        final AutoCompleteTextView autoSearchTV = articleSub.findViewById(R.id.autoSearchTV);
                        final TextInputEditText etWeight = articleSub.findViewById(R.id.etWeight);
                        final  TextInputEditText etUnitVolume = articleSub.findViewById(R.id.etUnitVolume);

                        if (autoSearchTV.getText().toString().isEmpty()){
                            binding.sagmentedGroup.setPosition(2,true);
                            cmn.showToast("Please Enter Article Name");
                            return false;
                        }

                        if (isEmpty(etWeight)){
                            binding.sagmentedGroup.setPosition(2,true);
                            cmn.showToast("Please Enter Article Weight");
                            return false;
                        }

                        if (isEmpty(etUnitVolume)){
                            binding.sagmentedGroup.setPosition(2,true);
                            cmn.showToast("Please Enter Article Weight");
                            return false;
                        }

                    }
                }
            }

            for (int i = 0; i < movingItemsSEA.size() ; i++) {
                View articalMain = movingItemsSEA.get(i);
                final TextInputEditText etRoomName = articalMain.findViewById(R.id.etRoomName);

                if (isEmpty(etRoomName)){
                    binding.sagmentedGroup.setPosition(2,true);
                    cmn.showToast("Please Enter SEA Article Room Name");
                    return false;
                }else{
                    LinearLayout holderArtical = articalMain.findViewById(R.id.holderArtical);
                    for (int j = 0; j < holderArtical.getChildCount() ; j++) {
                        View articleSub = holderArtical.getChildAt(i);
                        final AutoCompleteTextView autoSearchTV = articleSub.findViewById(R.id.autoSearchTV);
                        final TextInputEditText etWeight = articleSub.findViewById(R.id.etWeight);
                        final  TextInputEditText etUnitVolume = articleSub.findViewById(R.id.etUnitVolume);

                        if (autoSearchTV.getText().toString().isEmpty()){
                            binding.sagmentedGroup.setPosition(2,true);
                            cmn.showToast("Please Enter Article Name");
                            return false;
                        }

                        if (isEmpty(etWeight)){
                            binding.sagmentedGroup.setPosition(2,true);
                            cmn.showToast("Please Enter Article Weight");
                            return false;
                        }

                        if (isEmpty(etUnitVolume)){
                            binding.sagmentedGroup.setPosition(2,true);
                            cmn.showToast("Please Enter Article Weight");
                            return false;
                        }

                    }
                }
            }
        }

        for (int i = 0; i < binding.surveyArticalHolderNotMoving.getChildCount() ; i++) {
            View articalMain = binding.surveyArticalHolderNotMoving.getChildAt(i);
            final TextInputEditText etRoomName = articalMain.findViewById(R.id.etRoomName);

            if (isEmpty(etRoomName)){
                binding.sagmentedGroup.setPosition(2,true);
                cmn.showToast("Please Enter Not Moving Article Room Name");
                return false;
            }else{
                LinearLayout holderArtical = articalMain.findViewById(R.id.holderArtical);
                for (int j = 0; j < holderArtical.getChildCount() ; j++) {
                    View articleSub = holderArtical.getChildAt(i);
                    final AutoCompleteTextView autoSearchTV = articleSub.findViewById(R.id.autoSearchTV);
                    final TextInputEditText etWeight = articleSub.findViewById(R.id.etWeight);
                    final  TextInputEditText etUnitVolume = articleSub.findViewById(R.id.etUnitVolume);

                    if (autoSearchTV.getText().toString().isEmpty()){
                        binding.sagmentedGroup.setPosition(2,true);
                        cmn.showToast("Please Enter Article Name");
                        return false;
                    }

                    if (isEmpty(etWeight)){
                        binding.sagmentedGroup.setPosition(2,true);
                        cmn.showToast("Please Enter Article Weight");
                        return false;
                    }

                    if (isEmpty(etUnitVolume)){
                        binding.sagmentedGroup.setPosition(2,true);
                        cmn.showToast("Please Enter Article Weight");
                        return false;
                    }

                }
            }
        }

        for (int i = 0; i < binding.surveyArticalHolderMayBeMoving.getChildCount() ; i++) {
            View articalMain = binding.surveyArticalHolderMayBeMoving.getChildAt(i);
            final TextInputEditText etRoomName = articalMain.findViewById(R.id.etRoomName);

            if (isEmpty(etRoomName)){
                binding.sagmentedGroup.setPosition(2,true);
                cmn.showToast("Please Enter May be Moving Article Room Name");
                return false;
            }else{
                LinearLayout holderArtical = articalMain.findViewById(R.id.holderArtical);
                for (int j = 0; j < holderArtical.getChildCount() ; j++) {
                    View articleSub = holderArtical.getChildAt(i);
                    final AutoCompleteTextView autoSearchTV = articleSub.findViewById(R.id.autoSearchTV);
                    final TextInputEditText etWeight = articleSub.findViewById(R.id.etWeight);
                    final  TextInputEditText etUnitVolume = articleSub.findViewById(R.id.etUnitVolume);

                    if (autoSearchTV.getText().toString().isEmpty()){
                        binding.sagmentedGroup.setPosition(2,true);
                        cmn.showToast("Please Enter Article Name");
                        return false;
                    }

                    if (isEmpty(etWeight)){
                        binding.sagmentedGroup.setPosition(2,true);
                        cmn.showToast("Please Enter Article Weight");
                        return false;
                    }

                    if (isEmpty(etUnitVolume)){
                        binding.sagmentedGroup.setPosition(2,true);
                        cmn.showToast("Please Enter Article Weight");
                        return false;
                    }

                }
            }
        }

        if (binding.signaturePad.isEmpty()){
            cmn.showToast("Please draw shipper Signature");
            return false;
        }

        if (binding.signaturePadSurveyor.isEmpty()){
            cmn.showToast("Please draw surveyor Signature");
            return false;
        }

        return true;
    }

    boolean isEmpty(TextInputEditText tv){

        if (tv.getText().toString().isEmpty())
            return true;
        else
            return false;

    }

    void addArticalMove(final int moveType){
        final ArticalModel articalModel = new ArticalModel();
        final View articalMain = getLayoutInflater().inflate(R.layout.row_artical_main , null);
        final TextInputEditText etRoomName = articalMain.findViewById(R.id.etRoomName);
        if (moveType == 0){// 0 for Move Typ ROAD
            articalMain.setTag(movingItemsRoad.size());
        }else if (moveType == 1){ // 1 for Move Typ SEA
            articalMain.setTag(movingItemsSEA.size());
        }else if (moveType == 2){// 1 for Move Typ AIR
            articalMain.setTag(movingItemsAir.size());
        }else if (moveType == 3){// 1 for Move Typ RAIL
            articalMain.setTag(movingItemsRail.size());
        }

        ImageView ivDelete = articalMain.findViewById(R.id.ivDelete);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moveType == 0){// 0 for Move Typ ROAD
                    for (int i = 0; i <movingItemsRoad.size() ; i++) {
                        if (movingItemsRoad.get(i).getTag() == articalMain.getTag()){
                            movingItemsRoad.remove(i);
                            break;
                        }
                    }

                }else if (moveType == 1){ // 1 for Move Typ SEA
                    for (int i = 0; i <movingItemsSEA.size() ; i++) {
                        if (movingItemsSEA.get(i).getTag() == articalMain.getTag()){
                            movingItemsSEA.remove(i);
                            break;
                        }
                    }
                }else if (moveType == 2){// 1 for Move Typ AIR
                    for (int i = 0; i <movingItemsAir.size() ; i++) {
                        if (movingItemsAir.get(i).getTag() == articalMain.getTag()){
                            movingItemsAir.remove(i);
                            break;
                        }
                    }
                }else if (moveType == 3){// 1 for Move Typ RAIL
                    for (int i = 0; i <movingItemsRail.size() ; i++) {
                        if (movingItemsRail.get(i).getTag() == articalMain.getTag()){
                            movingItemsRail.remove(i);
                            break;
                        }
                    }
                }
                resetMovingItems(moveType);
            }
        });
        etRoomName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Room Name");
                alert.setSingleChoiceItems(roomItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            selection[0] = which;
                            etRoomName.setText(roomItems[selection[0]]);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        articalModel.rootView = articalMain;
        articalModel.moveType = moveType;
        Button addMore = articalMain.findViewById(R.id.addMore);
        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubArticalMoving(articalModel);
            }
        });
        addSubArticalMoving(articalModel);

        if (moveType == 0){// 0 for Move Typ ROAD
            movingItemsRoad.add(articalMain);
        }else if (moveType == 1){ // 1 for Move Typ SEA
            movingItemsSEA.add(articalMain);
        }else if (moveType == 2){// 2 for Move Typ AIR
            movingItemsAir.add(articalMain);
        }else if (moveType == 3){// 3 for Move Typ RAIL
            movingItemsRail.add(articalMain);
        }

        resetMovingItems(moveType);
    }

    void resetMovingItems(final  int moveType){
        if (moveType == 0){// 0 for Move Typ ROAD
            cmn.printLog("road moving Items "+movingItemsRoad.size());
           binding.surveyArticalHolderMoving.removeAllViews();
            for (int i = 0; i < movingItemsRoad.size() ; i++) {
                binding.surveyArticalHolderMoving.addView(movingItemsRoad.get(i));
            }
        }else if (moveType == 1){ // 1 for Move Typ SEA
            binding.surveyArticalHolderMoving.removeAllViews();
            cmn.printLog("sea moving Items "+movingItemsSEA.size());
            for (int i = 0; i < movingItemsSEA.size() ; i++) {
                binding.surveyArticalHolderMoving.addView(movingItemsSEA.get(i));
            }
        }else if (moveType == 2){// 1 for Move Typ AIR
            binding.surveyArticalHolderMoving.removeAllViews();
            cmn.printLog("air moving Items "+movingItemsAir.size());
            for (int i = 0; i < movingItemsAir.size() ; i++) {
                binding.surveyArticalHolderMoving.addView(movingItemsAir.get(i));
            }
        }else if (moveType == 3){// 1 for Move Typ RAIL
            binding.surveyArticalHolderMoving.removeAllViews();
            cmn.printLog("rail moving Items "+movingItemsRail.size());
            for (int i = 0; i < movingItemsRail.size() ; i++) {
                binding.surveyArticalHolderMoving.addView(movingItemsRail.get(i));
            }
        }
    }

    void addArtical(final int type){
        final ArticalModel articalModel = new ArticalModel();
        final View articalMain = getLayoutInflater().inflate(R.layout.row_artical_main , null);
        final TextInputEditText etRoomName = articalMain.findViewById(R.id.etRoomName);

        ImageView ivDelete = articalMain.findViewById(R.id.ivDelete);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0){
                    binding.surveyArticalHolderMoving.removeView(articalMain);
                }else if (type == 1){
                    binding.surveyArticalHolderNotMoving.removeView(articalMain);
                }else if (type == 2){
                    binding.surveyArticalHolderMayBeMoving.removeView(articalMain);
                }
            }
        });
        etRoomName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Room Name");
                alert.setSingleChoiceItems(roomItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            selection[0] = which;
                            etRoomName.setText(roomItems[selection[0]]);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        articalModel.rootView = articalMain;
        articalModel.moveType = type;
        Button addMore = articalMain.findViewById(R.id.addMore);
        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubArtical(articalModel);
            }
        });
        addSubArtical(articalModel);

        if (type == 1){ // for not moving
            binding.surveyArticalHolderNotMoving.addView(articalMain);
        }else{
            binding.surveyArticalHolderMayBeMoving.addView(articalMain);
        }


    }

    void addSubArticalMoving(ArticalModel articalModel){
        final View artical = getLayoutInflater().inflate(R.layout.row_artical , null);

        ImageView ivDelete = artical.findViewById(R.id.ivDelete);

        final AutoCompleteTextView autoSearchTV = artical.findViewById(R.id.autoSearchTV);

        final TextInputEditText etQuentity = artical.findViewById(R.id.etQuentity);
        final TextInputEditText etHeight = artical.findViewById(R.id.etHeight);
        final TextInputEditText etWidth = artical.findViewById(R.id.etWidth);
        final TextInputEditText etWeight = artical.findViewById(R.id.etWeight);
        final  TextInputEditText etBreadth = artical.findViewById(R.id.etBreadth);
        final  TextInputEditText etUnitVolume = artical.findViewById(R.id.etUnitVolume);

        autoSearchTV.setFocusable(false);
        etQuentity.setText("1");

        final  TextInputEditText etInstructions = artical.findViewById(R.id.etInstructions);
        etInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Creating" , "ASM" , "DSM" , "High Value" , "Packed By Owner" , "Heavey Items"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Instructions");
                final boolean[] checkedItems = new boolean[items.length];

                for (int i = 0; i < items.length ; i++) {
                    if (etInstructions.getText().toString().toLowerCase().contains(items[i].toLowerCase())){
                        checkedItems[i] = true;
                    }else{
                        checkedItems[i] = false;
                    }
                }
                alert.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                });
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String finalItems = "";
                        for (int i = 0; i < items.length ; i++) {
                            if (checkedItems[i] == true){
                                finalItems = finalItems.concat(items[i] + " , ");
                            }
                        }
                        if (finalItems.length()>2)
                            etInstructions.setText(finalItems.substring(0,finalItems.length()-2));
                    }
                });
                alert.show();
            }
        });


        final LinearLayout holderArtical = articalModel.rootView.findViewById(R.id.holderArtical);

        autoSearchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Artical_Selector_Dialog dialog =
                        Artical_Selector_Dialog.newInstance();
                Artical_Selector_Dialog.dataListSuggestions = standardItems;
                dialog.listiner = new ArticalSelector() {
                    @Override
                    public void articalSelected(StandardItemsModel data) {
                        try {
                            autoSearchTV.setText(data.name);
                            etUnitVolume.setText(""+data.volume);
                            //  etBreadth.setText(""+standardItems.get(i).length);
                            etWeight.setText(""+data.weight);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                dialog.show(getSupportFragmentManager() , Request_Details.TAG);

            }
        });

      /*  etHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etBreadth.getText().toString().isEmpty() || etHeight.getText().toString().isEmpty() || etWidth.getText().toString().isEmpty()){
                    etUnitVolume.setText("");
                }else if (!etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int qty = Integer.parseInt(etQuentity.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int total = height * width * breadth * qty;
                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else if (etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else{
                    etUnitVolume.setText("");
                }
            }
        });
        etWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etBreadth.getText().toString().isEmpty() || etHeight.getText().toString().isEmpty() || etWidth.getText().toString().isEmpty()){
                    etUnitVolume.setText("");
                }else if (!etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int qty = Integer.parseInt(etQuentity.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int total = height * width * breadth * qty;
                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else if (etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else{
                    etUnitVolume.setText("");
                }
            }
        });
        etBreadth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etBreadth.getText().toString().isEmpty() || etHeight.getText().toString().isEmpty() || etWidth.getText().toString().isEmpty()){
                    etUnitVolume.setText("");
                }else if (!etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int qty = Integer.parseInt(etQuentity.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int total = height * width * breadth * qty;
                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else if (etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else{
                    etUnitVolume.setText("");
                }
            }
        });
        etQuentity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etBreadth.getText().toString().isEmpty() || etHeight.getText().toString().isEmpty() || etWidth.getText().toString().isEmpty()){
                    etUnitVolume.setText("");
                }else if (!etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int qty = Integer.parseInt(etQuentity.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int total = height * width * breadth * qty;
                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else if (etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else{
                    etUnitVolume.setText("");
                }
            }
        });*/
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holderArtical.getChildCount() > 1){
                    holderArtical.removeView(artical);
                }else{
                    cmn.showToast("Atleast one artical required");
                }
            }
        });
        holderArtical.addView(artical);
    }

    void addSubArtical(ArticalModel articalModel){
        final View artical = getLayoutInflater().inflate(R.layout.row_artical , null);
        ImageView ivDelete = artical.findViewById(R.id.ivDelete);

        TextInputEditText etQuentity = artical.findViewById(R.id.etQuentity);
        TextInputEditText etHeight = artical.findViewById(R.id.etHeight);
        TextInputEditText etWidth = artical.findViewById(R.id.etWidth);
        TextInputEditText etBreadth = artical.findViewById(R.id.etBreadth);
        TextInputEditText etUnitVolume = artical.findViewById(R.id.etUnitVolume);
        final TextInputEditText etWeight = artical.findViewById(R.id.etWeight);
        final  TextInputEditText etInstructions = artical.findViewById(R.id.etInstructions);
        etInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Creating" , "ASM" , "DSM" , "High Value" , "Packed By Owner" , "Heavey Items"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Instructions");
                final boolean[] checkedItems = new boolean[items.length];

                for (int i = 0; i < items.length ; i++) {
                    if (etInstructions.getText().toString().toLowerCase().contains(items[i].toLowerCase())){
                        checkedItems[i] = true;
                    }else{
                        checkedItems[i] = false;
                    }
                }
                alert.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                });
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String finalItems = "";
                        for (int i = 0; i < items.length ; i++) {
                            if (checkedItems[i] == true){
                                finalItems = finalItems.concat(items[i] + " , ");
                            }
                        }
                        if (finalItems.length()>2)
                            etInstructions.setText(finalItems.substring(0,finalItems.length()-2));
                    }
                });
                alert.show();
            }
        });

        final LinearLayout holderArtical = articalModel.rootView.findViewById(R.id.holderArtical);
        final AutoCompleteTextView autoSearchTV = artical.findViewById(R.id.autoSearchTV);
        autoSearchTV.setFocusable(false);
        etQuentity.setText("1");

        autoSearchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Artical_Selector_Dialog dialog =
                        Artical_Selector_Dialog.newInstance();
                dialog.dataListSuggestions = standardItems;
                dialog.dataListSuggestionsFilter = standardItems;
                dialog.listiner = new ArticalSelector() {
                    @Override
                    public void articalSelected(StandardItemsModel data) {
                        try {
                            autoSearchTV.setText(data.name);
                            etUnitVolume.setText(""+data.volume);
                            //  etBreadth.setText(""+standardItems.get(i).length);
                            etWeight.setText(""+data.weight);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                dialog.show(getSupportFragmentManager() , Request_Details.TAG);
            }
        });

     /*   etHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etBreadth.getText().toString().isEmpty() || etHeight.getText().toString().isEmpty() || etWidth.getText().toString().isEmpty()){
                    etUnitVolume.setText("");
                }else if (!etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int qty = Integer.parseInt(etQuentity.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int total = height * width * breadth * qty;
                    int totalUnit = height * width * breadth;
                    etUnitVolume.setText(""+totalUnit);

                }else if (etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else{
                    etUnitVolume.setText("");
                }
            }
        });
        etWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etBreadth.getText().toString().isEmpty() || etHeight.getText().toString().isEmpty() || etWidth.getText().toString().isEmpty()){
                    etUnitVolume.setText("");
                }else if (!etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int qty = Integer.parseInt(etQuentity.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int total = height * width * breadth * qty;
                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else if (etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else{
                    etUnitVolume.setText("");
                }
            }
        });
        etBreadth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etBreadth.getText().toString().isEmpty() || etHeight.getText().toString().isEmpty() || etWidth.getText().toString().isEmpty()){
                    etUnitVolume.setText("");
                }else if (!etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int qty = Integer.parseInt(etQuentity.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else if (etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else{
                    etUnitVolume.setText("");
                }
            }
        });
        etQuentity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etBreadth.getText().toString().isEmpty() || etHeight.getText().toString().isEmpty() || etWidth.getText().toString().isEmpty()){
                    etUnitVolume.setText("");
                }else if (!etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int qty = Integer.parseInt(etQuentity.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int total = height * width * breadth * qty;
                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else if (etQuentity.getText().toString().isEmpty() && !etBreadth.getText().toString().isEmpty() && !etHeight.getText().toString().isEmpty() && !etWidth.getText().toString().isEmpty()){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    int width = Integer.parseInt(etWidth.getText().toString());
                    int  breadth = Integer.parseInt(etBreadth.getText().toString());

                    int totalUnit = height * width * breadth;

                    etUnitVolume.setText(""+totalUnit);

                }else{
                    etUnitVolume.setText("");
                }
            }
        });*/
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (articalModel.moveType == 1){
                    holderArtical.removeView(artical);
                }else {
                    holderArtical.removeView(artical);
                }
            }
        });
        holderArtical.addView(artical);
    }

    void setupListiners(){


        binding.etVehicleType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Small Vehicle" , "Medium Vehicle" , "Large Vehicle"};

                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Vehicle Type");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etVehicleType.setText("" + items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        binding.etShipModeVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Air" , "Road", "Sea" , "rail"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Ship Mode");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etShipModeVehicle.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        binding.etDeliveryTypeVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = getResources().getStringArray(R.array.deliveryType);
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Delivery Type");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etDeliveryTypeVehicle.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });


        binding.insuranceByCmpny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.insuranceByCmpny.setChecked(true);
                binding.insuranceByPMR.setChecked(false);
                binding.insuranceIndiviual.setChecked(false);
            }
        });
        binding.insuranceByPMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.insuranceByCmpny.setChecked(false);
                binding.insuranceByPMR.setChecked(true);
                binding.insuranceIndiviual.setChecked(false);
            }
        });
        binding.insuranceIndiviual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.insuranceByCmpny.setChecked(false);
                binding.insuranceByPMR.setChecked(false);
                binding.insuranceIndiviual.setChecked(true);
            }
        });
        binding.rbLongCarryYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbLongCarryYes.setChecked(true);
                binding.rbLongCarryNo.setChecked(false);
                binding.layLongCarryDistance.setVisibility(View.VISIBLE);
            }
        });
        binding.rbLongCarryNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbLongCarryYes.setChecked(false);
                binding.rbLongCarryNo.setChecked(true);
                binding.layLongCarryDistance.setVisibility(View.GONE);
            }
        });


        binding.rbLongCarryDestinationYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbLongCarryDestinationYes.setChecked(true);
                binding.rbLongCarryDestinationNo.setChecked(false);
                binding.layLongCarryDistanceDestination.setVisibility(View.VISIBLE);
            }
        });
        binding.rbLongCarryDestinationNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbLongCarryDestinationYes.setChecked(false);
                binding.rbLongCarryDestinationNo.setChecked(true);
                binding.layLongCarryDistanceDestination.setVisibility(View.GONE);
            }
        });
        binding.rbAdditionalStopYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbAdditionalStopYes.setChecked(true);
                binding.rbAdditionalStopNo.setChecked(false);
                binding.layNumberofStops.setVisibility(View.VISIBLE);
            }
        });
        binding.rbAdditionalStopNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbAdditionalStopYes.setChecked(false);
                binding.rbAdditionalStopNo.setChecked(true);
                binding.layNumberofStops.setVisibility(View.GONE);
            }
        });

        binding.rbAdditionalStopDestinationYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbAdditionalStopDestinationYes.setChecked(true);
                binding.rbAdditionalStopDestinationNo.setChecked(false);
                binding.layNumberofStopsDestination.setVisibility(View.VISIBLE);
            }
        });

        binding.rbAdditionalStopDestinationNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbAdditionalStopDestinationYes.setChecked(false);
                binding.rbAdditionalStopDestinationNo.setChecked(true);
                binding.layNumberofStopsDestination.setVisibility(View.GONE);
            }
        });

        binding.rbShuttleYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbShuttleYes.setChecked(true);
                binding.rbShuttleNo.setChecked(false);
                binding.layShuttleDistance.setVisibility(View.VISIBLE);
            }
        });

        binding.rbShuttleNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbShuttleYes.setChecked(false);
                binding.rbShuttleNo.setChecked(true);
                binding.layShuttleDistance.setVisibility(View.GONE);
            }
        });

        binding.rbShuttleDestinationYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbShuttleDestinationYes.setChecked(true);
                binding.rbShuttleDestinationNo.setChecked(false);
                binding.layShuttleDistanceDestination.setVisibility(View.VISIBLE);
            }
        });

        binding.rbShuttleDestinationNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbShuttleDestinationYes.setChecked(false);
                binding.rbShuttleDestinationNo.setChecked(true);
                binding.layShuttleDistanceDestination.setVisibility(View.GONE);
            }
        });


        binding.rbStairCarryYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbStairCarryYes.setChecked(true);
                binding.rbStairCarryNo.setChecked(false);
            }
        });

        binding.rbStairCarryNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbStairCarryYes.setChecked(false);
                binding.rbStairCarryNo.setChecked(true);
            }
        });

        binding.rbStairCarryDestinationYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbStairCarryDestinationYes.setChecked(true);
                binding.rbStairCarryDestinationNo.setChecked(false);
            }
        });

        binding.rbStairCarryDestinationNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbStairCarryDestinationYes.setChecked(false);
                binding.rbStairCarryDestinationNo.setChecked(true);
            }
        });

        binding.rbParkingRequirementsYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbParkingRequirementsYes.setChecked(true);
                binding.rbParkingRequirementsNo.setChecked(false);
            }
        });

        binding.rbParkingRequirementsNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbParkingRequirementsYes.setChecked(false);
                binding.rbParkingRequirementsNo.setChecked(true);
            }
        });

        binding.cbAnyPreviousExpinthepastYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbAnyPreviousExpinthepastYes.setChecked(true);
                binding.cbAnyPreviousExpinthepastNo.setChecked(false);
                binding.layWhowastheMover.setVisibility(View.VISIBLE);
            }
        });

        binding.cbAnyPreviousExpinthepastNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbAnyPreviousExpinthepastYes.setChecked(false);
                binding.cbAnyPreviousExpinthepastNo.setChecked(true);
                binding.layWhowastheMover.setVisibility(View.GONE);
            }
        });


        binding.cbStorageTermLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbStorageTermLong.setChecked(true);
                binding.cbStorageTermShort.setChecked(false);
            }
        });

        binding.cbStorageTermShort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbStorageTermLong.setChecked(false);
                binding.cbStorageTermShort.setChecked(true);
            }
        });


        binding.cbStorageAtOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbStorageAtOrigin.setChecked(true);
                binding.cbStorageAtDestination.setChecked(false);
            }
        });

        binding.cbStorageAtDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbStorageAtOrigin.setChecked(false);
                binding.cbStorageAtDestination.setChecked(true);
            }
        });

        binding.cbStorageModeAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbStorageModeAc.setChecked(true);
                binding.cbStorageModeNonAc.setChecked(false);
            }
        });

        binding.cbStorageModeNonAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbStorageModeAc.setChecked(false);
                binding.cbStorageModeNonAc.setChecked(true);
            }
        });


        binding.cbmostfactorNameFacilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbmostfactorNameFacilities.setChecked(true);
                binding.cbmostfactorPrice.setChecked(false);
                binding.cbmostfactorYourPreference.setChecked(false);
            }
        });

        binding.cbmostfactorPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbmostfactorNameFacilities.setChecked(false);
                binding.cbmostfactorPrice.setChecked(true);
                binding.cbmostfactorYourPreference.setChecked(false);
            }
        });

        binding.cbmostfactorYourPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbmostfactorNameFacilities.setChecked(false);
                binding.cbmostfactorPrice.setChecked(false);
                binding.cbmostfactorYourPreference.setChecked(true);
            }
        });


        binding.rbParkingRequirementsDestinationYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbParkingRequirementsDestinationYes.setChecked(true);
                binding.rbParkingRequirementsDestinationNo.setChecked(false);
            }
        });

        binding.rbParkingRequirementsDestinationNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbParkingRequirementsDestinationYes.setChecked(false);
                binding.rbParkingRequirementsDestinationNo.setChecked(true);
            }
        });


        binding.rbExternalElevatorYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbExternalElevatorYes.setChecked(true);
                binding.rbExternalElevatorNo.setChecked(false);
            }
        });

        binding.rbExternalElevatorNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbExternalElevatorYes.setChecked(false);
                binding.rbExternalElevatorNo.setChecked(true);
            }
        });

        binding.rbExternalElevatorDestinationYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbExternalElevatorDestinationYes.setChecked(true);
                binding.rbExternalElevatorDestinationNo.setChecked(false);
            }
        });

        binding.rbExternalElevatorDestinationNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbExternalElevatorDestinationYes.setChecked(false);
                binding.rbExternalElevatorDestinationNo.setChecked(true);
            }
        });

        binding.etShuttleDistanceUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Meter" , "Feet"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Surveyor");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etShuttleDistanceUnit.setText(items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        binding.etShuttleDistanceUnitDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Meter" , "Feet"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Surveyor");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etShuttleDistanceUnitDestination.setText(items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        binding.etLongCarryDistanceUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Meter" , "Feet"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Surveyor");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etLongCarryDistanceUnit.setText(items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        binding.etLongCarryDistanceUnitDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Meter" , "Feet"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Surveyor");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etLongCarryDistanceUnitDestination.setText(items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        binding.etTimeofPacking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmn.showTimePicker(binding.etTimeofPacking);
            }
        });

        binding.etPetSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Male" , "Female"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Pet Sex");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etPetSex.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        binding.etDeliveryTypePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = getResources().getStringArray(R.array.deliveryType);
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Delivery Type");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etDeliveryTypePet.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        binding.etShipMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Air" , "Road", "Sea" , "rail"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Ship Mode");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etShipMode.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        binding.etPetType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Cat" , "Dog", "Horse"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Pet Type");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etPetType.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        binding.etResidenceSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"1BHK","2BHK","3BHK","4BHK","5BHK"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Residence Size");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etResidenceSize.setText(items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });


        binding.etResidenceSizeDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"1BHK","2BHK","3BHK","4BHK","5BHK"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Residence Size");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etResidenceSizeDestination.setText(items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });


        binding.etDateofPacking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmn.showDatePicker(binding.etDateofPacking , inquiryDate);
            }
        });

        binding.etMovingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etDateofPacking.getText().toString().isEmpty()){
                    cmn.showDatePicker(binding.etMovingDate , inquiryDate);
                }else{
                    String dateStr = binding.etDateofPacking.getText().toString();

                    SimpleDateFormat curFormater = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date dateObj = curFormater.parse(dateStr);
                        cmn.showDatePicker(binding.etMovingDate , dateObj);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        binding.etMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Air" , "Rail" , "Road" , "Sea"};

                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Mode");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etMode.setText("" + items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        binding.etLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"FCL" , "LCL" , "Part Load" , "FTL" , "LTL" , "Air Pallet" , "Loose Load"};

                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Mode");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etLoad.setText("" + items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });


        binding.btAddMoreArtical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movePosition == 0){
                    if (movePositionMoveType == 0){// 0 for Move Typ ROAD
                        addArticalMove(movePositionMoveType);
                    }else if (movePositionMoveType == 1){ // 1 for Move Typ SEA
                        addArticalMove(movePositionMoveType);
                    }else if (movePositionMoveType == 2){// 1 for Move Typ AIR
                        addArticalMove(movePositionMoveType);
                    }else if (movePositionMoveType == 3){// 1 for Move Typ RAIL
                        addArticalMove(movePositionMoveType);
                    }
                }else{
                    addArtical(movePosition);
                }
            }
        });

        binding.btSaveSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signatureImage.setImageBitmap(binding.signaturePad.getSignatureBitmap());
                binding.signaturePad.setVisibility(View.GONE);
                binding.signatureImage.setVisibility(View.VISIBLE);
                binding.btSaveSign.setVisibility(View.GONE);
                binding.btClearSign.setText("Reset");
            }
        });

        binding.btSaveSignSurveyor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signatureImageSurveyor.setImageBitmap(binding.signaturePadSurveyor.getSignatureBitmap());
                binding.signaturePadSurveyor.setVisibility(View.GONE);
                binding.signatureImageSurveyor.setVisibility(View.VISIBLE);
                binding.btSaveSignSurveyor.setVisibility(View.GONE);
                binding.btClearSignSurveyor.setText("Reset");
            }
        });

        binding.btClearSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btClearSign.getText().toString().equalsIgnoreCase("reset")){
                    binding.signaturePad.clear();
                    binding.signaturePad.setVisibility(View.VISIBLE);
                    binding.btSaveSign.setVisibility(View.VISIBLE);
                    binding.btClearSign.setText("Clear");
                    binding.signatureImage.setVisibility(View.GONE);
                }else {
                    binding.signaturePad.clear();
                }
            }
        });
        binding.btClearSignSurveyor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btClearSignSurveyor.getText().toString().equalsIgnoreCase("reset")){
                    binding.signaturePadSurveyor.clear();
                    binding.signaturePadSurveyor.setVisibility(View.VISIBLE);
                    binding.btSaveSignSurveyor.setVisibility(View.VISIBLE);
                    binding.btClearSignSurveyor.setText("Clear");
                    binding.signatureImageSurveyor.setVisibility(View.GONE);
                }else {
                    binding.signaturePadSurveyor.clear();
                }
            }
        });

        binding.signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
//Event triggered when the pad is signed
            }

            @Override
            public void onClear() {
//Event triggered when the pad is cleared
            }
        });

        binding.signaturePadSurveyor.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
//Event triggered when the pad is signed
            }

            @Override
            public void onClear() {
//Event triggered when the pad is cleared
            }
        });


        binding.cbStorageNeeded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cbStorageNeeded.isChecked()){
                    binding.cbStorageNeeded.setChecked(true);
                    binding.layStorageNeeded.setVisibility(View.VISIBLE);
                }else{
                    binding.cbStorageNeeded.setChecked(false);
                    binding.layStorageNeeded.setVisibility(View.GONE);
                }
            }
        });

        binding.cbPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cbPet.isChecked()){
                    binding.cbPet.setChecked(true);
                    binding.layPet.setVisibility(View.VISIBLE);
                }else{
                    binding.cbPet.setChecked(false);
                    binding.layPet.setVisibility(View.GONE);
                }
            }
        });
        binding.cbVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cbVehicle.isChecked()){
                    binding.cbVehicle.setChecked(true);
                    binding.layVehicle.setVisibility(View.VISIBLE);
                }else{
                    binding.cbVehicle.setChecked(false);
                    binding.layVehicle.setVisibility(View.GONE);
                }
            }
        });

        binding.etClientDepartureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmn.showDatePicker(binding.etClientDepartureDate);
            }
        });

        binding.etPeriodFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmn.showDatePicker(binding.etPeriodFrom);
            }
        });

        binding.etPeriodTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmn.showDatePicker(binding.etPeriodTo);
            }
        });

        binding.cbKennelAvailibityYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cbKennelAvailibityYes.isChecked()){
                    binding.cbKennelAvailibityYes.setChecked(true);
                    binding.layKannelAvailibity.setVisibility(View.VISIBLE);
                }else{
                    binding.cbKennelAvailibityYes.setChecked(false);
                    binding.layKannelAvailibity.setVisibility(View.GONE);
                }
            }
        });

        binding.cbKennelAvailibityYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbKennelAvailibityYes.setChecked(true);
                binding.cbKennelAvailibityNo.setChecked(false);
                binding.layKannelAvailibity.setVisibility(View.VISIBLE);
            }
        });

        binding.cbKennelAvailibityNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbKennelAvailibityYes.setChecked(false);
                binding.cbKennelAvailibityNo.setChecked(true);
                binding.layKannelAvailibity.setVisibility(View.GONE);
            }
        });

        binding.cbimportpetYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbimportpetNo.setChecked(false);
                binding.cbimportpetYes.setChecked(true);
            }
        });

        binding.cbimportpetNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbimportpetNo.setChecked(true);
                binding.cbimportpetYes.setChecked(false);
            }
        });

        binding.cbTravelledtoEuropeancountriesYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbTravelledtoEuropeancountriesNo.setChecked(false);
                binding.cbTravelledtoEuropeancountriesYes.setChecked(true);
            }
        });
        binding.cbTravelledtoEuropeancountriesNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbTravelledtoEuropeancountriesNo.setChecked(true);
                binding.cbTravelledtoEuropeancountriesYes.setChecked(false);
            }
        });


        binding.cbVaccinationBookletavailableYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbVaccinationBookletavailableNo.setChecked(false);
                binding.cbVaccinationBookletavailableYes.setChecked(true);
            }
        });
        binding.cbVaccinationBookletavailableNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbVaccinationBookletavailableNo.setChecked(true);
                binding.cbVaccinationBookletavailableYes.setChecked(false);
            }
        });

        binding.cbPetInsuranceRequiredYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbPetInsuranceRequiredNo.setChecked(false);
                binding.cbPetInsuranceRequiredYes.setChecked(true);
            }
        });
        binding.cbPetInsuranceRequiredNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbPetInsuranceRequiredNo.setChecked(true);
                binding.cbPetInsuranceRequiredYes.setChecked(false);
            }
        });


        binding.cbNOCCertificateforPetYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbNOCCertificateforPetNo.setChecked(false);
                binding.cbNOCCertificateforPetYes.setChecked(true);
            }
        });
        binding.cbNOCCertificateforPetNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbNOCCertificateforPetNo.setChecked(true);
                binding.cbNOCCertificateforPetYes.setChecked(false);
            }
        });

        binding.cbBloodTestReportYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbBloodTestReportNo.setChecked(false);
                binding.cbBloodTestReportYes.setChecked(true);
            }
        });
        binding.cbBloodTestReportNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbBloodTestReportNo.setChecked(true);
                binding.cbBloodTestReportYes.setChecked(false);
            }
        });

        binding.cbVaccinationCertificateYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbVaccinationCertificateNo.setChecked(false);
                binding.cbVaccinationCertificateYes.setChecked(true);
            }
        });
        binding.cbVaccinationCertificateNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbVaccinationCertificateNo.setChecked(true);
                binding.cbVaccinationCertificateYes.setChecked(false);
            }
        });

        binding.tvRequiredDocumentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityResultLauncher<Intent> launcher=
                        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                            if(result.getResultCode()==RESULT_OK){
                                Uri imageUri=result.getData().getData();
                                String fileName = imageUri.getPath().substring(imageUri.getPath().lastIndexOf("/") + 1) ;
                                cmn.printLog("-----uri"+fileName);
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), imageUri);
                                    requiredPetDocument = cmn.getBase64FromBitmap(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                binding.tvRequiredDocumentName.setText(""+fileName);
                            }else if(result.getResultCode()==ImagePicker.RESULT_ERROR){
                                // Use ImagePicker.Companion.getError(result.getData()) to show an error
                            }
                        });
                ImagePicker.Companion.with(getParent())
                        .crop()
                        .cropOval()
                        .maxResultSize(512,512,true)
                        .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                        .createIntentFromDialog((Function1)(new Function1(){
                            public Object invoke(Object var1){
                                this.invoke((Intent)var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it){
                                Intrinsics.checkNotNullParameter(it,"it");
                                launcher.launch(it);
                            }
                        }));
            }
        });
        binding.ivDeleteRequireDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvRequiredDocumentName.setText("");
                requiredPetDocument = "";
            }
        });

        binding.tvImageOfPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityResultLauncher<Intent> launcher=
                        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                            if(result.getResultCode()==RESULT_OK){
                                Uri imageUri=result.getData().getData();
                                String fileName = imageUri.getPath().substring(imageUri.getPath().lastIndexOf("/") + 1) ;
                                cmn.printLog("-----uri"+fileName);
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), imageUri);
                                    imageOfPet = cmn.getBase64FromBitmap(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                binding.tvImageOfPet.setText(""+fileName);
                            }else if(result.getResultCode()==ImagePicker.RESULT_ERROR){
                                // Use ImagePicker.Companion.getError(result.getData()) to show an error
                            }
                        });
                ImagePicker.Companion.with(getParent())
                        .crop()
                        .cropOval()
                        .maxResultSize(512,512,true)
                        .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                        .createIntentFromDialog((Function1)(new Function1(){
                            public Object invoke(Object var1){
                                this.invoke((Intent)var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it){
                                Intrinsics.checkNotNullParameter(it,"it");
                                launcher.launch(it);
                            }
                        }));
            }
        });


        binding.ivDeletePetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvImageOfPet.setText("");
                imageOfPet = "";
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            cmn.printLog("---------------------------------------- Document details");

        }
    }
/*

    void getSurveyors(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        survyers.clear();
                        JSONArray array = new JSONArray(aResponse);
                        for (int index = 0;index<array.length();index++){
                            survyers.add(new CommanModel(array.optJSONObject(index).optString("id") , array.optJSONObject(index).optString("fullName")));
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
        url = "appuser?Find=ByRole&role=ROLE_SURVEYOR";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }
*/

    void getStandardItems(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        standardItems.clear();
                        JSONObject object = new JSONObject(aResponse);
                        JSONArray array = object.optJSONArray("contant");
                        for (int index = 0;index<array.length();index++){
                            double defaultValue = 0.0;

                            double myDouble = array.optJSONObject(index).optDouble("weight");
                            standardItems.add(new StandardItemsModel(array.optJSONObject(index).optString("id")
                                    , array.optJSONObject(index).optString("name")
                                    ,array.optJSONObject(index).optInt("height")
                                    ,array.optJSONObject(index).optInt("width")
                                    ,array.optJSONObject(index).optInt("length")
                                    ,array.optJSONObject(index).optDouble("volume"),Double.isNaN(myDouble) ? defaultValue : myDouble));
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
        url = "standard-item?page=0&size=1000&searchFields=name";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }
    void getReportDetails(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject obj = new JSONObject(aResponse);

                        setupDetailsData(obj);


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
        url = "surveyreports/"+ SurveyReport.requestData.id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }

    void getinquiryDetails(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject obj = new JSONObject(aResponse);
                        addInquiryData(obj);
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
        url = "inquiry/"+ inquiryId;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }


    void addInquiryData(JSONObject object){
        if (object.optBoolean("petAvailable") == true){
            binding.cbPet.setChecked(true);
            binding.layPet.setVisibility(View.VISIBLE);
            JSONObject petInfo = object.optJSONObject("petInfo");

            if (petInfo.optBoolean("travelledToEuropeanCounty")){
                binding.cbTravelledtoEuropeancountriesYes.setChecked(true);
                binding.cbTravelledtoEuropeancountriesNo.setChecked(false);
            }else if (!petInfo.optBoolean("petImport")){
                binding.cbTravelledtoEuropeancountriesNo.setChecked(true);
                binding.cbTravelledtoEuropeancountriesYes.setChecked(false);
            }

            if (petInfo.optBoolean("travelledToEuropeanCounty")){
                binding.cbimportpetYes.setChecked(true);
                binding.cbimportpetNo.setChecked(false);
            }else if (!petInfo.optBoolean("travelledToEuropeanCounty")){
                binding.cbimportpetNo.setChecked(true);
                binding.cbimportpetYes.setChecked(false);
            }

            if (petInfo.optBoolean("vaccinationBookletAvailable")){
                binding.cbVaccinationBookletavailableYes.setChecked(true);
                binding.cbVaccinationBookletavailableNo.setChecked(false);
            }else if (!petInfo.optBoolean("vaccinationBookletAvailable")){
                binding.cbVaccinationBookletavailableNo.setChecked(true);
                binding.cbVaccinationBookletavailableYes.setChecked(false);
            }

            if (petInfo.optBoolean("petInsuranceRequired")){
                binding.cbPetInsuranceRequiredYes.setChecked(true);
                binding.cbPetInsuranceRequiredNo.setChecked(false);
            }else if (!petInfo.optBoolean("petInsuranceRequired")){
                binding.cbPetInsuranceRequiredYes.setChecked(false);
                binding.cbPetInsuranceRequiredNo.setChecked(true);
            }

            if (petInfo.optBoolean("petNocCertificate")){
                binding.cbNOCCertificateforPetYes.setChecked(true);
                binding.cbNOCCertificateforPetNo.setChecked(false);
            }else if (!petInfo.optBoolean("petNocCertificate")){
                binding.cbNOCCertificateforPetYes.setChecked(false);
                binding.cbNOCCertificateforPetNo.setChecked(true);
            }

            if (petInfo.optBoolean("bloodTestReport")){
                binding.cbBloodTestReportYes.setChecked(true);
                binding.cbBloodTestReportNo.setChecked(false);
            }else if (!petInfo.optBoolean("bloodTestReport")){
                binding.cbBloodTestReportYes.setChecked(false);
                binding.cbBloodTestReportNo.setChecked(true);
            }

            if (petInfo.optBoolean("vaccinationCertificate")){
                binding.cbVaccinationCertificateYes.setChecked(true);
                binding.cbVaccinationCertificateNo.setChecked(false);
            }else if (!petInfo.optBoolean("vaccinationCertificate")){
                binding.cbVaccinationCertificateYes.setChecked(false);
                binding.cbVaccinationCertificateNo.setChecked(true);
            }

            binding.etRemarkIfAny.setText(""+petInfo.optString("remarks"));

            JSONObject pets = petInfo.optJSONArray("pets").optJSONObject(0);
            binding.etPetType.setText(""+pets.optString("petType"));
            binding.etPetOwnerName.setText(""+pets.optString("petOwnerName"));
            binding.etShipMode.setText(""+pets.optString("shipMode"));
            binding.etDeliveryTypePet.setText(""+pets.optString("deliveryType"));
            binding.etBreedName.setText(""+pets.optString("breedName"));
            binding.etPetAge.setText(""+pets.optString("petAge"));
            binding.etPetSex.setText(""+pets.optString("petSex"));
            binding.etPetCitizenship.setText(""+pets.optString("petCitizenship"));
            binding.etPetWeight.setText(""+pets.optString("petWeight"));

            if (pets.optBoolean("kennelAvailability")){
                binding.layKannelAvailibity.setVisibility(View.VISIBLE);
                binding.cbKennelAvailibityYes.setChecked(true);
                binding.cbKennelAvailibityNo.setChecked(false);
                binding.etHeightKanel.setText(""+pets.optString("kennelHeight"));
                binding.etBreathKanel.setText(""+pets.optString("kennelBreadth"));
                binding.etLengthKanel.setText(""+pets.optString("kennelLength"));
            }else{
                binding.cbKennelAvailibityNo.setChecked(true);
                binding.layKannelAvailibity.setVisibility(View.GONE);
                binding.cbKennelAvailibityYes.setChecked(false);
            }



        }

        if (object.optBoolean("vehicleAvailable") == true) {
            binding.cbVehicle.setChecked(true);
            binding.layVehicle.setVisibility(View.VISIBLE);

            JSONObject vehicleInfo = object.optJSONArray("vehicleInfo").optJSONObject(0);

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
        }
        if (!object.optString("inquiryDate").isEmpty()) {
            inquiryDate = object.optLong("inquiryDate");
        }


    }

    void setupDetailsData(JSONObject obj){

   /*     JSONObject inquiry = obj.optJSONObject("inquiryDetail");

        if (inquiry.optString("surveyDate").isEmpty()){
            binding.etDtNtime.setText("");
        }else{
            binding.etDtNtime.setText(""+cmn.getDateTime(inquiry.optLong("surveyDate")));
        }
        binding.etDtNtime.setEnabled(false);

        binding.etSurveyType.setText(""+inquiry.optString("surveyType"));
        binding.etSurveyType.setEnabled(false);
        binding.etSurveyor.setText(""+inquiry.optJSONObject("surveyExecutive").optString("firstName")+" "+inquiry.optJSONObject("surveyExecutive").optString("lastName"));
        binding.etSurveyor.setEnabled(false);
        if (inquiry.optString("packingDate").isEmpty()){
            binding.etDateofPacking.setText("");
        }else{
            binding.etDateofPacking.setText(""+cmn.getDateTime(inquiry.optLong("packingDate")));
        }
        binding.etDateofPacking.setEnabled(false);
        if (inquiry.optString("deliveryDate").isEmpty()){
            binding.etDeliveryNeededOn.setText("");
        }else{
            binding.etDeliveryNeededOn.setText(""+cmn.getDateTime(inquiry.optLong("deliveryDate")));
        }
        binding.etDeliveryNeededOn.setEnabled(false);
        binding.etPickupFloor.setText(""+inquiry.optString("pickupFloor"));
        binding.etPickupFloor.setEnabled(false);
        binding.etDeliveryFloor.setText(""+inquiry.optString("deliveryFloor"));
        binding.etDeliveryFloor.setEnabled(false);
        binding.etOrigin.setText(""+inquiry.optString("originHas"));
        binding.etOrigin.setEnabled(false);
        binding.etDestination.setText(""+inquiry.optString("destinationHas"));
        binding.etDestination.setEnabled(false);
        binding.etCarfAny.setText(""+inquiry.optString("carName"));
        binding.etCarfAny.setEnabled(false);
        binding.etCarfAnyModal.setText(""+inquiry.optString("carModel"));
        binding.layCarfAnyModal.setVisibility(View.VISIBLE);
        binding.etCarfAnyModal.setEnabled(false);

        if (inquiry.optBoolean("storageNeeded")){
            binding.storageNeededNo.setChecked(false);
            binding.storageNeededYes.setChecked(true);
            binding.layStorageNeededAt.setVisibility(View.VISIBLE);

            if (inquiry.optString("storageAt").equalsIgnoreCase("Origin")){
                binding.storageNeededDestination.setChecked(false);
                binding.storageNeededOrigin.setChecked(true);
            }
            if (inquiry.optString("storageAt").equalsIgnoreCase("Destination")){
                binding.storageNeededDestination.setChecked(true);
                binding.storageNeededOrigin.setChecked(false);
            }
            binding.storageNeededDestination.setClickable(false);
            binding.storageNeededOrigin.setClickable(false);
        }else{
            binding.storageNeededNo.setChecked(true);
            binding.storageNeededYes.setChecked(false);
            binding.layStorageNeededAt.setVisibility(View.GONE);
        }
        binding.storageNeededNo.setClickable(false);
        binding.storageNeededYes.setClickable(false);

        if (inquiry.optString("insurance").equalsIgnoreCase("By Individual")){
            binding.insuranceIndiviual.setChecked(true);
            binding.insuranceByPMR.setChecked(false);
            binding.insuranceByCmpny.setChecked(false);

            binding.insuranceIndiviual.setClickable(false);
            binding.insuranceByPMR.setClickable(false);
            binding.insuranceByCmpny.setClickable(false);

        }
        if (inquiry.optString("insurance").equalsIgnoreCase("By pmr")){
            binding.insuranceIndiviual.setChecked(false);
            binding.insuranceByPMR.setChecked(true);
            binding.insuranceByCmpny.setChecked(false);

            binding.insuranceIndiviual.setClickable(false);
            binding.insuranceByPMR.setClickable(false);
            binding.insuranceByCmpny.setClickable(false);

        }

        if (inquiry.optString("insurance").equalsIgnoreCase("By Company")){
            binding.insuranceIndiviual.setChecked(false);
            binding.insuranceByPMR.setChecked(false);
            binding.insuranceByCmpny.setChecked(true);

            binding.insuranceIndiviual.setClickable(false);
            binding.insuranceByPMR.setClickable(false);
            binding.insuranceByCmpny.setClickable(false);

        }

        if (inquiry.optString("departureDate").isEmpty()){
            binding.etDateofClientDeparture.setText("");
        }else{
            binding.etDateofClientDeparture.setText(""+cmn.getDateTime(inquiry.optLong("departureDate")));
        }
        binding.etDateofClientDeparture.setEnabled(false);

        binding.etQuoteRequiredfor.setText(""+inquiry.optString("quoteRequired"));
        binding.etQuoteRequiredfor.setEnabled(false);

        binding.etAllowanceEntitlement.setText(""+inquiry.optString("allowanceOrEntitlement"));
        binding.etAllowanceEntitlement.setEnabled(false);

        binding.etMODE.setText(""+inquiry.optString("mode"));
        binding.etMODE.setEnabled(false);

        binding.etEstimatedWtVol.setText(""+inquiry.optString("estimatedWtOrVol"));
        binding.etEstimatedWtVol.setEnabled(false);

        binding.etAnyOtherQuote.setText(""+inquiry.optString("anyOtherQuoteTakenBy"));
        binding.etAnyOtherQuote.setEnabled(false);

        binding.etAnySpecialReqInstru.setText(""+inquiry.optString("otherInstructions"));
        binding.etAnySpecialReqInstru.setEnabled(false);

        binding.etItemToBeDismanted.setText(""+inquiry.optString("itemsToBeDismantledByPMR"));
        binding.etItemToBeDismanted.setEnabled(false);


        JSONArray artcls = inquiry.optJSONArray("articles");

        if (artcls != null){
            for (int i = 0; i <artcls.length() ; i++) {
                JSONObject temp = artcls.optJSONObject(i);
                addArticalDetails(temp);
            }
        }

        binding.btClearSign.setVisibility(View.GONE);
        binding.btSaveSign.setVisibility(View.GONE);
        binding.signaturePad.setVisibility(View.GONE);
        binding.signatureImage.setVisibility(View.VISIBLE);
        binding.signatureImage.setImageBitmap(cmn.getDecodedImage(obj.optString("signature")));

        binding.titleLabel.setText("Survey Details");*/

    }

    void submitRequest(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject obj = new JSONObject(aResponse);
                        cmn.showToast("Survey Submitted SuccessFully !!");
                        finish();
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

        try {

            JSONObject inquiryDetail = new JSONObject();

            JSONObject inquiryDetailsObj = new JSONObject();

            if (binding.cbPet.isChecked()){
                inquiryDetailsObj.put("petAvailable" , binding.cbPet.isChecked());
                JSONObject petInfo = new JSONObject();

                JSONObject petObj = new JSONObject();
                JSONArray pates = new JSONArray();

                petObj.put("petType" , replaceToNull(binding.etPetType.getText().toString()));
                petObj.put("petOwnerName" , replaceToNull(binding.etPetOwnerName.getText().toString()));
                petObj.put("shipMode" , replaceToNull(binding.etShipMode.getText().toString()));
                petObj.put("deliveryType" , replaceToNull(binding.etDeliveryTypePet.getText().toString()));
                petObj.put("breedName" , replaceToNull(binding.etBreedName.getText().toString()));
                petObj.put("petAge" , replaceToNull(binding.etPetAge.getText().toString()));
                petObj.put("petSex" , replaceToNull(binding.etPetSex.getText().toString()));
                petObj.put("petCitizenship" , replaceToNull(binding.etPetCitizenship.getText().toString()));
                petObj.put("petWeight" , replaceToNull(binding.etPetWeight.getText().toString()));
                if (binding.cbKennelAvailibityYes.isChecked()){
                    petObj.put("kennelAvailability" , true);
                }else{
                    petObj.put("kennelAvailability" , false);
                }
                petObj.put("kennelLength" , replaceToNull(binding.etLengthKanel.getText().toString()));
                petObj.put("kennelBreadth" , replaceToNull(binding.etBreathKanel.getText().toString()));
                petObj.put("kennelHeight" , replaceToNull(binding.etHeightKanel.getText().toString()));
                pates.put(petObj);


                petInfo.put("pets" , pates);
                if (binding.cbimportpetYes.isChecked()){
                    petInfo.put("petImport" , true);
                }else{
                    petInfo.put("petImport" , false);
                }
                if (binding.cbTravelledtoEuropeancountriesYes.isChecked()){
                    petInfo.put("travelledToEuropeanCounty" , "true");
                }else{
                    petInfo.put("travelledToEuropeanCounty" , "false");
                }
                if (binding.cbVaccinationBookletavailableYes.isChecked()){
                    petInfo.put("vaccinationBookletAvailable" , "true");
                }else{
                    petInfo.put("vaccinationBookletAvailable" , "false");
                }
                if (binding.cbPetInsuranceRequiredYes.isChecked()){
                    petInfo.put("petInsuranceRequired" , "true");
                }else{
                    petInfo.put("petInsuranceRequired" , "false");
                }
                if (binding.cbNOCCertificateforPetYes.isChecked()){
                    petInfo.put("petNocCertificate" , "true");
                }else{
                    petInfo.put("petNocCertificate" , "false");
                }
                if (binding.cbBloodTestReportYes.isChecked()){
                    petInfo.put("bloodTestReport" , "true");
                }else{
                    petInfo.put("bloodTestReport" , "false");
                }

                if (binding.cbVaccinationCertificateYes.isChecked()){
                    petInfo.put("vaccinationCertificate" , "true");
                }else{
                    petInfo.put("vaccinationCertificate" , "false");
                }

                if (imageOfPet.isEmpty()){
                    petInfo.put("petImages" , new JSONArray());
                }else{
                    petInfo.put("petImages" , new JSONArray().put(new JSONObject().put("filePath" , imageOfPet).put("fileName" , binding.tvImageOfPet.getText().toString())));
                }

                if (requiredPetDocument.isEmpty()){
                    petInfo.put("petDocuments" , new JSONArray());
                }else{
                    petInfo.put("petDocuments" , new JSONArray().put(new JSONObject().put("filePath" , requiredPetDocument).put("fileName" , binding.tvRequiredDocumentName.getText().toString())));
                }

                petInfo.put("remarks" , replaceToNull(binding.etRemarkIfAny.getText().toString()));

                inquiryDetailsObj.put("petInfo" , petInfo);

            }else{
                inquiryDetailsObj.put("petAvailable" , binding.cbPet.isChecked());
                inquiryDetailsObj.put("petInfo" , JSONObject.NULL);
            }

            if (binding.cbVehicle.isChecked()){
                inquiryDetailsObj.put("vehicleAvailable" , binding.cbVehicle.isChecked());

                JSONObject vehicleInfo = new JSONObject();

                JSONArray vehicleInfoArray = new JSONArray();

                vehicleInfo.put("vehicleType" , replaceToNull(binding.etVehicleType.getText().toString()));
                vehicleInfo.put("vehicleOwnerName" , replaceToNull(binding.etVehicleOwnerName.getText().toString()));
                vehicleInfo.put("shipMode" , replaceToNull(binding.etShipModeVehicle.getText().toString()));
                vehicleInfo.put("deliveryType" , replaceToNull(binding.etDeliveryTypeVehicle.getText().toString()));
                vehicleInfo.put("vehicleMake" , replaceToNull(binding.etVehicleMake.getText().toString()));
                vehicleInfo.put("vehicleModelNumber" , replaceToNull(binding.etVehicleModelNumber.getText().toString()));
                vehicleInfo.put("vehicleRegistrationNumber" , replaceToNull(binding.etVehicleRegistrationNumber.getText().toString()));
                vehicleInfo.put("vehicleColor" , replaceToNull(binding.etVehicleColor.getText().toString()));
                vehicleInfo.put("vehicleEngineCapacity" , replaceToNull(binding.etVehicleEngineCapacity.getText().toString()));
                vehicleInfo.put("emissionStandard" , replaceToNull(binding.etEmissionStandard.getText().toString()));
                vehicleInfo.put("remarks" , replaceToNull(binding.etRemarksVehicle.getText().toString()));


                vehicleInfoArray.put(vehicleInfo);

                inquiryDetailsObj.put("vehicleInfo" , vehicleInfoArray);

            }else{
                inquiryDetailsObj.put("vehicleAvailable" , binding.cbVehicle.isChecked());
                inquiryDetailsObj.put("vehicleInfo" , new JSONArray());
            }

            inquiryDetailsObj.put("id" , inquiryId);



            parameters.put("inquiry" , inquiryDetailsObj);
            parameters.put("surveyRequest" , new JSONObject().put("id" , JSONObject.NULL));
            inquiryDetail.put("id" , JSONObject.NULL);
            inquiryDetail.put("surveyor" , new JSONObject().put("id" , null));
            inquiryDetail.put("packingDate" , cmn.getReverseDate(binding.etDateofPacking.getText().toString()));
            inquiryDetail.put("packingTime" , replaceToNull(binding.etTimeofPacking.getText().toString()));
            //inquiryDetail.put("packingTime" , JSONObject.NULL);
            inquiryDetail.put("movingDate" , cmn.getReverseDate(binding.etMovingDate.getText().toString()));
            inquiryDetail.put("mode" , replaceToNull(binding.etMode.getText().toString()));
            inquiryDetail.put("containerLoad" , replaceToNull(binding.etLoad.getText().toString()));


            boolean longCarry, longCarryDesti;
            String  insurance = null;

            if (binding.rbLongCarryYes.isChecked()){
                longCarry = true;
            }else{
                longCarry = false;
            }

            if (binding.rbLongCarryDestinationYes.isChecked()){
                longCarryDesti = true;
            }else{
                longCarryDesti = false;
            }

            if (binding.insuranceByCmpny.isChecked())
                insurance = "By Company";
            if (binding.insuranceByPMR.isChecked())
                insurance = "By PMR";
            if (binding.insuranceIndiviual.isChecked())
                insurance = "By Individual";

            inquiryDetail.put("insuranceBy" , insurance);
            JSONObject addressOrigin = new JSONObject();
            addressOrigin.put("id" , null);
            addressOrigin.put("residenceSize" ,replaceToNull(binding.etResidenceSize.getText().toString()) );
            addressOrigin.put("floor" ,replaceToNull(binding.etElevatorFloor.getText().toString()) );
            addressOrigin.put("longCarry", longCarry);
            if (longCarry) {
                addressOrigin.put("longCarryDistance" , replaceToNull(binding.etLongCarryDistance.getText().toString()));
                addressOrigin.put("longCarryDistanceUnit" , replaceToNull(binding.etLongCarryDistanceUnit.getText().toString()));
            }else {
                addressOrigin.put("longCarryDistance" , JSONObject.NULL);
                addressOrigin.put("longCarryDistanceUnit" , JSONObject.NULL);
            }

            if (binding.rbStairCarryYes.isChecked()){
                addressOrigin.put("stairCarry" ,true);
            }else{
                addressOrigin.put("stairCarry" ,false);
            }
            if (binding.rbAdditionalStopYes.isChecked()){
                addressOrigin.put("additionalStop" ,true);
                addressOrigin.put("numberOfStops" , replaceToNull(binding.etNumberofStops.getText().toString()));
            }else{
                addressOrigin.put("additionalStop" ,false);
                addressOrigin.put("numberOfStops" , JSONObject.NULL);
            }

            if (binding.rbParkingRequirementsYes.isChecked()){
                addressOrigin.put("parkingRequirements" ,true);
            }else{
                addressOrigin.put("parkingRequirements" ,false);
            }

            if (binding.rbExternalElevatorYes.isChecked()){
                addressOrigin.put("externalElevator" ,true);
            }else{
                addressOrigin.put("externalElevator" ,false);
            }

            if (binding.rbShuttleYes.isChecked()){
                addressOrigin.put("shuttle" ,true);
                addressOrigin.put("shuttleDistance" , replaceToNull(binding.etShuttleDistance.getText().toString()));
                addressOrigin.put("shuttleDistanceUnit" , replaceToNull(binding.etShuttleDistanceUnit.getText().toString()));
            }else{
                addressOrigin.put("shuttle" ,false);
                addressOrigin.put("shuttleDistance" , JSONObject.NULL);
                addressOrigin.put("shuttleDistanceUnit" , JSONObject.NULL);
            }

            addressOrigin.put("accessNotes" , replaceToNull(binding.etAccessNotes.getText().toString()));


            JSONObject addressDestination = new JSONObject();
            addressDestination.put("id" , null);
            addressDestination.put("residenceSize" ,replaceToNull(binding.etResidenceSizeDestination.getText().toString()) );
            addressDestination.put("floor" ,replaceToNull(binding.etElevatorFloorDestination.getText().toString()) );
            addressDestination.put("longCarry", longCarryDesti);
            if (longCarry) {
                addressDestination.put("longCarryDistance" , replaceToNull(binding.etLongCarryDistanceDestination.getText().toString()));
                addressDestination.put("longCarryDistanceUnit" , replaceToNull(binding.etLongCarryDistanceUnitDestination.getText().toString()));
            }else{
                addressDestination.put("longCarryDistance" , JSONObject.NULL);
                addressDestination.put("longCarryDistanceUnit" , JSONObject.NULL);
            }


            if (binding.rbStairCarryDestinationYes.isChecked()){
                addressDestination.put("stairCarry" ,true);
            }else{
                addressDestination.put("stairCarry" ,false);
            }
            if (binding.rbAdditionalStopDestinationYes.isChecked()){
                addressDestination.put("additionalStop" ,true);
                addressDestination.put("numberOfStops" , replaceToNull(binding.etNumberofStopsDestination.getText().toString()));
            }else{
                addressDestination.put("additionalStop" ,false);
                addressDestination.put("numberOfStops" , JSONObject.NULL);
            }

            if (binding.rbParkingRequirementsDestinationYes.isChecked()){
                addressDestination.put("parkingRequirements" ,true);
            }else{
                addressDestination.put("parkingRequirements" ,false);
            }

            if (binding.rbExternalElevatorDestinationYes.isChecked()){
                addressDestination.put("externalElevator" ,true);
            }else{
                addressDestination.put("externalElevator" ,false);
            }

            if (binding.rbShuttleDestinationYes.isChecked()){
                addressDestination.put("shuttle" ,true);
                addressDestination.put("shuttleDistance" , replaceToNull(binding.etShuttleDistanceDestination.getText().toString()));
                addressDestination.put("shuttleDistanceUnit" , replaceToNull(binding.etShuttleDistanceUnitDestination.getText().toString()));
            }else{
                addressDestination.put("shuttle" ,false);
                addressDestination.put("shuttleDistance" , JSONObject.NULL);
                addressDestination.put("shuttleDistanceUnit" , JSONObject.NULL);
            }

            addressDestination.put("accessNotes" , replaceToNull(binding.etAccessNotesDestination.getText().toString()));


            inquiryDetail.put("originAddressDetail" , addressOrigin);
            inquiryDetail.put("destinationAddressDetail" , addressDestination);

            if (binding.cbStorageNeeded.isChecked()){
                if (binding.cbStorageModeAc.isChecked()){
                    inquiryDetail.put("storageMode" ,"Ac");
                }else {
                    inquiryDetail.put("storageMode" ,"Non AC");
                }
                if (binding.cbStorageAtOrigin.isChecked()){
                    inquiryDetail.put("storageAt" ,"Ac");
                }else {
                    inquiryDetail.put("storageAt" ,"Non AC");
                }

                if (binding.cbStorageTermLong.isChecked()){
                    inquiryDetail.put("storageTerm" ,"Long Term");
                }else {
                    inquiryDetail.put("storageTerm" ,"Short Term");
                }
                inquiryDetail.put("storagePeriodFrom" , replaceToNull(binding.etPeriodFrom.getText().toString()));
                inquiryDetail.put("storagePeriodTo" , replaceToNull(binding.etPeriodTo.getText().toString()));
            }else {
                inquiryDetail.put("storageMode" ,null);
                inquiryDetail.put("storageAt" ,null);
                inquiryDetail.put("storageTerm" ,null);
                inquiryDetail.put("storagePeriodFrom" ,null);
                inquiryDetail.put("storagePeriodTo" ,null);
            }


            inquiryDetail.put("seaAllowance" , replaceToNull(binding.etSea.getText().toString()));
            inquiryDetail.put("airAllowance" , replaceToNull(binding.etAir.getText().toString()));
            inquiryDetail.put("surfaceAllowance" , replaceToNull(binding.etSurface.getText().toString()));
            inquiryDetail.put("departureDate" , cmn.getReverseDate(binding.etClientDepartureDate.getText().toString()));
            inquiryDetail.put("portOfDeparture" , replaceToNull(binding.etPortofDeparture.getText().toString()));
            inquiryDetail.put("portOfEntry" , replaceToNull(binding.etPortofEntry.getText().toString()));
            inquiryDetail.put("generalComments" , replaceToNull(binding.etGeneralCommentsforAllModes.getText().toString()));

            if (binding.cbAnyPreviousExpinthepastYes.isChecked()){
                inquiryDetail.put("previousExp" , true);
                inquiryDetail.put("previousMover" , replaceToNull(binding.etWhowastheMover.getText().toString()));
            }else{
                inquiryDetail.put("previousMover" , JSONObject.NULL);
                inquiryDetail.put("previousExp" , true);
            }

            if (binding.cbmostfactorYourPreference.isChecked()){
                inquiryDetail.put("decidingFactor" , "Client Preference");
            }else if (binding.cbmostfactorPrice.isChecked()){
                inquiryDetail.put("decidingFactor" , "Price");
            }else{
                inquiryDetail.put("decidingFactor" , "Mover name and Facilities");
            }

            inquiryDetail.put("anyOtherQuoteTakenBy" , replaceToNull(binding.etAnyotherquotetakenifyes.getText().toString()));

            JSONArray articlesArrMovable = new JSONArray();
            if (movingItemsSEA.size()>0){
                for (int i = 0; i < movingItemsSEA.size() ; i++) {
                    View artical = movingItemsSEA.get(i);

                    final TextInputEditText etRoomName = artical.findViewById(R.id.etRoomName);
                    LinearLayout holderArtical = artical.findViewById(R.id.holderArtical);

                    JSONObject obj = new JSONObject();
                    JSONArray areaTypesSEA = new JSONArray();

                    obj.put("mode" , "SEA");

                    for (int j = 0; j <holderArtical.getChildCount() ; j++) {
                        View newView = holderArtical.getChildAt(i);
                        AutoCompleteTextView autoSearchTV = newView.findViewById(R.id.autoSearchTV);
                        TextInputEditText etInstructions = newView.findViewById(R.id.etInstructions);
                        TextInputEditText etQuentity = newView.findViewById(R.id.etQuentity);
                        TextInputEditText etHeight = newView.findViewById(R.id.etHeight);
                        TextInputEditText etWidth = newView.findViewById(R.id.etWidth);
                        TextInputEditText etBreadth = newView.findViewById(R.id.etBreadth);
                        TextInputEditText etUnitVolume = newView.findViewById(R.id.etUnitVolume);
                        TextInputEditText etWeight = artical.findViewById(R.id.etWeight);
                        TextInputEditText etRemarks = newView.findViewById(R.id.etRemarks);
                        JSONObject temp = new JSONObject();

                        JSONObject articalObj = new JSONObject();

                        temp.put("name" , replaceToNull(autoSearchTV.getText().toString()));
                        temp.put("quantity" , replaceToNull(etQuentity.getText().toString()));
                        temp.put("height" , replaceToNull(etHeight.getText().toString()));
                        temp.put("width" , replaceToNull(etWidth.getText().toString()));
                        temp.put("breadth" , replaceToNull(etBreadth.getText().toString()));
                        temp.put("volume" , replaceToNull(etUnitVolume.getText().toString()));
                        temp.put("weight" , replaceToNull(etWeight.getText().toString()));
                        JSONArray instructionsArray = new JSONArray();
                        String[] instructions = etInstructions.getText().toString().split(",");

                        for (int k = 0; k < instructions.length ; k++) {
                            instructionsArray.put(instructions[k]);
                        }
                        temp.put("instructions" , instructionsArray);
                        temp.put("remarks" , replaceToNull(etRemarks.getText().toString()));
                        temp.put("mode" , "SEA");

                        articalObj.put("areaType" , etRoomName.getText().toString());
                        articalObj.put("articles", new JSONArray().put(temp));
                        areaTypesSEA.put(articalObj);
                    }
                    obj.put("areaTypes" , areaTypesSEA);
                    articlesArrMovable.put(obj);
                }
            }else{
                JSONObject obj = new JSONObject();
                obj.put("mode" , "SEA");
                obj.put("areaTypes" , new JSONArray());
                articlesArrMovable.put(obj);
            }
            if (movingItemsAir.size()>0) {
                for (int i = 0; i < movingItemsAir.size(); i++) {
                    View artical = movingItemsAir.get(i);

                    final TextInputEditText etRoomName = artical.findViewById(R.id.etRoomName);
                    LinearLayout holderArtical = artical.findViewById(R.id.holderArtical);

                    JSONObject obj = new JSONObject();
                    JSONArray movingItemsAir = new JSONArray();

                    obj.put("mode", "Air");

                    for (int j = 0; j < holderArtical.getChildCount(); j++) {
                        View newView = holderArtical.getChildAt(i);
                        AutoCompleteTextView autoSearchTV = newView.findViewById(R.id.autoSearchTV);
                        TextInputEditText etInstructions = newView.findViewById(R.id.etInstructions);
                        TextInputEditText etQuentity = newView.findViewById(R.id.etQuentity);
                        TextInputEditText etHeight = newView.findViewById(R.id.etHeight);
                        TextInputEditText etWidth = newView.findViewById(R.id.etWidth);
                        TextInputEditText etBreadth = newView.findViewById(R.id.etBreadth);
                        TextInputEditText etUnitVolume = newView.findViewById(R.id.etUnitVolume);
                        TextInputEditText etWeight = newView.findViewById(R.id.etWeight);
                        TextInputEditText etRemarks = newView.findViewById(R.id.etRemarks);

                        JSONObject temp = new JSONObject();

                        temp.put("name", replaceToNull(autoSearchTV.getText().toString()));
                        temp.put("quantity", replaceToNull(etQuentity.getText().toString()));
                        temp.put("height", replaceToNull(etHeight.getText().toString()));
                        temp.put("width", replaceToNull(etWidth.getText().toString()));
                        temp.put("breadth", replaceToNull(etBreadth.getText().toString()));
                        temp.put("volume", replaceToNull(etUnitVolume.getText().toString()));
                        temp.put("weight", replaceToNull(etWeight.getText().toString()));
                        JSONArray instructionsArray = new JSONArray();
                        String[] instructions = etInstructions.getText().toString().split(",");

                        for (int k = 0; k < instructions.length; k++) {
                            instructionsArray.put(instructions[k]);
                        }
                        temp.put("instructions", instructionsArray);
                        temp.put("remarks", replaceToNull(etRemarks.getText().toString()));
                        temp.put("mode", "Air");

                        JSONObject articalObj = new JSONObject();
                        articalObj.put("areaType", etRoomName.getText().toString());
                        articalObj.put("articles", new JSONArray().put(temp));
                        movingItemsAir.put(articalObj);

                    }
                    obj.put("areaTypes", movingItemsAir);
                    articlesArrMovable.put(obj);
                }
            }else{
                JSONObject obj = new JSONObject();
                obj.put("mode" , "Air");
                obj.put("areaTypes" , new JSONArray());
                articlesArrMovable.put(obj);
            }
            if (movingItemsRail.size()>0) {
                for (int i = 0; i < movingItemsRail.size(); i++) {
                    View artical = movingItemsRail.get(i);

                    final TextInputEditText etRoomName = artical.findViewById(R.id.etRoomName);
                    LinearLayout holderArtical = artical.findViewById(R.id.holderArtical);

                    JSONObject obj = new JSONObject();
                    JSONArray movingItemsRail = new JSONArray();

                    obj.put("mode", "Rail");

                    for (int j = 0; j < holderArtical.getChildCount(); j++) {
                        View newView = holderArtical.getChildAt(i);
                        AutoCompleteTextView autoSearchTV = newView.findViewById(R.id.autoSearchTV);
                        TextInputEditText etInstructions = newView.findViewById(R.id.etInstructions);
                        TextInputEditText etQuentity = newView.findViewById(R.id.etQuentity);
                        TextInputEditText etHeight = newView.findViewById(R.id.etHeight);
                        TextInputEditText etWidth = newView.findViewById(R.id.etWidth);
                        TextInputEditText etBreadth = newView.findViewById(R.id.etBreadth);
                        TextInputEditText etUnitVolume = newView.findViewById(R.id.etUnitVolume);
                        TextInputEditText etWeight = newView.findViewById(R.id.etWeight);
                        TextInputEditText etRemarks = newView.findViewById(R.id.etRemarks);

                        JSONObject temp = new JSONObject();

                        temp.put("mode", "Rail");
                        temp.put("name", replaceToNull(autoSearchTV.getText().toString()));
                        temp.put("quantity", replaceToNull(etQuentity.getText().toString()));
                        temp.put("height", replaceToNull(etHeight.getText().toString()));
                        temp.put("width", replaceToNull(etWidth.getText().toString()));
                        temp.put("breadth", replaceToNull(etBreadth.getText().toString()));
                        temp.put("volume", replaceToNull(etUnitVolume.getText().toString()));
                        temp.put("weight", replaceToNull(etWeight.getText().toString()));
                        JSONArray instructionsArray = new JSONArray();
                        String[] instructions = etInstructions.getText().toString().split(",");

                        for (int k = 0; k < instructions.length; k++) {
                            instructionsArray.put(instructions[k]);
                        }
                        temp.put("instructions", instructionsArray);
                        temp.put("remarks", replaceToNull(etRemarks.getText().toString()));

                        JSONObject articalObj = new JSONObject();
                        articalObj.put("areaType", etRoomName.getText().toString());
                        articalObj.put("articles", new JSONArray().put(temp));
                        movingItemsRail.put(articalObj);

                    }
                    obj.put("areaTypes", movingItemsRail);
                    articlesArrMovable.put(obj);
                }
            }else{
                JSONObject obj = new JSONObject();
                obj.put("mode" , "Rail");
                obj.put("areaTypes" , new JSONArray());
                articlesArrMovable.put(obj);
            }
            if (movingItemsRoad.size()>0) {
                for (int i = 0; i < movingItemsRoad.size(); i++) {
                    View artical = movingItemsRoad.get(i);

                    final TextInputEditText etRoomName = artical.findViewById(R.id.etRoomName);
                    LinearLayout holderArtical = artical.findViewById(R.id.holderArtical);

                    JSONObject obj = new JSONObject();
                    JSONArray movingItemsRoad = new JSONArray();

                    obj.put("mode", "Road");

                    for (int j = 0; j < holderArtical.getChildCount(); j++) {
                        View newView = holderArtical.getChildAt(i);
                        AutoCompleteTextView autoSearchTV = newView.findViewById(R.id.autoSearchTV);
                        TextInputEditText etInstructions = newView.findViewById(R.id.etInstructions);
                        TextInputEditText etQuentity = newView.findViewById(R.id.etQuentity);
                        TextInputEditText etHeight = newView.findViewById(R.id.etHeight);
                        TextInputEditText etWidth = newView.findViewById(R.id.etWidth);
                        TextInputEditText etBreadth = newView.findViewById(R.id.etBreadth);
                        TextInputEditText etUnitVolume = newView.findViewById(R.id.etUnitVolume);
                        TextInputEditText etWeight = newView.findViewById(R.id.etWeight);
                        TextInputEditText etRemarks = newView.findViewById(R.id.etRemarks);
                        JSONObject temp = new JSONObject();

                        temp.put("mode", "Road");
                        temp.put("name", replaceToNull(autoSearchTV.getText().toString()));
                        temp.put("volume", replaceToNull(etUnitVolume.getText().toString()));
                        temp.put("height", replaceToNull(etHeight.getText().toString()));
                        temp.put("width", replaceToNull(etWidth.getText().toString()));
                        temp.put("breadth", replaceToNull(etBreadth.getText().toString()));
                        temp.put("quantity", replaceToNull(etQuentity.getText().toString()));
                        temp.put("weight", replaceToNull(etWeight.getText().toString()));
                        JSONArray instructionsArray = new JSONArray();
                        String[] instructions = etInstructions.getText().toString().split(",");

                        for (int k = 0; k < instructions.length; k++) {
                            instructionsArray.put(instructions[k]);
                        }
                        temp.put("instructions", instructionsArray);
                        temp.put("remark", replaceToNull(etRemarks.getText().toString()));
                        temp.put("remarks", replaceToNull(etRemarks.getText().toString()));

                        JSONObject articalObj = new JSONObject();
                        articalObj.put("areaType", etRoomName.getText().toString());
                        articalObj.put("articles", new JSONArray().put(temp));
                        movingItemsRoad.put(articalObj);

                    }
                    obj.put("areaTypes", movingItemsRoad);
                    articlesArrMovable.put(obj);
                }
            }else{
                JSONObject obj = new JSONObject();
                obj.put("mode" , "Road");
                obj.put("areaTypes" , new JSONArray());
                articlesArrMovable.put(obj);
            }
            inquiryDetail.put("movingItems" , articlesArrMovable);

            JSONArray temp = new JSONArray();
            for (int i = 0; i < binding.surveyArticalHolderNotMoving.getChildCount() ; i++) {
                View artical = binding.surveyArticalHolderNotMoving.getChildAt(i);

                final TextInputEditText etRoomName = artical.findViewById(R.id.etRoomName);
                LinearLayout holderArtical = artical.findViewById(R.id.holderArtical);

                JSONObject obj = new JSONObject();
                JSONArray areaTypesSEA = new JSONArray();
                obj.put("mode" , JSONObject.NULL);
                for (int j = 0; j <holderArtical.getChildCount() ; j++) {
                    View newView = holderArtical.getChildAt(i);
                    AutoCompleteTextView autoSearchTV = newView.findViewById(R.id.autoSearchTV);
                    TextInputEditText etInstructions = newView.findViewById(R.id.etInstructions);
                    TextInputEditText etQuentity = newView.findViewById(R.id.etQuentity);
                    TextInputEditText etHeight = newView.findViewById(R.id.etHeight);
                    TextInputEditText etWidth = newView.findViewById(R.id.etWidth);
                    TextInputEditText etBreadth = newView.findViewById(R.id.etBreadth);
                    TextInputEditText etUnitVolume = newView.findViewById(R.id.etUnitVolume);
                    TextInputEditText etWeight = newView.findViewById(R.id.etWeight);
                    TextInputEditText etRemarks = newView.findViewById(R.id.etRemarks);

                    JSONObject tempObj = new JSONObject();

                    tempObj.put("mode" , JSONObject.NULL);
                    tempObj.put("name" , replaceToNull(autoSearchTV.getText().toString()));
                    tempObj.put("quantity" , replaceToNull(etQuentity.getText().toString()));
                    tempObj.put("height" , replaceToNull(etHeight.getText().toString()));
                    tempObj.put("width" , replaceToNull(etWidth.getText().toString()));
                    tempObj.put("breadth" , replaceToNull(etBreadth.getText().toString()));
                    tempObj.put("volume" , replaceToNull(etUnitVolume.getText().toString()));
                    tempObj.put("weight", replaceToNull(etWeight.getText().toString()));
                    JSONArray instructionsArray = new JSONArray();
                    String[] instructions = etInstructions.getText().toString().split(",");

                    for (int k = 0; k < instructions.length ; k++) {
                        instructionsArray.put(instructions[k]);
                    }
                    tempObj.put("instructions" , instructionsArray);
                    tempObj.put("remarks" , replaceToNull(etRemarks.getText().toString()));


                    JSONObject articalObj = new JSONObject();
                    articalObj.put("areaType" , etRoomName.getText().toString());
                    articalObj.put("articles", new JSONArray().put(tempObj));

                    areaTypesSEA.put(articalObj);

                }
                obj.put("areaTypes" , areaTypesSEA);
                temp.put(obj);
            }

            JSONArray articlesArrmaybeMovingItems = new JSONArray();
            for (int i = 0; i < binding.surveyArticalHolderMayBeMoving.getChildCount() ; i++) {
                View artical = binding.surveyArticalHolderMayBeMoving.getChildAt(i);

                final TextInputEditText etRoomName = artical.findViewById(R.id.etRoomName);
                LinearLayout holderArtical = artical.findViewById(R.id.holderArtical);

                JSONObject obj = new JSONObject();
                JSONArray areaTypesSEA = new JSONArray();
                obj.put("mode" , JSONObject.NULL);
                for (int j = 0; j <holderArtical.getChildCount() ; j++) {
                    View newView = holderArtical.getChildAt(i);
                    AutoCompleteTextView autoSearchTV = newView.findViewById(R.id.autoSearchTV);
                    TextInputEditText etInstructions = newView.findViewById(R.id.etInstructions);
                    TextInputEditText etQuentity = newView.findViewById(R.id.etQuentity);
                    TextInputEditText etHeight = newView.findViewById(R.id.etHeight);
                    TextInputEditText etWidth = newView.findViewById(R.id.etWidth);
                    TextInputEditText etBreadth = newView.findViewById(R.id.etBreadth);
                    TextInputEditText etUnitVolume = newView.findViewById(R.id.etUnitVolume);
                    TextInputEditText etRemarks = newView.findViewById(R.id.etRemarks);
                    TextInputEditText etWeight = newView.findViewById(R.id.etWeight);

                    JSONObject tempObj = new JSONObject();

                    tempObj.put("mode" , JSONObject.NULL);
                    tempObj.put("name" , replaceToNull(autoSearchTV.getText().toString()));
                    tempObj.put("quantity" , replaceToNull(etQuentity.getText().toString()));
                    tempObj.put("height" , replaceToNull(etHeight.getText().toString()));
                    tempObj.put("width" , replaceToNull(etWidth.getText().toString()));
                    tempObj.put("breadth" , replaceToNull(etBreadth.getText().toString()));
                    tempObj.put("volume" , replaceToNull(etUnitVolume.getText().toString()));
                    tempObj.put("weight", replaceToNull(etWeight.getText().toString()));

                    JSONArray instructionsArray = new JSONArray();
                    String[] instructions = etInstructions.getText().toString().split(",");

                    for (int k = 0; k < instructions.length ; k++) {
                        instructionsArray.put(instructions[k]);
                    }
                    tempObj.put("instructions" , instructionsArray);
                    tempObj.put("remarks" , replaceToNull(etRemarks.getText().toString()));


                    JSONObject articalObj = new JSONObject();
                    articalObj.put("areaType" , etRoomName.getText().toString());
                    articalObj.put("articles", new JSONArray().put(tempObj));

                    areaTypesSEA.put(articalObj);

                }
                obj.put("areaTypes" , areaTypesSEA);
                articlesArrmaybeMovingItems.put(obj);
            }

          //  temp.put(new JSONObject().put("mode" , JSONObject.NULL).put("areaTypes" , new JSONArray()));

            if (temp.length()>0) {
                inquiryDetail.put("notMovingItems", temp);
            }else{
                JSONObject obj = new JSONObject();
                obj.put("mode" , JSONObject.NULL);
                obj.put("areaTypes" , new JSONArray());
                inquiryDetail.put("notMovingItems", new JSONArray().put(obj));
            }

            if (articlesArrmaybeMovingItems.length()>0) {
                inquiryDetail.put("maybeMovingItems", articlesArrmaybeMovingItems);
            }else{
                JSONObject obj = new JSONObject();
                obj.put("mode" , JSONObject.NULL);
                obj.put("areaTypes" , new JSONArray());
                inquiryDetail.put("maybeMovingItems", new JSONArray().put(obj));
            }

            inquiryDetail.put("surveyType" , "TELEPHONIC_SURVEY");

            Log.e("all params " , parameters.toString());

            parameters.put("inquiryDetail" , inquiryDetail);
            parameters.put("shipperSignature" , "data:image/png;base64,"+cmn.getBase64FromBitmap(binding.signaturePad.getSignatureBitmap()));
            parameters.put("surveyorSignature" , "data:image/png;base64,"+cmn.getBase64FromBitmap(binding.signaturePadSurveyor.getSignatureBitmap()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";
        url = "surveyreport";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.postAPI();
    }


    String replaceToNull(String str){
        if (str.isEmpty())
            return null;
        else return str;
    }

}