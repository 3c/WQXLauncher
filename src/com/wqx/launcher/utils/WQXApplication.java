package com.wqx.launcher.utils;

import android.app.Application;

public class WQXApplication extends Application {

	public static Application mInstance;

	public void onCreate() {
		super.onCreate();
		mInstance = this;
	};
}
