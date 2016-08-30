/*
 * Copyright (C) 2014 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.temasek;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManagerGlobal;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;

import java.util.List;
import java.util.ArrayList;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class BatteryBar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {
    private static final String TAG = "BatteryBar";

    private static final String PREF_BATT_BAR = "battery_bar_list";
    private static final String PREF_BATT_BAR_STYLE = "battery_bar_style";
    private static final String PREF_BATT_BAR_COLOR = "battery_bar_color";
    private static final String PREF_BATT_BAR_WIDTH = "battery_bar_thickness";
    private static final String PREF_BATT_ANIMATE = "battery_bar_animate";
    private static final String PREF_BATT_BAR_CHARGING_COLOR = "battery_bar_charging_color";
    private static final String PREF_BATT_BAR_BATTERY_LOW_COLOR_WARNING = "battery_bar_battery_low_color_warning";
    private static final String STATUS_BAR_USE_GRADIENT_COLOR = "statusbar_battery_bar_use_gradient_color";
    private static final String STATUS_BAR_BAR_LOW_COLOR = "statusbar_battery_bar_low_color";
    private static final String STATUS_BAR_BAR_HIGH_COLOR = "statusbar_battery_bar_high_color";

    private static final int BATTERY_BAR_DEFAULT = 0xffffffff;
    private static final int BATTERY_BAR_LOW = 0xffff4444;
    private static final int BATTERY_BAR_HIGH = 0xff99cc00;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mBatteryBar;
    private ListPreference mBatteryBarStyle;
    private ListPreference mBatteryBarThickness;
    private SwitchPreference mBatteryBarChargingAnimation;
    private SwitchPreference mBatteryBarUseGradient;
    private ColorPickerPreference mBatteryBarColor;
    private ColorPickerPreference mBatteryBarChargingColor;
    private ColorPickerPreference mBatteryBarBatteryLowColorWarn;
    private ColorPickerPreference mBatteryBarBatteryLowColor;
    private ColorPickerPreference mBatteryBarBatteryHighColor;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

   public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.battery_bar);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mBatteryBar = (ListPreference) findPreference(PREF_BATT_BAR);
        int batteryBar = Settings.System.getIntForUser(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR, 0,
                UserHandle.USER_CURRENT);
        mBatteryBar.setValue(String.valueOf(batteryBar));
        mBatteryBar.setSummary(mBatteryBar.getEntry());
        mBatteryBar.setOnPreferenceChangeListener(this);

        mBatteryBarStyle = (ListPreference) findPreference(PREF_BATT_BAR_STYLE);
        int batteryBarStyle = Settings.System.getIntForUser(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0,
                UserHandle.USER_CURRENT);
        mBatteryBarStyle.setValue(String.valueOf(batteryBarStyle));
        mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntry());
        mBatteryBarStyle.setOnPreferenceChangeListener(this);

        mBatteryBarThickness = (ListPreference) findPreference(PREF_BATT_BAR_WIDTH);
        int batteryBarThickness = Settings.System.getIntForUser(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1,
                UserHandle.USER_CURRENT);
        mBatteryBarThickness.setValue(String.valueOf(batteryBarThickness));
        mBatteryBarThickness.setSummary(mBatteryBarThickness.getEntry());
        mBatteryBarThickness.setOnPreferenceChangeListener(this);

        mBatteryBarChargingAnimation = (SwitchPreference) findPreference(PREF_BATT_ANIMATE);
        mBatteryBarChargingAnimation.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1);

        mBatteryBarColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_COLOR, BATTERY_BAR_DEFAULT);
        mBatteryBarColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryBarColor.setSummary(hexColor);
        mBatteryBarColor.setOnPreferenceChangeListener(this);

        mBatteryBarChargingColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_CHARGING_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, BATTERY_BAR_DEFAULT);
        mBatteryBarChargingColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryBarChargingColor.setSummary(hexColor);
        mBatteryBarChargingColor.setOnPreferenceChangeListener(this);
 
        mBatteryBarBatteryLowColorWarn = (ColorPickerPreference) findPreference(PREF_BATT_BAR_BATTERY_LOW_COLOR_WARNING);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR_WARNING, BATTERY_BAR_DEFAULT);
        mBatteryBarBatteryLowColorWarn.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryBarBatteryLowColorWarn.setSummary(hexColor);
        mBatteryBarBatteryLowColorWarn.setOnPreferenceChangeListener(this);

        mBatteryBarUseGradient = (SwitchPreference) findPreference(STATUS_BAR_USE_GRADIENT_COLOR);
        mBatteryBarUseGradient.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_USE_GRADIENT_COLOR, 0) == 1);

        mBatteryBarBatteryLowColor = (ColorPickerPreference) findPreference(STATUS_BAR_BAR_LOW_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_LOW_COLOR, BATTERY_BAR_LOW);
        mBatteryBarBatteryLowColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffff4444 & intColor));
        mBatteryBarBatteryLowColor.setSummary(hexColor);
        mBatteryBarBatteryLowColor.setOnPreferenceChangeListener(this);
 
        mBatteryBarBatteryHighColor = (ColorPickerPreference) findPreference(STATUS_BAR_BAR_HIGH_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_HIGH_COLOR, BATTERY_BAR_HIGH);
        mBatteryBarBatteryHighColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xff99cc00 & intColor));
        mBatteryBarBatteryHighColor.setSummary(hexColor);
        mBatteryBarBatteryHighColor.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
          
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_reset) // use the KitKat backup icon
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;

        if (preference == mBatteryBar) {
            int batteryBar = Integer.valueOf((String) newValue);
            int index = mBatteryBar.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(mResolver,
                    Settings.System.STATUSBAR_BATTERY_BAR, batteryBar, UserHandle.USER_CURRENT);
            mBatteryBar.setSummary(mBatteryBar.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarStyle) {
            int batteryBarStyle = Integer.valueOf((String) newValue);
            int index = mBatteryBarStyle.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(mResolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_STYLE, batteryBarStyle, UserHandle.USER_CURRENT);
            mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarThickness) {
            int batteryBarThickness = Integer.valueOf((String) newValue);
            int index = mBatteryBarThickness.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(mResolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, batteryBarThickness, UserHandle.USER_CURRENT);
            mBatteryBarThickness.setSummary(mBatteryBarThickness.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBatteryBarChargingColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBatteryBarBatteryLowColorWarn) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR_WARNING, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBatteryBarBatteryLowColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_LOW_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBatteryBarBatteryHighColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_HIGH_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }     
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean value;

        if (preference == mBatteryBarChargingAnimation) {
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE,
                    ((SwitchPreference) preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mBatteryBarUseGradient) {
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_USE_GRADIENT_COLOR,
                    ((SwitchPreference) preference).isChecked() ? 1 : 0);
            return true;
        }
        return false;
    }

    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        BatteryBar getOwner() {
            return (BatteryBar) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.reset_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.dlg_reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_COLOR, BATTERY_BAR_DEFAULT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, BATTERY_BAR_DEFAULT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR_WARNING, BATTERY_BAR_DEFAULT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_USE_GRADIENT_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_LOW_COLOR, BATTERY_BAR_LOW);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_HIGH_COLOR, BATTERY_BAR_HIGH);
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.battery_bar;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
            };
}
