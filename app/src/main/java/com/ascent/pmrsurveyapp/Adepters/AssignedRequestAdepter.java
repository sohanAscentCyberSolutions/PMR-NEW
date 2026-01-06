package com.ascent.pmrsurveyapp.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.Models.RequestsModel;
import com.ascent.pmrsurveyapp.R;

import java.util.ArrayList;

public class AssignedRequestAdepter extends RecyclerView.Adapter<AssignedRequestAdepter.MyViewHolder> {

    private ArrayList<RequestsModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;
    int type;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView inquiry, orderDate, surveyDate,assignedBy,moving;
        ImageButton menuBt,navigationBt,callBt;

        public MyViewHolder(View view) {
            super(view);
            inquiry = (TextView) view.findViewById(R.id.inquiry);
            orderDate = (TextView) view.findViewById(R.id.orderDate);
            surveyDate = (TextView) view.findViewById(R.id.surveyDate);
            menuBt = view.findViewById(R.id.menuBt);
            navigationBt = view.findViewById(R.id.navigationBt);
            callBt = view.findViewById(R.id.callBt);
            assignedBy = view.findViewById(R.id.assignedBy);
            moving = view.findViewById(R.id.moving);
        }
    }


    public AssignedRequestAdepter(Activity ctx , ArrayList<RequestsModel> dataList , ClickAdepterListener onClickListener , int type) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
        this.type = type;
    }

    @Override
    public AssignedRequestAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_assigned_requests, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final RequestsModel model = dataList.get(position);
        if (model.inquiryModel.account.isEmpty()){
            holder.inquiry.setText(""+model.inquiryModel.shipper.name+" (individual)");
        }else{
            holder.inquiry.setText(""+model.inquiryModel.shipper.name+" ("+model.inquiryModel.account +")");
        }

        if (!model.startDate.isEmpty()) {
            String dt = model.startDate;
            holder.orderDate.setText("" + dt);
        }else {
            holder.orderDate.setText("");
        }
        holder.surveyDate.setText(""+model.requestDate);
        holder.assignedBy.setText(""+model.createdBy);
        holder.moving.setText(""+model.inquiryModel.originAddress.city.name + " to "+model.inquiryModel.destinationAddress.city.name);

        holder.callBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.callClicked( v , position);
            }
        });

        holder.navigationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.navigationClicked( v , position);
            }
        });


        holder.menuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPopup(position,v,type);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.requestDetailsClicked( v , position);
            }
        });
    }



    void setUpPopup(int forPosition , View v , int type){
        PopupMenu popup = new PopupMenu(ctx, v);
        if (type == 1) {
            popup.getMenu().add("View Survey Report");
        }else{
           // popup.getMenu().add("Edit");
            popup.getMenu().add("Start Survey");
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()){
                    case "View Survey Report":
                        onClickListener.detailsClicked( v , forPosition);
                        break;
                    case "Edit":
                        onClickListener.editClicked( v , forPosition);
                        break;
                    case "Start Survey":
                        onClickListener.uploadReportClicked( v , forPosition);
                        break;
                }
                //Toast.makeText(context, "Some Text" + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popup.show();
    }


    public interface ClickAdepterListener {
        void detailsClicked(View v, int position);
        void callClicked(View v, int position);
        void navigationClicked(View v, int position);
        void requestDetailsClicked(View v, int position);
        void uploadReportClicked(View v, int position);
        void editClicked(View v, int position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
