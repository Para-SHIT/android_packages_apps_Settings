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

package com.android.settings.temasek;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.temasek.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class Greeting extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String KEY_STATUS_BAR_GREETING = "status_bar_greeting";
    private static final String KEY_STATUS_BAR_GREETING_COLOR = "status_bar_greeting_color";
    private static final String KEY_STATUS_BAR_GREETING_TIMEOUT = "status_bar_greeting_timeout";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mStatusBarGreeting;
    private ColorPickerPreference mStatusBarGreetingColor;
    private SeekBarPreference mStatusBarGreetingTimeout;

    private ContentResolver mResolver;

    private String mCustomGreetingText = "";

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

        addPreferencesFromResource(R.xml.status_bar_greeting);
        mResolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();

        // Greeting
        mStatusBarGreeting =
            (SwitchPreference) prefSet.findPreference(KEY_STATUS_BAR_GREETING);
        mCustomGreetingText = Settings.System.getString(mResolver,
            Settings.System.STATUS_BAR_GREETING);
        boolean greeting = mCustomGreetingText != null
            && !TextUtils.isEmpty(mCustomGreetingText);
        mStatusBarGreeting.setChecked(greeting);

        mStatusBarGreetingColor =
                (ColorPickerPreference) findPreference(KEY_STATUS_BAR_GREETING_COLOR);
        int intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_COLOR,
                0xffffffff); 
        mStatusBarGreetingColor.setNewPreviewColor(intColor);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mStatusBarGreetingColor.setSummary(hexColor);
        mStatusBarGreetingColor.setOnPreferenceChangeListener(this);
        mStatusBarGreetingColor.setAlphaSliderEnabled(true);

        mStatusBarGreetingTimeout =
            (SeekBarPreference) prefSet.findPreference(KEY_STATUS_BAR_GREETING_TIMEOUT);
        int statusBarGreetingTimeout = Settings.System.getInt(mResolver,
            Settings.System.STATUS_BAR_GREETING_TIMEOUT, 1000);
        if (statusBarGreetingTimeout < 100) {
            statusBarGreetingTimeout = 100;
        }
        mStatusBarGreetingTimeout.setValue(statusBarGreetingTimeout / 1);
        mStatusBarGreetingTimeout.setOnPreferenceChangeListener(this);

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
        if (preference == mStatusBarGreetingColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_GREETING_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mStatusBarGreetingTimeout) {
            int timeout = (Integer) newValue;
            if (timeout < 100) {
              timeout = 100;
            }
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_TIMEOUT,
                timeout * 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mStatusBarGreeting) {
            boolean enabled = mStatusBarGreeting.isChecked();
            if (enabled) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle(R.string.status_bar_greeting_title);
                alert.setMessage(R.string.status_bar_greeting_dialog);

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                input.setText(mCustomGreetingText != null ? mCustomGreetingText :
                        getResources().getString(R.string.status_bar_greeting_main));
                alert.setView(input);
                alert.setPositiveButton(getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = ((Spannable) input.getText()).toString();
                            Settings.System.putString(getActivity().getContentResolver(),
                                Settings.System.STATUS_BAR_GREETING, value);
                            updateCheckState(value);
                        }
                });
                alert.setNegativeButton(getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                });

                alert.show();
            } else {
                Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_GREETING, "");
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void updateCheckState(String value) {
        if (value == null || TextUtils.isEmpty(value)) {
            mStatusBarGreeting.setChecked(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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

        Greeting getOwner() {
            return (Greeting) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_GREETING_COLOR,
                                    0xffffffff);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_GREETING_TIMEOUT, 1000);
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
