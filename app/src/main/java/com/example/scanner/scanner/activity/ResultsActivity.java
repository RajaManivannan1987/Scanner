package com.example.scanner.scanner.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.scanner.scanner.adapter.DatabaseAdapter;
import com.example.scanner.scanner.asynctask.AbbyyAsyncTask;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;


public class ResultsActivity extends AppCompatActivity {

    String outputPath;
    TextView tv;
    Long id;
    DatabaseAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db=new DatabaseAdapter(this);
        tv = new TextView(this);
        setContentView(tv);

        String imageUrl = "unknown";

        Bundle extras = getIntent().getExtras();
        if( extras != null) {
            id=extras.getLong("ID");
            imageUrl = extras.getString("IMAGE_PATH" );
            outputPath = extras.getString( "RESULT_PATH" );
        }

        // Starting recognition process
        new AbbyyAsyncTask(ResultsActivity.this).execute(imageUrl, outputPath);
    }

    public void updateResults(Boolean success) {
        if (!success)
            return;
        try {
            StringBuffer contents = new StringBuffer();

            FileInputStream fis = openFileInput(outputPath);
            try {
                Reader reader = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufReader = new BufferedReader(reader);
                String text = null;
                while ((text = bufReader.readLine()) != null) {
                    contents.append(text).append(System.getProperty("line.separator"));
                }
            } finally {
                fis.close();
            }

            displayMessage(contents.toString());
            db.updateOcrPathScannedLocation(id,outputPath);
        } catch (Exception e) {
            displayMessage("Error: " + e.getMessage());
        }
    }

    public void displayMessage( String text )
    {
        tv.post(new MessagePoster(text));
    }



    class MessagePoster implements Runnable {
        public MessagePoster( String message )
        {
            _message = message;
        }

        public void run() {
            tv.append( _message + "\n" );
            setContentView( tv );
        }

        private final String _message;
    }
}
