package com.solvetech.homeagent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.solvetech.homeagent.adapter.CustomerStatusAdapter;
import com.solvetech.homeagent.data.DataAccessClient;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import com.solvetech.homeagent.model.CustomerInfo;
import com.solvetech.homeagent.model.CustomerStatus;
import com.solvetech.homeagent.model.CustomerSummary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;
import static com.solvetech.homeagent.utils.Utils.reportError;

/**
 * Created by wpy on 9/27/15.
 */
public class CustomerStatusActivity extends Activity {

    private ArrayList<CustomerStatus> statusList;
    private CustomerInfo customer;
    final String[] statuses = {"没兴趣", "到访", "签约", "付款"};
    DataAccessClient client;

    ListView statusListView;
    ArrayAdapter statusListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        customer = (CustomerInfo) bundle.getSerializable("customer");
        String accessToken = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
        client = new DataAccessClient(accessToken);

        setContentView(R.layout.customer_status);
        new CustomerStatusRetrievalTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void initializeStatusView() throws JSONException, IOException, ClassNotFoundException {

        statusListAdapter = new ArrayAdapter<CustomerStatus>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1,
                statusList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                CustomerStatus entry = statusList.get(position);
                TextView text1 = (TextView) view
                        .findViewById(android.R.id.text1);
                TextView text2 = (TextView) view
                        .findViewById(android.R.id.text2);
                text1.setText(entry.getProjectName() + " - " + entry.getLayout());
                text1.setTextColor(Color.BLACK);
                text2.setText(entry.getStatus());
                text2.setTextColor(Color.BLACK);

                return view;
            }
        };
        statusListView = (ListView) findViewById(R.id.customer_status_list);
        statusListView.setAdapter(statusListAdapter);
        //statusListView.setAdapter(new CustomerStatusAdapter(this, statusList));
        statusListView.setLongClickable(true);
        statusListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("onclick", "yes");
                showChangeStatusDialog(position);
            }
        });
        statusListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("onlongclick", "yes");
                showDeleteStatusDialog(position);
                return true;
            }
        });
    }

    public class CustomerStatusRetrievalTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String url = BASE_URL + "/status";
            url += "/" + customer.getCustomerId();
            Log.d("CustomerStatus", url);
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
                    initCustomerStatus(result);
                    initializeStatusView();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void initCustomerStatus(String json) throws IOException, JSONException {
        JSONArray jarray = new JSONArray(json);
        statusList = new ArrayList<CustomerStatus>();
        for (int i = 0; i < jarray.length(); i ++) {
            JSONObject obj = jarray.getJSONObject(i);
            CustomerStatus status = new CustomerStatus();
            status.setCustomerId(obj.getInt("customerId"));
            status.setPropertyId(obj.getInt("propertyId"));
            status.setProjectName(obj.getString("projectName"));
            status.setLayout(obj.getString("layout"));
            status.setStatus(obj.getString("status"));
            statusList.add(status);
        }
    }

    public void showChangeStatusDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更新状态");
        builder.setItems(statuses, new UpdateStatusOnclickListener(position));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDeleteStatusDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定删除: ");
        builder.setPositiveButton("确定", new ConfirmDeleteOnclickListener(position));
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class UpdateStatusOnclickListener implements DialogInterface.OnClickListener {

        int position;

        public UpdateStatusOnclickListener(int pos) {
            position = pos;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d("Update", statuses[which] + position);
            try {
                String updateRequest = formRequestData(statuses[which], position);
                new UpdateCustomerStatusTask(updateRequest, position, statuses[which]).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String formRequestData(String selected, int index) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("customerId", statusList.get(index).getCustomerId());
        obj.put("propertyId", statusList.get(index).getPropertyId());
        obj.put("status", selected);
        return obj.toString();
    }

    public class ConfirmDeleteOnclickListener implements DialogInterface.OnClickListener {

        int position;

        public ConfirmDeleteOnclickListener(int pos) {
            position = pos;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d("DELETE", "" + position);
            new DeleteCustomerStatusTask(statusList.get(position)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public class UpdateCustomerStatusTask extends AsyncTask<Void, Void, Integer> {

        private String customerJson;
        private int position;
        private String statusStr;

        public UpdateCustomerStatusTask(String json, int position, String statusStr) {
            this.customerJson = json;
            this.position = position;
            this.statusStr = statusStr;
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
            Log.d("Update Code", "" + result);
            statusList.get(position).setStatus(statusStr);
            try {
                initializeStatusView();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public class DeleteCustomerStatusTask extends AsyncTask<Void, Void, Integer> {

        private CustomerStatus status;

        public DeleteCustomerStatusTask(CustomerStatus status) {
            this.status = status;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String url = BASE_URL + "/status/" + status.getCustomerId() + "/" + status.getPropertyId();
            Log.d("Delete", url);
            int code = client.deleteDataWithResponse(url);
            return code;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result.intValue() == 200) {
                statusList.remove(status);
                try {
                    initializeStatusView();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
//    private ArrayList<CustomerStatus> getCustomerStatusById(int customerId) {
//        CustomerStatus status1 = new CustomerStatus();
//        status1.setCustomerId(0);
//        status1.setPropertyId(0);
//        status1.setDeveloperName("富力");
//        status1.setLayout("三室一厅 (100 m2)");
//        status1.setStatus("到访");
//
//        CustomerStatus status2 = new CustomerStatus();
//        status2.setCustomerId(0);
//        status2.setPropertyId(1);
//        status2.setDeveloperName("富力");
//        status2.setLayout("三室两厅 (100 m2)");
//        status2.setStatus("到访");
//
//        CustomerStatus status3 = new CustomerStatus();
//        status3.setCustomerId(0);
//        status3.setPropertyId(3);
//        status3.setDeveloperName("万科");
//        status3.setLayout("三室三厅 (100 m2)");
//        status3.setStatus("没兴趣");
//
//        CustomerStatus status4 = new CustomerStatus();
//        status4.setCustomerId(0);
//        status4.setPropertyId(4);
//        status4.setDeveloperName("万达");
//        status4.setLayout("两室一厅 (100 m2)");
//        status4.setStatus("到访");
//
//        CustomerStatus status5 = new CustomerStatus();
//        status5.setCustomerId(0);
//        status5.setPropertyId(5);
//        status5.setDeveloperName("苹果");
//        status5.setLayout("三室一厅 (100 m2)");
//        status5.setStatus("签约");
//
//        ArrayList<CustomerStatus> statusList = new ArrayList<CustomerStatus>();
//        statusList.add(status1);
//        statusList.add(status2);
//        statusList.add(status3);
//        statusList.add(status4);
//        statusList.add(status5);
//        return statusList;
//    }
}
