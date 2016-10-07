/*
 * Copyright (C) 2016 ParaSHIT
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

import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class DSBSettings extends SettingsPreferenceFragment {

    private static final String KEY_DYNAMIC_STATUS_BAR = "dynamic_status_bar";
    private static final String KEY_DYNAMIC_HEADER = "dynamic_header";
    private static final String KEY_DYNAMIC_NAVIGATION_BAR = "dynamic_navigation_bar";
    private static final String KEY_DYNAMIC_SYSTEM_BARS_GRADIENT = "dynamic_system_bars_gradient";
    private static final String KEY_DYNAMIC_STATUS_BAR_FILTER = "dynamic_status_bar_filter";
    private static final String KEY_DYNAMIC_ICON_TINT = "dynamic_icon_tint";
    private static final String KEY_DYNAMIC_TRANS_PS = "dynamic_trans_ps";

    private CheckBoxPreference mDynamicStatusBar;
    private CheckBoxPreference mDynamicHeader;
    private CheckBoxPreference mDynamicNavigationBar;
    private CheckBoxPreference mDynamicSystemBarsGradient;
    private CheckBoxPreference mDynamicStatusBarFilter;
    private CheckBoxPreference mDynamicIconTint;
    private CheckBoxPreference mDynamicTransPs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.dsb_settings);

        mDynamicStatusBar = (CheckBoxPreference) findPreference(KEY_DYNAMIC_STATUS_BAR);
        mDynamicStatusBar.setPersistent(false);

        mDynamicHeader = (CheckBoxPreference) findPreference(KEY_DYNAMIC_HEADER);
        mDynamicHeader.setPersistent(false);

        mDynamicNavigationBar = (CheckBoxPreference) findPreference(KEY_DYNAMIC_NAVIGATION_BAR);
        mDynamicNavigationBar.setPersistent(false);

        mDynamicSystemBarsGradient =
            (CheckBoxPreference) findPreference(KEY_DYNAMIC_SYSTEM_BARS_GRADIENT);
        mDynamicSystemBarsGradient.setPersistent(false);

        mDynamicStatusBarFilter =
            (CheckBoxPreference) findPreference(KEY_DYNAMIC_STATUS_BAR_FILTER);
        mDynamicStatusBarFilter.setPersistent(false);

        mDynamicIconTint = (CheckBoxPreference) findPreference(KEY_DYNAMIC_ICON_TINT);
        mDynamicIconTint.setPersistent(false);

        mDynamicTransPs = (CheckBoxPreference) findPreference(KEY_DYNAMIC_TRANS_PS);
        mDynamicTransPs.setPersistent(false);

        updateDynamicSystemBarsCheckboxes();
    }
        
    private void updateDynamicSystemBarsCheckboxes () {
        final Resources res = getResources();
        ContentResolver resolver = getActivity().getContentResolver();

        final boolean isStatusBarDynamic = Settings.System.getInt(resolver,
            Settings.System.DYNAMIC_STATUS_BAR_STATE, 0) == 1;

        final boolean isHeaderDynamic = Settings.System.getInt(resolver,
            Settings.System.DYNAMIC_HEADER_STATE, 0) == 1;

        final boolean hasNavigationBar = res.getDimensionPixelSize(res.getIdentifier("navigation_bar_height", "dimen", "android")) > 0;
        final boolean isNavigationBarDynamic = hasNavigationBar && Settings.System.getInt(resolver,
            Settings.System.DYNAMIC_NAVIGATION_BAR_STATE, 0) == 1;

        final boolean isAnyBarDynamic = isStatusBarDynamic || isNavigationBarDynamic;

        mDynamicStatusBar.setChecked(isStatusBarDynamic);

        mDynamicHeader.setChecked(isHeaderDynamic);

        mDynamicNavigationBar.setEnabled(hasNavigationBar);
        mDynamicNavigationBar.setChecked(isNavigationBarDynamic);

        final boolean areSystemBarsGradient = isAnyBarDynamic && Settings.System.getInt(resolver,
            Settings.System.DYNAMIC_SYSTEM_BARS_GRADIENT_STATE, 0) == 1;
        final boolean isStatusBarFilter = isStatusBarDynamic && Settings.System.getInt(resolver,
            Settings.System.DYNAMIC_STATUS_BAR_FILTER_STATE, 0) == 1;

        final boolean isStatusBarColor = isStatusBarDynamic && Settings.System.getInt(resolver,
            Settings.System.DYNAMIC_ICON_TINT_STATE, 0) == 1;

        final boolean isTransPs = Settings.System.getInt(resolver,
            Settings.System.DYNAMIC_TRANSPARENT_PS, 0) == 1;

        mDynamicSystemBarsGradient.setEnabled(isAnyBarDynamic &&
            (areSystemBarsGradient || !isStatusBarFilter));
        mDynamicSystemBarsGradient.setChecked(areSystemBarsGradient);

        mDynamicStatusBarFilter.setEnabled(isStatusBarDynamic &&
            (isStatusBarFilter || !areSystemBarsGradient));
        mDynamicStatusBarFilter.setChecked(isStatusBarFilter);

        mDynamicIconTint.setEnabled(isStatusBarDynamic);
        mDynamicIconTint.setChecked(isStatusBarColor);

        mDynamicTransPs.setChecked(isTransPs);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final Resources res = getResources();
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mDynamicStatusBar) {
            Settings.System.putInt(resolver,
                Settings.System.DYNAMIC_STATUS_BAR_STATE,
                mDynamicStatusBar.isChecked() ? 1 : 0);
            updateDynamicSystemBarsCheckboxes();
        } else if (preference == mDynamicHeader) {
            Settings.System.putInt(resolver,
                Settings.System.DYNAMIC_HEADER_STATE,
                mDynamicHeader.isChecked() ? 1 : 0);
            updateDynamicSystemBarsCheckboxes();
        } else if (preference == mDynamicNavigationBar) {
            Settings.System.putInt(resolver,
                Settings.System.DYNAMIC_NAVIGATION_BAR_STATE,
                mDynamicNavigationBar.isChecked() && res.getDimensionPixelSize(
                    res.getIdentifier("navigation_bar_height", "dimen", "android")) > 0 ?
                        1 : 0);
            updateDynamicSystemBarsCheckboxes();
        } else if (preference == mDynamicSystemBarsGradient) {
            final boolean enableGradient = mDynamicSystemBarsGradient.isChecked();
            Settings.System.putInt(resolver,
                Settings.System.DYNAMIC_SYSTEM_BARS_GRADIENT_STATE,
                enableGradient ? 1 : 0);
            if (enableGradient) {
                Settings.System.putInt(resolver,
                    Settings.System.DYNAMIC_STATUS_BAR_FILTER_STATE, 0);
            }
            updateDynamicSystemBarsCheckboxes();
        } else if (preference == mDynamicStatusBarFilter) {
            final boolean enableFilter = mDynamicStatusBarFilter.isChecked();
            Settings.System.putInt(resolver,
                Settings.System.DYNAMIC_STATUS_BAR_FILTER_STATE,
                enableFilter ? 1 : 0);
            if (enableFilter) {
                Settings.System.putInt(resolver,
                    Settings.System.DYNAMIC_SYSTEM_BARS_GRADIENT_STATE, 0);
            }
            updateDynamicSystemBarsCheckboxes();
        } else if (preference == mDynamicIconTint) {
            Settings.System.putInt(resolver,
                Settings.System.DYNAMIC_ICON_TINT_STATE,
                mDynamicIconTint.isChecked() ? 1 : 0);
            updateDynamicSystemBarsCheckboxes();
        } else if (preference == mDynamicTransPs) {
            Settings.System.putInt(resolver,
                Settings.System.DYNAMIC_TRANSPARENT_PS,
                mDynamicTransPs.isChecked() ? 1 : 0);
            updateDynamicSystemBarsCheckboxes();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
