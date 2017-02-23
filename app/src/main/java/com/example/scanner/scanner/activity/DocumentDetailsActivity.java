package com.example.scanner.scanner.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.R;
import com.example.scanner.scanner.adapter.DatabaseAdapter;
import com.example.scanner.scanner.sharedpreference.SharedPreferenceSettings;

public class DocumentDetailsActivity extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    EditText name;
    CheckBox hasFolder;
    ImageView back,cancel,submit;
    DatabaseAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_details);

        listView=(ListView)findViewById(R.id.documentDetailsActivityPageSizeListView);
        arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.document_list_item,getResources().getStringArray(R.array.doc_details_listview));
        listView.setAdapter(arrayAdapter);
        listView.setItemChecked(new SharedPreferenceSettings(this).getDefaultPageSize(), true);
        db=new DatabaseAdapter(this);

        name=(EditText)findViewById(R.id.documentDetailsActivityNameEditText);
        hasFolder=(CheckBox)findViewById(R.id.documentDetailsActivityFolderEnableCheckBox);
        back=(ImageView)findViewById(R.id.documentDetailsActivityBackImageView);
        cancel=(ImageView)findViewById(R.id.documentDetailsActivityCancelImageView);
        submit=(ImageView)findViewById(R.id.documentDetailsActivityOkImageView);

        name.setText(Global.unTitle(getApplicationContext()));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderName="";
                String[] type=v.getContext().getResources().getStringArray(R.array.doc_details_listview);
                int hasFolder1=0;
                if(hasFolder.isChecked()){
                    hasFolder1=1;
                }
                folderName=name.getText().toString().trim().replace("( )+"," ");
                Long i=db.insertFolder(folderName, type[listView.getCheckedItemPosition()], hasFolder1);
                db.updateFolderForScannedLocation(i);
                finish();
            }
        });
    }
}
