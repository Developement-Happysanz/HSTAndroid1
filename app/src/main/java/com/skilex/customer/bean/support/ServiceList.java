package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServiceList  {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("services")
    @Expose
    private ArrayList<Service> serviceArrayList = new ArrayList<>();

    /**
     * @return The count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count The count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return The serviceArrayList
     */
    public ArrayList<Service> getserviceArrayList() {
        return serviceArrayList;
    }

    /**
     * @param serviceArrayList The serviceArrayList
     */
    public void setserviceArrayList(ArrayList<Service> serviceArrayList) {
        this.serviceArrayList = serviceArrayList;
    }
}