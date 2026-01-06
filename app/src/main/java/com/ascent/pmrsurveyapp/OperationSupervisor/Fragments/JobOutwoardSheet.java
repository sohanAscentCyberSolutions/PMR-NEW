package com.ascent.pmrsurveyapp.OperationSupervisor.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.ascent.pmrsurveyapp.OperationSupervisor.Adepters.JobOutwordSheetAdepter;
import com.ascent.pmrsurveyapp.OperationSupervisor.JobExecutionDetails;
import com.ascent.pmrsurveyapp.OperationSupervisor.JobOutwardSheetDetails;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.OutWordSheetModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.LayJobInwordSheetBinding;
import com.ascent.pmrsurveyapp.databinding.LayJobOutwardSheetBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobOutwoardSheet extends Fragment {

    LayJobOutwardSheetBinding binding;
    
    ArrayList<OutWordSheetModel> datalist = new ArrayList<>();

    JobOutwordSheetAdepter mAdapter;

    Comman cmn;
    Activity mActivity;

    public static JobOutwoardSheet newInstance() {
        return new JobOutwoardSheet();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.lay_job_outward_sheet, container, false);
        mActivity = getActivity();
        cmn = new Comman(mActivity);

        getData();

        mAdapter = new JobOutwordSheetAdepter(mActivity, datalist, new JobOutwordSheetAdepter.ClickAdepterListener() {
            @Override
            public void detailsClicked(View v, int position) {
                startActivity(new Intent(mActivity, JobOutwardSheetDetails.class).putExtra("id" , datalist.get(position).id));
                mActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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


                        JSONArray temp = new JSONArray(aResponse);

                        for (int i = 0; i < temp.length() ; i++) {
                            JSONObject obj = temp.optJSONObject(i);

                            String outwardDate = "";
                            String createdBy = obj.optJSONObject("createdBy").optString("fullName");
                            try {
                                outwardDate = cmn.getDate(obj.optLong("outwardDate"));
                            }catch (Exception e){

                            }

                            datalist.add(new OutWordSheetModel(obj.optString("id") , outwardDate , createdBy , obj.optString("noOfPackages")));

                        }

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
        url = "outwardsheet?Find=ByJobExecution&jobExecutionId="+ JobExecutionDetails.data.id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }

}