package com.example.scanner.scanner.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.printservice.PrintDocument;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.R;
import com.example.scanner.scanner.asynctask.DownloadFile;
import com.example.scanner.scanner.asynctask.OCRTess;
import com.example.scanner.scanner.interfaceclass.OcrInterface;
import com.example.scanner.scanner.printdocument.MyPrintDocumentAdapter;
import com.example.scanner.scanner.services.ConvertPdf;
import com.example.scanner.scanner.ui.PrintDialogActivity;
import com.example.scanner.scanner.ui.SaveToGallery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;

public class ExportActivity extends AppCompatActivity {
    private LinearLayout ocrLinearLayout, emailLinearLayout, printLinearLayout, galleryLinearLayout, uploadLinearLayout, shareLinearLayout, pdfPreviewLinearLayout;
    private String path1 = "", scannedText = "";
    private String TAG = "ExportActivity";
    private ImageView backImageView;
    private ArrayList<String> pathList = new ArrayList<>();
    int i = 0;
    private static final int MILS_IN_INCH = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        ocrLinearLayout = (LinearLayout) findViewById(R.id.exportActivityOcrLinearLayout);
        emailLinearLayout = (LinearLayout) findViewById(R.id.exportActivityEmailLinearLayout);
        printLinearLayout = (LinearLayout) findViewById(R.id.exportActivityPrintLinearLayout);
        galleryLinearLayout = (LinearLayout) findViewById(R.id.exportActivityGalleryLinearLayout);
        backImageView = (ImageView) findViewById(R.id.exportActivityBackImageView);
        uploadLinearLayout = (LinearLayout) findViewById(R.id.exportActivityUploadLinearLayout);
        shareLinearLayout = (LinearLayout) findViewById(R.id.exportActivityShareLinearLayout);
        pdfPreviewLinearLayout = (LinearLayout) findViewById(R.id.exportActivityPdfPreviewLinearLayout);
        if (getIntent().getExtras().getString("path") != null && !getIntent().getExtras().getString("path").equalsIgnoreCase("")) {
            path1 = getIntent().getExtras().getString("path");
        } else {
            pathList.clear();
            pathList = getIntent().getExtras().getStringArrayList("pathArray");
        }
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ocrLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!path1.equalsIgnoreCase("")) {
                    String path = Environment.getExternalStorageDirectory().toString() + "/Scan Rite/tessdata/eng.traineddata";
                    File tscFile = new File(path);
                    if (tscFile.exists()) {
                        try {
                            new OCRTess(ExportActivity.this, new OcrInterface() {
                                @Override
                                public void result(String result) {
                                    if (!result.equalsIgnoreCase(""))
                                        startActivity(new Intent(ExportActivity.this, ResultTessractActivity.class).putExtra("text", result));
                                    else
                                        Toast.makeText(ExportActivity.this, "No text is detected", Toast.LENGTH_SHORT).show();
                                }
                            }).execute(path1);
                        } catch (Exception e) {
                            Log.e(TAG, e + "");
                        }
                    } else {
                        new DownloadFile(ExportActivity.this).execute(Global.tesseractOcr);
                    }
                } else {
                    scannedText = "";
                    i = 0;
                    if (pathList.size() > 0) {
                        scan();
                    } else {
                        Toast.makeText(v.getContext(), "No Images Selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        emailLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] choice = {"Photo", "PDF"};
                final AlertDialog.Builder alert = new AlertDialog.Builder(ExportActivity.this);
                alert.setTitle("Email");
                alert.setSingleChoiceItems(choice, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (choice[which] == choice[0]) {
                            Intent emailIntent = null;
//                                  emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{strEmail});
//                                  emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Test Subject");
//                                  emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");
                            if (!path1.equalsIgnoreCase("")) {
                                ArrayList<String> list = new ArrayList<String>();
                                list.add(path1);
                                emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, convertUriList(list));

                            } else {
                                emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, convertUriList(pathList));
                            }
//                                  emailIntent.setType("application/image");
                            emailIntent.setType("message/rfc822");
                            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        } else if (choice[which] == choice[1]) {
                            ArrayList<Uri> result = new ArrayList<Uri>();
                            Intent emailIntent = null;
//                                  emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{strEmail});
//                                  emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Test Subject");
//                                  emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");

                            if (!path1.equalsIgnoreCase("")) {
                                ArrayList<String> list = new ArrayList<String>();
                                list.add(path1);
                                emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                String file = new ConvertPdf(ExportActivity.this, TAG).convertImagesToPdf(list);
                                if (new File(file).exists()) {
                                    Uri uri = Uri.fromFile(new File(file));
                                    result.add(uri);
                                } else {
                                    Log.e(TAG, "Exist");
                                }
                                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, result);

                            } else {
                                emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                String file = new ConvertPdf(ExportActivity.this, TAG).convertImagesToPdf(pathList);
                                if (new File(file).exists()) {
                                    Uri uri = Uri.fromFile(new File(file));
                                    result.add(uri);
                                } else {
                                    Log.e(TAG, "Exist");
                                }
                                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, result);
                            }
//                                  emailIntent.setType("application/image");
                            emailIntent.setType("application/pdf");
                            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                        }
                        dialog.cancel();
                    }
                });
                alert.show();

            }
        });
        printLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!path1.equalsIgnoreCase("")) {
                    List<String> list = new ArrayList<String>();
                    list.add(path1);
//                    doPhotoPrint(list);
                    documentPrint(list);
                } else
                    documentPrint(pathList);
            }
        });

        galleryLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(ExportActivity.this, SaveToGallery.class);
                if (!path1.equalsIgnoreCase("")) {
                    shareIntent.setAction(getResources().getString(R.string.intent_share_save_to_gallery));
                    shareIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path1)));
                } else {
                    shareIntent.setAction(getResources().getString(R.string.intent_share_save_to_gallery_multiple));
                    shareIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, convertUriList(pathList));
                }
                shareIntent.setType("image/*");
                startActivity(shareIntent);
            }
        });
        pdfPreviewLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_VIEW);
                if (!path1.equalsIgnoreCase("")) {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(path1);
                    String path = new ConvertPdf(ExportActivity.this, TAG).convertImagesToPdf(list);
                    File file = new File(path);
                    shareIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
                } else {
                    String path = new ConvertPdf(ExportActivity.this, TAG).convertImagesToPdf(pathList);
                    File file = new File(path);
                    shareIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
                }
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(shareIntent);
            }
        });
        shareLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] choice = {"Photo", "PDF"};
                final AlertDialog.Builder alert = new AlertDialog.Builder(ExportActivity.this);
                alert.setTitle("Share");
                alert.setSingleChoiceItems(choice, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (choice[which] == choice[0]) {
                            Intent emailIntent = null;
//                                  emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{strEmail});
//                                  emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Test Subject");
//                                  emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");
                            if (!path1.equalsIgnoreCase("")) {
                                ArrayList<String> list = new ArrayList<String>();
                                list.add(path1);
                                emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, convertUriList(list));

                            } else {
                                emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, convertUriList(pathList));
                            }
                            emailIntent.setType("*/image");
//                            emailIntent.setType("message/rfc822");
                            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(emailIntent, "Share"));
                        } else if (choice[which] == choice[1]) {
                            ArrayList<Uri> result = new ArrayList<Uri>();
                            Intent emailIntent = null;
//                                  emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{strEmail});
//                                  emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Test Subject");
//                                  emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");

                            if (!path1.equalsIgnoreCase("")) {
                                ArrayList<String> list = new ArrayList<String>();
                                list.add(path1);
                                emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                String file = new ConvertPdf(ExportActivity.this, TAG).convertImagesToPdf(list);
                                if (new File(file).exists()) {
                                    Uri uri = Uri.fromFile(new File(file));
                                    result.add(uri);
                                } else {
                                    Log.e(TAG, "Exist");
                                }
                                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, result);

                            } else {
                                emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                String file = new ConvertPdf(ExportActivity.this, TAG).convertImagesToPdf(pathList);
                                if (new File(file).exists()) {
                                    Uri uri = Uri.fromFile(new File(file));
                                    result.add(uri);
                                } else {
                                    Log.e(TAG, "Exist");
                                }
                                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, result);
                            }
//                                  emailIntent.setType("application/image");
                            emailIntent.setType("*/pdf");
                            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(emailIntent, "Share..."));

                        }
                        dialog.cancel();
                    }
                });
                alert.show();

            }
        });
    }

    private void scan() {
        String path = Environment.getExternalStorageDirectory().toString() + "/Scan Rite/tessdata/eng.traineddata";
        File tscFile = new File(path);
        if (tscFile.exists()) {
            try {
                new OCRTess(ExportActivity.this, new OcrInterface() {
                    @Override
                    public void result(String result) {
                        i++;
                        scannedText += "\n" + result;
                        if (i >= pathList.size()) {
                            if (!scannedText.equalsIgnoreCase(""))
                                startActivity(new Intent(ExportActivity.this, ResultTessractActivity.class).putExtra("text", scannedText));
                            else
                                Toast.makeText(ExportActivity.this, "No text is detected", Toast.LENGTH_SHORT).show();
                        } else {
                            scan();
                        }
                    }
                }).execute(pathList.get(i));
            } catch (Exception e) {
                Log.e(TAG, e + "");
            }
        } else {
            new DownloadFile(ExportActivity.this).execute(Global.tesseractOcr);
        }
    }

    private ArrayList<Uri> convertUriList(ArrayList<String> list) {
        ArrayList<Uri> result = new ArrayList<Uri>();

        for (int i = 0; i < list.size(); i++) {
            File destFile = Global.getOutputMediaExternalFile(i, ExportActivity.this);
            try {
                Global.copyDirectory(new File(list.get(i)), destFile);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            result.add(Uri.fromFile(destFile));
        }
        return result;
    }

    private void doPhotoPrint(List<String> data) {
        PrintHelper photoPrinter = new PrintHelper(ExportActivity.this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        for (int i = 0; i < data.size(); i++) {
            try {

                photoPrinter.printBitmap("IMG-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + "-" + i, Uri.fromFile(new File(data.get(i))));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void documentPrint(List<String> data) {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) + " Document";

        printManager.print(jobName, new MyPrintDocumentAdapter(this,data), null);
    }
}