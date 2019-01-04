package com.xanxus.tripky.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.xanxus.tripky.helper.AppHelper;


public class CheckInternetTask extends AsyncTask<String, Void, Boolean> {

    private Context context;

    public CheckInternetTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        return new AppHelper(context).isConnected();
    }

    @Override
    protected void onPostExecute(Boolean temp) {

    }


}
