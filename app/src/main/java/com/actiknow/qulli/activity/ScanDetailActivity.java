package com.actiknow.qulli.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.actiknow.qulli.R;
import com.actiknow.qulli.model.BookingStatus;
import com.actiknow.qulli.utils.AppConfigTags;
import com.actiknow.qulli.utils.AppConfigURL;
import com.actiknow.qulli.utils.Constants;
import com.actiknow.qulli.utils.NetworkConnection;
import com.actiknow.qulli.utils.UserDetailsPref;
import com.actiknow.qulli.utils.Utils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ScanDetailActivity extends AppCompatActivity {
    UserDetailsPref userDetailsPref;
    List<BookingStatus> bookingStatusList = new ArrayList<>();
    int status_id = 0;
    int booking_id = 0;
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
    TextView tvAvailableStatus;
    TextView tvSubmit;
    ImageView ivBack;
    int attribute_id;
    ArrayList<String> bookingStatusText = new ArrayList<String>();

    CoordinatorLayout clMain;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_product_detail);
        initView();
        initData();
        initListener();
    }


    private void initView() {
        tvBookingId = (TextView) findViewById(R.id.tvBookingId);
        tvBookingStatus = (TextView) findViewById(R.id.tvBookingStatus);
        tvClientName = (TextView) findViewById(R.id.tvClientName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvPickup = (TextView) findViewById(R.id.tvPickup);
        tvPickUpAddress = (TextView) findViewById(R.id.tvPickUpAddress);
        tvPickUpDate = (TextView) findViewById(R.id.tvPickUpDate);
        tvPickuptime = (TextView) findViewById(R.id.tvPickuptime);
        tvDropOff = (TextView) findViewById(R.id.tvDropOff);
        tvDropUpAddress = (TextView) findViewById(R.id.tvDropUpAddress);
        tvDropUpDate = (TextView) findViewById(R.id.tvDropUpDate);
        tvDropuptime = (TextView) findViewById(R.id.tvDropuptime);
        tvNoOfItems = (TextView) findViewById(R.id.tvNoOfItems);
        tvAvailableStatus = (TextView) findViewById(R.id.tvAvailableStatus);
        tvSubmit = (TextView) findViewById(R.id.tvSubmit);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        clMain = (CoordinatorLayout) findViewById(R.id.clMain);
    }

    private void initData() {
        userDetailsPref = UserDetailsPref.getInstance();
        progressDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        try {
            JSONObject jsonObj = new JSONObject(intent.getStringExtra("response"));
            boolean error = jsonObj.getBoolean(AppConfigTags.ERROR);
            String message = jsonObj.getString(AppConfigTags.MESSAGE);
            if (!error) {

                JSONArray available_status = jsonObj.getJSONArray(AppConfigTags.AVAILABLE_STATUS);
                if (available_status.length() > 0) {
                    tvAvailableStatus.setVisibility(View.VISIBLE);
                } else {
                    tvAvailableStatus.setVisibility(View.GONE);
                }
                bookingStatusList.clear();
                for (int i = 0; i < available_status.length(); i++) {
                    JSONObject statusObject = available_status.getJSONObject(i);
                    BookingStatus bookingStatus = new BookingStatus(statusObject.getInt(AppConfigTags.STATUS_ID),
                            statusObject.getString(AppConfigTags.STATUS_TEXT)
                    );
                    bookingStatusList.add(bookingStatus);
                }
                attribute_id = jsonObj.getInt(AppConfigTags.ATTRIBUTE_ID);
                booking_id = jsonObj.getInt(AppConfigTags.BOOKING_ID);
                tvBookingId.setText("Booking Id :" + jsonObj.getInt(AppConfigTags.BOOKING_ID));
                tvBookingStatus.setText(jsonObj.getString(AppConfigTags.BOOKING_STATUS));
                tvClientName.setText(jsonObj.getString(AppConfigTags.CLIENT_NAME));
                tvEmail.setText(jsonObj.getString(AppConfigTags.CLIENT_EMAIL));
                tvPhone.setText(jsonObj.getString(AppConfigTags.CLIENT_PHONE));
                tvPickUpAddress.setText(jsonObj.getString(AppConfigTags.PICKUP_LOCATION));
                tvPickUpDate.setText(jsonObj.getString(AppConfigTags.PICKUP_DATE));
                tvPickuptime.setText(jsonObj.getString(AppConfigTags.PICKUP_TIME_START));
                tvDropUpDate.setText(jsonObj.getString(AppConfigTags.DROP_DATE));
                tvDropuptime.setText(jsonObj.getString(AppConfigTags.DROP_TIME_START));

                tvBookingId.setText("Booking Id : " + jsonObj.getInt(AppConfigTags.BOOKING_ID));
                tvBookingStatus.setText("Current Status : " + jsonObj.getString(AppConfigTags.BOOKING_STATUS));
                tvClientName.setText("Name : " + jsonObj.getString(AppConfigTags.CLIENT_NAME));
                tvEmail.setText("Email : " + jsonObj.getString(AppConfigTags.CLIENT_EMAIL));
                tvPhone.setText("Phone : " + jsonObj.getString(AppConfigTags.CLIENT_PHONE));
                tvPickUpAddress.setText(jsonObj.getString(AppConfigTags.PICKUP_LOCATION));
                tvPickUpDate.setText(jsonObj.getString(AppConfigTags.PICKUP_DATE));
                tvPickuptime.setText(jsonObj.getString(AppConfigTags.PICKUP_TIME_START) + " - " + jsonObj.getString(AppConfigTags.PICKUP_TIME_END));
                tvDropUpAddress.setText(jsonObj.getString(AppConfigTags.DROP_LOCATION));
                tvDropUpDate.setText(jsonObj.getString(AppConfigTags.DROP_DATE));
                tvDropuptime.setText(jsonObj.getString(AppConfigTags.DROP_TIME_START) + " - " + jsonObj.getString(AppConfigTags.DROP_TIME_END));
                tvNoOfItems.setText("Number of Item : " + jsonObj.getString(AppConfigTags.NO_OF_ITEMS));
            } else {
                Utils.showSnackBar(ScanDetailActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
            }
            progressDialog.dismiss();
        } catch (Exception e) {
            progressDialog.dismiss();
            Utils.showSnackBar(ScanDetailActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
            e.printStackTrace();
        }
    }

    private void initListener() {
        tvAvailableStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusDialog();

            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBookingStatus();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void statusDialog() {
        int j = 0;
        bookingStatusText.clear();
        for (int i = 0; i < bookingStatusList.size(); i++) {
            bookingStatusText.add(bookingStatusList.get(i).getStatus_text());
        }

        new MaterialDialog.Builder(this)
                .items(bookingStatusText)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        //  Toast.makeText(ScanDetailActivity.this, which + ": " + text + ", ID = " + view.getId(), Toast.LENGTH_SHORT).show();
                        tvAvailableStatus.setVisibility(View.VISIBLE);
                        tvAvailableStatus.setText(text);
                        status_id = bookingStatusList.get(which).getStatus_id();
                        if (tvAvailableStatus.getText().toString().equalsIgnoreCase("Add Available Status")) {
                            tvSubmit.setVisibility(View.GONE);
                        } else {
                            tvSubmit.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .show();
    }

    private void updateBookingStatus() {
        if (NetworkConnection.isNetworkAvailable(ScanDetailActivity.this)) {
            Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.UPDATE_STATUS, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.UPDATE_STATUS,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                    if (!error) {
                                        MaterialDialog dialog = new MaterialDialog.Builder(ScanDetailActivity.this)
                                                .content(message)
                                                .positiveColor(getResources().getColor(R.color.primary_text))
                                                .contentColor(getResources().getColor(R.color.primary_text))
                                                .negativeColor(getResources().getColor(R.color.primary_text))
                                                .canceledOnTouchOutside(true)
                                                .cancelable(true)
                                                .positiveText("OK")
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        finish();
                                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                                    }
                                                }).build();
                                        dialog.show();
                                    } else {
                                        Utils.showSnackBar(ScanDetailActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(ScanDetailActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(ScanDetailActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss();
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog(Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
                            }
                            Utils.showSnackBar(ScanDetailActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                            progressDialog.dismiss();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.BOOKING_ID, String.valueOf(booking_id));
                    params.put(AppConfigTags.STATUS_ID, String.valueOf(status_id));
                    params.put(AppConfigTags.ATTRIBUTE_ID, String.valueOf(attribute_id));

                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {
            Utils.showSnackBar(this, clMain, getResources().getString(R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_go_to_settings), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
            });
        }
    }
}
