package com.solvetech.homeagent.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.solvetech.homeagent.HomeActivity;
import com.solvetech.homeagent.R;
import com.solvetech.homeagent.data.DataAccessClient;
import com.solvetech.homeagent.model.CustomerStatus;
import com.solvetech.homeagent.model.PropertyInfo;
import com.solvetech.homeagent.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;
import static com.solvetech.homeagent.utils.Utils.reportError;

/**
 * Created by wpy on 10/25/15.
 */
public class CustomerStatusAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<CustomerStatus> statusList;
    private LayoutInflater inflater;

    private TextView projectLayout;
    private TextView status;
    private Spinner selector;

    private DataAccessClient client;

    public CustomerStatusAdapter(Activity activity, ArrayList<CustomerStatus> statusList) {
        this.activity = activity;
        this.statusList = statusList;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String accessToken = this.activity.getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
        client = new DataAccessClient(accessToken);
    }

    @Override
    public int getCount() {
        return statusList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.status_row, null);
        }

        CustomerStatus info = statusList.get(position);
        projectLayout = (TextView) view.findViewById(R.id.status_layout);
        selector = (Spinner) view.findViewById(R.id.status_selector);
        status = (TextView) view.findViewById(R.id.status);

        projectLayout.setText(info.getProjectName() + info.getLayout());
        Log.d("getView", "called");
        status.setText(info.getStatus());

        setupSpinner(position);
        return view;
    }

    private void setupSpinner(int i) {
        ArrayList<String> statusMeta = new ArrayList<String>();
        statusMeta.add("到访");
        statusMeta.add("签约");
        statusMeta.add("付款");
        selector.setAdapter(new ArrayAdapter<String>(this.activity,
                android.R.layout.simple_spinner_item, statusMeta));
        selector.setSelected(false);
        selector.setOnItemSelectedListener(new StatusSpinnerOnItemSelectedListener(i, statusMeta));
    }

    public class StatusSpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        int index;
        ArrayList<String> statusMeta;

        public StatusSpinnerOnItemSelectedListener(int i, ArrayList<String> statusMeta) {
            this.index = i;
            this.statusMeta = statusMeta;
        }
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            try {
                Log.d("Spinner", "" + index);
                String jsonRequest = formRequestData(statusMeta.get(position));
                new UpdateCustomerStatusTask(jsonRequest, statusMeta.get(position), this.index).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
        }

        public String formRequestData(String selected) throws JSONException {
            JSONObject obj = new JSONObject();
            obj.put("customerId", statusList.get(index).getCustomerId());
            obj.put("propertyId", statusList.get(index).getPropertyId());
            obj.put("status", selected);
            return obj.toString();
        }
    }

    public class UpdateCustomerStatusTask extends AsyncTask<Void, Void, Integer> {

        private String customerJson;
        private String updateStatus;
        private int index;

        public UpdateCustomerStatusTask(String json, String updateStatus, int index) {
            this.customerJson = json;
            this.updateStatus = updateStatus;
            this.index = index;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String url = BASE_URL + "/status";
            Log.d("AddCustomer", url);
            Log.d("CustomerStatusPut", customerJson);
            int code = client.putDataInJsonWithResponse(url, customerJson);
            return code;
        }

        @Override
        protected void onPostExecute(Integer result) {
            reportError(activity, "Code: " + result);
            Log.d("UPDATE", "index " + index + " with " + updateStatus);
            status.setText(updateStatus);
            statusList.get(index).setStatus(updateStatus);
        }
    }

}
