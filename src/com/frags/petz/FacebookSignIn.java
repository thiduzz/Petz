package com.frags.petz;

import java.util.UUID;

import model.User;

import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.internal.NativeProtocol;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.main.petz.LoggedActivity;
import com.main.petz.LoginActivity;
import com.main.petz.Prefs;
import com.main.petz.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class FacebookSignIn extends Fragment{
	
	 private static final String DIALOG_CALL_BUNDLE_SAVE_KEY =
	            "com.facebook.UiLifecycleHelper.pendingFacebookDialogCallKey";

	    private final static String ACTIVITY_NULL_MESSAGE = "activity cannot be null";

	    private final Activity activity;
	    private final Session.StatusCallback callback;
	    private final BroadcastReceiver receiver;
	    private final LocalBroadcastManager broadcastManager;
	    // Members related to handling FacebookDialog calls
	    private FacebookDialog.PendingCall pendingFacebookDialogCall;
	    private AppEventsLogger appEventsLogger;
	    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);

    }
	
	public void makeMeRequest(final Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	        if (session != null) {
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {

			@Override
			public void onCompleted(GraphUser user, Response response) {
				 if (session == Session.getActiveSession()) {
		                if (user != null) {
		                    // Set the id for the ProfilePictureView
		                    // view that in turn displays the profile picture.
		                	if(user.getLocation() != null)
		                	{
		                		Prefs.setMyUser(activity.getApplicationContext(), new User(1, user.getFirstName(), user.getLocation().getCity() , user.getLocation().getCity()));	    
		                	}else{
		                		Prefs.setMyUser(activity.getApplicationContext(), new User(1, user.getFirstName(), null , null));	    
		                		}
		            	    Intent intent  = new Intent(activity, LoggedActivity.class);
		            	    intent.putExtra("fb_session", session);
		            	    intent.putExtra("gateway", 1);
		            	    activity.startActivity(intent);
		                }
		            }
		            if (response.getError() != null) {
		                // Handle errors, will do so later.
		            }
				
			}
	    });
	    request.executeAsync();
	        }
	} 


	    /**
	     * Creates a new UiLifecycleHelper.
	     *
	     * @param activity the Activity associated with the helper. If calling from a Fragment,
	     *                 use {@link android.support.v4.app.Fragment#getActivity()}
	     * @param callback the callback for Session status changes, can be null
	     */
	    public FacebookSignIn(Activity activity, Session.StatusCallback callback) {
	        if (activity == null) {
	            throw new IllegalArgumentException(ACTIVITY_NULL_MESSAGE);
	        }
	        this.activity = activity;
	        this.callback = callback;
	        this.receiver = new ActiveSessionBroadcastReceiver();
	        this.broadcastManager = LocalBroadcastManager.getInstance(activity);

	        // Make sure we've loaded default settings if we haven't already.
	        //Settings.loadDefaultsFromMetadataIfNeeded(activity);
	    }

	    /**
	     * To be called from an Activity or Fragment's onCreate method.
	     *
	     * @param savedInstanceState the previously saved state
	     */
	    public void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	setRetainInstance(true);
	        Session session = Session.getActiveSession();
	        if (session == null) {
	            if (savedInstanceState != null) {
	                session = Session.restoreSession(activity, null, callback, savedInstanceState);
	            }
	            if (session == null) {
	                session = new Session(activity);
	            }
	            Session.setActiveSession(session);
	        }
	        if (savedInstanceState != null) {
	            pendingFacebookDialogCall = savedInstanceState.getParcelable(DIALOG_CALL_BUNDLE_SAVE_KEY);
	        }
	    }

	    /**
	     * To be called from an Activity or Fragment's onResume method.
	     */
	    public void onResume() {
	    	super.onResume();
	        Session session = Session.getActiveSession();
	        if (session != null) {
	            if (callback != null) {
	                session.addCallback(callback);
	            }
	            if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())) {
	                session.openForRead(null);
	            }
	        }

	        // add the broadcast receiver
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(Session.ACTION_ACTIVE_SESSION_SET);
	        filter.addAction(Session.ACTION_ACTIVE_SESSION_UNSET);

	        // Add a broadcast receiver to listen to when the active Session
	        // is set or unset, and add/remove our callback as appropriate
	        broadcastManager.registerReceiver(receiver, filter);
	    }

	    /**
	     * To be called from an Activity or Fragment's onActivityResult method.
	     *
	     * @param requestCode the request code
	     * @param resultCode the result code
	     * @param data the result data
	     */
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        onActivityResult(requestCode, resultCode, data, null);
	    }

	    /**
	     * To be called from an Activity or Fragment's onActivityResult method, when the results of a FacebookDialog
	     * call are expected.
	     *
	     * @param requestCode the request code
	     * @param resultCode the result code
	     * @param data the result data
	     * @param dialogCallback the callback for handling FacebookDialog results, can be null
	     */
	    public void onActivityResult(int requestCode, int resultCode, Intent data,
	                FacebookDialog.Callback facebookDialogCallback) {
	        Session session = Session.getActiveSession();
	        if (session != null) {
	            session.onActivityResult(activity, requestCode, resultCode, data);
	        }

	        handleFacebookDialogActivityResult(requestCode, resultCode, data, facebookDialogCallback);
	    }

	    /**
	     * To be called from an Activity or Fragment's onSaveInstanceState method.
	     *
	     * @param outState the bundle to save state in
	     */
	    public void onSaveInstanceState(Bundle outState) {
	        Session.saveSession(Session.getActiveSession(), outState);
	        outState.putParcelable(DIALOG_CALL_BUNDLE_SAVE_KEY, pendingFacebookDialogCall);
	    }

	    /**
	     * To be called from an Activity or Fragment's onPause method.
	     */
	    public void onPause() {
	    	super.onPause();
	        // remove the broadcast receiver
	        broadcastManager.unregisterReceiver(receiver);

	        if (callback != null) {
	            Session session = Session.getActiveSession();
	            if (session != null) {
	                session.removeCallback(callback);
	            }
	        }
	    }

	    /**
	     * To be called from an Activity or Fragment's onStop method.
	     */
	    public void onStop() {
	    	super.onStop();
	        AppEventsLogger.onContextStop();
	    }

	    /**
	     * To be called from an Activity or Fragment's onDestroy method.
	     */
	    public void onDestroy() {
	    	super.onDestroy();
	    }

	    /**
	     * Register that we are expecting results from a call to the Facebook application (e.g., from a native
	     * dialog provided by the Facebook app). Activity results forwarded to onActivityResults will be parsed
	     * and handled if they correspond to this call. Only a single pending FacebookDialog call can be tracked
	     * at a time; attempting to track another one will cancel the first one.
	     * @param appCall an PendingCall object containing the call ID
	     */
	    public void trackPendingDialogCall(FacebookDialog.PendingCall pendingCall) {
	        if (pendingFacebookDialogCall != null) {
	            // If one is already pending, cancel it; we don't allow multiple pending calls.
	            Log.i("Facebook", "Tracking new app call while one is still pending; canceling pending call.");
	            cancelPendingAppCall(null);
	        }
	        pendingFacebookDialogCall = pendingCall;
	    }

	    /**
	     * Retrieves an instance of AppEventsLogger that can be used for the current Session, if any. Different
	     * instances may be returned if the current Session changes, so this value should not be cached for long
	     * periods of time -- always call getAppEventsLogger to get the right logger for the current Session. If
	     * no Session is currently available, this method will return null.
	     *
	     * To ensure delivery of app events across Activity lifecycle events, calling Activities should be sure to
	     * call the onStop method.
	     *
	     * @return an AppEventsLogger to use for logging app events
	     */
	    public AppEventsLogger getAppEventsLogger() {
	        Session session = Session.getActiveSession();
	        if (session == null) {
	            return null;
	        }

	        if (appEventsLogger == null /** ||  !appEventsLogger.isValidForSession(session)**/) {
	            if (appEventsLogger != null) {
	                // Pretend we got stopped so the old logger will persist its results now, in case we get stopped
	                // before events get flushed.
	                AppEventsLogger.onContextStop();
	            }
	            appEventsLogger = AppEventsLogger.newLogger(activity, session);
	        }

	        return appEventsLogger;
	    }

	    /**
	     * The BroadcastReceiver implementation that either adds or removes the callback
	     * from the active Session object as it's SET or UNSET.
	     */
	    private class ActiveSessionBroadcastReceiver extends BroadcastReceiver {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            if (Session.ACTION_ACTIVE_SESSION_SET.equals(intent.getAction())) {
	                Session session = Session.getActiveSession();
	                if (session != null && callback != null) {
	                    session.addCallback(callback);
	                }
	            } else if (Session.ACTION_ACTIVE_SESSION_UNSET.equals(intent.getAction())) {
	                Session session = Session.getActiveSession();
	                if (session != null && callback != null) {
	                    session.removeCallback(callback);
	                }
	            }
	        }
	    }

	    private boolean handleFacebookDialogActivityResult(int requestCode, int resultCode, Intent data,
	            FacebookDialog.Callback facebookDialogCallback) {
	        if (pendingFacebookDialogCall == null || pendingFacebookDialogCall.getRequestCode() != requestCode) {
	            return false;
	        }

	        if (data == null) {
	            // We understand the request code, but have no Intent. This can happen if the called Activity crashes
	            // before it can be started; we treat this as a cancellation because we have no other information.
	            cancelPendingAppCall(facebookDialogCallback);
	            return true;
	        }

	        String callIdString = data.getStringExtra(NativeProtocol.EXTRA_PROTOCOL_CALL_ID);
	        UUID callId = null;
	        if (callIdString != null) {
	            try {
	                callId = UUID.fromString(callIdString);
	            } catch (IllegalArgumentException exception) {
	            }
	        }

	        // Was this result for the call we are waiting on?
	        if (callId != null && pendingFacebookDialogCall.getCallId().equals(callId)) {
	            // Yes, we can handle it normally.
	            FacebookDialog.handleActivityResult(activity, pendingFacebookDialogCall, requestCode, data,
	                    facebookDialogCallback);
	        } else {
	            // No, send a cancellation error to the pending call and ignore the result, because we
	            // don't know what to do with it.
	            cancelPendingAppCall(facebookDialogCallback);
	        }

	        pendingFacebookDialogCall = null;
	        return true;
	    }

	    private void cancelPendingAppCall(FacebookDialog.Callback facebookDialogCallback) {
	        if (facebookDialogCallback != null) {
	            Intent pendingIntent = pendingFacebookDialogCall.getRequestIntent();

	            Intent cancelIntent = new Intent();
	            cancelIntent.putExtra(NativeProtocol.EXTRA_PROTOCOL_CALL_ID,
	                    pendingIntent.getStringExtra(NativeProtocol.EXTRA_PROTOCOL_CALL_ID));
	            cancelIntent.putExtra(NativeProtocol.EXTRA_PROTOCOL_ACTION,
	                    pendingIntent.getStringExtra(NativeProtocol.EXTRA_PROTOCOL_ACTION));
	            cancelIntent.putExtra(NativeProtocol.EXTRA_PROTOCOL_VERSION,
	                    pendingIntent.getIntExtra(NativeProtocol.EXTRA_PROTOCOL_VERSION, 0));
	            cancelIntent.putExtra(NativeProtocol.STATUS_ERROR_TYPE, NativeProtocol.ERROR_UNKNOWN_ERROR);

	            FacebookDialog.handleActivityResult(activity, pendingFacebookDialogCall,
	                    pendingFacebookDialogCall.getRequestCode(), cancelIntent, facebookDialogCallback);
	        }
	        pendingFacebookDialogCall = null;
	    }
	    
	    public boolean signOutFromFacebook()
	    {
	        Session session = Session.getActiveSession();
	        if (session != null) {

	            if (!session.isClosed()) {
	            	Prefs.clearMyPreferences(activity.getApplicationContext());
	                session.closeAndClearTokenInformation();
	            	SignInButton bt = (SignInButton) activity.findViewById(R.id.sign_in_button);
	            	bt.setVisibility(SignInButton.VISIBLE);
	            	LoginButton bt2 = (LoginButton) activity.findViewById(R.id.sign_in_button_fb);
	            	bt2.setVisibility(LoginButton.VISIBLE);
		        	TextView or = (TextView) activity.findViewById(R.id.textor);
		        	or.setText(activity.getResources().getString(R.string.logintextor));
             	    Toast.makeText(activity, "User disconnected!", Toast.LENGTH_LONG).show();
	                //clear your preferences if saved
	                return true;
	            }
	            return false;
	        } else {

	            session = new Session(getActivity());
	            Session.setActiveSession(session);
            	Prefs.clearMyPreferences(activity.getApplicationContext());
	            session.closeAndClearTokenInformation();
            	SignInButton bt = (SignInButton) activity.findViewById(R.id.sign_in_button);
            	bt.setVisibility(SignInButton.VISIBLE);
            	LoginButton bt2 = (LoginButton) activity.findViewById(R.id.sign_in_button_fb);
            	bt2.setVisibility(LoginButton.VISIBLE);

	        	TextView or = (TextView) activity.findViewById(R.id.textor);
	        	or.setText(activity.getResources().getString(R.string.logintextor));
         	    Toast.makeText(activity, "User disconnected!", Toast.LENGTH_LONG).show();
	                //clear your preferences if saved
	            return true;
	        }
	    }
}
