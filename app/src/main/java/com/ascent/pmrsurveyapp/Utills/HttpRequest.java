package com.ascent.pmrsurveyapp.Utills;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.ascent.pmrsurveyapp.R;
import com.ascent.pmrsurveyapp.UI.LogIn;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpRequest {


  //  String url= "http://103.224.246.35:8080/web/"; //production
     String url= "http://103.224.246.26:8080/web/"; // QA
     public static String imageUrl = "http://103.224.246.35:8080/web/product/photo/"; // production
    // public static String pdfDownloadUrl = "http://103.224.246.35:8080/web/"; //production
    public static String pdfDownloadUrl = "http://103.224.246.26:8080/web/"; // QA
   // public static String imageUrl = "http://103.224.246.37:8080/web/product/photo/"; // QA
    public static String imageUrlCategory = "http://103.224.246.37:8080/web/category/photo/"; // QA
    JSONObject parameters ;
    JSONArray parametersArray ;
    Handler handler;
    Activity ctx;


    public HttpRequest(String url, JSONObject parameters , Handler handler, Activity ctx){
        this.url = this.url.concat(url);
        this.parameters = parameters;
        this.handler = handler;
        this.ctx = ctx;
    }

    public HttpRequest(String url, JSONArray parameters , Handler handler, Activity ctx){
        this.url = this.url.concat(url);
        this.parametersArray = parameters;
        this.handler = handler;
        this.ctx = ctx;
    }

    public   void postAPI(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(600, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserId();

        Log.e("url----->" , url);
        Log.e("token----->" , token);
        Log.e("id----->" , id);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("ascent-pmr-api-token", token)
                .addHeader("X-ASCENT-USERID", id)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    dialog.dismiss();
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    dialog.dismiss();
                    showLogOutDialog();
                }else if (statusCode == 400){
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                dialog.dismiss();
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Log.e("error ==" , ""+e);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                              }
                            };
                        handler.post(doDisplayError);
                    }
                }else{
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }
            }
        });


    }

    public   void putAPI(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(600, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserId();

        Log.e("url----->" , url);
        Log.e("token----->" , token);
        Log.e("id----->" , id);

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("ascent-pmr-api-token", token)
                .addHeader("X-ASCENT-USERID", id)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    dialog.dismiss();
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    dialog.dismiss();
                    showLogOutDialog();
                }else if (statusCode == 400){
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                dialog.dismiss();
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Log.e("error ==" , ""+e);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }else{
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }
            }
        });


    }

    public   void postAPIWithArrayParams(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parametersArray.toString());
        Log.e("HttpService", "Params Request was: " + parametersArray);
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(600, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserId();

        Log.e("url----->" , url);
        Log.e("token----->" , token);
        Log.e("id----->" , id);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("ascent-pmr-api-token", token)
                .addHeader("X-ASCENT-USERID", id)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                dialog.dismiss();
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    showLogOutDialog();
                }else if (statusCode == 400){
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }
            }
        });


    }

    public   void postAPIWithoutVesionCode(String url){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserId();

        Log.e("url----->" , this.url + url);
        Log.e("token----->" , token);
        Log.e("id----->" , id);

        Request request = new Request.Builder()
                .url(this+url)
                .post(body)
                .addHeader("ascent-pmr-api-token", token)
                .addHeader("X-ASCENT-USERID", id)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                dialog.dismiss();
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    showLogOutDialog();
                }else if (statusCode == 400){
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }

            }
        });


    }

    public   void deleteAPI(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserId();

        Log.e("url----->" , url);
        Log.e("token----->" , token);
        Log.e("id----->" , id);

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("ascent-pmr-api-token", token)
                .addHeader("X-ASCENT-USERID", id)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                dialog.dismiss();
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201 || statusCode == 202){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    showLogOutDialog();
                }else if (statusCode == 400){
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }

            }
        });


    }

    public   void deleteAPIArrayParams(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Log.e("HttpService", "Params Request was: " + parametersArray);
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserId();

        Log.e("url----->" , url);
        Log.e("token----->" , token);
        Log.e("id----->" , id);

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("ascent-pmr-api-token", token)
                .addHeader("X-ASCENT-USERID", id)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                dialog.dismiss();
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201 || statusCode == 202){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    showLogOutDialog();
                }else if (statusCode == 400){
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }

            }
        });


    }

    public   void putAPIArrayParams(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Log.e("HttpService", "Params Request was: " + parametersArray);
        RequestBody body = RequestBody.create(JSON, parametersArray.toString());
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserId();

        Log.e("url----->" , url);
        Log.e("token----->" , token);
        Log.e("id----->" , id);

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("ascent-pmr-api-token", token)
                .addHeader("X-ASCENT-USERID", id)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                dialog.dismiss();
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201 || statusCode == 202){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    showLogOutDialog();
                }else if (statusCode == 400){
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }

            }
        });


    }

    public   void deleteWithoutVersionAPI(String url){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserId();

        Log.e("url----->" , this + url);
        Log.e("token----->" , token);
        Log.e("id----->" , id);

        Request request = new Request.Builder()
                .url(this + url)
                .delete()
                .addHeader("ascent-pmr-api-token", token)
                .addHeader("X-ASCENT-USERID", id)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                dialog.dismiss();
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    Log.e("response ", "onResponse(): " + response );
                }if(statusCode == 200){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    showLogOutDialog();
                }else if (statusCode == 400){
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }

            }
        });


    }


    public   void putAPIWithArrayParams(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parametersArray.toString());
        Log.e("HttpService", "Params Request was: " + parametersArray);
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserId();

        Log.e("url----->" , url);
        Log.e("token----->" , token);
        Log.e("id----->" , id);

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("ASCENT-SFA-API-TOKEN", token)
                .addHeader("X-ASCENT-USERID", id)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                dialog.dismiss();
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201 || statusCode == 202){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    showLogOutDialog();
                }else if (statusCode == 400){
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }

            }
        });


    }

    public   void putAPIWithoutVersion(String url){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserId();

        Log.e("url----->" , this.url + url);
        Log.e("token----->" , token);
        Log.e("id----->" , id);

        Request request = new Request.Builder()
                .url(this.url + url)
                .put(body)
                .addHeader("ASCENT-SFA-API-TOKEN", token)
                .addHeader("X-ASCENT-USERID", id)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                dialog.dismiss();
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    showLogOutDialog();
                }else if (statusCode == 400){
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }

            }
        });


    }

    public   void postAPILogIn(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);
        Log.e("url----->" , url);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("response ", "onResponse(): " + response );
                if(statusCode == 200){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    dialog.dismiss();
                }else if (statusCode == 400 || statusCode == 401){
                    dialog.dismiss();
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        dialog.dismiss();
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }else{
                    dialog.dismiss();
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }

            }
        });


    }

    public void getAPI(Boolean isLoader){
        final Dialog dialog =   new Dialog(ctx);
        if(isLoader){
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
        }

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(600, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserId();
        Log.e("url----->" , url);
        Log.e("token----->" , token);
        Log.e("id----->" , id);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("ascent-pmr-api-token", token)
               // .addHeader("X-ASCENT-USERID", id)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                if(isLoader){
                    dialog.dismiss();
                }
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("response ", "onResponse(): " + response );
                if(isLoader){
                    dialog.dismiss();
                }
                if(statusCode == 200){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }else if (statusCode == 401){
                    if(isLoader){
                        dialog.dismiss();
                    }
                    showLogOutDialog();
                }else if (statusCode == 400){
                    if(isLoader){
                        dialog.dismiss();
                    }
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }else{
                    if(isLoader){
                        dialog.dismiss();
                    }
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }

            }
        });


    }

    public void getAPIWithOutVersion(Boolean isLoader , String url){
        final Dialog dialog =   new Dialog(ctx);
        if(isLoader){
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
        }

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserId();
        Log.e("url----->" , this.url + url);
        Log.e("token----->" , token);
        Log.e("id----->" , id);

        Request request = new Request.Builder()
                .url(this.url.concat(url))
                .addHeader("ASCENT-SFA-API-TOKEN", token)
                .addHeader("X-ASCENT-USERID", id)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                if(isLoader){
                    dialog.dismiss();
                }
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response r) throws IOException {
                if(isLoader){
                    dialog.dismiss();
                }
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("response ", "onResponse(): " + response );
                if(statusCode == 200){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }else if (statusCode == 401){
                    showLogOutDialog();
                } else if (statusCode == 400){
                    try {
                        JSONArray jsnoArr = new JSONArray(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = "";
                                for (int i=0;i<jsnoArr.length();i++){
                                    JSONObject obj = jsnoArr.optJSONObject(i);
                                    msg = msg.concat(obj.optString("message"));
                                }
                                new Comman(ctx).showCommanAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }else{
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            new Comman(ctx).showErrorToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }

            }
        });


    }


    public void showLogOutDialog(){
        Runnable doDisplayError = new Runnable() {
            public void run() {
                final Dialog d = new Dialog(ctx);
                d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFade;
                d.setContentView(R.layout.successwithok);

                TextView Labeltitle = d.findViewById(R.id.Labeltitle);
                Labeltitle.setText("Session Expired Please Log in Again to Continue !!");

                d.findViewById(R.id.btokk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                        ctx.startActivity(new Intent(ctx , LogIn.class));
                    }
                });

                d.show();
                d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        handler.post(doDisplayError);
    }

}
