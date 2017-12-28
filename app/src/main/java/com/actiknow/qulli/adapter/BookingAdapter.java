package com.actiknow.qulli.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actiknow.qulli.R;
import com.actiknow.qulli.model.Booking;
import com.actiknow.qulli.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;

    private Activity activity;
    private List<Booking> bookingList = new ArrayList<Booking>();

    public BookingAdapter(Activity activity, List<Booking> bookingList) {
        this.activity = activity;
        this.bookingList = bookingList;
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from (parent.getContext ());
        final View sView = mInflater.inflate (R.layout.list_item_booking, parent, false);
        return new ViewHolder (sView);
    }

    @Override
    public void onBindViewHolder (final ViewHolder holder, int position) {//        runEnterAnimation (holder.itemView);
        final Booking booking = bookingList.get (position);
        Utils.setTypefaceToAllViews (activity, holder.tvBookingId);
        holder.tvBookingId.setText("Booking Id : "+booking.getBooking_id());
        holder.tvBookingStatus.setText(booking.getBooking_status());
        holder.tvClientName.setText(booking.getClient_name());
        holder.tvEmail.setText(booking.getClient_email());
        holder.tvPhone.setText(booking.getClient_phone());
        holder.tvPickUpAddress.setText(booking.getPickup_address());
        holder.tvPickUpDate.setText(booking.getPickup_date());
        holder.tvPickuptime.setText(booking.getPickup_time_start()+" - "+booking.getPickup_time_end());
        holder.tvDropUpAddress.setText(booking.getDrop_address());
        holder.tvDropUpDate.setText(booking.getDrop_date());
        holder.tvDropuptime.setText(booking.getDrop_time_start()+"-"+booking.getDrop_time_end());
        holder.tvNoOfItems.setText("Number of Item : "+booking.getNumber_of_item());



        holder.ivAddAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount () {
        return bookingList.size ();
    }

    public void SetOnItemClickListener (final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }



    public interface OnItemClickListener {
        public void onItemClick (View view, int position);
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvBookingId;
        TextView tvBookingStatus;
        TextView tvClientName;
        TextView tvEmail;
        TextView tvPhone;
        TextView tvPickup;
        TextView tvPickUpAddress;
        TextView tvPickUpDate;
        TextView tvPickuptime;
        TextView tvDropOff;
        TextView tvDropUpAddress;
        TextView tvDropUpDate;
        TextView tvDropuptime;
        TextView tvNoOfItems;
        ImageView ivAddAsset;

        
        ProgressBar progressBar;
        
        
        public ViewHolder (View view) {
            super (view);
            tvBookingId=(TextView)view.findViewById(R.id.tvBookingId);
            tvBookingStatus=(TextView)view.findViewById(R.id.tvBookingStatus);
            tvClientName=(TextView)view.findViewById(R.id.tvClientName);
            tvEmail=(TextView)view.findViewById(R.id.tvEmail);
            tvPhone=(TextView)view.findViewById(R.id.tvPhone);
            tvPickup=(TextView)view.findViewById(R.id.tvPickup);
            tvPickUpAddress=(TextView)view.findViewById(R.id.tvPickUpAddress);
            tvPickUpDate=(TextView)view.findViewById(R.id.tvPickUpDate);
            tvPickuptime=(TextView)view.findViewById(R.id.tvPickuptime);
            tvDropOff=(TextView)view.findViewById(R.id.tvDropOff);
            tvDropUpAddress=(TextView)view.findViewById(R.id.tvDropUpAddress);
            tvDropUpDate=(TextView)view.findViewById(R.id.tvDropUpDate);
            tvDropuptime=(TextView)view.findViewById(R.id.tvDropuptime);
            tvNoOfItems=(TextView)view.findViewById(R.id.tvNoOfItems);
            ivAddAsset=(ImageView)view.findViewById(R.id.ivAddAsset);

            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
            final Booking jobDescription = bookingList.get (getLayoutPosition ());

            activity.overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            
            
        }
    }
}