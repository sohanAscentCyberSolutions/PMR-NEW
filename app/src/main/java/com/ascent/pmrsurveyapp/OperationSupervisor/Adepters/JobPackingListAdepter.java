package com.ascent.pmrsurveyapp.OperationSupervisor.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobExecutionModel;
import com.ascent.pmrsurveyapp.OperationSupervisor.Models.PackingItemModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;

import java.util.ArrayList;

public class JobPackingListAdepter extends RecyclerView.Adapter<JobPackingListAdepter.MyViewHolder> {

    private ArrayList<PackingItemModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;
    Comman cmn;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView crRef, articles,packedBy,value,conditionAtOrigin,volume,itemNo;
        public LinearLayout layDetails;
        public ImageView ivAttachment;
        public CheckBox cbLoading,cbInward,cbOutwardg,cbUnloading;


        public MyViewHolder(View view) {
            super(view);

            itemNo = view.findViewById(R.id.itemNo);
            crRef = (TextView) view.findViewById(R.id.crRef);
            layDetails =view.findViewById(R.id.layDetails);
            articles = (TextView) view.findViewById(R.id.articles);
            packedBy = view.findViewById(R.id.packedBy);
            value = view.findViewById(R.id.value);
            conditionAtOrigin = view.findViewById(R.id.conditionAtOrigin);
            ivAttachment = view.findViewById(R.id.ivAttachment);
            cbLoading = view.findViewById(R.id.cbLoading);
            cbInward = view.findViewById(R.id.cbInward);
            cbOutwardg = view.findViewById(R.id.cbOutwardg);
            cbUnloading = view.findViewById(R.id.cbUnloading);
        }
    }


    public JobPackingListAdepter(Activity ctx , ArrayList<PackingItemModel> dataList , ClickAdepterListener onClickListener) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
        this.cmn = new Comman(ctx);
    }

    @Override
    public JobPackingListAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_packing_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final PackingItemModel model = dataList.get(position);
        holder.crRef.setText(""+model.crRef);
        holder.articles.setText(""+model.article);
        holder.packedBy.setText(""+model.packedBy);
        holder.value.setText(""+cmn.replaceNull(model.value));
        holder.conditionAtOrigin.setText(""+cmn.replaceNull(model.conditionAtOrigin));
        holder.itemNo.setText(""+model.itemNo);


        if (model.loadingDone){
            holder.cbLoading.setChecked(true);
        }else{
            holder.cbLoading.setChecked(false);
        }

        if (model.inwardDone){
            holder.cbInward.setChecked(true);
        }else{
            holder.cbInward.setChecked(false);
        }


        if (model.outwardDone){
            holder.cbOutwardg.setChecked(true);
        }else{
            holder.cbOutwardg.setChecked(false);
        }

        if (model.unloadingDone){
            holder.cbUnloading.setChecked(true);
        }else{
            holder.cbUnloading.setChecked(false);
        }


        holder.ivAttachment.setImageBitmap(new Comman(ctx).getDecodedImage(model.filePath));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onClickListener.detailsClicked( v , position);
                if (holder.layDetails.getVisibility() == View.VISIBLE){
                    holder.layDetails.setVisibility(View.GONE);
                }else{
                    holder.layDetails.setVisibility(View.VISIBLE);
                }
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
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
