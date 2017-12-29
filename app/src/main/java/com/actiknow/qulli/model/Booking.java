package com.actiknow.qulli.model;

/**
 * Created by l on 28/12/2017.
 */

public class Booking {
    int booking_id, number_of_item;
    String booking_status,client_name,client_email,client_phone,
    pickup_date, pickup_address, pickup_time_start, pickup_time_end, drop_address, drop_date, drop_time_start,
    drop_time_end,notes,cost,asset;


    public Booking(int booking_id, String booking_status, String client_name, String client_email, String client_phone,
                   String pickup_address, String pickup_date, String pickup_time_start, String pickup_time_end,
                   String drop_address, String drop_date, String drop_time_start, String drop_time_end,int number_of_item,
                   String notes, String cost, String asset) {
        this.booking_id = booking_id;
        this.booking_status = booking_status;
        this.client_name = client_name;
        this.client_email = client_email;
        this.client_phone = client_phone;
        this.number_of_item = number_of_item;
        this.pickup_date = pickup_date;
        this.pickup_address = pickup_address;
        this.pickup_time_start = pickup_time_start;
        this.pickup_time_end = pickup_time_end;
        this.drop_date = drop_date;
        this.drop_address = drop_address;
        this.drop_time_start = drop_time_start;
        this.drop_time_end = drop_time_end;
        this.notes = notes;
        this.cost = cost;
        this.asset = asset;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public Booking() {
    }

    public int getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(int booking_id) {
        this.booking_id = booking_id;
    }

    public String getBooking_status() {
        return booking_status;
    }

    public void setBooking_status(String booking_status) {
        this.booking_status = booking_status;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getClient_email() {
        return client_email;
    }

    public void setClient_email(String client_email) {
        this.client_email = client_email;
    }

    public String getClient_phone() {
        return client_phone;
    }

    public void setClient_phone(String client_phone) {
        this.client_phone = client_phone;
    }

    public int getNumber_of_item() {
        return number_of_item;
    }

    public void setNumber_of_item(int number_of_item) {
        this.number_of_item = number_of_item;
    }

    public String getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(String pickup_date) {
        this.pickup_date = pickup_date;
    }

    public String getPickup_address() {
        return pickup_address;
    }

    public void setPickup_address(String pickup_address) {
        this.pickup_address = pickup_address;
    }

    public String getPickup_time_start() {
        return pickup_time_start;
    }

    public void setPickup_time_start(String pickup_time_start) {
        this.pickup_time_start = pickup_time_start;
    }

    public String getPickup_time_end() {
        return pickup_time_end;
    }

    public void setPickup_time_end(String pickup_time_end) {
        this.pickup_time_end = pickup_time_end;
    }

    public String getDrop_date() {
        return drop_date;
    }

    public void setDrop_date(String drop_date) {
        this.drop_date = drop_date;
    }

    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }

    public String getDrop_time_start() {
        return drop_time_start;
    }

    public void setDrop_time_start(String drop_time_start) {
        this.drop_time_start = drop_time_start;
    }

    public String getDrop_time_end() {
        return drop_time_end;
    }

    public void setDrop_time_end(String drop_time_end) {
        this.drop_time_end = drop_time_end;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
