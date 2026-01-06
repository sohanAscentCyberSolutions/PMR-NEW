package com.ascent.pmrsurveyapp.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Toast;

import com.ascent.pmrsurveyapp.OperationSupervisor.DashboardOS;
import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.SalesExecutive.DashboardSalesExecutive;
import com.ascent.pmrsurveyapp.Utills.Comman;
import com.ascent.pmrsurveyapp.Utills.HttpRequest;
import com.ascent.pmrsurveyapp.databinding.ActivityLogInBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LogIn extends AppCompatActivity {

    ActivityLogInBinding binding;
    Activity ctx;
    Comman cmn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in);
        ctx = this;
        cmn = new Comman(ctx);

        binding.usernameEt.setText("kdsurveyor@gmail.com");
        binding.passwordEt.setText("Pmr@12345");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = binding.usernameEt.getText().toString();
                String password = binding.passwordEt.getText().toString();

                if(userName.isEmpty()){
                    cmn.showToast("Please Enter User Name");
                }else if(password.isEmpty()){
                    cmn.showToast("Please Enter Password");
                }else{
                    if (cmn.checkNetworkConnection()){
                        logInNow(userName,password);
                    }else {
                        cmn.showErrorToast("No internet connection found Please check your internet connection");
                    }


                }
            }
        });
        binding.usernameEt.setMovementMethod(LinkMovementMethod.getInstance());
        binding.passwordEt.setMovementMethod(LinkMovementMethod.getInstance());

        if (Build.MODEL.contains("SM-J8")){ binding.usernameEt.setOnLongClickListener(new View.OnLongClickListener() { @Override public boolean onLongClick(View view) { return true; } }); }
        if (Build.MODEL.contains("SM-J8")){ binding.passwordEt.setOnLongClickListener(new View.OnLongClickListener() { @Override public boolean onLongClick(View view) { return true; } }); }

        binding.forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(getApplicationContext(),ForgotPassword.class));
                // overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    private void logInNow(String uname , String pass) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userName" , uname );
            parameters.put("password" , pass );

            Handler handler = new Handler(){

                public void handleMessage(Message msg) {

                    String aResponse = msg.getData().getString("message");

                    if ((null != aResponse)) {
                        try {
                            JSONObject objectRes = new JSONObject(aResponse);

                            String token = objectRes.optString("authToken");
                            String userName = objectRes.optString("userName");
                            String name = objectRes.optString("firstName");
                            String lname = objectRes.optString("lastName");
                            String logo = objectRes.optString("profilePicPath");
                            String id = objectRes.optString("id");
                            String tokenType = objectRes.optString("tokenType");
                            JSONArray roles = objectRes.optJSONArray("roles");
                            String role = roles.optJSONObject(0).getString("name");
                            cmn.setIsLogged(true);
                            cmn.setToken(token);
                            cmn.setLogo(logo);
                            cmn.setUserName(userName);
                            cmn.setTokenType(tokenType);
                            cmn.setUserType(role);
                            cmn.setName(name);
                            cmn.setLastName(lname);
                            cmn.setUserId(id);


                            if (role.equalsIgnoreCase(cmn.userSalesExecutive) || role.equalsIgnoreCase(cmn.userPricingExecutive)){
                                startActivity(new Intent(LogIn.this, DashboardSalesExecutive.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }else if (role.equalsIgnoreCase(cmn.userOperationSupervisor)){
                                startActivity(new Intent(LogIn.this, DashboardOS.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }else{
                                startActivity(new Intent(LogIn.this,Dashboard.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            cmn.showToast("Invalid Response from server");
                        }
                    } else
                    {
                        // ALERT MESSAGE
                        Toast.makeText(
                                LogIn.this,
                                "Not Got Response From Server.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };

            HttpRequest request = new HttpRequest("login" , parameters , handler , LogIn.this);
            request.postAPILogIn();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
        System.exit(0);
    }

}