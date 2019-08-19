package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Bill implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("file_bill")
    @Expose
    private String file_bill;

    @SerializedName("size")
    @Expose
    private int size = 2;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The file_bill
     */
    public String getFile_bill() {
        return file_bill;
    }

    /**
     * @param file_bill The file_bill
     */
    public void setFile_bill(String file_bill) {
        this.file_bill = file_bill;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}