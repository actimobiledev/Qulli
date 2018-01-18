package com.actiknow.qulli.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actiknow.qulli.R;
import com.actiknow.qulli.model.Booking;
import com.actiknow.qulli.utils.AppConfigTags;
import com.actiknow.qulli.utils.AppConfigURL;
import com.actiknow.qulli.utils.Constants;
import com.actiknow.qulli.utils.NetworkConnection;
import com.actiknow.qulli.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    int scanItem;
    ProgressBar progressDialog;
    private Activity activity;
    private List<Booking> bookingList = new ArrayList<Booking> ();
    private Barcode barcodeResult;

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
        progressDialog=new ProgressBar(activity);
        Utils.setTypefaceToAllViews (activity, holder.tvBookingId);
        holder.tvBookingId.setText("Booking Id : "+booking.getBooking_id());
        holder.tvBookingStatus.setText("Current Status : "+booking.getBooking_status());
        holder.tvClientName.setText("Name : "+booking.getClient_name());
        holder.tvEmail.setText("Email : "+booking.getClient_email());
        holder.tvPhone.setText("Phone : "+booking.getClient_phone());
        holder.tvPickUpAddress.setText(booking.getPickup_address());
        holder.tvPickUpDate.setText("Date : "+booking.getPickup_date());
        holder.tvPickuptime.setText("Time : "+booking.getPickup_time_start()+" - "+booking.getPickup_time_end());
        holder.tvDropUpAddress.setText(booking.getDrop_address());
        holder.tvDropUpDate.setText(booking.getDrop_date());
        holder.tvDropuptime.setText(booking.getDrop_time_start()+" - "+booking.getDrop_time_end());
        holder.tvNoOfItems.setText("Number of Item : "+booking.getNumber_of_item());

        try {
            final JSONArray jsonArray= new JSONArray(booking.getAsset());
            scanItem= jsonArray.length();
            holder.tvNoOfItemsScan.setText("Item Scanned : "+scanItem);
            holder.ivAddAsset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (booking.getNumber_of_item()==jsonArray.length()) {
                        Toast.makeText(activity, "Already Maximum number of item scanned.", Toast.LENGTH_LONG).show();
                    } else {
                        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                                .withActivity (activity)
                                .withEnableAutoFocus (true)
                                .withBleepEnabled (true)
                                .withBackfacingCamera ()
                                .withCenterTracker ()
                                .withText ("Place the barcode in center")
                                //.withCenterTracker (R.drawable.barcode_scan_default, R.drawable.barcode_scan_pass)
                                .withResultListener (new MaterialBarcodeScanner.OnResultListener () {
                                    @Override
                                    public void onResult (Barcode barcode) {
                                        barcodeResult = barcode;
                                        addAssetValue(String.valueOf(barcode.rawValue),booking.getBooking_id());
                                    }
                                })
                                .build ();
                        materialBarcodeScanner.startScan ();

                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    
    private void addAssetValue (final String barcode, final String booking_id) {
        if (NetworkConnection.isNetworkAvailable (activity)) {
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.ADD_ASSET, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.ADD_ASSET,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog (Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put (AppConfigTags.BARCODE_VALUE, barcode);
                    params.put (AppConfigTags.BOOKING_ID, booking_id);
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 60);
        } else {
        }
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
        TextView tvNoOfItemsScan;
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
            tvNoOfItemsScan=(TextView)view.findViewById(R.id.tvNoAsset);
            ivAddAsset=(ImageView)view.findViewById(R.id.ivAddAsset);

            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
           // final Booking jobDescription = bookingList.get (getLayoutPosition ());
           // activity.overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            mItemClickListener.onItemClick(v,getLayoutPosition());
            
        }
    }
}