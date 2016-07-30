package com.solvetech.homeagent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.solvetech.homeagent.data.DataAccessClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;
import static com.solvetech.homeagent.utils.Utils.reportError;

/**
 * Created by wpy on 9/19/15.
 */
public class LoginActivity extends Activity {

    TextView usernameView;
    TextView passwordView;
    Button loginButton;
    DataAccessClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new DataAccessClient("");
        setContentView(R.layout.login);

        usernameView = (TextView) findViewById(R.id.username);
        passwordView = (TextView) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameView.getText().toString();
                String password = passwordView.getText().toString();
                try {
                    authenticate(username, password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void authenticate(String user, String pass) throws JSONException {
        JSONObject credentialJson = new JSONObject();
        credentialJson.put("username", user);
        credentialJson.put("password", pass);
        Log.d("Login", credentialJson.toString());
        new AuthenticationTask(credentialJson.toString()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainTabActivity.class);
        intent.putExtra("login", true);
        startActivity(intent);
    }

    public class AuthenticationTask extends AsyncTask<Void, Void, String> {

        private String customerJson;

        public AuthenticationTask(String json) {
            customerJson = json;
        }
        @Override
        protected String doInBackground(Void... params) {
            String url = BASE_URL + "/authentication";
            Log.d("Authentication", url);
            String response = client.postDataInJsonWithResponse(url, customerJson);
            Log.d("Response Token", response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            SharedPreferences sharedPref = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("accessToken", result);
            editor.commit();

            String accessToken = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
            Log.d("Preference: ", accessToken);
            startMainActivity();
        }
    }
}
