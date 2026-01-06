package com.ascent.pmrsurveyapp.OperationSupervisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.ascent.pmrsurveyapp.OperationSupervisor.Adepters.JobDetailsTabsAdepter;
import com.ascent.pmrsurveyapp.OperationSupervisor.Fragments.JobActivityHistory;
import com.ascent.pmrsurveyapp.OperationSupervisor.Fragments.JobDocuments;
import com.ascent.pmrsurveyapp.OperationSupervisor.Fragments.JobInwordSheet;
import com.ascent.pmrsurveyapp.OperationSupervisor.Fragments.JobLoadingSheet;
import com.ascent.pmrsurveyapp.OperationSupervisor.Fragments.JobOutwoardSheet;
import com.ascent.pmrsurveyapp.OperationSupervisor.Fragments.JobPackingList;
import com.ascent.pmrsurveyapp.OperationSupervisor.Fragments.JobSurveyReport;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobExecutionModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.databinding.ActivityInquiryDetailsBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityJobExecutionDetailsBinding;

public class JobExecutionDetails extends AppCompatActivity {


    ActivityJobExecutionDetailsBinding binding;
    Comman cmn;
    Activity mActivity;

    boolean ispageSetuped = false;

    public static JobExecutionModel data = new JobExecutionModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_execution_details);
        mActivity = this;
        cmn = new Comman(mActivity);

        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.tablayout.setTabGravity(TabLayout.GRAVITY_START);
        binding.viewPager.setOffscreenPageLimit(5);
        binding.tablayout.setupWithViewPager(binding.viewPager);

        setData();

    }

    @Override
    protected void onResume() {
        if (!ispageSetuped){
            setupViewPager(binding.viewPager);
            ispageSetuped = true;
        }
        super.onResume();
    }

    private void setupViewPager(ViewPager viewPager) {
        JobDetailsTabsAdepter adapter = new JobDetailsTabsAdepter(getSupportFragmentManager());
        adapter.addFragment(new JobActivityHistory() , "Activity History");
        adapter.addFragment(new JobSurveyReport() , "Survey Report");
        if (data.packingListCreated){
            adapter.addFragment(new JobPackingList() , "Packing List");
        }
        if (data.loadingSheetCreated){
            adapter.addFragment(new JobLoadingSheet() , "Loading Sheet");
        }
        if (data.inwardSheetCreated){
            adapter.addFragment(new JobInwordSheet() , "Inward Sheet");
        }

        if (data.outwardSheetCreated){
            adapter.addFragment(new JobOutwoardSheet() , "Outward Sheet");
        }

        if (data.documentUploaded){
            adapter.addFragment(new JobDocuments() , "Documents");
        }

        viewPager.setAdapter(adapter);
    }


    void setData(){
        binding.tvJobNumber.setText(""+data.job.jobNumber);
        binding.tvRequestedDate.setText(""+data.createdOn);
        binding.tvShipperName.setText(""+data.job.shipper.fullName);
        binding.tvGoodsType.setText(""+data.job.inquiry.goodsType);
        binding.tvMovementType.setText(""+data.job.inquiry.moveType);
        binding.tvMovement.setText(""+data.job.originAddress.city.name + " To "+ data.job.destinationAddress.city.name);
        binding.tvVolume.setText(""+data.volume);
        binding.tvStatus.setText(""+data.status);
    }


}