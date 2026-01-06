package com.ascent.pmrsurveyapp.SalesExecutive.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Adepters.QuotationHistoryAdepter;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.QuotationHistoryModel;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.ActivityInquiriesBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityQuotationDetailsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuotationDetails extends AppCompatActivity {

    Comman cmn;
    Activity mActivity;
    ActivityQuotationDetailsBinding binding;
    ArrayList<QuotationHistoryModel> quotationHistoryList = new ArrayList<>();
    String id = "";
    private QuotationHistoryAdepter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quotation_details);

        id = getIntent().getStringExtra("id");
        mActivity = this;
        cmn = new Comman(mActivity);

        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.sagmentedGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(final int position) {
                // Handle stuff here
                Log.e("position" , ""+ position);

                if (position== 0){
                    binding.layHistory.setVisibility(View.GONE);
                    binding.layOverview.setVisibility(View.VISIBLE);
                }else {
                    binding.layHistory.setVisibility(View.VISIBLE);
                    binding.layOverview.setVisibility(View.GONE);
                }
            }
        });

        getOverView();
        getQuotations();
    }

    void getOverView() {
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {

                        JSONObject obj = new JSONObject(aResponse);

                        binding.etQuotationDate.setText(""+cmn.getDate(obj.optLong("date")));
                        binding.etInquiryDate.setText(""+cmn.getDate(obj.optLong("inquiryDate")));
                        binding.etAccountName.setText(""+obj.optString("companyName"));
                        binding.etPreparedBy.setText(""+obj.optString("fristName"));
                        binding.etClientName.setText(""+obj.optString("clientName"));
                        binding.etClientEmail.setText(""+obj.optString("clientEmail"));
                        binding.etClientMobile.setText(""+obj.optString("clientContactNumber"));
                        binding.etMoveType.setText(""+obj.optString("moveType"));
                        binding.tvTotal.setText("Total : "+obj.optString("total"));
                        binding.etOrigin.setText(""+obj.optString("originAddressLine1")+" "+obj.optString("originAddressLine1")+"\n"+obj.optString("originCityName")+"\n"+obj.optString("originStateName"));
                        binding.etDestination.setText(""+obj.optString("addressLine1")+" "+obj.optString("addressLine2")+"\n"+obj.optString("cityName")+"\n"+obj.optString("stateName"));


                        JSONArray array = obj.optJSONArray("particulars");
                        for (int index = 0; index < array.length(); index++) {
                            JSONObject data = array.optJSONObject(index);
                            View temp = getLayoutInflater().inflate(R.layout.comman_row,null);
                            TextView itemName = temp.findViewById(R.id.item);
                            TextView itemPrice = temp.findViewById(R.id.item1);
                            itemName.setText(""+data.optJSONObject("item").optString("name"));
                            itemPrice.setText(""+data.optJSONObject("item").optString("price"));
                            binding.particularHolder.addView(temp);
                        }

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
        url = "quotations/"+id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(true);
    }

    void getQuotations() {
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {

                        JSONArray array = new JSONArray(aResponse);

                        for (int i = 0; i <array.length() ; i++) {
                            JSONObject obj = array.optJSONObject(i);
                            quotationHistoryList.add(new Parser(mActivity).parseQuotationHistory(obj));
                        }
                        mAdapter = new QuotationHistoryAdepter(mActivity,quotationHistoryList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
                        binding.reclycalView.setLayoutManager(mLayoutManager);
                        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());
                        binding.reclycalView.setAdapter(mAdapter);

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
        url = "quotations?start=0&length=100&FindAll=version&quotationId="+id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }
}