package com.crunii.android.fxpt;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.crunii.android.fxpt.business.CRApp;

import java.util.LinkedList;
import java.util.List;

public class CRApplication extends Application {

	private static CRApp app;
	private static String token;
	private static String id;
	private static String name;
	private static String phone;
	
	private static int unread;
	
	public void onCreate() {
		super.onCreate();
		app = new CRApp();
	}
	
	public static CRApp getApp() {
		if(app == null) {
			app = new CRApp();			
		}
		return app;
	}

	public static void setToken(Context context, String t) {
		token = t;

		// saved to preference
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putString("token", token);
		editor.commit();
	}

	public static String getToken(Context context) {
		if (token == null) {
			token = PreferenceManager.getDefaultSharedPreferences(context).getString("token", null);
		}
		return token;
	}

	public static void setId(Context context, String i) {
		id = i;

		// saved to preference
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putString("id", id);
		editor.commit();
	}

	public static String getId(Context context) {
		if (id == null) {
			id = PreferenceManager.getDefaultSharedPreferences(context).getString("id", null);
		}
		return id;
	}

	public static void setName(Context context, String n) {
		name = n;

		// saved to preference
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putString("name", name);
		editor.commit();
	}

	public static String getName(Context context) {
		if (name == null) {
			name = PreferenceManager.getDefaultSharedPreferences(context).getString("name", null);
		}
		return name;
	}

	public static void setPhone(Context context, String n) {
		phone = n;
		// saved to preference
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putString("phone", phone);
		editor.commit();
	}

	public static String getPhone(Context context) {
		if (phone == null) {
			phone = PreferenceManager.getDefaultSharedPreferences(context).getString("phone", null);
		}
		return phone;
	}
	
	public static int getUnread() {
		return unread;
	}
	
	public static void setUnread(int u) {
		unread = u;
	}

    private static CRApplication instance;
    private List<Activity> mList = new LinkedList<Activity>();

    public synchronized static CRApplication getInstance(){
        if(null == instance){
            instance = new CRApplication();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }



}
