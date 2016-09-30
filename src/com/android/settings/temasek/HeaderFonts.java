/*
* Copyright (C) 2016 Temasek
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
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.temasek.SeekBarPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.List;
import java.util.ArrayList;

public class HeaderFonts extends SettingsPreferenceFragment  implements Preference.OnPreferenceChangeListener ,Indexable {
 private static final String CUSTOM_HEADER_TEXT_SHADOW = "status_bar_custom_header_text_shadow";
 private static final String CUSTOM_HEADER_TEXT_SHADOW_COLOR = "status_bar_custom_header_text_shadow_color";
 private static final String PREF_STATUS_BAR_CLOCK_FONT_STYLE = "header_clock_font_style";
 private static final String PREF_STATUS_BAR_WEATHER_FONT_STYLE = "header_weather_font_style";
 private static final String PREF_STATUS_BAR_HEADER_FONT_STYLE = "status_bar_header_font_style";
 private static final String PREF_STATUS_BAR_DETAIL_FONT_STYLE = "header_detail_font_style";
 private static final String PREF_STATUS_BAR_DATE_FONT_STYLE = "header_date_font_style";
 private static final String PREF_STATUS_BAR_ALARM_FONT_STYLE = "header_alarm_font_style";

    static final int DEFAULT_HEADER_SHADOW_COLOR = 0xff000000;
    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mStatusBarClockFontStyle;
    private ListPreference mStatusBarWeatherFontStyle;
    private ListPreference mStatusBarHeaderFontStyle;
    private ListPreference mStatusBarDateFontStyle;
    private ListPreference mStatusBarDetailFontStyle;
    private ListPreference mStatusBarAlarmFontStyle;
    private SeekBarPreference mTextShadow;
    private ColorPickerPreference mTShadowColor;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.temasek_header_fonts);
        mResolver = getActivity().getContentResolver();

        // Status bar header Clock font style
        mStatusBarClockFontStyle = (ListPreference) findPreference(PREF_STATUS_BAR_CLOCK_FONT_STYLE);
        mStatusBarClockFontStyle.setOnPreferenceChangeListener(this);
        mStatusBarClockFontStyle.setValue(Integer.toString(Settings.System.getIntForUser(mResolver,
                Settings.System.HEADER_CLOCK_FONT_STYLE , 0, UserHandle.USER_CURRENT)));
        mStatusBarClockFontStyle.setSummary(mStatusBarClockFontStyle.getEntry());

        // Status bar header Weather font style
        mStatusBarWeatherFontStyle = (ListPreference) findPreference(PREF_STATUS_BAR_WEATHER_FONT_STYLE);
        mStatusBarWeatherFontStyle .setOnPreferenceChangeListener(this);
        mStatusBarWeatherFontStyle.setValue(Integer.toString(Settings.System.getIntForUser(mResolver,
                Settings.System.HEADER_WEATHER_FONT_STYLE, 0, UserHandle.USER_CURRENT)));
        mStatusBarWeatherFontStyle .setSummary(mStatusBarWeatherFontStyle.getEntry());

        // Status bar header font style
        mStatusBarHeaderFontStyle = (ListPreference) findPreference(PREF_STATUS_BAR_HEADER_FONT_STYLE);
        mStatusBarHeaderFontStyle.setOnPreferenceChangeListener(this);
        mStatusBarHeaderFontStyle.setValue(Integer.toString(Settings.System.getIntForUser(mResolver,
                Settings.System.STATUS_BAR_HEADER_FONT_STYLE, 0, UserHandle.USER_CURRENT)));
        mStatusBarHeaderFontStyle.setSummary(mStatusBarHeaderFontStyle.getEntry());

        // Status bar Detail font style
        mStatusBarDetailFontStyle = (ListPreference) findPreference(PREF_STATUS_BAR_DETAIL_FONT_STYLE);
        mStatusBarDetailFontStyle.setOnPreferenceChangeListener(this);
        mStatusBarDetailFontStyle.setValue(Integer.toString(Settings.System.getIntForUser(mResolver,
                Settings.System.HEADER_DETAIL_FONT_STYLE, 0, UserHandle.USER_CURRENT)));
        mStatusBarDetailFontStyle.setSummary(mStatusBarDetailFontStyle.getEntry());

        // Status bar header Date  font style
        mStatusBarDateFontStyle = (ListPreference) findPreference(PREF_STATUS_BAR_DATE_FONT_STYLE);
        mStatusBarDateFontStyle .setOnPreferenceChangeListener(this);
        mStatusBarDateFontStyle .setValue(Integer.toString(Settings.System.getIntForUser(mResolver,
                Settings.System.HEADER_DATE_FONT_STYLE, 0, UserHandle.USER_CURRENT)));
        mStatusBarDateFontStyle .setSummary(mStatusBarDateFontStyle .getEntry());

        // Status bar header Alarm font style
        mStatusBarAlarmFontStyle = (ListPreference) findPreference(PREF_STATUS_BAR_ALARM_FONT_STYLE);
        mStatusBarAlarmFontStyle.setOnPreferenceChangeListener(this);
        mStatusBarAlarmFontStyle.setValue(Integer.toString(Settings.System.getIntForUser(mResolver,
                Settings.System.HEADER_ALARM_FONT_STYLE, 0, UserHandle.USER_CURRENT)));
        mStatusBarAlarmFontStyle.setSummary(mStatusBarAlarmFontStyle.getEntry());

        // Status Bar header text shadow
        mTextShadow = (SeekBarPreference) findPreference(CUSTOM_HEADER_TEXT_SHADOW);
        final float textShadow = Settings.System.getFloat(mResolver,
                Settings.System.STATUS_BAR_CUSTOM_HEADER_TEXT_SHADOW, 0);
        mTextShadow.setValue((int)(textShadow));
        mTextShadow.setOnPreferenceChangeListener(this);

        //Status Bar header text shadow color
        mTShadowColor = (ColorPickerPreference) findPreference(CUSTOM_HEADER_TEXT_SHADOW_COLOR);
        mTShadowColor.setOnPreferenceChangeListener(this);
        int shadowColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_CUSTOM_HEADER_TEXT_SHADOW_COLOR, DEFAULT_HEADER_SHADOW_COLOR);
        String HexColor = String.format("#%08x", (0xff000000 & shadowColor));
        mTShadowColor.setSummary(HexColor);
        mTShadowColor.setNewPreviewColor(shadowColor);

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

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        Resources res = getResources();
        if (preference == mStatusBarClockFontStyle) {
           int val = Integer.parseInt((String) newValue);
           int index = mStatusBarClockFontStyle.findIndexOfValue((String) newValue);
           Settings.System.putIntForUser(mResolver,
                   Settings.System.HEADER_CLOCK_FONT_STYLE, val, UserHandle.USER_CURRENT);
           mStatusBarClockFontStyle.setSummary(mStatusBarClockFontStyle.getEntries()[index]);
           return true;
        } else if (preference == mStatusBarWeatherFontStyle) {
           int val = Integer.parseInt((String) newValue);
           int index = mStatusBarWeatherFontStyle.findIndexOfValue((String) newValue);
           Settings.System.putIntForUser(mResolver,
                   Settings.System.HEADER_WEATHER_FONT_STYLE, val, UserHandle.USER_CURRENT);
           mStatusBarWeatherFontStyle.setSummary(mStatusBarWeatherFontStyle.getEntries()[index]);
           return true;
        } else if (preference == mStatusBarHeaderFontStyle) {
           int val = Integer.parseInt((String) newValue);
           int index = mStatusBarHeaderFontStyle.findIndexOfValue((String) newValue);
           Settings.System.putIntForUser(mResolver,
                   Settings.System.STATUS_BAR_HEADER_FONT_STYLE, val, UserHandle.USER_CURRENT);
           mStatusBarHeaderFontStyle.setSummary(mStatusBarHeaderFontStyle.getEntries()[index]);
           return true;
        } else if (preference == mStatusBarDateFontStyle) {
           int val = Integer.parseInt((String) newValue);
           int index = mStatusBarDateFontStyle.findIndexOfValue((String) newValue);
           Settings.System.putIntForUser(mResolver,
                   Settings.System.HEADER_DATE_FONT_STYLE, val, UserHandle.USER_CURRENT);
           mStatusBarDateFontStyle.setSummary(mStatusBarDateFontStyle.getEntries()[index]);
           return true;
        } else if (preference == mStatusBarDetailFontStyle) {
           int val = Integer.parseInt((String) newValue);
           int index = mStatusBarDetailFontStyle.findIndexOfValue((String) newValue);
           Settings.System.putIntForUser(mResolver,
                   Settings.System.HEADER_DETAIL_FONT_STYLE, val, UserHandle.USER_CURRENT);
           mStatusBarDetailFontStyle.setSummary(mStatusBarDetailFontStyle.getEntries()[index]);
           return true;
        } else if (preference == mStatusBarAlarmFontStyle) {
           int val = Integer.parseInt((String) newValue);
           int index = mStatusBarAlarmFontStyle.findIndexOfValue((String) newValue);
           Settings.System.putIntForUser(mResolver,
                   Settings.System.HEADER_ALARM_FONT_STYLE, val, UserHandle.USER_CURRENT);
           mStatusBarAlarmFontStyle.setSummary(mStatusBarAlarmFontStyle.getEntries()[index]);
           return true;
        } else if (preference == mTextShadow) {
           float textShadow = (Integer) newValue;
           float realHeaderValue = (float) ((double) textShadow);
           Settings.System.putFloat(resolver,
                   Settings.System.STATUS_BAR_CUSTOM_HEADER_TEXT_SHADOW, realHeaderValue);
           return true;
        } else if (preference == mTShadowColor) {
           String hex = ColorPickerPreference.convertToARGB(
                   Integer.valueOf(String.valueOf(newValue)));
           preference.setSummary(hex);
           int intHex = ColorPickerPreference.convertToColorInt(hex);
           Settings.System.putInt(resolver,
                   Settings.System.STATUS_BAR_CUSTOM_HEADER_TEXT_SHADOW_COLOR, intHex);
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

        HeaderFonts getOwner() {
            return (HeaderFonts) getTargetFragment();
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
                                   Settings.System.HEADER_CLOCK_FONT_STYLE , 0);
                            Settings.System.putInt(getOwner().mResolver,
                                   Settings.System.HEADER_WEATHER_FONT_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                   Settings.System.STATUS_BAR_HEADER_FONT_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                   Settings.System.HEADER_DETAIL_FONT_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                   Settings.System.HEADER_DATE_FONT_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                   Settings.System.HEADER_ALARM_FONT_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                   Settings.System.STATUS_BAR_CUSTOM_HEADER_TEXT_SHADOW_COLOR, DEFAULT_HEADER_SHADOW_COLOR);
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
                     sir.xmlResId = R.xml.temasek_header_fonts;
                     result.add(sir);
 
                     return result;
                 }
 
                 @Override
                 public List<String> getNonIndexableKeys(Context context) {
                     final List<String> keys = new ArrayList<String>();
                     return keys;
                 }
    };
}
