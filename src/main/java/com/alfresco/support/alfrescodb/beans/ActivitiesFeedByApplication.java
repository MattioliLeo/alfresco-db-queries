package com.alfresco.support.alfrescodb.beans;

import java.io.Serializable;
import java.math.BigInteger;

public class ActivitiesFeedByApplication implements Serializable {
    private BigInteger count;
    private String postDate;
    private String siteNetwork;
    private String appTool;
    
    public BigInteger getCount() {
        return count;
    }
    public void setCount(BigInteger count) {
        this.count = count;
    }
    public String getPostDate() {
        return postDate;
    }
    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }
    public String getSiteNetwork() {
        return siteNetwork;
    }
    public void setSiteNetwork(String siteNetwork) {
        this.siteNetwork = siteNetwork;
    }
    public String getAppTool() {
        return appTool;
    }
    public void setAppTool(String appTool) {
        this.appTool = appTool;
    }
    
}
