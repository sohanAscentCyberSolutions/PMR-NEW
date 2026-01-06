package com.ascent.pmrsurveyapp.OperationSupervisor;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.PackingItemModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.ActivityNewLoadingSheetBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityNewPackingSheetBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class NewPackingSheet extends AppCompatActivity {


    Comman cmn;
    Activity mActivity;
    ActivityNewPackingSheetBinding binding;
    ArrayList<PackingItemModel> dataList = new ArrayList<>();
    String jobId = "";
    String jobNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_packing_sheet);

        mActivity = this;
        cmn = new Comman(mActivity);

        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        jobId = getIntent().getStringExtra("id");
        jobNumber = getIntent().getStringExtra("jobNumber");

        addDocument();


        binding.btViewSymboles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(mActivity);
                d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFade;
                d.setContentView(R.layout.lay_symboles);
                Button btClose =  d.findViewById(R.id.btClose);
                btClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                d.show();
                d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            }
        });


        binding.btAddmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDocument();
            }
        });


        binding.btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean isAllAreValid = true;

                for (int i = 0; i <dataList.size() ; i++) {
                    PackingItemModel model = dataList.get(i);

                    final TextInputEditText etCRRef = model.itemView.findViewById(R.id.etCRRef);
                    final TextInputEditText etArticle = model.itemView.findViewById(R.id.etArticle);
                    final TextInputEditText etPackedBy = model.itemView.findViewById(R.id.etPackedBy);
                    final TextInputEditText etValue = model.itemView.findViewById(R.id.etValue);
                    final TextInputEditText etConditionAtOrigin = model.itemView.findViewById(R.id.etConditionAtOrigin);


                    if (etCRRef.getText().toString().isEmpty()){
                        cmn.showToast("CR. Ref. Required !!");
                        etCRRef.requestFocus();
                        isAllAreValid = false;
                        break;
                    }
                    if (etArticle.getText().toString().isEmpty()){
                        cmn.showToast("Article  Required !!");
                        etArticle.requestFocus();
                        isAllAreValid = false;
                        break;
                    }
                    if (etPackedBy.getText().toString().isEmpty()){
                        cmn.showToast("Packed By Required !!");
                        etPackedBy.requestFocus();
                        isAllAreValid = false;
                        break;
                    }
                   /* if (etValue.getText().toString().isEmpty()){
                        cmn.showToast("Value Required !!");
                        etValue.requestFocus();
                        isAllAreValid = false;
                        break;
                    }
                    if (etConditionAtOrigin.getText().toString().isEmpty()){
                        cmn.showToast("Condition At Origin Required !!");
                        etConditionAtOrigin.requestFocus();
                        isAllAreValid = false;
                        break;
                    }*/

                    if (binding.signaturePadShipper.isEmpty()){
                        cmn.showToast("Please draw Shipper Signature");
                        isAllAreValid = false;
                        break;
                    }

                    if (binding.signaturePadContractor.isEmpty()){
                        cmn.showToast("Please draw Contractor, Carrier or Authorized Agent(Driver) Signature");
                        isAllAreValid = false;
                        break;
                    }

                }

                if (isAllAreValid){
                    SaveData();
                }

            }
        });


        binding.btSaveSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signatureImage.setImageBitmap(binding.signaturePadShipper.getSignatureBitmap());
                binding.signaturePadShipper.setVisibility(View.GONE);
                binding.signatureImage.setVisibility(View.VISIBLE);
                binding.btSaveSign.setVisibility(View.GONE);
                binding.btClearSign.setText("Reset");
            }
        });

        binding.btSaveSignSurveyor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signatureImageSurveyor.setImageBitmap(binding.signaturePadContractor.getSignatureBitmap());
                binding.signaturePadContractor.setVisibility(View.GONE);
                binding.signatureImageSurveyor.setVisibility(View.VISIBLE);
                binding.btSaveSignSurveyor.setVisibility(View.GONE);
                binding.btClearSignSurveyor.setText("Reset");
            }
        });

        binding.btClearSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btClearSign.getText().toString().equalsIgnoreCase("reset")){
                    binding.signaturePadShipper.clear();
                    binding.signaturePadShipper.setVisibility(View.VISIBLE);
                    binding.btSaveSign.setVisibility(View.VISIBLE);
                    binding.btClearSign.setText("Clear");
                    binding.signatureImage.setVisibility(View.GONE);
                }else {
                    binding.signaturePadShipper.clear();
                }
            }
        });
        binding.btClearSignSurveyor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btClearSignSurveyor.getText().toString().equalsIgnoreCase("reset")){
                    binding.signaturePadContractor.clear();
                    binding.signaturePadContractor.setVisibility(View.VISIBLE);
                    binding.btSaveSignSurveyor.setVisibility(View.VISIBLE);
                    binding.btClearSignSurveyor.setText("Clear");
                    binding.signatureImageSurveyor.setVisibility(View.GONE);
                }else {
                    binding.signaturePadContractor.clear();
                }
            }
        });


    }


    void addDocument(){
        PackingItemModel model = new PackingItemModel();

        final View temp = mActivity.getLayoutInflater().inflate(R.layout.row_new_paking_item , null);
        temp.setTag(dataList.size());

        final ImageView ivImage = temp.findViewById(R.id.ivImage);
        final TextInputEditText etItemNo = temp.findViewById(R.id.etItemNo);
        final TextInputEditText etCRRef = temp.findViewById(R.id.etCRRef);
        final TextInputEditText etArticle = temp.findViewById(R.id.etArticle);
        final TextInputEditText etPackedBy = temp.findViewById(R.id.etPackedBy);
        final TextInputEditText etValue = temp.findViewById(R.id.etValue);
        final Button btSelectFile = temp.findViewById(R.id.btSelectFile);
        final ImageView ivDelete = temp.findViewById(R.id.ivDelete);

        etItemNo.setText(""+(dataList.size()+1));

        btSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityResultLauncher<Intent> launcher=
                        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                            if(result.getResultCode()==RESULT_OK){
                                Uri imageUri=result.getData().getData();
                                String fileName = imageUri.getPath().substring(imageUri.getPath().lastIndexOf("/") + 1) ;
                                cmn.printLog("-----uri"+fileName);
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), imageUri);
                                    model.filePath = cmn.getBase64FromBitmap(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                model.fileName = fileName;
                                ivImage.setImageURI(imageUri);
                            }else if(result.getResultCode()== ImagePicker.RESULT_ERROR){
                                // Use ImagePicker.Companion.getError(result.getData()) to show an error
                            }
                        });
                ImagePicker.Companion.with(getParent())
                        .crop()
                        .cropOval()
                        .maxResultSize(512,512,true)
                        .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                        .createIntentFromDialog((Function1)(new Function1(){
                            public Object invoke(Object var1){
                                this.invoke((Intent)var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it){
                                Intrinsics.checkNotNullParameter(it,"it");
                                launcher.launch(it);
                            }
                        }));
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmn.printLog("tag = " + temp.getTag());
                if (dataList.size()>1){
                    for (int i = 0; i <dataList.size() ; i++) {
                        if (temp.getTag() == dataList.get(i).itemView.getTag()){
                            dataList.remove(i);
                            resetViews();
                            break;
                        }
                    }
                }else {
                    cmn.showToast("Atleast one document required.");
                }
            }
        });

        model.itemView = temp;

        dataList.add(model);
        binding.placeHolder.addView(temp);
    }


    void resetViews(){
        cmn.showProgressDialog();
        binding.placeHolder.removeAllViews();
        for (int i = 0; i < dataList.size() ; i++) {
            dataList.get(i).itemView.setTag(i);
            final TextInputEditText etItemNo = dataList.get(i).itemView.findViewById(R.id.etItemNo);
            etItemNo.setText(""+(i+1));
            binding.placeHolder.addView(dataList.get(i).itemView);
        }

        cmn.hideProgressDialog();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            cmn.printLog("---------------------------------------- Document details");
          //  imagePicker.handleActivityResult(resultCode, requestCode, data);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       // imagePicker.handlePermission(requestCode, grantResults);
    }


    void SaveData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    cmn.showToast("Packing List Uploaded Successfully !!");
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
            for (int i = 0; i <dataList.size() ; i++) {

                JSONObject temp = new JSONObject();
                PackingItemModel model = dataList.get(i);
                temp.put("id" , null);
                if (dataList.get(i).filePath.isEmpty()){
                    temp.put("filePath" , null);
                }else{
                    temp.put("filePath" , dataList.get(i).filePath);
                }

                if (dataList.get(i).filePath.isEmpty()){
                    temp.put("fileName" , null);
                }else{
                    temp.put("fileName" , dataList.get(i).fileName);
                }
                final TextInputEditText etItemNo = model.itemView.findViewById(R.id.etItemNo);
                final TextInputEditText etCRRef = model.itemView.findViewById(R.id.etCRRef);
                final TextInputEditText etArticle = model.itemView.findViewById(R.id.etArticle);
                final TextInputEditText etPackedBy = model.itemView.findViewById(R.id.etPackedBy);
                final TextInputEditText etValue = model.itemView.findViewById(R.id.etValue);
                final TextInputEditText etConditionAtOrigin = model.itemView.findViewById(R.id.etConditionAtOrigin);

                temp.put("itemNo" , etItemNo.getText().toString());
                temp.put("crRef" , etCRRef.getText().toString());
                temp.put("article" , etArticle.getText().toString());
                temp.put("packedBy" , etPackedBy.getText().toString());
                temp.put("value" , etValue.getText().toString());
                temp.put("conditionAtOrigin" , etConditionAtOrigin.getText().toString());
                packedItems.put(temp);
            }
            parameters.put("packedItems" , packedItems);

            parameters.put("remarks" , binding.etRemarks.getText().toString());
            parameters.put("contractorSignature" , "data:image/png;base64,"+cmn.getBase64FromBitmap(binding.signaturePadContractor.getSignatureBitmap()));
            parameters.put("shipperSignature" , "data:image/png;base64,"+cmn.getBase64FromBitmap(binding.signaturePadShipper.getSignatureBitmap()));



        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "packinglist";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.postAPI();
    }

}