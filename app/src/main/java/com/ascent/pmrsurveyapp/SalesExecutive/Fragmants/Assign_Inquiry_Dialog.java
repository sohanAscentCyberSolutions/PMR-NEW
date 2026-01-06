package com.ascent.pmrsurveyapp.SalesExecutive.Fragmants;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Adepters.SearchAdepter;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel;
import com.ascent.pmrsurveyapp.SalesExecutive.UpdateData;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.LayAssignInquiryDialogBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Assign_Inquiry_Dialog extends DialogFragment {

    public InquiryModel model;
    public static final String TAG = "ActionBottomDialog";

    LayAssignInquiryDialogBinding binding;
    SearchAdepter adapterSuggestions;
    public UpdateData listiner;

    Comman cmn;

    CommanModel selectedModel;
   public static ArrayList<CommanModel> dataListSuggestions = new ArrayList<>();
    public ArrayList<CommanModel> dataListSuggestionsFilter = new ArrayList<>();

    public static Assign_Inquiry_Dialog newInstance() {
        return new Assign_Inquiry_Dialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.lay_assign_inquiry_dialog, container, false);
        cmn = new Comman(getActivity());
        dataListSuggestionsFilter.addAll(dataListSuggestions);
        adapterSuggestions = new SearchAdepter(getActivity(), dataListSuggestionsFilter, new SearchAdepter.ClickAdepterListener() {
            @Override
            public void itemSelected(View v, int position) {
                selectedModel = dataListSuggestions.get(position);
                binding.etFilter.setText(""+selectedModel.name);
            }
        });

        binding.cbAssignSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedModel = null;
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
                dataListSuggestionsFilter.clear();
                String filterStr = binding.etFilter.getText().toString().toLowerCase();
                if (filterStr.isEmpty()){
                    dataListSuggestionsFilter.addAll(dataListSuggestions);
                    binding.ivClear.setVisibility(View.GONE);
                }else{
                    binding.ivClear.setVisibility(View.VISIBLE);
                    for(CommanModel model : dataListSuggestions){
                        if (model.name.toLowerCase().startsWith(filterStr)){
                            dataListSuggestionsFilter.add(model);
                        }
                    }
                }
                adapterSuggestions.notifyDataSetChanged();
            }
        });

        binding.btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.reclycalView.setLayoutManager(mLayoutManager);
        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());
        binding.reclycalView.setAdapter(adapterSuggestions);

        binding.ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etFilter.setText("");
            }
        });


        binding.btAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.cbAssignSelf.isChecked()){
                    assignInquiry();
                }else{
                    if (selectedModel == null){
                        new Comman(getActivity()).showToast("Please Search or select a pricing executive");
                    }else{
                        assignInquiry();
                    }
                }
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


    void assignInquiry(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    new Comman(getActivity()).showToast("Enquiry Assigned Successfully !!");
                    listiner.reloadTheData();
                    closeDialog();
                } else
                {
                    Toast.makeText(
                            getActivity(),
                            "Not Got Response From Server.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        try {
            parameters.put("id" , model.id);
            JSONObject assignedTo = new JSONObject();
            if (binding.cbAssignSelf.isChecked()){
                assignedTo.put("id" , cmn.getUserId());
                assignedTo.put("fullName" , cmn.getUserName());
            }else{
                assignedTo.put("id" , selectedModel.id);
                assignedTo.put("fullName" , selectedModel.name);
            }
            parameters.put("assignedTo" , assignedTo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";
        url = "inquiry/assign";
        HttpRequest request = new HttpRequest(url, parameters, handler, getActivity());
        request.putAPI();
    }
}