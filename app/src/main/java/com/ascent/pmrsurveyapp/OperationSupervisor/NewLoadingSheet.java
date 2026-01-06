package com.ascent.pmrsurveyapp.OperationSupervisor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.textfield.TextInputEditText;
import com.ascent.pmrsurveyapp.OperationSupervisor.Adepters.ItemSuggestionAdapter;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.PackingItemModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.ActivityJobExecutionsBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityNewLoadingSheetBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewLoadingSheet extends AppCompatActivity {

    Comman cmn;
    Activity mActivity;
    ActivityNewLoadingSheetBinding binding;
    ArrayList<PackingItemModel> dataList = new ArrayList<>();
    ArrayList<PackingItemModel> loadingList = new ArrayList<>();
    ItemSuggestionAdapter adapterSuggestions;

    String jobId = "";
    String jobNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_loading_sheet);

        mActivity = this;
        cmn = new Comman(mActivity);

        jobId = getIntent().getStringExtra("id");
        jobNumber = getIntent().getStringExtra("jobNumber");

        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getData();

        binding.btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataList.isEmpty()){
                    cmn.showToast("Packed Items remaining to load");
                }else if (binding.etShippingCustomTruckSealNo.getText().toString().isEmpty()){
                    cmn.showToast("Shipping/Custom/Truck Seal No required !");
                }else if (binding.etTruckContainerNo.getText().toString().isEmpty()){
                    cmn.showToast("Truck Container No required !");
                }else if (binding.signaturePadShipper.isEmpty()){
                    cmn.showToast("Please draw Shipper Signature");
                }else if (binding.signaturePadSupervisor.isEmpty()){
                    cmn.showToast("Please draw Supervisor Signature");
                }else{
                    SaveData();
                }
            }
        });

        binding.btSaveSignShipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signatureImageShipper.setImageBitmap(binding.signaturePadShipper.getSignatureBitmap());
                binding.signaturePadShipper.setVisibility(View.GONE);
                binding.signatureImageShipper.setVisibility(View.VISIBLE);
                binding.btSaveSignShipper.setVisibility(View.GONE);
                binding.btClearSignShipper.setText("Reset");
            }
        });

        binding.btSaveSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signatureImage.setImageBitmap(binding.signaturePadSupervisor.getSignatureBitmap());
                binding.signaturePadSupervisor.setVisibility(View.GONE);
                binding.signatureImage.setVisibility(View.VISIBLE);
                binding.btSaveSign.setVisibility(View.GONE);
                binding.btClearSign.setText("Reset");
            }
        });

        binding.btClearSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btClearSign.getText().toString().equalsIgnoreCase("reset")){
                    binding.signaturePadSupervisor.clear();
                    binding.signaturePadSupervisor.setVisibility(View.VISIBLE);
                    binding.btSaveSign.setVisibility(View.VISIBLE);
                    binding.btClearSign.setText("Clear");
                    binding.signatureImage.setVisibility(View.GONE);
                }else {
                    binding.signaturePadSupervisor.clear();
                }
            }
        });
        binding.btClearSignShipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btClearSignShipper.getText().toString().equalsIgnoreCase("reset")){
                    binding.signaturePadShipper.clear();
                    binding.signaturePadShipper.setVisibility(View.VISIBLE);
                    binding.btSaveSignShipper.setVisibility(View.VISIBLE);
                    binding.btClearSignShipper.setText("Clear");
                    binding.signatureImageShipper.setVisibility(View.GONE);
                }else {
                    binding.signaturePadShipper.clear();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void resetViews(){
        cmn.showProgressDialog();
        binding.placeHolder.removeAllViews();

        cmn.printLog(""+loadingList.size());

        for (int i = 0; i < loadingList.size() ; i++) {
            final int index =  i;
            View temp = loadingList.get(i).itemView;
            loadingList.get(i).itemView.setTag(i);
            final TextInputEditText etItemNo =  temp.findViewById(R.id.etItemNo);
            final TextInputEditText etCRRef =  temp.findViewById(R.id.etCRRef);
            final TextInputEditText etArticle =  temp.findViewById(R.id.etArticle);
            final TextInputEditText etLoadingSequence =  temp.findViewById(R.id.etLoadingSequence);
            final ImageView ivDelete =  temp.findViewById(R.id.ivDelete);

            etLoadingSequence.setText(""+(i+1));
            etItemNo.setText(""+(loadingList.get(index).itemNo));
            etCRRef.setText(""+(loadingList.get(index).crRef));
            etArticle.setText(""+(loadingList.get(index).article));

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cmn.printLog("tag = " + index);
                    dataList.add(loadingList.get(index));
                    loadingList.remove(index);
                    resetViews();
                }
            });

            binding.placeHolder.addView(temp);
        }
        cmn.hideProgressDialog();
    }

    void getData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        dataList.clear();
                        JSONArray array = new JSONArray(aResponse);
                        for (int index = 0;index<array.length();index++){
                            PackingItemModel model = new Parser(mActivity).parsePackingItem(array.optJSONObject(index));
                            final View temp = mActivity.getLayoutInflater().inflate(R.layout.row_new_loading_item , null);
                            model.itemView = temp;
                            dataList.add(model);
                        }
                        adapterSuggestions = new ItemSuggestionAdapter(mActivity, android.R.layout.simple_list_item_1, dataList, new ItemSuggestionAdapter.SuggestionAdapterListener() {
                            @Override
                            public void placeHolderClicked(PackingItemModel model) {
                                cmn.printLog("item selected "+model.itemNo);
                                binding.autoSearchTV.setText("");
                                for (int i = 0; i <dataList.size() ; i++) {
                                    if (dataList.get(i).itemNo.equalsIgnoreCase(model.itemNo)){
                                        loadingList.add(model);
                                        dataList.remove(model);
                                        resetViews();
                                        break;
                                    }
                                }
                                binding.etTotalPackets.setText(""+loadingList.size());
                            }
                        });
                        binding.autoSearchTV.setAdapter(adapterSuggestions);

                        binding.autoSearchTV.setOnTouchListener(new View.OnTouchListener(){
                            @Override
                            public boolean onTouch(View v, MotionEvent event){
                                binding.autoSearchTV.showDropDown();
                                return false;
                            }
                        });

                        binding.autoSearchTV.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                                if (binding.autoSearchTV.getText().toString().isEmpty()){
                                    binding.ivClear.setVisibility(View.GONE);
                                }else{
                                    binding.ivClear.setVisibility(View.VISIBLE);
                                }

                            }
                        });
                        binding.autoSearchTV.setThreshold(0);

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
        String url = "packinglist?Find=PackedItemByJobExecution&jobExecutionId="+jobId+"&filterType=ALL";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(true);
    }
    void SaveData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    cmn.showToast("Loading Sheet Uploaded Successfully !!");
                    JobExecutions.updateData.reloadTheData();
                    finish();
                } else
                {
                    Toast.makeText(
                            mActivity,
                            "Not Got Response From Server.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        try {
            parameters.put("id" , null);
            parameters.put("jobExecution" , new JSONObject().put("id" , jobId).put("job" , new JSONObject().put("jobNumber" , jobNumber)));
            JSONArray packedItems = new JSONArray();
            for (int i = 0; i <loadingList.size() ; i++) {

                JSONObject temp = new JSONObject();
                PackingItemModel model = loadingList.get(i);
                temp.put("id" , model.id);

                temp.put("itemNo" , model.itemNo);
                temp.put("crRef" , model.crRef);
                temp.put("article" , model.article);
                temp.put("quantity" , model.quantity);
                temp.put("packedBy" , model.packedBy);
                temp.put("value" , model.value);
                temp.put("conditionAtOrigin" , model.conditionAtOrigin);
                temp.put("loadingSequence" , (i+1));
                packedItems.put(temp);
            }
            parameters.put("packingList" , new JSONObject().put("packedItems" , packedItems));

            parameters.put("transportShippingCo" , binding.etTransportShippingCo.getText().toString());
            parameters.put("liftVanSize" , binding.etLiftvansizes.getText().toString());
            parameters.put("truckSealNo" , binding.etShippingCustomTruckSealNo.getText().toString());
            parameters.put("truckContainerNo" , binding.etTruckContainerNo.getText().toString());
            parameters.put("liftVanNos" , binding.etLiftVanNos.getText().toString());
            parameters.put("createNos" , binding.etCrateNos.getText().toString());

            parameters.put("supervisorSignature" , "data:image/png;base64,"+cmn.getBase64FromBitmap(binding.signaturePadSupervisor.getSignatureBitmap()));
            parameters.put("shipperSignature" , "data:image/png;base64,"+cmn.getBase64FromBitmap(binding.signaturePadShipper.getSignatureBitmap()));



        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "loadingsheet";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.postAPI();
    }
}