package com.solvetech.homeagent.model;

import java.io.Serializable;

public class CustomerInfo implements Serializable {
	
	private int customerId;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private int agentId;
	private int propertyClass;
	private int locationCd;
	private int priceRange;
	private int layoutReq;
	private int areaReq;
	private int furnish;
    private String other;
	
	public int getLayoutReq() {
		return layoutReq;
	}
	public void setLayoutReq(int layoutReq) {
		this.layoutReq = layoutReq;
	}
	
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public int getAgentId() {
		return agentId;
	}
	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	public int getPropertyClass() {
		return propertyClass;
	}
	public void setPropertyClass(int propertyClass) {
		this.propertyClass = propertyClass;
	}
	public int getLocationCd() {
		return locationCd;
	}
	public void setLocationCd(int locationCd) {
		this.locationCd = locationCd;
	}
	public int getPriceRange() {
		return priceRange;
	}
	public void setPriceRange(int priceRange) {
		this.priceRange = priceRange;
	}
	public int getAreaReq() {
		return areaReq;
	}
	public void setAreaReq(int areaReq) {
		this.areaReq = areaReq;
	}
	public int getFurnish() {
		return furnish;
	}
	public void setFurnish(int furnish) {
		this.furnish = furnish;
	}

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

}
