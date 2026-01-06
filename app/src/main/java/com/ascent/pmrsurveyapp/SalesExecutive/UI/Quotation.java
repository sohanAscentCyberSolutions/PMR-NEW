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
import android.widget.Toast;

import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Adepters.QuotationAdepter;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.QuotationModel;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.ActivityAssignedRequestsBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityQuotationBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Quotation extends AppCompatActivity {

    Comman cmn;
    Activity mActivity;
    ActivityQuotationBinding binding;
    ArrayList<QuotationModel> dataList = new ArrayList<>();
    ArrayList<QuotationModel> dataListFilter = new ArrayList<>();
    private QuotationAdepter mAdapter;
    // 0 for assigned & 1 for completed
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quotation);

        mActivity = this;
        cmn = new Comman(mActivity);

        type = getIntent().getIntExtra("status", 0);

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
                if (filterStr.isEmpty()) {
                    dataListFilter.addAll(dataList);
                    binding.ivClear.setVisibility(View.GONE);
                } else {
                    binding.ivClear.setVisibility(View.VISIBLE);
                    for (QuotationModel model : dataList) {
                        if (model.date.toLowerCase().startsWith(filterStr) || model.preparedBy.name.toLowerCase().startsWith(filterStr) || model.inquiry.account.name.toLowerCase().startsWith(filterStr) || model.inquiry.client.name.toLowerCase().startsWith(filterStr)) {
                            dataListFilter.add(model);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                Log.e("text changed", "records found" + dataListFilter.size());
            }
        });

        mAdapter = new QuotationAdepter(mActivity, dataListFilter, new QuotationAdepter.ClickAdepterListener() {

            @Override
            public void detailsClicked(View v, int position) {
                Intent mainIntent = new Intent(mActivity, QuotationDetails.class);
                mainIntent.putExtra("id" , dataListFilter.get(position).inquiry.id);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void inquiryClicked(View v, int position) {
                Intent mainIntent = new Intent(mActivity, InquiryDetails.class);
                mainIntent.putExtra("id" , dataListFilter.get(position).inquiry.id);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        binding.reclycalView.setLayoutManager(mLayoutManager);
        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());
        binding.reclycalView.setAdapter(mAdapter);

        getData();
    }

    void getData() {
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        dataList.clear();
                        dataListFilter.clear();
                        JSONObject obj = new JSONObject(aResponse);
                        JSONArray array = obj.optJSONArray("data");
                        for (int index = 0; index < array.length(); index++) {
                            dataList.add(new Parser(mActivity).parseQuotation(array.optJSONObject(index)));
                        }
                        dataListFilter.addAll(dataList);
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(
                            mActivity,
                            "Not Got Response From Server.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        String url = "";
        url = "quotations?Find=BySalesExe&draw=1&columns%5B0%5D%5Bdata%5D=date&columns%5B0%5D%5Bname%5D=&columns%5B0%5D%5Bsearchable%5D=true&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=preparedBy&columns%5B1%5D%5Bname%5D=&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=false&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=inquiry.account.companyName&columns%5B2%5D%5Bname%5D=&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=false&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B3%5D%5Bdata%5D=inquiry.client.name&columns%5B3%5D%5Bname%5D=&columns%5B3%5D%5Bsearchable%5D=true&columns%5B3%5D%5Borderable%5D=false&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B4%5D%5Bdata%5D=inquiry&columns%5B4%5D%5Bname%5D=&columns%5B4%5D%5Bsearchable%5D=true&columns%5B4%5D%5Borderable%5D=false&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=0&order%5B0%5D%5Bdir%5D=desc&start=0&length=10000&search%5Bvalue%5D=&search%5Bregex%5D=false&_=1612340698139";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(true);
    }
}