package com.ascent.pmrsurveyapp.SalesExecutive.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.tabs.TabLayout;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Adepters.InquiryAdepter;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.AccountModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.ContactsModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.ActivityAccountDetailsBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityAccountsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AccountDetails extends AppCompatActivity {

    Comman cmn;
    Activity mActivity;
    ActivityAccountDetailsBinding binding;
    ArrayList<InquiryModel> dataList = new ArrayList<>();
    ArrayList<InquiryModel> dataListFilter = new ArrayList<>();
    private InquiryAdepter mAdapter;

    public static  AccountModel tempData = new AccountModel();

    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_details);

        mActivity = this;
        cmn = new Comman(mActivity);

        id = getIntent().getStringExtra("id");

        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setDetails(tempData);

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
                    for(InquiryModel model : dataList){
                        if (model.id.toLowerCase().startsWith(filterStr) || model.date.toLowerCase().startsWith(filterStr)  || model.moveType.toLowerCase().startsWith(filterStr) || model.client.name.toLowerCase().startsWith(filterStr)){
                            dataListFilter.add(model);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                Log.e("text changed" , "records found" + dataListFilter.size());
            }
        });

        mAdapter = new InquiryAdepter(mActivity, dataListFilter, new InquiryAdepter.ClickAdepterListener() {
            @Override
            public void detailsClicked(View v, int position) {
                Intent mainIntent = new Intent(mActivity, InquiryDetails.class);
                mainIntent.putExtra("id" , dataListFilter.get(position).id);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void ediClicked(View v, int position) {

            }

            @Override
            public void uploadDocumentClicked(View v, int position) {

            }

            @Override
            public void assignClickedClicked(View v, int position) {

            }

            @Override
            public void telephonicSurveyClicked(View v, int position) {

            }

            @Override
            public void requestInfoClicked(View v, int position) {

            }

            @Override
            public void newSurveyRequestClicked(View v, int position) {

            }
        },false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        binding.reclycalView.setLayoutManager(mLayoutManager);
        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());
        binding.reclycalView.setAdapter(mAdapter);

        getInquiries();

        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateData(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    void updateData(int position){
        if (position==0){
            binding.layContacts.setVisibility(View.VISIBLE);
            binding.layInquiries.setVisibility(View.GONE);
            YoYo.with(Techniques.FadeIn)
                    .duration(700)
                    .playOn(findViewById(R.id.layContacts));
        }else{
            binding.layContacts.setVisibility(View.GONE);
            binding.layInquiries.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn)
                    .duration(700)
                    .playOn(findViewById(R.id.layInquiries));
        }
    }

    void setDetails(AccountModel detail){
        binding.tvName.setText(""+detail.companyName);
        binding.tvGSTUIN.setText(""+cmn.replaceNull(detail.gstinUin));
        binding.tvIndustry.setText(""+detail.industry.name);
        binding.tvPaymentTerms.setText(""+detail.paymentTerms);
        binding.tvContactNo.setText(""+detail.mobile);
        binding.tvEmail.setText(""+detail.email);
        binding.tvRegCode.setText(""+detail.registrationCode);
        binding.tvAddress.setText(""+detail.address.addressLine1 +","+detail.address.addressLine2 +","+detail.address.area+"\n"+detail.address.city.name+","+detail.address.state.name);
        binding.tvName.setText(""+detail.companyName);

        for (int i = 0; i < detail.contacts.size() ; i++) {
            ContactsModel contact = detail.contacts.get(i);

            View temp = getLayoutInflater().inflate(R.layout.row_contacts , null);

            TextView tvName = temp.findViewById(R.id.tvName);
            TextView tvEmail = temp.findViewById(R.id.tvEmail);
            TextView tvContactNo = temp.findViewById(R.id.tvContactNo);

            tvName.setText(""+contact.name);
            tvEmail.setText(""+contact.email);
            tvContactNo.setText(""+contact.number);

            binding.contactHolder.addView(temp);
        }

    }

    void getInquiries(){
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
                            dataList.add(new Parser(mActivity).parseInquirySales(array.optJSONObject(index)));
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
        url = "inquiry?Find=ByAccount&page=0&size=15&searchFields=name&sortField=undefined&sortOrder=asc&accountId="+id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }
}