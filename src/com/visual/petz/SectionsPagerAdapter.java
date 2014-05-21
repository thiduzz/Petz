package com.visual.petz;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.frags.petz.FinderFragment;
import com.frags.petz.SocialFragment;
import com.main.petz.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	Activity _activity;
	
	public SectionsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public SectionsPagerAdapter(Activity _activity,
			FragmentManager fm) {
		super(fm);
		this._activity = _activity;
	}
	
	@Override
	public Fragment getItem(int position) {
		//return PlaceholderFragment.newInstance(position + 1);
	    Fragment fragment =null;
	    switch (position) {
	        case 0:
	            fragment = Fragment.instantiate(_activity, FinderFragment.class.getName());
	            break;
	        case 1:
	            fragment = Fragment.instantiate(_activity, SocialFragment.class.getName());
	            break;            
	            }
	    return fragment; 
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return _activity.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return _activity.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return _activity.getString(R.string.title_section3).toUpperCase(l);
		}
		return null;
	}
}

/**
 * A placeholder fragment containing a simple view.

public static class PlaceholderFragment extends Fragment {
	private static final String ARG_SECTION_NUMBER = "section_number";
	public static PlaceholderFragment newInstance(int sectionNumber) {
		PlaceholderFragment fragment = new PlaceholderFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public PlaceholderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_logged,
				container, false);
		TextView textView = (TextView) rootView
				.findViewById(R.id.section_label);
		textView.setText(Integer.toString(getArguments().getInt(
				ARG_SECTION_NUMBER)));
		return rootView;
	}
	 
}
 */