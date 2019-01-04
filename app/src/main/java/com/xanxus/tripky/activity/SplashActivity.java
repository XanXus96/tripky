package com.xanxus.tripky.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.xanxus.tripky.asyncTask.CheckInternetTask;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class SplashActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = SplashActivity.this;
        checkPermission();
    }

    private void checkPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        if(ContextCompat.checkSelfPermission(mActivity,Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(
                mActivity,Manifest.permission.ACCESS_COARSE_LOCATION)
                + ContextCompat.checkSelfPermission(
                mActivity,Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(
                mActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            //do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity,Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity,Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity,Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                //if we should give explanation of requested permissions

                //show an alert dialog here with request explanation
                builder.setMessage("Position, Read and Write External" +
                        " Storage permissions are required to do the task.");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    ActivityCompat.requestPermissions(
                            mActivity,
                            new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            MY_PERMISSIONS_REQUEST_CODE
                    );
                    checkPermission();
                });
                builder.setNeutralButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                //directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        mActivity,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
                checkPermission();
            }
        }else {
            File f = getFileStreamPath("weather.json");
            if (f.exists()) {
                //go to mainActivity when permissions are already granted
                launchMainActivity();
            } else {
                try {
                    if (new CheckInternetTask(this).execute().get()) {
                        launchMainActivity();
                    } else {
                        builder.setMessage("No internet please try to check your internet data");
                        builder.setTitle("Internet issue");
                        builder.setPositiveButton("retry", (dialogInterface, i) -> {
                            checkPermission();
                        });
                        builder.setNeutralButton("quit", (dialogInterface, i) -> {
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        });
                        builder.setCancelable(false);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } catch (ExecutionException e) {

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void launchMainActivity (){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}

