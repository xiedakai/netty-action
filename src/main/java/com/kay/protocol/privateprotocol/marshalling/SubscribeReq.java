package com.kay.protocol.privateprotocol.marshalling;

import java.io.Serializable;

/**
 * Created by 3307 on 2016/3/5.
 */
public class SubscribeReq implements Serializable {
    private static final long serialVersionUID = 1L;

    private int subReqID;
    private String userName;
    private String productName;

    public int getSubReqID() {
        return subReqID;
    }

    public void setSubReqID(int subReqID) {
        this.subReqID = subReqID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "SubscribeReq{" +
                "subReqID=" + subReqID +
                ", userName='" + userName + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}
