package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServiceHistoryList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("service_list")
    @Expose
    private ArrayList<ServiceHistory> serviceArrayList = new ArrayList<>();

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
    public ArrayList<ServiceHistory> getserviceArrayList() {
        return serviceArrayList;
    }

    /**
     * @param serviceArrayList The serviceArrayList
     */
    public void setserviceArrayList(ArrayList<ServiceHistory> serviceArrayList) {
        this.serviceArrayList = serviceArrayList;
    }
}