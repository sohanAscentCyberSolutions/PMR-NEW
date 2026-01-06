package com.ascent.pmrsurveyapp.SalesExecutive.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.QuotationHistoryModel;

import java.util.ArrayList;

public class QuotationHistoryAdepter extends RecyclerView.Adapter<QuotationHistoryAdepter.MyViewHolder> {

    private ArrayList<QuotationHistoryModel> dataList;
    Activity ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextInputEditText etQuotationDate, etInquiryDate, etAccountName,etPreparedBy,etClientName,etClientEmail,etClientMobile
                ,etMoveType,etOrigin,etDestination;
        public TextView tvTotal,quotationVersion;
        public LinearLayout particularHolder;

        public MyViewHolder(View view) {
            super(view);
            etQuotationDate = view.findViewById(R.id.etQuotationDate);
            etInquiryDate = view.findViewById(R.id.etInquiryDate);
            etAccountName = view.findViewById(R.id.etAccountName);
            etPreparedBy = view.findViewById(R.id.etPreparedBy);
            etClientName = view.findViewById(R.id.etClientName);
            etClientEmail = view.findViewById(R.id.etClientEmail);
            etClientMobile = view.findViewById(R.id.etClientMobile);
            etMoveType = view.findViewById(R.id.etMoveType);
            tvTotal = view.findViewById(R.id.tvTotal);
            etOrigin = view.findViewById(R.id.etOrigin);
            etDestination = view.findViewById(R.id.etDestination);
            particularHolder = view.findViewById(R.id.particularHolder);
            quotationVersion = view.findViewById(R.id.quotationVersion);
        }
    }


    public QuotationHistoryAdepter(Activity ctx , ArrayList<QuotationHistoryModel> dataList) {
        this.dataList = dataList;
        this.ctx = ctx;
    }

    @Override
    public QuotationHistoryAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_quotations_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        QuotationHistoryModel modal = dataList.get(position);

        holder.etQuotationDate.setText(""+modal.date);
        holder.etInquiryDate.setText(""+modal.inquiryDate);
        holder.etAccountName.setText(""+modal.companyName);

        holder.etPreparedBy.setText(""+modal.fristName);
        holder.etClientName.setText(""+modal.clientName);
        holder.etClientEmail.setText(""+modal.clientEmail);
        holder.etClientMobile.setText(""+modal.clientContactNumber);
        holder.etMoveType.setText(""+modal.moveType);
        holder.tvTotal.setText("Total : "+modal.total);

        holder.quotationVersion.setText("Version No: "+modal.quotationVersion);

        holder.etOrigin.setText(""+modal.originAddressLine1+" "+modal.originAddressLine2+"\n"+modal.originCityName+"\n"+modal.originStateName);
        holder.etDestination.setText(""+modal.addressLine1+" "+modal.addressLine2+"\n"+modal.cityName+"\n"+modal.stateName);

        ArrayList<CommanModel> particulors = modal.particulars;

        for (int index = 0; index < particulors.size(); index++) {
            CommanModel tempData = particulors.get(index);
            View temp = ctx.getLayoutInflater().inflate(R.layout.comman_row,null);
            TextView itemName = temp.findViewById(R.id.item);
            TextView itemPrice = temp.findViewById(R.id.item1);
            itemName.setText(""+tempData.id);
            itemPrice.setText(""+tempData.name);
            holder.particularHolder.addView(temp);
        }

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
