package com.main.petz;

import model.User;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Prefs {

    private static String LOGGED_USER = "petz_user";
	
    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("petzprefs", Context.MODE_PRIVATE);
    }
    
    public static boolean clearMyPreferences(Context context)
    {
        getPrefs(context).edit().clear().commit();
         return true;
    }
    
    public static String getMyString(Context context, String name) {
        return getPrefs(context).getString(name, null);
    }
    
    public static void setMyString(Context context, String name, String value) {
        getPrefs(context).edit().putString(name, value).commit();
    }
    
    public static User getMyUser(Context context)
    {
    	Gson gson = new Gson();
    	String json = getPrefs(context).getString(LOGGED_USER, null);    
    	return gson.fromJson(json, User.class);
    }
    
    public static void setMyUser(Context context, User c)
    {
    	Log.i("user src", String.valueOf(c.getUser_source()));
        Editor prefsEditor =  getPrefs(context).edit();
    	Gson gson = new Gson();
        String json = gson.toJson(c);
        prefsEditor.putString(LOGGED_USER, json);
        prefsEditor.commit();
    }
}