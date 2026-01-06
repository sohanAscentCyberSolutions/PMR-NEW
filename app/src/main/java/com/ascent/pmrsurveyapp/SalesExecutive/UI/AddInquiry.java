package com.ascent.pmrsurveyapp.SalesExecutive.UI;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.ascent.pmrsurveyapp.DBHelper.CityModel;
import com.ascent.pmrsurveyapp.DBHelper.CountryModel;
import com.ascent.pmrsurveyapp.DBHelper.DatabaseHelper;
import com.ascent.pmrsurveyapp.DBHelper.StateModel;
import com.ascent.pmrsurveyapp.Fragments.ItemSelecter;
import com.ascent.pmrsurveyapp.Fragments.Selection_Dialog;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.OfflineInquryModel;
import com.ascent.pmrsurveyapp.UI.Dashboard;
import com.ascent.pmrsurveyapp.UI.LogIn;
import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.ActivityAddAccountBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityAddInquiryBinding;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.file_picker.FilePicker;
import com.github.file_picker.FileType;
import com.google.android.material.textfield.TextInputEditText;
import com.rmartinper.filepicker.controller.DialogSelectionListener;
import com.rmartinper.filepicker.model.DialogConfigs;
import com.rmartinper.filepicker.model.DialogProperties;
import com.rmartinper.filepicker.view.FilePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;

public class AddInquiry extends AppCompatActivity implements ItemSelecter {

    Comman cmn;
    Activity mActivity;
    ActivityAddInquiryBinding binding;

    ItemSelecter selecter;
    ArrayList<CommanModel> countryList = new ArrayList<>();
    ArrayList<CommanModel> companyList = new ArrayList<>();

    ArrayList<CommanModel> statesListOrigin = new ArrayList<>();
    ArrayList<CommanModel> statesListOtherAddress = new ArrayList<>();
    ArrayList<CommanModel> statesListDestination = new ArrayList<>();

    ArrayList<CommanModel> cityListOrigin = new ArrayList<>();
    ArrayList<CommanModel> cityListOtherAddress = new ArrayList<>();
    ArrayList<CommanModel> cityListDestination = new ArrayList<>();


    CommanModel selectedSuggestionModel;
    ArrayList<CommanModel> dataListSuggestions = new ArrayList<>();


    CommanModel selectedOriginCountry;
    CommanModel selectedOriginState;
    CommanModel selectedOriginCity;

    CommanModel selectedDesinationCountry;
    CommanModel selectedDesinationState;
    CommanModel selectedDesitnationCity;
    CommanModel selectedCompany;

    String selectedPetDoc = null;
    String selectedPetImage = null;


    String inquiryId = "";
    Boolean isEdit = false;
    String originAddId = "";
    String destAddId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_inquiry);
        mActivity = this;
        cmn = new Comman(mActivity);
        selecter = this::itemSelected;
        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        isEdit = getIntent().getBooleanExtra("isEdit" , false);

        if (isEdit){
            inquiryId =  getIntent().getStringExtra("id");
            getInquiryData();
            getCountries();
            getCompany();
            getAccountData();
            binding.btAdd.setText("Update");
        }else{
            getCountries();
            getCompany();
            getAccountData();
        }
        addListeners();
    }


    void addListeners(){
        binding.btSaveOffLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllFieldsAreValid()){
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(r -> {
                        // Find the current maximum ID
                        String uniqueId = UUID.randomUUID().toString();
                        // Create a new User with the new ID
                        OfflineInquryModel data = r.createObject(OfflineInquryModel.class, uniqueId);
                        data.setdata(getAllParams().toString());
                        r.insertOrUpdate(data);
                        cmn.showToast("Saved Successfully !!");
                        if (cmn.getUserType().equalsIgnoreCase(cmn.userSurveyor)){
                            runOnUiThread(() -> {
                                startActivity(new Intent(AddInquiry.this, Dashboard.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            });

                        }else{
                            runOnUiThread(() -> {
                                finish();
                            });
                        }
                    });
                    realm.close();
                }
            }
        });
        binding.etCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selection_Dialog newFragment = new Selection_Dialog();
                newFragment.dataList = companyList;
                newFragment.listiner = selecter;
                newFragment.requestCode = 44;
                newFragment.titleStr = "Select Company";
                newFragment.show(getSupportFragmentManager() , "temp");
            }
        });
        binding.etCountryOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selection_Dialog newFragment = new Selection_Dialog();
                newFragment.dataList = countryList;
                newFragment.listiner = selecter;
                newFragment.requestCode = 1;
                newFragment.titleStr = "Select Country";
                newFragment.show(getSupportFragmentManager() , "temp");
            }
        });

        binding.etCountryDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selection_Dialog newFragment = new Selection_Dialog();
                newFragment.dataList = countryList;
                newFragment.listiner = selecter;
                newFragment.requestCode = 11;
                newFragment.titleStr = "Select Country";
                newFragment.show(getSupportFragmentManager() , "temp");
            }
        });

        binding.etStateOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selection_Dialog newFragment = new Selection_Dialog();
                newFragment.dataList = statesListOrigin;
                newFragment.listiner = selecter;
                newFragment.requestCode = 2;
                newFragment.titleStr = "Select State";
                newFragment.show(getSupportFragmentManager() , "temp");
            }
        });

        binding.etStateDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selection_Dialog newFragment = new Selection_Dialog();
                newFragment.dataList = statesListDestination;
                newFragment.listiner = selecter;
                newFragment.requestCode = 22;
                newFragment.titleStr = "Select State";
                newFragment.show(getSupportFragmentManager() , "temp");
            }
        });

        binding.etCityOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selection_Dialog newFragment = new Selection_Dialog();
                newFragment.dataList = cityListOrigin;
                newFragment.listiner = selecter;
                newFragment.requestCode = 3;
                newFragment.titleStr = "Select City";
                newFragment.show(getSupportFragmentManager() , "temp");
            }
        });

        binding.etCityDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selection_Dialog newFragment = new Selection_Dialog();
                newFragment.dataList = cityListDestination;
                newFragment.requestCode = 10000;
                newFragment.titleStr = "Select City";
                newFragment.show(getSupportFragmentManager() , "temp");
                newFragment.listiner = new ItemSelecter() {
                    @Override
                    public void itemSelected(CommanModel selectedItem, int requestCode) {
                        selectedDesitnationCity = selectedItem;
                        binding.etCityDestination.setText(selectedItem.name);
                    }
                };
            }
        });
        binding.etEnquiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmn.showDatePicker(binding.etEnquiryDate , new Date());
            }
        });
        binding.etInquirySource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = getResources().getStringArray(R.array.inquirySource);
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Enquiry Source");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etInquirySource.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        binding.etEnquiryType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = getResources().getStringArray(R.array.inquiryType);
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Enquiry Type");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        if (items[selection[0]].equalsIgnoreCase("Individual")){
                            binding.layAccount.setVisibility(View.GONE);
                        }else{
                            binding.layAccount.setVisibility(View.VISIBLE);
                        }
                        binding.etEnquiryType.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        binding.etMovementType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = getResources().getStringArray(R.array.movementType);
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Movement Type");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etMovementType.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        binding.etGoodsType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = getResources().getStringArray(R.array.goodsType);
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Goods Type");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etGoodsType.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        binding.etServiceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = getResources().getStringArray(R.array.serviceType);
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Scope");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etServiceType.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        binding.etDeliveryType.setOnClickListener(new View.OnClickListener() {
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
                        binding.etDeliveryType.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        binding.etExpectedMovementDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmn.showDatePicker(binding.etExpectedMovementDate , new Date());
            }
        });
        binding.btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllFieldsAreValid()){
                    submitRequest();
                }
            }
        });
        binding.etAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = new String[dataListSuggestions.size()];
                for (int i = 0; i <dataListSuggestions.size() ; i++) {
                    items[i] = dataListSuggestions.get(i).name;
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Account");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etAccount.setText(""+items[selection[0]]);
                        selectedSuggestionModel = dataListSuggestions.get(selection[0]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        binding.cbAdditionalServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cbAdditionalServices.isChecked()){
                    binding.cbAdditionalServices.setChecked(true);
                    binding.layAdditionalServices.setVisibility(View.VISIBLE);
                }else{
                    binding.cbAdditionalServices.setChecked(false);
                    binding.layAdditionalServices.setVisibility(View.GONE);
                }
            }
        });
        binding.etWarehousing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Short Term" , "Long Term"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Warehousing");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etWarehousing.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        binding.etWarehouseTemprature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Controlled Storage" , "Normal Storage"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Warehouse Temprature");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etWarehouseTemprature.setText(""+items[selection[0]]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        binding.etRelocationServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"School Search" , "Home Search" , "Car Rental" , "City Orientation"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Relocation Services");
                final boolean[] checkedItems = new boolean[items.length];

                for (int i = 0; i < items.length ; i++) {
                    if (binding.etRelocationServices.getText().toString().toLowerCase().contains(items[i].toLowerCase())){
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
                        binding.etRelocationServices.setText(finalItems.substring(0,finalItems.length()-2));
                    }
                });
                alert.show();
            }
        });
        binding.etVisaImmigrationAssistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Visa Extension" , "Residence Permit"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Visa and Immigration Assistance");
                final boolean[] checkedItems = new boolean[items.length];

                for (int i = 0; i < items.length ; i++) {
                    if (binding.etVisaImmigrationAssistance.getText().toString().toLowerCase().contains(items[i].toLowerCase())){
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
                            binding.etVisaImmigrationAssistance.setText(finalItems.substring(0,finalItems.length()-2));
                    }
                });
                alert.show();
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

        binding.etHandymanServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Plumbing" , "AC Installation and Removal" , "Electrical Assistance"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Handyman Services");
                final boolean[] checkedItems = new boolean[items.length];

                for (int i = 0; i < items.length ; i++) {
                    if (binding.etHandymanServices.getText().toString().toLowerCase().contains(items[i].toLowerCase())){
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
                            binding.etHandymanServices.setText(finalItems.substring(0,finalItems.length()-2));
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
        binding.etShipMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Air" , "Road", "Sea" , "Rail"};
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
                binding.cbimportpetYes.setChecked(true);
                binding.cbimportpetNo.setChecked(false);
            }
        });
        binding.cbimportpetNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbimportpetYes.setChecked(false);
                binding.cbimportpetNo.setChecked(true);
            }
        });

        binding.cbTravelledtoEuropeancountriesYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbTravelledtoEuropeancountriesYes.setChecked(true);
                binding.cbTravelledtoEuropeancountriesNo.setChecked(false);
            }
        });
        binding.cbTravelledtoEuropeancountriesNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbTravelledtoEuropeancountriesYes.setChecked(false);
                binding.cbTravelledtoEuropeancountriesNo.setChecked(true);
            }
        });

        binding.cbVaccinationBookletavailableYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbVaccinationBookletavailableYes.setChecked(true);
                binding.cbVaccinationBookletavailableNo.setChecked(false);
            }
        });
        binding.cbVaccinationBookletavailableNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbVaccinationBookletavailableYes.setChecked(false);
                binding.cbVaccinationBookletavailableNo.setChecked(true);
            }
        });

        binding.cbPetInsuranceRequiredYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbPetInsuranceRequiredYes.setChecked(true);
                binding.cbPetInsuranceRequiredNo.setChecked(false);
            }
        });
        binding.cbPetInsuranceRequiredNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbPetInsuranceRequiredYes.setChecked(false);
                binding.cbPetInsuranceRequiredNo.setChecked(true);
            }
        });

        binding.cbNOCCertificateforPetYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbNOCCertificateforPetYes.setChecked(true);
                binding.cbNOCCertificateforPetNo.setChecked(false);
            }
        });
        binding.cbNOCCertificateforPetNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbNOCCertificateforPetYes.setChecked(false);
                binding.cbNOCCertificateforPetNo.setChecked(true);
            }
        });

        binding.cbBloodTestReportYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbBloodTestReportYes.setChecked(true);
                binding.cbBloodTestReportNo.setChecked(false);
            }
        });
        binding.cbBloodTestReportNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbBloodTestReportYes.setChecked(false);
                binding.cbBloodTestReportNo.setChecked(true);
            }
        });

        binding.cbVaccinationCertificateYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbVaccinationCertificateYes.setChecked(true);
                binding.cbBloodTestReportNo.setChecked(false);
            }
        });
        binding.cbVaccinationCertificateNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cbVaccinationCertificateYes.setChecked(false);
                binding.cbVaccinationCertificateNo.setChecked(true);
            }
        });

        binding.layUploadDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FilePicker.Builder(AddInquiry.this)
                        .setLimitItemSelection(1)
                        .setAccentColor(Color.BLUE)
                        .setCancellable(true)
                        .setFileType(FileType.IMAGE)
                        .setOnSubmitClickListener(files -> {
                            selectedPetDoc =  cmn.getBase64FromPath(files.get(0).getFile().getPath());
                            binding.tvDocumentName.setText(files.get(0).getFile().getPath());
                        })
                        .setOnItemClickListener((media, pos, adapter) -> {
                            if (!media.getFile().isDirectory()) {
                                adapter.setSelected(pos);
                            }
                        })
                        .buildAndShow();
//                int permissionCheck = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
//                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
//                } else {
//                    DialogProperties properties = new DialogProperties(true);
//                    properties.setSelectionMode(DialogConfigs.SINGLE_MODE);
//                    properties.setSelectionType(DialogConfigs.FILE_SELECT);
//                   // properties.setRoot(new File(DialogConfigs.EXTERNAL_DIR));
//                    properties.setErrorDir(new File(DialogConfigs.EXTERNAL_DIR));
//                    properties.setOffset(new File(DialogConfigs.EXTERNAL_DIR));
//                   // properties.setExtensions(new String[]{".pdf", ".xls" , ".doc", ".ppt"});
//                    properties.setHiddenFilesShown(false);
//                    FilePickerDialog dialog = new FilePickerDialog(mActivity, properties);
//                    dialog.setTitle("Select Files");
//                    dialog.setDialogSelectionListener(new DialogSelectionListener() {
//                        @Override
//                        public void onSelectedFilePaths(String[] files) {
//
//                          selectedPetDoc =  cmn.getBase64FromPath(files[0]);
//
//                            binding.tvDocumentName.setText(files[0]);
//                        }
//                    });
//                    dialog.show();
//                }
            }
        });
        binding.layUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FilePicker.Builder(AddInquiry.this)
                        .setLimitItemSelection(1)
                        .setAccentColor(Color.CYAN)
                        .setCancellable(true)
                        .setFileType(FileType.IMAGE)
                        .setOnSubmitClickListener(files -> {
                            selectedPetImage =  cmn.getBase64FromPath(files.get(0).getFile().getPath());
                        binding.tvImageName.setText(files.get(0).getFile().getPath());
                        })
                        .setOnItemClickListener((media, pos, adapter) -> {
                            if (!media.getFile().isDirectory()) {
                                adapter.setSelected(pos);
                            }
                        })
                        .buildAndShow();
//                int permissionCheck = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
//                DialogProperties properties = new DialogProperties(true);
//                properties.setSelectionMode(DialogConfigs.SINGLE_MODE);
//                properties.setSelectionType(DialogConfigs.FILE_SELECT);
//                properties.setRoot(new File(DialogConfigs.EXTERNAL_DIR));
//                properties.setErrorDir(new File(DialogConfigs.EXTERNAL_DIR));
//                properties.setOffset(new File(DialogConfigs.EXTERNAL_DIR));
//                properties.setExtensions(new String[]{".jpg", ".jpeg" ,".png"});
//                properties.setHiddenFilesShown(false);
//                FilePickerDialog dialog = new FilePickerDialog(mActivity, properties);
//                dialog.setTitle("Select an Image");
//                dialog.setDialogSelectionListener(new DialogSelectionListener() {
//                    @Override
//                    public void onSelectedFilePaths(String[] files) {
//                        selectedPetImage =  cmn.getBase64FromPath(files[0]);
//                        binding.tvImageName.setText(files[0]);
//                    }
//                });
//                dialog.show();

                //                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
//                } else {
//
//                }
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

        binding.etVehicleType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = {"Small Vehicle" , "Medium Vehicle", "Large Vehicle"};
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Vehicle Type");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etVehicleType.setText(""+items[selection[0]]);
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
                String[] items = {"Air" , "Road", "Sea" , "Rail"};
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

        binding.cbOtherDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cbOtherDestination.isChecked()){
                    binding.cbOtherDestination.setChecked(true);
                    binding.layVehicle.setVisibility(View.VISIBLE);
                    View view = mActivity.getLayoutInflater().inflate(R.layout.addressrow , null);
                    TextInputEditText etCountry = view.findViewById(R.id.etCountry);
                    TextInputEditText etState = view.findViewById(R.id.etState);
                    TextInputEditText etCity = view.findViewById(R.id.etCity);
                    ImageView btDeleteOther = view.findViewById(R.id.btDeleteOther);
                    TextView addressIndextv = view.findViewById(R.id.addressIndextv);
                    addressIndextv.setText("Address 1");
                    ItemSelecter delegateselector = new ItemSelecter() {
                        @Override
                        public void itemSelected(CommanModel selectedItem, int requestCode) {

                            switch (requestCode){
                                case 101 :
                                    etCountry.setText(selectedItem.name);
                                    etCountry.setTag(selectedItem.id);
                                    getStatesOther(selectedItem.id);
                                    break;
                                case 102 :
                                    etState.setText(selectedItem.name);
                                    etState.setTag(selectedItem.id);
                                    getCityOtherAddress(selectedItem.id);
                                    break;
                                case 103 :
                                    etCity.setText(selectedItem.name);
                                    etCity.setTag(selectedItem.id);
                                    break;

                            }
                        }
                    };
                    etCountry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Selection_Dialog newFragment = new Selection_Dialog();
                            newFragment.dataList = countryList;
                            newFragment.listiner = delegateselector;
                            newFragment.requestCode = 101;
                            newFragment.titleStr = "Select Country";
                            newFragment.show(getSupportFragmentManager() , "temp");
                        }
                    });
                    etState.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Selection_Dialog newFragment = new Selection_Dialog();
                            newFragment.dataList = statesListOtherAddress;
                            newFragment.listiner = delegateselector;
                            newFragment.requestCode = 102;
                            newFragment.titleStr = "Select State";
                            newFragment.show(getSupportFragmentManager() , "temp");
                        }
                    });
                    etCity.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Selection_Dialog newFragment = new Selection_Dialog();
                            newFragment.dataList = cityListOtherAddress;
                            newFragment.listiner = delegateselector;
                            newFragment.requestCode = 103;
                            newFragment.titleStr = "Select City";
                            newFragment.show(getSupportFragmentManager() , "temp");
                        }
                    });
                    btDeleteOther.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View viewtexx) {
                            int childCount = binding.layOtherDestAddress.getChildCount();
                            if (childCount == 1){
                                binding.cbOtherDestination.setChecked(false);
                                binding.layOtherDestAddress.removeAllViews();
                            }else{
                                binding.layOtherDestAddress.removeView(view);
                                for (int i = 0 ; i <= binding.layOtherDestAddress.getChildCount() ; i ++){
                                  TextView textIndex =  binding.layOtherDestAddress.getChildAt(i).findViewById(R.id.addressIndextv);
                                    textIndex.setText("Adddress "+i+1);
                                }
                            }
                        }
                    });
                    binding.btAddMoreOtherDestinationAddress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View viewtemp) {
                            View view = mActivity.getLayoutInflater().inflate(R.layout.addressrow , null);
                            TextInputEditText etCountry = view.findViewById(R.id.etCountry);
                            TextInputEditText etState = view.findViewById(R.id.etState);
                            TextInputEditText etCity = view.findViewById(R.id.etCity);
                            ImageView btDeleteOther = view.findViewById(R.id.btDeleteOther);
                            TextView addressIndextv = view.findViewById(R.id.addressIndextv);
                            int count = binding.layOtherDestAddress.getChildCount() + 1;
                            addressIndextv.setText("Address "+count);
                            ItemSelecter delegateselector = new ItemSelecter() {
                                @Override
                                public void itemSelected(CommanModel selectedItem, int requestCode) {

                                    switch (requestCode){
                                        case 101 :
                                            etCountry.setText(selectedItem.name);
                                            etCountry.setTag(selectedItem.id);
                                            getStatesOther(selectedItem.id);
                                            break;
                                        case 102 :
                                            etState.setText(selectedItem.name);
                                            etState.setTag(selectedItem.id);
                                            getCityOtherAddress(selectedItem.id);
                                            break;
                                        case 103 :
                                            etCity.setText(selectedItem.name);
                                            etCity.setTag(selectedItem.id);
                                            break;

                                    }
                                }
                            };
                            etCountry.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Selection_Dialog newFragment = new Selection_Dialog();
                                    newFragment.dataList = countryList;
                                    newFragment.listiner = delegateselector;
                                    newFragment.requestCode = 101;
                                    newFragment.titleStr = "Select Country";
                                    newFragment.show(getSupportFragmentManager() , "temp");
                                }
                            });
                            etState.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Selection_Dialog newFragment = new Selection_Dialog();
                                    newFragment.dataList = statesListOtherAddress;
                                    newFragment.listiner = delegateselector;
                                    newFragment.requestCode = 102;
                                    newFragment.titleStr = "Select State";
                                    newFragment.show(getSupportFragmentManager() , "temp");
                                }
                            });
                            etCity.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Selection_Dialog newFragment = new Selection_Dialog();
                                    newFragment.dataList = cityListOtherAddress;
                                    newFragment.listiner = delegateselector;
                                    newFragment.requestCode = 103;
                                    newFragment.titleStr = "Select City";
                                    newFragment.show(getSupportFragmentManager() , "temp");
                                }
                            });
                            btDeleteOther.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View viewtexx) {
                                    int childCount = binding.layOtherDestAddress.getChildCount();
                                    if (childCount == 1){
                                        binding.cbOtherDestination.setChecked(false);
                                        binding.layOtherDestAddress.removeAllViews();
                                    }else{
                                        binding.layOtherDestAddress.removeView(view);
                                        for (int i = 0 ; i < binding.layOtherDestAddress.getChildCount() ; i ++){
                                            View test = binding.layOtherDestAddress.getChildAt(i);
                                            TextView textIndex =  test.findViewById(R.id.addressIndextv);
                                            int index = i + 1;
                                            textIndex.setText("Address "+index);
                                        }
                                    }
                                }
                            });
                            binding.layOtherDestAddress.addView(view);
                        }
                    });
                    binding.layOtherDestAddress.addView(view);
                }else{
                    binding.cbOtherDestination.setChecked(false);
                    binding.layVehicle.setVisibility(View.GONE);
                    binding.layOtherDestAddress.removeAllViews();
                }
            }
        });
        binding.cbOtherOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cbOtherOrigin.isChecked()){
                    binding.cbOtherOrigin.setChecked(true);
                    View view = mActivity.getLayoutInflater().inflate(R.layout.addressrow , null);
                    TextInputEditText etCountry = view.findViewById(R.id.etCountry);
                    TextInputEditText etState = view.findViewById(R.id.etState);
                    TextInputEditText etCity = view.findViewById(R.id.etCity);
                    ImageView btDeleteOther = view.findViewById(R.id.btDeleteOther);
                    TextView addressIndextv = view.findViewById(R.id.addressIndextv);
                    addressIndextv.setText("Address 1");
                    ItemSelecter delegateselector = new ItemSelecter() {
                        @Override
                        public void itemSelected(CommanModel selectedItem, int requestCode) {

                            switch (requestCode){
                                case 101 :
                                    etCountry.setText(selectedItem.name);
                                    etCountry.setTag(selectedItem.id);
                                    getStatesOther(selectedItem.id);
                                    break;
                                case 102 :
                                    etState.setText(selectedItem.name);
                                    etState.setTag(selectedItem.id);
                                    getCityOtherAddress(selectedItem.id);
                                    break;
                                case 103 :
                                    etCity.setText(selectedItem.name);
                                    etCity.setTag(selectedItem.id);
                                    break;

                            }
                        }
                    };
                    etCountry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Selection_Dialog newFragment = new Selection_Dialog();
                            newFragment.dataList = countryList;
                            newFragment.listiner = delegateselector;
                            newFragment.requestCode = 101;
                            newFragment.titleStr = "Select Country";
                            newFragment.show(getSupportFragmentManager() , "temp");
                        }
                    });
                    etState.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Selection_Dialog newFragment = new Selection_Dialog();
                            newFragment.dataList = statesListOtherAddress;
                            newFragment.listiner = delegateselector;
                            newFragment.requestCode = 102;
                            newFragment.titleStr = "Select State";
                            newFragment.show(getSupportFragmentManager() , "temp");
                        }
                    });
                    etCity.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Selection_Dialog newFragment = new Selection_Dialog();
                            newFragment.dataList = cityListOtherAddress;
                            newFragment.listiner = delegateselector;
                            newFragment.requestCode = 103;
                            newFragment.titleStr = "Select City";
                            newFragment.show(getSupportFragmentManager() , "temp");
                        }
                    });
                    btDeleteOther.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View viewtexx) {
                            int childCount = binding.layOtherOriginAddress.getChildCount();
                            if (childCount == 1){
                                binding.cbOtherOrigin.setChecked(false);
                                binding.layOtherOriginAddress.removeAllViews();
                            }else{
                                binding.layOtherOriginAddress.removeView(view);
                                for (int i = 0 ; i <= binding.layOtherOriginAddress.getChildCount() ; i ++){
                                    TextView textIndex =  binding.layOtherOriginAddress.getChildAt(i).findViewById(R.id.addressIndextv);
                                    textIndex.setText("Adddress "+i+1);
                                }
                            }
                        }
                    });
                    binding.btAddMoreOtherOriginAddress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View viewtemp) {
                            View view = mActivity.getLayoutInflater().inflate(R.layout.addressrow , null);
                            TextInputEditText etCountry = view.findViewById(R.id.etCountry);
                            TextInputEditText etState = view.findViewById(R.id.etState);
                            TextInputEditText etCity = view.findViewById(R.id.etCity);
                            ImageView btDeleteOther = view.findViewById(R.id.btDeleteOther);
                            TextView addressIndextv = view.findViewById(R.id.addressIndextv);
                            int count = binding.layOtherOriginAddress.getChildCount() + 1;
                            addressIndextv.setText("Address "+count);
                            ItemSelecter delegateselector = new ItemSelecter() {
                                @Override
                                public void itemSelected(CommanModel selectedItem, int requestCode) {

                                    switch (requestCode){
                                        case 101 :
                                            etCountry.setText(selectedItem.name);
                                            etCountry.setTag(selectedItem.id);
                                            getStatesOther(selectedItem.id);
                                            break;
                                        case 102 :
                                            etState.setText(selectedItem.name);
                                            etState.setTag(selectedItem.id);
                                            getCityOtherAddress(selectedItem.id);
                                            break;
                                        case 103 :
                                            etCity.setText(selectedItem.name);
                                            etCity.setTag(selectedItem.id);
                                            break;

                                    }
                                }
                            };
                            etCountry.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Selection_Dialog newFragment = new Selection_Dialog();
                                    newFragment.dataList = countryList;
                                    newFragment.listiner = delegateselector;
                                    newFragment.requestCode = 101;
                                    newFragment.titleStr = "Select Country";
                                    newFragment.show(getSupportFragmentManager() , "temp");
                                }
                            });
                            etState.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Selection_Dialog newFragment = new Selection_Dialog();
                                    newFragment.dataList = statesListOtherAddress;
                                    newFragment.listiner = delegateselector;
                                    newFragment.requestCode = 102;
                                    newFragment.titleStr = "Select State";
                                    newFragment.show(getSupportFragmentManager() , "temp");
                                }
                            });
                            etCity.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Selection_Dialog newFragment = new Selection_Dialog();
                                    newFragment.dataList = cityListOtherAddress;
                                    newFragment.listiner = delegateselector;
                                    newFragment.requestCode = 103;
                                    newFragment.titleStr = "Select City";
                                    newFragment.show(getSupportFragmentManager() , "temp");
                                }
                            });
                            btDeleteOther.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View viewtexx) {
                                    int childCount = binding.layOtherOriginAddress.getChildCount();
                                    if (childCount == 1){
                                        binding.cbOtherOrigin.setChecked(false);
                                        binding.layOriginAddress.removeAllViews();
                                    }else{
                                        binding.layOtherOriginAddress.removeView(view);
                                        for (int i = 0 ; i < binding.layOtherOriginAddress.getChildCount() ; i ++){
                                            View test = binding.layOtherOriginAddress.getChildAt(i);
                                            TextView textIndex =  test.findViewById(R.id.addressIndextv);
                                            int index = i + 1;
                                            textIndex.setText("Address "+index);
                                        }
                                    }
                                }
                            });
                            binding.layOtherOriginAddress.addView(view);
                        }
                    });
                    binding.layOtherOriginAddress.addView(view);
                }else{
                    binding.cbOtherOrigin.setChecked(false);
                    binding.layOtherOriginAddress.removeAllViews();
                }
            }
        });

        binding.sagmentedGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(final int position) {
                // Handle stuff here
                Log.e("position" , ""+ position);

                if (position== 0){
                    binding.layOverview.setVisibility(View.VISIBLE);
                    binding.layShipperInfo.setVisibility(View.GONE);
                    binding.layShipperAddress.setVisibility(View.GONE);
                    binding.layOtherInfo.setVisibility(View.GONE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(700)
                            .playOn(findViewById(R.id.layOverview));

                }else if (position== 1){
                    binding.layOverview.setVisibility(View.GONE);
                    binding.layShipperInfo.setVisibility(View.VISIBLE);
                    binding.layShipperAddress.setVisibility(View.GONE);
                    binding.layOtherInfo.setVisibility(View.GONE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(700)
                            .playOn(findViewById(R.id.layShipperInfo));
                }else if (position== 2){
                    binding.layOverview.setVisibility(View.GONE);
                    binding.layShipperInfo.setVisibility(View.GONE);
                    binding.layShipperAddress.setVisibility(View.VISIBLE);
                    binding.layOtherInfo.setVisibility(View.GONE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(700)
                            .playOn(findViewById(R.id.layShipperAddress));
                }else{
                    binding.layOverview.setVisibility(View.GONE);
                    binding.layShipperInfo.setVisibility(View.GONE);
                    binding.layShipperAddress.setVisibility(View.GONE);
                    binding.layOtherInfo.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(700)
                            .playOn(findViewById(R.id.layOtherInfo));

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
            }
        }
    }


    void getInquiryData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject obj = new JSONObject(aResponse);
                        setupData(obj);
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
        String url = "inquiry/"+inquiryId;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(true);
    }


    void setupData(JSONObject obj){

        JSONObject company = obj.optJSONObject("company");

        String cmnyName = company.optString("legalName");
        String id = company.optString("id");

        binding.etCompany.setText(cmnyName);

        selectedCompany = new CommanModel(id , cmnyName);

        JSONArray otherDestinationAddresses = obj.optJSONArray("otherDestinationAddresses");

        if (otherDestinationAddresses != null){
            binding.cbOtherDestination.setChecked(true);
            for (int i = 0; i < otherDestinationAddresses.length() ; i ++){
                JSONObject object = otherDestinationAddresses.optJSONObject(i);
                View view = mActivity.getLayoutInflater().inflate(R.layout.addressrow , null);
                TextInputEditText etAddressLine1 = view.findViewById(R.id.etAddressLine1);
                etAddressLine1.setTag(object.optInt("id"));
                etAddressLine1.setText(object.optString("addressLine1"));
                TextInputEditText etAddressLine2 = view.findViewById(R.id.etAddressLine2);
                etAddressLine2.setText(object.optString("addressLine2"));
                JSONObject country = object.optJSONObject("country");
                TextInputEditText etCountry = view.findViewById(R.id.etCountry);
                etCountry.setTag(country.optInt("id"));
                etCountry.setText(country.optString("name"));
                JSONObject state = object.optJSONObject("state");
                TextInputEditText etState = view.findViewById(R.id.etState);
                etState.setTag(state.optInt("id"));
                etState.setText(state.optString("name"));
                JSONObject city = object.optJSONObject("city");
                TextInputEditText etCity = view.findViewById(R.id.etCity);
                etCity.setTag(city.optInt("id"));
                etCity.setText(city.optString("name"));
                ImageView btDeleteOther = view.findViewById(R.id.btDeleteOther);
                TextView addressIndextv = view.findViewById(R.id.addressIndextv);
                addressIndextv.setText("Address 1");
                TextInputEditText etPinCode = view.findViewById(R.id.etPinCode);
                etPinCode.setText(object.optString("pinCode"));
                ItemSelecter delegateselector = new ItemSelecter() {
                    @Override
                    public void itemSelected(CommanModel selectedItem, int requestCode) {

                        switch (requestCode){
                            case 101 :
                                etCountry.setText(selectedItem.name);
                                etCountry.setTag(selectedItem.id);
                                getStatesOther(selectedItem.id);
                                break;
                            case 102 :
                                etState.setText(selectedItem.name);
                                etState.setTag(selectedItem.id);
                                getCityOtherAddress(selectedItem.id);
                                break;
                            case 103 :
                                etCity.setText(selectedItem.name);
                                etCity.setTag(selectedItem.id);
                                break;

                        }
                    }
                };
                etCountry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Selection_Dialog newFragment = new Selection_Dialog();
                        newFragment.dataList = countryList;
                        newFragment.listiner = delegateselector;
                        newFragment.requestCode = 101;
                        newFragment.titleStr = "Select Country";
                        newFragment.show(getSupportFragmentManager() , "temp");
                    }
                });
                etState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Selection_Dialog newFragment = new Selection_Dialog();
                        newFragment.dataList = statesListOtherAddress;
                        newFragment.listiner = delegateselector;
                        newFragment.requestCode = 102;
                        newFragment.titleStr = "Select State";
                        newFragment.show(getSupportFragmentManager() , "temp");
                    }
                });
                etCity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Selection_Dialog newFragment = new Selection_Dialog();
                        newFragment.dataList = cityListOtherAddress;
                        newFragment.listiner = delegateselector;
                        newFragment.requestCode = 103;
                        newFragment.titleStr = "Select City";
                        newFragment.show(getSupportFragmentManager() , "temp");
                    }
                });
                btDeleteOther.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewtexx) {
                        int childCount = binding.layOtherDestAddress.getChildCount();
                        if (childCount == 1){
                            binding.cbOtherDestination.setChecked(false);
                            binding.layOtherDestAddress.removeAllViews();
                        }else{
                            binding.layOtherDestAddress.removeView(view);
                            for (int i = 0 ; i <= binding.layOtherDestAddress.getChildCount() ; i ++){
                                TextView textIndex =  binding.layOtherDestAddress.getChildAt(i).findViewById(R.id.addressIndextv);
                                textIndex.setText("Adddress "+i+1);
                            }
                        }
                    }
                });
                binding.btAddMoreOtherDestinationAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewtemp) {
                        View view = mActivity.getLayoutInflater().inflate(R.layout.addressrow , null);
                        TextInputEditText etCountry = view.findViewById(R.id.etCountry);
                        TextInputEditText etState = view.findViewById(R.id.etState);
                        TextInputEditText etCity = view.findViewById(R.id.etCity);
                        ImageView btDeleteOther = view.findViewById(R.id.btDeleteOther);
                        TextView addressIndextv = view.findViewById(R.id.addressIndextv);
                        int count = binding.layOtherDestAddress.getChildCount() + 1;
                        addressIndextv.setText("Address "+count);
                        ItemSelecter delegateselector = new ItemSelecter() {
                            @Override
                            public void itemSelected(CommanModel selectedItem, int requestCode) {

                                switch (requestCode){
                                    case 101 :
                                        etCountry.setText(selectedItem.name);
                                        etCountry.setTag(selectedItem.id);
                                        getStatesOther(selectedItem.id);
                                        break;
                                    case 102 :
                                        etState.setText(selectedItem.name);
                                        etState.setTag(selectedItem.id);
                                        getCityOtherAddress(selectedItem.id);
                                        break;
                                    case 103 :
                                        etCity.setText(selectedItem.name);
                                        etCity.setTag(selectedItem.id);
                                        break;

                                }
                            }
                        };
                        etCountry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Selection_Dialog newFragment = new Selection_Dialog();
                                newFragment.dataList = countryList;
                                newFragment.listiner = delegateselector;
                                newFragment.requestCode = 101;
                                newFragment.titleStr = "Select Country";
                                newFragment.show(getSupportFragmentManager() , "temp");
                            }
                        });
                        etState.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Selection_Dialog newFragment = new Selection_Dialog();
                                newFragment.dataList = statesListOtherAddress;
                                newFragment.listiner = delegateselector;
                                newFragment.requestCode = 102;
                                newFragment.titleStr = "Select State";
                                newFragment.show(getSupportFragmentManager() , "temp");
                            }
                        });
                        etCity.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Selection_Dialog newFragment = new Selection_Dialog();
                                newFragment.dataList = cityListOtherAddress;
                                newFragment.listiner = delegateselector;
                                newFragment.requestCode = 103;
                                newFragment.titleStr = "Select City";
                                newFragment.show(getSupportFragmentManager() , "temp");
                            }
                        });
                        btDeleteOther.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View viewtexx) {
                                int childCount = binding.layOtherDestAddress.getChildCount();
                                if (childCount == 1){
                                    binding.cbOtherDestination.setChecked(false);
                                    binding.layOtherDestAddress.removeAllViews();
                                }else{
                                    binding.layOtherDestAddress.removeView(view);
                                    for (int i = 0 ; i < binding.layOtherDestAddress.getChildCount() ; i ++){
                                        View test = binding.layOtherDestAddress.getChildAt(i);
                                        TextView textIndex =  test.findViewById(R.id.addressIndextv);
                                        int index = i + 1;
                                        textIndex.setText("Address "+index);
                                    }
                                }
                            }
                        });
                        binding.layOtherDestAddress.addView(view);
                    }
                });
                binding.layOtherDestAddress.addView(view);
            }


        }

        JSONArray otherOriginAddresses = obj.optJSONArray("otherOriginAddresses");

        if (otherOriginAddresses != null){
            binding.cbOtherOrigin.setChecked(true);
            for (int i = 0; i < otherOriginAddresses.length() ; i ++){
                JSONObject object = otherOriginAddresses.optJSONObject(i);
                View view = mActivity.getLayoutInflater().inflate(R.layout.addressrow , null);
                TextInputEditText etAddressLine1 = view.findViewById(R.id.etAddressLine1);
                etAddressLine1.setTag(object.optInt("id"));
                etAddressLine1.setText(object.optString("addressLine1"));
                TextInputEditText etAddressLine2 = view.findViewById(R.id.etAddressLine2);
                etAddressLine2.setText(object.optString("addressLine2"));
                JSONObject country = object.optJSONObject("country");
                TextInputEditText etCountry = view.findViewById(R.id.etCountry);
                etCountry.setTag(country.optInt("id"));
                etCountry.setText(country.optString("name"));
                JSONObject state = object.optJSONObject("state");
                TextInputEditText etState = view.findViewById(R.id.etState);
                etState.setTag(state.optInt("id"));
                etState.setText(state.optString("name"));
                JSONObject city = object.optJSONObject("city");
                TextInputEditText etCity = view.findViewById(R.id.etCity);
                etCity.setTag(city.optInt("id"));
                etCity.setText(city.optString("name"));
                TextInputEditText etPinCode = view.findViewById(R.id.etPinCode);
                etPinCode.setText(object.optString("pinCode"));
                ImageView btDeleteOther = view.findViewById(R.id.btDeleteOther);
                TextView addressIndextv = view.findViewById(R.id.addressIndextv);
                addressIndextv.setText("Address 1");
                ItemSelecter delegateselector = new ItemSelecter() {
                    @Override
                    public void itemSelected(CommanModel selectedItem, int requestCode) {

                        switch (requestCode){
                            case 101 :
                                etCountry.setText(selectedItem.name);
                                etCountry.setTag(selectedItem.id);
                                getStatesOther(selectedItem.id);
                                break;
                            case 102 :
                                etState.setText(selectedItem.name);
                                etState.setTag(selectedItem.id);
                                getCityOtherAddress(selectedItem.id);
                                break;
                            case 103 :
                                etCity.setText(selectedItem.name);
                                etCity.setTag(selectedItem.id);
                                break;

                        }
                    }
                };
                etCountry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Selection_Dialog newFragment = new Selection_Dialog();
                        newFragment.dataList = countryList;
                        newFragment.listiner = delegateselector;
                        newFragment.requestCode = 101;
                        newFragment.titleStr = "Select Country";
                        newFragment.show(getSupportFragmentManager() , "temp");
                    }
                });
                etState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Selection_Dialog newFragment = new Selection_Dialog();
                        newFragment.dataList = statesListOtherAddress;
                        newFragment.listiner = delegateselector;
                        newFragment.requestCode = 102;
                        newFragment.titleStr = "Select State";
                        newFragment.show(getSupportFragmentManager() , "temp");
                    }
                });
                etCity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Selection_Dialog newFragment = new Selection_Dialog();
                        newFragment.dataList = cityListOtherAddress;
                        newFragment.listiner = delegateselector;
                        newFragment.requestCode = 103;
                        newFragment.titleStr = "Select City";
                        newFragment.show(getSupportFragmentManager() , "temp");
                    }
                });
                btDeleteOther.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewtexx) {
                        int childCount = binding.layOtherDestAddress.getChildCount();
                        if (childCount == 1){
                            binding.cbOtherDestination.setChecked(false);
                            binding.layOtherDestAddress.removeAllViews();
                        }else{
                            binding.layOtherDestAddress.removeView(view);
                            for (int i = 0 ; i <= binding.layOtherDestAddress.getChildCount() ; i ++){
                                TextView textIndex =  binding.layOtherDestAddress.getChildAt(i).findViewById(R.id.addressIndextv);
                                textIndex.setText("Adddress "+i+1);
                            }
                        }
                    }
                });
                binding.btAddMoreOtherOriginAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewtemp) {
                        View view = mActivity.getLayoutInflater().inflate(R.layout.addressrow , null);
                        TextInputEditText etCountry = view.findViewById(R.id.etCountry);
                        TextInputEditText etState = view.findViewById(R.id.etState);
                        TextInputEditText etCity = view.findViewById(R.id.etCity);
                        ImageView btDeleteOther = view.findViewById(R.id.btDeleteOther);
                        TextView addressIndextv = view.findViewById(R.id.addressIndextv);
                        int count = binding.layOtherDestAddress.getChildCount() + 1;
                        addressIndextv.setText("Address "+count);
                        ItemSelecter delegateselector = new ItemSelecter() {
                            @Override
                            public void itemSelected(CommanModel selectedItem, int requestCode) {

                                switch (requestCode){
                                    case 101 :
                                        etCountry.setText(selectedItem.name);
                                        etCountry.setTag(selectedItem.id);
                                        getStatesOther(selectedItem.id);
                                        break;
                                    case 102 :
                                        etState.setText(selectedItem.name);
                                        etState.setTag(selectedItem.id);
                                        getCityOtherAddress(selectedItem.id);
                                        break;
                                    case 103 :
                                        etCity.setText(selectedItem.name);
                                        etCity.setTag(selectedItem.id);
                                        break;

                                }
                            }
                        };
                        etCountry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Selection_Dialog newFragment = new Selection_Dialog();
                                newFragment.dataList = countryList;
                                newFragment.listiner = delegateselector;
                                newFragment.requestCode = 101;
                                newFragment.titleStr = "Select Country";
                                newFragment.show(getSupportFragmentManager() , "temp");
                            }
                        });
                        etState.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Selection_Dialog newFragment = new Selection_Dialog();
                                newFragment.dataList = statesListOtherAddress;
                                newFragment.listiner = delegateselector;
                                newFragment.requestCode = 102;
                                newFragment.titleStr = "Select State";
                                newFragment.show(getSupportFragmentManager() , "temp");
                            }
                        });
                        etCity.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Selection_Dialog newFragment = new Selection_Dialog();
                                newFragment.dataList = cityListOtherAddress;
                                newFragment.listiner = delegateselector;
                                newFragment.requestCode = 103;
                                newFragment.titleStr = "Select City";
                                newFragment.show(getSupportFragmentManager() , "temp");
                            }
                        });
                        btDeleteOther.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View viewtexx) {
                                int childCount = binding.layOtherDestAddress.getChildCount();
                                if (childCount == 1){
                                    binding.cbOtherDestination.setChecked(false);
                                    binding.layOtherDestAddress.removeAllViews();
                                }else{
                                    binding.layOtherDestAddress.removeView(view);
                                    for (int i = 0 ; i < binding.layOtherDestAddress.getChildCount() ; i ++){
                                        View test = binding.layOtherDestAddress.getChildAt(i);
                                        TextView textIndex =  test.findViewById(R.id.addressIndextv);
                                        int index = i + 1;
                                        textIndex.setText("Address "+index);
                                    }
                                }
                            }
                        });
                        binding.layOtherDestAddress.addView(view);
                    }
                });
                binding.layOtherOriginAddress.addView(view);
            }


        }

        String inqDate = cmn.getDate(obj.optLong("inquiryDate"));
        binding.etEnquiryDate.setText(""+inqDate);
        binding.etEnquiryType.setText(""+obj.optString("inquiryType").toLowerCase());
        binding.etMovementType.setText(""+obj.optString("movementType").toLowerCase());
        binding.etGoodsType.setText(""+obj.optString("goodsTypeValue").toLowerCase());
        binding.etInquirySource.setText(""+obj.optString("source").toLowerCase());

        if (binding.etEnquiryType.getText().toString().equalsIgnoreCase("Individual")){
            binding.layAccount.setVisibility(View.GONE);
        }else{
            binding.layAccount.setVisibility(View.VISIBLE);
            CommanModel temp = new CommanModel();
            temp.id = obj.optJSONObject("account").optString("id");
            temp.name = obj.optJSONObject("account").optString("companyName");
            selectedSuggestionModel = temp;
            binding.etAccount.setText(""+selectedSuggestionModel.name);
        }
        binding.etContactPerson.setText(""+obj.optString("contactPerson"));

        JSONObject shipper = obj.optJSONObject("shipper");
        binding.etClientFirstName.setText(""+shipper.optString("firstName"));
        binding.etClientLastName.setText(""+shipper.optString("lastName"));
        binding.etClientContact.setText(""+shipper.optString("contactNumber"));
        binding.etClientAlternetContact.setText(""+cmn.replaceNull(shipper.optString("alternateNumber")));
        binding.etClientEmail.setText(""+shipper.optString("email"));
        binding.etClientAlternetEmail.setText(""+cmn.replaceNull(shipper.optString("alternateEmail")));
        binding.etDesignation.setText(""+cmn.replaceNull(shipper.optString("designation")));

        binding.etServiceType.setText(""+cmn.replaceNull(obj.optString("serviceType")));
        binding.etDeliveryType.setText(""+cmn.replaceNull(obj.optString("deliveryType")));
        if (obj.optString("expectedMovementDate") != null){
            String moveDate = cmn.getDate(obj.optLong("expectedMovementDate"));
            binding.etExpectedMovementDate.setText(""+moveDate);
        }

        JSONObject originAddress = obj.optJSONObject("originAddress");
        originAddId = originAddress.optString("id");
        binding.etAddressLine1Origin.setText(""+cmn.replaceNull(originAddress.optString("addressLine1")));
        binding.etAddressLine2Origin.setText(""+cmn.replaceNull(originAddress.optString("addressLine2")));

        CommanModel temp = new CommanModel();

        temp.id = ""+originAddress.optJSONObject("country").optString("id");
        temp.name = ""+originAddress.optJSONObject("country").optString("name");
        selectedOriginCountry= temp;
        binding.etCountryOrigin.setText(""+selectedOriginCountry.name);

        CommanModel temp1 = new CommanModel();
        temp1.id = originAddress.optJSONObject("state").optString("id");
        temp1.name = originAddress.optJSONObject("state").optString("name");
        selectedOriginState= temp1;
        binding.etStateOrigin.setText(""+selectedOriginState.name);

        CommanModel temp2 = new CommanModel();

        temp2.id = originAddress.optJSONObject("city").optString("id");
        temp2.name = originAddress.optJSONObject("city").optString("name");
        selectedOriginCity = temp2;
        binding.etCityOrigin.setText(""+selectedOriginCity.name);


        binding.etPinCodeOrigin.setText(""+cmn.replaceNull(originAddress.optString("pinCode")));

        JSONObject destinationAddress = obj.optJSONObject("destinationAddress");

        destAddId = destinationAddress.optString("id");
        binding.etAddressLine1Destination.setText(""+cmn.replaceNull(destinationAddress.optString("addressLine1")));
        binding.etAddressLine2Destination.setText(""+cmn.replaceNull(destinationAddress.optString("addressLine2")));
        CommanModel temp3 = new CommanModel();
        temp3.id = destinationAddress.optJSONObject("country").optString("id");
        temp3.name = destinationAddress.optJSONObject("country").optString("name");
        selectedDesinationCountry = temp3;
        binding.etCountryDestination.setText(""+selectedDesinationCountry.name);

        CommanModel temp4 = new CommanModel();
        temp4.id = destinationAddress.optJSONObject("state").optString("id");
        temp4.name = destinationAddress.optJSONObject("state").optString("name");
        selectedDesinationState = temp4;
        binding.etStateDestination.setText(""+selectedDesinationState.name);

        CommanModel temp5 = new CommanModel();

        temp5.id = destinationAddress.optJSONObject("city").optString("id");
        temp5.name = destinationAddress.optJSONObject("city").optString("name");
        selectedDesitnationCity = temp5;
        binding.etCityDestination.setText(""+selectedDesitnationCity.name);

        binding.etPinCodeDestination.setText(""+cmn.replaceNull(destinationAddress.optString("pinCode")));


       if (obj.optBoolean("additionalServices")){

           binding.layAdditionalServices.setVisibility(View.VISIBLE);
           binding.cbAdditionalServices.setChecked(true);

           binding.etWarehousing.setText(""+cmn.replaceNull(obj.optString("warehousing")));
           binding.etWarehouseTemprature.setText(""+cmn.replaceNull(obj.optString("warehouseTemprature")));

           JSONArray relocationServices = obj.optJSONArray("relocationServices");

           String finalItems = "";
           for (int i = 0; i < relocationServices.length() ; i++) {
                   finalItems = finalItems.concat(relocationServices.optString(i) + " , ");
           }
           if (finalItems.length()>2)
               binding.etRelocationServices.setText(finalItems.substring(0,finalItems.length()-2));

           JSONArray visaImmigrationAssistance = obj.optJSONArray("visaImmigrationAssistance");

           String finalItems1 = "";
           for (int i = 0; i < visaImmigrationAssistance.length() ; i++) {
               finalItems1 = finalItems1.concat(visaImmigrationAssistance.optString(i) + " , ");
           }
           if (finalItems1.length()>2)
               binding.etVisaImmigrationAssistance.setText(finalItems1.substring(0,finalItems1.length()-2));


           JSONArray handymanServices = obj.optJSONArray("handymanServices");

           String finalItems2 = "";
           for (int i = 0; i < handymanServices.length() ; i++) {
               finalItems2 = finalItems2.concat(handymanServices.optString(i) + " , ");
           }
           if (finalItems2.length()>2)
               binding.etVisaImmigrationAssistance.setText(finalItems2.substring(0,finalItems2.length()-2));
       }else{
           binding.layAdditionalServices.setVisibility(View.GONE);
           binding.cbAdditionalServices.setChecked(false);
       }

        if (obj.optBoolean("petAvailable")){

            JSONObject petInfo = obj.optJSONObject("petInfo");

            binding.layPet.setVisibility(View.VISIBLE);
            binding.cbPet.setChecked(true);


            if (petInfo.optBoolean("petImport")){
                binding.cbimportpetYes.setChecked(true);
                binding.cbimportpetNo.setChecked(false);
            }else{
                binding.cbimportpetYes.setChecked(false);
                binding.cbimportpetNo.setChecked(true);
            }
            if (petInfo.optBoolean("travelledToEuropeanCounty")){
                binding.cbTravelledtoEuropeancountriesYes.setChecked(true);
                binding.cbTravelledtoEuropeancountriesNo.setChecked(false);
            }else{
                binding.cbTravelledtoEuropeancountriesYes.setChecked(false);
                binding.cbTravelledtoEuropeancountriesNo.setChecked(true);
            }
            if (petInfo.optBoolean("vaccinationBookletAvailable")){
                binding.cbVaccinationBookletavailableYes.setChecked(true);
                binding.cbVaccinationBookletavailableNo.setChecked(false);
            }else{
                binding.cbVaccinationBookletavailableYes.setChecked(false);
                binding.cbVaccinationBookletavailableNo.setChecked(true);
            }
            if (petInfo.optBoolean("petInsuranceRequired")){
                binding.cbPetInsuranceRequiredYes.setChecked(true);
                binding.cbPetInsuranceRequiredNo.setChecked(false);
            }else{
                binding.cbPetInsuranceRequiredYes.setChecked(false);
                binding.cbPetInsuranceRequiredNo.setChecked(true);
            }
            if (petInfo.optBoolean("petNocCertificate")){
                binding.cbNOCCertificateforPetYes.setChecked(true);
                binding.cbNOCCertificateforPetNo.setChecked(false);
            }else{
                binding.cbNOCCertificateforPetYes.setChecked(false);
                binding.cbNOCCertificateforPetNo.setChecked(true);
            }
            if (petInfo.optBoolean("bloodTestReport")){
                binding.cbBloodTestReportYes.setChecked(true);
                binding.cbBloodTestReportNo.setChecked(false);
            }else{
                binding.cbBloodTestReportYes.setChecked(false);
                binding.cbBloodTestReportNo.setChecked(true);
            }

            if (petInfo.optBoolean("vaccinationCertificate")){
                binding.cbVaccinationCertificateYes.setChecked(true);
                binding.cbVaccinationCertificateNo.setChecked(false);
            }else{
                binding.cbVaccinationCertificateYes.setChecked(false);
                binding.cbVaccinationCertificateNo.setChecked(true);
            }

            binding.etRemarkIfAny.setText(""+cmn.replaceNull(petInfo.optString("remarks")));

            JSONArray pets = petInfo.optJSONArray("pets");

            JSONObject pet = pets.optJSONObject(0);
            binding.etPetType.setText(""+cmn.replaceNull(pet.optString("petType")));
            binding.etPetOwnerName.setText(""+cmn.replaceNull(pet.optString("petOwnerName")));
            binding.etShipMode.setText(""+cmn.replaceNull(pet.optString("shipMode")));
            binding.etDeliveryTypePet.setText(""+cmn.replaceNull(pet.optString("deliveryType")));
            binding.etBreedName.setText(""+cmn.replaceNull(pet.optString("breedName")));
            binding.etPetAge.setText(""+cmn.replaceNull(pet.optString("petAge")));
            binding.etPetSex.setText(""+cmn.replaceNull(pet.optString("petSex")));
            binding.etPetCitizenship.setText(""+cmn.replaceNull(pet.optString("petCitizenship")));
            binding.etPetWeight.setText(""+cmn.replaceNull(pet.optString("petWeight")));

            if (pet.optBoolean("kennelAvailability")){
                binding.layKannelAvailibity.setVisibility(View.VISIBLE);
                binding.etLengthKanel.setText(""+cmn.replaceNull(pet.optString("kennelLength")));
                binding.etBreathKanel.setText(""+cmn.replaceNull(pet.optString("kennelBreadth")));
                binding.etHeightKanel.setText(""+cmn.replaceNull(pet.optString("kennelHeight")));
            }else{
                binding.layKannelAvailibity.setVisibility(View.GONE);
            }
        }else{
            binding.layPet.setVisibility(View.GONE);
            binding.cbPet.setChecked(false);
        }


        if (obj.optBoolean("vehicleAvailable")){

            JSONArray vehicleInfo = obj.optJSONArray("vehicleInfo");

            JSONObject vehicle = vehicleInfo.optJSONObject(0);

            binding.layVehicle.setVisibility(View.VISIBLE);
            binding.cbVehicle.setChecked(true);

            binding.etVehicleType.setText(""+cmn.replaceNull(vehicle.optString("vehicleType")));
            binding.etVehicleOwnerName.setText(""+cmn.replaceNull(vehicle.optString("vehicleOwnerName")));
            binding.etShipModeVehicle.setText(""+cmn.replaceNull(vehicle.optString("shipMode")));
            binding.etDeliveryTypeVehicle.setText(""+cmn.replaceNull(vehicle.optString("deliveryType")));
            binding.etVehicleMake.setText(""+cmn.replaceNull(vehicle.optString("vehicleMake")));
            binding.etVehicleModelNumber.setText(""+cmn.replaceNull(vehicle.optString("vehicleModelNumber")));
            binding.etVehicleRegistrationNumber.setText(""+cmn.replaceNull(vehicle.optString("vehicleRegistrationNumber")));
            binding.etVehicleColor.setText(""+cmn.replaceNull(vehicle.optString("vehicleColor")));
            binding.etVehicleEngineCapacity.setText(""+cmn.replaceNull(vehicle.optString("vehicleEngineCapacity")));
            binding.etEmissionStandard.setText(""+cmn.replaceNull(vehicle.optString("emissionStandard")));
            binding.etRemarksVehicle.setText(""+cmn.replaceNull(vehicle.optString("remarks")));

        }else{
            binding.layVehicle.setVisibility(View.GONE);
            binding.cbVehicle.setChecked(false);
        }



    }


    void getStates(boolean isOrigin){
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        if (isOrigin){
            statesListOrigin.clear();
            List<StateModel> states = dbHelper.getAllStates(Integer.parseInt(selectedOriginCountry.id));
            for (int i = 0; i < states.size(); i++) {
                StateModel cuntmodel = states.get(i);
                CommanModel model = new CommanModel(""+cuntmodel.id,""+cuntmodel.name.replaceAll("\"", ""));
                statesListOrigin.add(model);
            }
        }else{
            statesListDestination.clear();
            List<StateModel> states = dbHelper.getAllStates(Integer.parseInt(selectedDesinationCountry.id));
            for (int i = 0; i < states.size(); i++) {
                StateModel cuntmodel = states.get(i);
                CommanModel model = new CommanModel(""+cuntmodel.id,""+cuntmodel.name.replaceAll("\"", ""));
                statesListDestination.add(model);
            }
        }

//        JSONObject parameters = new JSONObject();
//        Handler handler = new Handler(){
//            public void handleMessage(Message msg) {
//                String aResponse = msg.getData().getString("message");
//                if ((null != aResponse)) {
//                    try {
//
//                        if (isOrigin){
//                            statesListOrigin.clear();
//                            JSONArray array = new JSONArray(aResponse);
//                            for (int index = 0;index<array.length();index++){
//                                String idComb = array.optJSONObject(index).optString("id");
//                                statesListOrigin.add(new CommanModel(idComb,array.optJSONObject(index).optString("name")));
//                            }
//                        }else{
//                            statesListDestination.clear();
//                            JSONArray array = new JSONArray(aResponse);
//
//                            for (int index = 0;index<array.length();index++){
//                                String idComb = array.optJSONObject(index).optString("id");
//                                statesListDestination.add(new CommanModel(idComb,array.optJSONObject(index).optString("name")));
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else
//                {
//                    Toast.makeText(
//                            mActivity,
//                            "Not Got Response From Server.",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//        String url = "";
//        if (isOrigin){
//            url = "countries/"+selectedOriginCountry.id+"/states/";
//        }else{
//            url = "countries/"+selectedDesinationCountry.id+"/states/";
//        }
//        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
//        request.getAPI(false);
    }

    void getStatesOther(String countryId){
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        statesListOtherAddress.clear();
        List<StateModel> states = dbHelper.getAllStates(Integer.parseInt(countryId));
        for (int i = 0; i < states.size(); i++) {
            StateModel cuntmodel = states.get(i);
            CommanModel model = new CommanModel("" + cuntmodel.id, "" + cuntmodel.name.replaceAll("\"", ""));
            statesListOtherAddress.add(model);
        }
//        JSONObject parameters = new JSONObject();
//        Handler handler = new Handler(){
//            public void handleMessage(Message msg) {
//                String aResponse = msg.getData().getString("message");
//                if ((null != aResponse)) {
//                    try {
//                        statesListOtherAddress.clear();
//                        JSONArray array = new JSONArray(aResponse);
//                        for (int index = 0;index<array.length();index++){
//                            statesListOtherAddress.add(new CommanModel(array.optJSONObject(index).optString("id") + ","+array.optJSONObject(index).optString("stateCode"),array.optJSONObject(index).optString("name")));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else
//                {
//                    Toast.makeText(
//                            mActivity,
//                            "Not Got Response From Server.",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//        String url = "";
//        url = "countries/"+countryId+"/states/";
//        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
//        request.getAPI(false);
    }


    void getCountries(){
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<CountryModel> country = dbHelper.getAllCountry();
        for (int i = 0; i < country.size(); i++) {
            CountryModel cuntmodel = country.get(i);
            CommanModel model = new CommanModel(""+cuntmodel.id,""+cuntmodel.name.replaceAll("\"", ""));
                            countryList.add(model);
                                if (model.id.equalsIgnoreCase("101")) {
                                    if (!isEdit){
                                        selectedOriginCountry = model;
                                        selectedDesinationCountry = model;
                                        getStates(true);
                                        getStates(false);
                                        binding.etCountryOrigin.setText("India");
                                        binding.etCountryDestination.setText("India");
                                    }
                                }
        }

//        JSONObject parameters = new JSONObject();
//        Handler handler = new Handler(){
//            public void handleMessage(Message msg) {
//                String aResponse = msg.getData().getString("message");
//                if ((null != aResponse)) {
//                    try {
//                        countryList.clear();
//                        JSONArray array = new JSONArray(aResponse);
//                        for (int index = 0;index<array.length();index++){
//                            CommanModel model = new CommanModel(array.optJSONObject(index).optString("id"),array.optJSONObject(index).optString("name"));
//                            countryList.add(model);
//
//
//                                if (model.id.equalsIgnoreCase("101")) {
//                                    if (!isEdit){
//                                        selectedOriginCountry = model;
//                                        selectedDesinationCountry = model;
//                                        getStates(true);
//                                        getStates(false);
//                                        binding.etCountryOrigin.setText("India");
//                                        binding.etCountryDestination.setText("India");
//                                    }
//                                }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else
//                {
//                    Toast.makeText(
//                            mActivity,
//                            "Not Got Response From Server.",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//        String url = "";
//        url = "countries";
//        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
//        request.getAPI(false);
    }

    void getCompany(){
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<CountryModel> country = dbHelper.getAllCompany();
        for (int i = 0; i < country.size(); i++) {
            CountryModel cuntmodel = country.get(i);
            CommanModel model = new CommanModel(""+cuntmodel.id,""+cuntmodel.name.replaceAll("\"", ""));
            companyList.add(model);
        }

//        JSONObject parameters = new JSONObject();
//        Handler handler = new Handler(){
//            public void handleMessage(Message msg) {
//                String aResponse = msg.getData().getString("message");
//                if ((null != aResponse)) {
//                    try {
//                        companyList.clear();
//                        JSONObject res = new JSONObject(aResponse);
//                        JSONArray array = res.optJSONArray("contant");
//                        for (int index = 0;index<array.length();index++){
//                            CommanModel model = new CommanModel(array.optJSONObject(index).optString("id"),array.optJSONObject(index).optString("legalName"));
//                            companyList.add(model);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else
//                {
//                    Toast.makeText(
//                            mActivity,
//                            "Not Got Response From Server.",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//        String url = "";
//        url = "seller-organization?page=0&size=15&searchFields=";
//        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
//        request.getAPI(false);
    }


    void getCity(boolean isOrigin , boolean isDestination){

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        if (isOrigin){
            cityListOrigin.clear();
            List<CityModel> states = dbHelper.getAllCities(Integer.parseInt(selectedOriginState.id));
            for (int i = 0; i < states.size(); i++) {
                CityModel cuntmodel = states.get(i);
                CommanModel model = new CommanModel(""+cuntmodel.id,""+cuntmodel.name.replaceAll("\"", ""));
                cityListOrigin.add(model);
            }
        }else{
            cityListDestination.clear();
            List<CityModel> states = dbHelper.getAllCities(Integer.parseInt(selectedDesinationState.id));
            for (int i = 0; i < states.size(); i++) {
                CityModel cuntmodel = states.get(i);
                CommanModel model = new CommanModel(""+cuntmodel.id,""+cuntmodel.name.replaceAll("\"", ""));
                cityListDestination.add(model);
            }
        }

//        JSONObject parameters = new JSONObject();
//        Handler handler = new Handler(){
//            public void handleMessage(Message msg) {
//                String aResponse = msg.getData().getString("message");
//                if ((null != aResponse)) {
//                    try {
//                        if (isOrigin){
//                            cityListOrigin.clear();
//                            JSONArray array = new JSONArray(aResponse);
//                            for (int index = 0;index<array.length();index++){
//                                cityListOrigin.add(new CommanModel(array.optJSONObject(index).optString("id"),array.optJSONObject(index).optString("name")));
//                            }
//                        }else if (isDestination){
//                            cityListDestination.clear();
//                            JSONArray array = new JSONArray(aResponse);
//                            for (int index = 0;index<array.length();index++){
//                                cityListDestination.add(new CommanModel(array.optJSONObject(index).optString("id"),array.optJSONObject(index).optString("name")));
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else
//                {
//                    Toast.makeText(
//                            mActivity,
//                            "Not Got Response From Server.",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//        String url = "";
//        if (isOrigin){
//            url = "states/"+selectedOriginState.id+"/cities/";
//        }else if (isDestination){
//            url = "states/"+selectedDesinationState.id+"/cities/";
//        }
//        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
//        request.getAPI(false);
    }

    void getCityOtherAddress(String statId){
        String[] strArr = statId.split(",");
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        cityListOtherAddress.clear();
        List<CityModel> states = dbHelper.getAllCities(Integer.parseInt(strArr[0]));
        for (int i = 0; i < states.size(); i++) {
            CityModel cuntmodel = states.get(i);
            CommanModel model = new CommanModel(""+cuntmodel.id,""+cuntmodel.name.replaceAll("\"", ""));
            cityListOtherAddress.add(model);
        }

//        JSONObject parameters = new JSONObject();
//        Handler handler = new Handler(){
//            public void handleMessage(Message msg) {
//                String aResponse = msg.getData().getString("message");
//                if ((null != aResponse)) {
//                    try {
//                        cityListOtherAddress.clear();
//                        JSONArray array = new JSONArray(aResponse);
//                        for (int index = 0;index<array.length();index++){
//                            cityListOtherAddress.add(new CommanModel(array.optJSONObject(index).optString("id"),array.optJSONObject(index).optString("name")));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else
//                {
//                    Toast.makeText(
//                            mActivity,
//                            "Not Got Response From Server.",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//        String[] strArr = statId.split(",");
//        String url = "";
//        url = "states/"+strArr[0]+"/cities/";
//        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
//        request.getAPI(false);
    }

    boolean isAllFieldsAreValid(){
        if (binding.etCompany.getText().toString().isEmpty()){
            cmn.showToast("Select Company !!");
            binding.sagmentedGroup.setPosition(0 , true);
            return false;
        }else if (binding.etEnquiryDate.getText().toString().isEmpty()){
            cmn.showToast("Enter Select Enquiry date !!");
            binding.sagmentedGroup.setPosition(0 , true);
            return false;
        }else if (binding.etEnquiryType.getText().toString().isEmpty()){
            cmn.showToast("Enter Select Enquiry type !!");
            binding.sagmentedGroup.setPosition(0 , true);
            return false;
        }else if (binding.etMovementType.getText().toString().isEmpty()){
            cmn.showToast("Enter Select movement type !!");
            binding.sagmentedGroup.setPosition(0 , true);
            return false;
        }else if (binding.etGoodsType.getText().toString().isEmpty()){
            cmn.showToast("Enter Select goods type !!");
            binding.sagmentedGroup.setPosition(0 , true);
            return false;
        }else if (binding.etInquirySource.getText().toString().isEmpty()){
            cmn.showToast("Enter Select Enquiry source !!");
            binding.sagmentedGroup.setPosition(0 , true);
            return false;
        }else if (binding.etEnquiryType.getText().toString().equalsIgnoreCase("individual")){
            /*if (binding.etContactPerson.getText().toString().isEmpty()){
                cmn.showToast("Enter Contact Person Name !!");
                binding.sagmentedGroup.setPosition(0 , true);
                return false;
            }else*/ if (binding.etClientFirstName.getText().toString().isEmpty()){
                cmn.showToast("Enter Shipper First Name !!");
                binding.sagmentedGroup.setPosition(1 , true);
                return false;
            }else if (binding.etClientLastName.getText().toString().isEmpty()){
                cmn.showToast("Enter Shipper Last Name !!");
                binding.sagmentedGroup.setPosition(1 , true);
                return false;
            }else if (binding.etClientContact.getText().toString().isEmpty()){
                cmn.showToast("Enter Shipper contact number !!");
                binding.sagmentedGroup.setPosition(1 , true);
                return false;
            }else if (!cmn.isEmailValid(binding.etClientEmail.getText().toString())){
                cmn.showToast("Enter valid Shipper e-mail address !!");
                binding.sagmentedGroup.setPosition(1 , true);
                return false;
            }else if (binding.etServiceType.getText().toString().isEmpty()){
                cmn.showToast("Enter Service Type !!");
                binding.sagmentedGroup.setPosition(1 , true);
                return false;
            }else if (binding.etDeliveryType.getText().toString().isEmpty()){
                cmn.showToast("Enter Delivery Type !!");
                binding.sagmentedGroup.setPosition(1 , true);
                return false;
            }/*else if (binding.etExpectedMovementDate.getText().toString().isEmpty()){
                cmn.showToast("Enter Expected Movement Date !!");
                binding.sagmentedGroup.setPosition(1 , true);
                return false;
            }*/else if (binding.etAddressLine1Origin.getText().toString().isEmpty()){
                cmn.showToast("Enter Origin Address Line 1 !!");
                binding.sagmentedGroup.setPosition(2 , true);
                return false;
            }else if (selectedOriginState == null){
                cmn.showToast("Select a Origin state !!");
                binding.sagmentedGroup.setPosition(2 , true);
                return false;
            }else if (selectedOriginCity == null){
                cmn.showToast("Select a  Origin city !!");
                binding.sagmentedGroup.setPosition(2 , true);
                return false;
            }else if (binding.etPinCodeOrigin.getText().toString().length() < 6){
                cmn.showToast("Enter valid 6 digit Origin Pin code !!");
                binding.sagmentedGroup.setPosition(2 , true);
                return false;
            }/*else if (binding.etAddressLine1Destination.getText().toString().isEmpty()){
                cmn.showToast("Enter Destination Address Line 1 !!");
                binding.sagmentedGroup.setPosition(2 , true);
                return false;
            }*/else if (selectedDesinationState == null){
                cmn.showToast("Select a Destination state !!");
                binding.sagmentedGroup.setPosition(2 , true);
                return false;
            }else if (selectedDesitnationCity == null){
                cmn.showToast("Select a  Destination city !!");
                binding.sagmentedGroup.setPosition(2 , true);
                return false;
            }else{
                return true;
            }
        }else if (selectedSuggestionModel == null) {
            cmn.showToast("Enter or select an account !!");
            binding.sagmentedGroup.setPosition(0 , true);
            return false;
        }else if (binding.etContactPerson.getText().toString().isEmpty()){
            cmn.showToast("Enter Contact Person Name !!");
            binding.sagmentedGroup.setPosition(0 , true);
            return false;
        }else if (binding.etClientFirstName.getText().toString().isEmpty()){
            cmn.showToast("Enter Shipper First Name !!");
            binding.sagmentedGroup.setPosition(1 , true);
            return false;
        }else if (binding.etClientLastName.getText().toString().isEmpty()){
            cmn.showToast("Enter Shipper Last Name !!");
            binding.sagmentedGroup.setPosition(1 , true);
            return false;
        }else if (binding.etClientContact.getText().toString().isEmpty()){
            cmn.showToast("Enter Shipper contact number !!");
            binding.sagmentedGroup.setPosition(1 , true);
            return false;
        }else if (!cmn.isEmailValid(binding.etClientEmail.getText().toString())){
            cmn.showToast("Enter valid Shipper e-mail address !!");
            binding.sagmentedGroup.setPosition(1 , true);
            return false;
        }else if (binding.etServiceType.getText().toString().isEmpty()){
            cmn.showToast("Enter Service Type !!");
            binding.sagmentedGroup.setPosition(1 , true);
            return false;
        }else if (binding.etDeliveryType.getText().toString().isEmpty()){
            cmn.showToast("Enter Delivery Type !!");
            binding.sagmentedGroup.setPosition(1 , true);
            return false;
        }/*else if (binding.etExpectedMovementDate.getText().toString().isEmpty()){
            cmn.showToast("Enter Expected Movement Date !!");
            binding.sagmentedGroup.setPosition(1 , true);
            return false;
        }*/else if (binding.etAddressLine1Origin.getText().toString().isEmpty()){
            cmn.showToast("Enter Origin Address Line 1 !!");
            binding.sagmentedGroup.setPosition(2 , true);
            return false;
        }else if (selectedOriginState == null){
            cmn.showToast("Select a Origin state !!");
            binding.sagmentedGroup.setPosition(2 , true);
            return false;
        }else if (selectedOriginCity == null){
            cmn.showToast("Select a  Origin city !!");
            binding.sagmentedGroup.setPosition(2 , true);
            return false;
        }else if (binding.etPinCodeOrigin.getText().toString().length() < 6){
            cmn.showToast("Enter valid 6 digit Origin Pin code !!");
            binding.sagmentedGroup.setPosition(2 , true);
            return false;
        }/*else if (binding.etAddressLine1Destination.getText().toString().isEmpty()){
            cmn.showToast("Enter Destination Address Line 1 !!");
            binding.sagmentedGroup.setPosition(2 , true);
            return false;
        }*/else if (selectedDesinationState == null){
            cmn.showToast("Select a Destination state !!");
            binding.sagmentedGroup.setPosition(2 , true);
            return false;
        }else if (selectedDesitnationCity == null){
            cmn.showToast("Select a  Destination city !!");
            binding.sagmentedGroup.setPosition(2 , true);
            return false;
        }/*else if (binding.etPinCodeDestination.getText().toString().length() < 6){
            cmn.showToast("Enter valid 6 digit Destination Pin code !!");
            binding.sagmentedGroup.setPosition(2 , true);
            return false;
        }*/else{
            return true;
        }
    }


    JSONObject getAllParams(){
        JSONObject parameters = new JSONObject();
        try {
            JSONObject client = new JSONObject();
            if (isEdit){
                parameters.put("id" , inquiryId);
            }else{
                parameters.put("id" , JSONObject.NULL);
            }

            parameters.put("inquiryDate" , replaceToNull(cmn.getReverseDate(binding.etEnquiryDate.getText().toString())));
            parameters.put("inquiryType" , replaceToNull(binding.etEnquiryType.getText().toString().toUpperCase()));
            parameters.put("movementType" , replaceToNull(binding.etMovementType.getText().toString().toUpperCase()));
            parameters.put("goodsType" , replaceToNull(binding.etGoodsType.getText().toString().replace(" " , "_").toUpperCase()));
            parameters.put("source" , replaceToNull(binding.etInquirySource.getText().toString().toUpperCase()));

            parameters.put("company" , new JSONObject().put("id" , selectedCompany.id));

            if (!binding.etEnquiryType.getText().toString().equalsIgnoreCase("individual")){
                parameters.put("account" , new JSONObject().put("id" , selectedSuggestionModel.id).put("accountName", selectedSuggestionModel.name));
            }else{
                parameters.put("account" , new JSONObject().put("id" , JSONObject.NULL).put("accountName", JSONObject.NULL));
            }

            parameters.put("contactPerson" , replaceToNull(binding.etContactPerson.getText().toString()));


            client.put("firstName" ,replaceToNull(binding.etClientFirstName.getText().toString()));
            client.put("lastName" ,replaceToNull(binding.etClientLastName.getText().toString()));
            client.put("contactNumber" ,replaceToNull(binding.etClientContact.getText().toString()));
            client.put("alternateNumber" ,replaceToNull(binding.etClientAlternetContact.getText().toString()));
            client.put("email" ,replaceToNull(binding.etClientEmail.getText().toString()));
            client.put("alternateEmail" ,replaceToNull(binding.etClientAlternetEmail.getText().toString()));

            parameters.put("shipper" , client);

            parameters.put("serviceType" , replaceToNull(binding.etServiceType.getText().toString()));
            parameters.put("deliveryType" , replaceToNull(binding.etDeliveryType.getText().toString()));
            parameters.put("expectedMovementDate" , replaceToNull(cmn.getReverseDate(binding.etExpectedMovementDate.getText().toString())));


            JSONObject addressOrigin = new JSONObject();
            if (isEdit){
                addressOrigin.put("id" , originAddId);
            }else{
                addressOrigin.put("id" , JSONObject.NULL);
            }
            addressOrigin.put("addressLine1" ,replaceToNull(binding.etAddressLine1Origin.getText().toString()) );
            addressOrigin.put("addressLine2" ,replaceToNull(binding.etAddressLine2Origin.getText().toString()) );
            addressOrigin.put("pinCode" ,replaceToNull(binding.etPinCodeOrigin.getText().toString()) );
            addressOrigin.put("country" , new JSONObject().put("id" , selectedOriginCountry.id).put("name" , selectedOriginCountry.name));
           // String[] srr = selectedOriginState.id.split(",");
            addressOrigin.put("state" , new JSONObject().put("id" ,selectedOriginState.id).put("name" , selectedOriginState.name));
            addressOrigin.put("city" , new JSONObject().put("id" , selectedOriginCity.id).put("name" , selectedOriginCity.name));
            parameters.put("originAddress" , addressOrigin);

            if (binding.cbOtherDestination.isChecked()){
                parameters.put("multipleDestinationAddress" , true);

                JSONArray otherDestinationAddresses = new JSONArray();

                for (int i = 0 ; i < binding.layOtherDestAddress.getChildCount() ; i ++){
                    JSONObject address = new JSONObject();
                    View desti = binding.layOtherDestAddress.getChildAt(i);
                    TextInputEditText etAddressLine1 = desti.findViewById(R.id.etAddressLine1);
                    TextInputEditText etAddressLine2 = desti.findViewById(R.id.etAddressLine2);
                    TextInputEditText etCountry = desti.findViewById(R.id.etCountry);
                    TextInputEditText etState = desti.findViewById(R.id.etState);
                    TextInputEditText etCity = desti.findViewById(R.id.etCity);
                    TextInputEditText pin = desti.findViewById(R.id.etPinCode);

                      address.put("addressLine1" ,replaceToNull(etAddressLine1.getText().toString()) );
                     address.put("addressLine2" ,replaceToNull(etAddressLine2.getText().toString()) );
                     address.put("pinCode" ,replaceToNull(pin.getText().toString()) );
                     address.put("country" , new JSONObject().put("id" , etCountry.getTag()).put("name" , etCountry.getText().toString()));
                    String[] stateId = etState.getTag().toString().split(",");
                     address.put("state" , new JSONObject().put("id" , stateId[0]).put("name" , etState.getText().toString()));
                     address.put("city" , new JSONObject().put("id" , etCity.getTag()).put("name" , etCity.getText().toString()));
                      otherDestinationAddresses.put(address);

                }
                parameters.put("otherDestinationAddresses" , otherDestinationAddresses);
            }else {
                parameters.put("multipleDestinationAddress" , false);
                parameters.put("otherDestinationAddresses" , JSONObject.NULL);
            }

            if (binding.cbOtherOrigin.isChecked()){
                parameters.put("multipleOriginAddress" , true);
                JSONArray otherDestinationAddresses = new JSONArray();

                for (int i = 0 ; i < binding.layOtherOriginAddress.getChildCount() ; i ++){
                    JSONObject address = new JSONObject();
                    TextInputEditText etAddressLine1 = binding.layOtherOriginAddress.getChildAt(i).findViewById(R.id.etAddressLine1);
                    TextInputEditText etAddressLine2 = binding.layOtherOriginAddress.getChildAt(i).findViewById(R.id.etAddressLine2);
                    TextInputEditText etCountry = binding.layOtherOriginAddress.getChildAt(i).findViewById(R.id.etCountry);
                    TextInputEditText etState = binding.layOtherOriginAddress.getChildAt(i).findViewById(R.id.etState);
                    TextInputEditText etCity = binding.layOtherOriginAddress.getChildAt(i).findViewById(R.id.etCity);
                    address.put("addressLine1" ,replaceToNull(etAddressLine1.getText().toString()) );
                    address.put("addressLine2" ,replaceToNull(etAddressLine2.getText().toString()) );
                    address.put("pinCode" ,replaceToNull(binding.etPinCodeDestination.getText().toString()) );
                    address.put("country" , new JSONObject().put("id" , etCountry.getTag()).put("name" , etCountry.getText().toString()));
                    String[] stateId = etState.getTag().toString().split(",");
                    address.put("state" , new JSONObject().put("id" ,stateId[0]).put("name" , etState.getText().toString()));
                    address.put("city" , new JSONObject().put("id" , etCity.getTag()).put("name" , etCity.getText().toString()));
                    otherDestinationAddresses.put(address);
                }
                parameters.put("otherOriginAddresses" , otherDestinationAddresses);
            }else {
                parameters.put("multipleOriginAddress" , false);
                parameters.put("otherOriginAddresses" , JSONObject.NULL);
            }



            JSONObject destinationAddress = new JSONObject();
            if (isEdit){
                destinationAddress.put("id" , destAddId);
            }else{
                destinationAddress.put("id" , JSONObject.NULL);
            }
            destinationAddress.put("addressLine1" ,replaceToNull(binding.etAddressLine1Destination.getText().toString()) );
            destinationAddress.put("addressLine2" ,replaceToNull(binding.etAddressLine2Destination.getText().toString()) );
            destinationAddress.put("pinCode" ,replaceToNull(binding.etPinCodeDestination.getText().toString()) );
            destinationAddress.put("country" , new JSONObject().put("id" , selectedDesinationCountry.id).put("name" , selectedDesinationCountry.name));
          //  String[] srr1 = selectedOriginState.id.split(",");
            destinationAddress.put("state" , new JSONObject().put("id" ,selectedDesinationState.id).put("name" , selectedDesinationState.name));
            destinationAddress.put("city" , new JSONObject().put("id" , selectedDesitnationCity.id).put("name" , selectedDesitnationCity.name));
            parameters.put("destinationAddress" , destinationAddress);
            parameters.put("additionalServices" , binding.cbAdditionalServices.isChecked());

           // parameters.put("warehousing" , replaceToNull(binding.etWarehousing.getText().toString()));
           // parameters.put("warehouseTemprature" , replaceToNull(binding.etWarehouseTemprature.getText().toString()));
             parameters.put("warehousing" , JSONObject.NULL);
             parameters.put("warehouseTemprature" , JSONObject.NULL);
            parameters.put("seaAllowance" , JSONObject.NULL);
            parameters.put("seaUnit" , JSONObject.NULL);
            parameters.put("airAllowance" , JSONObject.NULL);
            parameters.put("airUnit" , JSONObject.NULL);
            parameters.put("surfaceAllowance" , JSONObject.NULL);
            parameters.put("surfaceUnit" , JSONObject.NULL);

            JSONArray handimanArray = new JSONArray();
            String[] handiman = binding.etHandymanServices.getText().toString().split(",");

            for (int i = 0; i < handiman.length ; i++) {
                handimanArray.put(handiman[i]);
            }
            JSONArray relocationServicesArray = new JSONArray();
            String[] relocationServices = binding.etRelocationServices.getText().toString().split(",");

            for (int i = 0; i < relocationServices.length ; i++) {
                relocationServicesArray.put(relocationServices[i]);
            }

            JSONArray visaImmigrationAssistanceArray = new JSONArray();
            String[] visaImmigrationAssistance = binding.etVisaImmigrationAssistance.getText().toString().split(",");

            for (int i = 0; i < visaImmigrationAssistance.length ; i++) {
                visaImmigrationAssistanceArray.put(visaImmigrationAssistance[i]);
            }

            parameters.put("visaImmigrationAssistance" , visaImmigrationAssistanceArray);
            parameters.put("handymanServices" , handimanArray);
            parameters.put("relocationServices" , relocationServicesArray);
            parameters.put("petAvailable" , binding.cbPet.isChecked());


            if (binding.cbPet.isChecked()){
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
                }else  if (binding.cbimportpetNo.isChecked()){
                    petInfo.put("petImport" , false);
                }else{
                    petInfo.put("petImport" , null);
                }

                if (binding.cbTravelledtoEuropeancountriesYes.isChecked()){
                    petInfo.put("travelledToEuropeanCounty" , "true");
                }else if (binding.cbTravelledtoEuropeancountriesNo.isChecked()){
                    petInfo.put("travelledToEuropeanCounty" , "false");
                }else{
                    petInfo.put("travelledToEuropeanCounty" , null);
                }

                if (binding.cbVaccinationBookletavailableYes.isChecked()){
                    petInfo.put("vaccinationBookletAvailable" , "true");
                }else if (binding.cbVaccinationBookletavailableNo.isChecked()){
                    petInfo.put("vaccinationBookletAvailable" , "false");
                }else{
                    petInfo.put("vaccinationBookletAvailable" , null);
                }

                if (binding.cbPetInsuranceRequiredYes.isChecked()){
                    petInfo.put("petInsuranceRequired" , "true");
                }else  if (binding.cbPetInsuranceRequiredNo.isChecked()){
                    petInfo.put("petInsuranceRequired" , "false");
                }else{
                    petInfo.put("petInsuranceRequired" , null);
                }

                if (binding.cbNOCCertificateforPetYes.isChecked()){
                    petInfo.put("petNocCertificate" , "true");
                }else if (binding.cbNOCCertificateforPetNo.isChecked()){
                    petInfo.put("petNocCertificate" , "false");
                }else{
                    petInfo.put("petNocCertificate" , null);
                }

                if (binding.cbBloodTestReportYes.isChecked()){
                    petInfo.put("bloodTestReport" , "true");
                }else  if (binding.cbBloodTestReportNo.isChecked()){
                    petInfo.put("bloodTestReport" , "false");
                }else{
                    petInfo.put("bloodTestReport" , null);
                }

                if (binding.cbVaccinationCertificateYes.isChecked()){
                    petInfo.put("vaccinationCertificate" , "true");
                }else if (binding.cbVaccinationCertificateNo.isChecked()){
                    petInfo.put("vaccinationCertificate" , "false");
                }else{
                    petInfo.put("vaccinationCertificate" , null);
                }

                if (binding.tvDocumentName.getText().toString().isEmpty()){
                    petInfo.put("petDocuments" , new JSONArray());
                }else{
                    petInfo.put("petDocuments" , new JSONArray());
                }

                if (binding.tvDocumentName.getText().toString().isEmpty()){
                    // petInfo.put("petImages" , new JSONArray().put("fileName" , "petDoc"));
                    petInfo.put("petImages" , new JSONArray());
                }else{
                    petInfo.put("petImages" , new JSONArray());
                }

                petInfo.put("remarks" , replaceToNull(binding.etRemarkIfAny.getText().toString()));
                parameters.put("petInfo" , petInfo);
            }else{
                parameters.put("petInfo" , JSONObject.NULL);
            }

            parameters.put("vehicleAvailable" , binding.cbVehicle.isChecked());

            if (binding.cbVehicle.isChecked()){
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

                if (binding.cbVehicle.isChecked()){
                    vehicleInfoArray.put(vehicleInfo);
                    parameters.put("vehicleInfo" , vehicleInfoArray);
                }else{
                    parameters.put("vehicleInfo" , vehicleInfoArray.put(new JSONObject()));
                }
            }else{
                parameters.put("vehicleInfo" , new JSONArray());
            }






            Log.e("all params " , parameters.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parameters;
    }

    void submitRequest(){
        JSONObject parameters = getAllParams();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject obj = new JSONObject(aResponse);
                        if (isEdit){
                            cmn.showToast("Enquiry Updated Successfully !!");
                        }else {
                            cmn.showToast("Enquiry Added Successfully !!");
                        }
                        if (cmn.getUserType().equalsIgnoreCase(cmn.userSurveyor)){
                            startActivity(new Intent(AddInquiry.this, Dashboard.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }else{
                            finish();
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



        String url = "inquiry";

        if (isEdit){
            HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
            request.putAPI();
        }else {
            HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
            request.postAPI();
        }

    }
    void getAccountData(){
        dataListSuggestions.clear();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<CountryModel> account = dbHelper.getAllAccount();
        for (int i = 0; i < account.size(); i++) {
            CountryModel cuntmodel = account.get(i);
            CommanModel model = new CommanModel(""+cuntmodel.id,""+cuntmodel.name.replaceAll("\"", ""));
            dataListSuggestions.add(model);
           // companyList.add(model);
        }
//        JSONObject parameters = new JSONObject();
//        Handler handler = new Handler(){
//            public void handleMessage(Message msg) {
//                String aResponse = msg.getData().getString("message");
//                if ((null != aResponse)) {
//                    try {
//                        JSONObject obj = new JSONObject(aResponse);
//                        JSONArray data = obj.optJSONArray("contant");
//                        dataListSuggestions.clear();
//                        for (int i = 0; i <data.length() ; i++) {
//                            dataListSuggestions.add(new CommanModel(data.optJSONObject(i).optString("id")
//                                    ,data.optJSONObject(i).optString("companyName")));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else
//                {
//                    Toast.makeText(
//                            mActivity,
//                            "Not Got Response From Server.",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//        String url = "account?page=0&size=100&searchFields=account&areaType=DOMESTIC";
//        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
//        request.getAPI(false);
    }

    String replaceToNull(String str){
        if (str.isEmpty())
            return null;
        else return str;
    }


    @Override
    public void itemSelected(CommanModel selectedItem, int requestCode) {
        switch (requestCode){
            case 1 :
                binding.etCountryOrigin.setText("" + selectedItem.name);
                selectedOriginCountry = selectedItem;
                selectedOriginCity = null;
                selectedOriginState = null;
                binding.etStateOrigin.setText("");
                binding.etCityOrigin.setText("");
                getStates(true);
                break;
            case 2 :
                binding.etStateOrigin.setText("" + selectedItem.name);
                selectedOriginState = selectedItem;
                selectedOriginCity = null;
                binding.etCityOrigin.setText("");
                getCity(true,false);
                break;
            case 3 :
                binding.etCityOrigin.setText("" + selectedItem.name);
                selectedOriginCity = selectedItem;
                break;

            case 11 :
                binding.etCountryDestination.setText("" + selectedItem.name);
                selectedDesinationCountry = selectedItem;
                selectedDesitnationCity = null;
                selectedDesinationState = null;
                binding.etStateDestination.setText("");
                binding.etCityDestination.setText("");
                getStates(false);
                break;
            case 22 :
                binding.etStateDestination.setText("" + selectedItem.name);
                selectedDesinationState = selectedItem;
                selectedDesitnationCity = null;
                binding.etCityDestination.setText("");
                getCity(false,true);
                break;
            case 10000 :
                binding.etCityDestination.setText("" + selectedItem.name);
                selectedDesitnationCity = selectedItem;
            case 44 :
                binding.etCompany.setText("" + selectedItem.name);
                selectedCompany = selectedItem;
                break;
        }
    }

}