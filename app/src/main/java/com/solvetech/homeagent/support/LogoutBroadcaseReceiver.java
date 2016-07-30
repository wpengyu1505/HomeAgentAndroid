package com.solvetech.homeagent.support;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by wpy on 10/8/15.
 */
public class LogoutBroadcaseReceiver extends BroadcastReceiver {

    Activity activity;

    public LogoutBroadcaseReceiver(Activity activity) {
        this.activity = activity;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive", "Logout in progress " + activity.getLocalClassName());
        //At this point you should start the login activity and finish this one
        activity.unregisterReceiver(this);
        activity.finish();
    }
}
