package com.example.scanner.scanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.R;
import com.example.scanner.scanner.activity.CameraActivity;
import com.example.scanner.scanner.activity.ImagePreviewActivity;
import com.example.scanner.scanner.module.DefaultLocationImagePath;
import com.example.scanner.scanner.module.ScannedLocationImage;
import com.example.scanner.scanner.ondragswaphelper.ItemTouchHelperAdapter;
import com.example.scanner.scanner.ondragswaphelper.ItemTouchHelperViewHolder;
import com.example.scanner.scanner.ondragswaphelper.OnStartDragListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IM021 on 11/6/2015.
 */
public class RecycleViewCameraActivityAdapter extends RecyclerView.Adapter<RecycleViewCameraActivityAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private String TAG = "RecycleViewCameraActivityAdapter";
    List<DefaultLocationImagePath> listPath=new ArrayList<DefaultLocationImagePath>();
    boolean isDelete;
    Context context;
    DatabaseAdapter databaseAdapter;
    int dataType;//  1- Default location and 2- Scanned Location
    OnStartDragListener dragStartListener;

    public RecycleViewCameraActivityAdapter(boolean isDelete, Context context,int dataType, List<DefaultLocationImagePath> listPath, OnStartDragListener dragStartListener) {
        this.isDelete = isDelete;
        this.context = context;
        databaseAdapter = new DatabaseAdapter(context);
        this.listPath = listPath;
        this.dragStartListener = dragStartListener;
        this.dataType=dataType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_camera_gallery_recycleview_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int text = position + 1;
        holder.number.setText(text + "");
        File imageFile = new File(listPath.get(position).getPath());
        Picasso.with(context)
                .load(imageFile)
                .placeholder(R.drawable.default_error)
                        //.error(R.drawable.default_error)
                .resize(75, 75)
                .centerCrop()
                .into(holder.image);
        holder.image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        if (isDelete) {
            holder.remove.setVisibility(View.VISIBLE);
            holder.remove.setTag(listPath.get(position).getKey());
            holder.relativeLayout.setOnClickListener(null);
        } else {
            holder.remove.setVisibility(View.GONE);
            holder.relativeLayout.setTag(listPath.get(text - 1).getPath());
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ImagePreviewActivity.class);
                    intent.putExtra("path", v.findViewById(R.id.cameraRecycleViewRelativeLayout).getTag().toString());
                    v.getContext().startActivity(intent);
                }
            });
        }
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ImageView imageView = (ImageView) v.findViewById(R.id.cameraRecycleViewRemoveImageView);
                    remove((long) imageView.getTag());
                } catch (Exception e) {
                    Log.e("RecycleView", e.toString());
                }
            }
        });

    }

    public void setDataType(int dataType){
        this.dataType=dataType;
    }

    public void setDeleteStatus(boolean deleteStatus){
        this.isDelete=deleteStatus;
        notifyDataSetChanged();
    }
    public void remove(long item) throws IOException {
        int position = getlocation(item);
        listPath.remove(position);
        notifyItemRemoved(position);
        if (dataType == 1) {
            Global.deleteFile(context, databaseAdapter.getDefaultLocation(item).getPath());
            databaseAdapter.deleteDefault(item);
        } else {
            ScannedLocationImage scannedLocationImage = databaseAdapter.getScannedLocation(item);
            Global.deleteFile(context, scannedLocationImage.getPath());
            Global.deleteFile(context, scannedLocationImage.getOriginalImagePath());
            databaseAdapter.deleteScannedLocation(item);
        }
        CameraActivity.refreshCount(databaseAdapter.getSizeScannedLocation(), databaseAdapter.getSizeDefaultLocation());
    }

    public int getlocation(long string) {
        for (int i = 0; i < listPath.size(); i++) {
            if (listPath.get(i).getKey() == string) {
                return i;
            }
        }
        return -1;
    }

    public void moveItem(final Long fromPosition,final Long toPosition) {
        if(dataType==1){
            databaseAdapter.swapDefaultLocation(fromPosition,toPosition);
        }else{
            databaseAdapter.swapScannedLocation(fromPosition,toPosition);
        }
    }

    @Override
    public void onItemDismiss(int position) {
//        listPath.remove(position);
//        notifyItemRemoved(position);
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "fromPosition-" + fromPosition + " toPosition-" + toPosition + " Total Length-" + listPath.size());
        Collections.swap(listPath, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        moveItem(listPath.get(fromPosition).getKey(),listPath.get(toPosition).getKey());
        notifyDataSetChanged();
        return true;
    }

    @Override
    public int getItemCount() {
        return listPath.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        // each data item is just a string in this case
        public TextView number;
        public ImageView image, remove;
        public RelativeLayout relativeLayout;

        public ViewHolder(View v) {
            super(v);
            number = (TextView) v.findViewById(R.id.cameraRecycleViewImageTextView);
            image = (ImageView) v.findViewById(R.id.cameraRecycleViewImageView);
            remove = (ImageView) v.findViewById(R.id.cameraRecycleViewRemoveImageView);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.cameraRecycleViewRelativeLayout);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}