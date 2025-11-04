package com.alfresco.support.alfrescodb.beans;

import java.io.Serializable;
import java.math.BigInteger;

public class ActivitiesFeedByUserBean implements Serializable {
    private BigInteger count;
    private String postDate;
    private String siteNetwork;
    private String feedUserId;
    
    public BigInteger getCount() {
        return count;
    }
    public void setCount(BigInteger occurrences) {
        this.count = occurrences;
    }
    public String getPostDate() {
        return postDate;
    }
    public void setPostDate(String post_date) {
        this.postDate = post_date;
    }
    public String getSiteNetwork() {
        return siteNetwork;
    }
    public void setSiteNetwork(String siteNetwork) {
        this.siteNetwork = siteNetwork;
    }
    public String getFeedUserId() {
        return feedUserId;
    }
    public void setFeedUserId(String feedUserId) {
        this.feedUserId = feedUserId;
    }
}
