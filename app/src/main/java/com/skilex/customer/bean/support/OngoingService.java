package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OngoingService implements Serializable {
    
    @SerializedName("service_order_id")
    @Expose
    private String service_order_id;

    @SerializedName("main_category")
    @Expose
    private String main_category;

    @SerializedName("main_category_ta")
    @Expose
    private String main_category_ta;

    @SerializedName("sub_category")
    @Expose
    private String sub_category;

    @SerializedName("sub_category_ta")
    @Expose
    private String sub_category_ta;

    @SerializedName("service_name")
    @Expose
    private String service_name;

    @SerializedName("service_ta_name")
    @Expose
    private String service_ta_name;

    @SerializedName("contact_person_name")
    @Expose
    private String contact_person_name;

    @SerializedName("order_date")
    @Expose
    private String order_date;

    @SerializedName("time_slot")
    @Expose
    private String time_slot;

    /**
     * @return The service_order_id
     */
    public String getservice_order_id() {
        return service_order_id;
    }

    /**
     * @param service_order_id The service_order_id
     */
    public void setservice_order_id(String service_order_id) {
        this.service_order_id = service_order_id;
    }


    /**
     * @return The main_category
     */
    public String getmain_category() {
        return main_category;
    }

    /**
     * @param main_category The main_category
     */
    public void setmain_category(String main_category) {
        this.main_category = main_category;
    }

    /**
     * @return The main_category_ta
     */
    public String getmain_category_ta() {
        return main_category_ta;
    }

    /**
     * @param main_category_ta The main_category_ta
     */
    public void setmain_category_ta(String main_category_ta) {
        this.main_category_ta = main_category_ta;
    }


    /**
     * @return The sub_category
     */
    public String getSub_main_category() {
        return sub_category;
    }

    /**
     * @param sub_category The sub_category
     */
    public void setSub_main_category(String sub_category) {
        this.sub_category = sub_category;
    }

    /**
     * @return The sub_category_ta
     */
    public String getSub_main_category_ta() {
        return sub_category_ta;
    }

    /**
     * @param sub_category_ta The sub_category_ta
     */
    public void setSub_main_category_ta(String sub_category_ta) {
        this.sub_category_ta = sub_category_ta;
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
     * @return The contact_person_name
     */
    public String getContact_person_name() {
        return contact_person_name;
    }

    /**
     * @param contact_person_name The contact_person_name
     */
    public void setContact_person_name(String contact_person_name) {
        this.contact_person_name = contact_person_name;
    }

    /**
     * @return The order_date
     */
    public String getOrder_date() {
        return order_date;
    }

    /**
     * @param order_date The order_date
     */
    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    /**
     * @return The time_slot
     */
    public String getTime_slot() {
        return time_slot;
    }

    /**
     * @param time_slot The time_slot
     */
    public void setTime_slot(String time_slot) {
        this.time_slot = time_slot;
    }

}