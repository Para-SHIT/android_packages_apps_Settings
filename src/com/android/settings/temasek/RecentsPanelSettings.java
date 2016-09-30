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

    private static final String RECENTS_PANEL_CATEGORY = "recent_panel";
    private static final String MEMBAR_CATEGORY = "memory_bar";
    private static final String SHOW_CLEAR_ALL_RECENTS = "show_clear_all_recents";
    private static final String RECENTS_CLEAR_ALL_DISMISS = "recents_clear_all_dismiss_all";
    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String PREF_CLEAR_ALL_BG_COLOR = "android_recents_clear_all_bg_color";
    private static final String PREF_CLEAR_ALL_ICON_COLOR = "android_recents_clear_all_icon_color";
    private static final String SHOW_RECENTS_MEMORY_BAR = "systemui_recents_mem_display";
    private static final String MEMORY_BAR_COLOR = "memory_bar_color";
    private static final String MEMORY_BAR_FREE_COLOR = "memory_bar_free_color";
    private static final String MEM_TEXT_COLOR = "mem_text_color";
    private static final String RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE = "recents_full_screen_clock_date_size";
    private static final String RECENTS_FONT_STYLE = "recents_font_style";
    private static final String PREF_HIDDEN_RECENTS_APPS_START = "hide_app_from_recents";
    
    // Package name of the hidden recetns apps activity
    public static final String HIDDEN_RECENTS_PACKAGE_NAME = "com.android.settings";
    // Intent for launching the hidden recents actvity
    public static Intent INTENT_HIDDEN_RECENTS_SETTINGS = new Intent(Intent.ACTION_MAIN)
    .setClassName(HIDDEN_RECENTS_PACKAGE_NAME,
    HIDDEN_RECENTS_PACKAGE_NAME + ".temasek.recentshidden.HAFRAppListActivity");

    private static final int BLACK = 0xff1b231d;
    private static final int GREEN = 0xff82d989;
    private static final int RED = 0xffdc4c3c;
    private static final int WHITE = 0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mRecentsClearAll;
    private SwitchPreference mRecentsClearAllDismiss;
    private ListPreference mRecentsClearAllLocation;
    private ColorPickerPreference mClearAllIconColor;
    private ColorPickerPreference mClearAllBgColor;
    private SwitchPreference mShowMemBar;
    private ColorPickerPreference mMemBarColor;
    private ColorPickerPreference mMemBarFreeColor;
    private ColorPickerPreference mMemTextColor;
    private SeekBarPreference mRecentsFontSize;
    private ListPreference mRecentsFontStyle;
    private Preference mHiddenRecentsApps;

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

        addPreferencesFromResource(R.xml.recents_panel_settings);

        mResolver = getActivity().getContentResolver();
        int intvalue;
        int intColor;
        String hexColor;

        boolean recentsClearAll = Settings.System.getInt(mResolver,
                Settings.System.SHOW_CLEAR_ALL_RECENTS, 0) == 1;
        boolean showMemBar = Settings.System.getInt(mResolver,
                Settings.System.SYSTEMUI_RECENTS_MEM_DISPLAY, 0) == 1;

        mRecentsClearAll = (SwitchPreference) findPreference(SHOW_CLEAR_ALL_RECENTS);
        mRecentsClearAll.setChecked(recentsClearAll);
        mRecentsClearAll.setOnPreferenceChangeListener(this);

        mShowMemBar = (SwitchPreference) findPreference(SHOW_RECENTS_MEMORY_BAR);
        mShowMemBar.setChecked(showMemBar);
        mShowMemBar.setOnPreferenceChangeListener(this);

        PreferenceCategory catRecents =
                (PreferenceCategory) findPreference(RECENTS_PANEL_CATEGORY);
        mRecentsClearAllDismiss =
                (SwitchPreference) findPreference(RECENTS_CLEAR_ALL_DISMISS);
        mRecentsClearAllLocation =
                (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        mClearAllBgColor =
                (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_BG_COLOR);
        mClearAllIconColor =
                (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_ICON_COLOR);
        PreferenceCategory catMembar =
                (PreferenceCategory) findPreference(MEMBAR_CATEGORY);
        mMemBarColor =
                (ColorPickerPreference) findPreference(MEMORY_BAR_COLOR);
        mMemBarFreeColor =
                (ColorPickerPreference) findPreference(MEMORY_BAR_FREE_COLOR);
        mMemTextColor =
                (ColorPickerPreference) findPreference(MEM_TEXT_COLOR);

        if (recentsClearAll) {
            mRecentsClearAllDismiss.setChecked(Settings.System.getInt(mResolver,
                Settings.System.RECENTS_CLEAR_ALL_DISMISS_ALL, 0) == 1);
            mRecentsClearAllDismiss.setOnPreferenceChangeListener(this);

            int location = Settings.System.getIntForUser(mResolver,
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, 4, UserHandle.USER_CURRENT);
            mRecentsClearAllLocation.setValue(String.valueOf(location));
            mRecentsClearAllLocation.setOnPreferenceChangeListener(this);
            updateRecentsLocation(location);

            intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, RED);
            hexColor = String.format("#%08x", (0xffdc4c3c & intColor));
            mClearAllBgColor.setSummary(hexColor);
            mClearAllBgColor.setNewPreviewColor(intColor);
            mClearAllBgColor.setOnPreferenceChangeListener(this);

            intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, WHITE);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mClearAllIconColor.setSummary(hexColor);
            mClearAllIconColor.setNewPreviewColor(intColor);
            mClearAllIconColor.setOnPreferenceChangeListener(this);
        } else {
            catRecents.removePreference(mRecentsClearAllDismiss);
            catRecents.removePreference(mRecentsClearAllLocation);
            catRecents.removePreference(mClearAllBgColor);
            catRecents.removePreference(mClearAllIconColor);
            removePreference(RECENTS_PANEL_CATEGORY);
        }

        if (showMemBar) {
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.MEMORY_BAR_COLOR, BLACK); 
            mMemBarColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xff1b231d & intColor));
            mMemBarColor.setSummary(hexColor);
            mMemBarColor.setOnPreferenceChangeListener(this);

            intColor = Settings.System.getInt(mResolver,
                    Settings.System.MEMORY_BAR_FREE_COLOR, GREEN); 
            mMemBarFreeColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xff82d989 & intColor));
            mMemBarFreeColor.setSummary(hexColor);
            mMemBarFreeColor.setOnPreferenceChangeListener(this);

            intColor = Settings.System.getInt(mResolver,
                Settings.System.MEM_TEXT_COLOR, WHITE);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mMemTextColor.setSummary(hexColor);
            mMemTextColor.setNewPreviewColor(intColor);
            mMemTextColor.setOnPreferenceChangeListener(this);
        } else {
            catMembar.removePreference(mMemBarColor);
            catMembar.removePreference(mMemBarFreeColor);
            catMembar.removePreference(mMemTextColor);
            removePreference(MEMBAR_CATEGORY);
        }

        mRecentsFontSize = (SeekBarPreference) findPreference(RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE);
        mRecentsFontSize.setValue(Settings.System.getInt(mResolver,
            Settings.System.RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE, 14));
        mRecentsFontSize.setOnPreferenceChangeListener(this);

        mRecentsFontStyle = (ListPreference) findPreference(RECENTS_FONT_STYLE);
        mRecentsFontStyle.setValue(Integer.toString(Settings.System.getInt(mResolver,
            Settings.System.RECENTS_FONT_STYLE, 0)));
        mRecentsFontStyle.setSummary(mRecentsFontStyle.getEntry());
        mRecentsFontStyle.setOnPreferenceChangeListener(this);

        mHiddenRecentsApps = (Preference) findPreference(PREF_HIDDEN_RECENTS_APPS_START);

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
        boolean value;
        int intvalue;
        String hex;
        int intHex;
        if (preference == mRecentsClearAll) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SHOW_CLEAR_ALL_RECENTS, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowMemBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SYSTEMUI_RECENTS_MEM_DISPLAY, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mRecentsClearAllDismiss) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_CLEAR_ALL_DISMISS_ALL, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mRecentsClearAllLocation) {
            int location = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(mResolver,
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location, UserHandle.USER_CURRENT);
            updateRecentsLocation(location);
            return true;
        } else if (preference == mClearAllBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mClearAllIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mMemBarColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.MEMORY_BAR_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mMemBarFreeColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.MEMORY_BAR_FREE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }  else if (preference == mMemTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.MEM_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mRecentsFontSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE, width);
            return true;
        } else if (preference == mRecentsFontStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mRecentsFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_FONT_STYLE, val);
            mRecentsFontStyle.setSummary(mRecentsFontStyle.getEntries()[index]);
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

        RecentsPanelSettings getOwner() {
            return (RecentsPanelSettings) getTargetFragment();
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
                                    Settings.System.SHOW_CLEAR_ALL_RECENTS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SYSTEMUI_RECENTS_MEM_DISPLAY, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_CLEAR_ALL_DISMISS_ALL, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, 4);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, RED);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEMORY_BAR_COLOR, BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEMORY_BAR_FREE_COLOR, GREEN);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEM_TEXT_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE, 14);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FONT_STYLE, 0);
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

    @Override
    public void onResume() {
        super.onResume();
    }

    private void updateRecentsLocation(int value) {
        Resources res = getResources();
        int summary = -1;

        Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, value);

        if (value == 0) {
            Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 0);
            summary = R.string.recents_clear_all_location_top_right;
        } else if (value == 1) {
            Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 1);
            summary = R.string.recents_clear_all_location_top_left;
        } else if (value == 2) {
            Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 2);
            summary = R.string.recents_clear_all_location_top_center;
        } else if (value == 3) {
            Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3);
            summary = R.string.recents_clear_all_location_bottom_right;
        } else if (value == 4) {
            Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 4);
            summary = R.string.recents_clear_all_location_bottom_left;
        } else if (value == 5) {
            Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 5);
            summary = R.string.recents_clear_all_location_bottom_center;
        }
        if (mRecentsClearAllLocation != null && summary != -1) {
            mRecentsClearAllLocation.setSummary(res.getString(summary));
        }
    }
}
