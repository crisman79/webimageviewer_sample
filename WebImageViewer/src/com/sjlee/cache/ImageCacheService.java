package com.sjlee.cache;

import com.sjlee.lib.htmlutil.WebImageInfo;
import com.sjlee.webimageviewer.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

// 디스크캐쉬와 메모리 캐쉬를 가지고 있음
// Memory 캐쉬는 다운 받으면 로딩함.
// 디스크 캐쉬에도 없으면 다운로드하도록.
//
public final class ImageCacheService {
	final static String TAG = "ImageCacheService";

	private static ImageCacheService _instance;
	public static ImageCacheService getInstance() {
		if (_instance == null) {
			synchronized (ImageCacheService.class) {
				if (_instance == null) {										
					_instance = new ImageCacheService();
				}
			}
		}
		return _instance;
	}
	
	final static int mMemoryCacheSize = 1024 * 1024 * 10; // 1024 byte * 1024 =
															// 1Mb * 10 = 10 MB
	public static ImageDiskCache mDiskCache = null;
	public static ImageMemoryCache mMemCache = null;
	public static Drawable mPlaceHolder = null;
	public static Context mContext = null;

	public ImageCacheService() {
		// TODO Auto-generated constructor stub
	}

	// 디스크 캐쉬, 메모리 캐시 초기화 
	public void init(Context context) {
		Log.d(TAG, "ImageCacheServie init");
		mContext = context;
		if( mDiskCache == null ) {
			mDiskCache = new ImageDiskCache(context);
		} 
		if( mMemCache == null ) {
			mMemCache = new ImageMemoryCache(mMemoryCacheSize);
		}
		
	}
	
	// 1. 메모리 캐쉬이 있으면 메모리 캐시에서 데이터를 읽어서 전달
	// 2. 메모리 캐시에 없으면 디스크 캐시에서 데이터를 읽어서 전달
	// 3. 디스크 캐시에만 있는 경우에 메모리 캐시로 저장함. 
	public final Bitmap get(int key) {
		Bitmap bm = null;

		if (mMemCache != null) {
			synchronized (mMemCache) {
				if (mMemCache != null) {
					bm = mMemCache.getBitmap(key);
				}
			}
		}

		if (bm != null) {
			Log.d(TAG, "hit memcache");
			return bm;
		}

		if (mDiskCache != null) {
			try {
				bm = mDiskCache.getBitmap(key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (bm != null) {
			Log.d(TAG, "hit diskcache");
			if (mMemCache != null) {
				synchronized (mMemCache) {
					mMemCache.put(key, bm);
				}
			}
			return bm;
		}

		return null;
	}

	// 캐시서비스에 데이터를 넣는 함수
	// 1. 메모리 캐시에 집어 넣음.
	// 2. 디스크 캐시에 데이터가 없으면 디스크 캐시에 저장하도록 함.
	
	public final void put(String url, Bitmap bitmap) {
		if (mMemCache != null) {
			synchronized (mMemCache) {
				int key = url.hashCode();

				mMemCache.addBitmap(key, bitmap);

				Log.d(TAG, "MEM  COUNT:" + mMemCache.size());
			}
		}

		if (mDiskCache == null) {
			return;
		}
		synchronized (mDiskCache) {
			if (mDiskCache != null) {
				int key = url.hashCode();
				mDiskCache.addBitmap(key, bitmap);
				//Log.d(TAG, "DISK COUNT:"+mDiskCache.size());
			}
		}
	}

	// 기본 이미지 view의 place holder
	public final Drawable getPlaceHolder() {
		if (mContext == null) {
			return null;
		}
		if (mPlaceHolder == null) {
			mPlaceHolder = mContext.getResources().getDrawable(R.drawable.placeholder_small);
		}
		return mPlaceHolder;
	}
	

	public final void deinit() {
		if (mMemCache != null) {
			synchronized (mMemCache) {
				mMemCache = null;
			}
		}
	}
	
}
