package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FeedbackList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("feedback_question")
    @Expose
    private ArrayList<Feedback> feedbackArrayList = new ArrayList<>();

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
    public ArrayList<Feedback> getFeedbackArrayList() {
        return feedbackArrayList;
    }

    /**
     * @param feedbackArrayList The feedbackArrayList
     */
    public void setFeedbackArrayList(ArrayList<Feedback> feedbackArrayList) {
        this.feedbackArrayList = feedbackArrayList;
    }
}