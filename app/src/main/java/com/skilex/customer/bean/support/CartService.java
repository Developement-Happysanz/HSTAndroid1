package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartService implements Serializable {
    @SerializedName("cart_id")
    @Expose
    private String cart_id;

    @SerializedName("service_name")
    @Expose
    private String service_name;

    @SerializedName("service_ta_name")
    @Expose
    private String service_ta_name;

    @SerializedName("service_picture")
    @Expose
    private String service_picture;

    @SerializedName("rate_card")
    @Expose
    private String rate_card;

    @SerializedName("is_advance_payment")
    @Expose
    private String is_advance_payment;

    @SerializedName("advance_amount")
    @Expose
    private String advance_amount;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("size")
    @Expose
    private int size = 3;

    /**
     * @return The cart_id
     */
    public String getCart_id() {
        return cart_id;
    }

    /**
     * @param cart_id The cart_id
     */
    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
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
     * @return The rate_card
     */
    public String getRate_card() {
        return rate_card;
    }

    /**
     * @param rate_card The rate_card
     */
    public void setRate_card(String rate_card) {
        this.rate_card = rate_card;
    }

    /**
     * @return The service_picture
     */
    public String getService_picture() {
        return service_picture;
    }

    /**
     * @param service_picture The service_picture
     */
    public void setService_picture(String service_picture) {
        this.service_picture = service_picture;
    }

    /**
     * @return The is_advance_payment
     */
    public String getIs_advance_payment() {
        return is_advance_payment;
    }

    /**
     * @param is_advance_payment The is_advance_payment
     */
    public void setIs_advance_payment(String is_advance_payment) {
        this.is_advance_payment = is_advance_payment;
    }

    /**
     * @return The advance_amount
     */
    public String getAdvance_amount() {
        return advance_amount;
    }

    /**
     * @param advance_amount The advance_amount
     */
    public void setAdvance_amount(String advance_amount) {
        this.advance_amount = advance_amount;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}