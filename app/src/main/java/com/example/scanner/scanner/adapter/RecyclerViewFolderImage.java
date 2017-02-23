package com.example.scanner.scanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.R;
import com.example.scanner.scanner.activity.ImagePreviewActivity;
import com.example.scanner.scanner.activity.ResultTessractActivity;
import com.example.scanner.scanner.asynctask.DownloadFile;
import com.example.scanner.scanner.asynctask.OCRTess;
import com.example.scanner.scanner.interfaceclass.OcrInterface;
import com.example.scanner.scanner.module.ScannedLocationImage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IM021 on 11/30/2015.
 */
public class RecyclerViewFolderImage extends RecyclerView.Adapter<RecyclerViewFolderImage.ViewHolder> {
    private String TAG = "RecyclerViewFolderImage";
    private Context context;
    List<ScannedLocationImage> scannedLocationImages= new ArrayList<ScannedLocationImage>();
    private List<ScannedLocationImage> selected = new ArrayList<ScannedLocationImage>();

    public RecyclerViewFolderImage(Context context, List<ScannedLocationImage> scannedLocationImages) {
        this.context = context;
        this.scannedLocationImages = scannedLocationImages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_image_recycler_view_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ScannedLocationImage scannedLocationImage = scannedLocationImages.get(position);
        File file = new File(scannedLocationImage.getPath());
        Picasso.with(context)
                .load(file)
                .placeholder(R.drawable.default_error)
                //.error(R.drawable.default_error)
                .resize(75, 75)
                .centerCrop()
                .into(holder.imageView);
        if (selected.contains(scannedLocationImages.get(position))){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!selected.contains(scannedLocationImages.get(position)))
                        selected.add(scannedLocationImages.get(position));
                } else {
                    selected.remove(scannedLocationImages.get(position));
                }
            }
        });
        holder.imageView.setTag(scannedLocationImages.get(position).getPath());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) v.findViewById(R.id.folderImageRecyclerViewImageView);
                Intent intent = new Intent(context, ImagePreviewActivity.class);
                intent.putExtra("path", imageView.getTag().toString());
                context.startActivity(intent);
            }
        });
        holder.ocr.setTag(scannedLocationImage);
        holder.ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView ocr = (ImageView) v.findViewById(R.id.folderImageOcrImageView);
                ScannedLocationImage scannedLocationImage1 = (ScannedLocationImage) ocr.getTag();
//                try {
//                    Intent results = new Intent(context, ResultsActivity.class);
//                    results.putExtra("ID", scannedLocationImage1.getKey());
//                    results.putExtra("IMAGE_PATH", scannedLocationImage1.getPath());
//                    results.putExtra("RESULT_PATH", Global.getOutputTextFile(0, v.getContext()));
//                    context.startActivity(results);
//                    Toast.makeText(v.getContext(), "Ocr Generated", Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                String path = Environment.getExternalStorageDirectory().toString() + "/Scan Rite/tessdata/eng.traineddata";
                File tscFile = new File(path);
                if (tscFile.exists()) {
                    try {
                        new OCRTess(context, new OcrInterface() {
                            @Override
                            public void result(String result) {
                                if (!result.equalsIgnoreCase(""))
                                    context.startActivity(new Intent(context, ResultTessractActivity.class).putExtra("text", result));
                                else
                                    Toast.makeText(context, "No text is detected", Toast.LENGTH_SHORT).show();
                            }
                        }).execute(scannedLocationImage1.getPath());
                    } catch (Exception e) {
                        Log.e(TAG, e + "");
                    }
                } else {
                    new DownloadFile(context).execute(Global.tesseractOcr);
                }
            }
        });
    }
   public void selectAll(){
       selected.clear();
       for (ScannedLocationImage s:scannedLocationImages) {
            selected.add(s);
       }
       notifyDataSetChanged();
   }
    public void unSelectAll(){
        selected.clear();
        notifyDataSetChanged();
    }
   public List<ScannedLocationImage> getSelected(){
       return selected;
   }
    @Override
    public int getItemCount() {
        return scannedLocationImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView, ocr, option;
        public LinearLayout linearLayout;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.folderImageRecyclerViewImageView);
            ocr = (ImageView) itemView.findViewById(R.id.folderImageOcrImageView);
            option = (ImageView) itemView.findViewById(R.id.folderImageOptionsImageView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.folderImageRecyclerLinearLayout);
            checkBox = (CheckBox) itemView.findViewById(R.id.folderImageOptionsCheckBox);
        }
    }
}
