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
import com.actiknow.qulli.model.Assets;
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

public class AssetsAdapter extends RecyclerView.Adapter<AssetsAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;

    private Activity activity;
    private List<Assets> assetsList = new ArrayList<Assets>();
    int scanItem;
    ProgressBar progressDialog;
    private Barcode barcodeResult;

    public AssetsAdapter(Activity activity, List<Assets> assetsList) {
        this.activity = activity;
        this.assetsList = assetsList;
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from (parent.getContext ());
        final View sView = mInflater.inflate (R.layout.list_item_asset, parent, false);
        return new ViewHolder (sView);
    }

    @Override
    public void onBindViewHolder (final ViewHolder holder, int position) {//        runEnterAnimation (holder.itemView);
        final Assets assets = assetsList.get (position);
        progressDialog=new ProgressBar(activity);
        Utils.setTypefaceToAllViews (activity, holder.tvBarCode);
        holder.tvBarCode.setText(assets.getBar_code());
        holder.tvStatus.setText(assets.getStatus());

    }


    @Override
    public int getItemCount () {
        return assetsList.size ();
    }

    public void SetOnItemClickListener (final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvBarCode;
        TextView tvStatus;

        
        public ViewHolder (View view) {
            super (view);
            tvBarCode=(TextView)view.findViewById(R.id.tvBarcode);
            tvStatus=(TextView)view.findViewById(R.id.tvStatus);
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
           // final Assets jobDescription = bookingList.get (getLayoutPosition ());
           // activity.overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
          //  mItemClickListener.onItemClick(v,getLayoutPosition());
            
        }
    }
}