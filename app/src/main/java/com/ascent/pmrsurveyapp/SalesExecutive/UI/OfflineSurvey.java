package com.ascent.pmrsurveyapp.SalesExecutive.UI;

import static com.ascent.pmrsurveyapp.UI.Dashboard.dataUpdator;

import android.app.Activity;
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

import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Adepters.OfflineInquiryAdepter;
import com.ascent.pmrsurveyapp.SalesExecutive.Adepters.OfflineSurveyAdepter;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.OfflineInquryModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.OfflineSurveyModel;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.ActivityOfflineInquiriesBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityOfflineSurveyBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class OfflineSurvey extends AppCompatActivity {

    Comman cmn;
    Activity mActivity;
    ActivityOfflineSurveyBinding binding;
    ArrayList<OfflineSurveyModel> dataList = new ArrayList<>();
    ArrayList<OfflineSurveyModel> dataListFilter = new ArrayList<>();
    private OfflineSurveyAdepter mAdapter;
    String moveType = "HOUSE_HOLD_GOODS";
    String areaType =  "DOMESTIC";
    String transactionType = "Import";
    ArrayList<CommanModel> dataListSuggestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_offline_survey);

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
                    for(OfflineSurveyModel model : dataList){
                        try {
                            JSONObject obj = new JSONObject(model.getdata());
                            String packingDate = obj.optString("packingDate") + obj.optString("packingTime");
                            String movingDate = obj.optString("movingDate");
                            String mode = obj.optString("mode");
                            String insuranceBy = obj.optString("insuranceBy");
                            if (packingDate.toLowerCase().startsWith(filterStr) || movingDate.toLowerCase().startsWith(filterStr)  || mode.toLowerCase().startsWith(filterStr) || insuranceBy.toLowerCase().startsWith(filterStr)
                                  ){
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

        mAdapter = new OfflineSurveyAdepter(mActivity, dataListFilter, new OfflineSurveyAdepter.ClickAdepterListener() {

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

    void deleteInquiry(OfflineSurveyModel model){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> {
            // Find the object by its primary key
            OfflineSurveyModel user = r.where(OfflineSurveyModel.class).equalTo("id", model.getId()).findFirst();
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
        RealmResults<OfflineSurveyModel> alldata = realm.where(OfflineSurveyModel.class).findAll();
        // Loop through the results
        for (OfflineSurveyModel data : alldata) {
                dataList.add(data);
                cmn.printLog(data.getdata());
        }
       // realm.close();
        dataListFilter.addAll(dataList);
        mAdapter.notifyDataSetChanged();
    }
    void submitRequest(OfflineSurveyModel surveyModel , JSONObject parameters){
        try {
            parameters.put("saveAsDraft", false);
            parameters.put("id", JSONObject.NULL);
        } catch (JSONException e) {
           e.printStackTrace();
        }
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject obj = new JSONObject(aResponse);
                        dataUpdator.reloadTheData();
                        deleteInquiry(surveyModel);
                        cmn.showToast("Survey Submitted SuccessFully !!");
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
        url = "surveyreport";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.postAPI();
    }
}