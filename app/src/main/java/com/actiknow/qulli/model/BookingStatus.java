package com.actiknow.qulli.model;

/**
 * Created by sud on 29/12/17.
 */

public class BookingStatus {
    int status_id;
    String status_text;

    public BookingStatus(int status_id, String status_text) {
        this.status_id = status_id;
        this.status_text = status_text;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public String getStatus_text() {
        return status_text;
    }

    public void setStatus_text(String status_text) {
        this.status_text = status_text;
    }
}
