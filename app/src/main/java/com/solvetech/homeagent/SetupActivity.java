package com.solvetech.homeagent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.solvetech.homeagent.service.CustomerReferalService;
import com.solvetech.homeagent.support.LogoutBroadcaseReceiver;


public class SetupActivity extends Activity {

    Button logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setup);

        // Set BroadcaseReceiver for logout action
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.solvetech.homeagent.LOGOUT");
//        registerReceiver(new LogoutBroadcaseReceiver(this), intentFilter);

        logout = (Button) findViewById(R.id.btn_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanup();
                startWelcomeActivity();
            }
        });
    }

    public void cleanup() {
        // Clear access token
        SharedPreferences sharedPref = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("accessToken");
        editor.commit();

        // Stop service
        Intent stopIntent = new Intent(this, CustomerReferalService.class);
        stopService(stopIntent);
    }

    public void startWelcomeActivity() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        broadcastIntent.setAction("com.solvetech.homeagent.LOGOUT");
        sendBroadcast(broadcastIntent);

        Intent intent = new Intent(this, WelcomeActivity.class);
        //Intent intent = new Intent(Intent.ACTION_MAIN);
        //intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
