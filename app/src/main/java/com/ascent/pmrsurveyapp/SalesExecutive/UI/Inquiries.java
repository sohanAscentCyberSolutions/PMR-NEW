package com.ascent.pmrsurveyapp.SalesExecutive.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.ascent.pmrsurveyapp.Fragments.Request_Details;
import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.OperationSupervisor.UploadDocumentsJob;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Adepters.InquiryAdepter;
import com.ascent.pmrsurveyapp.SalesExecutive.Fragmants.Assign_Inquiry_Dialog;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel;
import com.ascent.pmrsurveyapp.SalesExecutive.UpdateData;
import com.ascent.pmrsurveyapp.UI.NewSurveyRequest;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.ActivityAccountsBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityInquiriesBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Inquiries extends AppCompatActivity implements UpdateData {

    Comman cmn;
    Activity mActivity;
    ActivityInquiriesBinding binding;
    ArrayList<InquiryModel> dataList = new ArrayList<>();
    ArrayList<InquiryModel> dataListFilter = new ArrayList<>();
    private InquiryAdepter mAdapter;
    String moveType = "HOUSE_HOLD_GOODS";
    String areaType =  "DOMESTIC";
    String transactionType = "Import";
    ArrayList<CommanModel> dataListSuggestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inquiries);

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
                    for(InquiryModel model : dataList){
                        if (cmn.replaceNull(model.client.name).toLowerCase().startsWith(filterStr) || cmn.replaceNull(model.account.name).toLowerCase().startsWith(filterStr)  || cmn.replaceNull(model.status).toLowerCase().startsWith(filterStr) || cmn.replaceNull(model.date).toLowerCase().startsWith(filterStr)
                                || cmn.replaceNull(model.goodsType).toLowerCase().startsWith(filterStr) || cmn.replaceNull(model.shipper.name).toLowerCase().startsWith(filterStr)){
                            dataListFilter.add(model);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                Log.e("text changed" , "records found" + dataListFilter.size());
            }
        });

        mAdapter = new InquiryAdepter(mActivity, dataListFilter, new InquiryAdepter.ClickAdepterListener() {
            @Override
            public void detailsClicked(View v, int position) {
                Intent mainIntent = new Intent(mActivity, InquiryDetails.class);
                mainIntent.putExtra("id" , dataListFilter.get(position).id);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void ediClicked(View v, int position) {
                startActivity(new Intent(mActivity, AddInquiry.class).putExtra("isEdit" , true).putExtra("id" , dataListFilter.get(position).id));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void uploadDocumentClicked(View v, int position) {
                Intent mainIntent = new Intent(mActivity, UploadDocumentInquiries.class);
                UploadDocumentInquiries.listioner = Inquiries.this::reloadTheData;
                mainIntent.putExtra("id" , dataListFilter.get(position).id);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void assignClickedClicked(View v, int position) {
                Assign_Inquiry_Dialog dialog =
                        Assign_Inquiry_Dialog.newInstance();
                dialog.model = dataListFilter.get(position);
                Assign_Inquiry_Dialog.dataListSuggestions = dataListSuggestions;
                dialog.listiner = Inquiries.this::reloadTheData;
                dialog.show(getSupportFragmentManager() , Request_Details.TAG);
            }

            @Override
            public void telephonicSurveyClicked(View v, int position) {
                Intent mainIntent = new Intent(mActivity, TelephonicSurvey.class);
                mainIntent.putExtra("id" , dataListFilter.get(position).id);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void requestInfoClicked(View v, int position) {

            }
            @Override
            public void newSurveyRequestClicked(View v, int position) {
                Intent mainIntent = new Intent(mActivity, NewSurveyRequest.class);
                NewSurveyRequest.detail = dataListFilter.get(position);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        },true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        binding.reclycalView.setLayoutManager(mLayoutManager);
        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());
        binding.reclycalView.setAdapter(mAdapter);
        binding.sagmentedGroupMoveType.setVisibility(View.GONE);

        binding.sagmentedGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(final int position) {
                // Handle stuff here
                Log.e("position" , ""+ position);
                if (position== 0){
                    areaType = "DOMESTIC";
                    moveType = "HOUSE_HOLD_GOODS";
                    binding.sagmentedGroupMoveType.setVisibility(View.GONE);
                   // binding.sagmentedGroupMoveType.getButton(0).setText("House Hold Goods");
                   // binding.sagmentedGroupMoveType.getButton(1).setText("Office Goods");
                    getData();
                }else {
                    binding.sagmentedGroupMoveType.setVisibility(View.VISIBLE);
                    areaType = "INTERNATIONAL";
                    transactionType = "Import";
                    binding.sagmentedGroupMoveType.getButton(0).setText("Import");
                    binding.sagmentedGroupMoveType.getButton(1).setText("Export");
                    getData();
                }
                binding.sagmentedGroupMoveType.setPosition(0,true);
            }
        });
        binding.sagmentedGroupMoveType.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(final int position) {
                // Handle stuff here
                Log.e("position" , ""+ position);
                if (position== 0){
                    if (areaType.equalsIgnoreCase("DOMESTIC")){
                        moveType = "HOUSE_HOLD_GOODS";
                        getData();
                    }else{
                        transactionType = "Import";
                        getData();
                    }
                }else {
                    if (areaType.equalsIgnoreCase("DOMESTIC")){
                        moveType = "OFFICE_GOODS";
                        getData();
                    }else{
                        transactionType = "Export";
                        getData();
                    }
                }
            }
        });

        getDataAssign();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    void getData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        dataList.clear();
                        dataListFilter.clear();
                        JSONObject obj = new JSONObject(aResponse);
                        JSONArray array = obj.optJSONArray("contant");
                        for (int index = 0;index<array.length();index++){
                            dataList.add(new Parser(mActivity).parseInquirySales(array.optJSONObject(index)));
                        }
                        dataListFilter.addAll(dataList);
                        mAdapter.notifyDataSetChanged();
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
        String url = "inquiry/v1?page=0&size=15&searchFields=account&areaType=DOMESTIC&searchText=&sortField=undefined&sortOrder=asc";

        if (areaType.equalsIgnoreCase("DOMESTIC")){
            url = "inquiry?page=0&size=1000&searchFields=account&areaType="+areaType+"&searchText=&sortField=undefined&sortOrder=asc";
        }else{
            url = "inquiry?page=0&size=1000&searchFields=account&areaType="+areaType+"&moveType=&searchText=&sortField=undefined&sortOrder=asc&transactionType="+transactionType;
        }
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(true);
    }

    void getDataAssign(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONArray data = new JSONArray(aResponse);
                        dataListSuggestions.clear();
                        for (int i = 0; i <data.length() ; i++) {
                            dataListSuggestions.add(new CommanModel(data.optJSONObject(i).optString("id")
                                    ,data.optJSONObject(i).optString("fullName")));
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
        url = "appuser?Find=ByRole&role=ROLE_PRICING_EXECUTIVE";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }

    @Override
    public void reloadTheData() {
        getData();
    }
}