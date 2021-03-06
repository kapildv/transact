package com.algo.transact.home.smart_home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.algo.transact.AppConfig.AppConfig;
import com.algo.transact.R;
import com.algo.transact.home.smart_home.beans.Peripheral;
import com.algo.transact.home.smart_home.beans.Room;
import com.algo.transact.home.smart_home.beans.SmartHomeConfig;
import com.algo.transact.home.smart_home.beans.SmartHomeStore;
import com.algo.transact.home.smart_home.module_configuration.ScanWifiActivity;
import com.algo.transact.home.smart_home.settings.SettingsActivity;
import com.algo.transact.login.User;
import com.algo.transact.server_communicator.request_handler.SmartHomeRequestHandler;

import static com.algo.transact.home.smart_home.beans.Room.ROOM_ID_NOT_REQUIRED;
import static com.algo.transact.home.smart_home.beans.SmartHomeConfig.VIEW.EXPAND_VIEW;
import static com.algo.transact.home.smart_home.beans.SmartHomeConfig.VIEW.LIST_VIEW;
import static com.algo.transact.server_communicator.request_handler.SmartHomeRequestHandler.RECENT_LISTENER.SMART_HOME_ACTIVITY;

public class SmartHomeActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    LinearLayout llViewHolder;
    public static SmartHomeActivity context;
    private Switch swMainSwitch;
    private ProgressDialog pDialog;

    private static String newRoomString = "Add New Room";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_home);

        context = this;

        SmartHomeRequestHandler.registerUser(this);
        LinearLayout llWaterLevel = (LinearLayout) findViewById(R.id.smart_home_ll_water_level);
        llWaterLevel.setOnClickListener(this);

        LinearLayout llAlexa = (LinearLayout) findViewById(R.id.smart_home_ll_alexa);
        llAlexa.setOnClickListener(this);

        ImageView ivSettings = (ImageView) findViewById(R.id.smart_home_iv_settings);
        ivSettings.setOnClickListener(this);

        swMainSwitch = (Switch) findViewById(R.id.smart_home_sw_main_switch);
        swMainSwitch.setOnClickListener(this);
        swMainSwitch.setChecked(true);
        llViewHolder = (LinearLayout) findViewById(R.id.smart_home_ll_view_holder);

        SmartHomeRequestHandler.getHouse(User.getUserPreferences(this), SMART_HOME_ACTIVITY);

        if (SmartHomeConfig.getUserPreferences(this) == null) {
            SmartHomeConfig smartHomeConfig = new SmartHomeConfig();
            smartHomeConfig.setDefaultView(EXPAND_VIEW);
            smartHomeConfig.setUserPreferences(this);
        }
    }

    public void updateAllRooms() {
        if (SmartHomeRequestHandler.getInstance().roomFragment == null) {
            displayRoomsViewWithThread();
        } else {
            try {
                for (int i = 0; i < SmartHomeRequestHandler.getInstance().roomFragment.size(); i++) {
                    SmartHomeRequestHandler.getInstance().roomFragment.get(i).UpdateRoomView();
                }
            } catch (NullPointerException e) {

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // updateAllRooms();
        if (SmartHomeStore.getSHStore(this) != null) {
            //displayRoomsView();
            displayRoomsViewWithThread();
        }
    }

    public void displayRoomsViewWithThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                displayRoomsView();
            }
        };
        thread.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //super.onSaveInstanceState(outState, outPersistentState);
    }

    public void displayRoomsView() {
        if (SmartHomeConfig.getUserPreferences(this).getDefaultView() == EXPAND_VIEW) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    llViewHolder.removeAllViews();
                    new ExpansionManager(context, SmartHomeStore.getSHStore(context));
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    llViewHolder.removeAllViews();
                    SingleRoomViewFragment singleRoomViewFragment = new SingleRoomViewFragment();
                    android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    //transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                    transaction.replace(R.id.smart_home_ll_view_holder, singleRoomViewFragment, LinearLayout.class.getName());
                    transaction.commit();
                }
            });
        }
    }

    public void showSettingsMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_smart_home_settings, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    private void showProgressDialog() {
        if (pDialog == null)
            pDialog = new ProgressDialog(this);
        if (!pDialog.isShowing()) {
            pDialog.show();
            pDialog.setMessage("Retrieving Water level data");
        }
    }

    public void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.smart_home_ll_water_level:
                showProgressDialog();
                SmartHomeRequestHandler.getWaterLevelPeripherals(SmartHomeStore.getSHStore(this).getHouse(), SmartHomeRequestHandler.RECENT_LISTENER.WATER_INDICATOR_DIALOGUE);

                //SmartHomeRequestHandler.getWaterLevelPeripherals(SmartHomeStore.getSHStore(this).getHouse(), SmartHomeRequestHandler.RECENT_LISTENER.WATER_INDICATOR_DIALOGUE);
                break;

            case R.id.smart_home_iv_settings:
                showSettingsMenu(v);
                break;
            case R.id.smart_home_ll_alexa:

                break;

            case R.id.smart_home_sw_main_switch:
                if (swMainSwitch.isChecked()) {
                    Log.d(AppConfig.TAG, "Clicked on main switch, switch ON all the peripherals");
                } else {
                    Log.d(AppConfig.TAG, "Clicked on main switch, switch OFF all the peripherals");
                }
                //per_id, room_id, per_type, per_name, per_status, per_value,per_is_in_quick_access
                Peripheral peripheral = new Peripheral(Peripheral.HOUSE_SWITCH_ID, ROOM_ID_NOT_REQUIRED,
                        Peripheral.PERIPHERAL_TYPE.MAIN_SWITCH, "MAIN_SWITCH",
                        swMainSwitch.isChecked() ? Peripheral.Status.ON : Peripheral.Status.OFF,
                        0, true);
                SmartHomeRequestHandler.updatePeripheralStatus(new Room(ROOM_ID_NOT_REQUIRED, SmartHomeStore.getSHStore(this).getHouse().getHouse_id(), ""), peripheral, SMART_HOME_ACTIVITY);
                break;

        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch_view:
                llViewHolder.removeAllViews();
                SmartHomeConfig smartHomeConfig = new SmartHomeConfig();
                if (SmartHomeConfig.getUserPreferences(this).getDefaultView() == EXPAND_VIEW) {
                    //llViewHolder.removeAllViews();
                    smartHomeConfig = new SmartHomeConfig();
                    smartHomeConfig.setDefaultView(LIST_VIEW);
                    smartHomeConfig.setUserPreferences(this);

                    SingleRoomViewFragment singleRoomViewFragment = new SingleRoomViewFragment();
                    android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    //transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                    transaction.replace(R.id.smart_home_ll_view_holder, singleRoomViewFragment, LinearLayout.class.getName());
                    transaction.commit();

                } else {
                    smartHomeConfig.setDefaultView(EXPAND_VIEW);
                    smartHomeConfig.setUserPreferences(this);
                    new ExpansionManager(this, SmartHomeStore.getSHStore(this));

                }
                return true;

            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_set_config_device:
                this.startActivity(new Intent(this, ScanWifiActivity.class));
                break;
            default:
        }
        return false;
    }

}
