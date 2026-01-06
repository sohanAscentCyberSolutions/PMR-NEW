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

import com.ascent.pmrsurveyapp.OperationSupervisor.Adepters.JobInwordSheetAdepter;
import com.ascent.pmrsurveyapp.OperationSupervisor.JobExecutionDetails;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobModel;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.PackingItemModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.LayJobInwordSheetBinding;
import com.ascent.pmrsurveyapp.databinding.LayJobLoadingSheetBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobInwordSheet extends Fragment {

    LayJobInwordSheetBinding binding;
    
    ArrayList<PackingItemModel> datalist = new ArrayList<>();

    JobInwordSheetAdepter mAdapter;
    JSONObject object = null;

    Comman cmn;
    Activity mActivity;

    public static JobInwordSheet newInstance() {
        return new JobInwordSheet();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.lay_job_inword_sheet, container, false);
        mActivity = getActivity();
        cmn = new Comman(mActivity);

        getData();

        mAdapter = new JobInwordSheetAdepter(mActivity, datalist, new JobInwordSheetAdepter.ClickAdepterListener() {
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

                        String inwardDate = "";
                        String createdBy = object.optJSONObject("createdBy").optString("fullName");
                        try {
                            inwardDate = cmn.getDate(object.optLong("inwardDate"));
                        }catch (Exception e){

                        }
                        binding.tvInwordDate.setText(""+inwardDate);
                        binding.tvPreparedBy.setText(""+createdBy);

                        JSONArray packedItems = object.optJSONObject("packingList").optJSONArray("packedItems");

                        for (int i = 0; i < packedItems.length(); i++) {
                            datalist.add(new Parser(mActivity).parsePackingItem(packedItems.getJSONObject(i)));
                        }

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
                        binding.reclycalView.setLayoutManager(mLayoutManager);
                        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());
                        binding.reclycalView.setAdapter(mAdapter);

                        binding.ivWareHouseManagerSign.setImageBitmap(cmn.getDecodedImage(object.optString("wareHouseManagerSignature")));
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
        url = "inwardsheet?Find=ByJobExecution&jobExecutionId="+ JobExecutionDetails.data.id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }


    void showDetailDialog(){
        if (object != null){
            final Dialog d = new Dialog(mActivity);
            d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFade;
            d.setContentView(R.layout.lay_inword_sheet_details);
            TextView jobNumber = d.findViewById(R.id.jobNumber);
            TextView inwordDate = d.findViewById(R.id.inwordDate);
            TextView preparedBy = d.findViewById(R.id.preparedBy);
            TextView transportShippingCo = d.findViewById(R.id.transportShippingCo);
            TextView ShippingCustomTruckSealNo = d.findViewById(R.id.ShippingCustomTruckSealNo);
            TextView truckContainerNo = d.findViewById(R.id.truckContainerNo);
            TextView liftVanNos = d.findViewById(R.id.liftVanNos);
            TextView crateNos = d.findViewById(R.id.crateNos);
            TextView totalPackets = d.findViewById(R.id.totalPackets);
            TextView account = d.findViewById(R.id.account);
            TextView shipperName = d.findViewById(R.id.shipperName);
            TextView mobileNumber = d.findViewById(R.id.mobileNumber);
            TextView originAddress = d.findViewById(R.id.originAddress);
            TextView destinationAddress = d.findViewById(R.id.destinationAddress);
            TextView wareHouse = d.findViewById(R.id.wareHouse);
            TextView section = d.findViewById(R.id.section);


            String inwardDateStr = "";
            String createdBy = object.optJSONObject("createdBy").optString("fullName");
            try {
                inwardDateStr = cmn.getDate(object.optLong("loadingDate"));
            }catch (Exception e){

            }
            inwordDate.setText(""+inwardDateStr);
            preparedBy.setText(""+createdBy);

            String wareHouseStr = object.optJSONObject("wareHouse").optString("name");
            String sectionStr = object.optJSONObject("createdBy").optString("section");

            wareHouse.setText(""+wareHouseStr);
            section.setText(""+sectionStr);

            transportShippingCo.setText(""+object.optString("transportShippingCo"));
            liftVanNos.setText(""+object.optString("liftVanNos"));
            truckContainerNo.setText(""+object.optString("truckContainerNo"));
            ShippingCustomTruckSealNo.setText(""+object.optString("truckSealNo"));
            crateNos.setText(""+object.optString("createNos"));
            totalPackets.setText(""+datalist.size());



            JobModel jobModel = new Parser(mActivity).parseJob(object.optJSONObject("jobExecution").optJSONObject("job"));

            jobNumber.setText(""+jobModel.jobNumber);
            account.setText(""+jobModel.account.companyName);
            shipperName.setText(""+jobModel.shipper.fullName);
            mobileNumber.setText(""+jobModel.shipper.contactNumber);
            originAddress.setText(""+jobModel.originAddress.addressLine1+" "+jobModel.originAddress.addressLine2
                    +" "+jobModel.originAddress.area
                    +"\n"+jobModel.originAddress.city.name+","+jobModel.originAddress.state.name+","+jobModel.originAddress.pinCode);
            destinationAddress.setText(""+jobModel.destinationAddress.addressLine1+" "+jobModel.destinationAddress.addressLine2
                    +" "+jobModel.destinationAddress.area
                    +"\n"+jobModel.destinationAddress.city.name+","+jobModel.destinationAddress.state.name+","+jobModel.destinationAddress.pinCode);



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
       binding.tvOriginAddress.setText(""+data.originAddress.addressLine1+" "+data.originAddress.addressLine2
               +" "+data.originAddress.area
               +"\n"+data.originAddress.city.name+","+data.originAddress.state.name+","+data.originAddress.pinCode);
       binding.tvDestiAddress.setText(""+data.destinationAddress.addressLine1+" "+data.destinationAddress.addressLine2
               +" "+data.destinationAddress.area
               +"\n"+data.destinationAddress.city.name+","+data.destinationAddress.state.name+","+data.destinationAddress.pinCode);

    }

}