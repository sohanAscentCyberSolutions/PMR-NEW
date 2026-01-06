package com.ascent.pmrsurveyapp.SalesExecutive.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.ascent.pmrsurveyapp.Fragments.Request_Details;
import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Adepters.InquiryAdepter;
import com.ascent.pmrsurveyapp.SalesExecutive.Adepters.OfflineInquiryAdepter;
import com.ascent.pmrsurveyapp.SalesExecutive.Fragmants.Assign_Inquiry_Dialog;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.OfflineInquryModel;
import com.ascent.pmrsurveyapp.SalesExecutive.UpdateData;
import com.ascent.pmrsurveyapp.UI.Dashboard;
import com.ascent.pmrsurveyapp.UI.NewSurveyRequest;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.ActivityInquiriesBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityOfflineInquiriesBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class OfflineInquiries extends AppCompatActivity {

    Comman cmn;
    Activity mActivity;
    ActivityOfflineInquiriesBinding binding;
    ArrayList<OfflineInquryModel> dataList = new ArrayList<>();
    ArrayList<OfflineInquryModel> dataListFilter = new ArrayList<>();
    private OfflineInquiryAdepter mAdapter;
    String moveType = "HOUSE_HOLD_GOODS";
    String areaType =  "DOMESTIC";
    String transactionType = "Import";
    ArrayList<CommanModel> dataListSuggestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_offline_inquiries);

        mActivity = this;
        cmn = new Comman(mActivity);

        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dataListFilter.clear();
                String filterStr = binding.etFilter.getText().toString().toLowerCase();
                if (filterStr.isEmpty()){
                    dataListFilter.addAll(dataList);
                    binding.ivClear.setVisibility(View.GONE);
                }else{
                    binding.ivClear.setVisibility(View.VISIBLE);
                    for(OfflineInquryModel model : dataList){
                        try {
                            JSONObject obj = new JSONObject(model.getdata());

                            String name = obj.optJSONObject("shipper").optString("firstName") + obj.optJSONObject("shipper").optString("lastName");
                            String accountName = obj.optJSONObject("account").optString("accountName");
                            String inquiryDate = obj.optString("inquiryDate");
                            String goodsType = obj.optString("goodsType");
                            String moving = obj.optJSONObject("originAddress").optJSONObject("city").optString("name") + " to "+obj.optJSONObject("destinationAddress").optJSONObject("city").optString("name");

                            if (name.toLowerCase().startsWith(filterStr) || accountName.toLowerCase().startsWith(filterStr)  || inquiryDate.toLowerCase().startsWith(filterStr) || goodsType.toLowerCase().startsWith(filterStr)
                                    || moving.toLowerCase().startsWith(filterStr)){
                                dataListFilter.add(model);
                            }
                        } catch (JSONException e) {
                           e.printStackTrace();
                        }

                    }
                }
                mAdapter.notifyDataSetChanged();
                Log.e("text changed" , "records found" + dataListFilter.size());
            }
        });

        mAdapter = new OfflineInquiryAdepter(mActivity, dataListFilter, new OfflineInquiryAdepter.ClickAdepterListener() {

            @Override
            public void submitClicked(View v, int position) {
                try {
                    submitRequest(dataListFilter.get(position) , new JSONObject(dataListFilter.get(position).getdata()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        binding.reclycalView.setLayoutManager(mLayoutManager);
        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());
        binding.reclycalView.setAdapter(mAdapter);

    //    getDataAssign();
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void deleteInquiry(OfflineInquryModel model){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> {
            // Find the object by its primary key
            OfflineInquryModel user = r.where(OfflineInquryModel.class).equalTo("id", model.getId()).findFirst();
            if (user != null) {
                // Delete the object
                user.deleteFromRealm();
            } else {
                System.out.println("User with ID " + model.getId() + " not found.");
            }
        });
        realm.close();
        getData();
    }


    void getData(){
        dataListFilter.clear();
        dataList.clear();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<OfflineInquryModel> alldata = realm.where(OfflineInquryModel.class).findAll();
        // Loop through the results
        for (OfflineInquryModel data : alldata) {
                dataList.add(data);
                cmn.printLog(data.getdata());
        }
       // realm.close();
        dataListFilter.addAll(dataList);
        mAdapter.notifyDataSetChanged();
    }

    void submitRequest(OfflineInquryModel model ,JSONObject parameters){
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject obj = new JSONObject(aResponse);
                        cmn.showToast("Enquiry Submitted Successfully !!");
                        deleteInquiry(model);
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
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.postAPI();

    }


//    void getDataAssign(){
//        JSONObject parameters = new JSONObject();
//        Handler handler = new Handler(){
//            public void handleMessage(Message msg) {
//                String aResponse = msg.getData().getString("message");
//                if ((null != aResponse)) {
//                    try {
//                        JSONArray data = new JSONArray(aResponse);
//                        dataListSuggestions.clear();
//                        for (int i = 0; i <data.length() ; i++) {
//                            dataListSuggestions.add(new CommanModel(data.optJSONObject(i).optString("id")
//                                    ,data.optJSONObject(i).optString("fullName")));
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
//        url = "appuser?Find=ByRole&role=ROLE_PRICING_EXECUTIVE";
//        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
//        request.getAPI(false);
//    }
}