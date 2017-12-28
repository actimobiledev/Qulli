package com.actiknow.qulli.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.actiknow.qulli.R;
import com.actiknow.qulli.utils.SetTypeFace;
import com.actiknow.qulli.utils.UserDetailsPref;
import com.bumptech.glide.Glide;
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

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_LOGIN_SCREEN_RESULT = 2;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rvBooking;
    UserDetailsPref userDetailsPref;

    private AccountHeader headerResult = null;
    private Drawer result = null;
    Bundle savedInstanceState;
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

    private void isLogin() {
        userDetailsPref = UserDetailsPref.getInstance();
        if (userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.DRIVER_LOGIN_KEY).length() == 0) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private void initView() {

    }

    private void initData() {
        userDetailsPref = UserDetailsPref.getInstance ();

    }

    private void initAdapter() {

    }

    private void initListener() {

    }




    private void initDrawer () {
        IProfile profile = new IProfile () {
            @Override
            public Object withName (String name) {
                return null;
            }

            @Override
            public StringHolder getName () {
                return null;
            }

            @Override
            public Object withEmail (String email) {
                return null;
            }

            @Override
            public StringHolder getEmail () {
                return null;
            }

            @Override
            public Object withIcon (Drawable icon) {
                return null;
            }

            @Override
            public Object withIcon (Bitmap bitmap) {
                return null;
            }

            @Override
            public Object withIcon (@DrawableRes int iconRes) {
                return null;
            }

            @Override
            public Object withIcon (String url) {
                return null;
            }

            @Override
            public Object withIcon (Uri uri) {
                return null;
            }

            @Override
            public Object withIcon (IIcon icon) {
                return null;
            }

            @Override
            public ImageHolder getIcon () {
                return null;
            }

            @Override
            public Object withSelectable (boolean selectable) {
                return null;
            }

            @Override
            public boolean isSelectable () {
                return false;
            }

            @Override
            public Object withIdentifier (long identifier) {
                return null;
            }

            @Override
            public long getIdentifier () {
                return 0;
            }
        };

        DrawerImageLoader.init (new AbstractDrawerImageLoader() {
            @Override
            public void set (ImageView imageView, Uri uri, Drawable placeholder) {
                if (uri != null) {
                    Glide.with (imageView.getContext ()).load (uri).placeholder (placeholder).into (imageView);
                }
            }

            @Override
            public void cancel (ImageView imageView) {
                Glide.clear (imageView);
            }

            @Override
            public Drawable placeholder (Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name ().equals (tag)) {
                    return DrawerUIUtils.getPlaceHolder (ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name ().equals (tag)) {
                    return new IconicsDrawable(ctx).iconText (" ").backgroundColorRes (com.mikepenz.materialdrawer.R.color.colorPrimary).sizeDp (56);
                } else if ("customUrlItem".equals (tag)) {
                    return new IconicsDrawable (ctx).iconText (" ").backgroundColorRes (R.color.md_white_1000);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder (ctx, tag);
            }
        });


        headerResult = new AccountHeaderBuilder()
                .withActivity (this)
                .withCompactStyle (false)
                .withTypeface (SetTypeFace.getTypeface (MainActivity.this))
                .withTypeface (SetTypeFace.getTypeface (this))
                .withPaddingBelowHeader (false)
                .withSelectionListEnabled (false)
                .withSelectionListEnabledForSingleProfile (false)
                .withProfileImagesVisible (false)
                .withOnlyMainProfileImageVisible (true)
                .withDividerBelowHeader (true)
                .withHeaderBackground (R.color.primary)
                .withSavedInstance (savedInstanceState)
                .withOnAccountHeaderListener (new AccountHeader.OnAccountHeaderListener () {
                    @Override
                    public boolean onProfileChanged (View view, IProfile profile, boolean currentProfile) {
                        Intent intent = new Intent (MainActivity.this, MainActivity.class);
                        startActivity (intent);
                        return false;
                    }
                })
                .build ();
        headerResult.addProfiles (new ProfileDrawerItem()
                .withIcon (R.drawable.doctor)
                .withName (userDetailsPref.getStringPref (MainActivity.this, UserDetailsPref.DRIVER_NAME))
                .withEmail (userDetailsPref.getStringPref (MainActivity.this, UserDetailsPref.DRIVER_EMAIL)));


        result = new DrawerBuilder()
                .withActivity (this)
                .withAccountHeader (headerResult)
//                .withToolbar (toolbar)
//                .withItemAnimator (new AlphaCrossFadeAnimator ())
                .addDrawerItems (
                        new PrimaryDrawerItem().withName ("Dashboard").withIcon (FontAwesome.Icon.faw_tachometer).withIdentifier (1).withTypeface (SetTypeFace.getTypeface (MainActivity.this)).withSelectable (false),
                        new PrimaryDrawerItem ().withName ("Change Password").withIcon (FontAwesome.Icon.faw_search).withIdentifier (2).withSelectable (false).withTypeface (SetTypeFace.getTypeface (MainActivity.this))

                )
                .withSavedInstance (savedInstanceState)
                .withOnDrawerItemClickListener (new Drawer.OnDrawerItemClickListener () {
                    @Override
                    public boolean onItemClick (View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier ()) {
                            case 2:
                                Intent intent2 = new Intent (MainActivity.this, ChangePasswordActivity.class);
                                startActivity (intent2);
                                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 3:
                             //   showLogOutDialog ();
                                break;
                        }
                        return false;
                    }
                })
                .build ();
//        result.getActionBarDrawerToggle ().setDrawerIndicatorEnabled (false);
    }



}
