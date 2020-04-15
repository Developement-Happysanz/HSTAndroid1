package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Feedback implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("feedback_question")
    @Expose
    private String feedback_question;
    
    /**
     * @return The id
     */
    public String getid() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setid(String id) {
        this.id = id;
    }

    /**
     * @return The feedback_question
     */
    public String getfeedback_question() {
        return feedback_question;
    }

    /**
     * @param feedback_question The feedback_question
     */
    public void setfeedback_question(String feedback_question) {
        this.feedback_question = feedback_question;
    }

}