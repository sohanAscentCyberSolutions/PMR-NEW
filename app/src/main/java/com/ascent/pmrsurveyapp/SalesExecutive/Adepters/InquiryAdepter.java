package com.ascent.pmrsurveyapp.SalesExecutive.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel;
import com.ascent.pmrsurveyapp.Utills.Comman;

import java.util.ArrayList;

public class InquiryAdepter extends RecyclerView.Adapter<InquiryAdepter.MyViewHolder> {

    private ArrayList<InquiryModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;
    boolean isAction;
    Comman cmn;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView inqDate, shipperName, client,status,movement,goodsType;
        public ImageButton menuBt;


        public MyViewHolder(View view) {
            super(view);
            inqDate = (TextView) view.findViewById(R.id.inqDate);
            shipperName = (TextView) view.findViewById(R.id.shipperName);
            client = (TextView) view.findViewById(R.id.client);
            movement = view.findViewById(R.id.movement);
            goodsType = view.findViewById(R.id.goodsType);
            status = view.findViewById(R.id.status);
            menuBt = view.findViewById(R.id.menuBt);
        }
    }


    public InquiryAdepter(Activity ctx , ArrayList<InquiryModel> dataList , ClickAdepterListener onClickListener ,boolean isAction) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
        this.isAction = isAction;
        this.cmn = new Comman(ctx);
    }

    @Override
    public InquiryAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_inquiry, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final InquiryModel model = dataList.get(position);
        holder.inqDate.setText("Enquiry Date : "+model.date);
        holder.shipperName.setText(""+model.shipper.name);
        holder.goodsType.setText(""+model.goodsType);
        if (model.account.name == null){
            holder.client.setText("individual");
        }else{
            holder.client.setText(""+model.account.name);
        }
        if (new Comman(ctx).getUserType().equalsIgnoreCase(new Comman(ctx).userSalesExecutive)){
            holder.status.setText(""+model.status);
            if (model.assignedTo.id == null){
                holder.menuBt.setVisibility(View.VISIBLE);
            }else if (model.assignedTo.id.equalsIgnoreCase(cmn.getUserId())){
                holder.menuBt.setVisibility(View.VISIBLE);
            }else{
                holder.status.setText(""+model.status);
                holder.menuBt.setVisibility(View.GONE);
            }
        }else{
            if (model.status.equalsIgnoreCase("RECEIVED")){
                holder.menuBt.setVisibility(View.VISIBLE);
                holder.status.setText(""+model.status);
            }else{
                holder.status.setText(""+model.status);
                holder.menuBt.setVisibility(View.GONE);
            }
        }

        holder.movement.setText(""+model.originAddress.city.name+" To "+model.destinationAddress.city.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.detailsClicked( v , position);
            }
        });

        holder.menuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPopup(position,v , model , holder);
            }
        });
    }


    void setUpPopup(int forPosition , View v , InquiryModel model , MyViewHolder holder){
        PopupMenu popup = new PopupMenu(ctx, v);
        if ((new Comman(ctx).getUserType().equalsIgnoreCase(new Comman(ctx).userSalesExecutive) || new Comman(ctx).getUserType().equalsIgnoreCase(new Comman(ctx).userOperationSupervisor) || new Comman(ctx).getUserType().equalsIgnoreCase(new Comman(ctx).userSurveyor)) && model.assignedTo.id == null){
            popup.getMenu().add("Assign");
            if (cmn.getUserType().equalsIgnoreCase(cmn.userSurveyor) || new Comman(ctx).getUserType().equalsIgnoreCase(new Comman(ctx).userOperationSupervisor)){
                popup.getMenu().add("Edit");
                if (!model.documentUploaded){
                    popup.getMenu().add("Upload Document");
                }
            }else{

            }
        }else{
            if (model.status.equalsIgnoreCase("RECEIVED")){
                popup.getMenu().add("Telephonic Survey");
                popup.getMenu().add("Request Information");
                popup.getMenu().add("Onsite Survey Request");
            }else{
                //popup.getMenu().add("New Quotation");
            }
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()){
                    case "Assign":
                        onClickListener.assignClickedClicked( v , forPosition);
                        break;
                    case "Telephonic Survey":
                        onClickListener.telephonicSurveyClicked( v , forPosition);
                        break;
                    case "Request Information":
                        onClickListener.requestInfoClicked( v , forPosition);
                        break;
                    case "Onsite Survey Request":
                        onClickListener.newSurveyRequestClicked( v , forPosition);
                        break;
                    case "Upload Document":
                        onClickListener.uploadDocumentClicked( v , forPosition);
                        break;
                    case "Edit":
                        onClickListener.ediClicked( v , forPosition);
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
        void ediClicked(View v, int position);
        void uploadDocumentClicked(View v, int position);
        void assignClickedClicked(View v, int position);
        void telephonicSurveyClicked(View v, int position);
        void requestInfoClicked(View v, int position);
        void newSurveyRequestClicked(View v, int position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
