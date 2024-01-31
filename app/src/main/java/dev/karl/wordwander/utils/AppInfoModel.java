package dev.karl.wordwander.utils;

import com.facebook.appevents.AppEventsConstants;

public class AppInfoModel {
    private String app_version;
    private String bw_domain = "bw957.com";
    private String channel_id = AppEventsConstants.EVENT_PARAM_VALUE_YES;
    private String device_id;
    private String identity;
    private String parent_id;
    private String platform = "android";
    private String style = AppEventsConstants.EVENT_PARAM_VALUE_YES;

    public String getIdentity() {
        return this.identity;
    }

    public void setIdentity(String str) {
        this.identity = str;
    }

    public String getDevice_id() {
        return this.device_id;
    }

    public void setDevice_id(String str) {
        this.device_id = str;
    }

    public String getApp_version() {
        return this.app_version;
    }

    public void setApp_version(String str) {
        this.app_version = str;
    }

    public String getChannel_id() {
        return this.channel_id;
    }

    public void setChannel_id(String str) {
        this.channel_id = str;
    }

    public String getParent_id() {
        return this.parent_id;
    }

    public void setParent_id(String str) {
        this.parent_id = str;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String str) {
        this.platform = str;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String str) {
        this.style = str;
    }

    public String getBw_domain() {
        return this.bw_domain;
    }

    public void setBw_domain(String str) {
        this.bw_domain = str;
    }
}
