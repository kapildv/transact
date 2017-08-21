package com.algo.transact.home.shopatshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.algo.transact.AppConfig.AppState;
import com.algo.transact.AppConfig.IntentPutExtras;
import com.algo.transact.AppConfig.IntentRequestResponseType;
import com.algo.transact.AppConfig.IntentResultCode;
import com.algo.transact.R;
import com.algo.transact.barcode.CodeScannerActivity;
import com.algo.transact.home.LocateCategories;
import com.algo.transact.home.shopatshop.data_beans.Item;
import com.algo.transact.home.shopatshop.mycart.MyCartFragment;
import com.algo.transact.login.LoginActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.algo.transact.AppConfig.IntentResultCode.RESULT_CANCELLED_SHOP_SELECTION;

public class OutletFront extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final String TAG = "CognitionMall";
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private static final String LOG_TAG = OutletFront.class.getSimpleName();
    public MyCartFragment myCartFragment;
    private int shopID;
    //public OffersFragment offersFragment;
    private boolean isBack = false;
    // private Button showCartButton;
    private int back_press_counter = 0;
    private GoogleApiClient mGoogleApiClient;
    private String requestType;
    private DrawerLayout mDrawerLayout;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private NestedScrollView nscScroll;
    private FloatingActionButton outlet_fab_code_scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_front);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.shop_at_shop_drawerPane);

        outlet_fab_code_scanner = (FloatingActionButton) findViewById(R.id.outlet_front_fab_code_scanner);
        outlet_fab_code_scanner.setOnClickListener(this);

      //  nscScroll = (NestedScrollView) findViewById(R.id.shop_at_shop_nsv_scroll);
      //  nscScroll.smoothScrollTo(0,0);
        myCartFragment = new MyCartFragment();
        //  offersFragment = new OffersFragment();
        Log.i(AppState.TAG, " Activity onCreate ShopAtShop");

        requestType = getIntent().getStringExtra(IntentPutExtras.REQUEST_TYPE);
        shopID = getIntent().getIntExtra(IntentPutExtras.ID, 0);

/*        Log.i(AppState.TAG, "Class: " + this.getClass().getSimpleName() + " Method: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " Calling sequence is wrong "+shopID);*/

        Log.i(AppState.TAG, "Class: " + this.getClass().getSimpleName() + " Method: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + "Selected ShopID "+shopID);


        /*if (requestType.equals(IntentPutExtras.REQUEST_SELECT_SHOP)) {
            shopID = getIntent().getIntExtra(IntentPutExtras.ID, 0);
        } else {
            Log.i(AppState.TAG, "Class: " + this.getClass().getSimpleName() + " Method: " + new Object() {
            }.getClass().getEnclosingMethod().getName() + " Calling sequence is wrong");
            this.finish();
        }*/
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

/*
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
*/

        //---------------------------

   //     FragmentManager fragmentManager = getFragmentManager();
   //     FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
   //     fragmentTransaction.add(R.id.shop_at_shop_page_frame, myCartFragment);
   //     fragmentTransaction.commit();

        viewPager = (ViewPager) findViewById(R.id.shop_at_shop_viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.shop_at_shop_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CatalogueFragment(), "Catalogue");
        adapter.addFragment(myCartFragment, "Cart");
       // nscScroll.smoothScrollTo(0,0);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        // Intent intent = new Intent(getApplicationContext(), ShopScannerActivity.class);
        // intent.putExtra(IntentPutExtras.REQUEST_TYPE,IntentPutExtras.REQUEST_SELECT_SHOP);
        //  startActivityForResult(intent, IntentRequestResponseType.REQUEST_SELECT_SHOP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(AppState.TAG, "onActivityResult ShopAtShop");
        if (resultCode == RESULT_CANCELLED_SHOP_SELECTION) {
            Log.e(AppState.TAG, "Error in OnActivityResult of ShopAtShop RequestCode: " + requestCode + " Shop Selection cancelled");
            this.finish();
            return;
        } /*else if (resultCode != RESULT_OK) {
            Log.e(AppState.TAG, "Error in OnActivityResult of ShopAtShop RequestCode: " + requestCode);
            //this.finish();
            return;
        }*/

        //Toast.makeText(this, "ReqCode" + requestCode + " DATA " + data.getStringExtra(IntentPutExtras.SCANNER_RESPONSE), Toast.LENGTH_LONG).show();

        int request_type = -1;

        if (data != null)
            request_type = data.getIntExtra(IntentPutExtras.REQUEST_TYPE, 0);

        switch (request_type) {
            /* case IntentPutExtras.REQUEST_SELECT_SHOP: {
                shopID = data.getIntExtra(IntentPutExtras.ID,0);
                Log.i(AppState.TAG, "onActivityResult ShopAtShop SelectedShopID " + shopID);
                break;
            }*/

            case IntentPutExtras.RESPONSE_NEW_ITEM_SELECTED: {
                if (data != null) {
                    Item newItem = (Item)data.getSerializableExtra(IntentPutExtras.NEW_ITEM_DATA);
                    Log.i(AppState.TAG, "Class: " + this.getClass().getSimpleName() + " Method: " + new Object() {
                    }.getClass().getEnclosingMethod().getName()+"REQUEST_SELECT_ITEM_FROM_SHOP adding new item");
                    if(newItem!=null)
                    {
                        myCartFragment.addItemToCart(newItem);
                    }
                    else
                        Log.e(AppState.TAG, "Class: " + this.getClass().getSimpleName() + " Method: " + new Object() {
                        }.getClass().getEnclosingMethod().getName()+"Item is null... oo");
                }
                break;
            }
            default:
//                Log.e(AppState.TAG, String.format(getString(R.string.barcode_error_format), CommonStatusCodes.getStatusCodeString(resultCode)));
                Log.e(AppState.TAG, "Error in ShopAtShop onActivityResult");
                break;
        }
    }

    public void openDrawer(View v)
    {
        Log.i(AppState.TAG, "Class: " + this.getClass().getSimpleName() + " Method: " + new Object() {
        }.getClass().getEnclosingMethod().getName());

        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    /*
    public void showMyCart(View view) {

        if (AppState.checkProccedStatus() == false) {
            Toast.makeText(this, "Please select mall first !!!", Toast.LENGTH_SHORT).show();
            return;
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        if (isBack == false) {
            showCartButton.setText("Back");
            fragmentTransaction.replace(R.id.shop_at_shop_page_frame, myCartFragment);
            isBack = true;
        } else {
            showCartButton.setText("My Cart");
            fragmentTransaction.replace(R.id.shop_at_shop_page_frame, offersFragment);
            isBack = false;
        }

        fragmentTransaction.commit();
    }
*/
    public void locateCategories(View view) {
        Log.i("Home", "locateCategories Clicked");
        Intent myIntent = new Intent(this, LocateCategories.class);
        this.startActivity(myIntent);

    }

    public void selectMall(View view) {
        Log.i("Home", "Select mall Clicked");
        Intent intent = new Intent(getApplicationContext(), ShopScannerActivity.class);
        startActivityForResult(intent, IntentRequestResponseType.REQUEST_SELECT_SHOP);

//        Intent myIntent = new Intent(this, MallSelectionActivity.class);
        //      this.startActivity(myIntent);

    }

    public void gotoHomefromSAS(View v) {
        Log.i("Home", "Select mall Clicked");
        // Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        // startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        this.finish();
    }

    public void browseOffers(View v) {
        Log.i("Home", "Select mall Clicked");
        Intent intent = new Intent(getApplicationContext(), SASOffersActivity.class);
        startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
    }

    public void checkoutCart(View v) {
        Log.i("Home", "checkoutCart Clicked");
        Intent intent = new Intent(getApplicationContext(), SASCheckoutActivity.class);

        intent.putExtra(IntentPutExtras.REQUEST_TYPE, IntentPutExtras.REQUEST_SELECT_SHOP);
        intent.putExtra(IntentPutExtras.ID, shopID);
        this.startActivityForResult(intent, IntentResultCode.RESULT_OK_SHOP_SELECTION);

       // startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public void logout(View view) {
        Log.i("Home", "Logout clicked");

        File sessionFile = new File(AppState.sessionFile);
        if (sessionFile.exists()) {
            signOutFromGmail();
            LoginManager.getInstance().logOut();
            Log.i("Home", "Logout, file exists, deleting");
            sessionFile.delete();
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
            this.finish();
        } else {
            Log.i("Home", "Logout, file does not exists, its a error case");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.outlet_front_fab_code_scanner:
            {
                Intent intent = new Intent(getApplicationContext(), CodeScannerActivity.class);
                intent.putExtra(IntentPutExtras.REQUEST_TYPE, IntentPutExtras.REQUEST_SELECT_ITEM_FROM_SHOP);
                intent.putExtra(IntentPutExtras.ID, shopID);
                startActivityForResult(intent, IntentRequestResponseType.REQUEST_SELECT_ITEM_FROM_SHOP);
                break;
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(android.support.v4.app.FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(AppState.TAG, "onBackPressed of ShopAtShop");

    }

    // [START signOut]
    private void signOutFromGmail() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        //  updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
// [END signOut]
}