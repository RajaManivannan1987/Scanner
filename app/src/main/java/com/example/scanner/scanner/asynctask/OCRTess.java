package com.example.scanner.scanner.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.interfaceclass.OcrInterface;
import com.example.scanner.scanner.services.ImageProcessing;
import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by Sanjay on 2/23/16.
 */
public class OCRTess extends AsyncTask<String, String, String> {
    private Context context;
    private String TAG = "OCRTess";
    ProgressDialog progressBar;
    private OcrInterface ocrInterface;

    public OCRTess(Context context, OcrInterface ocrInterface) {
        this.context = context;
        progressBar = new ProgressDialog(context);
        progressBar.setMessage("Detecting...");
        progressBar.setTitle("OCR");
        progressBar.setCancelable(false);
        this.ocrInterface=ocrInterface;
    }

    @Override
    protected String doInBackground(String... params) {
        String recognizedText="";
        try {
            Log.d(TAG, "Path " + params[0]);
            TessBaseAPI baseApi = new TessBaseAPI();
            Log.d(TAG, params[0]);
            baseApi.setDebug(true);
//        baseApi.setPageSegMode(TessBaseAPI.OEM_TESSERACT_ONLY);
            baseApi.init(Environment.getExternalStorageDirectory().toString() + "/Scan Rite/", "eng"); // myDir + "/tessdata/eng.traineddata" must be present
            baseApi.setImage(ImageProcessing.doGreyscale(Global.correctBitmap(params[0])));

            recognizedText = baseApi.getUTF8Text(); // Log or otherwise display this string...
            Log.d(TAG, recognizedText);
            baseApi.end();
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
        //recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        return recognizedText;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.show();
    }

    @Override
    protected void onPostExecute(String text) {
        // dismiss the dialog after the file was downloaded
        progressBar.dismiss();
        ocrInterface.result(text);
        Log.d(TAG, "Result:" + text);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

    }
}
