package com.ascent.pmrsurveyapp.Utills;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ascent.pmrsurveyapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Comman {

    private static final String APP_SHARED_PREFS = "PMR_SURVEY";
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    public String userDistributor = "retailer";
    public int REQUEST_CODE = 1234;
    public String userOperationSupervisor =  "ROLE_OPERATION_SUPERVISOR";
    public String userSurveyor =  "ROLE_SURVEYOR";
    public String userSalesExecutive =  "ROLE_SALES_EXECUTIVE";
    public String userPricingExecutive =  "ROLE_PRICING_EXECUTIVE";
    public int typMyorders =  0;
    public int typRecievedorders =  1;
    public int typPlacedorders =  2;
    public int typEdit =  1;
    public int typDetail =  2;
    public int typAdd =  3;
    public int typVerify =  4;
    public String oneLevelSetting = "ONE_LEVEL_APPROVAL";
    public String twoLevelSetting = "TWO_LEVEL_APPROVAL";
    Dialog dialog;

    public String pageDefault =  "DEFAULT";
    public String pageUsers =  "USER";
    public String pageRoles =  "ROLE";
    public String pageApprovals =  "APPROVALS";
    public String pageRetailers =  "RETAILERS";
    public String pageNewFromRetailes =  "NEW_FROM_RETAILER";
    public String pageNewToDistributor =  "NEW_TO_DISTRIBUTOR";
    public String pagePlacedOrder =  "PLACED_ORDER";
    public String pageReceivedOrder =  "RECEIVED_ORDER";
    public String pageMyOrders =  "MY_ORDER";
    public String pageNewToBrand =  "NEW_TO_BRAND";
    Context ctx;


    public Comman(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
        this.ctx = context;
    }


    public  void setUser(String value) {
        prefsEditor.putString("userType", value);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public  String getUser(Context con) {
        SharedPreferences sharedPreferences = con.getSharedPreferences(
                "sfa", 0);
        String value = sharedPreferences.getString("userType", "");
        return value;

    }
    public void RemoveAllSharedPreference() {
        prefsEditor.clear();
        prefsEditor.apply();
        prefsEditor.commit();
    }


    public void showToast(String msg){
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View layout = inflater.inflate(R.layout.customtoast,
                null);
        // set a message
        TextView text = (TextView) layout.findViewById(R.id.msgToast);
        text.setText(msg);

        // Toast...
        Toast toast = new Toast(ctx);
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public String getToken() {
        return appSharedPrefs.getString("token", " ");
    }

    public  void setCredientialName(String value) {
        prefsEditor.putString("CredientialName", value);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public  void setCredientialPass(String value) {
        prefsEditor.putString("CredientialPass", value);
        prefsEditor.apply();
        prefsEditor.commit();
    }
    public  String getCredientialName() {
        return appSharedPrefs.getString("CredientialName", "");

    }
    public  String getCredientialPass() {
        return appSharedPrefs.getString("CredientialPass", "");

    }


    public void setToken(String token) {
        prefsEditor.putString("token", token);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public void setLogo(String logo) {
        prefsEditor.putString("logo", logo);
        prefsEditor.apply();
        prefsEditor.commit();
    }
    public String getLogo() {
        return appSharedPrefs.getString("logo", " ");
    }

    public void setTokenType(String token) {
        prefsEditor.putString("tokenType", token);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy ", cal).toString();
        return date;
    }

    public String getDateTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy HH:mm aa", cal).toString();
        return date;
    }

    public String getTokenType() {
        return appSharedPrefs.getString("tokenType", " ");
    }

    public String getUserId() {
        return appSharedPrefs.getString("userId", " ");
    }

    public void setUserId(String id) {
        prefsEditor.putString("userId", id);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public void setUserType(String id) {
        prefsEditor.putString("userType", id);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public String getUserType() {
        return appSharedPrefs.getString("userType", "");
    }


    public String getName() {
        return appSharedPrefs.getString("name", " ");
    }

    public void setName(String id) {
        prefsEditor.putString("name", id);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public String getLastName() {
        return appSharedPrefs.getString("lName", " ");
    }

    public void setLastName(String id) {
        prefsEditor.putString("lName", id);
        prefsEditor.apply();
    }
   /* public void setPrivileges(ArrayList<MenuModel> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditor.putString("privileges", json);
        prefsEditor.commit();
        prefsEditor.apply();
    }
    public ArrayList<MenuModel> getPrivileges() {
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("privileges", "");
        Type type = new TypeToken<List<MenuModel>>(){}.getType();
        ArrayList<MenuModel> preList = gson.fromJson(json, type);
        return preList;
    }

    public ArrayList<PrivilagesModel> getPrivilegesfor(String forStr) {
        ArrayList<MenuModel> list = getPrivileges();
        ArrayList<PrivilagesModel> listPrivilages = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MenuModel mod = list.get(i);
            for (int j = 0; j < mod.subGroups.size(); j++) {
                SubMenuModel mod1 = mod.subGroups.get(j);
                if (forStr.equalsIgnoreCase(mod1.name)){
                    listPrivilages = mod1.privilege;
                    break;
                }
            }
        }
        return listPrivilages;
    }  */
    public void printLog(String msg) {
        Log.e("Custom log ===" , msg);
    }
    public String getProfilePic() {
        return appSharedPrefs.getString("ProfilePic", " ");
    }

    public void setProfilePic(String id) {
        prefsEditor.putString("ProfilePic", id);
        prefsEditor.apply();
    }

    public String getUserName() {
        return appSharedPrefs.getString("UserName", " ");
    }

    public void setUserName(String id) {
        prefsEditor.putString("UserName", id);
        prefsEditor.apply();
    }

    public String getContactNo() {
        return appSharedPrefs.getString("ContactNo", " ");
    }

    public void setContactNo(String id) {
        prefsEditor.putString("ContactNo", id);
        prefsEditor.commit();
    }
    public  void SetUser(Context con, String value) {
// save the data
        SharedPreferences preferences = con.getSharedPreferences(
                "sfa", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", value);
        editor.commit();
    }

    public  void setTenant(String value) {
        prefsEditor.putString("tenant", value);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    /**************************************************** get shared preferences ***************************************************/

    public  String getTenant() {
        return appSharedPrefs.getString("tenant", "");

    }

    public Boolean getIsLogged() {
        return appSharedPrefs.getBoolean("IsLogged", false);
    }

    public void setIsLogged(Boolean id) {
        prefsEditor.putBoolean("IsLogged", id);
        prefsEditor.apply();
    }
    public void showCommanAlert(String msg) {
        final Dialog d = new Dialog(ctx);
        d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFade;
        d.setContentView(R.layout.successwithok);
        TextView Labeltitle = d.findViewById(R.id.Labeltitle);
        Labeltitle.setText(msg);
        Button btOkk =  d.findViewById(R.id.btokk);
        btOkk.setText("OK");
        btOkk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
        d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    public void showToastAlert(String msg) {
        final Dialog d = new Dialog(ctx);
        d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFade;
        d.setContentView(R.layout.custom_toast_dialog);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView Labeltitle = d.findViewById(R.id.tvText);
        Labeltitle.setText(msg);
        Handler hand = new Handler();

        try {
            hand.postDelayed(new Runnable() {
                @Override
                public void run() {
                    d.dismiss();
                }
            }, 1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        d.show();

    }


    public boolean checkNetworkConnection() {
        int i = 1;
        boolean flag = true;
        ConnectivityManager connectivity = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo localNetworkInfo1 = connectivity.getNetworkInfo(i);
            NetworkInfo localNetworkInfo2 = connectivity.getActiveNetworkInfo();
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            System.out.println("wifi" + localNetworkInfo1.isAvailable());
            System.out.println("info" + localNetworkInfo2);

            if (((localNetworkInfo2 == null) || (!localNetworkInfo2
                    .isConnected())) && (!localNetworkInfo1.isAvailable()))
                i = 0;
            if (info != null) {
                for (int j = 0; j < info.length; j++)
                    if (info[j].getState() == NetworkInfo.State.CONNECTED) {
                        i = 1;
                        break;
                    } else
                        i = 0;
            }

        } else
            i = 0;

        if (i == 0)
            flag = false;
        if (i == 1)
            flag = true;

        return flag;
    }

    public boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public boolean isGSTINlValid(String email) {
        boolean isValid = false;
        String expression = "[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9]{1}[A-Z]{2}";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
/*

    public void showDatePicker(final TextInputEditText editText , Date minDate){
        printLog("min date "+minDate);
        final Calendar calendar = Calendar.getInstance();

        calendar.setTime(minDate);

        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);

        int dd = calendar.get(Calendar.DAY_OF_MONTH);
       DatePickerDialog datePicker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String mon = "";
                monthOfYear = monthOfYear + 1;
                if (monthOfYear<10){
                    mon = "0"+monthOfYear;
                }else{
                    mon = "" + monthOfYear;
                }

                String dy = "";
                if (dayOfMonth<10){
                    dy = "0"+dayOfMonth;
                } else{
                    dy = "" + dayOfMonth;
                }

                String date = ""+String.valueOf(dy) +"-"+String.valueOf(mon)
                        +"-"+String.valueOf(year);

                editText.setText(date);
            }
        }, yy, mm, dd);

        try{
            long minDateInMilliSeconds = minDate.getTime();
           // datePicker.getDatePicker().setCalendarViewShown(false);
            datePicker.getDatePicker().setMinDate(minDateInMilliSeconds);
            datePicker.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
*/



    public void showDatePicker(final TextInputEditText editText , Date minDate){
        printLog("min date "+minDate);
        final Calendar calendar = Calendar.getInstance();

        calendar.setTime(minDate);

        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);

        int dd = calendar.get(Calendar.DAY_OF_MONTH);
       DatePickerDialog datePicker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String mon = "";
                monthOfYear = monthOfYear + 1;
                if (monthOfYear<10){
                    mon = "0"+monthOfYear;
                }else{
                    mon = "" + monthOfYear;
                }

                String dy = "";
                if (dayOfMonth<10){
                    dy = "0"+dayOfMonth;
                } else{
                    dy = "" + dayOfMonth;
                }

                String date = ""+String.valueOf(dy) +"-"+String.valueOf(mon)
                        +"-"+String.valueOf(year);

                editText.setText(date);
            }
        }, yy, mm, dd);
       try{
            long minDateInMilliSeconds = minDate.getTime();
            // datePicker.getDatePicker().setCalendarViewShown(false);
            datePicker.getDatePicker().setMinDate(minDateInMilliSeconds);
            datePicker.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void showDatePicker(final TextInputEditText editText){
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String mon = "";
                monthOfYear = monthOfYear + 1;
                if (monthOfYear<10){
                    mon = "0"+monthOfYear;
                }else{
                    mon = "" + monthOfYear;
                }

                String dy = "";
                if (dayOfMonth<10){
                    dy = "0"+dayOfMonth;
                } else{
                    dy = "" + dayOfMonth;
                }

                String date = ""+String.valueOf(dy) +"-"+String.valueOf(mon)
                        +"-"+String.valueOf(year);

                editText.setText(date);
            }
        }, yy, mm, dd);
        datePicker.show();
    }

    public void showDatePicker(final TextInputEditText editText , Long timeStamp){
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String mon = "";
                monthOfYear = monthOfYear + 1;
                if (monthOfYear<10){
                    mon = "0"+monthOfYear;
                }else{
                    mon = "" + monthOfYear;
                }

                String dy = "";
                if (dayOfMonth<10){
                    dy = "0"+dayOfMonth;
                } else{
                    dy = "" + dayOfMonth;
                }

                String date = ""+String.valueOf(dy) +"-"+String.valueOf(mon)
                        +"-"+String.valueOf(year);

                editText.setText(date);
            }
        }, yy, mm, dd);
        datePicker.getDatePicker().setCalendarViewShown(false);
        datePicker.getDatePicker().setMinDate(timeStamp);
        datePicker.show();
    }

    public void showTimePicker(final TextInputEditText editText){
        TimePickerDialog timePickerDialog = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
               int hour = hourOfDay;
               int minutes = minute;
                String timeSet = "";
                if (hour > 12) {
                    hour -= 12;
                    timeSet = "PM";
                } else if (hour == 0) {
                    hour += 12;
                    timeSet = "AM";
                } else if (hour == 12){
                    timeSet = "PM";
                }else{
                    timeSet = "AM";
                }

                String min = "";
                if (minutes < 10)
                    min = "0" + minutes ;
                else
                    min = String.valueOf(minutes);

                //Append in a StringBuilder
                String aTime = new StringBuilder().append(hour).append(':')
                        .append(min ).append(" ").append(timeSet).toString();

                boolean isPM = (hourOfDay >= 12);
                String aTime1  = String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM");

//                String finalTime = "";
//                if (hourOfDay <10  && minute < 10){
//                    finalTime = "0"+hourOfDay+":0"+minute;
//                }else if (hourOfDay <10  && minute > 10){
//                    finalTime = "0"+hourOfDay+":"+minute;
//                }else if (hourOfDay >10  && minute < 10 || minute == 0){
//                    finalTime = ""+hourOfDay+":0"+minute;
//                }else{
//                    finalTime = ""+hourOfDay+":"+minute;
//                }
                   editText.setText(aTime1);
            }
        } , 0 ,0 ,false );
        timePickerDialog.show();

    }


    public void showTimePicker24(final TextInputEditText editText){
        TimePickerDialog timePickerDialog = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                int hour = hourOfDay;
//                int minutes = minute;
//                String timeSet = "";
//                if (hour > 12) {
//                    hour -= 12;
//                    timeSet = "PM";
//                } else if (hour == 0) {
//                    hour += 12;
//                    timeSet = "AM";
//                } else if (hour == 12){
//                    timeSet = "PM";
//                }else{
//                    timeSet = "AM";
//                }
//
//                String min = "";
//                if (minutes < 10)
//                    min = "0" + minutes ;
//                else
//                    min = String.valueOf(minutes);

                //Append in a StringBuilder
//                String aTime = new StringBuilder().append(hour).append(':')
//                        .append(min ).append(" ").append(timeSet).toString();
//
//                boolean isPM = (hourOfDay >= 12);
//                String aTime1  = String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM");

                String finalTime = "";
                if (hourOfDay <10  && minute < 10){
                    finalTime = "0"+hourOfDay+":0"+minute;
                }else if (hourOfDay <10  && minute > 10){
                    finalTime = "0"+hourOfDay+":"+minute;
                }else if (hourOfDay >10  && minute < 10 || minute == 0){
                    finalTime = ""+hourOfDay+":0"+minute;
                }else{
                    finalTime = ""+hourOfDay+":"+minute;
                }
                editText.setText(finalTime);
            }
        } , 0 ,0 ,false );
        timePickerDialog.show();

    }


    public void showDatePicker(final TextView textView){
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH) + 1;
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String mon = "";
                if (monthOfYear<10){
                    mon = "0"+monthOfYear;
                }else{
                    mon = "" + monthOfYear;
                }

                String dy = "";
                if (dayOfMonth<10){
                    dy = "0"+dayOfMonth;
                }else{
                    dy = "" + dayOfMonth;
                }

                String date = ""+String.valueOf(dy) +"-"+String.valueOf(mon)
                        +"-"+String.valueOf(year);
                textView.setText(date);
            }
        }, yy, mm, dd);
        datePicker.show();
    }
    public void showDatePickerYYMMDD(final TextView textView){
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH) + 1;
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String mon = "";
                if (monthOfYear<10){
                    mon = "0"+monthOfYear;
                }else{
                    mon = "" + monthOfYear;
                }

                String dy = "";
                if (dayOfMonth<10){
                    dy = "0"+dayOfMonth;
                }else{
                    dy = "" + dayOfMonth;
                }

                String date = ""+String.valueOf(year) +"-"+String.valueOf(mon)
                        +"-"+String.valueOf(dy);
                textView.setText(date);
            }
        }, yy, mm, dd);
        datePicker.show();
    }

    public void showErrorToast(String msg){
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View layout = inflater.inflate(R.layout.customtoast,
                null);
        // set a message
        TextView text = (TextView) layout.findViewById(R.id.msgToast);
        text.setText(msg);
        text.setTextColor( ctx.getResources().getColor(android.R.color.holo_red_dark));
        Toast toast = new Toast(ctx);
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    String checkForNull(String text){
        String rtn;
        if (text.isEmpty()){
            rtn = null;
        }else{
            rtn = text;
        }
        return rtn;
    }

    public Bitmap getDecodedImage(String encodedImage){

        String trimmedString = encodedImage.replace("data:image/jpg;base64,","");
        String trimmedString1 = trimmedString.replace("data:image/png;base64,","");
        Log.e("encodedImage" , trimmedString1);
        byte[] decodedString = Base64.decode(trimmedString1.trim(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return  decodedByte;
    }

    public void showProgressDialog(){
        dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
    }

    public void hideProgressDialog(){
        dialog.dismiss();
    }

    public String replaceNull(String str){
        if (str == null)
            return "";
        if (str.equalsIgnoreCase("null")){
            return "";
        }else{
            return str;
        }
    }

    public String parseImagetoBase64(Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String b = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return b;
    }
    public  String getBase64FromPath(String path) {
        String base64 = "";
        try {/*from w  w w.j a v  a2 s  .  c  om*/
            File file = new File(path);

            Log.e("selected file name = " , ""+file.getName());

            byte[] buffer = new byte[(int) file.length() + 100];
            @SuppressWarnings("resource")
            int length = new FileInputStream(file).read(buffer);
            base64 = Base64.encodeToString(buffer, 0, length,
                    Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }

    public  String getBase64FromBitmap(Bitmap image) {
        String base64 = "";
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }

    public String getReverseDate(String input) {
        String dateStr = "";
        printLog(input);
        String[] dateArr = input.split("-");
        try {
            for (int i = dateArr.length - 1; i >= 0; i--) {
                dateStr =  dateStr.concat(dateArr[i]);
                printLog(dateArr[i]);
                if (i != 0) {
                    dateStr = dateStr.concat("-");
                    printLog("-");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        dateStr = dateStr.replace(" " , "");
        printLog(dateStr);
        return dateStr;
    }

}
