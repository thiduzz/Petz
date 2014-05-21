package com.data.petz;



import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.petz.R;

public class VolleyErrorHelper {
    /**
    * Returns appropriate message which is to be displayed to the user 
    * against the specified error object.
    * 
    * @param error
    * @param context
    * @return
    */
 public static String getMessage(Object error, Context context) {
     if (error instanceof TimeoutError) {
         return context.getResources().getString(R.string.volley_timeout_error);
     }
     else if (isServerProblem(error)) {
         return handleServerError(error, context);
     }else if(isParsingProblem(error)){
    	 return context.getResources().getString(R.string.volley_parse_error);
     }else if(error instanceof NoConnectionError){
    	if( ((NoConnectionError) error).getCause() instanceof UnknownHostException)
    	{
    		 return context.getResources().getString(R.string.volley_unknownhost_error);
    	}
        return context.getResources().getString(R.string.volley_network_unavailable_title);
     }
     else if (isNetworkProblem(error)) {
         return context.getResources().getString(R.string.volley_no_internet);
     }
     return context.getResources().getString(R.string.volley_generic_error);
 }
 private static boolean isParsingProblem(Object error) {
	 return (error instanceof ParseError);
	}
/**
 * Determines whether the error is related to network
 * @param error
 * @return
 */
 private static boolean isNetworkProblem(Object error) {
	 return (error instanceof NetworkError);
 }
 /**
 * Determines whether the error is related to server
 * @param error
 * @return
 */
 private static boolean isServerProblem(Object error) {
     return (error instanceof ServerError) || (error instanceof AuthFailureError);
 }
 /**
 * Handles the server error, tries to determine whether to show a stock message or to 
 * show a message retrieved from the server.
 * 
 * @param err
 * @param context
 * @return
 */
 private static String handleServerError(Object err, Context context) {
     VolleyError error = (VolleyError) err;
 
     NetworkResponse response = error.networkResponse;
 
     if (response != null) {
         switch (response.statusCode) {
           case 404: return context.getResources().getString(R.string.volley_generic_error);
           case 403: return "Forbidden";
           case 422: return context.getResources().getString(R.string.volley_generic_error);
           case 401:
               try {
                   // server might return error like this { "error": "Some error occured" }
                   // Use "Gson" to parse the result
                   HashMap<String, String> result = new Gson().fromJson(new String(response.data),
                           new TypeToken<Map<String, String>>() {
                           }.getType());

                   if (result != null && result.containsKey("error")) {
                       return result.get("error");
                   }

               } catch (Exception e) {
                   e.printStackTrace();
               }
               // invalid request
               return error.getMessage();
           case 500: return context.getResources().getString(R.string.volley_internalserver_error);
           default:
               return context.getResources().getString(R.string.volley_generic_server_down);
           }
     }
       return context.getResources().getString(R.string.volley_generic_error);
 }
}