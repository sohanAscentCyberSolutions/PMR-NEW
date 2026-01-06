package com.ascent.pmrsurveyapp.OperationSupervisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.ascent.pmrsurveyapp.OperationSupervisor.Adepters.JobOutwordSheetDetailsAdepter;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobModel;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.OutWordSheetDetailsModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.ActivityInquiryDetailsBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityOutwordSheetDetailsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobOutwardSheetDetails extends AppCompatActivity {

    String id = "";
    Comman cmn;
    Activity mActivity;
    ActivityOutwordSheetDetailsBinding binding;

    JobOutwordSheetDetailsAdepter mAdapter;
    ArrayList<OutWordSheetDetailsModel> datalist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_outword_sheet_details);

        mActivity = this;
        cmn = new Comman(mActivity);
        id = getIntent().getStringExtra("id");

        getData();

    }

    void getData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {


                        JSONObject object = new JSONObject(aResponse);

                        JSONArray outwardItems = object.optJSONArray("outwardItems");

                        for (int i = 0; i < outwardItems.length() ; i++) {
                            JSONObject obj = outwardItems.optJSONObject(i);

                            String crRef = obj.optJSONObject("packedItem").optString("crRef");
                            String article = obj.optJSONObject("packedItem").optString("article");

                            datalist.add(new OutWordSheetDetailsModel(obj.optString("id") , obj.optString("itemNo"), obj.optString("outwardSequence"),crRef,article));

                        }

                        String outwardDateStr = "";
                        String createdBy = object.optJSONObject("createdBy").optString("fullName");
                        try {
                            outwardDateStr = cmn.getDate(object.optLong("outwardDate"));
                        }catch (Exception e){

                        }
                        binding.outwardDate.setText(""+outwardDateStr);
                        binding.preparedBy.setText(""+createdBy);


                        binding.transportShippingCo.setText(""+object.optString("transportShippingCo"));
                        binding.liftVanNos.setText(""+object.optString("liftVanNos"));
                        binding.truckContainerNo.setText(""+object.optString("truckContainerNo"));
                        binding.ShippingCustomTruckSealNo.setText(""+object.optString("truckSealNo"));
                        binding.crateNos.setText(""+object.optString("createNos"));
                        binding.totalPackets.setText(""+datalist.size());



                        JobModel jobModel = new Parser(mActivity).parseJob(object.optJSONObject("jobExecution").optJSONObject("job"));

                        binding.jobNumber.setText(""+jobModel.jobNumber);
                        binding.account.setText(""+jobModel.account.companyName);
                        binding.shipperName.setText(""+jobModel.shipper.fullName);
                        binding.mobileNumber.setText(""+jobModel.shipper.contactNumber);
                        binding.originAddress.setText(""+jobModel.originAddress.addressLine1+" "+jobModel.originAddress.addressLine2
                                +" "+jobModel.originAddress.area
                                +"\n"+jobModel.originAddress.city.name+","+jobModel.originAddress.state.name+","+jobModel.originAddress.pinCode);
                        binding.destinationAddress.setText(""+jobModel.destinationAddress.addressLine1+" "+jobModel.destinationAddress.addressLine2
                                +" "+jobModel.destinationAddress.area
                                +"\n"+jobModel.destinationAddress.city.name+","+jobModel.destinationAddress.state.name+","+jobModel.destinationAddress.pinCode);


                        mAdapter = new JobOutwordSheetDetailsAdepter(mActivity, datalist, new JobOutwordSheetDetailsAdepter.ClickAdepterListener() {
                            @Override
                            public void detailsClicked(View v, int position) {

                            }
                        });

                        binding.ivWareHouseManagerSign.setImageBitmap(cmn.getDecodedImage(object.optString("wareHouseManagerSignature")));
                        binding.ivSupervisorSignature.setImageBitmap(cmn.getDecodedImage(object.optString("supervisorSignature")));


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
        url = "outwardsheet/"+ id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }

}