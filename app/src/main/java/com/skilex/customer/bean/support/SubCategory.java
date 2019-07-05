package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubCategory implements Serializable {

    @SerializedName("main_cat_id")
    @Expose
    private String main_cat_id;

    @SerializedName("sub_cat_id")
    @Expose
    private String sub_cat_id;

    @SerializedName("sub_cat_name")
    @Expose
    private String sub_cat_name;

    @SerializedName("sub_cat_ta_name")
    @Expose
    private String sub_cat_ta_name;

    @SerializedName("sub_cat_pic_url")
    @Expose
    private String sub_cat_pic_url;

    /**
     * @return The main_cat_id
     */
    public String getMain_cat_id() {
        return main_cat_id;
    }

    /**
     * @param main_cat_id The main_cat_id
     */
    public void setMain_cat_id(String main_cat_id) {
        this.main_cat_id = main_cat_id;
    }

    /**
     * @return The sub_cat_id
     */
    public String getSub_cat_id() {
        return sub_cat_id;
    }

    /**
     * @param sub_cat_id The sub_cat_id
     */
    public void setSub_cat_id(String sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    /**
     * @return The sub_cat_name
     */
    public String getSub_cat_name() {
        return sub_cat_name;
    }

    /**
     * @param sub_cat_name The sub_cat_name
     */
    public void setSub_cat_name(String sub_cat_name) {
        this.sub_cat_name = sub_cat_name;
    }

    /**
     * @return The sub_cat_ta_name
     */
    public String getSub_cat_ta_name() {
        return sub_cat_ta_name;
    }

    /**
     * @param sub_cat_ta_name The sub_cat_ta_name
     */
    public void setSub_cat_ta_name(String sub_cat_ta_name) {
        this.sub_cat_ta_name = sub_cat_ta_name;
    }

    /**
     * @return The sub_cat_pic_url
     */
    public String getSub_cat_pic_url() {
        return sub_cat_pic_url;
    }

    /**
     * @param sub_cat_pic_url The sub_cat_pic_url
     */
    public void setSub_cat_pic_url(String sub_cat_pic_url) {
        this.sub_cat_pic_url = sub_cat_pic_url;
    }

}