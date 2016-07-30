package com.solvetech.homeagent;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.solvetech.homeagent.adapter.ProjectAdapter;
import com.solvetech.homeagent.adapter.SaleLayoutAdapter;
import com.solvetech.homeagent.data.DataAccessClient;
import com.solvetech.homeagent.model.ProjectSummary;
import com.solvetech.homeagent.R;
import com.solvetech.homeagent.model.PropertyInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;
import static com.solvetech.homeagent.utils.Utils.reportError;

public class SaleListActivity extends Activity {

	int projectId;
	ArrayList<PropertyInfo> properties;
	DataAccessClient client;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		projectId = bundle.getInt("project_id");
		String accessToken = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
		client = new DataAccessClient(accessToken);
		new SaleListRetrievalTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
		Log.d("ClientActivity", "Option menu");
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
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void startPropertyDisplayActivity(int position) {
		Intent intent = new Intent(this, PropertyDisplayActivity.class);
		intent.putExtra("propertyId", position);
		startActivity(intent);
	}

	public class SaleListRetrievalTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String url = BASE_URL + "/property?project_id=" + projectId;
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
				if (result != null ) {
					initSaleList(result);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				//reportError(getApplicationContext(), "Data error");
			}
		}
	}
	public void initSaleList(String json) throws JSONException {
		properties = new ArrayList<PropertyInfo>();
		JSONArray array = new JSONArray(json);
		for (int i = 0; i < array.length(); i ++) {
			JSONObject obj = array.getJSONObject(i);
			properties.add(new PropertyInfo(obj.getInt("propertyId"),
					obj.getInt("projectId"),
                    obj.getDouble("propertyArea"),
					obj.getDouble("propertyPrice"),
                    obj.getString("propertyLayout")));
		}

        setContentView(R.layout.salelist);

        ListView projectListView = (ListView) findViewById(R.id.sale_list);
        projectListView.setAdapter(new SaleLayoutAdapter(this, properties));

    }
	
}
