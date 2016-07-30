package com.solvetech.homeagent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.solvetech.homeagent.data.DataAccessClient;
import com.solvetech.homeagent.model.CustomerInfo;
import com.solvetech.homeagent.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;
import static com.solvetech.homeagent.utils.Utils.reportError;

/**
 * Created by wpy on 9/26/15.
 */
@SuppressWarnings("deprecation")
public class CustomerTabActivity extends Activity {
    /** Called when the activity is first created. */

    TabHost mTabHost;
    LocalActivityManager mLocalActivityManager;
    Button addStatus;
    EditText statusInput;
    Spinner statusSpinner;

    CustomerInfo customer;
    int customerId;
    ArrayList<String> statusMeta;
    DataAccessClient client;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        customerId = bundle.getInt("customer_id");

        String accessToken = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
        client = new DataAccessClient(accessToken);

        mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);

        new CustomerRetrievalTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setupTabs() {

        Intent intent1 = new Intent(this, CustomerInfoActivity.class);
        intent1.putExtra("customer", customer);
        Intent intent2 = new Intent(this, CustomerStatusActivity.class);
        intent2.putExtra("customer", customer);

        mTabHost = (TabHost)findViewById(R.id.customer_tab);
        mTabHost.setup(mLocalActivityManager);
        mTabHost.addTab(mTabHost.newTabSpec("Info").setIndicator("Info").setContent(intent1));
        mTabHost.addTab(mTabHost.newTabSpec("Status").setIndicator("Status").setContent(intent2));
        mTabHost.setCurrentTab(0);
    }

    public void setupUI() {
        setContentView(R.layout.customer_tabview);
        TextView nameView = (TextView) findViewById(R.id.name_view);
        nameView.setText(customer.getLastName() + " " + customer.getFirstName());
        addStatus = (Button) findViewById(R.id.btn_add_status);
        addStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddStatusDialog();
            }
        });
    }

    public class CustomerRetrievalTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String url = BASE_URL + "/customers";
            url += "/" + customerId;
            Log.d("Customer", url);
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
                    customer = getCustomerByJson(result);
                    setupUI();
                    setupTabs();
                }
            } catch (JSONException e) {
                reportError(getApplicationContext(), "Data error");
                e.printStackTrace();
            }
        }
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

    public void showAddStatusDialog() {
        LinearLayout dialogLayout = new LinearLayout(getApplicationContext());
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        statusInput = new EditText(this);
        statusInput.setHint("房产编号");

        statusMeta = new ArrayList<String>();
        statusMeta.add("没兴趣");
        statusMeta.add("到访");
        statusMeta.add("签约");
        statusMeta.add("付款");
        statusSpinner = new Spinner(this);
        statusSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, statusMeta));

        dialogLayout.addView(statusInput);
        dialogLayout.addView(statusSpinner);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加客户状态");
        builder.setView(dialogLayout);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int propertyId = Integer.parseInt(statusInput.getText().toString());
                String status = statusSpinner.getSelectedItem().toString();
                try {
                    new InsertCustomerStatusTask(formRequestData(customer.getCustomerId(), propertyId, status))
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class InsertCustomerStatusTask extends AsyncTask<Void, Void, String> {

        private String customerJson;

        public InsertCustomerStatusTask(String json) {
            this.customerJson = json;
        }

        @Override
        protected String doInBackground(Void... params) {
            String url = BASE_URL + "/status";
            Log.d("AddCustomer", url);
            Log.d("CustomerStatusPut", customerJson);
            String code = client.postDataInJsonWithResponse(url, customerJson);
            return code;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("POST AsyncTask", "" + result);
            //restartCurrentActivity();
            recreate();
        }
    }

    public String formRequestData(int customerId, int propertyId, String status) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("customerId", customerId);
        obj.put("propertyId", propertyId);
        obj.put("status", status);
        return obj.toString();
    }

    public void restartCurrentActivity() {
        Intent intent = new Intent(this, CustomerTabActivity.class);
        intent.putExtra("customer_id", customer.getCustomerId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocalActivityManager.dispatchResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocalActivityManager.dispatchPause(isFinishing());
    }

}
