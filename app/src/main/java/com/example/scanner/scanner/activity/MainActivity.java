package com.example.scanner.scanner.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.scanner.scanner.R;
import com.example.scanner.scanner.Utility.CommonMethods;


public class MainActivity extends AppCompatActivity {

    private ImageButton helpImageButton, cameraImageButton, galleryImageButton, settingsImageButton, folderImageButton;
    Toast toast;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //UI declaration Starts
        helpImageButton = (ImageButton) findViewById(R.id.mainActivityHelpImageButton);
        settingsImageButton = (ImageButton) findViewById(R.id.mainActivitySettingsImageButton);
        cameraImageButton = (ImageButton) findViewById(R.id.mainActivityCameraImageButton);
        galleryImageButton = (ImageButton) findViewById(R.id.mainActivityGalleryImageButton);
        folderImageButton = (ImageButton) findViewById(R.id.mainActivityFolderImageButton);
        //UI declaration Ends

        startAnimation();
        toast = Toast.makeText(getApplicationContext(), "Under development", Toast.LENGTH_SHORT);

        //Help Button OnClickListener Starts
        helpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toast.show();
                Animation helpAnimation = blinkAnimation();
                helpAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                helpImageButton.startAnimation(helpAnimation);
            }
        });
        //Help Button OnClickListener Ends

        //Setting Button OnClickListener Starts
        settingsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation settingsAnimation = blinkAnimation();
                settingsAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
//                        toast.show();
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                settingsImageButton.startAnimation(settingsAnimation);
            }
        });
        //Setting Button OnClickListener Ends

        //Camera Button OnClickListener Starts
        cameraImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation cameraAnimation = blinkAnimation();
                cameraAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                cameraImageButton.startAnimation(cameraAnimation);

            }
        });
        //Camera Button OnClickListener Ends

        //Gallery Button OnclickListener Starts
        galleryImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                toast.show();
                Animation galleryAnimation = blinkAnimation();
                galleryAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
//                        toast.show();
                        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                        intent.putExtra("gallery", true);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                galleryImageButton.startAnimation(galleryAnimation);
            }
        });
        //Gallery Button OnclickListener Ends

        //Folder Button OnclickListener Starts
        folderImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation folderAnimation = blinkAnimation();
                folderAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startActivity(new Intent(MainActivity.this, FolderListActivity.class));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                folderImageButton.startAnimation(folderAnimation);
            }
        });
        //Folder Button OnclickListener Ends
    }

    private Animation blinkAnimation() {
        Animation scale = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(200);
        scale.setInterpolator(new OvershootInterpolator());
        return scale;
    }

    private void startAnimation() {
        Animation animation = blinkAnimation();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation animation1 = blinkAnimation();
                animation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation animation2 = blinkAnimation();
                        animation2.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                Animation animation3 = blinkAnimation();
                                animation3.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        folderImageButton.startAnimation(blinkAnimation());
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                galleryImageButton.startAnimation(animation3);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        cameraImageButton.startAnimation(animation2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                settingsImageButton.startAnimation(animation1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        helpImageButton.setAnimation(animation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permision = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
            CommonMethods.checkmarshmallowPermission(MainActivity.this, permision, MY_PERMISSIONS_REQUEST_LOCATION);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        onStart();
                    }
                }
                break;
        }

    }
}
