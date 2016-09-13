/*
 * Copyright (C) 2015 DarkKat
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
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.temasek.GreetingTextHelper;

import com.android.settings.R;
import com.android.settings.temasek.SeekBarPreference;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarGreetingSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String PREF_SHOW_LABEL =
            "greeting_show_label";
    private static final String PREF_CUSTOM_LABEL =
            "greeting_custom_label";
    private static final String PREF_TIMEOUT =
            "greeting_timeout";
    private static final String PREF_PREVIEW_LABEL  =
            "status_bar_greeting_show_label_preview";
    private static final String PREF_FONT_SIZE  =
            "status_bar_greeting_font_size";
    private static final String PREF_COLOR =
            "greeting_color";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;
    
    private static final int WHITE = 0xffffffff;
    private static final int TEMASEK_BLUE = 0xff33b5e5;

    private static final int HIDDEN = 2;

    private ListPreference mShowLabel;
    private EditTextPreference mCustomLabel;
    private SeekBarPreference mTimeOut;
    SwitchPreference mPreviewLabel;
    private SeekBarPreference mGreetingFontSize;
    private ColorPickerPreference mColor;

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

        mResolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.status_bar_greeting_settings);

        mShowLabel =
                (ListPreference) findPreference(PREF_SHOW_LABEL);
        int showLabel = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_SHOW_LABEL, 1);
        mShowLabel.setValue(String.valueOf(showLabel));
        mShowLabel.setOnPreferenceChangeListener(this);

        if (showLabel != HIDDEN) {
            mCustomLabel = (EditTextPreference) findPreference(PREF_CUSTOM_LABEL);
            mCustomLabel.getEditText().setHint(
                    GreetingTextHelper.getDefaultGreetingText(getActivity()));
            mCustomLabel.setDialogMessage(getString(R.string.weather_hide_panel_custom_summary,
                    GreetingTextHelper.getDefaultGreetingText(getActivity())));
            mCustomLabel.setOnPreferenceChangeListener(this);

            mTimeOut =
                    (SeekBarPreference) findPreference(PREF_TIMEOUT);
            int timeout = Settings.System.getInt(getContentResolver(),
                    Settings.System.STATUS_BAR_GREETING_TIMEOUT, 400);
            mTimeOut.setValue(timeout / 1);
            mTimeOut.setOnPreferenceChangeListener(this);

            mPreviewLabel = (SwitchPreference) findPreference(PREF_PREVIEW_LABEL);
            boolean previewLabel = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_SHOW_LABEL_PREVIEW, 0) == 1;
            mPreviewLabel.setChecked(previewLabel);
            mPreviewLabel.setOnPreferenceChangeListener(this);

            if (previewLabel) {
                mGreetingFontSize = 
                        (SeekBarPreference) findPreference(PREF_FONT_SIZE);
                mGreetingFontSize.setValue(Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_GREETING_FONT_SIZE, 12));
                mGreetingFontSize.setOnPreferenceChangeListener(this);
            } else {
                removePreference(PREF_FONT_SIZE);
            }

            mColor =
                    (ColorPickerPreference) findPreference(PREF_COLOR);
            int intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_GREETING_COLOR,
                    WHITE); 
            mColor.setNewPreviewColor(intColor);
            String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mColor.setSummary(hexColor);
            mColor.setOnPreferenceChangeListener(this);

            updateCustomLabelPreference();
            updateShowLabelSummary(showLabel);
        } else {
            removePreference(PREF_CUSTOM_LABEL);
            removePreference(PREF_TIMEOUT);
            removePreference(PREF_FONT_SIZE);
            removePreference(PREF_COLOR);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_backup_restore)
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

        if (preference == mShowLabel) {
            int showLabel = Integer.valueOf((String) newValue);
            int index = mShowLabel.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_SHOW_LABEL, showLabel);
            updateShowLabelSummary(index);
            refreshSettings();
            return true;
        } else if (preference == mCustomLabel) {
            String label = (String) newValue;
            Settings.System.putString(mResolver,
                    Settings.System.STATUS_BAR_GREETING_CUSTOM_LABEL, label);
            updateCustomLabelPreference();
        } else if (preference == mTimeOut) {
            int timeout = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_GREETING_TIMEOUT, timeout * 1);
            return true;
        } else if (preference == mPreviewLabel) {
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_GREETING_SHOW_LABEL_PREVIEW,
            (Boolean) newValue ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mGreetingFontSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_GREETING_FONT_SIZE, width);
            return true;
        } else if (preference == mColor) {
            String hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void updateShowLabelSummary(int index) {
        int resId;

        if (index == 0) {
            resId = R.string.greeting_show_label_always_summary;
        } else if (index == 1) {
            resId = R.string.greeting_show_label_once_summary;
        } else {
            resId = R.string.greeting_show_label_never_summary;
        }
        mShowLabel.setSummary(getResources().getString(resId));
    }

    private void updateCustomLabelPreference() {
        String customLabelText = Settings.System.getString(mResolver,
                Settings.System.STATUS_BAR_GREETING_CUSTOM_LABEL);
        if (customLabelText == null) {
            customLabelText = "";
        }
        mCustomLabel.setText(customLabelText);
        mCustomLabel.setSummary(customLabelText.isEmpty() 
                ? GreetingTextHelper.getDefaultGreetingText(getActivity()) : customLabelText);
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

        StatusBarGreetingSettings getOwner() {
            return (StatusBarGreetingSettings) getTargetFragment();
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
                    .setNeutralButton(R.string.reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_GREETING_SHOW_LABEL, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_GREETING_TIMEOUT,
                                    400);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_GREETING_FONT_SIZE, 14);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_GREETING_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_temasek,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_GREETING_SHOW_LABEL, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_GREETING_TIMEOUT,
                                    1000);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_GREETING_FONT_SIZE, 14);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_GREETING_COLOR,
                                    TEMASEK_BLUE);
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
