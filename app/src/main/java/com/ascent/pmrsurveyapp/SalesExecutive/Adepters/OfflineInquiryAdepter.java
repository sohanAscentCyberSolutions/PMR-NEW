package com.ascent.pmrsurveyapp.SalesExecutive.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.InquiryModel;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.OfflineInquryModel;
import com.ascent.pmrsurveyapp.Utills.Comman;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OfflineInquiryAdepter extends RecyclerView.Adapter<OfflineInquiryAdepter.MyViewHolder> {

    private ArrayList<OfflineInquryModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;
    boolean isAction;
    Comman cmn;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView inqDate, shipperName, client,status,movement,goodsType;
        public ImageButton menuBt;
        public Button btSubmitInquery;


        public MyViewHolder(View view) {
            super(view);
            inqDate = (TextView) view.findViewById(R.id.inqDate);
            shipperName = (TextView) view.findViewById(R.id.shipperName);
            client = (TextView) view.findViewById(R.id.client);
            movement = view.findViewById(R.id.movement);
            goodsType = view.findViewById(R.id.goodsType);
            status = view.findViewById(R.id.status);
            menuBt = view.findViewById(R.id.menuBt);
            btSubmitInquery = view.findViewById(R.id.btSubmitInquery);
        }
    }


    public OfflineInquiryAdepter(Activity ctx , ArrayList<OfflineInquryModel> dataList , ClickAdepterListener onClickListener , boolean isAction) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
        this.isAction = isAction;
        this.cmn = new Comman(ctx);
    }

    @Override
    public OfflineInquiryAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_offline_inquiry, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final JSONObject obj;
        try {
            obj = new JSONObject(dataList.get(position).getdata());
            String name = obj.optJSONObject("shipper").optString("firstName") + obj.optJSONObject("shipper").optString("lastName");
            String accountName = obj.optJSONObject("account").optString("accountName");
            String inquiryDate = obj.optString("inquiryDate");
            String goodsType = obj.optString("goodsType");
            String moving = obj.optJSONObject("originAddress").optJSONObject("city").optString("name") + " to "+obj.optJSONObject("destinationAddress").optJSONObject("city").optString("name");
            holder.inqDate.setText("Enquiry Date : "+inquiryDate);
            holder.shipperName.setText(""+name);
            holder.goodsType.setText(""+goodsType);


            if (obj.optJSONObject("account").optString("id") == null){
                holder.client.setText("individual");
            }else{
                holder.client.setText(accountName);
            }

            holder.btSubmitInquery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.submitClicked(view , position);
                }
            });

            holder.movement.setText(moving);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickListener.detailsClicked( v , position);
//            }
//        });
//
//        holder.menuBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setUpPopup(position,v , model , holder);
//            }
//        });
    }


//    void setUpPopup(int forPosition , View v , InquiryModel model , MyViewHolder holder){
//        PopupMenu popup = new PopupMenu(ctx, v);
//        if ((new Comman(ctx).getUserType().equalsIgnoreCase(new Comman(ctx).userSalesExecutive) || new Comman(ctx).getUserType().equalsIgnoreCase(new Comman(ctx).userOperationSupervisor) || new Comman(ctx).getUserType().equalsIgnoreCase(new Comman(ctx).userSurveyor)) && model.assignedTo.id == null){
//            popup.getMenu().add("Assign");
//            if (cmn.getUserType().equalsIgnoreCase(cmn.userSurveyor) || new Comman(ctx).getUserType().equalsIgnoreCase(new Comman(ctx).userOperationSupervisor)){
//                popup.getMenu().add("Edit");
//                if (!model.documentUploaded){
//                    popup.getMenu().add("Upload Document");
//                }
//            }else{
//
//            }
//        }else{
//            if (model.status.equalsIgnoreCase("RECEIVED")){
//                popup.getMenu().add("Telephonic Survey");
//                popup.getMenu().add("Request Information");
//                popup.getMenu().add("Onsite Survey Request");
//            }else{
//                //popup.getMenu().add("New Quotation");
//            }
//        }
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getTitle().toString()){
//                    case "Assign":
//                        onClickListener.assignClickedClicked( v , forPosition);
//                        break;
//                    case "Telephonic Survey":
//                        onClickListener.telephonicSurveyClicked( v , forPosition);
//                        break;
//                    case "Request Information":
//                        onClickListener.requestInfoClicked( v , forPosition);
//                        break;
//                    case "Onsite Survey Request":
//                        onClickListener.newSurveyRequestClicked( v , forPosition);
//                        break;
//                    case "Upload Document":
//                        onClickListener.uploadDocumentClicked( v , forPosition);
//                        break;
//                    case "Edit":
//                        onClickListener.ediClicked( v , forPosition);
//                        break;
//                }
//                //Toast.makeText(context, "Some Text" + item.getTitle(), Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
//        popup.show();
//    }


    public interface ClickAdepterListener {
        void submitClicked(View v, int position);
//        void ediClicked(View v, int position);
//        void uploadDocumentClicked(View v, int position);
//        void assignClickedClicked(View v, int position);
//        void telephonicSurveyClicked(View v, int position);
//        void requestInfoClicked(View v, int position);
//        void newSurveyRequestClicked(View v, int position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
