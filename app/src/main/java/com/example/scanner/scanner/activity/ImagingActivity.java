package com.example.scanner.scanner.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.R;
import com.example.scanner.scanner.adapter.DatabaseAdapter;
import com.example.scanner.scanner.adapter.FolderListListView;
import com.example.scanner.scanner.controller.AppController;
import com.example.scanner.scanner.module.DefaultLocationImagePath;
import com.example.scanner.scanner.services.ImageProcessing;
import com.example.scanner.scanner.sharedpreference.SharedPreferenceSettings;
import com.soundcloud.android.crop.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagingActivity extends AppCompatActivity {

    Bitmap bitmap, changedBitmap;
    boolean bwFlag = false;
    ProgressDialog progressDialog;
    ImageView back, blackWhite, leftRotate, rightRotate, imageView, original, enhance, enhanceOk, submit, delete, crop;
    SeekBar leftSeekBar, rightSeekBar;
    float leftSeekBarValue = 0, rightSeekBarValue = 1;
    Long key, docId;
    DatabaseAdapter databaseAdapter;
    Intent i;
    private TextView snapshotTextView, scanTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imaging);

        back = (ImageView) findViewById(R.id.back);
        blackWhite = (ImageView) findViewById(R.id.backWhite);
        leftRotate = (ImageView) findViewById(R.id.leftRotate);
        rightRotate = (ImageView) findViewById(R.id.rightRotate);
        imageView = (ImageView) findViewById(R.id.imageView);
        original = (ImageView) findViewById(R.id.original);
        enhance = (ImageView) findViewById(R.id.enhance);
        enhanceOk = (ImageView) findViewById(R.id.enhanceOk);
        leftSeekBar = (SeekBar) findViewById(R.id.leftSeekBar);
        rightSeekBar = (SeekBar) findViewById(R.id.rightSeekBar);
        submit = (ImageView) findViewById(R.id.ok);
        delete = (ImageView) findViewById(R.id.delete);
        crop = (ImageView) findViewById(R.id.crop);
        snapshotTextView = (TextView) findViewById(R.id.imageActivitySnapshotsTextView);
        scanTextView = (TextView) findViewById(R.id.imageActivityScanTextView);

        databaseAdapter = new DatabaseAdapter(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        snapshotTextView.setText(databaseAdapter.getSizeDefaultLocation() + "");
        scanTextView.setText(databaseAdapter.getSizeScannedLocation() + "");
        i = getIntent();
        key = i.getExtras().getLong("key");
        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImagingActivity.this, CropActivity.class).putExtra("key", key).putExtra("path", i.getExtras().getString("path")).putExtra(Global.folderId,i.getExtras().getLong(Global.folderId)));
                finish();
            }
        });
        try {
            bitmap = ((AppController) getApplication()).cropped;
        } catch (Exception e) {
            Log.e(e.toString());
        }
        changedBitmap = bitmap;
        imageView.setImageBitmap(bitmap);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        enhance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        enhanceOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        if (new SharedPreferenceSettings(this).getAutoImageEnhancement()) {
            progressDialog.show();
            new Thread(new Runnable() {
                public void run() {
                    changedBitmap = ImageProcessing.doGreyscale(bitmap);
                    imageView.post(new Runnable() {
                        public void run() {
                            imageView.setImageBitmap(changedBitmap);
                            bwFlag = true;
                            progressDialog.dismiss();
                        }
                    });
                }
            }).start();
        }

        blackWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bwFlag) {
                    bwFlag = true;
                    bitmapEnhance();
                } else {
                    Toast.makeText(v.getContext(), "Already in Black & White", Toast.LENGTH_SHORT).show();
                }
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        leftRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                new Thread(new Runnable() {
                    public void run() {
                        bitmap = rotateBitmap(bitmap, 90);
                        changedBitmap = rotateBitmap(changedBitmap, 90);
                        imageView.post(new Runnable() {
                            public void run() {
                                imageView.setImageBitmap(changedBitmap);
                                progressDialog.dismiss();
                            }
                        });
                    }
                }).start();
            }
        });

        rightRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                new Thread(new Runnable() {
                    public void run() {
                        bitmap = rotateBitmap(bitmap, 270);
                        changedBitmap = rotateBitmap(changedBitmap, 270);
                        imageView.post(new Runnable() {
                            public void run() {
                                imageView.setImageBitmap(changedBitmap);
                                progressDialog.dismiss();
                            }
                        });
                    }
                }).start();
            }
        });

        original.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changedBitmap = bitmap;
                imageView.setImageBitmap(bitmap);
                bwFlag = false;
            }
        });

        leftSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                leftSeekBarValue = (progress - 75);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bitmapEnhance();
            }
        });

        rightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float i=((float) progress)/100f;
                rightSeekBarValue = (float) 1+ (i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                bitmapEnhance();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File pictureFile = Global.getOutputMediaFile(0, v.getContext());
                if (pictureFile == null) {
                    return;
                }
                try {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(Global.convertBitmapToByteArray(changedBitmap));
                    fos.close();
                    databaseAdapter.insertScannedLocation(key, pictureFile.getAbsolutePath(), 0);
                    Intent intent = new Intent(ImagingActivity.this, CropActivity.class);
                    if (databaseAdapter.getDefaultLast() != null) {
                        DefaultLocationImagePath path = databaseAdapter.getDefaultLast();
                        intent.putExtra("path", path.getPath());
                        intent.putExtra("key", path.getKey());
                        intent.putExtra(Global.folderId,i.getExtras().getLong(Global.folderId));
                        startActivity(intent);
                    } else {
                        if(i.getExtras().getLong(Global.folderId)==0l) {
                            startActivity(new Intent(ImagingActivity.this, DocumentDetailsActivity.class));
                        }else{
                            databaseAdapter.updateFolderForScannedLocation(i.getExtras().getLong(Global.folderId));
                            startActivity(new Intent(ImagingActivity.this, FolderListActivity.class));

                            finish();
                        }
                    }
                    finish();
                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }
            }
        });

    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    void bitmapEnhance() {
        android.util.Log.d("ImagingActivity","rightSeekBarValue-"+rightSeekBarValue+" leftSeekBarValue-"+leftSeekBarValue);
        progressDialog.show();
        new Thread(new Runnable() {
            public void run() {
                changedBitmap = ImageProcessing.changeBitmapContrastBrightness(bitmap, rightSeekBarValue, leftSeekBarValue);
                imageView.post(new Runnable() {
                    public void run() {
                        if (!bwFlag)
                            imageView.setImageBitmap(changedBitmap);
//                        bwFlag = false;
                        progressDialog.dismiss();
                        if (bwFlag) {
                            progressDialog.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    changedBitmap = ImageProcessing.doGreyscale(changedBitmap);
                                    imageView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageView.setImageBitmap(changedBitmap);
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                });
            }
        }).start();
    }

}
