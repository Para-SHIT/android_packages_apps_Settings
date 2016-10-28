package com.android.settings.temasek.fragments;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class OtherAnimations extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "OtherAnimations";

    private static final String PREF_TILE_ANIM_STYLE = "qs_tile_animation_style";
    private static final String PREF_TILE_ANIM_DURATION = "qs_tile_animation_duration";
    private static final String PREF_TILE_ANIM_INTERPOLATOR = "qs_tile_animation_interpolator";
    private static final String POWER_MENU_ANIMATIONS = "power_menu_animations";
    private static final String RECENTS_ENTER_ANIMATIONS = "recents_enter_animations";
    private static final String QS_TASK_ANIMATION = "qs_task_animation";

    private ListPreference mTileAnimation;
    private ListPreference mTileAnimationDuration;
    private ListPreference mTileAnimationInterpolator;
    private ListPreference mPowerMenuAnimations;
    private ListPreference mRecentsEnterAnimations;
    private ListPreference mTaskManagerAnimation;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.other_animations);
        ContentResolver resolver = getActivity().getContentResolver();

        mPowerMenuAnimations = 
                (ListPreference) findPreference(POWER_MENU_ANIMATIONS);
        int powerMenuAnimations = Settings.System.getIntForUser(resolver,
                Settings.System.POWER_MENU_ANIMATIONS, 0,
                UserHandle.USER_CURRENT);
        mPowerMenuAnimations.setValue(String.valueOf(powerMenuAnimations));
        mPowerMenuAnimations.setSummary(mPowerMenuAnimations.getEntry());
        mPowerMenuAnimations.setOnPreferenceChangeListener(this);

        mTileAnimation =
                (ListPreference) findPreference(PREF_TILE_ANIM_STYLE);
        int tileAnimation = Settings.System.getIntForUser(resolver,
                Settings.System.ANIM_TILE_STYLE, 0,
                UserHandle.USER_CURRENT);
        mTileAnimation.setValue(String.valueOf(tileAnimation));
        mTileAnimation.setSummary(mTileAnimation.getEntry());
        mTileAnimation.setOnPreferenceChangeListener(this);

        mTileAnimationDuration =
                (ListPreference) findPreference(PREF_TILE_ANIM_DURATION);
        int tileAnimationDuration = Settings.System.getIntForUser(resolver,
                Settings.System.ANIM_TILE_DURATION, 1500,
                UserHandle.USER_CURRENT);
        mTileAnimationDuration.setValue(String.valueOf(tileAnimationDuration));
        mTileAnimationDuration.setSummary(mTileAnimationDuration.getEntry());
        mTileAnimationDuration.setOnPreferenceChangeListener(this);

        mTileAnimationInterpolator =
                (ListPreference) findPreference(PREF_TILE_ANIM_INTERPOLATOR);
        int tileAnimationInterpolator = Settings.System.getIntForUser(resolver,
                Settings.System.ANIM_TILE_INTERPOLATOR, 0,
                UserHandle.USER_CURRENT);
        mTileAnimationInterpolator.setValue(String.valueOf(tileAnimationInterpolator));
        mTileAnimationInterpolator.setSummary(mTileAnimationInterpolator.getEntry());
        mTileAnimationInterpolator.setOnPreferenceChangeListener(this);

        mRecentsEnterAnimations =
                (ListPreference) findPreference(RECENTS_ENTER_ANIMATIONS);
        int recentsEnterAnimations = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_ENTER_ANIMATIONS, 0,
                UserHandle.USER_CURRENT);
        mRecentsEnterAnimations.setValue(String.valueOf(recentsEnterAnimations));
        mRecentsEnterAnimations.setSummary(mRecentsEnterAnimations.getEntry());
        mRecentsEnterAnimations.setOnPreferenceChangeListener(this);

        mTaskManagerAnimation =
                (ListPreference) findPreference(QS_TASK_ANIMATION);
        int taskManagerAnimation = Settings.System.getIntForUser(resolver,
                Settings.System.QS_TASK_ANIMATION, 0,
                UserHandle.USER_CURRENT);
        mTaskManagerAnimation.setValue(String.valueOf(taskManagerAnimation));
        mTaskManagerAnimation.setSummary(mTaskManagerAnimation.getEntry());
        mTaskManagerAnimation.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        int index;
        if (preference == mPowerMenuAnimations) {
            int powerMenuAnimations = Integer.valueOf((String) objValue);
            index = mPowerMenuAnimations.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.POWER_MENU_ANIMATIONS, powerMenuAnimations,
                    UserHandle.USER_CURRENT);
            mPowerMenuAnimations.setSummary(mPowerMenuAnimations.getEntries()[index]);
            return true;
        } else if (preference == mTileAnimation) {
            int tileAnimation = Integer.valueOf((String) objValue);
            index = mTileAnimation.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.ANIM_TILE_STYLE, tileAnimation,
                    UserHandle.USER_CURRENT);
            mTileAnimation.setSummary(mTileAnimation.getEntries()[index]);
            tileAnimationSettingsDisabler(tileAnimation);
            return true;
        } else if (preference == mTileAnimationDuration) {
            int tileAnimationDuration = Integer.valueOf((String) objValue);
            index = mTileAnimationDuration.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.ANIM_TILE_DURATION, tileAnimationDuration,
                    UserHandle.USER_CURRENT);
            mTileAnimationDuration.setSummary(mTileAnimationDuration.getEntries()[index]);
            return true;
        } else if (preference == mTileAnimationInterpolator) {
            int tileAnimationInterpolator = Integer.valueOf((String) objValue);
            index = mTileAnimationInterpolator.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.ANIM_TILE_INTERPOLATOR, tileAnimationInterpolator,
                    UserHandle.USER_CURRENT);
            mTileAnimationInterpolator.setSummary(mTileAnimationInterpolator.getEntries()[index]);
            return true;
        } else if (preference == mRecentsEnterAnimations) {
            int recentsEnterAnimations = Integer.valueOf((String) objValue);
            index = mRecentsEnterAnimations.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.RECENTS_ENTER_ANIMATIONS, recentsEnterAnimations,
                    UserHandle.USER_CURRENT);
            mRecentsEnterAnimations.setSummary(mRecentsEnterAnimations.getEntries()[index]);
            return true;
        } else if (preference == mTaskManagerAnimation) {
            int taskManagerAnimation = Integer.valueOf((String) objValue);
            index = mTaskManagerAnimation.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_TASK_ANIMATION, taskManagerAnimation,
                    UserHandle.USER_CURRENT);
            mTaskManagerAnimation.setSummary(mTaskManagerAnimation.getEntries()[index]);
            return true;
        }
        return false;
    }

    private void tileAnimationSettingsDisabler(int tileAnimation) {
         if (tileAnimation == 0) {
             mTileAnimationDuration.setEnabled(false);
             mTileAnimationInterpolator.setEnabled(false);
         } else if (tileAnimation == 1) {
             mTileAnimationDuration.setEnabled(true);
             mTileAnimationInterpolator.setEnabled(true);
         } else {
             mTileAnimationDuration.setEnabled(true);
             mTileAnimationInterpolator.setEnabled(true);
         }
    }
}
