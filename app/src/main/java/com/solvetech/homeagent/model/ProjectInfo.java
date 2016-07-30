package com.solvetech.homeagent.model;

import java.util.ArrayList;

public class ProjectInfo {
	
	public ProjectInfo(int developerId, String developerName, String projectName, int locationId,
			String description) {
		super();
		this.developerName = developerName;
		this.projectName = projectName;
		this.locationId = locationId;
		this.description = description;
	}

	public ProjectInfo() {
		super();
        this.saleList = new ArrayList<PropertyLayout>();
        this.images = new ArrayList<String>();
	}

	private String developerName;
	private String projectName;
	private int locationId;
	private String description;
	private ArrayList<PropertyLayout> saleList;
    private ArrayList<String> images;

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
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<PropertyLayout> getSaleList() {
		return saleList;
	}
	public void setSaleList(ArrayList<PropertyLayout> saleList) {
		this.saleList = saleList;
	}

	public void addSale(PropertyLayout sale) {
		this.saleList.add(sale);
	}

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

}
