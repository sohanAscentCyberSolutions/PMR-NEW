package com.ascent.pmrsurveyapp.OperationSupervisor.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobExecutionModel;
import com.ascent.pmrsurveyapp.R;

import java.util.ArrayList;

public class JobExecutionsAdepter extends RecyclerView.Adapter<JobExecutionsAdepter.MyViewHolder> {

    private ArrayList<JobExecutionModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView jonNumber, requestDate, shipper,goodsType,movementType,moveTo,volume;
        public ImageButton menuBt;


        public MyViewHolder(View view) {
            super(view);
            jonNumber = (TextView) view.findViewById(R.id.jonNumber);
            requestDate = (TextView) view.findViewById(R.id.requestDate);
            shipper = (TextView) view.findViewById(R.id.shipper);
            goodsType = view.findViewById(R.id.goodsType);
            movementType = view.findViewById(R.id.movementType);
            volume = view.findViewById(R.id.volume);
            moveTo = view.findViewById(R.id.moveTo);
            menuBt = view.findViewById(R.id.menuBt);
        }
    }


    public JobExecutionsAdepter(Activity ctx , ArrayList<JobExecutionModel> dataList , ClickAdepterListener onClickListener) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
    }

    @Override
    public JobExecutionsAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_job_executions, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final JobExecutionModel model = dataList.get(position);
        holder.jonNumber.setText(""+model.job.jobNumber);
        holder.requestDate.setText(""+model.createdOn);
        holder.shipper.setText(""+model.job.shipper.fullName);
        holder.goodsType.setText(""+model.job.inquiry.goodsType);
        holder.movementType.setText(""+model.job.inquiry.moveType);
        holder.moveTo.setText(""+model.job.originAddress.city.name+" To "+model.job.destinationAddress.city.name);
        holder.volume.setText(""+model.volume);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.detailsClicked( v , position);
            }
        });

        if (model.packingListCreated && model.loadingSheetCreated &&  model.documentUploaded){
            holder.menuBt.setVisibility(View.GONE);
        }else{
            holder.menuBt.setVisibility(View.VISIBLE);
        }

        holder.menuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPopup(position,v , model , holder);
            }
        });
    }


    void setUpPopup(int forPosition , View v , JobExecutionModel model , MyViewHolder holder){
        PopupMenu popup = new PopupMenu(ctx, v);

        if (model.requestLocation.equalsIgnoreCase("ORIGIN") && model.packingListCreated==false){
            popup.getMenu().add("New Packing List");
        }
        if (model.requestLocation.equalsIgnoreCase("ORIGIN") && model.loadingSheetCreated==false){
            popup.getMenu().add("New Loading Sheet");
        }
        if (model.requestLocation.equalsIgnoreCase("DESTINATION") && model.unloadingCompleted==false){
            popup.getMenu().add("New Unloading Sheet");
        }

        if (model.documentUploaded == false){
            popup.getMenu().add("Upload Document");
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()){
                    case "Upload Document":
                        onClickListener.uploadDocClicked( v , forPosition);
                        break;
                    case "New Packing List":
                        onClickListener.newPackingListClicked( v , forPosition);
                        break;
                    case "New Loading Sheet":
                        onClickListener.newLoadingSheetClicked( v , forPosition);
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
        void uploadDocClicked(View v, int position);
        void newPackingListClicked(View v, int position);
        void newLoadingSheetClicked(View v, int position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
