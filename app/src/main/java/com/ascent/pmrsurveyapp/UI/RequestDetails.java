package com.ascent.pmrsurveyapp.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.ascent.pmrsurveyapp.Models.RequestsModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.ActivityRequestDetailsBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestDetails extends AppCompatActivity {

    Comman cmn;
    Activity mActivity;
    ActivityRequestDetailsBinding binding;
    String id = "";
    public RequestsModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_details);

        mActivity = this;
        cmn = new Comman(mActivity);
        id = getIntent().getStringExtra("id");
        getData();


        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void getData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONObject data = new JSONObject(aResponse);
                        model = new Parser(mActivity).parseRequest(data);
                        binding.accountCompny.setText(""+model.inquiryModel.inquiryType);
                        binding.contactPerson.setText(""+cmn.replaceNull(model.inquiryModel.contactPerson));
                        binding.clintName.setText(""+model.inquiryModel.shipper.name);
                        binding.clientMobile.setText(""+model.inquiryModel.shipper.contactNumber);
                        binding.clientEmail.setText(""+model.inquiryModel.shipper.email);
                        binding.surveyDateTime.setText(""+model.startDate);
                        binding.surveyor.setText(""+model.surveyor);
                        binding.origin.setText(""+cmn.replaceNull(model.inquiryModel.originAddress.addressLine1) + "\n" +cmn.replaceNull(model.inquiryModel.originAddress.city.name) + " "
                                +cmn.replaceNull(model.inquiryModel.originAddress.state.name) + "\n"+cmn.replaceNull(model.inquiryModel.originAddress.pinCode));
                        binding.destination.setText(""+cmn.replaceNull(model.inquiryModel.destinationAddress.addressLine1) + "\n" +cmn.replaceNull(model.inquiryModel.destinationAddress.city.name) + " "
                                +cmn.replaceNull(model.inquiryModel.destinationAddress.state.name) + "\n"+cmn.replaceNull(model.inquiryModel.destinationAddress.pinCode));
                        binding.requestDate.setText(""+model.requestDate);
                        binding.surveyStatus.setText(""+model.status);
                        binding.moveType.setText(""+model.inquiryModel.goodsType);
                        binding.assignedBy.setText(""+model.createdBy);
                        binding.salesExecutiveName.setText(""+model.createdBy);
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
        url = "surveyrequests/"+id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(true);
    }

}