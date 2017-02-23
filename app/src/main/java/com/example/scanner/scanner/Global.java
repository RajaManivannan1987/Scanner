package com.example.scanner.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IM021 on 11/17/2015.
 */
public class Global {
    private static String TAG = "Global";
    public static String folderId="folderId";

    public static String tesseractOcr = "https://github.com/tesseract-ocr/tessdata/blob/master/eng.traineddata?raw=true";

    public static void copyDirectory(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);


            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            Log.w("Copy File", sourceLocation.getAbsolutePath() + " to " + targetLocation.getAbsolutePath());
        }
    }

    public static File getOutputMediaFile(int i, Context context) {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File(Environment.getDataDirectory().getAbsolutePath());
        try {
            mediaStorageDir = new File(getDataDir(context));
        } catch (Exception e) {
        }
        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String a = "";
        if (i != 0) {
            a = i + "";
        }
        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG-" + timeStamp + a + ".jpg");
        return mediaFile;
    }

    public static File getOutputMediaExternalFile(int i, Context context) {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Scan Rite/Mail");
//        try {
//            mediaStorageDir = new File(getDataDir(context));
//        } catch (Exception e) {
//        }
        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String a = "";
        if (i != 0) {
            a = i + "";
        }
        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG-" + timeStamp + a + ".jpg");
        return mediaFile;
    }

    public static File getOutputPdfFile(int i, Context context) {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString() + "/Scan Rite/Mail");
//        try {
//            mediaStorageDir = new File(getDataDir(context));
//        } catch (Exception e) {
//        }
        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String a = "";
        if (i != 0) {
            a = i + "";
        }
        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "PDF-" + timeStamp + a + ".pdf");
        return mediaFile;
    }

    public static File getOutputTextFile(int i, Context context) {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File(Environment.getDataDirectory().getAbsolutePath());
        try {
            mediaStorageDir = new File(getDataDir(context));
        } catch (Exception e) {
        }
        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String a = "";
        if (i != 0) {
            a = i + "";
        }
        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "OCR-" + timeStamp + a + ".txt");
        return mediaFile;
    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static void deleteFile(Context context, String path) {
        try {
            File file = new File(path);
            file.delete();
            if (file.exists()) {
                try {
                    file.getCanonicalFile().delete();
                } catch (Exception e) {
                    Log.e("Delete File", e.toString());
                }
                if (file.exists()) {
                    context.deleteFile(file.getName());
                }
            }
        } catch (Exception e) {
            Log.e("Delete File", e.toString());
        }
    }

    public static String getDataDir(Context context) throws Exception {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.dataDir;
    }

    public static boolean hasFeature(Context context, String feature) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(feature);
    }

    public static String unTitle(Context context) {
        int result;
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.share_preference_folder), context.MODE_PRIVATE);
        result = pref.getInt(context.getString(R.string.share_pref_un_title), 1);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(context.getString(R.string.share_pref_un_title), result + 1);
        editor.commit();
        return "Untitled_" + result;
    }

    public static Bitmap correctBitmap(String path) {
        File file = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        try {
            ExifInterface exif = new ExifInterface(path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            if (rotate != 0) {
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap & convert to ARGB_8888, required by tess
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        } catch (Exception e) {
            Log.e("correctBitmap " + TAG, e.toString());
        }
        return bitmap;
    }

    public static void imageToPdf(String sourcePath, String destinationPath) {
        Document document = new Document();
        String input = sourcePath; // .gif and .jpg are ok too!
        String output = destinationPath;
        try {
            FileOutputStream fos = new FileOutputStream(output);
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.open();
            document.open();
            document.add(Image.getInstance(input));
            document.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
