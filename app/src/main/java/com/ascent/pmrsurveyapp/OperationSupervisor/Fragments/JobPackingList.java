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

import com.ascent.pmrsurveyapp.OperationSupervisor.Adepters.JobPackingListAdepter;
import com.ascent.pmrsurveyapp.OperationSupervisor.JobExecutionDetails;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobModel;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.PackingItemModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.LayJobActivityHistoryBinding;
import com.ascent.pmrsurveyapp.databinding.LayJobPackingListBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobPackingList extends Fragment {

    LayJobPackingListBinding binding;
    
    ArrayList<PackingItemModel> datalist = new ArrayList<>();
    
    JobPackingListAdepter mAdapter;

    Comman cmn;
    Activity mActivity;

    public static JobPackingList newInstance() {
        return new JobPackingList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.lay_job_packing_list, container, false);
        mActivity = getActivity();
        cmn = new Comman(mActivity);

        getData();

        mAdapter = new JobPackingListAdepter(mActivity, datalist, new JobPackingListAdepter.ClickAdepterListener() {
            @Override
            public void detailsClicked(View v, int position) {
                
            }

            @Override
            public void uploadDocClicked(View v, int position) {

            }

            @Override
            public void newPackingListClicked(View v, int position) {

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

                        String packingDate = "";
                        String createdBy = object.optJSONObject("createdBy").optString("fullName");
                        try {
                            packingDate = cmn.getDate(object.optLong("packingDate"));
                        }catch (Exception e){

                        }
                        binding.tvPackingDate.setText(""+packingDate);
                        binding.tvPreparedBy.setText(""+createdBy);

                        JSONArray packedItems = object.optJSONArray("packedItems");

                        for (int i = 0; i < packedItems.length(); i++) {
                            datalist.add(new Parser(mActivity).parsePackingItem(packedItems.getJSONObject(i)));
                        }
                        

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
                        binding.reclycalView.setLayoutManager(mLayoutManager);
                        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());
                        binding.reclycalView.setAdapter(mAdapter);

                        binding.tvRemarks.setText(""+object.optString("remarks"));
                        binding.ivContractorAgentSign.setImageBitmap(cmn.getDecodedImage(object.optString("contractorSignature")));
                        binding.ivOwnerAgentSign.setImageBitmap(cmn.getDecodedImage(object.optString("shipperSignature")));

                        setJobDetails(new Parser(mActivity).parseJob(object.optJSONObject("jobExecution").optJSONObject("job")));
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
        url = "packinglist?Find=ByJobExecution&jobExecutionId="+ JobExecutionDetails.data.id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }


   void setJobDetails(JobModel data){
        binding.tvJobNumber.setText(""+data.jobNumber);
       binding.tvAccount.setText(""+data.account.companyName);
       binding.tvShipperName.setText(""+data.shipper.fullName);
       binding.tvMobileNumber.setText(""+data.shipper.contactNumber);
       binding.tvOriginAddress.setText(""+cmn.replaceNull(data.originAddress.addressLine1)+" "+cmn.replaceNull(data.originAddress.addressLine2)
               +" "+cmn.replaceNull(data.originAddress.area)
               +"\n"+cmn.replaceNull(data.originAddress.city.name)+","+cmn.replaceNull(data.originAddress.state.name)+","+cmn.replaceNull(data.originAddress.pinCode));
       binding.tvDestiAddress.setText(""+cmn.replaceNull(data.destinationAddress.addressLine1)+" "+cmn.replaceNull(data.destinationAddress.addressLine2)
               +" "+cmn.replaceNull(data.destinationAddress.area)
               +"\n"+cmn.replaceNull(data.destinationAddress.city.name)+","+cmn.replaceNull(data.destinationAddress.state.name)+","+cmn.replaceNull(data.destinationAddress.pinCode));

    }

}