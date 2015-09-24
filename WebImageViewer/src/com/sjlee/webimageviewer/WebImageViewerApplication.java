/**
 * 
 */
package com.sjlee.webimageviewer;

import com.sjlee.cache.ImageCacheService;

import android.app.Application;
import android.util.Log;

/**
 * @author sjlee1225
 * 
 */
public class WebImageViewerApplication extends Application {

	static public final String TAG = "WebImageViewerApplication";

	/**
	 * 
	 */
	public WebImageViewerApplication() {
		// TODO Auto-generated constructor stub
	} 

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onCreate()");
		ImageCacheService.getInstance().init(this.getBaseContext());
		
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		
		Log.d(TAG, "onTerminate()");
		
	}

}
