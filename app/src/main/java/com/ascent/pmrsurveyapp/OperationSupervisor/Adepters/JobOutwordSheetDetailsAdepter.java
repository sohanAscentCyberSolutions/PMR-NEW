package com.ascent.pmrsurveyapp.OperationSupervisor.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.OperationSupervisor.Models.OutWordSheetDetailsModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;

import java.util.ArrayList;

public class JobOutwordSheetDetailsAdepter extends RecyclerView.Adapter<JobOutwordSheetDetailsAdepter.MyViewHolder> {

    private ArrayList<OutWordSheetDetailsModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;
    Comman cmn;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemNo, cRRef,articles,outwardSequnce;


        public MyViewHolder(View view) {
            super(view);

            itemNo = view.findViewById(R.id.itemNo);
            cRRef = (TextView) view.findViewById(R.id.cRRef);
            articles = (TextView) view.findViewById(R.id.articles);
            outwardSequnce = view.findViewById(R.id.outwardSequnce);
        }
    }


    public JobOutwordSheetDetailsAdepter(Activity ctx , ArrayList<OutWordSheetDetailsModel> dataList , ClickAdepterListener onClickListener) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
        this.cmn = new Comman(ctx);
    }

    @Override
    public JobOutwordSheetDetailsAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_outward_detail_sheet, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final OutWordSheetDetailsModel model = dataList.get(position);
        holder.itemNo.setText(""+model.itemNo);
        holder.cRRef.setText(""+model.crRef);
        holder.articles.setText(""+model.article);
        holder.outwardSequnce.setText(""+model.outwardSequence);
    }

    public interface ClickAdepterListener {
        void detailsClicked(View v, int position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
