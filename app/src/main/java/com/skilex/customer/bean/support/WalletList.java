package com.skilex.customer.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WalletList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("wallet_data")
    @Expose
    private ArrayList<Wallet> walletArrayList = new ArrayList<>();

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
     * @return The walletArrayList
     */
    public ArrayList<Wallet> getWalletArrayList() {
        return walletArrayList;
    }

    /**
     * @param walletArrayList The walletArrayList
     */
    public void setWalletArrayList(ArrayList<Wallet> walletArrayList) {
        this.walletArrayList = walletArrayList;
    }
}