package com.ascent.pmrsurveyapp.OperationSupervisor.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.OperationSupervisor.Models.JobDocumentModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.Utills.Comman;

import java.util.ArrayList;

public class JobDocumentsAdepter extends RecyclerView.Adapter<JobDocumentsAdepter.MyViewHolder> {

    private ArrayList<JobDocumentModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;
    Comman cmn;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDocumentName, tvDesc;

        public ImageView ivDocument;

        public MyViewHolder(View view) {
            super(view);

            ivDocument = view.findViewById(R.id.ivDocument);
            tvDocumentName = (TextView) view.findViewById(R.id.tvDocumentName);
            tvDesc = (TextView) view.findViewById(R.id.tvDesc);
        }
    }


    public JobDocumentsAdepter(Activity ctx , ArrayList<JobDocumentModel> dataList , ClickAdepterListener onClickListener) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
        this.cmn = new Comman(ctx);
    }

    @Override
    public JobDocumentsAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_job_documets, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final JobDocumentModel model = dataList.get(position);
        holder.ivDocument.setImageBitmap(new Comman(ctx).getDecodedImage(model.filePath));
        holder.tvDocumentName.setText("File Name : "+model.fileName);
        holder.tvDesc.setText("Description : "+model.description);
    }



    public interface ClickAdepterListener {
        void detailsClicked(View v, int position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
