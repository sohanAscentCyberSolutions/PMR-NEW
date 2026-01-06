package com.ascent.pmrsurveyapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ascent.pmrsurveyapp.Models.RequestsModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.LayRequestDetailsBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class Request_Details extends BottomSheetDialogFragment {

    public RequestsModel model;
    public static final String TAG = "ActionBottomDialog";

    LayRequestDetailsBinding binding;


    public static Request_Details newInstance() {
        return new Request_Details();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.lay_request_details, container, false);



        getData();


        binding.btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });


        return binding.getRoot();


    }

    void closeDialog(){
        this.dismiss();
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
                        JSONObject data = new JSONObject(aResponse);
                        model = new Parser(getActivity()).parseRequest(data);
                        binding.accountCompny.setText(""+model.inquiryModel.account);
                        binding.contactPerson.setText(""+model.inquiryModel.contactPerson);
                        binding.clintName.setText(""+model.inquiryModel.shipper.name);
                        binding.clintAddress.setText(""+model.inquiryModel.originAddress.addressLine1 + "\n"
                                +model.inquiryModel.originAddress.addressLine2 + "\n" +model.inquiryModel.originAddress.city.name + " "
                                +model.inquiryModel.originAddress.state.name + "\n"+model.inquiryModel.originAddress.pinCode);
                        binding.clientMobile.setText(""+model.inquiryModel.shipper.contactNumber);
                        binding.clientEmail.setText(""+model.inquiryModel.shipper.email);
                        binding.surveyDateTime.setText(""+model.requestDate);
                        binding.surveyor.setText(""+model.surveyor);
                        binding.origin.setText(""+model.inquiryModel.originAddress.addressLine1 + "\n"
                                +model.inquiryModel.originAddress.addressLine2 + "\n" +model.inquiryModel.originAddress.city.name + " "
                                +model.inquiryModel.originAddress.state.name + "\n"+model.inquiryModel.originAddress.pinCode);
                        binding.destination.setText(""+model.inquiryModel.destinationAddress.addressLine1 + "\n"
                                +model.inquiryModel.destinationAddress.addressLine2 + "\n" +model.inquiryModel.destinationAddress.city.name + " "
                                +model.inquiryModel.destinationAddress.state.name + "\n"+model.inquiryModel.destinationAddress.pinCode);
                        binding.requestDate.setText(""+model.requestDate);
                        binding.surveyStatus.setText(""+model.status);
                        binding.moveType.setText(""+model.inquiryModel.moveType);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                {
                    Toast.makeText(
                            getActivity(),
                            "Not Got Response From Server.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        String url = "";
        url = "surveyrequests/"+model.id;
        HttpRequest request = new HttpRequest(url, parameters, handler, getActivity());
        request.getAPI(true);
    }
}