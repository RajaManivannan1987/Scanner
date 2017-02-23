package com.example.scanner.scanner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.R;
import com.example.scanner.scanner.adapter.DatabaseAdapter;
import com.example.scanner.scanner.adapter.RecyclerViewFolderImage;
import com.example.scanner.scanner.asynctask.DownloadFile;
import com.example.scanner.scanner.asynctask.OCRTess;
import com.example.scanner.scanner.interfaceclass.OcrInterface;
import com.example.scanner.scanner.module.ScannedLocationImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderImagesActivity extends AppCompatActivity {
    private int i=0;
    private String scannedText="";
    private List<ScannedLocationImage> selectedImages=new ArrayList<ScannedLocationImage>();
    private TextView title;
    private RecyclerView listView;
    private List<ScannedLocationImage> images;
    private DatabaseAdapter db;
    private RecyclerViewFolderImage recyclerViewFolderImage;
    private CheckBox selectAllCheckBox;
    private ImageView ocrImageView;
    private String TAG="FolderImagesActivity";
    private ImageView backImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_images);

        db=new DatabaseAdapter(this);
        title=(TextView)findViewById(R.id.folderImageTitleTextView);
        listView=(RecyclerView)findViewById(R.id.folderImagesListView);
        selectAllCheckBox=(CheckBox)findViewById(R.id.folderImageSelectAllCheckBox);
        ocrImageView=(ImageView)findViewById(R.id.folderImageOcrImageView);
        backImageView=(ImageView)findViewById(R.id.folderImagesActivityBackImageView);

        listView.setHasFixedSize(true);
        LinearLayoutManager recyclerViewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(recyclerViewLayoutManager);
        Intent intent=getIntent();
        Long id=intent.getLongExtra("id", -1l);
        images=db.getImagesFolder(id);
        title.setText(db.getFolder(id).getName()+"");
        recyclerViewFolderImage=new RecyclerViewFolderImage(this,images);
        listView.setAdapter(recyclerViewFolderImage);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        selectAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    recyclerViewFolderImage.selectAll();
                else
                    recyclerViewFolderImage.unSelectAll();
            }
        });
        ocrImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> shareData=new ArrayList<String>();
                for(ScannedLocationImage s:recyclerViewFolderImage.getSelected()){
                    shareData.add(s.getPath());
                }
                startActivity(new Intent(FolderImagesActivity.this,ExportActivity.class).putExtra("pathArray",shareData));
//                scannedText="";
//                i=0;
//                selectedImages=recyclerViewFolderImage.getSelected();
//                if(selectedImages.size()>0){
//                    scan();
//                }else{
//                    Toast.makeText(v.getContext(),"No Images Selected",Toast.LENGTH_SHORT).show();
//                }
            }
        });

    }
    private void scan(){
        String path = Environment.getExternalStorageDirectory().toString() + "/Scan Rite/tessdata/eng.traineddata";
        File tscFile = new File(path);
        if (tscFile.exists()) {
            try {
                new OCRTess(FolderImagesActivity.this, new OcrInterface() {
                    @Override
                    public void result(String result) {
                        i++;
                        scannedText+="\n"+result;
                        if(i>=selectedImages.size()) {
                            if (!scannedText.equalsIgnoreCase(""))
                                startActivity(new Intent(FolderImagesActivity.this, ResultTessractActivity.class).putExtra("text", scannedText));
                            else
                                Toast.makeText(FolderImagesActivity.this, "No text is detected", Toast.LENGTH_SHORT).show();
                        }else{
                            scan();
                        }
                    }
                }).execute(selectedImages.get(i).getPath());
            } catch (Exception e) {
                Log.e(TAG, e + "");
            }
        } else {
            new DownloadFile(FolderImagesActivity.this).execute(Global.tesseractOcr);
        }
    }
}
