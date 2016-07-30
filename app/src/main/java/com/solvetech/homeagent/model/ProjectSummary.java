package com.solvetech.homeagent.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ProjectSummary implements Serializable {

	int projectId;
	String developerName;
	String projectName;
	String locationName;
	String thumbnailUrl;
    Bitmap bitmap;

	public ProjectSummary(int developerId, String developerName,
			String projectName, String locationName, String thumbnailUrl) {
		super();
		this.projectId = developerId;
		this.developerName = developerName;
		this.projectName = projectName;
        this.locationName = locationName;
		this.thumbnailUrl = thumbnailUrl;
	}


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int developerId) {
		this.projectId = developerId;
	}
	public String getDeveloperName() {
		return developerName;
	}
	public void setDeveloperName(String developerName) {
		this.developerName = developerName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getLocatonName() {
		return locationName;
	}

	public void setLocatonName(String locatonName) {
		this.locationName = locatonName;
	}


}
