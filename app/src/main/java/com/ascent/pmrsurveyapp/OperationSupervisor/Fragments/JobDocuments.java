package com.ascent.pmrsurveyapp.OperationSupervisor.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.OperationSupervisor.Adepters.JobDocumentsAdepter;
import com.ascent.pmrsurveyapp.OperationSupervisor.JobExecutionDetails;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobDocumentModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.LayJobDocumentsBinding;
import com.ascent.pmrsurveyapp.databinding.LayJobInwordSheetBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobDocuments extends Fragment {

    LayJobDocumentsBinding binding;
    
    ArrayList<JobDocumentModel> datalist = new ArrayList<>();

    JobDocumentsAdepter mAdapter;

    Comman cmn;
    Activity mActivity;

    public static JobDocuments newInstance() {
        return new JobDocuments();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.lay_job_documents, container, false);
        mActivity = getActivity();
        cmn = new Comman(mActivity);

        getData();

        mAdapter = new JobDocumentsAdepter(mActivity, datalist, new JobDocumentsAdepter.ClickAdepterListener() {
            @Override
            public void detailsClicked(View v, int position) {
                
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


    void getData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                       JSONObject object = new JSONObject(aResponse);
                        JSONArray documents = object.optJSONArray("documents");

                        for (int i = 0; i < documents.length(); i++) {
                            datalist.add(new JobDocumentModel(documents.getJSONObject(i).optString("id"),documents.getJSONObject(i).optString("filePath"),documents.getJSONObject(i).optString("fileName"),documents.getJSONObject(i).optString("description")));
                        }

                        binding.uploadedBy.setText(""+object.optJSONObject("uploadedBy").optString("fullName"));
                        try {
                            binding.uploadedOn.setText("" + cmn.getDate(object.optLong("uploadedOn")));
                        }catch (Exception e){}

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
                        binding.reclycalView.setLayoutManager(mLayoutManager);
                        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());
                        binding.reclycalView.setAdapter(mAdapter);

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
        url = "jobexecution?Get=DocumentByJobExecution&jobExecutionId="+ JobExecutionDetails.data.id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }

}