package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Category implements Serializable {

    @SerializedName("cat_id")
    @Expose
    private String cat_id;

    @SerializedName("cat_name")
    @Expose
    private String cat_name;

    @SerializedName("cat_ta_name")
    @Expose
    private String cat_ta_name;

    @SerializedName("cat_pic_url")
    @Expose
    private String cat_pic_url;

    @SerializedName("size")
    @Expose
    private int size = 2;

    /**
     * @return The cat_id
     */
    public String getCat_id() {
        return cat_id;
    }

    /**
     * @param cat_id The cat_id
     */
    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    /**
     * @return The cat_name
     */
    public String getCat_name() {
        return cat_name;
    }

    /**
     * @param cat_name The cat_name
     */
    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    /**
     * @return The cat_ta_name
     */
    public String getCat_ta_name() {
        return cat_ta_name;
    }

    /**
     * @param cat_ta_name The cat_ta_name
     */
    public void setCat_ta_name(String cat_ta_name) {
        this.cat_ta_name = cat_ta_name;
    }

    /**
     * @return The cat_pic_url
     */
    public String getCat_pic_url() {
        return cat_pic_url;
    }

    /**
     * @param cat_pic_url The cat_pic_url
     */
    public void setCat_pic_url(String cat_pic_url) {
        this.cat_pic_url = cat_pic_url;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}