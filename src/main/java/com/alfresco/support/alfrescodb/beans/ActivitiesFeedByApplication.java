package com.alfresco.support.alfrescodb.beans;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;

public class ActivitiesFeedByApplication implements Serializable {
    private BigInteger count;
    private Date postDate;
    private String siteNetwork;
    private String appTool;
    
    public BigInteger getCount() {
        return count;
    }
    public void setCount(BigInteger count) {
        this.count = count;
    }
    public Date getPostDate() {
        return postDate;
    }
    public void setPostDate(Date postDate) {
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
