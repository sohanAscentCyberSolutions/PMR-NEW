package com.ascent.pmrsurveyapp.OperationSupervisor.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ascent.pmrsurveyapp.OperationSupervisor.JobExecutionDetails;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.LayJobActivityHistoryBinding;
import com.ascent.pmrsurveyapp.databinding.LayRequestDetailsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JobActivityHistory extends Fragment {

    LayJobActivityHistoryBinding binding;

    Comman cmn;
    Activity mActivity;

    public static JobActivityHistory newInstance() {
        return new JobActivityHistory();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.lay_job_activity_history, container, false);
        mActivity = getActivity();
        cmn = new Comman(mActivity);

        getActivityHistoryData();

        return binding.getRoot();


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }


    void getActivityHistoryData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        JSONArray array = new JSONArray(aResponse);
                        for (int index = 0;index<array.length();index++){
                            JSONObject data = array.getJSONObject(index);
                            View temp = getLayoutInflater().inflate(R.layout.row_contacts , null);

                            TextView tvName = temp.findViewById(R.id.tvName);
                            TextView tvEmail = temp.findViewById(R.id.tvEmail);
                            TextView tvContactNo = temp.findViewById(R.id.tvContactNo);

                            tvName.setText(""+cmn.getDate(data.optLong("performedOn")));
                            tvEmail.setText(""+data.optString("description"));
                            tvContactNo.setText(""+data.optJSONObject("performedBy").optString("fullName"));

                            binding.activityHolder.addView(temp);

                        }
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
        url = "jobexecution/activities/"+ JobExecutionDetails.data.id;
        HttpRequest request = new HttpRequest(url, parameters, handler, mActivity);
        request.getAPI(false);
    }

}