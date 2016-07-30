package com.solvetech.homeagent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.solvetech.homeagent.adapter.ImageSlideAdapter;
import com.solvetech.homeagent.data.DataAccessClient;
import com.solvetech.homeagent.model.ProjectInfo;
import com.solvetech.homeagent.model.TypeMetadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;
import static com.solvetech.homeagent.utils.Utils.reportError;

public class DeveloperInfoActivity extends Activity {
	
	private int projectId;
	private ProjectInfo project;
    Button saleButton;
    Button dealButton;
    ViewPager imagePager;
    DataAccessClient client;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		projectId = bundle.getInt("project_id");
        String accessToken = getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
        client = new DataAccessClient(accessToken);
        new ProjectRetrievalTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

	public class ProjectRetrievalTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String url = BASE_URL + "/project";
			url += "/" + projectId;
			Log.d("Project", url);
			String response = null;
			try {
				response = client.retrieveDataInJson(url);
			} catch (IOException e) {
				//reportError(getApplicationContext(), "Network error");
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result != null) {
                    project = getProjectByJson(result);
                    initializeUI();
                }
			} catch (JSONException e) {
				reportError(getApplicationContext(), "Data error");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private ProjectInfo getProjectByJson(String jsonStr) throws JSONException {

		project = new ProjectInfo();
		JSONObject jo = new JSONObject(jsonStr);
        project.setProjectName(jo.getString("projectName"));
        project.setDeveloperName(jo.getString("developerName"));
        project.setLocationId(jo.getInt("locationId"));
        project.setDescription(jo.getString("summary"));
        JSONArray imgObj = jo.getJSONArray("images");

		for (int i = 0; i < imgObj.length(); i ++) {
			project.getImages().add(imgObj.getString(i));
		}
		return project;
	}

    public void startSaleListActivity(int projectId) {
        Intent intent = new Intent(this, SaleListActivity.class);
        intent.putExtra("project_id", projectId);
        startActivity(intent);
    }

    public void initializeUI() throws JSONException, IOException, ClassNotFoundException {

        setContentView(R.layout.project_info);
        saleButton = (Button) findViewById(R.id.btn_sale);
        saleButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                startSaleListActivity(projectId);
            }
        });

        dealButton = (Button) findViewById(R.id.btn_deal);
        dealButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                reportError(getApplicationContext(), "暂不可用");
            }
        });
        showProject(project);
    }

    public void showProject(ProjectInfo project) throws IOException, JSONException, ClassNotFoundException {
        TypeMetadata meta = TypeMetadata.getInstance(getApplicationContext());
        //ImageView frontPic = (ImageView) findViewById(R.id.developer_front);

		// Set Image Slideshow adapter
		ImageSlideAdapter imageAdapter = new ImageSlideAdapter(this, project.getImages());
		imagePager = (ViewPager) findViewById(R.id.image_pager);
		imagePager.setAdapter(imageAdapter);
        imagePager.setOffscreenPageLimit(5);

		// Set all text fields
        TextView developerNameText = (TextView) findViewById(R.id.developer_title);
        TextView projectNameText = (TextView) findViewById(R.id.project_title);
        TextView locationText = (TextView) findViewById(R.id.location_title);
        TextView descText = (TextView) findViewById(R.id.developer_desc);

        developerNameText.setText(project.getProjectName());
        projectNameText.setText("开发商: " + project.getDeveloperName());
        locationText.setText("地点: " + meta.getLocation().get(project.getLocationId()));
        descText.setText(project.getDescription());
    }
//	private ProjectInfo getProjectById(int developerId) {
//
//		if (developerId == 0) return new ProjectInfo(developerId, "富力", "国庆献礼", 0,
//				"天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，" +
//				"最近的一个商业中心距离只有20米。天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。" +
//				"毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，最近的一个商业中心距离只有20米。" +
//				"占地面积大概是100000平方米，最近的一个商业中心距离只有20米。" +
//				"毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，最近的一个商业中心距离只有20米。" +
//				"占地面积大概是100000平方米，最近的一个商业中心距离只有20米。" +
//				"最近的一个商业中心距离只有20米。天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。" +
//				"最近的一个商业中心距离只有20米。占地面积大概是100000平方米，最近的一个商业中心距离只有20米。" +
//				"最近的一个商业中心距离只有20米。天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。" +
//				"最近的一个商业中心距离只有20米。天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。");
//		else if (developerId == 1) return new ProjectInfo(developerId, "万科", "国庆献礼", 0,
//				"天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，" +
//				"最近的一个商业中心距离只有20米。天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。" +
//				"毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，最近的一个商业中心距离只有20米。" +
//				"占地面积大概是100000平方米，最近的一个商业中心距离只有20米。");
//		else if (developerId == 2) return new ProjectInfo(developerId, "恒大", "国庆献礼", 0,
//				"天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，" +
//				"最近的一个商业中心距离只有20米。天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。" +
//				"毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，最近的一个商业中心距离只有20米。" +
//				"占地面积大概是100000平方米，最近的一个商业中心距离只有20米。");
//		else if (developerId == 3) return new ProjectInfo(developerId, "方瑞", "国庆献礼", 0,
//				"天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，" +
//				"最近的一个商业中心距离只有20米。天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。" +
//				"毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，最近的一个商业中心距离只有20米。" +
//				"占地面积大概是100000平方米，最近的一个商业中心距离只有20米。");
//		else if (developerId == 4) return new ProjectInfo(developerId, "Long Foster", "国庆献礼", 0,
//				"天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，" +
//				"最近的一个商业中心距离只有20米。天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。" +
//				"毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，最近的一个商业中心距离只有20米。" +
//				"占地面积大概是100000平方米，最近的一个商业中心距离只有20米。");
//		else if (developerId == 5) return new ProjectInfo(developerId, "国美", "国庆献礼", 0,
//				"天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，" +
//				"最近的一个商业中心距离只有20米。天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。" +
//				"毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，最近的一个商业中心距离只有20米。" +
//				"占地面积大概是100000平方米，最近的一个商业中心距离只有20米。");
//		else return new ProjectInfo(developerId, "苏宁", "国庆献礼", 0,
//				"天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，" +
//				"最近的一个商业中心距离只有20米。天津富力城项目位于南开区老城墙内，毗邻规划中的地铁1好险，2好险。" +
//				"毗邻规划中的地铁1好险，2好险。占地面积大概是100000平方米，最近的一个商业中心距离只有20米。" +
//				"占地面积大概是100000平方米，最近的一个商业中心距离只有20米。");
//
//	}

}
