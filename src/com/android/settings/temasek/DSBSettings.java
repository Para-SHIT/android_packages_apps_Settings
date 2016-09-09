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

import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.content.res.Resources;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.CheckBoxPreference;
import android.provider.Settings;
import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.ArrayList;
import java.util.List;

public class DSBSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener, Indexable {

private static final String KEY_DYNAMIC_STATUS_BAR = "dynamic_status_bar" ;
private static final String KEY_DYNAMIC_NAVIGATION_BAR = "dynamic_navigation_bar" ;
private static final String KEY_DYNAMIC_SYSTEM_BARS_GRADIENT = "dynamic_system_bars_gradient" ;
private static final String KEY_DYNAMIC_STATUS_BAR_FILTER = "dynamic_status_bar_filter" ;
private static final String KEY_DYNAMIC_ICON_TINT = "dynamic_icon_tint" ;
	
private CheckBoxPreference mDynamicStatusBar;
private CheckBoxPreference mDynamicNavigationBar;
private CheckBoxPreference mDynamicSystemBarsGradient;
private CheckBoxPreference mDynamicStatusBarFilter;
private CheckBoxPreference mDynamicIconTint;

private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.dsb_settings);

        	mDynamicStatusBar = (CheckBoxPreference) findPreference( KEY_DYNAMIC_STATUS_BAR );
		     mDynamicStatusBar . setPersistent( false );
		
			      mDynamicNavigationBar = (CheckBoxPreference) findPreference( KEY_DYNAMIC_NAVIGATION_BAR );
		      mDynamicNavigationBar . setPersistent( false );

                      mDynamicIconTint = (CheckBoxPreference) findPreference( KEY_DYNAMIC_ICON_TINT );
                      mDynamicIconTint . setPersistent(false);
		
		       mDynamicSystemBarsGradient =
		              (CheckBoxPreference) findPreference( KEY_DYNAMIC_SYSTEM_BARS_GRADIENT );
		      mDynamicSystemBarsGradient . setPersistent( false );
		
			     mDynamicStatusBarFilter =
			                (CheckBoxPreference) findPreference( KEY_DYNAMIC_STATUS_BAR_FILTER );
		       mDynamicStatusBarFilter . setPersistent( false );
		
			
		updateDynamicSystemBarsCheckboxes();
		     mResolver=getActivity().getContentResolver();
    }

    protected void removePreference(String key) {
        Preference pref = findPreference(key);
        if (pref != null) {
            getPreferenceScreen().removePreference(pref);
        }
    }
	 private void updateDynamicSystemBarsCheckboxes () {
		final Resources res = getResources();
		
		 final boolean isStatusBarDynamic = Settings .System. getInt(getActivity().getContentResolver(),
																	                "DYNAMIC_STATUS_BAR_STATE" , 0 ) == 1 ;
		
		 final boolean hasNavigationBar = res . getDimensionPixelSize(res .getIdentifier(
																		  "navigation_bar_height" , "dimen" , "android" )) > 0 ;
		final boolean isNavigationBarDynamic = hasNavigationBar && Settings . System. getInt(
			              getActivity().getContentResolver(), "DYNAMIC_NAVIGATION_BAR_STATE" , 0) == 1 ;

                 final boolean isStatusBarColor = Settings .System. getInt(getActivity().getContentResolver(),
																	                "DYNAMIC_ICON_TINT_STATE" , 0 ) == 1 ;
		
		 final boolean isAnyBarDynamic = isStatusBarDynamic || isNavigationBarDynamic;
		
			       mDynamicStatusBar . setChecked(isStatusBarDynamic);
		
			       mDynamicNavigationBar . setEnabled(hasNavigationBar);
		        mDynamicNavigationBar . setChecked(isNavigationBarDynamic);

                               mDynamicIconTint . setChecked(isStatusBarColor);
		
		final boolean areSystemBarsGradient = isAnyBarDynamic && Settings . System.getInt(
			               getActivity().getContentResolver(), "DYNAMIC_SYSTEM_BARS_GRADIENT_STATE" , 0) == 1 ;
		 final boolean isStatusBarFilter = isStatusBarDynamic && Settings . System. getInt(
			               getActivity().getContentResolver(), "DYNAMIC_STATUS_BAR_FILTER_STATE" , 0 ) == 1;
		
			      mDynamicSystemBarsGradient . setEnabled(isAnyBarDynamic &&
															               (areSystemBarsGradient || ! isStatusBarFilter));
		       mDynamicSystemBarsGradient . setChecked(areSystemBarsGradient);
		
			      mDynamicStatusBarFilter . setEnabled(isStatusBarDynamic &&
														                 (isStatusBarFilter || ! areSystemBarsGradient));
		        mDynamicStatusBarFilter . setChecked(isStatusBarFilter);
				
	 }

    @Override
	public boolean onPreferenceChange(Preference p1, Object p2)
	{
		return false;
	}
	
    @Override
	 public boolean onPreferenceTreeClick(PreferenceScreen p1, Preference preference)
	 {
		
	  if (preference == mDynamicStatusBar) {
		            Settings . System. putInt(getActivity().getContentResolver(),
											                    "DYNAMIC_STATUS_BAR_STATE" ,
											                     mDynamicStatusBar . isChecked() ? 1 : 0 );
		           updateDynamicSystemBarsCheckboxes();
	       }
                   else if (preference == mDynamicNavigationBar) {
		 final Resources res = getResources();
		          Settings . System. putInt(getActivity().getContentResolver(),"DYNAMIC_NAVIGATION_BAR_STATE" ,
											                  mDynamicNavigationBar . isChecked() && res . getDimensionPixelSize(res . getIdentifier( "navigation_bar_height" , "dimen" , "android" )) > 0 ? 1 : 0 );
		            updateDynamicSystemBarsCheckboxes();
	       } else if (preference == mDynamicIconTint) {
		 Settings . System. putInt(getActivity().getContentResolver(),
											                    "DYNAMIC_ICON_TINT_STATE" ,
											                     mDynamicIconTint . isChecked() ? 1 : 0 );
		           updateDynamicSystemBarsCheckboxes();
	       } else if (preference == mDynamicSystemBarsGradient) {
		final boolean enableGradient = mDynamicSystemBarsGradient .isChecked();
		            Settings . System. putInt(getActivity().getContentResolver(),"DYNAMIC_SYSTEM_BARS_GRADIENT_STATE" , enableGradient ? 1 : 0);
		 if (enableGradient) {
			              Settings . System. putInt(getActivity().getContentResolver(),"DYNAMIC_STATUS_BAR_FILTER_STATE" , 0 );
		          }
		           updateDynamicSystemBarsCheckboxes();
	      } else if (preference == mDynamicStatusBarFilter) {
		 final boolean enableFilter = mDynamicStatusBarFilter . isChecked();
		          Settings . System. putInt(getActivity().getContentResolver(),"DYNAMIC_STATUS_BAR_FILTER_STATE",enableFilter ? 1 : 0);
		 if (enableFilter) {
		               Settings . System. putInt(getActivity().getContentResolver(),"DYNAMIC_SYSTEM_BARS_GRADIENT_STATE" , 0 );
		         }
		          updateDynamicSystemBarsCheckboxes();
	}

return super.onPreferenceTreeClick(p1,preference);
    }

         public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                             boolean enabled) {
                     ArrayList<SearchIndexableResource> result =
                             new ArrayList<SearchIndexableResource>();
 
                     SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.dsb_settings;
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
