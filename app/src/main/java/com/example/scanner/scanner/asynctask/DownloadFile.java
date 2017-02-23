package com.example.scanner.scanner.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Sanjay on 2/23/16.
 */

public class DownloadFile extends AsyncTask<String, Integer, String> {
    private Context context;
    private String TAG = "DownloadFileFromURL";
    private ProgressDialog progressBar;

    public DownloadFile(Context context) {
        this.context = context;
        progressBar = new ProgressDialog(this.context);
        progressBar.setMessage("Downloading...");
        progressBar.setTitle("Downloading OCR data");
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }


    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.show();
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // this will be useful so that you can show a tipical 0-100%           progress bar
            int lenghtOfFile = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Scan Rite/tessdata");
            try {
                if (!dir.exists())
                    Log.d(TAG, dir.getAbsoluteFile() + " " + dir.mkdirs() + "");

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Scan Rite/tessdata", "eng.traineddata");
                file.createNewFile();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            // Output stream
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Scan Rite/tessdata/eng.traineddata");

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress((int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(Integer... progress) {
        // setting progress percentage
        Log.d(TAG, progress[0] + "");
//        progressBar.setMessage("Downloading..." + progress[0] + "%");
        progressBar.setProgress(progress[0]);


    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
        progressBar.dismiss();

        // Displaying downloaded image into image view
        // Reading image path from sdcard
        String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
        // setting downloaded into image view

    }

}

