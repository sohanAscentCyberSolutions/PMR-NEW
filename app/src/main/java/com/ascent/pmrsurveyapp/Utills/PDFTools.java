package com.ascent.pmrsurveyapp.Utills;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;


import java.io.File;
import java.util.List;
import java.util.Random;

public class PDFTools {
    private static final String TAG = "PDFTools";
    private static final String GOOGLE_DRIVE_PDF_READER_PREFIX = "http://drive.google.com/viewer?url=";
    private static final String PDF_MIME_TYPE = "application/pdf";
    private static final String HTML_MIME_TYPE = "text/html";

    private static Context ctx = null;




    public static void showPDFUrl(final Context context, final String pdfUrl , final String id) {
        ctx = context;
       // if (isPDFSupported(context)) {
            downloadAndOpenPDF(context, pdfUrl , id);
       // } else {
            //askToOpenPDFThroughGoogleDrive(context, pdfUrl);
       // }
    }
    public static int getRendomNo(){
        final int min = 0;
        final int max = 400000;
        final int random = new Random().nextInt((max - min) + 1) + min;
        return random;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void downloadAndOpenPDF(final Context context, final String pdfUrl , final String tempid) {
        // Get filename
        //final String filename = pdfUrl.substring( pdfUrl.lastIndexOf( "/" ) + 1 );
        ctx = context;

        final ProgressDialog progress = ProgressDialog.show(context, "Downloading...", "Downloading pdf ...", true);
        // Show progress dialog while downloading

        String filename = "";
        filename = "survey_id_"+tempid+".pdf";
        // The place where the downloaded PDF file will be put
        final File tempFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename);
       Log.e(TAG, "File Path:" + tempFile);
        if (tempFile.exists()) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            // If we have downloaded the file before, just go ahead and show it.
            openPDF(context, tempFile.getAbsolutePath());
            return;
        }
        // Create the download request
        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(pdfUrl));
        String token = new Comman(ctx).getToken();
        r.addRequestHeader("ascent-pmr-api-token", token);
        r.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, filename);
        final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Cursor c = dm.query(new DownloadManager.Query().setFilterById(downloadId));

                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        if (progress.isShowing()) {
                            progress.dismiss();
                        }
                        Toast.makeText(context, "PDF Downloaded Successfully.", Toast.LENGTH_SHORT).show();
                        openPDF(context, tempFile.getAbsolutePath());
                    }
                }
                c.close();
            }
        };
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        // Enqueue the request
        dm.enqueue(r);
    }


    public static void askToOpenPDFThroughGoogleDrive(final Context context, final String pdfUrl) {
        new AlertDialog.Builder(context)
                .setTitle("Open File")
                .setMessage("Open details using google drive ?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openPDFThroughGoogleDrive(context, pdfUrl);
                    }
                })
                .show();
    }

    public static void openPDFThroughGoogleDrive(final Context context, final String pdfUrl) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse(GOOGLE_DRIVE_PDF_READER_PREFIX + pdfUrl), HTML_MIME_TYPE);
        context.startActivity(i);
    }

   /* public static final void openPDF(Context context, Uri localUri) {
        try
        {
            Intent intentUrl = new Intent(Intent.ACTION_VIEW);
            intentUrl.setDataAndType(localUri, "application/pdf");
            intentUrl.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentUrl);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(context, "No PDF Viewer Installed", Toast.LENGTH_LONG).show();
        }
       // Intent i = new Intent(Intent.ACTION_VIEW);
       // i.setDataAndType(localUri, PDF_MIME_TYPE);
       // context.startActivity(i);
    }*/

    public static final void openPDF(Context context, String path){

        File file = new File(path);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            PackageManager pm = context.getPackageManager();
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("application/pdf");
            Intent openInChooser = Intent.createChooser(intent, "Choose");
          //  List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
            try {
                context.startActivity(openInChooser);
            } catch (Throwable throwable) {
                Toast.makeText(context, "PDF apps are not installed", Toast.LENGTH_SHORT).show();
                // PDF apps are not installed
            }
          /* if (resInfo.size() > 0) {

            } else {
                Toast.makeText(context, "PDF apps are not installed", Toast.LENGTH_SHORT).show();
            }*/
        }
    }
}