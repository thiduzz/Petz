package com.frags.petz;

import java.io.InputStream;

import model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.model.people.Person;
import com.main.petz.LoggedActivity;
import com.main.petz.LoginActivity;
import com.main.petz.Prefs;
import com.main.petz.R;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class GooglePlusSignIn extends Fragment implements  OnConnectionFailedListener{
    private GoogleApiClient mGoogleApiClient;
    public boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;
    private ConnectionCallbacks callback;
    private Activity activity;
	public static boolean mLogginOut = false;
	public static boolean mSignInClicked;
    private static final int PROFILE_PIC_SIZE = 400;
    
    public GooglePlusSignIn(Activity act, ConnectionCallbacks callback) {
        this.activity = act;
        this.callback = callback;
	}
    
    public GooglePlusSignIn()
    {
    	
    }
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(this.activity);
    }
    
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
        .addConnectionCallbacks(callback)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API, null)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .addScope(Plus.SCOPE_PLUS_PROFILE)
        .build();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	return null;

    }

    
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
 
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
	
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {	 
	    if (!mIntentInProgress) {
	        // Store the ConnectionResult for later usage
	        mConnectionResult = result;
	        if (mSignInClicked) {
	            // The user has already clicked 'sign-in' so we attempt to resolve all
	            // errors until the user is signed in, or they cancel.
	            resolveSignInError();
	          }
	    }
	    
	}
	
	public GoogleApiClient getSession()
	{
		return mGoogleApiClient;
	}
	
	public boolean isConnected(){
	return mGoogleApiClient.isConnected();
	}
	
	public void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
		    try {
		      mIntentInProgress = true;
		      activity.startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
		          LoginActivity.RC_SIGN_IN, null, 0, 0, 0);
		    } catch (SendIntentException e) {
		      // The intent was canceled before it was sent.  Return to the default
		      // state and attempt to connect to get an updated ConnectionResult.
		      mIntentInProgress = false;
		      mGoogleApiClient.connect();
		    }
		  }
	}
	
    public boolean signOutFromGplus() {
    	if (mGoogleApiClient.isConnected()) {
        	Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
             				Prefs.clearMyPreferences(activity.getApplicationContext());
                 	        mGoogleApiClient.disconnect();
                 	        mGoogleApiClient.connect();
                     	    Toast.makeText(activity, "User disconnected!", Toast.LENGTH_LONG).show();
                        }
 
                    });
    	       
            return true;
            }
	    Toast.makeText(activity, "Error!", Toast.LENGTH_LONG).show();
    	return false;
    }
    
	public void makeMeRequest()
	{
		if(mGoogleApiClient.isConnected())
		{
		Person p = getCurrentUserInfo();
	    if(p!=null)
	    {	    
		    if(p.getCurrentLocation() != null)
		    {
		    	Prefs.setMyUser(activity.getApplicationContext(), new User(0, p.getName().getGivenName(), p.getCurrentLocation() , p.getCurrentLocation()));	    
		    }
		    else{
		    	 Prefs.setMyUser(activity.getApplicationContext(), new User(0, p.getName().getGivenName(), p.getPlacesLived().get(0).getValue() , p.getPlacesLived().get(0).getValue()));	    
		    }	
	    }
		}
	}
	
	public Person getCurrentUserInfo()
	{
		mGoogleApiClient.connect();
		if(mGoogleApiClient.isConnected())
		{
		    // Get user's information
	        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
				
				@Override
				public void onResult(LoadPeopleResult arg0) {
					
				}
			});
		    return getProfileInformation();
		}
		return null;
	}
	
	private Person getProfileInformation() {
	    try {
	        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
	            Person currentPerson = Plus.PeopleApi
	                    .getCurrentPerson(mGoogleApiClient);
	            String personPhotoUrl = currentPerson.getImage().getUrl();
	            personPhotoUrl = personPhotoUrl.substring(0,
	                    personPhotoUrl.length() - 2)
	                    + PROFILE_PIC_SIZE;
	            return currentPerson;
	        } else {
	            Toast.makeText(getActivity().getApplicationContext(),
	                    "Person information is null", Toast.LENGTH_LONG).show();
	            return null;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;
	 
	    public LoadProfileImage(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }
	 
	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }
	 
	    protected void onPostExecute(Bitmap result) {
	        bmImage.setImageBitmap(result);
	    }
	}
	
}