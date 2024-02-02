package dev.karl.wordwander.utils;

import com.facebook.appevents.AppEventsConstants;

public class AppInfoModel {
    private String app_version;
    private String channel_id = AppEventsConstants.EVENT_PARAM_VALUE_YES;
    private String device_id;
    private String identity;
    private String parent_id;
    private String style = AppEventsConstants.EVENT_PARAM_VALUE_YES;

    public void setIdentity(String str) {
        this.identity = str;
    }

    public void setDevice_id(String str) {
        this.device_id = str;
    }

    public void setApp_version(String str) {
        this.app_version = str;
    }

    public void setChannel_id(String str) {
        this.channel_id = str;
    }

    public void setParent_id(String str) {
        this.parent_id = str;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String str) {
        this.style = str;
    }
}
