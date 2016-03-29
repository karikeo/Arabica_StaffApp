package com.shevchenko.staffapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;
import com.shevchenko.staffapp.Common.Common;
import com.shevchenko.staffapp.Model.CompleteTask;
import com.shevchenko.staffapp.Model.GpsInfo;
import com.shevchenko.staffapp.Model.LocationLoader;
import com.shevchenko.staffapp.Model.LogEvent;
import com.shevchenko.staffapp.Model.PendingTasks;
import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.db.DBManager;
import com.shevchenko.staffapp.net.NetworkManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.GravityCompat;
import java.lang.reflect.Array;
import java.util.ArrayList;
import android.widget.ListView;
import com.shevchenko.staffapp.Model.MenuListAdapter;
import com.shevchenko.staffapp.Model.MenuItemButton;
import android.support.v4.app.FragmentActivity;
import android.app.ActionBar;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.shevchenko.staffapp.Model.TinTask;
import android.content.SharedPreferences;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.ConnectionResult;
public class MainActivity extends FragmentActivity implements ActionBar.TabListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private ProgressDialog mProgDlg;
    private ProgressDialog mProgDlgLoading;
    private DBManager dbManager;
    private ComponentName mService;
    private ComponentName mUploadService;
    private DrawerLayout drawerLayout;
    private ListView lvDrawer;
    private ArrayList<MenuItemButton> drawerItems = new ArrayList<>();
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    Boolean uploadFlag = false;
    private MenuItem item_upload, item_loading;
    private Menu mMenu;
    SharedPreferences sp;
    public LocationLoader mLocationLoader;
    private Location mNewLocation;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    GoogleApiClient mGoogleClient;
    android.content.SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapsInitializer.initialize(getApplicationContext());
        mLocationLoader = new LocationLoader(this, false);
        mLocationLoader
                .SetOnLoadEventListener(new LocationLoader.OnLoadEventListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mNewLocation = location;
                        getLocation();
                        // mUpdateLocationHandler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onAddressChanged(String strAddress) {

                    }

                    @Override
                    public void onError(int iErrorCode) {
                        getLocation();
                    }
                });
        mLocationLoader.Start();

        int service = getIntent().getIntExtra("service", 0);
        if(service == 1) {
            Common.getInstance().arrIncompleteTasks.clear();
            Common.getInstance().arrCompleteTasks.clear();
            Common.getInstance().arrCategory.clear();
            Common.getInstance().arrProducto.clear();
            Common.getInstance().arrPendingTasks.clear();
            Common.getInstance().arrTinTasks.clear();
            Common.getInstance().arrCompleteTinTasks.clear();
            Common.getInstance().arrTaskTypes.clear();
            for (int i = 0; i < Common.getInstance().arrIncompleteTasks_copy.size(); i++)
                Common.getInstance().arrIncompleteTasks.add(Common.getInstance().arrIncompleteTasks_copy.get(i));

            for (int i = 0; i < Common.getInstance().arrCompleteTasks_copy.size(); i++)
                Common.getInstance().arrCompleteTasks.add(Common.getInstance().arrCompleteTasks_copy.get(i));

            for (int i = 0; i < Common.getInstance().arrCategory_copy.size(); i++)
                Common.getInstance().arrCategory.add(Common.getInstance().arrCategory_copy.get(i));

            for (int i = 0; i < Common.getInstance().arrProducto_copy.size(); i++)
                Common.getInstance().arrProducto.add(Common.getInstance().arrProducto_copy.get(i));

            for (int i = 0; i < Common.getInstance().arrPendingTasks_copy.size(); i++)
                Common.getInstance().arrPendingTasks.add(Common.getInstance().arrPendingTasks_copy.get(i));

            for(int i = 0; i < Common.getInstance().arrCompleteTinTasks_copy.size(); i++)
                Common.getInstance().arrCompleteTinTasks.add(Common.getInstance().arrCompleteTinTasks_copy.get(i));

            for(int i = 0; i < Common.getInstance().arrTinTasks_copy.size(); i++)
                Common.getInstance().arrTinTasks.add(Common.getInstance().arrTinTasks_copy.get(i));
        }
        dbManager = new DBManager(this);

        Common.getInstance().arrCompleteTasks.clear();
        Common.getInstance().arrCompleteTasks.addAll(dbManager.getCompleteTask(Common.getInstance().getUserID()));

        Common.getInstance().arrIncompleteTasks.clear();
        Common.getInstance().arrIncompleteTasks = dbManager.getInCompleteTask(Common.getInstance().getUserID());

        Common.getInstance().arrPendingTasks.clear();
        Common.getInstance().arrPendingTasks = dbManager.getPendingTask(Common.getInstance().getUserID());

        Common.getInstance().arrTinTasks.clear();
        Common.getInstance().arrTinTasks = dbManager.getTinPendingTask(Common.getInstance().getUserID());

        Common.getInstance().arrCompleteTinTasks.clear();
        Common.getInstance().arrCompleteTinTasks = dbManager.getCompleteTinTask(Common.getInstance().getUserID());

        Common.getInstance().arrCategory.clear();
        Common.getInstance().arrCategory = dbManager.getAllCategory();

        Common.getInstance().arrProducto.clear();
        Common.getInstance().arrProducto = dbManager.getAllProducto();

        Common.getInstance().arrProducto_Ruta.clear();
        Common.getInstance().arrProducto_Ruta = dbManager.getAllProducto_Ruta();

        Common.getInstance().arrTaskTypes.clear();
        Common.getInstance().arrTaskTypes = dbManager.getAllTypes();

        //new Thread(mCheckNetWorkRunnable).start();

        sp = getSharedPreferences("userinfo", 1);
        ed = sp.edit();
        final ActionBar actionBar = getActionBar();
        int position = getIntent().getIntExtra("position", 0);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
        actionBar.setSelectedNavigationItem(position);
        MenuItemButton item = new MenuItemButton("Menu", 0);
        drawerItems.add(item);
        item = new MenuItemButton("Sincronizar", R.drawable.planet);
        drawerItems.add(item);
        item = new MenuItemButton("Mapas", R.drawable.media);
        drawerItems.add(item);
        item = new MenuItemButton("Salir", 0);
        drawerItems.add(item);
        /*
        if((Common.getInstance().arrCompleteTasks.size() == 0) && (Common.getInstance().arrIncompleteTasks.size() == 0) && (Common.getInstance().arrCategory.size() == 0) && (Common.getInstance().arrProducto.size() == 0))
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        */


        lvDrawer = (ListView) findViewById(R.id.lvDrawer);
        lvDrawer.setAdapter(new MenuListAdapter(this, drawerItems));
        lvDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (drawerLayout.isDrawerOpen(GravityCompat.END))
                    drawerLayout.closeDrawer(GravityCompat.END);
                if (position == 3) {
                    ed.putBoolean("login", false);
                    ed.commit();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    MainActivity.this.finish();
                } else if (position == 2) {
                    setService("The user clicks the Google map button");
                    if (getConnectivityStatus())
                        startActivity(new Intent(MainActivity.this, MapActivity.class));
                    else
                        Toast.makeText(MainActivity.this, "The network is not available now!!!", Toast.LENGTH_SHORT).show();
                } else if (position == 1) {
                    setService("The user clicks the Sincronize button");
                    if (getConnectivityStatus()) {
                        mProgDlgLoading.show();
                        new Thread(mRunnable_pendingtasks).start();
                    } else
                        Toast.makeText(MainActivity.this, "The network is not available now!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mGoogleClient = new GoogleApiClient.Builder(this, this, this).addApi(
                LocationServices.API).build();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mProgDlg = new ProgressDialog(this);
        mProgDlg.setCancelable(false);
        mProgDlg.setTitle("Sincronize");
        mProgDlg.setMessage("Please Wait!");
        mProgDlgLoading = new ProgressDialog(this);
        mProgDlgLoading.setCancelable(false);
        mProgDlgLoading.setTitle("Sincronize");
        mProgDlgLoading.setMessage("Loading Now!");
        drawerLayout.setEnabled(false);

        Timer timer = new Timer();
        timer.schedule(mytask, 3000, 3000);
        if(Common.getInstance().latitude.equals("")){
            settingsrequest();
        }

    }
    private void getLocation() {
        if (mNewLocation == null) {
            settingsrequest();
            return;
        }

        Common.getInstance().latitude = String.valueOf(mNewLocation.getLatitude());
        Common.getInstance().longitude = String.valueOf(mNewLocation.getLongitude());
    }
    public void settingsrequest()
    {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
    TimerTask mytask = new TimerTask() {
        public void run() {
            if(getConnectivityStatus()){
                mCheckHandler.sendEmptyMessage(0);
            }
        }
    };

    private Handler mCheckHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            mProgDlg.hide();
            if(msg.what == 0) {
                //Toast.makeText(MainActivity.this, "Your phone regains interent!", Toast.LENGTH_LONG).show();
                ArrayList<PendingTasks> tasks = dbManager.getPendingTask(Common.getInstance().getUserID());
                if(tasks.size() != 0){
                    if(Common.getInstance().isUpload == false)
                        new UploadThread().start();
                }
                //postAllPendingTask();
            } else {

            }
        }

    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //item_upload =  menu.add(0, 0, Menu.NONE, "").setIcon(R.drawable.upload);
        //item_upload.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        //item_loading =  menu.add(0, Menu.FIRST + 1, Menu.NONE, "").setIcon(R.drawable.loading_icon);
        //item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.imgMenu){
            drawerLayout.setEnabled(true);
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END);
            else
                drawerLayout.openDrawer(GravityCompat.END);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
                              FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Context mContext;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            switch(position) {
                case 0:
                    //return new PendingTask(MainActivity.this);
                    return PendingTask.getInstance(MainActivity.this);
                case 1:
                    //return new CompletedTask(MainActivity.this);
                    return CompletedTask.getInstance(MainActivity.this, mLocationLoader);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            String strPending = "Tareas(" + String.valueOf(Common.getInstance().arrIncompleteTasks.size()) + ")";
            //String strComplete = "Realizadas(" + String.valueOf(Common.getInstance().arrCompleteTasks.size() + Common.getInstance().arrPendingTasks.size()) + ")";
            String strComplete = "Realizadas(" + String.valueOf(Common.getInstance().arrCompleteTasks.size()) + ")";
            switch (position) {
                case 0:
                    return strPending.toUpperCase(l);
                case 1:
                    return strComplete.toUpperCase(l);
            }
            return null;
        }
    }
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_dummy,
                    container, false);
            TextView dummyTextView = (TextView) rootView
                    .findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(
                    ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.closeDrawer(GravityCompat.END);
        else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            LoadingActivity loading = (LoadingActivity) LoadingActivity.loadingActivity;
            if(loading != null) loading.finish();
            LoginActivity login = (LoginActivity) LoginActivity.loginActivity;
            login.finish();
            MainActivity.this.finish();
            stopService(new Intent(MainActivity.this, LogService.class));
            moveTaskToBack(true);
            System.exit(0);
        }
    }
    private void DialogSelectOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Upload the pending tasks.")
                .setMessage("Will you upload the pending tasks?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        uploadFlag = true;
                        mMenu.add(0, 1, Menu.NONE, "").setIcon(R.drawable.upload).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                        //item_upload.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        //item_upload.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                        //item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        new UploadThread().start();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void DialogSelectOption3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Sincronize")
                .setMessage("You can`t sincronize the app because the app sincronize now.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void DialogSelectOption2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Refresh the screen.")
                .setMessage("Loading the tasks was completed. Please refresh the app screen.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        uploadFlag = false;
                        item_upload.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.putExtra("position", getActionBar().getSelectedTab().getPosition());
                        startActivity(intent);
                        MainActivity.this.finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mLocationLoader.Start();
                        break;
                    case Activity.RESULT_CANCELED:
                        settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }
    private Runnable mRunnable_pendingtasks = new Runnable() {
        @Override
        public void run() {
            postAllPendingTask();
            postAllTinPendingTask();
            postAllLogEvents();
            mHandler_pendingtasks.sendEmptyMessage(1);

        }
    };
    private Handler mHandler_pendingtasks = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            mProgDlg.hide();
            if (msg.what == 1) {
                Toast.makeText(MainActivity.this, "Pending Tasks was uploaded!!!", Toast.LENGTH_SHORT).show();
                loadTasks();
            } else if (msg.what == 0) {
                Toast.makeText(MainActivity.this, "Pending Tasks was failed to upload!!!", Toast.LENGTH_SHORT).show();
                loadTasks();
            }
        }
    };
    private void loadTasks() {
        //mProgDlgLoading.show();
        new Thread(mRunnable_tasks).start();
    }
    private Runnable mRunnable_tasks = new Runnable() {

        @Override
        public void run() {

            Common.getInstance().arrIncompleteTasks.clear();
            Common.getInstance().arrCompleteTasks.clear();
            Common.getInstance().arrCompleteTinTasks.clear();
            Common.getInstance().arrCategory.clear();
            Common.getInstance().arrProducto.clear();
            Common.getInstance().arrProducto_Ruta.clear();
            Common.getInstance().arrUsers.clear();
            Common.getInstance().arrTaskTypes.clear();

            int nRet = NetworkManager.getManager().loadTasks(Common.getInstance().arrIncompleteTasks, Common.getInstance().arrCompleteTasks, Common.getInstance().arrCompleteTinTasks);
            NetworkManager.getManager().loadCategory(Common.getInstance().arrCategory, Common.getInstance().arrProducto, Common.getInstance().arrProducto_Ruta, Common.getInstance().arrUsers, Common.getInstance().arrTaskTypes);

            dbManager.deleteAllIncompleteTask(Common.getInstance().getUserID());
            dbManager.deleteAllCompleteTask(Common.getInstance().getUserID());
            dbManager.deleteAllCompleteTinTask(Common.getInstance().getUserID());
            dbManager.deleteAllProducto();
            dbManager.deleteAllProducto_Ruta();
            dbManager.deleteAllCategory();
            dbManager.deleteAllUser();
            dbManager.deleteAllTypes();

            for (int i = 0; i < Common.getInstance().arrIncompleteTasks.size(); i++) {
                dbManager.insertInCompleteTask(Common.getInstance().arrIncompleteTasks.get(i));
            }
            for (int i = 0; i < Common.getInstance().arrCompleteTasks.size(); i++) {
                dbManager.insertCompleteTask(Common.getInstance().arrCompleteTasks.get(i));
            }
            for (int i = 0; i < Common.getInstance().arrCompleteTinTasks.size(); i++) {
                dbManager.insertCompleteTinTask(Common.getInstance().arrCompleteTinTasks.get(i));
            }
            for (int i = 0; i < Common.getInstance().arrCategory.size(); i++) {
                dbManager.insertCategory(Common.getInstance().arrCategory.get(i));
            }
            for (int i = 0; i < Common.getInstance().arrProducto.size(); i++) {
                dbManager.insertProducto(Common.getInstance().arrProducto.get(i));
            }
            for (int i = 0; i < Common.getInstance().arrProducto_Ruta.size(); i++) {
                dbManager.insertProducto_Ruta(Common.getInstance().arrProducto_Ruta.get(i));
            }
            for (int i = 0; i < Common.getInstance().arrUsers.size(); i++) {
                dbManager.insertUser(Common.getInstance().arrUsers.get(i));
            }
            for(int i = 0; i < Common.getInstance().arrTaskTypes.size(); i++){
                dbManager.insertType(Common.getInstance().arrTaskTypes.get(i));
            }
            //NetworkManager.getManager().loadProducto(Common.getInstance().arrProducto);
            Common.getInstance().arrPendingTasks = dbManager.getPendingTask(Common.getInstance().getUserID());
            Common.getInstance().arrTinTasks = dbManager.getTinPendingTask(Common.getInstance().getUserID());
            mHandler_task.sendEmptyMessage(nRet);
        }
    };
    private Handler mHandler_task = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            mProgDlgLoading.hide();
            if (msg.what == 0) {
                Toast.makeText(MainActivity.this, "Load Success!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("position", 0);
                startActivity(intent);
            } else if (msg.what == 1) {
                Toast.makeText(MainActivity.this, "Load failed!", Toast.LENGTH_SHORT).show();
            } else if (msg.what == -1) {
                Toast.makeText(MainActivity.this, "Load failed due to network problem! Please check your network status", Toast.LENGTH_SHORT).show();
            }
            //setTaskNumber();
        }
    };
    private int postAllPendingTask() {
        ArrayList<PendingTasks> tasks = dbManager.getPendingTask(Common.getInstance().getUserID());
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {
            String[] arrPhotos = new String[]{null, null, null, null, null};
            int nCurIndex = 0;
            if (tasks.get(i).file1 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file1;
                nCurIndex++;
            }
            if (tasks.get(i).file2 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file2;
                nCurIndex++;
            }
            if (tasks.get(i).file3 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file3;
                nCurIndex++;
            }
            if (tasks.get(i).file4 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file4;
                nCurIndex++;
            }
            if (tasks.get(i).file5 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file5;
                nCurIndex++;
            }
            Boolean bRet1 = NetworkManager.getManager().postTask(tasks.get(i).taskid, tasks.get(i).date, tasks.get(i).tasktype, tasks.get(i).RutaAbastecimiento, tasks.get(i).TaskBusinessKey, tasks.get(i).Customer, tasks.get(i).Adress, tasks.get(i).LocationDesc, tasks.get(i).Model, tasks.get(i).latitude, tasks.get(i).longitude, tasks.get(i).epv, tasks.get(i).logLatitude, tasks.get(i).logLongitude, tasks.get(i).ActionDate, tasks.get(i).MachineType, tasks.get(i).Signature, tasks.get(i).NumeroGuia, tasks.get(i).Aux_valor1, tasks.get(i).Glosa, arrPhotos, nCurIndex);
            if (!bRet1)
                return 0;
            dbManager.deletePendingTask(Common.getInstance().getUserID(), tasks.get(i).taskid);
        }
        return 1;
    }

    private int postAllLogEvents() {
        ArrayList<LogEvent> logs = dbManager.getLogEvents(Common.getInstance().getUserID());
        int sum = 0;
        for (int i = 0; i < logs.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postLogEvent(logs.get(i));
            if (bRet1)
                dbManager.deleteLogEvent(Common.getInstance().getUserID(), logs.get(i).datetime);
            else
                return 0;
        }
        return 1;
    }

    private int postAllTinPendingTask() {
        ArrayList<TinTask> tasks = dbManager.getTinPendingTask(Common.getInstance().getUserID());
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postTinTask(tasks.get(i));
            if (bRet1)
                dbManager.deletePendingTinTask(Common.getInstance().getUserID(), tasks.get(i).taskid);
            else
                return 0;
        }
        return 1;
    }
    private void uploadPendingTask() {
        DialogSelectOption();
        //mProgDlg.show();
    }
    class UploadThread extends Thread {

        @Override
        public void run() {
            Intent service = new Intent(MainActivity.this, UploadService.class);
            service.putExtra("userid", Common.getInstance().getUserID());
            Common.getInstance().isUpload = true;
            mUploadService = startService(service);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

      private  boolean getConnectivityStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }

    public  void setService(String description){
        GpsInfo info = new GpsInfo(MainActivity.this);
        Intent service = new Intent(MainActivity.this, LogService.class);
        service.putExtra("userid", Common.getInstance().getUserID());
        service.putExtra("taskid", "");
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        service.putExtra("datetime", time);
        service.putExtra("description", description);
        service.putExtra("latitude", Common.getInstance().latitude);
        service.putExtra("longitude", Common.getInstance().longitude);
        mService = startService(service);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mService != null){
            Intent i = new Intent();
            i.setComponent(mService);
            stopService(i);
        }
        if(mUploadService != null){
            Intent i = new Intent();
            i.setComponent(mUploadService);
            stopService(i);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        mGoogleClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleClient.disconnect();

        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {

    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 0);
            } catch (IntentSender.SendIntentException e) {
            }
        } else {
        }
    }
}
