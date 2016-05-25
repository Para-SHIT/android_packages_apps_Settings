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

import android.content.Context;
import android.content.ContentResolver;
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

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
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

    private static final int RED = 0xffDC4C3C;
    private static final int WHITE = 0xffffffff;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private SwitchPreference mRecentsClearAll;
    private ListPreference mRecentsClearAllLocation;
    private ColorPickerPreference mClearAllIconColor;
    private ColorPickerPreference mClearAllBgColor;
    private ListPreference mImmersiveRecents;

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

        mClearAllBgColor =
        (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_BG_COLOR);
        intColor = Settings.System.getInt(resolver,
            Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, RED); 
        mClearAllBgColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mClearAllBgColor.setSummary(hexColor);
        //mClearAllBgColor.setDefaultColors(RED, RED);
        mClearAllBgColor.setOnPreferenceChangeListener(this);

        mClearAllIconColor =
		      (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_ICON_COLOR);
        intColor = Settings.System.getInt(resolver,
           Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, WHITE); 
        mClearAllIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mClearAllIconColor.setSummary(hexColor);
        //mClearAllIconColor.setDefaultColors(WHITE, WHITE);
        mClearAllIconColor.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;
        int intvalue;
        int index;
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
        }
        return false;
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
