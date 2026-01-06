package com.ascent.pmrsurveyapp.SalesExecutive.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Adepters.AccountsAdepter;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.AccountModel;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.ActivityAccountsBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityAssignedRequestsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Accounts extends AppCompatActivity {

    Comman cmn;
    Activity mActivity;
    ActivityAccountsBinding binding;
    ArrayList<AccountModel> dataList = new ArrayList<>();
    ArrayList<AccountModel> dataListFilter = new ArrayList<>();
    private AccountsAdepter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_accounts);

        mActivity = this;
        cmn = new Comman(mActivity);

        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                String filterStr = binding.etFilter.getText().toString().toLowerCase();
                if (filterStr.isEmpty()){
                    dataListFilter.addAll(dataList);
                    binding.ivClear.setVisibility(View.GONE);
                }else{
                    binding.ivClear.setVisibility(View.VISIBLE);
                    for(AccountModel model : dataList){
                        if (model.companyName.toLowerCase().startsWith(filterStr) || model.email.toLowerCase().startsWith(filterStr)  || model.address.state.name.toLowerCase().startsWith(filterStr) || model.address.city.name.toLowerCase().startsWith(filterStr)|| model.address.addressLine1.toLowerCase().startsWith(filterStr)){
                            dataListFilter.add(model);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                Log.e("text changed" , "records found" + dataListFilter.size());
            }
        });

        binding.ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etFilter.setText("");
            }
        });

        mAdapter = new AccountsAdepter(mActivity, dataListFilter, new AccountsAdepter.ClickAdepterListener() {
            @Override
            public void detailsClicked(View v, int position) {
                AccountDetails.tempData =  dataListFilter.get(position);
                Intent mainIntent = new Intent(mActivity, AccountDetails.class);
                mainIntent.putExtra("id" , dataListFilter.get(position).id);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void deleteClicked(View v, int position) {
                deleteAccount(dataListFilter.get(position));
            }

            @Override
            public void editClicked(View v, int position) {
                AddAccount.tempData =  dataListFilter.get(position);
                Intent mainIntent = new Intent(mActivity, AddAccount.class);
                mainIntent.putExtra("isEdit" , true);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        binding.reclycalView.setLayoutManager(mLayoutManager);
        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());
        binding.reclycalView.setAdapter(mAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    void deleteAccount(AccountModel data){
        new MaterialAlertDialogBuilder(mActivity, R.style.AlertDialogMaterialTheme)
                .setTitle("Logout")
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        JSONArray parameters = new JSONArray();
                        Handler handler = new Handler(){
                            public void handleMessage(Message msg) {
                                String aResponse = msg.getData().getString("message");
                                if ((null != aResponse)) {
                                    cmn.showToast("Account Deleted Successfully !!");
                                   getData();
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
                        try {
                            parameters.put(new JSONObject().put("id" , data.id));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        url = "account/delete";
                        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
                        request.putAPIArrayParams();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    void getData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        dataList.clear();
                        dataListFilter.clear();
                        JSONObject obj = new JSONObject(aResponse);
                        JSONArray array = obj.optJSONArray("contant");
                        for (int index = 0;index<array.length();index++){
                            dataList.add(new Parser(mActivity).parseAccount(array.optJSONObject(index)));
                        }
                        dataListFilter.addAll(dataList);
                        mAdapter.notifyDataSetChanged();
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
        url = "account?page=0&size=15&searchFields=companyName&searchText=&sortField=undefined&sortOrder=asc";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(true);
    }
}