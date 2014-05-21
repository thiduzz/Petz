package com.frags.petz;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

import com.main.petz.Prefs;
import com.main.petz.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FinderFragment extends Fragment {

	public FinderFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		View rootView = inflater.inflate(R.layout.fragment_finder, container,
				false);
		TextView t = (TextView) rootView.findViewById(R.id.teste);
		if(Prefs.getMyUser(getActivity()) !=null)
		{
		t.setText("Source " + String.valueOf(Prefs.getMyUser(getActivity()).getUser_source()) +" Name "+ Prefs.getMyUser(getActivity()).getUser_name());
		}
		// getActivity().setTitle(title);
		return rootView;
	}
	
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}
	
	
}