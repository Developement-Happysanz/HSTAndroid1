package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Wallet implements Serializable {
    
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("transaction_amt")
    @Expose
    private String transaction_amt;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("created_date")
    @Expose
    private String created_date;

    @SerializedName("created_time")
    @Expose
    private String created_time;
    
    @SerializedName("notes")
    @Expose
    private String notes;

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
     * @return The transaction_amt
     */
    public String gettransaction_amt() {
        return transaction_amt;
    }

    /**
     * @param transaction_amt The transaction_amt
     */
    public void settransaction_amt(String transaction_amt) {
        this.transaction_amt = transaction_amt;
    }

    /**
     * @return The status
     */
    public String getstatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setstatus(String status) {
        this.status = status;
    }

    /**
     * @return The created_date
     */
    public String getcreated_date() {
        return created_date;
    }

    /**
     * @param created_date The created_date
     */
    public void setcreated_date(String created_date) {
        this.created_date = created_date;
    }

    /**
     * @return The created_time
     */
    public String getcreated_time() {
        return created_time;
    }

    /**
     * @param created_time The created_time
     */
    public void setcreated_time(String created_time) {
        this.created_time = created_time;
    }


    /**
     * @return The notes
     */
    public String getnotes() {
        return notes;
    }

    /**
     * @param notes The notes
     */
    public void setnotes(String notes) {
        this.notes = notes;
    }


}