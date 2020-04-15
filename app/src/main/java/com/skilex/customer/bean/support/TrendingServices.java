package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TrendingServices implements Serializable {
    @SerializedName("service_id")
    @Expose
    private String service_id;

    @SerializedName("service_name")
    @Expose
    private String service_name;

    @SerializedName("service_ta_name")
    @Expose
    private String service_ta_name;

    @SerializedName("service_pic_url")
    @Expose
    private String service_pic_url;
    
    @SerializedName("main_cat_id")
    @Expose
    private String main_cat_id;

    @SerializedName("sub_cat_id")
    @Expose
    private String sub_cat_id;

    @SerializedName("size")
    @Expose
    private int size = 3;

    /**
     * @return The service_id
     */
    public String getservice_id() {
        return service_id;
    }

    /**
     * @param service_id The service_id
     */
    public void setservice_id(String service_id) {
        this.service_id = service_id;
    }

    /**
     * @return The service_name
     */
    public String getservice_name() {
        return service_name;
    }

    /**
     * @param service_name The service_name
     */
    public void setservice_name(String service_name) {
        this.service_name = service_name;
    }

    /**
     * @return The service_ta_name
     */
    public String getservice_ta_name() {
        return service_ta_name;
    }

    /**
     * @param service_ta_name The service_ta_name
     */
    public void setservice_ta_name(String service_ta_name) {
        this.service_ta_name = service_ta_name;
    }

    /**
     * @return The service_pic_url
     */
    public String getservice_pic_url() {
        return service_pic_url;
    }

    /**
     * @param service_pic_url The service_pic_url
     */
    public void setservice_pic_url(String service_pic_url) {
        this.service_pic_url = service_pic_url;
    }

    /**
     * @return The main_cat_id
     */
    public String getmain_cat_id() {
        return main_cat_id;
    }

    /**
     * @param main_cat_id The main_cat_id
     */
    public void setmain_cat_id(String main_cat_id) {
        this.main_cat_id = main_cat_id;
    }

    /**
     * @return The sub_cat_id
     */
    public String getsub_cat_id() {
        return sub_cat_id;
    }

    /**
     * @param sub_cat_id The sub_cat_id
     */
    public void setsub_cat_id(String sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}