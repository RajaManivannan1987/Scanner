package com.example.scanner.scanner.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scanner.scanner.R;
import com.example.scanner.scanner.activity.FolderListActivity;
import com.example.scanner.scanner.module.DocumentFolder;
import com.example.scanner.scanner.module.ScannedLocationImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IM021 on 11/30/2015.
 */
public class FolderListListView extends BaseAdapter implements Filterable {
    String TAG = FolderListListView.class.getSimpleName();
    FolderListActivity context;
    List<DocumentFolder> documentFolderList, documentFolderListOriginal;
    LayoutInflater inflater;
    DatabaseAdapter databaseAdapter;
    ItemFilter filter = new ItemFilter();

    public FolderListListView(FolderListActivity context) {
        this.context = context;
        databaseAdapter = new DatabaseAdapter(context);
        documentFolderListOriginal = databaseAdapter.getAllDocumentFolder();
        documentFolderList = documentFolderListOriginal;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return documentFolderList.size();
    }

    @Override
    public Object getItem(int position) {
        return documentFolderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return documentFolderList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.folder_list_list_view_item, parent, false);
        }
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.folderListViewItemImageView);
        TextView name = (TextView) convertView.findViewById(R.id.folderListViewItemNameTextView);
        TextView size = (TextView) convertView.findViewById(R.id.folderListViewItemTotalImagesTextView);
        TextView type = (TextView) convertView.findViewById(R.id.folderListViewItemTypeTextView);
        TextView id = (TextView) convertView.findViewById(R.id.folderListViewIdTextVIew);
        TextView date = (TextView) convertView.findViewById(R.id.folderListViewItemDateTextView);

        List<ScannedLocationImage> scannedLocationImages = databaseAdapter.getImagesFolder(documentFolderList.get(position).getId());
        name.setText(documentFolderList.get(position).getName());
        type.setText(documentFolderList.get(position).getDocType());
        date.setText(documentFolderList.get(position).getDocDate());
        size.setText(scannedLocationImages.size() + "");
        id.setText(documentFolderList.get(position).getId() + "");

        if (documentFolderList.get(position).getHasFolder() == 0) {
            final String path = scannedLocationImages.get(scannedLocationImages.size() - 1).getPath();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap=ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path),50,50);
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }).start();
        } else {
            imageView.setImageResource(R.drawable.folder_icontemp);
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String toFilter = constraint.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();

            final List<DocumentFolder> list = documentFolderListOriginal;
            int count = list.size();
            final List<DocumentFolder> listFiltered = new ArrayList<DocumentFolder>();
            String filterableString = "";

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName() + " " + list.get(i).getDocType();
                if (filterableString.toLowerCase().contains(toFilter)) {
                    listFiltered.add(list.get(i));
                }
            }
            filterResults.values = listFiltered;
            filterResults.count = listFiltered.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            documentFolderList = (List<DocumentFolder>) results.values;
            notifyDataSetChanged();
        }
    }
}
