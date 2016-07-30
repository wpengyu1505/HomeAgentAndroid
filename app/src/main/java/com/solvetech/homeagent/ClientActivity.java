package com.solvetech.homeagent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.solvetech.homeagent.data.DataAccessClient;
import com.solvetech.homeagent.model.CustomerSummary;
import com.solvetech.homeagent.model.ModelCacheHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;
import static com.solvetech.homeagent.utils.Utils.reportError;

public class ClientActivity extends Activity {

	private ArrayList<String[]> customersDummy;
	private ArrayList<CustomerSummary> customers;
	DataAccessClient client;
    ModelCacheHelper cache;

	ListView clientsListView;
	ListAdapter clientsAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.customers);
        cache = new ModelCacheHelper(getApplicationContext());
        try {
            customers = cache.fetchCustomers();
            Log.d("Cached customers", "" + customers.size());
            if (customers.size() > 0) {
                refreshUI();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String accessToken = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
		client = new DataAccessClient(accessToken);
		try {
			new CustomerListRetrievalTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
		Log.d("ClientActivity", "Option menu");
	    MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.customer_menu, menu);
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
	        case R.id.action_add:
				startAddCustomerActivity();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void startCustomerActivity(int position) {
		Log.d("ClintActivity", "Customer: " + position);
		Intent intent = new Intent(this, CustomerTabActivity.class);
		intent.putExtra("customer_id", customers.get(position).getCustomerId());
		startActivity(intent);
	}

	public class CustomerListRetrievalTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String url = BASE_URL + "/customers";
			Log.d("CustomerList", url);
			String response = null;
			try {
				response = client.retrieveDataInJson(url);
			} catch (IOException e) {
                e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result != null) {
                    initCustomerList(result);
                }
			} catch (IOException e) {
                e.printStackTrace();
			} catch (JSONException e) {
                e.printStackTrace();
				reportError(getApplicationContext(), "Data error");
			}
		}
	}

	public void initCustomerList(String json) throws IOException, JSONException {
		JSONArray jarray = new JSONArray(json);
		customers.clear();
		for (int i = 0; i < jarray.length(); i ++) {
			JSONObject obj = jarray.getJSONObject(i);
			customers.add(new CustomerSummary(obj.getInt("customerId"), obj.getString("firstName"),
					obj.getString("lastName"), obj.getString("phoneNum")));
		}

		Log.d("json", json);
		Log.d("customers", "" + customers.size());
        refreshUI();
        cache.cacheCustomers(customers);
	}

	public void refreshUI() {
        clientsAdapter = new ArrayAdapter<CustomerSummary>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1,
                customers) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                CustomerSummary entry = customers.get(position);
                TextView text1 = (TextView) view
                        .findViewById(android.R.id.text1);
                TextView text2 = (TextView) view
                        .findViewById(android.R.id.text2);
                text1.setText(entry.getLastName() + " " + entry.getFirstName());
                text1.setTextColor(Color.BLACK);
                text2.setText(entry.getPhoneNumber());
                text2.setTextColor(Color.BLACK);

                return view;
            }
        };
		clientsListView = (ListView) findViewById(R.id.customer_list);
		clientsListView.setAdapter(clientsAdapter);

		clientsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startCustomerActivity(position);
			}
		});
	}

	public void startAddCustomerActivity() {
		Intent intent = new Intent(this, AddCustomerActivity.class);
		startActivity(intent);
	}
	
}
