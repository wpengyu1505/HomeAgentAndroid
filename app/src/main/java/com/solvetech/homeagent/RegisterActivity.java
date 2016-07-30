package com.solvetech.homeagent;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.solvetech.homeagent.data.DataAccessClient;
import com.solvetech.homeagent.model.AgentInfo;

import org.json.JSONException;
import org.json.JSONObject;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;
import static com.solvetech.homeagent.utils.Utils.reportError;

/**
 * Created by wpy on 10/30/15.
 */
public class RegisterActivity extends Activity {

    EditText firstNameView;
    EditText lastNameView;
    EditText phoneNumberView;
    EditText passwordView;
    EditText developerView;
    EditText confirmPassView;
    EditText accountView;
    Button submitButton;

    AgentInfo agent;
    DataAccessClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        client = new DataAccessClient("");
        agent = new AgentInfo();
        initUIComponents();
    }

    private void initUIComponents() {
        firstNameView = (EditText) findViewById(R.id.agent_first);
        lastNameView = (EditText) findViewById(R.id.agent_last);
        phoneNumberView = (EditText) findViewById(R.id.agent_phone);
        passwordView = (EditText) findViewById(R.id.agent_password);
        developerView = (EditText) findViewById(R.id.agent_developer);
        confirmPassView = (EditText) findViewById(R.id.agent_confirm_pass);
        accountView = (EditText) findViewById(R.id.agent_account);
        submitButton = (Button) findViewById(R.id.btn_register_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Finalize customer info object
                if (firstNameView.getText().toString().isEmpty() ||
                        lastNameView.getText().toString().isEmpty() ||
                        phoneNumberView.getText().toString().isEmpty()) {
                    reportError(getApplicationContext(), "请输入必要信息");
                    return;
                }
                if (!passwordView.getText().toString().equals(confirmPassView.getText().toString())) {
                    reportError(getApplicationContext(), "确认密码不匹配");
                    return;
                }
                agent.setFirstName(firstNameView.getText().toString());
                agent.setLastName(lastNameView.getText().toString());
                agent.setPhoneNumber(phoneNumberView.getText().toString());
                agent.setDeveloperId(Integer.parseInt(developerView.getText().toString()));
                agent.setPassword(passwordView.getText().toString());
                agent.setAccountNumber(accountView.getText().toString());

                // Execute POST method
                try {
                    new RegisterAgentTask(buildRegisterRequest(agent)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (JSONException e) {
                    e.printStackTrace();
                    reportError(getApplicationContext(), "数据格式错误");
                }
            }
        });
    }

    public String buildRegisterRequest(AgentInfo agent) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("firstName", agent.getFirstName());
        obj.put("lastName", agent.getLastName());
        obj.put("phoneNum", agent.getPhoneNumber());
        obj.put("password", agent.getPassword());
        obj.put("accountNum", agent.getAccountNumber());
        obj.put("developerId", agent.getDeveloperId());
        return obj.toString();
    }

    public class RegisterAgentTask extends AsyncTask<Void, Void, String> {

        private String agentJson;

        public RegisterAgentTask(String json) {
            agentJson = json;
        }
        @Override
        protected String doInBackground(Void... params) {
            String url = BASE_URL + "/agent";
            Log.d("Register", url);
            String response = null;
            client.postDataInJson(url, agentJson);
            Log.d("Register", agentJson);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            reportError(getApplicationContext(), "添加成功");
            startMainActivity();
        }
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }
}
