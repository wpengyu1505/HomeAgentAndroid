package com.solvetech.homeagent;

import android.app.TabActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

import com.solvetech.homeagent.support.LogoutBroadcaseReceiver;
import com.solvetech.homeagent.utils.Utils;

@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity {
	
	private TabHost mTabHost;
	private RadioGroup mRadioGroup;
	private RadioButton mTabProjectButton;
	private RadioButton mTabClientButton;
	//private RadioButton mTabAgentButton;
	private RadioButton mTabSetupButton;
	private final String CLASSNAME = "MainTabActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		Log.d(CLASSNAME, "onCreate");
		
		super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setupTabs(0, false);

		// Set BroadcaseReceiver for logout action
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.solvetech.homeagent.LOGOUT");
		//registerReceiver(new LogoutBroadcaseReceiver(this), intentFilter);
	}

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
		Log.d("MainTabActivity", "onNewIntent");
		if (intent.getExtras().containsKey("login") && intent.getExtras().getBoolean("login")) {
            Log.d("MainTabActivity", "reset tabview");
            setupTabs(0, true);
        }

    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
		Log.d("MainTabActivity", "Option menu");
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_back:
	            onBackPressed();
	            return true;
            case R.id.action_add:
                try {
                    Utils.startSelectedActivity(this, AddCustomerActivity.class);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void setupTabs(int tabId, boolean onLogin) {

        setContentView(R.layout.tab_mainscreen);
		mTabHost = getTabHost();
		mTabHost.setup();
		final TabWidget tabWidget = mTabHost.getTabWidget();
		tabWidget.setStripEnabled(false);

        Intent projectIntent = new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent clientIntent = new Intent(this, ClientActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent setupIntent = new Intent(this, SetupActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (onLogin) {
            projectIntent.putExtra("login", true);
        }

		// Add 4 main activities to the tab
        mTabHost.addTab(mTabHost.newTabSpec("TAG1").setIndicator("0")
                .setContent(projectIntent));
		mTabHost.addTab(mTabHost.newTabSpec("TAG2").setIndicator("1")
				.setContent(clientIntent));
		mTabHost.addTab(mTabHost.newTabSpec("TAG3").setIndicator("2")
				.setContent(setupIntent));

		mRadioGroup = (RadioGroup) findViewById(R.id.main_radio);
		mTabProjectButton = (RadioButton) findViewById(R.id.tab_icon_project);
		mTabClientButton = (RadioButton) findViewById(R.id.tab_icon_client);
		mTabSetupButton = (RadioButton) findViewById(R.id.tab_icon_setup);
		
		mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int id) {
				if (id == mTabProjectButton.getId()) {
					mTabHost.setCurrentTab(0);
				} else if (id == mTabClientButton.getId()) {
					mTabHost.setCurrentTab(1);
				} else if (id == mTabSetupButton.getId()) {
					mTabHost.setCurrentTab(2);
				}
			}
		});
		
		mTabHost.setCurrentTab(tabId);
		
		for (int i = 0; i < tabWidget.getChildCount(); i ++) {
			View v = tabWidget.getChildAt(i);
			v.getLayoutParams().height = 45;
			v.setBackgroundDrawable(null);
		}
	}

}
