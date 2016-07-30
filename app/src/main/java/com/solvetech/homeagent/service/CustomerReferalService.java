package com.solvetech.homeagent.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.solvetech.homeagent.HomeActivity;
import com.solvetech.homeagent.data.DataAccessClient;

/**
 * Created by wpy on 11/3/15.
 */
public class CustomerReferalService extends Service {

    NotificationPoller poller;

    @Override
    public void onCreate() {
        super.onCreate();
        String accessToken = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
        poller = new NotificationPoller(getApplicationContext(), accessToken);
        poller.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.d("CustomerReferalService", "onStartCommand");
        super.onStartCommand(intent, flag, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("CustomerReferalService", "Service destroy is called");
        super.onDestroy();
        poller.interrupt();
    }
}
