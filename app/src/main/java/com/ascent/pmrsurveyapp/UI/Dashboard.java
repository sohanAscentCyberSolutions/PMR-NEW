package com.ascent.pmrsurveyapp.UI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.ascent.pmrsurveyapp.DBHelper.CityModel;
import com.ascent.pmrsurveyapp.DBHelper.CountryModel;
import com.ascent.pmrsurveyapp.DBHelper.DatabaseHelper;
import com.ascent.pmrsurveyapp.DBHelper.ExcelImporter;
import com.ascent.pmrsurveyapp.DBHelper.StateModel;
import com.ascent.pmrsurveyapp.SalesExecutive.UI.AddInquiry;
import com.ascent.pmrsurveyapp.SalesExecutive.UI.Inquiries;
import com.ascent.pmrsurveyapp.SalesExecutive.UI.OfflineInquiries;
import com.ascent.pmrsurveyapp.SalesExecutive.UI.OfflineSurvey;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ascent.pmrsurveyapp.Adepters.AssignedRequestAdepter;
import com.ascent.pmrsurveyapp.Adepters.MenuAdapter;
import com.ascent.pmrsurveyapp.Models.MenuModel;
import com.ascent.pmrsurveyapp.Models.RequestsModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.UI.InquiryDetails;
import com.ascent.pmrsurveyapp.SalesExecutive.UpdateData;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.Utills.Parser;
import com.ascent.pmrsurveyapp.databinding.ActivityDashboardBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class Dashboard extends AppCompatActivity implements DuoMenuView.OnMenuClickListener , UpdateData {

    TextView duo_view_footer_text1;
    Comman cmn;
    LinearLayout ll;
    JSONArray arr;
    List<MenuModel> menu_list = new ArrayList<>();
    CircleImageView profile_image;
    TextView username,email;
    ActivityDashboardBinding binding;
    Activity ctx;

    int type = 0;

    public static UpdateData dataUpdator;

    private MenuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;

    private ArrayList<String> mTitles = new ArrayList<>();

    ArrayList<RequestsModel> dataListAll = new ArrayList<>();
    ArrayList<RequestsModel> dataList = new ArrayList<>();
    ArrayList<RequestsModel> dataListFilter = new ArrayList<>();
    private AssignedRequestAdepter mAdapter;

    public static ArrayList<androidx.fragment.app.Fragment> instanses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        ctx = this;

        dataUpdator = this;

        cmn = new Comman(ctx);

        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
        mViewHolder = new ViewHolder();

        handleMenu();
        handleToolbar();
        handleDrawer();

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);

        username.setText(""+cmn.getName() + " "+cmn.getLastName());
        email.setText(""+cmn.getUserName());


        duo_view_footer_text1 = findViewById(R.id.duo_view_footer_text1);
        duo_view_footer_text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(ctx, R.style.AlertDialogMaterialTheme)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cmn.RemoveAllSharedPreference();
                                cmn.showToast("Logged Out Successfully !!");
                                startActivity(new Intent(ctx, LogIn.class));
                                overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();


            }
        });
        ll = findViewById(R.id.ll);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  mViewHolder.mDuoDrawerLayout.closeDrawer();
                FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction1.replace(R.id.container, new ProfileTabs());
                fragmentTransaction1.commit(); */
            }
        });


        binding.etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dataListFilter.clear();
                String filterStr = binding.etFilter.getText().toString().toLowerCase();
                if (filterStr.isEmpty()){
                    dataListFilter.addAll(dataList);
                }else{
                    for(RequestsModel model : dataList){
                        if (model.startDate.toLowerCase().startsWith(filterStr) || model.requestDate.toLowerCase().startsWith(filterStr)  || model.inquiryModel.account.toLowerCase().startsWith(filterStr) || model.inquiryModel.shipper.name.toLowerCase().startsWith(filterStr)){
                            dataListFilter.add(model);
                        }
                    }
                }
                if (mAdapter != null){
                    mAdapter.notifyDataSetChanged();
                }
                Log.e("text changed" , "records found" + dataListFilter.size());
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx);
        binding.reclycalView.setLayoutManager(mLayoutManager);
        binding.reclycalView.setItemAnimator(new DefaultItemAnimator());


        binding.sagmentedGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(final int position) {
                // Handle stuff here
                Log.e("position" , ""+ position);

                if (position== 0){
                    type = 0;
                    updateData();
                }else {
                    type = 1;
                    updateData();
                }
            }
        });

        getData();

    }

    @Override
    public void onFooterClicked() {

    }

    @Override
    public void onHeaderClicked() {

    }

    @Override
    protected void onResume() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            new ExcelImporter(this, dbHelper, success -> {
                if (success) {
                   // Toast.makeText(this, "CSV imported successfully!", Toast.LENGTH_SHORT).show();
                } else {
                   // Toast.makeText(this, "CSV import failed!", Toast.LENGTH_SHORT).show();
                }
            }).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    private void handleMenu() {

        /*
        try {
            menu_list = new ArrayList<>();

            String menu_data = appPreferences.getLeftMenu();
            try {
                JSONArray arr = new JSONArray(menu_data);
                //arr = obj.getJSONArray("side_menu");
                menu_list.add(new MenuModel("dashboard", "Service", "1", "", "", "", "", "", ""));
                menu_list.add(new MenuModel("service", "Sales", "1", "", "", "", "", "", ""));
                for (int i = 0; i < arr.length(); i++) {

                    if (arr.getJSONObject(i).getString("view").matches("1")) {

                        if(arr.getJSONObject(i).getString("key").matches("call_center")){
                            menu_list.add(new MenuModel("cust_list", "Customer List", "1","","","","","",""));
                            //menu_list.add(new MenuModel("miss_call", "Missed Calls", "1","","","","","",""));
                            menu_list.add(new MenuModel("miss_call_history", "History", "1","","","","","",""));
                        } else {
                            menu_list.add(new MenuModel(arr.getJSONObject(i).getString("key"),
                                    arr.getJSONObject(i).getString("value"),
                                    arr.getJSONObject(i).getString("view"),
                                    arr.getJSONObject(i).getString("edit"),
                                    arr.getJSONObject(i).getString("delete"),
                                    arr.getJSONObject(i).getString("create"),
                                    arr.getJSONObject(i).getString("download"),
                                    arr.getJSONObject(i).getString("upload"),
                                    arr.getJSONObject(i).getString("icon")));
                        }


                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } */


        menu_list.add(new MenuModel("home", "Home", "1", "", "", "", "", "", ""));
        menu_list.add(new MenuModel("assigned", "Assigned", "1", "", "", "", "", "", ""));
        menu_list.add(new MenuModel("closed", "Closed", "1", "", "", "", "", "", ""));
        menu_list.add(new MenuModel("add inquiry", "Add New Inquiry", "1", "", "", "", "", "", ""));
        menu_list.add(new MenuModel("Offline Inquiries", "Offline Inquiries", "1", "", "", "", "", "", ""));
        menu_list.add(new MenuModel("inquiries", "Inquiries", "1", "", "", "", "", "", ""));
        menu_list.add(new MenuModel("Offline Survey", "Offline Survey", "1", "", "", "", "", "", ""));
        setTitle(menu_list.get(0).value);
        mMenuAdapter = new MenuAdapter(menu_list, ctx);
        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
        mMenuAdapter.setViewSelected(0, true);

    }

    @Override
    public void reloadTheData() {
        getData();
    }

    private class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }


    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        setTitle(menu_list.get(position).value);
        mMenuAdapter.setViewSelected(position, true);

        if (menu_list.get(position).key.matches("home")) {

        } else if (menu_list.get(position).key.matches("assigned")) {
            binding.sagmentedGroup.setPosition(0,true);
        } else if (menu_list.get(position).key.matches("inprocess")) {
        } else if (menu_list.get(position).key.matches("closed")) {
            binding.sagmentedGroup.setPosition(1,true);
        } else if (menu_list.get(position).key.matches("add inquiry")) {
            startActivity(new Intent(ctx, AddInquiry.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (menu_list.get(position).key.matches("inquiries")) {
            startActivity(new Intent(ctx, Inquiries.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }else if (menu_list.get(position).key.matches("Offline Inquiries")) {
            startActivity(new Intent(ctx, OfflineInquiries.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }else if (menu_list.get(position).key.matches("Offline Survey")){
            startActivity(new Intent(ctx, OfflineSurvey.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }


        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        if (type == 1){
            binding.sagmentedGroup.setPosition(0,true);
        }else{
            new MaterialAlertDialogBuilder(this, R.style.AlertDialogMaterialTheme)
                    .setTitle("Exit APP")
                    .setMessage("Are you sure you want to Exit?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }


    void updateData(){
        dataList.clear();
        dataListFilter.clear();
        for (int i = 0; i < dataListAll.size() ; i++) {
            if (type==0){
                if (dataListAll.get(i).status.equalsIgnoreCase("requested")){
                    dataList.add(dataListAll.get(i));
                }
            }else{
                if (!dataListAll.get(i).status.equalsIgnoreCase("requested")){
                    dataList.add(dataListAll.get(i));
                }
            }
        }
        if (type==0) {
            setTitle("Assigned Requests");
            if (dataList.size() == 0){
                binding.noRecored.setText("No Assigned Request Found !!");
                binding.noRecored.setVisibility(View.VISIBLE);
                binding.dataView.setVisibility(View.INVISIBLE);
            }else{
                binding.noRecored.setVisibility(View.GONE);
                binding.dataView.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (dataList.size() == 0){
                binding.noRecored.setText("No Closed Request Found !!");
                binding.noRecored.setVisibility(View.VISIBLE);
                binding.dataView.setVisibility(View.INVISIBLE);
            }else{
                binding.noRecored.setVisibility(View.GONE);
                binding.dataView.setVisibility(View.VISIBLE);
            }
            setTitle("Closed Requests");
        }

        dataListFilter.addAll(dataList);
        mAdapter = new AssignedRequestAdepter(ctx, dataListFilter, new AssignedRequestAdepter.ClickAdepterListener() {
            @Override
            public void detailsClicked(View v, int position) {
                Intent mainIntent = new Intent(ctx, InquiryDetails.class);
                mainIntent.putExtra("id" , dataListFilter.get(position).id);
                mainIntent.putExtra("isSurveyReport" , true);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void callClicked(View v, int position) {
                RequestsModel modal = dataListFilter.get(position);
                Uri number = Uri.parse("tel:"+modal.inquiryModel.shipper.contactNumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }

            @Override
            public void navigationClicked(View v, int position) {
                RequestsModel modal = dataListFilter.get(position);
                String map = "http://maps.google.co.in/maps?q="+modal.inquiryModel.originAddress.city.name+","+modal.inquiryModel.originAddress.state.name;
                String uri = String.format(Locale.ENGLISH, map);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);

            }

            @Override
            public void requestDetailsClicked(View v, int position) {
                Intent mainIntent = new Intent(ctx, RequestDetails.class);
                mainIntent.putExtra("id" , dataListFilter.get(position).id);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void uploadReportClicked(View v, int position) {
                Intent mainIntent = new Intent(ctx, SurveyReport.class);
                mainIntent.putExtra("id" , dataListFilter.get(position).inquiryModel.id);
                mainIntent.putExtra("surveyId" , dataListFilter.get(position).id);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void editClicked(View v, int position) {

            }
        } , type);
        binding.reclycalView.setAdapter(mAdapter);

    }

    void getData(){
        JSONObject parameters = new JSONObject();
        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String aResponse = msg.getData().getString("message");
                if ((null != aResponse)) {
                    try {
                        dataList.clear();
                        dataListFilter.clear();
                        dataListAll.clear();
                        JSONObject obj = new JSONObject(aResponse);
                        JSONArray array = obj.optJSONArray("contant");
                        for (int index = 0;index<array.length();index++){
                            RequestsModel modal = new Parser(ctx).parseRequest(array.optJSONObject(index));
                            dataListAll.add(modal);
                        }
                        updateData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                {
                    Toast.makeText(
                            ctx,
                            "Not Got Response From Server.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        String url = "";
        url = "surveyrequests?page=0&size=1500&searchFields=inquiry&searchText=&sortField=undefined&sortOrder=asc&type=MOVE_INQUIRY";
        HttpRequest request = new HttpRequest(url, parameters, handler, ctx);
        request.getAPI(true);
    }
}