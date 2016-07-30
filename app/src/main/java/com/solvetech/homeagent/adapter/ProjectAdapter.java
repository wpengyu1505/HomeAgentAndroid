package com.solvetech.homeagent.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.solvetech.homeagent.HomeActivity;
import com.solvetech.homeagent.data.DataAccessClient;
import com.solvetech.homeagent.model.ProjectSummary;
import com.solvetech.homeagent.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProjectAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<ProjectSummary> projectList;
	private LayoutInflater inflater;
    private List<Integer> usedPositions  = new ArrayList<Integer>();
    DataAccessClient client;
	
	public ProjectAdapter(Context context, ArrayList<ProjectSummary> projectList) {
		this.projectList = projectList;
        this.inflater = LayoutInflater.from(context);
        String accessToken = context.getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
        client = new DataAccessClient(accessToken);
	}
	
	@Override
	public int getCount() {
		return projectList.size();
	}

	@Override
	public Object getItem(int position) {
		return projectList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.project_row, null);
            holder = new ViewHolder();
            holder.projectText = (TextView) convertView.findViewById(R.id.title);
            holder.developerText = (TextView) convertView.findViewById(R.id.project);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_project_thumbnail);
            holder.locationText = (TextView) convertView.findViewById(R.id.location);
            convertView.setTag(holder);
		} else {
            holder = (ViewHolder) convertView.getTag();
        }

		ProjectSummary currentProject = projectList.get(position);
		holder.projectText.setText(currentProject.getProjectName());
        Log.d("ProjectAdapter", "Project: " + currentProject.getProjectName());
		holder.developerText.setText("开发商: " + currentProject.getDeveloperName());
        holder.locationText.setText(currentProject.getLocatonName());

        if (holder.imageView != null) {
            new ImageDownloadTask(holder.imageView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentProject.getThumbnailUrl());
        }
		return convertView;
	}

    public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView = null;

        public ImageDownloadTask(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... urls) {

            Log.d("ProjectAdapter", "Now get image: " + urls[0]);
            Bitmap image = client.getWebImage((String) urls[0]);
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                imageView.setImageBitmap(image);
            }
        }
    }

    static class ViewHolder {
        TextView projectText;
        TextView developerText;
        TextView locationText;
        ImageView imageView;
    }

}
