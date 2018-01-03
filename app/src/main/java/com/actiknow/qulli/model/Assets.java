package com.actiknow.qulli.model;

/**
 * Created by l on 03/01/2018.
 */

public class Assets {
    String bar_code,status;

    public Assets(String bar_code, String status) {
        this.bar_code = bar_code;
        this.status = status;
    }

    public String getBar_code() {
        return bar_code;
    }

    public void setBar_code(String bar_code) {
        this.bar_code = bar_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
