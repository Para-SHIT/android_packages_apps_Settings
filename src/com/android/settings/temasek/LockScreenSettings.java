/*
 * Copyright (C) 2015 crDroid Android
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

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.preference.ListPreference;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.preference.SwitchPreference;
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.temasek.SeekBarPreference;
import com.android.settings.util.Helpers;

public class LockScreenSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {
    public static final int IMAGE_PICK = 1;

    private static final String TAG = "LockScreenSettings";

    private static final String KEY_WALLPAPER_SET = "lockscreen_wallpaper_set";
    private static final String KEY_WALLPAPER_CLEAR = "lockscreen_wallpaper_clear";
    private static final String KEY_LOCKSCREEN_BLUR_RADIUS = "lockscreen_blur_radius";
    private static final String LOCK_CLOCK_FONTS = "lock_clock_fonts";
    private static final String LOCK_DATE_FONTS = "lock_date_fonts";
    private static final String CLOCK_FONT_SIZE  = "lockclock_font_size";
    private static final String DATE_FONT_SIZE  = "lockdate_font_size";

    private ListPreference mLockClockFonts;
    private ListPreference mDateFonts;
    private ListPreference mClockFontSize;
    private ListPreference mDateFontSize;
    private Preference mSetWallpaper;
    private Preference mClearWallpaper;
    private SeekBarPreference mBlurRadius;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.temasek_lockscreen);
        ContentResolver resolver = getActivity().getContentResolver();

        mSetWallpaper = (Preference) findPreference(KEY_WALLPAPER_SET);
        mClearWallpaper = (Preference) findPreference(KEY_WALLPAPER_CLEAR);
        
        mBlurRadius = (SeekBarPreference) findPreference(KEY_LOCKSCREEN_BLUR_RADIUS);
        mBlurRadius.setValue(Settings.System.getInt(resolver,
                Settings.System.LOCKSCREEN_BLUR_RADIUS, 14));
        mBlurRadius.setOnPreferenceChangeListener(this);

        mLockClockFonts = (ListPreference) findPreference(LOCK_CLOCK_FONTS);
        mLockClockFonts.setValue(String.valueOf(Settings.System.getInt(
             resolver, Settings.System.LOCK_CLOCK_FONTS, 4)));
        mLockClockFonts.setSummary(mLockClockFonts.getEntry());
        mLockClockFonts.setOnPreferenceChangeListener(this);
            
        mDateFonts = (ListPreference) findPreference(LOCK_DATE_FONTS);
        mDateFonts.setValue(String.valueOf(Settings.System.getInt(
             resolver, Settings.System.LOCK_DATE_FONTS, 4)));
        mDateFonts.setSummary(mDateFonts.getEntry());
        mDateFonts.setOnPreferenceChangeListener(this);
            
        mClockFontSize = (ListPreference) findPreference(CLOCK_FONT_SIZE);
        mClockFontSize.setOnPreferenceChangeListener(this);
        mClockFontSize.setValue(Integer.toString(Settings.System.getInt(getActivity()
            .getContentResolver(), Settings.System.LOCKCLOCK_FONT_SIZE, 14)));
        mClockFontSize.setSummary(mClockFontSize.getEntry());

        mDateFontSize = (ListPreference) findPreference(DATE_FONT_SIZE);
        mDateFontSize.setOnPreferenceChangeListener(this);
        mDateFontSize.setValue(Integer.toString(Settings.System.getInt(getActivity()
            .getContentResolver(), Settings.System.LOCKDATE_FONT_SIZE, 14)));
        mDateFontSize.setSummary(mDateFontSize.getEntry());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getApplicationContext().getContentResolver();
         if (preference == mBlurRadius) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, width);
            return true;
        } else if (preference == mLockClockFonts) {
            Settings.System.putInt(resolver, Settings.System.LOCK_CLOCK_FONTS,
                    Integer.valueOf((String) newValue));
            mLockClockFonts.setValue(String.valueOf(newValue));
            mLockClockFonts.setSummary(mLockClockFonts.getEntry());
            return true;
        } else if (preference == mDateFonts) {
            Settings.System.putInt(resolver, Settings.System.LOCK_DATE_FONTS,
                   Integer.valueOf((String) newValue));
            mDateFonts.setValue(String.valueOf(newValue));
            mDateFonts.setSummary(mDateFonts.getEntry());
            return true;
        } else if (preference == mClockFontSize) {
            int val = Integer.parseInt((String) newValue);
            int index = mClockFontSize.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                   Settings.System.LOCKCLOCK_FONT_SIZE, val);
            mClockFontSize.setSummary(mClockFontSize.getEntries()[index]);
            return true;
        } else if (preference == mDateFontSize) {
            int val = Integer.parseInt((String) newValue);
            int index = mDateFontSize.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                   Settings.System.LOCKDATE_FONT_SIZE, val);
            mDateFontSize.setSummary(mDateFontSize.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mSetWallpaper) {
            setKeyguardWallpaper();
            return true;
        } else if (preference == mClearWallpaper) {
            clearKeyguardWallpaper();
            Toast.makeText(getView().getContext(), getString(R.string.reset_lockscreen_wallpaper),
            Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                Intent intent = new Intent();
                intent.setClassName("com.android.wallpapercropper", "com.android.wallpapercropper.WallpaperCropActivity");
                intent.putExtra("keyguardMode", "1");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void setKeyguardWallpaper() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
    }

    private void clearKeyguardWallpaper() {
        WallpaperManager wallpaperManager = null;
        wallpaperManager = WallpaperManager.getInstance(getActivity());
        wallpaperManager.clearKeyguardWallpaper();
    }
}
