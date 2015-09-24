package com.sjlee.webimageloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sjlee.lib.htmlutil.WebImageInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;


public class WebImageLoader {
	private static final String TAG = "WebImageLoader";
	
	private static final int HTTP_CONNECT_TIMEOUT = 5000;
	private static final int HTTP_READ_TIMEOUT = 5000;
	
	protected String mImageHostUrl = null;
	protected Context mContext = null;
	private ExecutorService mExecutor = null;
	private final int mExecutorCnt = 5;		// CPU_COUNT * 2 + 1; AsyncTask.THREAD_POOL_EXECUTOR NUMBER	
	
	private static WebImageLoader _instance = null;
	public static WebImageLoader getInstance() {
		if (_instance == null) {
			synchronized (WebImageLoader.class) {
				if (_instance == null) {
					_instance = new WebImageLoader();					
				}
			}			
		}
		return _instance;
	}
	
	public WebImageLoader(){
		
		mExecutor = Executors.newFixedThreadPool(mExecutorCnt);
	}
	public void initLoader(Context context, String hostUrl) {
		mContext = context;
		mImageHostUrl = hostUrl;
	}
	
	public Bitmap loadImage(String url) {
		if( mContext == null || mImageHostUrl == null ) {
			return null;
		}
		Bitmap bmp = null;
		
		String loadingUrl = makeFullImageUrl(mImageHostUrl, url);
		if (loadingUrl == null || loadingUrl.length() == 0) {
			return null;
		}
		
		
		//Log.d(TAG, "loadingUrl:"+loadingUrl);
		bmp = getBitmap(loadingUrl);		
		
		//Log.d(TAG, "loaded");
		return bmp;
	}
	
	// image loading and bitmap decode
	private Bitmap getBitmap(String url) {
		Bitmap bmp = null;
		InputStream is = null;
		try {
			URLConnection conn = new URL(url).openConnection();
			is = (InputStream)conn.getContent();			
			bmp = BitmapFactory.decodeStream(is);			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return bmp;
	}
	
	private String makeFullImageUrl(String host, String imageUrl) {		
		URL retURL = null;
		
		try {
			retURL = new URL(imageUrl);
		} catch (MalformedURLException e) {
			String tmpUrl = String.format("http://%s%s", host, imageUrl);
			try {
				retURL = new URL(tmpUrl);
			} catch (MalformedURLException ex) {
			}
		}

		return retURL == null ? "" : retURL.toString();
	}
	
	public ExecutorService getExecutor() {
		synchronized(mExecutor) {
			//Log.d(TAG, mExecutor.)			
			return mExecutor;
		}
	}
	
	public void closeAll() {
		if( mExecutor != null ){
			Log.d(TAG, "mExecutor.isShutdown():"+mExecutor.isShutdown());
			Log.d(TAG, "mExecutor.isTerminated():"+mExecutor.isTerminated());
			
			List<Runnable> list = mExecutor.shutdownNow();
			
			if( list != null || list.size() > 0 ){
				Log.d(TAG, "ExecutorSize:"+list.size());
			}
		}
	}


}
