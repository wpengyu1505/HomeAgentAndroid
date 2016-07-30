package com.solvetech.homeagent.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.solvetech.homeagent.R;
import com.solvetech.homeagent.ReferredCustomerActivity;
import com.solvetech.homeagent.data.DataAccessClient;
import com.solvetech.homeagent.model.CustomerInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;

/**
 * Created by wpy on 11/4/15.
 */
public class NotificationPoller extends Thread {

    private Context context;
    private DataAccessClient client;
    private String referalUrl = BASE_URL + "/referal/1";
    private String customerUrl = BASE_URL + "/customers/";
    private final int SLEEP_DURATION_MILLISECONDS = 60000;

    public NotificationPoller(Context context, String accessToken) {
        this.context = context;
        client = new DataAccessClient(accessToken);
    }

    @Override
    public void run() {
        while (true) {
            try {
                processNotification();
                sleep(SLEEP_DURATION_MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void processNotification() throws JSONException {
        try {
            String response = client.retrieveDataInJson(referalUrl);
            Log.d("Notification", response);
            JSONArray array = new JSONArray(response);

            for (int i = 0; i < array.length(); i ++) {
                JSONObject obj = array.getJSONObject(i);
                sendNotification(obj.getInt("pushId"), obj.getInt("customerId"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(int pushId, int customerId) throws IOException {
        String response = client.retrieveDataInJson(customerUrl + customerId);
        Intent intent = new Intent(context, ReferredCustomerActivity.class);
        intent.putExtra("customer", response);
        intent.putExtra("pushId", pushId);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Set notification
        Log.d("Notification", "About to notify");
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
        nBuilder.setContentTitle("HomeAgent");
        nBuilder.setContentText("您有新推送客户");
        nBuilder.setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
        nBuilder.setSmallIcon(R.drawable.ic_launcher);
        nBuilder.setAutoCancel(true);
        int notificationId = pushId;

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(notificationId, nBuilder.build());
    }


    private CustomerInfo getCustomerByJson(String jsonStr) throws JSONException {

        CustomerInfo customer = new CustomerInfo();
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
}
