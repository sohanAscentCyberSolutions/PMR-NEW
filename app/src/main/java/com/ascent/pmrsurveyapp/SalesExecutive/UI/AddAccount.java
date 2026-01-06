package com.ascent.pmrsurveyapp.SalesExecutive.UI;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ascent.pmrsurveyapp.Fragments.ItemSelecter;
import com.ascent.pmrsurveyapp.Fragments.Selection_Dialog;
import com.google.android.material.textfield.TextInputEditText;
import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.AccountModel;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.ActivityAddAccountBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddAccount extends AppCompatActivity implements ItemSelecter {
    Comman cmn;
    Activity mActivity;
    ActivityAddAccountBinding binding;

    public static AccountModel tempData = new AccountModel();

    ArrayList<View> contactsList = new ArrayList<>();

    ItemSelecter selecter;

    ArrayList<CommanModel> countryList = new ArrayList<>();
    ArrayList<CommanModel> statesList = new ArrayList<>();
    ArrayList<CommanModel> cityList = new ArrayList<>();

    ArrayList<CommanModel> industryList = new ArrayList<>();

    CommanModel selectedCountry;
    CommanModel selectedState;
    CommanModel selectedCity;
    CommanModel selectedIndustry;

    Boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_account);
        mActivity = this;
        cmn = new Comman(mActivity);
        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selecter = this::itemSelected;
        isEdit = getIntent().getBooleanExtra("isEdit" , false);

        if (isEdit){
            getCountries();
            getIndustry();
            addDetails();
        }else{
            addContact();
            getCountries();
            getIndustry();
        }



        binding.etCountry.setOnClickListener(new View.OnClickListener() {
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

        binding.etState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selection_Dialog newFragment = new Selection_Dialog();
                newFragment.dataList = statesList;
                newFragment.listiner = selecter;
                newFragment.requestCode = 2;
                newFragment.titleStr = "Select State";
                newFragment.show(getSupportFragmentManager() , "temp");
            }
        });

        binding.etCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selection_Dialog newFragment = new Selection_Dialog();
                newFragment.dataList = cityList;
                newFragment.listiner = selecter;
                newFragment.requestCode = 3;
                newFragment.titleStr = "Select City";
                newFragment.show(getSupportFragmentManager() , "temp");
            }
        });


        binding.etIndustry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = new String[industryList.size()];
                for (int i = 0; i <industryList.size() ; i++) {
                    items[i] = industryList.get(i).name;
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select industry");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etIndustry.setText("" + items[selection[0]]);
                        selectedIndustry = industryList.get(selection[0]);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });



        binding.btAddMoreContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
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
    }

    void addContact(){
        final View artical = getLayoutInflater().inflate(R.layout.row_contact , null);
        final int index = contactsList.size();
        ImageView ivDelete = artical.findViewById(R.id.ivDelete);

        TextInputEditText etContactPerson = artical.findViewById(R.id.etContactPerson);
        TextInputEditText etEmail = artical.findViewById(R.id.etEmail);
        TextInputEditText etContactnumber = artical.findViewById(R.id.etContactnumber);
        TextInputEditText etDesignation = artical.findViewById(R.id.etDesignation);

        artical.setTag(index);

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < contactsList.size() ; i++) {
                    int tag = (int) contactsList.get(i).getTag();
                    if (tag == index){
                        binding.contactHolder.removeView(contactsList.get(i));
                        contactsList.remove(i);
                        break;
                    }
                }


               /* if (contactsList.size()>1){
                    binding.contactHolder.removeView(artical);
                    contactsList.remove(index-1);
                }else{
                    cmn.showToast("Atleast one contact required");
                }*/

            }
        });

        binding.contactHolder.addView(artical);
        contactsList.add(artical);
    }

    void addDetails(){
        binding.etCompanyName.setText(""+cmn.replaceNull(tempData.companyName));
        binding.etEmail.setText(""+cmn.replaceNull(tempData.email));
        binding.etContactnumber.setText(""+cmn.replaceNull(tempData.mobile));
        binding.etlandlineContactnumber.setText(""+cmn.replaceNull(tempData.landlineNumber));
        binding.etVendorRegistrationCode.setText(""+cmn.replaceNull(tempData.registrationCode));
        binding.etPaymentTerms.setText(""+cmn.replaceNull(tempData.paymentTerms));
        binding.etGSTIN.setText(""+cmn.replaceNull(tempData.gstinUin));
        binding.etIndustry.setText(""+cmn.replaceNull(tempData.industry.name));
        selectedIndustry = tempData.industry;
        binding.etAddressLine1.setText(""+cmn.replaceNull(tempData.address.addressLine1));
        binding.etAddressLine2.setText(""+cmn.replaceNull(tempData.address.addressLine2));
        binding.etState.setText(""+cmn.replaceNull(tempData.address.state.name));
        selectedState = tempData.address.state;
        binding.etCity.setText(""+cmn.replaceNull(tempData.address.city.name));
        selectedCity = tempData.address.city;
        binding.etPinCode.setText(""+cmn.replaceNull(tempData.address.pinCode));

        for (int i = 0; i < tempData.contacts.size(); i++) {
            final View artical = getLayoutInflater().inflate(R.layout.row_contact , null);
            final int index = contactsList.size();
            ImageView ivDelete = artical.findViewById(R.id.ivDelete);

            TextInputEditText etContactPerson = artical.findViewById(R.id.etContactPerson);
            TextInputEditText etEmail = artical.findViewById(R.id.etEmail);
            TextInputEditText etContactnumber = artical.findViewById(R.id.etContactnumber);

            etContactPerson.setText(""+tempData.contacts.get(i).name);
            etEmail.setText(""+tempData.contacts.get(i).number);
            etContactnumber.setText(""+tempData.contacts.get(i).email);


            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (contactsList.size()>1){
                        binding.contactHolder.removeView(artical);
                        contactsList.remove(index-1);
                    }else{
                        cmn.showToast("Atleast one contact required");
                    }

                }
            });

            binding.contactHolder.addView(artical);
            contactsList.add(artical);
        }
    }

    void getIndustry(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        industryList.clear();
                        JSONArray array = new JSONArray(aResponse);
                        for (int index = 0;index<array.length();index++){
                            industryList.add(new CommanModel(array.optJSONObject(index).optString("id"),array.optJSONObject(index).optString("name")));
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
        url = "industry?Find=All";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }


    void getCountries(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        countryList.clear();
                        JSONArray array = new JSONArray(aResponse);
                        for (int index = 0;index<array.length();index++){
                            CommanModel temp = new CommanModel(array.optJSONObject(index).optString("id"),array.optJSONObject(index).optString("name"));
                            countryList.add(temp);
                            if (temp.id.equalsIgnoreCase("101")){
                                selectedCountry = temp;
                                binding.etCountry.setText(""+selectedCountry.name);
                                getState();
                            }
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
        url = "countries";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }


    void getState(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        statesList.clear();
                        JSONArray array = new JSONArray(aResponse);
                        for (int index = 0;index<array.length();index++){
                            statesList.add(new CommanModel(array.optJSONObject(index).optString("id"),array.optJSONObject(index).optString("name")));
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
        url = "countries/"+selectedCountry.id+"/states/";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }

    void getCity(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        cityList.clear();
                        JSONArray array = new JSONArray(aResponse);
                        for (int index = 0;index<array.length();index++){
                            cityList.add(new CommanModel(array.optJSONObject(index).optString("id"),array.optJSONObject(index).optString("name")));
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
        url = "states/"+selectedState.id+"/cities/";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }

    boolean isAllFieldsAreValid(){

        if (binding.etCompanyName.getText().toString().isEmpty()){
            cmn.showToast("Enter Company Name !!");
            return false;
        }else if (!cmn.isEmailValid(binding.etEmail.getText().toString())){
            cmn.showToast("Enter valid e-mail address !!");
            return false;
        }else if (binding.etContactnumber.getText().toString().isEmpty()){
            cmn.showToast("Enter contact number !!");
            return false;
        }else if (binding.etPaymentTerms.getText().toString().isEmpty()){
            cmn.showToast("Enter Payment Terms !!");
            return false;
        }else if (binding.etIndustry.getText().toString().isEmpty()){
            cmn.showToast("Enter Industry !!");
            return false;
        }else if (binding.etAddressLine1.getText().toString().isEmpty()){
            cmn.showToast("Enter AddressLine 1 !!");
            return false;
        }else if (selectedCountry == null){
            cmn.showToast("Select a country !!");
            return false;
        }else if (selectedState == null){
            cmn.showToast("Select a state !!");
            return false;
        }else if (selectedCity == null){
            cmn.showToast("Select a city !!");
            return false;
        }else if (binding.etPinCode.getText().toString().length() < 6){
            cmn.showToast("Enter valid 6 digit Pin code !!");
            return false;
        }else {
            if (contactsList.size() > 0){
                for (int i = 0; i < contactsList.size(); i++) {
                    TextInputEditText etContactPerson = contactsList.get(i).findViewById(R.id.etContactPerson);
                    TextInputEditText etContactnumber = contactsList.get(i).findViewById(R.id.etContactnumber);
                    TextInputEditText etEmail = contactsList.get(i).findViewById(R.id.etEmail);
                    if (etContactPerson.getText().toString().isEmpty()){
                        cmn.showToast("Enter Contact Person Name !!");
                        etContactPerson.requestFocus();
                        return false;
                    }else if (!cmn.isEmailValid(etEmail.getText().toString())){
                        cmn.showToast("Enter valid contacts Email !!");
                        etEmail.requestFocus();
                        return false;
                    }else if (etContactnumber.getText().toString().isEmpty()){
                        cmn.showToast("Enter contacts contact number !!");
                        etContactnumber.requestFocus();
                        return false;
                    }
                }
                return true;
            }else{
                return true;
            }
        }
    }

    void submitRequest(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject obj = new JSONObject(aResponse);
                        if (isEdit) {
                            cmn.showToast("Account Updated Successfully !!");
                        }else{
                            cmn.showToast("Account Added Successfully !!");
                        }
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
            JSONObject address = new JSONObject();

            parameters.put("id" , null);
            parameters.put("companyName" , replaceToNull(binding.etCompanyName.getText().toString()));
            parameters.put("email" , replaceToNull(binding.etEmail.getText().toString()));
            parameters.put("mobile" , replaceToNull(binding.etContactnumber.getText().toString()));
            parameters.put("landlineNumber" , replaceToNull(binding.etlandlineContactnumber.getText().toString()));
            parameters.put("registrationCode" , replaceToNull(binding.etVendorRegistrationCode.getText().toString()));

            parameters.put("paymentTerms" , replaceToNull(binding.etPaymentTerms.getText().toString()));
            parameters.put("gstinUin" , replaceToNull(binding.etGSTIN.getText().toString()));

            parameters.put("industry" , new JSONObject().put("id" , selectedIndustry.id));


            address.put("addressLine1" ,replaceToNull(binding.etAddressLine1.getText().toString()) );
            address.put("addressLine2" ,replaceToNull(binding.etAddressLine2.getText().toString()) );
            address.put("pinCode" ,replaceToNull(binding.etPinCode.getText().toString()) );
            address.put("country" , new JSONObject().put("id" , selectedCountry.id).put("name" , selectedCountry.name));
            address.put("state" , new JSONObject().put("id" , selectedState.id).put("name" , selectedState.name));
            address.put("city" , new JSONObject().put("id" , selectedCity.id).put("name" , selectedCity.name));


            parameters.put("address" , address);

            JSONArray contactsArray = new JSONArray();

            if (contactsList.size() > 0 ){
                for (int i = 0; i < contactsList.size() ; i++) {
                    View artical = contactsList.get(i);
                    TextInputEditText etContactPerson = artical.findViewById(R.id.etContactPerson);
                    TextInputEditText etEmail = artical.findViewById(R.id.etEmail);
                    TextInputEditText etContactnumber = artical.findViewById(R.id.etContactnumber);
                    TextInputEditText etDesignation = artical.findViewById(R.id.etDesignation);
                    JSONObject temp = new JSONObject();

                    temp.put("id" , null);
                    temp.put("name" , replaceToNull(etContactPerson.getText().toString()));
                    temp.put("email" , replaceToNull(etEmail.getText().toString()));
                    temp.put("number" , replaceToNull(etContactnumber.getText().toString()));
                    temp.put("designation" , etDesignation.getText().toString());
                    contactsArray.put(temp);

                }
            }
            parameters.put("contacts" , contactsArray);
            if (isEdit) {
                parameters.put("id", tempData.id);
            }
            Log.e("all params " , parameters.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";
        url = "account";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        if (isEdit){
            request.putAPI();
        }else{
            request.postAPI();
        }

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
                selectedCountry = selectedItem;
                selectedState = null;
                binding.etState.setText("");
                selectedCity = null;
                binding.etCity.setText("");
                binding.etCountry.setText(""+selectedCountry.name);
                getState();
            break;
            case 2 :
                selectedState = selectedItem;
                selectedCity = null;
                binding.etCity.setText("");
                binding.etState.setText(""+selectedState.name);
                getCity();
                break;
            case 3 :
                selectedCity = selectedItem;
                binding.etCity.setText(""+selectedCity.name);
                break;
        }
    }
}