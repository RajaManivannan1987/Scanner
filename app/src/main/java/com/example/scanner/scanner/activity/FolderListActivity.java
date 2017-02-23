package com.example.scanner.scanner.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.R;
import com.example.scanner.scanner.adapter.DatabaseAdapter;
import com.example.scanner.scanner.adapter.FolderListListView;
import com.example.scanner.scanner.adapter.RecyclerViewFolderImageGridView;
import com.example.scanner.scanner.asynctask.DownloadFile;
import com.example.scanner.scanner.asynctask.OCRTess;
import com.example.scanner.scanner.interfaceclass.OcrInterface;
import com.example.scanner.scanner.module.DocumentFolder;
import com.example.scanner.scanner.module.ScannedLocationImage;
import com.example.scanner.scanner.ondragswaphelper.OnStartDragListener;
import com.example.scanner.scanner.ondragswaphelper.SimpleItemTouchHelperCallback;
import com.example.scanner.scanner.view.SwipeDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderListActivity extends AppCompatActivity implements OnStartDragListener {
    ListView folderListView;
    FolderListListView folderListListView;
    EditText search;
    ImageView camera;
    //    DrawerLayout drawerLayout;
    RecyclerView imagesListRecyclerView;
    RecyclerViewFolderImageGridView adapter;
    private TextView folderName;
    private DatabaseAdapter databaseAdapter;
    private CheckBox folderImageCheckBox;
    private ImageView folderImageOcrImageView, folderImageCameraImageView, folderImageGalleryImageView, folderReorderingImageView;
    private boolean multiFlag = false;
    private int i = 0;
    private String scannedText = "";
    private List<ScannedLocationImage> selectedImages = new ArrayList<ScannedLocationImage>();
    private SwipeDetector swipeDetector = new SwipeDetector();
    private ItemTouchHelper mItemTouchHelper;
    public boolean reorderFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);

        databaseAdapter = new DatabaseAdapter(this);
        folderImageOcrImageView = (ImageView) findViewById(R.id.folderImageOcrImageView);
        folderReorderingImageView = (ImageView) findViewById(R.id.folderImageReorderImageView);
        folderListView = (ListView) findViewById(R.id.folderListActivityListView);
        folderListListView = new FolderListListView(this);
        folderListView.setAdapter(folderListListView);
        folderListView.setOnTouchListener(swipeDetector);

//        folderListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                final DocumentFolder d = (DocumentFolder) folderListListView.getItem(position);
//                final AlertDialog deleteDialog = new AlertDialog.Builder(FolderListActivity.this).create();
//                deleteDialog.setTitle("Delete Folder");
//                deleteDialog.setMessage("Are you Delete " + d.getName() + " Folder");
//                deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        deleteFolder(d.getId());
//                        deleteDialog.dismiss();
//                    }
//                });
//                deleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        deleteDialog.dismiss();
//                    }
//                });
//                return true;
//            }
//        });
        folderImageCameraImageView = (ImageView) findViewById(R.id.folderImageCameraImageView);
        folderImageCameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FolderListActivity.this, CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
        });
        folderImageGalleryImageView = (ImageView) findViewById(R.id.folderImageGalleryImageView);
        folderImageGalleryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FolderListActivity.this, CameraActivity.class).putExtra("gallery", true));
                finish();
            }
        });
        folderImageCheckBox = (CheckBox) findViewById(R.id.folderImageCheckBox);
        imagesListRecyclerView = (RecyclerView) findViewById(R.id.folderListActivityImagesRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        imagesListRecyclerView.setLayoutManager(gridLayoutManager);
        folderName = (TextView) findViewById(R.id.folderImageTitleTextView);
        camera = (ImageView) findViewById(R.id.folderListActivityCameraImageView);
//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        search = (EditText) findViewById(R.id.folderListActivitySearchEditText);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                folderListListView.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        folderImageOcrImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderReorderingFalse();
                if (!multiFlag) {
                    multiFlag = true;
                    multiSelectOn(multiFlag);
                    adapter.setMultiSelect(multiFlag);
                } else {
                    if (adapter.getSelectListData().size() > 0) {
                        i = 0;
                        selectedImages = adapter.getSelectListData();
                        ArrayList<String> shareData = new ArrayList<String>();
                        for (ScannedLocationImage s : adapter.getSelectListData()) {
                            shareData.add(s.getPath());
                        }
                        startActivity(new Intent(FolderListActivity.this, ExportActivity.class).putExtra("pathArray", shareData));
                    } else {
                        multiFlag = false;
                        multiSelectOn(multiFlag);
                        adapter.setMultiSelect(multiFlag);
                    }
                }
            }
        });
        folderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView idTextView = (TextView) view.findViewById(R.id.folderListViewIdTextVIew);
                String idItem = idTextView.getText().toString();
//                Intent intent = new Intent(FolderListActivity.this, FolderImagesActivity.class);
//                intent.putExtra("id", Long.parseLong(idItem));
//                startActivity(intent);
                if (swipeDetector.swipeDetected()) {
                    if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        deleteAction(position);
                    } else {

                    }
                } else {
                    openDrawer(Long.parseLong(idItem));
                }

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.folderListActivityImagesLinearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });
        folderImageCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    adapter.selectAll();
                else
                    adapter.unSelectAll();
            }
        });
        folderReorderingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleSelectFalse();
                if (!reorderFlag) {
                    ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
                    mItemTouchHelper = new ItemTouchHelper(callback);
                    mItemTouchHelper.attachToRecyclerView(imagesListRecyclerView);
                    reorderFlag = !reorderFlag;
                    adapter.notifyDataSetChanged();
                } else {
                    mItemTouchHelper = null;
                    reorderFlag = !reorderFlag;
                    adapter.notifyDataSetChanged();
                }

            }
        });

    }

    public void folderReorderingFalse(){
        mItemTouchHelper = null;
        reorderFlag = false;
        adapter.notifyDataSetChanged();
    }
    public void multipleSelectFalse(){
        multiFlag = false;
        multiSelectOn(multiFlag);
        adapter.setMultiSelect(multiFlag);
    }
    public void multiSelectOn(boolean is) {
        if (is) {
            folderImageCheckBox.setVisibility(View.VISIBLE);
        } else {
            folderImageCheckBox.setVisibility(View.GONE);
        }
    }

    public void closeDrawer() {
        Animation out = AnimationUtils.makeOutAnimation(this, true);
        reorderFlag=false;
        mItemTouchHelper = null;
        findViewById(R.id.folderListActivityImagesLinearLayout).startAnimation(out);
        findViewById(R.id.folderListActivityImagesLinearLayout).setVisibility(View.GONE);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.folderListActivityImagesLinearLayout1).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void openDrawer(Long id) {
        folderName.setText(databaseAdapter.getFolder(id).getName());
        findViewById(R.id.folderListActivityImagesLinearLayout1).setVisibility(View.VISIBLE);
        Animation in = AnimationUtils.makeInAnimation(this, false);
        findViewById(R.id.folderListActivityImagesLinearLayout).startAnimation(in);
        findViewById(R.id.folderListActivityImagesLinearLayout).setVisibility(View.VISIBLE);
        adapter = new RecyclerViewFolderImageGridView(FolderListActivity.this, id);
        imagesListRecyclerView.setAdapter(adapter);
        Global.hideKeyboard(FolderListActivity.this);
        multiFlag = false;
        multiSelectOn(multiFlag);
        adapter.setMultiSelect(multiFlag);
    }

    private void scan() {
        String path = Environment.getExternalStorageDirectory().toString() + "/Scan Rite/tessdata/eng.traineddata";
        File tscFile = new File(path);
        if (tscFile.exists()) {
            try {
                new OCRTess(FolderListActivity.this, new OcrInterface() {
                    @Override
                    public void result(String result) {
                        i++;
                        scannedText += "\n" + result;
                        if (i >= selectedImages.size()) {
                            if (!scannedText.equalsIgnoreCase(""))
                                startActivity(new Intent(FolderListActivity.this, ResultTessractActivity.class).putExtra("text", scannedText));
                            else
                                Toast.makeText(FolderListActivity.this, "No text is detected", Toast.LENGTH_SHORT).show();
                        } else {
                            scan();
                        }
                    }
                }).execute(selectedImages.get(i).getPath());
            } catch (Exception e) {
                Log.e("Scanning Ocr", e + "");
            }
        } else {
            new DownloadFile(FolderListActivity.this).execute(Global.tesseractOcr);
        }
    }

    private void deleteAction(int position) {
        final DocumentFolder d = (DocumentFolder) folderListListView.getItem(position);
        final AlertDialog deleteDialog = new AlertDialog.Builder(FolderListActivity.this).create();
        deleteDialog.setTitle("Delete Folder");
        deleteDialog.setMessage("Are you Delete " + d.getName() + " Folder");
        deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFolder(d.getId());
                deleteDialog.dismiss();
            }
        });
        deleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog.show();
    }

    private boolean deleteFolder(Long folderId) {
        List<ScannedLocationImage> list = databaseAdapter.getImagesFolder(folderId);
        for (ScannedLocationImage image : list) {
            File file = new File(image.getPath());
            file.delete();
            databaseAdapter.deleteScannedLocation(image.getKey());
        }
        databaseAdapter.deleteFolder(folderId);
        folderListListView = new FolderListListView(this);
        folderListView.setAdapter(folderListListView);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.folderListActivityImagesLinearLayout1).getVisibility() == View.VISIBLE) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (folderListListView != null) {
            folderListListView.notifyDataSetChanged();
            folderListListView.getFilter().filter(search.getText().toString());
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (mItemTouchHelper != null)
            mItemTouchHelper.startDrag(viewHolder);
    }

}
