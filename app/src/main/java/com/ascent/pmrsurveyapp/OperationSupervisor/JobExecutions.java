package com.ascent.pmrsurveyapp.OperationSupervisor;

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

import com.ascent.pmrsurveyapp.OperationSupervisor.Adepters.JobExecutionsAdepter;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobExecutionModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.UpdateData;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.ActivityInquiriesBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityJobExecutionsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobExecutions extends AppCompatActivity implements UpdateData {

    Comman cmn;
    Activity mActivity;
    ActivityJobExecutionsBinding binding;
    ArrayList<JobExecutionModel> dataList = new ArrayList<>();
    ArrayList<JobExecutionModel> dataListFilter = new ArrayList<>();
    private JobExecutionsAdepter mAdapter;

    public static UpdateData updateData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_executions);

        mActivity = this;
        cmn = new Comman(mActivity);
        updateData = this::reloadTheData;

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
                    for(JobExecutionModel model : dataList){
                        if (cmn.replaceNull(model.job.jobNumber).toLowerCase().startsWith(filterStr) || cmn.replaceNull(model.job.createdOn).toLowerCase().startsWith(filterStr)
                                || cmn.replaceNull(model.status).toLowerCase().startsWith(filterStr) || cmn.replaceNull(model.job.moveType).toLowerCase().startsWith(filterStr)
                                || cmn.replaceNull(model.job.goodsType).toLowerCase().startsWith(filterStr) || cmn.replaceNull(model.job.shipper.fullName).toLowerCase().startsWith(filterStr)){
                            dataListFilter.add(model);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                Log.e("text changed" , "records found" + dataListFilter.size());
            }
        });
        getData();
        mAdapter = new JobExecutionsAdepter(mActivity, dataListFilter, new JobExecutionsAdepter.ClickAdepterListener() {
            @Override
            public void detailsClicked(View v, int position) {
                JobExecutionDetails.data = dataListFilter.get(position);
                Intent mainIntent = new Intent(mActivity, JobExecutionDetails.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void uploadDocClicked(View v, int position) {
                Intent mainIntent = new Intent(mActivity, UploadDocumentsJob.class);
                mainIntent.putExtra("id" , dataListFilter.get(position).id);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void newPackingListClicked(View v, int position) {
               Intent mainIntent = new Intent(mActivity, NewPackingSheet.class);
                mainIntent.putExtra("id" , dataListFilter.get(position).id);
                mainIntent.putExtra("jobNumber" , dataListFilter.get(position).job.jobNumber);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void newLoadingSheetClicked(View v, int position) {
                Intent mainIntent = new Intent(mActivity, NewLoadingSheet.class);
                mainIntent.putExtra("id" , dataListFilter.get(position).id);
                mainIntent.putExtra("jobNumber" , dataListFilter.get(position).job.jobNumber);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        binding.reclycalView.setLayoutManager(mLayoutManager);
        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());
        binding.reclycalView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                            dataList.add(new Parser(mActivity).parseJobExecutions(array.optJSONObject(index)));
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
        String url = "jobexecution?page=0&size=500&searchFields=job&searchText=&sortField=undefined&sortOrder=asc";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(true);
    }

    @Override
    public void reloadTheData() {
        getData();
    }
}