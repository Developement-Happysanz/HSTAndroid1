package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CartServiceList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("cart_list")
    @Expose
    private ArrayList<CartService> serviceArrayList = new ArrayList<>();

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
    public ArrayList<CartService> getserviceArrayList() {
        return serviceArrayList;
    }

    /**
     * @param serviceArrayList The serviceArrayList
     */
    public void setserviceArrayList(ArrayList<CartService> serviceArrayList) {
        this.serviceArrayList = serviceArrayList;
    }
}