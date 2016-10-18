/*
 * Copyright (C) 2015 Temasek
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

package com.android.settings.temasek.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class Logo extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    public static final String TAG = "Logo";

    private static final String KEY_TEMASEK_LOGO_STYLE = "status_bar_temasek_logo_style";
    private static final String KEY_TEMASEK_LOGO_COLOR = "status_bar_temasek_logo_color";

    private ListPreference mTemasekLogoStyle;
    private ColorPickerPreference mTemasekLogoColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.temasek_logo);
        PreferenceScreen prefSet = getPreferenceScreen();

        // Temasek logo
        mTemasekLogoStyle = (ListPreference) findPreference(KEY_TEMASEK_LOGO_STYLE);
        int temasekLogoStyle = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_TEMASEK_LOGO_STYLE, 0,
                UserHandle.USER_CURRENT);
        mTemasekLogoStyle.setValue(String.valueOf(temasekLogoStyle));
        mTemasekLogoStyle.setSummary(mTemasekLogoStyle.getEntry());
        mTemasekLogoStyle.setOnPreferenceChangeListener(this);

        mTemasekLogoColor =
            (ColorPickerPreference) prefSet.findPreference(KEY_TEMASEK_LOGO_COLOR);
        mTemasekLogoColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(getContentResolver(),
            Settings.System.STATUS_BAR_TEMASEK_LOGO_COLOR, 0xffffffff);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTemasekLogoColor.setSummary(hexColor);
        mTemasekLogoColor.setNewPreviewColor(intColor);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mTemasekLogoStyle) {
            int temasekLogoStyle = Integer.valueOf((String) newValue);
            int index = mTemasekLogoStyle.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_TEMASEK_LOGO_STYLE, temasekLogoStyle,
                UserHandle.USER_CURRENT);
            mTemasekLogoStyle.setSummary(mTemasekLogoStyle.getEntries()[index]);
            return true;
        } else if (preference == mTemasekLogoColor) {
            String hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                Settings.System.STATUS_BAR_TEMASEK_LOGO_COLOR,
                intHex);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
