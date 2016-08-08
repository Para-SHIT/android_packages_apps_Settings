/*
 * Copyright (C) 2016 Cyanide Android (rogersb11)
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
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class TaskManagerSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String ENABLE_TASK_MANAGER = "enable_task_manager";
    private static final String COLORS_CATEGORY = "task_manager_colors";
    private static final String TASK_MANAGER_APP_COLOR = "task_manager_app_color";
    private static final String TASK_MANAGER_MEMORY_TEXT_COLOR = "task_manager_memory_text_color";
    private static final String TASK_MANAGER_SLIDER_COLOR = "task_manager_slider_color";
    private static final String TASK_MANAGER_SLIDER_INACTIVE_COLOR = "task_manager_slider_inactive_color";
    private static final String TASK_MANAGER_TASK_KILL_BUTTON_COLOR = "task_manager_task_kill_button_color";
    private static final String TASK_MANAGER_TASK_TEXT_COLOR = "task_manager_task_text_color";
    private static final String TASK_MANAGER_TITLE_TEXT_COLOR = "task_manager_title_text_color";
    private static final String TASK_MANAGER_TASK_KILL_ALL_COLOR = "task_manager_kill_all_color";
    private static final String TASK_MANAGER_FONT_STYLE = "task_manager_font_style";
    private static final String TASK_MANAGER_BAR_THICKNESS = "task_manager_bar_thickness";

    private static final int TEMASEK_GREEN = 0xff009688;
    private static final int TEMASEK_RED = 0xfff44235;
    private static final int WHITE = 0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mTaskManager;
    private ColorPickerPreference mAppColor;
    private ColorPickerPreference mMemTextColor;
    private ColorPickerPreference mSliderColor;
    private ColorPickerPreference mSliderInactiveColor;
    private ColorPickerPreference mTaskKillColor;
    private ColorPickerPreference mTaskTextColor;
    private ColorPickerPreference mTaskTitleTextColor;
    private ColorPickerPreference mTaskKillAllColor;
    private ListPreference mFontStyle;
    private ListPreference mBarThickness;

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

        addPreferencesFromResource(R.xml.task_manager_settings);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(COLORS_CATEGORY);

        boolean showTaskManager = Settings.System.getInt(mResolver,
                Settings.System.ENABLE_TASK_MANAGER, 0) == 1;

        mTaskManager = (SwitchPreference) findPreference(ENABLE_TASK_MANAGER);
        mTaskManager.setChecked(showTaskManager);
        mTaskManager.setOnPreferenceChangeListener(this);

        if (showTaskManager) {
            mAppColor =
                    (ColorPickerPreference) findPreference(TASK_MANAGER_APP_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.TASK_MANAGER_APP_COLOR,
                    WHITE);
            mAppColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mAppColor.setSummary(hexColor);
            mAppColor.setOnPreferenceChangeListener(this);

            mMemTextColor =
                    (ColorPickerPreference) findPreference(TASK_MANAGER_MEMORY_TEXT_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.TASK_MANAGER_MEMORY_TEXT_COLOR,
                    WHITE);
            mMemTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mMemTextColor.setSummary(hexColor);
            mMemTextColor.setOnPreferenceChangeListener(this);

            mSliderColor =
                    (ColorPickerPreference) findPreference(TASK_MANAGER_SLIDER_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.TASK_MANAGER_SLIDER_COLOR,
                    TEMASEK_GREEN);
            mSliderColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xff009688 & intColor));
            mSliderColor.setSummary(hexColor);
            mSliderColor.setOnPreferenceChangeListener(this);

            mSliderInactiveColor =
                    (ColorPickerPreference) findPreference(TASK_MANAGER_SLIDER_INACTIVE_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.TASK_MANAGER_SLIDER_INACTIVE_COLOR, TEMASEK_RED); 
            mSliderInactiveColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xfff44235 & intColor));
            mSliderInactiveColor.setSummary(hexColor);
            mSliderInactiveColor.setOnPreferenceChangeListener(this);

            mTaskKillColor =
                    (ColorPickerPreference) findPreference(TASK_MANAGER_TASK_KILL_BUTTON_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.TASK_MANAGER_TASK_KILL_BUTTON_COLOR, WHITE); 
            mTaskKillColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTaskKillColor.setSummary(hexColor);
            mTaskKillColor.setOnPreferenceChangeListener(this);

            mTaskKillAllColor =
                    (ColorPickerPreference) findPreference(TASK_MANAGER_TASK_KILL_ALL_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.TASK_MANAGER_TASK_KILL_ALL_COLOR, TEMASEK_RED); 
            mTaskKillAllColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xfff44235 & intColor));
            mTaskKillAllColor.setSummary(hexColor);
            mTaskKillAllColor.setOnPreferenceChangeListener(this);

            mTaskTextColor =
                    (ColorPickerPreference) findPreference(TASK_MANAGER_TASK_TEXT_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.TASK_MANAGER_TASK_TEXT_COLOR, WHITE); 
            mTaskTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTaskTextColor.setSummary(hexColor);
            mTaskTextColor.setOnPreferenceChangeListener(this);

            mTaskTitleTextColor =
                    (ColorPickerPreference) findPreference(TASK_MANAGER_TITLE_TEXT_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.TASK_MANAGER_TITLE_TEXT_COLOR, WHITE); 
            mTaskTitleTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTaskTitleTextColor.setSummary(hexColor);
            mTaskTitleTextColor.setOnPreferenceChangeListener(this);

            mFontStyle = (ListPreference) findPreference(TASK_MANAGER_FONT_STYLE);
            mFontStyle.setOnPreferenceChangeListener(this);
            mFontStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                    .getContentResolver(), Settings.System.TASK_MANAGER_FONT_STYLE, 0)));
            mFontStyle.setSummary(mFontStyle.getEntry());

            mBarThickness = (ListPreference) findPreference(TASK_MANAGER_BAR_THICKNESS);
            mBarThickness.setOnPreferenceChangeListener(this);
            mBarThickness.setValue(Integer.toString(Settings.System.getInt(getActivity()
                    .getContentResolver(), Settings.System.TASK_MANAGER_BAR_THICKNESS, 1)));
            mBarThickness.setSummary(mBarThickness.getEntry());

        } else {
            removePreference(COLORS_CATEGORY);
            removePreference(TASK_MANAGER_FONT_STYLE);
            removePreference(TASK_MANAGER_BAR_THICKNESS);
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
        String hex;
        int intHex;
        int index;

        if (preference == mTaskManager) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.ENABLE_TASK_MANAGER,
                value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mAppColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.TASK_MANAGER_APP_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mMemTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.TASK_MANAGER_MEMORY_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mSliderColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.TASK_MANAGER_SLIDER_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mSliderInactiveColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.TASK_MANAGER_SLIDER_INACTIVE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTaskKillColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.TASK_MANAGER_TASK_KILL_BUTTON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTaskKillAllColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.TASK_MANAGER_TASK_KILL_ALL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTaskTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.TASK_MANAGER_TASK_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTaskTitleTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.TASK_MANAGER_TITLE_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mFontStyle) {
            int val = Integer.parseInt((String) newValue);
            index = mFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.TASK_MANAGER_FONT_STYLE, val);
            mFontStyle.setSummary(mFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mBarThickness) {
            int val = Integer.parseInt((String) newValue);
            index = mBarThickness.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.TASK_MANAGER_BAR_THICKNESS, val);
            mBarThickness.setSummary(mBarThickness.getEntries()[index]);
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

        TaskManagerSettings getOwner() {
            return (TaskManagerSettings) getTargetFragment();
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
                                    Settings.System.ENABLE_TASK_MANAGER,
                                    0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.TASK_MANAGER_APP_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.TASK_MANAGER_MEMORY_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.TASK_MANAGER_SLIDER_COLOR,
                                    TEMASEK_GREEN);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.TASK_MANAGER_SLIDER_INACTIVE_COLOR,
                                    TEMASEK_RED);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.TASK_MANAGER_TASK_KILL_BUTTON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.TASK_MANAGER_TASK_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.TASK_MANAGER_TITLE_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.TASK_MANAGER_TASK_KILL_ALL_COLOR,
                                    TEMASEK_RED);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.TASK_MANAGER_FONT_STYLE,
                                    0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.TASK_MANAGER_BAR_THICKNESS,
                                    1);
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
