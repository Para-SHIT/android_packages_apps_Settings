/*
 * Copyright (C) 2014 DarkKat
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
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatusBarBatteryStatusSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener, Indexable {

    private static final String PREF_CAT_CIRCLE_OPTIONS =
            "battery_status_cat_circle_options";
    private static final String PREF_CAT_BAR_OPTIONS =
            "battery_status_cat_bar_options";
    private static final String PREF_CAT_COLORS =
            "battery_status_cat_colors";
    private static final String PREF_STYLE =
            "battery_status_style";
    private static final String PREF_BATT_BAR =
            "battery_bar_list";
    private static final String PREF_PERCENT_STYLE =
            "battery_status_percent_style";
    private static final String PREF_CHARGE_ANIMATION_SPEED =
            "battery_status_charge_animation_speed";
    private static final String PREF_CIRCLE_DOT_LENGTH =
            "battery_status_circle_dot_length";
    private static final String PREF_CIRCLE_DOT_INTERVAL =
            "battery_status_circle_dot_interval";
    private static final String PREF_BATT_BAR_STYLE =
            "battery_bar_style";
    private static final String PREF_BATT_BAR_WIDTH =
            "battery_bar_thickness";
    private static final String PREF_BATT_ANIMATE =
            "battery_bar_animate";
    private static final String PREF_BATTERY_COLOR =
            "battery_status_battery_color";
    private static final String PREF_TEXT_COLOR =
            "battery_status_text_color";
    private static final String PREF_BATT_BAR_COLOR =
            "battery_bar_color";
    private static final String PREF_BATT_BAR_BATTERY_LOW_COLOR_WARNING = 
            "battery_bar_battery_low_color_warning";
    private static final String PREF_BATT_BAR_CHARGING_COLOR =
            "battery_bar_charging_color";
    private static final String STATUS_BAR_USE_GRADIENT_COLOR = 
            "statusbar_battery_bar_use_gradient_color";
    private static final String STATUS_BAR_BAR_LOW_COLOR =
            "statusbar_battery_bar_low_color";
    private static final String STATUS_BAR_BAR_HIGH_COLOR =
            "statusbar_battery_bar_high_color";

    private static final int BATTERY_BAR_CHARGING = 0xffffff00;
    private static final int BATTERY_BAR_DEFAULT = 0xffffffff;
    private static final int BATTERY_BAR_HIGH = 0xff99cc00;
    private static final int BATTERY_BAR_LOW = 0xffff4444;
    private static final int BATTERY_BAR_LOW_WARNING = 0xffff0000;

    private static final int DEFAULT_BATTERY_COLOR = 0xffffffff;
    private static final int DEFAULT_TEXT_COLOR = 0xff000000;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private static final int BATTERY_STATUS_PORTRAIT = 0;
    private static final int BATTERY_STATUS_LANDSCAPE = 5;
    private static final int BATTERY_STATUS_CIRCLE = 2;
    private static final int BATTERY_STATUS_CIRCLE_DOTTED = 3;
    private static final int BATTERY_STATUS_TEXT = 6;
    private static final int BATTERY_STATUS_HIDDEN = 4;
    private static final int BATTERY_STATUS_BAR_HIDDEN = 0;

    private ListPreference mStyle;
    private ListPreference mBatteryBar;
    private ListPreference mPercentStyle;
    private ListPreference mChargeAnimationSpeed;
    private ListPreference mCircleDotLength;
    private ListPreference mCircleDotInterval;
    private ListPreference mBatteryBarStyle;
    private ListPreference mBatteryBarThickness;
    private SwitchPreference mBatteryBarChargingAnimation;
    private ColorPickerPreference mBatteryColor;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mBatteryBarColor;
    private ColorPickerPreference mBatteryBarBatteryLowColorWarn;
    private ColorPickerPreference mBatteryBarChargingColor;
    private SwitchPreference mBatteryBarUseGradient;
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

        addPreferencesFromResource(R.xml.status_bar_battery_status_settings);
        mResolver = getActivity().getContentResolver();

        int intColor = 0xffffffff;
        String hexColor = String.format("#%08x", (0xffffffff & 0xffffffff));

        mStyle = (ListPreference) findPreference(PREF_STYLE);
        int style = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_BATTERY_STYLE, 0);
        mStyle.setValue(String.valueOf(style));
        mStyle.setSummary(mStyle.getEntry());
        mStyle.setOnPreferenceChangeListener(this);

        mBatteryBar = (ListPreference) findPreference(PREF_BATT_BAR);
        int batteryBar = Settings.System.getIntForUser(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR, 0,
                UserHandle.USER_CURRENT);
        mBatteryBar.setValue(String.valueOf(batteryBar));
        mBatteryBar.setSummary(mBatteryBar.getEntry());
        mBatteryBar.setOnPreferenceChangeListener(this);

        boolean batteryStatusVisible = style != BATTERY_STATUS_HIDDEN;
        boolean batteryStatusBarVisible = batteryBar != BATTERY_STATUS_BAR_HIDDEN;
        boolean isCircle = style == BATTERY_STATUS_CIRCLE;
        boolean isCircleDotted = style == BATTERY_STATUS_CIRCLE_DOTTED;
        boolean isTextOnly = style == BATTERY_STATUS_TEXT;

        PreferenceCategory catCircleOptions =
                (PreferenceCategory) findPreference(PREF_CAT_CIRCLE_OPTIONS);
        PreferenceCategory catBarOptions =
                (PreferenceCategory) findPreference(PREF_CAT_BAR_OPTIONS);
        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);
        mCircleDotLength =
                (ListPreference) findPreference(PREF_CIRCLE_DOT_LENGTH);
        mCircleDotInterval =
                (ListPreference) findPreference(PREF_CIRCLE_DOT_INTERVAL);
        mBatteryBarStyle =
                (ListPreference) findPreference(PREF_BATT_BAR_STYLE);
        mBatteryBarThickness =
                (ListPreference) findPreference(PREF_BATT_BAR_WIDTH);
        mBatteryBarChargingAnimation =
                (SwitchPreference) findPreference(PREF_BATT_ANIMATE);
        mBatteryColor =
                (ColorPickerPreference) findPreference(PREF_BATTERY_COLOR);
        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        mBatteryBarColor =
                (ColorPickerPreference) findPreference(PREF_BATT_BAR_COLOR);
        mBatteryBarBatteryLowColorWarn =
                (ColorPickerPreference) findPreference(PREF_BATT_BAR_BATTERY_LOW_COLOR_WARNING);
        mBatteryBarChargingColor =
                (ColorPickerPreference) findPreference(PREF_BATT_BAR_CHARGING_COLOR);
        mBatteryBarUseGradient =
                (SwitchPreference) findPreference(STATUS_BAR_USE_GRADIENT_COLOR);
        mBatteryBarBatteryLowColor =
                (ColorPickerPreference) findPreference(STATUS_BAR_BAR_LOW_COLOR);
        mBatteryBarBatteryHighColor =
                (ColorPickerPreference) findPreference(STATUS_BAR_BAR_HIGH_COLOR);

        if ((batteryStatusVisible && !isTextOnly) || batteryStatusBarVisible) {
            mPercentStyle =
                    (ListPreference) findPreference(PREF_PERCENT_STYLE);
            int percentStyle = Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 2);
            mPercentStyle.setValue(String.valueOf(percentStyle));
            mPercentStyle.setSummary(mPercentStyle.getEntry());
            mPercentStyle.setOnPreferenceChangeListener(this);

            mChargeAnimationSpeed =
                    (ListPreference) findPreference(PREF_CHARGE_ANIMATION_SPEED);
            int chargeAnimationSpeed = Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_BATTERY_STATUS_CHARGING_ANIMATION_SPEED, 3);
            mChargeAnimationSpeed.setValue(String.valueOf(chargeAnimationSpeed));
            mChargeAnimationSpeed.setSummary(mChargeAnimationSpeed.getEntry());
            mChargeAnimationSpeed.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_PERCENT_STYLE);
            removePreference(PREF_CHARGE_ANIMATION_SPEED);
        }

        if (batteryStatusVisible && isCircleDotted) {
            int circleDotLength = Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_LENGTH, 3);
            mCircleDotLength.setValue(String.valueOf(circleDotLength));
            mCircleDotLength.setSummary(mCircleDotLength.getEntry());
            mCircleDotLength.setOnPreferenceChangeListener(this);

            int circleDotInterval = Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_INTERVAL, 2);
            mCircleDotInterval.setValue(String.valueOf(circleDotInterval));
            mCircleDotInterval.setSummary(mCircleDotInterval.getEntry());
            mCircleDotInterval.setOnPreferenceChangeListener(this);
        } else {
            catCircleOptions.removePreference(mCircleDotLength);
            catCircleOptions.removePreference(mCircleDotInterval);
            removePreference(PREF_CAT_CIRCLE_OPTIONS);
        }

        if (batteryStatusBarVisible) {
        	int batteryBarStyle = Settings.System.getIntForUser(mResolver,
                	Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0,
                	UserHandle.USER_CURRENT);
        	mBatteryBarStyle.setValue(String.valueOf(batteryBarStyle));
        	mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntry());
        	mBatteryBarStyle.setOnPreferenceChangeListener(this);

        	int batteryBarThickness = Settings.System.getIntForUser(mResolver,
                	Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1,
                	UserHandle.USER_CURRENT);
        	mBatteryBarThickness.setValue(String.valueOf(batteryBarThickness));
        	mBatteryBarThickness.setSummary(mBatteryBarThickness.getEntry());
        	mBatteryBarThickness.setOnPreferenceChangeListener(this);

        	mBatteryBarChargingAnimation.setChecked(Settings.System.getInt(mResolver,
                	Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1);
        } else {
            catBarOptions.removePreference(mBatteryBarStyle);
            catBarOptions.removePreference(mBatteryBarThickness);
            catBarOptions.removePreference(mBatteryBarChargingAnimation);
            removePreference(PREF_CAT_BAR_OPTIONS);
        }

        if (batteryStatusVisible && !isTextOnly) {
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                    DEFAULT_BATTERY_COLOR);
            mBatteryColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mBatteryColor.setSummary(hexColor);
            mBatteryColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(mBatteryColor);
        }

        if (batteryStatusVisible) {
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_TEXT_COLOR,
                    DEFAULT_BATTERY_COLOR);
            mTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTextColor.setSummary(hexColor);
            mTextColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(mTextColor);
        }

        if (batteryStatusBarVisible) {
        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.STATUSBAR_BATTERY_BAR_COLOR, BATTERY_BAR_DEFAULT);
        	mBatteryBarColor.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xffffffff & intColor));
        	mBatteryBarColor.setSummary(hexColor);
        	mBatteryBarColor.setOnPreferenceChangeListener(this);

        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR_WARNING, BATTERY_BAR_LOW_WARNING);
        	mBatteryBarBatteryLowColorWarn.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xffff0000 & intColor));
        	mBatteryBarBatteryLowColorWarn.setSummary(hexColor);
        	mBatteryBarBatteryLowColorWarn.setOnPreferenceChangeListener(this);

        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, BATTERY_BAR_CHARGING);
        	mBatteryBarChargingColor.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xffffff00 & intColor));
        	mBatteryBarChargingColor.setSummary(hexColor);
        	mBatteryBarChargingColor.setOnPreferenceChangeListener(this);

        	mBatteryBarUseGradient.setChecked(Settings.System.getInt(mResolver,
                	Settings.System.STATUSBAR_BATTERY_BAR_USE_GRADIENT_COLOR, 0) == 1);

        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.STATUSBAR_BATTERY_BAR_LOW_COLOR, BATTERY_BAR_LOW);
        	mBatteryBarBatteryLowColor.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xffff4444 & intColor));
        	mBatteryBarBatteryLowColor.setSummary(hexColor);
        	mBatteryBarBatteryLowColor.setOnPreferenceChangeListener(this);
 
        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.STATUSBAR_BATTERY_BAR_HIGH_COLOR, BATTERY_BAR_HIGH);
        	mBatteryBarBatteryHighColor.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xff99cc00 & intColor));
        	mBatteryBarBatteryHighColor.setSummary(hexColor);
        	mBatteryBarBatteryHighColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(mBatteryBarColor);
            catColors.removePreference(mBatteryBarBatteryLowColorWarn);
            catColors.removePreference(mBatteryBarChargingColor);
            catColors.removePreference(mBatteryBarUseGradient);
            catColors.removePreference(mBatteryBarBatteryLowColor);
            catColors.removePreference(mBatteryBarBatteryHighColor);
        }

        if (!batteryStatusVisible && !batteryStatusBarVisible) {
            removePreference(PREF_CAT_COLORS);
        }

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
        int intValue, index, intHex;
        String hex;

        if (preference == mStyle) {
            intValue = Integer.valueOf((String) newValue);
            index = mStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_STYLE, intValue);
            mStyle.setSummary(mStyle.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mBatteryBar) {
            intValue = Integer.valueOf((String) newValue);
            index = mBatteryBar.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(mResolver,
                    Settings.System.STATUSBAR_BATTERY_BAR, intValue, UserHandle.USER_CURRENT);
            mBatteryBar.setSummary(mBatteryBar.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mPercentStyle) {
            intValue = Integer.valueOf((String) newValue);
            index = mPercentStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, intValue);
            mPercentStyle.setSummary(mPercentStyle.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mChargeAnimationSpeed) {
            intValue = Integer.valueOf((String) newValue);
            index = mChargeAnimationSpeed.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_STATUS_CHARGING_ANIMATION_SPEED, intValue);
            mChargeAnimationSpeed.setSummary(mChargeAnimationSpeed.getEntries()[index]);
            return true;
        } else if (preference == mCircleDotLength) {
            intValue = Integer.valueOf((String) newValue);
            index = mCircleDotLength.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_LENGTH, intValue);
            mCircleDotLength.setSummary(mCircleDotLength.getEntries()[index]);
            return true;
        } else if (preference == mCircleDotInterval) {
            intValue = Integer.valueOf((String) newValue);
            index = mCircleDotInterval.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_INTERVAL, intValue);
            mCircleDotInterval.setSummary(mCircleDotInterval.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarStyle) {
            intValue = Integer.valueOf((String) newValue);
            index = mBatteryBarStyle.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(mResolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_STYLE, intValue, UserHandle.USER_CURRENT);
            mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarThickness) {
            intValue = Integer.valueOf((String) newValue);
            index = mBatteryBarThickness.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(mResolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, intValue, UserHandle.USER_CURRENT);
            mBatteryBarThickness.setSummary(mBatteryBarThickness.getEntries()[index]);
            return true;
        } else if (preference == mBatteryColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBatteryBarColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_COLOR, intHex);
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
        } else if (preference == mBatteryBarChargingColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, intHex);
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
        boolean value;

        if (preference == mBatteryBarChargingAnimation) {
            Settings.System.putInt(mResolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE,
                    ((SwitchPreference) preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mBatteryBarUseGradient) {
            Settings.System.putInt(mResolver,
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

        StatusBarBatteryStatusSettings getOwner() {
            return (StatusBarBatteryStatusSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.reset_values_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_CHARGING_ANIMATION_SPEED, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_LENGTH, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_INTERVAL, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                                    DEFAULT_BATTERY_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_TEXT_COLOR,
                                    DEFAULT_BATTERY_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_COLOR,
                                    BATTERY_BAR_DEFAULT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR_WARNING,
                                    BATTERY_BAR_LOW_WARNING);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR,
                                    BATTERY_BAR_DEFAULT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_USE_GRADIENT_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_LOW_COLOR,
                                    BATTERY_BAR_LOW);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_HIGH_COLOR,
                                    BATTERY_BAR_HIGH);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_temasek,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STYLE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_CHARGING_ANIMATION_SPEED, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_LENGTH, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_INTERVAL, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                                    0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_TEXT_COLOR,
                                    DEFAULT_BATTERY_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_COLOR,
                                    BATTERY_BAR_DEFAULT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR_WARNING,
                                    BATTERY_BAR_LOW_WARNING);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR,
                                    BATTERY_BAR_CHARGING);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_USE_GRADIENT_COLOR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_LOW_COLOR,
                                    BATTERY_BAR_LOW);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUSBAR_BATTERY_BAR_HIGH_COLOR,
                                    BATTERY_BAR_HIGH);
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
            sir.xmlResId = R.xml.status_bar_battery_status_settings;
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
