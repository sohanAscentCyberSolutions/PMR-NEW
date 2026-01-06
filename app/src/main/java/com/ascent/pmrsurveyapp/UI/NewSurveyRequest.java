package com.ascent.pmrsurveyapp.UI;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.ActivityNewSurveyRequestBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewSurveyRequest extends AppCompatActivity {

    ActivityNewSurveyRequestBinding binding;
    Activity mActivity;
    Comman cmn;
    ArrayList<CommanModel> branchList = new ArrayList<>();

    ArrayList<CommanModel> surveyorList = new ArrayList<>();

    public static InquiryModel detail;

    CommanModel selectedBranch;
    CommanModel selectedSurveyor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_survey_request);
        mActivity = this;
        cmn = new Comman(mActivity);
        binding.datePicker.setVisibility(View.GONE);
        binding.timePicker.setVisibility(View.GONE);
        getBranches();


        binding.etEnquiry.setText(""+detail.shipper.name + " "+cmn.replaceNull(detail.account.name)+" "+detail.date);
        binding.btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (binding.etBranch.getText().toString().isEmpty()){
                    cmn.showToast("Select a branch !!");
                }else if (binding.etsurveyor.getText().toString().isEmpty()){
                    cmn.showToast("Select a Surveyor !!");
                }else if (binding.etFromDate.getText().toString().isEmpty()){
                    cmn.showToast("Select From date !!");
                }else if (binding.etFromTime.getText().toString().isEmpty()){
                    cmn.showToast("Select From Time !!");
                }else if (binding.etToDate.getText().toString().isEmpty()){
                    cmn.showToast("Select To date !!");
                }else if (binding.etToTime.getText().toString().isEmpty()){
                    cmn.showToast("Select To Time !!");
                }else{
                    String[] temp = binding.etFromDate.getText().toString().split("-");
                    String fromYear = ""+temp[2];
                    String fromMonth = ""+temp[1];
                    String fromDay = ""+temp[0];

                    String fromTime = binding.etFromTime.getText().toString();
//                    String fromHH = ""+temptime[0];
//                    String fromMM = ""+temptime[1];
//                    String fromAP = ""+temptime[2];

                    String[] temp1 = binding.etToDate.getText().toString().split("-");
                    String fromYear1 = ""+temp1[2];
                    String fromMonth1 = ""+temp1[1];
                    String fromDay1 = ""+temp1[0];

                    String toTime = binding.etToTime.getText().toString();
//                    String fromHH1 = ""+temptime1[0];
//                    String fromMM1 = ""+temptime1[1];
//                    String fromAP1 = ""+temptime1[2];

                    String dateTimeFrom =  fromYear + "-"+fromMonth+"-"
                            +fromDay + "T"+fromTime+"+05:30";


                    String dateTimeTo =  fromYear1 + "-"+fromMonth1+"-"
                            +fromDay1 + "T"+toTime+":00+05:30";


                    Log.e("date&time" , "From"+dateTimeFrom + "  To"+dateTimeTo);


                    requestSurvey(dateTimeFrom , dateTimeTo);
                }

            }
        });


        binding.etBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = new String[branchList.size()];
                for (int i = 0; i <branchList.size() ; i++) {
                    items[i] = branchList.get(i).name;
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Branch");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        binding.etBranch.setText("" + items[selection[0]]);
                        selectedBranch = branchList.get(selection[0]);
                        selectedSurveyor = null;
                        binding.etsurveyor.setText("");
                        getSurveyors();
                        binding.datePicker.setVisibility(View.GONE);
                        binding.timePicker.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        binding.etsurveyor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] selection = {0};
                String[] items = new String[surveyorList.size()];
                for (int i = 0; i <surveyorList.size() ; i++) {
                    items[i] = surveyorList.get(i).name;
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("Select Surveyor");
                alert.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection[0] = which;
                        selectedSurveyor = surveyorList.get(selection[0]);
                        binding.etsurveyor.setText(""+selectedSurveyor.name);
                     //   binding.datePicker.setVisibility(View.VISIBLE);
                     //   binding.timePicker.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });


        binding.etFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etToTime.setText("");
                binding.etToDate.setText("");
                String dtStart = ""+ detail.date;
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date date = format.parse(dtStart);
                    System.out.println(dtStart);
                    cmn.showDatePicker(binding.etFromDate , date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.etToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.etFromDate.getText().toString().isEmpty()){
                    cmn.showToast("Please select from date");
                }else{
                    String dtStart = ""+binding.etFromDate.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date date = format.parse(dtStart);
                        System.out.println(date);
                        cmn.showDatePicker(binding.etToDate , date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        binding.etFromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cmn.showTimePicker24(binding.etFromTime);

            }
        });

        binding.etToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmn.showTimePicker24(binding.etToTime);
            }
        });
    }
    void requestSurvey(String from , String to){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                   cmn.showToast("Requested Successfully !!");
                   finish();
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
            parameters.put("id" , JSONObject.NULL);
            parameters.put("startDate" , from);
            parameters.put("endDate" , to);
            parameters.put("inquiry" , new JSONObject().put("id" , detail.id));
            parameters.put("surveyor" , new JSONObject().put("id" , selectedSurveyor.id));
            parameters.put("branch" , new JSONObject().put("id" , selectedBranch.id));
            parameters.put("type" , "MOVE_INQUIRY");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";
        url = "surveyrequests";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.postAPI();
    }
    void getBranches(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        branchList.clear();
                        JSONObject obj = new JSONObject(aResponse);
                        JSONArray array = obj.optJSONArray("contant");
                        for (int index = 0;index<array.length();index++){
                            branchList.add(new CommanModel(array.optJSONObject(index).optString("id"),array.optJSONObject(index).optString("name")));
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
        url = "branch?searchFields=name&page=0&size=500";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }
    void getSurveyors(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        surveyorList.clear();
                        JSONArray array = new JSONArray(aResponse);
                        for (int index = 0;index<array.length();index++){
                            surveyorList.add(new CommanModel(array.optJSONObject(index).optString("id"),array.optJSONObject(index).optString("fullName")));
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
        url = "appuser?Find=ByBranch&branchId="+selectedBranch.id+"&role=ROLE_SURVEYOR";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }
}