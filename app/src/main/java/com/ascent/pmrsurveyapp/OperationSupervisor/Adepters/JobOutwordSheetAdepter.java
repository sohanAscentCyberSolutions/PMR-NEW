package com.ascent.pmrsurveyapp.OperationSupervisor.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.OperationSupervisor.Models.OutWordSheetModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;

import java.util.ArrayList;

public class JobOutwordSheetAdepter extends RecyclerView.Adapter<JobOutwordSheetAdepter.MyViewHolder> {

    private ArrayList<OutWordSheetModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;
    Comman cmn;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView outwardDate, noOfPackages,createdBy;


        public Button btView;

        public MyViewHolder(View view) {
            super(view);

            createdBy = view.findViewById(R.id.createdBy);
            outwardDate = (TextView) view.findViewById(R.id.outwardDate);
            noOfPackages = (TextView) view.findViewById(R.id.noOfPackages);
            btView = view.findViewById(R.id.btView);
        }
    }


    public JobOutwordSheetAdepter(Activity ctx , ArrayList<OutWordSheetModel> dataList , ClickAdepterListener onClickListener) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
        this.cmn = new Comman(ctx);
    }

    @Override
    public JobOutwordSheetAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_outward_sheet, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final OutWordSheetModel model = dataList.get(position);
        holder.outwardDate.setText(""+model.outwardDate);
        holder.noOfPackages.setText(""+model.noOfPackages);
        holder.createdBy.setText(""+model.createdBy);


        holder.btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.detailsClicked(v,position);
            }
        });

    }

    public interface ClickAdepterListener {
        void detailsClicked(View v, int position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
