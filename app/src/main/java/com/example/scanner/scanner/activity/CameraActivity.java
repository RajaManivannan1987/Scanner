package com.example.scanner.scanner.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.R;
import com.example.scanner.scanner.adapter.DatabaseAdapter;
import com.example.scanner.scanner.adapter.RecycleViewCameraActivityAdapter;
import com.example.scanner.scanner.module.DefaultLocationImagePath;
import com.example.scanner.scanner.ondragswaphelper.OnStartDragListener;
import com.example.scanner.scanner.ondragswaphelper.SimpleItemTouchHelperCallback;
import com.example.scanner.scanner.view.CameraPreview;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class CameraActivity extends AppCompatActivity implements OnStartDragListener {
    private Camera mCamera;
    private CameraPreview mPreview;
    private PictureCallback mPicture;
    private ImageView capture;
    private Context myContext;
    private FrameLayout cameraPreview;
    private boolean cameraFront = false;
    private RecyclerView recyclerView;
    private RecycleViewCameraActivityAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ProgressDialog progressDialog;
    private int REQUEST_IMAGE = 512;
    private int REQUEST_IMAGING = 513;
    private DatabaseAdapter databaseAdapter;
    private static TextView snapshotTextView, scannedTextView;
    private TextView cameraPageNextTextView;
    String flashStatus = Camera.Parameters.FLASH_MODE_OFF;
    //    private TextView flashType;
    private int dataType = 1;//  1- Default location and 2- Scanned Location
    private boolean deleteStatus = false;
    private boolean cropFlag = false;
    private boolean remove = true;
    private ImageView cameraFlash;
    private ImageView gallery;
    private ImageView menuImageView, cameraPageBackIcon;
    private View toolbarOne;
    private LinearLayout toolbarTwo, menuFolderLinearLayout;
    private ItemTouchHelper mItemTouchHelper;
    private LinearLayout snapshotLinearLayout;
    private boolean reorderFlag = false;
    private List<DefaultLocationImagePath> recyclerViewList = new ArrayList<DefaultLocationImagePath>();
    private Long folderId = 0l;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraFlash = (ImageView) findViewById(R.id.cameraActivityMenuFlashImageView);
        gallery = (ImageView) findViewById(R.id.cameraActivityMenuGalleryLinearLayout);
//        flashType = (TextView) findViewById(R.id.cameraActivityMenuFlashTextView);
        menuImageView = (ImageView) findViewById(R.id.menu);
        toolbarOne = findViewById(R.id.CameraActivityBottom1ToolBar);
        toolbarTwo = (LinearLayout) findViewById(R.id.CameraActivityBottom2ToolBar);
        menuFolderLinearLayout = (LinearLayout) findViewById(R.id.cameraActivityRearrangeLinearLayout);
        cameraPageBackIcon = (ImageView) findViewById(R.id.cameraPageBackIcon);
        cameraPageNextTextView = (TextView) findViewById(R.id.cameraPageNextTextView);

        folderId = getIntent().getLongExtra(Global.folderId, 0l);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("gallery")) {
                Intent intent = new Intent(CameraActivity.this, MultiImageSelectorActivity.class);
                // whether show camera
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                // max select image amount
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
                // select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        }

        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            dataType = savedInstanceState.getInt("type");
            Log.d("DataType", dataType + "");
        }
        cameraPreview = (FrameLayout) findViewById(R.id.camera_preview);
        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);
        capture = (ImageView) findViewById(R.id.button_capture);
        snapshotLinearLayout = (LinearLayout) findViewById(R.id.snapshotLinearLayout);
        capture.setOnClickListener(captrureListener);

//        switchCamera.setOnClickListener(switchCameraListener);
        snapshotTextView = (TextView) findViewById(R.id.snapshotTextView);
        scannedTextView = (TextView) findViewById(R.id.cameraActivityMenuScansTextView);

        findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStatus = !deleteStatus;
                refreshImageGallery(deleteStatus, dataType);
            }
        });
        scannedTextView.setText(databaseAdapter.getSizeScannedLocation() + "");
        snapshotTextView.setText(databaseAdapter.getSizeDefaultLocation() + "");

        recyclerView = (RecyclerView) findViewById(R.id.cameraActivityRecycleView);
        recyclerView.setHasFixedSize(true);

        recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new RecycleViewCameraActivityAdapter(remove, getApplicationContext(), dataType, recyclerViewList, this);
        recyclerView.setAdapter(recyclerViewAdapter);

//        refreshImageGallery(deleteStatus, dataType);

        findViewById(R.id.folderImageView).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (databaseAdapter.getSizeScannedLocation() > 0)
                    startActivity(new Intent(CameraActivity.this, DocumentDetailsActivity.class));
                else
                    startActivity(new Intent(CameraActivity.this, FolderListActivity.class));
            }
        });
        menuImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarOne.getVisibility() == View.VISIBLE) {
                    toolbarOne.setVisibility(View.GONE);
                    toolbarTwo.setVisibility(View.GONE);
                } else {
                    toolbarOne.setVisibility(View.VISIBLE);
                }
            }
        });
        cameraPageNextTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toolbarTwo.getVisibility() != View.VISIBLE || dataType != 1) {
                    dataType = 1;
                    if (databaseAdapter.getSizeDefaultLocation() > 0)
                        toolbarTwo.setVisibility(View.VISIBLE);
                    else
                        toolbarTwo.setVisibility(View.GONE);
                    refreshImageGallery(false, dataType);
                } else {
                    toolbarTwo.setVisibility(View.GONE);
                }
            }
        });
        cameraPageBackIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        gallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, MultiImageSelectorActivity.class);
                // whether show camera
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                // max select image amount
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
                // select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
        findViewById(R.id.folderImageView).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, CropActivity.class);
                if (databaseAdapter.getDefaultLast() != null) {
                    DefaultLocationImagePath path = databaseAdapter.getDefaultLast();
                    intent.putExtra("path", path.getPath());
                    intent.putExtra("key", path.getKey());
                    intent.putExtra(Global.folderId, folderId);
                    startActivity(intent);
                    cropFlag = true;
                } else {
                    refreshImageGallery(false, dataType);
                    if (databaseAdapter.getSizeScannedLocation() > 0) {
                        if (folderId == 0l) {
                            startActivity(new Intent(CameraActivity.this, DocumentDetailsActivity.class));
                        } else {
                            databaseAdapter.updateFolderForScannedLocation(folderId);
                            finish();
                        }
                    } else
                        startActivity(new Intent(CameraActivity.this, FolderListActivity.class));
                }
            }
        });
        menuFolderLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reorderFlag) {
                    ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(recyclerViewAdapter);
                    mItemTouchHelper = new ItemTouchHelper(callback);
                    mItemTouchHelper.attachToRecyclerView(recyclerView);
                    reorderFlag = !reorderFlag;

                } else {
                    mItemTouchHelper = null;
                    reorderFlag = !reorderFlag;
                }

            }
        });
        findViewById(R.id.cameraActivityMenuScansLinearLayout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarTwo.getVisibility() != View.VISIBLE || dataType != 2) {
                    dataType = 2;
                    if (databaseAdapter.getSizeScannedLocation() > 0)
                        toolbarTwo.setVisibility(View.VISIBLE);
                    else
                        toolbarTwo.setVisibility(View.GONE);
                    refreshImageGallery(false, dataType);
                } else {
                    toolbarTwo.setVisibility(View.GONE);
                }
            }
        });
        snapshotLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarTwo.getVisibility() != View.VISIBLE || dataType != 1) {
                    dataType = 1;
                    if (databaseAdapter.getSizeDefaultLocation() > 0)
                        toolbarTwo.setVisibility(View.VISIBLE);
                    else
                        toolbarTwo.setVisibility(View.GONE);
                    refreshImageGallery(false, dataType);
                } else {
                    toolbarTwo.setVisibility(View.GONE);
                }
            }
        });

        if (!Global.hasFeature(this, PackageManager.FEATURE_CAMERA_FLASH))
            cameraFlash.setVisibility(View.INVISIBLE);

        cameraFlash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                switch (flashStatus) {
                    case Camera.Parameters.FLASH_MODE_AUTO:
                        flashStatus = Camera.Parameters.FLASH_MODE_ON;
                        break;
                    case Camera.Parameters.FLASH_MODE_OFF:
                        flashStatus = Camera.Parameters.FLASH_MODE_AUTO;
                        break;
                    case Camera.Parameters.FLASH_MODE_ON:
                        flashStatus = Camera.Parameters.FLASH_MODE_OFF;
                        break;
                    default:
                        flashStatus = Camera.Parameters.FLASH_MODE_OFF;
                        break;

                }
                if (findFrontFacingCamera() == -1)
                    findBackFacingCamera();
                releaseCamera();
                chooseCamera(flashStatus);
                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
        });
    }

//    void flashStatus() {
//        switch (flashStatus) {
//            case Camera.Parameters.FLASH_MODE_AUTO:
//                flashType.setText("AUTO");
//                break;
//            case Camera.Parameters.FLASH_MODE_OFF:
//                flashType.setText("OFF");
//                break;
//            case Camera.Parameters.FLASH_MODE_ON:
//                flashType.setText("ON");
//                break;
//            default:
//                flashType.setText("OFF");
//                break;
//        }
//    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() != -1) {
                //release the old camera instance
                //switch camera, from the front and the back and vice versa
                releaseCamera();
                chooseCamera(flashStatus);
            } else {
                Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
        refreshImageGallery(deleteStatus, dataType);

    }

    public void chooseCamera(String flash) {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview
                mCamera = Camera.open(cameraId);
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                int degrees = 0;
                switch (rotation) {
                    case Surface.ROTATION_0:
                        degrees = 90;
                        break; // Natural orientation
                    case Surface.ROTATION_90:
                        degrees = 0;
                        break; // Landscape left
                    case Surface.ROTATION_180:
                        degrees = 270;
                        break;// Upside down
                    case Surface.ROTATION_270:
                        degrees = 180;
                        break;// Landscape right
                }
                mCamera.setDisplayOrientation(degrees);
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                parameters.setPreviewSize(previewSizes.get(4).width, previewSizes.get(4).height);
                if (Global.hasFeature(this, PackageManager.FEATURE_CAMERA_AUTOFOCUS))
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                if (Global.hasFeature(this, PackageManager.FEATURE_CAMERA_FLASH))
                    parameters.setFlashMode(flash);
//                flashStatus();
                mCamera.setParameters(parameters);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview
                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }


    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                // Get the result list of select image paths
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // do your logic
                for (int i = 0; i < path.size(); i++) {
                    File destinationFile = Global.getOutputMediaFile(i, getApplicationContext());
                    File sourceFile = new File(path.get(i));
                    if (sourceFile.exists()) {
                        try {
                            Global.copyDirectory(sourceFile, destinationFile);
                        } catch (Exception e) {
                        }
                        databaseAdapter.insertDefaultLocation(destinationFile.getAbsolutePath());
                    } else {
                        Log.e("Image Gallery", "File not Exist " + path.get(i));
                    }
                }
                refreshImageGallery(false, dataType);
                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(CameraActivity.this, ImagingActivity.class);
            intent.putExtra("path", Crop.getOutput(result).getPath());
            startActivityForResult(intent, REQUEST_IMAGING);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private PictureCallback getPictureCallback() {
        PictureCallback picture = new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file

                File pictureFile = Global.getOutputMediaFile(0, getApplicationContext());

                if (pictureFile == null) {
                    return;
                }
                try {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(rotate90(data));
                    fos.close();
                    databaseAdapter.insertDefaultLocation(pictureFile.getAbsolutePath());
//                    Toast toast = Toast.makeText(myContext, "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
//                    toast.show();

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }
                dataType = 1;
                refreshImageGallery(false, dataType);
                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }

    OnClickListener captrureListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            mCamera.enableShutterSound(true);
            if (Global.hasFeature(v.getContext(), PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success)
                            mCamera.takePicture(null, null, mPicture);
                    }
                });
            } else {
                mCamera.takePicture(null, null, mPicture);
            }
        }
    };

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    //  1- Default location and 2- Scanned Location
    public void refreshImageGallery(boolean remove, int dataType) {
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        recyclerViewAdapter.setDeleteStatus(remove);
        if (dataType == 1) {
            recyclerViewList.clear();
            recyclerViewList.addAll(databaseAdapter.getAllDefaultLocation());
            recyclerViewAdapter.setDataType(1);
            recyclerViewAdapter.notifyDataSetChanged();
        } else {
            recyclerViewList.clear();
            recyclerViewAdapter.setDataType(2);
            recyclerViewList.addAll(databaseAdapter.getAllGalleryScannedLocation());
            recyclerViewAdapter.notifyDataSetChanged();
        }
        scannedTextView.setText(databaseAdapter.getSizeScannedLocation() + "");
        snapshotTextView.setText(databaseAdapter.getSizeDefaultLocation() + "");
        deleteStatus = remove;
    }

    public static void refreshCount(int scannedLength, int snapshotLength) {
        scannedTextView.setText(scannedLength + "");
        snapshotTextView.setText(snapshotLength + "");
    }

    private byte[] rotate90(byte[] bytes) {
        Matrix mat = new Matrix();
        mat.postRotate(90);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (mItemTouchHelper != null)
            mItemTouchHelper.startDrag(viewHolder);
    }
}