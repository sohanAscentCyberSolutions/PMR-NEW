package com.ascent.pmrsurveyapp.SalesExecutive.UI;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ascent.pmrsurveyapp.OperationSupervisor.JobExecutions;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobDocumentModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.UpdateData;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.ActivityUploadDocumentInquiriesBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityUploadDocumentsJobBinding;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class UploadDocumentInquiries extends AppCompatActivity {


    Comman cmn;
    Activity mActivity;
    ActivityUploadDocumentInquiriesBinding binding;
    ArrayList<JobDocumentModel> dataList = new ArrayList<>();
    String inquiryId = "";
    public static UpdateData listioner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload_document_inquiries);

        mActivity = this;
        cmn = new Comman(mActivity);

        inquiryId = getIntent().getStringExtra("id");

        addDocument();

        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                    JobDocumentModel model = dataList.get(i);

                    if (model.filePath.isEmpty()){
                        cmn.showToast("Please Select Document !!");
                        isAllAreValid = false;
                        break;
                    }
                    if (model.description.isEmpty()){
                        cmn.showToast("Description Required !!");
                        isAllAreValid = false;
                        break;
                    }

                }

                if (isAllAreValid){
                    SaveData();
                }

            }
        });

    }



    void addDocument(){
        JobDocumentModel model = new JobDocumentModel();
        final View temp = mActivity.getLayoutInflater().inflate(R.layout.row_upload_document , null);
        temp.setTag(dataList.size());

        final ImageView ivImage = temp.findViewById(R.id.ivImage);
        final TextInputEditText etDescription = temp.findViewById(R.id.etDescription);
        final Button btSelectFile = temp.findViewById(R.id.btSelectFile);
        final ImageView ivDelete = temp.findViewById(R.id.ivDelete);


        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                model.description = s.toString();
            }
        });

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
                        if (temp.getTag() == dataList.get(i).tempView.getTag()){
                            binding.placeHolder.removeView(dataList.get(i).tempView);
                            dataList.remove(i);
                            break;
                        }
                    }
                }else {
                    cmn.showToast("Atleast one document required.");
                }
            }
        });

        model.tempView = temp;

        dataList.add(model);
        binding.placeHolder.addView(temp);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            cmn.printLog("---------------------------------------- Document details");

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    void SaveData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    cmn.showToast("Document Uploaded Successfully !!");
                    listioner.reloadTheData();
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
            parameters.put("id" ,  inquiryId);
            JSONArray documents = new JSONArray();
            for (int i = 0; i <dataList.size() ; i++) {
                JSONObject temp = new JSONObject();
                temp.put("id" , null);
                temp.put("filePath" , dataList.get(i).filePath);
                temp.put("fileName" , dataList.get(i).fileName);
                temp.put("description" , dataList.get(i).description);
                documents.put(temp);
            }
            parameters.put("documents" , documents);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "inquiry?Operation=UploadDocument";
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.postAPI();
    }

}