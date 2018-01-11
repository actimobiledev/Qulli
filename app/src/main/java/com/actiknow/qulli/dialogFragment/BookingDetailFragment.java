package com.actiknow.qulli.dialogFragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actiknow.qulli.R;
import com.actiknow.qulli.activity.MainActivity;
import com.actiknow.qulli.adapter.AssetsAdapter;
import com.actiknow.qulli.adapter.BookingAdapter;
import com.actiknow.qulli.model.Assets;
import com.actiknow.qulli.utils.AppConfigTags;
import com.actiknow.qulli.utils.AppConfigURL;
import com.actiknow.qulli.utils.Constants;
import com.actiknow.qulli.utils.NetworkConnection;
import com.actiknow.qulli.utils.RecyclerViewMargin;
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

public class BookingDetailFragment extends DialogFragment {
   // ImageView ivCancel;
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
    TextView tvTitle;
    TextView tvScan;
    ImageView ivAddAsset;
    ImageView ivCancel;
    ProgressDialog progressDialog;

    int booking_id;
    String arrayResponse;
    int scanItem=0;
    int numberOfItem;
    JSONArray jsonArrayAsset;
    private Barcode barcodeResult;
    AssetsAdapter assetsAdapter;
    RecyclerView rvAssetList;
    List<Assets> assetsList = new ArrayList<>();
    public BookingDetailFragment newInstance(String response, int booking_id) {
        BookingDetailFragment f = new BookingDetailFragment();
        Bundle args = new Bundle();
        args.putString("arrayResponse", response);
        args.putInt(AppConfigTags.BOOKING_ID, booking_id);
        f.setArguments(args);
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Window window = getDialog().getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;

    }
    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    //This is the filter
                    if (event.getAction() != KeyEvent.ACTION_UP)
                        return true;
                    else {
                        getDialog().dismiss();
                        //Hide your keyboard here!!!!!!
                        return true; // pretend we've processed it
                    }
                } else
                    return false; // pass on to be processed as normal
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }
    @Override


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fragment_booking_detail, container, false);
        initView(root);
        initBundle();
        initData();
        initListener();
        initAdapter();
        setData();
        return root;
    }
    private void initView(View root) {
        tvTitle = (TextView) root.findViewById(R.id.tvTitle);
        tvBookingId = (TextView) root.findViewById(R.id.tvBookingId);
        tvBookingStatus = (TextView) root.findViewById(R.id.tvBookingStatus);
        tvClientName = (TextView) root.findViewById(R.id.tvClientName);
        tvEmail = (TextView) root.findViewById(R.id.tvEmail);
        tvPhone = (TextView) root.findViewById(R.id.tvPhone);
        tvPickup = (TextView) root.findViewById(R.id.tvPickup);
        tvPickUpAddress = (TextView) root.findViewById(R.id.tvPickUpAddress);
        tvPickUpDate = (TextView) root.findViewById(R.id.tvPickUpDate);
        tvPickuptime = (TextView) root.findViewById(R.id.tvPickuptime);
        tvDropOff = (TextView) root.findViewById(R.id.tvDropOff);
        tvDropUpAddress = (TextView) root.findViewById(R.id.tvDropUpAddress);
        tvDropUpDate = (TextView) root.findViewById(R.id.tvDropUpDate);
        tvDropuptime = (TextView) root.findViewById(R.id.tvDropuptime);
        tvNoOfItems = (TextView) root.findViewById(R.id.tvNoOfItems);
        tvNoOfItemsScan = (TextView) root.findViewById(R.id.tvNoAsset);
        tvTitle = (TextView) root.findViewById(R.id.tvTitle);
        tvScan = (TextView) root.findViewById(R.id.tvScan);
        ivAddAsset = (ImageView) root.findViewById(R.id.ivAddAsset);
        ivCancel = (ImageView) root.findViewById (R.id.ivCancel);
        rvAssetList=(RecyclerView)root.findViewById(R.id.rvBarcodeValueList);
    }
    private void initBundle() {
        Bundle bundle = this.getArguments();
        booking_id = bundle.getInt(AppConfigTags.BOOKING_ID);
        arrayResponse = bundle.getString("arrayResponse");
    }
    private void initData() {
        Utils.setTypefaceToAllViews(getActivity(), tvTitle);
        progressDialog=new ProgressDialog(getActivity());
    }
    private void initAdapter() {
        assetsAdapter = new AssetsAdapter(getActivity(), assetsList);
        rvAssetList.setAdapter(assetsAdapter);
        rvAssetList.setHasFixedSize(true);
        rvAssetList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvAssetList.setLayoutManager (new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        rvAssetList.setItemAnimator (new DefaultItemAnimator ());
        rvAssetList.addItemDecoration (new RecyclerViewMargin ((int) Utils.pxFromDp (getActivity(), 16), (int) Utils.pxFromDp (getActivity(), 16), (int) Utils.pxFromDp (getActivity(), 16), (int) Utils.pxFromDp (getActivity(), 16), 2, 0, RecyclerViewMargin.LAYOUT_MANAGER_GRID, RecyclerViewMargin.ORIENTATION_VERTICAL));
    }
    private void initListener() {
        tvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberOfItem==scanItem) {
                    Toast.makeText(getActivity(), "Already Maximum number of item scanned.", Toast.LENGTH_LONG).show();
                } else {
                    final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                            .withActivity (getActivity())
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
                                    addAssetValue(String.valueOf(barcode.rawValue),booking_id);
                                }
                            })
                            .build ();
                    materialBarcodeScanner.startScan ();

                }
            }
        });


        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }
    private void setData() {
        try {
            JSONObject jsonObj = new JSONObject(arrayResponse);
            JSONArray jsonArrayBooking = jsonObj.getJSONArray(AppConfigTags.BOOKINGS);
            for (int i = 0; i < jsonArrayBooking.length(); i++) {
                JSONObject jsonObjectBooking = jsonArrayBooking.getJSONObject(i);
                if (jsonObjectBooking.getInt(AppConfigTags.BOOKING_ID) == booking_id) {
                    tvBookingId.setText("Booking Id : " + jsonObjectBooking.getInt(AppConfigTags.BOOKING_ID));
                    tvBookingStatus.setText("Current Status : " + jsonObjectBooking.getString(AppConfigTags.BOOKING_STATUS));
                    tvClientName.setText("Name : " + jsonObjectBooking.getString(AppConfigTags.CLIENT_NAME));
                    tvEmail.setText("Email : " + jsonObjectBooking.getString(AppConfigTags.CLIENT_EMAIL));
                    tvPhone.setText("Phone : " + jsonObjectBooking.getString(AppConfigTags.CLIENT_PHONE));
                    tvPickUpAddress.setText("Address : "+jsonObjectBooking.getString(AppConfigTags.PICKUP_LOCATION));
                    tvPickUpDate.setText("Date : "+jsonObjectBooking.getString(AppConfigTags.PICKUP_DATE));
                    tvPickuptime.setText("Time : "+jsonObjectBooking.getString(AppConfigTags.PICKUP_TIME_START) + " - " + jsonObjectBooking.getString(AppConfigTags.PICKUP_TIME_END));
                    tvDropUpAddress.setText("Address : "+jsonObjectBooking.getString(AppConfigTags.DROP_LOCATION));
                    tvDropUpDate.setText("Date : "+jsonObjectBooking.getString(AppConfigTags.DROP_DATE));
                    tvDropuptime.setText("Time : "+jsonObjectBooking.getString(AppConfigTags.DROP_TIME_START) + " - " + jsonObjectBooking.getString(AppConfigTags.DROP_TIME_END));
                    tvNoOfItems.setText("Number of Item : " + jsonObjectBooking.getInt(AppConfigTags.NO_OF_ITEMS));
                    numberOfItem= jsonObjectBooking.getInt(AppConfigTags.NO_OF_ITEMS);
                    tvTitle.setText("Booking Id : " + jsonObjectBooking.getInt(AppConfigTags.BOOKING_ID));
                    jsonArrayAsset = new JSONArray (jsonObjectBooking.getString (AppConfigTags.ASSETS));
                    scanItem= jsonArrayAsset.length();
                    tvNoOfItemsScan.setText("Item Scanned : "+scanItem);

                    for (int j=0; j<jsonArrayAsset.length();j++){
                        JSONObject jsonObject=jsonArrayAsset.getJSONObject(j);
                        assetsList.add(new Assets(jsonObject.getString(AppConfigTags.ASSET_BARCODE),jsonObject.getString(AppConfigTags.ASSET_STATUS)));
                    }
                    assetsAdapter.notifyDataSetChanged();


                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void addAssetValue (final String barcode, final int booking_id) {
        if (NetworkConnection.isNetworkAvailable (getActivity())) {
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.ADD_ASSET, true);
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_please_wait), true);
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
                                        assetsList.clear();
                                       // getDialog().dismiss();
                                        jsonArrayAsset = new JSONArray (jsonObj.getString (AppConfigTags.ASSETS));
                                        for (int j=0; j<jsonArrayAsset.length();j++){
                                            JSONObject jsonObject=jsonArrayAsset.getJSONObject(j);
                                            assetsList.add(new Assets(jsonObject.getString(AppConfigTags.ASSET_BARCODE),jsonObject.getString(AppConfigTags.ASSET_STATUS)));

                                        }
                                        assetsAdapter.notifyDataSetChanged();
                                        scanItem= jsonArrayAsset.length();
                                        tvNoOfItemsScan.setText("Item Scanned : "+scanItem);
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
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
                    params.put (AppConfigTags.BOOKING_ID, String.valueOf(booking_id));
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
}