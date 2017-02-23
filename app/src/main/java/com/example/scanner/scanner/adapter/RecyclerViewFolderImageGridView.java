package com.example.scanner.scanner.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.R;
import com.example.scanner.scanner.activity.CameraActivity;
import com.example.scanner.scanner.activity.FolderListActivity;
import com.example.scanner.scanner.activity.ImagePreviewActivity;
import com.example.scanner.scanner.module.ScannedLocationImage;
import com.example.scanner.scanner.ondragswaphelper.ItemTouchHelperAdapter;
import com.example.scanner.scanner.ondragswaphelper.ItemTouchHelperViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sanjay on 3/16/16.
 */
public class RecyclerViewFolderImageGridView extends RecyclerView.Adapter<RecyclerViewFolderImageGridView.CustomHolder> implements ItemTouchHelperAdapter {
    private String TAG = "RecyclerViewFolderImageGridView";
    private FolderListActivity context;
    private Long folderId;
    private List<ScannedLocationImage> listData = new ArrayList<ScannedLocationImage>(), selectListData = new ArrayList<ScannedLocationImage>();
    private DatabaseAdapter databaseAdapter;
    private boolean multiSelect = false;
    private int CONTENT = 1, ADDIMAGES = 2;

    public RecyclerViewFolderImageGridView(FolderListActivity context, Long folderId) {
        this.context = context;
        this.folderId = folderId;
        databaseAdapter = new DatabaseAdapter(context);
        listData = databaseAdapter.getImagesFolder(folderId);

    }

    @Override
    public CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_recycleview_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_recycleview_item_add, parent, false);
        }
        CustomHolder holder = new CustomHolder(view, viewType);
        return holder;
    }

    public void setMultiSelect(boolean isSelect) {
        multiSelect = isSelect;
        context.multiSelectOn(isSelect);
        if (!multiSelect) {
            selectListData.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(CustomHolder holder, final int position) {
        if (getItemViewType(position) == CONTENT) {
            int count = position + 1;
            holder.textView.setText("" + count);
            Picasso.with(context)
                    .load(new File(listData.get(position).getPath()))
                    .placeholder(R.drawable.default_error)
                    //.error(R.drawable.default_error)
                    .resize(75, 75)
                    .centerCrop()
                    .into(holder.imageview);
            if (!multiSelect) {
                holder.multiSelectRelativeLayout.setVisibility(View.GONE);
                holder.checkBox.setChecked(false);
                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ImagePreviewActivity.class);
                        intent.putExtra("path", listData.get(position).getPath());
                        v.getContext().startActivity(intent);
                    }
                });
            } else {
                holder.multiSelectRelativeLayout.setVisibility(View.VISIBLE);
                holder.relativeLayout.setOnClickListener(null);
                holder.checkBox.setChecked(selectListData.contains(listData.get(position)));
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (!selectListData.contains(listData.get(position)))
                                selectListData.add(listData.get(position));
                        } else {
                            if (selectListData.contains(listData.get(position)))
                                selectListData.remove(listData.get(position));
                        }
                    }
                });
            }
        } else {
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CameraActivity.class);
                    intent.putExtra(Global.folderId, folderId);
                    context.startActivity(intent);
                    context.closeDrawer();
                }
            });
        }
    }

    public List<ScannedLocationImage> getSelectListData() {
        return selectListData;
    }

    @Override
    public int getItemCount() {
        if (multiSelect || context.reorderFlag)
            return listData.size();
        return listData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < listData.size()) {
            return CONTENT;
        }
        return ADDIMAGES;
    }

    public void selectAll() {
        selectListData.clear();
        selectListData.addAll(listData);
        notifyDataSetChanged();
    }

    public void unSelectAll() {
        selectListData.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "fromPosition-" + fromPosition + " toPosition-" + toPosition + " Total Length-" + listData.size());
        Collections.swap(listData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        databaseAdapter.swapScannedLocation(listData.get(fromPosition).getKey(), listData.get(toPosition).getKey());
        notifyDataSetChanged();
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    class CustomHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public ImageView imageview;
        public View view;
        public TextView textView;
        public RelativeLayout relativeLayout, multiSelectRelativeLayout;
        public CheckBox checkBox;


        public CustomHolder(View itemView, int contentType) {
            super(itemView);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            if (contentType == CONTENT) {
                imageview = (ImageView) itemView.findViewById(R.id.cameraRecycleViewImageView);
                view = itemView.findViewById(R.id.cameraRecycleViewViewImageView);
                textView = (TextView) itemView.findViewById(R.id.cameraRecycleViewImageTextView);
                multiSelectRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.cameraRecycleViewMultiSelectRelativeLayout);
                checkBox = (CheckBox) itemView.findViewById(R.id.cameraRecycleViewMultiSelectCheckBox);
            }
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
