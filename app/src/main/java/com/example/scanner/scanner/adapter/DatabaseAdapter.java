package com.example.scanner.scanner.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.module.DefaultLocationImagePath;
import com.example.scanner.scanner.module.DocumentFolder;
import com.example.scanner.scanner.module.ScannedLocationImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IM021 on 11/6/2015.
 */
public class DatabaseAdapter {

    private static final String DATABASE_NAME = "Scanner";
    private static final int DATABASE_VERSION = 7;

    private static final String DATABASE_TABLE_DEFAULT_LOCATION = "default_location";
    private static final String DATABASE_TABLE_DOCUMENT = "document";
    private static final String DATABASE_TABLE_SCANNED_LOCATION = "scanned_location";


    public static final String KEY_ID = "id";
    public static final String KEY_PATH = "path";
    public static final String KEY_NAME = "name";
    public static final String KEY_DOC_TYPE = "doc_type";
    public static final String KEY_DOC_DATE = "doc_date";
    public static final String KEY_DOCID = "doc_id";
    public static final String KEY_ORIGINAL_PATH = "path_original";
    public static final String KEY_OCR_PATH = "path_ocr";
    public static final String KEY_HAS_FOLDER = "has_folder";

    private static final String TAG = DatabaseAdapter.class.getSimpleName();

    private static final String DATABASE_CREATE_DEFAULT_LOCATION = "create table " + DATABASE_TABLE_DEFAULT_LOCATION +
            " (" +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_PATH + " varchar(511) not null" +
            ");";

    private static final String DATABASE_CREATE_DOCUMENT = "create table " + DATABASE_TABLE_DOCUMENT +
            " (" +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_NAME + " varchar(255) not null," +
            KEY_DOC_TYPE + " varchar(255)," +
            KEY_DOC_DATE + " varchar(255)," +
            KEY_HAS_FOLDER + " integer default 0" +
            " );";

    private static final String DATABASE_CREATE_SCANNED_LOCATION = "create table " + DATABASE_TABLE_SCANNED_LOCATION +
            " (" +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_DOCID + " integer default 0 , " +
            KEY_PATH + " varchar(511) not null, " +
            KEY_OCR_PATH + " varchar(511), " +
            KEY_ORIGINAL_PATH + " varchar(511) not null" +
            " );";

    private Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DatabaseAdapter(Context context) {
        this.context = context;
        DBHelper = new DatabaseHelper(this.context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE_DEFAULT_LOCATION);
                db.execSQL(DATABASE_CREATE_DOCUMENT);
                db.execSQL(DATABASE_CREATE_SCANNED_LOCATION);
                Log.w(TAG, "Table Created");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_DOCUMENT);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_DEFAULT_LOCATION);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SCANNED_LOCATION);
            onCreate(db);
        }
    }


    //---opens the database---
    public DatabaseAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    //---closes the database---
    public void close() {
        DBHelper.close();
    }

    /*
    Default Database location Table Starts
    */
    public long insertDefaultLocation(String path) {
        open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PATH, path);
        Log.w(TAG, "Default Path : " + path);
        Long result = db.insert(DATABASE_TABLE_DEFAULT_LOCATION, null, initialValues);
        close();
        Log.w(TAG, "Default Location Table row inserted " + result);
        return result;
    }

    public DefaultLocationImagePath getDefaultLocation(long rowId) {
        open();
        Cursor mCursor = db.query(true, DATABASE_TABLE_DEFAULT_LOCATION, new String[]{KEY_ID, KEY_PATH}, KEY_ID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            DefaultLocationImagePath defaultLocationImagePath = new DefaultLocationImagePath();
            defaultLocationImagePath.setKey(mCursor.getLong(0));
            defaultLocationImagePath.setPath(mCursor.getString(1));
            close();
            Log.w(TAG, "Default Location Table got row " + rowId);
            return defaultLocationImagePath;
        }
        close();
        Log.w(TAG, "Default Location Table not get row " + rowId);
        return null;
    }

    public boolean updateDefaultLocation(long rowId, String path) {
        open();
        ContentValues args = new ContentValues();
        Log.d(TAG, rowId + " " + path);
        args.put(KEY_PATH, path);
        boolean result = db.update(DATABASE_TABLE_DEFAULT_LOCATION, args, KEY_ID + "=" + rowId, null) > 0;
        close();
        return result;
    }

    public boolean swapDefaultLocation(long fromId, long toId) {
        DefaultLocationImagePath fromPath = getDefaultLocation(fromId);
        DefaultLocationImagePath toPath = getDefaultLocation(toId);
        boolean result = updateDefaultLocation(toId, fromPath.getPath());
        boolean result1 = updateDefaultLocation(fromId, toPath.getPath());
        return result && result1;
    }

    public int getSizeDefaultLocation() {
        open();
        int a = 0;
        Cursor c = db.query(DATABASE_TABLE_DEFAULT_LOCATION, new String[]{KEY_ID, KEY_PATH}, null, null, null, null, null);
        a = c.getCount();
        close();
        Log.w(TAG, "Default Location Table row size " + a);
        return a;
    }

    public List<DefaultLocationImagePath> getAllDefaultLocation() {
        open();
        Cursor c = db.query(DATABASE_TABLE_DEFAULT_LOCATION, new String[]{KEY_ID, KEY_PATH}, null, null, null, null, null);
        List<DefaultLocationImagePath> defaultLocationImagePaths = new ArrayList<DefaultLocationImagePath>();
        if (c.moveToFirst()) {
            do {
                DefaultLocationImagePath defaultLocationImagePath = new DefaultLocationImagePath();
                defaultLocationImagePath.setKey(c.getLong(0));
                defaultLocationImagePath.setPath(c.getString(1));
                defaultLocationImagePaths.add(defaultLocationImagePath);
            } while (c.moveToNext());
        }
        close();
        Log.w(TAG, "Default Location Table all row size " + defaultLocationImagePaths.size());
        return defaultLocationImagePaths;
    }

    public boolean deleteDefault(long rowId) {
        open();
        Boolean b = db.delete(DATABASE_TABLE_DEFAULT_LOCATION, KEY_ID + "=" + rowId, null) > 0;
        close();
        if (b)
            Log.w(TAG, "Default Location Table row deleted " + rowId);
        else
            Log.w(TAG, "Default Location Table row not deleted " + rowId);
        return b;
    }

    public DefaultLocationImagePath getDefaultLast() {
        open();
        Cursor c = db.query(DATABASE_TABLE_DEFAULT_LOCATION, new String[]{KEY_ID, KEY_PATH}, null, null, null, null, null);
        if (c.moveToFirst()) {
            DefaultLocationImagePath defaultLocationImagePath = new DefaultLocationImagePath();
            defaultLocationImagePath.setKey(c.getLong(0));
            defaultLocationImagePath.setPath(c.getString(1));
            close();
            Log.w(TAG, "Default Location Table got Last row " + c.getLong(0));
            return defaultLocationImagePath;
        } else {
            close();
            Log.w(TAG, "Default Location Table Empty");
            return null;
        }
    }
    /*
    Default Database location Table Ends
    */

    /*
    Scanned Database location Table Starts
    */
    public long insertScannedLocation(long defaultLocationId, String scannedLocationPath, long docId) {
        String originalPath = getDefaultLocation(defaultLocationId).getPath();
        //To save Initial Image comment below line
        Global.deleteFile(context, originalPath);

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PATH, scannedLocationPath);
        initialValues.put(KEY_DOCID, docId);
        initialValues.put(KEY_ORIGINAL_PATH, originalPath);
        open();
        long result = db.insert(DATABASE_TABLE_SCANNED_LOCATION, null, initialValues);
        deleteDefault(defaultLocationId);
        close();
        Log.w(TAG, "Scanned Location Table row inserted " + result);
        return result;
    }

    public ScannedLocationImage getScannedLocation(long id) {
        open();
        Cursor mCursor = db.query(true, DATABASE_TABLE_SCANNED_LOCATION, new String[]{KEY_ID, KEY_PATH, KEY_ORIGINAL_PATH}, KEY_ID + "=" + id, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            ScannedLocationImage scannedLocationImage = new ScannedLocationImage();
            scannedLocationImage.setKey(mCursor.getLong(0));
            scannedLocationImage.setPath(mCursor.getString(1));
            scannedLocationImage.setOriginalImagePath(mCursor.getString(2));
            close();
            Log.w(TAG, "Scanned Location Table got row " + id);
            return scannedLocationImage;
        }
        close();
        Log.w(TAG, "Scanned Location Table not get row");
        return null;
    }

    public List<DefaultLocationImagePath> getAllGalleryScannedLocation() {
        open();
        Cursor c = db.query(true, DATABASE_TABLE_SCANNED_LOCATION, new String[]{KEY_ID, KEY_PATH}, KEY_DOCID + "=" + 0, null, null, null, null, null);
        List<DefaultLocationImagePath> scannedLocationImages = new ArrayList<DefaultLocationImagePath>();
        if (c.moveToFirst()) {
            do {
                DefaultLocationImagePath scannedLocationImage = new DefaultLocationImagePath();
                scannedLocationImage.setKey(c.getLong(0));
                scannedLocationImage.setPath(c.getString(1));
                scannedLocationImages.add(scannedLocationImage);

            } while (c.moveToNext());
        }
        close();
        Log.w(TAG, "Scanned Location Table all row size " + scannedLocationImages.size());
        return scannedLocationImages;
    }

    public List<ScannedLocationImage> getAllScannedLocation() {
        open();
        Cursor c = db.query(DATABASE_TABLE_SCANNED_LOCATION, new String[]{KEY_ID, KEY_PATH, KEY_ORIGINAL_PATH, KEY_OCR_PATH}, null, null, null, null, null);
        List<ScannedLocationImage> scannedLocationImages = new ArrayList<ScannedLocationImage>();
        if (c.moveToFirst()) {
            do {
                ScannedLocationImage scannedLocationImage = new ScannedLocationImage();
                scannedLocationImage.setKey(c.getLong(0));
                scannedLocationImage.setPath(c.getString(1));
                scannedLocationImage.setOriginalImagePath(c.getString(2));
                scannedLocationImage.setOcrPath(c.getString(3));
                scannedLocationImages.add(scannedLocationImage);

            } while (c.moveToNext());
        }
        close();
        Log.w(TAG, "Scanned Location Table all row size " + scannedLocationImages.size());
        return scannedLocationImages;
    }

    public boolean updateScannedLocation(long rowId, String path) {
        open();
        ContentValues args = new ContentValues();
        args.put(KEY_PATH, path);
        Log.d(TAG, rowId + " " + path);
        boolean result = db.update(DATABASE_TABLE_SCANNED_LOCATION, args, KEY_ID + "=" + rowId, null) > 0;
        close();
        return result;
    }

    public boolean swapScannedLocation(long fromId, long toId) {
        ScannedLocationImage fromPath = getScannedLocation(fromId);
        ScannedLocationImage toPath = getScannedLocation(toId);
        boolean result = updateScannedLocation(toId, fromPath.getPath());
        boolean result1 = updateScannedLocation(fromId, toPath.getPath());
        return result && result1;
    }

    public boolean deleteScannedLocation(Long id) {
        open();
        boolean b = db.delete(DATABASE_TABLE_SCANNED_LOCATION, KEY_ID + "=" + id, null) > 0;
        close();
        if (b)
            Log.w(TAG, "Scanned Location Table row deleted");
        else
            Log.w(TAG, "Scanned Location Table row not deleted");
        return b;
    }

    public int getSizeScannedLocation() {
        open();
        int a = 0;
        Cursor c = db.query(true, DATABASE_TABLE_SCANNED_LOCATION, new String[]{KEY_ID, KEY_PATH}, KEY_DOCID + "=" + 0, null, null, null, null, null);
        a = c.getCount();
        close();
        Log.w(TAG, "Scanned Location Table row size " + a);
        return a;
    }

    public int updateOcrPathScannedLocation(Long id, String ocrPath) {
        open();
        int result = 0;
        ContentValues ocrValues = new ContentValues();
        ocrValues.put(KEY_OCR_PATH, ocrPath);
        result = db.update(DATABASE_TABLE_SCANNED_LOCATION, ocrValues, KEY_ID + " = " + id, null);
        close();
        return result;
    }

    public int updateFolderForScannedLocation(Long docId) {
        open();
        int result = 0;
        ContentValues docIdChange = new ContentValues();
        docIdChange.put(KEY_DOCID, docId);
        result = db.update(DATABASE_TABLE_SCANNED_LOCATION, docIdChange, KEY_DOCID + " = " + 0, null);
        close();
        Log.d(TAG, "Scanned Location Table update folder id in " + result);
        return result;
    }
    /*
    Scanned Database location Table Ends
    */

    /*
    Folder Database location Table Starts
    */
    public long insertFolder(String folderName, String docType, int hasFolder) {
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm a");
        ContentValues initialValues = new ContentValues();
        if (folderName.trim().equals("") || folderName == null) {
            initialValues.put(KEY_NAME, Global.unTitle(context));
        } else {
            initialValues.put(KEY_NAME, folderName);
        }
        initialValues.put(KEY_DOC_TYPE, docType);
        initialValues.put(KEY_HAS_FOLDER, hasFolder);
        initialValues.put(KEY_DOC_DATE, df.format(new Date()));
        open();
        long result = db.insert(DATABASE_TABLE_DOCUMENT, null, initialValues);
        close();
        Log.w(TAG, "Document Table row inserted " + result);
        return result;
    }

    public boolean deleteFolder(Long id) {
        open();
        db.delete(DATABASE_TABLE_DOCUMENT, KEY_ID + "=" + id, null);
        close();
        return true;
    }

    public DocumentFolder getFolder(Long id) {
        DocumentFolder result = new DocumentFolder();
        open();
        Cursor mCursor = db.query(true, DATABASE_TABLE_DOCUMENT, new String[]{KEY_ID, KEY_NAME, KEY_DOC_TYPE, KEY_DOC_DATE, KEY_HAS_FOLDER}, KEY_ID + "=" + id, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            result.setId(mCursor.getLong(0));
            result.setName(mCursor.getString(1));
            result.setDocType(mCursor.getString(2));
            result.setDocDate(mCursor.getString(3));
            result.setHasFolder(mCursor.getLong(4));
            close();
            Log.w(TAG, "Scanned Location Table got row " + id);
            return result;
        }
        close();
        Log.w(TAG, "Scanned Location Table not get row");
        return result;
    }

    public List<ScannedLocationImage> getImagesFolder(Long id) {
        open();
        Cursor c = db.query(true, DATABASE_TABLE_SCANNED_LOCATION, new String[]{KEY_ID, KEY_PATH, KEY_OCR_PATH}, KEY_DOCID + "=" + id, null, null, null, null, null);
        List<ScannedLocationImage> scannedLocationImages = new ArrayList<ScannedLocationImage>();
        if (c.moveToFirst()) {
            do {
                ScannedLocationImage scannedLocationImage = new ScannedLocationImage();
                scannedLocationImage.setKey(c.getLong(0));
                scannedLocationImage.setPath(c.getString(1));
                scannedLocationImage.setOcrPath(c.getString(2));
                scannedLocationImages.add(scannedLocationImage);

            } while (c.moveToNext());
        }
        close();
        Log.w(TAG, "Document Table get Scanned Location row size " + scannedLocationImages.size() + " document Id " + id);
        return scannedLocationImages;
    }

    public List<DocumentFolder> getAllDocumentFolder() {
        List<DocumentFolder> result = new ArrayList<DocumentFolder>();
        open();
        Cursor c = db.query(DATABASE_TABLE_DOCUMENT, new String[]{KEY_ID, KEY_NAME, KEY_DOC_TYPE, KEY_DOC_DATE, KEY_HAS_FOLDER}, null, null, null, null, KEY_ID + " DESC");

        if (c.moveToFirst()) {
            do {
                DocumentFolder documentFolder = new DocumentFolder();
                documentFolder.setId(c.getLong(0));
                documentFolder.setName(c.getString(1));
                documentFolder.setDocType(c.getString(2));
                documentFolder.setDocDate(c.getString(3));
                documentFolder.setHasFolder(c.getLong(4));
                result.add(documentFolder);
            } while (c.moveToNext());
        }
        close();
        Log.w(TAG, "Document Table row get all size " + result.size());
        return result;
    }
}
