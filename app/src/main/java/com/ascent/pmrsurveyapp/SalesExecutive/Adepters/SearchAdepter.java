package com.ascent.pmrsurveyapp.SalesExecutive.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.Models.CommanModel;
import com.ascent.pmrsurveyapp.R;

import java.util.ArrayList;

public class SearchAdepter extends RecyclerView.Adapter<SearchAdepter.MyViewHolder> {

    private ArrayList<CommanModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;


        public MyViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.itemName);
        }
    }


    public SearchAdepter(Activity ctx , ArrayList<CommanModel> dataList , ClickAdepterListener onClickListener) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
    }

    @Override
    public SearchAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_suggestions, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CommanModel model = dataList.get(position);
        holder.itemName.setText(""+model.name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.itemSelected( v , position);
            }
        });
    }
    

    public interface ClickAdepterListener {
        void itemSelected(View v, int position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
