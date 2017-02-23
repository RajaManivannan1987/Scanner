package com.example.scanner.scanner.services;

import android.content.Context;
import android.util.Log;
import com.example.scanner.scanner.Global;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IM028 on 4/6/16.
 */
public class ConvertPdf {
    private String TAG="ConvertPdf";
    private Context context;
    public ConvertPdf(Context context,String TAG){
        this.TAG=TAG+this.TAG;
        this.context=context;
    }
    public String convertImagesToPdf(ArrayList<String> pathList){
        String result=Global.getOutputPdfFile(0,context).getAbsolutePath();
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(new File(result)));
            document.open();
            for (String path:pathList) {
                if(document.newPage()){
                    Image image = Image.getInstance (path);
                    Log.v(TAG, "Image path adding "+path);
                    image.setAlignment(Image.MIDDLE | Image.TEXTWRAP);
                    document.add(image);
                }
            }
            document.close();
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
        return result;
    }
}
