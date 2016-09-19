/*
 * Copyright (C) 2015 DarkKat
 * Copyright (C) 2015 AICP
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
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.cyanogenmod.qs.QSTiles;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.temasek.SeekBarPreference;

import java.util.ArrayList;
import java.util.List;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class QSColors extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String PREF_QS_COLOR_SWITCH = "qs_color_switch";
    private static final String PREF_QS_TRANSPARENT_SHADE = "qs_transparent_shade";
    private static final String QS_COLOR_CATEGORY = "qs_cat_colors";
    private static final String PREF_QS_BACKGROUND_COLOR = "qs_background_color";
    private static final String PREF_QS_ICON_COLOR = "qs_icon_color";
    private static final String PREF_QS_TEXT_COLOR = "qs_text_color";
    private static final String PREF_QS_RIPPLE_COLOR = "qs_ripple_color";
    private static final String PREF_QS_BRIGHTNESS_ICON_COLOR = "qs_brightness_icon_color";
    private static final String PREF_QS_BRIGHTNESS_SLIDER_ICON_COLOR = "qs_brightness_slider_icon_color";
    private static final String PREF_QS_BRIGHTNESS_SLIDER_COLOR = "qs_brightness_slider_color";
    private static final String PREF_QS_BRIGHTNESS_SLIDER_BG_COLOR = "qs_brightness_slider_bg_color";
    private static final String PREF_QS_PANEL_LOGO = "qs_panel_logo";
    private static final String PREF_QS_PANEL_LOGO_ALPHA = "qs_panel_logo_alpha";
    private static final String PREF_QS_PANEL_LOGO_COLOR = "qs_panel_logo_color";

    private static final int DEFAULT_BACKGROUND_COLOR = 0xff263238;
    private static final int DEFAULT_BG_COLOR = 0xff384248;
    private static final int DEFAULT_QS_PANEL_LOGO_COLOR = 0xff33b5e5;
    private static final int DEFAULT_SLIDER_COLOR = 0xff80cbc4;
    private static final int WHITE = 0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mQSSwitch;
    private SwitchPreference mQSShadeTransparency;
    private ColorPickerPreference mQSBackgroundColor;
    private ColorPickerPreference mQSIconColor;
    private ColorPickerPreference mQSTextColor;
    private ColorPickerPreference mQSRippleColor;
    private ColorPickerPreference mSliderIconColor;
    private ColorPickerPreference mQSBrightnessSliderIconColor;
    private ColorPickerPreference mQSBrightnessSliderColor;
    private ColorPickerPreference mQSBrightnessSliderBgColor;
    private ListPreference mQSPanelLogo;
    private SeekBarPreference mQSPanelLogoAlpha;
    private ColorPickerPreference mQSPanelLogoColor;

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

        addPreferencesFromResource(R.xml.qs_color_settings);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(QS_COLOR_CATEGORY);

        boolean qSSwitch = Settings.System.getInt(mResolver,
                Settings.System.QS_COLOR_SWITCH, 0) == 1;

        mQSSwitch = (SwitchPreference) findPreference(PREF_QS_COLOR_SWITCH);
        mQSSwitch.setChecked(qSSwitch);
        mQSSwitch.setOnPreferenceChangeListener(this);

        mQSShadeTransparency = (SwitchPreference) findPreference(PREF_QS_TRANSPARENT_SHADE);
        mQSShadeTransparency.setChecked((Settings.System.getInt(mResolver,
                Settings.System.QS_TRANSPARENT_SHADE, 0) == 1));
        mQSShadeTransparency.setOnPreferenceChangeListener(this);

        if (qSSwitch) {
        	mQSBackgroundColor = (ColorPickerPreference) findPreference(PREF_QS_BACKGROUND_COLOR);
        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.QS_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
        	mQSBackgroundColor.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xffffffff & intColor));
        	mQSBackgroundColor.setSummary(hexColor);
        	mQSBackgroundColor.setAlphaSliderEnabled(true);
        	mQSBackgroundColor.setOnPreferenceChangeListener(this);

        	mQSIconColor = (ColorPickerPreference) findPreference(PREF_QS_ICON_COLOR);
        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.QS_ICON_COLOR, WHITE);
        	mQSIconColor.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xffffffff & intColor));
        	mQSIconColor.setSummary(hexColor);
        	mQSIconColor.setOnPreferenceChangeListener(this);

        	mQSTextColor = (ColorPickerPreference) findPreference(PREF_QS_TEXT_COLOR);
        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.QS_TEXT_COLOR, WHITE);
        	mQSTextColor.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xffffffff & intColor));
        	mQSTextColor.setSummary(hexColor);
        	mQSTextColor.setOnPreferenceChangeListener(this);

        	mQSRippleColor = (ColorPickerPreference) findPreference(PREF_QS_RIPPLE_COLOR);
        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.QS_RIPPLE_COLOR, WHITE); 
        	mQSRippleColor.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xffffffff & intColor));
        	mQSRippleColor.setSummary(hexColor);
        	mQSRippleColor.setOnPreferenceChangeListener(this);

        	mSliderIconColor = (ColorPickerPreference) findPreference(PREF_QS_BRIGHTNESS_ICON_COLOR);
        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.QS_BRIGHTNESS_ICON_COLOR, WHITE);
        	mSliderIconColor.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xffffffff & intColor));
        	mSliderIconColor.setSummary(hexColor);
        	mSliderIconColor.setOnPreferenceChangeListener(this);

        	mQSBrightnessSliderIconColor =
                	(ColorPickerPreference) findPreference(PREF_QS_BRIGHTNESS_SLIDER_ICON_COLOR);
        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.QS_BRIGHTNESS_SLIDER_ICON_COLOR, WHITE);
        	mQSBrightnessSliderIconColor.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xffffffff & intColor));
        	mQSBrightnessSliderIconColor.setSummary(hexColor);
        	mQSBrightnessSliderIconColor.setOnPreferenceChangeListener(this);

        	mQSBrightnessSliderColor =
                	(ColorPickerPreference) findPreference(PREF_QS_BRIGHTNESS_SLIDER_COLOR);
        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.QS_BRIGHTNESS_SLIDER_COLOR, DEFAULT_SLIDER_COLOR); 
        	mQSBrightnessSliderColor.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xff80cbc4 & intColor));
        	mQSBrightnessSliderColor.setSummary(hexColor);
        	mQSBrightnessSliderColor.setOnPreferenceChangeListener(this);

        	mQSBrightnessSliderBgColor =
                	(ColorPickerPreference) findPreference(PREF_QS_BRIGHTNESS_SLIDER_BG_COLOR);
        	intColor = Settings.System.getInt(mResolver,
                	Settings.System.QS_BRIGHTNESS_SLIDER_BG_COLOR, WHITE); 
        	mQSBrightnessSliderBgColor.setNewPreviewColor(intColor);
        	hexColor = String.format("#%08x", (0xffffffff & intColor));
        	mQSBrightnessSliderBgColor.setSummary(hexColor);
	    	mQSBrightnessSliderBgColor.setOnPreferenceChangeListener(this);

        } else {
            removePreference(QS_COLOR_CATEGORY);
            removePreference(PREF_QS_TRANSPARENT_SHADE);
        }

        // QS panel logo
        mQSPanelLogo =
                (ListPreference) findPreference(PREF_QS_PANEL_LOGO);
        int qSPanelLogo = Settings.System.getIntForUser(mResolver,
                Settings.System.QS_PANEL_LOGO, 0,
                UserHandle.USER_CURRENT);
        mQSPanelLogo.setValue(String.valueOf(qSPanelLogo));
        mQSPanelLogo.setSummary(mQSPanelLogo.getEntry());
        mQSPanelLogo.setOnPreferenceChangeListener(this);
 
        // QS panel logo alpha
        mQSPanelLogoAlpha =
                (SeekBarPreference) findPreference(PREF_QS_PANEL_LOGO_ALPHA);
        int qSPanelLogoAlpha = Settings.System.getInt(mResolver,
                Settings.System.QS_PANEL_LOGO_ALPHA, 50);
        mQSPanelLogoAlpha.setValue(qSPanelLogoAlpha / 1);
        mQSPanelLogoAlpha.setOnPreferenceChangeListener(this);

        // QS panel logo color
        mQSPanelLogoColor =
                (ColorPickerPreference) findPreference(PREF_QS_PANEL_LOGO_COLOR);
        mQSPanelLogoColor.setOnPreferenceChangeListener(this);
        int qSPanelLogoColor = Settings.System.getInt(mResolver,
                Settings.System.QS_PANEL_LOGO_COLOR, DEFAULT_QS_PANEL_LOGO_COLOR);
        String qSHexLogoColor = String.format("#%08x", (0xff33b5e5 & qSPanelLogoColor));
        mQSPanelLogoColor.setSummary(qSHexLogoColor);
        mQSPanelLogoColor.setNewPreviewColor(qSPanelLogoColor);

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
        
        if (preference == mQSSwitch) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.QS_COLOR_SWITCH, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mQSShadeTransparency) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.QS_TRANSPARENT_SHADE, value ? 1 : 0);
            return true;
        } else if (preference == mQSBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_BACKGROUND_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mQSIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mQSTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mQSRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_RIPPLE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mSliderIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.QS_BRIGHTNESS_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mQSBrightnessSliderIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_BRIGHTNESS_SLIDER_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }  else if (preference == mQSBrightnessSliderColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_BRIGHTNESS_SLIDER_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mQSBrightnessSliderBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_BRIGHTNESS_SLIDER_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mQSPanelLogo) {
            int qSPanelLogo = Integer.parseInt((String) newValue);
            int index = mQSPanelLogo.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(mResolver, Settings.System.
                    QS_PANEL_LOGO, qSPanelLogo, UserHandle.USER_CURRENT);
            mQSPanelLogo.setSummary(mQSPanelLogo.getEntries()[index]);
            QSPanelLogoSettingsDisabler(qSPanelLogo);
            return true;
        } else if (preference == mQSPanelLogoAlpha) {
            int val = (Integer) newValue;
            Settings.System.putInt(mResolver,
                   Settings.System.QS_PANEL_LOGO_ALPHA, val * 1);
            return true;
        } else if (preference == mQSPanelLogoColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.QS_PANEL_LOGO_COLOR, intHex);
            return true;
        }
        return false;
    }

    private void QSPanelLogoSettingsDisabler(int qSPanelLogo) {
         if (qSPanelLogo == 0) {
             mQSPanelLogoColor.setEnabled(false);
             mQSPanelLogoAlpha.setEnabled(false);
         } else if (qSPanelLogo == 1) {
             mQSPanelLogoColor.setEnabled(false);
             mQSPanelLogoAlpha.setEnabled(true);
         } else {
             mQSPanelLogoColor.setEnabled(true);
             mQSPanelLogoAlpha.setEnabled(true);
         }
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

        QSColors getOwner() {
            return (QSColors) getTargetFragment();
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
                                    Settings.System.QS_BACKGROUND_COLOR,
                                    DEFAULT_BACKGROUND_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_ICON_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_TEXT_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_RIPPLE_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_BRIGHTNESS_ICON_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_BRIGHTNESS_SLIDER_ICON_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_BRIGHTNESS_SLIDER_COLOR,
                                    DEFAULT_SLIDER_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_BRIGHTNESS_SLIDER_BG_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_TRANSPARENT_SHADE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_PANEL_LOGO, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_PANEL_LOGO_ALPHA, 50);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_PANEL_LOGO_COLOR,
                                    DEFAULT_QS_PANEL_LOGO_COLOR);
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
            sir.xmlResId = R.xml.qs_color_settings;
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
