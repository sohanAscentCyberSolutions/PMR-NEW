package com.ascent.pmrsurveyapp.OperationSupervisor.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.OperationSupervisor.Adepters.JobLoadingSheetAdepter;
import com.ascent.pmrsurveyapp.OperationSupervisor.JobExecutionDetails;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobModel;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.PackingItemModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.LayJobLoadingSheetBinding;
import com.ascent.pmrsurveyapp.databinding.LayJobPackingListBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobLoadingSheet extends Fragment {

    LayJobLoadingSheetBinding binding;
    
    ArrayList<PackingItemModel> datalist = new ArrayList<>();

    JobLoadingSheetAdepter mAdapter;
    JSONObject object = null;

    Comman cmn;
    Activity mActivity;

    public static JobLoadingSheet newInstance() {
        return new JobLoadingSheet();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.lay_job_loading_sheet, container, false);
        mActivity = getActivity();
        cmn = new Comman(mActivity);

        getData();

        mAdapter = new JobLoadingSheetAdepter(mActivity, datalist, new JobLoadingSheetAdepter.ClickAdepterListener() {
            @Override
            public void detailsClicked(View v, int position) {
                
            }
        });

        binding.btViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailDialog();
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
                        object = new JSONObject(aResponse);

                        String loadingDate = "";
                        String createdBy = object.optJSONObject("createdBy").optString("fullName");
                        try {
                            loadingDate = cmn.getDate(object.optLong("loadingDate"));
                        }catch (Exception e){

                        }
                        binding.tvLoadingDate.setText(""+loadingDate);
                        binding.tvPreparedBy.setText(""+createdBy);

                        JSONArray packedItems = object.optJSONObject("packingList").optJSONArray("packedItems");

                        for (int i = 0; i < packedItems.length(); i++) {
                            datalist.add(new Parser(mActivity).parsePackingItem(packedItems.getJSONObject(i)));
                        }

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
                        binding.reclycalView.setLayoutManager(mLayoutManager);
                        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());
                        binding.reclycalView.setAdapter(mAdapter);

                        binding.ivShipperSign.setImageBitmap(cmn.getDecodedImage(object.optString("shipperSignature")));
                        binding.ivSupervisorSignature.setImageBitmap(cmn.getDecodedImage(object.optString("supervisorSignature")));

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
        url = "loadingsheet?Find=ByJobExecution&jobExecutionId="+ JobExecutionDetails.data.id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }


    void showDetailDialog(){
        if (object != null){
            final Dialog d = new Dialog(mActivity);
            d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFade;
            d.setContentView(R.layout.lay_loading_sheet_details);
            TextView jobNumber = d.findViewById(R.id.jobNumber);
            TextView tvloadingDate = d.findViewById(R.id.loadingDate);
            TextView preparedBy = d.findViewById(R.id.preparedBy);
            TextView transportShippingCo = d.findViewById(R.id.transportShippingCo);
            TextView ShippingCustomTruckSealNo = d.findViewById(R.id.ShippingCustomTruckSealNo);
            TextView truckContainerNo = d.findViewById(R.id.truckContainerNo);
            TextView liftVanNos = d.findViewById(R.id.liftVanNos);
            TextView crateNos = d.findViewById(R.id.crateNos);
            TextView Liftvansizes = d.findViewById(R.id.Liftvansizes);
            TextView totalPackets = d.findViewById(R.id.totalPackets);
            TextView account = d.findViewById(R.id.account);
            TextView shipperName = d.findViewById(R.id.shipperName);
            TextView mobileNumber = d.findViewById(R.id.mobileNumber);
            TextView originAddress = d.findViewById(R.id.originAddress);
            TextView destinationAddress = d.findViewById(R.id.destinationAddress);


            tvloadingDate.setText("" + cmn.getDate(object.optLong("loadingDate")));
            String loadingDate = "";
            String createdBy = object.optJSONObject("createdBy").optString("fullName");
            try {
                loadingDate = cmn.getDate(object.optLong("loadingDate"));
            }catch (Exception e){

            }
            tvloadingDate.setText(""+loadingDate);
            preparedBy.setText(""+createdBy);
            transportShippingCo.setText(""+object.optString("transportShippingCo"));
            liftVanNos.setText(""+object.optString("liftVanNos"));
            Liftvansizes.setText(""+cmn.replaceNull(object.optString("liftVanSize")));
            truckContainerNo.setText(""+object.optString("truckContainerNo"));
            ShippingCustomTruckSealNo.setText(""+object.optString("truckSealNo"));
            crateNos.setText(""+object.optString("createNos"));
            totalPackets.setText(""+datalist.size());


            JobModel jobModel = new Parser(mActivity).parseJob(object.optJSONObject("jobExecution").optJSONObject("job"));

            jobNumber.setText(""+jobModel.jobNumber);
            account.setText(""+jobModel.account.companyName);
            shipperName.setText(""+jobModel.shipper.fullName);
            mobileNumber.setText(""+jobModel.shipper.contactNumber);
            originAddress.setText(""+cmn.replaceNull(jobModel.originAddress.addressLine1)+" "+cmn.replaceNull(jobModel.originAddress.addressLine2)
                    +" "+cmn.replaceNull(jobModel.originAddress.area)
                    +"\n"+jobModel.originAddress.city.name+","+jobModel.originAddress.state.name+","+cmn.replaceNull(jobModel.originAddress.pinCode));
            destinationAddress.setText(""+cmn.replaceNull(jobModel.destinationAddress.addressLine1)+" "+cmn.replaceNull(jobModel.destinationAddress.addressLine2)
                    +" "+cmn.replaceNull(jobModel.destinationAddress.area)
                    +"\n"+jobModel.destinationAddress.city.name+","+jobModel.destinationAddress.state.name+","+cmn.replaceNull(jobModel.destinationAddress.pinCode));



            Button btClose =  d.findViewById(R.id.btClose);
            btClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            d.show();
            d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }else{
            cmn.showToast("Unable to fetch details..");
        }

    }


   void setJobDetails(JobModel data){
        binding.tvJobNumber.setText(""+data.jobNumber);
       binding.tvAccount.setText(""+data.account.companyName);
       binding.tvShipperName.setText(""+data.shipper.fullName);
       binding.tvMobileNumber.setText(""+data.shipper.contactNumber);
       binding.tvOriginAddress.setText(""+cmn.replaceNull(data.originAddress.addressLine1)+" "+cmn.replaceNull(data.originAddress.addressLine2)
               +" "+cmn.replaceNull(data.originAddress.area)
               +"\n"+data.originAddress.city.name+","+data.originAddress.state.name+","+cmn.replaceNull(data.originAddress.pinCode));
       binding.tvDestiAddress.setText(""+cmn.replaceNull(data.destinationAddress.addressLine1)+" "+cmn.replaceNull(data.destinationAddress.addressLine2)
               +" "+cmn.replaceNull(data.destinationAddress.area)
               +"\n"+cmn.replaceNull(data.destinationAddress.city.name)+","+data.destinationAddress.state.name+","+cmn.replaceNull(data.destinationAddress.pinCode));

    }

}