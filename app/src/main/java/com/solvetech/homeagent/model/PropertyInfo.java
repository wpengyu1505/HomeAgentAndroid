package com.solvetech.homeagent.model;

import java.math.BigDecimal;

/**
 * Created by wpy on 10/18/15.
 */
public class PropertyInfo {

    int propertyId;
    int projectId;
    double propertyArea;
    double propertyPrice;
    String propertyLayout;

    public PropertyInfo(int propertyId, int projectId, double propertyArea, double propertyPrice, String propertyLayout) {
        this.propertyId = propertyId;
        this.projectId = projectId;
        this.propertyArea = propertyArea;
        this.propertyPrice = propertyPrice;
        this.propertyLayout = propertyLayout;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public double getPropertyArea() {
        return propertyArea;
    }

    public void setPropertyArea(double propertyArea) {
        this.propertyArea = propertyArea;
    }

    public double getPropertyPrice() {
        return propertyPrice;
    }

    public void setPropertyPrice(double propertyPrice) {
        this.propertyPrice = propertyPrice;
    }

    public String getPropertyLayout() {
        return propertyLayout;
    }

    public void setPropertyLayout(String propertyLayout) {
        this.propertyLayout = propertyLayout;
    }

}
