package com.alfresco.support.alfrescodb.beans;

import java.io.Serializable;
import java.math.BigInteger;

public class ActivitiesFeedByTypeBean implements Serializable {
    private BigInteger count;
    private String postDate;
    private String siteNetwork;
    private String activityType;
    
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
    public String getActivityType() {
        return activityType;
    }
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
}
