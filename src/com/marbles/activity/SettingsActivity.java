package com.marbles.activity;



import com.marblesheaven.R;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub 
		super.onCreate(savedInstanceState); 
		addPreferencesFromResource(R.xml.preferences);
	}

	

}
