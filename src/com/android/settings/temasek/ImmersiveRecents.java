/*
 * Copyright (C) 2015 Temasek Android
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

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class ImmersiveRecents extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "ImmersiveRecents";

    private static final String IMMERSIVE_CATEGORY = "immersive_settings";
    private static final String IMMERSIVE_RECENTS = "immersive_recents";
    private static final String SHOW_RECENTS_CLOCK = "recents_full_screen_clock";
    private static final String SHOW_RECENTS_DATE = "recents_full_screen_date";
    private static final String RECENTS_DATE_COLOR = "recents_date_color";
    private static final String RECENTS_CLOCK_COLOR = "recents_clock_color";

    private static final int WHITE = 0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private static final int IMMERSIVE_RECENTS_OFF = 0;
    private static final int IMMERSIVE_RECENTS_SB_ONLY = 2;

    private ListPreference mImmersiveRecents;
    private SwitchPreference mShowRecentsClock;
    private SwitchPreference mShowRecentsDate;
    private ColorPickerPreference mClockColor;
    private ColorPickerPreference mDateColor;

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

        addPreferencesFromResource(R.xml.immersive_recents);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mImmersiveRecents = (ListPreference) findPreference(IMMERSIVE_RECENTS);
        int immersiveRecents = Settings.System.getIntForUser(mResolver,
                Settings.System.IMMERSIVE_RECENTS, 0,
                UserHandle.USER_CURRENT);
        mImmersiveRecents.setValue(String.valueOf(immersiveRecents));
        mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
        mImmersiveRecents.setOnPreferenceChangeListener(this);

        boolean immersiveRecentsVisible = immersiveRecents != IMMERSIVE_RECENTS_OFF;
        boolean isSbOnly = immersiveRecents == IMMERSIVE_RECENTS_SB_ONLY;

        PreferenceCategory catImmersive =
                (PreferenceCategory) findPreference(IMMERSIVE_CATEGORY);
        mShowRecentsClock = 
                (SwitchPreference) findPreference(SHOW_RECENTS_CLOCK);
        mShowRecentsDate =
                (SwitchPreference) findPreference(SHOW_RECENTS_DATE);
        mClockColor =
                (ColorPickerPreference) findPreference(RECENTS_CLOCK_COLOR);
        mDateColor =
                (ColorPickerPreference) findPreference(RECENTS_DATE_COLOR);

        if (immersiveRecentsVisible && !isSbOnly) {
        	mShowRecentsClock.setChecked(Settings.System.getInt(mResolver,
            	Settings.System.RECENTS_FULL_SCREEN_CLOCK, 0) == 1);
        	mShowRecentsClock.setOnPreferenceChangeListener(this);

        	mShowRecentsDate.setChecked(Settings.System.getInt(mResolver,
            	Settings.System.RECENTS_FULL_SCREEN_DATE, 0) == 1);
        	mShowRecentsDate.setOnPreferenceChangeListener(this);

        	mClockColor.setOnPreferenceChangeListener(this);
        	intColor = Settings.System.getInt(mResolver,
            	Settings.System.RECENTS_CLOCK_COLOR, WHITE);
        	hexColor = String.format("#%08x", (0xffffffff & intColor));
        	mClockColor.setSummary(hexColor);
        	mClockColor.setNewPreviewColor(intColor);

        	mDateColor.setOnPreferenceChangeListener(this);
        	intColor = Settings.System.getInt(mResolver,
            	Settings.System.RECENTS_DATE_COLOR, WHITE);
        	hexColor = String.format("#%08x", (0xffffffff & intColor));
        	mDateColor.setSummary(hexColor);
        	mDateColor.setNewPreviewColor(intColor);
        } else {
            catImmersive.removePreference(mShowRecentsClock);
            catImmersive.removePreference(mShowRecentsDate);
            catImmersive.removePreference(mClockColor);
            catImmersive.removePreference(mDateColor);
            removePreference(IMMERSIVE_CATEGORY);
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
        boolean value;
        String hex;
        int intValue, index, intHex;

        if (preference == mImmersiveRecents) {
            intValue = Integer.valueOf((String) newValue);
            index = mImmersiveRecents.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(mResolver,
                    Settings.System.IMMERSIVE_RECENTS, intValue, UserHandle.USER_CURRENT);
            mImmersiveRecents.setSummary(mImmersiveRecents.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mShowRecentsClock) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_FULL_SCREEN_CLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mShowRecentsDate) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_FULL_SCREEN_DATE, value ? 1 : 0);
            return true;
        } else if (preference == mClockColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_CLOCK_COLOR, intHex);
            return true;
        } else if (preference == mDateColor) {
            hex = ColorPickerPreference.convertToARGB(
                   Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                   Settings.System.RECENTS_DATE_COLOR, intHex);
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

        ImmersiveRecents getOwner() {
            return (ImmersiveRecents) getTargetFragment();
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
                                    Settings.System.IMMERSIVE_RECENTS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_CLOCK, 0);
        	                Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_DATE, 0);
        	                Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_CLOCK_COLOR, WHITE);
        	                Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_DATE_COLOR, WHITE);
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
}
