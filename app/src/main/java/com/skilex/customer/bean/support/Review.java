package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Review  implements Serializable {

    @SerializedName("service_id")
    @Expose
    private String service_id;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("review")
    @Expose
    private String review;
    @SerializedName("customer_name")
    @Expose
    private String customer_name;
    @SerializedName("customer_id")
    @Expose
    private String customer_id;
    @SerializedName("profile_picture")
    @Expose
    private String profile_picture;

    @SerializedName("review_date")
    @Expose
    private String review_date;

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String id) {
        this.service_id = service_id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getReview_date() {
        return review_date;
    }

    public void setReview_date(String review_date) {
        this.review_date = review_date;
    }
}
