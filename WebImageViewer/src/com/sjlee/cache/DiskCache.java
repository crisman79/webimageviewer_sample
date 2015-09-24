package com.sjlee.cache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.sjlee.helper.AppHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

// TODO
// LRU DiskCache는?
// Clear, Rotation?
// Comment : 갤러리에 있던 Diskcache를 따라하지 못함. 성능이 나오지 않을것 같다.

public class DiskCache {

	private final String TAG = "DiskCache";
	private final String mCacheName = "webimageviewer_thumbnail";
	private final String CACHE_FILE_POSTFIX = ".jpg";
	private String mCacheDir = "";
	private Context mContext = null;

	public DiskCache(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;

		mCacheDir = String.format("%s/%s", context.getCacheDir().getAbsolutePath(), mCacheName);
		Log.d(TAG, "CacheDir:" + mCacheDir);
		
		if( AppHelper.isExternalStorageReady() == false ) {
			Log.e(TAG, "External Storage Not Ready");
		}
		if( AppHelper.isExistPath(mCacheDir) == true ) {
			Log.d(TAG, "Exist cache dir");
		}
		else {
			AppHelper.makeDirectory(mCacheDir);
			Log.d(TAG, "make cache dir:"+mCacheDir);
		}
	}

	// 파일 저장
	public boolean put(int key, Bitmap bitmap) {
		if (bitmap == null ) {
			return false; 
		}

		//09-23 17:53:30.760: D/DiskCache(10149): filePath:/data/data/com.sjlee.webimageviewer/cache/webimageviewer_thumbnail/-1431907361
		String cacheFullPath = getPathByKey(key);
		Log.d(TAG, "cacheFullPath:"+cacheFullPath);
		
		boolean result = AppHelper.saveBitmapToFile(cacheFullPath, bitmap);
		
		return result;
	}

	// 캐쉬파일이 있는지 확
	// 파일 읽어오기	
	public boolean isExist(int key) {
		String path = getPathByKey(key);
		boolean isExist = AppHelper.isExistPath(path);
		return isExist;
	}

	private String getPathByKey(int key) {
		String fileFullPath = mCacheDir + "/" + key + CACHE_FILE_POSTFIX;
		return fileFullPath;
	}
	
	public Bitmap get(int key) {
		if( isExist(key) == false ) {
			return null;
		}
		
		String filePath = getPathByKey(key);
		
		Bitmap bmp = BitmapFactory.decodeFile(filePath);
		//BitmapFactory.DecodeFile
		
		return bmp;		
	}
}
