package com.ascent.pmrsurveyapp.SalesExecutive.Fragmants;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.ascent.pmrsurveyapp.SalesExecutive.Adepters.ArticalSearchAdepter;
import com.ascent.pmrsurveyapp.SalesExecutive.ArticalSelector;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.StandardItemsModel;
import com.ascent.pmrsurveyapp.databinding.LayAssignInquiryDialogBinding;
import com.ascent.pmrsurveyapp.databinding.LaySelectArticalDialogBinding;

import java.util.ArrayList;

public class Artical_Selector_Dialog extends DialogFragment {

    public InquiryModel model;
    public static final String TAG = "ActionBottomDialog";

    LaySelectArticalDialogBinding binding;
    ArticalSearchAdepter adapterSuggestions;
    public ArticalSelector listiner;

    CommanModel selectedModel;
    public static ArrayList<StandardItemsModel> dataListSuggestions = new ArrayList<>();
    public ArrayList<StandardItemsModel> dataListSuggestionsFilter = new ArrayList<>();


    public static Artical_Selector_Dialog newInstance() {
        return new Artical_Selector_Dialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.lay_select_artical_dialog, container, false);
        dataListSuggestionsFilter.addAll(dataListSuggestions);
        Log.e("data list " , ""+dataListSuggestions.size());
        Log.e("filter list " , ""+dataListSuggestionsFilter.size());
        adapterSuggestions = new ArticalSearchAdepter(getActivity() , dataListSuggestionsFilter , new ArticalSearchAdepter.ArticalAdepterListener() {
            @Override
            public void itemSelected(View v, int position) {
                listiner.articalSelected(dataListSuggestionsFilter.get(position));
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
                dataListSuggestionsFilter.clear();
                String filterStr = binding.etFilter.getText().toString().toLowerCase();
                if (filterStr.isEmpty()){
                    dataListSuggestionsFilter.addAll(dataListSuggestions);
                    binding.ivClear.setVisibility(View.GONE);
                    Log.e("filter list empty text" , ""+dataListSuggestionsFilter.size()+","+dataListSuggestionsFilter.size());
                }else{
                    binding.ivClear.setVisibility(View.VISIBLE);
                    for(StandardItemsModel model : dataListSuggestions){
                        Log.e(""+model.name.toLowerCase() , "filter "+filterStr);
                        if (model.name.toLowerCase().startsWith(filterStr)){
                            dataListSuggestionsFilter.add(model);
                        }
                    }
                    Log.e("list not empty text" , ""+dataListSuggestionsFilter.size()+","+dataListSuggestionsFilter.size());
                }
                Log.e("filter list " , ""+dataListSuggestionsFilter.size()+","+dataListSuggestionsFilter.size());
                adapterSuggestions.notifyDataSetChanged();
            }
        });

        binding.ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etFilter.setText("");
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