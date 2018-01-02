package com.actiknow.qulli.activity;

import android.Manifest;
import android.annotation.TargetApi;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.actiknow.qulli.R;
import com.actiknow.qulli.adapter.BookingAdapter;
import com.actiknow.qulli.dialogFragment.BookingDetailFragment;
import com.actiknow.qulli.model.Booking;
import com.actiknow.qulli.utils.AppConfigTags;
import com.actiknow.qulli.utils.AppConfigURL;
import com.actiknow.qulli.utils.Constants;
import com.actiknow.qulli.utils.NetworkConnection;
import com.actiknow.qulli.utils.SetTypeFace;
import com.actiknow.qulli.utils.UserDetailsPref;
import com.actiknow.qulli.utils.Utils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_LOGIN_SCREEN_RESULT = 2;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rvBooking;
    UserDetailsPref userDetailsPref;
    List<Booking> bookingList = new ArrayList<>();
    BookingAdapter bookingAdapter;
    CoordinatorLayout clMain;
    public static int PERMISSION_REQUEST_CODE = 11;

    private Barcode barcodeResult;
    TextView tvScan;
    ProgressDialog progressDialog;
    ImageView ivNavigation;


    private AccountHeader headerResult = null;
    private Drawer result = null;
    Bundle savedInstanceState;
    String arrayResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initAdapter();
        initListener();
        isLogin();
        initDrawer();

    }

    @Override
    protected void onResume() {
        BookingList();
        super.onResume();
    }

    private void isLogin() {
        userDetailsPref = UserDetailsPref.getInstance();
        if (userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.DRIVER_LOGIN_KEY).length() == 0) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private void initView() {
        clMain = (CoordinatorLayout) findViewById(R.id.clMain);
        rvBooking = (RecyclerView) findViewById(R.id.rvBooking);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        tvScan = (TextView) findViewById(R.id.tvScan);
        ivNavigation = (ImageView) findViewById(R.id.ivNavigation);
    }

    private void initData() {
        swipeRefreshLayout.setRefreshing(true);
        userDetailsPref = UserDetailsPref.getInstance();
        progressDialog = new ProgressDialog(this);

    }

    private void initAdapter() {
        bookingAdapter = new BookingAdapter(this, bookingList);
        rvBooking.setAdapter(bookingAdapter);
        rvBooking.setHasFixedSize(true);
        rvBooking.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvBooking.setItemAnimator(new DefaultItemAnimator());
    }

    private void initListener() {
        bookingAdapter.SetOnItemClickListener(new BookingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Booking booking=bookingList.get(position);
                android.app.FragmentTransaction ft = getFragmentManager ().beginTransaction ();
                BookingDetailFragment dialog = new BookingDetailFragment ().newInstance (arrayResponse,booking.getBooking_id());
                dialog.show (ft, "Contacts");

            }
        });




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkConnection.isNetworkAvailable(MainActivity.this)) {
                    BookingList();
                } else {
                    OfflineBookingList();
                }


            }
        });
        ivNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.openDrawer();
            }
        });

        tvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                Intent intent = new Intent (MainActivity.this, BarcodeScanningActivity.class);
//                startActivity (intent);
//                overridePendingTransition (R.anim.slide_in_up, R.anim.stay);

                checkPermissions();
                startScan();
            }
        });
    }

    public void BookingList() {
        swipeRefreshLayout.setRefreshing(true);
        if (NetworkConnection.isNetworkAvailable(MainActivity.this)) {
            bookingList.clear();

            Utils.showLog(Log.INFO, AppConfigTags.URL, AppConfigURL.BOOKING, true);
            StringRequest strRequest = new StringRequest(Request.Method.GET, AppConfigURL.BOOKING,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            arrayResponse=response;
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                userDetailsPref.putStringPref(MainActivity.this, UserDetailsPref.RESPONSE, response);
                                try {

                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean is_error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                    if (!is_error) {
                                        swipeRefreshLayout.setRefreshing(false);
                                        JSONArray jsonArrayBooking = jsonObj.getJSONArray(AppConfigTags.BOOKINGS);

                                        for (int i = 0; i < jsonArrayBooking.length(); i++) {
                                            JSONObject jsonObjectBooking = jsonArrayBooking.getJSONObject(i);
                                            Booking booking = new Booking(
                                                    jsonObjectBooking.getInt(AppConfigTags.BOOKING_ID),
                                                    jsonObjectBooking.getString(AppConfigTags.BOOKING_STATUS),
                                                    jsonObjectBooking.getString(AppConfigTags.CLIENT_NAME),
                                                    jsonObjectBooking.getString(AppConfigTags.CLIENT_EMAIL),
                                                    jsonObjectBooking.getString(AppConfigTags.CLIENT_PHONE),
                                                    jsonObjectBooking.getString(AppConfigTags.PICKUP_LOCATION),
                                                    jsonObjectBooking.getString(AppConfigTags.PICKUP_DATE),
                                                    jsonObjectBooking.getString(AppConfigTags.PICKUP_TIME_START),
                                                    jsonObjectBooking.getString(AppConfigTags.PICKUP_TIME_END),
                                                    jsonObjectBooking.getString(AppConfigTags.DROP_LOCATION),
                                                    jsonObjectBooking.getString(AppConfigTags.DROP_DATE),
                                                    jsonObjectBooking.getString(AppConfigTags.DROP_TIME_START),
                                                    jsonObjectBooking.getString(AppConfigTags.DROP_TIME_END),
                                                    jsonObjectBooking.getInt(AppConfigTags.NO_OF_ITEMS),
                                                    jsonObjectBooking.getString(AppConfigTags.NOTES),
                                                    jsonObjectBooking.getString(AppConfigTags.COST),
                                                    jsonObjectBooking.getJSONArray(AppConfigTags.ASSETS).toString()
                                            );
                                            bookingList.add(i, booking);
                                        }


                                        bookingAdapter.notifyDataSetChanged();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);

                                }
                            } else {
                                Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog(Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
                            }
                            Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.HEADER_DRIVER_LOGIN_KEY, userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.DRIVER_LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest, 30);
        } else {
            Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_go_to_settings), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
            });
        }
    }

    public void OfflineBookingList() {
        bookingList.clear();

        try {

            JSONObject jsonObj = new JSONObject(userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.RESPONSE));
            boolean is_error = jsonObj.getBoolean(AppConfigTags.ERROR);
            String message = jsonObj.getString(AppConfigTags.MESSAGE);
            if (!is_error) {
                swipeRefreshLayout.setRefreshing(false);
                JSONArray jsonArrayBooking = jsonObj.getJSONArray(AppConfigTags.BOOKINGS);

                for (int i = 0; i < jsonArrayBooking.length(); i++) {
                    JSONObject jsonObjectBooking = jsonArrayBooking.getJSONObject(i);
                    Booking booking = new Booking(
                            jsonObjectBooking.getInt(AppConfigTags.BOOKING_ID),
                            jsonObjectBooking.getString(AppConfigTags.BOOKING_STATUS),
                            jsonObjectBooking.getString(AppConfigTags.CLIENT_NAME),
                            jsonObjectBooking.getString(AppConfigTags.CLIENT_EMAIL),
                            jsonObjectBooking.getString(AppConfigTags.CLIENT_PHONE),
                            jsonObjectBooking.getString(AppConfigTags.PICKUP_LOCATION),
                            jsonObjectBooking.getString(AppConfigTags.PICKUP_DATE),
                            jsonObjectBooking.getString(AppConfigTags.PICKUP_TIME_START),
                            jsonObjectBooking.getString(AppConfigTags.PICKUP_TIME_END),
                            jsonObjectBooking.getString(AppConfigTags.DROP_LOCATION),
                            jsonObjectBooking.getString(AppConfigTags.DROP_DATE),
                            jsonObjectBooking.getString(AppConfigTags.DROP_TIME_START),
                            jsonObjectBooking.getString(AppConfigTags.DROP_TIME_END),
                            jsonObjectBooking.getInt(AppConfigTags.NO_OF_ITEMS),
                            jsonObjectBooking.getString(AppConfigTags.NOTES),
                            jsonObjectBooking.getString(AppConfigTags.COST),
                            jsonObjectBooking.getJSONArray(AppConfigTags.ASSETS).toString()
                    );
                    bookingList.add(i, booking);
                }
                bookingAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);

        }
    }

    private void initDrawer() {
        IProfile profile = new IProfile() {
            @Override
            public Object withName(String name) {
                return null;
            }

            @Override
            public StringHolder getName() {
                return null;
            }

            @Override
            public Object withEmail(String email) {
                return null;
            }

            @Override
            public StringHolder getEmail() {
                return null;
            }

            @Override
            public Object withIcon(Drawable icon) {
                return null;
            }

            @Override
            public Object withIcon(Bitmap bitmap) {
                return null;
            }

            @Override
            public Object withIcon(@DrawableRes int iconRes) {
                return null;
            }

            @Override
            public Object withIcon(String url) {
                return null;
            }

            @Override
            public Object withIcon(Uri uri) {
                return null;
            }

            @Override
            public Object withIcon(IIcon icon) {
                return null;
            }

            @Override
            public ImageHolder getIcon() {
                return null;
            }

            @Override
            public Object withSelectable(boolean selectable) {
                return null;
            }

            @Override
            public boolean isSelectable() {
                return false;
            }

            @Override
            public Object withIdentifier(long identifier) {
                return null;
            }

            @Override
            public long getIdentifier() {
                return 0;
            }
        };

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                if (uri != null) {
                    Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
                }
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.colorPrimary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_white_1000);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });


        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withTypeface(SetTypeFace.getTypeface(MainActivity.this))
                .withTypeface(SetTypeFace.getTypeface(this))
                .withPaddingBelowHeader(false)
                .withSelectionListEnabled(false)
                .withSelectionListEnabledForSingleProfile(false)
                .withProfileImagesVisible(false)
                .withOnlyMainProfileImageVisible(true)
                .withDividerBelowHeader(true)
                .withHeaderBackground(R.color.primary)
                .withSavedInstance(savedInstanceState)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        return false;
                    }
                })
                .build();
        headerResult.addProfiles(new ProfileDrawerItem()
                .withIcon(R.drawable.doctor)
                .withName(userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.DRIVER_NAME))
                .withEmail(userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.DRIVER_EMAIL)));


        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
//                .withToolbar (toolbar)
//                .withItemAnimator (new AlphaCrossFadeAnimator ())
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Dashboard").withIcon(FontAwesome.Icon.faw_tachometer).withIdentifier(1).withTypeface(SetTypeFace.getTypeface(MainActivity.this)).withSelectable(false),
                        new PrimaryDrawerItem().withName("Change Password").withIcon(FontAwesome.Icon.faw_search).withIdentifier(2).withSelectable(false).withTypeface(SetTypeFace.getTypeface(MainActivity.this)),
                        new PrimaryDrawerItem().withName("Sign Out").withIcon(FontAwesome.Icon.faw_search).withIdentifier(3).withSelectable(false).withTypeface(SetTypeFace.getTypeface(MainActivity.this))

                )
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case 2:
                                Intent intent2 = new Intent(MainActivity.this, ChangePasswordActivity.class);
                                startActivity(intent2);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;
                            case 3:
                                showLogOutDialog();
                                break;
                        }
                        return false;
                    }
                })
                .build();
//        result.getActionBarDrawerToggle ().setDrawerIndicatorEnabled (false);
    }

    private void showLogOutDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .limitIconToDefaultSize()
                .content("Do you wish to Sign Out?")
                .positiveText("Yes")
                .negativeText("No")

                .typeface(SetTypeFace.getTypeface(MainActivity.this), SetTypeFace.getTypeface(MainActivity.this))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        userDetailsPref.putStringPref(MainActivity.this, UserDetailsPref.DRIVER_NAME, "");
                        userDetailsPref.putStringPref(MainActivity.this, UserDetailsPref.DRIVER_ID, "");
                        userDetailsPref.putStringPref(MainActivity.this, UserDetailsPref.DRIVER_EMAIL, "");
                        userDetailsPref.putStringPref(MainActivity.this, UserDetailsPref.DRIVER_LOGIN_KEY, "");
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }).build();
        dialog.show();
    }

    private void startScan() {
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(MainActivity.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Place the barcode in center")
                //.withCenterTracker (R.drawable.barcode_scan_default, R.drawable.barcode_scan_pass)
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {

                        barcodeResult = barcode;
                        getScannedData(String.valueOf(barcode.rawValue));
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.INTERNET, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        /*android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder (MainActivity.this);
                        builder.setMessage ("Permission are required please enable them on the App Setting page")
                                .setCancelable (false)
                                .setPositiveButton ("OK", new DialogInterface.OnClickListener () {
                                    public void onClick (DialogInterface dialog, int id) {
                                        dialog.dismiss ();
                                        Intent intent = new Intent (Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts ("package", getPackageName (), null));
                                        startActivity (intent);
                                    }
                                });
                        android.support.v7.app.AlertDialog alert = builder.create ();
                        alert.show ();*/
                        // user denied flagging NEVER ASK AGAIN
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                    } else if (Manifest.permission.CAMERA.equals(permission)) {
//                        Utils.showToast (this, "Camera Permission is required");
//                        showRationale (permission, R.string.permission_denied_contacts);
                        // user denied WITHOUT never ask again
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    } else if (WRITE_EXTERNAL_STORAGE.equals(permission)) {
//                        Utils.showToast (this, "Write Permission is required");
//                        showRationale (permission, R.string.permission_denied_contacts);
                        // user denied WITHOUT never ask again
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    }
                }
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }


    private void getScannedData(final String scan_value) {
        if (NetworkConnection.isNetworkAvailable(MainActivity.this)) {
            Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.SCAN_PRODUCT, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.SCAN_PRODUCT,
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
                                        Intent intent = new Intent(MainActivity.this, ScanDetailActivity.class);
                                        intent.putExtra("response", response);
                                        startActivity(intent);

                                    } else {
                                        Utils.showSnackBar(MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
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
                            Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                            progressDialog.dismiss();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.BARCODE_VALUE, scan_value);

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
