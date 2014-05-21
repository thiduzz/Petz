package com.main.petz;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import model.User;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.widget.LoginButton;
import com.frags.petz.FacebookSignIn;
import com.frags.petz.GooglePlusSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.test.suitebuilder.TestSuiteBuilder.FailedToCreateTests;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    public static final int RC_SIGN_IN = 1010;
    public static final int FB_SIGN_IN =  64206;
    public static final int RC_SIGN_OUT = 1111;
    public static final int RC_GET = 1112;
    private static final String TAG = "LoginActivity";
    static GooglePlusSignIn google;
    static FacebookSignIn uiHelperFacebook;
    public boolean isResumed;
    FragmentManager fragmentManager = getFragmentManager();  
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);
        	setContentView(R.layout.activity_login);
        	if (getIntent().getBooleanExtra("EXIT",false)) {
        	    this.finish();
        	    clearMem();
        	    return;
        	}
        	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        	google = new GooglePlusSignIn(this, callbackg);
        	fragmentTransaction.add(google, "google"); 
        	google.onCreate(savedInstanceState);    
        	SignInButton bt = (SignInButton) this.findViewById(R.id.sign_in_button);
        	bt.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				if(!google.getSession().isConnecting()) {
    					google.mSignInClicked = true;
    					google.resolveSignInError();
    				}
    			}
    		});
            uiHelperFacebook = new FacebookSignIn(this, callback);
            uiHelperFacebook.onCreate(savedInstanceState);
        	fragmentTransaction.add(uiHelperFacebook, "facebook"); 
        	fragmentTransaction.commit();
        	Session s = Session.getActiveSession();
        	if(s != null)
        	{
        		if(s.isOpened())
            	{
                    Log.i(TAG, "Loggin in Facebook...");
                	((SignInButton) this.findViewById(R.id.sign_in_button)).setVisibility(SignInButton.INVISIBLE);
                	((LoginButton) this.findViewById(R.id.sign_in_button_fb)).setVisibility(LoginButton.INVISIBLE);
                	((TextView) this.findViewById(R.id.textor)).setText(this.getResources().getString(R.string.logintextlogging));
                    uiHelperFacebook.makeMeRequest(s);
            	}
        	}
    }
 
    public void clearMem() {
        ActivityManager amgr = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = amgr.getRunningAppProcesses();
        if (list != null){
            for (int i = 0; i < list.size(); i++) {
                ActivityManager.RunningAppProcessInfo apinfo = list.get(i);

                String[] pkgList = apinfo.pkgList;
                if ((apinfo.processName.equals("com.main.petz"))) {
                        amgr.killBackgroundProcesses(apinfo.processName);
                }
            }
        }
    }
    
	@Override
	protected void onActivityResult(int requestCode, int responseCode,
	        Intent intent) {
		if (requestCode == FB_SIGN_IN && responseCode == RESULT_OK)
	    {
	    super.onActivityResult(requestCode, responseCode, intent);
	    uiHelperFacebook.onActivityResult(requestCode, responseCode, intent);
	    }else{
	    	  if (requestCode == RC_SIGN_IN) {
	    		    if (responseCode != RESULT_OK) {
	    		      google.mSignInClicked = false;
	    		    }
	    		    if(responseCode == RESULT_OK){
	    		    	google.mIntentInProgress = false;
		    		    	google.getSession().connect();
	    		    }
	    		    
	    		    
	    	  }
	    }
	}

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Loggin in Facebook...");
        	SignInButton bt = (SignInButton) this.findViewById(R.id.sign_in_button);
        	bt.setVisibility(SignInButton.INVISIBLE);
        	LoginButton bt2 = (LoginButton) this.findViewById(R.id.sign_in_button_fb);
        	bt2.setVisibility(LoginButton.INVISIBLE);
        	TextView or = (TextView) this.findViewById(R.id.textor);
        	or.setText(this.getResources().getString(R.string.logintextlogging));
            uiHelperFacebook.makeMeRequest(session);
        } else if (state.isClosed()) {
        	SignInButton bt = (SignInButton) this.findViewById(R.id.sign_in_button);
        	bt.setVisibility(SignInButton.VISIBLE);
        	LoginButton bt2 = (LoginButton) this.findViewById(R.id.sign_in_button_fb);
        	bt2.setVisibility(LoginButton.VISIBLE);
        	TextView or = (TextView) this.findViewById(R.id.textor);
        	or.setText(this.getResources().getString(R.string.logintextor));
            Log.e(TAG, "Logged out Facebook...");
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        uiHelperFacebook.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelperFacebook.onPause();
    }
    
@Override
public void onDestroy() {
    super.onDestroy();
}
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    if(uiHelperFacebook != null)
	    {
	    	uiHelperFacebook.onSaveInstanceState(outState);
	    }
	    if(google != null)
	    {
	    	
	    }
	}
	
    private ConnectionCallbacks callbackg = new ConnectionCallbacks() {
		
		@Override
		public void onConnectionSuspended(int arg0) {
			google.getSession().connect();
		}
		
		@Override
		public void onConnected(Bundle arg0) {
			if(google.mLogginOut)
			{
				google.mLogginOut = false;
				google.signOutFromGplus();
	        	SignInButton bt = (SignInButton) LoginActivity.this.findViewById(R.id.sign_in_button);
	        	bt.setVisibility(SignInButton.VISIBLE);
	        	LoginButton bt2 = (LoginButton) LoginActivity.this.findViewById(R.id.sign_in_button_fb);
	        	bt2.setVisibility(LoginButton.VISIBLE);
	        	TextView or = (TextView) LoginActivity.this.findViewById(R.id.textor);
	        	or.setText(LoginActivity.this.getResources().getString(R.string.logintextor));
			}else{
				google.mSignInClicked = false;
				Log.e(TAG, "Loggin in Google+...");
	        	SignInButton bt = (SignInButton) LoginActivity.this.findViewById(R.id.sign_in_button);
	        	bt.setVisibility(SignInButton.INVISIBLE);
	        	LoginButton bt2 = (LoginButton) LoginActivity.this.findViewById(R.id.sign_in_button_fb);
	        	bt2.setVisibility(LoginButton.INVISIBLE);

	        	TextView or = (TextView)  LoginActivity.this.findViewById(R.id.textor);
	        	or.setText(LoginActivity.this.getResources().getString(R.string.logintextlogging));
	    	    Intent intent  = new Intent(LoginActivity.this, LoggedActivity.class);
	    	    intent.putExtra("gateway", 0);
	    	    startActivity(intent);
			}
			
		}
	};
	


}
