package com.solvetech.homeagent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.solvetech.homeagent.data.DataAccessClient;
import com.solvetech.homeagent.model.CustomerInfo;
import com.solvetech.homeagent.model.TypeMetadata;
import com.solvetech.homeagent.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;
import static com.solvetech.homeagent.utils.Utils.reportError;

/**
 * Created by wpy on 9/22/15.
 */
public class AddCustomerActivity extends Activity implements AdapterView.OnItemSelectedListener {

    // UI Components
    Button submitButton;
    TypeMetadata meta;
    Spinner cPropertyClass;
    Spinner cLocation;
    Spinner cLayout;
    Spinner cPrice;
    Spinner cFurnish;
    EditText cFirstName;
    EditText cLastName;
    EditText cPhoneNumber;
    EditText cOther;

    // Customer Model
    CustomerInfo customerInfo;

    DataAccessClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_customer);
        String accessToken = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
        client = new DataAccessClient(accessToken);
        try {
            meta = TypeMetadata.getInstance(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        customerInfo = new CustomerInfo();
        initUIComponents();
        initSpinner();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Finalize customer info object
                if (cFirstName.getText().toString().isEmpty() ||
                        cLastName.getText().toString().isEmpty() ||
                        cPhoneNumber.getText().toString().isEmpty() ) {
                    reportError(getApplicationContext(), "请输入必要信息");
                    return;
                }
                customerInfo.setAgentId(1);
                customerInfo.setFirstName(cFirstName.getText().toString());
                customerInfo.setLastName(cLastName.getText().toString());
                customerInfo.setPhoneNumber(cPhoneNumber.getText().toString());

                // Execute POST method
                try {
                    new InsertCustomerTask(toCustomerJson(customerInfo)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (JSONException e) {
                    e.printStackTrace();
                    reportError(getApplicationContext(), "数据格式错误");
                }
            }
        });
    }

    public void initUIComponents() {
        submitButton = (Button) findViewById(R.id.btn_add_customer);
        cFirstName = (EditText) findViewById(R.id.et_customer_first);
        cLastName = (EditText) findViewById(R.id.et_customer_last);
        cPhoneNumber = (EditText) findViewById(R.id.et_customer_phone);
        cPropertyClass = (Spinner) findViewById(R.id.sp_property_type);
        cLocation = (Spinner) findViewById(R.id.sp_customer_location);
        cLayout = (Spinner) findViewById(R.id.sp_customer_layout);
        cPrice = (Spinner) findViewById(R.id.sp_customer_price);
        cFurnish = (Spinner) findViewById(R.id.sp_customer_furnish);
        cOther = (EditText) findViewById(R.id.et_customer_other);
    }

    public void initSpinner() {
        cPropertyClass.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Utils.convertHashKeyToArray(meta.getPropertyClass())));
        cPropertyClass.setOnItemSelectedListener(this);

        cLocation.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Utils.convertHashKeyToArray(meta.getLocation())));
        cLocation.setOnItemSelectedListener(this);

        cLayout.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Utils.convertHashKeyToArray(meta.getLayout())));
        cLayout.setOnItemSelectedListener(this);

        cPrice.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Utils.convertHashKeyToArray(meta.getPrice())));
        cPrice.setOnItemSelectedListener(this);

        cFurnish.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Utils.convertHashKeyToArray(meta.getFurnish())));
        cFurnish.setOnItemSelectedListener(this);
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainTabActivity.class);
        intent.putExtra("tab_id", 1);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch(parent.getId()) {
            case R.id.sp_property_type:
                customerInfo.setPropertyClass(Utils.getKeyFromHash(meta.getPropertyClass(),
                        parent.getItemAtPosition(position).toString()));
                break;
            case R.id.sp_customer_location:
                customerInfo.setLocationCd(Utils.getKeyFromHash(meta.getLocation(),
                        parent.getItemAtPosition(position).toString()));
                break;
            case R.id.sp_customer_layout:
                customerInfo.setLayoutReq(Utils.getKeyFromHash(meta.getLayout(),
                        parent.getItemAtPosition(position).toString()));
                break;
            case R.id.sp_customer_price:
                customerInfo.setPriceRange(Utils.getKeyFromHash(meta.getPrice(),
                        parent.getItemAtPosition(position).toString()));
                break;
            case R.id.sp_customer_furnish:
                customerInfo.setFurnish(Utils.getKeyFromHash(meta.getFurnish(),
                        parent.getItemAtPosition(position).toString()));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        customerInfo.setPropertyClass(0);
        customerInfo.setLocationCd(0);
        customerInfo.setLayoutReq(0);
        customerInfo.setPriceRange(0);
        customerInfo.setFurnish(0);
    }

    public String toCustomerJson(CustomerInfo customerInfo) throws JSONException {
        JSONObject customer = new JSONObject();
        customer.put("agentId", customerInfo.getAgentId());
        customer.put("firstName", customerInfo.getFirstName());
        customer.put("lastName", customerInfo.getLastName());
        customer.put("phoneNum", customerInfo.getPhoneNumber());
        customer.put("propertyClass", customerInfo.getPropertyClass());
        customer.put("locationCd", customerInfo.getLocationCd());
        customer.put("priceRange", customerInfo.getPriceRange());
        customer.put("layoutReq", customerInfo.getLayoutReq());
        customer.put("areaReq", customerInfo.getAreaReq());
        customer.put("furnishCd", customerInfo.getFurnish());
        customer.put("other", customerInfo.getOther());
        return customer.toString();
    }

    public class InsertCustomerTask extends AsyncTask<Void, Void, String> {

        private String customerJson;

        public InsertCustomerTask(String json) {
            customerJson = json;
        }
        @Override
        protected String doInBackground(Void... params) {
            String url = BASE_URL + "/customers";
            Log.d("AddCustomer", url);
            String response = null;
            client.postDataInJson(url, customerJson);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            reportError(getApplicationContext(), "添加成功");
            startMainActivity();
        }
    }
}
