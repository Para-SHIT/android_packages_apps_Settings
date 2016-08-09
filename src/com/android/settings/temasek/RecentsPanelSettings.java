/*
 * Copyright (C) 2014 crDroid Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.temasek.SeekBarPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.ArrayList;
import java.util.List;

public class RecentsPanelSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "RecentPanelSettings";

    private static final String SHOW_CLEAR_ALL_RECENTS = "show_clear_all_recents";
    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String PREF_CLEAR_ALL_BG_COLOR = "android_recents_clear_all_bg_color";
    private static final String PREF_CLEAR_ALL_ICON_COLOR = "android_recents_clear_all_icon_color";
    private static final String IMMERSIVE_RECENTS = "immersive_recents";	
    private static final String MEM_TEXT_COLOR = "mem_text_color";
    private static final String MEMORY_BAR_COLOR = "memory_bar_color";
    private static final String MEMORY_BAR_FREE_COLOR = "memory_bar_free_color";
    private static final String RECENTS_DATE_COLOR = "recents_date_color";
    private static final String RECENTS_CLOCK_COLOR = "recents_clock_color";
    private static final String RECENTS_FONT_STYLE = "recents_font_style";
    private static final String RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE = "recents_full_screen_clock_date_size";
    private static final String PREF_HIDDEN_RECENTS_APPS_START = "hide_app_from_recents";
    
    // Package name of the hidden recetns apps activity
    public static final String HIDDEN_RECENTS_PACKAGE_NAME = "com.android.settings";
    // Intent for launching the hidden recents actvity
    public static Intent INTENT_HIDDEN_RECENTS_SETTINGS = new Intent(Intent.ACTION_MAIN)
    .setClassName(HIDDEN_RECENTS_PACKAGE_NAME,
    HIDDEN_RECENTS_PACKAGE_NAME + ".temasek.recentshidden.HAFRAppListActivity");

    private static final int MENU_RESET = Menu.FIRST;
    private static final int BLACK = 0xff1b231d;
    private static final int GREEN = 0xff82d989;
    private static final int RED = 0xffDC4C3C;
    private static final int WHITE = 0xffffffff;

    private SwitchPreference mRecentsClearAll;
    private ListPreference mRecentsClearAllLocation;
    private ListPreference mImmersiveRecents;
    private ColorPickerPreference mClearAllIconColor;
    private ColorPickerPreference mClearAllBgColor;
    private ColorPickerPreference mMemTextColor;
    private ColorPickerPreference mMemBarColor;
    private ColorPickerPreference mMemBarFreeColor;
    private ColorPickerPreference mClockColor;
    private ColorPickerPreference mDateColor;
    private ListPreference mRecentsFontStyle;
    private SeekBarPreference mRecentsFontSize;
    private Preference mHiddenRecentsApps;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.recents_panel_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
        int intvalue;
        int intColor;
        String hexColor;

        mImmersiveRecents = (ListPreference) findPreference(IMMERSIVE_RECENTS);
        mImmersiveRecents.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.IMMERSIVE_RECENTS, 0)));
        mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
        mImmersiveRecents.setOnPreferenceChangeListener(this);

        mRecentsClearAll = (SwitchPreference) prefSet.findPreference(SHOW_CLEAR_ALL_RECENTS);
        mRecentsClearAll.setChecked(Settings.System.getIntForUser(resolver,
            Settings.System.SHOW_CLEAR_ALL_RECENTS, 1, UserHandle.USER_CURRENT) == 1);
        mRecentsClearAll.setOnPreferenceChangeListener(this);

        mRecentsClearAllLocation = (ListPreference) prefSet.findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 0, UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);
        updateRecentsLocation(location);

        mClearAllBgColor = (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_BG_COLOR);
        mClearAllBgColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
            Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, RED);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mClearAllBgColor.setSummary(hexColor);
        mClearAllBgColor.setNewPreviewColor(intColor);

        mClearAllIconColor = (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_ICON_COLOR);
        mClearAllIconColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
            Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, WHITE);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mClearAllIconColor.setSummary(hexColor);
        mClearAllIconColor.setNewPreviewColor(intColor);

        mHiddenRecentsApps = (Preference) prefSet.findPreference(PREF_HIDDEN_RECENTS_APPS_START);

        mMemTextColor = (ColorPickerPreference) prefSet.findPreference(MEM_TEXT_COLOR);
        mMemTextColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
            Settings.System.MEM_TEXT_COLOR, WHITE);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mMemTextColor.setSummary(hexColor);
        mMemTextColor.setNewPreviewColor(intColor);

        mMemBarColor =
                (ColorPickerPreference) findPreference(MEMORY_BAR_COLOR);
        intColor = Settings.System.getInt(resolver,
                Settings.System.MEMORY_BAR_COLOR, BLACK); 
        mMemBarColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xff1b231d & intColor));
        mMemBarColor.setSummary(hexColor);
        mMemBarColor.setOnPreferenceChangeListener(this);

        mMemBarFreeColor =
                (ColorPickerPreference) findPreference(MEMORY_BAR_FREE_COLOR);
        intColor = Settings.System.getInt(resolver,
                Settings.System.MEMORY_BAR_FREE_COLOR, GREEN); 
        mMemBarFreeColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xff82d989 & intColor));
        mMemBarFreeColor.setSummary(hexColor);
        mMemBarFreeColor.setOnPreferenceChangeListener(this);

        mClockColor= (ColorPickerPreference) prefSet.findPreference(RECENTS_CLOCK_COLOR);
        mClockColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
            Settings.System.RECENTS_CLOCK_COLOR, WHITE);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mClockColor.setSummary(hexColor);
        mClockColor.setNewPreviewColor(intColor);

        mDateColor= (ColorPickerPreference) prefSet.findPreference(RECENTS_DATE_COLOR);
        mDateColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
            Settings.System.RECENTS_DATE_COLOR, WHITE);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mDateColor.setSummary(hexColor);
        mDateColor.setNewPreviewColor(intColor);

        mRecentsFontStyle = (ListPreference) findPreference(RECENTS_FONT_STYLE);
        mRecentsFontStyle.setOnPreferenceChangeListener(this);
        mRecentsFontStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.RECENTS_FONT_STYLE, 0)));
        mRecentsFontStyle.setSummary(mRecentsFontStyle.getEntry());

        mRecentsFontSize =
                (SeekBarPreference) findPreference(RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE);
        mRecentsFontSize.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE, 14));
        mRecentsFontSize.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;
        int intvalue;
        String hex;
        int intHex;
        if (preference == mRecentsClearAll) {
            value = (Boolean) newValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.SHOW_CLEAR_ALL_RECENTS, value ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        }
        if (preference == mImmersiveRecents) {
            Settings.System.putInt(getContentResolver(), Settings.System.IMMERSIVE_RECENTS,
                    Integer.valueOf((String) newValue));
            mImmersiveRecents.setValue(String.valueOf(newValue));
            mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
        } else if (preference == mRecentsClearAllLocation) {
            int location = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location, UserHandle.USER_CURRENT);
            updateRecentsLocation(location);
            return true;
        } else if (preference == mClearAllBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mClearAllIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }  else if (preference == mMemTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.MEM_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mMemBarColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.MEMORY_BAR_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mMemBarFreeColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.MEMORY_BAR_FREE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mClockColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_CLOCK_COLOR, intHex);
            return true;
        } else if (preference == mDateColor) {
            hex = ColorPickerPreference.convertToARGB(
                   Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                   Settings.System.RECENTS_DATE_COLOR, intHex);
            return true;
        } else if (preference == mRecentsFontStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mRecentsFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_FONT_STYLE, val);
            mRecentsFontStyle.setSummary(mRecentsFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mRecentsFontSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE, width);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mHiddenRecentsApps) {
            getActivity().startActivity(INTENT_HIDDEN_RECENTS_SETTINGS);
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_reset)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefault();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void resetToDefault() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.recents_colors_reset_title);
        alertDialog.setMessage(R.string.recents_colors_reset_message);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                resetValues();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, null);
        alertDialog.create().show();
    }

    private void resetValues() {
        Settings.System.putInt(getContentResolver(),
                Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, RED);
        mClearAllBgColor.setNewPreviewColor(RED);
        mClearAllBgColor.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, WHITE);
        mClearAllIconColor.setNewPreviewColor(WHITE);
        mClearAllIconColor.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.MEM_TEXT_COLOR, WHITE);
        mMemTextColor.setNewPreviewColor(WHITE);
        mMemTextColor.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.MEMORY_BAR_COLOR, BLACK);
        mMemBarColor.setNewPreviewColor(BLACK);
        mMemBarColor.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.MEMORY_BAR_FREE_COLOR, GREEN);
        mMemBarFreeColor.setNewPreviewColor(GREEN);
        mMemBarFreeColor.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.RECENTS_CLOCK_COLOR, WHITE);
        mClockColor.setNewPreviewColor(WHITE);
        mClockColor.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.RECENTS_DATE_COLOR, WHITE);
        mDateColor.setNewPreviewColor(WHITE);
        mDateColor.setSummary(R.string.default_string);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void updateRecentsLocation(int value) {
        ContentResolver resolver = getContentResolver();
        Resources res = getResources();
        int summary = -1;

        Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, value);

        if (value == 0) {
            Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 0);
            summary = R.string.recents_clear_all_location_top_right;
        } else if (value == 1) {
            Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 1);
            summary = R.string.recents_clear_all_location_top_left;
        } else if (value == 2) {
            Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 2);
            summary = R.string.recents_clear_all_location_top_center;
        } else if (value == 3) {
            Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3);
            summary = R.string.recents_clear_all_location_bottom_right;
        } else if (value == 4) {
            Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 4);
            summary = R.string.recents_clear_all_location_bottom_left;
        } else if (value == 5) {
            Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 5);
            summary = R.string.recents_clear_all_location_bottom_center;
        }
        if (mRecentsClearAllLocation != null && summary != -1) {
            mRecentsClearAllLocation.setSummary(res.getString(summary));
        }
    }
}
