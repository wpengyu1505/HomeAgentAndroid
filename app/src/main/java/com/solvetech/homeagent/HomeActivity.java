package com.solvetech.homeagent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.solvetech.homeagent.adapter.ProjectAdapter;
import com.solvetech.homeagent.data.DataAccessClient;
import com.solvetech.homeagent.model.ModelCacheHelper;
import com.solvetech.homeagent.model.ProjectSummary;
import com.solvetech.homeagent.model.TypeMetadata;
import com.solvetech.homeagent.service.CustomerReferalService;
import com.solvetech.homeagent.support.LogoutBroadcaseReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;
import static com.solvetech.homeagent.utils.Utils.reportError;

public class HomeActivity extends Activity{

	private ArrayList<ProjectSummary> projects;
	public static TypeMetadata meta;
	public static String PREFS_NAME = "SHARED_PREFS";
    ModelCacheHelper cache;
    DataAccessClient client;

    ListView projectListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		String accessToken = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
        if (accessToken == null) {
            Log.d("HomeActivity", "No accessToken avail, jump to Welcome");
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            return;
        }
        try {
            meta = TypeMetadata.getInstance(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // If accessToken is retrieved, proceed to this activity
        cache = new ModelCacheHelper(getApplicationContext());
        setContentView(R.layout.projects);
        try {
            projects = cache.fetchProjects();
            Log.d("Cached projects", "" + projects.size());
            if (projects.size() > 0) {
                refreshUI();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        client = new DataAccessClient(accessToken);

        // Set BroadcaseReceiver for logout action
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.solvetech.homeagent.LOGOUT");
        registerReceiver(new LogoutBroadcaseReceiver(this), intentFilter);

        // Start background data access
		new MetadataRetrievalTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new ProjectListRetrievalTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        startNotificationService();

	}

    @Override
    public void onResume() {
        super.onResume();
        String accessToken = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
        if (accessToken == null) {
            Log.d("HomeActivity", "No accessToken avail, jump to Welcome");
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            return;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d("HomeActivity", "onNewIntent");
        if (intent.getExtras() != null &&
                intent.getExtras().containsKey("login") &&
                intent.getExtras().getBoolean("login")) {
            Log.d("HomeActivity", "reset HomeAgent");

            // Repeate what onCreate does
            String accessToken = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
            if (accessToken == null) {
                Log.d("HomeActivity", "No accessToken avail, jump to Welcome");
                Intent welcomeIntent = new Intent(this, WelcomeActivity.class);
                startActivity(welcomeIntent);
                return;
            }
            cache = new ModelCacheHelper(getApplicationContext());
            setContentView(R.layout.projects);
            try {
                projects = cache.fetchProjects();
                Log.d("Cached projects", "" + projects.size());
                if (projects.size() > 0) {
                    refreshUI();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            client = new DataAccessClient(accessToken);

            // Set BroadcaseReceiver for logout action
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.solvetech.homeagent.LOGOUT");
            registerReceiver(new LogoutBroadcaseReceiver(this), intentFilter);

            // Start background data access
            new MetadataRetrievalTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new ProjectListRetrievalTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
        Log.d("ClientActivity", "Option menu");
	    MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
	    //return super.onCreateOptionsMenu(menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_back:
	            onBackPressed();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void startProjectInfoActivity(int position) {
		Intent intent = new Intent(this, DeveloperInfoActivity.class);
		intent.putExtra("project_id", projects.get(position).getProjectId());
		startActivity(intent);
	}

	public class MetadataRetrievalTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				meta = TypeMetadata.pullMetadata(getApplicationContext());
                meta.persistMetadata(getApplicationContext());
			} catch (IOException e) {
                e.printStackTrace();
				Log.d("Meta", "Network error");
			} catch (JSONException e) {
                e.printStackTrace();
			} catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
		}
	}

    public class ProjectListRetrievalTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String url = BASE_URL + "/project";
            Log.d("ProjectList", url);
            String response = null;
            try {
                response = client.retrieveDataInJson(url);
            } catch (IOException e) {
                e.printStackTrace();
                //reportError(getApplicationContext(), "Network error");
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (result != null) {
                    initProjectList(result);
                }
            } catch (IOException e) {
                e.printStackTrace();
                reportError(getApplicationContext(), "Network error");
            } catch (JSONException e) {
                e.printStackTrace();
                reportError(getApplicationContext(), "Data error");
            }
        }
    }

    public void initProjectList(String json) throws IOException, JSONException {
        JSONArray jarray = new JSONArray(json);
        projects.clear();
        for (int i = 0; i < jarray.length(); i ++) {
            JSONObject obj = jarray.getJSONObject(i);
            projects.add(new ProjectSummary(obj.getInt("projectId"),
                    obj.getString("developerName"),
                    obj.getString("projectName"),
                    meta.getLocation().get(obj.getInt("locationId")),
                    obj.getString("topPics")));
        }
        refreshUI();
        cache.cacheProjects(projects);
    }

    public void refreshUI() {

        Log.d("Refresh list", "NOW");
        projectListView = (ListView) findViewById(R.id.project_list);
        projectListView.setAdapter(new ProjectAdapter(this, projects));

        projectListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startProjectInfoActivity(position);
            }
        });
    }

    public void startNotificationService() {
        Intent serviceIntent = new Intent(this, CustomerReferalService.class);
        startService(serviceIntent);
    }
	
}
