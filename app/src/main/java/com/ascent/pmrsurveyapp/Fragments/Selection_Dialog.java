package com.ascent.pmrsurveyapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Adepters.SearchAdepter;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.databinding.LaySelectionDialogBinding;

import java.util.ArrayList;

public class Selection_Dialog extends DialogFragment {

    public static final String TAG = "ActionBottomDialog";

    public ArrayList<CommanModel> dataList = new ArrayList<>();
    public ArrayList<CommanModel> dataListFilter = new ArrayList<>();
    Comman cmn;
    LaySelectionDialogBinding binding;
    SearchAdepter adapterSuggestions;
    public ItemSelecter listiner;
    public int requestCode;
    public String titleStr;
    Activity mActivity;
    CommanModel selectedModel;

    public static Selection_Dialog newInstance() {
        return new Selection_Dialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.lay_selection_dialog, container, false);
        mActivity = getActivity();
        cmn = new Comman(getActivity());
        binding.titleLabel.setText(""+titleStr);
        dataListFilter.addAll(dataList);
        adapterSuggestions = new SearchAdepter(getActivity(), dataListFilter, new SearchAdepter.ClickAdepterListener() {
            @Override
            public void itemSelected(View v, int position) {
                    listiner.itemSelected(dataListFilter.get(position) , requestCode);
                    dismiss();
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
                String filterStr =  binding.etFilter.getText().toString().toLowerCase();
                if (filterStr.isEmpty()){
                    dataListFilter.addAll(dataList);
                }else{
                    int index = 0;
                    for(CommanModel model : dataList){
                        if (model.name.toLowerCase().startsWith(filterStr)){
                            dataListFilter.add(model);
                        }
                        index ++;
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

}