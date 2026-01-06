package com.ascent.pmrsurveyapp.OperationSupervisor.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.OperationSupervisor.Models.PackingItemModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;

import java.util.ArrayList;

public class JobInwordSheetAdepter extends RecyclerView.Adapter<JobInwordSheetAdepter.MyViewHolder> {

    private ArrayList<PackingItemModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;
    Comman cmn;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView crRef, articles,itemNo;


        public MyViewHolder(View view) {
            super(view);

            itemNo = view.findViewById(R.id.itemNo);
            crRef = (TextView) view.findViewById(R.id.crRef);
            articles = (TextView) view.findViewById(R.id.articles);
        }
    }


    public JobInwordSheetAdepter(Activity ctx , ArrayList<PackingItemModel> dataList , ClickAdepterListener onClickListener) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
        this.cmn = new Comman(ctx);
    }

    @Override
    public JobInwordSheetAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_inword_sheet, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final PackingItemModel model = dataList.get(position);
        holder.crRef.setText(""+model.crRef);
        holder.articles.setText(""+model.article);
        holder.itemNo.setText(""+model.itemNo);
    }



    public interface ClickAdepterListener {
        void detailsClicked(View v, int position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
