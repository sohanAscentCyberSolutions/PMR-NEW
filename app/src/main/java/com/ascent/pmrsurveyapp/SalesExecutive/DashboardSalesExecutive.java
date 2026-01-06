package com.ascent.pmrsurveyapp.SalesExecutive;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ascent.pmrsurveyapp.Adepters.MenuAdapter;
import com.ascent.pmrsurveyapp.Models.MenuModel;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.UI.Accounts;
import com.ascent.pmrsurveyapp.SalesExecutive.UI.AddAccount;
import com.ascent.pmrsurveyapp.SalesExecutive.UI.AddInquiry;
import com.ascent.pmrsurveyapp.SalesExecutive.UI.Inquiries;
import com.ascent.pmrsurveyapp.SalesExecutive.UI.Quotation;
import com.ascent.pmrsurveyapp.UI.LogIn;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.databinding.ActivityDashboardBinding;
import com.ascent.pmrsurveyapp.databinding.ActivityDashboardSalesExecutiveBinding;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class DashboardSalesExecutive extends AppCompatActivity implements DuoMenuView.OnMenuClickListener{

    TextView duo_view_footer_text1;
    Comman cmn;
    LinearLayout ll;
    JSONArray arr;
    List<MenuModel> menu_list = new ArrayList<>();
    CircleImageView profile_image;
    TextView username,email;
    ActivityDashboardSalesExecutiveBinding binding;
    Activity ctx;

    int type = 0;

    private MenuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;

    private ArrayList<String> mTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard_sales_executive);
        ctx = this;

        cmn = new Comman(ctx);

        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
        mViewHolder = new ViewHolder();

        handleMenu();
        handleToolbar();
        handleDrawer();

        ArrayList sliderlist = new ArrayList<SlideModel>();

        sliderlist.add(new SlideModel("https://www.pmrelocations.com/img/hero.jpg" , ScaleTypes.FIT));
        sliderlist.add(new SlideModel("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR1bNVXmD_CL7HB45ivtr2VaNo-Mj9gyrUruA&usqp=CAU" , ScaleTypes.FIT));
        sliderlist.add(new SlideModel("https://www.pmrelocations.com/img/hero2.jpg" , ScaleTypes.FIT));
        sliderlist.add(new SlideModel("https://www.pmrelocations.com/img/hero3.jpg" , ScaleTypes.FIT));

        binding.imageSlider.setImageList(sliderlist);

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

        binding.cardAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, Accounts.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        binding.cardInquiries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, Inquiries.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        binding.cardQuotations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, Quotation.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        binding.cardNewAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, AddAccount.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

     /*   binding.cardInternationalInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, AddInquiry.class).putExtra("isDomestic" , false));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });*/

        binding.cardDomesticInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, AddInquiry.class).putExtra("isDomestic" , true));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


    }

    @Override
    public void onFooterClicked() {

    }

    @Override
    public void onHeaderClicked() {

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
        menu_list.add(new MenuModel("accounts", "Accounts", "1", "", "", "", "", "", ""));
        menu_list.add(new MenuModel("inquiries", "Enquiries", "1", "", "", "", "", "", ""));
      //  menu_list.add(new MenuModel("tasks", "My Tasks", "1", "", "", "", "", "", ""));
      //  menu_list.add(new MenuModel("quotations", "Quotations", "1", "", "", "", "", "", ""));

        setTitle(menu_list.get(0).value);
        mMenuAdapter = new MenuAdapter(menu_list, ctx);
        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
        mMenuAdapter.setViewSelected(0, true);

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

        } else if (menu_list.get(position).key.matches("accounts")) {
            startActivity(new Intent(ctx, Accounts.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (menu_list.get(position).key.matches("inquiries")) {
            startActivity(new Intent(ctx, Inquiries.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (menu_list.get(position).key.matches("tasks")) {

        }else if (menu_list.get(position).key.matches("quotations")) {
            startActivity(new Intent(ctx, Quotation.class));
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