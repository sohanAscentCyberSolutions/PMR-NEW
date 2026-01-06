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
import com.ascent.pmrsurveyapp.SalesExecutive.Modals.AccountModel;

import java.util.ArrayList;

public class AccountsAdepter extends RecyclerView.Adapter<AccountsAdepter.MyViewHolder> {

    private ArrayList<AccountModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;
    int type;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, email, contactNo,state,city,address;
        ImageButton menuBt;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            email = (TextView) view.findViewById(R.id.email);
            contactNo = (TextView) view.findViewById(R.id.contactNo);
            city = view.findViewById(R.id.city);
            state = view.findViewById(R.id.state);
            address = view.findViewById(R.id.address);
            menuBt = view.findViewById(R.id.menuBt);
        }
    }


    public AccountsAdepter(Activity ctx , ArrayList<AccountModel> dataList , ClickAdepterListener onClickListener) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
        this.type = type;
    }

    @Override
    public AccountsAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_accounts, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final AccountModel model = dataList.get(position);
        holder.name.setText(""+model.companyName);
        holder.email.setText(""+model.email);
        holder.contactNo.setText(""+model.mobile);
        holder.state.setText(""+model.address.state.name);
        holder.city.setText(""+model.address.city.name);
        holder.address.setText(""+model.address.addressLine1);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.detailsClicked( v , position);
            }
        });

        holder.menuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPopup(position,v);
            }
        });

    }


    void setUpPopup(int forPosition , View v){
        PopupMenu popup = new PopupMenu(ctx, v);
        popup.getMenu().add("Delete");
        popup.getMenu().add("Edit");

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()){
                    case "Delete":
                        onClickListener.deleteClicked( v , forPosition);
                        break;
                    case "Edit":
                        onClickListener.editClicked( v , forPosition);
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
        void deleteClicked(View v, int position);
        void editClicked(View v, int position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
