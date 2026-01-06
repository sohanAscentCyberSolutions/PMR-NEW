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
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.QuotationModel;

import java.util.ArrayList;

public class QuotationAdepter extends RecyclerView.Adapter<QuotationAdepter.MyViewHolder> {

    private ArrayList<QuotationModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, preparedBy, account,client;
        ImageButton menuBt;

        public MyViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            preparedBy = (TextView) view.findViewById(R.id.preparedBy);
            account = (TextView) view.findViewById(R.id.account);
            client = view.findViewById(R.id.client);
            menuBt = view.findViewById(R.id.menuBt);
        }
    }


    public QuotationAdepter(Activity ctx , ArrayList<QuotationModel> dataList , ClickAdepterListener onClickListener) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
    }

    @Override
    public QuotationAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_quotations, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final QuotationModel model = dataList.get(position);
        if (!model.date.isEmpty()) {
            String dt = model.date;
            holder.date.setText("" + dt);
        }else {
            holder.date.setText("");
        }
        holder.preparedBy.setText(""+model.preparedBy.name);
        holder.account.setText(""+model.inquiry.account.name);
        holder.client.setText(""+model.inquiry.client.name);

        holder.menuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPopup(position,v);
            }
        });

    }



    void setUpPopup(int forPosition , View v){
        PopupMenu popup = new PopupMenu(ctx, v);
        popup.getMenu().add("Details");
        popup.getMenu().add("Inquiry Details");

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()){
                    case "Details":
                        onClickListener.detailsClicked( v , forPosition);
                        break;
                    case "Inquiry Details":
                        onClickListener.inquiryClicked( v , forPosition);
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
        void inquiryClicked(View v, int position);

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
