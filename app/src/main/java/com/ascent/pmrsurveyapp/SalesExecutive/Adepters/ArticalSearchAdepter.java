package com.ascent.pmrsurveyapp.SalesExecutive.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.StandardItemsModel;

import java.util.ArrayList;

public class ArticalSearchAdepter extends RecyclerView.Adapter<ArticalSearchAdepter.MyViewHolder> {

    private ArrayList<StandardItemsModel> dataList;
    public ArticalAdepterListener onClickListener;
    Activity ctx;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;


        public MyViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.itemName);
        }
    }


    public ArticalSearchAdepter(Activity ctx , ArrayList<StandardItemsModel> dataList , ArticalAdepterListener onClickListener) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
    }

    @Override
    public ArticalSearchAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_suggestions, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final StandardItemsModel model = dataList.get(position);
        holder.itemName.setText(""+model.name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.itemSelected( v , position);
            }
        });
    }
    

    public interface ArticalAdepterListener {
        void itemSelected(View v, int position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
