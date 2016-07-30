package com.solvetech.homeagent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.solvetech.homeagent.data.DataAccessClient;
import com.solvetech.homeagent.model.CustomerInfo;
import com.solvetech.homeagent.model.TypeMetadata;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;
import static com.solvetech.homeagent.utils.Utils.reportError;

public class ReferredCustomerActivity extends Activity {

    private final int REJECT = 0;
    private final int ACCEPT = 1;
    private CustomerInfo customer;
	private int pushId;
    TypeMetadata meta;

    TextView nameView;
    TextView phoneNumView;
    TextView propertyTypeView;
    TextView locationView;
    TextView priceView;
    TextView layoutView;
    TextView areaView;
    TextView furnishView;
    Button acceptButton;
    Button denyButton;

    DataAccessClient client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
        String customerStr = bundle.getString("customer");
        pushId = bundle.getInt("pushId");
        setContentView(R.layout.refered_customer);
        String accessToken = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
        client = new DataAccessClient(accessToken);
        try {
            customer = getCustomerByJson(customerStr);
            setupUI();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
		Log.d("CustomerInfoActivity", "Option menu");
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

    public void setupUI() throws IOException, JSONException, ClassNotFoundException {
		meta = TypeMetadata.getInstance(getApplicationContext());
		nameView = (TextView) findViewById(R.id.name_view);
		phoneNumView = (TextView) findViewById(R.id.phonenum_view);
		propertyTypeView = (TextView) findViewById(R.id.property_type_view);
		locationView = (TextView) findViewById(R.id.location_view);
		priceView = (TextView) findViewById(R.id.price_view);
		layoutView = (TextView) findViewById(R.id.layout_view);
		areaView = (TextView) findViewById(R.id.area_view);
		furnishView = (TextView) findViewById(R.id.furnish_view);
		
		nameView.setText(customer.getLastName() + " " + customer.getFirstName());
		phoneNumView.setText(customer.getPhoneNumber());
		propertyTypeView.setText(meta.getPropertyClass().get(customer.getPropertyClass()));
		locationView.setText(meta.getLocation().get(customer.getLocationCd()));
		priceView.setText(meta.getPrice().get(customer.getPriceRange()));
		layoutView.setText(meta.getLayout().get(customer.getPriceRange()));
		areaView.setText(meta.getArea().get(customer.getAreaReq()));
		furnishView.setText(meta.getFurnish().get(customer.getFurnish()));

        acceptButton = (Button) findViewById(R.id.btn_accept_customer);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new NotificationFeedbackTask(buildFeedback(ACCEPT)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                //startMainActivity();
                onBackPressed();
            }
        });

        denyButton = (Button) findViewById(R.id.btn_deny_customer);
        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new NotificationFeedbackTask(buildFeedback(REJECT)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                //startMainActivity();
                onBackPressed();
            }
        });
	}

   private CustomerInfo getCustomerByJson(String jsonStr) throws JSONException {

        customer = new CustomerInfo();
        JSONObject jo = new JSONObject(jsonStr);
        customer.setCustomerId(jo.getInt("customerId"));
        customer.setFirstName(jo.getString("firstName"));
        customer.setLastName(jo.getString("lastName"));
        customer.setPhoneNumber(jo.getString("phoneNum"));
        customer.setAgentId(jo.getInt("agentId"));
        customer.setLocationCd(jo.optInt("locationCd"));
        customer.setAreaReq(jo.getInt("areaReq"));
        customer.setPriceRange(jo.getInt("priceRange"));
        customer.setPropertyClass(jo.getInt("propertyClass"));
        customer.setLayoutReq(jo.getInt("layoutReq"));
        customer.setFurnish(jo.getInt("furnishCd"));
        return customer;
    }

    public String buildFeedback(int action) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("pushId", pushId);
        obj.put("feedback", action);
        return obj.toString();
    }

    public class NotificationFeedbackTask extends AsyncTask<Void, Void, String> {

        private String customerJson;

        public NotificationFeedbackTask(String json) {
            customerJson = json;
        }
        @Override
        protected String doInBackground(Void... params) {
            String url = BASE_URL + "/referal";
            Log.d("Referal", url);
            String response = client.postDataInJsonWithResponse(url, customerJson);
            Log.d("Response Token", response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            reportError(getApplicationContext(), "已添加推荐客户");
        }
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainTabActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("login", true);
        startActivity(intent);
    }

}
